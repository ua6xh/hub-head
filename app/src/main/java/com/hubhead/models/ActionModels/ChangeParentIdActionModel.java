package com.hubhead.models.ActionModels;


import com.hubhead.models.NotificationActionModel;
import com.hubhead.R;

public class ChangeParentIdActionModel extends NotificationActionModel implements IActionModel {
    public long dt;
    public String name;
    public int parent_id;
    public String parent_name;

    public ChangeParentIdActionModel(String name, long dt, int parent_id, String parent_name) {
        this.name = name;
        this.dt = dt;
        this.parent_id = parent_id;
        this.parent_name = parent_name;
    }

    public String toString() {
        if(this.parent_id == 0){
            return context.getResources().getString(R.string.action_chg_par_id_move_to_root);
        }
        return context.getResources().getString(R.string.action_chg_par_id_move_to) + " \"" + parent_name + "\"";
    }

    @Override
    public int getImgResource() {
        return R.drawable.action_change_parent_id;
    }
}
