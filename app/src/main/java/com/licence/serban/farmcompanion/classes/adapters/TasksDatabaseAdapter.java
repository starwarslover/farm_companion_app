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
    private static DatabaseReference userTasksReference;
    private DatabaseReference mainReference;
    private DatabaseReference tasksReference;

    private TasksDatabaseAdapter() {

    }

    private TasksDatabaseAdapter(String adminID) {
        tasksReference = FirebaseDatabase.getInstance().getReference().child(DB_TASKS);
        setUserReference(adminID);
    }

    public static synchronized TasksDatabaseAdapter getInstance(String adminID) {
        if (instance == null) {
            instance = new TasksDatabaseAdapter(adminID);
        } else {
            setUserReference(adminID);
        }
        return instance;
    }

    private static void setUserReference(String adminID) {
        userTasksReference = FirebaseDatabase.getInstance().getReference().child(Utilities.Constants.DB_USERS).child(adminID).child(DB_TASKS);
    }

    public String insertTask(Task task) {
        String id = tasksReference.push().getKey();
        task.setId(id);
        tasksReference.child(id).setValue(task);
        userTasksReference.child(id).setValue(true);

        return id;
    }

    public void setListener(final TaskAdapter adapter) {
        userTasksReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String id = dataSnapshot.getKey();
                tasksReference.child(id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Task task = dataSnapshot.getValue(Task.class);
                        adapter.add(task);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String id = dataSnapshot.getKey();
                adapter.removeTask(id);
                adapter.notifyDataSetChanged();

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
        userTasksReference.child(taskId).removeValue();
        tasksReference.child(taskId).removeValue();
    }

}
