package com.springboot.MyTodoList.model;

public class WorkedHoursKpiResponse {

    //Attributes
    public Double workedHoursKpi;
    public Double totalEstHours;

    //Constructor
    public WorkedHoursKpiResponse(Double workedHoursKpi, Double totalEstHours) {
        this.workedHoursKpi = workedHoursKpi;
        this.totalEstHours = totalEstHours;
    }

    //Getters and Setters
    public Double getWorkedHoursKpi() {
        return workedHoursKpi;
    }

    public void setWorkedHoursKpi(Double workedHoursKpi) {
        this.workedHoursKpi = workedHoursKpi;
    }

    public Double getTotalEstHours() {
        return totalEstHours;
    }

    public void setTotalEstHours(Double totalEstHours) {
        this.totalEstHours = totalEstHours;
    }
}
