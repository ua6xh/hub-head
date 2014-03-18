package com.hubhead.service;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.test.IsolatedContext;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hubhead.contentprovider.NotificationsContentProvider;
import com.hubhead.helpers.TextHelper;
import com.hubhead.parsers.AlertDataStructureJson;
import com.hubhead.parsers.ParseHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.tavendo.autobahn.Wamp;
import de.tavendo.autobahn.WampConnection;

public class WampService extends Service {
    private static final int LOCAL_NOTIFICATION_ID = 1;
    private final String TAG = getClass().getCanonicalName();
    private static int mNotificationId = 0;

    public void onCreate() {
        super.onCreate();
        start();
        Log.d(TAG, "onCreate");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public WampService getService() {
            return WampService.this;
        }
    }

    private final WampConnection mConnection = new WampConnection();
    private static final String MY_PREF = "MY_PREF";
    private static final String wsuri = "ws://tm.dev-lds.ru:12126";

    private void start() {

        mConnection.connect(wsuri, new Wamp.ConnectionHandler() {

            @Override
            public void onOpen() {
                sendSessionIdMessage();
            }

            @Override
            public void onClose(int code, String reason) {
                Log.d(TAG, "code: " + code + " reason:" + reason);
            }
        });
    }

    private void sendSessionIdMessage() {
        String cookieSend = TextHelper.getCookieForSend(getSharedPreferences(MY_PREF, IsolatedContext.MODE_PRIVATE).getString("cookies", ""));

        Log.d(TAG, "sendSessionIdMessage");

        mConnection.call("userAuth", Integer.class, new Wamp.CallHandler() {
            @Override
            public void onResult(Object result) {
                Log.d(TAG, "userAuth: onResult:" + result);
                mConnection.subscribe("u_" + result, Event.class, new Wamp.EventHandler() {
                    @Override
                    public void onEvent(String topicUri, Object eventResult) {
                        Event event = (Event) eventResult;
                        Gson gson2 = new Gson();
                        String jsonStr2 = gson2.toJson(event);
                        Log.d(TAG, "EVENT REAL: " + jsonStr2);

                        if (event.type.equals("notification")) {
                            Gson gson = new Gson();
                            Map<String, Object> notification = new HashMap<String, Object>();
                            notification.put("data", event.data);
                            notification.put("alert", event.alert);
                            String jsonStr = gson.toJson(notification);
                            Log.d(TAG, "WAMP:notification: " + jsonStr);
                            ParseHelper parseHelper = new ParseHelper(getApplicationContext());
                            parseHelper.parseNotifications(jsonStr, true);
                        } else if (event.type.equals("system")) {
                            Gson gson = new Gson();
                            String jsonStr = gson.toJson(event.data);
                            try {
                                JSONObject jsonObject = new JSONObject(jsonStr);
                                String systemEvent = jsonObject.getString("event");
                                if (systemEvent.equals("notification-read")) {
                                    long notificationId = TextHelper.convertToNotificationId(jsonObject.getString("model"), jsonObject.getString("model_id"));
                                    Uri itemUri = ContentUris.withAppendedId(NotificationsContentProvider.NOTIFICATION_CONTENT_URI, notificationId);
                                    getContentResolver().delete(itemUri, null, null);
                                }else if (systemEvent.equals("notification-read-all")) {
                                    int circleId = jsonObject.getInt("circle_id");
                                    getContentResolver().delete(NotificationsContentProvider.NOTIFICATION_CONTENT_URI, "circle_id = ?", new String[]{Integer.toString(circleId)});
                                }else if (systemEvent.equals("update")) {
                                    Log.d(TAG, "Event UPDATE local DB");
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.d(TAG, "WAMP:system: " + jsonStr);
                        }
                    }
                });
            }

            @Override
            public void onError(String errorUri, String errorDesc) {
                Log.d(TAG, "userAuth: onError");
            }
        }, cookieSend);
    }

    private static class Event {
        public String type;
        public Object data;
        public AlertDataStructureJson alert;
    }

//    private void sendNotification (AlertDataStructureJson alert) {
//        NotificationHelper notificationInstance = NotificationHelper.getInstance(this);
//        String messageNotification = "in " + alert.model + " add " + alert.event + ": " + alert.value;
//        Log.d(TAG, "mNotificationId: " + mNotificationId);
//        if (mNotificationId != 0 && notificationInstance.issetNotification(mNotificationId)) {
//            notificationInstance.updateInfoNotification(mNotificationId, messageNotification, alert.circle_id);
//        } else {
//            mNotificationId = notificationInstance.createInfoNotification(messageNotification, alert.circle_id);
//        }
//    }

    public void sendNotificationSetReaded(final long notificationId) {
        String roomName = TextHelper.getTypeAndModel(notificationId);

        try {
            mConnection.call("notificationSetReaded", Boolean.class, new Wamp.CallHandler() {
                @Override
                public void onResult(Object result) {
                    Log.d(TAG, "notificationSetReaded: onResult:" + result);

                    //Uri itemUri = ContentUris.withAppendedId(NotificationsContentProvider.NOTIFICATION_CONTENT_URI, notificationId);
                    //getContentResolver().delete(itemUri, null, null);
                    Toast.makeText(getApplicationContext(), "Id record:" + notificationId, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String errorUri, String errorDesc) {
                    Log.d(TAG, "notificationSetReaded: onError" + errorDesc);
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            }, roomName);
        } catch (Exception e) {
            String err = (e.getMessage() == null) ? "Eron don don" : e.getMessage();
            Log.e(TAG, err);
            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
        }
    }
}
