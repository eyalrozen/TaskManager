package com.lauraeyal.taskmanager.activities;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.lauraeyal.taskmanager.R;
import com.lauraeyal.taskmanager.bl.TimeService;
import com.parse.ParseUser;

import java.sql.Time;

public class SettingsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    boolean mBounded;
    TimeService tService;
    NavigationView navigationView;
    DrawerLayout drawer;
    private Boolean exit = false;
    Toolbar toolbar;
    Spinner timeSpinner;
    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((int) ParseUser.getCurrentUser().get("isAdmin") == 0) {
            setContentView(R.layout.activity_settings_member);
            navigationView = (NavigationView) findViewById(R.id.membernav_view_settings);
            navigationView.setNavigationItemSelectedListener(this);
        }
        else {
            setContentView(R.layout.activity_setting_admin);
            navigationView = (NavigationView) findViewById(R.id.nav_view_settings);
            navigationView.setNavigationItemSelectedListener(this);
        }
        //controller = new UsersController(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
       /* progressDialog = new ProgressDialog(getApplicationContext());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");*/
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // in content do not change the layout size of the RecyclerView
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        timeSpinner = (Spinner) findViewById(R.id.timespinner);
        timeSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
        sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        int refreshTimer=sharedpreferences.getInt("autoRefresh", 0);
        switch(refreshTimer)
        {
            case 1:
                timeSpinner.setSelection(0);
                break;
            case 5:
                timeSpinner.setSelection(1);
                break;
            case 10:
                timeSpinner.setSelection(2);
                break;
            case 30:
                timeSpinner.setSelection(3);
                break;
            case 60:
                timeSpinner.setSelection(4);
                break;
        }
    }

    //connect to service that contain refresh timer according to selected number
    @Override
    protected void onStart() {
        super.onStart();
        Intent mIntent = new Intent(this, TimeService.class);
        bindService(mIntent, mConnection, BIND_AUTO_CREATE);
    };

    ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            mBounded = false;
            tService = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            mBounded = true;
            TimeService.LocalBinder mLocalBinder = (TimeService.LocalBinder)service;
            tService = mLocalBinder.getServerInstance();
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        if(mBounded) {
            unbindService(mConnection);
            mBounded = false;
        }
    };


    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        //boolean drawerOpen = drawer.isDrawerOpen(mDrawerList);
        menu.removeItem(R.id.nav_manageteam);
       // menu.findItem(R.id.nav_manageteam);
        return super.onPrepareOptionsMenu(menu);
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

    //Update time refresh interval on service
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                finish();
                return true;
            default:
                String time =  String.valueOf(timeSpinner.getSelectedItem().toString());
                sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
                int refreshTimer=sharedpreferences.getInt("autoRefresh", 0);
                if(Integer.parseInt(time) != refreshTimer) {
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putInt("autoRefresh", Integer.parseInt(time));
                    editor.apply();
                    tService.SetTimerInterval();
                    //TODO Update auto refresh timer
                    Toast toast = Toast.makeText(getApplicationContext(), "Auto refresh timer Updated Successfully!", Toast.LENGTH_LONG);
                    toast.show();
                }
                else
                {
                    Toast toast = Toast.makeText(getApplicationContext(), "Time is already set to "+time +" Minutes", Toast.LENGTH_LONG);
                    toast.show();
                }
                return true;
            //return super.onOptionsItemSelected(item);
        }
    }

    private class CustomOnItemSelectedListener implements android.widget.AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {

        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }
    }

}
