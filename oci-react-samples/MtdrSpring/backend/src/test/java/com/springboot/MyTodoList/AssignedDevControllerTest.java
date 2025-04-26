package com.springboot.MyTodoList;

import com.springboot.MyTodoList.model.AssignedDev;
import com.springboot.MyTodoList.model.AssignedDevId;
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
public class AssignedDevControllerTest {

    //Predefined test variables
    //If database is modified or this specific ids are not present, the test will fail.
    private final int developerIDTest = 210;
    private final int toDoItemIDTest = 124;

    @Autowired
    private TestRestTemplate restTemplate;

    private static AssignedDevId assignedDevID;

    //Test Creation endpoint for AssignedDev
    @Test
    @Order(1)
    void testAddAssignedDev() {
        AssignedDevId assignedDevIdCreated = new AssignedDevId(toDoItemIDTest, developerIDTest);
        AssignedDev newAssignedDev = new AssignedDev(assignedDevIdCreated);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AssignedDev> request = new HttpEntity<>(newAssignedDev, headers);

        ResponseEntity response = restTemplate.postForEntity("/assignedDev", request, AssignedDevId.class);
        assignedDevID = (AssignedDevId) response.getBody();
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        System.out.println("AssignedDev created: ToDoItem = "+ assignedDevID.getToDoItemId() + " - Employee = " + assignedDevID.getEmployeeId());
    }


    // Test Get endpoint for AssignedDev by ID
    @Test
    @Order(2)
    void getAssignedDev() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<AssignedDev> response = restTemplate.exchange(
            "/assignedDev/" + assignedDevID.getToDoItemId() + "/" + assignedDevID.getEmployeeId(),
            HttpMethod.GET,
            request,
            AssignedDev.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(assignedDevID.getToDoItemId(), response.getBody().getToDoItemId());
        assertEquals(assignedDevID.getEmployeeId(), response.getBody().getAssignedDevId());
        System.out.println("AssignedDev retrieved: ToDoItem = " + response.getBody().getToDoItemId() + " - Employee = " + response.getBody().getAssignedDevId());
    }

    // Test Deletion endpoint for AssignedDev
    @Test
    @Order(3)
    void deleteAssignedDev() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<Boolean> response = restTemplate.exchange(
            "/assignedDev/" + assignedDevID.getToDoItemId() + "/" + assignedDevID.getEmployeeId(),
            HttpMethod.DELETE,
            request,
            Boolean.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody());
        System.out.println("AssignedDev deleted: ToDoItem = " + assignedDevID.getToDoItemId() + " - Employee = " + assignedDevID.getEmployeeId());
    }


}
