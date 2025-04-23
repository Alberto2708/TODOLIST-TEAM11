package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.AssignedDev;
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

    public List<AssignedDev> getAssignedDevsByToDoItemId(Integer toDoItemid) {
        try {
            List<AssignedDev> devAssignedTaskData = assignedDevRepository.findByToDoItemId(toDoItemid);
            return devAssignedTaskData;
        } catch (Exception e) {
            return null;
        }
    }

    public List<AssignedDev> getAssignedDevsByDevId(Integer assignedDevId) {
        try {
            List<AssignedDev> devAssignedTaskData = assignedDevRepository.findByEmployeeId(assignedDevId);
            return devAssignedTaskData;
        } catch (Exception e) {
            return null;
        }
    }

    public AssignedDev addAssignedDev(AssignedDev devAssignedTask) {
        return assignedDevRepository.save(devAssignedTask);
    }
    

    public boolean deleteAssignedDev(Integer toDoItemId, Integer assignedDevId) {
        try {
            assignedDevRepository.deleteByToDoItemIdAndEmployeeId(toDoItemId, assignedDevId);
            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

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
}