package com.github.ma1co.openmemories.tweak;

public class BackupKeys {
    public static final BackupProperty.Byte REC_LIMIT_H = new BackupProperty.Byte(0x003c0373);
    public static final BackupProperty.Byte REC_LIMIT_M = new BackupProperty.Byte(0x003c0374);
    public static final BackupProperty.Byte REC_LIMIT_S = new BackupProperty.Byte(0x003c0375);
    public static final BackupProperty.CompoundProperty<Integer> REC_LIMIT = new BackupProperty.CompoundProperty<>(Integer[].class, new BackupProperty.Byte[] {
        REC_LIMIT_H,
        REC_LIMIT_M,
        REC_LIMIT_S,
    });

    public static final BackupProperty.Short REC_LIMIT_4K = new BackupProperty.Short(0x003c04b6);

    private static final int LANGUAGE_ACTIVE_FIRST = 0x010d008f;
    private static final int LANGUAGE_ACTIVE_COUNT = 35;
    public static final BackupProperty.Byte[] LANGUAGE_ACTIVE_LIST;
    public static final BackupProperty.CompoundProperty<Integer> LANGUAGE_ACTIVE;
    static {
        LANGUAGE_ACTIVE_LIST = new BackupProperty.Byte[LANGUAGE_ACTIVE_COUNT];
        for (int i = 0; i < LANGUAGE_ACTIVE_COUNT; i++)
            LANGUAGE_ACTIVE_LIST[i] = new BackupProperty.Byte(LANGUAGE_ACTIVE_FIRST + i);
        LANGUAGE_ACTIVE = new BackupProperty.CompoundProperty<>(Integer[].class, LANGUAGE_ACTIVE_LIST);
    }

    public static final BackupProperty.Byte PAL_NTSC_SELECTOR_ENABLED = new BackupProperty.Byte(0x01070148);

    public static final BackupProperty.CString MODEL_NAME = new BackupProperty.CString(0x003e0005, 16);

    public static final BackupProperty.ByteArray SERIAL_NUMBER = new BackupProperty.ByteArray(0x00e70003, 4);

    public static final BackupProperty.CString PLATFORM_VERSION = new BackupProperty.CString(0x01660024, 8);
}
