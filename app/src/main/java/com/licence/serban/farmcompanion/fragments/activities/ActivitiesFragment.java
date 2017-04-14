package com.licence.serban.farmcompanion.fragments.activities;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
public class ActivitiesFragment extends Fragment {

    private OnAppTitleChange updateTitleCallback;

    private DatabaseReference mainReference;
    private DatabaseReference userActivitiesReference;
    private DatabaseReference fullActivitiesReference;

    private TaskAdapter taskAdapter;

    private String userID;
    private ListView activitiesListView;

    public ActivitiesFragment() {
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
        View view = inflater.inflate(R.layout.fragment_activities, container, false);

        Bundle args = getArguments();
        if (args != null) {
            userID = args.getString(Utilities.Constants.USER_ID);
        }

        TasksDatabaseAdapter adapter = TasksDatabaseAdapter.getInstance(userID);
        activitiesListView = (ListView) view.findViewById(R.id.activitiesListView);
        taskAdapter = new TaskAdapter(getActivity(), R.layout.task_row, new ArrayList<Task>());
        activitiesListView.setAdapter(taskAdapter);
        adapter.setListener(taskAdapter);

        return view;
    }

}
