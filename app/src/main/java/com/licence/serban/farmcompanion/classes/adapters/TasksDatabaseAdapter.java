package com.licence.serban.farmcompanion.classes.adapters;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.licence.serban.farmcompanion.classes.Utilities;
import com.licence.serban.farmcompanion.classes.models.Task;

import java.text.SimpleDateFormat;

/**
 * Created by Serban on 14.04.2017.
 */

public class TasksDatabaseAdapter {

    public static final String DB_TASKS = "tasks";

    private static TasksDatabaseAdapter instance;
    private static DatabaseReference tasksReference;

    private TasksDatabaseAdapter() {

    }

    private TasksDatabaseAdapter(String adminID) {
        tasksReference = FirebaseDatabase.getInstance().getReference().child(DB_TASKS).child(adminID);
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
        tasksReference = FirebaseDatabase.getInstance().getReference().child(DB_TASKS).child(adminID);
    }

    public String insertTask(Task task) {
        String id = tasksReference.push().getKey();
        task.setId(id);
        tasksReference.child(id).setValue(task);
        return id;
    }

    public void setListener(final TaskAdapter adapter) {
        tasksReference.addChildEventListener(new ChildEventListener() {
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
        });
    }
    public void setListener(final EmpTasksAdapter adapter) {
        tasksReference.addChildEventListener(new ChildEventListener() {
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
        });
    }

    public void deleteTask(String taskId) {
        tasksReference.child(taskId).removeValue();
    }

}
