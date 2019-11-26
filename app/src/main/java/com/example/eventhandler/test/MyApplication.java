package com.example.eventhandler.test;

import android.app.Application;

import com.example.eventhandler.Event;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Event.start();
        // if you want observe in other process, you should set:
        Event.addRemoteListener(this);
    }
}
