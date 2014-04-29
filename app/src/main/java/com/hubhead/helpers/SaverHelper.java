package com.hubhead.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.hubhead.contentprovider.CirclesContentProvider;
import com.hubhead.contentprovider.ContactsContentProvider;
import com.hubhead.contentprovider.NotificationsContentProvider;
import com.hubhead.contentprovider.RemindersContentProvider;
import com.hubhead.contentprovider.SpheresContentProvider;
import com.hubhead.models.CircleModel;
import com.hubhead.models.ContactModel;
import com.hubhead.models.Reminder;
import com.hubhead.models.SphereModel;

import java.util.List;

public class SaverHelper {

    private String TAG = ((Object) this).getClass().getCanonicalName();
    private Context mContext;

    public SaverHelper(Context context) {
        mContext = context;
    }

    public static void saveNotifications(Context context, ContentValues[] contentValueses) {
        context.getContentResolver().bulkInsert(NotificationsContentProvider.CONTENT_URI, contentValueses);
    }

    public static void saveNotificationsSocket(Context context, ContentValues[] contentValueses) {
        context.getContentResolver().insert(NotificationsContentProvider.CONTENT_URI, contentValueses[0]);
    }

    public static void saveReminderSocket(Context context, ContentValues contentValues) {
        context.getContentResolver().insert(RemindersContentProvider.CONTENT_URI, contentValues);
    }

    public static void removeReminderSocket(Context context, long id) {
        context.getContentResolver().delete(RemindersContentProvider.CONTENT_URI, "_id = " + id, new String[]{});
    }

    public void saveCircles(List<CircleModel> items) {
        try {
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
            mContext.getContentResolver().bulkInsert(CirclesContentProvider.CONTENT_URI, circlesValues);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public void saveReminders(List<Reminder> items) {
        ContentValues[] values;
        values = new ContentValues[items.size()];
        int i = 0;
        for (Reminder item : items) {
            values[i++] = Reminder.createContentValues(item);
        }
        mContext.getContentResolver().bulkInsert(RemindersContentProvider.CONTENT_URI, values);
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
        mContext.getContentResolver().bulkInsert(ContactsContentProvider.CONTENT_URI, values);
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
        mContext.getContentResolver().bulkInsert(SpheresContentProvider.CONTENT_URI, values);
    }
}
