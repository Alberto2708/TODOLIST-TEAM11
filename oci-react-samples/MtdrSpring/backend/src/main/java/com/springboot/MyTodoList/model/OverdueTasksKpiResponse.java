// Model for the KPI of overdue tasks response

package com.springboot.MyTodoList.model;

public class OverdueTasksKpiResponse {
    //Attributes

    public Integer taskKpi;
    public Integer tasksKpiPercentage;

    //Constructor
    public OverdueTasksKpiResponse(Integer taskKpi, Integer tasksKpiPercentage) {
        this.taskKpi = taskKpi;
        this.tasksKpiPercentage = tasksKpiPercentage;
    }

    //Getters and Setters
    public Integer getTaskKpi() {
        return taskKpi;
    }

    public void setTasksKpi(Integer taskKpi) {
        this.taskKpi = taskKpi;
    }

    public Integer getTasksKpiPercentage() {
        return tasksKpiPercentage;
    }

    public void setTasksKpiPercentage(Integer tasksKpiPercentage) {
        this.tasksKpiPercentage = tasksKpiPercentage;
    }
}
