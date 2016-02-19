package com.lauraeyal.taskmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by Eyal on 1/6/2016.
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    PickDate mCallback;
    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        mCallback = (PickDate) activity;
    }

    public interface PickDate
    {
        public void returnDate(String value);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT, this, year, month, day);
       // return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        /*((TextView) getActivity().findViewById(R.id.dayText)).setText(day);
        ((TextView) getActivity().findViewById(R.id.month)).setText(month);
        ((TextView) getActivity().findViewById(R.id.year)).setText(year);*/
        if(mCallback!=null)
        {
            StringBuilder sb = new StringBuilder();
            sb.append(day);
            sb.append("/");
            if(month <9)
                sb.append("0"+(month+1));
            else
                sb.append(month+1);
            mCallback.returnDate(sb.toString());
        }

    }
}


