package com.lauraeyal.taskmanager.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.lauraeyal.taskmanager.R;
import com.parse.ParseUser;

public class AboutActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    //UsersController controller;
    NavigationView navigationView;
    DrawerLayout drawer;
    Toolbar toolbar;
    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((int) ParseUser.getCurrentUser().get("isAdmin") == 0) {
            setContentView(R.layout.activity_about_member);
            navigationView = (NavigationView) findViewById(R.id.membernav_view_about);
            navigationView.setNavigationItemSelectedListener(this);
        }
        else {
            setContentView(R.layout.activity_about_admin);
            navigationView = (NavigationView) findViewById(R.id.nav_view_about);
            navigationView.setNavigationItemSelectedListener(this);
        }
        //controller = new UsersController(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
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
        //getMenuInflater().inflate(R.menu.menu_save, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        //boolean drawerOpen = drawer.isDrawerOpen(mDrawerList);
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
}
