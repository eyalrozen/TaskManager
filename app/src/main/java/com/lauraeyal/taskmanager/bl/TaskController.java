package com.lauraeyal.taskmanager.bl;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.lauraeyal.taskmanager.activities.*;
import com.lauraeyal.taskmanager.common.*;
import com.lauraeyal.taskmanager.dal.*;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by Eyal on 11/7/2015.
 */
public class TaskController implements ITaskController {
    private IDataAcces dao;
    private Context context;
    //observers list.
    private List<OnDataSourceChangeListener> dataSourceChangedListenrs = new ArrayList<OnDataSourceChangeListener>();
    public TaskController(Context context)
    {
        this.context = context;
        dao = DAO.getInstance(context.getApplicationContext());
    }
    private List<String> descriptionList;


    public void GetParseTaskList(FindCallback<ParseObject> callback)
    {
        dao.GetParseTasksList(callback);
    }

    public void SyncParseTaskList(List<TaskItem> ParseTaskList){
        dao.SyncParseTaskList(ParseTaskList);
    }

    public String GetTeamName()
    {
        return dao.GetTeamName();
    }

    public void SyncTeamName()
    {
        dao.SyncTeamName();
    }
    public List<TaskItem> GetWaitingTaskList() {
        try{
            List<TaskItem> list = dao.GetWaitingTaskList();
            Collections.sort(list, new Comparator<TaskItem>() {
                public int compare(TaskItem obj1, TaskItem obj2) {
                    // TODO Auto-generated method stub
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm");
                    Date date1 = null;
                    Date date2 = null;
                    try {
                        date1 = sdf.parse(obj1.GetDueTime());
                        date2 = sdf.parse(obj2.GetDueTime());
                    } catch (java.text.ParseException e) {
                        e.printStackTrace();
                    }
                    return date1.compareTo(date2);
                }
            });
            return list;
        }
        catch(Exception e)
        {
            return new ArrayList<TaskItem>();
        }
    }

    public List<TaskItem> GetAllTaskList()
    {
        try{
            List<TaskItem> list = dao.GetAllTaskList();
            Collections.sort(list, new Comparator<TaskItem>() {
                public int compare(TaskItem obj1, TaskItem obj2) {
                    // TODO Auto-generated method stub
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm");
                    Date date1 = null;
                    Date date2 = null;
                    try {
                        date1 = sdf.parse(obj1.GetDueTime());
                        date2 = sdf.parse(obj2.GetDueTime());
                    } catch (java.text.ParseException e) {
                        e.printStackTrace();
                    }
                    return date1.compareTo(date2);
                }
            });
            return list;
        }
        catch(Exception e)
        {
            return new ArrayList<TaskItem>();
        }
    }

    public List<TaskItem> SortAllTasksByStatus(){
        try{
            List<TaskItem> list = dao.GetAllTaskList();
            Collections.sort(list, new Comparator<TaskItem>() {
                public int compare(TaskItem obj1, TaskItem obj2) {
                    // TODO Auto-generated method stub
                    return obj1.GetTaskStatus().compareToIgnoreCase(obj2.GetTaskStatus());
                }
            });
            return list;
        }
        catch(Exception e)
        {
            return new ArrayList<TaskItem>();
        }
    }

    public List<TaskItem> SortAllTasksByPriority(){
        try{
            List<TaskItem> list = dao.GetAllTaskList();
            Collections.sort(list, new Comparator<TaskItem>() {
                public int compare(TaskItem obj1, TaskItem obj2) {
                    // TODO Auto-generated method stub
                    return obj1.GetPriority().compareToIgnoreCase(obj2.GetPriority());
                }
            });
            return list;
        }
        catch(Exception e)
        {
            return new ArrayList<TaskItem>();
        }
    }

    public List<TaskItem> SortWaitingTasksByPriority(){
        try{
            List<TaskItem> list = dao.GetWaitingTaskList();
            Collections.sort(list, new Comparator<TaskItem>() {
                public int compare(TaskItem obj1, TaskItem obj2) {
                    // TODO Auto-generated method stub
                    return obj1.GetPriority().compareToIgnoreCase(obj2.GetPriority());
                }
            });
            return list;
        }
        catch(Exception e)
        {
            return new ArrayList<TaskItem>();
        }
    }

    public void AddTask(TaskItem task,SaveCallback callback)
    {
        try {
            //add the friend to the data base and use the returned friend and add it ti the local cache.
            //the friend that returned from the DAO contain the id of the entity.
            TaskItem retTask = dao.AddTask(task,callback);
            if(retTask == null) return;
            //update what ever it will be.
           // invokeDataSourceChanged();
        } catch (Exception e) {
            Log.e("TaskController", e.getMessage());
        }
    }

    public void UpdateTask( FindCallback<ParseObject> callback ,TaskItem task,String column,int UpdatedValue)
    {
        dao.UpdateTask(callback,task,column,UpdatedValue);
    }

    public void UpdateTask(FindCallback<ParseObject> callback , int taskID,String Description,String teamMember,String column,String UpdatedValue)
    {
        dao.UpdateTask(callback,taskID,Description,teamMember,column,UpdatedValue);
    }

    public void registerOnDataSourceChanged(OnDataSourceChangeListener listener)
    {
        if(listener!=null)
            dataSourceChangedListenrs.add(listener);
    }
    public void unRegisterOnDataSourceChanged(OnDataSourceChangeListener listener)
    {
        if(listener!=null)
            dataSourceChangedListenrs.remove(listener);
    }
    public void invokeDataSourceChanged()
    {
        for (OnDataSourceChangeListener listener : dataSourceChangedListenrs) {
            listener.DataSourceChanged();
        }
    }

    public void createAlarm(String taskDescription,String teamMember)
    {
        AlarmHelper.setAlarm(context,30);
    }


}
