package com.hubhead.fragments;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.hubhead.R;
import com.hubhead.contentprovider.CirclesContentProvider;
import com.hubhead.contentprovider.NotificationsContentProvider;
import com.hubhead.helpers.DBHelper;

public class NotificationsListFragment extends android.support.v4.app.ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CM_DELETE_ID = 1;
    private static final String TAG = "NotificationsListFragment";
    private static final int NOTIFICATIONS_LOADER_DELTA = 10000;
    private SimpleCursorAdapter mNotificationsAdapter;
    private int mCircleIdSelected;

    public NotificationsListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mCircleIdSelected = getArguments().getInt(CircleFragment.ARG_CIRCLE_ID);
        Log.d(TAG, "mCircleIdSelected: " + mCircleIdSelected);

        mNotificationsAdapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_1, null, new String[]{"model_name"}, new int[]{android.R.id.text1}, 0);
        getActivity().getSupportLoaderManager().initLoader(NOTIFICATIONS_LOADER_DELTA + mCircleIdSelected, null, this);

        /** Setting the list mNotificationsAdapter for the ListFragment */
        setListAdapter(mNotificationsAdapter);
        return super.onCreateView(inflater, container, savedInstanceState);
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

            Uri itemUri = ContentUris.withAppendedId(NotificationsContentProvider.NOTIFICATION_CONTENT_URI, acmi.id);
            getActivity().getContentResolver().delete(itemUri, null, null);
            Log.d(TAG, "Delete notification: " + acmi.id);


            return true;
        }
        return super.onContextItemSelected(item);
    }

    public void onActivityCreated(Bundle savedState) {
        setEmptyText(getActivity().getResources().getString(R.string.error_empty_list_notifications));
        registerForContextMenu(getListView());
        super.onActivityCreated(savedState);
    }
    /*----------------------End Create Context Menu --------------------------*/

    /*------------------------------LoaderCallbacks Override---------------------------*/
    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle bundle) {

        String[] args = {Integer.toString(mCircleIdSelected)};
        Log.d(TAG, "CreateLoader: " + mCircleIdSelected);
        return new CursorLoader(getActivity(), NotificationsContentProvider.NOTIFICATION_CONTENT_URI, new String[]{"model_name"}, "circle_id=?", args, null);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        mNotificationsAdapter.swapCursor(null);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor cursor) {
        mNotificationsAdapter.swapCursor(cursor);
    }
    /*------------------------------End LoaderCallbacks---------------------------*/

}
