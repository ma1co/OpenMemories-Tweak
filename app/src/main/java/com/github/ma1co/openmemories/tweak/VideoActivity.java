package com.github.ma1co.openmemories.tweak;

import android.os.Bundle;

public class VideoActivity extends ItemActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSwitch("Disable video recording limit", new BackupSwitchAdapter<Integer[]>(BackupKeys.REC_LIMIT, true) {
            @Override
            public Integer[] getOffValue() throws BackupProperty.BackupException {
                // 29m50s
                return new Integer[] {0, 29, 50};
            }

            @Override
            public Integer[] getOnValue() throws BackupProperty.BackupException {
                // The recording limit is converted to 90KHz. The theoretical limit is thus
                // 13h15m21s (just before a 32bit unsigned overflow). However, all camcorders have
                // this value set to 13h01m00s, so let's just use that.
                return new Integer[] {13, 1, 0};
            }

            @Override
            public String getSummary() {
                try {
                    Integer[] limit = getProperty().getValue();
                    return String.format("Current limit: %dh %02dm %02ds", limit[0], limit[1], limit[2]);
                } catch (BackupProperty.BackupException e) {
                    return "";
                }
            }
        });

        addSwitch("Disable 4K video recording limit (RX100M4 only)", new BackupSwitchAdapter<Integer>(BackupKeys.REC_LIMIT_4K, true) {
            @Override
            public Integer getOffValue() throws BackupProperty.BackupException {
                // 5m00s
                return 300;
            }

            @Override
            public Integer getOnValue() throws BackupProperty.BackupException {
                // 9h06m07s
                return 0x7fff;
            }

            @Override
            public String getSummary() {
                try {
                    Integer limit = getProperty().getValue();
                    int hours = limit / 3600;
                    int minutes = (limit - (hours * 3600)) / 60;
                    int seconds = limit % 60;
                    return String.format("Current limit: %dh %02dm %02ds", hours, minutes, seconds);
                } catch (BackupProperty.BackupException e) {
                    return "";
                }
            }
        });
    }
}
