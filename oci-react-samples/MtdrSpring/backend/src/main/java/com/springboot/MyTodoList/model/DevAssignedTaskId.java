package com.springboot.MyTodoList.model;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.*;

@Embeddable
public class DevAssignedTaskId implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "TODOITEM_ID")
    private Integer toDoItemId;

    @Column(name = "ASSIGNED_DEV_ID")
    private Integer assignedDevId;

    public DevAssignedTaskId() {}

    public DevAssignedTaskId(Integer toDoItemId, Integer assignedDevId) {
        this.toDoItemId = toDoItemId;
        this.assignedDevId = assignedDevId;
    }

    public Integer getToDoItemId() {
        return toDoItemId;
    }

    public void setToDoItemId(Integer toDoItemId) {
        this.toDoItemId = toDoItemId;
    }

    public Integer getAssignedDevId() {
        return assignedDevId;
    }

    public void setAssignedDevId(Integer assignedDevId) {
        this.assignedDevId = assignedDevId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DevAssignedTaskId that = (DevAssignedTaskId) o;
        return Objects.equals(toDoItemId, that.toDoItemId) && Objects.equals(assignedDevId, that.assignedDevId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(toDoItemId, assignedDevId);
    }
}