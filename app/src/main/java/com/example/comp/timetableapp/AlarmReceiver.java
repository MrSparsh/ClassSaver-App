package com.example.comp.timetableapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

/**
 * Created by Mayank on 11-10-2017.
 */

public class AlarmReceiver extends BroadcastReceiver {


    public static int getAlarmId(Context context) {
        SharedPreferences preferences1 = PreferenceManager.getDefaultSharedPreferences(context);
        int alarmId1 = preferences1.getInt("ALARM1", 1);
        SharedPreferences.Editor editor1 = preferences1.edit();
        editor1.putInt("ALARM1", alarmId1 + 1).apply();
        return alarmId1;
    }

    @Override
    public void onReceive(Context context, Intent intent) {


        Intent service_intent = new Intent(context, MainActivity.class);

        service_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,getAlarmId(context), service_intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder=(NotificationCompat.Builder)new NotificationCompat.Builder(context)
                .setContentTitle("Class of "+intent.getStringExtra("subjectname")+" in 15 minutes "+" in "+intent.getStringExtra("venue"))
                .setContentText(intent.getStringExtra("subjectname"))
                .setSmallIcon(R.drawable.time_table_icon)
                .setContentIntent(pendingIntent);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);

        notificationManager.notify(0,builder.build());
       // context.startService(service_intent);

    }

}