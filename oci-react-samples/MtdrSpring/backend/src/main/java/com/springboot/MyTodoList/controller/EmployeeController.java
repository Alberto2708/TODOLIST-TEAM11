package com.springboot.MyTodoList.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.MyTodoList.model.Employee;
import com.springboot.MyTodoList.service.EmployeeService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.HttpStatus;
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


    @GetMapping(value = "/employees")
    public List<Employee> getAllEmployees(){
        return employeeService.findAll();
    }

    @GetMapping(value = "/employees/{id}")
    public ResponseEntity<Employee> getEmployeeById(int id){
        try{
            ResponseEntity <Employee> responseEntity  = employeeService.getEmployeeById(id);
            return new ResponseEntity <Employee> (responseEntity.getBody(), HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //If it aint working, delete <Employee>
    @PostMapping(value = "/employees")
    public ResponseEntity addEmployee(@RequestBody Employee employee) throws Exception{
        Employee emp = employeeService.addEmployee(employee);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("location", "" + emp.getID());
        responseHeaders.set("Access-Control-Expose-Headers", "location");
        return ResponseEntity.ok()
                .headers(responseHeaders).build();
    }

    //If it aint working, delete <Employee>
    @PutMapping(value = "employees/{id}")
    public ResponseEntity updateEmployee(@RequestBody Employee employee, @PathVariable int id) {
        try{
            Employee emp = employeeService.updateEmployee(id, employee);
            System.out.println(emp.toString());
            return new ResponseEntity<>(emp, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(value = "employees/{id}")
    public ResponseEntity<Boolean> deleteEmployee(@PathVariable("id") int id){
        Boolean flag = false; 
        try{
            flag = employeeService.deleteEmployee(id);
            return new ResponseEntity<>(flag, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(flag, HttpStatus.NOT_FOUND);
        }
    }
}
