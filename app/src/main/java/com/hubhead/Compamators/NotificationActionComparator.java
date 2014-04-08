package com.hubhead.compamators;


import com.hubhead.models.NotificationActionModel;

import java.util.Comparator;

public class NotificationActionComparator implements Comparator<NotificationActionModel> {
    @Override
    public int compare(NotificationActionModel notificationActionModel, NotificationActionModel notificationActionModel2) {
        int result = 0;
        if(notificationActionModel.dt < notificationActionModel2.dt){
            result = 1;
        }else if(notificationActionModel.dt > notificationActionModel2.dt){
            result = -1;
        }
        return result;
    }
}
