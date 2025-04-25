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


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = com.springboot.MyTodoList.MyTodoListApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ToDoItemControllerTest {

     //Predefined test variables
    //If database is modified or this specific ids are not present, the test will fail.
    private final int sprintIDTest = 84;
    private final int managerIDTest = 206;

    @Autowired
    private TestRestTemplate restTemplate;

    private static Integer toDoItemID;

    //Test Creation endpoint for ToDoItem
    @Test
    @Order(1)
    void testAddToDoItem() {
        ToDoItem newToDoItem = new ToDoItem("SpringBootToDoItem", "PENDING",managerIDTest,OffsetDateTime.parse("2025-04-25T12:34:56+02:00"), OffsetDateTime.parse("2025-04-25T15:34:56+02:00"), sprintIDTest,"ToDoItem created using Springboot tests",3);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ToDoItem> request = new HttpEntity<>(newToDoItem, headers);

        ResponseEntity response = restTemplate.postForEntity("/todolist", request, Integer.class);
        toDoItemID = (Integer) response.getBody();
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        System.out.println("ToDoItem created: "+ toDoItemID);
    }

    //Test get endpoint for Employee by ID
    @Test
    @Order(2)
    void getToDoItemById() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<Employee> response = restTemplate.exchange("/todolist/" + toDoItemID, HttpMethod.GET, request, Employee.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(toDoItemID, response.getBody().getID());
        System.out.println("ToDoItem retrieved: " + response.getBody().getName());
    }

    //Test deletion endpoint for ToDoItem by ID
    @Test
    @Order(3)
    void deleteToDoItem() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity response = restTemplate.exchange("/todolist/" + toDoItemID, HttpMethod.DELETE, request, Boolean.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Check if the response body is true (indicating successful deletion)
        assertEquals(true, response.getBody());
        // Print the ID of the deleted employee
        System.out.println("ToDoItem deleted: " + toDoItemID);
    }


}