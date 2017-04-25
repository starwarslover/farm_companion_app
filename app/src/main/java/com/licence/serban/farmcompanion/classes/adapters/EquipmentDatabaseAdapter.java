package com.licence.serban.farmcompanion.classes.adapters;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.licence.serban.farmcompanion.classes.Equipment;

/**
 * Created by Serban on 25.04.2017.
 */

public class EquipmentDatabaseAdapter {
    public static final String DB_EQUIPMENTS = "equipment";

    private static EquipmentDatabaseAdapter instance;

    private static DatabaseReference equipmentReference;

    private EquipmentDatabaseAdapter() {

    }

    private EquipmentDatabaseAdapter(String adminId) {
        equipmentReference = FirebaseDatabase.getInstance().getReference().child(DB_EQUIPMENTS).child(adminId);
    }

    public static synchronized EquipmentDatabaseAdapter getInstance(String adminId) {
        if (instance == null) {
            instance = new EquipmentDatabaseAdapter(adminId);
        } else {
            updateReference(adminId);
        }
        return instance;
    }

    private static void updateReference(String adminId) {
        equipmentReference = FirebaseDatabase.getInstance().getReference().child(DB_EQUIPMENTS).child(adminId);
    }

    public String insertEquipment(Equipment equipment) {
        String id = equipmentReference.push().getKey();
        equipment.setId(id);
        equipmentReference.child(id).setValue(equipment);
        return id;
    }

    public void setListener(final EquipmentListAdapter adapter) {
        equipmentReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Equipment equipment = dataSnapshot.getValue(Equipment.class);
                if (equipment != null) {
                    adapter.add(equipment);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Equipment equipment = dataSnapshot.getValue(Equipment.class);
                if (equipment != null) {
                    adapter.updateEquipment(equipment);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String id = dataSnapshot.getKey();
                adapter.removeEquipment(id);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void deleteTask(String equipmentId) {
        equipmentReference.child(equipmentId).removeValue();
    }
}
