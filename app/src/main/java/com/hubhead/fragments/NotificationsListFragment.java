package com.hubhead.fragments;

import android.content.Intent;
import android.database.Cursor;
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
import com.hubhead.adapters.NotificationsCursorAdapter;
import com.hubhead.contentprovider.NotificationsContentProvider;
import com.hubhead.handlers.impl.RefreshNotificationsActionCommand;
import com.hubhead.ui.CirclesActivity;
import com.hubhead.ui.NotificationActivity;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import uk.co.senab.actionbarpulltorefresh.library.viewdelegates.ViewDelegate;


public class NotificationsListFragment extends SFBaseListFragment implements LoaderManager.LoaderCallbacks<Cursor>, OnRefreshListener, SFServiceCallbackListener {

    private static final int CM_READ_ID = 1;
    private static final int CM_OPEN_ID = 2;
    private final String TAG = ((Object) this).getClass().getCanonicalName();
    private static final int NOTIFICATIONS_LOADER_DELTA = 10000;
    private NotificationsCursorAdapter mNotificationsAdapter;
    private int mCircleIdSelected;
    private PullToRefreshLayout mPullToRefreshLayout;
    private static int mRequestRefreshNotificationsId = -1;


    public NotificationsListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mCircleIdSelected = getArguments().getInt(CircleFragment.ARG_CIRCLE_ID);
        mNotificationsAdapter = new NotificationsCursorAdapter(getActivity(), null, 0);
        //mNotificationsAdapter = new NotificationsCursorAdapter(getActivity(), android.R.layout.simple_list_item_1, null, new String[]{"model_name"}, new int[]{android.R.id.text1}, 0);
        getActivity().getSupportLoaderManager().initLoader(NOTIFICATIONS_LOADER_DELTA + mCircleIdSelected, null, this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
        registerForContextMenu(getListView());
        setListAdapter(mNotificationsAdapter);
        setListShownNoAnimation(true);
        super.onActivityCreated(savedState);
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_OPEN_ID, 1, R.string.action_open_record);
        menu.add(0, CM_READ_ID, 2, R.string.action_read_record);
    }

    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case CM_OPEN_ID:{
                // получаем из пункта контекстного меню данные по пункту списка
                AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

                // извлекаем id записи и удаляем соответствующую запись в БД

                Intent intent = new Intent(getActivity(), NotificationActivity.class);
                intent.putExtra("notification_id", acmi.id);
                startActivity(intent);

                break;
            }
            case CM_READ_ID:{
                // получаем из пункта контекстного меню данные по пункту списка
                AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

                // извлекаем id записи и удаляем соответствующую запись в БД

                CirclesActivity circlesActivity = (CirclesActivity) getActivity();
                circlesActivity.sendNotificationSetReaded(acmi.id);

                break;
            }
        }
        if (item.getItemId() == CM_READ_ID) {

        }
        return super.onContextItemSelected(item);
    }
    /*----------------------End Create Context Menu --------------------------*/

    public void onListItemClick(ListView l, View v, int position, long id) {
        getActivity().openContextMenu(v);
        super.onListItemClick(l, v, position, id);
//        mPullToRefreshLayout.setRefreshing(true);
//        //Toast.makeText(getActivity(), "Click!", Toast.LENGTH_SHORT).show();
//        mRequestRefreshNotificationsId = getServiceHelper().refreshNotificationsFromServer();

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
            } else if (resultCode == RefreshNotificationsActionCommand.RESPONSE_FAILURE) {
                mPullToRefreshLayout.setRefreshComplete();
                Toast.makeText(getActivity(), resultData.getString("error"), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mRequestRefreshNotificationsId != -1 && !getServiceHelper().isPending(mRequestRefreshNotificationsId)) {
            mPullToRefreshLayout.setRefreshComplete();
        } else if (mRequestRefreshNotificationsId != -1 && getServiceHelper().isPending(mRequestRefreshNotificationsId)) {
            Toast.makeText(getActivity(), "Refresh", Toast.LENGTH_LONG).show();
            mPullToRefreshLayout.setRefreshing(true);
        }
    }
}
