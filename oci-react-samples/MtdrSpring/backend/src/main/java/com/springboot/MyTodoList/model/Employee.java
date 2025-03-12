package com.springboot.MyTodoList.model;

import javax.persistence.*;

/*
    representation of the EMPLOYEE table that exists already
    in the autonomous database
 */
@Entity
@Table(name = "EMPLOYEE")
public class Employee {

    //Attributes
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int ID;
    @Column(name = "NAME")
    String name;
    @Column(name = "MANAGER_ID")
    int managerId;
    @Column(name = "EMAIL")
    String email;
    @Column(name = "PASSWORD")
    String password;
    @Column(name = "PROJECT_ID")
    int projectId;


    //Empty Constructor
    public Employee() {

    }

    //Full Constructor
    public Employee(int ID, String name, int managerId, String email, String password, int projectId) {
        this.ID = ID;
        this.name = name;
        this.managerId = managerId;
        this.email = email;
        this.password = password;
        this.projectId = projectId;
    }
    
    //Getters and setters
    
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

    public int getManagerId() {
        return managerId;
    }

    public void setManagerId(int managerId) {
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

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }


    //toString method override
    @Override
    public String toString(){
        return "Employee{" +
                "ID=" + ID +
                ", name='" + name + '\'' +
                ", managerId=" + managerId +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", projectId=" + projectId +
                '}';
    }

}