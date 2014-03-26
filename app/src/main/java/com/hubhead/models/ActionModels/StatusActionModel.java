package com.hubhead.models.ActionModels;

import com.hubhead.models.NotificationActionModel;
import com.hubhead.R;


public class StatusActionModel extends NotificationActionModel implements IActionModel {
    public long dt;
    public String name;
    public int value;

    public StatusActionModel(String name, long dt, int value) {
        this.name = name;
        this.dt = dt;
        this.value = value;
    }

    public String toString() {
        if (this.value == 0) {
            return context.getResources().getString(R.string.action_chg_status_uncomplete);
        } else if (this.value == 1) {
            return context.getResources().getString(R.string.action_chg_status_complete);
        }
        return "НЛО опубликовало это";
    }

    @Override
    public int getImgResource() {
        if (this.value == 0) {
            return R.drawable.action_uncomplete;
        } else if (this.value == 1) {
            return R.drawable.action_complete;
        }
        return R.drawable.action_default;
    }
}
