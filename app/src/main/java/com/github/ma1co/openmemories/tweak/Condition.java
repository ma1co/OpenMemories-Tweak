package com.github.ma1co.openmemories.tweak;

import java.util.concurrent.TimeoutException;

public class Condition {
    public interface Runnable {
        boolean run();
    }

    public static void waitFor(Runnable runnable, long interval, long timeout) throws InterruptedException, TimeoutException {
        if (runnable.run())
            return;
        for (long t = 0; t < timeout; t += interval) {
            Thread.sleep(interval);
            if (runnable.run())
                return;
        }
        throw new TimeoutException("waitFor timed out");
    }
}
