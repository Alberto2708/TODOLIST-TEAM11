// Controller for managing employee Entities in the ToDoList application
package com.springboot.MyTodoList.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.MyTodoList.model.Employee;
import com.springboot.MyTodoList.model.EmployeeResponse;
import com.springboot.MyTodoList.service.EmployeeService;
import com.springboot.MyTodoList.service.AssignedDevService;

import oracle.sql.TRANSDUMP;

import com.springboot.MyTodoList.model.LoginRequest;
import com.springboot.MyTodoList.model.Sprint;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private AssignedDevService assignedDevService;

    // -------------------- GET --------------------
    // All Employees
    @GetMapping(value = "/employees")
    public List<Employee> getAllEmployees() {
        return employeeService.findAll();
    }

    // All Employees by Project ID
    @GetMapping(value = "/employees/projectId/{projectId}")
    public List<Employee> getAllEmployeesByProjectId(@PathVariable Integer projectId) {
        try {
            return employeeService.findEmployeeByProjectId(projectId);
        } catch (Exception e) {
            return null;
        }
    }

    // Employee by ID
    @GetMapping(value = "/employees/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Integer id) {
        try {
            ResponseEntity<Employee> responseEntity = employeeService.findEmployeeById(id);
            return new ResponseEntity<Employee>(responseEntity.getBody(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Employee by Manager ID
    @GetMapping(value = "/employees/managerId/{managerId}")
    public List<Employee> getEmployeesByManagerId(@PathVariable Integer managerId) {
        try {
            return employeeService.findByManagerId(managerId);
        } catch (Exception e) {
            return null;
        }
    }

    // -------------------- POST --------------------
    // Request to add a new employee
    @PostMapping(value = "/employees")
    public ResponseEntity addEmployee(@RequestBody Employee employee) throws Exception {
        try {
            Employee emp = employeeService.addEmployee(employee);
            Integer responseEntity = emp.getID();
            return new ResponseEntity<>(responseEntity, HttpStatus.CREATED);

        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    // Request for login
    //Returns a EmployeeResponse object with the employeeId and managerId if the email and password are correct
    //Returns a 401 status code if the password is incorrect
    //Returns a 404 status code if the email is not found
    @PostMapping(value = "/employees/login")
    public ResponseEntity<EmployeeResponse> getEmployeeByEmail(@RequestBody LoginRequest loginRequest) {
        try {
            ResponseEntity<Employee> emp = employeeService.findEmployeeByEmail(loginRequest.getEmail());
            if (emp.getBody().getPassword().equals(loginRequest.getPassword())) {
                EmployeeResponse response = new EmployeeResponse(emp.getBody().getID(), emp.getBody().getManagerId(), emp.getBody().getProjectId());
                return new ResponseEntity<EmployeeResponse>(response, HttpStatus.OK);
            } else {
                System.out.println("Incorrect password");
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Request to validate if employee has telegram Id
    @PostMapping(value = "/employees/telegramId/{employeeId}")
    public ResponseEntity<Boolean> getEmployeeByTelegramId(@PathVariable Integer employeeId) {
        try {
            Boolean res = employeeService.doesEmployeeTelegramIdExists(employeeId);
            if (res == true) {
                return new ResponseEntity<>(true, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(false, HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // -------------------- PUT --------------------
    // Employee by ID
    @PutMapping(value = "/employees/{id}")
    public ResponseEntity updateEmployee(@RequestBody Employee employee, @PathVariable Integer id) {
        try {
            Employee emp = employeeService.updateEmployee(id, employee);
            return new ResponseEntity<>(emp, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // -------------------- DELETE --------------------
    // Employee by ID
    @DeleteMapping(value = "/employees/{id}")
    public ResponseEntity<Boolean> deleteEmployee(@PathVariable("id") Integer id) {
        Boolean flag = false;
        try {
            List<Boolean> flags = new ArrayList<>();
            flags.add(assignedDevService.deleteAssignedDevByAssignedDevId(id));
            flags.add(employeeService.deleteEmployee(id));
            for (Boolean f : flags) {
                if (f == null) {
                    return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
                }
                if (f == false) {
                    return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
            flag = true;
            return new ResponseEntity<>(flag, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
