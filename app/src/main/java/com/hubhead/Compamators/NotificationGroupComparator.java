package com.hubhead.compamators;



import com.hubhead.models.NotificationGroupModel;

import java.util.Comparator;

public class NotificationGroupComparator implements Comparator<NotificationGroupModel> {

    @Override
    public int compare(NotificationGroupModel notificationGroupModel, NotificationGroupModel notificationGroupModel2) {
        int result = 0;
        if(notificationGroupModel.dt < notificationGroupModel2.dt){
            result = 1;
        }else if(notificationGroupModel.dt > notificationGroupModel2.dt){
            result = -1;
        }
        return result;
    }
}
