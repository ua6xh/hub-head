package com.hubhead.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class OverviewListFragment extends android.support.v4.app.ListFragment {

    public OverviewListFragment() {
    }

    String[] overviews = new String[]{"O1", "O2", "O2", "O2", "O2", "O2", "O2", "O2", "O2", "O2", "O2", "O2", "O2", "O2", "O2", "O2", "O2", "O2", "O2", "O2", "O2", "O2"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /** Creating an array adapter to store the list of notifications **/
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(inflater.getContext(), android.R.layout.simple_list_item_1, overviews);

        /** Setting the list adapter for the ListFragment */
        setListAdapter(adapter);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
