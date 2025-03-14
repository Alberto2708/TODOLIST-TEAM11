package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.DevAssignedTask;
import com.springboot.MyTodoList.repository.DevAssignedTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DevAssignedTaskService {
    @Autowired
    private DevAssignedTaskRepository devAssignedTaskRepository;

    public List<DevAssignedTask> findAll() {
        List<DevAssignedTask> devAssignedTasks = devAssignedTaskRepository.findAll();
        return devAssignedTasks;
    }

    public List<DevAssignedTask> getDevAssignedTasksByToDoItemId(Integer toDoItemid) {
        try {
            List<DevAssignedTask> devAssignedTaskData = devAssignedTaskRepository.findByToDoItemId(toDoItemid);
            return devAssignedTaskData;
        } catch (Exception e) {
            return null;
        }
    }

    public List<DevAssignedTask> getDevAssignedTasksByAssignedDevId(Integer assignedDevId) {
        try {
            List<DevAssignedTask> devAssignedTaskData = devAssignedTaskRepository.findByAssignedDevId(assignedDevId);
            return devAssignedTaskData;
        } catch (Exception e) {
            return null;
        }
    }

    public DevAssignedTask addDevAssignedTask(DevAssignedTask devAssignedTask) {
        return devAssignedTaskRepository.save(devAssignedTask);
    }

    public boolean deleteDevAssignedTask(Integer toDoItemId, Integer assignedDevId) {
        try {
            devAssignedTaskRepository.deleteByToDoItemIdAndAssignedDevId(toDoItemId, assignedDevId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public DevAssignedTask updateDevAssignedTask(Integer toDoItemId, Integer assignedDevId, DevAssignedTask dat) {
        Optional<DevAssignedTask> devAssignedTaskData = devAssignedTaskRepository.findByToDoItemIdAndAssignedDevId(toDoItemId, assignedDevId);
        if (devAssignedTaskData.isPresent()) {
            DevAssignedTask devAssignedTask = devAssignedTaskData.get();
            devAssignedTask.setToDoItemId(dat.getToDoItemId());
            devAssignedTask.setAssignedDevId(dat.getAssignedDevId());
            return devAssignedTaskRepository.save(devAssignedTask);
        } else {
            return null;
        }
    }
}