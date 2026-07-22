package org.apache.commons.csv;

/* JADX INFO: loaded from: classes3.dex */
final class Assertions {
    private Assertions() {
    }

    public static void notNull(Object obj, String str) {
        if (obj == null) {
            throw new IllegalArgumentException("Parameter '" + str + "' must not be null!");
        }
    }
}
