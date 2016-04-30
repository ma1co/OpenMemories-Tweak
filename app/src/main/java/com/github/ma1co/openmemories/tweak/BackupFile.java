package com.github.ma1co.openmemories.tweak;

import java.io.IOException;

public class BackupFile extends Binary {
    public BackupFile(byte[] data) throws IOException {
        super(data);
        checkData();
    }

    private void checkData() throws IOException {
        if (getLength() < 0x100)
            throw new IOException("Data too short");
        String version = readCString(0xC);
        if (!"BK2".equals(version) && !"BK3".equals(version) && !"BK4".equals(version))
            throw new IOException("Unsupported backup version");
    }

    public String getRegion() {
        return readCString(0xC0);
    }
}
