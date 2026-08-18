package android.widget;

import android.R;
import android.app.INotificationManager;
import android.app.ITransientNotification;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes.dex */
public class Toast {
    public static final int LENGTH_LONG = 1;
    public static final int LENGTH_SHORT = 0;
    static final String TAG = "Toast";
    static final boolean localLOGV = false;
    private static INotificationManager sService;
    final Context mContext;
    int mDuration;
    View mNextView;
    final TN mTN;

    public void onHide() {
    }

    public Toast(Context context) {
        this.mContext = context;
        TN tn = new TN(this);
        this.mTN = tn;
        tn.mY = context.getResources().getDimensionPixelSize(17104907);
        tn.mGravity = context.getResources().getInteger(17694790);
    }

    public void show() {
        if (this.mNextView == null) {
            throw new RuntimeException("setView must have been called");
        }
        INotificationManager service = getService();
        String packageName = this.mContext.getPackageName();
        TN tn = this.mTN;
        tn.mNextView = this.mNextView;
        try {
            service.enqueueToast(packageName, tn, this.mDuration);
        } catch (RemoteException unused) {
        }
    }

    public void cancel() {
        this.mTN.hide();
        try {
            getService().cancelToast(this.mContext.getPackageName(), this.mTN);
        } catch (RemoteException unused) {
        }
    }

    public void setView(View view) {
        this.mNextView = view;
    }

    public View getView() {
        return this.mNextView;
    }

    public void setDuration(int i) {
        this.mDuration = i;
    }

    public int getDuration() {
        return this.mDuration;
    }

    public void setMargin(float f, float f2) {
        this.mTN.mHorizontalMargin = f;
        this.mTN.mVerticalMargin = f2;
    }

    public float getHorizontalMargin() {
        return this.mTN.mHorizontalMargin;
    }

    public float getVerticalMargin() {
        return this.mTN.mVerticalMargin;
    }

    public void setGravity(int i, int i2, int i3) {
        this.mTN.mGravity = i;
        this.mTN.mX = i2;
        this.mTN.mY = i3;
    }

    public int getGravity() {
        return this.mTN.mGravity;
    }

    public int getXOffset() {
        return this.mTN.mX;
    }

    public int getYOffset() {
        return this.mTN.mY;
    }

    public static Toast makeText(Context context, CharSequence charSequence, int i) {
        Toast toast = new Toast(context);
        View viewInflate = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(17367221, (ViewGroup) null);
        ((TextView) viewInflate.findViewById(R.id.message)).setText(charSequence);
        toast.mNextView = viewInflate;
        toast.mDuration = i;
        return toast;
    }

    public static Toast makeText(Context context, int i, int i2) throws Resources.NotFoundException {
        return makeText(context, context.getResources().getText(i), i2);
    }

    public void setText(int i) {
        setText(this.mContext.getText(i));
    }

    public void setText(CharSequence charSequence) {
        View view = this.mNextView;
        if (view == null) {
            throw new RuntimeException("This Toast was not created with Toast.makeText()");
        }
        TextView textView = (TextView) view.findViewById(R.id.message);
        if (textView == null) {
            throw new RuntimeException("This Toast was not created with Toast.makeText()");
        }
        textView.setText(charSequence);
    }

    public boolean isShowing() {
        return this.mTN.mView != null;
    }

    private static INotificationManager getService() {
        INotificationManager iNotificationManager = sService;
        if (iNotificationManager != null) {
            return iNotificationManager;
        }
        INotificationManager iNotificationManagerAsInterface = INotificationManager.Stub.asInterface(ServiceManager.getService(Context.NOTIFICATION_SERVICE));
        sService = iNotificationManagerAsInterface;
        return iNotificationManagerAsInterface;
    }

    private static class TN extends ITransientNotification.Stub {
        int mGravity;
        final Handler mHandler;
        float mHorizontalMargin;
        View mNextView;
        private final WindowManager.LayoutParams mParams;
        private WeakReference<Toast> mToast;
        float mVerticalMargin;
        View mView;
        WindowManager mWM;
        int mX;
        int mY;
        final Runnable mShow = new Runnable() { // from class: android.widget.Toast.TN.1
            @Override // java.lang.Runnable
            public void run() {
                TN.this.handleShow();
            }
        };
        final Runnable mHide = new Runnable() { // from class: android.widget.Toast.TN.2
            @Override // java.lang.Runnable
            public void run() {
                TN.this.handleHide();
                TN.this.mNextView = null;
            }
        };

        TN(Toast toast) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            this.mParams = layoutParams;
            this.mHandler = new Handler();
            this.mGravity = 81;
            layoutParams.height = -2;
            layoutParams.width = -2;
            layoutParams.flags = 152;
            layoutParams.format = -3;
            layoutParams.windowAnimations = R.style.Animation_Toast;
            layoutParams.type = 2005;
            layoutParams.setTitle(Toast.TAG);
            this.mToast = new WeakReference<>(toast);
        }

        @Override // android.app.ITransientNotification
        public void show() {
            this.mHandler.post(this.mShow);
        }

        @Override // android.app.ITransientNotification
        public void hide() {
            this.mHandler.post(this.mHide);
        }

        public void handleShow() {
            if (this.mView != this.mNextView) {
                handleHide();
                View view = this.mNextView;
                this.mView = view;
                Context applicationContext = view.getContext().getApplicationContext();
                if (applicationContext == null) {
                    applicationContext = this.mView.getContext();
                }
                this.mWM = (WindowManager) applicationContext.getSystemService(Context.WINDOW_SERVICE);
                int absoluteGravity = Gravity.getAbsoluteGravity(this.mGravity, this.mView.getContext().getResources().getConfiguration().getLayoutDirection());
                this.mParams.gravity = absoluteGravity;
                if ((absoluteGravity & 7) == 7) {
                    this.mParams.horizontalWeight = 1.0f;
                }
                if ((absoluteGravity & 112) == 112) {
                    this.mParams.verticalWeight = 1.0f;
                }
                this.mParams.x = this.mX;
                this.mParams.y = this.mY;
                this.mParams.verticalMargin = this.mVerticalMargin;
                this.mParams.horizontalMargin = this.mHorizontalMargin;
                if (this.mView.getParent() != null) {
                    this.mWM.removeView(this.mView);
                }
                this.mWM.addView(this.mView, this.mParams);
                trySendAccessibilityEvent();
            }
        }

        private void trySendAccessibilityEvent() {
            AccessibilityManager accessibilityManager = AccessibilityManager.getInstance(this.mView.getContext());
            if (accessibilityManager.isEnabled()) {
                AccessibilityEvent accessibilityEventObtain = AccessibilityEvent.obtain(64);
                accessibilityEventObtain.setClassName(getClass().getName());
                accessibilityEventObtain.setPackageName(this.mView.getContext().getPackageName());
                this.mView.dispatchPopulateAccessibilityEvent(accessibilityEventObtain);
                accessibilityManager.sendAccessibilityEvent(accessibilityEventObtain);
            }
        }

        public void handleHide() {
            View view = this.mView;
            if (view != null) {
                if (view.getParent() != null) {
                    this.mWM.removeView(this.mView);
                    if (this.mToast.get() != null) {
                        this.mToast.get().onHide();
                    }
                }
                this.mView = null;
            }
        }
    }
}
