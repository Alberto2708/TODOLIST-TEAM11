package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.AssignedDev;
import com.springboot.MyTodoList.model.SubToDoItem;
import com.springboot.MyTodoList.model.SubToDoItemId;
import com.springboot.MyTodoList.repository.SubToDoItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

@Service
public class SubToDoItemService {

    @Autowired
    private SubToDoItemRepository subToDoItemRepository;

    // Find all SubToDoItems
    public List<SubToDoItem> findAllSubToDoItems() {
        try {
            return subToDoItemRepository.findAll();
        } catch (Exception e) {
            return null;
        }
    }

    // Find SubToDoItem by ID
    public SubToDoItem findSubToDoItemById(SubToDoItemId subToDoItemID) {
        try {
            Integer toDoItemId = subToDoItemID.getToDoItemId();
            Integer subToDoItemId = subToDoItemID.getSubToDoItemId();
            Optional<SubToDoItem> subToDoItemData = subToDoItemRepository.findByToDoItemIdAndSubToDoItemId(toDoItemId, subToDoItemId);
            if (subToDoItemData.isPresent()) {
                return subToDoItemData.get();
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    // Check a given SubToDoItem ID is in fact a SubToDoItem ID or a ToDoItem ID
    public Boolean checkIfIdIsntSubToDoItem(Integer subToDoItemId) {
        try {
            if (subToDoItemRepository.findBySubToDoItemId(subToDoItemId).isEmpty()) {
                //Returns true if the id is a Father ToDo Item id
                return true;
            } else {
                //Returns false if the id is a Sub ToDo Item id
                return false;
            }
        } catch (Exception e) {
            return null;
        }
    }

    // Find SubToDoItems IDs by ToDoItem ID
    public List<Integer> findAllSubToDoItemsIdsByToDoItemId(Integer toDoItemId) {
        try {
            return subToDoItemRepository.findAllSubToDoItemIdsByToDoItemId(toDoItemId);
        } catch (Exception e) {
            return null;
        }
    }

    // Find SubToDoItems by ToDoItem ID
    public List<SubToDoItem> findAllSubToDoItemsByToDoItemId(Integer toDoItemId) {
        try {
            return subToDoItemRepository.findAllSubToDoItemsByToDoItemId(toDoItemId);
        } catch (Exception e) {
            return null;
        }
    }

    // Add a new SubToDoItem
    public SubToDoItem addSubToDoItem(SubToDoItem subToDoItem) {
        return subToDoItemRepository.save(subToDoItem);
    }

    // Delete a SubToDoItem by ToDoItem ID and SubToDoItem ID
    public Boolean deleteSubToDoItem(Integer toDoItemId, Integer subToDoItemId) {
        try {
            subToDoItemRepository.deleteByToDoItemIdAndSubToDoItemId(toDoItemId, subToDoItemId);
            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    // Delete a SubToDoItem by SubToDoItem ID
    public Boolean deleteBySubToDoItemById(Integer subToDoItemId) {
        try {
            subToDoItemRepository.deleteBySubToDoItemId(subToDoItemId);
            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    // Delete all SubToDoItems by ToDoItem ID
    public Boolean deleteByToDoItemId(Integer toDoItemId) {
        try {
            subToDoItemRepository.deleteByToDoItemId(toDoItemId);
            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

}
