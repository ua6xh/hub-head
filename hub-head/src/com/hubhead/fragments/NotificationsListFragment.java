package com.hubhead.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.hubhead.R;

public class NotificationsListFragment extends android.support.v4.app.ListFragment  {

    private static final int CM_DELETE_ID = 1;
    private static final String TAG = "NotificationsListFragment";
    private static final Uri NOTIFICATIONS_URI = Uri.parse("content://com.hubhead.contentproviders.NotificationsContentProvider/notifications");

    public NotificationsListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Cursor cursor = getActivity().getContentResolver().query(NOTIFICATIONS_URI, null, null, null, null);
        getActivity().startManagingCursor(cursor);

        String from[] = {"model_name"};
        int to[] = {android.R.id.text1};
        SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_1, cursor, from, to);

        /** Setting the list mAdapter for the ListFragment */
        setListAdapter(mAdapter);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void onActivityCreated(Bundle savedState) {
        registerForContextMenu(getListView());
        super.onActivityCreated(savedState);
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        Toast.makeText(getActivity(), "Item " + position, Toast.LENGTH_SHORT).show();
        super.onListItemClick(l, v, position, id);
    }


    /*----------------------Create Context Menu --------------------------*/
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE_ID, 0, R.string.action_delete_record);
    }

    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == CM_DELETE_ID) {
            // получаем из пункта контекстного меню данные по пункту списка
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            // извлекаем id записи и удаляем соответствующую запись в БД
            Toast.makeText(getActivity(), "Id record:" + acmi.id, Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onContextItemSelected(item);
    }
    /*----------------------End Create Context Menu --------------------------*/

}
