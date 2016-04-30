package com.github.ma1co.openmemories.tweak;

public class Shell {
    static {
        System.loadLibrary("tweak");
    }

    private static native void nativeExec(String command);

    public static void exec(String command) {
        nativeExec(command);
    }
}
