package com.hubhead.models;

import android.content.Context;
import com.hubhead.R;

public class NotificationActionModel {
    protected Context context;
    public long dt;

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
