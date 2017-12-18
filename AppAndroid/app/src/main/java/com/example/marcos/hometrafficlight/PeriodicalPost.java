package com.example.marcos.hometrafficlight;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.support.annotation.RequiresApi;
import android.util.Log;

public class PeriodicalPost extends BroadcastReceiver {

    private final String TAG = "PeriodicalPost";

    public PeriodicalPost(){}

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "PostRestTask");
        wakeLock.acquire();

        Log.d(TAG, "onReceive: se ejecuta el checking");

        try {
            String activityName = context.getClass().getSimpleName();
            Log.d(TAG, activityName);
            SendInfoRest sendInfoRest = new SendInfoRest(context);
            sendInfoRest.execute();

        } catch (Exception e){
            e.printStackTrace();
        }

        wakeLock.release();
    }

    public void setPeriodicalRestCheck(Context context) {
        Log.d(TAG, "setPeriodicalRestCheck");

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent periodicalRestCheck = new Intent(context, PeriodicalPost.class);
        PendingIntent broadcastFromPeriodicalRestCheck = PendingIntent.getBroadcast(context, 0, periodicalRestCheck, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),  100 * 10 * 2, broadcastFromPeriodicalRestCheck);
    }

    public void cancelPeriodicalRestCheck(Context context) {
        Log.d(TAG, "cancelPeriodicalRestCheck");

        Intent periodicalRestCheck = new Intent(context, PeriodicalPost.class);
        PendingIntent broadcastFromPeriodicalRestCheck = PendingIntent.getBroadcast(context, 0, periodicalRestCheck, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(broadcastFromPeriodicalRestCheck);
    }
}