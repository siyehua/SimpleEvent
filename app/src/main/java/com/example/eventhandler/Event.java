package com.example.eventhandler;

import android.content.Context;

import com.example.eventhandler.utils.EventLog;

public class Event {
    private static EventImpl eventImpl = new EventImpl();

    public static EventImpl getEventImpl() {
        return eventImpl;
    }

    public static void setDebug(boolean debug) {
        EventLog.setDebug(debug);
    }

    public static void addRemoteListener(Context context) {
        RemoteEventImpl remoteEvent = new RemoteEventImpl();
        remoteEvent.init(context);
        eventImpl.addEvent(remoteEvent);
    }

    public static void start() {
        eventImpl.addEvent(new LocalEventImpl());
    }
}
