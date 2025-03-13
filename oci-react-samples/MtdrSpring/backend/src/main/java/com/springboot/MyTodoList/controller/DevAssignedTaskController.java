package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.DevAssignedTask;
import com.springboot.MyTodoList.model.Employee;
import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.service.DevAssignedTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/task-assignments")
public class DevAssignedTaskController {

    @Autowired
    private DevAssignedTaskService devAssignedTaskService;
    
    @GetMapping
    public List<DevAssignedTask> getAllTaskAssignments() {
        return devAssignedTaskService.findAll();
    }
    
    @GetMapping("/{todoItemId}/{assignedDevId}")
    public ResponseEntity<DevAssignedTask> getTaskAssignment(
            @PathVariable("todoItemId") int todoItemId,
            @PathVariable("assignedDevId") int assignedDevId) {
        return devAssignedTaskService.getDevAssignedTaskById(todoItemId, assignedDevId);
    }
    
    @GetMapping("/developer/{developerId}")
    public List<ToDoItem> getTasksByDeveloperId(@PathVariable("developerId") int developerId) {
        return devAssignedTaskService.findTasksByDeveloperId(developerId);
    }
    
    @GetMapping("/task/{taskId}")
    public List<Employee> getDevelopersByTaskId(@PathVariable("taskId") int taskId) {
        return devAssignedTaskService.findDevelopersByTaskId(taskId);
    }
    
    @PostMapping
    public ResponseEntity<DevAssignedTask> assignTaskToDeveloper(@RequestBody DevAssignedTask assignment) {
        return devAssignedTaskService.assignTaskToDeveloper(assignment);
    }
    
    @DeleteMapping("/{todoItemId}/{assignedDevId}")
    public ResponseEntity<HttpStatus> removeTaskAssignment(
            @PathVariable("todoItemId") int todoItemId,
            @PathVariable("assignedDevId") int assignedDevId) {
        boolean success = devAssignedTaskService.removeTaskAssignment(todoItemId, assignedDevId);
        if (success) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
} 