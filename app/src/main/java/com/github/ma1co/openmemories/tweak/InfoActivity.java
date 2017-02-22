package com.github.ma1co.openmemories.tweak;

import android.os.Build;
import android.os.Bundle;

import java.io.IOException;

public class InfoActivity extends ItemActivity {
    public static class LoggingInfoAdapter implements InfoItem.Adapter {
        private final String key;
        private final InfoItem.Adapter parent;

        public LoggingInfoAdapter(String key, InfoItem.Adapter parent) {
            this.key = key;
            this.parent = parent;
        }

        @Override
        public boolean isAvailable() {
            return parent.isAvailable();
        }

        @Override
        public String getValue() {
            String value = parent.getValue();
            Logger.info("InfoAdapter", String.format("%s: %s", key, value));
            return value;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addLoggedInfo("Model", new BackupInfoAdapter<>(BackupKeys.MODEL_NAME));

        addInfo("Serial number", new BackupInfoAdapter<byte[]>(BackupKeys.SERIAL_NUMBER) {
            @Override
            public String getValue() {
                try {
                    byte[] serial = getProperty().getValue();
                    return String.format("%x%02x%02x%02x", serial[0], serial[1], serial[2], serial[3]);
                } catch (BackupProperty.BackupException e) {
                    return "";
                }
            }
        });

        addLoggedInfo("Backup region", new InfoItem.Adapter() {
            @Override
            public boolean isAvailable() {
                return true;
            }

            @Override
            public String getValue() {
                try {
                    return Backup.readData().getRegion();
                } catch (IOException | NativeException e) {
                    return "";
                }
            }
        });

        addLoggedInfo("Tweak app version", new InfoItem.Adapter() {
            @Override
            public boolean isAvailable() {
                return true;
            }

            @Override
            public String getValue() {
                return BuildConfig.VERSION_NAME;
            }
        });

        addLoggedInfo("Android version", new InfoItem.Adapter() {
            @Override
            public boolean isAvailable() {
                return true;
            }

            @Override
            public String getValue() {
                return Build.VERSION.RELEASE + " (SDK " + Build.VERSION.SDK_INT + ")";
            }
        });

        addLoggedInfo("Java API version", new BackupInfoAdapter<>(BackupKeys.PLATFORM_VERSION));
    }

    protected BaseItem addLoggedInfo(String title, InfoItem.Adapter adapter) {
        return addInfo(title, new LoggingInfoAdapter(title, adapter));
    }
}
