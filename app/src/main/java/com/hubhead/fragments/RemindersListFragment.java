package com.hubhead.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;


import com.hubhead.R;
import com.hubhead.SFBaseListFragment;
import com.hubhead.adapters.RemindersCursorAdapter;
import com.hubhead.contentprovider.OverviewContentProvider;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import uk.co.senab.actionbarpulltorefresh.library.viewdelegates.ViewDelegate;

public class RemindersListFragment extends SFBaseListFragment implements LoaderManager.LoaderCallbacks<Cursor>, OnRefreshListener {

    private static final int CM_READ_ID = 1;
    private static final int CM_OPEN_ID = 2;
    private final String TAG = ((Object) this).getClass().getCanonicalName();
    private static final int OVERVIEW_LOADER_DELTA = 11000;
    private RemindersCursorAdapter remindersCursorAdapter;
    private int mCircleIdSelected;
    private PullToRefreshLayout mPullToRefreshLayout;

    public RemindersListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mCircleIdSelected = getArguments().getInt(CircleFragment.ARG_CIRCLE_ID);
        remindersCursorAdapter = new RemindersCursorAdapter(getActivity(), null, 0);
        //remindersCursorAdapter = new RemindersCursorAdapter(getActivity(), android.R.layout.simple_list_item_1, null, new String[]{"model_name"}, new int[]{android.R.id.text1}, 0);
        getActivity().getSupportLoaderManager().initLoader(OVERVIEW_LOADER_DELTA + mCircleIdSelected, null, this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setEmptyText(getActivity().getResources().getString(R.string.error_empty_list_overview));

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
        setListAdapter(remindersCursorAdapter);
        setListShownNoAnimation(true);
        super.onActivityCreated(savedState);
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_OPEN_ID, 1, R.string.action_open_record);
        menu.add(0, CM_READ_ID, 2, R.string.action_read_record);
    }

//    public boolean onContextItemSelected(MenuItem item) {
//        switch (item.getItemId()){
//            case CM_OPEN_ID:{
//                // получаем из пункта контекстного меню данные по пункту списка
//                AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//
//                // извлекаем id записи и удаляем соответствующую запись в БД
//
//                Intent intent = new Intent(getActivity(), OverviewActivity.class);
//                intent.putExtra("notification_id", acmi.id);
//
//                break;
//            }
//            case CM_READ_ID:{
//                // получаем из пункта контекстного меню данные по пункту списка
//                AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//
//                // извлекаем id записи и удаляем соответствующую запись в БД
//
//                CirclesActivity circlesActivity = (CirclesActivity) getActivity();
//                circlesActivity.sendOverviewSetReaded(acmi.id);
//
//                break;
//            }
//        }
//        if (item.getItemId() == CM_READ_ID) {
//
//        }
//        return super.onContextItemSelected(item);
//    }
    /*----------------------End Create Context Menu --------------------------*/

    public void onListItemClick(ListView l, View v, int position, long id) {
        getActivity().openContextMenu(v);
        super.onListItemClick(l, v, position, id);
//        mPullToRefreshLayout.setRefreshing(true);
//        //Toast.makeText(getActivity(), "Click!", Toast.LENGTH_SHORT).show();
//        mRequestRefreshOverviewId = getServiceHelper().refreshOverviewFromServer();

    }

    /*------------------------------LoaderCallbacks Override---------------------------*/
    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        String[] args = {Integer.toString(mCircleIdSelected)};
        return new CursorLoader(getActivity(), OverviewContentProvider.OVERVIEW_CONTENT_URI, new String[]{"model_name"}, "circle_id=?", args, null);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        remindersCursorAdapter.swapCursor(null);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor cursor) {
        remindersCursorAdapter.swapCursor(cursor);
    }

    /*------------------------------End LoaderCallbacks---------------------------*/

    @Override
    public void onRefreshStarted(View view) {
        Log.d(TAG, "onRefreshStarted");
        //mRequestRefreshOverviewId = getServiceHelper().refreshOverviewFromServer();
    }


    @Override
    public void onServiceCallback(int requestId, Intent requestIntent, int resultCode, Bundle resultData) {
//        if (getServiceHelper().check(requestIntent, RefreshOverviewActionCommand.class)) {
//            if (resultCode == RefreshOverviewActionCommand.RESPONSE_SUCCESS) {
//                mPullToRefreshLayout.setRefreshComplete();
//            } else if (resultCode == RefreshOverviewActionCommand.RESPONSE_FAILURE) {
//                mPullToRefreshLayout.setRefreshComplete();
//                Toast.makeText(getActivity(), resultData.getString("error"), Toast.LENGTH_SHORT).show();
//            }
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
            mPullToRefreshLayout.setRefreshComplete();
//        if (mRequestRefreshOverviewId != -1 && !getServiceHelper().isPending(mRequestRefreshOverviewId)) {
//            mPullToRefreshLayout.setRefreshComplete();
//        } else if (mRequestRefreshOverviewId != -1 && getServiceHelper().isPending(mRequestRefreshOverviewId)) {
//            Toast.makeText(getActivity(), "Refresh", Toast.LENGTH_LONG).show();
//            mPullToRefreshLayout.setRefreshing(true);
//        }
    }
}

