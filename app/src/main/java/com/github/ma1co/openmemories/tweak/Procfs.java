package com.github.ma1co.openmemories.tweak;

import android.text.TextUtils;
import android.util.SparseArray;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Procfs {
    public static SparseArray<String> listProcesses() {
        SparseArray<String> processes = new SparseArray<>();

        for (File process : new File("/proc").listFiles()) {
            try {
                int pid = Integer.parseInt(process.getName());
                byte[] buf = new byte[128];
                int l = new FileInputStream(new File(process, "cmdline")).read(buf);
                if (l != -1)
                    processes.put(pid, new String(buf, 0, l));
            } catch (NumberFormatException | IOException e) {}
        }

        return processes;
    }

    public static int findProcess(String[] cmd) {
        String cmdline = TextUtils.join("\00", cmd) + "\00";
        SparseArray<String> processes = Procfs.listProcesses();
        for (int i = 0; i < processes.size(); i++) {
            if (cmdline.equals(processes.valueAt(i)))
                return processes.keyAt(i);
        }
        return -1;
    }
}
