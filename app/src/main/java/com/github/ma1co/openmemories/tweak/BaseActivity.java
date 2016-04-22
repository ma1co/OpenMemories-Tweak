package com.github.ma1co.openmemories.tweak;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.KeyEvent;

public class BaseActivity extends Activity {
    public static class BackupCheckException extends Exception {
        public BackupCheckException(String message) {
            super(message);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.info("onResume", getComponentName().getShortClassName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logger.info("onPause", getComponentName().getShortClassName());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void showMessage(String title, String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setPositiveButton("Ok", null);
        alert.show();
    }

    public void showError(Exception e) {
        showMessage(e.getClass().getSimpleName(), e.getMessage());
    }

    protected void checkBackupByteValues(int[] ids, int min, int max) throws BackupCheckException {
        for (int id : ids) {
            try {
                int size = Backup.getSize(id);
                if (size != 1) {
                    Logger.error("checkBackupByteValues", String.format("0x%x has wrong size: %d", id, size));
                    throw new BackupCheckException("Cannot read settings file: Wrong data size");
                }
                int value = Backup.getValue(id)[0];
                if (value < min || value > max) {
                    Logger.error("checkBackupByteValues", String.format("0x%x out of bounds: %d", id, value));
                    throw new BackupCheckException("Cannot read settings file:  Value out of bounds");
                }
            } catch (NativeException e) {
                Logger.error("checkBackupByteValues", String.format("error reading 0x%x", id), e);
                throw new BackupCheckException("Read failed");
            }
        }
    }

    protected void checkBackupWritable(int[] ids) throws BackupCheckException {
        for (int id : ids) {
            try {
                byte[] value = Backup.getValue(id);
                boolean isReadOnly = Backup.isReadOnly(id);
                try {
                    Backup.setValue(id, value);
                } catch (NativeException e) {
                    if (isReadOnly) {
                        Logger.info("checkBackupWritable", String.format("0x%x is protected", id));
                        throw new BackupCheckException("Cannot change settings because protection is active. Please disable protection and try again.");
                    } else {
                        Logger.error("checkBackupWritable", String.format("error writing 0x%x", id), e);
                        throw new BackupCheckException("Write failed");
                    }
                }
            } catch (NativeException e) {
                Logger.error("checkBackupWritable", String.format("error reading 0x%x", id), e);
                throw new BackupCheckException("Read failed");
            }
        }
    }
}
