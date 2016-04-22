package com.github.ma1co.openmemories.tweak;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class LanguageActivity extends BaseActivity {
    public static final int LANGUAGE_ENABLED  = 1;
    public static final int LANGUAGE_DISABLED = 2;

    private TextView langTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        langTextView = (TextView) findViewById(R.id.langTextView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showActiveLanguages();
    }

    protected void readCheck() throws BackupCheckException {
        checkBackupByteValues(BackupKeys.LANGUAGE_ACTIVE_LIST, 1, 2);
    }

    protected void writeCheck() throws BackupCheckException {
        checkBackupWritable(BackupKeys.LANGUAGE_ACTIVE_LIST);
    }

    protected boolean[] readActiveLanguages() throws NativeException {
        boolean[] langs = new boolean[BackupKeys.LANGUAGE_ACTIVE_LIST.length];
        for (int i = 0; i < BackupKeys.LANGUAGE_ACTIVE_LIST.length; i++)
            langs[i] = Backup.getValue(BackupKeys.LANGUAGE_ACTIVE_LIST[i])[0] == LANGUAGE_ENABLED;
        return langs;
    }

    protected void writeActiveLanguages(boolean[] langs) throws NativeException {
        for (int i = 0; i < BackupKeys.LANGUAGE_ACTIVE_LIST.length; i++) {
            int value = langs[i] ? LANGUAGE_ENABLED : LANGUAGE_DISABLED;
            Backup.setValue(BackupKeys.LANGUAGE_ACTIVE_LIST[i], new byte[]{(byte) value});
        }
    }

    protected void showActiveLanguages() {
        langTextView.setText("???");
        try {
            Logger.info("showActiveLanguages", "attempting to read active languages");
            readCheck();
            boolean[] langs = readActiveLanguages();
            int count = 0;
            for (boolean active : langs)
                count += active ? 1 : 0;
            langTextView.setText(String.format("%d / %d", count, langs.length));
            Logger.info("showActiveLanguages", "done: " + count);
        } catch (Exception e) {
            Logger.error("showActiveLanguages", e);
            showError(e);
        }
    }

    protected void setActiveLanguages(boolean[] langs) {
        try {
            Logger.info("setActiveLanguages", "attempting to write active languages");
            readCheck();
            writeCheck();
            writeActiveLanguages(langs);
            showActiveLanguages();
            Logger.info("setActiveLanguages", "done");
        } catch (Exception e) {
            Logger.error("setActiveLanguages", e);
            showError(e);
        }
    }

    public void onActivateJapaneseButtonClicked(View view) {
        boolean[] langs = new boolean[BackupKeys.LANGUAGE_ACTIVE_LIST.length];
        langs[1] = true;
        setActiveLanguages(langs);
    }

    public void onActivateEnglishButtonClicked(View view) {
        boolean[] langs = new boolean[BackupKeys.LANGUAGE_ACTIVE_LIST.length];
        langs[0] = true;
        setActiveLanguages(langs);
    }

    public void onActivateAllButtonClicked(View view) {
        boolean[] langs = new boolean[BackupKeys.LANGUAGE_ACTIVE_LIST.length];
        for (int i = 0; i < langs.length; i++)
            langs[i] = true;
        setActiveLanguages(langs);
    }
}
