package android.appwidget;

import android.R;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Process;
import android.os.UserHandle;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.RemoteViews;
import android.widget.RemoteViewsAdapter;
import android.widget.TextView;

/* JADX INFO: loaded from: classes.dex */
public class AppWidgetHostView extends FrameLayout {
    static final boolean CROSSFADE = false;
    static final int FADE_DURATION = 1000;
    static final boolean LOGD = false;
    static final String TAG = "AppWidgetHostView";
    static final int VIEW_MODE_CONTENT = 1;
    static final int VIEW_MODE_DEFAULT = 3;
    static final int VIEW_MODE_ERROR = 2;
    static final int VIEW_MODE_NOINIT = 0;
    static final LayoutInflater.Filter sInflaterFilter = new LayoutInflater.Filter() { // from class: android.appwidget.AppWidgetHostView.1
        @Override // android.view.LayoutInflater.Filter
        public boolean onLoadClass(Class cls) {
            return cls.isAnnotationPresent(RemoteViews.RemoteView.class);
        }
    };
    int mAppWidgetId;
    Context mContext;
    long mFadeStartTime;
    AppWidgetProviderInfo mInfo;
    int mLayoutId;
    Bitmap mOld;
    Paint mOldPaint;
    private RemoteViews.OnClickHandler mOnClickHandler;
    Context mRemoteContext;
    private UserHandle mUser;
    View mView;
    int mViewMode;

    public AppWidgetHostView(Context context) {
        this(context, R.anim.fade_in, R.anim.fade_out);
    }

    public AppWidgetHostView(Context context, RemoteViews.OnClickHandler onClickHandler) {
        this(context, R.anim.fade_in, R.anim.fade_out);
        this.mOnClickHandler = onClickHandler;
    }

    public AppWidgetHostView(Context context, int i, int i2) {
        super(context);
        this.mViewMode = 0;
        this.mLayoutId = -1;
        this.mFadeStartTime = -1L;
        this.mOldPaint = new Paint();
        this.mContext = context;
        this.mUser = Process.myUserHandle();
        setIsRootNamespace(true);
    }

    public void setUserId(int i) {
        this.mUser = new UserHandle(i);
    }

    public void setOnClickHandler(RemoteViews.OnClickHandler onClickHandler) {
        this.mOnClickHandler = onClickHandler;
    }

    public void setAppWidget(int i, AppWidgetProviderInfo appWidgetProviderInfo) {
        this.mAppWidgetId = i;
        this.mInfo = appWidgetProviderInfo;
        if (appWidgetProviderInfo != null) {
            Rect defaultPaddingForWidget = getDefaultPaddingForWidget(this.mContext, appWidgetProviderInfo.provider, null);
            setPadding(defaultPaddingForWidget.left, defaultPaddingForWidget.top, defaultPaddingForWidget.right, defaultPaddingForWidget.bottom);
            setContentDescription(appWidgetProviderInfo.label);
        }
    }

    public static Rect getDefaultPaddingForWidget(Context context, ComponentName componentName, Rect rect) {
        PackageManager packageManager = context.getPackageManager();
        if (rect == null) {
            rect = new Rect(0, 0, 0, 0);
        } else {
            rect.set(0, 0, 0, 0);
        }
        try {
            if (packageManager.getApplicationInfo(componentName.getPackageName(), 0).targetSdkVersion >= 14) {
                Resources resources = context.getResources();
                rect.left = resources.getDimensionPixelSize(17104985);
                rect.right = resources.getDimensionPixelSize(17104987);
                rect.top = resources.getDimensionPixelSize(17104986);
                rect.bottom = resources.getDimensionPixelSize(17104988);
            }
        } catch (PackageManager.NameNotFoundException unused) {
        }
        return rect;
    }

    public int getAppWidgetId() {
        return this.mAppWidgetId;
    }

    public AppWidgetProviderInfo getAppWidgetInfo() {
        return this.mInfo;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchSaveInstanceState(SparseArray<Parcelable> sparseArray) {
        ParcelableSparseArray parcelableSparseArray = new ParcelableSparseArray();
        super.dispatchSaveInstanceState(parcelableSparseArray);
        sparseArray.put(generateId(), parcelableSparseArray);
    }

    private int generateId() {
        int id = getId();
        return id == -1 ? this.mAppWidgetId : id;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchRestoreInstanceState(SparseArray<Parcelable> sparseArray) {
        Parcelable parcelable = sparseArray.get(generateId());
        ParcelableSparseArray parcelableSparseArray = (parcelable == null || !(parcelable instanceof ParcelableSparseArray)) ? null : (ParcelableSparseArray) parcelable;
        if (parcelableSparseArray == null) {
            parcelableSparseArray = new ParcelableSparseArray();
        }
        try {
            super.dispatchRestoreInstanceState(parcelableSparseArray);
        } catch (Exception e) {
            Log.e(TAG, "failed to restoreInstanceState for widget id: " + this.mAppWidgetId + ", " + (this.mInfo == null ? "null" : this.mInfo.provider), e);
        }
    }

    public void updateAppWidgetSize(Bundle bundle, int i, int i2, int i3, int i4) {
        updateAppWidgetSize(bundle, i, i2, i3, i4, false);
    }

    public void updateAppWidgetSize(Bundle bundle, int i, int i2, int i3, int i4, boolean z) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        Rect rect = new Rect();
        AppWidgetProviderInfo appWidgetProviderInfo = this.mInfo;
        if (appWidgetProviderInfo != null) {
            rect = getDefaultPaddingForWidget(this.mContext, appWidgetProviderInfo.provider, rect);
        }
        float f = getResources().getDisplayMetrics().density;
        int i5 = (int) ((rect.left + rect.right) / f);
        int i6 = (int) ((rect.top + rect.bottom) / f);
        int i7 = i - (z ? 0 : i5);
        int i8 = i2 - (z ? 0 : i6);
        if (z) {
            i5 = 0;
        }
        int i9 = i3 - i5;
        if (z) {
            i6 = 0;
        }
        int i10 = i4 - i6;
        Bundle appWidgetOptions = AppWidgetManager.getInstance(this.mContext).getAppWidgetOptions(this.mAppWidgetId);
        if ((i7 == appWidgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH) && i8 == appWidgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT) && i9 == appWidgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH) && i10 == appWidgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT)) ? false : true) {
            bundle.putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, i7);
            bundle.putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, i8);
            bundle.putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, i9);
            bundle.putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, i10);
            updateAppWidgetOptions(bundle);
        }
    }

    public void updateAppWidgetOptions(Bundle bundle) {
        AppWidgetManager.getInstance(this.mContext).updateAppWidgetOptions(this.mAppWidgetId, bundle);
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup
    public FrameLayout.LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        Context context = this.mRemoteContext;
        if (context == null) {
            context = this.mContext;
        }
        return new FrameLayout.LayoutParams(context, attributeSet);
    }

    void resetAppWidget(AppWidgetProviderInfo appWidgetProviderInfo) {
        this.mInfo = appWidgetProviderInfo;
        this.mViewMode = 0;
        updateAppWidget(null);
    }

    public void updateAppWidget(RemoteViews remoteViews) {
        View viewApply;
        View view = null;
        RuntimeException runtimeException = null;
        view = null;
        boolean z = false;
        if (remoteViews == null) {
            if (this.mViewMode == 3) {
                return;
            }
            viewApply = getDefaultView();
            this.mLayoutId = -1;
            this.mViewMode = 3;
        } else {
            this.mRemoteContext = getRemoteContext(remoteViews);
            int layoutId = remoteViews.getLayoutId();
            if (layoutId == this.mLayoutId) {
                try {
                    remoteViews.reapply(this.mContext, this.mView, this.mOnClickHandler);
                    e = null;
                    view = this.mView;
                    z = true;
                } catch (RuntimeException e) {
                    e = e;
                }
            } else {
                e = null;
            }
            if (view == null) {
                try {
                    viewApply = remoteViews.apply(this.mContext, this, this.mOnClickHandler);
                } catch (RuntimeException e2) {
                    View view2 = view;
                    runtimeException = e2;
                    viewApply = view2;
                }
            } else {
                viewApply = view;
            }
            runtimeException = e;
            this.mLayoutId = layoutId;
            this.mViewMode = 1;
        }
        if (viewApply == null) {
            if (this.mViewMode == 2) {
                return;
            }
            Log.w(TAG, "updateAppWidget couldn't find any view, using error view", runtimeException);
            viewApply = getErrorView();
            this.mViewMode = 2;
        }
        if (!z) {
            prepareView(viewApply);
            addView(viewApply);
        }
        View view3 = this.mView;
        if (view3 != viewApply) {
            removeView(view3);
            this.mView = viewApply;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    void viewDataChanged(int i) {
        View viewFindViewById = findViewById(i);
        if (viewFindViewById == null || !(viewFindViewById instanceof AdapterView)) {
            return;
        }
        AdapterView adapterView = (AdapterView) viewFindViewById;
        Adapter adapter = adapterView.getAdapter();
        if (adapter instanceof BaseAdapter) {
            ((BaseAdapter) adapter).notifyDataSetChanged();
        } else if (adapter == null && (adapterView instanceof RemoteViewsAdapter.RemoteAdapterConnectionCallback)) {
            ((RemoteViewsAdapter.RemoteAdapterConnectionCallback) adapterView).deferNotifyDataSetChanged();
        }
    }

    private Context getRemoteContext(RemoteViews remoteViews) {
        String str = remoteViews.getPackage();
        if (str == null) {
            return this.mContext;
        }
        try {
            return this.mContext.createPackageContextAsUser(str, 4, this.mUser);
        } catch (PackageManager.NameNotFoundException unused) {
            Log.e(TAG, "Package name " + str + " not found");
            return this.mContext;
        }
    }

    @Override // android.view.ViewGroup
    protected boolean drawChild(Canvas canvas, View view, long j) {
        return super.drawChild(canvas, view, j);
    }

    protected void prepareView(View view) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new FrameLayout.LayoutParams(-1, -1);
        }
        layoutParams.gravity = 17;
        view.setLayoutParams(layoutParams);
    }

    protected View getDefaultView() {
        View viewInflate;
        int i;
        Exception exc = null;
        try {
            AppWidgetProviderInfo appWidgetProviderInfo = this.mInfo;
            if (appWidgetProviderInfo != null) {
                Context contextCreatePackageContextAsUser = this.mContext.createPackageContextAsUser(appWidgetProviderInfo.provider.getPackageName(), 4, this.mUser);
                this.mRemoteContext = contextCreatePackageContextAsUser;
                LayoutInflater layoutInflaterCloneInContext = ((LayoutInflater) contextCreatePackageContextAsUser.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).cloneInContext(contextCreatePackageContextAsUser);
                layoutInflaterCloneInContext.setFilter(sInflaterFilter);
                Bundle appWidgetOptions = AppWidgetManager.getInstance(this.mContext).getAppWidgetOptions(this.mAppWidgetId);
                int i2 = this.mInfo.initialLayout;
                if (appWidgetOptions.containsKey(AppWidgetManager.OPTION_APPWIDGET_HOST_CATEGORY) && appWidgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_HOST_CATEGORY) == 2 && (i = this.mInfo.initialKeyguardLayout) != 0) {
                    i2 = i;
                }
                viewInflate = layoutInflaterCloneInContext.inflate(i2, (ViewGroup) this, false);
            } else {
                Log.w(TAG, "can't inflate defaultView because mInfo is missing");
                viewInflate = null;
            }
        } catch (PackageManager.NameNotFoundException | RuntimeException e) {
            exc = e;
            viewInflate = null;
        }
        if (exc != null) {
            Log.w(TAG, "Error inflating AppWidget " + this.mInfo + ": " + exc.toString());
        }
        return viewInflate == null ? getErrorView() : viewInflate;
    }

    protected View getErrorView() {
        TextView textView = new TextView(this.mContext);
        textView.setText(17040543);
        textView.setBackgroundColor(Color.argb(127, 0, 0, 0));
        return textView;
    }

    @Override // android.widget.FrameLayout, android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName(AppWidgetHostView.class.getName());
    }

    private static class ParcelableSparseArray extends SparseArray<Parcelable> implements Parcelable {
        public static final Parcelable.Creator<ParcelableSparseArray> CREATOR = new Parcelable.Creator<ParcelableSparseArray>() { // from class: android.appwidget.AppWidgetHostView.ParcelableSparseArray.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public ParcelableSparseArray createFromParcel(Parcel parcel) {
                ParcelableSparseArray parcelableSparseArray = new ParcelableSparseArray();
                ClassLoader classLoader = parcelableSparseArray.getClass().getClassLoader();
                int i = parcel.readInt();
                for (int i2 = 0; i2 < i; i2++) {
                    parcelableSparseArray.put(parcel.readInt(), parcel.readParcelable(classLoader));
                }
                return parcelableSparseArray;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public ParcelableSparseArray[] newArray(int i) {
                return new ParcelableSparseArray[i];
            }
        };

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        private ParcelableSparseArray() {
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            int size = size();
            parcel.writeInt(size);
            for (int i2 = 0; i2 < size; i2++) {
                parcel.writeInt(keyAt(i2));
                parcel.writeParcelable(valueAt(i2), 0);
            }
        }
    }
}
