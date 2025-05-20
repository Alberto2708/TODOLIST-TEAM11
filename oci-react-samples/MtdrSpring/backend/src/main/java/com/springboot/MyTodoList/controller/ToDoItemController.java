// Controller for the Telegram Bot, which handles user interactions and task management
// This bot is designed to manage tasks, authenticate users, and provide a user-friendly interface for task management
package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.service.ToDoItemService;
import com.springboot.MyTodoList.service.SubToDoItemService;
import com.springboot.MyTodoList.controller.SubToDoItemController;
import com.springboot.MyTodoList.controller.AssignedDevController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @Autowired
    private SubToDoItemController subToDoItemController;

    @Autowired
    private AssignedDevController assignedDevController;

    private static final Logger logger = LoggerFactory.getLogger(AssignedDevController.class);

    // -------------------- GET --------------------
    // All ToDoItems
    // @CrossOrigin
    @GetMapping(value = "/todolist")
    public List<ToDoItem> getAllToDoItems() {
        return toDoItemService.findAll();
    }

    // ToDoItems by Sprint ID
    // @CrossOrigin
    @GetMapping(value = "/todolist/{id}")
    public ResponseEntity<ToDoItem> getToDoItemById(@PathVariable Integer id) {
        try {
            ToDoItem response = toDoItemService.getItemById(id);
            return new ResponseEntity<ToDoItem>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Father ToDoItems by Manager ID and Sprint ID
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

    // Completed ToDoItems by Sprint ID
    @GetMapping(value = "/todolist/sprint/{sprintId}/completed")
    public ResponseEntity<List<ToDoItem>> getCompletedToDoItemsBySprintId(@PathVariable Integer sprintId) {
        try{
            List<ToDoItem> tasks = toDoItemService.getCompletedToDoItemsBySprintId(sprintId);
            if (tasks.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(tasks, HttpStatus.OK);
        } catch(Exception e){
            logger.error("Error retrieving completed ToDoItems by Sprint ID: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }    
    
    // -------------------- POST --------------------
    // ToDoItem
    @PostMapping(value = "/todolist")
    public ResponseEntity addToDoItem(@RequestBody ToDoItem todoItem) throws Exception {
        try{
            ToDoItem td = toDoItemService.addToDoItem(todoItem);
            Integer responseEntity = td.getID();
            return new ResponseEntity<>(responseEntity, HttpStatus.CREATED);
        } catch(Exception e){
            logger.error("Error creating ToDoItem: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // -------------------- PUT --------------------
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

    // ToDoItem Completion by ID
    @PutMapping(value="/todolist/complete/{id}")
    public ResponseEntity completeTask(@PathVariable Integer id) {
        try {
            ToDoItem toDoItem = toDoItemService.completeTask(id);
            return new ResponseEntity<>(toDoItem, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // ToDoItem UNDO Completion by ID
    @PutMapping(value="/todolist/undoCompletion/{id}")
    public ResponseEntity undoCompletion(@PathVariable Integer id) {
        try {
            ToDoItem toDoItem = toDoItemService.undoCompletion(id);
            return new ResponseEntity<>(toDoItem, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // -------------------- DELETE --------------------
    // ToDoItem by ID
    @DeleteMapping(value = "/todolist/{id}")
    public ResponseEntity<Boolean> deleteToDoItem(@PathVariable("id") Integer id) {
        Boolean flag = false;
        try {
            List<Boolean> flags = new ArrayList<>();
            flags.add((Boolean) subToDoItemController.deleteSubToDoItemBySubToDoItemId(id).getBody());
            flags.add((Boolean) subToDoItemController.deleteSubToDoItemByToDoItemId(id).getBody());
            flags.add((Boolean) assignedDevController.deleteDevAssignedTaskByToDoItemId(id).getBody());
            flags.add(toDoItemService.deleteToDoItem(id));
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
        } catch (Exception e) {
            return new ResponseEntity<>(flag, HttpStatus.NOT_FOUND);
        }
    }

    // Sprint ID
    @DeleteMapping(value = "/todolist/sprint/{sprintId}")
    public ResponseEntity<Boolean> deleteToDoItemBySprintId(@PathVariable("sprintId") Integer sprintId) {
        Boolean flag = false;
        try {
            List<Boolean> flags = new ArrayList<>();
            List<ToDoItem> toDoItems = toDoItemService.getToDoItemsBySprintId(sprintId);
            for (ToDoItem toDoItem : toDoItems) {
                flags.add((Boolean) subToDoItemController.deleteSubToDoItemBySubToDoItemId(toDoItem.getID()).getBody());
                flags.add((Boolean) subToDoItemController.deleteSubToDoItemByToDoItemId(toDoItem.getID()).getBody());
                flags.add((Boolean) assignedDevController.deleteDevAssignedTaskByToDoItemId(toDoItem.getID()).getBody());
                flags.add(toDoItemService.deleteToDoItem(toDoItem.getID()));
            }
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
        } catch (Exception e) {
            return new ResponseEntity<>(flag, HttpStatus.NOT_FOUND);
        }
    }

}