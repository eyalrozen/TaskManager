package com.lauraeyal.taskmanager.bl;

import android.content.Context;

import com.lauraeyal.taskmanager.common.*;
import com.parse.ParseUser;

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
    void AddTask(TaskItem task);
    void createAlarm(String message, int secondsFromNow);
    void removeAlarm(Context context, int alarmID);


}
