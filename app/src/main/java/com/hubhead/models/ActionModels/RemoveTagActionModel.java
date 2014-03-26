package com.hubhead.models.ActionModels;

import com.hubhead.models.NotificationActionModel;
import com.hubhead.R;

import java.util.List;

public class RemoveTagActionModel extends NotificationActionModel implements IActionModel{
    public String actionName;
    public long dt;
    public List<TagModel> tags;

    public RemoveTagActionModel(String actionName, long dt) {
        this.actionName = actionName;
        this.dt = dt;
    }

    @Override
    public String toString() {
        String result = "";
        if (tags.size() > 1) {
            result = context.getResources().getString(R.string.action_remove_tags);
        } else {
            result = context.getResources().getString(R.string.action_remove_tag);
        }

        for (TagModel tag : tags) {
            result += " <font color='" + tag.color + "'>" + tag.toString() + "</font>,";
        }
        result = result.substring(0, result.length() - 1);
        return result;
    }

    @Override
    public int getImgResource() {
        return R.drawable.action_add_tag;
    }
}
