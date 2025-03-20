//This model purpose is to manage the assigned tasks to the developers in the database.
//It has a composite primary key which is a combination of the toDoItemId and assignedDevId.

package com.springboot.MyTodoList.model;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "DEVASSIGNEDTASK")
public class DevAssignedTask implements Serializable {
    //Attributes

    @EmbeddedId
    private DevAssignedTaskId id;


    //Constructors

    public DevAssignedTask() {}

    public DevAssignedTask(DevAssignedTaskId id) {
        this.id = id;
    }


    //Getters and Setters

    public DevAssignedTaskId getId() {
        return id;
    }

    public void setId(DevAssignedTaskId id) {
        this.id = id;
    }

    public Integer getToDoItemId() {
        return id.getToDoItemId();
    }

    public void setToDoItemId(Integer toDoItemId) {
        id.setToDoItemId(toDoItemId);
    }

    public Integer getAssignedDevId() {
        return id.getAssignedDevId();
    }

    public void setAssignedDevId(Integer assignedDevId) {
        id.setAssignedDevId(assignedDevId);
    }


    //toString method override
    
    @Override
    public String toString() {
        return "DevAssignedTask{" +
                "toDoItemId=" + id.getToDoItemId() + '\n' +
                ", assignedDevId='" + id.getAssignedDevId() + '\n' +
                '}';
    }
}