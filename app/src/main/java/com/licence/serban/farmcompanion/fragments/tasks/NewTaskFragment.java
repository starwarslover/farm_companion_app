package com.licence.serban.farmcompanion.fragments.tasks;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.licence.serban.farmcompanion.R;
import com.licence.serban.farmcompanion.classes.Utilities;
import com.licence.serban.farmcompanion.classes.adapters.TasksDatabaseAdapter;
import com.licence.serban.farmcompanion.classes.models.ResourcePlaceholder;
import com.licence.serban.farmcompanion.classes.models.Task;
import com.licence.serban.farmcompanion.interfaces.OnAppTitleChange;
import com.licence.serban.farmcompanion.interfaces.OnTaskCreatedListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewTaskFragment extends Fragment {


    public OnTaskCreatedListener taskCreatedCallback;
    private OnAppTitleChange updateTitleCallback;
    private Button newTaskButton;
    private String empID;
    private String employerId;

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

        Bundle args = getArguments();
        if (args != null) {
            empID = args.getString(Utilities.Constants.USER_ID);
            employerId = args.getString(Utilities.Constants.DB_EMPLOYER_ID);
        }

        setUpViews(view);

        newTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResourcePlaceholder placeholder = new ResourcePlaceholder();
                placeholder.setName("Test field");
                Task task = new Task(placeholder);
                ResourcePlaceholder empPlaceholder = new ResourcePlaceholder();
                empPlaceholder.setId(empID);
                empPlaceholder.setName("ionel");
                task.addEmployee(empPlaceholder);
                TasksDatabaseAdapter adapter = TasksDatabaseAdapter.getInstance(employerId);
                String id = adapter.insertTask(task);

                taskCreatedCallback.StartActivityTracking(id);
            }
        });

        return view;
    }

    private void setUpViews(View view) {
        newTaskButton = (Button) view.findViewById(R.id.tasksCreateTaskButton);
    }

}
