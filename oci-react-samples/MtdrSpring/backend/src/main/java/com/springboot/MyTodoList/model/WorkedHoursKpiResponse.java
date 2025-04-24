package com.springboot.MyTodoList.model;

public class WorkedHoursKpiResponse {
    //Attributes

    public Integer workedHoursKpi;
    public Integer totalEstHours;

    //Constructor
    public WorkedHoursKpiResponse(Integer workedHoursKpi, Integer totalEstHours) {
        this.workedHoursKpi = workedHoursKpi;
        this.totalEstHours = totalEstHours;
    }

    //Getters and Setters
    public Integer getWorkedHoursKpi() {
        return workedHoursKpi;
    }

    public void setWorkedHoursKpi(Integer workedHoursKpi) {
        this.workedHoursKpi = workedHoursKpi;
    }

    public Integer getTotalEstHours() {
        return totalEstHours;
    }

    public void setTotalEstHours(Integer totalEstHours) {
        this.totalEstHours = totalEstHours;
    }
}
