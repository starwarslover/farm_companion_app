package com.licence.serban.farmcompanion.classes.models;

import com.licence.serban.farmcompanion.classes.TaskType;
import com.licence.serban.farmcompanion.classes.WorkState;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Serban on 14.04.2017.
 */

public class Task {
    private String id;
    private String type;
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
    private long createdAt;

    private Task() {

    }

    public Task(ResourcePlaceholder field) {
        this.inputs = new ArrayList<>();
        this.employees = new ArrayList<>();
        this.usedImplements = new ArrayList<>();
        this.field = field;

    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getId() {

        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ResourcePlaceholder getField() {
        return field;
    }

    public void setField(ResourcePlaceholder field) {
        this.field = field;
    }

    public WorkState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(String currentState) {
        this.currentState = WorkState.valueOf(currentState);
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        try {
            this.startDate = new SimpleDateFormat("dd MM yyyy HH:mm:ss zz", Locale.ENGLISH).parse(startDate);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Date getStopDate() {

        return stopDate;
    }

    public void setStopDate(String date) {
        try {
            this.stopDate = new SimpleDateFormat("dd MM yyyy HH:mm:ss zz", Locale.ENGLISH).parse(date);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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

    public String getType() {
        return type;

    }

    public void setType(String type) {
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

    public void addEmployee(ResourcePlaceholder emp) {
        this.employees.add(emp);
    }

    public void addEquipment(ResourcePlaceholder equip) {
        this.usedImplements.add(equip);
    }

    public void startTask(boolean canTrack) {
        this.startDate = new Date(Calendar.getInstance().getTimeInMillis());
        this.canTrack = canTrack;
    }

    public void stopTask() {
        this.stopDate = Calendar.getInstance().getTime();
        this.canTrack = false;
    }
}
