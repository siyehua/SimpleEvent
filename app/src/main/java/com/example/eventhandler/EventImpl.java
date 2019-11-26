package com.example.eventhandler;

import android.support.annotation.NonNull;

import com.example.eventhandler.utils.TaskEvent;

import java.util.ArrayList;
import java.util.List;

public class EventImpl implements IEvent {
    @NonNull
    private final List<IEvent> decoratorEvents = new ArrayList<>();

    public void addEvent(@NonNull final IEvent iEvent) {
        decoratorEvents.add(iEvent);
    }

    @Override
    public void sendEvent(@NonNull final Class<?> key, @NonNull final String data) {
        for (IEvent decoratorEvent : decoratorEvents) {
            decoratorEvent.sendEvent(key, data);
        }
    }

    @Override
    public void addRegister(@NonNull final Class<?> key, @NonNull final EventListener eventListener
            , @ListenerThread final int thread) {
        for (IEvent decoratorEvent : decoratorEvents) {
            decoratorEvent.addRegister(key, eventListener, thread);
        }
    }


    @Override
    public void addRegister(@NonNull final Class<?> key, @NonNull final EventListener eventListener) {
        for (IEvent decoratorEvent : decoratorEvents) {
            decoratorEvent.addRegister(key, eventListener);
        }
    }

    @Override
    public void removeRegister(@NonNull final Class<?> key, @NonNull final EventListener eventListener) {
        for (IEvent decoratorEvent : decoratorEvents) {
            decoratorEvent.removeRegister(key, eventListener);
        }

    }

    @Override
    public void removeRegisterAll(@NonNull final Class<?> key) {
        for (IEvent decoratorEvent : decoratorEvents) {
            decoratorEvent.removeRegisterAll(key);
        }
    }


    /**
     * callback data
     *
     * @param data          data
     * @param eventListener listener
     * @param thread        call thread
     */
    static void callbackData(@NonNull final String data, @NonNull final EventListener eventListener
            , @NonNull final Integer thread) {
        if (thread == ListenerThread.CALLBACK) {
            eventListener.change(data);
        } else if (thread == ListenerThread.UI) {
            TaskEvent.executeUI(new Runnable() {
                @Override
                public void run() {
                    eventListener.change(data);
                }
            });
        } else if (thread == ListenerThread.OTHER) {
            TaskEvent.execute(new Runnable() {
                @Override
                public void run() {
                    eventListener.change(data);
                }
            });
        }
    }

}
