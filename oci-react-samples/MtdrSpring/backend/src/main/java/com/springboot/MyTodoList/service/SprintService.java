package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.Employee;
import com.springboot.MyTodoList.model.Sprint;
import com.springboot.MyTodoList.repository.SprintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.List;
import java.time.OffsetDateTime;
 

@Service
public class SprintService {
    @Autowired
    private SprintRepository sprintRepository;

    public List<Sprint> findAllSprints(){
        try{
            List<Sprint> sprints = sprintRepository.findAll();
            return sprints;
        }catch (Exception e) {
            return null;
        }
    }

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
    
    public ResponseEntity<Sprint> findActualSprintByProjectId(Integer projectId) {
        try {
            List<Sprint> sprints = findSprintsByProjectId(projectId);
            for (Sprint sprint : sprints) {
                OffsetDateTime startDate = sprint.getStartDate();
                OffsetDateTime endDate = sprint.getEndDate().plusDays(1);
                if (startDate.isBefore(OffsetDateTime.now()) && (endDate.isAfter(OffsetDateTime.now()) || endDate.isEqual(OffsetDateTime.now()))) {
                    return new ResponseEntity<> (sprint, HttpStatus.OK);
                }
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public Sprint addSprint(Sprint sprint) throws Exception {
        try {
            return sprintRepository.save(sprint);
        } catch (Exception e) {
            throw new Exception("Error adding sprint: " + e.getMessage());
        }
    }

    public Sprint updateSprint(Integer sprintId, Sprint spr) {
        Optional<Sprint> sprintData = sprintRepository.findById(sprintId);
        if(sprintData.isPresent()){
            Sprint sprint = sprintData.get();
            sprint.setID(sprintId);

            if (spr.getName() != null){
                sprint.setName(spr.getName());
            }
            if (spr.getProjectId() != null){
                sprint.setProjectId(spr.getProjectId());
            }
            if (spr.getStartDate() != null){
                sprint.setStartDate(spr.getStartDate());
            }
            if (spr.getEndDate() != null){
                sprint.setEndDate(spr.getEndDate());
            }
            return sprintRepository.save(sprint);
        }else{
            return null;
        }
    }

    public Boolean deleteSprint(Integer sprintId) {
        try {
            sprintRepository.deleteById(sprintId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
