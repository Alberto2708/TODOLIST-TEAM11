package com.springboot.MyTodoList.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.MyTodoList.model.Employee;
import com.springboot.MyTodoList.model.Sprint;
import com.springboot.MyTodoList.service.SprintService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.HttpStatus;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class SprintController {
    @Autowired
    private SprintService sprintService;

    @GetMapping(value="sprint/{sprintId}")
    public ResponseEntity<Sprint> getSprintById(@PathVariable Integer sprintId) {
        try{
            ResponseEntity<Sprint> responseEntity = sprintService.findSprintById(sprintId);
            return new ResponseEntity<Sprint>(responseEntity.getBody(), HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "sprint/project/{projectId}")
    public List<Sprint> getSprintsByTeamId(@PathVariable Integer projectId) {
        try{
            List<Sprint> sprints = sprintService.findSprintsByProjectId(projectId);
            return sprints;
        } catch (Exception e) {
            return null;
        }
    }
    
    @PostMapping(value="/sprint")
    public ResponseEntity addSprint(@RequestBody Sprint sprint) throws Exception{
        Sprint spr = sprintService.addSprint(sprint);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("location", "" + spr.getID());
        responseHeaders.set("Access-Control-Expose-Headers", "location");
        return ResponseEntity.ok()
                .headers(responseHeaders).build();
    }
    
    
}
