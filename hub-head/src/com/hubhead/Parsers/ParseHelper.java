package com.hubhead.parsers;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.hubhead.contentprovider.NotificationsContentProvider;
import com.hubhead.models.NotificationModel;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;


public class ParseHelper {
    private static final String TAG = "hub-head: ParseHelper";
    private Context mContext;

    public ParseHelper(Context context) {
        mContext = context;
    }

    public static AllDataStructureJson parseAllData(String response) {
        ObjectMapper mapper = new ObjectMapper();
        AllDataStructureJson allDataStructureJson = null;
        try {
            allDataStructureJson = mapper.readValue(response, AllDataStructureJson.class);
            System.out.println("JACKSON JSON PARSE GOOD!");
        } catch (IOException e) {
            System.out.println("JACKSON json parse bad!:" + response);
            e.printStackTrace();
        }
        return allDataStructureJson;
    }

    public void parseNotifications(String response) {
        try {
            JSONObject json = new JSONObject(response);
            Log.d(TAG, "1");
            JSONObject notificationsObj = json.getJSONObject("data");
            Log.d(TAG, "2");
            if (notificationsObj == null) {
                Log.d(TAG, "3");
                notificationsObj = json.getJSONObject("notifications");
            }
            ContentValues cv = new ContentValues();
            NotificationModel notification = new NotificationModel();

            mContext.getContentResolver().delete(NotificationsContentProvider.NOTIFICATION_CONTENT_URI, null, null);
            Iterator objectsIterator = notificationsObj.keys();
            while (objectsIterator.hasNext()) {
                String roomName = (String) objectsIterator.next();
                JSONObject room = notificationsObj.getJSONObject(roomName);

                notification = createNotification(roomName, room, notification);

                cv.put("_id", notification.id);
                cv.put("messages_count", notification.messages_count);
                cv.put("create_date", notification.create_date);
                cv.put("model_name", notification.model_name);
                cv.put("sphere_id", notification.sphere_id);
                cv.put("circle_id", notification.circle_id);
                cv.put("dt", notification.dt);
                mContext.getContentResolver().insert(NotificationsContentProvider.NOTIFICATION_CONTENT_URI, cv);
                cv.clear();
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing data in parseNotifications: " + e.toString());
        }
    }

    protected NotificationModel createNotification(String roomName, JSONObject room, NotificationModel notification) throws JSONException {
        notification.room_name = roomName;
        notification.messages_count = room.getInt("messages_count");
        notification.create_date = room.getInt("create_date");
        notification.model_name = room.getString("model_name");
        notification.sphere_id = room.getInt("sphere_id");
        notification.circle_id = room.getInt("circle_id");
        notification.dt = room.getLong("dt");
        notification.groups = room.getJSONArray("groups");
        //notification.groupsList = ParseHelper.parseNotificationGroup(notification.groups, context, contactMap, notification.circle_id);
        if (roomName.indexOf("task") == 0) {
            notification.type_notification = NotificationModel.TYPE_TASK;
        } else if (roomName.indexOf("sphere") == 0) {
            notification.type_notification = NotificationModel.TYPE_SPHERE;
        }
        notification.id = notification.convertToId(notification.type_notification, roomName);
        return notification;
    }

//    public static List<NotificationModel> parseNotifications(String response, Context context, Map contactMap) {
//        List<NotificationModel> notifications = new ArrayList<NotificationModel>();
//        Map<Integer, Integer> notificationsMap = new HashMap<Integer, Integer>();
//        try {
//            JSONObject json = new JSONObject(response);
//            Log.d(TAG, "1");
//            JSONObject notificationsObj = json.getJSONObject("data");
//            Log.d(TAG, "2");
//            if (notificationsObj == null) {
//                Log.d(TAG, "3");
//                notificationsObj = json.getJSONObject("notifications");
//            }
//            Iterator objectsIterator = notificationsObj.keys();
//            while (objectsIterator.hasNext()) {
//
//                String roomName = (String) objectsIterator.next();
//                JSONObject room = notificationsObj.getJSONObject(roomName);
//
//                NotificationModel notification = new NotificationModel();
//                notification = createNotification(roomName, room, notification, context, contactMap);
//                notifications.add(notification);
//            }
//
//            Collections.sort(notifications, new NotificationComparator());
//            int i = 0;
//            for (NotificationModel n : notifications) {
//                notificationsMap.put(n.id, i);
//                i++;
//            }
//            EntityActivity.notificationsMap = notificationsMap;
//        } catch (JSONException e) {
//            Log.e(TAG, "Error parsing data in parseNotifications: " + e.toString());
//        }
//        return notifications;
//    }

//    protected static NotificationModel createNotification(String roomName, JSONObject room, NotificationModel notification, Context context, Map contactMap) throws JSONException {
//        notification.room_name = roomName;
//        notification.messages_count = room.getInt("messages_count");
//        notification.create_date = room.getInt("create_date");
//        notification.model_name = room.getString("model_name");
//        notification.sphere_id = room.getInt("sphere_id");
//        notification.circle_id = room.getInt("circle_id");
//        notification.dt = room.getInt("dt");
//        notification.groups = room.getJSONArray("groups");
//        notification.groupsList = ParseHelper.parseNotificationGroup(notification.groups, context, contactMap, notification.circle_id);
//        if (roomName.indexOf("task") == 0) {
//            notification.type_notification = NotificationModel.TYPE_TASK;
//        } else if (roomName.indexOf("sphere") == 0) {
//            notification.type_notification = NotificationModel.TYPE_SPHERE;
//        }
//        notification.id = notification.convertToId(notification.type_notification, roomName);
//        return notification;
//    }
//
//    public static NotificationModel parseNotificationSocket(String response, Context context, Map contactMap) {
//        NotificationModel notification = new NotificationModel();
//        try {
//            JSONObject json = new JSONObject(response);
//            JSONObject notificationObj = json.getJSONObject("data");
//            Iterator iterator = notificationObj.keys();
//            String roomName;
//            JSONObject room;
//
//            roomName = (String) iterator.next();
//            room = notificationObj.getJSONObject(roomName);
//            createNotification(roomName, room, notification, context, contactMap);
//        } catch (JSONException e) {
//            Log.e(TAG, "Error parsing data in parseNotificationSocket: " + e.toString());
//        }
//        return notification;
//    }
//
//
//    public static List<NotificationGroupModel> parseNotificationGroup(JSONArray items, Context context, Map contactMap, int circleId) {
//        List<NotificationGroupModel> groups = new ArrayList<NotificationGroupModel>();
//        try {
//            for (int i = 0; i < items.length(); i++) {
//                JSONObject item = items.getJSONObject(i);
//
//                NotificationGroupModel group = new NotificationGroupModel(item.getString("id"), item.getLong("dt"), item.getInt("user_id"));
//                JSONObject actions = item.getJSONObject("actions");
//                Iterator actionsIterator = actions.keys();
//
//                while (actionsIterator.hasNext()) {
//                    String key = (String) actionsIterator.next();
//                    JSONObject action = actions.getJSONObject(key);
//
//                    long dt = action.getLong("dt");
//
//                    if (key.equals("add-tags")) {
//                        group.actions.add(getAddTagActionModel(key, action, dt, context));
//                    } else if (key.equals("remove-tags")) {
//                        group.actions.add(getRemoveTagActionModel(key, action, dt, context));
//                    } else if (key.equals("remove-members")) {
//                        group.actions.add(getRemoveMemberActionModel(key, action, dt, context, contactMap, circleId));
//                    } else if (key.equals("add-members")) {
//                        group.actions.add(getAddMemberActionModel(key, action, dt, context, contactMap, circleId));
//                    } else if (key.equals("create")) {
//                        group.actions.add(getCreateActionModel(key, action, dt, context));
//                    } else if (key.equals("status")) {
//                        group.actions.add(getStatusActionModel(key, action, dt, context));
//                    } else if (key.equals("deadline")) {
//                        group.actions.add(getDeadlineActionModel(key, action, dt, context));
//                    } else if (key.equals("parent_id")) {
//                        group.actions.add(getChangeParentIdActionModel(key, action, dt, context));
//                    } else if (key.equals("sphere_id")) {
//                        group.actions.add(getChangeSphereIdActionModel(key, action, dt, context));
//                    } else if (key.equals("deleted")) {
//                        group.actions.add(getDeleteActionModel(key, action, dt, context));
//                    } else if (key.equals("add-roles")) {
//                        group.actions.add(getAddRolesActionModel(key, action, dt, context, contactMap, circleId));
//                    } else if (key.equals("remove-roles")) {
//                        group.actions.add(getRemoveRolesActionModel(key, action, dt, context, contactMap, circleId));
//                    } else if (key.equals("sphere-archived")) {
//                        group.actions.add(getSphereArchivedActionModel(key, action, dt, context));
//                    }
//                }
//                if (group.actions.size() > 0) {
//                    groups.add(group);
//                }
//            }
//            Collections.sort(groups, new NotificationGroupComparator());
//        } catch (JSONException e) {
//            Log.e(TAG, "Error parsing data in parseNotificationGroup: " + e.toString());
//        }
//        return groups;
//    }
//
//    private static SphereArchivedActionModel getSphereArchivedActionModel(String key, JSONObject action, long dt, Context context) throws JSONException {
//        SphereArchivedActionModel model = new SphereArchivedActionModel(key, dt, action.getInt("value"));
//        model.setContext(context);
//        return model;
//    }
//
//    private static RemoveRolesActionModel getRemoveRolesActionModel(String key, JSONObject action, long dt, Context context, Map contactMap, int circleId) throws JSONException {
//        RemoveRolesActionModel removeRolesActionModel = new RemoveRolesActionModel(key, dt);
//        removeRolesActionModel.setContext(context);
//        List<UserModel> modelsUsers = new ArrayList<UserModel>();
//
//        JSONObject values = action.getJSONObject("value");
//        Iterator iter = values.keys();
//        while (iter.hasNext()) {
//            String id_user = (String) iter.next();
//            UserModel user = createUser(context, contactMap, circleId, Integer.parseInt(id_user));
//            user.role = values.getInt(id_user);
//            modelsUsers.add(user);
//        }
//        removeRolesActionModel.users = modelsUsers;
//        return removeRolesActionModel;
//    }
//
//    private static AddRolesActionModel getAddRolesActionModel(String key, JSONObject action, long dt, Context context, Map contactMap, int circleId) throws JSONException {
//        AddRolesActionModel addRolesActionModel = new AddRolesActionModel(key, dt);
//        addRolesActionModel.setContext(context);
//        List<UserModel> modelsUsers = new ArrayList<UserModel>();
//
//        JSONObject values = action.getJSONObject("value");
//        Iterator iter = values.keys();
//        while (iter.hasNext()) {
//            String id_user = (String) iter.next();
//            UserModel user = createUser(context, contactMap, circleId, Integer.parseInt(id_user));
//            user.role = values.getInt(id_user);
//            modelsUsers.add(user);
//        }
//        addRolesActionModel.users = modelsUsers;
//        return addRolesActionModel;
//    }
//
//    private static DeleteActionModel getDeleteActionModel(String key, JSONObject action, long dt, Context context) throws JSONException {
//        DeleteActionModel model = new DeleteActionModel(key, dt, action.getInt("value"));
//        model.setContext(context);
//        return model;
//    }
//
//    private static ChangeParentIdActionModel getChangeParentIdActionModel(String key, JSONObject action, long dt, Context context) throws JSONException {
//        JSONObject value = action.getJSONObject("value");
//        ChangeParentIdActionModel model = new ChangeParentIdActionModel(key, dt, value.getInt("parent_id"), value.getString("parent_name"));
//        model.setContext(context);
//        return model;
//    }
//
//    private static ChangeSphereIdActionModel getChangeSphereIdActionModel(String key, JSONObject action, long dt, Context context) throws JSONException {
//        ChangeSphereIdActionModel model = new ChangeSphereIdActionModel(key, dt, action.getInt("value"));
//        model.setContext(context);
//        return model;
//    }
//
//    private static DeadlineActionModel getDeadlineActionModel(String key, JSONObject action, long dt, Context context) throws JSONException {
//        DeadlineActionModel model = new DeadlineActionModel(key, dt, action.getString("value"));
//        model.setContext(context);
//        return model;
//
//    }
//
//    private static CreateActionModel getCreateActionModel(String key, JSONObject action, long dt, Context context) {
//        CreateActionModel model = new CreateActionModel(key, dt);
//        model.setContext(context);
//        return model;
//    }
//
//    private static RemoveTagActionModel getRemoveTagActionModel(String key, JSONObject action, long dt, Context context) throws JSONException {
//        RemoveTagActionModel removeTagModel = new RemoveTagActionModel(key, dt);
//        removeTagModel.setContext(context);
//        List<TagModel> modelsTags = new ArrayList<TagModel>();
//        JSONArray values = action.getJSONArray("value");
//        for (int j = 0; j < values.length(); j++) {
//            JSONObject value = values.getJSONObject(j);
//            modelsTags.add(new TagModel(value.getInt("id"), value.getString("name"), value.getString("color")));
//        }
//        removeTagModel.tags = modelsTags;
//        return removeTagModel;
//    }
//
//    private static StatusActionModel getStatusActionModel(String key, JSONObject action, long dt, Context context) throws JSONException {
//        StatusActionModel model = new StatusActionModel(key, dt, action.getInt("value"));
//        model.setContext(context);
//        return model;
//    }
//
//    private static AddTagActionModel getAddTagActionModel(String key, JSONObject action, long dt, Context context) throws JSONException {
//        AddTagActionModel addTagModel = new AddTagActionModel(key, dt);
//        addTagModel.setContext(context);
//        List<TagModel> modelsTags = new ArrayList<TagModel>();
//
//        JSONArray values = action.getJSONArray("value");
//        for (int j = 0; j < values.length(); j++) {
//            JSONObject value = values.getJSONObject(j);
//            modelsTags.add(new TagModel(value.getInt("id"), value.getString("name"), value.getString("color")));
//        }
//        addTagModel.tags = modelsTags;
//        return addTagModel;
//    }
//
//    private static RemoveMembersActionModel getRemoveMemberActionModel(String key, JSONObject action, long dt, Context context, Map contactMap, int circleId) throws JSONException {
//        RemoveMembersActionModel removeMemberModel = new RemoveMembersActionModel(key, dt);
//        removeMemberModel.setContext(context);
//        List<UserModel> modelsUsers = new ArrayList<UserModel>();
//
//        JSONArray values = action.getJSONArray("value");
//        for (int j = 0; j < values.length(); j++) {
//            UserModel user = createUser(context, contactMap, circleId, values.getInt(j));
//            modelsUsers.add(user);
//        }
//        removeMemberModel.users = modelsUsers;
//        return removeMemberModel;
//    }
//
//    private static AddMembersActionModel getAddMemberActionModel(String key, JSONObject action, long dt, Context context, Map contactMap, int circleId) throws JSONException {
//        AddMembersActionModel addMemberModel = new AddMembersActionModel(key, dt);
//        addMemberModel.setContext(context);
//        List<UserModel> modelsUsers = new ArrayList<UserModel>();
//
//        JSONArray values = action.getJSONArray("value");
//        for (int j = 0; j < values.length(); j++) {
//            int id = values.getInt(j);
//            UserModel user = createUser(context, contactMap, circleId, id);
//            modelsUsers.add(user);
//        }
//        addMemberModel.users = modelsUsers;
//        return addMemberModel;
//    }
//
//    private static UserModel createUser(Context context, Map contactMap, int circleId, int id) {
//        UserModel user = new UserModel(id, context);
//        String keyMap = circleId + "_" + id;
//        if (contactMap.containsKey(keyMap)) {
//            ContactModel contactModel = (ContactModel) contactMap.get(keyMap);
//            user.name = contactModel.name;
//        }
//        return user;
//    }
}
