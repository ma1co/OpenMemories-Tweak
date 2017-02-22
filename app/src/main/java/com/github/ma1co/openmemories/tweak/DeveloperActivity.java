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

import java.util.concurrent.TimeoutException;

public class DeveloperActivity extends ItemActivity {
    private ConnectivityManager connectivityManager;
    private WifiManager wifiManager;
    private BroadcastReceiver receiver;
    private BaseItem wifiSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        wifiSwitch = addSwitch("Enable Wifi", new SwitchItem.Adapter() {
            @Override
            public boolean isAvailable() {
                return true;
            }

            @Override
            public boolean isEnabled() {
                return isWifiEnabled();
            }

            @Override
            public void setEnabled(boolean enabled) {
                setWifiEnabled(enabled);
            }

            @Override
            public String getSummary() {
                switch (wifiManager.getWifiState()) {
                    case WifiManager.WIFI_STATE_ENABLED:
                        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                        if (networkInfo.isConnected()) {
                            return String.format("Connected to %s (IP: %s)", wifiInfo.getSSID(), Formatter.formatIpAddress(wifiInfo.getIpAddress()));
                        } else {
                            NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState());
                            switch (state) {
                                case SCANNING:
                                    return "Scanning...";
                                case AUTHENTICATING:
                                case CONNECTING:
                                case OBTAINING_IPADDR:
                                    return "Connecting...";
                                default:
                                    return "Wifi enabled";
                            }
                        }
                    case WifiManager.WIFI_STATE_ENABLING:
                        return "Enabling...";
                    default:
                        return "Wifi disabled";
                }
            }
        });

        addLabel("Please disable Wifi before switching off the camera");

        addSwitch("Enable Telnet", new SwitchItem.Adapter() {
            private final String[] telnetStartCommand = new String[] { "busybox", "telnetd", "-l", "sh" };

            private int getTelnetPid() {
                return Procfs.findProcess(telnetStartCommand);
            }

            private void enableTelnet() throws NativeException {
                Shell.exec(TextUtils.join(" ", telnetStartCommand));
            }

            private void disableTelnet() throws NativeException {
                int pid = getTelnetPid();
                if (pid != -1)
                    Shell.exec("kill -HUP " + pid + " $(ps -o pid= --ppid " + pid + ")");
            }

            @Override
            public boolean isAvailable() {
                return true;
            }

            @Override
            public boolean isEnabled() {
                return getTelnetPid() != -1;
            }

            @Override
            public void setEnabled(final boolean enabled) throws InterruptedException, NativeException, TimeoutException {
                try {
                    Logger.info("TelnetAdapter.setEnabled", "setting telnetd to " + enabled);
                    if (enabled)
                        enableTelnet();
                    else
                        disableTelnet();

                    Condition.waitFor(new Condition.Runnable() {
                        @Override
                        public boolean run() {
                            return isEnabled() == enabled;
                        }
                    }, 500, 2000);
                    Logger.info("TelnetAdapter.setEnabled", "done");
                } catch (InterruptedException | NativeException | TimeoutException e) {
                    Logger.error("TelnetAdapter.setEnabled", e);
                    throw e;
                }
            }

            @Override
            public String getSummary() {
                return isEnabled() ? "telnetd running on port 23" : "telnetd stopped";
            }
        });

        addSwitch("Enable ADB", new SwitchItem.Adapter() {
            private String[] adbStartCommand = { getApplicationInfo().nativeLibraryDir + "/libadbd.so" };

            private int getAdbPid() {
                return Procfs.findProcess(adbStartCommand);
            }

            private void enableAdb() throws NativeException {
                Shell.execAndroid(TextUtils.join(" ", adbStartCommand) + " &");
            }

            private void disableAdb() throws NativeException {
                int pid = getAdbPid();
                if (pid != -1)
                    Shell.exec("kill -HUP " + pid);
            }

            @Override
            public boolean isAvailable() {
                return true;
            }

            @Override
            public boolean isEnabled() {
                return getAdbPid() != -1;
            }

            @Override
            public void setEnabled(final boolean enabled) throws InterruptedException, NativeException, TimeoutException {
                try {
                    Logger.info("AdbAdapter.setEnabled", "setting adbd to " + enabled);
                    if (enabled)
                        enableAdb();
                    else
                        disableAdb();

                    Condition.waitFor(new Condition.Runnable() {
                        @Override
                        public boolean run() {
                            return isEnabled() == enabled;
                        }
                    }, 500, 2000);
                    Logger.info("AdbAdapter.setEnabled", "done");
                } catch (InterruptedException | NativeException | TimeoutException e) {
                    Logger.error("AdbAdapter.setEnabled", e);
                    throw e;
                }
            }

            @Override
            public String getSummary() {
                return isEnabled() ? "adbd running on port 5555" : "adbd stopped";
            }
        });

        addButton("Wifi settings", new ButtonItem.Adapter() {
            @Override
            public void click() {
                onSettingsButtonClicked();
            }
        });

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                wifiSwitch.update();
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    public boolean isWifiEnabled() {
        int state = wifiManager.getWifiState();
        return state == WifiManager.WIFI_STATE_ENABLING || state == WifiManager.WIFI_STATE_ENABLED;
    }

    public void setWifiEnabled(boolean enabled) {
        Logger.info("setWifiEnabled", "setting wifi to " + enabled);
        wifiManager.setWifiEnabled(enabled);
    }

    public void onSettingsButtonClicked() {
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
