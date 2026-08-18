package android.view.accessibility;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class CaptioningManager {
    private static final int DEFAULT_ENABLED = 0;
    private static final float DEFAULT_FONT_SCALE = 1.0f;
    private static final int DEFAULT_PRESET = 0;
    private final ContentObserver mContentObserver;
    private final ContentResolver mContentResolver;
    private final Handler mHandler;
    private final ArrayList<CaptioningChangeListener> mListeners = new ArrayList<>();
    private final Runnable mStyleChangedRunnable;

    public static abstract class CaptioningChangeListener {
        public void onEnabledChanged(boolean z) {
        }

        public void onFontScaleChanged(float f) {
        }

        public void onLocaleChanged(Locale locale) {
        }

        public void onUserStyleChanged(CaptionStyle captionStyle) {
        }
    }

    public CaptioningManager(Context context) {
        Handler handler = new Handler();
        this.mHandler = handler;
        this.mContentObserver = new ContentObserver(handler) { // from class: android.view.accessibility.CaptioningManager.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z, Uri uri) {
                String path = uri.getPath();
                String strSubstring = path.substring(path.lastIndexOf(47) + 1);
                if (Settings.Secure.ACCESSIBILITY_CAPTIONING_ENABLED.equals(strSubstring)) {
                    CaptioningManager.this.notifyEnabledChanged();
                    return;
                }
                if (Settings.Secure.ACCESSIBILITY_CAPTIONING_LOCALE.equals(strSubstring)) {
                    CaptioningManager.this.notifyLocaleChanged();
                } else if (Settings.Secure.ACCESSIBILITY_CAPTIONING_FONT_SCALE.equals(strSubstring)) {
                    CaptioningManager.this.notifyFontScaleChanged();
                } else {
                    CaptioningManager.this.mHandler.removeCallbacks(CaptioningManager.this.mStyleChangedRunnable);
                    CaptioningManager.this.mHandler.post(CaptioningManager.this.mStyleChangedRunnable);
                }
            }
        };
        this.mStyleChangedRunnable = new Runnable() { // from class: android.view.accessibility.CaptioningManager.2
            @Override // java.lang.Runnable
            public void run() {
                CaptioningManager.this.notifyUserStyleChanged();
            }
        };
        this.mContentResolver = context.getContentResolver();
    }

    public final boolean isEnabled() {
        return Settings.Secure.getInt(this.mContentResolver, Settings.Secure.ACCESSIBILITY_CAPTIONING_ENABLED, 0) == 1;
    }

    public final String getRawLocale() {
        return Settings.Secure.getString(this.mContentResolver, Settings.Secure.ACCESSIBILITY_CAPTIONING_LOCALE);
    }

    public final Locale getLocale() {
        String rawLocale = getRawLocale();
        if (TextUtils.isEmpty(rawLocale)) {
            return null;
        }
        String[] strArrSplit = rawLocale.split("_");
        int length = strArrSplit.length;
        if (length == 1) {
            return new Locale(strArrSplit[0]);
        }
        if (length == 2) {
            return new Locale(strArrSplit[0], strArrSplit[1]);
        }
        if (length != 3) {
            return null;
        }
        return new Locale(strArrSplit[0], strArrSplit[1], strArrSplit[2]);
    }

    public final float getFontScale() {
        return Settings.Secure.getFloat(this.mContentResolver, Settings.Secure.ACCESSIBILITY_CAPTIONING_FONT_SCALE, 1.0f);
    }

    public int getRawUserStyle() {
        return Settings.Secure.getInt(this.mContentResolver, Settings.Secure.ACCESSIBILITY_CAPTIONING_PRESET, 0);
    }

    public CaptionStyle getUserStyle() {
        int rawUserStyle = getRawUserStyle();
        if (rawUserStyle == -1) {
            return CaptionStyle.getCustomStyle(this.mContentResolver);
        }
        return CaptionStyle.PRESETS[rawUserStyle];
    }

    public void addCaptioningChangeListener(CaptioningChangeListener captioningChangeListener) {
        synchronized (this.mListeners) {
            if (this.mListeners.isEmpty()) {
                registerObserver(Settings.Secure.ACCESSIBILITY_CAPTIONING_ENABLED);
                registerObserver(Settings.Secure.ACCESSIBILITY_CAPTIONING_FOREGROUND_COLOR);
                registerObserver(Settings.Secure.ACCESSIBILITY_CAPTIONING_BACKGROUND_COLOR);
                registerObserver(Settings.Secure.ACCESSIBILITY_CAPTIONING_EDGE_TYPE);
                registerObserver(Settings.Secure.ACCESSIBILITY_CAPTIONING_EDGE_COLOR);
                registerObserver(Settings.Secure.ACCESSIBILITY_CAPTIONING_TYPEFACE);
                registerObserver(Settings.Secure.ACCESSIBILITY_CAPTIONING_FONT_SCALE);
                registerObserver(Settings.Secure.ACCESSIBILITY_CAPTIONING_LOCALE);
            }
            this.mListeners.add(captioningChangeListener);
        }
    }

    private void registerObserver(String str) {
        this.mContentResolver.registerContentObserver(Settings.Secure.getUriFor(str), false, this.mContentObserver);
    }

    public void removeCaptioningChangeListener(CaptioningChangeListener captioningChangeListener) {
        synchronized (this.mListeners) {
            this.mListeners.remove(captioningChangeListener);
            if (this.mListeners.isEmpty()) {
                this.mContentResolver.unregisterContentObserver(this.mContentObserver);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyEnabledChanged() {
        boolean zIsEnabled = isEnabled();
        synchronized (this.mListeners) {
            Iterator<CaptioningChangeListener> it = this.mListeners.iterator();
            while (it.hasNext()) {
                it.next().onEnabledChanged(zIsEnabled);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyUserStyleChanged() {
        CaptionStyle userStyle = getUserStyle();
        synchronized (this.mListeners) {
            Iterator<CaptioningChangeListener> it = this.mListeners.iterator();
            while (it.hasNext()) {
                it.next().onUserStyleChanged(userStyle);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyLocaleChanged() {
        Locale locale = getLocale();
        synchronized (this.mListeners) {
            Iterator<CaptioningChangeListener> it = this.mListeners.iterator();
            while (it.hasNext()) {
                it.next().onLocaleChanged(locale);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyFontScaleChanged() {
        float fontScale = getFontScale();
        synchronized (this.mListeners) {
            Iterator<CaptioningChangeListener> it = this.mListeners.iterator();
            while (it.hasNext()) {
                it.next().onFontScaleChanged(fontScale);
            }
        }
    }

    public static final class CaptionStyle {
        private static final CaptionStyle BLACK_ON_WHITE;
        private static final CaptionStyle DEFAULT_CUSTOM;
        public static final int EDGE_TYPE_DROP_SHADOW = 2;
        public static final int EDGE_TYPE_NONE = 0;
        public static final int EDGE_TYPE_OUTLINE = 1;
        public static final CaptionStyle[] PRESETS;
        public static final int PRESET_CUSTOM = -1;
        private static final CaptionStyle WHITE_ON_BLACK;
        private static final CaptionStyle YELLOW_ON_BLACK;
        private static final CaptionStyle YELLOW_ON_BLUE;
        public final int backgroundColor;
        public final int edgeColor;
        public final int edgeType;
        public final int foregroundColor;
        private Typeface mParsedTypeface;
        public final String mRawTypeface;

        private CaptionStyle(int i, int i2, int i3, int i4, String str) {
            this.foregroundColor = i;
            this.backgroundColor = i2;
            this.edgeType = i3;
            this.edgeColor = i4;
            this.mRawTypeface = str;
        }

        public Typeface getTypeface() {
            if (this.mParsedTypeface == null && !TextUtils.isEmpty(this.mRawTypeface)) {
                this.mParsedTypeface = Typeface.create(this.mRawTypeface, 0);
            }
            return this.mParsedTypeface;
        }

        public static CaptionStyle getCustomStyle(ContentResolver contentResolver) {
            CaptionStyle captionStyle = DEFAULT_CUSTOM;
            int i = Settings.Secure.getInt(contentResolver, Settings.Secure.ACCESSIBILITY_CAPTIONING_FOREGROUND_COLOR, captionStyle.foregroundColor);
            int i2 = Settings.Secure.getInt(contentResolver, Settings.Secure.ACCESSIBILITY_CAPTIONING_BACKGROUND_COLOR, captionStyle.backgroundColor);
            int i3 = Settings.Secure.getInt(contentResolver, Settings.Secure.ACCESSIBILITY_CAPTIONING_EDGE_TYPE, captionStyle.edgeType);
            int i4 = Settings.Secure.getInt(contentResolver, Settings.Secure.ACCESSIBILITY_CAPTIONING_EDGE_COLOR, captionStyle.edgeColor);
            String string = Settings.Secure.getString(contentResolver, Settings.Secure.ACCESSIBILITY_CAPTIONING_TYPEFACE);
            if (string == null) {
                string = captionStyle.mRawTypeface;
            }
            return new CaptionStyle(i, i2, i3, i4, string);
        }

        static {
            CaptionStyle captionStyle = new CaptionStyle(-1, -16777216, 0, -16777216, null);
            WHITE_ON_BLACK = captionStyle;
            CaptionStyle captionStyle2 = new CaptionStyle(-16777216, -1, 0, -16777216, null);
            BLACK_ON_WHITE = captionStyle2;
            CaptionStyle captionStyle3 = new CaptionStyle(-256, -16777216, 0, -16777216, null);
            YELLOW_ON_BLACK = captionStyle3;
            CaptionStyle captionStyle4 = new CaptionStyle(-256, Color.BLUE, 0, -16777216, null);
            YELLOW_ON_BLUE = captionStyle4;
            PRESETS = new CaptionStyle[]{captionStyle, captionStyle2, captionStyle3, captionStyle4};
            DEFAULT_CUSTOM = captionStyle;
        }
    }
}
