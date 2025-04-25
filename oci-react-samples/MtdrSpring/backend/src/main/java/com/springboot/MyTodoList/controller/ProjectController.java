package com.springboot.MyTodoList.controller;


import com.springboot.MyTodoList.model.Project;
import com.springboot.MyTodoList.service.ProjectService;
import com.springboot.MyTodoList.controller.SprintController;
import com.springboot.MyTodoList.service.SprintService;
import com.springboot.MyTodoList.model.Sprint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


import java.util.ArrayList;
import java.util.List;


@RestController
public class ProjectController {
    
    @Autowired
    private ProjectService projectService;

    @Autowired
    private SprintController sprintController;

    @Autowired
    private SprintService sprintService;

    @GetMapping(value = "/projects")
    public ResponseEntity <List<Project>> getAllProjects(@RequestParam(required = false) String name) {
        try{
            List<Project> projects = projectService.findAll();
            return new ResponseEntity <List<Project>> (projects, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value="/project/{projectId}")
    public ResponseEntity<Project> getProjectById(@PathVariable Integer projectId) {
        try{
            Project responseEntity  = projectService.findProjectById(projectId);
            return new ResponseEntity <Project> (responseEntity, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value = "/projects")
    public  ResponseEntity addProject(@RequestBody Project project) {
        try{
            Project pro = projectService.addProject(project);
            Integer projectId = pro.getID();
            return new ResponseEntity<>(projectId, HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(value = "/projects/{projectId}")
    public ResponseEntity updateProject(@PathVariable Integer projectId, @RequestBody Project project) {
        try{
            Project pro = projectService.updateProject(projectId, project);
            return new ResponseEntity<>(pro, HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //Delete Project by ID and all Sprints by Project ID in cascade
    @DeleteMapping(value = "/projects/{projectId}")
    public ResponseEntity deleteProject(@PathVariable Integer projectId) {
        Boolean flag = false;
        try{
            List<Boolean> flags = new ArrayList<>();
            List<Sprint> sprints = sprintService.findSprintsByProjectId(projectId);
            for (Sprint sprint : sprints) {
                flags.add((Boolean) sprintController.deleteSprint(sprint.getID()).getBody());
            }
            flags.add(projectService.deleteProject(projectId));
            for(Boolean f : flags) {
                if (f == null) {
                    return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
                }
                if (f == false) {
                    return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
            flag = true;
            return new ResponseEntity<>(flag, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
