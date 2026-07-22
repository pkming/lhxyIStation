package android.app;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.MessageQueue;
import android.util.AttributeSet;
import android.view.InputQueue;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import java.io.File;

/* JADX INFO: loaded from: classes.dex */
public class NativeActivity extends Activity implements SurfaceHolder.Callback2, InputQueue.Callback, ViewTreeObserver.OnGlobalLayoutListener {
    private static final String KEY_NATIVE_SAVED_STATE = "android:native_state";
    public static final String META_DATA_FUNC_NAME = "android.app.func_name";
    public static final String META_DATA_LIB_NAME = "android.app.lib_name";
    private InputQueue mCurInputQueue;
    private SurfaceHolder mCurSurfaceHolder;
    private boolean mDestroyed;
    private boolean mDispatchingUnhandledKey;
    private InputMethodManager mIMM;
    int mLastContentHeight;
    int mLastContentWidth;
    int mLastContentX;
    int mLastContentY;
    final int[] mLocation = new int[2];
    private NativeContentView mNativeContentView;
    private int mNativeHandle;

    private native int loadNativeCode(String str, String str2, MessageQueue messageQueue, String str3, String str4, String str5, int i, AssetManager assetManager, byte[] bArr);

    private native void onConfigurationChangedNative(int i);

    private native void onContentRectChangedNative(int i, int i2, int i3, int i4, int i5);

    private native void onInputQueueCreatedNative(int i, int i2);

    private native void onInputQueueDestroyedNative(int i, int i2);

    private native void onLowMemoryNative(int i);

    private native void onPauseNative(int i);

    private native void onResumeNative(int i);

    private native byte[] onSaveInstanceStateNative(int i);

    private native void onStartNative(int i);

    private native void onStopNative(int i);

    private native void onSurfaceChangedNative(int i, Surface surface, int i2, int i3, int i4);

    private native void onSurfaceCreatedNative(int i, Surface surface);

    private native void onSurfaceDestroyedNative(int i);

    private native void onSurfaceRedrawNeededNative(int i, Surface surface);

    private native void onWindowFocusChangedNative(int i, boolean z);

    private native void unloadNativeCode(int i);

    static class NativeContentView extends View {
        NativeActivity mActivity;

        public NativeContentView(Context context) {
            super(context);
        }

        public NativeContentView(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:12:0x0076 A[PHI: r2
      0x0076: PHI (r2v2 java.lang.String) = (r2v1 java.lang.String), (r2v5 java.lang.String) binds: [B:5:0x005d, B:10:0x0072] A[DONT_GENERATE, DONT_INLINE]] */
    @Override // android.app.Activity
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected void onCreate(android.os.Bundle r14) {
        /*
            Method dump skipped, instruction units count: 265
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.app.NativeActivity.onCreate(android.os.Bundle):void");
    }

    private static String getAbsolutePath(File file) {
        if (file != null) {
            return file.getAbsolutePath();
        }
        return null;
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        this.mDestroyed = true;
        if (this.mCurSurfaceHolder != null) {
            onSurfaceDestroyedNative(this.mNativeHandle);
            this.mCurSurfaceHolder = null;
        }
        InputQueue inputQueue = this.mCurInputQueue;
        if (inputQueue != null) {
            onInputQueueDestroyedNative(this.mNativeHandle, inputQueue.getNativePtr());
            this.mCurInputQueue = null;
        }
        unloadNativeCode(this.mNativeHandle);
        super.onDestroy();
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
        onPauseNative(this.mNativeHandle);
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        onResumeNative(this.mNativeHandle);
    }

    @Override // android.app.Activity
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        byte[] bArrOnSaveInstanceStateNative = onSaveInstanceStateNative(this.mNativeHandle);
        if (bArrOnSaveInstanceStateNative != null) {
            bundle.putByteArray(KEY_NATIVE_SAVED_STATE, bArrOnSaveInstanceStateNative);
        }
    }

    @Override // android.app.Activity
    protected void onStart() {
        super.onStart();
        onStartNative(this.mNativeHandle);
    }

    @Override // android.app.Activity
    protected void onStop() {
        super.onStop();
        onStopNative(this.mNativeHandle);
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (this.mDestroyed) {
            return;
        }
        onConfigurationChangedNative(this.mNativeHandle);
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onLowMemory() {
        super.onLowMemory();
        if (this.mDestroyed) {
            return;
        }
        onLowMemoryNative(this.mNativeHandle);
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        if (this.mDestroyed) {
            return;
        }
        onWindowFocusChangedNative(this.mNativeHandle, z);
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if (this.mDestroyed) {
            return;
        }
        this.mCurSurfaceHolder = surfaceHolder;
        onSurfaceCreatedNative(this.mNativeHandle, surfaceHolder.getSurface());
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        if (this.mDestroyed) {
            return;
        }
        this.mCurSurfaceHolder = surfaceHolder;
        onSurfaceChangedNative(this.mNativeHandle, surfaceHolder.getSurface(), i, i2, i3);
    }

    @Override // android.view.SurfaceHolder.Callback2
    public void surfaceRedrawNeeded(SurfaceHolder surfaceHolder) {
        if (this.mDestroyed) {
            return;
        }
        this.mCurSurfaceHolder = surfaceHolder;
        onSurfaceRedrawNeededNative(this.mNativeHandle, surfaceHolder.getSurface());
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        this.mCurSurfaceHolder = null;
        if (this.mDestroyed) {
            return;
        }
        onSurfaceDestroyedNative(this.mNativeHandle);
    }

    @Override // android.view.InputQueue.Callback
    public void onInputQueueCreated(InputQueue inputQueue) {
        if (this.mDestroyed) {
            return;
        }
        this.mCurInputQueue = inputQueue;
        onInputQueueCreatedNative(this.mNativeHandle, inputQueue.getNativePtr());
    }

    @Override // android.view.InputQueue.Callback
    public void onInputQueueDestroyed(InputQueue inputQueue) {
        if (this.mDestroyed) {
            return;
        }
        onInputQueueDestroyedNative(this.mNativeHandle, inputQueue.getNativePtr());
        this.mCurInputQueue = null;
    }

    @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
    public void onGlobalLayout() {
        this.mNativeContentView.getLocationInWindow(this.mLocation);
        int width = this.mNativeContentView.getWidth();
        int height = this.mNativeContentView.getHeight();
        int[] iArr = this.mLocation;
        if (iArr[0] == this.mLastContentX && iArr[1] == this.mLastContentY && width == this.mLastContentWidth && height == this.mLastContentHeight) {
            return;
        }
        int i = iArr[0];
        this.mLastContentX = i;
        int i2 = iArr[1];
        this.mLastContentY = i2;
        this.mLastContentWidth = width;
        this.mLastContentHeight = height;
        if (this.mDestroyed) {
            return;
        }
        onContentRectChangedNative(this.mNativeHandle, i, i2, width, height);
    }

    void setWindowFlags(int i, int i2) {
        getWindow().setFlags(i, i2);
    }

    void setWindowFormat(int i) {
        getWindow().setFormat(i);
    }

    void showIme(int i) {
        this.mIMM.showSoftInput(this.mNativeContentView, i);
    }

    void hideIme(int i) {
        this.mIMM.hideSoftInputFromWindow(this.mNativeContentView.getWindowToken(), i);
    }
}
