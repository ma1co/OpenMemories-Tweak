package com.github.ma1co.openmemories.tweak;

public class NativeProtectionException extends NativeException {
    public NativeProtectionException() {
        super("Cannot change settings because protection is active. Please disable protection and try again.");
    }

    public NativeProtectionException(String message) {// Used by native code
        this();
    }
}
