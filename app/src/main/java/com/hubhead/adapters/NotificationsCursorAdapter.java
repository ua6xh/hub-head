package com.hubhead.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.hubhead.R;
import com.hubhead.contentprovider.NotificationsContentProvider;
import com.hubhead.helpers.TypefacesHelper;

public class NotificationsCursorAdapter extends CursorAdapter {
    private final String TAG = ((Object) this).getClass().getCanonicalName();
    private final Typeface tf;
    private LayoutInflater mInflater;

    public NotificationsCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        tf = TypefacesHelper.get(context, "fonts/segoeui.ttf");
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mInflater.inflate(R.layout.item_list_notification, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView contentTV = (TextView) view.findViewById(R.id.text1);
        contentTV.setText(cursor.getString(cursor.getColumnIndex(NotificationsContentProvider.NOTIFICATION_NAME)));
        contentTV.setTypeface(tf);

        TextView countTV = (TextView) view.findViewById(R.id.text2);
        int count = cursor.getInt(cursor.getColumnIndex(NotificationsContentProvider.NOTIFICATION_MESSAGES_COUNT));
        String countText = count == 0 ? "" : Integer.toString(count);
        countTV.setText(countText);
        countTV.setTypeface(tf);
    }
}
