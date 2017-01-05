package com.auvert.raphaela.myproject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by Raph on 30/12/2016.
 */

public class AlarmReceiver extends BroadcastReceiver  {
    private static final String TAG = "AlarmReceiver";
    private String authority ="com.auvert.raphaela.DeckContentProvider";

    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context=context;

        Log.d(TAG,"Dans onReceive");
        boolean news=false;
        Uri.Builder builder = new Uri.Builder();
        Uri uri = builder.scheme("content")
                .authority(authority)
                .appendPath("deck_table")
                .build();

        /*
        long date=System.currentTimeMillis();
        long milli=86400000;

        String requestData=" "+date+"> time + (niveau*"+milli+" )";
*       */

        Cursor cursor = context.getContentResolver().query( uri, new String[]{"_id", "nom", "time"},
                null, null, null);

        String txt="";

        int cmp=0;
        if(cursor.getCount()>0){

            cursor.moveToFirst();
            Log.d(TAG,"number of entries "+cursor.getCount());
            while (!cursor.isAfterLast()) {

                Long time=cursor.getLong(cursor.getColumnIndex("time"));
                String nom=cursor.getString(cursor.getColumnIndex("nom"));
                String timeStr="";
                if(time==0){
                    timeStr="NEVER USE";
                }else{
                    timeStr= DateFormat.getDateTimeInstance().format(new Date(time)).toString();
                }

                if(cmp>0){
                    txt+="\n";
                }
                cmp+=1;
                txt+=nom+" -> "+timeStr;
                cursor.moveToNext();
            }
            if(cmp>0){
                news=true;
            }
        }
        txt+="\n";

        Log.d(TAG,"txt -> "+txt);

        NotificationManager mNotifyMgr = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
        Notification.Builder mBuilder =
                new Notification.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Apprendre")
                        .setAutoCancel(true);
        if(news){
            mBuilder.setContentText("Deck without play since longue time")
                    .setStyle(new Notification.BigTextStyle().bigText(txt));
        }else{
            mBuilder.setContentText("No deck to play");
        }

        Intent resultIntent = new Intent(context, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        int mNotificationId = 1;

        mNotifyMgr.cancel(mNotificationId);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());

    }
}