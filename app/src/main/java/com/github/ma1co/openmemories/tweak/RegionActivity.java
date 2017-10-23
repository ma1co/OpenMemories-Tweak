package com.github.ma1co.openmemories.tweak;

import android.os.Bundle;

public class RegionActivity extends ItemActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addSwitch("Unlock all languages", new NativeTweak(NativeTweak.Key.LANGUAGE));
        addSwitch("Enable PAL / NTSC selector & warning", new NativeTweak(NativeTweak.Key.PAL_NTSC_SELECTOR));
    }
}
