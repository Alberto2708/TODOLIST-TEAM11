package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.Sprint;
import com.springboot.MyTodoList.repository.SprintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.List;

@Service
public class SprintService {
    @Autowired
    private SprintRepository sprintRepository;

    public ResponseEntity<Sprint> findSprintById(Integer sprintId) {
        try {
            Optional <Sprint> sprint = sprintRepository.findById(sprintId);
            return new ResponseEntity<>(sprint.get(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public List<Sprint> findSprintsByProjectId(Integer projectId) {
        try {
            List<Sprint> sprints = sprintRepository.findSprintsByProjectId(projectId);
            return sprints;
        } catch (Exception e) {
            return null;
        }
    }

    public Sprint addSprint(Sprint sprint) throws Exception {
        try {
            return sprintRepository.save(sprint);
        } catch (Exception e) {
            throw new Exception("Error adding sprint: " + e.getMessage());
        }
    }

}
