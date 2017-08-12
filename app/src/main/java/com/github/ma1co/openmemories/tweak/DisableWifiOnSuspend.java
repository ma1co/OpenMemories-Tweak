package com.github.ma1co.openmemories.tweak;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

public class DisableWiFiOnSuspend extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ((WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE)).setWifiEnabled(false);
    }
}