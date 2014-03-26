package com.hubhead.models.ActionModels;

import com.hubhead.models.NotificationActionModel;
import com.hubhead.R;

import java.util.List;

public class RemoveRolesActionModel extends NotificationActionModel implements IActionModel {
    public String actionName;
    public long dt;
    public List<UserModel> users;

    public RemoveRolesActionModel(String actionName, long dt) {
        this.actionName = actionName;
        this.dt = dt;
    }

    @Override
    public String toString() {
        String result = "";

        for (UserModel user : users) {
            result += "<b>" + user.toString() + "</b> " + context.getResources().getString(R.string.action_remove_from_sphere) + ",";
        }
        result = result.substring(0, result.length() - 1);
        return result;
    }

    @Override
    public int getImgResource() {
        return R.drawable.action_remove_member;
    }
}