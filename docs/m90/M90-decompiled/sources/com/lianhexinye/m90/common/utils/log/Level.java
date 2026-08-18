package com.lianhexinye.m90.common.utils.log;

/* JADX INFO: loaded from: classes2.dex */
public enum Level {
    VERBOSE(2),
    DEBUG(3),
    INFO(4),
    WARN(5),
    ERROR(6),
    ASSERT(7),
    CLOSE(8);

    int value;

    Level(int i) {
        this.value = i;
    }
}
