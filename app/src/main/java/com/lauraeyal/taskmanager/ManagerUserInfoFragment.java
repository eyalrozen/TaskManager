package com.lauraeyal.taskmanager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lauraeyal.taskmanager.activities.TeamMembersActivity;
import com.lauraeyal.taskmanager.bl.TaskAdapter;
import com.lauraeyal.taskmanager.bl.TaskController;
import com.lauraeyal.taskmanager.bl.UsersController;
import com.lauraeyal.taskmanager.common.OnDataSourceChangeListener;

/**
 * Created by Eyal on 1/2/2016.
 */
public class ManagerUserInfoFragment extends Fragment {

    private UsersController controller;
    public ManagerUserInfoFragment() {
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
        //create the controller.
        controller = new UsersController(getActivity());
        TeamMembersActivity activity = (TeamMembersActivity) getActivity();
        
        // Inflate the layout for this fragment
        return rootView;
    }



}
