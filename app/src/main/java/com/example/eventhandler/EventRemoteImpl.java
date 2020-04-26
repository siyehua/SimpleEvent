package com.example.eventhandler;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;

import com.example.eventhandler.process.EventServiceUtils;
import com.example.eventhandler.utils.EventLog;
import com.example.eventhandler.utils.TaskEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * remote event impl
 */
public class EventRemoteImpl implements IEvent {
    private static final String TAG = "RemoteEventImpl";
    private volatile Context context;
    private volatile IBinder service;
    /**
     * save event remote listeners
     */
    private final Map<EventListener, IBinder> eventRemoteListenerHashMap = new ConcurrentHashMap<>();
    private final Map<EventListener, TempEventListener> tempEventListenerMap = new ConcurrentHashMap<>();

    public void init(@NonNull Context context) {
        this.context = context;
        EventServiceUtils.getService(context, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                EventLog.d(TAG, "connected service success");
                EventRemoteImpl.this.service = service;
                fixRemoteListener();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                service = null;
                EventLog.d(TAG, "disconnected service");
            }
        });
    }

    @Override
    public void sendEvent(@NonNull final Class<?> key, @NonNull final String data) {
        //send message to remote
        //Avoid block current thread, use other thread
        TaskEvent.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    tryConnectionService();
                    if (service != null) {
                        ISendEvent stub = ISendEvent.Stub.asInterface(service);
                        stub.sendEvent(key.getName(), data);
                    } else {
                        EventLog.d(TAG, "service is null, please invoke init() method connection it");
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void addRegister(@NonNull final Class<?> key, @NonNull final EventListener eventListener
            , @ListenerThread final int thread, final boolean addTemp) {
        if (eventRemoteListenerHashMap.containsKey(eventListener)) {
            EventLog.d(TAG, "the key:" + key.getName()
                    + "already has remote listener: " + eventListener);
            return;
        }
        //remote register
        //Avoid block current thread, use other thread
        TaskEvent.execute(new Runnable() {
            @Override
            public void run() {
                tryConnectionService();
                ISendEvent stub = ISendEvent.Stub.asInterface(service);
                if (stub != null) {
                    try {
                        IBinder iBinder = getRemoteIBinder(eventListener, thread);
                        stub.addListenerBinder(key.getName(), iBinder);
                        eventRemoteListenerHashMap.put(eventListener, iBinder);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else if (addTemp) {
                    //add remote to tmp
                    tempEventListenerMap.put(eventListener, new TempEventListener(key, eventListener, thread));
                }
            }
        });
    }

    @Override
    public void addRegister(@NonNull final Class<?> key, @NonNull final EventListener eventListener
            , @ListenerThread final int thread) {
        addRegister(key, eventListener, thread, true);
    }

    @Override
    public void addRegister(@NonNull final Class<?> key, @NonNull final EventListener eventListener) {
        addRegister(key, eventListener, ListenerThread.CALLBACK);
    }

    @Override
    public void removeRegister(@NonNull final Class<?> key, @NonNull final EventListener eventListener) {
        tempEventListenerMap.remove(eventListener);
        final IBinder iBinder = eventRemoteListenerHashMap.remove(eventListener);
        if (iBinder == null) {
            return;
        }
        if (service == null || !service.isBinderAlive()) {
            //remote binder is died, so no need remove it.
            return;
        }
        //remove remote binder
        //Avoid block current thread, use other thread
        TaskEvent.execute(new Runnable() {
            @Override
            public void run() {
                ISendEvent stub = ISendEvent.Stub.asInterface(service);
                if (stub != null) {
                    try {
                        stub.removeListenerBinder(key.getName(), iBinder);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    @Override
    public void removeRegisterAll(@NonNull final Class<?> key) {
        eventRemoteListenerHashMap.clear();
        if (service == null || !service.isBinderAlive()) {
            //remote binder is died, so no need remove it.
            return;
        }
        //remove remote binder
        //Avoid block current thread, use other thread
        TaskEvent.execute(new Runnable() {
            @Override
            public void run() {
                ISendEvent stub = ISendEvent.Stub.asInterface(service);
                if (stub != null) {
                    try {
                        stub.removeListenerAll(key.getName());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void fixRemoteListener() {
        for (EventListener listener : tempEventListenerMap.keySet()) {
            TempEventListener tempEventListener = tempEventListenerMap.get(listener);
            if (tempEventListener != null) {
                addRegister(tempEventListener.key, tempEventListener.eventListener, tempEventListener.thread, false);
            }
        }
    }

    private void tryConnectionService() {
        //if service binder is null, try connection it.
        if (service == null) {
            EventLog.d(TAG, " service binder is null ,try connection it ");
            init(context);
        }
    }

    private static class RemoteSendEvent extends ISendEvent.Stub {
        @NonNull
        final EventListener eventListener;
        @ListenerThread
        final int thread;

        private RemoteSendEvent(@NonNull EventListener eventListener, @ListenerThread int thread) {
            this.eventListener = eventListener;
            this.thread = thread;
        }

        @Override
        public void sendEvent(final String className, final String data) {
            EventImpl.callbackData(data, eventListener, thread);
        }

        @Override
        public void addListenerBinder(String key, IBinder iBinder) {
        }

        @Override
        public void removeListenerBinder(String key, IBinder iBinder) {
        }

        @Override
        public void removeListenerAll(String key) {
        }
    }

    /**
     * get a remote binder
     *
     * @param eventListener event listener
     * @return remote binder
     */
    private IBinder getRemoteIBinder(@NonNull final EventListener eventListener,
                                     @ListenerThread final int thread) {
        return new RemoteSendEvent(eventListener, thread);
    }

    private static final class TempEventListener {
        private final @NonNull
        Class<?> key;
        private final @NonNull
        EventListener eventListener;
        private final @ListenerThread
        int thread;

        public TempEventListener(@NonNull Class<?> key, @NonNull EventListener eventListener, int thread) {
            this.key = key;
            this.eventListener = eventListener;
            this.thread = thread;
        }
    }
}
