package com.licence.serban.farmcompanion.classes.models;

import com.licence.serban.farmcompanion.classes.TaskType;
import com.licence.serban.farmcompanion.classes.WorkState;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Serban on 14.04.2017.
 */

public class Task {
    private String id;
    private TaskType type;
    private List<ResourcePlaceholder> inputs;
    private List<ResourcePlaceholder> employees;
    private List<ResourcePlaceholder> usedImplements;
    private ResourcePlaceholder field;
    private WorkState currentState;
    private Date startDate;
    private Date stopDate;
    private List<Date> intermediaries;
    private long totalTime;
    private long timeStopped;
    private double area;
    private boolean canTrack;

    private Task() {

    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {

        return id;
    }

    public ResourcePlaceholder getField() {
        return field;
    }

    public WorkState getCurrentState() {
        return currentState;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getStopDate() {
        return stopDate;
    }

    public List<Date> getIntermediaries() {
        return intermediaries;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public long getTimeStopped() {
        return timeStopped;
    }

    public double getArea() {
        return area;
    }

    public boolean isCanTrack() {
        return canTrack;
    }

    public TaskType getType() {
        return type;

    }

    public void setType(TaskType type) {
        this.type = type;
    }

    public List<ResourcePlaceholder> getInputs() {
        return inputs;
    }

    public void setInputs(List<ResourcePlaceholder> inputs) {
        this.inputs = inputs;
    }

    public List<ResourcePlaceholder> getEmployees() {
        return employees;
    }

    public void setEmployees(List<ResourcePlaceholder> employees) {
        this.employees = employees;
    }

    public List<ResourcePlaceholder> getUsedImplements() {
        return usedImplements;
    }

    public void setUsedImplements(List<ResourcePlaceholder> usedImplements) {
        this.usedImplements = usedImplements;
    }

    public Task(ResourcePlaceholder field) {
        this.inputs = new ArrayList<>();
        this.employees = new ArrayList<>();
        this.usedImplements = new ArrayList<>();
        this.field = field;

    }

    public void addEmployee(ResourcePlaceholder emp) {
        this.employees.add(emp);
    }
}
