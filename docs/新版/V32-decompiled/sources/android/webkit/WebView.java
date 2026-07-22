package android.webkit;

import android.R;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.http.SslCertificate;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.print.PrintDocumentAdapter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.AbsoluteLayout;
import dalvik.system.BlockGuard;
import java.io.BufferedWriter;
import java.io.File;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class WebView extends AbsoluteLayout implements ViewTreeObserver.OnGlobalFocusChangeListener, ViewGroup.OnHierarchyChangeListener, ViewDebug.HierarchyHandler {
    private static final String LOGTAG = "WebView";
    public static final String SCHEME_GEO = "geo:0,0?q=";
    public static final String SCHEME_MAILTO = "mailto:";
    public static final String SCHEME_TEL = "tel:";
    private static Boolean sEnforceThreadChecking = false;
    private FindListenerDistributor mFindListener;
    private WebViewProvider mProvider;
    private final Looper mWebViewThread;

    public interface FindListener {
        void onFindResultReceived(int i, int i2, boolean z);
    }

    @Deprecated
    public interface PictureListener {
        @Deprecated
        void onNewPicture(WebView webView, Picture picture);
    }

    @Override // android.view.ViewGroup.OnHierarchyChangeListener
    @Deprecated
    public void onChildViewAdded(View view, View view2) {
    }

    @Override // android.view.ViewGroup.OnHierarchyChangeListener
    @Deprecated
    public void onChildViewRemoved(View view, View view2) {
    }

    @Override // android.view.ViewTreeObserver.OnGlobalFocusChangeListener
    @Deprecated
    public void onGlobalFocusChanged(View view, View view2) {
    }

    public class WebViewTransport {
        private WebView mWebview;

        public WebViewTransport() {
        }

        public synchronized void setWebView(WebView webView) {
            this.mWebview = webView;
        }

        public synchronized WebView getWebView() {
            return this.mWebview;
        }
    }

    public static class HitTestResult {

        @Deprecated
        public static final int ANCHOR_TYPE = 1;
        public static final int EDIT_TEXT_TYPE = 9;
        public static final int EMAIL_TYPE = 4;
        public static final int GEO_TYPE = 3;

        @Deprecated
        public static final int IMAGE_ANCHOR_TYPE = 6;
        public static final int IMAGE_TYPE = 5;
        public static final int PHONE_TYPE = 2;
        public static final int SRC_ANCHOR_TYPE = 7;
        public static final int SRC_IMAGE_ANCHOR_TYPE = 8;
        public static final int UNKNOWN_TYPE = 0;
        private String mExtra;
        private int mType = 0;

        public void setType(int i) {
            this.mType = i;
        }

        public void setExtra(String str) {
            this.mExtra = str;
        }

        public int getType() {
            return this.mType;
        }

        public String getExtra() {
            return this.mExtra;
        }
    }

    public WebView(Context context) {
        this(context, null);
    }

    public WebView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.webViewStyle);
    }

    public WebView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, false);
    }

    @Deprecated
    public WebView(Context context, AttributeSet attributeSet, int i, boolean z) {
        this(context, attributeSet, i, null, z);
    }

    protected WebView(Context context, AttributeSet attributeSet, int i, Map<String, Object> map, boolean z) {
        super(context, attributeSet, i);
        this.mWebViewThread = Looper.myLooper();
        if (context == null) {
            throw new IllegalArgumentException("Invalid context argument");
        }
        sEnforceThreadChecking = Boolean.valueOf(context.getApplicationInfo().targetSdkVersion >= 18);
        checkThread();
        ensureProviderCreated();
        this.mProvider.init(map, z);
        CookieSyncManager.setGetInstanceIsAllowed();
    }

    public void setHorizontalScrollbarOverlay(boolean z) {
        checkThread();
        this.mProvider.setHorizontalScrollbarOverlay(z);
    }

    public void setVerticalScrollbarOverlay(boolean z) {
        checkThread();
        this.mProvider.setVerticalScrollbarOverlay(z);
    }

    public boolean overlayHorizontalScrollbar() {
        checkThread();
        return this.mProvider.overlayHorizontalScrollbar();
    }

    public boolean overlayVerticalScrollbar() {
        checkThread();
        return this.mProvider.overlayVerticalScrollbar();
    }

    public int getVisibleTitleHeight() {
        checkThread();
        return this.mProvider.getVisibleTitleHeight();
    }

    public SslCertificate getCertificate() {
        checkThread();
        return this.mProvider.getCertificate();
    }

    @Deprecated
    public void setCertificate(SslCertificate sslCertificate) {
        checkThread();
        this.mProvider.setCertificate(sslCertificate);
    }

    @Deprecated
    public void savePassword(String str, String str2, String str3) {
        checkThread();
        this.mProvider.savePassword(str, str2, str3);
    }

    public void setHttpAuthUsernamePassword(String str, String str2, String str3, String str4) {
        checkThread();
        this.mProvider.setHttpAuthUsernamePassword(str, str2, str3, str4);
    }

    public String[] getHttpAuthUsernamePassword(String str, String str2) {
        checkThread();
        return this.mProvider.getHttpAuthUsernamePassword(str, str2);
    }

    public void destroy() {
        checkThread();
        this.mProvider.destroy();
    }

    @Deprecated
    public static void enablePlatformNotifications() {
        getFactory().getStatics().setPlatformNotificationsEnabled(true);
    }

    @Deprecated
    public static void disablePlatformNotifications() {
        getFactory().getStatics().setPlatformNotificationsEnabled(false);
    }

    public void setNetworkAvailable(boolean z) {
        checkThread();
        this.mProvider.setNetworkAvailable(z);
    }

    public WebBackForwardList saveState(Bundle bundle) {
        checkThread();
        return this.mProvider.saveState(bundle);
    }

    @Deprecated
    public boolean savePicture(Bundle bundle, File file) {
        checkThread();
        return this.mProvider.savePicture(bundle, file);
    }

    @Deprecated
    public boolean restorePicture(Bundle bundle, File file) {
        checkThread();
        return this.mProvider.restorePicture(bundle, file);
    }

    public WebBackForwardList restoreState(Bundle bundle) {
        checkThread();
        return this.mProvider.restoreState(bundle);
    }

    public void loadUrl(String str, Map<String, String> map) {
        checkThread();
        this.mProvider.loadUrl(str, map);
    }

    public void loadUrl(String str) {
        checkThread();
        this.mProvider.loadUrl(str);
    }

    public void postUrl(String str, byte[] bArr) {
        checkThread();
        this.mProvider.postUrl(str, bArr);
    }

    public void loadData(String str, String str2, String str3) {
        checkThread();
        this.mProvider.loadData(str, str2, str3);
    }

    public void loadDataWithBaseURL(String str, String str2, String str3, String str4, String str5) {
        checkThread();
        this.mProvider.loadDataWithBaseURL(str, str2, str3, str4, str5);
    }

    public void evaluateJavascript(String str, ValueCallback<String> valueCallback) {
        checkThread();
        this.mProvider.evaluateJavaScript(str, valueCallback);
    }

    public void saveWebArchive(String str) {
        checkThread();
        this.mProvider.saveWebArchive(str);
    }

    public void saveWebArchive(String str, boolean z, ValueCallback<String> valueCallback) {
        checkThread();
        this.mProvider.saveWebArchive(str, z, valueCallback);
    }

    public void stopLoading() {
        checkThread();
        this.mProvider.stopLoading();
    }

    public void reload() {
        checkThread();
        this.mProvider.reload();
    }

    public boolean canGoBack() {
        checkThread();
        return this.mProvider.canGoBack();
    }

    public void goBack() {
        checkThread();
        this.mProvider.goBack();
    }

    public boolean canGoForward() {
        checkThread();
        return this.mProvider.canGoForward();
    }

    public void goForward() {
        checkThread();
        this.mProvider.goForward();
    }

    public boolean canGoBackOrForward(int i) {
        checkThread();
        return this.mProvider.canGoBackOrForward(i);
    }

    public void goBackOrForward(int i) {
        checkThread();
        this.mProvider.goBackOrForward(i);
    }

    public boolean isPrivateBrowsingEnabled() {
        checkThread();
        return this.mProvider.isPrivateBrowsingEnabled();
    }

    public boolean pageUp(boolean z) {
        checkThread();
        return this.mProvider.pageUp(z);
    }

    public boolean pageDown(boolean z) {
        checkThread();
        return this.mProvider.pageDown(z);
    }

    @Deprecated
    public void clearView() {
        checkThread();
        this.mProvider.clearView();
    }

    @Deprecated
    public Picture capturePicture() {
        checkThread();
        return this.mProvider.capturePicture();
    }

    public PrintDocumentAdapter createPrintDocumentAdapter() {
        checkThread();
        return this.mProvider.createPrintDocumentAdapter();
    }

    @ViewDebug.ExportedProperty(category = "webview")
    @Deprecated
    public float getScale() {
        checkThread();
        return this.mProvider.getScale();
    }

    public void setInitialScale(int i) {
        checkThread();
        this.mProvider.setInitialScale(i);
    }

    public void invokeZoomPicker() {
        checkThread();
        this.mProvider.invokeZoomPicker();
    }

    public HitTestResult getHitTestResult() {
        checkThread();
        return this.mProvider.getHitTestResult();
    }

    public void requestFocusNodeHref(Message message) {
        checkThread();
        this.mProvider.requestFocusNodeHref(message);
    }

    public void requestImageRef(Message message) {
        checkThread();
        this.mProvider.requestImageRef(message);
    }

    @ViewDebug.ExportedProperty(category = "webview")
    public String getUrl() {
        checkThread();
        return this.mProvider.getUrl();
    }

    @ViewDebug.ExportedProperty(category = "webview")
    public String getOriginalUrl() {
        checkThread();
        return this.mProvider.getOriginalUrl();
    }

    @ViewDebug.ExportedProperty(category = "webview")
    public String getTitle() {
        checkThread();
        return this.mProvider.getTitle();
    }

    public Bitmap getFavicon() {
        checkThread();
        return this.mProvider.getFavicon();
    }

    public String getTouchIconUrl() {
        return this.mProvider.getTouchIconUrl();
    }

    public int getProgress() {
        checkThread();
        return this.mProvider.getProgress();
    }

    @ViewDebug.ExportedProperty(category = "webview")
    public int getContentHeight() {
        checkThread();
        return this.mProvider.getContentHeight();
    }

    @ViewDebug.ExportedProperty(category = "webview")
    public int getContentWidth() {
        return this.mProvider.getContentWidth();
    }

    public void pauseTimers() {
        checkThread();
        this.mProvider.pauseTimers();
    }

    public void resumeTimers() {
        checkThread();
        this.mProvider.resumeTimers();
    }

    public void onPause() {
        checkThread();
        this.mProvider.onPause();
    }

    public void onResume() {
        checkThread();
        this.mProvider.onResume();
    }

    public boolean isPaused() {
        return this.mProvider.isPaused();
    }

    @Deprecated
    public void freeMemory() {
        checkThread();
        this.mProvider.freeMemory();
    }

    public void clearCache(boolean z) {
        checkThread();
        this.mProvider.clearCache(z);
    }

    public void clearFormData() {
        checkThread();
        this.mProvider.clearFormData();
    }

    public void clearHistory() {
        checkThread();
        this.mProvider.clearHistory();
    }

    public void clearSslPreferences() {
        checkThread();
        this.mProvider.clearSslPreferences();
    }

    public WebBackForwardList copyBackForwardList() {
        checkThread();
        return this.mProvider.copyBackForwardList();
    }

    public void setFindListener(FindListener findListener) {
        checkThread();
        setupFindListenerIfNeeded();
        this.mFindListener.mUserFindListener = findListener;
    }

    public void findNext(boolean z) {
        checkThread();
        this.mProvider.findNext(z);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: dalvik.system.BlockGuard$BlockGuardPolicyException */
    @Deprecated
    public int findAll(String str) throws BlockGuard.BlockGuardPolicyException {
        checkThread();
        StrictMode.noteSlowCall("findAll blocks UI: prefer findAllAsync");
        return this.mProvider.findAll(str);
    }

    public void findAllAsync(String str) {
        checkThread();
        this.mProvider.findAllAsync(str);
    }

    @Deprecated
    public boolean showFindDialog(String str, boolean z) {
        checkThread();
        return this.mProvider.showFindDialog(str, z);
    }

    public static String findAddress(String str) {
        return getFactory().getStatics().findAddress(str);
    }

    public void clearMatches() {
        checkThread();
        this.mProvider.clearMatches();
    }

    public void documentHasImages(Message message) {
        checkThread();
        this.mProvider.documentHasImages(message);
    }

    public void setWebViewClient(WebViewClient webViewClient) {
        checkThread();
        this.mProvider.setWebViewClient(webViewClient);
    }

    public void setDownloadListener(DownloadListener downloadListener) {
        checkThread();
        this.mProvider.setDownloadListener(downloadListener);
    }

    public void setWebChromeClient(WebChromeClient webChromeClient) {
        checkThread();
        this.mProvider.setWebChromeClient(webChromeClient);
    }

    @Deprecated
    public void setPictureListener(PictureListener pictureListener) {
        checkThread();
        this.mProvider.setPictureListener(pictureListener);
    }

    public void addJavascriptInterface(Object obj, String str) {
        checkThread();
        this.mProvider.addJavascriptInterface(obj, str);
    }

    public void removeJavascriptInterface(String str) {
        checkThread();
        this.mProvider.removeJavascriptInterface(str);
    }

    public WebSettings getSettings() {
        checkThread();
        return this.mProvider.getSettings();
    }

    public static void setWebContentsDebuggingEnabled(boolean z) {
        getFactory().getStatics().setWebContentsDebuggingEnabled(z);
    }

    @Deprecated
    public static synchronized PluginList getPluginList() {
        return new PluginList();
    }

    @Deprecated
    public void refreshPlugins(boolean z) {
        checkThread();
    }

    @Deprecated
    public void emulateShiftHeld() {
        checkThread();
    }

    @Deprecated
    public void setMapTrackballToArrowKeys(boolean z) {
        checkThread();
        this.mProvider.setMapTrackballToArrowKeys(z);
    }

    public void flingScroll(int i, int i2) {
        checkThread();
        this.mProvider.flingScroll(i, i2);
    }

    @Deprecated
    public View getZoomControls() {
        checkThread();
        return this.mProvider.getZoomControls();
    }

    @Deprecated
    public boolean canZoomIn() {
        checkThread();
        return this.mProvider.canZoomIn();
    }

    @Deprecated
    public boolean canZoomOut() {
        checkThread();
        return this.mProvider.canZoomOut();
    }

    public boolean zoomIn() {
        checkThread();
        return this.mProvider.zoomIn();
    }

    public boolean zoomOut() {
        checkThread();
        return this.mProvider.zoomOut();
    }

    @Deprecated
    public void debugDump() {
        checkThread();
    }

    @Override // android.view.ViewDebug.HierarchyHandler
    public void dumpViewHierarchyWithProperties(BufferedWriter bufferedWriter, int i) {
        this.mProvider.dumpViewHierarchyWithProperties(bufferedWriter, i);
    }

    @Override // android.view.ViewDebug.HierarchyHandler
    public View findHierarchyView(String str, int i) {
        return this.mProvider.findHierarchyView(str, i);
    }

    public WebViewProvider getWebViewProvider() {
        return this.mProvider;
    }

    public class PrivateAccess {
        public PrivateAccess() {
        }

        public int super_getScrollBarStyle() {
            return WebView.super.getScrollBarStyle();
        }

        public void super_scrollTo(int i, int i2) {
            WebView.super.scrollTo(i, i2);
        }

        public void super_computeScroll() {
            WebView.super.computeScroll();
        }

        public boolean super_onHoverEvent(MotionEvent motionEvent) {
            return WebView.super.onHoverEvent(motionEvent);
        }

        public boolean super_performAccessibilityAction(int i, Bundle bundle) {
            return WebView.super.performAccessibilityAction(i, bundle);
        }

        public boolean super_performLongClick() {
            return WebView.super.performLongClick();
        }

        public boolean super_setFrame(int i, int i2, int i3, int i4) {
            return WebView.super.setFrame(i, i2, i3, i4);
        }

        public boolean super_dispatchKeyEvent(KeyEvent keyEvent) {
            return WebView.super.dispatchKeyEvent(keyEvent);
        }

        public boolean super_onGenericMotionEvent(MotionEvent motionEvent) {
            return WebView.super.onGenericMotionEvent(motionEvent);
        }

        public boolean super_requestFocus(int i, Rect rect) {
            return WebView.super.requestFocus(i, rect);
        }

        public void super_setLayoutParams(ViewGroup.LayoutParams layoutParams) {
            WebView.super.setLayoutParams(layoutParams);
        }

        public void overScrollBy(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, boolean z) {
            WebView.this.overScrollBy(i, i2, i3, i4, i5, i6, i7, i8, z);
        }

        public void awakenScrollBars(int i) {
            WebView.this.awakenScrollBars(i);
        }

        public void awakenScrollBars(int i, boolean z) {
            WebView.this.awakenScrollBars(i, z);
        }

        public float getVerticalScrollFactor() {
            return WebView.this.getVerticalScrollFactor();
        }

        public float getHorizontalScrollFactor() {
            return WebView.this.getHorizontalScrollFactor();
        }

        public void setMeasuredDimension(int i, int i2) {
            WebView.this.setMeasuredDimension(i, i2);
        }

        public void onScrollChanged(int i, int i2, int i3, int i4) {
            WebView.this.onScrollChanged(i, i2, i3, i4);
        }

        public int getHorizontalScrollbarHeight() {
            return WebView.this.getHorizontalScrollbarHeight();
        }

        public void super_onDrawVerticalScrollBar(Canvas canvas, Drawable drawable, int i, int i2, int i3, int i4) {
            WebView.super.onDrawVerticalScrollBar(canvas, drawable, i, i2, i3, i4);
        }

        public void setScrollXRaw(int i) {
            WebView.this.mScrollX = i;
        }

        public void setScrollYRaw(int i) {
            WebView.this.mScrollY = i;
        }
    }

    void setFindDialogFindListener(FindListener findListener) {
        checkThread();
        setupFindListenerIfNeeded();
        this.mFindListener.mFindDialogFindListener = findListener;
    }

    void notifyFindDialogDismissed() {
        checkThread();
        this.mProvider.notifyFindDialogDismissed();
    }

    private class FindListenerDistributor implements FindListener {
        private FindListener mFindDialogFindListener;
        private FindListener mUserFindListener;

        private FindListenerDistributor() {
        }

        @Override // android.webkit.WebView.FindListener
        public void onFindResultReceived(int i, int i2, boolean z) {
            FindListener findListener = this.mFindDialogFindListener;
            if (findListener != null) {
                findListener.onFindResultReceived(i, i2, z);
            }
            FindListener findListener2 = this.mUserFindListener;
            if (findListener2 != null) {
                findListener2.onFindResultReceived(i, i2, z);
            }
        }
    }

    private void setupFindListenerIfNeeded() {
        if (this.mFindListener == null) {
            FindListenerDistributor findListenerDistributor = new FindListenerDistributor();
            this.mFindListener = findListenerDistributor;
            this.mProvider.setFindListener(findListenerDistributor);
        }
    }

    private void ensureProviderCreated() {
        checkThread();
        if (this.mProvider == null) {
            this.mProvider = getFactory().createWebView(this, new PrivateAccess());
        }
    }

    private static synchronized WebViewFactoryProvider getFactory() {
        return WebViewFactory.getProvider();
    }

    private void checkThread() {
        if (this.mWebViewThread == null || Looper.myLooper() == this.mWebViewThread) {
            return;
        }
        Throwable th = new Throwable("A WebView method was called on thread '" + Thread.currentThread().getName() + "'. All WebView methods must be called on the same thread. (Expected Looper " + this.mWebViewThread + " called on " + Looper.myLooper() + ", FYI main Looper is " + Looper.getMainLooper() + ")");
        Log.w(LOGTAG, Log.getStackTraceString(th));
        StrictMode.onWebViewMethodCalledOnWrongThread(th);
        if (sEnforceThreadChecking.booleanValue()) {
            throw new RuntimeException(th);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mProvider.getViewDelegate().onAttachedToWindow();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        this.mProvider.getViewDelegate().onDetachedFromWindow();
        super.onDetachedFromWindow();
    }

    @Override // android.view.View
    public void setLayoutParams(ViewGroup.LayoutParams layoutParams) {
        this.mProvider.getViewDelegate().setLayoutParams(layoutParams);
    }

    @Override // android.view.View
    public void setOverScrollMode(int i) {
        super.setOverScrollMode(i);
        ensureProviderCreated();
        this.mProvider.getViewDelegate().setOverScrollMode(i);
    }

    @Override // android.view.View
    public void setScrollBarStyle(int i) {
        this.mProvider.getViewDelegate().setScrollBarStyle(i);
        super.setScrollBarStyle(i);
    }

    @Override // android.view.View
    protected int computeHorizontalScrollRange() {
        return this.mProvider.getScrollDelegate().computeHorizontalScrollRange();
    }

    @Override // android.view.View
    protected int computeHorizontalScrollOffset() {
        return this.mProvider.getScrollDelegate().computeHorizontalScrollOffset();
    }

    @Override // android.view.View
    protected int computeVerticalScrollRange() {
        return this.mProvider.getScrollDelegate().computeVerticalScrollRange();
    }

    @Override // android.view.View
    protected int computeVerticalScrollOffset() {
        return this.mProvider.getScrollDelegate().computeVerticalScrollOffset();
    }

    @Override // android.view.View
    protected int computeVerticalScrollExtent() {
        return this.mProvider.getScrollDelegate().computeVerticalScrollExtent();
    }

    @Override // android.view.View
    public void computeScroll() {
        this.mProvider.getScrollDelegate().computeScroll();
    }

    @Override // android.view.View
    public boolean onHoverEvent(MotionEvent motionEvent) {
        return this.mProvider.getViewDelegate().onHoverEvent(motionEvent);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        return this.mProvider.getViewDelegate().onTouchEvent(motionEvent);
    }

    @Override // android.view.View
    public boolean onGenericMotionEvent(MotionEvent motionEvent) {
        return this.mProvider.getViewDelegate().onGenericMotionEvent(motionEvent);
    }

    @Override // android.view.View
    public boolean onTrackballEvent(MotionEvent motionEvent) {
        return this.mProvider.getViewDelegate().onTrackballEvent(motionEvent);
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        return this.mProvider.getViewDelegate().onKeyDown(i, keyEvent);
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        return this.mProvider.getViewDelegate().onKeyUp(i, keyEvent);
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyMultiple(int i, int i2, KeyEvent keyEvent) {
        return this.mProvider.getViewDelegate().onKeyMultiple(i, i2, keyEvent);
    }

    @Override // android.view.View
    public AccessibilityNodeProvider getAccessibilityNodeProvider() {
        AccessibilityNodeProvider accessibilityNodeProvider = this.mProvider.getViewDelegate().getAccessibilityNodeProvider();
        return accessibilityNodeProvider == null ? super.getAccessibilityNodeProvider() : accessibilityNodeProvider;
    }

    @Override // android.widget.AbsoluteLayout, android.view.ViewGroup
    @Deprecated
    public boolean shouldDelayChildPressedState() {
        return this.mProvider.getViewDelegate().shouldDelayChildPressedState();
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName(WebView.class.getName());
        this.mProvider.getViewDelegate().onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
    }

    @Override // android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        accessibilityEvent.setClassName(WebView.class.getName());
        this.mProvider.getViewDelegate().onInitializeAccessibilityEvent(accessibilityEvent);
    }

    @Override // android.view.View
    public boolean performAccessibilityAction(int i, Bundle bundle) {
        return this.mProvider.getViewDelegate().performAccessibilityAction(i, bundle);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onDrawVerticalScrollBar(Canvas canvas, Drawable drawable, int i, int i2, int i3, int i4) {
        this.mProvider.getViewDelegate().onDrawVerticalScrollBar(canvas, drawable, i, i2, i3, i4);
    }

    @Override // android.view.View
    protected void onOverScrolled(int i, int i2, boolean z, boolean z2) {
        this.mProvider.getViewDelegate().onOverScrolled(i, i2, z, z2);
    }

    @Override // android.view.View
    protected void onWindowVisibilityChanged(int i) {
        super.onWindowVisibilityChanged(i);
        this.mProvider.getViewDelegate().onWindowVisibilityChanged(i);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        this.mProvider.getViewDelegate().onDraw(canvas);
    }

    @Override // android.view.View
    public boolean performLongClick() {
        return this.mProvider.getViewDelegate().performLongClick();
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        this.mProvider.getViewDelegate().onConfigurationChanged(configuration);
    }

    @Override // android.view.View
    public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
        return this.mProvider.getViewDelegate().onCreateInputConnection(editorInfo);
    }

    @Override // android.view.View
    protected void onVisibilityChanged(View view, int i) {
        super.onVisibilityChanged(view, i);
        ensureProviderCreated();
        this.mProvider.getViewDelegate().onVisibilityChanged(view, i);
    }

    @Override // android.view.View
    public void onWindowFocusChanged(boolean z) {
        this.mProvider.getViewDelegate().onWindowFocusChanged(z);
        super.onWindowFocusChanged(z);
    }

    @Override // android.view.View
    protected void onFocusChanged(boolean z, int i, Rect rect) {
        this.mProvider.getViewDelegate().onFocusChanged(z, i, rect);
        super.onFocusChanged(z, i, rect);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public boolean setFrame(int i, int i2, int i3, int i4) {
        return this.mProvider.getViewDelegate().setFrame(i, i2, i3, i4);
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        this.mProvider.getViewDelegate().onSizeChanged(i, i2, i3, i4);
    }

    @Override // android.view.View
    protected void onScrollChanged(int i, int i2, int i3, int i4) {
        super.onScrollChanged(i, i2, i3, i4);
        this.mProvider.getViewDelegate().onScrollChanged(i, i2, i3, i4);
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        return this.mProvider.getViewDelegate().dispatchKeyEvent(keyEvent);
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean requestFocus(int i, Rect rect) {
        return this.mProvider.getViewDelegate().requestFocus(i, rect);
    }

    @Override // android.widget.AbsoluteLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        this.mProvider.getViewDelegate().onMeasure(i, i2);
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public boolean requestChildRectangleOnScreen(View view, Rect rect, boolean z) {
        return this.mProvider.getViewDelegate().requestChildRectangleOnScreen(view, rect, z);
    }

    @Override // android.view.View
    public void setBackgroundColor(int i) {
        this.mProvider.getViewDelegate().setBackgroundColor(i);
    }

    @Override // android.view.View
    public void setLayerType(int i, Paint paint) {
        super.setLayerType(i, paint);
        this.mProvider.getViewDelegate().setLayerType(i, paint);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        this.mProvider.getViewDelegate().preDispatchDraw(canvas);
        super.dispatchDraw(canvas);
    }
}
