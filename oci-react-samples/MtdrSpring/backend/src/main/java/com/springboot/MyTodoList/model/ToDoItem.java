//This model is the representation of the ToDoItem table
// that exists already in the autonomous database.
package com.springboot.MyTodoList.model;

import java.time.OffsetDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TODOITEM")
public class ToDoItem {

    //Attributes
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TODOITEM_ID")
    Integer ID;

    //Task name column
    @Column(name = "NAME")
    String name;

    //Task status column
    @Column(name = "STATUS")
    String status;

    //Task manager id column
    @Column(name = "MANAGER_ID")
    Integer managerId;

    //Task completion timestamp column
    @Column(name = "COMPLETION_TS")
    OffsetDateTime completionTs;

    //Task start date column
    @Column(name = "START_DATE")
    OffsetDateTime startDate;

    //Task deadline column
    @Column(name = "DEADLINE")
    OffsetDateTime deadline;

    //Task sprint id column
    @Column(name = "SPRINT_ID")
    Integer sprintId;

    //Task description column
    @Column(name = "DESCRIPTION")
    String description;

    //Task estimated hours column
    @Column(name = "EST_HOURS")
    Double estHours;

    //Empty Constructor
    public ToDoItem() {
    }

    //Full Constructor
    public ToDoItem(
            Integer ID,
            String name,
            String status,
            Integer managerId,
            OffsetDateTime completionTs,
            OffsetDateTime startDate,
            OffsetDateTime deadline,
            Integer sprintId,
            String description,
            Double estHours
    ) {
        this.ID = ID;
        this.name = name;
        this.status = status;
        this.managerId = managerId;
        this.completionTs = completionTs;
        this.startDate = startDate;
        this.deadline = deadline;
        this.sprintId = sprintId;
        this.description = description;
        this.estHours = estHours;
    }

    //Partial Constructor
    public ToDoItem(
            String name,
            String status,
            Integer managerId,
            OffsetDateTime startDate,
            OffsetDateTime deadline,
            Integer sprintId,
            String description,
            Double estHours
    ) {
        this.name = name;
        this.status = status;
        this.managerId = managerId;
        this.startDate = startDate;
        this.deadline = deadline;
        this.sprintId = sprintId;
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

    public Integer getSprintId() {
        return sprintId;
    }

    public void setSprintId(Integer sprintId) {
        this.sprintId = sprintId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getEstHours() {
        return estHours;
    }

    public void setEstHours(Double estHours) {
        this.estHours = estHours;
    }

    //toString method override
    @Override
    public String toString() {
        return "ToDoItem{"
                + "ID=" + ID + '\n'
                + ", name='" + name + '\n'
                + ", status='" + status + '\n'
                + ", managerId=" + managerId + '\n'
                + ", completionTs=" + completionTs + '\n'
                + ", startDate=" + startDate + '\n'
                + ", deadline=" + deadline + '\n'
                + ", sprintId=" + sprintId + '\n'
                + ", description='" + description + '\n'
                + ", estHours=" + estHours + '\n'
                + '}';
    }
}
