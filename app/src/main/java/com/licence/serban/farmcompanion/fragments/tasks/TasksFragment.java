package com.licence.serban.farmcompanion.fragments.tasks;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.licence.serban.farmcompanion.classes.Utilities;
import com.licence.serban.farmcompanion.classes.adapters.TaskAdapter;
import com.licence.serban.farmcompanion.classes.adapters.TasksDatabaseAdapter;
import com.licence.serban.farmcompanion.classes.models.Task;
import com.licence.serban.farmcompanion.interfaces.OnAppTitleChange;
import com.licence.serban.farmcompanion.R;

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

        final TasksDatabaseAdapter tasksDatabaseAdapter = TasksDatabaseAdapter.getInstance(userID);
        activitiesListView = (ListView) view.findViewById(R.id.activitiesListView);
        taskAdapter = new TaskAdapter(getActivity(), R.layout.task_row, new ArrayList<Task>());
        activitiesListView.setAdapter(taskAdapter);
        tasksDatabaseAdapter.setListener(taskAdapter);

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

}
