//Model to save the response for login, just the employeeId and the managerId
package com.springboot.MyTodoList.model;

public class EmployeeResponse {

    //Attributes
    
    public Integer employeeId;
    public Integer managerId;

    //Constructor

    public EmployeeResponse(Integer employeeId, Integer managerId) {
        this.employeeId = employeeId;
        this.managerId = managerId;
    }

    //Getters and Setters

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }
}
