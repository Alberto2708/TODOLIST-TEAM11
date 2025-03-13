package com.springboot.MyTodoList.model;

import javax.persistence.*;

/*
    Representation of the DEVASSIGNEDTASK table in the autonomous database
 */
@Entity
@Table(name = "DEVASSIGNEDTASK")
@IdClass(DevAssignedTaskId.class)
public class DevAssignedTask {
    
    @Id
    @Column(name = "TODOITEM_ID")
    private int todoItemId;
    
    @Id
    @Column(name = "ASSIGNED_DEV_ID")
    private int assignedDevId;
    
    // Constructors
    public DevAssignedTask() {
    }
    
    public DevAssignedTask(int todoItemId, int assignedDevId) {
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
    
    @Override
    public String toString() {
        return "DevAssignedTask{" +
                "todoItemId=" + todoItemId +
                ", assignedDevId=" + assignedDevId +
                '}';
    }
} 