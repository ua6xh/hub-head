package com.hubhead.models;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.hubhead.parsers.ParseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
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
    //public List<NotificationGroupModel> groupsList = new ArrayList<NotificationGroupModel>();
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

        if (this.groups.length() > 0) {
            JSONObject lastGroup = (JSONObject) this.groups.get(0);
            JSONObject actions = lastGroup.getJSONObject("actions");
            this.last_action_user_id = lastGroup.getInt("user_id");
            String contactKey = this.circle_id + "_" + last_action_user_id;
            Log.d(TAG, "contactMap:" + contactMap);
            Log.d(TAG, "contactKey:" + contactKey);
            if(contactMap.containsKey(contactKey)){
                this.last_action_author = contactMap.get(contactKey).name;
            }
            Log.d(TAG, "this.last_action_author:" + this.last_action_author);

            Iterator actionsIterator = actions.keys();
            int circleId = this.circle_id;
            while (actionsIterator.hasNext()) {
                String key = (String) actionsIterator.next();
                JSONObject action = actions.getJSONObject(key);

                long dt = action.getLong("dt");
                if (key.equals("add-tags")) {
                    last_action_text = ParseHelper.getAddTagActionModel(key, action, dt, context).toString();
                } else if (key.equals("remove-tags")) {
                    last_action_text = ParseHelper.getRemoveTagActionModel(key, action, dt, context).toString();
                } else if (key.equals("remove-members")) {
                    last_action_text = ParseHelper.getRemoveMemberActionModel(key, action, dt, context, contactMap, circleId).toString();
                } else if (key.equals("add-members")) {
                    last_action_text = ParseHelper.getAddMemberActionModel(key, action, dt, context, contactMap, circleId).toString();
                } else if (key.equals("create")) {
                    last_action_text = ParseHelper.getCreateActionModel(key, action, dt, context).toString();
                } else if (key.equals("status")) {
                    last_action_text = ParseHelper.getStatusActionModel(key, action, dt, context).toString();
                } else if (key.equals("deadline")) {
                    last_action_text = ParseHelper.getDeadlineActionModel(key, action, dt, context).toString();
                } else if (key.equals("parent_id")) {
                    last_action_text = ParseHelper.getChangeParentIdActionModel(key, action, dt, context).toString();
                } else if (key.equals("sphere_id")) {
                    last_action_text = ParseHelper.getChangeSphereIdActionModel(key, action, dt, context, sphereMap).toString();
                } else if (key.equals("deleted")) {
                    last_action_text = ParseHelper.getDeleteActionModel(key, action, dt, context).toString();
                } else if (key.equals("add-roles")) {
                    last_action_text = ParseHelper.getAddRolesActionModel(key, action, dt, context, contactMap, circleId).toString();
                } else if (key.equals("remove-roles")) {
                    last_action_text = ParseHelper.getRemoveRolesActionModel(key, action, dt, context, contactMap, circleId).toString();
                } else if (key.equals("sphere-archived")) {
                    last_action_text = ParseHelper.getSphereArchivedActionModel(key, action, dt, context).toString();
                }
                break;
            }
        }
        if (roomName.indexOf("task") == 0) {
            this.type_notification = NotificationModel.TYPE_TASK;
        } else if (roomName.indexOf("sphere") == 0) {
            this.type_notification = NotificationModel.TYPE_SPHERE;
        }
        this.id = this.convertToId(this.type_notification, roomName);
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
        cv.put("groups", groups.toString());
        cv.put("groups_count", groups.length());
        cv.put("last_action_user_id", last_action_user_id);
        cv.put("last_action_dt", last_action_dt);
        cv.put("last_action_text", last_action_text);
        cv.put("last_action_author", last_action_author);

        return cv;
    }
}