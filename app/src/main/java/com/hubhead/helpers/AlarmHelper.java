package com.hubhead.helpers;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hubhead.service.AlarmReceiver;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AlarmHelper {

    private static final String TAG = "com.hubhead.helpers.AlarmHelper";

    public static void testAlarm1(Context context) {
        Calendar when = Calendar.getInstance();
        when.add(Calendar.SECOND, 10);
        setAlarm(context, when, "extra 1");
    }

    public static void testAlarm2(Context context) {
        Calendar when = Calendar.getInstance();
        when.add(Calendar.SECOND, 20);
        setAlarm(context, when, "extra 2");
    }

    public static void setAlarm(Context context, String timestamp, long reminderId) {
        long timestampLong = Long.parseLong(timestamp) * 1000;
        Date d = new Date(timestampLong);
        Calendar when = Calendar.getInstance();
        when.setTime(d);
        setAlarm(context, when, "alarm_" + reminderId);
    }

    @SuppressLint("SimpleDateFormat")
    private static void setAlarm(Context context, Calendar when, String extra) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, when.getTimeInMillis(), AlarmManager.INTERVAL_DAY, getPendingIntent(context.getApplicationContext(), extra));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Log.i(TAG, "Alarm set " + sdf.format(when.getTime()));
    }

    private static PendingIntent getPendingIntent(Context context, String extra) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction(extra);
        intent.putExtra("extra", extra);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}