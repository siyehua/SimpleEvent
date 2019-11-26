package com.example.eventhandler;

// Declare any non-default types here with import statements

interface ISendEvent {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void sendEvent(String className, String data);

    void addListenerBinder(String key, IBinder iBinder);

    void removeListenerBinder(String key, IBinder iBinder);

    void removeListenerAll(String key);

}