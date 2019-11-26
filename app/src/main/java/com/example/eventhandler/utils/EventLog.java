package com.example.eventhandler.utils;

import android.util.Log;

public class EventLog {
    private static boolean debug = true;

    public static void setDebug(boolean debug) {
        EventLog.debug = debug;
    }

    public static void d(String tag, String msg) {
        if (debug) {
            Log.d(tag, msg);
        }
    }
}
