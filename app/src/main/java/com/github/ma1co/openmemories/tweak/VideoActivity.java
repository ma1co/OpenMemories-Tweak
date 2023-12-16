package com.github.ma1co.openmemories.tweak;

import android.app.AlertDialog;
import android.os.Bundle;

public class VideoActivity extends ItemActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addSwitch("Disable video recording limit", new NativeTweak(NativeTweak.Key.REC_LIMIT));
        addSwitch("Disable 4K video recording limit (RX100M4 only)", new NativeTweak(NativeTweak.Key.REC_LIMIT_4K));
        addButton("Clean HDMI output", this::onHDMIButtonClicked);
    }

    private void onHDMIButtonClicked() {
        Logger.info("onHDMIButtonClicked", "setting video codec to xavc to diable UI showing in HDMI");
        try {
            Shell.exec("bk.elf w 0x01070087 0a");
        } catch (NativeException e) {
            throw new RuntimeException(e);
        }
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("HDMI clean output");
        alert.setMessage("The video codec has now been set to XAVC to disable the recording function and prevent the UI showing in HDMI.\nChange it back to AVCHD to enable recording again.");
        alert.setPositiveButton("Ok", null);
        alert.show();
    }
}
