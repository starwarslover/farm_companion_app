package com.licence.serban.farmcompanion.tasks.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.licence.serban.farmcompanion.R;
import com.licence.serban.farmcompanion.activities.MainActivity;
import com.licence.serban.farmcompanion.consumables.adapters.ConsumableDatabaseAdapter;
import com.licence.serban.farmcompanion.consumables.models.Consumable;
import com.licence.serban.farmcompanion.employees.models.Employee;
import com.licence.serban.farmcompanion.equipment.adapters.EquipmentDatabaseAdapter;
import com.licence.serban.farmcompanion.equipment.models.Equipment;
import com.licence.serban.farmcompanion.fields.models.CompanyField;
import com.licence.serban.farmcompanion.interfaces.OnAppTitleChange;
import com.licence.serban.farmcompanion.interfaces.OnTaskCreatedListener;
import com.licence.serban.farmcompanion.misc.Utilities;
import com.licence.serban.farmcompanion.tasks.adapters.TasksDatabaseAdapter;
import com.licence.serban.farmcompanion.tasks.models.ResourcePlaceholder;
import com.licence.serban.farmcompanion.tasks.models.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewTaskFragment extends Fragment {


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
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_new_task, container, false);

    updateTitleCallback.updateTitle(getResources().getString(R.string.new_activity));

    equipmentCheckBoxes = new ArrayList<>();
    consumableCheckBoxes = new ArrayList<>();
    equipments = new ArrayList<>();
    fields = new ArrayList<>();

    isAdmin = ((MainActivity) getActivity()).isUserAdmin();

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
    employeeReference = !isAdmin ? employeesReference.child(empID) : null;
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
    if (this.getActivity() != null) {
      checkBox = new CheckBox(this.getActivity());
      LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
      checkBox.setLayoutParams(params);
      checkBox.setPadding(10, 5, 10, 5);
    }
    return checkBox;
  }

  private CheckBox getConsCheckBox(Consumable cons) {
    CheckBox checkBox = createEmptyCheckBox();
    String text = cons.getName() + " - " + cons.getType().toString();
    checkBox.setText(text);
    checkBox.setTag(cons);
    consLayout.addView(checkBox);
    return checkBox;
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
    }
    
  }

  private void fillEquipments() {
    equipmentReference.addListenerForSingleValueEvent(new ValueEventListener() {
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
    CheckBox checkBox = createEmptyCheckBox();
    String cbText = equipment.getManufacturer() + " " + equipment.getModel();
    checkBox.setText(cbText);
    equipmentLayout.addView(checkBox);
    checkBox.setTag(equipment);
    equipmentCheckBoxes.add(checkBox);
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

    setButtonListeners();

  }

  private void switchVisibility(View view) {
    if (view.getVisibility() == View.VISIBLE)
      view.setVisibility(View.GONE);
    else
      view.setVisibility(View.VISIBLE);
  }

  private void setButtonListeners() {
    expandEquipmentLayoutButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        switchVisibility(equipmentLayout);
      }
    });

    empExpandButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        switchVisibility(empsLayout);

      }
    });

    consExpandButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        switchVisibility(consLayout);
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

        ResourcePlaceholder empPh = new ResourcePlaceholder();
        empPh.setName(currentEmployee.getName());
        empPh.setId(currentEmployee.getId());

        Task task = new Task(fieldPh);
        task.addEmployee(empPh);
        task.setUsedImplements(usedEquipments);
        task.setType(type);

        TasksDatabaseAdapter adapter = TasksDatabaseAdapter.getInstance(employerId);
        String id = adapter.insertTask(task);

        if (((MainActivity) getActivity()).isUserAdmin()) {

        } else {
          if (isGpsProviderEnabled()) {
            taskCreatedCallback.StartActivityTracking(id);
          } else {
            requestGpsEnable();
          }
        }
      }
    });
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

  private CompanyField getSpinnerField() {
    int pos = fieldsSpinner.getSelectedItemPosition();
    return fields.get(pos);

  }

}
