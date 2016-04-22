package com.github.ma1co.openmemories.tweak;

public class Logger {
    private static final StringBuffer buffer = new StringBuffer();

    private static void log(String level, String tag, String msg) {
        buffer.append("[" + level + "][" + tag + "] " + msg + "\n");
    }

    public static String getLogs() {
        return buffer.toString();
    }

    public static void reset() {
        buffer.setLength(0);
    }

    public static void info(String tag, String msg) {
        log("INFO", tag, msg);
    }

    public static void error(String tag, String msg) {
        log("ERROR", tag, msg);
    }

    public static void error(String tag, String msg, Exception exp) {
        error(tag, (msg.isEmpty() ? "" : msg + ": ") + exp.getClass().getSimpleName() + " (" + exp.getMessage() + ")");
    }

    public static void error(String tag, Exception exp) {
        error(tag, "", exp);
    }
}
