package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.SubToDoItem;
import com.springboot.MyTodoList.model.SubToDoItemId;
import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.repository.SubToDoItemRepository;
import com.springboot.MyTodoList.repository.ToDoItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubToDoItemService {

    @Autowired
    private SubToDoItemRepository subToDoItemRepository;
    
    @Autowired
    private ToDoItemRepository toDoItemRepository;
    
    /**
     * Find all subtasks
     */
    public List<SubToDoItem> findAll() {
        return subToDoItemRepository.findAll();
    }
    
    /**
     * Find subtask by ID
     */
    public ResponseEntity<SubToDoItem> getSubToDoItemById(int todoItemId, int subTodoItemId) {
        SubToDoItemId id = new SubToDoItemId(todoItemId, subTodoItemId);
        Optional<SubToDoItem> subToDoItemData = subToDoItemRepository.findById(id);
        if (subToDoItemData.isPresent()) {
            return new ResponseEntity<>(subToDoItemData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * Find all subtasks for a task
     */
    public List<SubToDoItem> findByTodoItemId(int todoItemId) {
        return subToDoItemRepository.findByTodoItemId(todoItemId);
    }
    
    /**
     * Get all tasks where this item is a subtask
     */
    public List<SubToDoItem> findBySubTodoItemId(int subTodoItemId) {
        return subToDoItemRepository.findBySubTodoItemId(subTodoItemId);
    }
    
    /**
     * Add a new subtask relationship
     * Validates that both main task and subtask exist and prevents circular dependencies
     */
    public ResponseEntity<SubToDoItem> addSubToDoItem(SubToDoItem subToDoItem) {
        try {
            // Verify main task exists
            Optional<ToDoItem> mainTask = toDoItemRepository.findById(subToDoItem.getTodoItemId());
            if (!mainTask.isPresent()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            // Verify subtask exists
            Optional<ToDoItem> subTask = toDoItemRepository.findById(subToDoItem.getSubTodoItemId());
            if (!subTask.isPresent()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            // Prevent task being its own subtask
            if (subToDoItem.getTodoItemId() == subToDoItem.getSubTodoItemId()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            // Check for circular dependencies
            if (isCircularDependency(subToDoItem.getTodoItemId(), subToDoItem.getSubTodoItemId())) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            // Save the subtask relationship
            SubToDoItem savedSubToDoItem = subToDoItemRepository.save(subToDoItem);
            return new ResponseEntity<>(savedSubToDoItem, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Delete a subtask relationship
     */
    public boolean deleteSubToDoItem(int todoItemId, int subTodoItemId) {
        try {
            SubToDoItemId id = new SubToDoItemId(todoItemId, subTodoItemId);
            subToDoItemRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check if adding this subtask would create a circular dependency
     */
    private boolean isCircularDependency(int mainTaskId, int subTaskId) {
        // Check if subtask has the main task as its subtask
        List<SubToDoItem> dependencies = subToDoItemRepository.findByTodoItemId(subTaskId);
        
        for (SubToDoItem dependency : dependencies) {
            if (dependency.getSubTodoItemId() == mainTaskId) {
                return true;
            }
            
            // Check recursively
            if (isCircularDependency(mainTaskId, dependency.getSubTodoItemId())) {
                return true;
            }
        }
        
        return false;
    }
} 