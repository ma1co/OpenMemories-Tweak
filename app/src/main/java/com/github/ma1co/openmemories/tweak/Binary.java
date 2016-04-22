package com.github.ma1co.openmemories.tweak;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

public class Binary {
    private final byte[] data;

    public Binary(byte[] data) {
        this.data = data;
    }

    public Binary(File file) throws IOException {
        FileInputStream f = new FileInputStream(file);
        data = new byte[(int) file.length()];
        f.read(data);
        f.close();
    }

    public byte[] getData() {
        return data;
    }

    public int getLength() {
        return data.length;
    }

    public String readCString(int offset) {
        int length = 0;
        while (data[offset + length] != 0)
            length++;
        return new String(data, offset, length);
    }

    public void writeCString(int offset, String str) {
        writeBytes(offset, (str + "\u0000").getBytes());
    }

    public byte[] readBytes(int offset, int length) {
        return Arrays.copyOfRange(data, offset, offset + length);
    }

    public void writeBytes(int offset, byte[] bytes) {
        System.arraycopy(bytes, 0, data, offset, bytes.length);
    }
}
