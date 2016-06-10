package com.github.ma1co.openmemories.tweak;

public class BackupInfoAdapter<T> implements ItemActivity.InfoItem.Adapter {
    private final BackupProperty<T> property;

    public BackupInfoAdapter(BackupProperty<T> property) {
        this.property = property;
    }

    public BackupProperty<T> getProperty() {
        return property;
    }

    @Override
    public boolean isAvailable() {
        return property.exists();
    }

    @Override
    public String getValue() {
        try {
            return property.getValue().toString();
        } catch (BackupProperty.BackupException e) {
            return "";
        }
    }
}
