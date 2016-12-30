package com.auvert.raphaela.myproject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import static android.app.AlarmManager.RTC;

/**
 * Created by Raph on 29/12/2016.
 */

public class BackgroundService extends Service implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = "BackgroundService";

    private int timeValuePref;
    private Boolean prefChange;
    private Object mutex=new Object();


    private SharedPreferences pref;
    private AlarmManager manager;
    private PendingIntent contentIntent;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"onCreate");
        prefChange=false;

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        pref.registerOnSharedPreferenceChangeListener(this);

    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(TAG,"OnStartCommand");

        manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        contentIntent = PendingIntent.getBroadcast(this, 0, new Intent(this, AlarmReceiver.class), 0);

        /**
         * INIT VALUE OF TIMER
         */
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String value=preferences.getString("timeDeckNotif","1");

        try{
            this.timeValuePref=Integer.parseInt(value);
        }catch (NumberFormatException e ){
            this.timeValuePref=1;
        }

        startRunnable();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        manager.cancel(contentIntent);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void startRunnable() {
        new Thread() {
            @Override
            public void run() {
                boolean doIt = true;

                while (doIt) {
                    synchronized (mutex) {
                        try {
                            int value=timeValuePref*1000*60;
                            if(timeValuePref>0){
                                scheduleNotification(value);
                            }
                            Log.d(TAG, "pref GO ON WAIT");
                            mutex.wait(value);
                            if (prefChange) {
                                Log.d(TAG, "pref CHANGED OUT OF WAIT");
                                manager.cancel(contentIntent);
                                prefChange = false;
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.start();
    }

    private void scheduleNotification(int value) {
        Log.d(TAG, "new alarm");

        manager.set(RTC, System.currentTimeMillis()+value, contentIntent);

    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d("PREFERENCE CHANGE", "[" + key + "]");
        if ("timeDeckNotif".equals(key)) {
            String keys = pref.getString("timeDeckNotif", "");
            Log.d(TAG,"valeur timeDeckNotif ="+keys);
            int value=Integer.parseInt(keys);

            synchronized (mutex){
                Log.d(TAG,"pref CHANGED NOTIFY");
                timeValuePref =value;
                prefChange=true;
                mutex.notify();
            }

        }
    }
}
