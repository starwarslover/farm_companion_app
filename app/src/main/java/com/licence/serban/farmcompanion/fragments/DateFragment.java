package com.licence.serban.farmcompanion.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import com.licence.serban.farmcompanion.R;
import com.licence.serban.farmcompanion.classes.StringDateFormatter;
import com.licence.serban.farmcompanion.classes.Utilities;
import com.licence.serban.farmcompanion.interfaces.OnDateSelectedListener;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class DateFragment extends DialogFragment {

    private OnDateSelectedListener dateSelectedListener;

    private DatePicker datePicker;
    private Button datePickButton;
    private long editDate = -1;

    public DateFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            dateSelectedListener = (OnDateSelectedListener) context;
        } catch (ClassCastException ex) {
            throw new ClassCastException(context.toString()
                    + " must implement OnDateSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_date, container, false);

        datePicker = (DatePicker) view.findViewById(R.id.datePicker);
        datePickButton = (Button) view.findViewById(R.id.datePickerButton);

        try {
            datePicker.setMinDate(StringDateFormatter.stringToDate("01-01-1985").getTime());

        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date thisDate = new Date(System.currentTimeMillis());
        datePicker.updateDate(thisDate.getYear() + 1900, thisDate.getMonth(), thisDate.getDate());

        Bundle arguments = getArguments();
        if (arguments != null) {
            editDate = arguments.getLong(Utilities.Constants.DATE_EDIT, -1);
        }
        if (editDate != -1) {
            Date currentDate = new Date(editDate);
            datePicker.updateDate(currentDate.getYear() + 1900, currentDate.getMonth(), currentDate.getDate());

        }

        datePickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String month = String.valueOf(datePicker.getMonth() + 1);
                if (month.length() == 1) {
                    month = "0" + month;
                }
                String day = String.valueOf(datePicker.getDayOfMonth());
                if (day.length() == 1) {
                    day = "0" + day;
                }
                String finalDate = day + "-" + month + "-" + datePicker.getYear();
                dateSelectedListener.onDateSelected(finalDate);
                DateFragment.this.dismiss();
            }
        });

        return view;
    }


}
