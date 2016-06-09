package com.github.ma1co.openmemories.tweak;

import android.text.TextUtils;

public class Procfs {
    static {
        System.loadLibrary("tweak");
    }

    private static native int nativeFindProcess(byte[] cmd);

    public static int findProcess(String[] cmd) {
        String cmdline = TextUtils.join("\00", cmd) + "\00";
        return nativeFindProcess(cmdline.getBytes());
    }
}
