package android.webkit;

import android.content.Context;

/* JADX INFO: loaded from: classes.dex */
public abstract class WebSettings {
    public static final int LOAD_CACHE_ELSE_NETWORK = 1;
    public static final int LOAD_CACHE_ONLY = 3;
    public static final int LOAD_DEFAULT = -1;

    @Deprecated
    public static final int LOAD_NORMAL = 0;
    public static final int LOAD_NO_CACHE = 2;

    public enum LayoutAlgorithm {
        NORMAL,
        SINGLE_COLUMN,
        NARROW_COLUMNS,
        TEXT_AUTOSIZING
    }

    public enum PluginState {
        ON,
        ON_DEMAND,
        OFF
    }

    public enum RenderPriority {
        NORMAL,
        HIGH,
        LOW
    }

    public abstract boolean getAllowFileAccessFromFileURLs();

    public abstract boolean getAllowUniversalAccessFromFileURLs();

    public abstract void setAllowFileAccessFromFileURLs(boolean z);

    public abstract void setAllowUniversalAccessFromFileURLs(boolean z);

    public enum TextSize {
        SMALLEST(50),
        SMALLER(75),
        NORMAL(100),
        LARGER(150),
        LARGEST(200);

        int value;

        TextSize(int i) {
            this.value = i;
        }
    }

    public enum ZoomDensity {
        FAR(150),
        MEDIUM(100),
        CLOSE(75);

        int value;

        ZoomDensity(int i) {
            this.value = i;
        }

        public int getValue() {
            return this.value;
        }
    }

    protected WebSettings() {
    }

    @Deprecated
    public void setNavDump(boolean z) {
        throw new MustOverrideException();
    }

    @Deprecated
    public boolean getNavDump() {
        throw new MustOverrideException();
    }

    public void setSupportZoom(boolean z) {
        throw new MustOverrideException();
    }

    public boolean supportZoom() {
        throw new MustOverrideException();
    }

    public void setMediaPlaybackRequiresUserGesture(boolean z) {
        throw new MustOverrideException();
    }

    public boolean getMediaPlaybackRequiresUserGesture() {
        throw new MustOverrideException();
    }

    public void setBuiltInZoomControls(boolean z) {
        throw new MustOverrideException();
    }

    public boolean getBuiltInZoomControls() {
        throw new MustOverrideException();
    }

    public void setDisplayZoomControls(boolean z) {
        throw new MustOverrideException();
    }

    public boolean getDisplayZoomControls() {
        throw new MustOverrideException();
    }

    public void setAllowFileAccess(boolean z) {
        throw new MustOverrideException();
    }

    public boolean getAllowFileAccess() {
        throw new MustOverrideException();
    }

    public void setAllowContentAccess(boolean z) {
        throw new MustOverrideException();
    }

    public boolean getAllowContentAccess() {
        throw new MustOverrideException();
    }

    public void setLoadWithOverviewMode(boolean z) {
        throw new MustOverrideException();
    }

    public boolean getLoadWithOverviewMode() {
        throw new MustOverrideException();
    }

    @Deprecated
    public void setEnableSmoothTransition(boolean z) {
        throw new MustOverrideException();
    }

    @Deprecated
    public boolean enableSmoothTransition() {
        throw new MustOverrideException();
    }

    @Deprecated
    public void setUseWebViewBackgroundForOverscrollBackground(boolean z) {
        throw new MustOverrideException();
    }

    @Deprecated
    public boolean getUseWebViewBackgroundForOverscrollBackground() {
        throw new MustOverrideException();
    }

    public void setSaveFormData(boolean z) {
        throw new MustOverrideException();
    }

    public boolean getSaveFormData() {
        throw new MustOverrideException();
    }

    @Deprecated
    public void setSavePassword(boolean z) {
        throw new MustOverrideException();
    }

    @Deprecated
    public boolean getSavePassword() {
        throw new MustOverrideException();
    }

    public synchronized void setTextZoom(int i) {
        throw new MustOverrideException();
    }

    public synchronized int getTextZoom() {
        throw new MustOverrideException();
    }

    public synchronized void setTextSize(TextSize textSize) {
        setTextZoom(textSize.value);
    }

    public synchronized TextSize getTextSize() {
        TextSize textSize = null;
        int i = Integer.MAX_VALUE;
        int textZoom = getTextZoom();
        for (TextSize textSize2 : TextSize.values()) {
            int iAbs = Math.abs(textZoom - textSize2.value);
            if (iAbs == 0) {
                return textSize2;
            }
            if (iAbs < i) {
                textSize = textSize2;
                i = iAbs;
            }
        }
        if (textSize == null) {
            textSize = TextSize.NORMAL;
        }
        return textSize;
    }

    @Deprecated
    public void setDefaultZoom(ZoomDensity zoomDensity) {
        throw new MustOverrideException();
    }

    public ZoomDensity getDefaultZoom() {
        throw new MustOverrideException();
    }

    @Deprecated
    public void setLightTouchEnabled(boolean z) {
        throw new MustOverrideException();
    }

    @Deprecated
    public boolean getLightTouchEnabled() {
        throw new MustOverrideException();
    }

    @Deprecated
    public synchronized void setUseDoubleTree(boolean z) {
    }

    @Deprecated
    public synchronized boolean getUseDoubleTree() {
        return false;
    }

    @Deprecated
    public synchronized void setUserAgent(int i) {
        throw new MustOverrideException();
    }

    @Deprecated
    public synchronized int getUserAgent() {
        throw new MustOverrideException();
    }

    public synchronized void setUseWideViewPort(boolean z) {
        throw new MustOverrideException();
    }

    public synchronized boolean getUseWideViewPort() {
        throw new MustOverrideException();
    }

    public synchronized void setSupportMultipleWindows(boolean z) {
        throw new MustOverrideException();
    }

    public synchronized boolean supportMultipleWindows() {
        throw new MustOverrideException();
    }

    public synchronized void setLayoutAlgorithm(LayoutAlgorithm layoutAlgorithm) {
        throw new MustOverrideException();
    }

    public synchronized LayoutAlgorithm getLayoutAlgorithm() {
        throw new MustOverrideException();
    }

    public synchronized void setStandardFontFamily(String str) {
        throw new MustOverrideException();
    }

    public synchronized String getStandardFontFamily() {
        throw new MustOverrideException();
    }

    public synchronized void setFixedFontFamily(String str) {
        throw new MustOverrideException();
    }

    public synchronized String getFixedFontFamily() {
        throw new MustOverrideException();
    }

    public synchronized void setSansSerifFontFamily(String str) {
        throw new MustOverrideException();
    }

    public synchronized String getSansSerifFontFamily() {
        throw new MustOverrideException();
    }

    public synchronized void setSerifFontFamily(String str) {
        throw new MustOverrideException();
    }

    public synchronized String getSerifFontFamily() {
        throw new MustOverrideException();
    }

    public synchronized void setCursiveFontFamily(String str) {
        throw new MustOverrideException();
    }

    public synchronized String getCursiveFontFamily() {
        throw new MustOverrideException();
    }

    public synchronized void setFantasyFontFamily(String str) {
        throw new MustOverrideException();
    }

    public synchronized String getFantasyFontFamily() {
        throw new MustOverrideException();
    }

    public synchronized void setMinimumFontSize(int i) {
        throw new MustOverrideException();
    }

    public synchronized int getMinimumFontSize() {
        throw new MustOverrideException();
    }

    public synchronized void setMinimumLogicalFontSize(int i) {
        throw new MustOverrideException();
    }

    public synchronized int getMinimumLogicalFontSize() {
        throw new MustOverrideException();
    }

    public synchronized void setDefaultFontSize(int i) {
        throw new MustOverrideException();
    }

    public synchronized int getDefaultFontSize() {
        throw new MustOverrideException();
    }

    public synchronized void setDefaultFixedFontSize(int i) {
        throw new MustOverrideException();
    }

    public synchronized int getDefaultFixedFontSize() {
        throw new MustOverrideException();
    }

    public synchronized void setLoadsImagesAutomatically(boolean z) {
        throw new MustOverrideException();
    }

    public synchronized boolean getLoadsImagesAutomatically() {
        throw new MustOverrideException();
    }

    public synchronized void setBlockNetworkImage(boolean z) {
        throw new MustOverrideException();
    }

    public synchronized boolean getBlockNetworkImage() {
        throw new MustOverrideException();
    }

    public synchronized void setBlockNetworkLoads(boolean z) {
        throw new MustOverrideException();
    }

    public synchronized boolean getBlockNetworkLoads() {
        throw new MustOverrideException();
    }

    public synchronized void setJavaScriptEnabled(boolean z) {
        throw new MustOverrideException();
    }

    @Deprecated
    public synchronized void setPluginsEnabled(boolean z) {
        throw new MustOverrideException();
    }

    @Deprecated
    public synchronized void setPluginState(PluginState pluginState) {
        throw new MustOverrideException();
    }

    @Deprecated
    public synchronized void setPluginsPath(String str) {
    }

    @Deprecated
    public synchronized void setDatabasePath(String str) {
        throw new MustOverrideException();
    }

    public synchronized void setGeolocationDatabasePath(String str) {
        throw new MustOverrideException();
    }

    public synchronized void setAppCacheEnabled(boolean z) {
        throw new MustOverrideException();
    }

    public synchronized void setAppCachePath(String str) {
        throw new MustOverrideException();
    }

    @Deprecated
    public synchronized void setAppCacheMaxSize(long j) {
        throw new MustOverrideException();
    }

    public synchronized void setDatabaseEnabled(boolean z) {
        throw new MustOverrideException();
    }

    public synchronized void setDomStorageEnabled(boolean z) {
        throw new MustOverrideException();
    }

    public synchronized boolean getDomStorageEnabled() {
        throw new MustOverrideException();
    }

    @Deprecated
    public synchronized String getDatabasePath() {
        throw new MustOverrideException();
    }

    public synchronized boolean getDatabaseEnabled() {
        throw new MustOverrideException();
    }

    public synchronized void setGeolocationEnabled(boolean z) {
        throw new MustOverrideException();
    }

    public synchronized boolean getJavaScriptEnabled() {
        throw new MustOverrideException();
    }

    @Deprecated
    public synchronized boolean getPluginsEnabled() {
        throw new MustOverrideException();
    }

    @Deprecated
    public synchronized PluginState getPluginState() {
        throw new MustOverrideException();
    }

    @Deprecated
    public synchronized String getPluginsPath() {
        return "";
    }

    public synchronized void setJavaScriptCanOpenWindowsAutomatically(boolean z) {
        throw new MustOverrideException();
    }

    public synchronized boolean getJavaScriptCanOpenWindowsAutomatically() {
        throw new MustOverrideException();
    }

    public synchronized void setDefaultTextEncodingName(String str) {
        throw new MustOverrideException();
    }

    public synchronized String getDefaultTextEncodingName() {
        throw new MustOverrideException();
    }

    public synchronized void setUserAgentString(String str) {
        throw new MustOverrideException();
    }

    public synchronized String getUserAgentString() {
        throw new MustOverrideException();
    }

    public static String getDefaultUserAgent(Context context) {
        return WebViewFactory.getProvider().getStatics().getDefaultUserAgent(context);
    }

    public void setNeedInitialFocus(boolean z) {
        throw new MustOverrideException();
    }

    @Deprecated
    public synchronized void setRenderPriority(RenderPriority renderPriority) {
        throw new MustOverrideException();
    }

    public void setCacheMode(int i) {
        throw new MustOverrideException();
    }

    public int getCacheMode() {
        throw new MustOverrideException();
    }
}
