package com.springboot.MyTodoList;

import com.springboot.MyTodoList.model.Employee;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.assertEquals;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = com.springboot.MyTodoList.MyTodoListApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeControllerTest {

    //Predefined test variables
    //If database is modified or this specific ids are not present, the test will fail.
    private final int projectIDTest = 83;
    private final int managerIDTest = 206;

    @Autowired
    private TestRestTemplate restTemplate;

    private static Integer employeeId; 

    //Test Creation endpoint for Employee
    @Test
    @Order(1)
    void testAddEmployee() {
        Employee newEmployee = new Employee("SpringBootEmployee", managerIDTest, "spring@test.com", "password", projectIDTest);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Employee> request = new HttpEntity<>(newEmployee, headers);

        ResponseEntity response = restTemplate.postForEntity("/employees", request, Integer.class);
        employeeId = (Integer) response.getBody();
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        System.out.println("Employee created: "+ employeeId);
    }


    //Test get endpoint for Employee by ID
    @Test
    @Order(2)
    void getEmployee() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<Employee> response = restTemplate.exchange("/employees/" + employeeId, HttpMethod.GET, request, Employee.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(employeeId, response.getBody().getID());
        System.out.println("Employee retrieved: " + response.getBody().getName() + " with ID: " + response.getBody().getID());
    }

    //Test update endpoint for Employee
    @Test
    @Order(2)
    void updateEmployee(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Employee updatedEmployee = new Employee();
        updatedEmployee.setName("UpdatedName");
        updatedEmployee.setEmail("updated10001@gmail.com");
        updatedEmployee.setPassword("updatedPassword");
        updatedEmployee.setTelegramId(-5L);

        HttpEntity<Employee> request = new HttpEntity<>(updatedEmployee, headers);
        ResponseEntity<Employee> response = restTemplate.exchange("/employees/" + employeeId, HttpMethod.PUT, request, Employee.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("UpdatedName", response.getBody().getName());
        assertEquals("updated10001@gmail.com", response.getBody().getEmail());
        assertEquals("updatedPassword", response.getBody().getPassword());
        assertEquals(-5, response.getBody().getTelegramId());
        System.out.println("Employee name updated: " + response.getBody().getName() + " with ID: " + response.getBody().getID());
    }

    //Test deletion endpoint for Employee
    @Test
    @Order(3)
    void deleteEmployee() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity response = restTemplate.exchange("/employees/" + employeeId, HttpMethod.DELETE, request, Boolean.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        System.out.println("Employee deleted: " + employeeId);
    }


}
