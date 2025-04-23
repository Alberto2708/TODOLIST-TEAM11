//This model purpose is to manage the assigned tasks to the developers in the database.
//It has a composite primary key which is a combination of the toDoItemId and assignedDevId.

package com.springboot.MyTodoList.model;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "ASSIGNEDDEV")
public class AssignedDev implements Serializable {
    //Attributes

    @EmbeddedId
    private AssignedDevId id;


    //Constructors

    public AssignedDev() {}

    public AssignedDev(AssignedDevId id) {
        this.id = id;
    }


    //Getters and Setters

    public AssignedDevId getId() {
        return id;
    }

    public void setId(AssignedDevId id) {
        this.id = id;
    }

    public Integer getToDoItemId() {
        return id.getToDoItemId();
    }

    public void setToDoItemId(Integer toDoItemId) {
        id.setToDoItemId(toDoItemId);
    }

    public Integer getAssignedDevId() {
        return id.getEmployeeId();
    }

    public void setAssignedDevId(Integer assignedDevId) {
        id.setEmployeeId(assignedDevId);
    }


    //toString method override
    
    @Override
    public String toString() {
        return "DevAssignedTask{" +
                "toDoItemId=" + id.getToDoItemId() + '\n' +
                ", assignedDevId='" + id.getEmployeeId() + '\n' +
                '}';
    }
}