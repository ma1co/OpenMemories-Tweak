package com.github.ma1co.openmemories.tweak;

import android.os.Bundle;

import java.io.IOException;

public class ProtectionActivity extends ItemActivity {
    public static final BackupProperty.Byte TEST_KEY = BackupKeys.LANGUAGE_ACTIVE_LIST[0];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSwitch("Unlock protected settings", new SwitchItem.Adapter() {
            @Override
            public boolean isAvailable() {
                try {
                    guessProtected(TEST_KEY);
                    return true;
                } catch (BackupProperty.BackupException e) {
                    return false;
                }
            }

            @Override
            public boolean isEnabled() {
                try {
                    return !guessProtected(TEST_KEY);
                } catch (BackupProperty.BackupException e) {
                    return false;
                }
            }

            @Override
            public void setEnabled(boolean enabled) throws BackupProperty.BackupException, IOException {
                writeProtection(!enabled);
            }

            @Override
            public String getSummary() {
                return isEnabled() ? "Protection disabled" : "Protection enabled";
            }
        });
    }

    protected <T> boolean guessProtected(BackupProperty.BaseProperty<T> property) throws BackupProperty.BackupException {
        T value;
        try {
            value = property.getValue();
        } catch (BackupProperty.BackupException e) {
            Logger.error("guessProtected", String.format("error reading %s", property), e);
            throw new BackupProperty.BackupException("Read failed");
        }

        if (!property.isReadOnly()) {
            Logger.error("guessProtected", String.format("%s is not read only", property));
            throw new BackupProperty.BackupException("Test key is writable");
        }
        try {
            property.setValue(value);
            Logger.info("guessProtected", String.format("%s written successfully (no protection)", property));
            return false;
        } catch (BackupProperty.BackupException e) {
            Logger.info("guessProtected", String.format("cannot write %s (probably protection)", property));
            return true;
        }
    }

    protected void writeProtection(boolean enabled) throws BackupProperty.BackupException, IOException {
        Logger.info("writeProtection", "setting protection to " + enabled);

        Backup.cleanup();
        try {
            // Let's try to save the current settings to the disk. Since we're running in a chrooted
            // environment, /setting is actually interpreted as /android/setting, a folder which
            // doesn't exist. Luckily, on android 2, we have write access to /android and we can
            // create that folder. On android 4, this fails and we catch the exception. If this call
            // succeeds, it writes the settings to /android/setting/Backup.bin and
            // /android/setting/Backup.bak.
            Backup.save();
        } catch (Exception e) {
            Logger.info("writeProtection", "Backup.save() failed");
        }
        try {
            // Let's try to set the protection flag. On older cameras (android 2?), this needs
            // /android/setting/Backup.bin to exist (we've just written it above), otherwise the
            // flag isn't set and an exception is thrown. On newer cameras (android 4?), an
            // exception is thrown if the file doesn't exist, but the flag is still set.
            // On Japanese-only cameras (region J1), we can only enable protection using this
            // method, disabling it is impossible (this call will fail silently without throwing an
            // exception).
            // We just catch all exceptions and check afterwards if the flag was set.
            Backup.setProtection(enabled);
        } catch (Exception e) {
            Logger.info("writeProtection", "Backup.setProtection() failed");
        }
        Backup.cleanup();

        if (guessProtected(TEST_KEY) == enabled) {
            Logger.info("writeProtection", "success");
        } else {
            Logger.info("writeProtection", "check failed, let's try writeProtectionNative");
            writeProtectionNative(enabled);
        }
    }

    protected void writeProtectionNative(final boolean enabled) throws BackupProperty.BackupException {
        Logger.info("writeProtectionNative", "setting protection to " + enabled);

        try {
            Shell.exec("/android" + getApplicationInfo().nativeLibraryDir + "/libbackupsetid1.so " + (enabled ? "1" : "0"));

            Condition.waitFor(new Condition.Runnable() {
                @Override
                public boolean run() {
                    try {
                        return guessProtected(TEST_KEY) == enabled;
                    } catch (BackupProperty.BackupException e) {
                        return false;
                    }
                }
            }, 500, 5000);
        } catch (Exception e) {
            Logger.error("writeProtectionNative", "waitFor failed");
            throw new BackupProperty.BackupException("Native protection write failed");
        }
        Logger.info("writeProtectionNative", "success");
    }
}
