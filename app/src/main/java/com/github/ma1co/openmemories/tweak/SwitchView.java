package com.github.ma1co.openmemories.tweak;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SwitchView extends RelativeLayout implements View.OnClickListener {
    public interface CheckedListener {
        void onCheckedChanged(SwitchView view, boolean checked);
    }

    private CheckedListener listener;
    private TextView titleView;
    private TextView summaryView;
    private CheckBox checkBox;

    public SwitchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPadding(20, 10, 20, 10);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_switch, this);
        titleView = (TextView) findViewById(R.id.title);
        summaryView = (TextView) findViewById(R.id.summary);
        checkBox = (CheckBox) findViewById(R.id.checkbox);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SwitchView, 0, 0);
        setTitle(ta.getString(R.styleable.SwitchView_title));
        setSummary(ta.getString(R.styleable.SwitchView_summary));
        ta.recycle();

        checkBox.setOnClickListener(this);
    }

    public void setTitle(String title) {
        titleView.setText(title);
    }

    public void setSummary(String summary) {
        summaryView.setText(summary);
    }

    public boolean isChecked() {
        return checkBox.isChecked();
    }

    public void setChecked(boolean checked) {
        checkBox.setChecked(checked);
    }

    @Override
    public boolean isEnabled() {
        return checkBox.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        checkBox.setEnabled(enabled);
    }

    public void setListener(CheckedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if (listener != null)
            listener.onCheckedChanged(this, checkBox.isChecked());
    }
}
