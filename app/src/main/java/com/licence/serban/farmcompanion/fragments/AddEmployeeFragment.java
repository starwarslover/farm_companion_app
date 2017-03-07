package com.licence.serban.farmcompanion.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.licence.serban.farmcompanion.R;
import com.licence.serban.farmcompanion.classes.Employee;
import com.licence.serban.farmcompanion.classes.User;
import com.licence.serban.farmcompanion.classes.Utilities;
import com.licence.serban.farmcompanion.interfaces.OnAppTitleChange;
import com.licence.serban.farmcompanion.interfaces.OnDrawerMenuLock;
import com.licence.serban.farmcompanion.interfaces.OnEmployeeAdded;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddEmployeeFragment extends Fragment {

    private DatabaseReference databaseReference;
    private DatabaseReference employeesReference;
    private DatabaseReference userReference;
    private FirebaseAuth firebaseAuth;

    private String userID;
    private boolean editMode = false;


    private OnAppTitleChange updateTitleCallback;
    private OnDrawerMenuLock lockDrawerMenuCallback;
    private OnEmployeeAdded employeeAddedListener;

    private TextInputEditText nameEditText;
    private TextInputEditText persNumberEditText;
    private TextInputEditText idNumberEditText;
    private TextInputEditText dateEditText;
    private TextInputEditText positionEditText;
    private TextInputEditText salaryEditText;
    private TextInputEditText contractEditText;
    private TextInputEditText emailEditText;
    private TextInputEditText confirmEmailEditText;
    private TextInputEditText passwordEditText;
    private TextInputEditText confirmPasswordEditText;
    private Button addEmployeeButton;
    private String employeeID;


    public AddEmployeeFragment() {
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
            lockDrawerMenuCallback = (OnDrawerMenuLock) context;
        } catch (ClassCastException ex) {
            throw new ClassCastException(context.toString()
                    + " must implement OnDrawerMenuLock");
        }
        try {
            employeeAddedListener = (OnEmployeeAdded) context;
        } catch (ClassCastException ex) {
            throw new ClassCastException(context.toString()
                    + " must implement OnEmployeeAdded");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_employee, container, false);

        nameEditText = (TextInputEditText) view.findViewById(R.id.addEmployeeNameEditText);
        persNumberEditText = (TextInputEditText) view.findViewById(R.id.addEmployeePersonalIdEditText);
        idNumberEditText = (TextInputEditText) view.findViewById(R.id.addEmployeeIdEditText);
        dateEditText = (TextInputEditText) view.findViewById(R.id.addEmployeeEmploymentDateEditText);
        positionEditText = (TextInputEditText) view.findViewById(R.id.addEmployeePositionEditText);
        salaryEditText = (TextInputEditText) view.findViewById(R.id.addEmployeeSalaryEditText);
        contractEditText = (TextInputEditText) view.findViewById(R.id.addEmployeeContractEditText);
        emailEditText = (TextInputEditText) view.findViewById(R.id.addEmployeeEmailEditText);
        confirmEmailEditText = (TextInputEditText) view.findViewById(R.id.addEmployeeConfirmEmailEditText);
        passwordEditText = (TextInputEditText) view.findViewById(R.id.addEmployeePasswordEditText);
        confirmPasswordEditText = (TextInputEditText) view.findViewById(R.id.addEmployeeConfirmPassEditText);
        addEmployeeButton = (Button) view.findViewById(R.id.addEmployeeAddButton);

        updateTitleCallback.updateTitle(getResources().getString(R.string.add_employee));
        lockDrawerMenuCallback.lockDrawer();

        userID = getArguments().getString(Utilities.Constants.USER_ID);
        employeeID = getArguments().getString(Utilities.Constants.EMPLOYEE_ID);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        userReference = databaseReference.child(Utilities.Constants.DB_USERS).child(userID);
        employeesReference = databaseReference.child(Utilities.Constants.DB_EMPLOYEES);

        if (employeeID == null) {
            firebaseAuth = FirebaseAuth.getInstance();
            addEmployeeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = nameEditText.getText().toString().trim();
                    String personalNumber = persNumberEditText.getText().toString().trim();
                    String idNumber = idNumberEditText.getText().toString().trim();
                    String employmentDateString = dateEditText.getText().toString().trim();
                    String position = positionEditText.getText().toString().trim();
                    String salaryString = salaryEditText.getText().toString().trim();
                    String contractNumber = contractEditText.getText().toString().trim();
                    String email = emailEditText.getText().toString().trim();
                    String confirmEmail = confirmEmailEditText.getText().toString().trim();
                    String password = passwordEditText.getText().toString().trim();
                    String confirmPassword = confirmPasswordEditText.getText().toString().trim();

                    if (name.isEmpty()) {
                        nameEditText.setError(getResources().getString(R.string.no_name_error));
                        return;
                    }
                    if (email.isEmpty()) {
                        emailEditText.setError(getResources().getString(R.string.no_email_error));
                        return;
                    }
                    if (confirmEmail.isEmpty()) {
                        emailEditText.setError(getResources().getString(R.string.no_email_error));
                        return;
                    }
                    if (password.isEmpty()) {
                        passwordEditText.setError(getResources().getString(R.string.no_pass_error));
                        return;
                    }
                    if (confirmPassword.isEmpty()) {
                        confirmPasswordEditText.setError(getResources().getString(R.string.no_pass_error));
                        return;
                    }
                    double salary = 0;
                    if (!salaryString.isEmpty()) {
                        salary = Double.parseDouble(salaryString);
                    }
                    Date employmentDate = null;
                    if (!employmentDateString.isEmpty()) {
                        SimpleDateFormat dateFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
                        try {
                            employmentDate = dateFormat.parse(employmentDateString);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    final User employeeUser = new User();
                    employeeUser.setAdmin(false);
                    employeeUser.setEmail(email);
                    employeeUser.setName(name);

                    final Employee employee = new Employee();
                    employee.setName(name);
                    employee.setAccountEmail(email);
                    employee.setBaseSalary(salary);
                    employee.setStartDate(employmentDate);
                    employee.setContractNumber(contractNumber);
                    employee.setPersonalIdNumber(personalNumber);
                    employee.setIdNumber(idNumber);
                    employee.setPosition(position);

                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    String id = authResult.getUser().getUid();
                                    employeeUser.setId(id);
                                    employee.setId(id);
                                    employeesReference.child(id).setValue(employee);
                                    userReference.child(Utilities.Constants.DB_EMPLOYEES).child(id).setValue(true);
                                    databaseReference.child(Utilities.Constants.DB_USERS).child(id).setValue(employeeUser);
                                    Toast.makeText(AddEmployeeFragment.this.getActivity(), "Success", Toast.LENGTH_SHORT).show();
                                    employeeAddedListener.endFragment();

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AddEmployeeFragment.this.getActivity(), "Failure", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });
        } else {
            employeesReference.child(employeeID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Employee currentEmployee = dataSnapshot.getValue(Employee.class);
                    nameEditText.setText(currentEmployee.getName());
                    persNumberEditText.setText(currentEmployee.getPersonalIdNumber());
                    idNumberEditText.setText(currentEmployee.getIdNumber());
//                    dateEditText.setText(currentEmployee.getStartDate().toString());
                    positionEditText.setText(currentEmployee.getPosition());
                    salaryEditText.setText(String.valueOf(currentEmployee.getBaseSalary()));
                    contractEditText.setText(currentEmployee.getContractNumber());
                    emailEditText.setText(currentEmployee.getAccountEmail());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            emailEditText.setEnabled(false);
            confirmEmailEditText.setVisibility(View.GONE);
            passwordEditText.setVisibility(View.GONE);
            confirmPasswordEditText.setVisibility(View.GONE);

            changeEnabledState(editMode);
            addEmployeeButton.setText(getResources().getString(R.string.edit_button));

            addEmployeeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (editMode) {
                        String name = nameEditText.getText().toString().trim();
                        String personalNumber = persNumberEditText.getText().toString().trim();
                        String idNumber = idNumberEditText.getText().toString().trim();
                        String employmentDateString = dateEditText.getText().toString().trim();
                        String position = positionEditText.getText().toString().trim();
                        String salaryString = salaryEditText.getText().toString().trim();
                        String contractNumber = contractEditText.getText().toString().trim();
                        String email = emailEditText.getText().toString().trim();
                        String confirmEmail = confirmEmailEditText.getText().toString().trim();
                        String password = passwordEditText.getText().toString().trim();
                        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

                        if (name.isEmpty()) {
                            nameEditText.setError(getResources().getString(R.string.no_name_error));
                            return;
                        }

                        double salary = 0;
                        if (!salaryString.isEmpty()) {
                            salary = Double.parseDouble(salaryString);
                        }
                        Date employmentDate = null;
                        if (!employmentDateString.isEmpty()) {
                            SimpleDateFormat dateFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
                            try {
                                employmentDate = dateFormat.parse(employmentDateString);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }

                        final Employee employee = new Employee();
                        employee.setName(name);
                        employee.setAccountEmail(email);
                        employee.setBaseSalary(salary);
                        employee.setStartDate(employmentDate);
                        employee.setContractNumber(contractNumber);
                        employee.setPersonalIdNumber(personalNumber);
                        employee.setIdNumber(idNumber);
                        employee.setPosition(position);
                        employee.setId(employeeID);

                        employeesReference.child(employeeID).setValue(employee);
                        editMode = false;
                        changeEnabledState(editMode);
                        addEmployeeButton.setText(getResources().getString(R.string.edit_button));
                    } else {
                        editMode = true;
                        changeEnabledState(editMode);
                        addEmployeeButton.setText(getResources().getString(R.string.save_changes));
                    }
                }
            });

        }


        return view;
    }

    void changeEnabledState(boolean state) {
        nameEditText.setEnabled(state);
        persNumberEditText.setEnabled(state);
        idNumberEditText.setEnabled(state);
        dateEditText.setEnabled(state);
        positionEditText.setEnabled(state);
        salaryEditText.setEnabled(state);
        contractEditText.setEnabled(state);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        lockDrawerMenuCallback.unlockDrawerMenu();
    }
}
