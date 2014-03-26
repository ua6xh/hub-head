package com.hubhead.models.ActionModels;


import android.util.Log;

import com.hubhead.models.NotificationActionModel;
import com.hubhead.R;
import com.hubhead.models.SphereModel;

public class ChangeSphereIdActionModel extends NotificationActionModel implements IActionModel {
    private final String TAG = ((Object) this).getClass().getCanonicalName();
    public long dt;
    public String name;
    public long sphere_id;
    public SphereModel sphere;


    public ChangeSphereIdActionModel(String name, long dt, long sphere_id, SphereModel sphere) {
        this.name = name;
        this.dt = dt;
        this.sphere_id = sphere_id;
        this.sphere = sphere;
        Log.d(TAG, "sphere in ChangeSphereIdActionModel:" + sphere);
    }

    public String toString() {
        return context.getResources().getString(R.string.action_chg_sphere_id_move_to) + " \"" + sphere.name + "\"";
    }

    @Override
    public int getImgResource() {
        return R.drawable.action_change_sphere_id;
    }
}
