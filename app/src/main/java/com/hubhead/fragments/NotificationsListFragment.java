package com.hubhead.fragments;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.TextView;
import android.widget.Toast;

import com.hubhead.R;
import com.hubhead.SFBaseListFragment;
import com.hubhead.SFServiceCallbackListener;
import com.hubhead.contentprovider.NotificationsContentProvider;
import com.hubhead.handlers.impl.RefreshNotificationsActionCommand;
import com.hubhead.parsers.ParseHelper;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import uk.co.senab.actionbarpulltorefresh.library.viewdelegates.ViewDelegate;


public class NotificationsListFragment extends SFBaseListFragment implements LoaderManager.LoaderCallbacks<Cursor>, OnRefreshListener, SFServiceCallbackListener {

    private static final int CM_DELETE_ID = 1;
    private static final String TAG = "NotificationsListFragment";
    private static final int NOTIFICATIONS_LOADER_DELTA = 10000;
    private SimpleCursorAdapter mNotificationsAdapter;
    private int mCircleIdSelected;
    private PullToRefreshLayout mPullToRefreshLayout;
    private int mRequestRefreshNotificationsId = -1;


    public NotificationsListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        mCircleIdSelected = getArguments().getInt(CircleFragment.ARG_CIRCLE_ID);
        mNotificationsAdapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_1, null, new String[]{"model_name"}, new int[]{android.R.id.text1}, 0);
        getActivity().getSupportLoaderManager().initLoader(NOTIFICATIONS_LOADER_DELTA + mCircleIdSelected, null, this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated");
        setEmptyText(getActivity().getResources().getString(R.string.error_empty_list_notifications));

        ViewGroup viewGroup = (ViewGroup) view;
        mPullToRefreshLayout = new PullToRefreshLayout(viewGroup.getContext());
        ActionBarPullToRefresh.from(getActivity())
                .insertLayoutInto(viewGroup)
                .theseChildrenArePullable(getListView(), getListView().getEmptyView())
                .listener(this)
                .useViewDelegate(TextView.class, new ViewDelegate() {
                    @Override
                    public boolean isReadyForPull(View view, float x, float y) {
                        Log.d(TAG, "isReadyForPull");
                        return true;
                    }
                })
                .setup(mPullToRefreshLayout);
    }

    /*----------------------Create Context Menu --------------------------*/
    public void onActivityCreated(Bundle savedState) {
        Log.d(TAG, "onActivityCreated");
        registerForContextMenu(getListView());
        setListAdapter(mNotificationsAdapter);
        setListShownNoAnimation(true);
        super.onActivityCreated(savedState);
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        Log.d(TAG, "onCreateContextMenu");
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

            return true;
        }
        return super.onContextItemSelected(item);
    }
    /*----------------------End Create Context Menu --------------------------*/

    public void onListItemClick(ListView l, View v, int position, long id) {
        //Toast.makeText(getActivity(), "Item " + position, Toast.LENGTH_SHORT).show();
        super.onListItemClick(l, v, position, id);
    }

    /*------------------------------LoaderCallbacks Override---------------------------*/
    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle bundle) {

        String[] args = {Integer.toString(mCircleIdSelected)};
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

    @Override
    public void onRefreshStarted(View view) {
        mRequestRefreshNotificationsId = getServiceHelper().refreshNotificationsFromServer();
    }


    @Override
    public void onServiceCallback(int requestId, Intent requestIntent, int resultCode, Bundle resultData) {
        if (getServiceHelper().check(requestIntent, RefreshNotificationsActionCommand.class)) {
            if (resultCode == RefreshNotificationsActionCommand.RESPONSE_SUCCESS) {
                mPullToRefreshLayout.setRefreshComplete();
                Toast.makeText(getActivity(), "Good", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RefreshNotificationsActionCommand.RESPONSE_FAILURE) {
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mRequestRefreshNotificationsId != -1 && !getServiceHelper().isPending(mRequestRefreshNotificationsId)) {
            mPullToRefreshLayout.setRefreshComplete();
        }
    }
}
