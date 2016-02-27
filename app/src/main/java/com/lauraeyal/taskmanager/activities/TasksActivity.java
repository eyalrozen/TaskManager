package com.lauraeyal.taskmanager.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
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
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.lauraeyal.taskmanager.ManagerWTasksFragment;
import com.lauraeyal.taskmanager.R;
import com.lauraeyal.taskmanager.ManagerATasksFragment;
import com.lauraeyal.taskmanager.bl.TaskController;
import com.lauraeyal.taskmanager.bl.TimeService;
import com.lauraeyal.taskmanager.common.TaskItem;
import com.lauraeyal.taskmanager.pushNotification.App42GCMController;
import com.lauraeyal.taskmanager.pushNotification.App42GCMService;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.shephertz.app42.paas.sdk.android.App42API;
import java.util.ArrayList;
import java.util.List;

public class TasksActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,  App42GCMController.App42GCMListener{
    private TaskController controller;
    private static final String GoogleProjectNo = "219405474304";
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public static ArrayList<String> newTasksList;
    NavigationView navigationView;
    DrawerLayout drawer;
    ViewPagerAdapter adapter;
    SharedPreferences sharedpreferences;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        startService(new Intent(this, TimeService.class));
        App42API.initialize(this, "153663557f7a68f62f95cfae71037944af5a4a38a08cb94da5610124596fa159", "6b2e6c13f4753b4c1c248c07373da4b82714957db2f69a4f3b6d728398da98d5");
        App42API.setLoggedInUser(ParseUser.getCurrentUser().getUsername()) ;
        newTasksList = new ArrayList<String>();
        Bundle extras = getIntent().getExtras();
        if(extras !=null)
        {
            String newTaskDescription = getIntent().getStringExtra("newTask");
            String newTaskTMember = getIntent().getStringExtra("TeamMember");
            App42GCMController.sendPushToUser(newTaskTMember,
                    "You got new task:"+newTaskDescription, this);
        }
      /*  String newTaskDescription = intent.getStringExtra("newTask");
        String newTaskTMember = intent.getStringExtra("TeamMember");
        if(!newTaskDescription.equals(""))
        {
            App42GCMController.sendPushToUser(newTaskTMember,
                    "You got new task:"+newTaskDescription, this);
        }*/
        controller = new TaskController(this);
        if ((int) ParseUser.getCurrentUser().get("isAdmin") == 0) {
            setContentView(R.layout.activity_tasks_member);
            navigationView = (NavigationView) findViewById(R.id.membernav_view);
            navigationView.setNavigationItemSelectedListener(this);
            boolean firstTime = sharedpreferences.getBoolean("firstTime",true);
            if (firstTime) {
                Toast welcomeToast = Toast.makeText(getApplicationContext(), "You have been added to Team: "+controller.GetTeamName(), Toast.LENGTH_LONG);
                welcomeToast.show();
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean("firstTime", false);
                editor.apply();
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

    public void onStart() {
        super.onStart();
        if (App42GCMController.isPlayServiceAvailable(this)) {
            App42GCMController.getRegistrationId(TasksActivity.this,
                    GoogleProjectNo, this);
        } else {
            Log.i("App42PushNotification",
                    "No valid Google Play Services APK found.");
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        String message = getIntent().getStringExtra(
                App42GCMService.ExtraMessage);
        if (message != null)
            Log.d("MainActivity-onResume", "Message Recieved :" + message);
        IntentFilter filter = new IntentFilter(
                App42GCMService.DisplayMessageAction);
        filter.setPriority(2);
        registerReceiver(mBroadcastReceiver, filter);
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

    final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent
                    .getStringExtra(App42GCMService.ExtraMessage);
            /*Log.i("MainActivity-BroadcastReceiver", "Message Recieved " + " : "
                    + message);*/
        }
    };

    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    //Push notification methods
    @Override
    public void onError(String errorMsg) {

    }

    @Override
    public void onGCMRegistrationId(String gcmRegId) {
        App42GCMController.storeRegistrationId(this, gcmRegId);
        if(!App42GCMController.isApp42Registerd(TasksActivity.this))
            App42GCMController.registerOnApp42(App42API.getLoggedInUser(), gcmRegId, this);
    }

    @Override
    public void onApp42Response(final String responseMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("push", responseMessage);
            }
        });

    }

    @Override
    public void onRegisterApp42(final String responseMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("push", responseMessage);
                App42GCMController.storeApp42Success(TasksActivity.this);
            }
        });
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
