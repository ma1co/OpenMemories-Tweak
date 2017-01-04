package com.github.ma1co.openmemories.tweak;

import java.io.File;
import java.io.IOException;

public class Backup {
    static {
        System.loadLibrary("tweak");
    }

    private static native int nativeGetSize(int id) throws NativeException;
    private static native int nativeGetAttribute(int id) throws NativeException;
    private static native byte[] nativeRead(int id) throws NativeException;
    private static native void nativeWrite(int id, byte[] data) throws NativeException;
    private static native void nativeSync() throws NativeException;
    private static native void nativeSetId1(byte value) throws NativeException;
    private static native byte[] nativeReadPresetData() throws NativeException;

    private static File settingDir = new File("/setting");
    private static File backupBinFile = new File(settingDir, "Backup.bin");
    private static File backupBakFile = new File(settingDir, "Backup.bak");

    public static void save() throws IOException, NativeException {
        settingDir.mkdir();
        if (!settingDir.isDirectory())
            throw new IOException("Cannot create setting dir");

        nativeSync();
        if (!backupBinFile.isFile())
            throw new NativeException("Backup_sync_all failed");
    }

    public static void cleanup() throws IOException {
        backupBinFile.delete();
        backupBakFile.delete();
        settingDir.delete();
        if (settingDir.exists())
            throw new IOException("Cannot delete setting dir");
    }

    public static BackupFile readData() throws IOException, NativeException {
        return new BackupFile(nativeReadPresetData());
    }

    public static void setProtection(boolean protect) throws IOException, NativeException {
        nativeSetId1((byte) (protect ? 1 : 0));
    }

    public static int getSize(int id) throws NativeException {
        return nativeGetSize(id);
    }

    public static boolean isReadOnly(int id) throws NativeException {
        return nativeGetAttribute(id) == 1;
    }

    public static byte[] getValue(int id) throws NativeException {
        return nativeRead(id);
    }

    public static void setValue(int id, byte[] data) throws NativeException {
        nativeWrite(id, data);
    }
}
