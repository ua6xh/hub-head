package com.hubhead.models.ActionModels;

import com.hubhead.models.NotificationActionModel;
import com.hubhead.R;


public class CreateActionModel extends NotificationActionModel implements IActionModel {
    public long dt;
    public String name;

    public  CreateActionModel(String name, long dt){
        this.name = name;
        this.dt = dt;
    }

    public String toString() {
        return context.getResources().getString(R.string.action_create_task);
    }

    @Override
    public int getImgResource() {
        return R.drawable.action_create;
    }
}
