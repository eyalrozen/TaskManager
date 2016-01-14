package com.lauraeyal.taskmanager.dal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Eyal on 11/21/2015.
 */
public class MembersDBHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database
    // version.
    private static final int DATABASE_VERSION = 2;

    private static final String DATABASE_NAME = "members.db";

    public MembersDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a table to hold the friends - get the data to create table from DB contract;
        final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE "
                + MembersDBContract.MembersEntry.TABLE_NAME + " (" + MembersDBContract.MembersEntry._ID
                + " INTEGER PRIMARY KEY," + MembersDBContract.MembersEntry.COLUMN_MEMBER_USERNAME +" TEXT NOT NULL,"
                + MembersDBContract.MembersEntry.COLUMN_MEMBER_PASSWORD +" TEXT NOT NULL,"+
                MembersDBContract.MembersEntry.COLUMN_MEMBER_PHONE+" TEXT NOT NULL,"+
                MembersDBContract.MembersEntry.COLUMN_MEMBER_MAILSENT+" TEXT NOT NULL,"
                + MembersDBContract.MembersEntry.COLUMN_MEMBER_TEAM+" TEXT NOT NULL,"
                + MembersDBContract.MembersEntry.COLUMN_MEMBER_PERMISSION+ " TEXT NOT NULL)";
        db.execSQL(SQL_CREATE_LOCATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + MembersDBContract.MembersEntry.TABLE_NAME);
        onCreate(db);

    }
}
