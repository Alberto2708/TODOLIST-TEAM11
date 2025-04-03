package com.springboot.MyTodoList.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.MyTodoList.model.Employee;
import com.springboot.MyTodoList.model.Sprint;
import com.springboot.MyTodoList.service.SprintService;
import com.springboot.MyTodoList.service.ToDoItemService;
import com.springboot.MyTodoList.model.ToDoItem;

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
    
    @Autowired
    private ToDoItemService toDoItemService;

    @GetMapping(value="/sprint/{sprintId}")
    public ResponseEntity<Sprint> getSprintById(@PathVariable Integer sprintId) {
        try{
            ResponseEntity<Sprint> responseEntity = sprintService.findSprintById(sprintId);
            return responseEntity;
        }catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/sprint/projects/{projectId}")
    public List<Sprint> getSprintsByProjectId(@PathVariable Integer projectId) {
        try{
            List<Sprint> sprints = sprintService.findSprintsByProjectId(projectId);
            return sprints;
        } catch (Exception e) {
            return null;
        }
    }

    //Get Actual Sprint by Project ID
    @GetMapping(value = "/sprint/project/{projectId}")
    public ResponseEntity<Sprint> getActualSprintByProjectId(@PathVariable Integer projectId) {
        try{
            ResponseEntity<Sprint> sprint = sprintService.findActualSprintByProjectId(projectId);
            return sprint;
        } catch (Exception e) {
            return null;
        }
    }

    @GetMapping(value = "/sprint/{sprintId}/kpi")
    public Integer getCompletedTasksBySprint(@PathVariable Integer sprintId) {
        try{
            List<ToDoItem> tasks = toDoItemService.getToDoItemsBySprintId(sprintId);
            System.out.println(tasks);
            if(tasks.size() == 0) {
                return null;
            }
            Integer sum = 0;
            for (ToDoItem task : tasks) {
                if (task.getStatus().matches("COMPLETED")) {
                    sum += 1;
                }
            }
            System.out.println("Sum: " + sum);
            Integer response = (int) (((double) sum / tasks.size()) * 100);
            return response;
        }catch (Exception e) {
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
