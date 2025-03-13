package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.DevAssignedTask;
import com.springboot.MyTodoList.model.DevAssignedTaskId;
import com.springboot.MyTodoList.model.Employee;
import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.repository.DevAssignedTaskRepository;
import com.springboot.MyTodoList.repository.EmployeeRepository;
import com.springboot.MyTodoList.repository.ToDoItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DevAssignedTaskService {

    @Autowired
    private DevAssignedTaskRepository devAssignedTaskRepository;
    
    @Autowired
    private ToDoItemRepository toDoItemRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    /**
     * Find all task assignments
     */
    public List<DevAssignedTask> findAll() {
        return devAssignedTaskRepository.findAll();
    }
    
    /**
     * Find task assignment by ID
     */
    public ResponseEntity<DevAssignedTask> getDevAssignedTaskById(int todoItemId, int assignedDevId) {
        DevAssignedTaskId id = new DevAssignedTaskId(todoItemId, assignedDevId);
        Optional<DevAssignedTask> taskAssignmentData = devAssignedTaskRepository.findById(id);
        if (taskAssignmentData.isPresent()) {
            return new ResponseEntity<>(taskAssignmentData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * Find all tasks assigned to a developer
     */
    public List<ToDoItem> findTasksByDeveloperId(int developerId) {
        List<DevAssignedTask> assignments = devAssignedTaskRepository.findByAssignedDevId(developerId);
        return assignments.stream()
                .map(assignment -> toDoItemRepository.findById(assignment.getTodoItemId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
    
    /**
     * Find all developers assigned to a task
     */
    public List<Employee> findDevelopersByTaskId(int taskId) {
        List<DevAssignedTask> assignments = devAssignedTaskRepository.findByTodoItemId(taskId);
        return assignments.stream()
                .map(assignment -> employeeRepository.findById(assignment.getAssignedDevId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
    
    /**
     * Assign a task to a developer
     * Validates that both task and developer exist
     */
    public ResponseEntity<DevAssignedTask> assignTaskToDeveloper(DevAssignedTask devAssignedTask) {
        try {
            // Verify task exists
            Optional<ToDoItem> task = toDoItemRepository.findById(devAssignedTask.getTodoItemId());
            if (!task.isPresent()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            // Verify developer exists
            Optional<Employee> developer = employeeRepository.findById(devAssignedTask.getAssignedDevId());
            if (!developer.isPresent()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            // Save the assignment
            DevAssignedTask savedAssignment = devAssignedTaskRepository.save(devAssignedTask);
            return new ResponseEntity<>(savedAssignment, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Remove a task assignment
     */
    public boolean removeTaskAssignment(int todoItemId, int assignedDevId) {
        try {
            DevAssignedTaskId id = new DevAssignedTaskId(todoItemId, assignedDevId);
            devAssignedTaskRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
} 