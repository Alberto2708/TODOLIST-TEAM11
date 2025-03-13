package com.springboot.MyTodoList.model;

public class EmployeeResponse {
    public Integer employeeId;
    public Integer managerId;

    public EmployeeResponse(Integer employeeId, Integer managerId) {
        this.employeeId = employeeId;
        this.managerId = managerId;
    }

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
