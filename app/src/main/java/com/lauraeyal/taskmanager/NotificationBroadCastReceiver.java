package com.lauraeyal.taskmanager;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.lauraeyal.taskmanager.activities.TasksActivity;
import com.lauraeyal.taskmanager.common.AppConst;
import com.parse.Parse;
import com.parse.ParseUser;


public class NotificationBroadCastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// Create the Notification.
		// Fetch the message from the bundle.
		String description = intent.getStringExtra("Description");
        String teamMember = intent.getStringExtra("TeamMember");
		// crate the notification.
		/*if(ParseUser.getCurrentUser().getUsername().equals(teamMember))
		        createNotification(context, description);*/
            createNotification(context, description);
	}

	/*
	 * Crate notification with a specific message.
	 */
	public void createNotification(Context context, String message) {

        //User support library
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context).setSmallIcon(R.drawable.
                        side_nav_bar)
                        .setAutoCancel(true)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_info_outline_black_24dp))
                        .setContentTitle("New Task recieved")
                        .setContentText(message);

        Intent resultIntent = new Intent(context, TasksActivity.class);


        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        int mNotificationId = 001;
// Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
// Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

       /* public static void onCancelNotification(Context context,int notID)
        {
                NotificationManager mNotificationManager =   (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                mNotificationManager.cancelAll();
        }*/
}
