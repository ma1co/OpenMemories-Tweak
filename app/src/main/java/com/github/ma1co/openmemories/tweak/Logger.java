package com.github.ma1co.openmemories.tweak;

import java.io.PrintWriter;
import java.io.StringWriter;

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

    public static void error(String tag, String msg, Throwable exp) {
        StringWriter sw = new StringWriter();
        if (!msg.isEmpty()) {
            sw.append(msg);
            sw.append(": ");
        }
        exp.printStackTrace(new PrintWriter(sw));
        error(tag, sw.toString().trim());
    }

    public static void error(String tag, Throwable exp) {
        error(tag, "", exp);
    }
}
