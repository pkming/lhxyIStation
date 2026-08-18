package org.apache.tools.ant.util;

import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes3.dex */
public class WeakishReference {
    private WeakReference weakref;

    WeakishReference(Object obj) {
        this.weakref = new WeakReference(obj);
    }

    public Object get() {
        return this.weakref.get();
    }

    public static WeakishReference createReference(Object obj) {
        return new WeakishReference(obj);
    }

    public static class HardReference extends WeakishReference {
        public HardReference(Object obj) {
            super(obj);
        }
    }
}
