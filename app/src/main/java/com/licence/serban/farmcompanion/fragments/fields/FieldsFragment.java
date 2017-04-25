package com.licence.serban.farmcompanion.fragments.fields;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.licence.serban.farmcompanion.classes.models.Company;
import com.licence.serban.farmcompanion.classes.models.CompanyField;
import com.licence.serban.farmcompanion.classes.adapters.FieldsAdapter;
import com.licence.serban.farmcompanion.classes.Utilities;
import com.licence.serban.farmcompanion.interfaces.OnAddFieldListener;
import com.licence.serban.farmcompanion.interfaces.OnAppTitleChange;
import com.licence.serban.farmcompanion.R;
import com.licence.serban.farmcompanion.interfaces.OnFragmentStart;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FieldsFragment extends Fragment {

    private String userID;

    private FieldsAdapter fieldsAdapter;
    private OnAppTitleChange updateTitleCallback;
    private DatabaseReference databaseReference;
    private DatabaseReference fieldsReference;

    private LinearLayout noFieldLayout;
    private ListView fieldsListView;
    private Button addFieldButton;
    private LinearLayout messageLayout;
    private Button addFieldSecondButton;

    private OnAddFieldListener fieldListener;
    private OnFragmentStart startFragmentCallback;

    public FieldsFragment() {
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

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_fields, container, false);

        updateTitleCallback.updateTitle(getResources().getString(R.string.nav_fields));

        findViewsById(view);

        Bundle arguments = getArguments();
        if (arguments != null) {
            userID = arguments.getString(Utilities.Constants.USER_ID);
        }

        databaseReference = FirebaseDatabase.getInstance().getReference();
        fieldsReference = databaseReference.child(Utilities.Constants.DB_FIELDS).child(userID);

        fieldsAdapter = new FieldsAdapter(getActivity(), R.layout.field_row, new ArrayList<CompanyField>());
        fieldsListView.setAdapter(fieldsAdapter);

        fieldsReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                CompanyField field = dataSnapshot.getValue(CompanyField.class);
                if (field != null) {
                    fieldsAdapter.add(field);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                CompanyField field = dataSnapshot.getValue(CompanyField.class);
                if (field != null) {
                    fieldsAdapter.updateField(field);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String fieldID = dataSnapshot.getKey();
                fieldsAdapter.removeField(fieldID);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        setOnClickListeners();

        return view;

    }

    private void findViewsById(View view) {
        addFieldButton = (Button) view.findViewById(R.id.fieldsAddFieldButton);
        fieldsListView = (ListView) view.findViewById(R.id.fieldsListView);
        addFieldSecondButton = (Button) view.findViewById(R.id.fieldsAddFieldSecondButton);
        messageLayout = (LinearLayout) view.findViewById(R.id.fieldsNoFieldLayout);
    }

    private void setOnClickListeners() {

        addFieldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fieldListener.openAddFieldUI();
            }
        });

        addFieldSecondButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFieldButton.callOnClick();
            }
        });

        fieldsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String fieldID = fieldsAdapter.getItem(position).getId();
                fieldListener.openFieldDetailsFragment(fieldID);
            }
        });
    }
}
