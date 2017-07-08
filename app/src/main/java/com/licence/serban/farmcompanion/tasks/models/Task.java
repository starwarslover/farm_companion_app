package com.licence.serban.farmcompanion.tasks.models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.licence.serban.farmcompanion.activities.MainActivity;
import com.licence.serban.farmcompanion.misc.WorkState;
import com.licence.serban.farmcompanion.tasks.adapters.TasksDatabaseAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
  //  private long startDate;
//  private long stopDate;
  private List<Long> startDates;
  private List<Long> stopDates;
  private long totalTime;
  private long timeStopped;
  private double area;
  private boolean canTrack;
  private long createdAt;

  public List<Long> getStopDates() {
    return stopDates;
  }

  public void setStopDates(List<Long> stopDates) {
    this.stopDates = stopDates;
  }

  public List<Long> getStartDates() {

    return startDates;
  }

  public void setStartDates(List<Long> startDates) {
    this.startDates = startDates;
  }

  private Task() {

  }

  public Task(ResourcePlaceholder field) {
    this.inputs = new ArrayList<>();
    this.employees = new ArrayList<>();
    this.usedImplements = new ArrayList<>();
    this.startDates = new ArrayList<>();
    this.stopDates = new ArrayList<>();
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

//  public long getStartDate() {
//    return startDate;
//  }
//
//  public void setStartDate(long startDate) {
//    this.startDate = startDate;
//  }
//
//  public long getStopDate() {
//
//    return stopDate;
//  }
//
//  public void setStopDate(long date) {
//    this.stopDate = date;
//  }

  public List<Long> getIntermediaries() {
    return startDates;
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
    if (this.startDates == null)
      this.startDates = new ArrayList<>();
    this.startDates.add(Calendar.getInstance().getTimeInMillis());
    this.currentState = WorkState.STARTED;
    this.canTrack = canTrack;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(TasksDatabaseAdapter.DB_TASKS).child(MainActivity.adminID).child(this.id);
    ref.child("startDates").setValue(this.startDates);
    ref.child("currentState").setValue(this.currentState);
    ref.child("canTrack").setValue(this.canTrack);
  }

  public void stopTask() {
    if (this.stopDates == null)
      this.stopDates = new ArrayList<>();
    this.stopDates.add(Calendar.getInstance().getTimeInMillis());
    this.canTrack = false;
    this.currentState = WorkState.FINISHED;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(TasksDatabaseAdapter.DB_TASKS).child(MainActivity.adminID).child(this.id);
    ref.child("currentState").setValue(this.currentState);
    ref.child("stopDates").setValue(this.stopDates);
    ref.child("canTrack").setValue(this.canTrack);
  }

  public boolean hasEmployee(String empId) {
    if (this.employees != null)
      for (ResourcePlaceholder ph : this.employees) {
        if (ph.getId().equals(empId)) {
          return true;
        }
      }
    return false;
  }
}
