package com.github.ma1co.openmemories.tweak;

import java.lang.reflect.Array;
import java.util.Arrays;

public abstract class BackupProperty<T> {
    public static class BackupException extends Exception {
        public BackupException(String message) {
            super(message);
        }
    }

    public static class BackupProtectionException extends BackupException {
        public BackupProtectionException() {
            super("Cannot change settings because protection is active. Please disable protection and try again.");
        }
    }

    public static class ByteArray extends BaseProperty<byte[]> {
        public ByteArray(int id, int size) {
            super(id, byte[].class, size);
        }

        @Override
        protected byte[] toBytes(byte[] value) {
            if (value.length != getSize())
                throw new IllegalArgumentException("Wrong array length");
            return value;
        }

        @Override
        protected byte[] fromBytes(byte[] value) {
            return value;
        }
    }

    public static class Byte extends BaseProperty<Integer> {
        public Byte(int id) {
            super(id, Integer.class, 1);
        }

        @Override
        protected byte[] toBytes(Integer value) {
            return new byte[] {(byte) (int) value};
        }

        @Override
        protected Integer fromBytes(byte[] value) {
            return (int) value[0];
        }
    }

    public static class Short extends BaseProperty<Integer> {
        public Short(int id) {
            super(id, Integer.class, 2);
        }

        @Override
        protected byte[] toBytes(Integer value) {
            return new byte[] {(byte) (int) value, (byte) (value >> 8)};
        }

        @Override
        protected Integer fromBytes(byte[] value) {
            return (value[0] & 0xff) | value[1] << 8;
        }
    }

    public static class CString extends BaseProperty<String> {
        public CString(int id, int size) {
            super(id, String.class, size);
        }

        @Override
        protected byte[] toBytes(String value) {
            byte[] bytes = value.getBytes();
            if (bytes.length > getSize())
                throw new IllegalArgumentException("String too long");
            return Arrays.copyOf(bytes, getSize());
        }

        @Override
        protected String fromBytes(byte[] value) {
            String str = new String(value);
            int length = str.indexOf('\u0000');
            if (length == -1)
                length = value.length;
            return str.substring(0, length);
        }
    }

    public static abstract class BaseProperty<T> extends BackupProperty<T> {
        private final int id;
        private final int size;

        public BaseProperty(int id, Class<T> type, int size) {
            super(type);
            this.id = id;
            this.size = size;
        }

        public int getId() {
            return id;
        }

        public int getSize() {
            return size;
        }

        @Override
        public boolean exists() {
            try {
                int size = Backup.getSize(id);
                if (size != this.size) {
                    Logger.error("BaseProperty.exists", String.format("%s has wrong size: %d instead of %d", this, size, this.size));
                    return false;
                }
                return true;
            } catch (NativeException e) {
                Logger.info("BaseProperty.exists", String.format("%s does not exist", this));
                return false;
            }
        }

        public boolean isReadOnly() throws BackupException {
            try {
                return Backup.isReadOnly(id);
            } catch (NativeException e) {
                Logger.error("BaseProperty.isReadOnly", String.format("error reading %s", this), e);
                throw new BackupException("Read failed");
            }
        }

        @Override
        public T getValue() throws BackupException {
            try {
                return fromBytes(Backup.getValue(id));
            } catch (NativeException e) {
                Logger.error("BaseProperty.getValue", String.format("error reading %s", this), e);
                throw new BackupException("Read failed");
            }
        }

        @Override
        public void setValue(T value) throws BackupException {
            try {
                Backup.setValue(id, toBytes(value));
            } catch (NativeException e) {
                if (isReadOnly()) {
                    Logger.info("BaseProperty.setValue", String.format("%s is protected", this));
                    throw new BackupProtectionException();
                } else {
                    Logger.error("BaseProperty.setValue", String.format("error writing %s", this), e);
                    throw new BackupException("Write failed");
                }
            }
        }

        protected abstract byte[] toBytes(T value);

        protected abstract T fromBytes(byte[] value);

        @Override
        public String toString() {
            return String.format("backup property 0x%x", id);
        }
    }

    public static class CompoundProperty<T> extends BackupProperty<T[]> {
        private final BaseProperty<T>[] properties;

        public CompoundProperty(Class<T[]> type, BaseProperty<T>[] properties) {
            super(type);
            this.properties = properties;
        }

        public BaseProperty<T>[] getProperties() {
            return properties;
        }

        @Override
        public boolean exists() {
            for (BaseProperty<T> property : properties) {
                if (!property.exists())
                    return false;
            }
            return true;
        }

        @Override
        public T[] getValue() throws BackupException {
            @SuppressWarnings("unchecked")
            T[] values = (T[]) Array.newInstance(getType().getComponentType(), properties.length);
            for (int i = 0; i < properties.length; i++)
                values[i] = properties[i].getValue();
            return values;
        }

        @Override
        public void setValue(T[] value) throws BackupException {
            if (value.length != properties.length)
                throw new IllegalArgumentException("Wrong array length");
            for (int i = 0; i < properties.length; i++)
                properties[i].setValue(value[i]);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("compound backup property [");
            boolean first = true;
            for (BaseProperty<T> property : properties) {
                if (!first)
                    sb.append(", ");
                sb.append(String.format("0x%x", property.getId()));
                first = false;
            }
            sb.append("]");
            return sb.toString();
        }
    }

    private final Class<T> type;

    public BackupProperty(Class<T> type) {
        this.type = type;
    }

    public Class<T> getType() {
        return type;
    }

    public abstract boolean exists();
    public abstract T getValue() throws BackupException;
    public abstract void setValue(T value) throws BackupException;

    public boolean valueEquals(T correctValue) throws BackupException {
        T value = getValue();
        return (type.isArray() && Arrays.equals((Object[]) correctValue, (Object[]) value)) || correctValue.equals(value);
    }
}
