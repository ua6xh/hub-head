package com.hubhead.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

  final String TAG = "com.hubhead.services.AlarmReceiver";

  @Override
  public void onReceive(Context ctx, Intent intent) {
    Log.d(TAG, "onReceive");
    Log.d(TAG, "action = " + intent.getAction());
    Log.d(TAG, "extra = " + intent.getStringExtra("extra"));
  }
}