package com.springboot.MyTodoList.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import com.springboot.MyTodoList.model.ToDoItem;

import com.springboot.MyTodoList.model.DevAssignedTask;
import com.springboot.MyTodoList.service.DevAssignedTaskService;
import com.springboot.MyTodoList.service.ToDoItemService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
public class DevAssignedTaskController {
    @Autowired
    private DevAssignedTaskService devAssignedTaskService;
    @Autowired
    private ToDoItemService toDoItemService;

    @GetMapping(value = "/devassignedtasks/{assignedDevId}")
    public List<ResponseEntity<ToDoItem>> getDevAssignedTasksByAssignedDevId(@PathVariable Integer assignedDevId) {
        try{
            List<DevAssignedTask>  assignedDev = devAssignedTaskService.getDevAssignedTasksByAssignedDevId(assignedDevId);
            System.out.println(assignedDev.size());
            List<ResponseEntity<ToDoItem>> tasks = new ArrayList<>();
            for(DevAssignedTask task : assignedDev){
                System.out.println(task.getToDoItemId());
                tasks.add(toDoItemService.getItemById(task.getToDoItemId()));
            }
            return tasks;
        }catch(Exception e){
            System.out.println(e);
            return null;
        }
    }


    //Debe calcular el promedio de dias que le sobra a un desarrollador para terminar sus tareas asignadas
    @GetMapping(value = "/devassignedtasks/kpi/{assignedDevId}")
    public Float getCompletionDaysMean(@PathVariable Integer assignedDevId) {
        try{
            List<DevAssignedTask>  assignedDev = devAssignedTaskService.getDevAssignedTasksByAssignedDevId(assignedDevId);
            System.out.println(assignedDev.size());
            List<ResponseEntity<ToDoItem>> tasks = new ArrayList<>();
            for(DevAssignedTask task : assignedDev){
                System.out.println(task.getToDoItemId());
                tasks.add(toDoItemService.getItemById(task.getToDoItemId()));
            }
            Float sum = 0.0f;
            for(ResponseEntity<ToDoItem> task : tasks){
                sum += task.getBody().getDeadline().getDayOfYear() - task.getBody().getCompletionTs().getDayOfYear();
            }
            return sum/tasks.size();
            
        }catch(Exception e){
            System.out.println(e);
            return null;
        }
    }
    
    
}
