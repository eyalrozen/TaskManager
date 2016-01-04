package com.lauraeyal.taskmanager.dal;

import android.provider.BaseColumns;

/**
 * Created by Eyal on 11/21/2015.
 */
public class TaskDBContract {
    public static final class TaskEntry implements BaseColumns {

        // Table name
        public static final String TABLE_NAME = "Tasks";

        public static final String COLUMN_TASK_DESCRIPTION = "Task_description";
        public static final String COLUMN_TASK_CATEGORY = "Task_category";
        public static final String COLUMN_TASK_PRIORITY = "Task_priority";
        public static final String COLUMN_TASK_LOCATION = "Task_location";
        public static final String COLUMN_TASK_DUETIME = "Task_due_time";
        public static final String COLUMN_TASK_ASSIGNEDWORKER = "Task_assigned_worker";
        public static final String COLUMN_TASK_STATUS = "Task_status";
        public static final String COLUMN_TASK_APPROVLE = "Task_approvle";
    }
}
