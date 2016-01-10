package com.lauraeyal.taskmanager.dal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.lauraeyal.taskmanager.common.*;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eyal on 11/7/2015.
 */
public class DAO implements IDataAcces
{

    private List<TaskItem> WaitingTaskList = new ArrayList<TaskItem>();
    private List<TaskItem> AllTaskList = new ArrayList<TaskItem>();
    private static DAO instance;
    private static String tName;
    private Context context;
    private MembersDBHelper MembersdbHelper;
    private TaskDBHelper TaskdbHelper;
    private String[] membersColumns = {MembersDBContract.MembersEntry._ID,MembersDBContract.MembersEntry.COLUMN_MEMBER_USERNAME
    ,MembersDBContract.MembersEntry.COLUMN_MEMBER_PASSWORD,MembersDBContract.MembersEntry.COLUMN_MEMBER_PHONE,MembersDBContract.MembersEntry.COLUMN_MEMBER_MAILSENT
    ,MembersDBContract.MembersEntry.COLUMN_MEMBER_TEAM,MembersDBContract.MembersEntry.COLUMN_MEMBER_PERMISSION};
    private String[] tasksColumns = { TaskDBContract.TaskEntry._ID,TaskDBContract.TaskEntry.COLUMN_TASK_DESCRIPTION,
            TaskDBContract.TaskEntry.COLUMN_TASK_CATEGORY, TaskDBContract.TaskEntry.COLUMN_TASK_PRIORITY, TaskDBContract.TaskEntry.COLUMN_TASK_LOCATION,
            TaskDBContract.TaskEntry.COLUMN_TASK_DUETIME, TaskDBContract.TaskEntry.COLUMN_TASK_ASSIGNEDWORKER, TaskDBContract.TaskEntry.COLUMN_TASK_STATUS,TaskDBContract.TaskEntry.COLUMN_TASK_APPROVLE};


    private DAO(Context context) {
        this.context = context;
        TaskdbHelper = new TaskDBHelper(this.context);
        MembersdbHelper = new MembersDBHelper(this.context);
    }

    /*
     * Single tone implement.
     */
    public static DAO getInstance(Context context)
    {
        if(instance ==  null)
            instance = new DAO(context);
        return instance;
    }

    public List<User> GetUserList()
    {
        SQLiteDatabase database = null;
        try {
            database = MembersdbHelper.getReadableDatabase();
            List<User> users = new ArrayList<User>();

            Cursor cursor = database.query(MembersDBContract.MembersEntry.TABLE_NAME, membersColumns,
                    null, null, null, null, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                User user1 = cursorTouser(cursor);
                users.add(user1);
                cursor.moveToNext();
            }
            // make sure to close the cursor
            cursor.close();
            return users;
        } finally {
            if (database != null) {
                database.close();
            }
        }
    }

    public List<TaskItem> GetTaskList() {
        SQLiteDatabase database = null;
        try {
            database = TaskdbHelper.getReadableDatabase();
            List<TaskItem> tasks = new ArrayList<TaskItem>();
            Cursor cursor = database.query(TaskDBContract.TaskEntry.TABLE_NAME, tasksColumns,
                    null, null, null, null, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                TaskItem f = cursorToTask(cursor);
                tasks.add(f);
                cursor.moveToNext();
            }
            // make sure to close the cursor
            cursor.close();
            return tasks;
        } finally {
            if (database != null) {
                database.close();
            }
            else
                return null;
        }
    }


    public List<TaskItem> GetWaitingTaskList()
    {
        return WaitingTaskList;
    }

    public List<TaskItem> GetAllTaskList()
    {
        return AllTaskList;
    }

    public void SyncAllTaskList(final ParseUser user){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Tasks");
        if(user.getInt("isAdmin") == 0)
            query.whereEqualTo("user", user);
        query.whereEqualTo("isApproved", 1);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e==null) {
                    for (ParseObject task : objects) {
                        TaskItem f = new TaskItem();
                        f.setCategory(task.getString("Category"));
                        f.SetLocation(task.getString("Location"));
                        f.SetDescription(task.getString("Description"));
                        f.SetDueTime(task.getString("DueTime"));
                        f.SetTeamMemebr(user.getEmail());
                        f.SetPriority(task.getString("Priority"));
                        f.SetTaskApprovle(task.getInt("isApproved"));
                        f.SetTaskStatus(task.getString("Status"));
                        AllTaskList.add(f);
                    }
                }
            }

        });

    }

    public void SyncWaitingTaskList(final ParseUser user) {
       /* SQLiteDatabase database = null;
        try {
            database = TaskdbHelper.getReadableDatabase();
            List<TaskItem> tasks = new ArrayList<TaskItem>();
            Cursor cursor = database.query(TaskDBContract.TaskEntry.TABLE_NAME, tasksColumns,
                    null, null, null, null, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                TaskItem f = cursorToTask(cursor);
                if(f.GetTaskStatus() <2)   //0=status waiting , 1=in progress;
                    tasks.add(f);
                cursor.moveToNext();
            }
            // make sure to close the cursor
            cursor.close();
            return tasks;
        } finally {
            if (database != null) {
                database.close();
            }
            else
                return null;
        }*/
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Tasks");
        if(user.getInt("isAdmin") == 0)
            query.whereEqualTo("user", user);
        query.whereEqualTo("isApproved", 0);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e==null) {
                    for (ParseObject task : objects) {
                        TaskItem f = new TaskItem();
                        f.setCategory(task.getString("Category"));
                        f.SetLocation(task.getString("Location"));
                        f.SetDescription(task.getString("Description"));
                        f.SetDueTime(task.getString("DueTime"));
                        f.SetTeamMemebr(user.getEmail());
                        f.SetPriority(task.getString("Priority"));
                        f.SetTaskApprovle(task.getInt("isApproved"));
                        f.SetTaskStatus(task.getString("Status"));
                        WaitingTaskList.add(f);
                    }
                }
            }

        });

    }

    public List<TaskItem> GetDoneTaskList() {
        SQLiteDatabase database = null;
        try {
            database = TaskdbHelper.getReadableDatabase();
            List<TaskItem> tasks = new ArrayList<TaskItem>();
            Cursor cursor = database.query(TaskDBContract.TaskEntry.TABLE_NAME, tasksColumns,
                    null, null, null, null, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                TaskItem f = cursorToTask(cursor);
                if(f.GetTaskStatus() == "")      // 2 = status done
                    tasks.add(f);
                cursor.moveToNext();
            }
            // make sure to close the cursor
            cursor.close();
            return tasks;
        } finally {
            if (database != null) {
                database.close();
            }
            else
                return null;
        }
    }


    public TaskItem AddTask(final TaskItem task)
    {
        SQLiteDatabase database = null;
        try {
            database = TaskdbHelper.getReadableDatabase();
            if (task == null)
                return null;
            //build the content values. - add to db - need to do insert
            ContentValues values = new ContentValues();
            values.put(TaskDBContract.TaskEntry.COLUMN_TASK_DESCRIPTION, task.GetDescription());
            values.put(TaskDBContract.TaskEntry.COLUMN_TASK_CATEGORY, task.getCategory());
            values.put(TaskDBContract.TaskEntry.COLUMN_TASK_PRIORITY, task.GetPriority());
            values.put(TaskDBContract.TaskEntry.COLUMN_TASK_LOCATION, task.GetLocation());
            values.put(TaskDBContract.TaskEntry.COLUMN_TASK_DUETIME, task.GetDueTime());
            values.put(TaskDBContract.TaskEntry.COLUMN_TASK_ASSIGNEDWORKER, task.get_teamMemebr());
            values.put(TaskDBContract.TaskEntry.COLUMN_TASK_STATUS, task.GetTaskStatus());
            values.put(TaskDBContract.TaskEntry.COLUMN_TASK_APPROVLE, task.GetTaskApprovle());


            //do the insert.
            long insertId = database.insert(TaskDBContract.TaskEntry.TABLE_NAME, null, values);

            //get the entity from the data base - extra validation, entity was insert properly.
            Cursor cursor = database.query(TaskDBContract.TaskEntry.TABLE_NAME, tasksColumns,
                    TaskDBContract.TaskEntry._ID + " = " + insertId, null, null, null, null);
            cursor.moveToFirst();
            //create the friend object from the cursor.
            TaskItem newTask = cursorToTask(cursor);
            cursor.close();
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo("username", task.get_teamMemebr());
            query.findInBackground(new FindCallback<ParseUser>() {
                public void done(List<ParseUser> objects, ParseException e) {
                    if (e == null) {
                        for (ParseUser user: objects) {
                            ParseObject Parsetasks = new ParseObject("Tasks");
                            Parsetasks.put("Description", task.GetDescription());
                            Parsetasks.put("Category", task.getCategory());
                            Parsetasks.put("Priority", task.GetPriority());
                            Parsetasks.put("Location", task.GetLocation());
                            Parsetasks.put("DueTime", task.GetDueTime());
                            Parsetasks.put("user", user);
                            Parsetasks.put("isApprovle", task.GetTaskApprovle());
                            Parsetasks.put("Status", task.GetTaskStatus());
                            Parsetasks.saveEventually();
                        }
                    } else {
                        // Something went wrong.
                    }
                }
            });
           /* ParseObject Parsetasks = new ParseObject("Tasks");
            Parsetasks.put("Description", task.GetDescription());
            Parsetasks.put("Category", task.getCategory());
            Parsetasks.put("Priority", task.GetPriority());
            Parsetasks.put("Location", task.GetLocation());
            Parsetasks.put("DueTime", task.GetDueTime());
            Parsetasks.put("AssignedWorker", task.get_teamMemebr());
            Parsetasks.put("Pending", task.GetTaskApprovle());
            Parsetasks.saveEventually();

            /*ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo("username", "test1@gmail.com");
            query.findInBackground(new FindCallback<ParseUser>() {
                public void done(List<ParseUser> objects, ParseException e) {
                    if (e == null) {
                        for(ParseUser t :objects) {
                            ParseObject Parsetasks = new ParseObject("Tasks");
                            Parsetasks.put("Category", task.getCategory());
                            Parsetasks.put("Priority", task.GetPriority());
                            Parsetasks.put("Location", task.GetLocation());
                            Parsetasks.put("DueTime", task.GetDueTime());
                            Parsetasks.put("AssignedWorker", t);
                            Parsetasks.put("Pending", task.GetPendingStatus());
                            Parsetasks.saveEventually();
                        }
                        // The query was successful.
                    } else {
                        // Something went wrong.
                    }
                }
            });*/

            return newTask;
        }finally {
            if (database != null)
                database.close();
        }
       /* TaskItem item = new TaskItem(task);
        TaskList.add(item);*/
    }

    private TaskItem cursorToTask(Cursor cursor) {
        TaskItem f = new TaskItem();
        f.setId(cursor.getInt(cursor.getColumnIndex(TaskDBContract.TaskEntry._ID)));
        f.SetDescription(cursor.getString(cursor
                .getColumnIndex(TaskDBContract.TaskEntry.COLUMN_TASK_DESCRIPTION)));
        f.setCategory(cursor.getString(cursor
                .getColumnIndex(TaskDBContract.TaskEntry.COLUMN_TASK_CATEGORY)));
        f.SetDueTime(cursor.getString(cursor
                    .getColumnIndex(TaskDBContract.TaskEntry.COLUMN_TASK_DUETIME)));
        f.SetLocation(cursor.getString(cursor
                .getColumnIndex(TaskDBContract.TaskEntry.COLUMN_TASK_LOCATION)));
        f.SetPriority(cursor.getString(cursor
                .getColumnIndex(TaskDBContract.TaskEntry.COLUMN_TASK_PRIORITY)));
        f.SetTeamMemebr(cursor.getString(cursor
                .getColumnIndex(TaskDBContract.TaskEntry.COLUMN_TASK_ASSIGNEDWORKER)));
        f.SetTaskStatus(cursor.getString(cursor
                .getColumnIndex(TaskDBContract.TaskEntry.COLUMN_TASK_STATUS)));
        f.SetTaskApprovle(cursor.getInt(cursor
                .getColumnIndex(TaskDBContract.TaskEntry.COLUMN_TASK_APPROVLE)));
        return f;
    }

    private User cursorTouser(Cursor cursor) {
        User f = new User();
        f.setUserName(cursor.getString(cursor.getColumnIndex(MembersDBContract.MembersEntry.COLUMN_MEMBER_USERNAME)));
        f.setPassword(cursor.getString(cursor.getColumnIndex(MembersDBContract.MembersEntry.COLUMN_MEMBER_PASSWORD)));
        f.setPhoneNumber(cursor.getString((cursor.getColumnIndex(MembersDBContract.MembersEntry.COLUMN_MEMBER_PHONE))));
        f.setMailSent(cursor.getInt((cursor.getColumnIndex(MembersDBContract.MembersEntry.COLUMN_MEMBER_MAILSENT))));
        f.setPermission(cursor.getInt((cursor.getColumnIndex(MembersDBContract.MembersEntry.COLUMN_MEMBER_PERMISSION))));
        f.setTeamName(cursor.getString((cursor.getColumnIndex(MembersDBContract.MembersEntry.COLUMN_MEMBER_TEAM))));
        return f;
    }

    @Override
    public void RemoveTask(TaskItem task) {
        SQLiteDatabase database = null;
        try {
            database = TaskdbHelper.getReadableDatabase();
            long id = task.getId();
            database.delete(TaskDBContract.TaskEntry.TABLE_NAME, TaskDBContract.TaskEntry._ID + " = " + id,
                    null);
        }finally {
            if(database != null){
                database.close();
            }
        }
    }

    @Override
    public User AddUser(User usr) {
        SQLiteDatabase database = null;
        try {
            database = MembersdbHelper.getReadableDatabase();
            if (usr == null)
                return null;
            //build the content values. - add to db - need to do insert
            ContentValues values = new ContentValues();
            values.put(MembersDBContract.MembersEntry.COLUMN_MEMBER_USERNAME, usr.getUserName());
            values.put(MembersDBContract.MembersEntry.COLUMN_MEMBER_PASSWORD, usr.getPassword());
            values.put(MembersDBContract.MembersEntry.COLUMN_MEMBER_PHONE, usr.getPhoneNumber());
            values.put(MembersDBContract.MembersEntry.COLUMN_MEMBER_MAILSENT, (usr.getMailSend()));
            values.put(MembersDBContract.MembersEntry.COLUMN_MEMBER_TEAM, (MembersDBContract.MembersEntry.TABLE_NAME));
            values.put(MembersDBContract.MembersEntry.COLUMN_MEMBER_PERMISSION, ( usr.getPermission()));

            //do the insert.
            long insertId = database.insert(MembersDBContract.MembersEntry.TABLE_NAME, null, values);

            //get the entity from the data base - extra validation, entity was insert properly.
            Cursor cursor = database.query(MembersDBContract.MembersEntry.TABLE_NAME, membersColumns,
                    MembersDBContract.MembersEntry._ID + " = " + insertId, null, null, null, null);
            if (cursor != null)
                cursor.moveToFirst();
            //create the friend object from the cursor.
            User newUser = cursorTouser(cursor);
            cursor.close();
            final ParseUser user = new ParseUser();
            user.setUsername(usr.getUserName());
            user.setPassword(usr.getPassword());
            user.put("Phone", usr.getPhoneNumber());
            user.put("MailSend", usr.getMailSend());
            user.put("Team" , usr.getTeamName());
            user.put("isAdmin" ,usr.getPermission());
            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if(e==null)
                    {

                    }
                    else
                        Toast.makeText(context,"Problem Adding user",Toast.LENGTH_LONG);
                }
            });
            return newUser;
        }finally {
            if (database != null)
                database.close();
        }

    }

    public void SyncTeamName()
    {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("isAdmin", 1);
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    // The query was successful.
                    for(ParseUser usr : objects)
                    {
                        tName = usr.getString("Team");
                    }
                }
                else {
                    // Something went wrong.
                }
            }
        });
    }
    public String GetTeamName()
    {
        return tName;
    }



}
