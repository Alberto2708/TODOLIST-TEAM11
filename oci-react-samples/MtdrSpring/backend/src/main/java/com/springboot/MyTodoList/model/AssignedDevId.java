//This model purpose is to manage the assigned tasks to the developers in the database.
//It has a composite primary key which is a combination of the toDoItemId and assignedDevId.
package com.springboot.MyTodoList.model;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.*;

@Embeddable
public class AssignedDevId implements Serializable {

    //Attributes

    private static final long serialVersionUID = 1L;

    @Column(name = "TODOITEM_ID")
    private Integer toDoItemId;

    @Column(name = "EMPLOYEE_ID")
    private Integer employeeId;


    //Constructors
    public AssignedDevId() {}

    public AssignedDevId(Integer toDoItemId, Integer employeeId) {
        this.toDoItemId = toDoItemId;
        this.employeeId = employeeId;
    }

    // Getters and Setters

    public Integer getToDoItemId() {
        return toDoItemId;
    }

    public void setToDoItemId(Integer toDoItemId) {
        this.toDoItemId = toDoItemId;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }


    //equals() function override

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssignedDevId that = (AssignedDevId) o;
        return Objects.equals(toDoItemId, that.toDoItemId) && Objects.equals(employeeId, that.employeeId);
    }

    //hashCode() function override
    
    @Override
    public int hashCode() {
        return Objects.hash(toDoItemId, employeeId);
    }
}