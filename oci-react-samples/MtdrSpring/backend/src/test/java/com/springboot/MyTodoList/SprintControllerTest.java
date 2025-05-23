package com.springboot.MyTodoList;
import com.springboot.MyTodoList.model.Sprint;

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
 * Integration tests for SprintController endpoints.
 * Tests creation, retrieval, and deletion of Sprint entities.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = com.springboot.MyTodoList.MyTodoListApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SprintControllerTest {

    // Test data: Make sure these IDs exist in your test database. Predefined test variables.
    //If database is modified or this specific ids are not present, the test will fail.
    private final int projectIDTest = 83;

    @Autowired
    private TestRestTemplate restTemplate;

    private static Integer createdSprintId;

    /**
     Test the creation endpoint for Sprint.
     Verifies that a new Sprint can be created successfully.
     **/
    @Test
    @Order(1)
    void testAddSprint() {
        Sprint newSprint = new Sprint(projectIDTest, "SpringBootSprint", OffsetDateTime.parse("2025-04-25T12:34:56+02:00"), OffsetDateTime.parse("2025-06-04T15:34:56+02:00"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Sprint> request = new HttpEntity<>(newSprint, headers);

        ResponseEntity<Integer> response = restTemplate.postForEntity("/sprint", request, Integer.class);
        createdSprintId = response.getBody();
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        System.out.println("Sprint created: " + createdSprintId);
    }


     /**
     Tests retrieval of an Sprint entity by its key via GET /sprint/{sprintId}.
     Verifies that the correct entity is returned and the response status is 200 OK.
     **/
    @Test
    @Order(2)
    void testGetSprint() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<Sprint> response = restTemplate.exchange("/sprint/" + createdSprintId, HttpMethod.GET, request, Sprint.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(createdSprintId, response.getBody().getID());
        System.out.println("Sprint retrieved: " + response.getBody().getName() + " with ID: " + response.getBody().getID());
    }

    /**
      Tests updating an Sprint entity via PUT /sprint/{sprintId}.
      Verifies that the entity is updated and the response status is 200 OK.
     **/
    @Test
    @Order(2)
    void testUpdateSprint(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Sprint updatedSprint = new Sprint();
        updatedSprint.setName("UpdatedSprintName");
        updatedSprint.setStartDate(OffsetDateTime.parse("2025-04-25T12:34:56+02:00"));
        updatedSprint.setEndDate(OffsetDateTime.parse("2025-06-04T15:34:56+02:00"));

        HttpEntity<Sprint> request = new HttpEntity<>(updatedSprint, headers);
        ResponseEntity<Sprint> response = restTemplate.exchange("/sprint/" + createdSprintId, HttpMethod.PUT, request, Sprint.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("UpdatedSprintName", response.getBody().getName());
        assertEquals(OffsetDateTime.parse("2025-04-25T10:34:56Z"), response.getBody().getStartDate());
        assertEquals(OffsetDateTime.parse("2025-06-04T13:34:56Z"), response.getBody().getEndDate());
        System.out.println("Sprint name updated: " + response.getBody().getName() + " with ID: " + response.getBody().getID());
    }

    /**
      Tests deletion of an Sprint entity by its key via DELETE /sprint/{sprintId}.
      Verifies that the entity is deleted and the response status is 200 OK.
     **/
    @Test
    @Order(3)
    void testDeleteSprint() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<Boolean> response = restTemplate.exchange("/sprint/" + createdSprintId, HttpMethod.DELETE, request, Boolean.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody());
        System.out.println("Sprint deleted: " + createdSprintId);
    }
}
