package com.example.eventhandler.test;

import android.content.Intent;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.eventhandler.Event;
import com.example.eventhandler.IEvent;
import com.example.eventhandler.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.tv_content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEventInOtherThread();
                startActivity(new Intent(MainActivity.this, Main2Activity.class));
            }
        });
//        testUIListener();

    }

    private void sendEventInUIThread() {
        Event.getEventImpl().sendEvent(Person.class, "data ,from process: " + Process.myPid()
                + " thread:" + Thread.currentThread().getName());

    }

    private void sendEventInOtherThread() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Event.getEventImpl().sendEvent(Person.class, "data ,from process: " + Process.myPid()
                        + " thread:" + Thread.currentThread().getName());
            }
        }.start();
    }


    private void testUIListener() {
        Event.getEventImpl().addRegister(Person.class, new IEvent.EventListener() {
            @Override
            public void change(@NonNull String data) {
                Log.e("siyehua", "ui listener, callback in invoke thread: " + Thread.currentThread().getName()
                        + " data : " + data);
            }
        }, IEvent.ListenerThread.CALLBACK);
        Event.getEventImpl().addRegister(Person.class, new IEvent.EventListener() {
            @Override
            public void change(@NonNull String data) {
                Log.e("siyehua", "ui listener, callback in ui thread: " + Thread.currentThread().getName()
                        + " data : " + data);
            }
        }, IEvent.ListenerThread.UI);
        Event.getEventImpl().addRegister(Person.class, new IEvent.EventListener() {
            @Override
            public void change(@NonNull String data) {
                Log.e("siyehua", "ui listener, callback in other thread: " + Thread.currentThread().getName()
                        + " data : " + data);
            }
        }, IEvent.ListenerThread.OTHER);
    }

    private void testChildThreadListener() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                Event.getEventImpl().addRegister(Person.class, new IEvent.EventListener() {
                    @Override
                    public void change(@NonNull String data) {
                        Log.e("siyehua", "child thread listener, callback in invoke thread: " + Thread.currentThread().getName()
                                + " data : " + data);
                    }
                }, IEvent.ListenerThread.CALLBACK);
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                super.run();
                Event.getEventImpl().addRegister(Person.class, new IEvent.EventListener() {
                    @Override
                    public void change(@NonNull String data) {
                        Log.e("siyehua", "child thread listener, callback in ui thread: " + Thread.currentThread().getName()
                                + " data : " + data);
                    }
                }, IEvent.ListenerThread.UI);
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                super.run();
                Event.getEventImpl().addRegister(Person.class, new IEvent.EventListener() {
                    @Override
                    public void change(@NonNull String data) {
                        Log.e("siyehua", "child thread listener, callback in other thread: " + Thread.currentThread().getName()
                                + " data : " + data);
                    }
                }, IEvent.ListenerThread.UI);
            }
        }.start();
    }
}
