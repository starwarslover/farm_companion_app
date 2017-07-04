package com.licence.serban.farmcompanion.emp_account.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.licence.serban.farmcompanion.R;
import com.licence.serban.farmcompanion.misc.Utilities;
import com.licence.serban.farmcompanion.emp_account.adapters.EmpTasksAdapter;
import com.licence.serban.farmcompanion.tasks.adapters.TasksDatabaseAdapter;
import com.licence.serban.farmcompanion.tasks.models.Task;
import com.licence.serban.farmcompanion.tasks.fragments.TaskDetailsFragment;
import com.licence.serban.farmcompanion.interfaces.OnAppTitleChange;
import com.licence.serban.farmcompanion.interfaces.OnCreateNewTaskListener;
import com.licence.serban.farmcompanion.interfaces.OnFragmentStart;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmployeeTasksFragment extends Fragment {


    private OnAppTitleChange updateTitleCallback;
    private Button startBroadcastButton;
    private DatabaseReference databaseReference;
    private String userID;
    private String employerID;
    private DatabaseReference mainReference;
    private DatabaseReference userActivitiesReference;
    private DatabaseReference fullActivitiesReference;
    private Button newTaskButton;
    private OnCreateNewTaskListener newTaskCallback;
    private ListView tasksListView;
    private EmpTasksAdapter tasksAdapter;
    private TasksDatabaseAdapter databaseAdapter;
    private OnFragmentStart startFragmentCallBack;


    public EmployeeTasksFragment() {
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
            newTaskCallback = (OnCreateNewTaskListener) context;
        } catch (ClassCastException ex) {
            throw new ClassCastException(context.toString()
                    + " must implement OnNewTaskListener");
        }
        try {
            startFragmentCallBack = (OnFragmentStart) context;
        } catch (ClassCastException ex) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentStart");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_employee_tasks, container, false);

        newTaskButton = (Button) view.findViewById(R.id.empTaskNewTaskButton);

        updateTitleCallback.updateTitle(getResources().getString(R.string.my_tasks));

        newTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newTaskCallback.openNewTaskUI();
            }
        });

        Bundle args = getArguments();
        if (args != null) {
            employerID = args.getString(Utilities.Constants.DB_EMPLOYER_ID);
        }

        tasksListView = (ListView) view.findViewById(R.id.empTasksListView);
        tasksAdapter = new EmpTasksAdapter(getActivity(), R.layout.emp_task_row, new ArrayList<Task>());
        tasksListView.setAdapter(tasksAdapter);
        tasksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Task task = tasksAdapter.getItem(position);
                startFragment(task, position);
            }
        });

        databaseAdapter = TasksDatabaseAdapter.getInstance(employerID);
        databaseAdapter.setListener(tasksAdapter);


        return view;
    }

    private void startFragment(Task task, int position) {
        TaskDetailsFragment detailsFragment = new TaskDetailsFragment();
        Bundle args = new Bundle();
        args.putString(Utilities.Constants.TASK_ID_EXTRA, task.getId());
        args.putString(Utilities.Constants.DB_EMPLOYER_ID, employerID);
        args.putInt("position", position);
        detailsFragment.setArguments(args);
        startFragmentCallBack.startFragment(detailsFragment, true);
    }

}
