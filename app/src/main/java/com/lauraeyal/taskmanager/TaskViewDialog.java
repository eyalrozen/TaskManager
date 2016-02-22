package com.lauraeyal.taskmanager;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.Visibility;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lauraeyal.taskmanager.activities.TasksActivity;
import com.lauraeyal.taskmanager.activities.addtaskActivity;
import com.lauraeyal.taskmanager.bl.TaskController;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Created by Eyal on 1/16/2016.
 */
public class TaskViewDialog extends DialogFragment {
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1888;
    private TextView descText ,ctgryText,timeText,memberText,locationText,statusText,priorityText,ApprovleText;
    String Description,DueTime,Category,TeamMember,Location,Status,Priority;
    int id;
    Button update,downloadBtn;
    ImageButton cameraBtn;
    ProgressBar progressBar;
    private Spinner statusSpinner;
    private TaskController Tcontroller;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().setTitle("Task Details");
        View rootView = inflater.inflate(R.layout.taskdialog_fragment, null);
        getDialog().requestWindowFeature(STYLE_NO_TITLE);
        id = getArguments().getInt("ID");
        cameraBtn = (ImageButton)rootView.findViewById(R.id.cameraBtn);
        downloadBtn = (Button)rootView.findViewById(R.id.downloadBtn);
        progressBar =(ProgressBar)rootView.findViewById(R.id.uploadprogressBar);
        cameraBtn.setVisibility(rootView.INVISIBLE);
        progressBar.setVisibility(rootView.INVISIBLE);
        downloadBtn.setVisibility(rootView.INVISIBLE);
        Description = getArguments().getString("Description");
        DueTime = getArguments().getString("DueTime");
        Category = getArguments().getString("Category");
        TeamMember = getArguments().getString("TeamMember");
        Location = getArguments().getString("Location");
        Status = getArguments().getString("Status");
        Priority = getArguments().getString("Priority");
        Tcontroller = new TaskController(getActivity());
        descText = (TextView)rootView.findViewById(R.id.taskDescription_taskdialog);
        ctgryText = (TextView)rootView.findViewById(R.id.taskCategory_taskdialog);
        timeText = (TextView)rootView.findViewById(R.id.taskDueTime_taskdialog);
        memberText = (TextView)rootView.findViewById(R.id.taskMember_taskdialog);
        locationText = (TextView)rootView.findViewById(R.id.taskLocation_taskdialog);
        priorityText = (TextView)rootView.findViewById(R.id.taskPriority_taskdialog);
        statusSpinner = (Spinner)rootView.findViewById(R.id.taskdialog_statusSpinner);
        update = (Button) rootView.findViewById(R.id.updateTaskBtn);
        //Set current status as default show in spinner
        switch(Status)
        {
            case "Waiting":
                statusSpinner.setSelection(0);
                break;
            case "In Progress":
                statusSpinner.setSelection(1);
                break;
            case "Done":
                statusSpinner.setSelection(2);
                break;
        }
        statusSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Updating Task");

        return rootView;

    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        descText.setText(Description);
        ctgryText.setText(Category);
        timeText.setText(DueTime);
        memberText.setText(TeamMember);
        locationText.setText(Location);
        priorityText.setText(Priority);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUpdateClicked();
            }
        });
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,
                        CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });
        downloadBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                progressDialog = ProgressDialog.show(getActivity(), "",
                        "Downloading Image...", true);
                // Locate the class table named "ImageUpload" in Parse
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
                        "ImageUpload");
                query.whereEqualTo("ImageName", TeamMember + Description);
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null)
                            for (ParseObject obj : objects) {
                                // Locate the column named "ImageName" and set
                                // the string
                                ParseFile fileObject = (ParseFile) obj.get("ImageFile");
                                fileObject.getDataInBackground(new GetDataCallback() {
                                    public void done(byte[] data, ParseException e) {
                                        if (e == null) {
                                            Log.d("test", "We've got data in data.");
                                            // Decode the Byte[] into
                                            // Bitmap
                                            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                                            // Get the ImageView from
                                            // main.xml
                                            ImageView image = (ImageView) view.findViewById(R.id.image);
                                            // Set the Bitmap into the
                                            // ImageView
                                            image.setImageBitmap(bmp);
                                            // Close progress dialog
                                            progressDialog.dismiss();
                                        } else {
                                            Log.d("test",
                                                    "There was a problem downloading the data.");
                                        }
                                    }
                                });
                            }
                        else
                        {
                            Toast err = Toast.makeText(getContext(),"Unable to get data from server",Toast.LENGTH_LONG);
                            err.show();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                progressBar.setVisibility(View.VISIBLE);
                Bitmap bmp = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                // Upload picture to Parse
                ParseFile file = new ParseFile("Image.png", byteArray);
                file.saveInBackground(new SaveCallback() {
                    public void done(ParseException e) {
                        // Handle success or failure here ...
                        Log.d("parse", "Done uploadFIle");
                    }
                }, new ProgressCallback() {
                    public void done(Integer percentDone) {
                        // Update your progress spinner here. percentDone will be between 0 and 100.
                        progressBar.setProgress(percentDone);
                    }
                });
                ParseObject imgupload = new ParseObject("ImageUpload");
                // Create a column named "ImageName" and set the string
                imgupload.put("ImageName", TeamMember+Description);
                // Create a column named "ImageFile" and insert the image
                imgupload.put("ImageFile", file);
                // Create the class and the columns
                imgupload.saveEventually();
                onUpdateClicked();
            }
        }
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
                        task.put("Status",String.valueOf(statusSpinner.getSelectedItem()));
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
                else
                {
                    Toast err = Toast.makeText(getContext(),"Unable to get update data in server",Toast.LENGTH_LONG);
                    err.show();
                }
            }
        },id,Description,TeamMember,"Task_status",String.valueOf(statusSpinner.getSelectedItem()));
    }

    private class CustomOnItemSelectedListener implements android.widget.AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
            if(pos  == 2) {
                if (Status.equals("Done")) {
                    update.setVisibility(view.INVISIBLE);
                    cameraBtn.setVisibility(view.INVISIBLE);
                    downloadBtn.setVisibility(view.VISIBLE);

                }
                else { //Just changed to Done from in Progress
                    cameraBtn.setVisibility(view.VISIBLE);
                    update.setVisibility(view.INVISIBLE);
                }
            }
            else{
                update.setVisibility(view.VISIBLE);
                cameraBtn.setVisibility(view.INVISIBLE);
                downloadBtn.setVisibility(view.INVISIBLE);
                progressBar.setVisibility(view.INVISIBLE);
            }
        }
        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }
    }
}
