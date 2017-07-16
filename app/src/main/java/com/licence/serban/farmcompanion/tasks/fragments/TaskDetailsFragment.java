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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.licence.serban.farmcompanion.R;
import com.licence.serban.farmcompanion.emp_account.fragments.EmpTaskDetailsFragment;
import com.licence.serban.farmcompanion.emp_account.fragments.EmpTaskTrackingFragment;
import com.licence.serban.farmcompanion.interfaces.OnDrawerMenuLock;
import com.licence.serban.farmcompanion.interfaces.OnFragmentStart;
import com.licence.serban.farmcompanion.misc.StringDateFormatter;
import com.licence.serban.farmcompanion.misc.Utilities;
import com.licence.serban.farmcompanion.misc.WorkState;
import com.licence.serban.farmcompanion.tasks.adapters.TasksDatabaseAdapter;
import com.licence.serban.farmcompanion.tasks.models.ResourcePlaceholder;
import com.licence.serban.farmcompanion.tasks.models.Task;

import java.text.DecimalFormat;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaskDetailsFragment extends Fragment {


  private String userID;
  private String taskID;
  private String employerID;
  private DatabaseReference taskReference;
  private TextView taskTypeTextView;
  private TextView taskFieldTextView;
  private LinearLayout taskEquipLayout;
  private Button expandButton;
  private OnFragmentStart startFragmentCallback;
  private TextView taskNameTextView;
  private int position;
  private Button startTaskButton;
  private LinearLayout taskHistoryLayout;
  private LinearLayout taskHistoryViews;
  private ValueEventListener taskListener;
  private OnDrawerMenuLock drawerMenuLock;
  private LayoutInflater inflater;
  private Button tasksDetailsEmployeesExpandButton;
  private LinearLayout tasksDetailsEmployeesLayout;
  private Button tasksDetailsConsumablesExpandButton;
  private LinearLayout tasksDetailsConsumablesLayout;
  private ValueEventListener listener;
  private Task currentTask;
  private TextView taskDetailsTaskStateTextView;
  private Context context;
  private TextView taskDetailsTotalTimeTextView;
  private TextView taskDetailsStopTimeTextView;
  private TextView taskDetailsTotalDistanceTextView;

  public TaskDetailsFragment() {
    // Required empty public constructor
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    this.context = context;
    try {
      startFragmentCallback = (OnFragmentStart) context;
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
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_task_details, container, false);
    this.inflater = inflater;

    drawerMenuLock.lockDrawer();
    Bundle args = getArguments();
    if (args != null) {
      userID = args.getString(Utilities.Constants.USER_ID);
      taskID = args.getString(Utilities.Constants.TASK_ID_EXTRA);
      employerID = args.getString(Utilities.Constants.DB_EMPLOYER_ID);
      position = args.getInt("position");
    }

    setDbReferences();
    setViews(view);
    fillViews();

    return view;
  }

  private void fillViews() {
    taskListener = new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        currentTask = dataSnapshot.getValue(Task.class);
        if (currentTask != null) {
          fillTaskInfo();
          fillTaskResourcesInfo();

          showTaskHistory(currentTask);

        }
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    };
    taskReference.addValueEventListener(taskListener);
  }

  private void fillTaskResourcesInfo() {
    ResourcePlaceholder field = currentTask.getField();
    if (currentTask.getCurrentState() == WorkState.INCHEIATA) {
      startTaskButton.setVisibility(View.GONE);
    }
    if (field != null) {
      taskFieldTextView.setText(field.getName());
    }
    taskTypeTextView.setText(currentTask.getType());
    tasksDetailsEmployeesLayout.removeAllViews();
    if (currentTask.getEmployees() != null) {
      createResourceItems(currentTask.getEmployees(), tasksDetailsEmployeesLayout);
    }

    taskEquipLayout.removeAllViews();
    if (currentTask.getUsedImplements() != null) {
      createResourceItems(currentTask.getUsedImplements(), taskEquipLayout);
    }

    tasksDetailsConsumablesLayout.removeAllViews();
    if (currentTask.getInputs() != null) {
      createResourceItems(currentTask.getInputs(), tasksDetailsConsumablesLayout);
    }
  }

  private void fillTaskInfo() {
    taskNameTextView.setText(currentTask.getTitle());
    taskDetailsTaskStateTextView.setText(Utilities.Constants.getTaskStatus(context, currentTask.getCurrentState()));
    String speedStr = new DecimalFormat("#0.0").format(currentTask.getDistanceTraveled()) + " km/h";
    taskDetailsTotalDistanceTextView.setText(speedStr);
    taskDetailsTotalTimeTextView.setText(StringDateFormatter.millisToTime(currentTask.getTotalTime()));
    taskDetailsStopTimeTextView.setText(StringDateFormatter.millisToTime(currentTask.getTimeStopped()));
  }

  private void showTaskHistory(Task task) {
    taskHistoryLayout.removeAllViews();
    if (task.getStartDates() != null && task.getStopDates() != null)
      for (int i = 0; i < task.getStartDates().size(); i++) {
        Long stopDate = i < task.getStopDates().size() ? task.getStopDates().get(i) : null;
        taskHistoryLayout.addView(createHistoryItem(task.getStartDates().get(i), stopDate));
      }
  }

  private View createHistoryItem(Long startDate, Long stopDate) {

    View item = inflater.inflate(R.layout.activity_history_item, taskHistoryLayout, false);
    TextView startTextView = (TextView) item.findViewById(R.id.taskDetailsStartedAtTextView);
    TextView stoppedTextView = (TextView) item.findViewById(R.id.taskDetailsStoppedAtTextView);
    String startDateFormat = "N/A";
    if (startDate != null)
      startDateFormat = StringDateFormatter.millisToString(startDate, "dd MMM yyyy\nHH:mm a");
    startTextView.setText(startDateFormat);
    String stopDateFormat = "N/A";
    if (stopDate != null)
      stopDateFormat = StringDateFormatter.millisToString(stopDate, "dd MMM yyyy\nHH:mm a");
    stoppedTextView.setText(stopDateFormat);
    return item;
  }

  private void createResourceItems(List<ResourcePlaceholder> list, ViewGroup parent) {
    for (ResourcePlaceholder placeholder : list) {
      LinearLayout item = (LinearLayout) inflater.inflate(R.layout.item_task_details_resource, parent, false);
      ResourceHolder holder = new ResourceHolder(item);
      holder.primaryTextView.setText(placeholder.getName());
      parent.addView(item);
    }
  }

  private void setDbReferences() {
    if (userID != null) {
      taskReference = FirebaseDatabase.getInstance().getReference().child(TasksDatabaseAdapter.DB_TASKS).child(userID).child(taskID);
    } else {
      taskReference = FirebaseDatabase.getInstance().getReference().child(TasksDatabaseAdapter.DB_TASKS).child(employerID).child(taskID);
    }
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

  private void setViews(View view) {
    taskDetailsTotalTimeTextView = (TextView) view.findViewById(R.id.taskDetailsTotalTimeTextView);
    taskDetailsStopTimeTextView = (TextView) view.findViewById(R.id.taskDetailsStopTimeTextView);
    taskDetailsTotalDistanceTextView = (TextView) view.findViewById(R.id.taskDetailsTotalDistanceTextView);
    taskDetailsTaskStateTextView = (TextView) view.findViewById(R.id.taskDetailsTaskStateTextView);
    taskHistoryViews = (LinearLayout) view.findViewById(R.id.taskDetailsHistoryViews);
    taskHistoryLayout = (LinearLayout) view.findViewById(R.id.taskHistoryLinearLayout);
    taskNameTextView = (TextView) view.findViewById(R.id.taskDetailsTaskNameTextView);
    taskTypeTextView = (TextView) view.findViewById(R.id.taskDetailsTypeTextView);
    taskFieldTextView = (TextView) view.findViewById(R.id.taskDetailsFieldTextView);
    taskEquipLayout = (LinearLayout) view.findViewById(R.id.tasksDetailsEquipmentLayout);
    expandButton = (Button) view.findViewById(R.id.tasksDetailsEquipmentExpandButton);
    startTaskButton = (Button) view.findViewById(R.id.taskDetailsStartTaskButton);
    tasksDetailsEmployeesExpandButton = (Button) view.findViewById(R.id.tasksDetailsEmployeesExpandButton);
    tasksDetailsEmployeesLayout = (LinearLayout) view.findViewById(R.id.tasksDetailsEmployeesLayout);
    tasksDetailsConsumablesExpandButton = (Button) view.findViewById(R.id.tasksDetailsConsumablesExpandButton);
    tasksDetailsConsumablesLayout = (LinearLayout) view.findViewById(R.id.tasksDetailsConsumablesLayout);

    expandButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        switchVisibility(taskEquipLayout, expandButton);
      }
    });

    tasksDetailsEmployeesExpandButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        switchVisibility(tasksDetailsEmployeesLayout, tasksDetailsEmployeesExpandButton);
      }
    });

    tasksDetailsConsumablesExpandButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        switchVisibility(tasksDetailsConsumablesLayout, tasksDetailsConsumablesExpandButton);
      }
    });

    if (employerID != null) {
      taskHistoryViews.setVisibility(View.GONE);
      startTaskButton.setVisibility(View.VISIBLE);
      startTaskButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (isGpsProviderEnabled()) {
            startTrackingFragment();
          } else {
            requestGpsEnable();
          }
        }
      });
    } else {
      startTaskButton.setVisibility(View.GONE);
    }
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

  private void startTrackingFragment() {
    EmpTaskTrackingFragment trackingFragment = new EmpTaskTrackingFragment();
    Bundle args = new Bundle();
    args.putString(Utilities.Constants.DB_EMPLOYER_ID, employerID);
    args.putString(Utilities.Constants.TASK_ID_EXTRA, taskID);
    trackingFragment.setArguments(args);
    startFragmentCallback.startFragment(trackingFragment, true, EmpTaskDetailsFragment.TASK_TRACKING_TAG);
  }

  @Override
  public void onDetach() {
    super.onDetach();
    drawerMenuLock.unlockDrawerMenu();
    if (taskReference != null)
      taskReference.removeEventListener(taskListener);
  }

  private class ResourceHolder {
    public TextView primaryTextView;

    public ResourceHolder(View view) {
      primaryTextView = (TextView) view.findViewById(R.id.resourceNameTextView);
    }
  }
}
