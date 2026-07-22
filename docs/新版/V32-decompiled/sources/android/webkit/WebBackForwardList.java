package android.webkit;

import java.io.Serializable;

/* JADX INFO: loaded from: classes.dex */
public class WebBackForwardList implements Cloneable, Serializable {
    public synchronized WebHistoryItem getCurrentItem() {
        throw new MustOverrideException();
    }

    public synchronized int getCurrentIndex() {
        throw new MustOverrideException();
    }

    public synchronized WebHistoryItem getItemAtIndex(int i) {
        throw new MustOverrideException();
    }

    public synchronized int getSize() {
        throw new MustOverrideException();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
    public synchronized WebBackForwardList m20clone() {
        throw new MustOverrideException();
    }
}
