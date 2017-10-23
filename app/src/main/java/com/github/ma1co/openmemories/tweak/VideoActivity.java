package com.github.ma1co.openmemories.tweak;

import android.os.Bundle;

public class VideoActivity extends ItemActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addSwitch("Disable video recording limit", new NativeTweak(NativeTweak.Key.REC_LIMIT));
        addSwitch("Disable 4K video recording limit (RX100M4 only)", new NativeTweak(NativeTweak.Key.REC_LIMIT_4K));
    }
}
