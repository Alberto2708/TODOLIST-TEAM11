//This model is the representation of the ToDoItem table that exists already in the autonomous database.

package com.springboot.MyTodoList.model;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "TODOITEM")
public class ToDoItem {

    //Attributes
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TODOITEM_ID")
    Integer ID;
    
    @Column(name = "NAME")
    String name;

    @Column(name = "STATUS")
    String status;
    
    @Column(name = "MANAGER_ID")
    Integer managerId;
    
    @Column(name = "COMPLETION_TS")
    OffsetDateTime completionTs;
    
    @Column(name = "START_DATE")
    OffsetDateTime startDate;
    
    @Column(name = "DEADLINE")
    OffsetDateTime deadline;
    
    @Column(name = "SPRINT_ID")
    Integer projectId;
    
    @Column(name = "DESCRIPTION")
    String description;
    
    @Column(name = "EST_HOURS")
    Integer estHours;

    //Empty Constructor
    public ToDoItem(){}

    //Full Constructor
    public ToDoItem(Integer ID, String name, String status, Integer managerId, OffsetDateTime completionTs, OffsetDateTime startDate, OffsetDateTime deadline, Integer projectId, String description, Integer estHours) {
        this.ID = ID;
        this.name = name;
        this.status = status;
        this.managerId = managerId;
        this.completionTs = completionTs;
        this.startDate = startDate;
        this.deadline = deadline;
        this.projectId = projectId;
        this.description = description;
        this.estHours = estHours;
    }

    //Getters and setters

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
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

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
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

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getEstHours() {
        return estHours;
    }

    public void setEstHours(Integer estHours) {
        this.estHours = estHours;
    }


    //toString method override
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
                ", estHours=" + estHours + '\n' +
                '}';
    }
}