//Model to save the petition for login, just the employeeId and the managerId
package com.springboot.MyTodoList.model;

public class LoginRequest {

    //Attributes
    public String email;
    public String password;

    //Constructor
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    //Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
