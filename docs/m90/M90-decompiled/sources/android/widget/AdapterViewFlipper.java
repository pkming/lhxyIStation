package android.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.RemotableViewMethod;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.RemoteViews;
import com.android.internal.R;

/* JADX INFO: loaded from: classes.dex */
@RemoteViews.RemoteView
public class AdapterViewFlipper extends AdapterViewAnimator {
    private static final int DEFAULT_INTERVAL = 10000;
    private static final boolean LOGD = false;
    private static final String TAG = "ViewFlipper";
    private final int FLIP_MSG;
    private boolean mAdvancedByHost;
    private boolean mAutoStart;
    private int mFlipInterval;
    private final Handler mHandler;
    private final BroadcastReceiver mReceiver;
    private boolean mRunning;
    private boolean mStarted;
    private boolean mUserPresent;
    private boolean mVisible;

    public AdapterViewFlipper(Context context) {
        super(context);
        this.mFlipInterval = 10000;
        this.mAutoStart = false;
        this.mRunning = false;
        this.mStarted = false;
        this.mVisible = false;
        this.mUserPresent = true;
        this.mAdvancedByHost = false;
        this.mReceiver = new BroadcastReceiver() { // from class: android.widget.AdapterViewFlipper.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                String action = intent.getAction();
                if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                    AdapterViewFlipper.this.mUserPresent = false;
                    AdapterViewFlipper.this.updateRunning();
                } else if (Intent.ACTION_USER_PRESENT.equals(action)) {
                    AdapterViewFlipper.this.mUserPresent = true;
                    AdapterViewFlipper.this.updateRunning(false);
                }
            }
        };
        this.FLIP_MSG = 1;
        this.mHandler = new Handler() { // from class: android.widget.AdapterViewFlipper.2
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                if (message.what == 1 && AdapterViewFlipper.this.mRunning) {
                    AdapterViewFlipper.this.showNext();
                }
            }
        };
    }

    public AdapterViewFlipper(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mFlipInterval = 10000;
        this.mAutoStart = false;
        this.mRunning = false;
        this.mStarted = false;
        this.mVisible = false;
        this.mUserPresent = true;
        this.mAdvancedByHost = false;
        this.mReceiver = new BroadcastReceiver() { // from class: android.widget.AdapterViewFlipper.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                String action = intent.getAction();
                if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                    AdapterViewFlipper.this.mUserPresent = false;
                    AdapterViewFlipper.this.updateRunning();
                } else if (Intent.ACTION_USER_PRESENT.equals(action)) {
                    AdapterViewFlipper.this.mUserPresent = true;
                    AdapterViewFlipper.this.updateRunning(false);
                }
            }
        };
        this.FLIP_MSG = 1;
        this.mHandler = new Handler() { // from class: android.widget.AdapterViewFlipper.2
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                if (message.what == 1 && AdapterViewFlipper.this.mRunning) {
                    AdapterViewFlipper.this.showNext();
                }
            }
        };
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.AdapterViewFlipper);
        this.mFlipInterval = typedArrayObtainStyledAttributes.getInt(0, 10000);
        this.mAutoStart = typedArrayObtainStyledAttributes.getBoolean(1, false);
        this.mLoopViews = true;
        typedArrayObtainStyledAttributes.recycle();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_USER_PRESENT);
        getContext().registerReceiver(this.mReceiver, intentFilter);
        if (this.mAutoStart) {
            startFlipping();
        }
    }

    @Override // android.widget.AdapterView, android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mVisible = false;
        getContext().unregisterReceiver(this.mReceiver);
        updateRunning();
    }

    @Override // android.view.View
    protected void onWindowVisibilityChanged(int i) {
        super.onWindowVisibilityChanged(i);
        this.mVisible = i == 0;
        updateRunning(false);
    }

    @Override // android.widget.AdapterViewAnimator, android.widget.AdapterView
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        updateRunning();
    }

    public int getFlipInterval() {
        return this.mFlipInterval;
    }

    public void setFlipInterval(int i) {
        this.mFlipInterval = i;
    }

    public void startFlipping() {
        this.mStarted = true;
        updateRunning();
    }

    public void stopFlipping() {
        this.mStarted = false;
        updateRunning();
    }

    @Override // android.widget.AdapterViewAnimator
    @RemotableViewMethod
    public void showNext() {
        if (this.mRunning) {
            this.mHandler.removeMessages(1);
            this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(1), this.mFlipInterval);
        }
        super.showNext();
    }

    @Override // android.widget.AdapterViewAnimator
    @RemotableViewMethod
    public void showPrevious() {
        if (this.mRunning) {
            this.mHandler.removeMessages(1);
            this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(1), this.mFlipInterval);
        }
        super.showPrevious();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateRunning() {
        updateRunning(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateRunning(boolean z) {
        boolean z2 = !this.mAdvancedByHost && this.mVisible && this.mStarted && this.mUserPresent && this.mAdapter != null;
        if (z2 != this.mRunning) {
            if (z2) {
                showOnly(this.mWhichChild, z);
                this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(1), this.mFlipInterval);
            } else {
                this.mHandler.removeMessages(1);
            }
            this.mRunning = z2;
        }
    }

    public boolean isFlipping() {
        return this.mStarted;
    }

    public void setAutoStart(boolean z) {
        this.mAutoStart = z;
    }

    public boolean isAutoStart() {
        return this.mAutoStart;
    }

    @Override // android.widget.AdapterViewAnimator, android.widget.Advanceable
    public void fyiWillBeAdvancedByHostKThx() {
        this.mAdvancedByHost = true;
        updateRunning(false);
    }

    @Override // android.widget.AdapterViewAnimator, android.widget.AdapterView, android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        accessibilityEvent.setClassName(AdapterViewFlipper.class.getName());
    }

    @Override // android.widget.AdapterViewAnimator, android.widget.AdapterView, android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName(AdapterViewFlipper.class.getName());
    }
}
