package com.github.ma1co.openmemories.tweak;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class LanguageActivity extends BaseActivity {
    public static final int[] LANG_ALL  = new int[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
    public static final int[] LANG_APAC = new int[] { 1, 2, 1, 2, 1, 2, 1, 1, 1, 1, 1, 2, 2, 1, 1, 2, 2, 2, 2, 2, 1, 2, 2, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 };
    public static final int[] LANG_CN   = new int[] { 1, 2, 1, 2, 1, 2, 1, 1, 1, 1, 1, 2, 2, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 };
    public static final int[] LANG_EU   = new int[] { 1, 2, 1, 1, 1, 1, 1, 2, 2, 2, 2, 1, 1, 2, 2, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 1, 2, 1, 2, 2, 1, 2, 2, 2, 2 };
    public static final int[] LANG_JP   = new int[] { 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 };
    public static final int[] LANG_US   = new int[] { 1, 2, 1, 2, 1, 1, 2, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 };

    public static int[] getLangsForRegion(String r) {
        if ("ALLLANG".equals(r))
            return LANG_ALL;
        else if ("AP2".equals(r) || "IN5".equals(r))
            return LANG_APAC;
        else if ("CEC".equals(r) || "CE".equals(r) || "RU2".equals(r))
            return LANG_EU;
        else if ("CN2".equals(r) || "E38".equals(r) || "JE3".equals(r) || "KR2".equals(r) || "TW6".equals(r))
            return LANG_CN;
        else if ("J1".equals(r))
            return LANG_JP;
        else if ("UC2".equals(r))
            return LANG_US;
        else
            return null;
    }

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
        checkBackupByteValues(BackupKeys.LANGUAGE_ACTIVE_LIST, LANGUAGE_ENABLED, LANGUAGE_DISABLED);
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

    protected void writeActiveLanguages(int[] langs) throws NativeException {
        for (int i = 0; i < BackupKeys.LANGUAGE_ACTIVE_LIST.length; i++)
            Backup.setValue(BackupKeys.LANGUAGE_ACTIVE_LIST[i], new byte[]{(byte) langs[i]});
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

    protected void setActiveLanguages(int[] langs) {
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

    protected void resetActiveLanguages() {
        try {
            Logger.info("resetActiveLanguages", "attempting to read region");
            String region = Backup.readData().getRegion();
            Logger.info("resetActiveLanguages", "region is " + region);
            int[] langs = getLangsForRegion(region.split("_")[1]);
            if (langs == null)
                throw new BackupCheckException("Unknown region: " + region);
            setActiveLanguages(langs);
            Logger.info("resetActiveLanguages", "done");
        } catch (Exception e) {
            Logger.error("resetActiveLanguages", e);
            showError(e);
        }
    }

    public void onActivateAllButtonClicked(View view) {
        setActiveLanguages(LANG_ALL);
    }

    public void onResetButtonClicked(View view) {
        resetActiveLanguages();
    }
}
