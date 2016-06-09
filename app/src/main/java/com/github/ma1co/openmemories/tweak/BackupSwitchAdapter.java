package com.github.ma1co.openmemories.tweak;

public abstract class BackupSwitchAdapter<T> implements ItemActivity.SwitchItem.Adapter {
    public static class ConstantImpl<T> extends BackupSwitchAdapter<T> {
        private final T offValue;
        private final T onValue;

        public ConstantImpl(BackupProperty<T> property, T offValue, T onValue) {
            super(property, false);
            this.offValue = offValue;
            this.onValue = onValue;
        }

        @Override
        public T getOffValue() {
            return offValue;
        }

        @Override
        public T getOnValue() {
            return onValue;
        }
    }

    private final BackupProperty<T> property;
    private final boolean allowOtherValues;

    public BackupSwitchAdapter(BackupProperty<T> property, boolean allowOtherValues) {
        this.property = property;
        this.allowOtherValues = allowOtherValues;
    }

    public BackupProperty<T> getProperty() {
        return property;
    }

    public abstract T getOffValue() throws BackupProperty.BackupException;
    public abstract T getOnValue() throws BackupProperty.BackupException;

    @Override
    public boolean isAvailable() {
        if (!property.exists())
            return false;
        if (!allowOtherValues) {
            try {
                if (!property.valueEquals(getOffValue()) && !property.valueEquals(getOnValue())) {
                    Logger.error("BackupSwitchAdapter.isAvailable", String.format("%s has unknown value", property));
                    return false;
                }
            } catch (BackupProperty.BackupException e) {
                Logger.error("BackupSwitchAdapter.isAvailable", String.format("cannot compare value of %s", property), e);
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isEnabled() {
        try {
            return property.valueEquals(getOnValue());
        } catch (BackupProperty.BackupException e) {
            Logger.error("BackupSwitchAdapter.isEnabled", String.format("cannot compare value of %s", property), e);
            return false;
        }
    }

    @Override
    public void setEnabled(boolean enabled) throws BackupProperty.BackupException {
        property.setValue(enabled ? getOnValue() : getOffValue());
    }

    @Override
    public String getSummary() {
        return isEnabled() ? "Enabled" : "Disabled";
    }
}
