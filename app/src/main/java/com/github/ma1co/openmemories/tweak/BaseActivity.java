package com.github.ma1co.openmemories.tweak;

import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;

public class BaseActivity extends Activity {
    @Override
    protected void onResume() {
        super.onResume();
        Logger.info("onResume", getComponentName().getShortClassName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logger.info("onPause", getComponentName().getShortClassName());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            finish();
            startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
