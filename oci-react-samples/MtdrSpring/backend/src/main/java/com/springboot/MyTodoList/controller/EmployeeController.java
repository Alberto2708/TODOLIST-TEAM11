package com.springboot.MyTodoList.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.MyTodoList.model.Employee;
import com.springboot.MyTodoList.model.EmployeeResponse;
import com.springboot.MyTodoList.service.EmployeeService;

import oracle.sql.TRANSDUMP;

import com.springboot.MyTodoList.model.LoginRequest;

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

    @GetMapping(value = "/employees/projectId/{projectId}")
    public List <Employee> getAllEmployeesByProjectId(@PathVariable Integer projectId) {
        try{
            return employeeService.findEmployeeByProjectId(projectId);
        } catch (Exception e){
            return null;
        }
    }
    

    @GetMapping(value = "/employees/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Integer id){
        try{
            ResponseEntity <Employee> responseEntity  = employeeService.findEmployeeById(id);
            return new ResponseEntity <Employee> (responseEntity.getBody(), HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/employees/managerId/{managerId}")
    public List<Employee> getEmployeesByManagerId(@PathVariable Integer managerId) {
        try{
            return employeeService.findByManagerId(managerId);
        } catch (Exception e){
            return null;
        }
    }


    //Post request for login
    //Returns a EmployeeResponse object with the employeeId and managerId if the email and password are correct
    //Returns a 401 status code if the password is incorrect
    //Returns a 404 status code if the email is not found
    @PostMapping(value = "/employees/login")
    public ResponseEntity<EmployeeResponse> getEmployeeByEmail(@RequestBody LoginRequest loginRequest){
        try{
            ResponseEntity <Employee> emp = employeeService.findEmployeeByEmail(loginRequest.getEmail());
            if (emp.getBody().getPassword().equals(loginRequest.getPassword())){
                EmployeeResponse response = new EmployeeResponse(emp.getBody().getID(), emp.getBody().getManagerId());
                return new ResponseEntity<EmployeeResponse>(response, HttpStatus.OK);
            } else {
                System.out.println("Incorrect password");
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e){
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //Post request to validate if employee has telegram Id
    
    @PostMapping(value = "/employees/telegramId/{employeeId}")
    public ResponseEntity<Boolean> getEmployeeByTelegramId(@PathVariable Integer employeeId){
        try{
            Boolean res = employeeService.isEmployeeTelegramIdExists(employeeId);
            if (res == true){
                return new ResponseEntity<>(true, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(false, HttpStatus.OK);
            }
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value = "/employees")
    public ResponseEntity addEmployee(@RequestBody Employee employee) throws Exception{
        //System.out.print(employee.toString());
        Employee emp = employeeService.addEmployee(employee);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("location", "" + emp.getID());
        responseHeaders.set("Access-Control-Expose-Headers", "location");
        return ResponseEntity.ok()
                .headers(responseHeaders).build();
    }

    @PutMapping(value = "employees/{id}")
    public ResponseEntity updateEmployee(@RequestBody Employee employee, @PathVariable Integer id) {
        try{
            Employee emp = employeeService.updateEmployee(id, employee);
            return new ResponseEntity<>(emp, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(value = "employees/{id}")
    public ResponseEntity<Boolean> deleteEmployee(@PathVariable("id") Integer id){
        Boolean flag = false; 
        try{
            flag = employeeService.deleteEmployee(id);
            return new ResponseEntity<>(flag, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(flag, HttpStatus.NOT_FOUND);
        }
    }
}
