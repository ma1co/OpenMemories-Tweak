package com.github.ma1co.openmemories.tweak;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class VideoActivity extends BaseActivity {
    public static class TimeLimit {
        public final int hours;
        public final int minutes;
        public final int seconds;

        public TimeLimit(int hours, int minutes, int seconds) {
            this.hours = hours;
            this.minutes = minutes;
            this.seconds = seconds;
        }

        @Override
        public String toString() {
            return String.format("%dh %02dm %02ds", hours, minutes, seconds);
        }
    }

    public static final TimeLimit MIN_LIMIT = new TimeLimit(0, 0, 5);
    public static final TimeLimit DEFAULT_LIMIT = new TimeLimit(0, 29, 50);
    public static final TimeLimit MAX_LIMIT = new TimeLimit(13, 1, 0);

    private TextView limitTextView;
    private final int[] allBackupKeys = {
        BackupKeys.REC_LIMIT_H,
        BackupKeys.REC_LIMIT_M,
        BackupKeys.REC_LIMIT_S,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        limitTextView = (TextView) findViewById(R.id.limitTextView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showCurrentLimit();
    }

    protected void readCheck() throws BackupCheckException {
        checkBackupByteValues(allBackupKeys, 0, 59);
    }

    protected void writeCheck() throws BackupCheckException {
        checkBackupWritable(allBackupKeys);
    }

    protected TimeLimit readLimit() throws NativeException {
        int h = Backup.getValue(BackupKeys.REC_LIMIT_H)[0];
        int m = Backup.getValue(BackupKeys.REC_LIMIT_M)[0];
        int s = Backup.getValue(BackupKeys.REC_LIMIT_S)[0];
        return new TimeLimit(h, m, s);
    }

    protected void writeLimit(TimeLimit limit) throws NativeException {
        Backup.setValue(BackupKeys.REC_LIMIT_H, new byte[] {(byte) limit.hours});
        Backup.setValue(BackupKeys.REC_LIMIT_M, new byte[] {(byte) limit.minutes});
        Backup.setValue(BackupKeys.REC_LIMIT_S, new byte[] {(byte) limit.seconds});
    }

    protected void showCurrentLimit() {
        limitTextView.setText("???");
        try {
            Logger.info("showCurrentLimit", "attempting to read video rec limit");
            readCheck();
            TimeLimit limit = readLimit();
            limitTextView.setText(limit.toString());
            Logger.info("showCurrentLimit", "done: " + limit);
        } catch (Exception e) {
            Logger.error("showCurrentLimit", e);
            showError(e);
        }
    }

    protected void setLimit(TimeLimit limit) {
        try {
            Logger.info("setLimit", "attempting to write video rec limit: " + limit);
            readCheck();
            writeCheck();
            writeLimit(limit);
            showCurrentLimit();
            Logger.info("setLimit", "done");
        } catch (Exception e) {
            Logger.error("setLimit", e);
            showError(e);
        }
    }

    public void onMinLimitButtonClicked(View view) {
        setLimit(MIN_LIMIT);
    }

    public void onDefaultLimitButtonClicked(View view) {
        setLimit(DEFAULT_LIMIT);
    }

    public void onMaxLimitButtonClicked(View view) {
        setLimit(MAX_LIMIT);
    }
}
