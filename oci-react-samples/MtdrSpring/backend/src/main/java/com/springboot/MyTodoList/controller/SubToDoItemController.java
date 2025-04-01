package com.springboot.MyTodoList.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import com.springboot.MyTodoList.service.SubToDoItemService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;


@RestController
public class SubToDoItemController {
    @Autowired
    private SubToDoItemService subToDoItemService;

    //Get Mapping to get all subToDoItems by ToDoItemId
    @GetMapping(value="subToDoItems/{toDoItemId}")
    public List<Integer> getSubToDoItemsIdsByToDoItemId(@PathVariable Integer toDoItemId) {
        try{
            List<Integer> subToDoItems = subToDoItemService.findAllSubToDoItemsByToDoItemId(toDoItemId);
            return subToDoItems;
        } catch(Exception e){
            return null;
        }
    }
    
}
