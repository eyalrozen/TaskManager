package com.lauraeyal.taskmanager.bl;

import android.content.Context;

import com.lauraeyal.taskmanager.common.*;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

/**
 * Created by Eyal on 11/7/2015.
 */
public interface ITaskController
{
    List<TaskItem> GetAllTaskList();
    void SyncAllTaskList(ParseUser user);
    void SyncWaitingTaskList(ParseUser user);
    List<TaskItem> GetWaitingTaskList();
    void AddTask(TaskItem task, SaveCallback callback);
    void createAlarm(String message, int secondsFromNow);
    void removeAlarm(Context context, int alarmID);
    void GetList(FindCallback<ParseObject> callback);


}
