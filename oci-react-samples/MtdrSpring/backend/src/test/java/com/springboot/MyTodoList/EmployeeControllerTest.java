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

    @Autowired
    private TestRestTemplate restTemplate;

    private static Integer employeeId; // Assuming you have an employee with ID 1 in your database

    @Test
    @Order(1)
    void testAddEmployee() {
        Employee newEmployee = new Employee("SpringBoot", 1, "spring@test.com", "password", 1);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Employee> request = new HttpEntity<>(newEmployee, headers);

        ResponseEntity response = restTemplate.postForEntity("/employees", request, Integer.class);
        employeeId = (Integer) response.getBody();
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        System.out.println("Employee created: "+ employeeId);
    }

    @Test
    @Order(2)
    void deleteEmployee() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity response = restTemplate.exchange("/employees/" + employeeId, HttpMethod.DELETE, request, Boolean.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        System.out.println("Employee deleted: " + employeeId);
    }

}
