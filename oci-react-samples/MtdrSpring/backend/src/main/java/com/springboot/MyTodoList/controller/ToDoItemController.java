package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.service.ToDoItemService;
import com.springboot.MyTodoList.service.SubToDoItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;



//ADD
//GET ENDPOINT FOR TODOITEM BY SPRINT 


@RestController
public class ToDoItemController {
    @Autowired
    private ToDoItemService toDoItemService;

    @Autowired
    private SubToDoItemService subToDoItemService;

    // @CrossOrigin
    @GetMapping(value = "/todolist")
    public List<ToDoItem> getAllToDoItems() {
        return toDoItemService.findAll();
    }

    // @CrossOrigin
    @GetMapping(value = "/todolist/{id}")
    public ResponseEntity<ToDoItem> getToDoItemById(@PathVariable Integer id) {
        try {
            ResponseEntity<ToDoItem> responseEntity = toDoItemService.getItemById(id);
            return new ResponseEntity<ToDoItem>(responseEntity.getBody(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //Get Father ToDoItems by Manager ID and Sprint ID
    @GetMapping(value = "/todolist/manager/{managerId}/sprint/{sprintId}")
    public List<ToDoItem> getFatherToDoItemsByManagerIdAndSprintId(@PathVariable Integer managerId, @PathVariable Integer sprintId) {
        try{

            List<ToDoItem> tasks = toDoItemService.getFatherToDoItemsByManagerIdAndSprintId(managerId, sprintId);
            if (tasks.isEmpty()) {
                return null;
            }
            List<ToDoItem> fatherToDoItems = new ArrayList<>();
            for (ToDoItem task : tasks) {
                if(subToDoItemService.checkIfIdIsntSubToDoItem(task.getID())){
                    fatherToDoItems.add(task);
                }
            }
            return fatherToDoItems;
        } catch(Exception e){
            System.out.println(e);
            return null;
        }
    }
    
    
    
    // Post Map for ToDoItem creation
    @PostMapping(value = "/todolist")
    public ResponseEntity addToDoItem(@RequestBody ToDoItem todoItem) throws Exception {
        try{
            ToDoItem td = toDoItemService.addToDoItem(todoItem);
            Integer responseEntity = td.getID();
            return new ResponseEntity<>(responseEntity, HttpStatus.CREATED);
        } catch(Exception e){
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // @CrossOrigin
    @PutMapping(value = "/todolist/{id}")
    public ResponseEntity updateToDoItem(@RequestBody ToDoItem toDoItem, @PathVariable Integer id) {
        try {
            ToDoItem toDoItem1 = toDoItemService.updateToDoItem(id, toDoItem);
            return new ResponseEntity<>(toDoItem1, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(value="/todolist/complete/{id}")
    public ResponseEntity completeTask(@PathVariable Integer id) {
        try {
            ToDoItem toDoItem = toDoItemService.completeTask(id);
            return new ResponseEntity<>(toDoItem, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(value="/todolist/undoCompletion/{id}")
    public ResponseEntity undoCompletion(@PathVariable Integer id) {
        try {
            ToDoItem toDoItem = toDoItemService.undoCompletion(id);
            return new ResponseEntity<>(toDoItem, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }


    // @CrossOrigin
    @DeleteMapping(value = "/todolist/{id}")
    public ResponseEntity<Boolean> deleteToDoItem(@PathVariable("id") Integer id) {
        Boolean flag = false;
        try {
            flag = toDoItemService.deleteToDoItem(id);
            return new ResponseEntity<>(flag, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(flag, HttpStatus.NOT_FOUND);
        }
    }

}