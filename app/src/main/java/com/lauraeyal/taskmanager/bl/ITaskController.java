package com.lauraeyal.taskmanager.bl;

import android.content.Context;

import com.lauraeyal.taskmanager.common.*;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

/**
 * Created by Eyal on 11/7/2015.
 */
public interface ITaskController
{
    void GetParseTaskList(FindCallback<ParseObject> callback);
    void SyncParseTaskList(List<TaskItem> ParseTaskList);
    List<TaskItem> GetAllTaskList();
    List<TaskItem> GetWaitingTaskList();
    void AddTask(TaskItem task, SaveCallback callback);
    void createAlarm(String message, int secondsFromNow);
    void removeAlarm(Context context, int alarmID);
    void UpdateTask( FindCallback<ParseObject> callback , TaskItem task,String column,int UpdatedValue);
    void UpdateTask(FindCallback<ParseObject> callback , int taskID,String Description,String teamMember,String column,String UpdatedValue);
}
