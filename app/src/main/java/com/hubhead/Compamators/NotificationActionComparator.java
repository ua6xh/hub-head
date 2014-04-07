package com.hubhead.compamators;

import android.util.Log;

import com.hubhead.models.NotificationActionModel;

import java.util.Comparator;

public class NotificationActionComparator implements Comparator<NotificationActionModel> {
    @Override
    public int compare(NotificationActionModel notificationActionModel, NotificationActionModel notificationActionModel2) {
        Log.d("com.hubhead.NotificationActionComparator", notificationActionModel.dt + ":" + notificationActionModel2.dt);
        int result = 0;
        if(notificationActionModel.dt < notificationActionModel2.dt){
            result = 1;
        }else if(notificationActionModel.dt > notificationActionModel2.dt){
            result = -1;
        }
        return result;
    }
}
