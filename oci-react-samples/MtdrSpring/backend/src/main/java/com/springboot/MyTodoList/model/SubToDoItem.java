//This model is the representation of the SubToDoItem table
//that exists already in the autonomous database.
package com.springboot.MyTodoList.model;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "SUBTODOITEM")
public class SubToDoItem implements Serializable {

    //Attributes
    @EmbeddedId
    private SubToDoItemId id;

    //Constructors
    public SubToDoItem() {
    }

    public SubToDoItem(SubToDoItemId id) {
        this.id = id;
    }

    //Getters and Setters
    public SubToDoItemId getId() {
        return id;
    }

    public void setId(SubToDoItemId id) {
        this.id = id;
    }

    public Integer getToDoItemId() {
        return id.getToDoItemId();
    }

    public void setToDoItemId(Integer toDoItemId) {
        id.setToDoItemId(toDoItemId);
    }

    public Integer getSubToDoItemId() {
        return id.getSubToDoItemId();
    }

    public void setSubToDoItemId(Integer subToDoItemId) {
        id.setSubToDoItemId(subToDoItemId);
    }

    //toString method override
    // This method returns a string representation of the SubToDoItem object
    // It includes the toDoItemId and subToDoItemId attributes
    @Override
    public String toString() {
        return "SubToDoItem{"
                + "toDoItemId=" + id.getToDoItemId() + '\n'
                + ", subToDoItemId='" + id.getSubToDoItemId() + '\n'
                + '}';
    }

}
