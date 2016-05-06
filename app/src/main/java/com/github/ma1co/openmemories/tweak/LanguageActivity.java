package com.github.ma1co.openmemories.tweak;

import android.os.Bundle;

public class LanguageActivity extends BaseActivity implements SwitchView.CheckedListener {
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

    private SwitchView langSwitch;
    private SwitchView palNtscSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        langSwitch = (SwitchView) findViewById(R.id.lang_switch);
        langSwitch.setListener(this);
        palNtscSwitch = (SwitchView) findViewById(R.id.pal_ntsc_switch);
        palNtscSwitch.setListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showActiveLanguages();
        showPalNtscEnabled();
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
        try {
            Logger.info("showActiveLanguages", "attempting to read active languages");
            checkBackupByteValues(BackupKeys.LANGUAGE_ACTIVE_LIST, LANGUAGE_ENABLED, LANGUAGE_DISABLED);
            boolean[] langs = readActiveLanguages();
            int count = 0;
            for (boolean active : langs)
                count += active ? 1 : 0;
            langSwitch.setChecked(count == langs.length);
            langSwitch.setSummary(String.format("%d / %d activated", count, langs.length));
            Logger.info("showActiveLanguages", "done: " + count);
        } catch (Exception e) {
            langSwitch.setEnabled(false);
            Logger.error("showActiveLanguages", e);
            showError(e);
        }
    }

    protected void setActiveLanguages(int[] langs) {
        try {
            Logger.info("setActiveLanguages", "attempting to write active languages");
            checkBackupWritable(BackupKeys.LANGUAGE_ACTIVE_LIST);
            writeActiveLanguages(langs);
            Logger.info("setActiveLanguages", "done");
        } catch (Exception e) {
            Logger.error("setActiveLanguages", e);
            showError(e);
        }
        showActiveLanguages();
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

    protected boolean readPalNtscEnabled() throws NativeException {
        return Backup.getValue(BackupKeys.PAL_NTSC_SELECTOR_ENABLED)[0] == 1;
    }

    protected void writePalNtscEnabled(boolean enabled) throws NativeException {
        Backup.setValue(BackupKeys.PAL_NTSC_SELECTOR_ENABLED, new byte[] {(byte) (enabled ? 1 : 0)});
    }

    protected void showPalNtscEnabled() {
        try {
            Logger.info("showPalNtscEnabled", "attempting to read pal / ntsc");
            checkBackupByteValues(new int[] {BackupKeys.PAL_NTSC_SELECTOR_ENABLED}, 0, 1);
            boolean enabled = readPalNtscEnabled();
            palNtscSwitch.setChecked(enabled);
            palNtscSwitch.setSummary(String.format(enabled ? "Enabled" : "Disabled"));
            Logger.info("showPalNtscEnabled", "done: " + enabled);
        } catch (Exception e) {
            palNtscSwitch.setEnabled(false);
            Logger.error("showPalNtscEnabled", e);
            showError(e);
        }
    }

    protected void setPalNtscEnabled(boolean enabled) {
        try {
            Logger.info("setPalNtscEnabled", "attempting to write pal / ntsc: " + enabled);
            checkBackupWritable(new int[] {BackupKeys.PAL_NTSC_SELECTOR_ENABLED});
            writePalNtscEnabled(enabled);
            Logger.info("setPalNtscEnabled", "done");
        } catch (Exception e) {
            Logger.error("setPalNtscEnabled", e);
            showError(e);
        }
        showPalNtscEnabled();
    }

    @Override
    public void onCheckedChanged(SwitchView view, boolean checked) {
        if (langSwitch.equals(view)) {
            if (checked)
                setActiveLanguages(LANG_ALL);
            else
                resetActiveLanguages();
        } else if(palNtscSwitch.equals(view)) {
            setPalNtscEnabled(checked);
        }
    }
}
