package com.lauraeyal.taskmanager.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.lauraeyal.taskmanager.*;
import com.lauraeyal.taskmanager.R;
import com.lauraeyal.taskmanager.bl.*;
import com.lauraeyal.taskmanager.common.*;
import com.lauraeyal.taskmanager.dal.MembersDBContract;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


public class UsersActivity extends AppCompatActivity implements
        OnDataSourceChangeListener , NavigationView.OnNavigationItemSelectedListener,MyItemClickListener,MyItemLongClickListener {
    private ImageButton FAB;
    private String adminUsername;
    private String adminPassword;
    private TextView noMembersText;
    private RecyclerView mRecyclerView;
    private UserAdapter uAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public UsersController controller;
    ProgressDialog progressDialog;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycle_view);
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        adminUsername = ParseUser.getCurrentUser().getUsername();
        adminPassword = ParseUser.getCurrentUser().getString("Phone");
       // adminPassword = ParseUser.getCurrentUser().get
        //create the controller.
        controller = new UsersController(this);
        controller.SyncTeamName();
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        controller.registerOnDataSourceChanged(this);
        mRecyclerView.setHasFixedSize(true);
        noMembersText = (TextView)findViewById(R.id.noMemberText);
        Bundle extras = getIntent().getExtras();
        progressDialog = new ProgressDialog(UsersActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading Team Members...");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FloatingActionButton addBtn = (FloatingActionButton) findViewById(R.id.addUserBtn);
        FloatingActionButton doneBtn = (FloatingActionButton) findViewById(R.id.SendMailBtn);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        progressDialog.show();
        // specify an adapter (see also next example)
        controller.SyncParseUsers(new FindCallback<ParseUser>() {
          @Override
          public void done(List<ParseUser> objects, ParseException e) {
            if(objects.size() == controller.GetUsersList().size())
                ContinueInit();
              else
            {
                controller.UpdateUsersTable(objects);
                ContinueInit();
            }
          }
      });
        addBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                controller.SyncTeamName();
                Intent ContactListIntent = new Intent(v.getContext(), PhoneContactsActivity.class);
                startActivity(ContactListIntent);
            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> newUsers = new ArrayList<String>();
                progressDialog.setMessage("Loading..");
                List<User> allUsers = controller.GetUsersList();
                for (User usr:allUsers) {
                    if(usr.getMailSend()==0) {
                        newUsers.add(usr.getUserName());
                        controller.UpdateUserField(MembersDBContract.MembersEntry.COLUMN_MEMBER_MAILSENT,1,"",usr.getId());
                    }
                }
                if(newUsers.size()>0) {
                    progressDialog.dismiss();
                    sendMailToNewMembers(newUsers);
                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "All members got invitation already", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    void ContinueInit()
    {
        uAdapter = new UserAdapter(controller.GetUsersList());
        if(controller.GetUsersList().size()>0)
            noMembersText.setVisibility(View.GONE);
        progressDialog.dismiss();
        mRecyclerView.setAdapter(uAdapter);
        uAdapter.setOnItemClickListener(this);
        uAdapter.setOnItemLongClickListener(this);
    }

    public void onItemClick(View view, int postion) {

    }

    //Long click listener - delete Team member
    public void onItemLongClick(final View view, int postion) {
        final User usr = controller.GetUsersList().get(postion);

        if(usr != null){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Warning! ");
            alertDialogBuilder
                    .setMessage("Are you sure you want to delete \n" + usr.getUserName() + "?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            progressDialog.setMessage("Deleting User..");
                            progressDialog.show();
                            controller.DeleteUser(usr, new LogInCallback() {
                                @Override
                                public void done(ParseUser user, ParseException e) {
                                    if(e==null)
                                    {
                                        user.deleteInBackground(new DeleteCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                ParseUser.logInInBackground(adminUsername, adminPassword, new LogInCallback() {
                                                    @Override
                                                    public void done(ParseUser user, ParseException e) {
                                                        progressDialog.dismiss();
                                                        Snackbar.make(view,"User deleted successfully",Snackbar.LENGTH_LONG).setAction("action",null).show();
                                                        ContinueInit();
                                                    }
                                                });
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, just close
                            // the dialog box and do nothing
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            //Snackbar.make(view,"Short Click "+ usr.getUserName(),Snackbar.LENGTH_LONG).setAction("action",null).show();
        }
    }

    //Send mail to new members
    private void sendMailToNewMembers(ArrayList<String> newUsers)
    {
        Intent email = new Intent(Intent.ACTION_SEND_MULTIPLE);
        email.putExtra(Intent.EXTRA_BCC, newUsers.toArray(new String[newUsers.size()]));
        String subject = "Invitation to Join "+ controller.GetTeamName()+ " team";
        String message = "Hi\n" +
                "\tYou have been invited to be a team member in an "+ controller.GetTeamName() +" Team created by " + ParseUser.getCurrentUser().getUsername()+ ".\n" +
                "\tUse this link to download and install the App from Google Play.";
        email.putExtra(Intent.EXTRA_SUBJECT, subject);
        email.putExtra(Intent.EXTRA_TEXT, message);

        // need this to prompts email client only
        email.setType("message/rfc822");
        startActivity(Intent.createChooser(email, "Choose an Email client :"));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       /* if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_manageteam) {
            Intent membersIntent = new Intent(this, UsersActivity.class);
            startActivity(membersIntent);
            finish();
        }
        else if (id == R.id.nav_tasks) {
            Intent TasksIntent = new Intent(this, TasksActivity.class);
            startActivity(TasksIntent);
            finish();
        }
        else if (id == R.id.nav_settings) {
            Intent settingActivity = new Intent(this, SettingsActivity.class);
            startActivity(settingActivity);
            finish();
        }
        else if (id == R.id.nav_logout) {
            ParseUser.logOut();
            Intent LoginActivity = new Intent(this, LoginActivity.class);
            startActivity(LoginActivity);
            finish();
        }
        else if (id == R.id.nav_about) {
            Intent AboutActivity = new Intent(this, AboutActivity.class);
            startActivity(AboutActivity);
            finish();
        }

        else if (id == R.id.member_nav_tasks) {
            Intent TasksIntent = new Intent(this, TasksActivity.class);
            startActivity(TasksIntent);
            finish();
        }
        else if (id == R.id.member_nav_settings) {
            Intent settingActivity = new Intent(this, SettingsActivity.class);
            startActivity(settingActivity);
            finish();
        }
        else if (id == R.id.member_nav_logout) {
            ParseUser.logOut();
            Intent LoginActivity = new Intent(this, LoginActivity.class);
            startActivity(LoginActivity);
            finish();
        }
        else if (id == R.id.member_nav_about) {
            Intent AboutActivity = new Intent(this, AboutActivity.class);
            startActivity(AboutActivity);
            finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void DataSourceChanged() {
        if (uAdapter != null) {
            uAdapter.UpdateDataSource(controller.GetUsersList());
            uAdapter.notifyDataSetChanged();
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName("Users");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
