package android.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IRemoteCallback;
import android.os.RemoteException;
import android.view.View;

/* JADX INFO: loaded from: classes.dex */
public class ActivityOptions {
    public static final int ANIM_CUSTOM = 1;
    public static final int ANIM_NONE = 0;
    public static final int ANIM_SCALE_UP = 2;
    public static final int ANIM_THUMBNAIL_SCALE_DOWN = 4;
    public static final int ANIM_THUMBNAIL_SCALE_UP = 3;
    public static final String KEY_ANIM_ENTER_RES_ID = "android:animEnterRes";
    public static final String KEY_ANIM_EXIT_RES_ID = "android:animExitRes";
    public static final String KEY_ANIM_START_HEIGHT = "android:animStartHeight";
    public static final String KEY_ANIM_START_LISTENER = "android:animStartListener";
    public static final String KEY_ANIM_START_WIDTH = "android:animStartWidth";
    public static final String KEY_ANIM_START_X = "android:animStartX";
    public static final String KEY_ANIM_START_Y = "android:animStartY";
    public static final String KEY_ANIM_THUMBNAIL = "android:animThumbnail";
    public static final String KEY_ANIM_TYPE = "android:animType";
    public static final String KEY_PACKAGE_NAME = "android:packageName";
    private IRemoteCallback mAnimationStartedListener;
    private int mAnimationType;
    private int mCustomEnterResId;
    private int mCustomExitResId;
    private String mPackageName;
    private int mStartHeight;
    private int mStartWidth;
    private int mStartX;
    private int mStartY;
    private Bitmap mThumbnail;

    public interface OnAnimationStartedListener {
        void onAnimationStarted();
    }

    public static ActivityOptions makeCustomAnimation(Context context, int i, int i2) {
        return makeCustomAnimation(context, i, i2, null, null);
    }

    public static ActivityOptions makeCustomAnimation(Context context, int i, int i2, Handler handler, OnAnimationStartedListener onAnimationStartedListener) {
        ActivityOptions activityOptions = new ActivityOptions();
        activityOptions.mPackageName = context.getPackageName();
        activityOptions.mAnimationType = 1;
        activityOptions.mCustomEnterResId = i;
        activityOptions.mCustomExitResId = i2;
        activityOptions.setListener(handler, onAnimationStartedListener);
        return activityOptions;
    }

    private void setListener(final Handler handler, final OnAnimationStartedListener onAnimationStartedListener) {
        if (onAnimationStartedListener != null) {
            this.mAnimationStartedListener = new IRemoteCallback.Stub() { // from class: android.app.ActivityOptions.1
                @Override // android.os.IRemoteCallback
                public void sendResult(Bundle bundle) throws RemoteException {
                    handler.post(new Runnable() { // from class: android.app.ActivityOptions.1.1
                        @Override // java.lang.Runnable
                        public void run() {
                            onAnimationStartedListener.onAnimationStarted();
                        }
                    });
                }
            };
        }
    }

    public static ActivityOptions makeScaleUpAnimation(View view, int i, int i2, int i3, int i4) {
        ActivityOptions activityOptions = new ActivityOptions();
        activityOptions.mPackageName = view.getContext().getPackageName();
        activityOptions.mAnimationType = 2;
        int[] iArr = new int[2];
        view.getLocationOnScreen(iArr);
        activityOptions.mStartX = iArr[0] + i;
        activityOptions.mStartY = iArr[1] + i2;
        activityOptions.mStartWidth = i3;
        activityOptions.mStartHeight = i4;
        return activityOptions;
    }

    public static ActivityOptions makeThumbnailScaleUpAnimation(View view, Bitmap bitmap, int i, int i2) {
        return makeThumbnailScaleUpAnimation(view, bitmap, i, i2, null);
    }

    public static ActivityOptions makeThumbnailScaleUpAnimation(View view, Bitmap bitmap, int i, int i2, OnAnimationStartedListener onAnimationStartedListener) {
        return makeThumbnailAnimation(view, bitmap, i, i2, onAnimationStartedListener, true);
    }

    public static ActivityOptions makeThumbnailScaleDownAnimation(View view, Bitmap bitmap, int i, int i2, OnAnimationStartedListener onAnimationStartedListener) {
        return makeThumbnailAnimation(view, bitmap, i, i2, onAnimationStartedListener, false);
    }

    private static ActivityOptions makeThumbnailAnimation(View view, Bitmap bitmap, int i, int i2, OnAnimationStartedListener onAnimationStartedListener, boolean z) {
        ActivityOptions activityOptions = new ActivityOptions();
        activityOptions.mPackageName = view.getContext().getPackageName();
        activityOptions.mAnimationType = z ? 3 : 4;
        activityOptions.mThumbnail = bitmap;
        int[] iArr = new int[2];
        view.getLocationOnScreen(iArr);
        activityOptions.mStartX = iArr[0] + i;
        activityOptions.mStartY = iArr[1] + i2;
        activityOptions.setListener(view.getHandler(), onAnimationStartedListener);
        return activityOptions;
    }

    private ActivityOptions() {
        this.mAnimationType = 0;
    }

    public ActivityOptions(Bundle bundle) {
        this.mAnimationType = 0;
        this.mPackageName = bundle.getString(KEY_PACKAGE_NAME);
        int i = bundle.getInt(KEY_ANIM_TYPE);
        this.mAnimationType = i;
        if (i == 1) {
            this.mCustomEnterResId = bundle.getInt(KEY_ANIM_ENTER_RES_ID, 0);
            this.mCustomExitResId = bundle.getInt(KEY_ANIM_EXIT_RES_ID, 0);
            this.mAnimationStartedListener = IRemoteCallback.Stub.asInterface(bundle.getIBinder(KEY_ANIM_START_LISTENER));
        } else {
            if (i == 2) {
                this.mStartX = bundle.getInt(KEY_ANIM_START_X, 0);
                this.mStartY = bundle.getInt(KEY_ANIM_START_Y, 0);
                this.mStartWidth = bundle.getInt(KEY_ANIM_START_WIDTH, 0);
                this.mStartHeight = bundle.getInt(KEY_ANIM_START_HEIGHT, 0);
                return;
            }
            if (i == 3 || i == 4) {
                this.mThumbnail = (Bitmap) bundle.getParcelable(KEY_ANIM_THUMBNAIL);
                this.mStartX = bundle.getInt(KEY_ANIM_START_X, 0);
                this.mStartY = bundle.getInt(KEY_ANIM_START_Y, 0);
                this.mAnimationStartedListener = IRemoteCallback.Stub.asInterface(bundle.getIBinder(KEY_ANIM_START_LISTENER));
            }
        }
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public int getAnimationType() {
        return this.mAnimationType;
    }

    public int getCustomEnterResId() {
        return this.mCustomEnterResId;
    }

    public int getCustomExitResId() {
        return this.mCustomExitResId;
    }

    public Bitmap getThumbnail() {
        return this.mThumbnail;
    }

    public int getStartX() {
        return this.mStartX;
    }

    public int getStartY() {
        return this.mStartY;
    }

    public int getStartWidth() {
        return this.mStartWidth;
    }

    public int getStartHeight() {
        return this.mStartHeight;
    }

    public IRemoteCallback getOnAnimationStartListener() {
        return this.mAnimationStartedListener;
    }

    public void abort() {
        IRemoteCallback iRemoteCallback = this.mAnimationStartedListener;
        if (iRemoteCallback != null) {
            try {
                iRemoteCallback.sendResult(null);
            } catch (RemoteException unused) {
            }
        }
    }

    public static void abort(Bundle bundle) {
        if (bundle != null) {
            new ActivityOptions(bundle).abort();
        }
    }

    public void update(ActivityOptions activityOptions) {
        String str = activityOptions.mPackageName;
        if (str != null) {
            this.mPackageName = str;
        }
        int i = activityOptions.mAnimationType;
        if (i == 1) {
            this.mAnimationType = i;
            this.mCustomEnterResId = activityOptions.mCustomEnterResId;
            this.mCustomExitResId = activityOptions.mCustomExitResId;
            this.mThumbnail = null;
            IRemoteCallback iRemoteCallback = activityOptions.mAnimationStartedListener;
            if (iRemoteCallback != null) {
                try {
                    iRemoteCallback.sendResult(null);
                } catch (RemoteException unused) {
                }
            }
            this.mAnimationStartedListener = activityOptions.mAnimationStartedListener;
            return;
        }
        if (i == 2) {
            this.mAnimationType = i;
            this.mStartX = activityOptions.mStartX;
            this.mStartY = activityOptions.mStartY;
            this.mStartWidth = activityOptions.mStartWidth;
            this.mStartHeight = activityOptions.mStartHeight;
            IRemoteCallback iRemoteCallback2 = activityOptions.mAnimationStartedListener;
            if (iRemoteCallback2 != null) {
                try {
                    iRemoteCallback2.sendResult(null);
                } catch (RemoteException unused2) {
                }
            }
            this.mAnimationStartedListener = null;
            return;
        }
        if (i == 3 || i == 4) {
            this.mAnimationType = i;
            this.mThumbnail = activityOptions.mThumbnail;
            this.mStartX = activityOptions.mStartX;
            this.mStartY = activityOptions.mStartY;
            IRemoteCallback iRemoteCallback3 = activityOptions.mAnimationStartedListener;
            if (iRemoteCallback3 != null) {
                try {
                    iRemoteCallback3.sendResult(null);
                } catch (RemoteException unused3) {
                }
            }
            this.mAnimationStartedListener = activityOptions.mAnimationStartedListener;
        }
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        String str = this.mPackageName;
        if (str != null) {
            bundle.putString(KEY_PACKAGE_NAME, str);
        }
        int i = this.mAnimationType;
        if (i == 1) {
            bundle.putInt(KEY_ANIM_TYPE, i);
            bundle.putInt(KEY_ANIM_ENTER_RES_ID, this.mCustomEnterResId);
            bundle.putInt(KEY_ANIM_EXIT_RES_ID, this.mCustomExitResId);
            IRemoteCallback iRemoteCallback = this.mAnimationStartedListener;
            bundle.putIBinder(KEY_ANIM_START_LISTENER, iRemoteCallback != null ? iRemoteCallback.asBinder() : null);
        } else if (i == 2) {
            bundle.putInt(KEY_ANIM_TYPE, i);
            bundle.putInt(KEY_ANIM_START_X, this.mStartX);
            bundle.putInt(KEY_ANIM_START_Y, this.mStartY);
            bundle.putInt(KEY_ANIM_START_WIDTH, this.mStartWidth);
            bundle.putInt(KEY_ANIM_START_HEIGHT, this.mStartHeight);
        } else if (i == 3 || i == 4) {
            bundle.putInt(KEY_ANIM_TYPE, i);
            bundle.putParcelable(KEY_ANIM_THUMBNAIL, this.mThumbnail);
            bundle.putInt(KEY_ANIM_START_X, this.mStartX);
            bundle.putInt(KEY_ANIM_START_Y, this.mStartY);
            IRemoteCallback iRemoteCallback2 = this.mAnimationStartedListener;
            bundle.putIBinder(KEY_ANIM_START_LISTENER, iRemoteCallback2 != null ? iRemoteCallback2.asBinder() : null);
        }
        return bundle;
    }
}
