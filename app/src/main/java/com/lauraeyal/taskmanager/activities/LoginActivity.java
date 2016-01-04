package com.lauraeyal.taskmanager.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.lauraeyal.taskmanager.R;
import com.lauraeyal.taskmanager.bl.*;
import com.lauraeyal.taskmanager.common.*;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

//Start the app on LoginActivity
public class LoginActivity extends Activity {

	public static String teamName;
	private EditText userNameEditText;
	private EditText passwordEditText;
	private EditText phoneNumberEditText;
	private UsersController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
		Parse.initialize(this);
		final ParseUser currentUser = ParseUser.getCurrentUser();
		if(currentUser != null){
			if((int)currentUser.get("isAdmin") > 0){
				startMembersActivity();

			}
			else {
				startMembersActivity();

			}
		}
        controller = new UsersController(this);
        //ask the controller if the user is logged in.
       /* if(controller.isLoggedIn())
        {
        	//In case the user is logged in start the main activity.
			startMembersActivity();
        	return;
        }*/
        //get the useName and password edit text view 
        userNameEditText = (EditText) findViewById(R.id.editTextUserName);
        passwordEditText = (EditText) findViewById(R.id.editTextPassword);
		phoneNumberEditText = (EditText) findViewById(R.id.editTextphoneNumber);
    }
    
    public void logInClicked(View v) throws ParseException {
    	//get the password, user name and phone number from the edit text.
    	if(userNameEditText!=null && passwordEditText!=null && phoneNumberEditText!=null)
    	{
    		String userName  = userNameEditText.getText().toString();
    		String pass = passwordEditText.getText().toString();
			String phoneNumber = phoneNumberEditText.getText().toString();

			ParseUser.logInInBackground(userName, pass, new LogInCallback() {
				public void done(ParseUser user, ParseException e) {
					if (user != null) {
						// Hooray! The user is logged in.

						//user.signUpInBackground();
						startMembersActivity();
					}
					else {
						ParseQuery<ParseUser> query = ParseUser.getQuery();
						query.whereEqualTo("isAdmin",1);
						query.findInBackground(new FindCallback<ParseUser>() {
							@Override
							public void done(List<ParseUser> userList, ParseException e) {
								if (e == null) {
									if (userList.size()>0) {
										Log.d("Mylog", "Error in username/password!");
									}
									else { // Should be the admin login 1st time
										Intent teamNameIntent = new Intent(getApplicationContext(), AddTeamActivity.class);
										startActivityForResult(teamNameIntent, 1);
									}
								}
								else { // Should be the admin login 1st time
									Toast.makeText(getApplicationContext(),"Unable to get data from server!",Toast.LENGTH_LONG);
								}
							}
						});
					}
				}
			});
			/*if(controller.isListEmpty())
			{

				/*try {
					User u = controller.AddUser(userName, pass, phoneNumber,1);
					controller.setLogedIn(u);
					startMembersActivity();
					return;
				}
				catch(Exception e)
				{}
			}
			else {
				User u = controller.GetUser(userName, pass, phoneNumber);
				//the user is exists, set the IsLogin flag to true.
				if (u !=null) {
					controller.setLogedIn(u);
					startMembersActivity();
					return;
				}
				//log in was failed.
				Toast.makeText(this, "User name or password or phone number is incorrect", Toast.LENGTH_LONG).show();
			}*/
    	}
		
	}

	@Override
	//Recieve the input text from AddTeamActivity and insert it as teamName
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode) {
			case (1) : {
				if (resultCode == Activity.RESULT_OK) {
					String tName = data.getStringExtra("teamName");
					teamName = tName;
					String userName  = userNameEditText.getText().toString();
					String pass = passwordEditText.getText().toString();
					String phoneNumber = phoneNumberEditText.getText().toString();
					try {
						User u = controller.AddUser(userName, pass, phoneNumber,1,1,teamName);
						//controller.setLogedIn(u);
						startMembersActivity();

						return;
					}
					catch(Exception e)
					{}
				}
				break;
			}
		}
	}
    public void startMembersActivity()
    {
		//Explicit intent.
		Intent  i = new Intent(this,UsersActivity.class);
		//ParseUser currentUser = ParseUser.getCurrentUser();
		//i.putExtra("userName",currentUser.getUsername());
		//Start the activity
		startActivity(i);
		finish();
    }

	public void startWorkerActivity()
	{
		//Explicit intent.
		Intent  i = new Intent(this,TasksActivity.class);
		//ParseUser currentUser = ParseUser.getCurrentUser();
		//i.putExtra("userName",currentUser.getUsername());
		//Start the activity
		startActivity(i);
		finish();
	}
}
