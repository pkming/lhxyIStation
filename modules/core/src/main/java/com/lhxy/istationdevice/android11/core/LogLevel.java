package com.lhxy.istationdevice.android11.core;

import android.util.Log;

public enum LogLevel {
    VERBOSE(Log.VERBOSE),
    DEBUG(Log.DEBUG),
    INFO(Log.INFO),
    WARN(Log.WARN),
    ERROR(Log.ERROR);

    private final int androidPriority;

    LogLevel(int androidPriority) {
        this.androidPriority = androidPriority;
    }

    public int getAndroidPriority() {
        return androidPriority;
    }
}

