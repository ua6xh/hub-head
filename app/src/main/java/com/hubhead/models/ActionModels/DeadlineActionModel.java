package com.hubhead.models.ActionModels;


import com.hubhead.models.NotificationActionModel;
import com.hubhead.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DeadlineActionModel extends NotificationActionModel implements IActionModel {
    public long dt;
    public String name;
    public String date;

    public DeadlineActionModel(String name, long dt, String date) {
        this.name = name;
        this.dt = dt;
        this.date = date;
    }

    public String toString() {
        if(date.equals("1970-01-01 00:00:00")){
            return context.getResources().getString(R.string.action_deadline_remove);
        }
        long deadline = Long.parseLong(date);
        return context.getResources().getString(R.string.action_deadline) + " " + new SimpleDateFormat("d MMM, HH:mm").format(new Date(deadline * 1000));
    }

    @Override
    public int getImgResource() {
        return R.drawable.action_deadline;
    }
}
