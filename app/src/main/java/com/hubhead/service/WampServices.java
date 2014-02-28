package com.hubhead.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.test.IsolatedContext;
import android.util.Log;

import de.tavendo.autobahn.Wamp;
import de.tavendo.autobahn.WampConnection;

public class WampServices extends Service {
    private final String TAG = getClass().getCanonicalName();

    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
      //  new WampClient();
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
