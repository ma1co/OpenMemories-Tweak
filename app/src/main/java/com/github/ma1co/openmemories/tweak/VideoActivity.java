package com.github.ma1co.openmemories.tweak;

import android.app.AlertDialog;
import android.os.Bundle;

public class VideoActivity extends ItemActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addSwitch("Disable video recording limit", new NativeTweak(NativeTweak.Key.REC_LIMIT));
        addSwitch("Disable 4K video recording limit (RX100M4 only)", new NativeTweak(NativeTweak.Key.REC_LIMIT_4K));
        addButton("Clean HDMI output (α5000)", this::onA5000HDMIButtonClicked);
        addButton("Clean HDMI output (NEX-5/6)", this::onNexHDMIButtonClicked);
    }

    private void onA5000HDMIButtonClicked() {
        Logger.info("onA5000HDMIButtonClicked", "setting 0x01070a47 to 0 to enable clean HDMI mode");
        try {
            Shell.exec("bk.elf w 0x01070a47 00");
        } catch (NativeException e) {
            throw new RuntimeException(e);
        }
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Clean HDMI output (α5000)");
        alert.setMessage("This enables the hidden clean HDMI mode on α5000.");
        alert.setPositiveButton("Ok", null);
        alert.show();
    }

    private void onNexHDMIButtonClicked() {
        Logger.info("onNexHDMIButtonClicked", "setting video codec to xavc to diable UI showing in HDMI");
        try {
            Shell.exec("bk.elf w 0x01070087 0a");
        } catch (NativeException e) {
            throw new RuntimeException(e);
        }
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Clean HDMI output (NEX-5/6)");
        alert.setMessage("The video codec has now been set to XAVC to disable the recording function and prevent the UI showing in HDMI.\nChange it back to AVCHD to enable recording again.");
        alert.setPositiveButton("Ok", null);
        alert.show();
    }
}
