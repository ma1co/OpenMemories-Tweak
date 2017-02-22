package com.github.ma1co.openmemories.tweak;

import android.os.Process;

public class Shell {
    static {
        System.loadLibrary("tweak");
    }

    private static native void nativeExec(String command) throws NativeException;

    public static void exec(String command) throws NativeException {
        nativeExec(command);
    }

    public static void execAndroid(String command) throws NativeException {
        int pid = Process.myPid();
        exec("ps e --no-heading " + pid + " | (" +                  // Get the environment variables of the current process
                 "read _ _ _ _ _ e;" +                              //
                 "export $e;" +                                     // Set the environment variables
                 "echo $ANDROID_PROPERTY_WORKSPACE | (" +           // Get the property service file descriptor
                     "IFS=, read f _;" +                            //
                     "eval \"exec $f< /proc/" + pid + "/fd/$f\";" + // Copy the fd
                     "/bin/busybox chroot /android " + command +    // Chroot to /android and run the command
                 ")" +
             ")");
    }
}
