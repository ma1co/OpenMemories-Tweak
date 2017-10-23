package com.github.ma1co.openmemories.tweak;

public class NativeTweak implements ItemActivity.SwitchItem.Adapter {
    public enum Key {
        LANGUAGE("language"),
        PAL_NTSC_SELECTOR("pal_ntsc_selector"),
        PROTECTION("protection"),
        REC_LIMIT("rec_limit"),
        REC_LIMIT_4K("rec_limit_4k");

        public final String str;
        Key(String str) {
            this.str = str;
        }
    }

    static {
        System.loadLibrary("tweak");
    }

    private static native boolean nativeIsAvailable(String key);
    private static native boolean nativeIsEnabled(String key);
    private static native void nativeSetEnabled(String key, boolean enabled) throws NativeException;
    private static native String nativeGetStringValue(String key);

    private final String key;

    public NativeTweak(Key key) {
        this.key = key.str;
    }

    @Override
    public boolean isAvailable() {
        return nativeIsAvailable(key);
    }

    @Override
    public boolean isEnabled() {
        return nativeIsEnabled(key);
    }

    @Override
    public void setEnabled(boolean enabled) throws NativeException {
        nativeSetEnabled(key, enabled);
    }

    @Override
    public String getSummary() {
        return nativeGetStringValue(key);
    }
}
