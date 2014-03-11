package com.hubhead.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.hubhead.R;

public class EmptyFragment extends android.support.v4.app.Fragment {

    private String TAG = ((Object) this).getClass().getCanonicalName();

    public EmptyFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.empty_circle, container, false);
    }
}
