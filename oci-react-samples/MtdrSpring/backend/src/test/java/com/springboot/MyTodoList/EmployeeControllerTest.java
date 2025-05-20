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

/**
 * Integration tests for EmployeeController endpoints.
 * Tests creation, retrieval, and deletion of Employee entities.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = com.springboot.MyTodoList.MyTodoListApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeControllerTest {

    // Test data: Make sure these IDs exist in your test database. Predefined test variables.
    //If database is modified or this specific ids are not present, the test will fail.
    private final int projectIDTest = 83;
    private final int managerIDTest = 206;

    @Autowired
    private TestRestTemplate restTemplate;

    private static Integer createdEmployeeId; 

    /**
     Test the creation endpoint for Employee.
     Verifies that a new Employee can be created successfully.
     **/
    @Test
    @Order(1)
    void testAddEmployee() {
        Employee newEmployee = new Employee("SpringBootEmployee", managerIDTest, "spring@test.com", "password", projectIDTest);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Employee> request = new HttpEntity<>(newEmployee, headers);

        ResponseEntity response = restTemplate.postForEntity("/employees", request, Integer.class);
        createdEmployeeId = (Integer) response.getBody();
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        System.out.println("Employee created: "+ createdEmployeeId);
    }


    /**
     Tests retrieval of an Employee entity by its key via GET /employees/{createdEmployeeId}.
     Verifies that the correct entity is returned and the response status is 200 OK.
     **/
    @Test
    @Order(2)
    void testGetEmployee() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<Employee> response = restTemplate.exchange("/employees/" + createdEmployeeId, HttpMethod.GET, request, Employee.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(createdEmployeeId, response.getBody().getID());
        System.out.println("Employee retrieved: " + response.getBody().getName() + " with ID: " + response.getBody().getID());
    }

    /**
      Tests updating an Employee entity via PUT /employees/{id}.
      Verifies that the entity is updated and the response status is 200 OK.
     **/
    @Test
    @Order(2)
    void testUpdateEmployee(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        
        final Long inventedTelegramId = -5L; 
        Employee updatedEmployee = new Employee();
        updatedEmployee.setName("UpdatedName");
        updatedEmployee.setEmail("updated10001@gmail.com");
        updatedEmployee.setPassword("updatedPassword");
        updatedEmployee.setTelegramId(inventedTelegramId);

        HttpEntity<Employee> request = new HttpEntity<>(updatedEmployee, headers);
        ResponseEntity<Employee> response = restTemplate.exchange("/employees/" + createdEmployeeId, HttpMethod.PUT, request, Employee.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("UpdatedName", response.getBody().getName());
        assertEquals("updated10001@gmail.com", response.getBody().getEmail());
        assertEquals("updatedPassword", response.getBody().getPassword());
        assertEquals(inventedTelegramId, response.getBody().getTelegramId());
        System.out.println("Employee name updated: " + response.getBody().getName() + " with ID: " + response.getBody().getID());
    }

    /**
      Tests deletion of an Employee entity by its key via DELETE /employees/{createdEmployeeId}.
      Verifies that the entity is deleted and the response status is 200 OK.
     **/
    @Test
    @Order(3)
    void testDeleteEmployee() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity response = restTemplate.exchange("/employees/" + createdEmployeeId, HttpMethod.DELETE, request, Boolean.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody());
        System.out.println("Employee deleted: " + createdEmployeeId);
    }


}
