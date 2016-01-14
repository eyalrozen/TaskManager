package com.lauraeyal.taskmanager.dal;

import com.lauraeyal.taskmanager.common.*;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.List;

/**
 * Created by Eyal on 11/7/2015.
 */
public interface IDataAcces {
    User AddUser(User usr,SignUpCallback callback);
    void AddUserFromParse(User usr);
    void SyncParseUsers(FindCallback<ParseUser> callback);
    void UpdateUsersTable(List<User> updateUsersList);
    String GetTeamName();
    List<TaskItem> GetTaskList();
    List<TaskItem> GetAllTaskList();
    List<TaskItem> GetWaitingTaskList();
    void GetList(FindCallback<ParseObject> callback);
    void SyncWaitingTaskList(ParseUser user);
    void SyncAllTaskList(ParseUser user);
    void SyncTeamName();
    List<User> GetUserList();
    void RemoveTask(TaskItem task);
    TaskItem AddTask(TaskItem task,SaveCallback callback);

    void SyncWaitingTaskList(List<TaskItem> parseWaitingTaskList);
}
