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

    public WampClient getWampClient(){
        return new WampClient();
    }

    /* ---------------------- Auhobahn-----------------------*/
    class WampClient extends WampConnection {
        private final String wsuri = "ws://tm.dev-lds.ru:12126";


        public WampClient() {

            Log.d(TAG, "WAMP constructor");
            connect(wsuri, new Wamp.ConnectionHandler() {

                @Override
                public void onOpen() {
                    sendSessionIdMessage();
                }

                @Override
                public void onClose(int code, String reason) {
                    //reconnect();
                    Log.d(TAG, "code: " + code + " reason:" + reason);
                }
            });
        }

        private void sendSessionIdMessage() {
            String cookie = getSharedPreferences(MY_PREF, IsolatedContext.MODE_PRIVATE).getString("cookies", "");
            String[] cookieSplit = cookie.split("=");
            String cookieSend = cookieSplit[1].substring(0, cookieSplit[1].length() - 1);
            Log.d(TAG, "sendSessionIdMessage");

            call("userAuth", Integer.class, new CallHandler() {
                @Override
                public void onResult(Object result) {
                    Log.d(TAG, "userAuth: onResult:" + result);
                    subscribe("u_" + result, Event.class, new EventHandler() {
                        @Override
                        public void onEvent(String topicUri, Object eventResult) {
                            Event event = (Event) eventResult;
                            Log.d(TAG, "subscribe: onEvent:" + topicUri + ": event:" + event.type + " data:" + event.data + " dataClass" + event.data.getClass());
                        }
                    });
                }

                @Override
                public void onError(String errorUri, String errorDesc) {
                    Log.d(TAG, "userAuth: onError");
                }
            }, cookieSend);
        }
    }

    private static class Event {
        public String type;
        public Object data;
    }


    /* ---------------------End Auhobahn---------------------*/

}
