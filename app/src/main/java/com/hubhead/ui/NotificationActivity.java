package com.hubhead.ui;

import android.os.Bundle;

import com.hubhead.SFBaseActivity;


public class NotificationActivity extends SFBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setHomeButtonEnabled(true);

    }
}
