package com.springboot.MyTodoList.model;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "TODOITEM")
public class ToDoItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int ID;
    
    @Column(name = "NAME")
    String name;

    @Column(name = "STATUS")
    String status;
    
    @Column(name = "MANAGER_ID")
    int managerId;
    
    @Column(name = "COMPLETION_TS")
    OffsetDateTime completionTs;
    
    @Column(name = "START_DATE")
    OffsetDateTime startDate;
    
    @Column(name = "DEADLINE")
    OffsetDateTime deadline;
    
    @Column(name = "PROJECT_ID")
    int projectId;
    
    @Column(name = "DESCRIPTION")
    String description;

    public ToDoItem(){}

    public ToDoItem(
        int ID,
        String name,
        String status,
        int managerId,
        OffsetDateTime completionTs,
        OffsetDateTime startDate,
        OffsetDateTime deadline,
        int projectId,
        String description
        ) {
        this.ID = ID;
        this.name = name;
        this.status = status;
        this.managerId = managerId;
        this.completionTs = completionTs;
        this.startDate = startDate;
        this.deadline = deadline;
        this.projectId = projectId;
        this.description = description;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getManagerId() {
        return managerId;
    }

    public void setManagerId(int managerId) {
        this.managerId = managerId;
    }

    public OffsetDateTime getCompletionTs() {
        return completionTs;
    }

    public void setCompletionTs(OffsetDateTime completionTs) {
        this.completionTs = completionTs;
    }

    public OffsetDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(OffsetDateTime startDate) {
        this.startDate = startDate;
    }

    public OffsetDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(OffsetDateTime deadline) {
        this.deadline = deadline;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "ToDoItem{" +
                "ID=" + ID + '\n' +
                ", name='" + name + '\n' +
                ", status='" + status + '\n' +
                ", managerId=" + managerId + '\n' +
                ", completionTs=" + completionTs + '\n' +
                ", startDate=" + startDate + '\n' +
                ", deadline=" + deadline + '\n' +
                ", projectId=" + projectId + '\n' +
                ", description='" + description + '\n' +
                '}';
    }
}