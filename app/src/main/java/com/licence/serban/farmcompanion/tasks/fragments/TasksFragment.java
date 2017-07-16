package com.licence.serban.farmcompanion.tasks.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.firebase.database.DatabaseReference;
import com.licence.serban.farmcompanion.R;
import com.licence.serban.farmcompanion.interfaces.OnAppTitleChange;
import com.licence.serban.farmcompanion.interfaces.OnFragmentStart;
import com.licence.serban.farmcompanion.misc.Utilities;
import com.licence.serban.farmcompanion.misc.WorkState;
import com.licence.serban.farmcompanion.tasks.adapters.TaskAdapter;
import com.licence.serban.farmcompanion.tasks.adapters.TasksDatabaseAdapter;
import com.licence.serban.farmcompanion.tasks.models.Task;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class TasksFragment extends Fragment {

  private OnAppTitleChange updateTitleCallback;

  private DatabaseReference mainReference;
  private DatabaseReference userActivitiesReference;
  private DatabaseReference fullActivitiesReference;

  private TaskAdapter taskAdapter;

  private String userID;
  private ListView activitiesListView;
  private OnFragmentStart startFragmentCallback;
  private Button newTaskButton;
  private TasksDatabaseAdapter tasksDatabaseAdapter;
  private Spinner activityTypeSpinner;

  public TasksFragment() {
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
      startFragmentCallback = (OnFragmentStart) context;
    } catch (ClassCastException ex) {
      throw new ClassCastException(context.toString()
              + " must implement OnFragmentStart");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    tasksDatabaseAdapter.removeListeners();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    updateTitleCallback.updateTitle(getResources().getString(R.string.nav_activities));
    View view = inflater.inflate(R.layout.fragment_tasks, container, false);

    Bundle args = getArguments();
    if (args != null) {
      userID = args.getString(Utilities.Constants.USER_ID);
    }

    tasksDatabaseAdapter = TasksDatabaseAdapter.getInstance(userID);
    setViews(view);
    activitiesListView.setAdapter(taskAdapter);
    tasksDatabaseAdapter.setListener(taskAdapter);

    newTaskButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Fragment newTaskFragment = new NewTaskFragment();
        Bundle args = new Bundle();
        args.putString(Utilities.Constants.DB_EMPLOYER_ID, userID);
        newTaskFragment.setArguments(args);
        startFragmentCallback.startFragment(newTaskFragment, true);
      }
    });

    activitiesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Task task = taskAdapter.getItem(position);
        if (task != null) {
          startTaskDetails(task, position);
        }
      }
    });

    activitiesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
      @Override
      public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Task task = taskAdapter.getItem(position);
        tasksDatabaseAdapter.deleteTask(task.getId());
        return true;
      }
    });

    return view;
  }

  private void setViews(View view) {
    activitiesListView = (ListView) view.findViewById(R.id.activitiesListView);
    taskAdapter = new TaskAdapter(getActivity(), R.layout.task_row, new ArrayList<Task>());
    newTaskButton = (Button) view.findViewById(R.id.activitiesNewActivityButton);
    activityTypeSpinner = (Spinner) view.findViewById(R.id.activityTypeSpinner);

    activityTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        onFilterChanged(position);
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {

      }
    });
  }

  private void onFilterChanged(int position) {
    tasksDatabaseAdapter.removeListeners();
    taskAdapter.clear();
    if (position == 0)
      tasksDatabaseAdapter.setListener(taskAdapter);
    else
      tasksDatabaseAdapter.setListener(taskAdapter, getWorkState(position));
  }

  private WorkState getWorkState(int position) {
    switch (position) {
      case 1:
        return WorkState.NEINCEPUTA;
      case 2:
        return WorkState.IN_DESFASURARE;
      case 3:
        return WorkState.OPRITA;
      case 4:
        return WorkState.INCHEIATA;
      default:
        return WorkState.NEINCEPUTA;
    }
  }

  private void startTaskDetails(Task task, int position) {
    Fragment fragment = new TaskDetailsFragment();
    Bundle args = new Bundle();
    args.putString(Utilities.Constants.USER_ID, userID);
    args.putString(Utilities.Constants.TASK_ID_EXTRA, task.getId());
    args.putInt("position", position);
    fragment.setArguments(args);
    startFragmentCallback.startFragment(fragment, true);
  }

}
