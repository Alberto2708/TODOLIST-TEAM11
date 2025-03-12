package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.SubToDoItem;
import com.springboot.MyTodoList.repository.SubToDoItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubToDoItemService {

    @Autowired
    private SubToDoItemRepository SubToDoItemRepository;

    public List<SubToDoItem> findAll() {
        List<SubToDoItem> SubToDoItems = SubToDoItemRepository.findAll();
        return SubToDoItems;
    }

    public ResponseEntity<SubToDoItem> getItemById(int id) {
        Optional<SubToDoItem> todoData = SubToDoItemRepository.findById(id);
        if (todoData.isPresent()) {
            return new ResponseEntity<>(todoData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public SubToDoItem addSubToDoItem(SubToDoItem SubToDoItem) {
        return SubToDoItemRepository.save(SubToDoItem);
    }

    public boolean deleteSubToDoItem(int id) {
        try {
            SubToDoItemRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public SubToDoItem updateSubToDoItem(int id, SubToDoItem std) {
        Optional<SubToDoItem> SubToDoItemData = SubToDoItemRepository.findById(id);
        if (SubToDoItemData.isPresent()) {
            SubToDoItem SubToDoItem = SubToDoItemData.get();
            SubToDoItem.setToDoItemId(id);
            SubToDoItem.setSubToDoItemId(std.getSubToDoItemId());
            
            return SubToDoItemRepository.save(SubToDoItem);
        } else {
            return null;
        }
    }

}
