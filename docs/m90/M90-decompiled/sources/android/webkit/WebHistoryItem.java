package android.webkit;

import android.graphics.Bitmap;

/* JADX INFO: loaded from: classes.dex */
public class WebHistoryItem implements Cloneable {
    @Deprecated
    public int getId() {
        throw new MustOverrideException();
    }

    public String getUrl() {
        throw new MustOverrideException();
    }

    public String getOriginalUrl() {
        throw new MustOverrideException();
    }

    public String getTitle() {
        throw new MustOverrideException();
    }

    public Bitmap getFavicon() {
        throw new MustOverrideException();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
    public synchronized WebHistoryItem m21clone() {
        throw new MustOverrideException();
    }
}
