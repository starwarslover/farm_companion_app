package com.licence.serban.farmcompanion.classes.adapters;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.licence.serban.farmcompanion.R;
import com.licence.serban.farmcompanion.classes.models.Task;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Serban on 14.04.2017.
 */

public class TaskAdapter extends ArrayAdapter<Task> {
    private Context myContext;
    private int myResId;
    private List<Task> myTasks;
    View.OnClickListener myListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Integer position = (Integer) v.getTag();
            Task t = myTasks.get(position);
            Toast.makeText(myContext, t.getId(), Toast.LENGTH_SHORT).show();
        }
    };

    public TaskAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Task> objects) {
        super(context, resource, objects);
        this.myContext = context;
        this.myResId = resource;
        this.myTasks = objects;
    }

    @Nullable
    @Override
    public Task getItem(int position) {
        return this.myTasks.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        TaskHolder holder = null;
        if (row == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(myContext);
            row = layoutInflater.inflate(myResId, parent, false);
            holder = new TaskHolder();
            holder.nameTextView = (TextView) row.findViewById(R.id.taskRowNameTextView);
            holder.somethingTextView = (TextView) row.findViewById(R.id.taskRowSomethingTextView);
            holder.statusTextView = (TextView) row.findViewById(R.id.taskRowStatusTextView);
            holder.showOnMapButton = (Button) row.findViewById(R.id.taskRowShowButton);
            row.setTag(holder);
        } else {
            holder = (TaskHolder) row.getTag();
        }

        Task currentTask = myTasks.get(position);
        if (currentTask.isCanTrack()) {
            holder.showOnMapButton.setVisibility(View.VISIBLE);
            holder.showOnMapButton.setTag(position);
            holder.showOnMapButton.setOnClickListener(myListener);
        } else {
            holder.showOnMapButton.setVisibility(View.GONE);
        }

        holder.nameTextView.setText("Task " + position);


        return row;
    }

    public void removeTask(String id) {
        Task taskToRemove = null;
        for (Task t : myTasks) {
            if (t.getId().equals(id)) {
                taskToRemove = t;
                break;
            }
        }
        if (taskToRemove != null) {
            myTasks.remove(taskToRemove);
            notifyDataSetChanged();
        }
    }

    public int containsTask(Task task) {
        if (task != null) {
            for (int i = 0; i < myTasks.size(); i++) {
                if (task.getId().equals(myTasks.get(i).getId())) {
                    return i;
                }
            }
        }
        return -1;
    }

    public void setTask(int pos, Task task) {
        myTasks.set(pos, task);
        this.notifyDataSetChanged();
    }

    private class TaskHolder {
        private TextView nameTextView;
        private TextView somethingTextView;
        private TextView statusTextView;
        private Button showOnMapButton;
    }
}
