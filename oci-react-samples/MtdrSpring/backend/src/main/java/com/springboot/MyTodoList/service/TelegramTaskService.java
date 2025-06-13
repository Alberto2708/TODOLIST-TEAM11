package com.springboot.MyTodoList.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.springboot.MyTodoList.model.AssignedDev;
import com.springboot.MyTodoList.model.AssignedDevId;
import com.springboot.MyTodoList.model.Employee;
import com.springboot.MyTodoList.model.Sprint;
import com.springboot.MyTodoList.model.SubToDoItem;
import com.springboot.MyTodoList.model.SubToDoItemId;
import com.springboot.MyTodoList.model.ToDoItem;

@Service
public class TelegramTaskService {

    @Autowired
    private ToDoItemService toDoItemService;

    @Autowired
    private AssignedDevService assignedDevService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private SprintService sprintService;

    @Autowired
    private TelegramAuthService authService;

    @Autowired
    private SubToDoItemService subToDoItemService;

    // Get all tasks for a specific employee in the current sprint by their Telegram ID
    // This method checks if the user is authenticated and retrieves their tasks
    // It filters the tasks to show only those that are completed or pending
    // If no tasks are found, it returns a message indicating that
    public String getUserTasks(long telegramId) {
        try {
            Employee employee = authService.getEmployee(telegramId);
            if (employee == null) {
                return "🔒 Please authenticate first";
            }

            ResponseEntity<Sprint> sprintResponse = sprintService.findActualSprintByProjectId(1);
            if (sprintResponse.getStatusCode() != HttpStatus.OK) {
                return "⚠️ No active sprint found";
            }
            Sprint sprint = sprintResponse.getBody();

            List<ToDoItem> tasks = getTasksForEmployeeInSprint(employee.getID(), sprint.getID())
                    .stream()
                    .filter(task -> "COMPLETED".equals(task.getStatus()) || "PENDING".equals(task.getStatus()))
                    .collect(Collectors.toList());

            if (tasks.isEmpty()) {
                return "📭 You have no tasks in current sprint";
            }

            return formatTaskList(tasks);
        } catch (Exception e) {
            return "⚠️ Error retrieving tasks";
        }
    }

    // Get current sprint information
    public String getCurrentSprintInfo() {
        try {
            ResponseEntity<Sprint> sprintResponse = sprintService.findActualSprintByProjectId(1);
            if (sprintResponse.getStatusCode() != HttpStatus.OK) {
                return "⚠️ No active sprint found";
            }
            Sprint sprint = sprintResponse.getBody();
            return String.format(
                    "🏃 Current Sprint\n\n"
                    + "ID: %d\n"
                    + "Name: %s\n"
                    + "Start: %s\n"
                    + "End: %s",
                    sprint.getID(),
                    sprint.getName(),
                    sprint.getStartDate().toLocalDate(),
                    sprint.getEndDate().toLocalDate()
            );
        } catch (Exception e) {
            return "⚠️ Error retrieving sprint info";
        }
    }

    // Get developer KPIs for the current sprint
    public String getDeveloperKPIs(long telegramId) {
        try {
            Employee employee = authService.getEmployee(telegramId);
            if (employee == null) {
                return "🔒 Please authenticate first";
            }

            ResponseEntity<Sprint> sprintResponse = sprintService.findActualSprintByProjectId(1); // Adjust project ID
            if (sprintResponse.getStatusCode() != HttpStatus.OK) {
                return "⚠️ No active sprint found";
            }
            Sprint sprint = sprintResponse.getBody();

            List<ToDoItem> tasks = getTasksForEmployeeInSprint(employee.getID(), sprint.getID());
            if (tasks.isEmpty()) {
                return "📊 No tasks in current sprint";
            }

            int completed = (int) tasks.stream()
                    .filter(t -> "COMPLETED".equals(t.getStatus()))
                    .count();
            int total = tasks.size();
            Double hours = tasks.stream().mapToDouble(ToDoItem::getEstHours).sum();

            return String.format(
                    "📊 Sprint #%d KPIs\n\n"
                    + "✅ Completed: %d/%d (%d%%)\n"
                    + "⏱️ Estimated Hours: %.2f\n"
                    + "📅 Deadline: %s",
                    sprint.getID(),
                    completed, total,
                    total > 0 ? (completed * 100) / total : 0,
                    hours,
                    sprint.getEndDate().toLocalDate()
            );
        } catch (Exception e) {
            return "⚠️ Error calculating KPIs";
        }
    }

    // Create a new task
    public String createNewTask(long telegramId, String name, Double estHours, String description, OffsetDateTime deadline) {
        try {
            Employee manager = authService.getEmployee(telegramId);
            if (manager == null || !authService.isManager(telegramId)) {
                return "⛔ Only managers can create tasks";
            }

            ResponseEntity<Sprint> sprintResponse = sprintService.findActualSprintByProjectId(1);
            if (sprintResponse.getStatusCode() != HttpStatus.OK) {
                return "❌ No active sprint found";
            }
            Sprint sprint = sprintResponse.getBody();

            ToDoItem task = new ToDoItem();
            task.setName(name);
            task.setSprintId(sprint.getID());
            task.setEstHours(estHours);
            task.setDescription(description);
            task.setDeadline(deadline);
            task.setManagerId(manager.getID());
            task.setStatus("PENDING");

            ToDoItem savedTask = toDoItemService.addToDoItem(task);
            return String.format("✅ Task #%d created!\n%s", savedTask.getID(), formatTask(savedTask));
        } catch (Exception e) {
            return "❌ Failed to create task";
        }
    }

    // Assign a task to an employee
    // This method checks if the user is a manager and if the task exists
    // It also checks if the employee exists by email and assigns the task to them
    // If the task is already assigned, it returns an error message
    public String assignTask(long telegramId, long taskId, String email) {
        try {
            Employee manager = authService.getEmployee(telegramId);
            if (manager == null || !authService.isManager(telegramId)) {
                return "⛔ Only managers can assign tasks";
            }

            ResponseEntity<Employee> empResponse = employeeService.findEmployeeByEmail(email);
            if (empResponse.getStatusCode() != HttpStatus.OK || empResponse.getBody() == null) {
                return "❌ Employee not found";
            }
            Employee assignee = empResponse.getBody();

            ToDoItem task = toDoItemService.getItemById((int) taskId);
            if (task == null || !task.getManagerId().equals(manager.getID())) {
                return "❌ Task not found or unauthorized";
            }

            AssignedDev assignment = new AssignedDev();
            assignment.setId(new AssignedDevId((int) taskId, assignee.getID()));

            assignedDevService.addAssignedDev(assignment);
            return String.format("✅ Task #%d assigned to %s", taskId, assignee.getName());
        } catch (Exception e) {
            return "❌ Failed to assign task";
        }
    }

    // Complete a task by its ID
    public String completeTask(long taskId) {
        try {
            ToDoItem task = toDoItemService.completeTask((int) taskId);
            return task != null
                    ? String.format("✅ Task #%d completed!", taskId)
                    : "❌ Task not found";
        } catch (Exception e) {
            return "❌ Failed to complete task";
        }
    }

    // Complete a subtask by its ID under a parent task
    public String completeSubTask(long telegramId, int taskId, int subTaskId) {
        try {
            Employee employee = authService.getEmployee(telegramId);
            if (employee == null) {
                return "🔒 Please authenticate first";
            }

            ToDoItem subtask = toDoItemService.getItemById(subTaskId);
            if (subtask == null) {
                return "❌ Subtask not found or does not belong to the specified parent task";
            }

            if (!employee.getID().equals(subtask.getManagerId())) {
                List<AssignedDev> assignments = assignedDevService.getAssignedDevsByDevId(employee.getID());
                boolean isAssigned = assignments.stream()
                    .anyMatch(a -> a.getId().getToDoItemId() == subTaskId);
                if (!isAssigned) {
                    return "⛔ You are not authorized to complete this subtask";
                }
            }

            ToDoItem completedSubtask = toDoItemService.completeTask(subTaskId);
            return completedSubtask != null
                ? String.format("✅ Subtask #%d under Task #%d marked as completed!", subTaskId, taskId)
                : "❌ Failed to complete subtask";
        } catch (Exception e) {
            return "❌ Error completing subtask";
        }
    }

    // Get tasks for a specific employee in a specific sprint by employee ID and sprint ID
    private List<ToDoItem> getTasksForEmployeeInSprint(Integer employeeId, Integer sprintId) {
        return assignedDevService.getAssignedDevsByDevId(employeeId).stream()
                .map(assignment -> toDoItemService.getItemById(assignment.getToDoItemId()))
                .filter(task -> task != null && task.getSprintId() != null && task.getSprintId().equals(sprintId))
                .collect(Collectors.toList());
    }

    // Format a list of tasks for display
    private String formatTaskList(List<ToDoItem> tasks) {
        return tasks.stream()
                .map(this::formatTask)
                .collect(Collectors.joining("\n\n"));
    }

    // Format a single task for display
    private String formatTask(ToDoItem task) {
        String displayName = task.getName();
        if (isSubTask(task)) {
            Integer parentId = getParentTaskId(task.getID());
            if (parentId != null) {
                displayName += " (Subtask from #" + parentId + ")";
            } else {
                displayName += " (Subtask)";
            }
        }
        return String.format(
                "#%d - %s\n"
                + "Status: %s\n"
                + "Hours: %.2f\n"
                + "Description: %s\n"
                + "Deadline: %s",
                task.getID(),
                displayName,
                task.getStatus(),
                task.getEstHours(),
                task.getDescription() != null ? task.getDescription() : "No description",
                task.getDeadline() != null ? task.getDeadline().toLocalDate() : "Not set"
        );
    }

    // Helper to get the parent task ID for a given subtask ID
    private Integer getParentTaskId(Integer subTaskId) {
        try {
            List<SubToDoItem> allLinks = subToDoItemService.findAllSubToDoItems();
            for (SubToDoItem link : allLinks) {
                if (link.getId().getSubToDoItemId().equals(subTaskId)) {
                    return link.getId().getToDoItemId();
                }
            }
        } catch (Exception e) {
            // ignore errors
        }
        return null;
    }

    // Helper to determine if a task is a subtask
    private boolean isSubTask(ToDoItem task) {
        try {
            List<SubToDoItem> allLinks = subToDoItemService.findAllSubToDoItems();
            return allLinks.stream()
                .anyMatch(link -> link.getId().getSubToDoItemId().equals(task.getID()));
        } catch (Exception e) {
            return false;
        }
    }

    // Method to get all completed tasks in the actual sprint
    public String getAllCompletedTasks() {
        try {
            ResponseEntity<Sprint> sprintResponse = sprintService.findActualSprintByProjectId(1);
            if (sprintResponse.getStatusCode() != HttpStatus.OK) {
                return "⚠️ No active sprint found";
            }
            Sprint sprint = sprintResponse.getBody();

            List<Employee> allEmployees = employeeService.findAll();
            StringBuilder result = new StringBuilder("📂 Completed Tasks This Sprint:\n\n");

            for (Employee employee : allEmployees) {
                List<ToDoItem> completedTasks = getTasksForEmployeeInSprint(employee.getID(), sprint.getID()).stream()
                        .filter(task -> "COMPLETED".equals(task.getStatus()))
                        .collect(Collectors.toList());

                if (!completedTasks.isEmpty()) {
                    result.append("👤 ").append(employee.getName()).append(":\n");
                    for (ToDoItem task : completedTasks) {
                        Integer parentId = getParentTaskId(task.getID());
                        String subtaskInfo = "";
                        if (parentId != null) {
                            subtaskInfo = " (Subtask of Task #" + parentId + ")";
                        }
                        result.append("- ").append(task.getName()).append(" (#").append(task.getID()).append(")").append(subtaskInfo).append("\n");
                    }
                    result.append("\n");
                }
            }

            return result.length() > 0 ? result.toString() : "📭 No completed tasks in this sprint.";
        } catch (Exception e) {
            return "❌ Error retrieving completed tasks.";
        }
    }

    // Method to get all pending tasks in the actual sprint
    public String getAllPendingTasks() {
        try {
            ResponseEntity<Sprint> sprintResponse = sprintService.findActualSprintByProjectId(1);
            if (sprintResponse.getStatusCode() != HttpStatus.OK) {
                return "⚠️ No active sprint found";
            }
            Sprint sprint = sprintResponse.getBody();

            List<Employee> allEmployees = employeeService.findAll();
            StringBuilder result = new StringBuilder("📂 Pending Tasks This Sprint:\n\n");

            for (Employee employee : allEmployees) {
                List<ToDoItem> pendingTasks = getTasksForEmployeeInSprint(employee.getID(), sprint.getID()).stream()
                        .filter(task -> "PENDING".equals(task.getStatus()))
                        .collect(Collectors.toList());

                if (!pendingTasks.isEmpty()) {
                    result.append("👤 ").append(employee.getName()).append(":\n");
                    for (ToDoItem task : pendingTasks) {
                        Integer parentId = getParentTaskId(task.getID());
                        String subtaskInfo = "";
                        if (parentId != null) {
                            subtaskInfo = " (Subtask of Task #" + parentId + ")";
                        }
                        result.append("- ").append(task.getName()).append(" (#").append(task.getID()).append(")").append(subtaskInfo).append("\n");
                    }
                    result.append("\n");
                }
            }

            return result.length() > 0 ? result.toString() : "📭 No pending tasks in this sprint.";
        } catch (Exception e) {
            return "❌ Error retrieving pending tasks.";
        }
    }

    // Subtask creation
    public String initiateSubTaskCreation(long telegramId) {
        Employee manager = authService.getEmployee(telegramId);
        if (manager == null || !authService.isManager(telegramId)) {
            return "⛔ Only managers can create subtasks";
        }
        return "Please enter the Task ID for which you want to create a subtask.";
    }

    // Continue subtask creation with validation and linking
    public String continueSubTaskCreation(long telegramId, int taskId, String name, double estHours, String description, OffsetDateTime deadline) {
        try {
            Employee manager = authService.getEmployee(telegramId);
            if (manager == null || !authService.isManager(telegramId)) {
                return "⛔ Only managers can create subtasks";
            }

            ToDoItem parentTask = toDoItemService.getItemById(taskId);
            if (parentTask == null || !parentTask.getManagerId().equals(manager.getID())) {
                return "❌ Parent task not found or unauthorized";
            }

            // Prevent creating a subtask from another subtask
            if (isSubTask(parentTask)) {
                return "❌ You cannot create a subtask from another subtask.";
            }

            if (parentTask.getEstHours() == null || parentTask.getEstHours() < 4) {
                return "❌ Parent task must have estimated hours of at least 4 to allow subtasks.";
            }
            if (estHours >= parentTask.getEstHours()) {
                return String.format("❌ Subtask's estimated hours (%.2f) must be less than the parent task's estimated hours (%.2f).", estHours, parentTask.getEstHours());
            }

            if (parentTask.getDeadline() != null && deadline != null && deadline.isAfter(parentTask.getDeadline())) {
                return String.format("❌ Subtask deadline (%s) cannot be after the parent task's deadline (%s).",
                        deadline.toLocalDate(), parentTask.getDeadline().toLocalDate());
            }

            ToDoItem subtask = new ToDoItem();
            subtask.setName(name);
            subtask.setEstHours(estHours);
            subtask.setDescription(description);
            subtask.setDeadline(deadline);
            subtask.setManagerId(manager.getID());
            subtask.setStatus("PENDING");
            subtask.setSprintId(parentTask.getSprintId());

            ToDoItem savedSubtask = toDoItemService.addToDoItem(subtask);
            SubToDoItem link = new SubToDoItem(new SubToDoItemId(taskId, savedSubtask.getID()));
            subToDoItemService.addSubToDoItem(link);
            return String.format("✅ Subtask #%d created under Task #%d!\n%s", savedSubtask.getID(), taskId, formatTask(savedSubtask));
        } catch (Exception e) {
            return "❌ Failed to create subtask";
        }
    }

    public String assignSubTask(long telegramId, int parentTaskId, int subTaskId, String email) {
        try {
            Employee manager = authService.getEmployee(telegramId);
            if (manager == null || !authService.isManager(telegramId)) {
                return "⛔ Only managers can assign subtasks";
            }

            ResponseEntity<Employee> empResponse = employeeService.findEmployeeByEmail(email);
            if (empResponse.getStatusCode() != HttpStatus.OK || empResponse.getBody() == null) {
                return "❌ Employee not found";
            }
            Employee assignee = empResponse.getBody();

            ToDoItem subtask = toDoItemService.getItemById(subTaskId);
            if (subtask == null || !subtask.getManagerId().equals(manager.getID())) {
                return "❌ Subtask not found or unauthorized";
            }

            AssignedDev assignment = new AssignedDev(new AssignedDevId(subTaskId, assignee.getID()));

            assignedDevService.addAssignedDev(assignment);
            return String.format("✅ Subtask #%d assigned to %s", subTaskId, assignee.getName());
        } catch (Exception e) {
            return "❌ Failed to assign subtask";
        }
    }

}
