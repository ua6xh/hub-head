package com.hubhead.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hubhead.helpers.AlarmHelper;

public class OnBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmHelper.setAlarm(context);
        AlarmHelper.testAlarm1(context);
    }
}