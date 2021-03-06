package com.licence.serban.farmcompanion.tasks.adapters;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.licence.serban.farmcompanion.consumables.adapters.ConsumableDatabaseAdapter;
import com.licence.serban.farmcompanion.emp_account.adapters.EmpTasksAdapter;
import com.licence.serban.farmcompanion.equipment.adapters.EquipmentDatabaseAdapter;
import com.licence.serban.farmcompanion.misc.Utilities;
import com.licence.serban.farmcompanion.misc.WorkState;
import com.licence.serban.farmcompanion.tasks.models.ResourcePlaceholder;
import com.licence.serban.farmcompanion.tasks.models.Task;

/**
 * Created by Serban on 14.04.2017.
 */

public class TasksDatabaseAdapter {

  public static final String DB_TASKS = "tasks";
  public static final String DB_INPUTS = "inputs";

  private static String userID;
  private static TasksDatabaseAdapter instance;
  private static DatabaseReference tasksReference;
  private static DatabaseReference mainReference;

  private static ChildEventListener childEventListener;

  private TasksDatabaseAdapter() {

  }

  private TasksDatabaseAdapter(String adminID) {
    userID = adminID;
    mainReference = FirebaseDatabase.getInstance().getReference();
    tasksReference = mainReference.child(DB_TASKS).child(adminID);
  }

  public static synchronized TasksDatabaseAdapter getInstance(String adminID) {
    if (instance == null) {
      instance = new TasksDatabaseAdapter(adminID);
    } else {
      setTasksReference(adminID);
    }
    return instance;
  }

  private static void setTasksReference(String adminID) {
    userID = adminID;
    tasksReference = FirebaseDatabase.getInstance().getReference().child(DB_TASKS).child(adminID);
  }

  public DatabaseReference getTasksReference() {
    return tasksReference;
  }

  public void insertTask(String id, Task task, OnCompleteListener<Void> completeListener) {
    task.setId(id);
    task.setCreatedAt(System.currentTimeMillis());
    tasksReference.child(id).setValue(task).addOnCompleteListener(completeListener);

    DatabaseReference inputsReference = mainReference.child(ConsumableDatabaseAdapter.DB_CONSUMABLES).child(userID);
    for (ResourcePlaceholder ph : task.getInputs())
      inputsReference.child(ph.getId()).child(DB_TASKS).child(id).setValue(true);

    DatabaseReference employeesReference = mainReference.child(Utilities.Constants.DB_EMPLOYEES).child(userID);
    for (ResourcePlaceholder ph : task.getEmployees())
      employeesReference.child(ph.getId()).child(DB_TASKS).child(id).setValue(true);

    DatabaseReference fieldsReference = mainReference.child(Utilities.Constants.DB_FIELDS).child(userID);
    if (task.getField() != null) {
      fieldsReference.child(task.getField().getId()).child(DB_TASKS).child(id).setValue(true);
    }

    DatabaseReference equipmentReference = mainReference.child(EquipmentDatabaseAdapter.DB_EQUIPMENTS).child(userID);
    for (ResourcePlaceholder ph : task.getUsedImplements())
      equipmentReference.child(ph.getId()).child(DB_TASKS).child(id).setValue(true);
  }

  public void setListener(final TaskAdapter adapter) {
    childEventListener = new ChildEventListener() {
      @Override
      public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Task task = dataSnapshot.getValue(Task.class);
        if (task != null) {
          adapter.add(task);
        }
      }

      @Override
      public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        Task task = dataSnapshot.getValue(Task.class);
        if (task != null) {
          adapter.updateTask(task);
        }
      }

      @Override
      public void onChildRemoved(DataSnapshot dataSnapshot) {
        String id = dataSnapshot.getKey();
        adapter.removeTask(id);
      }

      @Override
      public void onChildMoved(DataSnapshot dataSnapshot, String s) {

      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    };
    tasksReference.addChildEventListener(childEventListener);
  }

  public void setListener(final TaskAdapter adapter, WorkState state) {
    childEventListener = new ChildEventListener() {
      @Override
      public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Task task = dataSnapshot.getValue(Task.class);
        if (task != null) {
          adapter.add(task);
        }
      }

      @Override
      public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        Task task = dataSnapshot.getValue(Task.class);
        if (task != null) {
          adapter.updateTask(task);
        }
      }

      @Override
      public void onChildRemoved(DataSnapshot dataSnapshot) {
        String id = dataSnapshot.getKey();
        adapter.removeTask(id);
      }

      @Override
      public void onChildMoved(DataSnapshot dataSnapshot, String s) {

      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    };
    tasksReference.orderByChild("currentState").equalTo(state.toString()).addChildEventListener(childEventListener);
  }

  public void setListener(final EmpTasksAdapter adapter, final String empID) {
    childEventListener = new ChildEventListener() {
      @Override
      public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Task task = dataSnapshot.getValue(Task.class);
        if (task != null && task.hasEmployee(empID)) {
          adapter.add(task);
        }
      }

      @Override
      public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        Task task = dataSnapshot.getValue(Task.class);
        if (task != null && task.hasEmployee(empID)) {
          adapter.updateTask(task);
        }
      }

      @Override
      public void onChildRemoved(DataSnapshot dataSnapshot) {
        String id = dataSnapshot.getKey();
        adapter.removeTask(id);
      }

      @Override
      public void onChildMoved(DataSnapshot dataSnapshot, String s) {

      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    };
    tasksReference.addChildEventListener(childEventListener);
  }

  public void removeListeners() {
    tasksReference.removeEventListener(childEventListener);
  }

  public void deleteTask(String taskId) {
    tasksReference.child(taskId).removeValue();
  }

}
