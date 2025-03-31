package com.springboot.MyTodoList.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import com.springboot.MyTodoList.service.SubToDoItemService;


@RestController
public class SubToDoItemController {
    @Autowired
    private SubToDoItemService subToDoItemService;

}
