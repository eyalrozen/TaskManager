package com.lauraeyal.taskmanager.dal;

import android.provider.BaseColumns;

/**
 * Created by Eyal on 11/21/2015.
 */
public class MembersDBContract {
    public static final class MembersEntry implements BaseColumns {

        // Table name
        public static final String TABLE_NAME = "Members";

        public static final String COLUMN_MEMBER_USERNAME = "Member_username";

        public static final String COLUMN_MEMBER_PASSWORD = "Member_password";

        public static final String COLUMN_MEMBER_PHONE = "Member_phone";

        public static final String COLUMN_MEMBER_MAILSENT = "Member_mailsent";

        public static final String COLUMN_MEMBER_TEAM = "Member_team";

        public static final String COLUMN_MEMBER_PERMISSION = "Member_permission";

    }
}
