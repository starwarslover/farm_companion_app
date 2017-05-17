package com.licence.serban.farmcompanion.fragments.tasks;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
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
import com.licence.serban.farmcompanion.classes.Equipment;
import com.licence.serban.farmcompanion.classes.Utilities;
import com.licence.serban.farmcompanion.classes.adapters.TasksDatabaseAdapter;
import com.licence.serban.farmcompanion.classes.models.CompanyField;
import com.licence.serban.farmcompanion.classes.models.ResourcePlaceholder;
import com.licence.serban.farmcompanion.classes.models.Task;
import com.licence.serban.farmcompanion.fragments.emp_account.EmpTaskTrackingFragment;
import com.licence.serban.farmcompanion.interfaces.OnFragmentStart;

import org.w3c.dom.Text;

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

    public TaskDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            startFragmentCallback = (OnFragmentStart) context;
        } catch (ClassCastException ex) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentStart");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_task_details, container, false);

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
        taskReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Task task = dataSnapshot.getValue(Task.class);
                taskNameTextView.setText("Task " + (position + 1));
                ResourcePlaceholder field = task.getField();
                if (field != null) {
                    taskFieldTextView.setText(field.getName());
                }
                taskTypeTextView.setText(task.getType());
                List<ResourcePlaceholder> equips = task.getUsedImplements();
                if (equips != null) {
                    for (ResourcePlaceholder equip : equips) {
                        addEquipment(equip);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addEquipment(ResourcePlaceholder equip) {
        TextView textView = new TextView(this.getActivity());
        textView.setPadding(0, 5, 0, 5);
        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        textView.setText(equip.getName());
        textView.setTag(equip);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResourcePlaceholder e = (ResourcePlaceholder) v.getTag();
                if (e != null) {
                    startEquipmentDetails(e);
                }
            }
        });
        taskEquipLayout.addView(textView);
    }

    private void startEquipmentDetails(ResourcePlaceholder e) {
    }

    private void setDbReferences() {
        if (userID != null) {
            taskReference = FirebaseDatabase.getInstance().getReference().child(TasksDatabaseAdapter.DB_TASKS).child(userID).child(taskID);
        } else {
            taskReference = FirebaseDatabase.getInstance().getReference().child(TasksDatabaseAdapter.DB_TASKS).child(employerID).child(taskID);
        }
    }

    private void setViews(View view) {
        taskNameTextView = (TextView) view.findViewById(R.id.taskDetailsTaskNameTextView);
        taskTypeTextView = (TextView) view.findViewById(R.id.taskDetailsTypeTextView);
        taskFieldTextView = (TextView) view.findViewById(R.id.taskDetailsFieldTextView);
        taskEquipLayout = (LinearLayout) view.findViewById(R.id.tasksDetailsEquipmentLayout);
        expandButton = (Button) view.findViewById(R.id.tasksDetailsEquipmentExpandButton);
        startTaskButton = (Button) view.findViewById(R.id.taskDetailsStartTaskButton);

        expandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (taskEquipLayout.getVisibility() == View.VISIBLE) {
                    taskEquipLayout.setVisibility(View.GONE);
                    expandButton.setBackgroundColor(Color.TRANSPARENT);
                } else {
                    taskEquipLayout.setVisibility(View.VISIBLE);
                    expandButton.setBackgroundColor(Color.LTGRAY);
                }
            }
        });

        if (employerID != null) {
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
        startFragmentCallback.startFragment(trackingFragment, true);
    }


}
