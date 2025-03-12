package com.springboot.MyTodoList.model;

import javax.persistence.*;

@Entity
@Table(name = "SubToDoItem")
public class SubToDoItem {

    @Column(name = "TODOITEM_ID")
    int toDoItemId;

    @Column(name = "SUBTODOITEM_ID")
    int subToDoItemId;
    

    public SubToDoItem(){}

    public SubToDoItem(
        int toDoItemId,
        int subToDoItemId        
        ) {
        this.toDoItemId = toDoItemId;
        this.subToDoItemId = subToDoItemId;
    }

    public int getToDoItemId() {
        return toDoItemId;
    }

    public void setToDoItemId(int toDoItemId) {
        this.toDoItemId = toDoItemId;
    }

    public int getSubToDoItemId() {
        return subToDoItemId;
    }

    public void setSubToDoItemId(int subToDoItemId) {
        this.subToDoItemId = subToDoItemId;
    }

    @Override
    public String toString() {
        return "ToDoItem{" +
                "toDoItemId=" + toDoItemId + '\n' +
                ", subToDoItemId=" + subToDoItemId + '\n' +
                '}';
    }
}