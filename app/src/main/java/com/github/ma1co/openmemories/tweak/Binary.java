package com.github.ma1co.openmemories.tweak;

import java.util.Arrays;

public class Binary {
    private final byte[] data;

    public Binary(byte[] data) {
        this.data = data;
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

    public byte[] readBytes(int offset, int length) {
        return Arrays.copyOfRange(data, offset, offset + length);
    }
}
