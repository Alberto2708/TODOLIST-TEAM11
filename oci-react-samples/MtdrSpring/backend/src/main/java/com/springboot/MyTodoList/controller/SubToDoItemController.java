// Controller for managing SubToDoItems and related in the ToDoList application
package com.springboot.MyTodoList.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.MyTodoList.model.SubToDoItem;
import com.springboot.MyTodoList.model.SubToDoItemId;
import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.service.AssignedDevService;
import com.springboot.MyTodoList.service.SubToDoItemService;
import com.springboot.MyTodoList.service.ToDoItemService;

@RestController
public class SubToDoItemController {

    @Autowired
    private SubToDoItemService subToDoItemService;

    @Autowired
    private ToDoItemService toDoItemService;

    @Autowired
    private AssignedDevService assignedDevService;

    private static final Logger logger = LoggerFactory.getLogger(AssignedDevController.class);

    // -------------------- GET --------------------
    // All SubToDoItems
    @GetMapping(value = "/subToDoItems")
    public List<SubToDoItem> getAllSubToDoItems() {
        try {
            List<SubToDoItem> subToDoItems = subToDoItemService.findAllSubToDoItems();
            return subToDoItems;
        } catch (Exception e) {
            return null;
        }
    }

    // SubToDoItems by ToDoItemId and employeeID
    @GetMapping(value = "/subToDoItems/{toDoItemId}/{subToDoItemId}")
    public ResponseEntity<SubToDoItem> getSubToDoItemByToDoItemIdAndEmployeeId(@PathVariable Integer toDoItemId, @PathVariable Integer subToDoItemId) {
        try {
            SubToDoItemId subToDoItemIdObj = new SubToDoItemId(toDoItemId, subToDoItemId);
            SubToDoItem subToDoItem = subToDoItemService.findSubToDoItemById(subToDoItemIdObj);
            return new ResponseEntity<>(subToDoItem, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    // All subToDoItems Ids by ToDoItemId
    @GetMapping(value = "/subToDoItems/{toDoItemId}")
    public List<Integer> getSubToDoItemsIdsByToDoItemId(@PathVariable Integer toDoItemId) {
        try {
            List<Integer> subToDoItems = subToDoItemService.findAllSubToDoItemsIdsByToDoItemId(toDoItemId);
            return subToDoItems;
        } catch (Exception e) {
            return null;
        }
    }

    // All subToDoItems Objects by ToDoItemId
    @GetMapping(value = "/subToDoItems/toDoItem/{toDoItemId}")
    public ResponseEntity<List<ToDoItem>> getSubToDoItemsBytoDoItemId(@PathVariable Integer toDoItemId) {
        try {
            List<SubToDoItem> subToDoItems = subToDoItemService.findAllSubToDoItemsByToDoItemId(toDoItemId);
            if (subToDoItems == null) {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }

            List<ToDoItem> toDoItems = new ArrayList<>();
            for (SubToDoItem subToDoItem : subToDoItems) {
                toDoItems.add(toDoItemService.getItemById(subToDoItem.getSubToDoItemId()));
            }
            return new ResponseEntity<>(toDoItems, HttpStatus.OK);
        } catch (Exception e) {
            logger.info("Error: " + e.getMessage());
            return null;
        }
    }

    // All subToDoItems by ToDoItemId and employeeID
    @GetMapping(value = "/subToDoItems/toDoItem/{toDoItemId}/employee/{employeeId}")
    public List<ToDoItem> getSubToDoItemsByToDoItemIdAndEmployeeId(@PathVariable Integer toDoItemId, @PathVariable Integer employeeId) {
        try {
            List<SubToDoItem> subToDoItems = subToDoItemService.findAllSubToDoItemsByToDoItemId(toDoItemId);
            if (subToDoItems == null) {
                return null;
            }
            List<ToDoItem> toDoItems = new ArrayList<>();
            for (SubToDoItem subToDoItem : subToDoItems) {
                if (assignedDevService.checkIfToDoItemIsAssignedToEmployeByIds(subToDoItem.getSubToDoItemId(), employeeId)) {
                    toDoItems.add(toDoItemService.getItemById(subToDoItem.getSubToDoItemId()));
                }
            }
            return toDoItems;
        } catch (Exception e) {
            logger.info("Error: " + e.getMessage());
            return null;
        }
    }

    // All Completed subToDoItems by ToDoItemId and employeeID
    @GetMapping(value = "/subToDoItems/toDoItem/{toDoItemId}/employee/{employeeId}/completed")
    public ResponseEntity<List<ToDoItem>> getCompletedSubToDoItemsByToDoItemIdAndEmployeeId(@PathVariable Integer toDoItemId, @PathVariable Integer employeeId) {
        try {
            List<ToDoItem> toDoItems = getSubToDoItemsByToDoItemIdAndEmployeeId(toDoItemId, employeeId);
            if (toDoItems == null) {
                logger.info("IS EMPTY");
                return null;
            }
            List<ToDoItem> completedToDoItems = new ArrayList<>();
            for (ToDoItem toDoItem : toDoItems) {
                logger.info("ToDoItem: " + toDoItem.getID() + " - Status: " + toDoItem.getStatus());
                if (toDoItem.getStatus().matches("COMPLETED")) {
                    completedToDoItems.add(toDoItem);
                }
            }
            return new ResponseEntity<>(completedToDoItems, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }

    // All Pending subToDoItems by ToDoItemId and employeeID
    @GetMapping(value = "/subToDoItems/toDoItem/{toDoItemId}/employee/{employeeId}/pending")
    public ResponseEntity<List<ToDoItem>> getPendingSubToDoItemsByToDoItemIdAndEmployeeId(@PathVariable Integer toDoItemId, @PathVariable Integer employeeId) {
        try {
            List<ToDoItem> toDoItems = getSubToDoItemsByToDoItemIdAndEmployeeId(toDoItemId, employeeId);
            if (toDoItems == null) {
                logger.info("IS EMPTY");
                return null;
            }
            List<ToDoItem> pendingToDoItems = new ArrayList<>();
            for (ToDoItem toDoItem : toDoItems) {
                logger.info("ToDoItem: " + toDoItem.getID() + " - Status: " + toDoItem.getStatus());
                if (toDoItem.getStatus().matches("PENDING")) {
                    pendingToDoItems.add(toDoItem);
                }
            }
            return new ResponseEntity<>(pendingToDoItems, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // ---------------- POST --------------------
    // SubToDoItem
    @PostMapping(value = "/subToDoItems")
    public ResponseEntity addSubToDoItem(@RequestBody SubToDoItem subToDoItem) {
        try {
            SubToDoItem subToDoItemResponse = subToDoItemService.addSubToDoItem(subToDoItem);
            SubToDoItemId response = subToDoItemResponse.getId();
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            return null;
        }
    }

    // -------------------- DELETE --------------------
    // SubToDoItemId and toDoItemId
    @DeleteMapping(value = "/subToDoItems/{toDoItemId}/{subToDoItemId}")
    public ResponseEntity deleteSubToDoItem(@PathVariable Integer toDoItemId, @PathVariable Integer subToDoItemId) {
        try {
            Boolean status = subToDoItemService.deleteSubToDoItem(toDoItemId, subToDoItemId);
            System.out.println(status);
            if (status == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            if (status == false) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity<>(status, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // SubToDoItemId
    @DeleteMapping(value = "/subToDoItems/childs/{subToDoItemId}")
    public ResponseEntity deleteSubToDoItemBySubToDoItemId(@PathVariable Integer subToDoItemId) {
        try {
            Boolean status = subToDoItemService.deleteBySubToDoItemById(subToDoItemId);
            System.out.println(status);
            if (status == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            if (status == false) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity<>(status, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ToDoItemId
    @DeleteMapping(value = "/subToDoItems/fathers/{toDoItemId}")
    public ResponseEntity deleteSubToDoItemByToDoItemId(@PathVariable Integer toDoItemId) {
        try {
            Boolean status = subToDoItemService.deleteByToDoItemId(toDoItemId);
            System.out.println(status);
            if (status == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            if (status == false) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity<>(status, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}
