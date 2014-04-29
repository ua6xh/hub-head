package com.hubhead.helpers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.hubhead.R;
import com.hubhead.ui.CirclesActivity;

import java.util.HashMap;

public class SystemNotificationHelper {

    private static final String TAG = "com.hubhead." + SystemNotificationHelper.class.getSimpleName();

    private static SystemNotificationHelper instance;

    private Context mContext;
    private NotificationManager manager; // Системная утилита, упарляющая уведомлениями
    private int lastId = 1; //постоянно увеличивающееся поле, уникальный номер каждого уведомления
    private HashMap<Integer, Notification> notifications; //массив ключ-значение на все отображаемые пользователю уведомления


    //приватный контструктор для Singleton
    private SystemNotificationHelper(Context context) {
        this.mContext = context;
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notifications = new HashMap<Integer, Notification>();
    }

    /**
     * Получение ссылки на синглтон
     */
    public static SystemNotificationHelper getInstance(Context context) {
        if (instance == null) {
            instance = new SystemNotificationHelper(context);
        } else {
            instance.mContext = context;
        }
        return instance;
    }

    public int createInfoNotification(String message, int circleId) {
        NotificationCompat.Builder mBuilder = getBuilder(message);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(mContext, CirclesActivity.class);
        Log.d(TAG, "CircleId: " + circleId);
        resultIntent.putExtra("circle_id", circleId);
        Log.d(TAG, "CircleId extras Int: " + resultIntent.getIntExtra("circle_id", -100));
        resultIntent.putExtra("notification", 1);
        resultIntent.putExtra("notification_id", lastId);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(CirclesActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        Notification notification = mBuilder.build();
        mNotificationManager.notify(lastId, notification); // отображаем его пользователю.
        notifications.put(lastId, notification); //теперь мы можем обращаться к нему по id
        return lastId++;
    }

    public void removeNotification(int notificationId) {
        notifications.remove(notificationId);
    }

    public boolean issetNotification(int id) {
        return notifications.containsKey(id);
    }

    public void updateInfoNotification(int notificationId, String message, int circleId) {
        NotificationCompat.Builder mBuilder = getBuilder(message);

        Intent notificationIntent = new Intent(mContext, CirclesActivity.class); // по клику на уведомлении откроется HomeActivity
        Log.d(TAG, "circle_id: " + circleId);
        notificationIntent.putExtra("circle_id", circleId);
        Log.d(TAG, "circle_id get: " + notificationIntent.getIntExtra("circle_id", -100));
        notificationIntent.putExtra("notification", 1);
        notificationIntent.putExtra("notification_id", notificationId);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(CirclesActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        Log.d(TAG, "updateInfoNotification:" + circleId + " " + notificationId);
        Notification notification = mBuilder.build();

        manager.notify(notificationId, notification);
    }

    private NotificationCompat.Builder getBuilder(String message) {
        return new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("HubHead")
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setTicker(message) //текст, который отобразится вверху статус-бара при создании уведомления
                .setAutoCancel(true) //уведомление закроется по клику на него
                .setWhen(System.currentTimeMillis());
    }
}