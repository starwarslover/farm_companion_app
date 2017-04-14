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
import android.widget.TextView;

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

    public TaskAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Task> objects) {
        super(context, resource, objects);
        this.myContext = context;
        this.myResId = resource;
        this.myTasks = objects;
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
            row.setTag(holder);
        } else {
            holder = (TaskHolder) row.getTag();
        }

        Task currentTask = myTasks.get(position);

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
            this.notifyDataSetChanged();
        }
    }

    private class TaskHolder {
        private TextView nameTextView;
        private TextView somethingTextView;
        private TextView statusTextView;
    }
}
