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
    public String GetTeamName();
    List<TaskItem> GetAllTaskList();
    List<TaskItem> GetWaitingTaskList();
    List<TaskItem> SortAllTasksByStatus();
    List<TaskItem> SortAllTasksByPriority();
    List<TaskItem> SortWaitingTasksByPriority();
    void createAlarm(String taskDescription,String teamMember);
    void AddTask(TaskItem task, SaveCallback callback);
    void UpdateTask( FindCallback<ParseObject> callback , TaskItem task,String column,int UpdatedValue);
    void UpdateTask(FindCallback<ParseObject> callback , int taskID,String Description,String teamMember,String column,String UpdatedValue);
}
