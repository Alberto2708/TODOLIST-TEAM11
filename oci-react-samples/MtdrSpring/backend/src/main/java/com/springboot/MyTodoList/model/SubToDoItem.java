package com.springboot.MyTodoList.model;

import javax.persistence.*;
import java.io.Serializable;

/*
    Representation of the SUBTODOITEM table in the autonomous database
 */
@Entity
@Table(name = "SUBTODOITEM")
@IdClass(SubToDoItemId.class)
public class SubToDoItem {
    
    @Id
    @Column(name = "TODOITEM_ID")
    private int todoItemId;
    
    @Id
    @Column(name = "SUBTODOITEM_ID")
    private int subTodoItemId;
    
    // Constructors
    public SubToDoItem() {
    }
    
    public SubToDoItem(int todoItemId, int subTodoItemId) {
        this.todoItemId = todoItemId;
        this.subTodoItemId = subTodoItemId;
    }
    
    // Getters and setters
    public int getTodoItemId() {
        return todoItemId;
    }
    
    public void setTodoItemId(int todoItemId) {
        this.todoItemId = todoItemId;
    }
    
    public int getSubTodoItemId() {
        return subTodoItemId;
    }
    
    public void setSubTodoItemId(int subTodoItemId) {
        this.subTodoItemId = subTodoItemId;
    }
    
    @Override
    public String toString() {
        return "SubToDoItem{" +
                "todoItemId=" + todoItemId +
                ", subTodoItemId=" + subTodoItemId +
                '}';
    }
} 