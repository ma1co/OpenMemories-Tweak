package com.github.ma1co.openmemories.tweak;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ItemActivity extends BaseActivity {
    public static abstract class BaseItem extends RelativeLayout {
        public BaseItem(Context context) {
            super(context);
            setPadding(20, 10, 20, 10);
        }

        public void update() {}
    }

    public static class SwitchItem extends BaseItem {
        public interface Adapter {
            boolean isAvailable();
            boolean isEnabled();
            void setEnabled(boolean enabled) throws Exception;
            String getSummary();
        }

        private final Adapter adapter;
        private final TextView titleView;
        private final TextView summaryView;
        private final CheckBox checkBox;

        public SwitchItem(Context context, String title, Adapter adapter) {
            super(context);
            inflate(context, R.layout.view_switch, this);
            titleView = (TextView) findViewById(R.id.title);
            summaryView = (TextView) findViewById(R.id.summary);
            checkBox = (CheckBox) findViewById(R.id.checkbox);

            this.adapter = adapter;
            titleView.setText(title);

            checkBox.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        SwitchItem.this.adapter.setEnabled(checkBox.isChecked());
                    } catch (Exception e) {
                        showError(e);
                    }
                    update();
                }
            });
        }

        public Adapter getAdapter() {
            return adapter;
        }

        @Override
        public void update() {
            boolean available = adapter.isAvailable();
            checkBox.setEnabled(available);
            checkBox.setChecked(available && adapter.isEnabled());
            summaryView.setText(available ? adapter.getSummary() : "(not available)");
        }

        private void showError(Exception e) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
            alert.setTitle("Error");
            alert.setMessage(e.getMessage());
            alert.setPositiveButton("Ok", null);
            alert.show();
        }
    }

    public static class ButtonItem extends BaseItem {
        public interface Adapter {
            void click();
        }

        private final Adapter adapter;
        private final Button buttonView;

        public ButtonItem(Context context, String text, Adapter adapter) {
            super(context);
            inflate(context, R.layout.view_button, this);
            buttonView = (Button) findViewById(R.id.button);

            this.adapter = adapter;
            buttonView.setText(text);

            buttonView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    ButtonItem.this.adapter.click();
                }
            });
        }

        public Adapter getAdapter() {
            return adapter;
        }
    }

    public static class InfoItem extends BaseItem {
        public interface Adapter {
            boolean isAvailable();
            String getValue();
        }

        private final Adapter adapter;
        private final TextView titleView;
        private final TextView valueView;

        public InfoItem(Context context, String title, Adapter adapter) {
            super(context);
            inflate(context, R.layout.view_info, this);
            titleView = (TextView) findViewById(R.id.title);
            valueView = (TextView) findViewById(R.id.value);

            this.adapter = adapter;
            titleView.setText(title);
        }

        public Adapter getAdapter() {
            return adapter;
        }

        @Override
        public void update() {
            valueView.setText(adapter.isAvailable() ? adapter.getValue() : "(not available)");
        }
    }

    public static class LabelItem extends BaseItem {
        private TextView textView;

        public LabelItem(Context context, String text) {
            super(context);
            inflate(context, R.layout.view_label, this);
            textView = (TextView) findViewById(R.id.text);

            textView.setText(text);
        }
    }

    private ViewGroup containerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        containerView = (ViewGroup) findViewById(R.id.container);
    }

    @Override
    protected void onResume() {
        super.onResume();
        update();
    }

    public void update() {
        for (int i = 0; i < containerView.getChildCount(); i++)
            ((BaseItem) containerView.getChildAt(i)).update();
    }

    protected BaseItem addItem(BaseItem item) {
        containerView.addView(item);
        return item;
    }

    protected BaseItem removeItem(BaseItem item) {
        containerView.removeView(item);
        return item;
    }

    protected BaseItem addSwitch(String title, SwitchItem.Adapter adapter) {
        return addItem(new SwitchItem(this, title, adapter));
    }

    protected BaseItem addButton(String text, ButtonItem.Adapter adapter) {
        return addItem(new ButtonItem(this, text, adapter));
    }

    protected BaseItem addInfo(String title, InfoItem.Adapter adapter) {
        return addItem(new InfoItem(this, title, adapter));
    }

    protected BaseItem addLabel(String text) {
        return addItem(new LabelItem(this, text));
    }
}
