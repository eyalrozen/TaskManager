package com.lauraeyal.taskmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by Eyal on 1/2/2016.
 */
public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    PickTime mCallback;
    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        mCallback = (PickTime) activity;
    }

    public interface PickTime
    {
        public void returnTime(String value);

    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT,this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int Minute) {
        // Do something with the time chosen by the user
        if(mCallback!=null)
        {
            StringBuilder sb = new StringBuilder();
            if(hourOfDay <10)
                sb.append("0"+hourOfDay);
            else
                sb.append(hourOfDay);
            sb.append(":");
            if(Minute < 10)
                sb.append("0"+Minute);
            else
                sb.append(Minute);
            mCallback.returnTime(sb.toString());
        }
    }
}
