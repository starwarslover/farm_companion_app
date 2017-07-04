package com.licence.serban.farmcompanion.consumables.adapters;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.licence.serban.farmcompanion.consumables.models.Consumable;
import com.licence.serban.farmcompanion.misc.ConsumableEnum;

/**
 * Created by Serban on 04.07.2017.
 */

public class ConsumableDatabaseAdapter {
  public static final String DB_CONSUMABLES = "consumables";

  private static String userID;
  private static ConsumableDatabaseAdapter instance;
  private static DatabaseReference consumablesReference;
  private static DatabaseReference mainReference;

  private ConsumableDatabaseAdapter() {

  }

  private ConsumableDatabaseAdapter(String adminID) {
    userID = adminID;
    mainReference = FirebaseDatabase.getInstance().getReference();
    consumablesReference = mainReference.child(DB_CONSUMABLES).child(adminID);
  }

  public static synchronized ConsumableDatabaseAdapter getInstance(String adminID) {
    if (instance == null) {
      instance = new ConsumableDatabaseAdapter(adminID);
    } else {
      setTasksReference(adminID);
    }
    return instance;
  }

  private static void setTasksReference(String adminID) {
    userID = adminID;
    consumablesReference = FirebaseDatabase.getInstance().getReference().child(DB_CONSUMABLES).child(adminID);
  }

  public String insertConsumable(Consumable consumable) {
    String id = consumablesReference.push().getKey();
    consumable.setId(id);
    consumable.setCreatedAt(System.currentTimeMillis());
    consumablesReference.child(id).setValue(consumable);

    return id;
  }

  public void setListener(final ConsumableListAdapter adapter, ConsumableEnum consumableType) {
    Query consumableQuery = consumablesReference.orderByChild("type").equalTo(consumableType.toString());
    consumableQuery.addChildEventListener(new ChildEventListener() {
      @Override
      public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Consumable cons = dataSnapshot.getValue(Consumable.class);
        if (cons != null)
          adapter.add(cons);
      }

      @Override
      public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        Consumable consumable = dataSnapshot.getValue(Consumable.class);
        if (consumable != null)
          adapter.updateConsumable(consumable);
      }

      @Override
      public void onChildRemoved(DataSnapshot dataSnapshot) {
        Consumable consumable = dataSnapshot.getValue(Consumable.class);
        if (consumable != null)
          adapter.removeConsumable(consumable.getId());
      }

      @Override
      public void onChildMoved(DataSnapshot dataSnapshot, String s) {

      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    });
  }

  public void deleteConsumable(String consId) {
    consumablesReference.child(consId).removeValue();
  }

}
