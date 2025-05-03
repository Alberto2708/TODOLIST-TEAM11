package com.springboot.MyTodoList.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import com.springboot.MyTodoList.model.ToDoItem;

import com.springboot.MyTodoList.model.AssignedDev;
import com.springboot.MyTodoList.model.AssignedDevId;
import com.springboot.MyTodoList.model.Employee;
import com.springboot.MyTodoList.model.SubToDoItem;
import com.springboot.MyTodoList.model.WorkedHoursKpiResponse;
import com.springboot.MyTodoList.model.OverdueTasksKpiResponse;
import com.springboot.MyTodoList.service.AssignedDevService;
import com.springboot.MyTodoList.service.ToDoItemService;
import oracle.jdbc.proxy.annotation.GetProxy;

import com.springboot.MyTodoList.service.SubToDoItemService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder.In;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.apache.http.protocol.ResponseServer;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//Get assigned tasks by developer id and sprint id



@RestController
public class AssignedDevController {
    private static final Logger logger = LoggerFactory.getLogger(AssignedDevController.class);

    @Autowired
    private AssignedDevService assignedDevService;
    @Autowired
    private ToDoItemService toDoItemService;
    @Autowired
    private SubToDoItemService subToDoItemService;


    @GetMapping(value = "/assignedDev")
    public List<AssignedDev> getAllDevAssignedTasks() {
        return assignedDevService.findAll();
    }
    

    //Get Mapping to get AssifnedDev by ToDoItemId and AssignedDevId
    @GetMapping(value = "/assignedDev/{toDoItemId}/{assignedDevId}")
    public ResponseEntity<AssignedDev> getDevAssignedTaskByToDoItemId(@PathVariable Integer toDoItemId, @PathVariable Integer assignedDevId) {
        try{
            AssignedDevId assignedDevIdObj = new AssignedDevId(toDoItemId, assignedDevId);
            AssignedDev assignedDev = assignedDevService.findAssignedDevById(assignedDevIdObj);
            return new ResponseEntity<>(assignedDev, HttpStatus.OK);
        }catch(Exception e){
            System.out.println(e);
            return null;
        }
    }

    @GetMapping(value = "/assignedDev/{assignedDevId}")
    public List<ResponseEntity<ToDoItem>> getDevAssignedTasksByAssignedDevId(@PathVariable Integer assignedDevId) {
        try{
            List<AssignedDev>  assignedDevs = assignedDevService.getAssignedDevsByDevId(assignedDevId);
            System.out.println(assignedDevs.size());
            List<ResponseEntity<ToDoItem>> tasks = new ArrayList<>();
            for(AssignedDev task : assignedDevs){
                //System.out.println(task.getToDoItemId());
                tasks.add(toDoItemService.getItemById(task.getToDoItemId()));
            }
            return tasks;
        }catch(Exception e){
            System.out.println(e);
            return null;
        }
    }

    //Get assigned tasks by developer id and sprint id
    @GetMapping(value = "/assignedDev/{assignedDevId}/sprint/{sprintId}")
    public ResponseEntity<List<ToDoItem>> getAssignedTasksByAssignedDevAndSprint(@PathVariable Integer assignedDevId, @PathVariable Integer sprintId) {
        try{
            List<AssignedDev>  assignedDevs = assignedDevService.getAssignedDevsByDevId(assignedDevId);
            //System.out.println(assignedDevs.size());
            List<ToDoItem> tasks = new ArrayList<>();
            for(AssignedDev task : assignedDevs){
                if (toDoItemService.getItemById(task.getToDoItemId()).getBody().getSprintId() == sprintId){
                    tasks.add(toDoItemService.getItemById(task.getToDoItemId()).getBody());
                }
            }
            return new ResponseEntity<>(tasks, HttpStatus.OK);
        }catch(Exception e){
            System.out.println(e);
            return null;
        }
    }
    //KPI
    //Get Completed percentage of assigned tasks by developer id and sprint id KPI
    @GetMapping(value="/assignedDev/{assignedDevId}/sprint/{sprintId}/completedTasks/kpi")
    public Integer getCompletedTasksByEmployeeAndSprint(@PathVariable Integer assignedDevId, @PathVariable Integer sprintId) {
        try{
            List<ToDoItem> tasks = getAssignedTasksByAssignedDevAndSprint(assignedDevId, sprintId).getBody();
            Integer sum = 0;
            for (ToDoItem task : tasks){
                logger.info("Task ID: " + task.getID() + ", Status: " + task.getStatus());
                if (task.getStatus().matches("COMPLETED")){
                    sum += 1;
                }
            }
            Integer response = (int) (((double) sum / tasks.size()) * 100);
            return response;
        }catch(Exception e){
            System.out.println(e);
            return null;
        }
    }

    //KPI
    //Get Worked Hours by developer id and sprint id based on estHours for each task KPI.
    @GetMapping(value = "/assignedDev/{assignedDevId}/sprint/{sprintId}/workedHours/kpi")
    public ResponseEntity<WorkedHoursKpiResponse> getWorkedHoursByEmployeeAndSprint(@PathVariable Integer assignedDevId, @PathVariable Integer sprintId) {
        try{
            List<ToDoItem> tasks = getAssignedTasksByAssignedDevAndSprint(assignedDevId, sprintId).getBody();
            Integer workedHours = 0;
            Integer workedHoursTotal = 0;
            if (tasks == null){
                return new ResponseEntity<> (null, HttpStatus.NOT_FOUND);
            }

            for (ToDoItem task : tasks) {
                logger.info("Task ID: " + task.getID() + ", Status: " + task.getStatus() + ", EstHours: " + task.getEstHours());
                workedHoursTotal += task.getEstHours();
                if (task.getStatus().matches("COMPLETED")){
                    workedHours += task.getEstHours();
                }
            }
            
            return new ResponseEntity<>(new WorkedHoursKpiResponse(workedHours, workedHoursTotal), HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e);
            return null;
        }
    }

    //KPI
    //Get Overdue tasks Sum and Overdue tasks percentage by developer id and sprint id KPI
    @GetMapping(value="/assignedDev/{assignedDevId}/sprint/{sprintId}/overdueTasks/kpi")
    public ResponseEntity<OverdueTasksKpiResponse> getPercentageOfOverdueTasksByDeveloperIdAndSprintId(@PathVariable Integer assignedDevId, @PathVariable Integer sprintId) {
        try{
            //First we get the tasks assigned to the developer and sprint
            List<ToDoItem> tasks = getAssignedTasksByAssignedDevAndSprint(assignedDevId, sprintId).getBody();
            if (tasks == null){
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
            //we initialize the variables to count the completed and overdue tasks
            Integer completedSum = 0, overdueSum = 0;
            for (ToDoItem task : tasks){
                logger.info("Task ID: " + task.getID() + ", Status: " + task.getStatus());
                //We check if the task is completed
                if (task.getStatus().matches("COMPLETED")){
                    completedSum += 1;
                    //We check if the task is overdue
                    if( task.getCompletionTs()!= null && task.getCompletionTs().isAfter(task.getDeadline())){
                        overdueSum += 1;
                    }
                }
            }
            Integer percentage = (int) (((double) overdueSum / completedSum) * 101);
            return new ResponseEntity<>(new OverdueTasksKpiResponse(overdueSum, percentage), HttpStatus.OK);
        }catch(Exception e){
            logger.error("Error: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    
    
    
    

    //Get father tasks by developer id and sprint id
    @GetMapping(value = "/assignedDev/{assignedDevId}/sprint/{sprintId}/father")
    public List<ResponseEntity<ToDoItem>> getAssignedTasksByAssignedDevAndSprintFather(@PathVariable Integer assignedDevId, @PathVariable Integer sprintId) {
        try{
            List<AssignedDev>  assignedDevs = assignedDevService.getAssignedDevsByDevId(assignedDevId);
            List<ResponseEntity<ToDoItem>> tasks = new ArrayList<>();
            for(AssignedDev task : assignedDevs){
                //Add logic for subtasks verification
                if (toDoItemService.getItemById(task.getToDoItemId()).getBody().getSprintId() == sprintId ){
                    if (subToDoItemService.checkIfIdIsntSubToDoItem(task.getToDoItemId())){
                        tasks.add(toDoItemService.getItemById(task.getToDoItemId()));
                    }
                }
            }
            return tasks;
        }catch(Exception e){
            System.out.println(e);
            return null;
        }
    }

    //Get Father Completed tasks by developer id and sprint id
    @GetMapping(value = "/assignedDev/{assignedDevId}/sprint/{sprintId}/father/completed")
    public ResponseEntity<List<ToDoItem>> getCompletedTasksByEmployeeAndSprintFather(@PathVariable Integer assignedDevId, @PathVariable Integer sprintId) {
        try{
            List<ResponseEntity<ToDoItem>> tasks = getAssignedTasksByAssignedDevAndSprintFather(assignedDevId, sprintId);
            if (tasks == null || tasks.isEmpty()){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            List<ToDoItem> completedTasks = new ArrayList<>();
            for (ResponseEntity<ToDoItem> task : tasks){
                if (task.getBody().getStatus().matches("COMPLETED")){
                    completedTasks.add(task.getBody());
                }
            }
            return new ResponseEntity<>(completedTasks, HttpStatus.OK);
        }catch(Exception e){
            System.out.println(e);
            return null;
        }
    }

    //Get Father Pending tasks by developer id and sprint id
    @GetMapping(value = "/assignedDev/{assignedDevId}/sprint/{sprintId}/father/pending")
    public ResponseEntity<List<ToDoItem>> getPendingTasksByEmployeeAndSprintFather(@PathVariable Integer assignedDevId, @PathVariable Integer sprintId) {
        try{
            List<ResponseEntity<ToDoItem>> tasks = getAssignedTasksByAssignedDevAndSprintFather(assignedDevId, sprintId);
            if (tasks == null || tasks.isEmpty()){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            List<ToDoItem> pendingTasks = new ArrayList<>();
            for (ResponseEntity<ToDoItem> task : tasks){
                if (task.getBody().getStatus().matches("PENDING")){
                    pendingTasks.add(task.getBody());
                }
            }
            return new ResponseEntity<>(pendingTasks, HttpStatus.OK);
        }catch(Exception e){
            System.out.println(e);
            return null;
        }
    }




    //KPI
    //calculates the average number of days a developer takes to complete their assigned tasks.
    //Positive values means developer is completing tasks faster than the deadline.
    //Negative values means developer is completing tasks slower than the deadline.
    @GetMapping(value = "/assignedDev/{assignedDevId}/averageDaysDif/kpi")
    public ResponseEntity<Float> getCompletionDaysMean(@PathVariable Integer assignedDevId) {
        try{
            List<AssignedDev>  assignedDev = assignedDevService.getAssignedDevsByDevId(assignedDevId);
            if (assignedDev == null){
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
            //System.out.println(assignedDev.size()); // Return number of tasks assigned to the developer.
            Float sum = 0.0f;
            Integer size = 0;
            for(AssignedDev task : assignedDev){
                //System.out.println(task.getToDoItemId()); //Returns the task id
                //System.out.println(toDoItemService.getItemById(task.getToDoItemId()).getBody().getStatus()); //Returns the status of the task
                ToDoItem toDoItem = toDoItemService.getItemById(task.getToDoItemId()).getBody();
                if (toDoItem.getStatus().matches("COMPLETED")){
                    logger.info("Task ID: " + toDoItem.getID() + ", Status: " + toDoItem.getStatus() + ", CompletionTs: " + toDoItem.getCompletionTs() + ", Deadline: " + toDoItem.getDeadline());
                    sum += toDoItem.getDeadline().getYear()*365 + toDoItem.getDeadline().getDayOfYear() - toDoItem.getCompletionTs().getYear()*365 - toDoItem.getCompletionTs().getDayOfYear();
                    size += 1;
                    logger.info("Sum" + sum.toString());
                    //System.out.println("Hello World from adding task!!!");
                }
            }
            return new ResponseEntity<>(sum/size, HttpStatus.OK);
            
        }catch(Exception e){
            System.out.println(e);
            return null;
        }
    }

    //KPI
    //calculates the sum of overdue tasks completed by employee 
    ////MERGED the endpoint of the method with the KPI endpoint to get percentage of overdue tasks completed by employee and sprint. 
    /*@GetMapping(value = "/assignedDev/{assignedDevId}/overdue/kpi")
    public ResponseEntity<Integer> getSumOfOverdueTasksByEmployee(@PathVariable Integer assignedDevId) {
        try{
            List<AssignedDev>  assignedDev = assignedDevService.getAssignedDevsByDevId(assignedDevId);
            if (assignedDev == null){
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
            //System.out.println(assignedDev.size()); // Return number of tasks assigned to the developer.
            Integer sum = 0;
            for(AssignedDev task : assignedDev){
                System.out.println(task.toString());
                //System.out.println(task.getToDoItemId()); //Returns the task id
                //System.out.println(toDoItemService.getItemById(task.getToDoItemId()).getBody().getStatus()); //Returns the status of the task
                ToDoItem toDoItem = toDoItemService.getItemById(task.getToDoItemId()).getBody();

                //If toDoItems deadline timestamps are properly set, you should use the commented if
                if (toDoItem.getStatus().matches("COMPLETED") && toDoItem.getCompletionTs().isAfter(toDoItem.getDeadline())){
                //if (toDoItem.getStatus().matches("COMPLETED") && toDoItem.getCompletionTs().getYear()*365 + toDoItem.getCompletionTs().getDayOfYear() > toDoItem.getDeadline().getYear()*365 + toDoItem.getDeadline().getDayOfYear()){
                    logger.info("Task ID: " + toDoItem.getID() + ", Status: " + toDoItem.getStatus() + ", CompletionTs: " + toDoItem.getCompletionTs() + ", Deadline: " + toDoItem.getDeadline());
                    sum += 1;
                }
            }
            return new ResponseEntity<>(sum, HttpStatus.OK);
        }catch(Exception e){
            System.out.println(e);
            return null;
        }
    }*/

    @PostMapping(value = "/assignedDev")
    public ResponseEntity addDevAssignedTask(@RequestBody AssignedDev devAssignedTask) throws Exception{
        try{
            AssignedDev emp = assignedDevService.addAssignedDev(devAssignedTask);
            AssignedDevId responseEntity = emp.getId();
            return new ResponseEntity<>(responseEntity, HttpStatus.CREATED);

        }catch(Exception e){
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @DeleteMapping(value = "assignedDev/{toDoItemId}/{assignedDevId}")
    public ResponseEntity deleteDevAssignedTask(@PathVariable Integer toDoItemId, @PathVariable Integer assignedDevId) {
        try{
            Boolean status = assignedDevService.deleteAssignedDev(toDoItemId, assignedDevId);
            System.out.println(status);
            if(status == null){ return new ResponseEntity<>(HttpStatus.NOT_FOUND);}
            if (status == false){ return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);}
            return new ResponseEntity<>(status, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(value = "assignedDev/toDoItem/{toDoItemId}")
    public ResponseEntity deleteDevAssignedTaskByToDoItemId(@PathVariable Integer toDoItemId) {
        try{
            Boolean status = assignedDevService.deleteAssignedDevByToDoItemId(toDoItemId);
            System.out.println(status);
            if(status == null){ return new ResponseEntity<>(HttpStatus.NOT_FOUND);}
            if (status == false){ return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);}
            return new ResponseEntity<>(status, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(value = "assignedDev/dev/{assignedDevId}")
    public ResponseEntity deleteDevAssignedTaskByAssignedDevId(@PathVariable Integer assignedDevId) {
        try{
            Boolean status = assignedDevService.deleteAssignedDevByAssignedDevId(assignedDevId);
            System.out.println(status);
            if(status == null){ return new ResponseEntity<>(HttpStatus.NOT_FOUND);}
            if (status == false){ return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);}
            return new ResponseEntity<>(status, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
