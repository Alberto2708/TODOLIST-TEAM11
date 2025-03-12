package com.springboot.MyTodoList.model;

import javax.persistence.*;

@Entity
@Table(name = "DEVASSIGNEDTASK")
public class DevAssignedTask {
    @Column(name = "TODOITEM_ID")
    int toDoItemId;

    @Column(name = "ASSIGNED_DEV_ID")
    int assignedDevId;

    public DevAssignedTask(){}

    public DevAssignedTask(
        int toDoItemId,
        int assignedDevId
        ) {
        this.toDoItemId = toDoItemId;
        this.assignedDevId = assignedDevId;
    }

    public int getToDoItemId() {
        return toDoItemId;
    }

    public void setToDoItemId(int toDoItemId) {
        this.toDoItemId = toDoItemId;
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
                "toDoItemId=" + toDoItemId + '\n' +
                ", assignedDevId='" + assignedDevId + '\n' +
                '}';
    }
}

