package com.hubhead.models;

import org.json.JSONArray;

public class NotificationModel{
    public static final byte TYPE_TASK = 1;
    public static final byte TYPE_SPHERE = 2;
    private static final String TAG = "NotificationModel";
    public int id = -1;
    public int type_notification;
    public int messages_count;
    public int create_date;
    public String model_name;
    public int circle_id;
    public int sphere_id;
    public Long dt;
    public JSONArray groups;
    //public List<NotificationGroupModel> groupsList = new ArrayList<NotificationGroupModel>();
    public String room_name;

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
}