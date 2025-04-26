package com.springboot.MyTodoList.model;

import javax.persistence.*;

@Entity
@Table(name = "PROJECT")
public class Project {
    //Attributes

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PROJECT_ID")
    Integer ID;
    @Column(name = "NAME")
    String name;

    //Empty Constructor
    public Project(){}

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

    @Override
    public String toString() {
        return "Project{" +
                "ID=" + ID +
                ", name='" + name + '\'' +
                '}';
    }

}
