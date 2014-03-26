package com.hubhead.models.ActionModels;


import com.hubhead.models.NotificationActionModel;
import com.hubhead.R;

public class SphereArchivedActionModel extends NotificationActionModel implements IActionModel {
    public long dt;
    public String name;
    public int value;


    public SphereArchivedActionModel(String name, long dt, int value) {
        this.name = name;
        this.dt = dt;
        this.value = value;
    }

    public String toString() {
        switch (this.value){
            case 1:
                return context.getResources().getString(R.string.action_archive_sphere_archive);
            case 0:
                return context.getResources().getString(R.string.action_archive_sphere_unarchive);//"Разархивирована";
            default:
                return context.getResources().getString(R.string.action_archive_sphere_undefined);
        }
    }

    @Override
    public int getImgResource() {
        switch (this.value){
            case 1:
                return R.drawable.action_sphere_archived;
            case 0:
                return R.drawable.action_sphere_extract;
            default:
                return R.drawable.action_default;
        }
    }
}
