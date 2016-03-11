package com.lauraeyal.taskmanager.bl;

import com.lauraeyal.taskmanager.common.User;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Eyal on 1/2/2016.
 */
public interface IUsersController {
    List<User> GetUsersList();
    void SyncTeamName();
    void SyncParseUsers(FindCallback<ParseUser> callback);
    void UpdateUsersTable(List<ParseUser> pUsers);
    void UpdateUserField(String fieldName,int numVal,String strVal,int userID);
    void DeleteUser(String userMail,LogInCallback callback);
}
