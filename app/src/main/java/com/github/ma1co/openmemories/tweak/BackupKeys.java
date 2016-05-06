package com.github.ma1co.openmemories.tweak;

public class BackupKeys {
    public static final int REC_LIMIT_H = 0x003c0373;
    public static final int REC_LIMIT_M = 0x003c0374;
    public static final int REC_LIMIT_S = 0x003c0375;

    public static final int REC_LIMIT_4K = 0x003c04b6;

    private static final int LANGUAGE_ACTIVE_FIRST = 0x010d008f;
    private static final int LANGUAGE_ACTIVE_COUNT = 35;
    public static final int[] LANGUAGE_ACTIVE_LIST;

    static {
        LANGUAGE_ACTIVE_LIST = new int[LANGUAGE_ACTIVE_COUNT];
        for (int i = 0; i < LANGUAGE_ACTIVE_COUNT; i++)
            LANGUAGE_ACTIVE_LIST[i] = LANGUAGE_ACTIVE_FIRST + i;
    }

    public static final int PAL_NTSC_SELECTOR_ENABLED = 0x01070148;
}
