package com.springboot.MyTodoList;

import com.springboot.MyTodoList.model.SubToDoItem;
import com.springboot.MyTodoList.model.SubToDoItemId;

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
 * Integration tests for SubToDoItemController endpoints.
 * Tests creation, retrieval, and deletion of SubToDoItem entities.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = com.springboot.MyTodoList.MyTodoListApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SubToDoItemControllerTest {

    // Test data: Make sure these IDs exist in your test database. Predefined test variables.
    //If database is modified or this specific ids are not present, the test will fail.
    private final int toDoItemIDTestFather = 124;
    private final int toDoItemIDTestChild = 125;

    @Autowired
    private TestRestTemplate restTemplate;

    // SubToDoItemId is used to save the composite key of the SubToDoItem entity.
    private static SubToDoItemId createdSubToDoItemId;

    /**
     Test the creation endpoint for SubToDoItem.
     Verifies that a new SubToDoItem can be created successfully.
     **/
    @Test
    @Order(1)
    void testAddSubToDoItem() {
        SubToDoItemId subToDoItemIdCreated = new SubToDoItemId(toDoItemIDTestFather, toDoItemIDTestChild);
        SubToDoItem newSubToDoItem = new SubToDoItem(subToDoItemIdCreated);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<SubToDoItem> request = new HttpEntity<>(newSubToDoItem, headers);

        ResponseEntity<SubToDoItemId> response = restTemplate.postForEntity("/subToDoItems", request, SubToDoItemId.class);
        createdSubToDoItemId = response.getBody();
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        System.out.println("SubToDoItem created: Parent ToDoItem = " + createdSubToDoItemId.getToDoItemId() + " - Child ToDoItem = " + createdSubToDoItemId.getSubToDoItemId());
    }


    /**
     Tests retrieval of an SubToDoItem entity by its composite key via GET /subToDoItems/{toDoItemId}/{subToDoItemId}.
     Verifies that the correct entity is returned and the response status is 200 OK.
     **/
    @Test
    @Order(2)
    void testGetSubToDoItem() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<SubToDoItem> response = restTemplate.exchange(
            "/subToDoItems/" + createdSubToDoItemId.getToDoItemId() + "/" + createdSubToDoItemId.getSubToDoItemId(),
            HttpMethod.GET,
            request,
            SubToDoItem.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(createdSubToDoItemId.getToDoItemId(), response.getBody().getToDoItemId());
        assertEquals(createdSubToDoItemId.getSubToDoItemId(), response.getBody().getSubToDoItemId());
        System.out.println("SubToDoItem retrieved: FatherToDoItem = " + response.getBody().getToDoItemId() + " - ChildSubToDoItem = " + response.getBody().getSubToDoItemId());
    }

    /**
      Tests deletion of an SubToDoItem entity by its composite key via DELETE /subToDoItems/{toDoItemId}/{subToDoItemId}.
      Verifies that the entity is deleted and the response status is 200 OK.
     **/
    @Test
    @Order(3)
    void testDeleteSubToDoItem() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<Boolean> response = restTemplate.exchange(
            "/subToDoItems/" + createdSubToDoItemId.getToDoItemId() + "/" + createdSubToDoItemId.getSubToDoItemId(),
            HttpMethod.DELETE,
            request,
            Boolean.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody());
        System.out.println("SubToDoItem deleted: FatherToDoItem = " + createdSubToDoItemId.getToDoItemId() + " - ChildSubToDoItem = " + createdSubToDoItemId.getSubToDoItemId());
    }


}
