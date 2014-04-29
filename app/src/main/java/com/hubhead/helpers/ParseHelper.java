package com.hubhead.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.hubhead.compamators.NotificationActionComparator;
import com.hubhead.compamators.NotificationGroupComparator;
import com.hubhead.models.ActionModels.AddMembersActionModel;
import com.hubhead.models.ActionModels.AddRolesActionModel;
import com.hubhead.models.ActionModels.AddTagActionModel;
import com.hubhead.models.ActionModels.ChangeParentIdActionModel;
import com.hubhead.models.ActionModels.ChangeSphereIdActionModel;
import com.hubhead.models.ActionModels.CreateActionModel;
import com.hubhead.models.ActionModels.DeadlineActionModel;
import com.hubhead.models.ActionModels.DeleteActionModel;
import com.hubhead.models.ActionModels.RemoveMembersActionModel;
import com.hubhead.models.ActionModels.RemoveRolesActionModel;
import com.hubhead.models.ActionModels.RemoveTagActionModel;
import com.hubhead.models.ActionModels.SphereArchivedActionModel;
import com.hubhead.models.ActionModels.StatusActionModel;
import com.hubhead.models.ActionModels.TagModel;
import com.hubhead.models.ActionModels.UserModel;
import com.hubhead.models.ContactModel;
import com.hubhead.models.NotificationActionModel;
import com.hubhead.models.NotificationGroupModel;
import com.hubhead.models.NotificationModel;
import com.hubhead.models.Reminder;
import com.hubhead.models.SphereModel;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class ParseHelper {
    private static final String TAGst = "com.hubhead.helpers.ParseHelper";
    private final String TAG = ((Object) this).getClass().getCanonicalName();
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

    public static Reminder parseReminder(String response) {
        ObjectMapper mapper = new ObjectMapper();
        Reminder reminder = null;
        try {
            reminder = mapper.readValue(response, Reminder.class);
            System.out.println("JACKSON JSON PARSE REMINDER GOOD!");
        } catch (IOException e) {
            System.out.println("JACKSON json parse reminder bad!:" + response);
            e.printStackTrace();
        }
        return reminder;
    }

    public static List<NotificationGroupModel> parseNotificationGroup(String jsonGroup, Context context, Map<String, ContactModel> contactMap, Map<Long, SphereModel> sphereMap, long circleId) throws JSONException {
        JSONArray items = new JSONArray(jsonGroup);
        List<NotificationGroupModel> groups = new ArrayList<NotificationGroupModel>();
        try {
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);

                NotificationGroupModel group = new NotificationGroupModel(item.getString("id"), item.getLong("dt"), item.getInt("user_id"));
                JSONObject actions = item.getJSONObject("actions");
                Iterator actionsIterator = actions.keys();

                while (actionsIterator.hasNext()) {
                    String key = (String) actionsIterator.next();
                    JSONObject action = actions.getJSONObject(key);
                    long dt = action.getLong("dt");
                    NotificationActionModel actionGroup = getAction(key, action, dt, context, contactMap, sphereMap, circleId);
                    actionGroup.dt = dt;
                    group.actions.add(actionGroup);
                }
                if (group.actions.size() > 0) {
                    Collections.sort(group.actions, new NotificationActionComparator());
                    groups.add(group);
                }
            }
            Collections.sort(groups, new NotificationGroupComparator());
        } catch (JSONException e) {
            Log.e(TAGst, "Error parsing data in parseNotificationGroup", e);
        } catch (Exception e) {
            Log.e(TAGst, "Error in parseNotificationGroup", e);
        }
        return groups;
    }

    public static NotificationActionModel getAction(String key, JSONObject action, long dt, Context context, Map<String, ContactModel> contactMap, Map<Long, SphereModel> sphereMap, long circleId) throws JSONException {
        if (key.equals("add-tags")) {
            return getAddTagActionModel(key, action, dt, context);
        } else if (key.equals("remove-tags")) {
            return getRemoveTagActionModel(key, action, dt, context);
        } else if (key.equals("remove-members")) {
            return getRemoveMemberActionModel(key, action, dt, context, contactMap, circleId);
        } else if (key.equals("add-members")) {
            return getAddMemberActionModel(key, action, dt, context, contactMap, circleId);
        } else if (key.equals("create")) {
            return getCreateActionModel(key, dt, context);
        } else if (key.equals("status")) {
            return getStatusActionModel(key, action, dt, context);
        } else if (key.equals("deadline")) {
            return getDeadlineActionModel(key, action, dt, context);
        } else if (key.equals("parent_id")) {
            return getChangeParentIdActionModel(key, action, dt, context);
        } else if (key.equals("sphere_id")) {
            return getChangeSphereIdActionModel(key, action, dt, context, sphereMap);
        } else if (key.equals("deleted")) {
            return getDeleteActionModel(key, action, dt, context);
        } else if (key.equals("add-roles")) {
            return getAddRolesActionModel(key, action, dt, context, contactMap, circleId);
        } else if (key.equals("remove-roles")) {
            return getRemoveRolesActionModel(key, action, dt, context, contactMap, circleId);
        } else if (key.equals("sphere-archived")) {
            return getSphereArchivedActionModel(key, action, dt, context);
        } else {
            return new NotificationActionModel();
        }
    }

    public static SphereArchivedActionModel getSphereArchivedActionModel(String key, JSONObject action, long dt, Context context) throws JSONException {
        SphereArchivedActionModel model = new SphereArchivedActionModel(key, dt, action.getInt("value"));
        model.setContext(context);
        return model;
    }

    public static RemoveRolesActionModel getRemoveRolesActionModel(String key, JSONObject action, long dt, Context context, Map contactMap, long circleId) throws JSONException {
        RemoveRolesActionModel removeRolesActionModel = new RemoveRolesActionModel(key, dt);
        removeRolesActionModel.setContext(context);
        List<UserModel> modelsUsers = new ArrayList<UserModel>();

        JSONObject values = action.getJSONObject("value");
        Iterator iter = values.keys();
        while (iter.hasNext()) {
            String id_user = (String) iter.next();
            UserModel user = createUser(context, contactMap, circleId, Integer.parseInt(id_user));
            user.role = values.getInt(id_user);
            modelsUsers.add(user);
        }
        removeRolesActionModel.users = modelsUsers;
        return removeRolesActionModel;
    }

    public static AddRolesActionModel getAddRolesActionModel(String key, JSONObject action, long dt, Context context, Map contactMap, long circleId) throws JSONException {
        AddRolesActionModel addRolesActionModel = new AddRolesActionModel(key, dt);
        addRolesActionModel.setContext(context);
        List<UserModel> modelsUsers = new ArrayList<UserModel>();

        JSONObject values = action.getJSONObject("value");
        Iterator iter = values.keys();
        while (iter.hasNext()) {
            String id_user = (String) iter.next();
            UserModel user = createUser(context, contactMap, circleId, Integer.parseInt(id_user));
            user.role = values.getInt(id_user);
            modelsUsers.add(user);
        }
        addRolesActionModel.users = modelsUsers;
        return addRolesActionModel;
    }

    public static DeleteActionModel getDeleteActionModel(String key, JSONObject action, long dt, Context context) throws JSONException {
        DeleteActionModel model = new DeleteActionModel(key, dt, action.getInt("value"));
        model.setContext(context);
        return model;
    }

    public static ChangeParentIdActionModel getChangeParentIdActionModel(String key, JSONObject action, long dt, Context context) throws JSONException {
        JSONObject value = action.getJSONObject("value");
        ChangeParentIdActionModel model = new ChangeParentIdActionModel(key, dt, value.getInt("parent_id"), value.getString("parent_name"));
        model.setContext(context);
        return model;
    }

    public static ChangeSphereIdActionModel getChangeSphereIdActionModel(String key, JSONObject action, long dt, Context context, Map sphereMap) throws JSONException {
        long sphereId = action.getLong("value");
        SphereModel sphereModel;
        if (sphereMap.containsKey(sphereId)) {
            sphereModel = (SphereModel) sphereMap.get(sphereId);
        } else {
            sphereModel = new SphereModel();
        }
        ChangeSphereIdActionModel model = new ChangeSphereIdActionModel(key, dt, sphereId, sphereModel);
        model.setContext(context);
        return model;
    }

    public static DeadlineActionModel getDeadlineActionModel(String key, JSONObject action, long dt, Context context) throws JSONException {
        DeadlineActionModel model = new DeadlineActionModel(key, dt, action.getString("value"));
        model.setContext(context);
        return model;

    }

    public static CreateActionModel getCreateActionModel(String key, long dt, Context context) {
        CreateActionModel model = new CreateActionModel(key, dt);
        model.setContext(context);
        return model;
    }

    public static RemoveTagActionModel getRemoveTagActionModel(String key, JSONObject action, long dt, Context context) throws JSONException {
        RemoveTagActionModel removeTagModel = new RemoveTagActionModel(key, dt);
        removeTagModel.setContext(context);
        List<TagModel> modelsTags = new ArrayList<TagModel>();
        JSONArray values = action.getJSONArray("value");
        for (int j = 0; j < values.length(); j++) {
            JSONObject value = values.getJSONObject(j);
            modelsTags.add(new TagModel(value.getInt("id"), value.getString("name"), value.getString("color")));
        }
        removeTagModel.tags = modelsTags;
        return removeTagModel;
    }

    public static StatusActionModel getStatusActionModel(String key, JSONObject action, long dt, Context context) throws JSONException {
        StatusActionModel model = new StatusActionModel(key, dt, action.getInt("value"));
        model.setContext(context);
        return model;
    }

    public static AddTagActionModel getAddTagActionModel(String key, JSONObject action, long dt, Context context) throws JSONException {
        AddTagActionModel addTagModel = new AddTagActionModel(key, dt);
        addTagModel.setContext(context);
        List<TagModel> modelsTags = new ArrayList<TagModel>();

        JSONArray values = action.getJSONArray("value");
        for (int j = 0; j < values.length(); j++) {
            JSONObject value = values.getJSONObject(j);
            modelsTags.add(new TagModel(value.getInt("id"), value.getString("name"), value.getString("color")));
        }
        addTagModel.tags = modelsTags;
        return addTagModel;
    }

    public static RemoveMembersActionModel getRemoveMemberActionModel(String key, JSONObject action, long dt, Context context, Map contactMap, long circleId) throws JSONException {
        RemoveMembersActionModel removeMemberModel = new RemoveMembersActionModel(key, dt);
        removeMemberModel.setContext(context);
        List<UserModel> modelsUsers = new ArrayList<UserModel>();

        JSONArray values = action.getJSONArray("value");
        for (int j = 0; j < values.length(); j++) {
            UserModel user = createUser(context, contactMap, circleId, values.getInt(j));
            modelsUsers.add(user);
        }
        removeMemberModel.users = modelsUsers;
        return removeMemberModel;
    }

    public static AddMembersActionModel getAddMemberActionModel(String key, JSONObject action, long dt, Context context, Map contactMap, long circleId) throws JSONException {
        AddMembersActionModel addMemberModel = new AddMembersActionModel(key, dt);
        addMemberModel.setContext(context);
        List<UserModel> modelsUsers = new ArrayList<UserModel>();

        JSONArray values = action.getJSONArray("value");
        for (int j = 0; j < values.length(); j++) {
            int id = values.getInt(j);
            UserModel user = createUser(context, contactMap, circleId, id);
            modelsUsers.add(user);
        }
        addMemberModel.users = modelsUsers;
        return addMemberModel;
    }

    private static UserModel createUser(Context context, Map contactMap, long circleId, int id) {
        UserModel user = new UserModel(id, context);
        String keyMap = circleId + "_" + id;
        if (contactMap.containsKey(keyMap)) {
            ContactModel contactModel = (ContactModel) contactMap.get(keyMap);
            user.name = contactModel.name;
        }
        return user;
    }

    public void parseNotifications(String response, boolean socket) {
        try {
            JSONObject json = new JSONObject(response);
            JSONObject notificationsObj = json.getJSONObject("data");

            Iterator objectsIterator = notificationsObj.keys();
            Map<String, ContactModel> contactMap = ContactModel.getMap(mContext.getContentResolver(), null);
            Map<Long, SphereModel> sphereMap = SphereModel.getMap(mContext.getContentResolver(), null);
            if (objectsIterator.hasNext()) {
                ArrayList<ContentValues> contentValuesArrayList = new ArrayList<ContentValues>();
                while (objectsIterator.hasNext()) {
                    String roomName = (String) objectsIterator.next();
                    JSONObject room = notificationsObj.getJSONObject(roomName);
                    NotificationModel notification = new NotificationModel(roomName, room, contactMap, sphereMap, mContext);
                    contentValuesArrayList.add(notification.getContentValues());
                }
                ContentValues[] contentValueses = contentValuesArrayList.toArray(new ContentValues[contentValuesArrayList.size()]);
                if (socket) {
                    SaverHelper.saveNotificationsSocket(mContext, contentValueses);
                } else {
                    SaverHelper.saveNotifications(mContext, contentValueses);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing data in parseNotifications: ", e);
        } catch (NullPointerException e) {
            Log.e(TAG, "NullPointerException in ParserHelper", e);
        }
    }
}

