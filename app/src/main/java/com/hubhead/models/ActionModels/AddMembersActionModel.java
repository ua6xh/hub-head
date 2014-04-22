package com.hubhead.models.ActionModels;


import com.hubhead.R;
import com.hubhead.models.NotificationActionModel;

import java.util.List;

public class AddMembersActionModel extends NotificationActionModel implements IActionModel{
    public String actionName;
    public long dt;
    public List<UserModel> users;

    public AddMembersActionModel(String actionName, long dt) {
        this.actionName = actionName;
        this.dt = dt;
    }

    @Override
    public String toString() {
        String result = "";
        if (users.size() > 1) {
            result = context.getResources().getString(R.string.actions_add_members) + ":";
        } else {
            result = context.getResources().getString(R.string.actions_add_member) + " ";
        }

        for (UserModel user : users) {
            result += " <b>" + user.toString() + "</b>,";
        }
        result = result.substring(0, result.length() - 1);
        return result;
    }

    @Override
    public int getImgResource() {
        return R.drawable.action_add_member;
    }
}