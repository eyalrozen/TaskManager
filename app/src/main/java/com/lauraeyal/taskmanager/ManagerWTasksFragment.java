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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lauraeyal.taskmanager.activities.TasksActivity;
import com.lauraeyal.taskmanager.bl.TaskAdapter;
import com.lauraeyal.taskmanager.bl.TaskController;
import com.lauraeyal.taskmanager.common.OnDataSourceChangeListener;
import com.lauraeyal.taskmanager.common.TaskItem;
import com.lauraeyal.taskmanager.common.User;
import com.parse.FindCallback;
import com.parse.GetCallback;
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
    SwipeRefreshLayout mSwipeRefreshLayout;
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
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
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
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ((TasksActivity)getActivity()).onRefreshClicked();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
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

    public void onItemClick(final View view, int postion) {

        DialogFragment taskF = new TaskViewDialog();
        final TaskItem selectedTask = controller.GetWaitingTaskList().get(postion);
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
            alertDialogBuilder.setTitle("Task Approvle");
            alertDialogBuilder.setMessage("Do you accept the task ?").setCancelable(false)
                    .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, int id) {
                            controller.UpdateTask(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> objects, ParseException e) {
                                  if (e == null) {
                                      for (ParseObject taskobj : objects) {
                                          taskobj.put("isApprovle", 1);
                                          taskobj.saveInBackground();
                                      }
                                      progressDialog.dismiss();
                                      dialog.cancel();
                                      controller.invokeDataSourceChanged();
                                  }
                                }
                            }, selectedTask, "Task_approvle",1);
                            progressDialog.setMessage("Update task info..");
                            progressDialog.show();
                        }
                    })
                    .setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, int id) {
                            controller.UpdateTask(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> objects, ParseException e) {
                                    if(e==null)
                                    {
                                        for (ParseObject taskobj : objects)
                                        {
                                            taskobj.put("isApprovle", -1);
                                            taskobj.saveInBackground();
                                        }
                                        progressDialog.dismiss();
                                        dialog.cancel();
                                        controller.invokeDataSourceChanged();
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
            final Bundle taskArgs = new Bundle();
            taskArgs.putInt("ID",(int)selectedTask.getId());
            taskArgs.putString("Description", selectedTask.GetDescription());
            taskArgs.putString("DueTime", selectedTask.GetDueTime());
            taskArgs.putString("Category", selectedTask.getCategory());
            taskArgs.putString("TeamMember", selectedTask.get_teamMemebr());
            taskArgs.putString("Location", selectedTask.GetLocation());
            taskArgs.putString("Status", selectedTask.GetTaskStatus());
            taskArgs.putString("Priority", selectedTask.GetPriority());
            taskF.setArguments(taskArgs);
            taskF.show(((Activity) view.getContext()).getFragmentManager(), "Task");
        }
    }

    public void onItemLongClick(View view, int postion) {
       /* User usr = controller.GetUsersList().get(postion);
        if(usr != null) {*/
            Snackbar.make(view, "Long click " , Snackbar.LENGTH_LONG).setAction("action", null).show();
    }
    public void OnRefreshClicked()
    {
        controller.invokeDataSourceChanged();
        progressDialog.dismiss();
    }

    public void OnSortByPriorityClicked()
    {
        mAdapter.UpdateDataSource(controller.SortWaitingTasksByPriority());
        mAdapter.notifyDataSetChanged();
    }

    public void OnSortByDueClicked()
    {
        mAdapter.UpdateDataSource(controller.GetWaitingTaskList());
        mAdapter.notifyDataSetChanged();
    }

    public void StartProgressDialog()
    {
        progressDialog.show();
    }
    @Override
    public void DataSourceChanged() {
        if (mAdapter != null) {
            mAdapter.UpdateDataSource(controller.GetWaitingTaskList());
            mAdapter.notifyDataSetChanged();
        }
    }

}
