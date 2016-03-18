package com.lauraeyal.taskmanager.bl;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.lauraeyal.taskmanager.activities.LoginActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Eyal on 2/20/2016.
 */
public class TimeService extends Service {
    // constant
   // public static final long NOTIFY_INTERVAL = 10 * 60000; // 10 seconds
    SharedPreferences sharedpreferences;

    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
    private Timer mTimer = null;
    IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public TimeService getServerInstance() {
            return TimeService.this;
        }
    }

    @Override
    public void onCreate() {
        // cancel if already existed
        if (mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
        }
        SetTimerInterval();
    }

    /**
     * Set refresh timer interval
     */
    public void SetTimerInterval()
    {
        sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        int refreshTimer=sharedpreferences.getInt("autoRefresh", 0);
        // schedule task
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, refreshTimer*60000);
    }
    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    // display toast
                    sendMessage();
                }

            });
        }

        private void sendMessage() {
            Intent intent = new Intent("my-event");
            // add data

            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        }
    }
}