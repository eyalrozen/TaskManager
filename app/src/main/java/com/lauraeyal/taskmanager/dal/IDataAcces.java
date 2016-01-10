package com.lauraeyal.taskmanager.dal;

import com.lauraeyal.taskmanager.common.*;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Eyal on 11/7/2015.
 */
public interface IDataAcces {
    User AddUser(User usr);
    String GetTeamName();
    List<TaskItem> GetTaskList();
    List<TaskItem> GetAllTaskList();
    List<TaskItem> GetWaitingTaskList();
    void SyncWaitingTaskList(ParseUser user);
    void SyncAllTaskList(ParseUser user);
    void SyncTeamName();
    List<User> GetUserList();
    void RemoveTask(TaskItem task);
    TaskItem AddTask(TaskItem task);
}
