// Service class for managing assigned developers to todo items
// This class contains methods to check, add, update, delete and retrieve assigned developers
package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.AssignedDev;
import com.springboot.MyTodoList.model.AssignedDevId;
import com.springboot.MyTodoList.repository.AssignedDevRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AssignedDevService {
    @Autowired
    private AssignedDevRepository assignedDevRepository;

    // Check if a todo item is assigned to a specific employee by their IDs
    // Returns true if the employee has the ToDoItem assigned, false otherwise
    public Boolean checkIfToDoItemIsAssignedToEmployeByIds(Integer toDoItemId, Integer assignedDevId) {
        try {
            if (assignedDevRepository.findByToDoItemIdAndEmployeeId(toDoItemId, assignedDevId).isEmpty()) {
                //Returns false if the x Employee doesn't has x ToDoItem assigned
                return false;
            } else {
                //Returns true if the x Employee has x ToDoItem assigned
                return true;
            }
        } catch (Exception e) {
            return null;
        }
    }
    public List<AssignedDev> findAll() {
        List<AssignedDev> devAssignedTasks = assignedDevRepository.findAll();
        return devAssignedTasks;
    }

    // Finds tasks assigned to a specific developer by developer ID
    public AssignedDev findAssignedDevById(AssignedDevId assignedDevId) {
        try {
            Integer toDoItemId = assignedDevId.getToDoItemId();
            Integer employeeId = assignedDevId.getEmployeeId();
            Optional<AssignedDev> devAssignedTaskData = assignedDevRepository.findByToDoItemIdAndEmployeeId(toDoItemId, employeeId);
            if (devAssignedTaskData.isPresent()) {
                return devAssignedTaskData.get();
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    // Find assigned developers by todo item ID
    public List<AssignedDev> getAssignedDevsByToDoItemId(Integer toDoItemid) {
        try {
            List<AssignedDev> devAssignedTaskData = assignedDevRepository.findByToDoItemId(toDoItemid);
            return devAssignedTaskData;
        } catch (Exception e) {
            return null;
        }
    }

    // Find assigned developers by developer ID
    public List<AssignedDev> getAssignedDevsByDevId(Integer assignedDevId) {
        try {
            List<AssignedDev> devAssignedTaskData = assignedDevRepository.findByEmployeeId(assignedDevId);
            return devAssignedTaskData;
        } catch (Exception e) {
            return null;
        }
    }

    // Add a developer
    public AssignedDev addAssignedDev(AssignedDev devAssignedTask) {
        return assignedDevRepository.save(devAssignedTask);
    }
    
    // Remove a developer
    public boolean deleteAssignedDev(Integer toDoItemId, Integer assignedDevId) {
        try {
            assignedDevRepository.deleteByToDoItemIdAndEmployeeId(toDoItemId, assignedDevId);
            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    // Update a developer's assignment by ToDoItem ID and developer ID
    public AssignedDev updateAssignedDev(Integer toDoItemId, Integer assignedDevId, AssignedDev dat) {
        Optional<AssignedDev> devAssignedTaskData = assignedDevRepository.findByToDoItemIdAndEmployeeId(toDoItemId, assignedDevId);
        if (devAssignedTaskData.isPresent()) {
            AssignedDev devAssignedTask = devAssignedTaskData.get();
            devAssignedTask.setToDoItemId(dat.getToDoItemId());
            devAssignedTask.setAssignedDevId(dat.getAssignedDevId());
            return assignedDevRepository.save(devAssignedTask);
        } else {
            return null;
        }
    }

    // Delete assigned developers by todo item ID
    public Boolean deleteAssignedDevByToDoItemId(Integer assignedDevId) {
        try {
            assignedDevRepository.deleteByToDoItemId(assignedDevId);
            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    // Delete assigned developers by developer ID
    public Boolean deleteAssignedDevByAssignedDevId(Integer assignedDevId) {
        try {
            assignedDevRepository.deleteByEmployeeId(assignedDevId);
            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }
}