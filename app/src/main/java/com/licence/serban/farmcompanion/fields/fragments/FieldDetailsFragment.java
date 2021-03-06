package com.licence.serban.farmcompanion.fields.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.licence.serban.farmcompanion.R;
import com.licence.serban.farmcompanion.fields.models.CompanyField;
import com.licence.serban.farmcompanion.interfaces.OnAddFieldListener;
import com.licence.serban.farmcompanion.interfaces.OnAppTitleChange;
import com.licence.serban.farmcompanion.interfaces.OnDrawerMenuLock;
import com.licence.serban.farmcompanion.interfaces.OnElementAdded;
import com.licence.serban.farmcompanion.misc.Utilities;
import com.licence.serban.farmcompanion.tasks.adapters.TaskAdapter;
import com.licence.serban.farmcompanion.tasks.adapters.TasksDatabaseAdapter;
import com.licence.serban.farmcompanion.tasks.models.Task;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FieldDetailsFragment extends Fragment {

  private String fieldID = null;
  private String userID = null;

  private OnAppTitleChange updateTitleCallback;
  private OnAddFieldListener fieldListener;

  private TextView fieldNameTextView;
  private TextView fieldAreaTextView;
  private TextView fieldLocationTextView;
  private TextView fieldCropTextView;
  private TextView fieldOwnershipTextView;
  private TextView fieldNotesTextView;
  private DatabaseReference databaseReference;
  private ImageButton editFieldButton;
  private Button deleteFieldButton;

  private OnElementAdded fieldAddedListener;
  private ListView fieldActivitiesListView;
  private ValueEventListener fieldDbListener;
  private OnDrawerMenuLock drawerLockListener;


  public FieldDetailsFragment() {
    // Required empty public constructor
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    try {
      updateTitleCallback = (OnAppTitleChange) context;
    } catch (ClassCastException ex) {
      throw new ClassCastException(context.toString()
              + " must implement OnHeadlineSelectedListener");
    }
    try {
      fieldListener = (OnAddFieldListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString() +
              " must implement OnAddFieldListener");
    }
    try {
      fieldAddedListener = (OnElementAdded) context;
    } catch (ClassCastException ex) {
      throw new ClassCastException(context.toString()
              + " must implement OnElementAdded");
    }
    try {
      drawerLockListener = (OnDrawerMenuLock) context;
    } catch (ClassCastException ex) {
      throw new ClassCastException(context.toString()
              + " must implement OnElementAdded");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    drawerLockListener.unlockDrawerMenu();
    databaseReference.child(Utilities.Constants.DB_FIELDS).child(userID).child(fieldID).removeEventListener(fieldDbListener);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {


    View view = inflater.inflate(R.layout.fragment_field_details, container, false);

    findViewsById(view);
    drawerLockListener.lockDrawer();
    Bundle arguments = getArguments();
    if (arguments != null) {
      userID = arguments.getString(Utilities.Constants.USER_ID);
      fieldID = arguments.getString(Utilities.Constants.FIELD_ID);
    }

    final TaskAdapter adapter = new TaskAdapter(this.getActivity(), R.layout.task_row, new ArrayList<Task>());
    fieldActivitiesListView.setAdapter(adapter);

    databaseReference = FirebaseDatabase.getInstance().getReference();
    fieldDbListener = new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        CompanyField companyField = dataSnapshot.getValue(CompanyField.class);
        if (companyField != null) {
          fieldNameTextView.setText(companyField.getName());
          fieldAreaTextView.setText(String.valueOf(companyField.getArea()) + " ha");
          fieldCropTextView.setText(companyField.getCropStatus());
          fieldOwnershipTextView.setText(companyField.getOwnership());
          fieldLocationTextView.setText(companyField.getLocation());
          fieldNotesTextView.setText(companyField.getNotes());
        }
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    };
    databaseReference.child(Utilities.Constants.DB_FIELDS).child(userID).child(fieldID).addValueEventListener(fieldDbListener);

    databaseReference.child(Utilities.Constants.DB_FIELDS).child(userID).child(fieldID).child(TasksDatabaseAdapter.DB_TASKS).addChildEventListener(new ChildEventListener() {
      @Override
      public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        String key = dataSnapshot.getKey();
        databaseReference.child(TasksDatabaseAdapter.DB_TASKS).child(userID).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
            Task task = dataSnapshot.getValue(Task.class);
            if (task != null) {
              adapter.add(task);
            }
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

      }

      @Override
      public void onChildMoved(DataSnapshot dataSnapshot, String s) {

      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    });

    editFieldButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        fieldListener.openEditFieldUI(fieldID);
      }
    });

    deleteFieldButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        DatabaseReference mainReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference fieldReference = mainReference.child(Utilities.Constants.DB_FIELDS).child(userID);
        fieldReference.child(fieldID).removeValue();
        fieldAddedListener.endFragment();
      }
    });

    return view;
  }

  private void findViewsById(View view) {
    fieldActivitiesListView = (ListView) view.findViewById(R.id.fieldDetailsActivitiesListView);

    View detailsView = LayoutInflater.from(FieldDetailsFragment.this.getActivity()).inflate(R.layout.field_list_header, fieldActivitiesListView, false);
    fieldActivitiesListView.addHeaderView(detailsView);

    fieldNameTextView = (TextView) detailsView.findViewById(R.id.fieldDetailsNameTextView);
    fieldAreaTextView = (TextView) detailsView.findViewById(R.id.fieldDetailsAreaTextView);
    fieldLocationTextView = (TextView) detailsView.findViewById(R.id.fieldDetailsLocationTextView);
    fieldCropTextView = (TextView) detailsView.findViewById(R.id.fieldDetailsCropTextView);
    fieldOwnershipTextView = (TextView) detailsView.findViewById(R.id.fieldDetailsOwnershipTextView);
    fieldNotesTextView = (TextView) detailsView.findViewById(R.id.fieldDetailsNotesTextView);
    editFieldButton = (ImageButton) detailsView.findViewById(R.id.fieldDetailsEditButton);
    deleteFieldButton = (Button) detailsView.findViewById(R.id.fieldDetailsDeleteButton);
  }

}
