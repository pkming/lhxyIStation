package android.content.pm;

import android.os.Binder;

/* JADX INFO: loaded from: classes.dex */
public class KeySet {
    private Binder token;

    public KeySet(Binder binder) {
        this.token = binder;
    }

    Binder getToken() {
        return this.token;
    }
}
