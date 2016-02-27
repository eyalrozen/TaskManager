package com.lauraeyal.taskmanager.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Debug;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import com.lauraeyal.taskmanager.NotificationBroadCastReceiver;
import com.lauraeyal.taskmanager.R;
import com.lauraeyal.taskmanager.TimePickerFragment;
import com.lauraeyal.taskmanager.bl.TaskController;
import com.lauraeyal.taskmanager.bl.UsersController;
import com.lauraeyal.taskmanager.common.TaskItem;
import com.lauraeyal.taskmanager.common.User;
import com.lauraeyal.taskmanager.pushNotification.App42GCMController;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;

import bolts.Task;


public class addtaskActivity extends AppCompatActivity implements DatePickerFragment.PickDate, TimePickerFragment.PickTime {
    private Button createBtn;
    private EditText dscText;
    private RadioButton normalRadio,urgentRadio,lowRadio;
    private EditText descText;
    private String _date,_time;
    TextView DateText,hourText;
    private UsersController Ucontroller;
    private TaskController Tcontroller;
    RadioGroup rg;
    List<String> emailslist = new ArrayList<String>();
    private Spinner locationSpinner,categorySpinner,usersSpinner;

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtask);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Ucontroller = new UsersController(this);
        Tcontroller = new TaskController(this);
        //createBtn = (Button) findViewById(R.id.createBtn);
        //createBtn.setOnClickListener(OnCreateBtnClickListener);
        rg = (RadioGroup) findViewById(R.id.statusradio);
        normalRadio = (RadioButton) findViewById(R.id.Normal);
        urgentRadio = (RadioButton) findViewById(R.id.Urgent);
        lowRadio = (RadioButton) findViewById(R.id.Low);
        descText = (EditText) findViewById(R.id.descrioptionText);
        hourText = (TextView) findViewById(R.id.addtask_hour);
        DateText = (TextView) findViewById(R.id.addtask_date);
        addListenerOnSpinnerItemSelection();
        addItemsOnUsersSpinner();
        progressDialog = new ProgressDialog(addtaskActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Task...");

    }

    public void addListenerOnSpinnerItemSelection() {
        locationSpinner = (Spinner) findViewById(R.id.locationspinner);
        locationSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
        categorySpinner = (Spinner) findViewById(R.id.categoryspinner);
        categorySpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
        usersSpinner = (Spinner) findViewById(R.id.usersspinner);
        usersSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    public void MoveToTaskActivity()
    {
        Intent nextScreen = new Intent(getApplicationContext(), TasksActivity.class);
        nextScreen.putExtra("newTask",descText.getText().toString());
        nextScreen.putExtra("TeamMember",String.valueOf(usersSpinner.getSelectedItem()));
        startActivity(nextScreen);
        progressDialog.show();
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
        DateText.setText(value);
        _date=value;
    }

    @Override
    public void returnTime(String value) {
        _time = value;
        hourText.setText(value);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                finish();
                return true;
            default:
                if(!TextUtils.isEmpty(descText.getText())) {
                    progressDialog.show();
                    String dueDate = _date + " "+ _time;
                    final String Desc = descText.getText().toString();
                    TextView checked = (TextView) findViewById(rg.getCheckedRadioButtonId());
                    final TaskItem newTask = new TaskItem();
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
                            //Tcontroller.createAlarm(Desc,newTask.get_teamMemebr());
                            MoveToTaskActivity();
                        }
                    });

                }
                else
                    Snackbar.make(findViewById(android.R.id.content),"Please fill description",Snackbar.LENGTH_LONG).setAction("Action", null).show();
                return true;
                //return super.onOptionsItemSelected(item);
        }
    }
}
