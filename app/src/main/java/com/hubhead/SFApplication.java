/*
 * Copyright (C) 2013 Alexander Osmanov (http://perfectear.educkapps.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.hubhead;

import android.app.Application;
import android.content.Context;
import android.test.IsolatedContext;
import android.util.Log;

import de.tavendo.autobahn.Wamp;
import de.tavendo.autobahn.WampConnection;

public class SFApplication extends Application {

    public static final String PACKAGE = "com.hubhead";
    private static final String MY_PREF = "MY_PREF";
    private final String TAG = ((Object) this).getClass().getCanonicalName();

    private SFServiceHelper serviceHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        serviceHelper = new SFServiceHelper(this);
    }

    public SFServiceHelper getServiceHelper() {
        return serviceHelper;
    }

    public static SFApplication getApplication(Context context) {
        if (context instanceof SFApplication) {
            return (SFApplication) context;
        }
        return (SFApplication) context.getApplicationContext();
    }
}
