package com.springboot.MyTodoList.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.model.SubToDoItem;
import com.springboot.MyTodoList.model.SubToDoItemId;
import com.springboot.MyTodoList.service.SubToDoItemService;
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

    //Get Mapping to get all subToDoItems by ToDoItemId
    @GetMapping(value="subToDoItems/{toDoItemId}")
    public List<Integer> getSubToDoItemsIdsByToDoItemId(@PathVariable Integer toDoItemId) {
        try{
            List<Integer> subToDoItems = subToDoItemService.findAllSubToDoItemsIdsByToDoItemId(toDoItemId);
            return subToDoItems;
        } catch(Exception e){
            return null;
        }
    }

    @GetMapping(value="subToDoItems/toDoItem/{toDoItemId}")
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

    @GetMapping(value="subToDoItems/toDoItem/{toDoItemId}/employee/{employeeId}")
    public List<ToDoItem> getSubToDoItemsByToDoItemIdAndEmployeeId(@PathVariable Integer toDoItemId, @PathVariable Integer employeeId) {
        try{
            List<SubToDoItem> subToDoItems = subToDoItemService.findAllSubToDoItemsByToDoItemId(toDoItemId);
            System.out.println(subToDoItems.size());
            if (subToDoItems.isEmpty()) {
                return null;
            }
            List<ToDoItem> toDoItems = new ArrayList<>();
            for(SubToDoItem subToDoItem : subToDoItems){
                System.out.println(subToDoItem.toString());
                if (assignedDevService.checkIfToDoItemIsAssignedToEmployeByIds(subToDoItem.getSubToDoItemId(), employeeId)){
                    toDoItems.add(toDoItemService.getItemById(subToDoItem.getSubToDoItemId()).getBody());
                }
            }
            return toDoItems;
        } catch(Exception e){
            return null;
        }
    }

    //POSTS

    @PostMapping(value="subToDoItems")
    public ResponseEntity addSubToDoItem(@RequestBody SubToDoItem subToDoItem) {
        try{
            SubToDoItem subToDoItemResponse = subToDoItemService.addSubToDoItem(subToDoItem);
            SubToDoItemId response =  subToDoItemResponse.getId();
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch(Exception e){
            return null;
        }
    }
    
    
}
