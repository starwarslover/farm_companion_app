package com.licence.serban.farmcompanion.employees.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import com.licence.serban.farmcompanion.employees.models.Employee;
import com.licence.serban.farmcompanion.employees.adapters.EmployeeAdapter;
import com.licence.serban.farmcompanion.misc.Utilities;
import com.licence.serban.farmcompanion.interfaces.OnAddEmployeeListener;
import com.licence.serban.farmcompanion.interfaces.OnAppTitleChange;
import com.licence.serban.farmcompanion.R;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class EmployeesFragment extends Fragment {

    FragmentManager fragmentManager;
    private List<Employee> employees;
    private String userID;
    private OnAppTitleChange updateTitleCallback;
    private OnAddEmployeeListener addEmployeeListener;
    private EmployeeAdapter employeeAdapter;
    private DatabaseReference databaseReference;
    private DatabaseReference employeesReference;
    private ListView employeesListView;
    private Button addEmployeeButton;
    private Button secondAddEmployeeButton;
    private LinearLayout noEmployeeLayout;


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

        Bundle args = getArguments();
        if(args != null) {
            userID = getArguments().getString(Utilities.Constants.USER_ID);
        }

        databaseReference = FirebaseDatabase.getInstance().getReference();
        employeesReference = databaseReference.child(Utilities.Constants.DB_EMPLOYEES).child(userID);

        setUpViews(view);


        employeesReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Employee employee = dataSnapshot.getValue(Employee.class);
                if (employee != null) {
                    employeeAdapter.add(employee);
                }
                checkAdapterCount();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Employee employee = dataSnapshot.getValue(Employee.class);
                employeeAdapter.updateEmployee(employee);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String empId = dataSnapshot.getKey();
                employeeAdapter.removeEmployee(empId);
                checkAdapterCount();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;

    }

    private void setUpViews(View view) {
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

    }

    private void checkAdapterCount() {
        if (employeeAdapter.getCount() == 0) {
            noEmployeeLayout.setVisibility(View.VISIBLE);
        }
    }

}
