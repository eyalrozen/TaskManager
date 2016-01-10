package com.lauraeyal.taskmanager;

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
import com.lauraeyal.taskmanager.common.User;
import com.parse.ParseUser;

/**
 * Created by Eyal on 1/2/2016.
 */
public class ManagerWTasksFragment extends Fragment implements OnDataSourceChangeListener,MyItemClickListener,MyItemLongClickListener {

    private RecyclerView mRecyclerView;
    private TaskAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private TaskController controller;

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

        View rootView = inflater.inflate(R.layout.fragment_one, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycle_view);
        //create the controller.
        Context context = getActivity();
        controller = new TaskController(getActivity());
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        controller.registerOnDataSourceChanged(this);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        ParseUser currentUser = ParseUser.getCurrentUser();
        mAdapter = new TaskAdapter(controller.GetWaitingTaskList());
        mRecyclerView.setAdapter(mAdapter);
        // Inflate the layout for this fragment

        return rootView;
    }

    public void onItemClick(View view, int postion) {
        /*User usr = controller.GetUsersList().get(postion);

        if(usr != null){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Warning! ");
            alertDialogBuilder
                    .setMessage("Are you sure you want to delete \n" + usr.getUserName() + "?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, close
                            // current activity
                            finish();
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
*/
            Snackbar.make(view,"Short Click ",Snackbar.LENGTH_LONG).setAction("action",null).show();

    }



    public void onItemLongClick(View view, int postion) {
       /* User usr = controller.GetUsersList().get(postion);
        if(usr != null) {*/
            Snackbar.make(view, "Long click " , Snackbar.LENGTH_LONG).setAction("action", null).show();

    }
    @Override
    public void DataSourceChanged() {
        if (mAdapter != null) {
            mAdapter.UpdateDataSource(controller.GetWaitingTaskList());
            mAdapter.notifyDataSetChanged();
        }

    }

}
