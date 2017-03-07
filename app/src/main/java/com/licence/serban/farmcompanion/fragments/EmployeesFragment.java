package com.licence.serban.farmcompanion.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.licence.serban.farmcompanion.classes.Employee;
import com.licence.serban.farmcompanion.classes.EmployeeAdapter;
import com.licence.serban.farmcompanion.classes.Utilities;
import com.licence.serban.farmcompanion.interfaces.OnAddEmployeeListener;
import com.licence.serban.farmcompanion.interfaces.OnAppTitleChange;
import com.licence.serban.farmcompanion.R;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class EmployeesFragment extends Fragment {

    private List<Employee> employees;
    private String userID;

    private OnAppTitleChange updateTitleCallback;
    private OnAddEmployeeListener addEmployeeListener;

    private EmployeeAdapter employeeAdapter;
    private DatabaseReference databaseReference;
    private DatabaseReference userReference;
    private DatabaseReference employeesReference;

    private ListView employeesListView;
    private Button addEmployeeButton;
    private Button secondAddEmployeeButton;
    private LinearLayout noEmployeeLayout;

    FragmentManager fragmentManager;


    public EmployeesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            updateTitleCallback = (OnAppTitleChange) context;
        } catch (ClassCastException ex) {
            throw new ClassCastException(context.toString()
                    + " must implement OnAppTitleChange");
        }
        try {
            addEmployeeListener = (OnAddEmployeeListener) context;
        } catch (ClassCastException ex) {
            throw new ClassCastException(context.toString()
                    + " must implement OnAddEmployeeListener");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        updateTitleCallback.updateTitle(getResources().getString(R.string.nav_employees));
        employees = new ArrayList<>();

        View view = inflater.inflate(R.layout.fragment_employees, container, false);

        userID = getArguments().getString(Utilities.Constants.USER_ID);

        fragmentManager = getChildFragmentManager();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        userReference = databaseReference.child(Utilities.Constants.DB_USERS).child(userID);
        employeesReference = databaseReference.child(Utilities.Constants.DB_EMPLOYEES);

        employeesListView = (ListView) view.findViewById(R.id.employeesListView);
        addEmployeeButton = (Button) view.findViewById(R.id.employeesAddEmployeeButton);
        secondAddEmployeeButton = (Button) view.findViewById(R.id.employeesAddEmployeeSecondButton);
        noEmployeeLayout = (LinearLayout) view.findViewById(R.id.employeesNoEmployeeMessage);


        employeeAdapter = new EmployeeAdapter(getActivity(), R.layout.employee_row, employees);
        employeesListView.setAdapter(employeeAdapter);

        employeesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Employee employee = employeeAdapter.getItem(position);
                if (employee != null) {
                    addEmployeeListener.openEditEmployeeUI(employee.getId());
                }
            }
        });

        addEmployeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEmployeeListener.openAddEmployeeUI();
            }
        });

        secondAddEmployeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEmployeeButton.callOnClick();
            }
        });

        userReference.child(Utilities.Constants.DB_EMPLOYEES).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                employeesReference.child(dataSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Employee employee = dataSnapshot.getValue(Employee.class);
                        employeeAdapter.add(employee);
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
                String empId = dataSnapshot.getKey();
                employeeAdapter.removeEmployee(empId);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        userReference.child(Utilities.Constants.DB_EMPLOYEES).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0) {
                    noEmployeeLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;

    }

}
