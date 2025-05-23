package com.springboot.MyTodoList;

import com.springboot.MyTodoList.model.Employee;
import com.springboot.MyTodoList.model.ToDoItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.OffsetDateTime;

/**
 * Integration tests for ToDoItemController endpoints.
 * Tests creation, retrieval, and deletion of ToDoItem entities.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = com.springboot.MyTodoList.MyTodoListApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ToDoItemControllerTest {

     // Test data: Make sure these IDs exist in your test database. Predefined test variables.
    //If database is modified or this specific ids are not present, the test will fail.
    private final int sprintIDTest = 84;
    private final int managerIDTest = 206;

    @Autowired
    private TestRestTemplate restTemplate;

    private static Integer createdToDoItemId;

    /**
     Test the creation endpoint for ToDoItem.
     Verifies that a new ToDoItem can be created successfully.
     **/
    @Test
    @Order(1)
    void testAddToDoItem() {
        ToDoItem newToDoItem = new ToDoItem("SpringBootToDoItem", "PENDING",managerIDTest,OffsetDateTime.parse("2025-04-25T12:34:56+02:00"), OffsetDateTime.parse("2025-04-25T15:34:56+02:00"), sprintIDTest,"ToDoItem created using Springboot tests",3.0);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ToDoItem> request = new HttpEntity<>(newToDoItem, headers);

        ResponseEntity response = restTemplate.postForEntity("/todolist", request, Integer.class);
        createdToDoItemId = (Integer) response.getBody();
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        System.out.println("ToDoItem created: "+ createdToDoItemId);
    }

    /**
     Tests retrieval of an ToDoItem entity by its key via GET /todolist/{toDoItemId}.
     Verifies that the correct entity is returned and the response status is 200 OK.
     **/
    @Test
    @Order(2)
    void testGetToDoItem() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<Employee> response = restTemplate.exchange("/todolist/" + createdToDoItemId, HttpMethod.GET, request, Employee.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(createdToDoItemId, response.getBody().getID());
        System.out.println("ToDoItem retrieved: " + response.getBody().getName() + " with ID: " + response.getBody().getID());
    }

    /**
      Tests updating an ToDoItem entity via PUT /todolist/{id}.
      Verifies that the entity is updated and the response status is 200 OK.
     **/
    @Test
    @Order(2)
    void testUpdateToDoItem(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ToDoItem updatedToDoItem = new ToDoItem();
        updatedToDoItem.setName("UpdatedName");
        updatedToDoItem.setStatus("COMPLETED");
        updatedToDoItem.setStartDate(OffsetDateTime.parse("2025-04-25T12:34:56+02:00"));
        updatedToDoItem.setDeadline(OffsetDateTime.parse("2025-04-25T15:34:56+02:00"));
        updatedToDoItem.setCompletionTs(OffsetDateTime.parse("2025-04-25T15:34:56+02:00"));
        updatedToDoItem.setDescription("Updated description using Springboot tests");
        updatedToDoItem.setEstHours(5.0);


        HttpEntity<ToDoItem> request = new HttpEntity<>(updatedToDoItem, headers);
        ResponseEntity<ToDoItem> response = restTemplate.exchange("/todolist/" + createdToDoItemId, HttpMethod.PUT, request, ToDoItem.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("UpdatedName", response.getBody().getName());
        assertEquals("COMPLETED", response.getBody().getStatus());
        System.out.println(response.getBody().getStartDate());
        System.out.println(response.getBody().getDeadline());
        System.out.println(response.getBody().getCompletionTs());
        assertEquals(OffsetDateTime.parse("2025-04-25T10:34:56Z") , response.getBody().getStartDate());
        assertEquals(OffsetDateTime.parse("2025-04-25T13:34:56Z"), response.getBody().getDeadline());
        assertEquals(OffsetDateTime.parse("2025-04-25T13:34:56Z"), response.getBody().getCompletionTs());
        assertEquals("Updated description using Springboot tests", response.getBody().getDescription());
        assertEquals(5, response.getBody().getEstHours());
        System.out.println("ToDoItem name updated: " + response.getBody().getName() + " with ID: " + response.getBody().getID());
    }

    /**
      Tests deletion of an ToDoItem entity by its key via DELETE /todolist/{toDoItemId}.
      Verifies that the entity is deleted and the response status is 200 OK.
     **/
    @Test
    @Order(3)
    void testDeleteToDoItem() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity response = restTemplate.exchange("/todolist/" + createdToDoItemId, HttpMethod.DELETE, request, Boolean.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Check if the response body is true (indicating successful deletion)
        assertEquals(true, response.getBody());
        // Print the ID of the deleted employee
        System.out.println("ToDoItem deleted: " + createdToDoItemId);
    }


}