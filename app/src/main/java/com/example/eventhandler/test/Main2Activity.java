package com.example.eventhandler.test;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.eventhandler.Event;
import com.example.eventhandler.IEvent;
import com.example.eventhandler.R;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        final TextView textView = findViewById(R.id.content);
        Event.getEventImpl().addRegister(Person.class, new IEvent.EventListener() {
            @Override
            public void change(@NonNull String data) {
                textView.setText("远程服务监听结果：" + data);
            }
        }, IEvent.ListenerThread.UI);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Event.getEventImpl().sendEvent(Person.class, "new process to other process");


            }
        });
    }
}
