package com.github.ma1co.openmemories.tweak;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.View;

public class DeveloperActivity extends BaseActivity implements SwitchView.CheckedListener {
    public static final String[] telnetStartCommand = new String[] { "busybox", "telnetd", "-l", "sh" };

    private ConnectivityManager connectivityManager;
    private WifiManager wifiManager;
    private BroadcastReceiver receiver;
    private SwitchView telnetSwitch;
    private SwitchView wifiSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer);

        telnetSwitch = (SwitchView) findViewById(R.id.telnet_switch);
        telnetSwitch.setListener(this);
        wifiSwitch = (SwitchView) findViewById(R.id.wifi_switch);
        wifiSwitch.setListener(this);

        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateWifiSwitch();
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter f = new IntentFilter();
        f.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        f.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        f.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(receiver, f);

        updateTelnetSwitch();
        updateWifiSwitch();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    protected void updateTelnetSwitch() {
        boolean telnetEnabled = isTelnetEnabled();
        telnetSwitch.setChecked(telnetEnabled);
        telnetSwitch.setSummary(telnetEnabled ? "telnetd running on port 23" : "telnetd disabled");
    }

    protected void updateWifiSwitch() {
        boolean wifiEnabled = isWifiEnabled();
        String summary;
        if (wifiEnabled) {
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (networkInfo.isConnected()) {
                summary = "Connected to " + wifiInfo.getSSID() + " (IP: " + Formatter.formatIpAddress(wifiInfo.getIpAddress()) + ")";
            } else {
                NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState());
                switch (state) {
                    case SCANNING:
                        summary = "Scanning...";
                        break;
                    case AUTHENTICATING:
                    case CONNECTING:
                    case OBTAINING_IPADDR:
                        summary = "Connecting...";
                        break;
                    default:
                        summary = "Wifi enabled";
                }
            }
        } else {
            summary = "Wifi disabled";
        }
        wifiSwitch.setChecked(wifiEnabled);
        wifiSwitch.setSummary(summary);
    }

    public int getTelnetPid() {
        Logger.info("getTelnetPid", "listing running processes");
        return Procfs.findProcess(telnetStartCommand);
    }

    public boolean isTelnetEnabled() {
        return getTelnetPid() != -1;
    }

    public void setTelnetEnabled(final boolean enabled) {
        try {
            Logger.info("setTelnetEnabled", "setting telnetd to " + enabled);
            if (enabled) {
                Shell.exec(TextUtils.join(" ", telnetStartCommand));
            } else {
                int pid = getTelnetPid();
                if (pid != -1)
                    Shell.exec("kill -HUP " + pid + " $(ps -o pid= --ppid " + pid + ")");
            }

            Condition.waitFor(new Condition.Runnable() {
                @Override
                public boolean run() {
                    return isTelnetEnabled() == enabled;
                }
            }, 500, 2000);
            Logger.info("setTelnetEnabled", "done");
        } catch (Exception e) {
            Logger.error("setTelnetEnabled", e);
            showError(e);
        }

        updateTelnetSwitch();
    }

    public boolean isWifiEnabled() {
        int state = wifiManager.getWifiState();
        return state == WifiManager.WIFI_STATE_ENABLING || state == WifiManager.WIFI_STATE_ENABLED;
    }

    public void setWifiEnabled(boolean enabled) {
        Logger.info("setWifiEnabled", "setting wifi to " + enabled);
        wifiManager.setWifiEnabled(enabled);
    }

    @Override
    public void onCheckedChanged(SwitchView view, boolean checked) {
        if (telnetSwitch.equals(view))
            setTelnetEnabled(checked);
        else if (wifiSwitch.equals(view))
            setWifiEnabled(checked);
    }

    public void onSettingsButtonClicked(View view) {
        Logger.info("onSettingsButtonClicked", "starting wifi settings activity");
        boolean wifiEnabled = isWifiEnabled();
        setWifiEnabled(true);
        startActivityForResult(new Intent("com.sony.scalar.app.wifisettings.WifiSettings"), wifiEnabled ? 1 : 0);
    }

    @Override
    protected void onActivityResult(int wifiEnabled, int result, Intent intent) {
        Logger.info("onActivityResult", "back from wifi settings activity");
        super.onActivityResult(wifiEnabled, result, intent);
        setWifiEnabled(wifiEnabled == 1);
    }
}
