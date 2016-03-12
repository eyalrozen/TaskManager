package com.lauraeyal.taskmanager;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lauraeyal.taskmanager.activities.TasksActivity;
import com.lauraeyal.taskmanager.bl.TaskAdapter;
import com.lauraeyal.taskmanager.bl.TaskController;
import com.lauraeyal.taskmanager.common.OnDataSourceChangeListener;
import com.lauraeyal.taskmanager.common.TaskItem;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eyal on 1/2/2016.
 */
public class ManagerATasksFragment extends Fragment implements OnDataSourceChangeListener,MyItemClickListener,MyItemLongClickListener {

    Button update,downloadBtn;
    ImageButton cameraBtn;
    ImageView imageTask;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1888;
    private TextView descText ,ctgryText,timeText,memberText,locationText,statusText,priorityText,ApprovleText;
    String Description,DueTime,Category,TeamMember,Location,Status,Priority;
    int id;
    private Spinner statusSpinner;
    private RecyclerView mRecyclerView;
    private TaskAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private TaskController controller;
    TextView noTasksText;
    SwipeRefreshLayout mSwipeRefreshLayout;
    List<TaskItem> ParseTaskList = new ArrayList<TaskItem>();
    public ProgressDialog progressDialog;
    public ManagerATasksFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_one, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycle_view);
        //create the controller.
        Context context = getActivity();
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        noTasksText = (TextView) rootView.findViewById(R.id.notask);
        progressDialog.setMessage("Loading Tasks...");
        controller = new TaskController(getActivity());
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        controller.registerOnDataSourceChanged(this);
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);
        progressDialog.show();
        controller.GetParseTaskList(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject task : objects) {
                        TaskItem f = new TaskItem();
                        f.setCategory(task.getString("Category"));
                        f.SetLocation(task.getString("Location"));
                        f.SetDescription(task.getString("Description"));
                        f.SetDueTime(task.getString("DueTime"));
                        f.SetTeamMemebr(task.getString("TeamMember"));
                        f.SetPriority(task.getString("Priority"));
                        f.SetTaskApprovle(task.getInt("isApprovle"));
                        f.SetTaskStatus(task.getString("Status"));
                        ParseTaskList.add(f);
                    }
                    controller.SyncParseTaskList(ParseTaskList);
                    mAdapter = new TaskAdapter(controller.GetAllTaskList());
                    ContinueInit();
                }
                else
                {
                    mAdapter = new TaskAdapter(controller.GetAllTaskList());
                    ContinueInit();
                }
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ((TasksActivity)getActivity()).onRefreshClicked();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        // Inflate the layout for this fragment
        return rootView;
    }

    void ContinueInit (){
        if(controller.GetAllTaskList().size()>0)
            noTasksText.setVisibility(View.GONE);
        else
            noTasksText.setVisibility(View.VISIBLE);
        progressDialog.dismiss();
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemLongClickListener(this);
    }

    public void OnRefreshClicked()
    {

        controller.invokeDataSourceChanged();
        mAdapter.notifyDataSetChanged();
        if(controller.GetAllTaskList().size()>0)
            noTasksText.setVisibility(View.GONE);
        else
            noTasksText.setVisibility(View.VISIBLE);
        progressDialog.dismiss();
    }
    public void OnSortByDueClicked()
    {
        mAdapter.UpdateDataSource(controller.GetAllTaskList());
        mAdapter.notifyDataSetChanged();
        // mAdapter = new TaskAdapter(controller.GetAllTaskList());

    }

    public void OnSortByPriorityClicked()
    {
        mAdapter.UpdateDataSource(controller.SortAllTasksByPriority());
        mAdapter.notifyDataSetChanged();
    }

    public void OnSortByStatusClicked()
    {
        mAdapter.UpdateDataSource(controller.SortAllTasksByStatus());
        mAdapter.notifyDataSetChanged();
    }

    public void StartProgressDialog()
    {
        progressDialog.show();
    }

    public void ParseError()
    {
        progressDialog.dismiss();
        Toast err = Toast.makeText(getContext(),"Unable to get data from server",Toast.LENGTH_LONG);
        err.show();
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void onItemClick(final View view, int postion) {
        DialogFragment taskF = new TaskViewDialog();
        final TaskItem selectedTask = controller.GetAllTaskList().get(postion);
        if(selectedTask.GetTaskApprovle() == -1 && ParseUser.getCurrentUser().getInt("isAdmin") == 1) {
            DialogFragment rejectedTask = new RejectTaskViewDialog();
            final Bundle taskArgs = new Bundle();
            taskArgs.putInt("ID",(int)selectedTask.getId());
            taskArgs.putString("Description", selectedTask.GetDescription());
            taskArgs.putString("DueTime", selectedTask.GetDueTime());
            taskArgs.putString("Category", selectedTask.getCategory());
            taskArgs.putString("TeamMember", selectedTask.get_teamMemebr());
            taskArgs.putString("Location", selectedTask.GetLocation());
            taskArgs.putString("Status", selectedTask.GetTaskStatus());
            taskArgs.putString("Priority", selectedTask.GetPriority());
            rejectedTask.setArguments(taskArgs);
            rejectedTask.show(((Activity) view.getContext()).getFragmentManager(), "RejectTask");
        }
        else if(selectedTask.GetTaskApprovle() == 0 && ParseUser.getCurrentUser().getInt("isAdmin") == 0) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
            alertDialogBuilder.setTitle("Task Approval");
            alertDialogBuilder.setMessage("Do you accept the task "+selectedTask.GetDescription()+"?").setCancelable(true)
                    .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, int id) {
                            TasksActivity.newTasksList.remove(selectedTask.GetDescription());
                            controller.UpdateTask(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> objects, ParseException e) {
                                    if (e == null) {
                                        for (ParseObject taskobj : objects) {
                                            taskobj.put("isNew",false);
                                            taskobj.put("isApprovle", 1);
                                            taskobj.saveInBackground();
                                        }
                                        progressDialog.dismiss();
                                        dialog.cancel();
                                        controller.invokeDataSourceChanged();
                                        ((TasksActivity)getActivity()).onRefreshClicked();
                                    }
                                }
                            }, selectedTask, "Task_approvle",1);
                            progressDialog.setMessage("Update task info..");
                            progressDialog.show();
                        }
                    })
                    .setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, int id) {
                            TasksActivity.newTasksList.remove(selectedTask.GetDescription());
                            controller.UpdateTask(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> objects, ParseException e) {
                                    if(e==null)
                                    {
                                        for (ParseObject taskobj : objects)
                                        {
                                            taskobj.put("isNew",false);
                                            taskobj.put("isApprovle", -1);
                                            taskobj.saveInBackground();
                                        }
                                        progressDialog.dismiss();
                                        dialog.cancel();
                                        controller.invokeDataSourceChanged();
                                        ((TasksActivity)getActivity()).onRefreshClicked();
                                    }
                                }
                            },selectedTask,"Task_approvle",-1);
                            progressDialog.setMessage("Update task info..");
                            progressDialog.show();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            final AlertDialog dialog = builder.create();
            final View dialogView = dialog.getLayoutInflater().inflate(R.layout.taskdialog_fragment, null);
            dialog.setView(dialogView);
            cameraBtn = (ImageButton)dialogView.findViewById(R.id.cameraBtn);
            downloadBtn = (Button)dialogView.findViewById(R.id.downloadBtn);
            descText = (TextView)dialogView.findViewById(R.id.taskDescription_taskdialog);
            ctgryText = (TextView)dialogView.findViewById(R.id.taskCategory_taskdialog);
            timeText = (TextView)dialogView.findViewById(R.id.taskDueTime_taskdialog);
            memberText = (TextView)dialogView.findViewById(R.id.taskMember_taskdialog);
            locationText = (TextView)dialogView.findViewById(R.id.taskLocation_taskdialog);
            priorityText = (TextView)dialogView.findViewById(R.id.taskPriority_taskdialog);
            statusSpinner = (Spinner)dialogView.findViewById(R.id.taskdialog_statusSpinner);
            update = (Button) dialogView.findViewById(R.id.updateTaskBtn);
            Description = selectedTask.GetDescription();
            DueTime = selectedTask.GetDueTime();
            Category = selectedTask.getCategory();
            TeamMember = selectedTask.get_teamMemebr();
            Location = selectedTask.GetLocation();
            Status = selectedTask.GetTaskStatus();
            Priority = selectedTask.GetPriority();
            id = (int)selectedTask.getId();
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
            descText.setText(Description);
            ctgryText.setText(Category);
            timeText.setText(DueTime);
            memberText.setText(TeamMember);
            locationText.setText(Location);
            priorityText.setText(Priority);
            statusSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
            imageTask = (ImageView) dialogView.findViewById(R.id.Taskimage);
            imageTask.setVisibility(View.GONE);
            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    onUpdateClicked();
                }
            });
            cameraBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    onCameraClicked();
                    //getDialog().dismiss();
                    //((TasksActivity)getActivity()).onCameraClicked(id, TeamMember,Description);
                    // Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    // getActivity().startActivityForResult(intent,CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
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
                                                imageTask = (ImageView) dialogView.findViewById(R.id.Taskimage);
                                                // Set the Bitmap into the
                                                // ImageView
                                                imageTask.setImageBitmap(bmp);
                                                imageTask.setVisibility(View.VISIBLE);
                                                // Close progress dialog
                                                progressDialog.dismiss();
                                            } else {
                                                Log.d("test",
                                                        "There was a problem downloading the data.");
                                            }
                                        }
                                    });
                                }
                            else {
                           /* Toast err = Toast.makeText(,"Unable to get data from server",Toast.LENGTH_LONG);
                            err.show();*/
                            }
                        }
                    });
                }
            });
            dialog.show();
        }
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
            }
        }
        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }
    }

    public void onUpdateClicked()
    {
        progressDialog.show();
        controller.UpdateTask(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (final ParseObject task : objects) {
                        task.put("Status", String.valueOf(statusSpinner.getSelectedItem()));
                        task.saveEventually(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    progressDialog.dismiss();
                                    try {
                                        Snackbar.make(getView(), "Changes updated successfully", Snackbar.LENGTH_SHORT).setAction("action", null).show();
                                        // Toast.makeText(getActivity().getApplicationContext(), "Changes updated successfully", Toast.LENGTH_LONG);
                                        ((TasksActivity) getActivity()).onRefreshClicked();
                                    } catch (Exception ex) {
                                        Log.i("err", "error");
                                    }
                                }
                            }
                        });
                    }
                } else {
                    Toast err = Toast.makeText(getContext(), "Unable to get update data in server", Toast.LENGTH_LONG);
                    err.show();
                }
            }
        }, id, Description, TeamMember, "Task_status", String.valueOf(statusSpinner.getSelectedItem()));
    }

    public void onCameraClicked()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getActivity().getPackageManager())!=null) {
            TasksActivity.currentFrag="frag2";
            getActivity().startActivityForResult(intent, 1888);
        }
    }

    public void onItemLongClick(final View view, int postion) {
       /* User usr = controller.GetUsersList().get(postion);
        if(usr != null) {*/
        final TaskItem selectedTask = controller.GetAllTaskList().get(postion);
        if(ParseUser.getCurrentUser().getInt("isAdmin") == 1){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
            alertDialogBuilder.setTitle("Warning! ");
            alertDialogBuilder
                    .setMessage("Are you sure you want to delete task \n" + selectedTask.GetDescription() + "?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            progressDialog.setMessage("In Progress..");
                            progressDialog.show();
                            controller.RemoveTask(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> objects, ParseException e) {
                                    if(e==null)
                                    {
                                        for(ParseObject tsk : objects)
                                            tsk.deleteInBackground(new DeleteCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if(e==null) {
                                                        progressDialog.dismiss();
                                                        ((TasksActivity)getActivity()).onRefreshClicked();
                                                        Snackbar.make(view, "Task removed successfully!", Snackbar.LENGTH_LONG).setAction("action", null).show();
                                                    }
                                                    else
                                                        Snackbar.make(view, "Unable to delete task from server", Snackbar.LENGTH_LONG).setAction("action", null).show();
                                                }
                                            });
                                    }
                                }
                            },selectedTask);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, just close
                            // the dialog box and do nothing
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        }
    }
    @Override
    public void DataSourceChanged() {
        if (mAdapter != null) {
            mAdapter.UpdateDataSource(controller.GetAllTaskList());
            mAdapter.notifyDataSetChanged();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 1888) {
            //    if (resultCode == Activity.RESULT_OK) {
             progressDialog.setMessage("Uploading image..");
            progressDialog.show();
            Bitmap bmp = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 140, stream);
            byte[] byteArray = stream.toByteArray();
            // Upload picture to Parse
            final ParseFile file = new ParseFile("Image.png", byteArray);
            file.saveInBackground(new SaveCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        // Handle success or failure here ...
                        Log.d("parse", "Done uploadFIle");
                        ParseObject imgupload = new ParseObject("ImageUpload");
                        // Create a column named "ImageName" and set the string
                        imgupload.put("ImageName", TeamMember + Description);
                        // Create a column named "ImageFile" and insert the image
                        imgupload.put("ImageFile", file);
                        // Create the class and the columns
                        imgupload.saveEventually(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                // Toast.makeText(getContext(),"haha",Toast.LENGTH_LONG).show();
                                onUpdateClicked();
                            }
                        });
                    }
                }
            }, new ProgressCallback() {
                public void done(Integer percentDone) {
                    // Update your progress spinner here. percentDone will be between 0 and 100.
                }
            });

            //}
        }
    }

    public void uploadPictureToParse(Bitmap bmp){
        progressDialog.setMessage("Uploading image..");
        progressDialog.show();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        // Upload picture to Parse
        final ParseFile file = new ParseFile("Image.png", byteArray);
        file.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if(e==null){
                    // Handle success or failure here ...
                    Log.d("parse", "Done uploadFIle");
                    ParseObject imgupload = new ParseObject("ImageUpload");
                    // Create a column named "ImageName" and set the string
                    imgupload.put("ImageName", TeamMember + Description);
                    // Create a column named "ImageFile" and insert the image
                    imgupload.put("ImageFile", file);
                    // Create the class and the columns
                    imgupload.saveEventually(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            onUpdateClicked();
                        }
                    });
                }
            }
        }, new ProgressCallback() {
            public void done(Integer percentDone) {
                // Update your progress spinner here. percentDone will be between 0 and 100.
            }
        });

        //}
    }
}
