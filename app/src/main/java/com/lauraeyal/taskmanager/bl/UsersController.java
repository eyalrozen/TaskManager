package com.lauraeyal.taskmanager.bl;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.lauraeyal.taskmanager.common.*;
import com.lauraeyal.taskmanager.dal.*;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.List;


public class UsersController implements IUsersController
{
	private List<OnDataSourceChangeListener> dataSourceChangedListenrs = new ArrayList<OnDataSourceChangeListener>();
	IDataAcces dao;
	Context context;
	public UsersController(Context context) {
		dao = DAO.getInstance(context.getApplicationContext());
		this.context = context;
	}

	public boolean isLoggedIn() {
		SharedPreferences prefs = context.getSharedPreferences(AppConst.SharedPrefsName, Context.MODE_PRIVATE);
		if (prefs != null) {
			//If AppConst.SharedPrefs_IsLogin then it return true, the default is false
			return prefs.getBoolean(AppConst.SharedPrefs_IsLogin, false);
		}
		return false;
	}

	public List<User> GetUsersList()
	{
		return dao.GetUserList();
	}

	public void UpdateUsersTable(List<ParseUser> pUsers){
		List<User> updatedUserList = new ArrayList<User>();
		for(ParseUser pUser : pUsers){
			User newUser = new User();
			newUser.setUserName(pUser.getUsername());
			newUser.setPassword(pUser.getString("Phone"));
			newUser.setPhoneNumber(pUser.getString("Phone"));
			newUser.setMailSent(pUser.getInt("MailSend"));
			newUser.setPermission(pUser.getInt("isAdmin"));
			newUser.setTeamName(pUser.getString("Team"));
			updatedUserList.add(newUser);
		}
		dao.UpdateUsersTable(updatedUserList);
		invokeDataSourceChanged();
	}

	public void SyncParseUsers(FindCallback<ParseUser> callback){
		dao.SyncParseUsers(callback);
	}

	public void AddUser(User newUser , SignUpCallback callback)
	{
		User usr;
		usr = dao.AddUser(newUser,callback);
		invokeDataSourceChanged();
	}

	public User GetUser(String userName,String password,String phoneNumber)
	{
		List<User> updatedList = dao.GetUserList();
		for (User t:updatedList)
		{
			if(t.getUserName() == userName && t.getPassword()==password && t.getPhoneNumber()==phoneNumber)
				return t;
		}
			return null;
	}

	public void registerOnDataSourceChanged(OnDataSourceChangeListener listener)
	{
		if(listener!=null)
			dataSourceChangedListenrs.add(listener);
	}
	public void unRegisterOnDataSourceChanged(OnDataSourceChangeListener listener)
	{
		if(listener!=null)
			dataSourceChangedListenrs.remove(listener);
	}
	public void invokeDataSourceChanged()
	{
		for (OnDataSourceChangeListener listener : dataSourceChangedListenrs) {
			listener.DataSourceChanged();
		}
	}

	public String GetTeamName()
	{
		return dao.GetTeamName();
	}

	public void UpdateUserField(String fieldName,int numVal,String strVal,int userID)
	{
		dao.UpdateUserField(fieldName,numVal,strVal,userID);
	}

	@Override
	public void SyncTeamName() {
		dao.SyncTeamName();
	}
	public void DeleteUser(String userMail,LogInCallback callback)
	{
		dao.DeleteUser(userMail,callback);
	}
}
