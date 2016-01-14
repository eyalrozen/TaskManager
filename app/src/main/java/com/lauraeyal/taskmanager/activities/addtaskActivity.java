package com.lauraeyal.taskmanager.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.lauraeyal.taskmanager.DatePickerFragment;
import com.lauraeyal.taskmanager.R;
import com.lauraeyal.taskmanager.TimePickerFragment;
import com.lauraeyal.taskmanager.bl.TaskController;
import com.lauraeyal.taskmanager.bl.UsersController;
import com.lauraeyal.taskmanager.common.TaskItem;
import com.lauraeyal.taskmanager.common.User;
import com.parse.ParseException;
import com.parse.SaveCallback;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;

import bolts.Task;


public class addtaskActivity extends AppCompatActivity implements DatePickerFragment.PickDate, TimePickerFragment.PickTime {
    private Button createBtn;
    private EditText dscText;
    private RadioButton noramRadio,urgentRadio;
    private EditText descText;
    private String _date,_time;
    private UsersController Ucontroller;
    private TaskController Tcontroller;
    RadioGroup rg;
    List<String> emailslist = new ArrayList<String>();
    private Spinner locationSpinner,categorySpinner,usersSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtask);
        Ucontroller = new UsersController(this);
        Tcontroller = new TaskController(this);
        createBtn = (Button) findViewById(R.id.createBtn);
        createBtn.setOnClickListener(OnCreateBtnClickListener);
        rg = (RadioGroup) findViewById(R.id.statusradio);
        noramRadio = (RadioButton) findViewById(R.id.Noraml);
        urgentRadio = (RadioButton) findViewById(R.id.Urgent);
        descText = (EditText) findViewById(R.id.descrioptionText);
        addListenerOnSpinnerItemSelection();
        addItemsOnUsersSpinner();

    }

    public void addListenerOnSpinnerItemSelection() {
        locationSpinner = (Spinner) findViewById(R.id.locationspinner);
        locationSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
        categorySpinner = (Spinner) findViewById(R.id.categoryspinner);
        categorySpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
        usersSpinner = (Spinner) findViewById(R.id.usersspinner);
        usersSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    private View.OnClickListener OnCreateBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!TextUtils.isEmpty(descText.getText())) {
                String dueDate = _date + "T"+ _time;
                String Desc = descText.getText().toString();
                TextView checked = (TextView) findViewById(rg.getCheckedRadioButtonId());
                TaskItem newTask = new TaskItem();
                newTask.setCategory(String.valueOf(categorySpinner.getSelectedItem()));
                newTask.SetTaskApprovle(0);
                newTask.SetPriority(checked.getText().toString());
                newTask.SetTeamMemebr(String.valueOf(usersSpinner.getSelectedItem()));
                newTask.SetDueTime(dueDate);
                newTask.SetDescription(Desc);
                newTask.SetLocation(String.valueOf(locationSpinner.getSelectedItem()));
                newTask.SetTaskStatus("Waiting");
                Tcontroller.AddTask(newTask, new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        MoveToTaskActivity();
                    }
                });

            }
            else
                Snackbar.make(v,"Please fill description",Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }

        ;

    };

    public void MoveToTaskActivity()
    {
        Intent nextScreen = new Intent(getApplicationContext(), TasksActivity.class);
        startActivity(nextScreen);
        finish();
    }
    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }


    @Override
    public void returnDate(String value) {
        _date=value;
    }

    @Override
    public void returnTime(String value) {
        _time = value;
    }

    private class CustomOnItemSelectedListener implements android.widget.AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {

        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }
    }

    // add items into spinner dynamically
    public void addItemsOnUsersSpinner() {

        usersSpinner = (Spinner) findViewById(R.id.usersspinner);
        List<User> tempList = Ucontroller.GetUsersList();
        for(User b : tempList)
        {
            emailslist.add(b.getUserName());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, emailslist);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        usersSpinner.setAdapter(dataAdapter);
    }
}
