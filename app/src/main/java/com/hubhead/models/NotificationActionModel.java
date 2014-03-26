package com.hubhead.models;

import android.content.Context;

import com.hubhead.R;

import java.util.Date;

public class NotificationActionModel {
    public String actionName = "ActionModel";
    public long dt = new Date().getTime();
    protected Context context;


    public int getId(){
        return -1;
    }

    public int getImgResource(){
        return R.drawable.action_default;
    }

    public void setContext(Context context){
        this.context = context;
    }
}
