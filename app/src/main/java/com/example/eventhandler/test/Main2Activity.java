package com.example.eventhandler.test;

import android.os.Bundle;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.eventhandler.Event;
import com.example.eventhandler.IEvent;
import com.example.eventhandler.R;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        findViewById(R.id.content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Event.getEventImpl().addRegister(Person.class, new IEvent.EventListener() {
                    @Override
                    public void change(@NonNull String data) {
                        Log.e("siyehua", "current process:" + Process.myPid() + " data: " + data);
                    }
                });
            }
        });

    }
}
