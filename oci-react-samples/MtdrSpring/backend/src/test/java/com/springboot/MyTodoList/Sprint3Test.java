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



@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = com.springboot.MyTodoList.MyTodoListApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class Sprint3Test {

    //Predefined test variables
    //If database is modified or this specific ids are not present, the test will fail.
    private final int sprintIDTest = 84;
    private final int managerIDTest = 206;
    private final int developerIDTest = 210;

    @Autowired
    private TestRestTemplate restTemplate;

    private static Integer toDoItemID;

    private static AssignedDevId assignedDevID;

    private static final Logger logger = LoggerFactory.getLogger(AssignedDevController.class);


    //Test creates a ToDoItem and stores the ID for further tests in toDoItemID
    @Test
    @Order(1)
    void testAddToDoItem() {
        ToDoItem newToDoItem = new ToDoItem("Sprint3Test", "PENDING",managerIDTest,OffsetDateTime.parse("2025-04-25T12:34:56+02:00"), OffsetDateTime.parse("2025-04-25T15:34:56+02:00"), sprintIDTest,"ToDoItem created using Springboot tests",3);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ToDoItem> request = new HttpEntity<>(newToDoItem, headers);

        ResponseEntity response = restTemplate.postForEntity("/todolist", request, Integer.class);
        toDoItemID = (Integer) response.getBody();
        assertEquals(HttpStatus.CREATED, response.getStatusCode());//Validates the response estatus = 200
        assertNotEquals(null, response.getBody());//Validates that the response body is not null, which indicates that the ToDoItem was created successfully and the Id isnt null
        System.out.println("ToDoItem created: "+ toDoItemID);
    }

    //Test get endpoint for ToDoItem by ID
    //This test retrieves the ToDoItem created in the previous test
    @Test
    @Order(2)
    void getToDoItem() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<ToDoItem> response = restTemplate.exchange("/todolist/" + toDoItemID, HttpMethod.GET, request, ToDoItem.class);
        System.out.println("ToDoItem retrieved: "+ response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());//Validates the response estatus = 200
        boolean nameStatus = false;
        if (response.getBody().getName().matches("Sprint3Test")) {
            nameStatus = true;
        }
        assertEquals(nameStatus, true);
    }

    //Testing completion endpoint for previously created ToDoItem
    @Test
    @Order(2)
    void completeToDoItem() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<ToDoItem> response = restTemplate.exchange("/todolist/complete/" + toDoItemID, HttpMethod.PUT, request, ToDoItem.class);
        assertEquals(HttpStatus.OK, response.getStatusCode()); //Validates the response estatus = 200
        assertEquals("COMPLETED", response.getBody().getStatus()); //Validates that the status of the ToDoItem is "COMPLETED"
        assertEquals(toDoItemID, response.getBody().getID()); //Validates that the ID of the ToDoItem is the same as the one created in the previous test
        System.out.println("ToDoItem completed: "+ toDoItemID);
    }

    //Assign the created ToDoItem to a developer created for testing purposes
    @Test
    @Order(2)
    void assignToDoItem() {
        AssignedDevId assignedDevIdCreated = new AssignedDevId(toDoItemID, developerIDTest);
        AssignedDev newAssignedDev = new AssignedDev(assignedDevIdCreated);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AssignedDev> request = new HttpEntity<>(newAssignedDev, headers);

        ResponseEntity response = restTemplate.postForEntity("/assignedDev", request, AssignedDevId.class);
        assignedDevID = (AssignedDevId) response.getBody();
        assertEquals(HttpStatus.CREATED, response.getStatusCode()); //Validates the response estatus = 200
        assertNotEquals(null, response.getBody()); //Validates that the response body is not null, which indicates that the AssignedDev was created successfully and the Id isnt null   
        logger.info("AssignedDev created: " + assignedDevID);
    }

    //Test get endpoint for AssignedDev by ID
    @Test
    @Order(3)
    void getCompletedToDoItemsBySprintId() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<List> response = restTemplate.exchange("/todolist/sprint/" + sprintIDTest + "/completed", HttpMethod.GET, request, List.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        logger.info("Completed ToDoItems retrieved: " + response.getBody());
    }

    @Test
    @Order(3)
    void getAssignedDevByToDoItemId() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<List> response = restTemplate.exchange("/assignedDev/" + developerIDTest + "/sprint/" + sprintIDTest + "/completed", HttpMethod.GET, request, List.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        logger.info("AssignedDev retrieved: " + response.getBody());
    }

}