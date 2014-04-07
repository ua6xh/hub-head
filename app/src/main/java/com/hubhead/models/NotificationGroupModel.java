package com.hubhead.models;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NotificationGroupModel{
    public String id;
    public long dt;
    public int user_id;
    public List<NotificationActionModel> actions = new ArrayList<NotificationActionModel>();

    public NotificationGroupModel(String id, long dt, int user_id){
        this.id = id;
        this.dt = dt;
        this.user_id = user_id;
    }

    public int getId() {
        return -1;
    }

    public String getDt() {
        return new SimpleDateFormat("d MMM, HH:mm").format(new Date(dt * 1000));
    }

    public String toString(){
        String result = "";
        for (NotificationActionModel action: actions){
            result += action.toString() + "<br />";
        }
        return result;
    }
}
