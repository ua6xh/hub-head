package com.hubhead.models;

import android.content.ContentValues;
import android.util.Log;

import org.json.JSONArray;

public class NotificationModel {
    private final String TAG = ((Object) this).getClass().getCanonicalName();
    public static final byte TYPE_TASK = 1;
    public static final byte TYPE_SPHERE = 2;
    public int id = -1;
    public int type_notification = 0;
    public int messages_count = 0;
    public int create_date = 0;
    public String model_name = "";
    public int circle_id = 0;
    public int sphere_id = 0;
    public Long dt = (long) 0;
    public JSONArray groups;
    //public List<NotificationGroupModel> groupsList = new ArrayList<NotificationGroupModel>();
    public String room_name = "";

    public int getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return "roomName:" + room_name + "; modelName:" + model_name + "; type:" + type_notification;
    }

    public int convertToId(int type_notification, String room_name) {
        String[] paths = room_name.split("_");
        return Integer.parseInt(Integer.toString(type_notification) + paths[1]);
    }

    public ContentValues getContentValues() {
        ContentValues cv = new ContentValues();
        cv.put("_id", id);
        cv.put("messages_count", messages_count);
        cv.put("create_date", create_date);
        cv.put("model_name", model_name);
        cv.put("sphere_id", sphere_id);
        cv.put("circle_id", circle_id);
        cv.put("dt", dt);
        return cv;
    }
}