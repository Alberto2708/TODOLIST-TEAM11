package com.springboot.MyTodoList.model;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "DEVASSIGNEDTASK")
public class DevAssignedTask implements Serializable {
    @EmbeddedId
    private DevAssignedTaskId id;

    public DevAssignedTask() {}

    public DevAssignedTask(DevAssignedTaskId id) {
        this.id = id;
    }

    public DevAssignedTaskId getId() {
        return id;
    }

    public void setId(DevAssignedTaskId id) {
        this.id = id;
    }

    public Integer getToDoItemId() {
        return id.getToDoItemId();
    }

    public void setToDoItemId(Integer toDoItemId) {
        id.setToDoItemId(toDoItemId);
    }

    public Integer getAssignedDevId() {
        return id.getAssignedDevId();
    }

    public void setAssignedDevId(Integer assignedDevId) {
        id.setAssignedDevId(assignedDevId);
    }

    @Override
    public String toString() {
        return "DevAssignedTask{" +
                "toDoItemId=" + id.getToDoItemId() + '\n' +
                ", assignedDevId='" + id.getAssignedDevId() + '\n' +
                '}';
    }
}