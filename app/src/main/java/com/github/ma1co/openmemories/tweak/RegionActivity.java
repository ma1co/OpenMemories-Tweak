package com.github.ma1co.openmemories.tweak;

import android.os.Bundle;

import java.io.IOException;

public class RegionActivity extends ItemActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSwitch("Unlock all languages", new BackupSwitchAdapter<Integer[]>(BackupKeys.LANGUAGE_ACTIVE, true) {
            private final int LANGUAGE_ENABLED  = 1;
            private final int LANGUAGE_DISABLED = 2;

            private Integer[] getLangsForRegion(String region) {
                switch (region) {
                    case "ALLLANG":
                        return new Integer[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
                    case "AP2":
                    case "IN5":
                    case "JE3":
                        return new Integer[] { 1, 2, 1, 2, 1, 2, 1, 1, 1, 1, 1, 2, 2, 1, 1, 2, 2, 2, 2, 2, 1, 2, 2, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 };
                    case "AU2":
                    case "CE7":
                    case "CN1":
                    case "E32":
                    case "E33":
                    case "E37":
                    case "EA8":
                    case "HK1":
                    case "KR2":
                    case "TW6":
                        return new Integer[] { 1, 2, 1, 2, 1, 2, 1, 1, 1, 1, 1, 2, 2, 1, 1, 2, 2, 2, 2, 2, 1, 2, 2, 1, 1, 2, 2, 2, 2, 1, 2, 1, 2, 2, 2 };
                    case "CA2":
                        return new Integer[] { 1, 2, 1, 2, 1, 1, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 };
                    case "CE":
                    case "RU2":
                        return new Integer[] { 1, 2, 1, 1, 1, 1, 1, 2, 2, 2, 2, 1, 1, 2, 2, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 1, 2, 1, 2, 2, 2, 2, 2, 2, 2 };
                    case "CE3":
                    case "CEH":
                        return new Integer[] { 1, 2, 1, 1, 1, 1, 1, 2, 2, 2, 2, 1, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2 };
                    case "CEC":
                        return new Integer[] { 1, 2, 1, 1, 1, 1, 1, 2, 2, 2, 2, 1, 1, 2, 2, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 1, 2, 1, 2, 2, 1, 2, 2, 2, 2 };
                    case "CN2":
                    case "E38":
                        return new Integer[] { 1, 2, 1, 2, 1, 2, 1, 1, 1, 1, 1, 2, 2, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2 };
                    case "J1":
                        return new Integer[] { 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 };
                    case "RU3":
                        return new Integer[] { 1, 2, 1, 1, 1, 1, 1, 2, 2, 2, 2, 1, 1, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 1, 1, 1, 2, 2, 1, 2, 2, 2, 2 };
                    case "U2":
                        return new Integer[] { 1, 2, 1, 2, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 };
                    case "UC2":
                        return new Integer[] { 1, 2, 1, 2, 1, 1, 2, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 };
                    default:
                        return null;
                }
            }

            private int countActiveLanguages() throws BackupProperty.BackupException {
                int count = 0;
                for (int lang : getProperty().getValue()) {
                    if (lang == LANGUAGE_ENABLED)
                        count += 1;
                }
                return count;
            }

            @Override
            public boolean isAvailable() {
                if (!super.isAvailable())
                    return false;
                try {
                    for (int lang : getProperty().getValue()) {
                        if (lang != LANGUAGE_DISABLED && lang != LANGUAGE_ENABLED) {
                            Logger.error("LanguageAdapter.isAvailable", String.format("unknown language value: %d", lang));
                            return false;
                        }
                    }
                } catch (BackupProperty.BackupException e) {
                    return false;
                }
                return true;
            }

            @Override
            public Integer[] getOffValue() throws BackupProperty.BackupException {
                String region;
                try {
                    region = Backup.readData().getRegion();
                } catch (IOException | NativeException e) {
                    Logger.error("LanguageAdapter.getOffValue", "error reading region", e);
                    throw new BackupProperty.BackupException("Cannot read region");
                }

                Integer[] langs = getLangsForRegion(region.split("_")[1]);
                if (langs == null) {
                    Logger.error("LanguageAdapter.getOffValue", String.format("unknown region: %s", region));
                    throw new BackupProperty.BackupException(String.format("Unknown region: %s", region));
                } else {
                    Logger.info("LanguageAdapter.getOffValue", String.format("region is %s", region));
                }
                return langs;
            }

            @Override
            public Integer[] getOnValue() {
                return getLangsForRegion("ALLLANG");
            }

            @Override
            public String getSummary() {
                try {
                    return String.format("%d / %d languages activated", countActiveLanguages(), BackupKeys.LANGUAGE_ACTIVE_LIST.length);
                } catch (BackupProperty.BackupException e) {
                    return "";
                }
            }
        });

        addSwitch("Enable PAL / NTSC selector & warning", new BackupSwitchAdapter.ConstantImpl<>(BackupKeys.PAL_NTSC_SELECTOR_ENABLED, 0, 1));
    }
}
