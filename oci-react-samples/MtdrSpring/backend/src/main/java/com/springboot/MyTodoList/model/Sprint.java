//This model is the representation of the SPRINT table that exists already in the autonomous database.
package com.springboot.MyTodoList.model;
import java.time.OffsetDateTime;

import javax.persistence.*;

@Entity
@Table(name = "SPRINT")
public class Sprint {

    //Attributes

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SPRINT_ID")
    Integer ID;
    @Column(name = "PROJECT_ID")
    Integer projectId;
    @Column(name = "NAME")
    String name;
    @Column(name = "START_DATE")
    OffsetDateTime startDate;
    @Column(name = "END_DATE")
    OffsetDateTime endDate;

    //Empty Constructor

    public Sprint() {
    }

    //Full Constructor

    public Sprint(Integer ID, Integer projectId, String name, OffsetDateTime startDate, OffsetDateTime endDate) {
        this.ID = ID;
        this.projectId = projectId;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    //Getters and setters
    public Integer getID() {
        return ID;
    }   

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OffsetDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(OffsetDateTime startDate) {
        this.startDate = startDate;
    }

    public OffsetDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(OffsetDateTime endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "Sprint{" +
                "ID=" + ID +
                ", projectId=" + projectId +
                ", name='" + name + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
    
}
