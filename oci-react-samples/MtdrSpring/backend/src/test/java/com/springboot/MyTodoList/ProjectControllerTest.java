package com.springboot.MyTodoList;

import com.springboot.MyTodoList.model.Project;

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
 * Integration tests for ProjectController endpoints.
 * Tests creation, retrieval, and deletion of Project entities.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = com.springboot.MyTodoList.MyTodoListApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProjectControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static Integer projectId;

    /**
     Test the creation endpoint for Project.
     Verifies that a new Project can be created successfully.
     **/
    @Test
    @Order(1)
    void testAddProject() {
        Project newProject = new Project("SpringBootProject");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Project> request = new HttpEntity<>(newProject, headers);

        ResponseEntity<Integer> response = restTemplate.postForEntity("/projects", request, Integer.class);
        projectId = response.getBody();
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        System.out.println("Project created: " + projectId);
    }


    /**
     Tests retrieval of an Project entity by its key via GET /project/{projectId}.
     Verifies that the correct entity is returned and the response status is 200 OK.
     **/
    @Test
    @Order(2)
    void testGetProject() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<Project> response = restTemplate.exchange("/project/" + projectId, HttpMethod.GET, request, Project.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(projectId, response.getBody().getID());
        System.out.println("Project retrieved: " + response.getBody().getName() + " with ID: " + response.getBody().getID());
    }

    /**
      Tests updating an Project entity via PUT /projects/{id}.
      Verifies that the entity is updated and the response status is 200 OK.
     **/
    @Test
    @Order(2)
    void testUpdateProject(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Project updatedProject = new Project();
        updatedProject.setName("UpdatedProjectName");

        HttpEntity<Project> request = new HttpEntity<>(updatedProject, headers);
        ResponseEntity<Project> response = restTemplate.exchange("/projects/" + projectId, HttpMethod.PUT, request, Project.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("UpdatedProjectName", response.getBody().getName());
        assertEquals(projectId, response.getBody().getID());
        System.out.println("Project name updated: " + response.getBody().getName() + " with ID: " + response.getBody().getID());
    }

    /**
      Tests deletion of an Project entity by its key via DELETE /projects/{projectId}.
      Verifies that the entity is deleted and the response status is 200 OK.
     **/
    @Test
    @Order(3)
    void testDeleteProject() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<Boolean> response = restTemplate.exchange("/projects/" + projectId, HttpMethod.DELETE, request, Boolean.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody());
        System.out.println("Project deleted: " + projectId);
    }
}
