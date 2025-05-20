// Model for the Project entity
// This class represents a project in the database
package com.springboot.MyTodoList.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PROJECT")
public class Project {

    //Attributes
    // ID is the primary key and is auto-generated
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PROJECT_ID")
    Integer ID;

    // Name of the project
    @Column(name = "NAME")
    String name;

    //Empty Constructor
    public Project() {
    }

    //Full Constructor
    public Project(Integer id, String name) {
        this.ID = id;
        this.name = name;
    }

    //Partial Constructor
    public Project(String name) {
        this.name = name;
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

    // toString method
    @Override
    public String toString() {
        return "Project{"
                + "ID=" + ID
                + ", name='" + name + '\''
                + '}';
    }

}
