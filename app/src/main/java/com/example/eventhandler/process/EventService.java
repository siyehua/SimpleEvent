package com.example.eventhandler.process;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.example.eventhandler.ISendEvent;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventService extends Service {
    public EventService() {
    }


    @Override
    public IBinder onBind(Intent intent) {
        return new ServiceBinder();
    }

    public class ServiceBinder extends ISendEvent.Stub {
        private Map<String, List<IBinder>> stringListMap = new ConcurrentHashMap<>();

        @Override
        public void sendEvent(String className, String data) throws RemoteException {
            List<IBinder> eventListeners = stringListMap.get(className);
            if (eventListeners != null) {
                for (IBinder iBinder : eventListeners) {
                    ISendEvent sendEvent = ISendEvent.Stub.asInterface(iBinder);
                    if (sendEvent != null) {
                        sendEvent.sendEvent(className, data);
                    }
                }
            }

        }

        @Override
        public void addListenerBinder(final String key, final IBinder iBinder) throws RemoteException {
            List<IBinder> eventListeners = stringListMap.get(key);
            if (eventListeners != null && eventListeners.contains(iBinder)) {
                return;
            }
            if (eventListeners == null) {
                eventListeners = new CopyOnWriteArrayList<>();
            }
            iBinder.linkToDeath(new IBinder.DeathRecipient() {
                //remove binder when process died
                @Override
                public void binderDied() {
                    try {
                        removeListenerBinder(key, iBinder);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }, 0);
            eventListeners.add(iBinder);
            stringListMap.put(key, eventListeners);
        }

        @Override
        public void removeListenerBinder(String key, IBinder iBinder) throws RemoteException {
            if (!(iBinder instanceof ISendEvent.Stub)) {
                return;
            }
            List<IBinder> eventListeners = stringListMap.get(key);
            if (eventListeners != null) {
                eventListeners.remove(iBinder);
            }
        }

        @Override
        public void removeListenerAll(String key) {
            stringListMap.remove(key);
        }
    }

}
