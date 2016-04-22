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

    protected void writeProtection(boolean enabled) throws BackupCheckException, IOException, NativeException {
        Logger.info("writeProtection", "setting protection to " + enabled);
        Backup.setProtection(enabled);

        if (guessProtected(TEST_KEY) == enabled) {
            Logger.info("writeProtection", "success");
        } else {
            if (enabled) {
                Logger.error("writeProtection", "check failed");
                throw new BackupCheckException("Protection enable failed");
            } else {
                Logger.info("writeProtection", "check failed, probably JP1");
                writeProtectionJP1(enabled);
            }
        }
    }

    protected void writeProtectionJP1(boolean enabled) throws BackupCheckException, IOException, NativeException {
        Backup.cleanup();

        Logger.info("writeProtectionJP1", "reading backup data to reset region");
        BackupFile bin = Backup.getData();
        String region = bin.getRegion();
        Logger.info("writeProtectionJP1", "region: " + region);
        bin.setRegion("");
        Logger.info("writeProtectionJP1", "writing backup data without region");
        Backup.setData(bin);

        try {
            Logger.info("writeProtectionJP1", "setting protection to " + enabled);
            Backup.setProtection(enabled);
        } finally {
            Logger.info("writeProtectionJP1", "reading backup data to restore region");
            bin = Backup.getData();
            bin.setRegion(region);
            Logger.info("writeProtectionJP1", "writing backup data with region");
            Backup.setData(bin);

            Backup.cleanup();
        }

        if (guessProtected(TEST_KEY) == enabled) {
            Logger.info("writeProtectionJP1", "success");
        } else {
            Logger.error("writeProtectionJP1", "check failed");
            throw new BackupCheckException("JP1 protection write failed");
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
