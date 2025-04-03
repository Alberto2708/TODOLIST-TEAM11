package com.springboot.MyTodoList.controller;


import com.springboot.MyTodoList.model.Project;
import com.springboot.MyTodoList.service.ProjectService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class ProjectController {
    
    @Autowired
    private ProjectService projectService;

    @GetMapping(value="/project/{projectId}")
    public ResponseEntity<Project> getProjectById(@PathVariable Integer projectId) {
        try{
            Project responseEntity  = projectService.findProjectById(projectId);
            return new ResponseEntity <Project> (responseEntity, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    


}
