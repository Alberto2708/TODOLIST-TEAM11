package com.springboot.MyTodoList.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import com.springboot.MyTodoList.model.ToDoItem;

import com.springboot.MyTodoList.model.AssignedDev;
import com.springboot.MyTodoList.model.AssignedDevId;
import com.springboot.MyTodoList.model.SubToDoItem;
import com.springboot.MyTodoList.service.AssignedDevService;
import com.springboot.MyTodoList.service.ToDoItemService;
import com.springboot.MyTodoList.service.SubToDoItemService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.HttpHeaders;
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
                System.out.println(task.getToDoItemId());
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
    public List<ResponseEntity<ToDoItem>> getAssignedTasksByAssignedDevAndSprint(@PathVariable Integer assignedDevId, @PathVariable Integer sprintId) {
        try{
            List<AssignedDev>  assignedDevs = assignedDevService.getAssignedDevsByDevId(assignedDevId);
            System.out.println(assignedDevs.size());
            List<ResponseEntity<ToDoItem>> tasks = new ArrayList<>();
            for(AssignedDev task : assignedDevs){
                if (toDoItemService.getItemById(task.getToDoItemId()).getBody().getSprintId() == sprintId){
                    tasks.add(toDoItemService.getItemById(task.getToDoItemId()));
                }
            }
            return tasks;
        }catch(Exception e){
            System.out.println(e);
            return null;
        }
    }

    //Get father tasks by developer id and sprint id
    @GetMapping(value = "/assignedDev/{assignedDevId}/sprint/{sprintId}/father")
    public List<ResponseEntity<ToDoItem>> getAssignedTasksByAssignedDevAndSprintFather(@PathVariable Integer assignedDevId, @PathVariable Integer sprintId) {
        try{
            List<AssignedDev>  assignedDevs = assignedDevService.getAssignedDevsByDevId(assignedDevId);
            System.out.println(assignedDevs.size());
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





    //Debe calcular el promedio de horas que le sobra a un desarrollador para terminar sus tareas asignadas
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
    
    


    
}
