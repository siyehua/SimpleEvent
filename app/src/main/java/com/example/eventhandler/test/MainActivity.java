package com.example.eventhandler.test;

import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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
                sendEventInUIThread();
            }
        });
        findViewById(R.id.tv_content1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEventInOtherThread();
            }
        });
        final TextView textView2 = findViewById(R.id.tv_content2);
        Event.getEventImpl().addRegister(Person.class, new IEvent.EventListener() {
            @Override
            public void change(@NonNull String data) {
                textView2.setText("UI线程监听数据结果：" + data);
            }
        }, IEvent.ListenerThread.UI);
        final TextView textView3 = findViewById(R.id.tv_content3);
        Event.getEventImpl().addRegister(Person.class, new IEvent.EventListener() {
            @Override
            public void change(@NonNull final String data) {
                textView3.post(new Runnable() {
                    @Override
                    public void run() {
                        textView3.setText("其他线程监听数据结果：" + data);

                    }
                });
            }
        }, IEvent.ListenerThread.OTHER);

        findViewById(R.id.tv_content4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEventInOtherThread();
                startActivity(new Intent(MainActivity.this, Main2Activity.class));
            }
        });
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
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.e("siyehua", "start send event...");
                Event.getEventImpl().sendEvent(Person.class, "data ,from process: " + Process.myPid()
                        + " thread:" + Thread.currentThread().getName());
            }
        }.start();
    }

}
