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
import com.licence.serban.farmcompanion.classes.models.CompanyField;

import java.util.List;

/**
 * Created by Serban on 17.03.2017.
 */

public class FieldsAdapter extends ArrayAdapter<CompanyField> {
    private Context myContext;
    private int myResourceId;
    private List<CompanyField> myFields;

    public FieldsAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<CompanyField> objects) {
        super(context, resource, objects);
        myContext = context;
        myResourceId = resource;
        myFields = objects;
    }

    @Nullable
    @Override
    public CompanyField getItem(int position) {
        return myFields.get(position);
    }

    public void removeField(String fieldID) {
        CompanyField temp = null;
        for (CompanyField field : myFields) {
            if (fieldID.equals(field.getId())) {
                temp = field;
                break;
            }
        }
        if (temp != null) {
            myFields.remove(temp);
            notifyDataSetChanged();
        }

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        FieldHolder holder = null;


        if (row == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(myContext);
            row = layoutInflater.inflate(myResourceId, parent, false);
            holder = new FieldHolder();
            holder.fieldName = (TextView) row.findViewById(R.id.fieldRowNameTextView);
            holder.fieldArea = (TextView) row.findViewById(R.id.fieldRowAreaTextView);
            holder.fieldCrop = (TextView) row.findViewById(R.id.fieldRowCropTextView);
            row.setTag(holder);

        } else {
            holder = (FieldHolder) row.getTag();
        }

        CompanyField companyField = myFields.get(position);

        String areaString = String.valueOf(companyField.getArea()) + " ha";

        holder.fieldArea.setText(areaString);
        holder.fieldCrop.setText(companyField.getCropStatus());
        holder.fieldName.setText(companyField.getName());


        return row;
    }

    private class FieldHolder {
        TextView fieldName;
        TextView fieldCrop;
        TextView fieldArea;
    }
}
