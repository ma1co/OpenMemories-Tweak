package com.github.ma1co.openmemories.tweak;

import android.os.Bundle;

public class ProtectionActivity extends ItemActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSwitch("Unlock protected settings", new NativeTweak(NativeTweak.Key.PROTECTION) {
            @Override
            public void setEnabled(boolean enabled) throws NativeException {
                Logger.info("writeProtection", "setting protection unlock to " + enabled);
                try {
                    super.setEnabled(enabled);
                    Logger.info("writeProtection", "success");
                } catch (NativeException e) {
                    Logger.info("writeProtection", "setEnabled failed, let's try writeProtectionNative");
                    writeProtectionNative(enabled);
                }
            }

            protected void writeProtectionNative(final boolean enabled) throws NativeException {
                Logger.info("writeProtectionNative", "setting protection unlock to " + enabled);

                try {
                    Shell.exec("/android" + getApplicationInfo().nativeLibraryDir + "/libprotectiontweak.so " + (enabled ? "1" : "0"));
                    Condition.waitFor(() -> isEnabled() == enabled, 500, 5000);
                } catch (Exception e) {
                    Logger.error("writeProtectionNative", "waitFor failed");
                    throw new NativeException("Failed to set backup protection (native)");
                }
                Logger.info("writeProtectionNative", "success");
            }
        });
    }
}
