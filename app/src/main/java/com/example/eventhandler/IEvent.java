package com.example.eventhandler;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface IEvent {

    /**
     * callback thread <br>
     * <p>
     * {@link EventListener#addRegister(Class, EventListener, int)}
     */
    @SuppressWarnings("JavadocReference")
    @IntDef({ListenerThread.UI,
            ListenerThread.CALLBACK,
            ListenerThread.OTHER})
    @Retention(RetentionPolicy.SOURCE)
    @interface ListenerThread {
        /**
         * ui thread
         */
        int UI = 0;
        /**
         * the event invoke thread<br>
         * note: if callback is remote process, the callback is binder thread
         */
        int CALLBACK = 1;
        /**
         * background thread
         */
        int OTHER = 2;
    }

    /**
     * Event listener
     */
    interface EventListener {
        void change(@NonNull final String data);
    }

    /**
     * send event
     *
     * @param key  class
     * @param data event data
     */
    void sendEvent(@NonNull final Class<?> key, @NonNull final String data);

    /**
     * send event
     *
     * @param key           class
     * @param eventListener event observer
     * @param thread        callback thread {@link ListenerThread}
     */
    void addRegister(@NonNull final Class<?> key, @NonNull final EventListener eventListener
            , @ListenerThread final int thread);

    /**
     * send event<br>
     * callback in the event invoke thread by default {@link ListenerThread#CALLBACK}
     *
     * @param key           class
     * @param eventListener event observer
     */
    void addRegister(@NonNull final Class<?> key, @NonNull final EventListener eventListener);

    /**
     * remove listener
     *
     * @param key           key
     * @param eventListener event listener
     */
    void removeRegister(@NonNull final Class<?> key, @NonNull final EventListener eventListener);

    /**
     * remove all listener with key
     *
     * @param key key
     */
    void removeRegisterAll(@NonNull final Class<?> key);
}
