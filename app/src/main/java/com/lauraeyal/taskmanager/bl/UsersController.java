package com.lauraeyal.taskmanager.bl;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.lauraeyal.taskmanager.common.*;
import com.lauraeyal.taskmanager.dal.*;
import com.parse.FindCallback;
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

	/*public User GetUserFromServer(final String userName, final String password, final String phoneNumber)
	{

		ParseUser.logInInBackground(userName, password, new LogInCallback() {
			@Override
			public void done(ParseUser user, ParseException e) {
				if(user !=null) try {
					ParseQuery<ParseUser> query = ParseUser.getQuery();
					query.whereEqualTo("username", userName);
					query.findInBackground(new FindCallback<ParseUser>() {
						@Override
						public void done(List<ParseUser> objects, ParseException e) {
							String team = "";
							for (ParseUser t : objects) {
								team = t.get("Team").toString();
							}
							User u = AddUser(userName, password, phoneNumber, 1, 1,team );
							return u;
						}
					});

					Toast.makeText(context, "Logged In!", Toast.LENGTH_LONG);


				} catch (Exception ea) {
				}

				else
					Toast.makeText(context,"Username / Password is not correct", Toast.LENGTH_LONG);

			}
		});
		/*List<User> updatedList = dao.GetUserList();
		for (User t:updatedList)
		{
			if(t.getUserName() == userName && t.getPassword()==password && t.getPhoneNumber()==phoneNumber)
				return t;
		}
		return null;
	}*/

	public boolean isListEmpty()
	{
		try {
			List<User> updatedList = dao.GetUserList();
			if (updatedList.size() == 0)
				return true;
			else
				return false;
		}
		catch (Exception e)
		{return true;}
	}

	/*public void setLogedIn(User user)
	{
		if(user!=null)
		{
			//editor is builder - add data to db
			SharedPreferences prefs = context.getSharedPreferences(AppConst.SharedPrefsName, 0);
			if(prefs!=null)
			{
				Editor editor = prefs.edit();
				editor.putBoolean(AppConst.SharedPrefs_IsLogin, true);
				editor.putString(AppConst.SharedPrefs_UserName, user.getUserName());
				editor.commit();
				//in 1 line
				prefs.edit().putBoolean(AppConst.SharedPrefs_IsLogin, true).putString(AppConst.SharedPrefs_UserName, user.getUserName()).commit();
			}
		}
	}*/

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


	@Override
	public void SyncTeamName() {
		dao.SyncTeamName();
	}
}
