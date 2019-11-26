package com.example.eventhandler;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;

import com.example.eventhandler.process.EventService;
import com.example.eventhandler.process.EventServiceUtils;
import com.example.eventhandler.utils.EventLog;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * remote event impl
 */
public class RemoteEventImpl implements IEvent {
    private static final String TAG = "RemoteEventImpl";
    private Context context;
    private IBinder service;
    /**
     * save event remote listeners
     */
    private final Map<EventListener, IBinder> eventRemoteListenerHashMap = new ConcurrentHashMap<>();

    public void init(Context context) {
        this.context = context;
        EventServiceUtils.getService(context, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                RemoteEventImpl.this.service = service;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                service = null;
            }
        });
    }

    @Override
    public void sendEvent(@NonNull final Class<?> key, @NonNull final String data) {
        try {
            if (service != null && service instanceof EventService.ServiceBinder) {
                ((EventService.ServiceBinder) service).sendEvent(key.getName(), data);
            }
            if (service == null) {
                init(context);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void addRegister(@NonNull final Class<?> key, @NonNull final EventListener eventListener
            , @ListenerThread final int thread) {
        //remote register
        ISendEvent stub = ISendEvent.Stub.asInterface(service);
        if (stub != null) {
            try {
                IBinder iBinder = getRemoteIBinder(eventListener, thread);
                stub.addListenerBinder(key.getName(), iBinder);
                if (eventRemoteListenerHashMap.containsKey(eventListener)) {
                    EventLog.d(TAG, "the key:" + key.getName() + "already has remote listener: "
                            + iBinder.toString());
                    return;
                }
                eventRemoteListenerHashMap.put(eventListener, iBinder);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        //if service binder is null, try connection it.
        if (service == null) {
            init(context);
        }
    }

    @Override
    public void addRegister(@NonNull final Class<?> key, @NonNull final EventListener eventListener) {
        addRegister(key, eventListener, ListenerThread.CALLBACK);
    }

    @Override
    public void removeRegister(@NonNull final Class<?> key, @NonNull final EventListener eventListener) {
        ISendEvent stub = ISendEvent.Stub.asInterface(service);
        if (stub != null) {
            IBinder iBinder = eventRemoteListenerHashMap.get(eventListener);
            if (iBinder != null) {
                try {
                    stub.removeListenerBinder(key.getName(), iBinder);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void removeRegisterAll(@NonNull final Class<?> key) {
        ISendEvent stub = ISendEvent.Stub.asInterface(service);
        if (stub != null) {
            try {
                stub.removeListenerAll(key.getName());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * get a remote binder
     *
     * @param eventListener event listener
     * @return remote binder
     */
    private IBinder getRemoteIBinder(@NonNull final EventListener eventListener, @ListenerThread final int thread) {
        return new ISendEvent.Stub() {
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
        };
    }
}
