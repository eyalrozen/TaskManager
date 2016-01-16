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
    List<User> GetUserList();
    String GetTeamName();
    void SyncTeamName();
    void GetParseTasksList(FindCallback<ParseObject> callback);
    void SyncParseTaskList(List<TaskItem> parseTaskList);
    List<TaskItem> GetAllTaskList();
    List<TaskItem> GetWaitingTaskList();
    void RemoveTask(TaskItem task);
    TaskItem AddTask(TaskItem task,SaveCallback callback);

}
