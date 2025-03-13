package com.springboot.MyTodoList.model;

public class EmployeeResponse {
    public int employeeId;
    public Integer managerId;

    public EmployeeResponse(int employeeId, Integer managerId) {
        this.employeeId = employeeId;
        this.managerId = managerId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }
}
