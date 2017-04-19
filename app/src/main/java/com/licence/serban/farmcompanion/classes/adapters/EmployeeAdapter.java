package com.licence.serban.farmcompanion.classes.adapters;

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
import com.licence.serban.farmcompanion.classes.models.Employee;

import java.util.List;

/**
 * Created by Serban on 06.03.2017.
 */

public class EmployeeAdapter extends ArrayAdapter<Employee> {
    private Context myContext;
    private int myResourceId;
    private List<Employee> myEmployees;

    public EmployeeAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Employee> objects) {
        super(context, resource, objects);
        this.myContext = context;
        this.myResourceId = resource;
        this.myEmployees = objects;
    }

    @Nullable
    @Override
    public Employee getItem(int position) {
        return myEmployees.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        EmployeeHolder holder = null;

        if (row == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(myContext);
            row = layoutInflater.inflate(myResourceId, parent, false);
            holder = new EmployeeHolder();
            holder.nameTextView = (TextView) row.findViewById(R.id.employeeRowNameTextView);
            holder.positionTextView = (TextView) row.findViewById(R.id.employeeRowPositionTextView);

            row.setTag(holder);
        } else {
            holder = (EmployeeHolder) row.getTag();
        }


        Employee currentEmployee = myEmployees.get(position);
        holder.nameTextView.setText(currentEmployee.getName());
        holder.positionTextView.setText(currentEmployee.getPosition());

        return row;
    }

    public void removeEmployee(String empId) {
        Employee tempEmp = null;
        for (Employee emp : myEmployees) {
            if (empId.equals(emp.getId())) {
                tempEmp = emp;
                break;
            }
        }
        if (tempEmp != null) {
            myEmployees.remove(tempEmp);
            this.notifyDataSetChanged();
        }
    }

    public void updateEmployee(Employee employee) {
        Employee tempEmp = null;
        for (Employee emp : myEmployees) {
            if (employee.getId().equals(emp.getId())) {
                emp = employee;
                break;
            }
        }
        notifyDataSetChanged();
    }

    private class EmployeeHolder {
        public TextView nameTextView;
        public TextView positionTextView;
    }
}
