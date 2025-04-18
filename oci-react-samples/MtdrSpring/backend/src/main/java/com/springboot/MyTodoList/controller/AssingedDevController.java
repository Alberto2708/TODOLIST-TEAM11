package com.springboot.MyTodoList.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import com.springboot.MyTodoList.model.ToDoItem;

import com.springboot.MyTodoList.model.AssignedDev;
import com.springboot.MyTodoList.model.AssignedDevId;
import com.springboot.MyTodoList.model.SubToDoItem;
import com.springboot.MyTodoList.service.AssignedDevService;
import com.springboot.MyTodoList.service.ToDoItemService;

import oracle.jdbc.proxy.annotation.GetProxy;

import com.springboot.MyTodoList.service.SubToDoItemService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


//Get assigned tasks by developer id and sprint id



@RestController
public class AssingedDevController {
    @Autowired
    private AssignedDevService assignedDevService;
    @Autowired
    private ToDoItemService toDoItemService;
    @Autowired
    private SubToDoItemService subToDoItemService;


    @GetMapping(value = "/assignedDev")
    public List<AssignedDev> getAllDevAssignedTasks() {
        return assignedDevService.findAll();
    }
    

    @GetMapping(value = "/assignedDev/{assignedDevId}")
    public List<ResponseEntity<ToDoItem>> getDevAssignedTasksByAssignedDevId(@PathVariable Integer assignedDevId) {
        try{
            List<AssignedDev>  assignedDevs = assignedDevService.getAssignedDevsByDevId(assignedDevId);
            System.out.println(assignedDevs.size());
            List<ResponseEntity<ToDoItem>> tasks = new ArrayList<>();
            for(AssignedDev task : assignedDevs){
                //System.out.println(task.getToDoItemId());
                tasks.add(toDoItemService.getItemById(task.getToDoItemId()));
            }
            return tasks;
        }catch(Exception e){
            System.out.println(e);
            return null;
        }
    }

    //Get assigned tasks by developer id and sprint id
    @GetMapping(value = "/assignedDev/{assignedDevId}/sprint/{sprintId}")
    public ResponseEntity<List<ToDoItem>> getAssignedTasksByAssignedDevAndSprint(@PathVariable Integer assignedDevId, @PathVariable Integer sprintId) {
        try{
            List<AssignedDev>  assignedDevs = assignedDevService.getAssignedDevsByDevId(assignedDevId);
            //System.out.println(assignedDevs.size());
            List<ToDoItem> tasks = new ArrayList<>();
            for(AssignedDev task : assignedDevs){
                if (toDoItemService.getItemById(task.getToDoItemId()).getBody().getSprintId() == sprintId){
                    tasks.add(toDoItemService.getItemById(task.getToDoItemId()).getBody());
                }
            }
            return new ResponseEntity<>(tasks, HttpStatus.OK);
        }catch(Exception e){
            System.out.println(e);
            return null;
        }
    }

    //Get Completed percentage of assigned tasks by developer id and sprint id KPI
    @GetMapping(value="/assignedDev/{assignedDevId}/sprint/{sprintId}/completedTasks/kpi")
    public Integer getCompletedTasksByEmployeeAndSprint(@PathVariable Integer assignedDevId, @PathVariable Integer sprintId) {
        try{
            List<ToDoItem> tasks = getAssignedTasksByAssignedDevAndSprint(assignedDevId, sprintId).getBody();
            Integer sum = 0;
            for (ToDoItem task : tasks){
                if (task.getStatus().matches("COMPLETED")){
                    sum += 1;
                }
            }
            Integer response = (int) (((double) sum / tasks.size()) * 100);
            return response;
        }catch(Exception e){
            System.out.println(e);
            return null;
        }
    }

    //Get Worked Hours by developer id and sprint id based on estHours for each task.
    @GetMapping(value = "/assignedDev/{assignedDevId}/sprint/{sprintId}/workedHours/kpi")
    public ResponseEntity<Integer> getWorkedHoursByEmployeeAndSprint(@PathVariable Integer assignedDevId, @PathVariable Integer sprintId) {
        try{
            List<ToDoItem> tasks = getAssignedTasksByAssignedDevAndSprint(assignedDevId, sprintId).getBody();
            Integer workedHours = 0;
            if (tasks.isEmpty()){
                return new ResponseEntity<> (null, HttpStatus.NOT_FOUND);
            }

            for (ToDoItem task : tasks) {
                if (task.getStatus().matches("COMPLETED")){
                    workedHours += task.getEstHours();
                }
            }
            
            return new ResponseEntity<>(workedHours, HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e);
            return null;
        }
    }
    
    
    

    //Get father tasks by developer id and sprint id
    @GetMapping(value = "/assignedDev/{assignedDevId}/sprint/{sprintId}/father")
    public List<ResponseEntity<ToDoItem>> getAssignedTasksByAssignedDevAndSprintFather(@PathVariable Integer assignedDevId, @PathVariable Integer sprintId) {
        try{
            List<AssignedDev>  assignedDevs = assignedDevService.getAssignedDevsByDevId(assignedDevId);
            List<ResponseEntity<ToDoItem>> tasks = new ArrayList<>();
            for(AssignedDev task : assignedDevs){
                //Add logic for subtasks verification
                if (toDoItemService.getItemById(task.getToDoItemId()).getBody().getSprintId() == sprintId ){
                    if (subToDoItemService.checkIfIdIsntSubToDoItem(task.getToDoItemId())){
                        tasks.add(toDoItemService.getItemById(task.getToDoItemId()));
                    }
                }
            }
            return tasks;
        }catch(Exception e){
            System.out.println(e);
            return null;
        }
    }

    //Get Father Completed tasks by developer id and sprint id
    @GetMapping(value = "/assignedDev/{assignedDevId}/sprint/{sprintId}/father/completed")
    public ResponseEntity<List<ToDoItem>> getCompletedTasksByEmployeeAndSprintFather(@PathVariable Integer assignedDevId, @PathVariable Integer sprintId) {
        try{
            List<ResponseEntity<ToDoItem>> tasks = getAssignedTasksByAssignedDevAndSprintFather(assignedDevId, sprintId);
            if (tasks == null || tasks.isEmpty()){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            List<ToDoItem> completedTasks = new ArrayList<>();
            for (ResponseEntity<ToDoItem> task : tasks){
                if (task.getBody().getStatus().matches("COMPLETED")){
                    completedTasks.add(task.getBody());
                }
            }
            return new ResponseEntity<>(completedTasks, HttpStatus.OK);
        }catch(Exception e){
            System.out.println(e);
            return null;
        }
    }





    //calculates the average number of hours a developer has left to complete their assigned tasks.
    @GetMapping(value = "/assignedDev/kpi/{assignedDevId}")
    public Float getCompletionDaysMean(@PathVariable Integer assignedDevId) {
        try{
            List<AssignedDev>  assignedDev = assignedDevService.getAssignedDevsByDevId(assignedDevId);
            //System.out.println(assignedDev.size()); // Return number of tasks assigned to the developer.
            List<ResponseEntity<ToDoItem>> tasks = new ArrayList<>();
            for(AssignedDev task : assignedDev){
                //System.out.println(task.getToDoItemId()); //Returns the task id
                //System.out.println(toDoItemService.getItemById(task.getToDoItemId()).getBody().getStatus()); //Returns the status of the task
                if (toDoItemService.getItemById(task.getToDoItemId()).getBody().getStatus().matches("COMPLETED")){
                    tasks.add(toDoItemService.getItemById(task.getToDoItemId()));
                    //System.out.println("Hello World from adding task!!!");
                }
            }
            Float sum = 0.0f;
            for(ResponseEntity<ToDoItem> task : tasks){
                sum += Duration.between(task.getBody().getCompletionTs(), task.getBody().getDeadline()).toHours();
            }
            return sum/tasks.size();
            
        }catch(Exception e){
            System.out.println(e);
            return null;
        }
    }

    //calculates the sum of overdue tasks completed by employee
    @GetMapping(value = "/assignedDev/kpi/{assignedDevId}/overdue")
    public ResponseEntity<Integer> getSumOfOverdueTasksByEmployee(@PathVariable Integer assignedDevId) {
        try{
            List<AssignedDev>  assignedDev = assignedDevService.getAssignedDevsByDevId(assignedDevId);
            //System.out.println(assignedDev.size()); // Return number of tasks assigned to the developer.
            List<ResponseEntity<ToDoItem>> tasks = new ArrayList<>();
            for(AssignedDev task : assignedDev){
                System.out.println(task.toString());
                //System.out.println(task.getToDoItemId()); //Returns the task id
                //System.out.println(toDoItemService.getItemById(task.getToDoItemId()).getBody().getStatus()); //Returns the status of the task
                if (toDoItemService.getItemById(task.getToDoItemId()).getBody().getStatus().matches("COMPLETED")){
                    tasks.add(toDoItemService.getItemById(task.getToDoItemId()));
                }
            }
            Integer sum = 0;
            for(ResponseEntity<ToDoItem> task : tasks){
                System.out.println(task.toString());
                if(task.getBody().getCompletionTs().isAfter(task.getBody().getDeadline())){
                    sum += 1;
                }
            }
            return new ResponseEntity<>(sum, HttpStatus.OK);
        }catch(Exception e){
            System.out.println(e);
            return null;
        }
    }

    @PostMapping(value = "/assignedDev")
    public ResponseEntity addDevAssignedTask(@RequestBody AssignedDev devAssignedTask) throws Exception{
        //System.out.println(devAssignedTask);
        //System.out.println(devAssignedTask.getToDoItemId());
        assignedDevService.addAssignedDev(devAssignedTask);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("location", "" );
        responseHeaders.set("Access-Control-Expose-Headers", "location");
        return ResponseEntity.ok().headers(responseHeaders).build();
    }

    @DeleteMapping(value = "assignedDev/{toDoItemId}/{assignedDevId}")
    public ResponseEntity deleteDevAssignedTask(@PathVariable Integer toDoItemId, @PathVariable Integer assignedDevId) {
        try{
            Boolean status = assignedDevService.deleteAssignedDev(toDoItemId, assignedDevId);
            System.out.println(status);
            if(status == null){ return new ResponseEntity<>(HttpStatus.NOT_FOUND);}
            if (status == false){ return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);}
            return new ResponseEntity<>(status, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
