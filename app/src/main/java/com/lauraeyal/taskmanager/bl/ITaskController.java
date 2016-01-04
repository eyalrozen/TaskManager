package com.lauraeyal.taskmanager.bl;

import android.content.Context;

import com.lauraeyal.taskmanager.common.*;

import java.util.List;

/**
 * Created by Eyal on 11/7/2015.
 */
public interface ITaskController
{
    List<TaskItem> GetWorkerWaitingTaskList(String username);
    List<TaskItem> GetWorkerTaskList(String username);
    List<TaskItem> GetTaskList();
    List<TaskItem> GetWaitingTaskList();
    List<TaskItem> GetDoneTaskList();
    void AddTask(TaskItem task);
    void createAlarm(String message, int secondsFromNow);
    void removeAlarm(Context context, int alarmID);


}
