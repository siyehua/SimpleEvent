package com.example.eventhandler.test;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.example.eventhandler.Event;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Event.start();
        // if you want observe in other process, you should set:
        Event.addRemoteListener(this);
//        if (getProcessNameInDocApp(this).contains("siyehua")) {
//            try {
//                Thread.sleep(300000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
    }

    public static String getProcessNameInDocApp(Context context) {
        String mStrProcessName = "";
        File cmdFile = new File("/proc/self/cmdline");

        if (cmdFile.exists() && !cmdFile.isDirectory()) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(
                        new FileInputStream(cmdFile)));
                String procName = reader.readLine();

                if (!TextUtils.isEmpty(procName)) {
                    return procName.trim();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityManager am = (ActivityManager) context.getSystemService(
                    Context.ACTIVITY_SERVICE);
            if (null != am) {
                List<ActivityManager.RunningAppProcessInfo> appProcessInfoList =
                        am.getRunningAppProcesses();
                if (null != appProcessInfoList) {
                    for (ActivityManager.RunningAppProcessInfo i : appProcessInfoList) {
                        if (i.pid == android.os.Process.myPid()) {
                            mStrProcessName = i.processName.trim();
                        }
                    }
                }
            }
        }
        return mStrProcessName;
    }

}
