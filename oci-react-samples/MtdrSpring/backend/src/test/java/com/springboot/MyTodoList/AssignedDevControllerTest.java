package com.springboot.MyTodoList;

import com.springboot.MyTodoList.model.AssignedDev;
import com.springboot.MyTodoList.model.AssignedDevId;

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
 * Integration tests for AssignedDevController endpoints.
 * Tests creation, retrieval, and deletion of AssignedDev entities.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = com.springboot.MyTodoList.MyTodoListApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AssignedDevControllerTest {

    // Test data: Make sure these IDs exist in your test database. Predefined test variables.
    //If database is modified or this specific ids are not present, the test will fail.
    private final int developerIDTest = 210;
    private final int toDoItemIDTest = 124;

    @Autowired
    private TestRestTemplate restTemplate;

    // AssignedDevId is used to save the composite key of the AssignedDev entity.
    private static AssignedDevId assignedDevID;

    /**
     Test the creation endpoint for AssignedDev.
     Verifies that a new AssignedDev can be created successfully.
     **/
    @Test
    @Order(1)
    void testAddAssignedDev() {
        AssignedDevId assignedDevId = new AssignedDevId(toDoItemIDTest, developerIDTest);
        AssignedDev newAssignedDev = new AssignedDev(assignedDevId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AssignedDev> request = new HttpEntity<>(newAssignedDev, headers);

        ResponseEntity response = restTemplate.postForEntity("/assignedDev", request, AssignedDevId.class);
        assignedDevID = (AssignedDevId) response.getBody();
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        System.out.println("AssignedDev created: ToDoItem = "+ assignedDevID.getToDoItemId() + " - Employee = " + assignedDevID.getEmployeeId());
    }


    /**
     Tests retrieval of an AssignedDev entity by its composite key via GET /assignedDev/{toDoItemId}/{employeeId}.
     Verifies that the correct entity is returned and the response status is 200 OK.
     **/
    @Test
    @Order(2)
    void testGetAssignedDev() {
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

    /**
      Tests deletion of an AssignedDev entity by its composite key via DELETE /assignedDev/{toDoItemId}/{employeeId}.
      Verifies that the entity is deleted and the response status is 200 OK.
     **/
    @Test
    @Order(3)
    void testDeleteAssignedDev() {
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
