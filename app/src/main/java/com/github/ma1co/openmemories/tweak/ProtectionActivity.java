package com.github.ma1co.openmemories.tweak;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

public class ProtectionActivity extends BaseActivity {
    public static final int TEST_KEY = BackupKeys.LANGUAGE_ACTIVE_LIST[0];

    private TextView protectionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protection);
        protectionTextView = (TextView) findViewById(R.id.protectionTextView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showCurrentProtection();
    }

    protected boolean guessProtected(int id) throws BackupCheckException {
        try {
            byte[] value = Backup.getValue(id);
            boolean isReadOnly = Backup.isReadOnly(id);
            if (!isReadOnly) {
                Logger.error("guessProtected", String.format("0x%x not read only", id));
                throw new BackupCheckException("Test key is writable");
            }
            try {
                Backup.setValue(id, value);
                Logger.info("guessProtected", String.format("0x%x written successfully (no protection)", id));
                return false;
            } catch (NativeException e) {
                Logger.info("guessProtected", String.format("cannot write 0x%x (probably protection)", id));
                return true;
            }
        } catch (NativeException e) {
            Logger.error("guessProtected", String.format("error reading 0x%x", id), e);
            throw new BackupCheckException("Read failed");
        }
    }

    protected void writeProtection(boolean enabled) throws BackupCheckException, InterruptedException, IOException, NativeException {
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

    protected void writeProtectionNative(boolean enabled) throws BackupCheckException, NativeException, InterruptedException {
        Logger.info("writeProtectionNative", "setting protection to " + enabled);

        Shell.exec("/android" + getApplicationInfo().nativeLibraryDir + "/libbackupsetid1.so " + (enabled ? "1" : "0"));
        Thread.sleep(3000);// Give it some time

        if (guessProtected(TEST_KEY) == enabled) {
            Logger.info("writeProtectionNative", "success");
        } else {
            Logger.error("writeProtectionNative", "check failed");
            throw new BackupCheckException("native protection write failed");
        }
    }

    protected void showCurrentProtection() {
        protectionTextView.setText("???");
        try {
            Logger.info("showCurrentProtection", "attempting to guess protection");
            boolean enabled = guessProtected(TEST_KEY);
            protectionTextView.setText(enabled ? "Protection enabled" : "Protection disabled");
            Logger.info("showCurrentProtection", "done: " + enabled);
        } catch (Exception e) {
            Logger.error("showCurrentProtection", e);
            showError(e);
        }
    }

    protected void setProtection(boolean enabled) {
        try {
            Logger.info("setProtection", "attempting to set protection to " + enabled);
            guessProtected(TEST_KEY);
            writeProtection(enabled);
            showCurrentProtection();
            Logger.info("setProtection", "done");
        } catch (Exception e) {
            Logger.error("setProtection", e);
            showError(e);
        }
    }

    public void onEnableProtectionButtonClicked(View view) {
        setProtection(true);
    }

    public void onDisableProtectionButtonClicked(View view) {
        setProtection(false);
    }
}
