package com.lauraeyal.taskmanager.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.lauraeyal.taskmanager.ManagerWTasksFragment;
import com.lauraeyal.taskmanager.MyItemClickListener;
import com.lauraeyal.taskmanager.MyItemLongClickListener;
import com.lauraeyal.taskmanager.R;
import com.lauraeyal.taskmanager.ManagerATasksFragment;
import com.lauraeyal.taskmanager.bl.TaskAdapter;
import com.lauraeyal.taskmanager.bl.TaskController;
import com.lauraeyal.taskmanager.bl.TimeService;
import com.lauraeyal.taskmanager.common.OnDataSourceChangeListener;
import com.lauraeyal.taskmanager.common.TaskItem;
import com.lauraeyal.taskmanager.common.User;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import java.util.ArrayList;
import java.util.List;

public class TasksActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private TaskController controller;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    NavigationView navigationView;
    DrawerLayout drawer;
    ViewPagerAdapter adapter;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ParseInstallation.getCurrentInstallation().saveInBackground();
        startService(new Intent(this, TimeService.class));
        controller = new TaskController(this);
        if ((int) ParseUser.getCurrentUser().get("isAdmin") == 0) {
            setContentView(R.layout.activity_tasks_member);
            navigationView = (NavigationView) findViewById(R.id.membernav_view);
            navigationView.setNavigationItemSelectedListener(this);
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                Toast welcomeToast = Toast.makeText(getApplicationContext(), "You have been added to Team: "+controller.GetTeamName(), Toast.LENGTH_LONG);
                welcomeToast.show();
            }
        }
        else {
            setContentView(R.layout.activity_tasks);
            navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressDialog = new ProgressDialog(getApplicationContext());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading Tasks...");
      //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        // in content do not change the layout size of the RecyclerView
        FloatingActionButton refreshBtn = (FloatingActionButton) findViewById(R.id.refreshButton);
        FloatingActionButton addTaskBtn = (FloatingActionButton) findViewById(R.id.addBtn);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        if ((int) ParseUser.getCurrentUser().get("isAdmin") == 0) {
            addTaskBtn.setVisibility(View.GONE);
        }
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRefreshClicked();
            }
        });
        addTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nextScreen = new Intent(getApplicationContext(), addtaskActivity.class);
                startActivity(nextScreen);
                finish();
            }
        });
    }

    public void onRefreshClicked()
    {
        final ManagerWTasksFragment frag1 = (ManagerWTasksFragment)adapter.getItem(0);
        final ManagerATasksFragment frag2 = (ManagerATasksFragment)adapter.getItem(1);
        frag1.StartProgressDialog();
        frag2.StartProgressDialog();
        List<TaskItem> UpdateList = new ArrayList<TaskItem>();
        controller.GetParseTaskList(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    List<TaskItem> UpdateList = new ArrayList<>();
                    for (ParseObject task : objects) {
                        TaskItem f = new TaskItem();
                        f.setCategory(task.getString("Category"));
                        f.SetLocation(task.getString("Location"));
                        f.SetDescription(task.getString("Description"));
                        f.SetDueTime(task.getString("DueTime"));
                        f.SetTeamMemebr(task.getString("TeamMember"));
                        f.SetPriority(task.getString("Priority"));
                        f.SetTaskApprovle(task.getInt("isApprovle"));
                        f.SetTaskStatus(task.getString("Status"));
                        UpdateList.add(f);
                    }
                    controller.SyncParseTaskList(UpdateList);
                    frag1.OnRefreshClicked();
                    frag2.OnRefreshClicked();
                }
                else
                {
                    frag1.ParseError();
                    frag2.ParseError();
                }
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
         adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ManagerWTasksFragment(), "Waiting Tasks");
        adapter.addFragment(new ManagerATasksFragment(), "All Tasks");
        viewPager.setAdapter(adapter);
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        final ManagerWTasksFragment frag1 = (ManagerWTasksFragment)adapter.getItem(0);
        final ManagerATasksFragment frag2 = (ManagerATasksFragment)adapter.getItem(1);
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sortStatus) {

            frag2.OnSortByStatusClicked();
            return true;
        }
        if (id == R.id.action_sortDue) {
            frag2.OnSortByDueClicked();
            frag1.OnSortByDueClicked();
            return true;
        }

        if (id == R.id.action_sortPriority) {
            frag1.OnSortByPriorityClicked();
            frag2.OnSortByPriorityClicked();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        //boolean drawerOpen = drawer.isDrawerOpen(mDrawerList);
        menu.removeItem(R.id.nav_manageteam);
       // menu.findItem(R.id.nav_manageteam);
        return super.onPrepareOptionsMenu(menu);
    }

    @SuppressWarnings("StatementWithEmptyBody")
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
    public void onResume() {
        super.onResume();

        // Register mMessageReceiver to receive messages.
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("my-event"));
    }
    // handler for received Intents for the "my-event" event
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            onRefreshClicked();
        }
    };

    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
