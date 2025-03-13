package com.springboot.MyTodoList.model;

import javax.persistence.*;

/*
    Representation of the PROJECT table in the autonomous database
 */
@Entity
@Table(name = "PROJECT")
public class Project {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;
    
    @Column(name = "NAME", length = 4000)
    private String name;
    
    // Empty constructor
    public Project() {
    }
    
    // Full constructor
    public Project(int id, String name) {
        this.id = id;
        this.name = name;
    }
    
    // Getters and setters
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
    
    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
} 