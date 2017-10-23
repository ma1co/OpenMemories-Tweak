package com.github.ma1co.openmemories.tweak;

import android.os.Build;
import android.os.Bundle;

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

        addLoggedInfo("Model", new NativeProperty(NativeProperty.Key.MODEL_NAME));

        addInfo("Serial number", new NativeProperty(NativeProperty.Key.SERIAL_NUMBER));

        addLoggedInfo("Backup region", new NativeProperty(NativeProperty.Key.BACKUP_REGION));

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

        addLoggedInfo("Java API version", new NativeProperty(NativeProperty.Key.ANDROID_PLATFORM_VERSION));
    }

    protected BaseItem addLoggedInfo(String title, InfoItem.Adapter adapter) {
        return addInfo(title, new LoggingInfoAdapter(title, adapter));
    }
}
