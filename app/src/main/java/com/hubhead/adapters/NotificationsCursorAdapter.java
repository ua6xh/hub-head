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

    public NotificationsCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        tf = TypefacesHelper.get(context, "fonts/AndroidClockMono-Light.ttf");
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View rowView = ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.item_list_notification, parent, false);
        ViewHolder holder = new ViewHolder();
        holder.v1 = (TextView) rowView.findViewById(R.id.text1);
        holder.v2 = (TextView) rowView.findViewById(R.id.text2);
        rowView.setTag(holder);
        return rowView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.v1.setText(cursor.getString(NotificationsContentProvider.MODEL_NAME_INDEX));
        holder.v1.setTypeface(tf);

        int count = cursor.getInt(NotificationsContentProvider.MESSAGES_COUNT_INDEX);
        String countText = count == 0 ? "" : Integer.toString(count);
        holder.v2.setText(countText);
        holder.v2.setTypeface(tf);
    }

    class ViewHolder {
        TextView v1, v2;
    }
}
