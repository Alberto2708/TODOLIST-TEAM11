package com.springboot.MyTodoList.model;

import java.io.Serializable;
import java.util.Objects;

/*
 * Composite primary key class for DevAssignedTask
 */
public class DevAssignedTaskId implements Serializable {
    
    private int todoItemId;
    private int assignedDevId;
    
    // Default constructor
    public DevAssignedTaskId() {
    }
    
    // Full constructor
    public DevAssignedTaskId(int todoItemId, int assignedDevId) {
        this.todoItemId = todoItemId;
        this.assignedDevId = assignedDevId;
    }
    
    // Getters and setters
    public int getTodoItemId() {
        return todoItemId;
    }
    
    public void setTodoItemId(int todoItemId) {
        this.todoItemId = todoItemId;
    }
    
    public int getAssignedDevId() {
        return assignedDevId;
    }
    
    public void setAssignedDevId(int assignedDevId) {
        this.assignedDevId = assignedDevId;
    }
    
    // Required for composite key classes
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DevAssignedTaskId that = (DevAssignedTaskId) o;
        return todoItemId == that.todoItemId && 
               assignedDevId == that.assignedDevId;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(todoItemId, assignedDevId);
    }
} 