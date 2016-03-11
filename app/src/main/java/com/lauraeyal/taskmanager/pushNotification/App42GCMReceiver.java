
package com.lauraeyal.taskmanager.pushNotification;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.lauraeyal.taskmanager.activities.LoginActivity;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * @author Vishnu Garg
 *
 */
public class App42GCMReceiver  extends WakefulBroadcastReceiver {
	public static int badgeCounter =0 ;
	 /* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	    public void onReceive(Context context, Intent intent) {
		    System.out.println(App42GCMService.class.getName());
	        ComponentName comp = new ComponentName(context.getPackageName(),
	        		App42GCMService.class.getName());
	        startWakefulService(context, (intent.setComponent(comp)));
	        setResultCode(Activity.RESULT_OK);
		ShortcutBadger.applyCount(context, ++badgeCounter); //for 1.1.4
	}
}