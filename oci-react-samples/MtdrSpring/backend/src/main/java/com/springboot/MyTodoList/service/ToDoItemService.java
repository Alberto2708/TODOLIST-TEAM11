package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.repository.ToDoItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ToDoItemService {

    @Autowired
    private ToDoItemRepository toDoItemRepository;

    public List<ToDoItem> findAll() {
        List<ToDoItem> todoItems = toDoItemRepository.findAll();
        return todoItems;
    }

    public ResponseEntity<ToDoItem> getItemById(Integer id) {
        Optional<ToDoItem> todoData = toDoItemRepository.findById(id);
        if (todoData.isPresent()) {
            return new ResponseEntity<>(todoData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ToDoItem addToDoItem(ToDoItem toDoItem) {
        return toDoItemRepository.save(toDoItem);
    }


    public boolean deleteToDoItem(Integer id) {
        try {
            toDoItemRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public ToDoItem updateToDoItem(Integer id, ToDoItem td) {
        Optional<ToDoItem> toDoItemData = toDoItemRepository.findById(id);
        if (toDoItemData.isPresent()) {
            ToDoItem toDoItem = toDoItemData.get();
            toDoItem.setID(id);
            if (td.getName() != null){
                toDoItem.setName(td.getName());
            }
            if (td.getStatus() != null){
                toDoItem.setStatus(td.getStatus());
            }
            if (td.getManagerId() != null){
                toDoItem.setManagerId(td.getManagerId());
            }
            if (td.getCompletionTs() != null){
                toDoItem.setCompletionTs(td.getCompletionTs());
            }
            if (td.getStartDate() != null){
                toDoItem.setStartDate(td.getStartDate());
            }
            if (td.getDeadline() != null){
                toDoItem.setDeadline(td.getDeadline());
            }
            if (td.getProjectId() != null){
                toDoItem.setProjectId(td.getProjectId());
            }
            if (td.getDescription() != null){
                toDoItem.setDescription(td.getDescription());
            }
            return toDoItemRepository.save(toDoItem);
        } else {
            return null;
        }

    }

}
