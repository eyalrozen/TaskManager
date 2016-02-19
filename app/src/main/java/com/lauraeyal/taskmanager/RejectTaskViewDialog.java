package com.lauraeyal.taskmanager;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.lauraeyal.taskmanager.activities.TasksActivity;
import com.lauraeyal.taskmanager.bl.TaskController;
import com.lauraeyal.taskmanager.bl.UsersController;
import com.lauraeyal.taskmanager.common.User;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eyal on 1/16/2016.
 */
public class RejectTaskViewDialog extends DialogFragment {
    private TextView memberText;
    String Description,DueTime,Category,TeamMember,Location,Status,Priority;
    int id;
    private UsersController Ucontroller;
    List<String> emailslist = new ArrayList<String>();
    Button update;
    private Spinner userSpinner;
    private TaskController Tcontroller;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().setTitle("Rejected Task details");
        View rootView = inflater.inflate(R.layout.rejecttaskdialog_fragment, null);
        getDialog().requestWindowFeature(STYLE_NO_TITLE);
        id = getArguments().getInt("ID");
        Description = getArguments().getString("Description");
        DueTime = getArguments().getString("DueTime");
        Category = getArguments().getString("Category");
        TeamMember = getArguments().getString("TeamMember");
        Location = getArguments().getString("Location");
        Status = getArguments().getString("Status");
        Priority = getArguments().getString("Priority");
        Tcontroller = new TaskController(getActivity());
        memberText = (TextView)rootView.findViewById(R.id.rejectMember);
        update = (Button) rootView.findViewById(R.id.updaterejTaskBtn);
        userSpinner = (Spinner) rootView.findViewById(R.id.rejusersspinner);
        userSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Updating Task");
        Ucontroller = new UsersController(getActivity());
        addItemsOnUsersSpinner();
        return rootView;

    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        memberText.setText(TeamMember);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUpdateClicked();
            }
        });
    }
    public void onUpdateClicked()
    {
        progressDialog.show();
        Tcontroller.UpdateTask(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e==null)
                {
                    for(ParseObject task:objects)
                    {
                        task.put("TeamMember",String.valueOf(userSpinner.getSelectedItem()));
                        task.put("isApprovle",0);
                        task.saveEventually(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e==null) {
                                    progressDialog.dismiss();
                                    ((TasksActivity)getActivity()).onRefreshClicked();
                                    getDialog().dismiss();
                                }
                            }
                        });
                    }

                }
            }
        },id,Description,TeamMember,"Task_assigned_worker",String.valueOf(userSpinner.getSelectedItem()));
    }

    private class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {

        }
        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }
    }
    // add items into spinner dynamically
    public void addItemsOnUsersSpinner() {

        List<User> tempList = Ucontroller.GetUsersList();
        for(User b : tempList)
        {
            emailslist.add(b.getUserName());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, emailslist);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userSpinner.setAdapter(dataAdapter);
    }
}
