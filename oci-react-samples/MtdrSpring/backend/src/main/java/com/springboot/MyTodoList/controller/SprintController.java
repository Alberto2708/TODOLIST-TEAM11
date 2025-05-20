// Controller for managing Sprint Entity and related in the ToDoList application
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.MyTodoList.model.OverdueTasksKpiResponse;
import com.springboot.MyTodoList.model.Sprint;
import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.model.WorkedHoursKpiResponse;
import com.springboot.MyTodoList.service.SprintService;
import com.springboot.MyTodoList.service.ToDoItemService;

@RestController
public class SprintController {

    @Autowired
    private SprintService sprintService;

    @Autowired
    private ToDoItemService toDoItemService;

    @Autowired
    private ToDoItemController toDoItemController;

    private static final Logger logger = LoggerFactory.getLogger(SprintController.class);

    // -------------------- GET --------------------
    // All Sprints
    @GetMapping(value = "/sprint")
    public ResponseEntity<List<Sprint>> getAllSprints() {
        try {

            List<Sprint> sprints = sprintService.findAllSprints();
            logger.info("Sprints found: " + sprints.size());
            return new ResponseEntity<>(sprints, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Sprint by ID
    @GetMapping(value = "/sprint/{sprintId}")
    public ResponseEntity<Sprint> getSprintById(@PathVariable Integer sprintId) {
        try {
            ResponseEntity<Sprint> responseEntity = sprintService.findSprintById(sprintId);
            return responseEntity;
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // All Sprints by Project ID
    @GetMapping(value = "/sprint/projects/{projectId}")
    public List<Sprint> getSprintsByProjectId(@PathVariable Integer projectId) {
        try {
            List<Sprint> sprints = sprintService.findSprintsByProjectId(projectId);
            return sprints;
        } catch (Exception e) {
            return null;
        }
    }

    // Actual Sprint by Project ID
    @GetMapping(value = "/sprint/project/{projectId}")
    public ResponseEntity<Sprint> getActualSprintByProjectId(@PathVariable Integer projectId) {
        try {
            ResponseEntity<Sprint> sprint = sprintService.findActualSprintByProjectId(projectId);
            return sprint;
        } catch (Exception e) {
            return null;
        }
    }

    // ---------- KPI ----------
    // Percentage of completed tasks by a sprint
    @GetMapping(value = "/sprint/{sprintId}/kpi")
    public ResponseEntity<Integer> getCompletedTasksPercentageBySprint(@PathVariable Integer sprintId) {
        try {
            List<ToDoItem> tasks = toDoItemService.getToDoItemsBySprintId(sprintId);
            if (tasks.size() == 0 || tasks == null) {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
            Integer sum = 0;
            for (ToDoItem task : tasks) {
                if (task.getStatus().matches("COMPLETED")) {
                    sum += 1;
                }
            }
            Integer response = (int) (((double) sum / tasks.size()) * 100);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return null;
        }
    }

    // ---------- KPI ----------
    //Get Overdue tasks percentage by sprint id KPI
    @GetMapping(value = "/sprint/{sprintId}/overdue/kpi")
    public ResponseEntity<OverdueTasksKpiResponse> getOverdueTasksBySprint(@PathVariable Integer sprintId) {
        try {
            //Get all tasks by sprint id
            List<ToDoItem> tasks = toDoItemService.getToDoItemsBySprintId(sprintId);
            //Verify if the list is empty
            //If null, return 404
            if (tasks == null) {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
            Integer completedSum = 0, overdueSum = 0;
            for (ToDoItem task : tasks) {
                logger.info(
                        "Task: " + task.getName()
                        + " - Status: " + task.getStatus()
                        + " - CompletionTs: " + task.getCompletionTs()
                        + " - Deadline: " + task.getDeadline()
                );
                if (task.getStatus().matches("COMPLETED")) {
                    completedSum += 1;
                    logger.info("Completed tasks: " + completedSum);
                    if (task.getCompletionTs() != null && task.getCompletionTs().isAfter(task.getDeadline())) {
                        overdueSum += 1;
                        logger.info("Overdue tasks: " + overdueSum);
                    }
                }
            }
            logger.info("Out of the FOR CYCLE");
            Integer percentage = (int) (((double) overdueSum / completedSum) * 100);
            logger.info("Overdue tasks percentage: " + percentage);
            return new ResponseEntity<>(new OverdueTasksKpiResponse(overdueSum, percentage), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ---------- KPI ----------
    //Get Worked Hours by sprint id based on estHours for each task.
    @GetMapping(value = "/sprint/{sprintId}/workedHours/kpi")
    public ResponseEntity<WorkedHoursKpiResponse> getWorkedHoursBySprint(@PathVariable Integer sprintId) {
        try {
            //Get all tasks by sprint id
            List<ToDoItem> tasks = toDoItemService.getToDoItemsBySprintId(sprintId);
            //Verify if the list is empty
            //If null, return 404
            Integer workedHours = 0;
            Integer workedHoursTotal = 0;
            if (tasks == null) {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }

            for (ToDoItem task : tasks) {
                logger.info("Task: " + task.getName() + " - Status: " + task.getStatus() + " - EstHours: " + task.getEstHours());
                workedHoursTotal += task.getEstHours();
                if (task.getStatus().matches("COMPLETED")) {
                    workedHours += task.getEstHours();
                }
            }
            logger.info("Worked hours Total : " + workedHoursTotal + " - Worked hours: " + workedHours);
            return new ResponseEntity<>(new WorkedHoursKpiResponse(workedHours, workedHoursTotal), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // -------------------- POST --------------------
    // Sprint
    @PostMapping(value = "/sprint")
    public ResponseEntity addSprint(@RequestBody Sprint sprint) throws Exception {
        try {
            Sprint spr = sprintService.addSprint(sprint);
            Integer responseEntity = spr.getID();
            return new ResponseEntity<>(responseEntity, HttpStatus.CREATED);

        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // -------------------- PUT --------------------
    // Sprint by ID
    @PutMapping(value = "/sprint/{id}")
    public ResponseEntity updateSprint(@RequestBody Sprint sprint, @PathVariable Integer id) {
        try {
            Sprint spr = sprintService.updateSprint(id, sprint);
            return new ResponseEntity<>(spr, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // -------------------- DELETE --------------------
    // Sprint by ID
    @DeleteMapping(value = "/sprint/{sprintId}")
    public ResponseEntity<Boolean> deleteSprint(@PathVariable Integer sprintId) {
        Boolean flag = false;
        try {
            List<Boolean> flags = new ArrayList<>();
            flags.add((Boolean) toDoItemController.deleteToDoItemBySprintId(sprintId).getBody());
            flags.add(sprintService.deleteSprint(sprintId));
            for (Boolean f : flags) {
                if (f == null) {
                    return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
                }
                if (f == false) {
                    return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
            flag = true;
            return new ResponseEntity<>(flag, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
