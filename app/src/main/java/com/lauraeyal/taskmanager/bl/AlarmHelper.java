package com.lauraeyal.taskmanager.bl;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.lauraeyal.taskmanager.NotificationBroadCastReceiver;
import com.lauraeyal.taskmanager.common.AppConst;

import java.util.concurrent.TimeUnit;


public class AlarmHelper {
	public static void setAlarm(Context c,String taskDescription,String teamMember ){
		// create the intent, with the receiver that should handle the alarm.
		AlarmManager alarmMgr = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(c, NotificationBroadCastReceiver.class);
		intent.setAction(AppConst.ACTION_ALARM);
		intent.putExtra("Description", taskDescription);
		intent.putExtra("TeamMember",teamMember);
		PendingIntent alarmIntent = PendingIntent.getBroadcast(c, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				SystemClock.elapsedRealtime() +
						TimeUnit.SECONDS.toMillis(1), alarmIntent);
	}

	public static void cancelAlarm(Context context,int alarmID) {
		//NotificationBroadCastReceiver.onCancelNotification(context,alarmID);

	}

}
