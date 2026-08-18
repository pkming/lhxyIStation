package android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.RemotableViewMethod;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.RemoteViews;
import com.android.internal.R;
import java.util.Formatter;
import java.util.IllegalFormatException;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
@RemoteViews.RemoteView
public class Chronometer extends TextView {
    private static final String TAG = "Chronometer";
    private static final int TICK_WHAT = 2;
    private long mBase;
    private String mFormat;
    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;
    private Object[] mFormatterArgs;
    private Locale mFormatterLocale;
    private Handler mHandler;
    private boolean mLogged;
    private OnChronometerTickListener mOnChronometerTickListener;
    private StringBuilder mRecycle;
    private boolean mRunning;
    private boolean mStarted;
    private boolean mVisible;

    public interface OnChronometerTickListener {
        void onChronometerTick(Chronometer chronometer);
    }

    public Chronometer(Context context) {
        this(context, null, 0);
    }

    public Chronometer(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public Chronometer(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mFormatterArgs = new Object[1];
        this.mRecycle = new StringBuilder(8);
        this.mHandler = new Handler() { // from class: android.widget.Chronometer.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                if (Chronometer.this.mRunning) {
                    Chronometer.this.updateText(SystemClock.elapsedRealtime());
                    Chronometer.this.dispatchChronometerTick();
                    sendMessageDelayed(Message.obtain(this, 2), 1000L);
                }
            }
        };
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.Chronometer, i, 0);
        setFormat(typedArrayObtainStyledAttributes.getString(0));
        typedArrayObtainStyledAttributes.recycle();
        init();
    }

    private void init() {
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        this.mBase = jElapsedRealtime;
        updateText(jElapsedRealtime);
    }

    @RemotableViewMethod
    public void setBase(long j) {
        this.mBase = j;
        dispatchChronometerTick();
        updateText(SystemClock.elapsedRealtime());
    }

    public long getBase() {
        return this.mBase;
    }

    @RemotableViewMethod
    public void setFormat(String str) {
        this.mFormat = str;
        if (str == null || this.mFormatBuilder != null) {
            return;
        }
        this.mFormatBuilder = new StringBuilder(str.length() * 2);
    }

    public String getFormat() {
        return this.mFormat;
    }

    public void setOnChronometerTickListener(OnChronometerTickListener onChronometerTickListener) {
        this.mOnChronometerTickListener = onChronometerTickListener;
    }

    public OnChronometerTickListener getOnChronometerTickListener() {
        return this.mOnChronometerTickListener;
    }

    public void start() {
        this.mStarted = true;
        updateRunning();
    }

    public void stop() {
        this.mStarted = false;
        updateRunning();
    }

    @RemotableViewMethod
    public void setStarted(boolean z) {
        this.mStarted = z;
        updateRunning();
    }

    @Override // android.widget.TextView, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mVisible = false;
        updateRunning();
    }

    @Override // android.view.View
    protected void onWindowVisibilityChanged(int i) {
        super.onWindowVisibilityChanged(i);
        this.mVisible = i == 0;
        updateRunning();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void updateText(long j) {
        String elapsedTime = DateUtils.formatElapsedTime(this.mRecycle, (j - this.mBase) / 1000);
        if (this.mFormat != null) {
            Locale locale = Locale.getDefault();
            if (this.mFormatter == null || !locale.equals(this.mFormatterLocale)) {
                this.mFormatterLocale = locale;
                this.mFormatter = new Formatter(this.mFormatBuilder, locale);
            }
            this.mFormatBuilder.setLength(0);
            Object[] objArr = this.mFormatterArgs;
            objArr[0] = elapsedTime;
            try {
                this.mFormatter.format(this.mFormat, objArr);
                elapsedTime = this.mFormatBuilder.toString();
            } catch (IllegalFormatException unused) {
                if (!this.mLogged) {
                    Log.w(TAG, "Illegal format string: " + this.mFormat);
                    this.mLogged = true;
                }
            }
            setText(elapsedTime);
        } else {
            setText(elapsedTime);
        }
    }

    private void updateRunning() {
        boolean z = this.mVisible && this.mStarted;
        if (z != this.mRunning) {
            if (z) {
                updateText(SystemClock.elapsedRealtime());
                dispatchChronometerTick();
                Handler handler = this.mHandler;
                handler.sendMessageDelayed(Message.obtain(handler, 2), 1000L);
            } else {
                this.mHandler.removeMessages(2);
            }
            this.mRunning = z;
        }
    }

    void dispatchChronometerTick() {
        OnChronometerTickListener onChronometerTickListener = this.mOnChronometerTickListener;
        if (onChronometerTickListener != null) {
            onChronometerTickListener.onChronometerTick(this);
        }
    }

    @Override // android.widget.TextView, android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        accessibilityEvent.setClassName(Chronometer.class.getName());
    }

    @Override // android.widget.TextView, android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName(Chronometer.class.getName());
    }
}
