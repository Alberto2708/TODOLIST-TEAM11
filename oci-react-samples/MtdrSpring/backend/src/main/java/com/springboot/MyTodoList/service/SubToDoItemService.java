package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.SubToDoItem;
import com.springboot.MyTodoList.repository.SubToDoItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubToDoItemService {
    @Autowired
    private SubToDoItemRepository subToDoItemRepository;
    
}
