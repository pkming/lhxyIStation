package android.widget;

import android.app.ActivityOptions;
import android.app.PendingIntent;
import android.appwidget.AppWidgetHostView;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Process;
import android.os.StrictMode;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.RemotableViewMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import libcore.util.Objects;
import org.apache.tools.ant.taskdefs.optional.vss.MSVSSConstants;

/* JADX INFO: loaded from: classes.dex */
public class RemoteViews implements Parcelable, LayoutInflater.Filter {
    static final String EXTRA_REMOTEADAPTER_APPWIDGET_ID = "remoteAdapterAppWidgetId";
    private static final String LOG_TAG = "RemoteViews";
    private static final int MODE_HAS_LANDSCAPE_AND_PORTRAIT = 1;
    private static final int MODE_NORMAL = 0;
    private ArrayList<Action> mActions;
    private BitmapCache mBitmapCache;
    private boolean mIsRoot;
    private boolean mIsWidgetCollectionChild;
    private RemoteViews mLandscape;
    private final int mLayoutId;
    private MemoryUsageCounter mMemoryUsageCounter;
    private final String mPackage;
    private final MutablePair<String, Class<?>> mPair;
    private RemoteViews mPortrait;
    private UserHandle mUser;
    private static final OnClickHandler DEFAULT_ON_CLICK_HANDLER = new OnClickHandler();
    private static final Object[] sMethodsLock = new Object[0];
    private static final ArrayMap<Class<? extends View>, ArrayMap<MutablePair<String, Class<?>>, Method>> sMethods = new ArrayMap<>();
    private static final ThreadLocal<Object[]> sInvokeArgsTls = new ThreadLocal<Object[]>() { // from class: android.widget.RemoteViews.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.lang.ThreadLocal
        public Object[] initialValue() {
            return new Object[1];
        }
    };
    public static final Parcelable.Creator<RemoteViews> CREATOR = new Parcelable.Creator<RemoteViews>() { // from class: android.widget.RemoteViews.2
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public RemoteViews createFromParcel(Parcel parcel) {
            return new RemoteViews(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public RemoteViews[] newArray(int i) {
            return new RemoteViews[i];
        }
    };

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface RemoteView {
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    static class MutablePair<F, S> {
        F first;
        S second;

        MutablePair(F f, S s) {
            this.first = f;
            this.second = s;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof MutablePair)) {
                return false;
            }
            MutablePair mutablePair = (MutablePair) obj;
            return Objects.equal(mutablePair.first, this.first) && Objects.equal(mutablePair.second, this.second);
        }

        public int hashCode() {
            F f = this.first;
            int iHashCode = f == null ? 0 : f.hashCode();
            S s = this.second;
            return iHashCode ^ (s != null ? s.hashCode() : 0);
        }
    }

    public static class ActionException extends RuntimeException {
        public ActionException(Exception exc) {
            super(exc);
        }

        public ActionException(String str) {
            super(str);
        }
    }

    public static class OnClickHandler {
        public boolean onClickHandler(View view, PendingIntent pendingIntent, Intent intent) {
            try {
                view.getContext().startIntentSender(pendingIntent.getIntentSender(), intent, 268435456, 268435456, 0, ActivityOptions.makeScaleUpAnimation(view, 0, 0, view.getMeasuredWidth(), view.getMeasuredHeight()).toBundle());
                return true;
            } catch (IntentSender.SendIntentException e) {
                Log.e(RemoteViews.LOG_TAG, "Cannot send pending intent: ", e);
                return false;
            } catch (Exception e2) {
                Log.e(RemoteViews.LOG_TAG, "Cannot send pending intent due to unknown exception: ", e2);
                return false;
            }
        }
    }

    private static abstract class Action implements Parcelable {
        public static final int MERGE_APPEND = 1;
        public static final int MERGE_IGNORE = 2;
        public static final int MERGE_REPLACE = 0;
        int viewId;

        public abstract void apply(View view, ViewGroup viewGroup, OnClickHandler onClickHandler) throws ActionException;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public abstract String getActionName();

        public int mergeBehavior() {
            return 0;
        }

        public void setBitmapCache(BitmapCache bitmapCache) {
        }

        public void updateMemoryUsageEstimate(MemoryUsageCounter memoryUsageCounter) {
        }

        private Action() {
        }

        public String getUniqueKey() {
            return getActionName() + this.viewId;
        }
    }

    public void mergeRemoteViews(RemoteViews remoteViews) {
        if (remoteViews == null) {
            return;
        }
        RemoteViews remoteViewsM22clone = remoteViews.m22clone();
        HashMap map = new HashMap();
        if (this.mActions == null) {
            this.mActions = new ArrayList<>();
        }
        int size = this.mActions.size();
        for (int i = 0; i < size; i++) {
            Action action = this.mActions.get(i);
            map.put(action.getUniqueKey(), action);
        }
        ArrayList<Action> arrayList = remoteViewsM22clone.mActions;
        if (arrayList == null) {
            return;
        }
        int size2 = arrayList.size();
        for (int i2 = 0; i2 < size2; i2++) {
            Action action2 = arrayList.get(i2);
            String uniqueKey = arrayList.get(i2).getUniqueKey();
            int iMergeBehavior = arrayList.get(i2).mergeBehavior();
            if (map.containsKey(uniqueKey) && iMergeBehavior == 0) {
                this.mActions.remove(map.get(uniqueKey));
                map.remove(uniqueKey);
            }
            if (iMergeBehavior == 0 || iMergeBehavior == 1) {
                this.mActions.add(action2);
            }
        }
        BitmapCache bitmapCache = new BitmapCache();
        this.mBitmapCache = bitmapCache;
        setBitmapCache(bitmapCache);
    }

    private class SetEmptyView extends Action {
        public static final int TAG = 6;
        int emptyViewId;
        int viewId;

        @Override // android.widget.RemoteViews.Action
        public String getActionName() {
            return "SetEmptyView";
        }

        SetEmptyView(int i, int i2) {
            super();
            this.viewId = i;
            this.emptyViewId = i2;
        }

        SetEmptyView(Parcel parcel) {
            super();
            this.viewId = parcel.readInt();
            this.emptyViewId = parcel.readInt();
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(6);
            parcel.writeInt(this.viewId);
            parcel.writeInt(this.emptyViewId);
        }

        @Override // android.widget.RemoteViews.Action
        public void apply(View view, ViewGroup viewGroup, OnClickHandler onClickHandler) {
            View viewFindViewById = view.findViewById(this.viewId);
            if (viewFindViewById instanceof AdapterView) {
                AdapterView adapterView = (AdapterView) viewFindViewById;
                View viewFindViewById2 = view.findViewById(this.emptyViewId);
                if (viewFindViewById2 == null) {
                    return;
                }
                adapterView.setEmptyView(viewFindViewById2);
            }
        }
    }

    private class SetOnClickFillInIntent extends Action {
        public static final int TAG = 9;
        Intent fillInIntent;

        @Override // android.widget.RemoteViews.Action
        public String getActionName() {
            return "SetOnClickFillInIntent";
        }

        public SetOnClickFillInIntent(int i, Intent intent) {
            super();
            this.viewId = i;
            this.fillInIntent = intent;
        }

        public SetOnClickFillInIntent(Parcel parcel) {
            super();
            this.viewId = parcel.readInt();
            this.fillInIntent = Intent.CREATOR.createFromParcel(parcel);
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(9);
            parcel.writeInt(this.viewId);
            this.fillInIntent.writeToParcel(parcel, 0);
        }

        @Override // android.widget.RemoteViews.Action
        public void apply(View view, ViewGroup viewGroup, final OnClickHandler onClickHandler) {
            View viewFindViewById = view.findViewById(this.viewId);
            if (viewFindViewById == null) {
                return;
            }
            if (!RemoteViews.this.mIsWidgetCollectionChild) {
                Log.e(RemoteViews.LOG_TAG, "The method setOnClickFillInIntent is available only from RemoteViewsFactory (ie. on collection items).");
            } else if (viewFindViewById == view) {
                viewFindViewById.setTagInternal(16908894, this.fillInIntent);
            } else if (this.fillInIntent != null) {
                viewFindViewById.setOnClickListener(new View.OnClickListener() { // from class: android.widget.RemoteViews.SetOnClickFillInIntent.1
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view2) {
                        View view3 = (View) view2.getParent();
                        while (view3 != null && !(view3 instanceof AdapterView) && !(view3 instanceof AppWidgetHostView)) {
                            view3 = (View) view3.getParent();
                        }
                        if ((view3 instanceof AppWidgetHostView) || view3 == null) {
                            Log.e(RemoteViews.LOG_TAG, "Collection item doesn't have AdapterView parent");
                            return;
                        }
                        if (!(view3.getTag() instanceof PendingIntent)) {
                            Log.e(RemoteViews.LOG_TAG, "Attempting setOnClickFillInIntent without calling setPendingIntentTemplate on parent.");
                            return;
                        }
                        PendingIntent pendingIntent = (PendingIntent) view3.getTag();
                        SetOnClickFillInIntent.this.fillInIntent.setSourceBounds(RemoteViews.getSourceBounds(view2));
                        onClickHandler.onClickHandler(view2, pendingIntent, SetOnClickFillInIntent.this.fillInIntent);
                    }
                });
            }
        }
    }

    private class SetPendingIntentTemplate extends Action {
        public static final int TAG = 8;
        PendingIntent pendingIntentTemplate;

        @Override // android.widget.RemoteViews.Action
        public String getActionName() {
            return "SetPendingIntentTemplate";
        }

        public SetPendingIntentTemplate(int i, PendingIntent pendingIntent) {
            super();
            this.viewId = i;
            this.pendingIntentTemplate = pendingIntent;
        }

        public SetPendingIntentTemplate(Parcel parcel) {
            super();
            this.viewId = parcel.readInt();
            this.pendingIntentTemplate = PendingIntent.readPendingIntentOrNullFromParcel(parcel);
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(8);
            parcel.writeInt(this.viewId);
            this.pendingIntentTemplate.writeToParcel(parcel, 0);
        }

        @Override // android.widget.RemoteViews.Action
        public void apply(View view, ViewGroup viewGroup, final OnClickHandler onClickHandler) {
            View viewFindViewById = view.findViewById(this.viewId);
            if (viewFindViewById == null) {
                return;
            }
            if (viewFindViewById instanceof AdapterView) {
                AdapterView adapterView = (AdapterView) viewFindViewById;
                adapterView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: android.widget.RemoteViews.SetPendingIntentTemplate.1
                    @Override // android.widget.AdapterView.OnItemClickListener
                    public void onItemClick(AdapterView<?> adapterView2, View view2, int i, long j) {
                        if (view2 instanceof ViewGroup) {
                            ViewGroup viewGroup2 = (ViewGroup) view2;
                            int i2 = 0;
                            if (adapterView2 instanceof AdapterViewAnimator) {
                                viewGroup2 = (ViewGroup) viewGroup2.getChildAt(0);
                            }
                            if (viewGroup2 == null) {
                                return;
                            }
                            Intent intent = null;
                            int childCount = viewGroup2.getChildCount();
                            while (true) {
                                if (i2 >= childCount) {
                                    break;
                                }
                                Object tag = viewGroup2.getChildAt(i2).getTag(16908894);
                                if (tag instanceof Intent) {
                                    intent = (Intent) tag;
                                    break;
                                }
                                i2++;
                            }
                            if (intent == null) {
                                return;
                            }
                            new Intent().setSourceBounds(RemoteViews.getSourceBounds(view2));
                            onClickHandler.onClickHandler(view2, SetPendingIntentTemplate.this.pendingIntentTemplate, intent);
                        }
                    }
                });
                adapterView.setTag(this.pendingIntentTemplate);
                return;
            }
            Log.e(RemoteViews.LOG_TAG, "Cannot setPendingIntentTemplate on a view which is notan AdapterView (id: " + this.viewId + ")");
        }
    }

    private class SetRemoteViewsAdapterList extends Action {
        public static final int TAG = 15;
        ArrayList<RemoteViews> list;
        int viewTypeCount;

        @Override // android.widget.RemoteViews.Action
        public String getActionName() {
            return "SetRemoteViewsAdapterList";
        }

        public SetRemoteViewsAdapterList(int i, ArrayList<RemoteViews> arrayList, int i2) {
            super();
            this.viewId = i;
            this.list = arrayList;
            this.viewTypeCount = i2;
        }

        public SetRemoteViewsAdapterList(Parcel parcel) {
            super();
            this.viewId = parcel.readInt();
            this.viewTypeCount = parcel.readInt();
            int i = parcel.readInt();
            this.list = new ArrayList<>();
            for (int i2 = 0; i2 < i; i2++) {
                this.list.add(RemoteViews.CREATOR.createFromParcel(parcel));
            }
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(15);
            parcel.writeInt(this.viewId);
            parcel.writeInt(this.viewTypeCount);
            ArrayList<RemoteViews> arrayList = this.list;
            if (arrayList == null || arrayList.size() == 0) {
                parcel.writeInt(0);
                return;
            }
            int size = this.list.size();
            parcel.writeInt(size);
            for (int i2 = 0; i2 < size; i2++) {
                this.list.get(i2).writeToParcel(parcel, i);
            }
        }

        @Override // android.widget.RemoteViews.Action
        public void apply(View view, ViewGroup viewGroup, OnClickHandler onClickHandler) {
            View viewFindViewById = view.findViewById(this.viewId);
            if (viewFindViewById == null) {
                return;
            }
            if (!(viewGroup instanceof AppWidgetHostView)) {
                Log.e(RemoteViews.LOG_TAG, "SetRemoteViewsAdapterIntent action can only be used for AppWidgets (root id: " + this.viewId + ")");
                return;
            }
            boolean z = viewFindViewById instanceof AbsListView;
            if (!z && !(viewFindViewById instanceof AdapterViewAnimator)) {
                Log.e(RemoteViews.LOG_TAG, "Cannot setRemoteViewsAdapter on a view which is not an AbsListView or AdapterViewAnimator (id: " + this.viewId + ")");
                return;
            }
            if (z) {
                AbsListView absListView = (AbsListView) viewFindViewById;
                ListAdapter adapter = absListView.getAdapter();
                if ((adapter instanceof RemoteViewsListAdapter) && this.viewTypeCount <= adapter.getViewTypeCount()) {
                    ((RemoteViewsListAdapter) adapter).setViewsList(this.list);
                    return;
                } else {
                    absListView.setAdapter((ListAdapter) new RemoteViewsListAdapter(absListView.getContext(), this.list, this.viewTypeCount));
                    return;
                }
            }
            if (viewFindViewById instanceof AdapterViewAnimator) {
                AdapterViewAnimator adapterViewAnimator = (AdapterViewAnimator) viewFindViewById;
                Adapter adapter2 = adapterViewAnimator.getAdapter();
                if ((adapter2 instanceof RemoteViewsListAdapter) && this.viewTypeCount <= adapter2.getViewTypeCount()) {
                    ((RemoteViewsListAdapter) adapter2).setViewsList(this.list);
                } else {
                    adapterViewAnimator.setAdapter(new RemoteViewsListAdapter(adapterViewAnimator.getContext(), this.list, this.viewTypeCount));
                }
            }
        }
    }

    private class SetRemoteViewsAdapterIntent extends Action {
        public static final int TAG = 10;
        Intent intent;

        @Override // android.widget.RemoteViews.Action
        public String getActionName() {
            return "SetRemoteViewsAdapterIntent";
        }

        public SetRemoteViewsAdapterIntent(int i, Intent intent) {
            super();
            this.viewId = i;
            this.intent = intent;
        }

        public SetRemoteViewsAdapterIntent(Parcel parcel) {
            super();
            this.viewId = parcel.readInt();
            this.intent = Intent.CREATOR.createFromParcel(parcel);
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(10);
            parcel.writeInt(this.viewId);
            this.intent.writeToParcel(parcel, i);
        }

        @Override // android.widget.RemoteViews.Action
        public void apply(View view, ViewGroup viewGroup, OnClickHandler onClickHandler) {
            View viewFindViewById = view.findViewById(this.viewId);
            if (viewFindViewById == null) {
                return;
            }
            if (!(viewGroup instanceof AppWidgetHostView)) {
                Log.e(RemoteViews.LOG_TAG, "SetRemoteViewsAdapterIntent action can only be used for AppWidgets (root id: " + this.viewId + ")");
                return;
            }
            boolean z = viewFindViewById instanceof AbsListView;
            if (!z && !(viewFindViewById instanceof AdapterViewAnimator)) {
                Log.e(RemoteViews.LOG_TAG, "Cannot setRemoteViewsAdapter on a view which is not an AbsListView or AdapterViewAnimator (id: " + this.viewId + ")");
                return;
            }
            this.intent.putExtra(RemoteViews.EXTRA_REMOTEADAPTER_APPWIDGET_ID, ((AppWidgetHostView) viewGroup).getAppWidgetId());
            if (z) {
                AbsListView absListView = (AbsListView) viewFindViewById;
                absListView.setRemoteViewsAdapter(this.intent);
                absListView.setRemoteViewsOnClickHandler(onClickHandler);
            } else if (viewFindViewById instanceof AdapterViewAnimator) {
                AdapterViewAnimator adapterViewAnimator = (AdapterViewAnimator) viewFindViewById;
                adapterViewAnimator.setRemoteViewsAdapter(this.intent);
                adapterViewAnimator.setRemoteViewsOnClickHandler(onClickHandler);
            }
        }
    }

    private class SetOnClickPendingIntent extends Action {
        public static final int TAG = 1;
        PendingIntent pendingIntent;

        @Override // android.widget.RemoteViews.Action
        public String getActionName() {
            return "SetOnClickPendingIntent";
        }

        public SetOnClickPendingIntent(int i, PendingIntent pendingIntent) {
            super();
            this.viewId = i;
            this.pendingIntent = pendingIntent;
        }

        public SetOnClickPendingIntent(Parcel parcel) {
            super();
            this.viewId = parcel.readInt();
            if (parcel.readInt() != 0) {
                this.pendingIntent = PendingIntent.readPendingIntentOrNullFromParcel(parcel);
            }
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(1);
            parcel.writeInt(this.viewId);
            parcel.writeInt(this.pendingIntent == null ? 0 : 1);
            PendingIntent pendingIntent = this.pendingIntent;
            if (pendingIntent != null) {
                pendingIntent.writeToParcel(parcel, 0);
            }
        }

        @Override // android.widget.RemoteViews.Action
        public void apply(View view, ViewGroup viewGroup, final OnClickHandler onClickHandler) {
            View viewFindViewById = view.findViewById(this.viewId);
            if (viewFindViewById == null) {
                return;
            }
            if (RemoteViews.this.mIsWidgetCollectionChild) {
                Log.w(RemoteViews.LOG_TAG, "Cannot setOnClickPendingIntent for collection item (id: " + this.viewId + ")");
                ApplicationInfo applicationInfo = view.getContext().getApplicationInfo();
                if (applicationInfo != null && applicationInfo.targetSdkVersion >= 16) {
                    return;
                }
            }
            viewFindViewById.setOnClickListener(this.pendingIntent != null ? new View.OnClickListener() { // from class: android.widget.RemoteViews.SetOnClickPendingIntent.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view2) {
                    Rect sourceBounds = RemoteViews.getSourceBounds(view2);
                    Intent intent = new Intent();
                    intent.setSourceBounds(sourceBounds);
                    onClickHandler.onClickHandler(view2, SetOnClickPendingIntent.this.pendingIntent, intent);
                }
            } : null);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Rect getSourceBounds(View view) {
        float f = view.getContext().getResources().getCompatibilityInfo().applicationScale;
        view.getLocationOnScreen(new int[2]);
        Rect rect = new Rect();
        rect.left = (int) ((r1[0] * f) + 0.5f);
        rect.top = (int) ((r1[1] * f) + 0.5f);
        rect.right = (int) (((r1[0] + view.getWidth()) * f) + 0.5f);
        rect.bottom = (int) (((r1[1] + view.getHeight()) * f) + 0.5f);
        return rect;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:593)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    public Method getMethod(View view, String str, Class<?> cls) {
        Method method;
        Class<?> cls2 = view.getClass();
        synchronized (sMethodsLock) {
            ArrayMap<Class<? extends View>, ArrayMap<MutablePair<String, Class<?>>, Method>> arrayMap = sMethods;
            ArrayMap arrayMap2 = (ArrayMap) arrayMap.get(cls2);
            if (arrayMap2 == null) {
                arrayMap2 = new ArrayMap();
                arrayMap.put(cls2, arrayMap2);
            }
            this.mPair.first = str;
            this.mPair.second = cls;
            method = (Method) arrayMap2.get(this.mPair);
            if (method == null) {
                try {
                    if (cls == 0) {
                        method = cls2.getMethod(str, new Class[0]);
                    } else {
                        method = cls2.getMethod(str, cls);
                    }
                    if (!method.isAnnotationPresent(RemotableViewMethod.class)) {
                        throw new ActionException("view: " + cls2.getName() + " can't use method with RemoteViews: " + str + getParameters(cls));
                    }
                    arrayMap2.put(new MutablePair(str, cls), method);
                } catch (NoSuchMethodException unused) {
                    throw new ActionException("view: " + cls2.getName() + " doesn't have method: " + str + getParameters(cls));
                }
            }
        }
        return method;
    }

    private static String getParameters(Class<?> cls) {
        return cls == null ? "()" : "(" + cls + ")";
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Object[] wrapArg(Object obj) {
        Object[] objArr = sInvokeArgsTls.get();
        objArr[0] = obj;
        return objArr;
    }

    private class SetDrawableParameters extends Action {
        public static final int TAG = 3;
        int alpha;
        int colorFilter;
        PorterDuff.Mode filterMode;
        int level;
        boolean targetBackground;

        @Override // android.widget.RemoteViews.Action
        public String getActionName() {
            return "SetDrawableParameters";
        }

        public SetDrawableParameters(int i, boolean z, int i2, int i3, PorterDuff.Mode mode, int i4) {
            super();
            this.viewId = i;
            this.targetBackground = z;
            this.alpha = i2;
            this.colorFilter = i3;
            this.filterMode = mode;
            this.level = i4;
        }

        public SetDrawableParameters(Parcel parcel) {
            super();
            this.viewId = parcel.readInt();
            this.targetBackground = parcel.readInt() != 0;
            this.alpha = parcel.readInt();
            this.colorFilter = parcel.readInt();
            if (parcel.readInt() != 0) {
                this.filterMode = PorterDuff.Mode.valueOf(parcel.readString());
            } else {
                this.filterMode = null;
            }
            this.level = parcel.readInt();
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(3);
            parcel.writeInt(this.viewId);
            parcel.writeInt(this.targetBackground ? 1 : 0);
            parcel.writeInt(this.alpha);
            parcel.writeInt(this.colorFilter);
            if (this.filterMode != null) {
                parcel.writeInt(1);
                parcel.writeString(this.filterMode.toString());
            } else {
                parcel.writeInt(0);
            }
            parcel.writeInt(this.level);
        }

        @Override // android.widget.RemoteViews.Action
        public void apply(View view, ViewGroup viewGroup, OnClickHandler onClickHandler) {
            PorterDuff.Mode mode;
            View viewFindViewById = view.findViewById(this.viewId);
            if (viewFindViewById == null) {
                return;
            }
            Drawable drawable = null;
            if (this.targetBackground) {
                drawable = viewFindViewById.getBackground();
            } else if (viewFindViewById instanceof ImageView) {
                drawable = ((ImageView) viewFindViewById).getDrawable();
            }
            if (drawable != null) {
                int i = this.alpha;
                if (i != -1) {
                    drawable.setAlpha(i);
                }
                int i2 = this.colorFilter;
                if (i2 != -1 && (mode = this.filterMode) != null) {
                    drawable.setColorFilter(i2, mode);
                }
                int i3 = this.level;
                if (i3 != -1) {
                    drawable.setLevel(i3);
                }
            }
        }
    }

    private final class ReflectionActionWithoutParams extends Action {
        public static final int TAG = 5;
        final String methodName;

        @Override // android.widget.RemoteViews.Action
        public String getActionName() {
            return "ReflectionActionWithoutParams";
        }

        ReflectionActionWithoutParams(int i, String str) {
            super();
            this.viewId = i;
            this.methodName = str;
        }

        ReflectionActionWithoutParams(Parcel parcel) {
            super();
            this.viewId = parcel.readInt();
            this.methodName = parcel.readString();
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(5);
            parcel.writeInt(this.viewId);
            parcel.writeString(this.methodName);
        }

        @Override // android.widget.RemoteViews.Action
        public void apply(View view, ViewGroup viewGroup, OnClickHandler onClickHandler) {
            View viewFindViewById = view.findViewById(this.viewId);
            if (viewFindViewById == null) {
                return;
            }
            try {
                RemoteViews.this.getMethod(viewFindViewById, this.methodName, null).invoke(viewFindViewById, new Object[0]);
            } catch (ActionException e) {
                throw e;
            } catch (Exception e2) {
                throw new ActionException(e2);
            }
        }

        @Override // android.widget.RemoteViews.Action
        public int mergeBehavior() {
            return (this.methodName.equals("showNext") || this.methodName.equals("showPrevious")) ? 2 : 0;
        }
    }

    private static class BitmapCache {
        ArrayList<Bitmap> mBitmaps;

        public BitmapCache() {
            this.mBitmaps = new ArrayList<>();
        }

        public BitmapCache(Parcel parcel) {
            int i = parcel.readInt();
            this.mBitmaps = new ArrayList<>();
            for (int i2 = 0; i2 < i; i2++) {
                this.mBitmaps.add(Bitmap.CREATOR.createFromParcel(parcel));
            }
        }

        public int getBitmapId(Bitmap bitmap) {
            if (bitmap == null) {
                return -1;
            }
            if (this.mBitmaps.contains(bitmap)) {
                return this.mBitmaps.indexOf(bitmap);
            }
            this.mBitmaps.add(bitmap);
            return this.mBitmaps.size() - 1;
        }

        public Bitmap getBitmapForId(int i) {
            if (i == -1 || i >= this.mBitmaps.size()) {
                return null;
            }
            return this.mBitmaps.get(i);
        }

        public void writeBitmapsToParcel(Parcel parcel, int i) {
            int size = this.mBitmaps.size();
            parcel.writeInt(size);
            for (int i2 = 0; i2 < size; i2++) {
                this.mBitmaps.get(i2).writeToParcel(parcel, i);
            }
        }

        public void assimilate(BitmapCache bitmapCache) {
            ArrayList<Bitmap> arrayList = bitmapCache.mBitmaps;
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                Bitmap bitmap = arrayList.get(i);
                if (!this.mBitmaps.contains(bitmap)) {
                    this.mBitmaps.add(bitmap);
                }
            }
        }

        public void addBitmapMemory(MemoryUsageCounter memoryUsageCounter) {
            for (int i = 0; i < this.mBitmaps.size(); i++) {
                memoryUsageCounter.addBitmapMemory(this.mBitmaps.get(i));
            }
        }
    }

    private class BitmapReflectionAction extends Action {
        public static final int TAG = 12;
        Bitmap bitmap;
        int bitmapId;
        String methodName;

        @Override // android.widget.RemoteViews.Action
        public String getActionName() {
            return "BitmapReflectionAction";
        }

        BitmapReflectionAction(int i, String str, Bitmap bitmap) {
            super();
            this.bitmap = bitmap;
            this.viewId = i;
            this.methodName = str;
            this.bitmapId = RemoteViews.this.mBitmapCache.getBitmapId(bitmap);
        }

        BitmapReflectionAction(Parcel parcel) {
            super();
            this.viewId = parcel.readInt();
            this.methodName = parcel.readString();
            this.bitmapId = parcel.readInt();
            this.bitmap = RemoteViews.this.mBitmapCache.getBitmapForId(this.bitmapId);
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(12);
            parcel.writeInt(this.viewId);
            parcel.writeString(this.methodName);
            parcel.writeInt(this.bitmapId);
        }

        @Override // android.widget.RemoteViews.Action
        public void apply(View view, ViewGroup viewGroup, OnClickHandler onClickHandler) throws ActionException {
            RemoteViews.this.new ReflectionAction(this.viewId, this.methodName, 12, this.bitmap).apply(view, viewGroup, onClickHandler);
        }

        @Override // android.widget.RemoteViews.Action
        public void setBitmapCache(BitmapCache bitmapCache) {
            this.bitmapId = bitmapCache.getBitmapId(this.bitmap);
        }
    }

    private final class ReflectionAction extends Action {
        static final int BITMAP = 12;
        static final int BOOLEAN = 1;
        static final int BUNDLE = 13;
        static final int BYTE = 2;
        static final int CHAR = 8;
        static final int CHAR_SEQUENCE = 10;
        static final int DOUBLE = 7;
        static final int FLOAT = 6;
        static final int INT = 4;
        static final int INTENT = 14;
        static final int LONG = 5;
        static final int SHORT = 3;
        static final int STRING = 9;
        static final int TAG = 2;
        static final int URI = 11;
        String methodName;
        int type;
        Object value;

        ReflectionAction(int i, String str, int i2, Object obj) {
            super();
            this.viewId = i;
            this.methodName = str;
            this.type = i2;
            this.value = obj;
        }

        ReflectionAction(Parcel parcel) {
            super();
            this.viewId = parcel.readInt();
            this.methodName = parcel.readString();
            int i = parcel.readInt();
            this.type = i;
            switch (i) {
                case 1:
                    this.value = Boolean.valueOf(parcel.readInt() != 0);
                    break;
                case 2:
                    this.value = Byte.valueOf(parcel.readByte());
                    break;
                case 3:
                    this.value = Short.valueOf((short) parcel.readInt());
                    break;
                case 4:
                    this.value = Integer.valueOf(parcel.readInt());
                    break;
                case 5:
                    this.value = Long.valueOf(parcel.readLong());
                    break;
                case 6:
                    this.value = Float.valueOf(parcel.readFloat());
                    break;
                case 7:
                    this.value = Double.valueOf(parcel.readDouble());
                    break;
                case 8:
                    this.value = Character.valueOf((char) parcel.readInt());
                    break;
                case 9:
                    this.value = parcel.readString();
                    break;
                case 10:
                    this.value = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
                    break;
                case 11:
                    if (parcel.readInt() != 0) {
                        this.value = Uri.CREATOR.createFromParcel(parcel);
                    }
                    break;
                case 12:
                    if (parcel.readInt() != 0) {
                        this.value = Bitmap.CREATOR.createFromParcel(parcel);
                    }
                    break;
                case 13:
                    this.value = parcel.readBundle();
                    break;
                case 14:
                    if (parcel.readInt() != 0) {
                        this.value = Intent.CREATOR.createFromParcel(parcel);
                    }
                    break;
            }
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(2);
            parcel.writeInt(this.viewId);
            parcel.writeString(this.methodName);
            parcel.writeInt(this.type);
            switch (this.type) {
                case 1:
                    parcel.writeInt(((Boolean) this.value).booleanValue() ? 1 : 0);
                    break;
                case 2:
                    parcel.writeByte(((Byte) this.value).byteValue());
                    break;
                case 3:
                    parcel.writeInt(((Short) this.value).shortValue());
                    break;
                case 4:
                    parcel.writeInt(((Integer) this.value).intValue());
                    break;
                case 5:
                    parcel.writeLong(((Long) this.value).longValue());
                    break;
                case 6:
                    parcel.writeFloat(((Float) this.value).floatValue());
                    break;
                case 7:
                    parcel.writeDouble(((Double) this.value).doubleValue());
                    break;
                case 8:
                    parcel.writeInt(((Character) this.value).charValue());
                    break;
                case 9:
                    parcel.writeString((String) this.value);
                    break;
                case 10:
                    TextUtils.writeToParcel((CharSequence) this.value, parcel, i);
                    break;
                case 11:
                    parcel.writeInt(this.value == null ? 0 : 1);
                    Object obj = this.value;
                    if (obj != null) {
                        ((Uri) obj).writeToParcel(parcel, i);
                    }
                    break;
                case 12:
                    parcel.writeInt(this.value == null ? 0 : 1);
                    Object obj2 = this.value;
                    if (obj2 != null) {
                        ((Bitmap) obj2).writeToParcel(parcel, i);
                    }
                    break;
                case 13:
                    parcel.writeBundle((Bundle) this.value);
                    break;
                case 14:
                    parcel.writeInt(this.value == null ? 0 : 1);
                    Object obj3 = this.value;
                    if (obj3 != null) {
                        ((Intent) obj3).writeToParcel(parcel, i);
                    }
                    break;
            }
        }

        private Class<?> getParameterType() {
            switch (this.type) {
                case 1:
                    return Boolean.TYPE;
                case 2:
                    return Byte.TYPE;
                case 3:
                    return Short.TYPE;
                case 4:
                    return Integer.TYPE;
                case 5:
                    return Long.TYPE;
                case 6:
                    return Float.TYPE;
                case 7:
                    return Double.TYPE;
                case 8:
                    return Character.TYPE;
                case 9:
                    return String.class;
                case 10:
                    return CharSequence.class;
                case 11:
                    return Uri.class;
                case 12:
                    return Bitmap.class;
                case 13:
                    return Bundle.class;
                case 14:
                    return Intent.class;
                default:
                    return null;
            }
        }

        @Override // android.widget.RemoteViews.Action
        public void apply(View view, ViewGroup viewGroup, OnClickHandler onClickHandler) {
            View viewFindViewById = view.findViewById(this.viewId);
            if (viewFindViewById == null) {
                return;
            }
            Class<?> parameterType = getParameterType();
            if (parameterType != null) {
                try {
                    RemoteViews.this.getMethod(viewFindViewById, this.methodName, parameterType).invoke(viewFindViewById, RemoteViews.wrapArg(this.value));
                    return;
                } catch (ActionException e) {
                    throw e;
                } catch (Exception e2) {
                    throw new ActionException(e2);
                }
            }
            throw new ActionException("bad type: " + this.type);
        }

        @Override // android.widget.RemoteViews.Action
        public int mergeBehavior() {
            return this.methodName.equals("smoothScrollBy") ? 1 : 0;
        }

        @Override // android.widget.RemoteViews.Action
        public String getActionName() {
            return "ReflectionAction" + this.methodName + this.type;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void configureRemoteViewsAsChild(RemoteViews remoteViews) {
        this.mBitmapCache.assimilate(remoteViews.mBitmapCache);
        remoteViews.setBitmapCache(this.mBitmapCache);
        remoteViews.setNotRoot();
    }

    void setNotRoot() {
        this.mIsRoot = false;
    }

    private class ViewGroupAction extends Action {
        public static final int TAG = 4;
        RemoteViews nestedViews;

        @Override // android.widget.RemoteViews.Action
        public int mergeBehavior() {
            return 1;
        }

        public ViewGroupAction(int i, RemoteViews remoteViews) {
            super();
            this.viewId = i;
            this.nestedViews = remoteViews;
            if (remoteViews != null) {
                RemoteViews.this.configureRemoteViewsAsChild(remoteViews);
            }
        }

        /* JADX WARN: Illegal instructions before constructor call */
        public ViewGroupAction(Parcel parcel, BitmapCache bitmapCache) {
            super();
            this.viewId = parcel.readInt();
            if (!(parcel.readInt() == 0)) {
                this.nestedViews = new RemoteViews(parcel, bitmapCache);
            } else {
                this.nestedViews = null;
            }
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(4);
            parcel.writeInt(this.viewId);
            if (this.nestedViews != null) {
                parcel.writeInt(1);
                this.nestedViews.writeToParcel(parcel, i);
            } else {
                parcel.writeInt(0);
            }
        }

        @Override // android.widget.RemoteViews.Action
        public void apply(View view, ViewGroup viewGroup, OnClickHandler onClickHandler) {
            Context context = view.getContext();
            ViewGroup viewGroup2 = (ViewGroup) view.findViewById(this.viewId);
            if (viewGroup2 == null) {
                return;
            }
            RemoteViews remoteViews = this.nestedViews;
            if (remoteViews != null) {
                viewGroup2.addView(remoteViews.apply(context, viewGroup2, onClickHandler));
            } else {
                viewGroup2.removeAllViews();
            }
        }

        @Override // android.widget.RemoteViews.Action
        public void updateMemoryUsageEstimate(MemoryUsageCounter memoryUsageCounter) {
            RemoteViews remoteViews = this.nestedViews;
            if (remoteViews != null) {
                memoryUsageCounter.increment(remoteViews.estimateMemoryUsage());
            }
        }

        @Override // android.widget.RemoteViews.Action
        public void setBitmapCache(BitmapCache bitmapCache) {
            RemoteViews remoteViews = this.nestedViews;
            if (remoteViews != null) {
                remoteViews.setBitmapCache(bitmapCache);
            }
        }

        @Override // android.widget.RemoteViews.Action
        public String getActionName() {
            return "ViewGroupAction" + (this.nestedViews == null ? "Remove" : MSVSSConstants.COMMAND_ADD);
        }
    }

    private class TextViewDrawableAction extends Action {
        public static final int TAG = 11;
        int d1;
        int d2;
        int d3;
        int d4;
        boolean isRelative;

        @Override // android.widget.RemoteViews.Action
        public String getActionName() {
            return "TextViewDrawableAction";
        }

        public TextViewDrawableAction(int i, boolean z, int i2, int i3, int i4, int i5) {
            super();
            this.isRelative = false;
            this.viewId = i;
            this.isRelative = z;
            this.d1 = i2;
            this.d2 = i3;
            this.d3 = i4;
            this.d4 = i5;
        }

        public TextViewDrawableAction(Parcel parcel) {
            super();
            this.isRelative = false;
            this.viewId = parcel.readInt();
            this.isRelative = parcel.readInt() != 0;
            this.d1 = parcel.readInt();
            this.d2 = parcel.readInt();
            this.d3 = parcel.readInt();
            this.d4 = parcel.readInt();
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(11);
            parcel.writeInt(this.viewId);
            parcel.writeInt(this.isRelative ? 1 : 0);
            parcel.writeInt(this.d1);
            parcel.writeInt(this.d2);
            parcel.writeInt(this.d3);
            parcel.writeInt(this.d4);
        }

        @Override // android.widget.RemoteViews.Action
        public void apply(View view, ViewGroup viewGroup, OnClickHandler onClickHandler) {
            TextView textView = (TextView) view.findViewById(this.viewId);
            if (textView == null) {
                return;
            }
            if (this.isRelative) {
                textView.setCompoundDrawablesRelativeWithIntrinsicBounds(this.d1, this.d2, this.d3, this.d4);
            } else {
                textView.setCompoundDrawablesWithIntrinsicBounds(this.d1, this.d2, this.d3, this.d4);
            }
        }
    }

    private class TextViewSizeAction extends Action {
        public static final int TAG = 13;
        float size;
        int units;

        @Override // android.widget.RemoteViews.Action
        public String getActionName() {
            return "TextViewSizeAction";
        }

        public TextViewSizeAction(int i, int i2, float f) {
            super();
            this.viewId = i;
            this.units = i2;
            this.size = f;
        }

        public TextViewSizeAction(Parcel parcel) {
            super();
            this.viewId = parcel.readInt();
            this.units = parcel.readInt();
            this.size = parcel.readFloat();
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(13);
            parcel.writeInt(this.viewId);
            parcel.writeInt(this.units);
            parcel.writeFloat(this.size);
        }

        @Override // android.widget.RemoteViews.Action
        public void apply(View view, ViewGroup viewGroup, OnClickHandler onClickHandler) {
            TextView textView = (TextView) view.findViewById(this.viewId);
            if (textView == null) {
                return;
            }
            textView.setTextSize(this.units, this.size);
        }
    }

    private class ViewPaddingAction extends Action {
        public static final int TAG = 14;
        int bottom;
        int left;
        int right;
        int top;

        @Override // android.widget.RemoteViews.Action
        public String getActionName() {
            return "ViewPaddingAction";
        }

        public ViewPaddingAction(int i, int i2, int i3, int i4, int i5) {
            super();
            this.viewId = i;
            this.left = i2;
            this.top = i3;
            this.right = i4;
            this.bottom = i5;
        }

        public ViewPaddingAction(Parcel parcel) {
            super();
            this.viewId = parcel.readInt();
            this.left = parcel.readInt();
            this.top = parcel.readInt();
            this.right = parcel.readInt();
            this.bottom = parcel.readInt();
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(14);
            parcel.writeInt(this.viewId);
            parcel.writeInt(this.left);
            parcel.writeInt(this.top);
            parcel.writeInt(this.right);
            parcel.writeInt(this.bottom);
        }

        @Override // android.widget.RemoteViews.Action
        public void apply(View view, ViewGroup viewGroup, OnClickHandler onClickHandler) {
            View viewFindViewById = view.findViewById(this.viewId);
            if (viewFindViewById == null) {
                return;
            }
            viewFindViewById.setPadding(this.left, this.top, this.right, this.bottom);
        }
    }

    private class MemoryUsageCounter {
        int mMemoryUsage;

        private MemoryUsageCounter() {
        }

        public void clear() {
            this.mMemoryUsage = 0;
        }

        public void increment(int i) {
            this.mMemoryUsage += i;
        }

        public int getMemoryUsage() {
            return this.mMemoryUsage;
        }

        /* JADX WARN: Removed duplicated region for block: B:11:0x001b  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void addBitmapMemory(android.graphics.Bitmap r6) {
            /*
                r5 = this;
                android.graphics.Bitmap$Config r0 = r6.getConfig()
                r1 = 2
                r2 = 1
                r3 = 4
                if (r0 == 0) goto L1b
                int[] r4 = android.widget.RemoteViews.AnonymousClass3.$SwitchMap$android$graphics$Bitmap$Config
                int r0 = r0.ordinal()
                r0 = r4[r0]
                if (r0 == r2) goto L19
                if (r0 == r1) goto L1c
                r2 = 3
                if (r0 == r2) goto L1c
                goto L1b
            L19:
                r1 = r2
                goto L1c
            L1b:
                r1 = r3
            L1c:
                int r0 = r6.getWidth()
                int r6 = r6.getHeight()
                int r0 = r0 * r6
                int r0 = r0 * r1
                r5.increment(r0)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.RemoteViews.MemoryUsageCounter.addBitmapMemory(android.graphics.Bitmap):void");
        }
    }

    /* JADX INFO: renamed from: android.widget.RemoteViews$3, reason: invalid class name */
    static /* synthetic */ class AnonymousClass3 {
        static final /* synthetic */ int[] $SwitchMap$android$graphics$Bitmap$Config;

        static {
            int[] iArr = new int[Bitmap.Config.values().length];
            $SwitchMap$android$graphics$Bitmap$Config = iArr;
            try {
                iArr[Bitmap.Config.ALPHA_8.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$android$graphics$Bitmap$Config[Bitmap.Config.RGB_565.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$android$graphics$Bitmap$Config[Bitmap.Config.ARGB_4444.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$android$graphics$Bitmap$Config[Bitmap.Config.ARGB_8888.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
        }
    }

    public RemoteViews(String str, int i) {
        this.mUser = Process.myUserHandle();
        this.mIsRoot = true;
        this.mLandscape = null;
        this.mPortrait = null;
        this.mIsWidgetCollectionChild = false;
        this.mPair = new MutablePair<>(null, null);
        this.mPackage = str;
        this.mLayoutId = i;
        this.mBitmapCache = new BitmapCache();
        this.mMemoryUsageCounter = new MemoryUsageCounter();
        recalculateMemoryUsage();
    }

    public void setUser(UserHandle userHandle) {
        this.mUser = userHandle;
    }

    private boolean hasLandscapeAndPortraitLayouts() {
        return (this.mLandscape == null || this.mPortrait == null) ? false : true;
    }

    public RemoteViews(RemoteViews remoteViews, RemoteViews remoteViews2) {
        this.mUser = Process.myUserHandle();
        this.mIsRoot = true;
        this.mLandscape = null;
        this.mPortrait = null;
        this.mIsWidgetCollectionChild = false;
        this.mPair = new MutablePair<>(null, null);
        if (remoteViews == null || remoteViews2 == null) {
            throw new RuntimeException("Both RemoteViews must be non-null");
        }
        if (remoteViews.getPackage().compareTo(remoteViews2.getPackage()) != 0) {
            throw new RuntimeException("Both RemoteViews must share the same package");
        }
        this.mPackage = remoteViews2.getPackage();
        this.mLayoutId = remoteViews2.getLayoutId();
        this.mLandscape = remoteViews;
        this.mPortrait = remoteViews2;
        this.mMemoryUsageCounter = new MemoryUsageCounter();
        this.mBitmapCache = new BitmapCache();
        configureRemoteViewsAsChild(remoteViews);
        configureRemoteViewsAsChild(remoteViews2);
        recalculateMemoryUsage();
    }

    public RemoteViews(Parcel parcel) {
        this(parcel, (BitmapCache) null);
    }

    private RemoteViews(Parcel parcel, BitmapCache bitmapCache) {
        this.mUser = Process.myUserHandle();
        this.mIsRoot = true;
        this.mLandscape = null;
        this.mPortrait = null;
        this.mIsWidgetCollectionChild = false;
        this.mPair = new MutablePair<>(null, null);
        int i = parcel.readInt();
        if (bitmapCache == null) {
            this.mBitmapCache = new BitmapCache(parcel);
        } else {
            setBitmapCache(bitmapCache);
            setNotRoot();
        }
        if (i == 0) {
            this.mPackage = parcel.readString();
            this.mLayoutId = parcel.readInt();
            this.mIsWidgetCollectionChild = parcel.readInt() == 1;
            int i2 = parcel.readInt();
            if (i2 > 0) {
                this.mActions = new ArrayList<>(i2);
                for (int i3 = 0; i3 < i2; i3++) {
                    int i4 = parcel.readInt();
                    switch (i4) {
                        case 1:
                            this.mActions.add(new SetOnClickPendingIntent(parcel));
                            break;
                        case 2:
                            this.mActions.add(new ReflectionAction(parcel));
                            break;
                        case 3:
                            this.mActions.add(new SetDrawableParameters(parcel));
                            break;
                        case 4:
                            this.mActions.add(new ViewGroupAction(parcel, this.mBitmapCache));
                            break;
                        case 5:
                            this.mActions.add(new ReflectionActionWithoutParams(parcel));
                            break;
                        case 6:
                            this.mActions.add(new SetEmptyView(parcel));
                            break;
                        case 7:
                        default:
                            throw new ActionException("Tag " + i4 + " not found");
                        case 8:
                            this.mActions.add(new SetPendingIntentTemplate(parcel));
                            break;
                        case 9:
                            this.mActions.add(new SetOnClickFillInIntent(parcel));
                            break;
                        case 10:
                            this.mActions.add(new SetRemoteViewsAdapterIntent(parcel));
                            break;
                        case 11:
                            this.mActions.add(new TextViewDrawableAction(parcel));
                            break;
                        case 12:
                            this.mActions.add(new BitmapReflectionAction(parcel));
                            break;
                        case 13:
                            this.mActions.add(new TextViewSizeAction(parcel));
                            break;
                        case 14:
                            this.mActions.add(new ViewPaddingAction(parcel));
                            break;
                        case 15:
                            this.mActions.add(new SetRemoteViewsAdapterList(parcel));
                            break;
                    }
                }
            }
        } else {
            this.mLandscape = new RemoteViews(parcel, this.mBitmapCache);
            RemoteViews remoteViews = new RemoteViews(parcel, this.mBitmapCache);
            this.mPortrait = remoteViews;
            this.mPackage = remoteViews.getPackage();
            this.mLayoutId = this.mPortrait.getLayoutId();
        }
        this.mMemoryUsageCounter = new MemoryUsageCounter();
        recalculateMemoryUsage();
    }

    /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
    public RemoteViews m22clone() {
        Parcel parcelObtain = Parcel.obtain();
        writeToParcel(parcelObtain, 0);
        parcelObtain.setDataPosition(0);
        return new RemoteViews(parcelObtain);
    }

    public String getPackage() {
        return this.mPackage;
    }

    public int getLayoutId() {
        return this.mLayoutId;
    }

    void setIsWidgetCollectionChild(boolean z) {
        this.mIsWidgetCollectionChild = z;
    }

    private void recalculateMemoryUsage() {
        this.mMemoryUsageCounter.clear();
        if (!hasLandscapeAndPortraitLayouts()) {
            ArrayList<Action> arrayList = this.mActions;
            if (arrayList != null) {
                int size = arrayList.size();
                for (int i = 0; i < size; i++) {
                    this.mActions.get(i).updateMemoryUsageEstimate(this.mMemoryUsageCounter);
                }
            }
            if (this.mIsRoot) {
                this.mBitmapCache.addBitmapMemory(this.mMemoryUsageCounter);
                return;
            }
            return;
        }
        this.mMemoryUsageCounter.increment(this.mLandscape.estimateMemoryUsage());
        this.mMemoryUsageCounter.increment(this.mPortrait.estimateMemoryUsage());
        this.mBitmapCache.addBitmapMemory(this.mMemoryUsageCounter);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setBitmapCache(BitmapCache bitmapCache) {
        this.mBitmapCache = bitmapCache;
        if (!hasLandscapeAndPortraitLayouts()) {
            ArrayList<Action> arrayList = this.mActions;
            if (arrayList != null) {
                int size = arrayList.size();
                for (int i = 0; i < size; i++) {
                    this.mActions.get(i).setBitmapCache(bitmapCache);
                }
                return;
            }
            return;
        }
        this.mLandscape.setBitmapCache(bitmapCache);
        this.mPortrait.setBitmapCache(bitmapCache);
    }

    public int estimateMemoryUsage() {
        return this.mMemoryUsageCounter.getMemoryUsage();
    }

    private void addAction(Action action) {
        if (hasLandscapeAndPortraitLayouts()) {
            throw new RuntimeException("RemoteViews specifying separate landscape and portrait layouts cannot be modified. Instead, fully configure the landscape and portrait layouts individually before constructing the combined layout.");
        }
        if (this.mActions == null) {
            this.mActions = new ArrayList<>();
        }
        this.mActions.add(action);
        action.updateMemoryUsageEstimate(this.mMemoryUsageCounter);
    }

    public void addView(int i, RemoteViews remoteViews) {
        addAction(new ViewGroupAction(i, remoteViews));
    }

    public void removeAllViews(int i) {
        addAction(new ViewGroupAction(i, (RemoteViews) null));
    }

    public void showNext(int i) {
        addAction(new ReflectionActionWithoutParams(i, "showNext"));
    }

    public void showPrevious(int i) {
        addAction(new ReflectionActionWithoutParams(i, "showPrevious"));
    }

    public void setDisplayedChild(int i, int i2) {
        setInt(i, "setDisplayedChild", i2);
    }

    public void setViewVisibility(int i, int i2) {
        setInt(i, "setVisibility", i2);
    }

    public void setTextViewText(int i, CharSequence charSequence) {
        setCharSequence(i, "setText", charSequence);
    }

    public void setTextViewTextSize(int i, int i2, float f) {
        addAction(new TextViewSizeAction(i, i2, f));
    }

    public void setTextViewCompoundDrawables(int i, int i2, int i3, int i4, int i5) {
        addAction(new TextViewDrawableAction(i, false, i2, i3, i4, i5));
    }

    public void setTextViewCompoundDrawablesRelative(int i, int i2, int i3, int i4, int i5) {
        addAction(new TextViewDrawableAction(i, true, i2, i3, i4, i5));
    }

    public void setImageViewResource(int i, int i2) {
        setInt(i, "setImageResource", i2);
    }

    public void setImageViewUri(int i, Uri uri) {
        setUri(i, "setImageURI", uri);
    }

    public void setImageViewBitmap(int i, Bitmap bitmap) {
        setBitmap(i, "setImageBitmap", bitmap);
    }

    public void setEmptyView(int i, int i2) {
        addAction(new SetEmptyView(i, i2));
    }

    public void setChronometer(int i, long j, String str, boolean z) {
        setLong(i, "setBase", j);
        setString(i, "setFormat", str);
        setBoolean(i, "setStarted", z);
    }

    public void setProgressBar(int i, int i2, int i3, boolean z) {
        setBoolean(i, "setIndeterminate", z);
        if (z) {
            return;
        }
        setInt(i, "setMax", i2);
        setInt(i, "setProgress", i3);
    }

    public void setOnClickPendingIntent(int i, PendingIntent pendingIntent) {
        addAction(new SetOnClickPendingIntent(i, pendingIntent));
    }

    public void setPendingIntentTemplate(int i, PendingIntent pendingIntent) {
        addAction(new SetPendingIntentTemplate(i, pendingIntent));
    }

    public void setOnClickFillInIntent(int i, Intent intent) {
        addAction(new SetOnClickFillInIntent(i, intent));
    }

    public void setDrawableParameters(int i, boolean z, int i2, int i3, PorterDuff.Mode mode, int i4) {
        addAction(new SetDrawableParameters(i, z, i2, i3, mode, i4));
    }

    public void setTextColor(int i, int i2) {
        setInt(i, "setTextColor", i2);
    }

    @Deprecated
    public void setRemoteAdapter(int i, int i2, Intent intent) {
        setRemoteAdapter(i2, intent);
    }

    public void setRemoteAdapter(int i, Intent intent) {
        addAction(new SetRemoteViewsAdapterIntent(i, intent));
    }

    public void setRemoteAdapter(int i, ArrayList<RemoteViews> arrayList, int i2) {
        addAction(new SetRemoteViewsAdapterList(i, arrayList, i2));
    }

    public void setScrollPosition(int i, int i2) {
        setInt(i, "smoothScrollToPosition", i2);
    }

    public void setRelativeScrollPosition(int i, int i2) {
        setInt(i, "smoothScrollByOffset", i2);
    }

    public void setViewPadding(int i, int i2, int i3, int i4, int i5) {
        addAction(new ViewPaddingAction(i, i2, i3, i4, i5));
    }

    public void setBoolean(int i, String str, boolean z) {
        addAction(new ReflectionAction(i, str, 1, Boolean.valueOf(z)));
    }

    public void setByte(int i, String str, byte b) {
        addAction(new ReflectionAction(i, str, 2, Byte.valueOf(b)));
    }

    public void setShort(int i, String str, short s) {
        addAction(new ReflectionAction(i, str, 3, Short.valueOf(s)));
    }

    public void setInt(int i, String str, int i2) {
        addAction(new ReflectionAction(i, str, 4, Integer.valueOf(i2)));
    }

    public void setLong(int i, String str, long j) {
        addAction(new ReflectionAction(i, str, 5, Long.valueOf(j)));
    }

    public void setFloat(int i, String str, float f) {
        addAction(new ReflectionAction(i, str, 6, Float.valueOf(f)));
    }

    public void setDouble(int i, String str, double d) {
        addAction(new ReflectionAction(i, str, 7, Double.valueOf(d)));
    }

    public void setChar(int i, String str, char c) {
        addAction(new ReflectionAction(i, str, 8, Character.valueOf(c)));
    }

    public void setString(int i, String str, String str2) {
        addAction(new ReflectionAction(i, str, 9, str2));
    }

    public void setCharSequence(int i, String str, CharSequence charSequence) {
        addAction(new ReflectionAction(i, str, 10, charSequence));
    }

    public void setUri(int i, String str, Uri uri) {
        if (uri != null) {
            uri = uri.getCanonicalUri();
            if (StrictMode.vmFileUriExposureEnabled()) {
                uri.checkFileUriExposed("RemoteViews.setUri()");
            }
        }
        addAction(new ReflectionAction(i, str, 11, uri));
    }

    public void setBitmap(int i, String str, Bitmap bitmap) {
        addAction(new BitmapReflectionAction(i, str, bitmap));
    }

    public void setBundle(int i, String str, Bundle bundle) {
        addAction(new ReflectionAction(i, str, 13, bundle));
    }

    public void setIntent(int i, String str, Intent intent) {
        addAction(new ReflectionAction(i, str, 14, intent));
    }

    public void setContentDescription(int i, CharSequence charSequence) {
        setCharSequence(i, "setContentDescription", charSequence);
    }

    public void setLabelFor(int i, int i2) {
        setInt(i, "setLabelFor", i2);
    }

    private RemoteViews getRemoteViewsToApply(Context context) {
        if (!hasLandscapeAndPortraitLayouts()) {
            return this;
        }
        if (context.getResources().getConfiguration().orientation == 2) {
            return this.mLandscape;
        }
        return this.mPortrait;
    }

    public View apply(Context context, ViewGroup viewGroup) {
        return apply(context, viewGroup, null);
    }

    public View apply(Context context, ViewGroup viewGroup, OnClickHandler onClickHandler) {
        RemoteViews remoteViewsToApply = getRemoteViewsToApply(context);
        Context contextPrepareContext = prepareContext(context);
        LayoutInflater layoutInflaterCloneInContext = ((LayoutInflater) contextPrepareContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).cloneInContext(contextPrepareContext);
        layoutInflaterCloneInContext.setFilter(this);
        View viewInflate = layoutInflaterCloneInContext.inflate(remoteViewsToApply.getLayoutId(), viewGroup, false);
        remoteViewsToApply.performApply(viewInflate, viewGroup, onClickHandler);
        return viewInflate;
    }

    public void reapply(Context context, View view) {
        reapply(context, view, null);
    }

    public void reapply(Context context, View view, OnClickHandler onClickHandler) {
        RemoteViews remoteViewsToApply = getRemoteViewsToApply(context);
        if (hasLandscapeAndPortraitLayouts() && view.getId() != remoteViewsToApply.getLayoutId()) {
            throw new RuntimeException("Attempting to re-apply RemoteViews to a view that that does not share the same root layout id.");
        }
        prepareContext(context);
        remoteViewsToApply.performApply(view, (ViewGroup) view.getParent(), onClickHandler);
    }

    private void performApply(View view, ViewGroup viewGroup, OnClickHandler onClickHandler) {
        ArrayList<Action> arrayList = this.mActions;
        if (arrayList != null) {
            if (onClickHandler == null) {
                onClickHandler = DEFAULT_ON_CLICK_HANDLER;
            }
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                this.mActions.get(i).apply(view, viewGroup, onClickHandler);
            }
        }
    }

    private Context prepareContext(Context context) {
        String str = this.mPackage;
        if (str == null) {
            return context;
        }
        try {
            return context.createPackageContextAsUser(str, 4, this.mUser);
        } catch (PackageManager.NameNotFoundException unused) {
            Log.e(LOG_TAG, "Package name " + str + " not found");
            return context;
        }
    }

    @Override // android.view.LayoutInflater.Filter
    public boolean onLoadClass(Class cls) {
        return cls.isAnnotationPresent(RemoteView.class);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        if (!hasLandscapeAndPortraitLayouts()) {
            parcel.writeInt(0);
            if (this.mIsRoot) {
                this.mBitmapCache.writeBitmapsToParcel(parcel, i);
            }
            parcel.writeString(this.mPackage);
            parcel.writeInt(this.mLayoutId);
            parcel.writeInt(this.mIsWidgetCollectionChild ? 1 : 0);
            ArrayList<Action> arrayList = this.mActions;
            int size = arrayList != null ? arrayList.size() : 0;
            parcel.writeInt(size);
            for (int i2 = 0; i2 < size; i2++) {
                this.mActions.get(i2).writeToParcel(parcel, 0);
            }
            return;
        }
        parcel.writeInt(1);
        if (this.mIsRoot) {
            this.mBitmapCache.writeBitmapsToParcel(parcel, i);
        }
        this.mLandscape.writeToParcel(parcel, i);
        this.mPortrait.writeToParcel(parcel, i);
    }
}
