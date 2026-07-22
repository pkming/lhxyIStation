package android.view;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemProperties;
import android.view.ActionMode;
import android.view.InputQueue;
import android.view.SurfaceHolder;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import com.android.internal.R;

/* JADX INFO: loaded from: classes.dex */
public abstract class Window {
    protected static final int DEFAULT_FEATURES = 65;
    public static final int FEATURE_ACTION_BAR = 8;
    public static final int FEATURE_ACTION_BAR_OVERLAY = 9;
    public static final int FEATURE_ACTION_MODE_OVERLAY = 10;
    public static final int FEATURE_CONTEXT_MENU = 6;
    public static final int FEATURE_CUSTOM_TITLE = 7;
    public static final int FEATURE_INDETERMINATE_PROGRESS = 5;
    public static final int FEATURE_LEFT_ICON = 3;
    public static final int FEATURE_MAX = 10;
    public static final int FEATURE_NO_TITLE = 1;
    public static final int FEATURE_OPTIONS_PANEL = 0;
    public static final int FEATURE_PROGRESS = 2;
    public static final int FEATURE_RIGHT_ICON = 4;
    public static final int ID_ANDROID_CONTENT = 16908290;
    public static final int PROGRESS_END = 10000;
    public static final int PROGRESS_INDETERMINATE_OFF = -4;
    public static final int PROGRESS_INDETERMINATE_ON = -3;
    public static final int PROGRESS_SECONDARY_END = 30000;
    public static final int PROGRESS_SECONDARY_START = 20000;
    public static final int PROGRESS_START = 0;
    public static final int PROGRESS_VISIBILITY_OFF = -2;
    public static final int PROGRESS_VISIBILITY_ON = -1;
    private static final String PROPERTY_HARDWARE_UI = "persist.sys.ui.hw";
    private Window mActiveChild;
    private String mAppName;
    private IBinder mAppToken;
    private Callback mCallback;
    private Window mContainer;
    private final Context mContext;
    private boolean mDestroyed;
    private boolean mHardwareAccelerated;
    private WindowManager mWindowManager;
    private TypedArray mWindowStyle;
    private boolean mIsActive = false;
    private boolean mHasChildren = false;
    private boolean mCloseOnTouchOutside = false;
    private boolean mSetCloseOnTouchOutside = false;
    private int mForcedWindowFlags = 0;
    private int mFeatures = 65;
    private int mLocalFeatures = 65;
    private boolean mHaveWindowFormat = false;
    private boolean mHaveDimAmount = false;
    private int mDefaultWindowFormat = -1;
    private boolean mHasSoftInputMode = false;
    private final WindowManager.LayoutParams mWindowAttributes = new WindowManager.LayoutParams();

    public interface Callback {
        boolean dispatchGenericMotionEvent(MotionEvent motionEvent);

        boolean dispatchKeyEvent(KeyEvent keyEvent);

        boolean dispatchKeyShortcutEvent(KeyEvent keyEvent);

        boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent);

        boolean dispatchTouchEvent(MotionEvent motionEvent);

        boolean dispatchTrackballEvent(MotionEvent motionEvent);

        void onActionModeFinished(ActionMode actionMode);

        void onActionModeStarted(ActionMode actionMode);

        void onAttachedToWindow();

        void onContentChanged();

        boolean onCreatePanelMenu(int i, Menu menu);

        View onCreatePanelView(int i);

        void onDetachedFromWindow();

        boolean onMenuItemSelected(int i, MenuItem menuItem);

        boolean onMenuOpened(int i, Menu menu);

        void onPanelClosed(int i, Menu menu);

        boolean onPreparePanel(int i, View view, Menu menu);

        boolean onSearchRequested();

        void onWindowAttributesChanged(WindowManager.LayoutParams layoutParams);

        void onWindowFocusChanged(boolean z);

        ActionMode onWindowStartingActionMode(ActionMode.Callback callback);
    }

    public abstract void addContentView(View view, ViewGroup.LayoutParams layoutParams);

    public abstract void alwaysReadCloseOnTouchAttr();

    public abstract void closeAllPanels();

    public abstract void closePanel(int i);

    public abstract View getCurrentFocus();

    public abstract View getDecorView();

    public abstract LayoutInflater getLayoutInflater();

    public abstract int getVolumeControlStream();

    public void injectInputEvent(InputEvent inputEvent) {
    }

    public abstract void invalidatePanelMenu(int i);

    public abstract boolean isFloating();

    public abstract boolean isShortcutKey(int i, KeyEvent keyEvent);

    protected abstract void onActive();

    public abstract void onConfigurationChanged(Configuration configuration);

    public abstract void openPanel(int i, KeyEvent keyEvent);

    public abstract View peekDecorView();

    public abstract boolean performContextMenuIdentifierAction(int i, int i2);

    public abstract boolean performPanelIdentifierAction(int i, int i2, int i3);

    public abstract boolean performPanelShortcut(int i, int i2, KeyEvent keyEvent, int i3);

    public abstract void restoreHierarchyState(Bundle bundle);

    public abstract Bundle saveHierarchyState();

    public abstract void setBackgroundDrawable(Drawable drawable);

    public abstract void setChildDrawable(int i, Drawable drawable);

    public abstract void setChildInt(int i, int i2);

    public abstract void setContentView(int i);

    public abstract void setContentView(View view);

    public abstract void setContentView(View view, ViewGroup.LayoutParams layoutParams);

    public void setDefaultIcon(int i) {
    }

    public void setDefaultLogo(int i) {
    }

    public abstract void setFeatureDrawable(int i, Drawable drawable);

    public abstract void setFeatureDrawableAlpha(int i, int i2);

    public abstract void setFeatureDrawableResource(int i, int i2);

    public abstract void setFeatureDrawableUri(int i, Uri uri);

    public abstract void setFeatureInt(int i, int i2);

    public void setIcon(int i) {
    }

    public void setLocalFocus(boolean z, boolean z2) {
    }

    public void setLogo(int i) {
    }

    public abstract void setTitle(CharSequence charSequence);

    public abstract void setTitleColor(int i);

    public void setUiOptions(int i) {
    }

    public void setUiOptions(int i, int i2) {
    }

    public abstract void setVolumeControlStream(int i);

    public abstract boolean superDispatchGenericMotionEvent(MotionEvent motionEvent);

    public abstract boolean superDispatchKeyEvent(KeyEvent keyEvent);

    public abstract boolean superDispatchKeyShortcutEvent(KeyEvent keyEvent);

    public abstract boolean superDispatchTouchEvent(MotionEvent motionEvent);

    public abstract boolean superDispatchTrackballEvent(MotionEvent motionEvent);

    public abstract void takeInputQueue(InputQueue.Callback callback);

    public abstract void takeKeyEvents(boolean z);

    public abstract void takeSurface(SurfaceHolder.Callback2 callback2);

    public abstract void togglePanel(int i, KeyEvent keyEvent);

    public Window(Context context) {
        this.mContext = context;
    }

    public final Context getContext() {
        return this.mContext;
    }

    public final TypedArray getWindowStyle() {
        TypedArray typedArray;
        synchronized (this) {
            if (this.mWindowStyle == null) {
                this.mWindowStyle = this.mContext.obtainStyledAttributes(R.styleable.Window);
            }
            typedArray = this.mWindowStyle;
        }
        return typedArray;
    }

    public void setContainer(Window window) {
        this.mContainer = window;
        if (window != null) {
            this.mFeatures |= 2;
            this.mLocalFeatures |= 2;
            window.mHasChildren = true;
        }
    }

    public final Window getContainer() {
        return this.mContainer;
    }

    public final boolean hasChildren() {
        return this.mHasChildren;
    }

    public final void destroy() {
        this.mDestroyed = true;
    }

    public final boolean isDestroyed() {
        return this.mDestroyed;
    }

    public void setWindowManager(WindowManager windowManager, IBinder iBinder, String str) {
        setWindowManager(windowManager, iBinder, str, false);
    }

    public void setWindowManager(WindowManager windowManager, IBinder iBinder, String str, boolean z) {
        this.mAppToken = iBinder;
        this.mAppName = str;
        this.mHardwareAccelerated = z || SystemProperties.getBoolean(PROPERTY_HARDWARE_UI, false);
        if (windowManager == null) {
            windowManager = (WindowManager) this.mContext.getSystemService(Context.WINDOW_SERVICE);
        }
        this.mWindowManager = ((WindowManagerImpl) windowManager).createLocalWindowManager(this);
    }

    void adjustLayoutParamsForSubWindow(WindowManager.LayoutParams layoutParams) {
        CharSequence charSequence;
        String string;
        View viewPeekDecorView;
        CharSequence title = layoutParams.getTitle();
        if (layoutParams.type >= 1000 && layoutParams.type <= 1999) {
            if (layoutParams.token == null && (viewPeekDecorView = peekDecorView()) != null) {
                layoutParams.token = viewPeekDecorView.getWindowToken();
            }
            if (title == null || title.length() == 0) {
                if (layoutParams.type == 1001) {
                    string = "Media";
                } else if (layoutParams.type == 1004) {
                    string = "MediaOvr";
                } else if (layoutParams.type == 1000) {
                    string = "Panel";
                } else if (layoutParams.type == 1002) {
                    string = "SubPanel";
                } else {
                    string = layoutParams.type == 1003 ? "AtchDlg" : Integer.toString(layoutParams.type);
                }
                if (this.mAppName != null) {
                    string = string + ":" + this.mAppName;
                }
                layoutParams.setTitle(string);
            }
        } else {
            if (layoutParams.token == null) {
                Window window = this.mContainer;
                layoutParams.token = window == null ? this.mAppToken : window.mAppToken;
            }
            if ((title == null || title.length() == 0) && (charSequence = this.mAppName) != null) {
                layoutParams.setTitle(charSequence);
            }
        }
        if (layoutParams.packageName == null) {
            layoutParams.packageName = this.mContext.getPackageName();
        }
        if (this.mHardwareAccelerated) {
            layoutParams.flags |= 16777216;
        }
    }

    public WindowManager getWindowManager() {
        return this.mWindowManager;
    }

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    public final Callback getCallback() {
        return this.mCallback;
    }

    public void setLayout(int i, int i2) {
        WindowManager.LayoutParams attributes = getAttributes();
        attributes.width = i;
        attributes.height = i2;
        Callback callback = this.mCallback;
        if (callback != null) {
            callback.onWindowAttributesChanged(attributes);
        }
    }

    public void setGravity(int i) {
        WindowManager.LayoutParams attributes = getAttributes();
        attributes.gravity = i;
        Callback callback = this.mCallback;
        if (callback != null) {
            callback.onWindowAttributesChanged(attributes);
        }
    }

    public void setType(int i) {
        WindowManager.LayoutParams attributes = getAttributes();
        attributes.type = i;
        Callback callback = this.mCallback;
        if (callback != null) {
            callback.onWindowAttributesChanged(attributes);
        }
    }

    public void setFormat(int i) {
        WindowManager.LayoutParams attributes = getAttributes();
        if (i != 0) {
            attributes.format = i;
            this.mHaveWindowFormat = true;
        } else {
            attributes.format = this.mDefaultWindowFormat;
            this.mHaveWindowFormat = false;
        }
        Callback callback = this.mCallback;
        if (callback != null) {
            callback.onWindowAttributesChanged(attributes);
        }
    }

    public void setWindowAnimations(int i) {
        WindowManager.LayoutParams attributes = getAttributes();
        attributes.windowAnimations = i;
        Callback callback = this.mCallback;
        if (callback != null) {
            callback.onWindowAttributesChanged(attributes);
        }
    }

    public void setSoftInputMode(int i) {
        WindowManager.LayoutParams attributes = getAttributes();
        if (i != 0) {
            attributes.softInputMode = i;
            this.mHasSoftInputMode = true;
        } else {
            this.mHasSoftInputMode = false;
        }
        Callback callback = this.mCallback;
        if (callback != null) {
            callback.onWindowAttributesChanged(attributes);
        }
    }

    public void addFlags(int i) {
        setFlags(i, i);
    }

    public void addPrivateFlags(int i) {
        setPrivateFlags(i, i);
    }

    public void clearFlags(int i) {
        setFlags(0, i);
    }

    public void setFlags(int i, int i2) {
        WindowManager.LayoutParams attributes = getAttributes();
        attributes.flags = (i & i2) | (attributes.flags & (~i2));
        if ((1073741824 & i2) != 0) {
            attributes.privateFlags |= 8;
        }
        this.mForcedWindowFlags |= i2;
        Callback callback = this.mCallback;
        if (callback != null) {
            callback.onWindowAttributesChanged(attributes);
        }
    }

    private void setPrivateFlags(int i, int i2) {
        WindowManager.LayoutParams attributes = getAttributes();
        attributes.privateFlags = (i & i2) | (attributes.privateFlags & (~i2));
        Callback callback = this.mCallback;
        if (callback != null) {
            callback.onWindowAttributesChanged(attributes);
        }
    }

    public void setDimAmount(float f) {
        WindowManager.LayoutParams attributes = getAttributes();
        attributes.dimAmount = f;
        this.mHaveDimAmount = true;
        Callback callback = this.mCallback;
        if (callback != null) {
            callback.onWindowAttributesChanged(attributes);
        }
    }

    public void setAttributes(WindowManager.LayoutParams layoutParams) {
        this.mWindowAttributes.copyFrom(layoutParams);
        Callback callback = this.mCallback;
        if (callback != null) {
            callback.onWindowAttributesChanged(this.mWindowAttributes);
        }
    }

    public final WindowManager.LayoutParams getAttributes() {
        return this.mWindowAttributes;
    }

    protected final int getForcedWindowFlags() {
        return this.mForcedWindowFlags;
    }

    protected final boolean hasSoftInputMode() {
        return this.mHasSoftInputMode;
    }

    public void setCloseOnTouchOutside(boolean z) {
        this.mCloseOnTouchOutside = z;
        this.mSetCloseOnTouchOutside = true;
    }

    public void setCloseOnTouchOutsideIfNotSet(boolean z) {
        if (this.mSetCloseOnTouchOutside) {
            return;
        }
        this.mCloseOnTouchOutside = z;
        this.mSetCloseOnTouchOutside = true;
    }

    public boolean shouldCloseOnTouch(Context context, MotionEvent motionEvent) {
        return this.mCloseOnTouchOutside && motionEvent.getAction() == 0 && isOutOfBounds(context, motionEvent) && peekDecorView() != null;
    }

    private boolean isOutOfBounds(Context context, MotionEvent motionEvent) {
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        int scaledWindowTouchSlop = ViewConfiguration.get(context).getScaledWindowTouchSlop();
        View decorView = getDecorView();
        int i = -scaledWindowTouchSlop;
        return x < i || y < i || x > decorView.getWidth() + scaledWindowTouchSlop || y > decorView.getHeight() + scaledWindowTouchSlop;
    }

    public boolean requestFeature(int i) {
        int i2 = 1 << i;
        int i3 = this.mFeatures | i2;
        this.mFeatures = i3;
        int i4 = this.mLocalFeatures;
        Window window = this.mContainer;
        this.mLocalFeatures = i4 | (window != null ? (~window.mFeatures) & i2 : i2);
        return (i2 & i3) != 0;
    }

    protected void removeFeature(int i) {
        int i2 = 1 << i;
        this.mFeatures &= ~i2;
        int i3 = this.mLocalFeatures;
        Window window = this.mContainer;
        if (window != null) {
            i2 &= ~window.mFeatures;
        }
        this.mLocalFeatures = (~i2) & i3;
    }

    public final void makeActive() {
        Window window = this.mContainer;
        if (window != null) {
            Window window2 = window.mActiveChild;
            if (window2 != null) {
                window2.mIsActive = false;
            }
            window.mActiveChild = this;
        }
        this.mIsActive = true;
        onActive();
    }

    public final boolean isActive() {
        return this.mIsActive;
    }

    public View findViewById(int i) {
        return getDecorView().findViewById(i);
    }

    public void setBackgroundDrawableResource(int i) {
        setBackgroundDrawable(this.mContext.getResources().getDrawable(i));
    }

    protected final int getFeatures() {
        return this.mFeatures;
    }

    public boolean hasFeature(int i) {
        return ((1 << i) & getFeatures()) != 0;
    }

    protected final int getLocalFeatures() {
        return this.mLocalFeatures;
    }

    protected void setDefaultWindowFormat(int i) {
        this.mDefaultWindowFormat = i;
        if (this.mHaveWindowFormat) {
            return;
        }
        WindowManager.LayoutParams attributes = getAttributes();
        attributes.format = i;
        Callback callback = this.mCallback;
        if (callback != null) {
            callback.onWindowAttributesChanged(attributes);
        }
    }

    protected boolean haveDimAmount() {
        return this.mHaveDimAmount;
    }
}
