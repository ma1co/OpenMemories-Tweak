package com.github.ma1co.openmemories.tweak;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class VideoActivity4k extends BaseActivity {
    public static final int MIN_LIMIT = 5;
    public static final int DEFAULT_LIMIT = 300;
    public static final int MAX_LIMIT = 0x7fff;

    private TextView limit4kTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_4k);
        limit4kTextView = (TextView) findViewById(R.id.limit4kTextView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showCurrentLimit();
    }

    protected void readCheck() throws BackupCheckException {
        checkBackupShortValues(new int[] { BackupKeys.REC_LIMIT_4K }, 0, 0x00ffff);
    }

    protected void writeCheck() throws BackupCheckException {
        checkBackupWritable(new int[] { BackupKeys.REC_LIMIT_4K });
    }

    protected int readLimit() throws NativeException {
        byte[] bytes = Backup.getValue(BackupKeys.REC_LIMIT_4K);
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        return (int)bb.getShort() & 0x00ffff;
    }

    protected void writeLimit(int limit) throws NativeException {
        Backup.setValue(BackupKeys.REC_LIMIT_4K, new byte[] { (byte)limit, (byte)(limit >> 8) });
    }

    protected void showCurrentLimit() {
        limit4kTextView.setText("???");
        try {
            Logger.info("showCurrentLimit", "attempting to read 4k video rec limit");
            readCheck();
            int limit = readLimit();
            int hours = limit / 3600;
            int minutes = (limit - (hours * 3600)) / 60;
            int seconds = limit % 60;
            limit4kTextView.setText(
                    String.format("%dh %02dm %02ds", hours, minutes, seconds));
            Logger.info("showCurrentLimit", "done: " + limit);
        } catch (Exception e) {
            Logger.error("showCurrentLimit", e);
            showError(e);
        }
    }

    protected void setLimit(int limit) {
        try {
            Logger.info("setLimit", "attempting to write 4k video rec limit: " + limit);
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

    public void onMinLimit4kButtonClicked(View view) {
        setLimit(MIN_LIMIT);
    }

    public void onDefaultLimit4kButtonClicked(View view) {
        setLimit(DEFAULT_LIMIT);
    }

    public void onMaxLimit4kButtonClicked(View view) {
        setLimit(MAX_LIMIT);
    }
}
