package com.springboot.MyTodoList.model;

import java.io.Serializable;
import java.util.Objects;

/*
 * Composite primary key class for SubToDoItem
 */
public class SubToDoItemId implements Serializable {
    
    private int todoItemId;
    private int subTodoItemId;
    
    // Default constructor
    public SubToDoItemId() {
    }
    
    // Full constructor
    public SubToDoItemId(int todoItemId, int subTodoItemId) {
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
    
    // Required for composite key classes
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubToDoItemId that = (SubToDoItemId) o;
        return todoItemId == that.todoItemId && 
               subTodoItemId == that.subTodoItemId;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(todoItemId, subTodoItemId);
    }
} 