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

    public ResponseEntity<DevAssignedTask> getDevAssignedTaskById(int id) {
        Optional<DevAssignedTask> devAssignedTaskData = devAssignedTaskRepository.findById(id);
        if (devAssignedTaskData.isPresent()) {
            return new ResponseEntity<>(devAssignedTaskData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public DevAssignedTask addDevAssignedTask(DevAssignedTask devAssignedTask) {
        return devAssignedTaskRepository.save(devAssignedTask);
    }

    public boolean deleteDevAssignedTask(int id) {
        try {
            devAssignedTaskRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public DevAssignedTask updateDevAssignedTask(int id, DevAssignedTask dat) {
        Optional<DevAssignedTask> devAssignedTaskData = devAssignedTaskRepository.findById(id);
        if (devAssignedTaskData.isPresent()) {
            DevAssignedTask devAssignedTask = devAssignedTaskData.get();
            devAssignedTask.setToDoItemId(id);
            devAssignedTask.setAssignedDevId(dat.getAssignedDevId());
            return devAssignedTaskRepository.save(devAssignedTask);
        } else {
            return null;
        }
    }
}