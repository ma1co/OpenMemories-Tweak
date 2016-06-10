package com.github.ma1co.openmemories.tweak;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends TabActivity {
    public static final File LOG_FILE = new File("/sdcard/TWEAKLOG.TXT");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable exp) {
                Logger.error("UncaughtExceptionHandler", exp);
                saveLog();
                System.exit(0);
            }
        });

        addTab("info", "Info", android.R.drawable. ic_menu_info_details, InfoActivity.class);
        addTab("video", "Video", android.R.drawable.ic_menu_camera, VideoActivity.class);
        addTab("region", "Region", android.R.drawable.ic_menu_mapmode, RegionActivity.class);
        addTab("protection", "Protection", android.R.drawable.ic_lock_lock, ProtectionActivity.class);
        addTab("developer", "Developer", android.R.drawable.ic_menu_manage, DeveloperActivity.class);
    }

    protected void addTab(String tag, String label, int iconId, Class activity) {
        TabHost.TabSpec tab = getTabHost().newTabSpec(tag);
        tab.setIndicator(label, getResources().getDrawable(iconId));
        tab.setContent(new Intent(this, activity));
        getTabHost().addTab(tab);
    }

    @Override
    protected void onResume() {
        Logger.info("MainActivity", "application start");
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logger.info("MainActivity", "application end");
        saveLog();
    }

    protected void saveLog() {
        try {
            FileWriter writer = new FileWriter(LOG_FILE, true);
            writer.write(Logger.getLogs() + "\n");
            writer.close();
            Logger.reset();
        } catch (IOException e) {}
    }
}
