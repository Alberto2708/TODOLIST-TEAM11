package com.springboot.MyTodoList.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.model.SubToDoItem;
import com.springboot.MyTodoList.model.SubToDoItemId;
import com.springboot.MyTodoList.service.SubToDoItemService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.ArrayList;
import org.springframework.web.bind.annotation.RequestParam;
import com.springboot.MyTodoList.model.SubToDoItem;
import com.springboot.MyTodoList.service.ToDoItemService;
import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.service.AssignedDevService;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
public class SubToDoItemController {
    @Autowired
    private SubToDoItemService subToDoItemService;

    @Autowired
    private ToDoItemService toDoItemService;

    @Autowired
    private AssignedDevService assignedDevService;

    @GetMapping(value="/subToDoItems")
    public List<SubToDoItem> getAllSubToDoItems() {
        try{
            List<SubToDoItem> subToDoItems = subToDoItemService.findAllSubToDoItems();
            return subToDoItems;
        } catch(Exception e){
            return null;
        }
    }

    //Get Mapping to get all subToDoItems Ids by ToDoItemId
    @GetMapping(value="/subToDoItems/{toDoItemId}")
    public List<Integer> getSubToDoItemsIdsByToDoItemId(@PathVariable Integer toDoItemId) {
        try{
            List<Integer> subToDoItems = subToDoItemService.findAllSubToDoItemsIdsByToDoItemId(toDoItemId);
            return subToDoItems;
        } catch(Exception e){
            return null;
        }
    }

    //Get Mapping to get all subToDoItems Objects by ToDoItemId
    @GetMapping(value="/subToDoItems/toDoItem/{toDoItemId}")
    public List<ResponseEntity<ToDoItem>> getSubToDoItemsBytoDoItemId(@PathVariable Integer toDoItemId) {
        try{
            List<SubToDoItem> subToDoItems = subToDoItemService.findAllSubToDoItemsByToDoItemId(toDoItemId);
            List<ResponseEntity<ToDoItem>> toDoItems = new ArrayList<>();
            for(SubToDoItem subToDoItem : subToDoItems){
                toDoItems.add(toDoItemService.getItemById(subToDoItem.getSubToDoItemId()));
            }
            return toDoItems;
        } catch(Exception e){
            return null;
        }
    }

    @GetMapping(value="/subToDoItems/toDoItem/{toDoItemId}/employee/{employeeId}")
    public List<ToDoItem> getSubToDoItemsByToDoItemIdAndEmployeeId(@PathVariable Integer toDoItemId, @PathVariable Integer employeeId) {
        try{
            List<SubToDoItem> subToDoItems = subToDoItemService.findAllSubToDoItemsByToDoItemId(toDoItemId);
            if (subToDoItems.isEmpty()) {
                return null;
            }
            List<ToDoItem> toDoItems = new ArrayList<>();
            for(SubToDoItem subToDoItem : subToDoItems){
                if (assignedDevService.checkIfToDoItemIsAssignedToEmployeByIds(subToDoItem.getSubToDoItemId(), employeeId)){
                    toDoItems.add(toDoItemService.getItemById(subToDoItem.getSubToDoItemId()).getBody());
                }
            }
            return toDoItems;
        } catch(Exception e){
            return null;
        }
    }

    //Get Mapping to get all Completed subToDoItems by ToDoItemId and employeeID
    @GetMapping(value = "/subToDoItems/toDoItem/{toDoItemId}/employee/{employeeId}/completed")
    public ResponseEntity<List<ToDoItem>> getCompletedSubToDoItemsByToDoItemIdAndEmployeeId(@PathVariable Integer toDoItemId, @PathVariable Integer employeeId) {
        try{
            List<ToDoItem> toDoItems = getSubToDoItemsByToDoItemIdAndEmployeeId(toDoItemId, employeeId);
            if (toDoItems.isEmpty()) {
                return null;
            }
            List<ToDoItem> completedToDoItems = new ArrayList<>();
            for(ToDoItem toDoItem : toDoItems){
                if (toDoItem.getStatus().matches("COMPLETED")){
                    completedToDoItems.add(toDoItem);
                }
            }
            return new ResponseEntity<>(completedToDoItems, HttpStatus.OK);
        } catch(Exception e){
            return null;
        }
    }
    

    //POSTS

    @PostMapping(value="/subToDoItems")
    public ResponseEntity addSubToDoItem(@RequestBody SubToDoItem subToDoItem) {
        try{
            SubToDoItem subToDoItemResponse = subToDoItemService.addSubToDoItem(subToDoItem);
            SubToDoItemId response =  subToDoItemResponse.getId();
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch(Exception e){
            return null;
        }
    }

    //Delete by subToDoItemId and toDoItemId
    @DeleteMapping(value = "/subToDoItems/{toDoItemId}/{subToDoItemId}")
    public ResponseEntity deleteSubToDoItem(@PathVariable Integer toDoItemId, @PathVariable Integer subToDoItemId) {
        try{
            Boolean status = subToDoItemService.deleteSubToDoItem(toDoItemId, subToDoItemId);
            System.out.println(status);
            if (status == true){
                return new ResponseEntity<>(HttpStatus.OK);
            } else if (status == false){
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Delete by subToDoItemId
    @DeleteMapping(value = "/subToDoItems/childs/{subToDoItemId}")
    public ResponseEntity deleteSubToDoItemBySubToDoItemId(@PathVariable Integer subToDoItemId) {
        try{
            Boolean status = subToDoItemService.deleteBySubToDoItemById(subToDoItemId);
            if (status == true){
                return new ResponseEntity<>(HttpStatus.OK);
            } else if (status == false){
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Delete by toDoItemId
    @DeleteMapping(value = "/subToDoItems/fathers/{toDoItemId}")
    public ResponseEntity deleteSubToDoItemByToDoItemId(@PathVariable Integer toDoItemId) {
        try{
            Boolean status = subToDoItemService.deleteByToDoItemId(toDoItemId);
            if (status == true){
                return new ResponseEntity<>(HttpStatus.OK);
            } else if (status == false){
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
    
}
