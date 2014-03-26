/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hubhead.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


import com.google.android.gms.gcm.GoogleCloudMessaging;

import com.hubhead.helpers.NotificationHelper;


/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class GcmIntentService extends IntentService {
    private static int mNotificationId = 0;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    public static final String TAG = "com.hubhead.GCMIntentService";

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM will be
             * extended in the future with new message types, just ignore any message types you're
             * not interested in, or that you don't recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                //sendNotification("Send error: " + extras.toString(), -1);
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                //sendNotification("Deleted messages on server: " + extras.toString(), -1);
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
                Log.i(TAG, "extras: " + extras);
                // Post notification of received message.
                try {
                    int circleId = Integer.parseInt(extras.getString("circle_id"));
                    if (circleId == 0) {
                        circleId = extras.getInt("circle_id");
                    }
                    sendNotification(extras.getString("title"), circleId);
                } catch (ClassCastException e) {
                    Log.d(TAG, e.getMessage());
                }
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg, int circleId) {
        NotificationHelper notificationInstance = NotificationHelper.getInstance(this);
        Log.d(TAG, "mNotificationId: " + mNotificationId);
        if (mNotificationId != 0 && notificationInstance.issetNotification(mNotificationId)) {
            notificationInstance.updateInfoNotification(mNotificationId, msg, circleId);
        } else {
            Log.d(TAG, "circleId before call create: " + circleId);
            mNotificationId = notificationInstance.createInfoNotification(msg, circleId);
        }
    }
}
