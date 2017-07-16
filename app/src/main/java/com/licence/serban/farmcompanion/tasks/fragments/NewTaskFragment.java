package com.licence.serban.farmcompanion.tasks.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.licence.serban.farmcompanion.R;
import com.licence.serban.farmcompanion.activities.MainActivity;
import com.licence.serban.farmcompanion.consumables.adapters.ConsumableDatabaseAdapter;
import com.licence.serban.farmcompanion.consumables.models.Consumable;
import com.licence.serban.farmcompanion.emp_account.fragments.EmpTaskTrackingFragment;
import com.licence.serban.farmcompanion.employees.models.EEmployeeState;
import com.licence.serban.farmcompanion.employees.models.Employee;
import com.licence.serban.farmcompanion.equipment.adapters.EquipmentDatabaseAdapter;
import com.licence.serban.farmcompanion.equipment.models.Equipment;
import com.licence.serban.farmcompanion.equipment.models.EquipmentState;
import com.licence.serban.farmcompanion.fields.models.CompanyField;
import com.licence.serban.farmcompanion.interfaces.OnAppTitleChange;
import com.licence.serban.farmcompanion.interfaces.OnDrawerMenuLock;
import com.licence.serban.farmcompanion.interfaces.OnFragmentStart;
import com.licence.serban.farmcompanion.interfaces.OnTaskCreatedListener;
import com.licence.serban.farmcompanion.misc.Utilities;
import com.licence.serban.farmcompanion.misc.WorkState;
import com.licence.serban.farmcompanion.tasks.adapters.TasksDatabaseAdapter;
import com.licence.serban.farmcompanion.tasks.models.ResourcePlaceholder;
import com.licence.serban.farmcompanion.tasks.models.Task;

import java.util.ArrayList;
import java.util.List;

import static com.licence.serban.farmcompanion.emp_account.fragments.EmpTaskDetailsFragment.TASK_TRACKING_TAG;
import static com.licence.serban.farmcompanion.misc.Utilities.Constants.TASK_ID_EXTRA;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewTaskFragment extends Fragment {


  public static final String FROM_CREATE = "from_create";
  public OnTaskCreatedListener taskCreatedCallback;
  private OnAppTitleChange updateTitleCallback;
  private Button newTaskButton;
  private String empID;
  private String employerId;
  private Spinner typeSpinner;
  private Spinner fieldsSpinner;
  private LinearLayout equipmentLayout;
  private Button expandEquipmentLayoutButton;
  private List<CheckBox> equipmentCheckBoxes;
  private List<Equipment> equipments;
  private List<CompanyField> fields;
  private DatabaseReference equipmentReference;
  private DatabaseReference fieldsReference;
  private ArrayAdapter<String> fieldsSpinnerAdapter;
  private DatabaseReference employeeReference;
  private Employee currentEmployee;
  private LinearLayout empsLayout;
  private Button empExpandButton;
  private LinearLayout consLayout;
  private Button consExpandButton;
  private DatabaseReference consumablesReference;
  private DatabaseReference employeesReference;
  boolean isAdmin;
  private ArrayList<CheckBox> consumableCheckBoxes;
  private ArrayList<CheckBox> employeeCheckBoxes;
  private OnFragmentStart onFragmentStartCallback;
  private OnDrawerMenuLock drawerMenuLock;
  private LinearLayout newTaskEmpsLayout;
  private LayoutInflater inflater;
  private EditText taskTitleEditText;

  public NewTaskFragment() {
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
      taskCreatedCallback = (OnTaskCreatedListener) context;
    } catch (ClassCastException ex) {
      throw new ClassCastException(context.toString()
              + " must implement OnTaskCreatedListener");
    }
    try {
      onFragmentStartCallback = (OnFragmentStart) context;
    } catch (ClassCastException ex) {
      throw new ClassCastException(context.toString()
              + " must implement OnFragmentStart");
    }
    try {
      drawerMenuLock = (OnDrawerMenuLock) context;
    } catch (ClassCastException ex) {
      throw new ClassCastException(context.toString()
              + " must implement OnDrawerMenuLock");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    drawerMenuLock.unlockDrawerMenu();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_new_task, container, false);

    this.inflater = inflater;
    drawerMenuLock.lockDrawer();
    updateTitleCallback.updateTitle(getResources().getString(R.string.new_activity));

    equipmentCheckBoxes = new ArrayList<>();
    consumableCheckBoxes = new ArrayList<>();
    employeeCheckBoxes = new ArrayList<>();
    equipments = new ArrayList<>();
    fields = new ArrayList<>();

    isAdmin = ((MainActivity) NewTaskFragment.this.getActivity()).isUserAdmin();
    Bundle args = getArguments();
    if (args != null) {
      empID = args.getString(Utilities.Constants.USER_ID);
      employerId = args.getString(Utilities.Constants.DB_EMPLOYER_ID);
    }

    setUpViews(view);
    setDbReferences();
    fillViews();

    return view;
  }

  private void setDbReferences() {
    equipmentReference = FirebaseDatabase.getInstance().getReference().child(EquipmentDatabaseAdapter.DB_EQUIPMENTS).child(employerId);
    fieldsReference = FirebaseDatabase.getInstance().getReference().child(Utilities.Constants.DB_FIELDS).child(employerId);
    employeesReference = FirebaseDatabase.getInstance().getReference().child(Utilities.Constants.DB_EMPLOYEES);
    employeesReference = employeesReference.child(employerId);
    if (empID != null)
      employeeReference = employeesReference.child(empID);
    consumablesReference = FirebaseDatabase.getInstance().getReference().child(ConsumableDatabaseAdapter.DB_CONSUMABLES).child(employerId);
  }

  private void fillViews() {
    fillEquipments();
    fillEmployees();
    fillConsumables();
    fillFields();

  }

  private void fillFields() {
    fieldsReference.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        for (DataSnapshot child : dataSnapshot.getChildren()) {
          CompanyField field = child.getValue(CompanyField.class);
          fields.add(field);
          fieldsSpinnerAdapter.add(field.getName());
        }
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    });

  }

  private void fillConsumables() {
    consumablesReference.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        for (DataSnapshot child : dataSnapshot.getChildren()) {
          Consumable cons = child.getValue(Consumable.class);
          if (cons != null) {
            consumableCheckBoxes.add(getConsCheckBox(cons));
          }
        }
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
      }
    });
  }

  private CheckBox createEmptyCheckBox() {
    CheckBox checkBox = null;
    if (NewTaskFragment.this.getActivity() != null) {
      checkBox = new CheckBox(this.getActivity());
      LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
      checkBox.setLayoutParams(params);
      checkBox.setPadding(10, 5, 10, 5);
    }
    return checkBox;
  }

  private CheckBox getConsCheckBox(Consumable cons) {
    LinearLayout resourceItem = (LinearLayout) inflater.inflate(R.layout.resource_item, consLayout, false);
    ResourceHolder holder = new ResourceHolder(resourceItem);
    String text = cons.getName() + " - " + cons.getType().toString();
    holder.mainCheckBox.setText(text);
    holder.mainCheckBox.setTag(cons);
    String amount = "Disponibil: " + String.valueOf(cons.getAmount()) + " " + cons.getUM();
    holder.secondaryTextView.setText(amount);
    consLayout.addView(resourceItem);
    return holder.mainCheckBox;
  }

  private void fillEmployees() {
    if (!isAdmin) {
      employeeReference.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
          Employee employee = dataSnapshot.getValue(Employee.class);
          if (employee != null) {
            currentEmployee = employee;
          }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
      });
    } else {
      employeesReference.orderByChild("state").equalTo(EEmployeeState.DISPONIBIL.toString()).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
          for (DataSnapshot child : dataSnapshot.getChildren()) {
            Employee employee = child.getValue(Employee.class);
            employeeCheckBoxes.add(getEmployeeCheckBox(employee));
          }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
      });
    }

  }

  private CheckBox getEmployeeCheckBox(Employee employee) {
    CheckBox empCheckBox = createEmptyCheckBox();
    String name = employee.getName();
    empCheckBox.setText(name);
    empCheckBox.setTag(employee);
    empsLayout.addView(empCheckBox);
    return empCheckBox;
  }

  private void fillEquipments() {
    equipmentReference.orderByChild("state").equalTo(EquipmentState.DISPONIBIL.toString()).addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        for (DataSnapshot child : dataSnapshot.getChildren()) {
          Equipment equipment = child.getValue(Equipment.class);
          equipments.add(equipment);
          createEquipmentCheckbox(equipment);
        }
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    });
  }

  private void createEquipmentCheckbox(Equipment equipment) {
    LinearLayout resourceItem = (LinearLayout) inflater.inflate(R.layout.resource_item, consLayout, false);
    ResourceHolder holder = new ResourceHolder(resourceItem);
    String cbText = equipment.getManufacturer() + " " + equipment.getModel();
    holder.mainCheckBox.setText(cbText);
    holder.mainCheckBox.setTag(equipment);
    holder.secondaryTextView.setText(equipment.getType());
    equipmentCheckBoxes.add(holder.mainCheckBox);
    equipmentLayout.addView(resourceItem);
  }

  private void requestGpsEnable() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
    builder.setTitle(R.string.gps_not_found_title);  // GPS not found
    builder.setMessage(R.string.gps_not_found_message); // Want to enable?
    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialogInterface, int i) {
        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
      }
    });
    builder.setNegativeButton(R.string.no, null);
    builder.create().show();
  }

  private boolean isGpsProviderEnabled() {
    LocationManager locationManager = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
  }

  private void setUpViews(View view) {
    taskTitleEditText = (EditText) view.findViewById(R.id.taskTitleEditText);
    newTaskEmpsLayout = (LinearLayout) view.findViewById(R.id.newTaskEmpsLayout);
    typeSpinner = (Spinner) view.findViewById(R.id.tasksTypeSpinner);
    fieldsSpinner = (Spinner) view.findViewById(R.id.tasksFieldSpinner);
    fieldsSpinnerAdapter = new ArrayAdapter<String>(this.getActivity(), R.layout.support_simple_spinner_dropdown_item, new ArrayList<String>());
    fieldsSpinner.setAdapter(fieldsSpinnerAdapter);
    equipmentLayout = (LinearLayout) view.findViewById(R.id.tasksEquipmentLayout);
    expandEquipmentLayoutButton = (Button) view.findViewById(R.id.tasksEquipmentExpandButton);
    newTaskButton = (Button) view.findViewById(R.id.tasksCreateTaskButton);
    empsLayout = (LinearLayout) view.findViewById(R.id.tasksEmployeesLayout);
    empExpandButton = (Button) view.findViewById(R.id.tasksEmployeesExpandButton);
    consLayout = (LinearLayout) view.findViewById(R.id.tasksConsumablesLayout);
    consExpandButton = (Button) view.findViewById(R.id.tasksConsumablesExpandButton);

    if (isAdmin)
      newTaskEmpsLayout.setVisibility(View.VISIBLE);

    setButtonListeners();

  }

  private void switchVisibility(View view, Button caller) {
    if (view.getVisibility() == View.VISIBLE) {
      view.setVisibility(View.GONE);
      caller.setText(getResources().getString(R.string.expand));
    } else {
      view.setVisibility(View.VISIBLE);
      caller.setText(getResources().getString(R.string.collapse));
    }
  }

  private void setButtonListeners() {
    expandEquipmentLayoutButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        switchVisibility(equipmentLayout, expandEquipmentLayoutButton);
      }
    });

    empExpandButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        switchVisibility(empsLayout, empExpandButton);

      }
    });

    consExpandButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        switchVisibility(consLayout, consExpandButton);
      }
    });

    newTaskButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        CompanyField field = getSpinnerField();
        if (field == null) {
          Toast.makeText(NewTaskFragment.this.getActivity(), getResources().getString(R.string.no_field_selected), Toast.LENGTH_SHORT).show();
          return;
        }

        ResourcePlaceholder fieldPh = new ResourcePlaceholder();
        fieldPh.setName(field.getName());
        fieldPh.setId(field.getId());

        List<ResourcePlaceholder> usedEquipments = getEquipmentsList();
        if (usedEquipments.size() == 0) {
          expandEquipmentLayoutButton.setError(getResources().getString(R.string.no_equip_selected));
          return;
        }
        String type = typeSpinner.getSelectedItem().toString();
        String taskTitle = taskTitleEditText.getText().toString();
        if (taskTitle.isEmpty()) {
          taskTitleEditText.setError(getResources().getString(R.string.required_error));
          return;
        }

        Task task = new Task(fieldPh);
        task.setTitle(taskTitle);
        if (!isAdmin) {
          ResourcePlaceholder empPh = null;
          empPh = new ResourcePlaceholder();
          empPh.setName(currentEmployee.getName());
          empPh.setId(currentEmployee.getId());
          task.addEmployee(empPh);
        } else {
          task.setEmployees(getEmployeesList());
        }

        task.setUsedImplements(usedEquipments);
        task.setType(type);
        task.setInputs(getConsumablesList());
        task.setCurrentState(WorkState.NEINCEPUTA.toString());
        TasksDatabaseAdapter adapter = TasksDatabaseAdapter.getInstance(employerId);
        final String id = adapter.getTasksReference().push().getKey();
        adapter.insertTask(id, task, new OnCompleteListener<Void>() {
          @Override
          public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
            onFragmentStartCallback.popBackStack();
            if (isAdmin) {
              Fragment fragmentDetails = new TaskDetailsFragment();
              Bundle args = new Bundle();
              args.putString(Utilities.Constants.USER_ID, employerId);
              args.putString(TASK_ID_EXTRA, id);
              fragmentDetails.setArguments(args);
              onFragmentStartCallback.startFragment(fragmentDetails, false);
            } else {
              if (isGpsProviderEnabled()) {
                startTrackingFragment(id);
              } else {
                requestGpsEnable();
              }
            }
          }
        });
      }
    });
  }

  private void startTrackingFragment(String taskID) {
    EmpTaskTrackingFragment trackingFragment = new EmpTaskTrackingFragment();
    Bundle args = new Bundle();
    args.putString(Utilities.Constants.DB_EMPLOYER_ID, employerId);
    args.putString(TASK_ID_EXTRA, taskID);
    args.putBoolean(FROM_CREATE, true);
    trackingFragment.setArguments(args);
    onFragmentStartCallback.startFragment(trackingFragment, true, TASK_TRACKING_TAG);
  }

  private List<ResourcePlaceholder> getEquipmentsList() {
    List<ResourcePlaceholder> equips = new ArrayList<>();
    for (CheckBox cb : equipmentCheckBoxes) {
      if (cb.isChecked()) {
        Equipment equipment = (Equipment) cb.getTag();
        if (equipment != null) {
          ResourcePlaceholder equipPh = new ResourcePlaceholder();
          equipPh.setId(equipment.getId());
          equipPh.setName(equipment.getManufacturer() + " " + equipment.getModel());
          equips.add(equipPh);
        }
      }
    }
    return equips;
  }

  private List<ResourcePlaceholder> getConsumablesList() {
    List<ResourcePlaceholder> cons = new ArrayList<>();
    for (CheckBox cb : consumableCheckBoxes) {
      if (cb.isChecked()) {
        Consumable consumable = (Consumable) cb.getTag();
        if (consumable != null) {
          ResourcePlaceholder consPh = new ResourcePlaceholder();
          consPh.setId(consumable.getId());
          consPh.setName(consumable.getName());
          cons.add(consPh);
        }
      }
    }
    return cons;
  }

  private List<ResourcePlaceholder> getEmployeesList() {
    List<ResourcePlaceholder> emps = new ArrayList<>();
    for (CheckBox cb : employeeCheckBoxes) {
      if (cb.isChecked()) {
        Employee employee = (Employee) cb.getTag();
        if (employee != null) {
          ResourcePlaceholder empPh = new ResourcePlaceholder();
          empPh.setId(employee.getId());
          empPh.setName(employee.getName());
          emps.add(empPh);
        }
      }
    }
    return emps;
  }

  private CompanyField getSpinnerField() {
    int pos = fieldsSpinner.getSelectedItemPosition();
    return fields.get(pos);

  }

  private class ResourceHolder {
    public CheckBox mainCheckBox;
    public TextView secondaryTextView;

    public ResourceHolder(View view) {
      mainCheckBox = (CheckBox) view.findViewById(R.id.resourceMainCheckBox);
      secondaryTextView = (TextView) view.findViewById(R.id.resourceSecondaryTextView);
    }
  }

}
