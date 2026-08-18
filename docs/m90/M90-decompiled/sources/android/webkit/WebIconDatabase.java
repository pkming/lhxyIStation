package android.webkit;

import android.content.ContentResolver;
import android.graphics.Bitmap;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public class WebIconDatabase {

    @Deprecated
    public interface IconListener {
        void onReceivedIcon(String str, Bitmap bitmap);
    }

    public void open(String str) {
        throw new MustOverrideException();
    }

    public void close() {
        throw new MustOverrideException();
    }

    public void removeAllIcons() {
        throw new MustOverrideException();
    }

    public void requestIconForPageUrl(String str, IconListener iconListener) {
        throw new MustOverrideException();
    }

    public void bulkRequestIconForPageUrl(ContentResolver contentResolver, String str, IconListener iconListener) {
        throw new MustOverrideException();
    }

    public void retainIconForPageUrl(String str) {
        throw new MustOverrideException();
    }

    public void releaseIconForPageUrl(String str) {
        throw new MustOverrideException();
    }

    public static WebIconDatabase getInstance() {
        return WebViewFactory.getProvider().getWebIconDatabase();
    }

    protected WebIconDatabase() {
    }
}
