package com.springboot.MyTodoList.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;

/*
    representation of the TODOITEM table that exists already
    in the autonomous database
 */
@Entity
@Table(name = "TODOITEM")
public class ToDoItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;
    
    @Column(name = "NAME", length = 200)
    private String name;
    
    @Column(name = "DESCRIPTION", length = 4000)
    private String description;
    
    @Column(name = "CREATION_TS")
    private OffsetDateTime creation_ts;
    
    @Column(name = "START_DATE")
    private LocalDate startDate;
    
    @Column(name = "DEADLINE")
    private LocalDate deadline;
    
    @Column(name = "STATUS", length = 20)
    private String status; // "Completed", "Pending", "Cancelled", "Reviewing"
    
    @Column(name = "COMPLETION_TS")
    private OffsetDateTime completion_ts;
    
    @Column(name = "MANAGER_ID")
    private Integer managerId;
    
    @Column(name = "PROJECT_ID")
    private Integer projectId;
    
    public ToDoItem() {
    }
    
    public ToDoItem(int id, String name, String description, OffsetDateTime creation_ts, 
                   LocalDate startDate, LocalDate deadline, String status, OffsetDateTime completion_ts,
                   Integer managerId, Integer projectId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.creation_ts = creation_ts;
        this.startDate = startDate;
        this.deadline = deadline;
        this.status = status;
        this.completion_ts = completion_ts;
        this.managerId = managerId;
        this.projectId = projectId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public OffsetDateTime getCreation_ts() {
        return creation_ts;
    }

    public void setCreation_ts(OffsetDateTime creation_ts) {
        this.creation_ts = creation_ts;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OffsetDateTime getCompletion_ts() {
        return completion_ts;
    }

    public void setCompletion_ts(OffsetDateTime completion_ts) {
        this.completion_ts = completion_ts;
    }

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    @Override
    public String toString() {
        return "ToDoItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", creation_ts=" + creation_ts +
                ", startDate=" + startDate +
                ", deadline=" + deadline +
                ", status='" + status + '\'' +
                ", completion_ts=" + completion_ts +
                ", managerId=" + managerId +
                ", projectId=" + projectId +
                '}';
    }
}
