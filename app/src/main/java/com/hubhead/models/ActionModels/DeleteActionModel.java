package com.hubhead.models.ActionModels;


import com.hubhead.models.NotificationActionModel;
import com.hubhead.R;

public class DeleteActionModel extends NotificationActionModel implements IActionModel {
    public long dt;
    public String name;
    public int value;


    public DeleteActionModel(String name, long dt, int value) {
        this.name = name;
        this.dt = dt;
        this.value = value;
    }

    public String toString() {
        return context.getResources().getString(R.string.action_delete);
    }

    @Override
    public int getImgResource() {
        return R.drawable.action_delete;
    }
}
