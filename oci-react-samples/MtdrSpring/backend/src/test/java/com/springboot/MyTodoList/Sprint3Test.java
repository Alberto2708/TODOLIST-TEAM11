package com.springboot.MyTodoList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.OffsetDateTime;
import java.util.List;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

//Logger imports
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.springboot.MyTodoList.controller.AssignedDevController;
import com.springboot.MyTodoList.model.AssignedDev;
import com.springboot.MyTodoList.model.AssignedDevId;
import com.springboot.MyTodoList.model.ToDoItem;


/**
 * Integration tests for Sprint 3 delivery.
 * Covers:
 * - ToDoItem creation, retrieval, completion, and deletion
 * - Assignment of ToDoItem to a developer
 * - Retrieval of completed ToDoItems by Sprint ID
 * - Retrieval and deletion of AssignedDev by ToDoItem ID
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = com.springboot.MyTodoList.MyTodoListApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class Sprint3Test {

    // Test data: Make sure these IDs exist in your test database. Predefined test variables.
    //If database is modified or this specific ids are not present, the test will fail.
    private final int sprintIDTest = 84;
    private final int managerIDTest = 206;
    private final int developerIDTest = 210;

    @Autowired
    private TestRestTemplate restTemplate;

    //Static variables to store the IDs of the created ToDoItem and AssignedDev for further tests
    private static Integer createdToDoItemId;
    private static AssignedDevId createdAssignedDevId;

    private static final Logger logger = LoggerFactory.getLogger(AssignedDevController.class);


    /**
     Test the creation endpoint for ToDoItem.
     Verifies that a new ToDoItem can be created successfully and saves the ID for further tests.
     **/
    @Test
    @Order(1)
    void testAddToDoItem() {
        ToDoItem newToDoItem = new ToDoItem("Sprint3Test", "PENDING",managerIDTest,OffsetDateTime.parse("2025-04-25T12:34:56+02:00"), OffsetDateTime.parse("2025-04-25T15:34:56+02:00"), sprintIDTest,"ToDoItem created using Springboot tests",3.0);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ToDoItem> request = new HttpEntity<>(newToDoItem, headers);

        ResponseEntity response = restTemplate.postForEntity("/todolist", request, Integer.class);
        createdToDoItemId = (Integer) response.getBody();
        assertEquals(HttpStatus.CREATED, response.getStatusCode());//Validates the response estatus = 200
        assertNotEquals(null, response.getBody());//Validates that the response body is not null, which indicates that the ToDoItem was created successfully and the Id isnt null
        System.out.println("ToDoItem created: "+ createdToDoItemId);
    }

    /**
     Tests retrieval of an ToDoItem entity by its key via GET /todolist/{id}.
     Verifies that the correct entity is returned and the response status is 200 OK.
     **/
    @Test
    @Order(2)
    void testGetToDoItem() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<ToDoItem> response = restTemplate.exchange("/todolist/" + createdToDoItemId, HttpMethod.GET, request, ToDoItem.class);
        System.out.println("ToDoItem retrieved: "+ response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());//Validates the response status = 200
        boolean nameStatus = false;
        //Using regex to validate the name of the ToDoItem, assertEquals with string was failing.
        if (response.getBody().getName().matches("Sprint3Test")) {
            nameStatus = true;
        }
        assertEquals(nameStatus, true);
    }

    /**
     * Tests completion of a ToDoItem via PUT /todolist/complete/{id}.
     * Verifies that the status is updated to "COMPLETED".
     */
    @Test
    @Order(2)
    void testCompleteToDoItem() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<ToDoItem> response = restTemplate.exchange("/todolist/complete/" + createdToDoItemId, HttpMethod.PUT, request, ToDoItem.class);
        assertEquals(HttpStatus.OK, response.getStatusCode()); //Validates the response estatus = 200
        assertEquals("COMPLETED", response.getBody().getStatus()); //Validates that the status of the ToDoItem is "COMPLETED"
        assertEquals(createdToDoItemId, response.getBody().getID()); //Validates that the ID of the ToDoItem is the same as the one created in the previous test
        System.out.println("ToDoItem completed: "+ createdToDoItemId);
    }

    /**
     * Tests assignment of the created ToDoItem to a developer via POST /assignedDev.
     * Verifies that the AssignedDev entity is created.
     */
    @Test
    @Order(2)
    void testAssignToDoItem() {
        AssignedDevId assignedDevIdCreated = new AssignedDevId(createdToDoItemId, developerIDTest);
        AssignedDev newAssignedDev = new AssignedDev(assignedDevIdCreated);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AssignedDev> request = new HttpEntity<>(newAssignedDev, headers);

        ResponseEntity response = restTemplate.postForEntity("/assignedDev", request, AssignedDevId.class);
        createdAssignedDevId = (AssignedDevId) response.getBody();
        assertEquals(HttpStatus.CREATED, response.getStatusCode()); //Validates the response estatus = 200
        assertNotEquals(null, response.getBody()); //Validates that the response body is not null, which indicates that the AssignedDev was created successfully and the Id isnt null   
        logger.info("AssignedDev created: " + createdAssignedDevId);
    }

    /**
     * Tests retrieval of completed ToDoItems by Sprint ID via GET /todolist/sprint/{sprintId}/completed.
     * Verifies that the response status is 200 OK.
     * Previosly assigned and completed ToDoItem must be present in the response.
     */
    @Test
    @Order(3)
    void testGetCompletedToDoItemsBySprintId() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<List> response = restTemplate.exchange("/todolist/sprint/" + sprintIDTest + "/completed", HttpMethod.GET, request, List.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        logger.info("Completed ToDoItems retrieved: " + response.getBody());
    }

    /**
     * Tests retrieval of AssignedDev by developer and sprint via GET /assignedDev/{developerId}/sprint/{sprintId}/completed.
     * Verifies that the response status is 200 OK.
     */
    @Test
    @Order(3)
    void testGetAssignedDevByToDoItemId() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<List> response = restTemplate.exchange("/assignedDev/" + developerIDTest + "/sprint/" + sprintIDTest + "/completed", HttpMethod.GET, request, List.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        logger.info("AssignedDev retrieved: " + response.getBody());
    }

    /**
     * Tests deletion of the AssignedDev entity via DELETE /assignedDev/{assignedDevId}.
     * Verifies that the response status is 200 OK.
     */
    @Test
    @Order(4)
    void testDeleteAssignedDev() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<Boolean> response = restTemplate.exchange("/assignedDev/" + createdAssignedDevId.getToDoItemId()+"/"+createdAssignedDevId.getEmployeeId(), HttpMethod.DELETE, request, Boolean.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody());
        logger.info("AssignedDev deleted: " + createdAssignedDevId);
    }

    /**
     * Tests deletion of the ToDoItem entity via DELETE /todolist/{id}.
     * Verifies that the response status is 200 OK.
     */
    @Test
    @Order(5)
    void testDeleteToDoItem() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<Boolean> response = restTemplate.exchange("/todolist/" + createdToDoItemId, HttpMethod.DELETE, request, Boolean.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody());
        logger.info("ToDoItem deleted: " + createdToDoItemId);
    }

}