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


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = com.springboot.MyTodoList.MyTodoListApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SubToDoItemControllerTest {

    //Predefined test variables
    //If database is modified or this specific ids are not present, the test will fail.
    private final int toDoItemIDTestFather = 124;
    private final int toDoItemIDTestChild = 125;

    @Autowired
    private TestRestTemplate restTemplate;

    private static SubToDoItemId subToDoItemId;

    //Test Creation endpoint for SubToDoItem
    @Test
    @Order(1)
    void testAddSubToDoItem() {
        SubToDoItemId subToDoItemIdCreated = new SubToDoItemId(toDoItemIDTestFather, toDoItemIDTestChild);
        SubToDoItem newSubToDoItem = new SubToDoItem(subToDoItemIdCreated);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<SubToDoItem> request = new HttpEntity<>(newSubToDoItem, headers);

        ResponseEntity<SubToDoItemId> response = restTemplate.postForEntity("/subToDoItems", request, SubToDoItemId.class);
        subToDoItemId = response.getBody();
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        System.out.println("SubToDoItem created: Parent ToDoItem = " + subToDoItemId.getToDoItemId() + " - Child ToDoItem = " + subToDoItemId.getSubToDoItemId());
    }


    // Test Get endpoint for SubToDoItem ID
    @Test
    @Order(2)
    void getSubToDoItem() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<SubToDoItem> response = restTemplate.exchange(
            "/subToDoItems/" + subToDoItemId.getToDoItemId() + "/" + subToDoItemId.getSubToDoItemId(),
            HttpMethod.GET,
            request,
            SubToDoItem.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(subToDoItemId.getToDoItemId(), response.getBody().getToDoItemId());
        assertEquals(subToDoItemId.getSubToDoItemId(), response.getBody().getSubToDoItemId());
        System.out.println("SubToDoItem retrieved: FatherToDoItem = " + response.getBody().getToDoItemId() + " - ChildSubToDoItem = " + response.getBody().getSubToDoItemId());
    }

    // Test Deletion endpoint for SubToDoItem
    @Test
    @Order(3)
    void deleteSubToDoItem() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<Boolean> response = restTemplate.exchange(
            "/subToDoItems/" + subToDoItemId.getToDoItemId() + "/" + subToDoItemId.getToDoItemId(),
            HttpMethod.DELETE,
            request,
            Boolean.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody());
        System.out.println("SubToDoItem deleted: FatherToDoItem = " + subToDoItemId.getToDoItemId() + " - ChildSubToDoItem = " + subToDoItemId.getSubToDoItemId());
    }


}
