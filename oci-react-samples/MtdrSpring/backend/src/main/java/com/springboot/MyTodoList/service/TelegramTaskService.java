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
        return String.format(
                "#%d - %s\n"
                + "Status: %s\n"
                + "Hours: %.2f\n"
                + "Description: %s\n"
                + "Deadline: %s",
                task.getID(),
                task.getName(),
                task.getStatus(),
                task.getEstHours(),
                task.getDescription() != null ? task.getDescription() : "No description",
                task.getDeadline() != null ? task.getDeadline().toLocalDate() : "Not set"
        );
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
                        result.append("- ").append(task.getName()).append(" (#").append(task.getID()).append(")\n");
                    }
                    result.append("\n");
                }
            }

            return result.length() > 0 ? result.toString() : "📭 No completed tasks in this sprint.";
        } catch (Exception e) {
            return "❌ Error retrieving completed tasks.";
        }
    }
}
