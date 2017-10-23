package com.github.ma1co.openmemories.tweak;

public class NativeProperty implements ItemActivity.InfoItem.Adapter {
    public enum Key {
        ANDROID_PLATFORM_VERSION("android_platform_version"),
        BACKUP_REGION("backup_region"),
        MODEL_NAME("model_name"),
        SERIAL_NUMBER("serial_number");

        public final String str;
        Key(String str) {
            this.str = str;
        }
    }

    static {
        System.loadLibrary("tweak");
    }

    private static native boolean nativeIsAvailable(String key);
    private static native String nativeGetStringValue(String key);

    private final String key;

    public NativeProperty(Key key) {
        this.key = key.str;
    }

    @Override
    public boolean isAvailable() {
        return nativeIsAvailable(key);
    }

    @Override
    public String getValue() {
        return nativeGetStringValue(key);
    }
}
