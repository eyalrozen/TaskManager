package com.lauraeyal.taskmanager.dal;

import android.app.Activity;
import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.lauraeyal.taskmanager.common.*;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.List;

import bolts.Task;

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

    public void SyncParseUsers(FindCallback<ParseUser> callback)
    {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("isAdmin", 0);
        query.findInBackground(callback);
    }

    @Override
    public void UpdateUsersTable(List<User> updateUsersList) {
        SQLiteDatabase database = null;
        try {
            database = MembersdbHelper.getReadableDatabase();
            database.delete(MembersDBContract.MembersEntry.TABLE_NAME, null, null);
        }
        catch(Exception e)
        {}
        for(User t:updateUsersList)
            AddUserFromParse(t);
    }

    public List<TaskItem> GetWaitingTaskList()
    {
        ParseUser user = ParseUser.getCurrentUser();
        SQLiteDatabase database = null;
        try {
            database = TaskdbHelper.getReadableDatabase();
            List<TaskItem> tasks = new ArrayList<TaskItem>();
            Cursor cursor = database.query(TaskDBContract.TaskEntry.TABLE_NAME, tasksColumns,
                    null, null, null, null, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                TaskItem f = cursorToTask(cursor);
                if(user.getInt("isAdmin") == 0) {
                    if (f.GetTaskStatus().equals("Waiting") && f.GetTaskApprovle() > -1)      // 2 = status done
                        tasks.add(f);
                }
                else if (f.GetTaskStatus().equals("Waiting"))
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

    public List<TaskItem> GetAllTaskList()
    {
        SQLiteDatabase database = null;
        List<TaskItem> tasks = new ArrayList<TaskItem>();
        try {
            database = TaskdbHelper.getReadableDatabase();
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
                return tasks;
        }
    }

    public void GetParseTasksList(FindCallback<ParseObject> callback)
    {
        ParseUser user = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Tasks");
        if(user.getInt("isAdmin") == 0)
            query.whereEqualTo("TeamMember", user.getUsername());
        query.findInBackground(callback);
    }


    public void SyncParseTaskList(List<TaskItem> ParseTaskList)
    {
        SQLiteDatabase database = null;
        try {
            database = TaskdbHelper.getReadableDatabase();
            database.delete(TaskDBContract.TaskEntry.TABLE_NAME, null, null);
        }
        catch(Exception e)
        {}
        for(TaskItem newTask : ParseTaskList)
        {
            UpdateTaskTable(newTask);
        }
    }

    public void  UpdateTask(FindCallback<ParseObject> callback , int taskID,String Description,String teamMember,String column,String UpdatedValue)
    {
        SQLiteDatabase database = null;
        try {
            database = TaskdbHelper.getReadableDatabase();
            ContentValues args = new ContentValues();
            if(column.equals(TaskDBContract.TaskEntry.COLUMN_TASK_ASSIGNEDWORKER))
                args.put(TaskDBContract.TaskEntry.COLUMN_TASK_APPROVLE,0);
            args.put(column,UpdatedValue );
            database.update(TaskDBContract.TaskEntry.TABLE_NAME, args, TaskDBContract.TaskEntry._ID + "=" + taskID, null);

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Tasks");
            query.whereEqualTo("Description",Description);
            query.whereEqualTo("TeamMember",teamMember);
            query.findInBackground(callback);
        }

        finally {
            if (database != null)
                database.close();
        }
    }

    public void UpdateTask( FindCallback<ParseObject> callback,TaskItem task,String column,int UpdatedValue)
    {
        SQLiteDatabase database = null;
        try {
            database = TaskdbHelper.getReadableDatabase();
            ContentValues args = new ContentValues();
            args.put(column,UpdatedValue );
            database.update(TaskDBContract.TaskEntry.TABLE_NAME, args, TaskDBContract.TaskEntry._ID + "=" + task.getId(), null);

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Tasks");
            query.whereEqualTo("Description",task.GetDescription());
            query.whereEqualTo("TeamMember", task.get_teamMemebr());
            query.findInBackground(callback);
        }

        finally {
            if (database != null)
                database.close();
        }


    }

    public void UpdateTaskTable(TaskItem newTask){
        SQLiteDatabase database = null;
        try {
            database = TaskdbHelper.getReadableDatabase();
            //build the content values. - add to db - need to do insert
            ContentValues values = new ContentValues();
            values.put(TaskDBContract.TaskEntry.COLUMN_TASK_DESCRIPTION, newTask.GetDescription());
            values.put(TaskDBContract.TaskEntry.COLUMN_TASK_CATEGORY, newTask.getCategory());
            values.put(TaskDBContract.TaskEntry.COLUMN_TASK_PRIORITY, newTask.GetPriority());
            values.put(TaskDBContract.TaskEntry.COLUMN_TASK_LOCATION, newTask.GetLocation());
            values.put(TaskDBContract.TaskEntry.COLUMN_TASK_DUETIME, newTask.GetDueTime());
            values.put(TaskDBContract.TaskEntry.COLUMN_TASK_ASSIGNEDWORKER, newTask.get_teamMemebr());
            values.put(TaskDBContract.TaskEntry.COLUMN_TASK_STATUS, newTask.GetTaskStatus());
            values.put(TaskDBContract.TaskEntry.COLUMN_TASK_APPROVLE, newTask.GetTaskApprovle());
            //do the insert.
            long insertId = database.insert(TaskDBContract.TaskEntry.TABLE_NAME, null, values);

            //get the entity from the data base - extra validation, entity was insert properly.
            Cursor cursor = database.query(TaskDBContract.TaskEntry.TABLE_NAME, tasksColumns,
                    TaskDBContract.TaskEntry._ID + " = " + insertId, null, null, null, null);
            cursor.moveToFirst();
            cursor.close();
        }
        finally {
            if (database != null)
                database.close();
        }
    }

    public TaskItem AddTask(final TaskItem task , final SaveCallback callback)
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
                            Parsetasks.put("TeamMember", user.getUsername());
                            Parsetasks.put("isApprovle", task.GetTaskApprovle());
                            Parsetasks.put("Status", task.GetTaskStatus());
                            Parsetasks.saveEventually(callback);
                        }
                    } else {
                        Toast err = Toast.makeText(context,"Unable to connect server",Toast.LENGTH_LONG);
                        err.show();// Something went wrong.
                    }
                }
            });

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
    public User AddUser(User usr,SignUpCallback callback) {
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
            values.put(MembersDBContract.MembersEntry.COLUMN_MEMBER_TEAM, usr.getTeamName());
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
            ParseUser user = new ParseUser();
            user.setUsername(usr.getUserName());
            user.setPassword(usr.getPassword());
            user.put("Phone", usr.getPhoneNumber());
            user.put("MailSend", usr.getMailSend());
            user.put("Team" , usr.getTeamName());
            user.put("isAdmin", usr.getPermission());
            user.signUpInBackground(callback);
            return newUser;
        }finally {
            if (database != null)
                database.close();
        }

    }

    @Override
    public void AddUserFromParse(User usr) {
        SQLiteDatabase database = null;
        try {
            database = MembersdbHelper.getReadableDatabase();
            if (usr == null)
                return ;
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
            cursor.close();
        }
        finally {
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
                    Toast err = Toast.makeText(context,"Unable to get data from server",Toast.LENGTH_LONG);
                    err.show();
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
