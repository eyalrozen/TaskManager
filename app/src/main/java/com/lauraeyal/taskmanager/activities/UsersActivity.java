package com.lauraeyal.taskmanager.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.lauraeyal.taskmanager.*;
import com.lauraeyal.taskmanager.R;
import com.lauraeyal.taskmanager.bl.*;
import com.lauraeyal.taskmanager.common.*;
import com.parse.ParseUser;

import java.util.List;


public class UsersActivity extends AppCompatActivity implements
        OnDataSourceChangeListener , NavigationView.OnNavigationItemSelectedListener {
    private ImageButton FAB;
    private Button addBtn;
    private Button doneBtn;
    private RecyclerView mRecyclerView;
    private UsersAdapter uAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public UsersController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycle_view);
        //create the controller.
        controller = new UsersController(this);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        controller.registerOnDataSourceChanged(this);
        mRecyclerView.setHasFixedSize(true);
        Bundle extras = getIntent().getExtras();
        if(extras !=null){
            String username = extras.getString("userName");
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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

        // specify an adapter (see also next example)
        uAdapter = new UsersAdapter(controller.GetUsersList());
        mRecyclerView.setAdapter(uAdapter);
        doneBtn = (Button) findViewById(R.id.donebutton);
        doneBtn.setOnClickListener(OnDoneBtnClickListener);
        addBtn =(Button) findViewById(R.id.addBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent ContactListIntent = new Intent(v.getContext(),PhoneContactsActivity.class);
                startActivityForResult(ContactListIntent,2);

            }
        });
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_manageteam) {

        }else if (id == R.id.nav_tasks) {
            Intent TasksIntent = new Intent(this, TasksActivity.class);
            startActivity(TasksIntent);
            finish();
        }
        else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_logout) {
            ParseUser.logOut();
        } else if (id == R.id.nav_about) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private View.OnClickListener OnDoneBtnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent email = new Intent(Intent.ACTION_SEND);
            List<User> allUsers = controller.GetUsersList();
            for (User usr:allUsers) {
                if(usr.getMailSend()==0) {
                    email.putExtra(Intent.EXTRA_BCC, new String[]{usr.getUserName()});
                    usr.setMailSent(1);
                }
            }
            String subject = "TestMail";
            String message = "This is a test";
            email.putExtra(Intent.EXTRA_SUBJECT, subject);
            email.putExtra(Intent.EXTRA_TEXT, message);

            // need this to prompts email client only
            email.setType("message/rfc822");
            startActivity(Intent.createChooser(email, "Choose an Email client :"));
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1001:
                if (resultCode == Activity.RESULT_OK) {

                    Cursor s = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            null, null, null);

                    if (s.moveToFirst()) {
                        String phoneNum = s.getString(s.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Toast.makeText(getBaseContext(), phoneNum, Toast.LENGTH_LONG).show();
                    }

                }

                break;
            case 2:
               // controller.GetUsersList();
                DataSourceChanged();

        }

    }


    @Override
    public void DataSourceChanged() {
        if (uAdapter != null) {
            uAdapter.UpdateDataSource(controller.GetUsersList());
            uAdapter.notifyDataSetChanged();
        }

    }
}