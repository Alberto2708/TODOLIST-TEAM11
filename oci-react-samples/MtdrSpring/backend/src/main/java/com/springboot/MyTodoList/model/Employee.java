//This model is the representation of the EMPLOYEE table that exists already in the autonomous database.
package com.springboot.MyTodoList.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "EMPLOYEE")
public class Employee {

    //Attributes
    // Employee ID is a generated value
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EMPLOYEE_ID")
    Integer ID;

    // Name column
    @Column(name = "NAME")
    String name;

    // Manager ID column
    @Column(name = "MANAGER_ID")
    Integer managerId;

    // Email column
    @Column(name = "EMAIL")
    String email;

    // Password column
    @Column(name = "PASSWORD")
    String password;

    // Project ID column
    // This column is used to identify the project that the employee is working on
    @Column(name = "PROJECT_ID")
    Integer projectId;

    // Telegram ID column
    @Column(name = "TELEGRAM_ID")
    Long telegramId;

    //Empty Constructor
    public Employee() {

    }

    //Full Constructor
    public Employee(Integer id, String name, Integer managerId, String email, String password, Integer projectId, Long telegramId) {
        this.ID = id;
        this.name = name;
        this.managerId = managerId;
        this.email = email;
        this.password = password;
        this.projectId = projectId;
        this.telegramId = telegramId;
    }

    //Partial Constructor
    public Employee(String name, Integer managerId, String email, String password, Integer projectId) {
        this.name = name;
        this.managerId = managerId;
        this.email = email;
        this.password = password;
        this.projectId = projectId;
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

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Long getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(Long telegramId) {
        this.telegramId = telegramId;
    }

    //toString method override
    @Override
    public String toString() {
        return "Employee{"
                + "ID=" + ID
                + ", name='" + name + '\''
                + ", managerId=" + managerId
                + ", email='" + email + '\''
                + ", password='" + password + '\''
                + ", projectId='" + projectId + '\''
                + ", telegramId=" + telegramId
                + '}';
    }

}
