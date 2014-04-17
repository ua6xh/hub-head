package com.hubhead.models;


import android.content.ContentResolver;
import android.database.Cursor;

import com.hubhead.contentprovider.SpheresContentProvider;

import java.util.HashMap;
import java.util.Map;

public class SphereModel {
    private String TAG = ((Object) this).getClass().getCanonicalName();
    public long id = 0;
    public long circle_id = 0;
    public String name = "";
    public int user_id;
    public int sphere_group_id;
    public int archived;
    public int deleted;
    public String followers;
    public String roles;
    public long create_date;
    public long archived_time;
    public long update_time;

    public SphereModel() {
    }

    public SphereModel(int id, int circle_id, String name) {
        this.id = id;
        this.circle_id = circle_id;
        this.name = name;
    }

    public SphereModel(Cursor c) {
        id = c.getLong(SpheresContentProvider.ID_INDEX);
        name = c.getString(SpheresContentProvider.NAME_INDEX);
        circle_id = c.getLong(SpheresContentProvider.CIRCLE_ID_INDEX);
    }


    public long getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return "id = "+ id + "\n name = " + name + "\n circle_id = " + circle_id + "\n\n";
    }

    public String getName(){
        return name;
    }

    public static Map<Long, SphereModel> getMap(ContentResolver contentResolver, String selection, String... selectionArgs) {
        Cursor cursor = contentResolver.query(SpheresContentProvider.SPHERE_CONTENT_URI, SpheresContentProvider.QUERY_COLUMNS, selection, selectionArgs, null);
        Map<Long,SphereModel> result = new HashMap<Long, SphereModel>();
        if (cursor == null) {
            return result;
        }

        try {
            if (cursor.moveToFirst()) {
                do {
                    SphereModel sphereModel = new SphereModel(cursor);
                    result.put(sphereModel.getId(), sphereModel);
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }

        return result;
    }
}
