package com.lauraeyal.taskmanager.dal;

import com.lauraeyal.taskmanager.common.*;

import java.util.List;

/**
 * Created by Eyal on 11/7/2015.
 */
public interface IDataAcces {
    User AddUser(User usr);
    String GetTeamName();
    List<TaskItem> GetWorkerWaitingTaskList(String username);
    List<TaskItem >GetWorkerTaskList(String username);
    List<TaskItem> GetTaskList();
    List<TaskItem> GetDoneTaskList();
    List<TaskItem> GetWaitingTaskList();
    List<User> GetUserList();
    void RemoveTask(TaskItem task);
    TaskItem AddTask(TaskItem task);
}
