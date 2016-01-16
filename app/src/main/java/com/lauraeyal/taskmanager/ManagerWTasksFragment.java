package com.lauraeyal.taskmanager;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lauraeyal.taskmanager.bl.TaskAdapter;
import com.lauraeyal.taskmanager.bl.TaskController;
import com.lauraeyal.taskmanager.common.OnDataSourceChangeListener;
import com.lauraeyal.taskmanager.common.TaskItem;
import com.lauraeyal.taskmanager.common.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eyal on 1/2/2016.
 */
public class ManagerWTasksFragment extends Fragment implements OnDataSourceChangeListener,MyItemClickListener,MyItemLongClickListener {

    private RecyclerView mRecyclerView;
    private TaskAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private TaskController controller;
    List<TaskItem> ParseTaskList = new ArrayList<TaskItem>();
    ProgressDialog progressDialog;
    public ManagerWTasksFragment() {
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
        progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading Tasks...");
        progressDialog.show();
        controller = new TaskController(getActivity());
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        controller.registerOnDataSourceChanged(this);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);

        controller.GetParseTaskList(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e==null) {
                    for (ParseObject task : objects) {
                        TaskItem f = new TaskItem();
                        f.setCategory(task.getString("Category"));
                        f.SetLocation(task.getString("Location"));
                        f.SetDescription(task.getString("Description"));
                        f.SetDueTime(task.getString("DueTime"));
                        //task.getParseObject("user")
                        //ParseUser user = (ParseUser) task.getParseObject("user");
                        f.SetTeamMemebr(task.getString("TeamMember"));
                        f.SetPriority(task.getString("Priority"));
                        f.SetTaskApprovle(task.getInt("isApprovle"));
                        f.SetTaskStatus(task.getString("Status"));
                        ParseTaskList.add(f);
                    }
                    controller.SyncParseTaskList(ParseTaskList);
                    mAdapter = new TaskAdapter(controller.GetWaitingTaskList());
                    ContinueInit();
                }
            }
        });

        // Inflate the layout for this fragment

        return rootView;
    }

    void ContinueInit (){
        progressDialog.dismiss();
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemLongClickListener(this);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public void onItemClick(View view, int postion) {
        DialogFragment taskF = new TaskViewDialog();
        TaskItem selectedTask = controller.GetWaitingTaskList().get(postion);
        final Bundle taskArgs = new Bundle();
        taskArgs.putString("Description",selectedTask.GetDescription());
        taskArgs.putString("DueTime",selectedTask.GetDueTime());
        taskArgs.putString("Category",selectedTask.getCategory());
        taskArgs.putString("TeamMember",selectedTask.get_teamMemebr());
        taskArgs.putString("Location", selectedTask.GetLocation());
        taskArgs.putInt("Approvle", selectedTask.GetTaskApprovle());
        taskArgs.putString("Status",selectedTask.GetTaskStatus());
        taskArgs.putString("Priority",selectedTask.GetPriority());
        taskF.setArguments(taskArgs);
        taskF.show(((Activity) view.getContext()).getFragmentManager(),"Task");
        /*
        TaskItem task = controller.GetWaitingTaskList().get(postion);

        if (task != null) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
            alertDialogBuilder.setTitle("Warning! ");
            alertDialogBuilder
                    .setMessage("Are you sure you want to delete ?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, close
                            // current activity

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
            Snackbar.make(view, "Short Click ", Snackbar.LENGTH_LONG).setAction("action", null).show();

        }*/
    }



    public void onItemLongClick(View view, int postion) {
       /* User usr = controller.GetUsersList().get(postion);
        if(usr != null) {*/
            Snackbar.make(view, "Long click " , Snackbar.LENGTH_LONG).setAction("action", null).show();

    }
    @Override
    public void DataSourceChanged() {
        if (mAdapter != null) {
           // controller.SyncWaitingTaskList(ParseUser.getCurrentUser());
            //controller.SyncAllTaskList(ParseUser.getCurrentUser());
            mAdapter.UpdateDataSource(controller.GetWaitingTaskList());
            mAdapter.notifyDataSetChanged();
        }
    }

}
