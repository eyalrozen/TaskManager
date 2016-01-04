package com.lauraeyal.taskmanager.dal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Eyal on 11/21/2015.
 */
public class TaskDBHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database
    // version.
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "tasks.db";

    public TaskDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a table to hold the friends - get the data to create table from DB contract;
        final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE "
                + TaskDBContract.TaskEntry.TABLE_NAME + " (" + TaskDBContract.TaskEntry._ID
                + " INTEGER PRIMARY KEY," + TaskDBContract.TaskEntry.COLUMN_TASK_DESCRIPTION +
                " TEXT NOT NULL,"+ TaskDBContract.TaskEntry.COLUMN_TASK_CATEGORY +
                " TEXT NOT NULL," + TaskDBContract.TaskEntry.COLUMN_TASK_PRIORITY +" TEXT NOT NULL,"
                + TaskDBContract.TaskEntry.COLUMN_TASK_LOCATION +" TEXT NOT NULL,"
                +TaskDBContract.TaskEntry.COLUMN_TASK_DUETIME +" TEXT NOT NULL,"
                +TaskDBContract.TaskEntry.COLUMN_TASK_ASSIGNEDWORKER +" TEXT NOT NULL,"
                +TaskDBContract.TaskEntry.COLUMN_TASK_STATUS+" TEXT NOT NULL,"
                +TaskDBContract.TaskEntry.COLUMN_TASK_APPROVLE + " TEXT NOT NULL  UNIQUE ON CONFLICT REPLACE)";
        db.execSQL(SQL_CREATE_LOCATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TaskDBContract.TaskEntry.TABLE_NAME);
        onCreate(db);

    }
}
