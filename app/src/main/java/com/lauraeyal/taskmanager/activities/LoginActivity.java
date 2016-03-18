package com.lauraeyal.taskmanager.activities;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.lauraeyal.taskmanager.AnalyticsApplication;
import com.lauraeyal.taskmanager.R;
import com.lauraeyal.taskmanager.bl.*;
import com.lauraeyal.taskmanager.common.*;
import com.lauraeyal.taskmanager.pushNotification.App42GCMReceiver;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.List;

import me.leolin.shortcutbadger.ShortcutBadger;

//Start the app on LoginActivity
public class LoginActivity extends Activity {

	public static String teamName;
	private EditText userNameEditText;
	private EditText passwordEditText;
	private EditText phoneNumberEditText;
	private UsersController controller;
	ProgressDialog progressDialog;
	SharedPreferences sharedpreferences;
	public static boolean isFirstTime=false;
	public static final String MyPREFERENCES = "MyPrefs" ;
	private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
		AnalyticsApplication application = (AnalyticsApplication) getApplication();
		mTracker = application.getDefaultTracker();
		sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
		try {
			Parse.initialize(this);
		}
		catch (Exception e) {
			Log.d("parse", "Parse Already init");
		}
		//Reset badge number
		ShortcutBadger.removeCount(getApplicationContext());
		progressDialog = new ProgressDialog(LoginActivity.this);
		progressDialog.setIndeterminate(true);
		progressDialog.setMessage("Authenticating...");
		final ParseUser currentUser = ParseUser.getCurrentUser();
		if(currentUser != null){
				startTasksActivity();
		}
        controller = new UsersController(this);
		controller.SyncTeamName();
        //ask the controller if the user is logged in.
        //get the useName and password edit text view 
        userNameEditText = (EditText) findViewById(R.id.editTextUserName);
        passwordEditText = (EditText) findViewById(R.id.editTextPassword);
		phoneNumberEditText = (EditText) findViewById(R.id.editTextphoneNumber);
    }

	//Handle login flow - if all fields was field
    public void logInClicked(View v) throws ParseException {
    	//get the password, user name and phone number from the edit text.
    	if(userNameEditText!=null && passwordEditText!=null && phoneNumberEditText!=null)
    	{
			progressDialog.show();
    		String userName  = userNameEditText.getText().toString();
    		String pass = passwordEditText.getText().toString();
			String phoneNumber = phoneNumberEditText.getText().toString();

			//todo spinner
			ParseUser.logInInBackground(userName, pass, new LogInCallback() {
				public void done(ParseUser user, ParseException e) {
					//todo close

					if (user != null) {
						// Hooray! The user is logged in.
						//user.signUpInBackground();
						SharedPreferences.Editor editor = sharedpreferences.edit();
						editor.putBoolean("firstTime", true);
						editor.apply();
						startTasksActivity();
					}
					else {
						ParseQuery<ParseUser> query = ParseUser.getQuery();
						query.whereEqualTo("isAdmin",1);
						query.findInBackground(new FindCallback<ParseUser>() {
							@Override
							public void done(List<ParseUser> userList, ParseException e) {
								if (e == null) {
									if (userList.size()>0) {
										progressDialog.dismiss();
										Toast.makeText(getApplicationContext(),"Not such user in system, please recheck your login details",Toast.LENGTH_LONG).show();
									}
									else { // Should be the admin login 1st time

										Intent teamNameIntent = new Intent(getApplicationContext(), AddTeamActivity.class);
										startActivityForResult(teamNameIntent, 1);
									}
								}
								else { // Should be the admin login 1st time
									progressDialog.dismiss();
									Toast.makeText(getApplicationContext(),"Unable to get data from server!",Toast.LENGTH_LONG).show();
								}
							}
						});
					}
				}
			});
    	}
	}

	@Override
	//Recieve the input text from AddTeamActivity and insert it as teamName
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode) {
			case (1) : {
				progressDialog.show();
				if (resultCode == Activity.RESULT_OK) {
					String tName = data.getStringExtra("teamName");
					teamName = tName;
					String userName  = userNameEditText.getText().toString();
					String pass = passwordEditText.getText().toString();
					String phoneNumber = phoneNumberEditText.getText().toString();
					try {
						User newUser = new User();
						newUser.setUserName(userName);
						newUser.setPassword(pass);
						newUser.setPhoneNumber(phoneNumber);
						newUser.setTeamName(teamName);
						newUser.setPermission(1);
						newUser.setMailSent(1);
						controller.AddUser(newUser, new SignUpCallback() {
							@Override
							public void done(ParseException e) {
								if(e ==null){
									isFirstTime=true;
									startTasksActivity();
								}

								else
									Toast.makeText(getApplicationContext(),"Unable to save data in server! please reInstall the app",Toast.LENGTH_LONG);
							}
						});
						//controller.setLogedIn(u);
					}
					catch(Exception e)
					{}
				}
				break;
			}
		}
	}
	//Load tasks activity after login authenitaction success , set default refresh timer service to 30 minutes.
    public void startTasksActivity()
    {
		int refreshTimer=sharedpreferences.getInt("autoRefresh", 0);
		//Set default refresh timer to 30 Minutes
		if(refreshTimer==0) {
			SharedPreferences.Editor editor = sharedpreferences.edit();
			editor.putInt("autoRefresh", 30);
			editor.apply();
		}
		Intent i = new Intent(getApplicationContext(), TasksActivity.class);
		startActivity(i);
		finish();
    }

	@Override
	protected void onResume() {
		super.onResume();
		mTracker.setScreenName("Login");
		mTracker.send(new HitBuilders.ScreenViewBuilder().build());
	}

}
