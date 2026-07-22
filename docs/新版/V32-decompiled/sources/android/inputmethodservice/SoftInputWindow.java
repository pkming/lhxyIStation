package android.inputmethodservice;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.media.videoeditor.MediaArtistNativeHelper;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager;

/* JADX INFO: loaded from: classes.dex */
class SoftInputWindow extends Dialog {
    private final Rect mBounds;
    final KeyEvent.DispatcherState mDispatcherState;

    public void setToken(IBinder iBinder) {
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.token = iBinder;
        getWindow().setAttributes(attributes);
    }

    public SoftInputWindow(Context context, int i, KeyEvent.DispatcherState dispatcherState) {
        super(context, i);
        this.mBounds = new Rect();
        this.mDispatcherState = dispatcherState;
        initDockWindow();
    }

    @Override // android.app.Dialog, android.view.Window.Callback
    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        this.mDispatcherState.reset();
    }

    @Override // android.app.Dialog, android.view.Window.Callback
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        getWindow().getDecorView().getHitRect(this.mBounds);
        if (motionEvent.isWithinBoundsNoHistory(this.mBounds.left, this.mBounds.top, this.mBounds.right - 1, this.mBounds.bottom - 1)) {
            return super.dispatchTouchEvent(motionEvent);
        }
        MotionEvent motionEventClampNoHistory = motionEvent.clampNoHistory(this.mBounds.left, this.mBounds.top, this.mBounds.right - 1, this.mBounds.bottom - 1);
        boolean zDispatchTouchEvent = super.dispatchTouchEvent(motionEventClampNoHistory);
        motionEventClampNoHistory.recycle();
        return zDispatchTouchEvent;
    }

    public int getSize() {
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        if (attributes.gravity == 48 || attributes.gravity == 80) {
            return attributes.height;
        }
        return attributes.width;
    }

    public void setSize(int i) {
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        if (attributes.gravity == 48 || attributes.gravity == 80) {
            attributes.width = -1;
            attributes.height = i;
        } else {
            attributes.width = i;
            attributes.height = -1;
        }
        getWindow().setAttributes(attributes);
    }

    public void setGravity(int i) {
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        boolean z = attributes.gravity == 48 || attributes.gravity == 80;
        attributes.gravity = i;
        if (z != (attributes.gravity == 48 || attributes.gravity == 80)) {
            int i2 = attributes.width;
            attributes.width = attributes.height;
            attributes.height = i2;
            getWindow().setAttributes(attributes);
        }
    }

    private void initDockWindow() {
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.type = 2011;
        attributes.setTitle("InputMethod");
        attributes.gravity = 80;
        attributes.width = -1;
        getWindow().setAttributes(attributes);
        getWindow().setFlags(264, MediaArtistNativeHelper.VideoEffect.FIFTIES);
    }
}
