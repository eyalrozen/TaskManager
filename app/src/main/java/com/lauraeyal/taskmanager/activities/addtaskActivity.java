package com.lauraeyal.taskmanager.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lauraeyal.taskmanager.R;
import com.lauraeyal.taskmanager.TimePickerFragment;


public class addtaskActivity extends AppCompatActivity {
    private Button createBtn;
    private EditText dscText;
    private RadioButton noramRadio,urgentRadio;
    private EditText descText;
    RadioGroup rg;
    private Spinner locationSpinner,categorySpinner,usersSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtask);
        createBtn = (Button) findViewById(R.id.createBtn);
        createBtn.setOnClickListener(OnCreateBtnClickListener);
        rg = (RadioGroup) findViewById(R.id.statusradio);
        noramRadio = (RadioButton) findViewById(R.id.Noraml);
        urgentRadio = (RadioButton) findViewById(R.id.Urgent);
        descText = (EditText) findViewById(R.id.descrioptionText);
        addListenerOnSpinnerItemSelection();

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
                String Desc = descText.getText().toString();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("Description", Desc);
                resultIntent.putExtra("Category", String.valueOf(categorySpinner.getSelectedItem()));
                resultIntent.putExtra("Location", String.valueOf(locationSpinner.getSelectedItem()));
                resultIntent.putExtra("User", String.valueOf(usersSpinner.getSelectedItem()));
                TextView checked = (TextView) findViewById(rg.getCheckedRadioButtonId());
                resultIntent.putExtra("Priority", checked.getText().toString());
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
            else
                Snackbar.make(v,"Please fill description",Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }

        ;

    };

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    private class CustomOnItemSelectedListener implements android.widget.AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {

        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }
    }
}
