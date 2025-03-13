package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.repository.ToDoItemRepository;
import com.springboot.MyTodoList.repository.DevAssignedTaskRepository;
import com.springboot.MyTodoList.repository.SubToDoItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ToDoItemService {

    @Autowired
    private ToDoItemRepository toDoItemRepository;
    
    @Autowired
    private DevAssignedTaskRepository devAssignedTaskRepository;
    
    @Autowired
    private SubToDoItemRepository subToDoItemRepository;
    
    /**
     * Find all ToDoItems
     */
    public List<ToDoItem> findAll() {
        return toDoItemRepository.findAll();
    }
    
    /**
     * Find ToDoItem by ID
     */
    public ResponseEntity<ToDoItem> getItemById(int id) {
        Optional<ToDoItem> todoData = toDoItemRepository.findById(id);
        if (todoData.isPresent()) {
            return new ResponseEntity<>(todoData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * Add a new ToDoItem
     */
    public ToDoItem addToDoItem(ToDoItem toDoItem) {
        // Set default values for new tasks
        if (toDoItem.getCreation_ts() == null) {
            toDoItem.setCreation_ts(OffsetDateTime.now());
        }
        if (toDoItem.getStatus() == null) {
            toDoItem.setStatus("Pending");
        }
        return toDoItemRepository.save(toDoItem);
    }
    
    /**
     * Delete a ToDoItem by ID
     */
    public boolean deleteToDoItem(int id) {
        try {
            toDoItemRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Update an existing ToDoItem
     */
    public ToDoItem updateToDoItem(int id, ToDoItem td) {
        Optional<ToDoItem> toDoItemData = toDoItemRepository.findById(id);
        if (toDoItemData.isPresent()) {
            ToDoItem toDoItem = toDoItemData.get();
            toDoItem.setId(id);
            
            if (td.getName() != null) {
                toDoItem.setName(td.getName());
            }
            
            if (td.getDescription() != null) {
                toDoItem.setDescription(td.getDescription());
            }
            
            if (td.getStatus() != null) {
                toDoItem.setStatus(td.getStatus());
                
                // If marked as completed, set completion timestamp
                if ("Completed".equals(td.getStatus())) {
                    toDoItem.setCompletion_ts(OffsetDateTime.now());
                }
            }
            
            if (td.getStartDate() != null) {
                toDoItem.setStartDate(td.getStartDate());
            }
            
            if (td.getDeadline() != null) {
                toDoItem.setDeadline(td.getDeadline());
            }
            
            if (td.getManagerId() != null) {
                toDoItem.setManagerId(td.getManagerId());
            }
            
            if (td.getProjectId() != null) {
                toDoItem.setProjectId(td.getProjectId());
            }
            
            return toDoItemRepository.save(toDoItem);
        } else {
            return null;
        }
    }
    
    /**
     * Mark a ToDoItem as completed
     */
    public ToDoItem markAsCompleted(int id, String userRole) {
        Optional<ToDoItem> toDoItemData = toDoItemRepository.findById(id);
        if (toDoItemData.isPresent()) {
            ToDoItem toDoItem = toDoItemData.get();
            
            // Developer can only mark as "Reviewing"
            // Manager can mark as "Completed"
            if ("Developer".equals(userRole)) {
                toDoItem.setStatus("Reviewing");
            } else if ("Manager".equals(userRole)) {
                toDoItem.setStatus("Completed");
                toDoItem.setCompletion_ts(OffsetDateTime.now());
            }
            
            return toDoItemRepository.save(toDoItem);
        }
        return null;
    }
    
    /**
     * Get tasks by status
     */
    public List<ToDoItem> getTasksByStatus(String status) {
        return toDoItemRepository.findByStatus(status);
    }
    
    /**
     * Get tasks by manager
     */
    public List<ToDoItem> getTasksByManager(Integer managerId) {
        return toDoItemRepository.findByManagerId(managerId);
    }
    
    /**
     * Get tasks by project
     */
    public List<ToDoItem> getTasksByProject(Integer projectId) {
        return toDoItemRepository.findByProjectId(projectId);
    }
    
    /**
     * Get tasks with deadline before a specific date
     */
    public List<ToDoItem> getTasksWithDeadlineBefore(LocalDate date) {
        return toDoItemRepository.findByDeadlineBefore(date);
    }
    
    /**
     * Check if all subtasks are completed
     */
    public boolean areAllSubtasksCompleted(int todoItemId) {
        List<Integer> subtaskIds = subToDoItemRepository.findByTodoItemId(todoItemId)
                .stream()
                .map(subTask -> subTask.getSubTodoItemId())
                .toList();
        
        for (Integer subtaskId : subtaskIds) {
            Optional<ToDoItem> subtask = toDoItemRepository.findById(subtaskId);
            if (subtask.isPresent() && !"Completed".equals(subtask.get().getStatus())) {
                return false;
            }
        }
        
        return true;
    }
}
