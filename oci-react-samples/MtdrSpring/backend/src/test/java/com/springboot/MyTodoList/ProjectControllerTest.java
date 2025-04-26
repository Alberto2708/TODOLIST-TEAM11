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


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = com.springboot.MyTodoList.MyTodoListApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProjectControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static Integer projectId;

    //Test Creation endpoint for Project
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


    //Test get endpoint for Project by ID
    @Test
    @Order(2)
    void getProject() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<Project> response = restTemplate.exchange("/project/" + projectId, HttpMethod.GET, request, Project.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(projectId, response.getBody().getID());
        System.out.println("Project retrieved: " + response.getBody().getName() + " with ID: " + response.getBody().getID());
    }

    //Test update endpoint for Project
    @Test
    @Order(2)
    void updateProject(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Project updatedProject = new Project();
        updatedProject.setName("UpdatedProjectName");

        HttpEntity<Project> request = new HttpEntity<>(updatedProject, headers);
        ResponseEntity<Project> response = restTemplate.exchange("/projects/" + projectId, HttpMethod.PUT, request, Project.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("UpdatedProjectName", response.getBody().getName());
        System.out.println("Project name updated: " + response.getBody().getName() + " with ID: " + response.getBody().getID());
    }

    //Test deletion endpoint for Project
    @Test
    @Order(3)
    void deleteProject() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<Boolean> response = restTemplate.exchange("/projects/" + projectId, HttpMethod.DELETE, request, Boolean.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody());
        System.out.println("Project deleted: " + projectId);
    }
}
