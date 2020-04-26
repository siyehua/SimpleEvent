package com.example.eventhandler;

import android.support.annotation.NonNull;

import com.example.eventhandler.utils.EventLog;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * local event impl
 */
public class EventLocalImpl implements IEvent {
    private static final String TAG = "LocalEventImpl";
    /**
     * save event listeners
     */
    private final Map<Class<?>, List<EventListener>> eventListenerHashMap = new ConcurrentHashMap<>();
    /**
     * save listeners callback thread
     */
    private final Map<EventListener, Integer> listenerThreadMap = new ConcurrentHashMap<>();

    @Override
    public void sendEvent(@NonNull final Class<?> key, @NonNull final String data) {
        List<EventListener> eventListeners = eventListenerHashMap.get(key);
        if (eventListeners != null) {
            for (final EventListener eventListener : eventListeners) {
                Integer thread = listenerThreadMap.get(eventListener);
                if (thread == null) {
                    throw new IllegalStateException("can't found listener thread: " + eventListener.toString());
                }
                EventImpl.callbackData(data, eventListener, thread);
            }
        }
    }

    @Override
    public void addRegister(@NonNull final Class<?> key, @NonNull final EventListener eventListener
            , @ListenerThread final int thread) {
        //local register
        List<EventListener> eventListeners = eventListenerHashMap.get(key);
        if (eventListeners != null && eventListeners.contains(eventListener)) {
            EventLog.d(TAG, "the key:" + key.getName() + "already has listener: "
                    + eventListener.toString());
            return;
        }
        if (eventListeners == null) {
            eventListeners = new CopyOnWriteArrayList<>();
        }
        eventListeners.add(eventListener);
        eventListenerHashMap.put(key, eventListeners);
        listenerThreadMap.put(eventListener, thread);
    }

    @Override
    public void addRegister(@NonNull final Class<?> key, @NonNull final EventListener eventListener) {
        addRegister(key, eventListener, ListenerThread.CALLBACK);
    }

    @Override
    public void removeRegister(@NonNull final Class<?> key, @NonNull final EventListener eventListener) {
        List<EventListener> eventListeners = eventListenerHashMap.get(key);
        if (eventListeners != null) {
            eventListeners.remove(eventListener);
        }
        listenerThreadMap.remove(eventListener);
    }

    @Override
    public void removeRegisterAll(@NonNull final Class<?> key) {
        List<EventListener> eventListeners = eventListenerHashMap.get(key);
        if (eventListeners != null) {
            eventListenerHashMap.remove(key);
            for (EventListener eventListener : eventListeners) {
                listenerThreadMap.remove(eventListener);
            }
        }
    }
}
