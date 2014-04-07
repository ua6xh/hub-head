package com.hubhead.models;

import android.content.ContentValues;
import android.content.Context;

import com.hubhead.helpers.ParseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


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
    public long dt = 0;
    public JSONArray groups = new JSONArray();
    public int last_action_user_id = 0;
    public long last_action_dt = 0;
    public String last_action_text = "";
    public String last_action_author = "";
    public List<NotificationGroupModel> groupsList = new ArrayList<NotificationGroupModel>();
    public String room_name = "";

    public NotificationModel(String roomName, JSONObject room, Map<String, ContactModel> contactMap, Map<Long, SphereModel> sphereMap, Context context) throws JSONException {
        this.room_name = roomName;
        this.messages_count = room.getInt("messages_count");
        this.create_date = room.getInt("create_date");
        this.model_name = room.getString("model_name");
        this.sphere_id = room.getInt("sphere_id");
        this.circle_id = room.getInt("circle_id");
        this.dt = room.getLong("dt");
        this.groups = room.getJSONArray("groups");

        getLastAction(contactMap, sphereMap, context);


        if (roomName.indexOf("task") == 0) {
            this.type_notification = NotificationModel.TYPE_TASK;
        } else if (roomName.indexOf("sphere") == 0) {
            this.type_notification = NotificationModel.TYPE_SPHERE;
        }
        this.id = this.convertToId(this.type_notification, roomName);
    }

    private void getLastAction(Map<String, ContactModel> contactMap, Map<Long, SphereModel> sphereMap, Context context) throws JSONException {
        if (this.groups.length() > 0) {
            JSONObject lastGroup = (JSONObject) this.groups.get(0);
            JSONObject actions = lastGroup.getJSONObject("actions");
            this.last_action_user_id = lastGroup.getInt("user_id");
            String contactKey = this.circle_id + "_" + last_action_user_id;
            if (contactMap.containsKey(contactKey)) {
                this.last_action_author = contactMap.get(contactKey).name;
            }

            Iterator actionsIterator = actions.keys();
            int circleId = this.circle_id;
            long lastDtAction = 0;
            JSONObject lastAction = new JSONObject();
            String lastKey = "";
            if (actionsIterator.hasNext()) {
                lastKey = (String) actionsIterator.next();
                lastAction = actions.getJSONObject(lastKey);
                lastDtAction = lastAction.getLong("dt");
            }
            while (actionsIterator.hasNext()) {
                String key = (String) actionsIterator.next();
                JSONObject action = actions.getJSONObject(key);
                long dtAction = action.getLong("dt");
                if (dtAction > lastDtAction) {
                    lastAction = action;
                    lastKey = key;
                }
            }
            if (lastDtAction != 0 && !lastKey.equals("")) {
                last_action_text = ParseHelper.getAction(lastKey, lastAction, dt, context, contactMap,sphereMap, circleId).toString();
            }
        }
    }

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
        cv.put("groups", groups.toString());
        cv.put("groups_count", groups.length());
        cv.put("last_action_user_id", last_action_user_id);
        cv.put("last_action_dt", last_action_dt);
        cv.put("last_action_text", last_action_text);
        cv.put("last_action_author", last_action_author);

        return cv;
    }
}