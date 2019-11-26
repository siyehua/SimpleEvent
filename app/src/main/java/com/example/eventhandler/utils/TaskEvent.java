package com.example.eventhandler.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskEvent {
    private static Handler mainLooperHandler = new Handler(Looper.getMainLooper());
    private static ExecutorService executorService = Executors.newSingleThreadExecutor();

    public static void executeUI(Runnable runnable) {
        mainLooperHandler.post(runnable);
    }

    public static void execute(Runnable runnable) {
        executorService.execute(runnable);
    }
}
