package com.licence.serban.farmcompanion.employees.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.licence.serban.farmcompanion.R;
import com.licence.serban.farmcompanion.employees.models.EEmployeeState;
import com.licence.serban.farmcompanion.employees.models.Employee;
import com.licence.serban.farmcompanion.interfaces.OnAppTitleChange;
import com.licence.serban.farmcompanion.interfaces.OnDatePickerSelectedListener;
import com.licence.serban.farmcompanion.interfaces.OnDrawerMenuLock;
import com.licence.serban.farmcompanion.interfaces.OnElementAdded;
import com.licence.serban.farmcompanion.interfaces.OnFragmentStart;
import com.licence.serban.farmcompanion.misc.StringDateFormatter;
import com.licence.serban.farmcompanion.misc.Utilities;
import com.licence.serban.farmcompanion.misc.models.User;

import java.text.ParseException;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddEmployeeFragment extends Fragment implements OnDatePickerSelectedListener {

  long dateMils = -1;
  private DatabaseReference databaseReference;
  private DatabaseReference employeesReference;
  private DatabaseReference userReference;
  private FirebaseAuth firebaseAuth;
  private String userID;
  private boolean editMode = false;
  private OnAppTitleChange updateTitleCallback;
  private OnDrawerMenuLock lockDrawerMenuCallback;
  private OnElementAdded employeeAddedListener;

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
  private Button datePickerButton;
  private OnFragmentStart startFragmentCallback;
  private CheckBox employeeAccountCheckBox;
  private LinearLayout employeeAccountLayout;
  private ValueEventListener empListener;


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
      employeeAddedListener = (OnElementAdded) context;
    } catch (ClassCastException ex) {
      throw new ClassCastException(context.toString()
              + " must implement OnElementAdded");
    }
    try {
      startFragmentCallback = (OnFragmentStart) context;
    } catch (ClassCastException ex) {
      throw new ClassCastException(context.toString()
              + " must implement OnElementAdded");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    lockDrawerMenuCallback.unlockDrawerMenu();
    if (employeeID != null)
      employeesReference.child(employeeID).removeEventListener(empListener);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_add_employee, container, false);

    findViews(view);

    updateTitleCallback.updateTitle(getResources().getString(R.string.add_employee));
    lockDrawerMenuCallback.lockDrawer();

    userID = getArguments().getString(Utilities.Constants.USER_ID);
    employeeID = getArguments().getString(Utilities.Constants.EMPLOYEE_ID);

    databaseReference = FirebaseDatabase.getInstance().getReference();
    employeesReference = databaseReference.child(Utilities.Constants.DB_EMPLOYEES).child(userID);

    if (employeeID == null) {
      addEmployeeBehaviour();
    } else {
      editEmployeeBehaviour(employeeID);
    }

    datePickerButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String date = dateEditText.getText().toString().trim();
        long time = -1;
        if (!date.isEmpty()) {
          try {
            Date tempDate = StringDateFormatter.stringToDate(date);
            time = tempDate.getTime();
          } catch (ParseException e) {
            e.printStackTrace();
          }
        }
        employeeAddedListener.openDatePicker(time);
      }
    });

    employeeAccountCheckBox.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (employeeAccountCheckBox.isChecked()) {
          employeeAccountLayout.setVisibility(View.VISIBLE);
        } else {
          employeeAccountLayout.setVisibility(View.GONE);
        }
      }
    });

    return view;
  }

  private void changeEnabledState(boolean state) {
    nameEditText.setEnabled(state);
    persNumberEditText.setEnabled(state);
    idNumberEditText.setEnabled(state);
    positionEditText.setEnabled(state);
    salaryEditText.setEnabled(state);
    contractEditText.setEnabled(state);
    datePickerButton.setEnabled(state);
  }

  private void editEmployeeBehaviour(final String employeeID) {

    empListener = new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        Employee currentEmployee = dataSnapshot.getValue(Employee.class);
        if (currentEmployee != null) {
          nameEditText.setText(currentEmployee.getName());
          persNumberEditText.setText(currentEmployee.getPersonalIdNumber());
          idNumberEditText.setText(currentEmployee.getIdNumber());
          dateEditText.setText(StringDateFormatter.dateToString(new Date(currentEmployee.getStartDate())));
          dateMils = currentEmployee.getStartDate();
          positionEditText.setText(currentEmployee.getPosition());
          salaryEditText.setText(String.valueOf(currentEmployee.getBaseSalary()));
          contractEditText.setText(currentEmployee.getContractNumber());
          emailEditText.setText(currentEmployee.getAccountEmail());
        }
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    };
    employeesReference.child(employeeID).addValueEventListener(empListener);


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
            try {
              employmentDate = StringDateFormatter.stringToDate(employmentDateString);
            } catch (ParseException e) {
              e.printStackTrace();
            }
          }

          final Employee employee = new Employee();
          employee.setName(name);
          employee.setAccountEmail(email);
          employee.setBaseSalary(salary);
          employee.setStartDate(employmentDate.getTime());
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

  private void addEmployeeBehaviour() {
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
        if (employeeAccountCheckBox.isChecked()) {
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
        }
        double salary = 0;
        if (!salaryString.isEmpty()) {
          try {
            salary = Double.parseDouble(salaryString);
          } catch (Exception ex) {
            ex.printStackTrace();
          }
        }
        Date employmentDate = null;
        if (!employmentDateString.isEmpty()) {
          try {
            employmentDate = StringDateFormatter.stringToDate(employmentDateString);
          } catch (ParseException e) {
            e.printStackTrace();
          }
        }

        final Employee employee = new Employee();
        employee.setName(name);
        employee.setAccountEmail(email);
        employee.setBaseSalary(salary);
        if (employmentDate != null) {
          employee.setStartDate(employmentDate.getTime());
        }
        employee.setContractNumber(contractNumber);
        employee.setPersonalIdNumber(personalNumber);
        employee.setIdNumber(idNumber);
        employee.setPosition(position);
        employee.setCreatedAt(System.currentTimeMillis());
        employee.setState(EEmployeeState.AVAILABLE);

        final FirebaseUser user = firebaseAuth.getCurrentUser();


        if (employeeAccountCheckBox.isChecked()) {
          final User employeeUser = new User();
          employeeUser.setAdmin(false);
          employeeUser.setEmail(email);
          employeeUser.setName(name);
          employeeUser.setEmployerID(userID);
          employeeUser.setCreatedAt(System.currentTimeMillis());

          firebaseAuth.createUserWithEmailAndPassword(email, password)
                  .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                      String id = authResult.getUser().getUid();
                      employeeUser.setId(id);
                      employee.setId(id);
                      employeesReference.child(id).setValue(employee);
                      databaseReference.child(Utilities.Constants.DB_USERS).child(id).setValue(employeeUser);
                      Toast.makeText(AddEmployeeFragment.this.getActivity(), "Success", Toast.LENGTH_SHORT).show();
                      firebaseAuth.signOut();
                      signInCurrentUser(id);
                    }
                  })
                  .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                      Log.e("Error", e.getMessage());
                      Toast.makeText(AddEmployeeFragment.this.getActivity(), "Failure", Toast.LENGTH_SHORT).show();
                    }
                  });
        } else {
          final String id = employeesReference.push().getKey();
          employee.setId(id);
          employeesReference.child(id).setValue(employee).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
              goToDetails(id);
            }
          });
        }


      }
    });
  }

  private void signInCurrentUser(final String id) {
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
    String email = sharedPreferences.getString(Utilities.Constants.EMAIL, "");
    String pass = sharedPreferences.getString(Utilities.Constants.PASSWORD, "");
    if (!email.isEmpty() && !pass.isEmpty()) {
      firebaseAuth.signInWithEmailAndPassword(email, pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
        @Override
        public void onSuccess(AuthResult authResult) {
          goToDetails(id);
        }
      });
    }
  }

  private void goToDetails(String id) {
    Fragment fragment = new AddEmployeeFragment();
    Bundle args = new Bundle();
    args.putString(Utilities.Constants.USER_ID, userID);
    args.putString(Utilities.Constants.EMPLOYEE_ID, id);
    fragment.setArguments(args);
    startFragmentCallback.popBackStack();
    startFragmentCallback.startFragment(fragment, true);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    lockDrawerMenuCallback.unlockDrawerMenu();
  }

  public void setDate(String date) {
    dateEditText.setText(date);
  }

  private void findViews(View view) {
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
    datePickerButton = (Button) view.findViewById(R.id.addEmployeeDatePickButton);
    addEmployeeButton = (Button) view.findViewById(R.id.addEmployeeAddButton);
    employeeAccountCheckBox = (CheckBox) view.findViewById(R.id.addEmployeeEmployeeAccountCheckbox);
    employeeAccountLayout = (LinearLayout) view.findViewById(R.id.addEmployeeEmployeeAccountLayout);
  }
}

