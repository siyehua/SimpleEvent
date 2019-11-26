package com.example.eventhandler.process;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.support.annotation.NonNull;

import static android.content.Context.BIND_AUTO_CREATE;

public class EventServiceUtils {
    public static void getService(@NonNull final Context context, @NonNull final ServiceConnection serviceConnection) {
        Intent intent = new Intent(context, EventService.class);
        context.bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }
}
