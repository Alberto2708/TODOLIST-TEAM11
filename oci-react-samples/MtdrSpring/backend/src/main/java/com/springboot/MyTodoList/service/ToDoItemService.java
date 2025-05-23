// Service class for managing ToDoItems in the application
package com.springboot.MyTodoList.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.repository.ToDoItemRepository;

@Service
public class ToDoItemService {

    @Autowired
    private ToDoItemRepository toDoItemRepository;

    // Method to find all ToDoItems
    public List<ToDoItem> findAll() {
        List<ToDoItem> todoItems = toDoItemRepository.findAll();
        return todoItems;
    }

    // Method to find all ToDoItems by ID
    public ToDoItem getItemById(Integer id) {
        Optional<ToDoItem> todoData = toDoItemRepository.findById(id);
        if (todoData.isPresent()) {
            return todoData.get();
        } else {
            return null;
        }
    }

    // Method to get all ToDoItems by Sprint ID
    public List<ToDoItem> getToDoItemsBySprintId(Integer sprintId) {
        List<ToDoItem> todoData = toDoItemRepository.findBySprintId(sprintId);
        return todoData;
    }

    // Method to get all completed ToDoItems by Sprint ID
    public List<ToDoItem> getCompletedToDoItemsBySprintId(Integer sprintId) {
        List<ToDoItem> todoData = toDoItemRepository.findBySprintIdAndStatus(sprintId, "COMPLETED");
        return todoData;
    }

    // Method to get Father ToDoItems by manager ID and sprint ID
    public List<ToDoItem> getFatherToDoItemsByManagerIdAndSprintId(Integer managerId, Integer sprintId) {
        List<ToDoItem> todoData = toDoItemRepository.findByManagerIdAndSprintId(managerId, sprintId);
        return todoData;
    }

    // Method to add a new ToDoItem
    public ToDoItem addToDoItem(ToDoItem toDoItem) {
        return toDoItemRepository.save(toDoItem);
    }

    // Method to delete a ToDoItem by ID
    public boolean deleteToDoItem(Integer id) {
        try {
            toDoItemRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Method to update a ToDoItem
    public ToDoItem updateToDoItem(Integer id, ToDoItem td) {
        Optional<ToDoItem> toDoItemData = toDoItemRepository.findById(id);
        if (toDoItemData.isPresent()) {
            ToDoItem toDoItem = toDoItemData.get();
            toDoItem.setID(id);
            if (td.getName() != null) {
                toDoItem.setName(td.getName());
            }
            if (td.getStatus() != null) {
                toDoItem.setStatus(td.getStatus());
            }
            if (td.getManagerId() != null) {
                toDoItem.setManagerId(td.getManagerId());
            }
            if (td.getCompletionTs() != null) {
                toDoItem.setCompletionTs(td.getCompletionTs());
            }
            if (td.getStartDate() != null) {
                toDoItem.setStartDate(td.getStartDate());
            }
            if (td.getDeadline() != null) {
                toDoItem.setDeadline(td.getDeadline());
            }
            if (td.getSprintId() != null) {
                toDoItem.setSprintId(td.getSprintId());
            }
            if (td.getDescription() != null) {
                toDoItem.setDescription(td.getDescription());
            }
            if (td.getEstHours() != null) {
                toDoItem.setEstHours(td.getEstHours());
            }
            return toDoItemRepository.save(toDoItem);
        } else {
            return null;
        }

    }

    // Method to complete a task
    public ToDoItem completeTask(Integer id) {
        Optional<ToDoItem> toDoItemData = toDoItemRepository.findById(id);
        if (toDoItemData.isPresent()) {
            ToDoItem toDoItem = toDoItemData.get();
            toDoItem.setStatus("COMPLETED");
            toDoItem.setCompletionTs(OffsetDateTime.now());
            return toDoItemRepository.save(toDoItem);
        } else {
            return null;
        }
    }

    // Method to undo the completion of a task
    public ToDoItem undoCompletion(Integer id) {
        Optional<ToDoItem> toDoItemData = toDoItemRepository.findById(id);
        if (toDoItemData.isPresent()) {
            ToDoItem toDoItem = toDoItemData.get();
            toDoItem.setStatus("PENDING");
            toDoItem.setCompletionTs(null);
            return toDoItemRepository.save(toDoItem);
        } else {
            return null;
        }
    }

}
