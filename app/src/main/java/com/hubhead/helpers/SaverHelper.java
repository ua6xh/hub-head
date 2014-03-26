package com.hubhead.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.hubhead.contentprovider.CirclesContentProvider;
import com.hubhead.contentprovider.ContactsContentProvider;
import com.hubhead.contentprovider.NotificationsContentProvider;
import com.hubhead.contentprovider.OverviewContentProvider;

import com.hubhead.contentprovider.SpheresContentProvider;
import com.hubhead.models.CircleModel;
import com.hubhead.models.ContactModel;
import com.hubhead.models.ReminderModel;
import com.hubhead.models.SphereModel;

import java.util.List;

public class SaverHelper {

    private String TAG = ((Object) this).getClass().getCanonicalName();

    public SaverHelper(Context context) {
        mContext = context;
    }

    private Context mContext;

    public void saveCircles(List<CircleModel> items) {
        ContentValues[] circlesValues;
        circlesValues = new ContentValues[items.size()];
        int i = 0;
        for (CircleModel item : items) {
            ContentValues cv = new ContentValues();
            cv.put("_id", item.getId());
            cv.put("name", item.name);
            cv.put("add_date", item.add_date);
            cv.put("user_id", item.user_id);
            cv.put("contact_id", item.contact_id);
            cv.put("status", item.status);
            circlesValues[i++] = cv;
        }
        mContext.getContentResolver().bulkInsert(CirclesContentProvider.CIRCLE_CONTENT_URI, circlesValues);
    }

    public void saveReminders(List<ReminderModel> items) {
        ContentValues[] values;
        values = new ContentValues[items.size()];
        int i = 0;
        for (ReminderModel item : items) {
            ContentValues cv = new ContentValues();
            cv.put("_id", item.getId());
            cv.put("circle_id", item.circle_id);
            cv.put("sphere_id", item.sphere_id);
            cv.put("task_id", item.task_id);
            cv.put("task_name", item.task_name);
            cv.put("task_status", item.task_status);
            cv.put("start_time", item.start_time);
            cv.put("deadline", item.deadline);
            cv.put("type_reminder", item.type);
            values[i++] = cv;
        }
        mContext.getContentResolver().bulkInsert(OverviewContentProvider.OVERVIEW_CONTENT_URI, values);
    }

    public void saveContacts(List<ContactModel> items) {
        ContentValues[] values;
        values = new ContentValues[items.size()];
        int i = 0;
        for (ContactModel item : items) {
            ContentValues cv = new ContentValues();
            cv.put("_id", item.getId());
            cv.put("name", item.name);
            cv.put("circle_id", item.circle_id);
            cv.put("account_id", item.account_id);
            values[i++] = cv;
        }
        mContext.getContentResolver().bulkInsert(ContactsContentProvider.CONTACT_CONTENT_URI, values);
    }

    public void saveSpheres(List<SphereModel> items) {
        ContentValues[] values;
        values = new ContentValues[items.size()];
        int i = 0;
        for (SphereModel item : items) {
            ContentValues cv = new ContentValues();
            cv.put("_id", item.getId());
            cv.put("circle_id", item.circle_id);
            cv.put("name", item.name);
            values[i++] = cv;
        }
        mContext.getContentResolver().bulkInsert(SpheresContentProvider.SPHERE_CONTENT_URI, values);
    }

    public static void saveNotifications(Context context, ContentValues[] contentValueses) {
        context.getContentResolver().bulkInsert(NotificationsContentProvider.NOTIFICATION_CONTENT_URI, contentValueses);
    }

    public static void saveNotificationsSocket(Context context, ContentValues[] contentValueses) {
        context.getContentResolver().insert(NotificationsContentProvider.NOTIFICATION_CONTENT_URI, contentValueses[0]);
    }
}
