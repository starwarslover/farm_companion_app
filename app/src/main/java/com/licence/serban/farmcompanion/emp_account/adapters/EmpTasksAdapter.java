package com.licence.serban.farmcompanion.emp_account.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.licence.serban.farmcompanion.R;
import com.licence.serban.farmcompanion.misc.Utilities;
import com.licence.serban.farmcompanion.tasks.models.Task;

import java.util.List;

/**
 * Created by Serban on 24.04.2017.
 */

public class EmpTasksAdapter extends ArrayAdapter<Task> {
  private Context myContext;
  private int myResId;
  private List<Task> myTasks;

  public EmpTasksAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Task> objects) {
    super(context, resource, objects);
    this.myContext = context;
    this.myResId = resource;
    this.myTasks = objects;
  }

  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    View row = convertView;
    EmpTaskHolder holder = null;

    if (row == null) {
      LayoutInflater inflater = LayoutInflater.from(myContext);
      row = inflater.inflate(myResId, parent, false);
      holder = new EmpTaskHolder();
      holder.titleTextView = (TextView) row.findViewById(R.id.empTaskRowTitleTextView);
      holder.statusTextView = (TextView) row.findViewById(R.id.empTaskRowStatusTextView);
      row.setTag(holder);
    } else {
      holder = (EmpTaskHolder) row.getTag();
    }

    Task task = myTasks.get(position);
    holder.titleTextView.setText(task.getTitle());
    if (task.getCurrentState() != null) {
      holder.statusTextView.setText(Utilities.Constants.getTaskStatus(myContext, task.getCurrentState()));
    }
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

  public void updateTask(Task task) {
    int index = 0;
    for (Task t : myTasks) {
      index++;
      if (t.getId().equals(task.getId())) {
        break;
      }
    }
    myTasks.set(index - 1, task);
    this.notifyDataSetChanged();
  }

  private class EmpTaskHolder {
    public TextView titleTextView;
    public TextView statusTextView;
  }
}
