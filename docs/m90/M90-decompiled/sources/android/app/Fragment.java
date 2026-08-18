package android.app;

import android.animation.Animator;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AndroidRuntimeException;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.DebugUtils;
import android.util.SparseArray;
import android.util.SuperNotCalledException;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/* JADX INFO: loaded from: classes.dex */
public class Fragment implements ComponentCallbacks2, View.OnCreateContextMenuListener {
    static final int ACTIVITY_CREATED = 2;
    static final int CREATED = 1;
    static final int INITIALIZING = 0;
    static final int INVALID_STATE = -1;
    static final int RESUMED = 5;
    static final int STARTED = 4;
    static final int STOPPED = 3;
    private static final ArrayMap<String, Class<?>> sClassMap = new ArrayMap<>();
    Activity mActivity;
    boolean mAdded;
    Animator mAnimatingAway;
    Bundle mArguments;
    int mBackStackNesting;
    boolean mCalled;
    boolean mCheckedForLoaderManager;
    FragmentManagerImpl mChildFragmentManager;
    ViewGroup mContainer;
    int mContainerId;
    boolean mDeferStart;
    boolean mDetached;
    int mFragmentId;
    FragmentManagerImpl mFragmentManager;
    boolean mFromLayout;
    boolean mHasMenu;
    boolean mHidden;
    boolean mInLayout;
    LoaderManagerImpl mLoaderManager;
    boolean mLoadersStarted;
    int mNextAnim;
    Fragment mParentFragment;
    boolean mRemoving;
    boolean mRestored;
    boolean mResumed;
    boolean mRetainInstance;
    boolean mRetaining;
    Bundle mSavedFragmentState;
    SparseArray<Parcelable> mSavedViewState;
    int mStateAfterAnimating;
    String mTag;
    Fragment mTarget;
    int mTargetRequestCode;
    View mView;
    String mWho;
    int mState = 0;
    int mIndex = -1;
    int mTargetIndex = -1;
    boolean mMenuVisible = true;
    boolean mUserVisibleHint = true;

    public void onActivityResult(int i, int i2, Intent intent) {
    }

    public boolean onContextItemSelected(MenuItem menuItem) {
        return false;
    }

    public Animator onCreateAnimator(int i, boolean z, int i2) {
        return null;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return null;
    }

    public void onDestroyOptionsMenu() {
    }

    public void onHiddenChanged(boolean z) {
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        return false;
    }

    public void onOptionsMenuClosed(Menu menu) {
    }

    public void onPrepareOptionsMenu(Menu menu) {
    }

    public void onSaveInstanceState(Bundle bundle) {
    }

    public void onViewCreated(View view, Bundle bundle) {
    }

    public static class SavedState implements Parcelable {
        public static final Parcelable.ClassLoaderCreator<SavedState> CREATOR = new Parcelable.ClassLoaderCreator<SavedState>() { // from class: android.app.Fragment.SavedState.1
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel, null);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.ClassLoaderCreator
            public SavedState createFromParcel(Parcel parcel, ClassLoader classLoader) {
                return new SavedState(parcel, classLoader);
            }

            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        final Bundle mState;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        SavedState(Bundle bundle) {
            this.mState = bundle;
        }

        SavedState(Parcel parcel, ClassLoader classLoader) {
            Bundle bundle = parcel.readBundle();
            this.mState = bundle;
            if (classLoader == null || bundle == null) {
                return;
            }
            bundle.setClassLoader(classLoader);
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeBundle(this.mState);
        }
    }

    public static class InstantiationException extends AndroidRuntimeException {
        public InstantiationException(String str, Exception exc) {
            super(str, exc);
        }
    }

    public static Fragment instantiate(Context context, String str) {
        return instantiate(context, str, null);
    }

    public static Fragment instantiate(Context context, String str, Bundle bundle) {
        try {
            ArrayMap<String, Class<?>> arrayMap = sClassMap;
            Class<?> clsLoadClass = arrayMap.get(str);
            if (clsLoadClass == null) {
                clsLoadClass = context.getClassLoader().loadClass(str);
                if (!Fragment.class.isAssignableFrom(clsLoadClass)) {
                    throw new InstantiationException("Trying to instantiate a class " + str + " that is not a Fragment", new ClassCastException());
                }
                arrayMap.put(str, clsLoadClass);
            }
            Fragment fragment = (Fragment) clsLoadClass.newInstance();
            if (bundle != null) {
                bundle.setClassLoader(fragment.getClass().getClassLoader());
                fragment.mArguments = bundle;
            }
            return fragment;
        } catch (ClassNotFoundException e) {
            throw new InstantiationException("Unable to instantiate fragment " + str + ": make sure class name exists, is public, and has an empty constructor that is public", e);
        } catch (IllegalAccessException e2) {
            throw new InstantiationException("Unable to instantiate fragment " + str + ": make sure class name exists, is public, and has an empty constructor that is public", e2);
        } catch (java.lang.InstantiationException e3) {
            throw new InstantiationException("Unable to instantiate fragment " + str + ": make sure class name exists, is public, and has an empty constructor that is public", e3);
        }
    }

    final void restoreViewState(Bundle bundle) {
        SparseArray<Parcelable> sparseArray = this.mSavedViewState;
        if (sparseArray != null) {
            this.mView.restoreHierarchyState(sparseArray);
            this.mSavedViewState = null;
        }
        this.mCalled = false;
        onViewStateRestored(bundle);
        if (!this.mCalled) {
            throw new SuperNotCalledException("Fragment " + this + " did not call through to super.onViewStateRestored()");
        }
    }

    final void setIndex(int i, Fragment fragment) {
        this.mIndex = i;
        if (fragment != null) {
            this.mWho = fragment.mWho + ":" + this.mIndex;
        } else {
            this.mWho = "android:fragment:" + this.mIndex;
        }
    }

    final boolean isInBackStack() {
        return this.mBackStackNesting > 0;
    }

    public final boolean equals(Object obj) {
        return super.equals(obj);
    }

    public final int hashCode() {
        return super.hashCode();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        DebugUtils.buildShortClassTag(this, sb);
        if (this.mIndex >= 0) {
            sb.append(" #");
            sb.append(this.mIndex);
        }
        if (this.mFragmentId != 0) {
            sb.append(" id=0x");
            sb.append(Integer.toHexString(this.mFragmentId));
        }
        if (this.mTag != null) {
            sb.append(" ");
            sb.append(this.mTag);
        }
        sb.append('}');
        return sb.toString();
    }

    public final int getId() {
        return this.mFragmentId;
    }

    public final String getTag() {
        return this.mTag;
    }

    public void setArguments(Bundle bundle) {
        if (this.mIndex >= 0) {
            throw new IllegalStateException("Fragment already active");
        }
        this.mArguments = bundle;
    }

    public final Bundle getArguments() {
        return this.mArguments;
    }

    public void setInitialSavedState(SavedState savedState) {
        if (this.mIndex >= 0) {
            throw new IllegalStateException("Fragment already active");
        }
        this.mSavedFragmentState = (savedState == null || savedState.mState == null) ? null : savedState.mState;
    }

    public void setTargetFragment(Fragment fragment, int i) {
        this.mTarget = fragment;
        this.mTargetRequestCode = i;
    }

    public final Fragment getTargetFragment() {
        return this.mTarget;
    }

    public final int getTargetRequestCode() {
        return this.mTargetRequestCode;
    }

    public final Activity getActivity() {
        return this.mActivity;
    }

    public final Resources getResources() {
        Activity activity = this.mActivity;
        if (activity == null) {
            throw new IllegalStateException("Fragment " + this + " not attached to Activity");
        }
        return activity.getResources();
    }

    public final CharSequence getText(int i) {
        return getResources().getText(i);
    }

    public final String getString(int i) {
        return getResources().getString(i);
    }

    public final String getString(int i, Object... objArr) {
        return getResources().getString(i, objArr);
    }

    public final FragmentManager getFragmentManager() {
        return this.mFragmentManager;
    }

    public final FragmentManager getChildFragmentManager() {
        if (this.mChildFragmentManager == null) {
            instantiateChildFragmentManager();
            int i = this.mState;
            if (i >= 5) {
                this.mChildFragmentManager.dispatchResume();
            } else if (i >= 4) {
                this.mChildFragmentManager.dispatchStart();
            } else if (i >= 2) {
                this.mChildFragmentManager.dispatchActivityCreated();
            } else if (i >= 1) {
                this.mChildFragmentManager.dispatchCreate();
            }
        }
        return this.mChildFragmentManager;
    }

    public final Fragment getParentFragment() {
        return this.mParentFragment;
    }

    public final boolean isAdded() {
        return this.mActivity != null && this.mAdded;
    }

    public final boolean isDetached() {
        return this.mDetached;
    }

    public final boolean isRemoving() {
        return this.mRemoving;
    }

    public final boolean isInLayout() {
        return this.mInLayout;
    }

    public final boolean isResumed() {
        return this.mResumed;
    }

    public final boolean isVisible() {
        View view;
        return (!isAdded() || isHidden() || (view = this.mView) == null || view.getWindowToken() == null || this.mView.getVisibility() != 0) ? false : true;
    }

    public final boolean isHidden() {
        return this.mHidden;
    }

    public void setRetainInstance(boolean z) {
        if (z && this.mParentFragment != null) {
            throw new IllegalStateException("Can't retain fragements that are nested in other fragments");
        }
        this.mRetainInstance = z;
    }

    public final boolean getRetainInstance() {
        return this.mRetainInstance;
    }

    public void setHasOptionsMenu(boolean z) {
        if (this.mHasMenu != z) {
            this.mHasMenu = z;
            if (!isAdded() || isHidden()) {
                return;
            }
            this.mFragmentManager.invalidateOptionsMenu();
        }
    }

    public void setMenuVisibility(boolean z) {
        if (this.mMenuVisible != z) {
            this.mMenuVisible = z;
            if (this.mHasMenu && isAdded() && !isHidden()) {
                this.mFragmentManager.invalidateOptionsMenu();
            }
        }
    }

    public void setUserVisibleHint(boolean z) {
        if (!this.mUserVisibleHint && z && this.mState < 4) {
            this.mFragmentManager.performPendingDeferredStart(this);
        }
        this.mUserVisibleHint = z;
        this.mDeferStart = !z;
    }

    public boolean getUserVisibleHint() {
        return this.mUserVisibleHint;
    }

    public LoaderManager getLoaderManager() {
        LoaderManagerImpl loaderManagerImpl = this.mLoaderManager;
        if (loaderManagerImpl != null) {
            return loaderManagerImpl;
        }
        Activity activity = this.mActivity;
        if (activity == null) {
            throw new IllegalStateException("Fragment " + this + " not attached to Activity");
        }
        this.mCheckedForLoaderManager = true;
        LoaderManagerImpl loaderManager = activity.getLoaderManager(this.mWho, this.mLoadersStarted, true);
        this.mLoaderManager = loaderManager;
        return loaderManager;
    }

    public void startActivity(Intent intent) {
        startActivity(intent, null);
    }

    public void startActivity(Intent intent, Bundle bundle) {
        Activity activity = this.mActivity;
        if (activity == null) {
            throw new IllegalStateException("Fragment " + this + " not attached to Activity");
        }
        if (bundle != null) {
            activity.startActivityFromFragment(this, intent, -1, bundle);
        } else {
            activity.startActivityFromFragment(this, intent, -1);
        }
    }

    public void startActivityForResult(Intent intent, int i) {
        startActivityForResult(intent, i, null);
    }

    public void startActivityForResult(Intent intent, int i, Bundle bundle) {
        Activity activity = this.mActivity;
        if (activity == null) {
            throw new IllegalStateException("Fragment " + this + " not attached to Activity");
        }
        if (bundle != null) {
            activity.startActivityFromFragment(this, intent, i, bundle);
        } else {
            activity.startActivityFromFragment(this, intent, i, bundle);
        }
    }

    public LayoutInflater getLayoutInflater(Bundle bundle) {
        return this.mActivity.getLayoutInflater();
    }

    @Deprecated
    public void onInflate(AttributeSet attributeSet, Bundle bundle) {
        this.mCalled = true;
    }

    public void onInflate(Activity activity, AttributeSet attributeSet, Bundle bundle) {
        onInflate(attributeSet, bundle);
        this.mCalled = true;
    }

    public void onAttach(Activity activity) {
        this.mCalled = true;
    }

    public void onCreate(Bundle bundle) {
        this.mCalled = true;
    }

    public View getView() {
        return this.mView;
    }

    public void onActivityCreated(Bundle bundle) {
        this.mCalled = true;
    }

    public void onViewStateRestored(Bundle bundle) {
        this.mCalled = true;
    }

    public void onStart() {
        this.mCalled = true;
        if (this.mLoadersStarted) {
            return;
        }
        this.mLoadersStarted = true;
        if (!this.mCheckedForLoaderManager) {
            this.mCheckedForLoaderManager = true;
            this.mLoaderManager = this.mActivity.getLoaderManager(this.mWho, true, false);
        }
        LoaderManagerImpl loaderManagerImpl = this.mLoaderManager;
        if (loaderManagerImpl != null) {
            loaderManagerImpl.doStart();
        }
    }

    public void onResume() {
        this.mCalled = true;
    }

    @Override // android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        this.mCalled = true;
    }

    public void onPause() {
        this.mCalled = true;
    }

    public void onStop() {
        this.mCalled = true;
    }

    @Override // android.content.ComponentCallbacks
    public void onLowMemory() {
        this.mCalled = true;
    }

    @Override // android.content.ComponentCallbacks2
    public void onTrimMemory(int i) {
        this.mCalled = true;
    }

    public void onDestroyView() {
        this.mCalled = true;
    }

    public void onDestroy() {
        this.mCalled = true;
        if (!this.mCheckedForLoaderManager) {
            this.mCheckedForLoaderManager = true;
            this.mLoaderManager = this.mActivity.getLoaderManager(this.mWho, this.mLoadersStarted, false);
        }
        LoaderManagerImpl loaderManagerImpl = this.mLoaderManager;
        if (loaderManagerImpl != null) {
            loaderManagerImpl.doDestroy();
        }
    }

    void initState() {
        this.mIndex = -1;
        this.mWho = null;
        this.mAdded = false;
        this.mRemoving = false;
        this.mResumed = false;
        this.mFromLayout = false;
        this.mInLayout = false;
        this.mRestored = false;
        this.mBackStackNesting = 0;
        this.mFragmentManager = null;
        this.mActivity = null;
        this.mFragmentId = 0;
        this.mContainerId = 0;
        this.mTag = null;
        this.mHidden = false;
        this.mDetached = false;
        this.mRetaining = false;
        this.mLoaderManager = null;
        this.mLoadersStarted = false;
        this.mCheckedForLoaderManager = false;
    }

    public void onDetach() {
        this.mCalled = true;
    }

    @Override // android.view.View.OnCreateContextMenuListener
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        getActivity().onCreateContextMenu(contextMenu, view, contextMenuInfo);
    }

    public void registerForContextMenu(View view) {
        view.setOnCreateContextMenuListener(this);
    }

    public void unregisterForContextMenu(View view) {
        view.setOnCreateContextMenuListener(null);
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.print(str);
        printWriter.print("mFragmentId=#");
        printWriter.print(Integer.toHexString(this.mFragmentId));
        printWriter.print(" mContainerId=#");
        printWriter.print(Integer.toHexString(this.mContainerId));
        printWriter.print(" mTag=");
        printWriter.println(this.mTag);
        printWriter.print(str);
        printWriter.print("mState=");
        printWriter.print(this.mState);
        printWriter.print(" mIndex=");
        printWriter.print(this.mIndex);
        printWriter.print(" mWho=");
        printWriter.print(this.mWho);
        printWriter.print(" mBackStackNesting=");
        printWriter.println(this.mBackStackNesting);
        printWriter.print(str);
        printWriter.print("mAdded=");
        printWriter.print(this.mAdded);
        printWriter.print(" mRemoving=");
        printWriter.print(this.mRemoving);
        printWriter.print(" mResumed=");
        printWriter.print(this.mResumed);
        printWriter.print(" mFromLayout=");
        printWriter.print(this.mFromLayout);
        printWriter.print(" mInLayout=");
        printWriter.println(this.mInLayout);
        printWriter.print(str);
        printWriter.print("mHidden=");
        printWriter.print(this.mHidden);
        printWriter.print(" mDetached=");
        printWriter.print(this.mDetached);
        printWriter.print(" mMenuVisible=");
        printWriter.print(this.mMenuVisible);
        printWriter.print(" mHasMenu=");
        printWriter.println(this.mHasMenu);
        printWriter.print(str);
        printWriter.print("mRetainInstance=");
        printWriter.print(this.mRetainInstance);
        printWriter.print(" mRetaining=");
        printWriter.print(this.mRetaining);
        printWriter.print(" mUserVisibleHint=");
        printWriter.println(this.mUserVisibleHint);
        if (this.mFragmentManager != null) {
            printWriter.print(str);
            printWriter.print("mFragmentManager=");
            printWriter.println(this.mFragmentManager);
        }
        if (this.mActivity != null) {
            printWriter.print(str);
            printWriter.print("mActivity=");
            printWriter.println(this.mActivity);
        }
        if (this.mParentFragment != null) {
            printWriter.print(str);
            printWriter.print("mParentFragment=");
            printWriter.println(this.mParentFragment);
        }
        if (this.mArguments != null) {
            printWriter.print(str);
            printWriter.print("mArguments=");
            printWriter.println(this.mArguments);
        }
        if (this.mSavedFragmentState != null) {
            printWriter.print(str);
            printWriter.print("mSavedFragmentState=");
            printWriter.println(this.mSavedFragmentState);
        }
        if (this.mSavedViewState != null) {
            printWriter.print(str);
            printWriter.print("mSavedViewState=");
            printWriter.println(this.mSavedViewState);
        }
        if (this.mTarget != null) {
            printWriter.print(str);
            printWriter.print("mTarget=");
            printWriter.print(this.mTarget);
            printWriter.print(" mTargetRequestCode=");
            printWriter.println(this.mTargetRequestCode);
        }
        if (this.mNextAnim != 0) {
            printWriter.print(str);
            printWriter.print("mNextAnim=");
            printWriter.println(this.mNextAnim);
        }
        if (this.mContainer != null) {
            printWriter.print(str);
            printWriter.print("mContainer=");
            printWriter.println(this.mContainer);
        }
        if (this.mView != null) {
            printWriter.print(str);
            printWriter.print("mView=");
            printWriter.println(this.mView);
        }
        if (this.mAnimatingAway != null) {
            printWriter.print(str);
            printWriter.print("mAnimatingAway=");
            printWriter.println(this.mAnimatingAway);
            printWriter.print(str);
            printWriter.print("mStateAfterAnimating=");
            printWriter.println(this.mStateAfterAnimating);
        }
        if (this.mLoaderManager != null) {
            printWriter.print(str);
            printWriter.println("Loader Manager:");
            this.mLoaderManager.dump(str + "  ", fileDescriptor, printWriter, strArr);
        }
        if (this.mChildFragmentManager != null) {
            printWriter.print(str);
            printWriter.println("Child " + this.mChildFragmentManager + ":");
            this.mChildFragmentManager.dump(str + "  ", fileDescriptor, printWriter, strArr);
        }
    }

    Fragment findFragmentByWho(String str) {
        if (str.equals(this.mWho)) {
            return this;
        }
        FragmentManagerImpl fragmentManagerImpl = this.mChildFragmentManager;
        if (fragmentManagerImpl != null) {
            return fragmentManagerImpl.findFragmentByWho(str);
        }
        return null;
    }

    void instantiateChildFragmentManager() {
        FragmentManagerImpl fragmentManagerImpl = new FragmentManagerImpl();
        this.mChildFragmentManager = fragmentManagerImpl;
        fragmentManagerImpl.attachActivity(this.mActivity, new FragmentContainer() { // from class: android.app.Fragment.1
            @Override // android.app.FragmentContainer
            public View findViewById(int i) {
                if (Fragment.this.mView == null) {
                    throw new IllegalStateException("Fragment does not have a view");
                }
                return Fragment.this.mView.findViewById(i);
            }
        }, this);
    }

    void performCreate(Bundle bundle) {
        Parcelable parcelable;
        FragmentManagerImpl fragmentManagerImpl = this.mChildFragmentManager;
        if (fragmentManagerImpl != null) {
            fragmentManagerImpl.noteStateNotSaved();
        }
        this.mCalled = false;
        onCreate(bundle);
        if (!this.mCalled) {
            throw new SuperNotCalledException("Fragment " + this + " did not call through to super.onCreate()");
        }
        if (bundle == null || (parcelable = bundle.getParcelable("android:fragments")) == null) {
            return;
        }
        if (this.mChildFragmentManager == null) {
            instantiateChildFragmentManager();
        }
        this.mChildFragmentManager.restoreAllState(parcelable, null);
        this.mChildFragmentManager.dispatchCreate();
    }

    View performCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        FragmentManagerImpl fragmentManagerImpl = this.mChildFragmentManager;
        if (fragmentManagerImpl != null) {
            fragmentManagerImpl.noteStateNotSaved();
        }
        return onCreateView(layoutInflater, viewGroup, bundle);
    }

    void performActivityCreated(Bundle bundle) {
        FragmentManagerImpl fragmentManagerImpl = this.mChildFragmentManager;
        if (fragmentManagerImpl != null) {
            fragmentManagerImpl.noteStateNotSaved();
        }
        this.mCalled = false;
        onActivityCreated(bundle);
        if (!this.mCalled) {
            throw new SuperNotCalledException("Fragment " + this + " did not call through to super.onActivityCreated()");
        }
        FragmentManagerImpl fragmentManagerImpl2 = this.mChildFragmentManager;
        if (fragmentManagerImpl2 != null) {
            fragmentManagerImpl2.dispatchActivityCreated();
        }
    }

    void performStart() {
        FragmentManagerImpl fragmentManagerImpl = this.mChildFragmentManager;
        if (fragmentManagerImpl != null) {
            fragmentManagerImpl.noteStateNotSaved();
            this.mChildFragmentManager.execPendingActions();
        }
        this.mCalled = false;
        onStart();
        if (!this.mCalled) {
            throw new SuperNotCalledException("Fragment " + this + " did not call through to super.onStart()");
        }
        FragmentManagerImpl fragmentManagerImpl2 = this.mChildFragmentManager;
        if (fragmentManagerImpl2 != null) {
            fragmentManagerImpl2.dispatchStart();
        }
        LoaderManagerImpl loaderManagerImpl = this.mLoaderManager;
        if (loaderManagerImpl != null) {
            loaderManagerImpl.doReportStart();
        }
    }

    void performResume() {
        FragmentManagerImpl fragmentManagerImpl = this.mChildFragmentManager;
        if (fragmentManagerImpl != null) {
            fragmentManagerImpl.noteStateNotSaved();
            this.mChildFragmentManager.execPendingActions();
        }
        this.mCalled = false;
        onResume();
        if (!this.mCalled) {
            throw new SuperNotCalledException("Fragment " + this + " did not call through to super.onResume()");
        }
        FragmentManagerImpl fragmentManagerImpl2 = this.mChildFragmentManager;
        if (fragmentManagerImpl2 != null) {
            fragmentManagerImpl2.dispatchResume();
            this.mChildFragmentManager.execPendingActions();
        }
    }

    void performConfigurationChanged(Configuration configuration) {
        onConfigurationChanged(configuration);
        FragmentManagerImpl fragmentManagerImpl = this.mChildFragmentManager;
        if (fragmentManagerImpl != null) {
            fragmentManagerImpl.dispatchConfigurationChanged(configuration);
        }
    }

    void performLowMemory() {
        onLowMemory();
        FragmentManagerImpl fragmentManagerImpl = this.mChildFragmentManager;
        if (fragmentManagerImpl != null) {
            fragmentManagerImpl.dispatchLowMemory();
        }
    }

    void performTrimMemory(int i) {
        onTrimMemory(i);
        FragmentManagerImpl fragmentManagerImpl = this.mChildFragmentManager;
        if (fragmentManagerImpl != null) {
            fragmentManagerImpl.dispatchTrimMemory(i);
        }
    }

    boolean performCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        boolean z = false;
        if (this.mHidden) {
            return false;
        }
        if (this.mHasMenu && this.mMenuVisible) {
            onCreateOptionsMenu(menu, menuInflater);
            z = true;
        }
        FragmentManagerImpl fragmentManagerImpl = this.mChildFragmentManager;
        return fragmentManagerImpl != null ? z | fragmentManagerImpl.dispatchCreateOptionsMenu(menu, menuInflater) : z;
    }

    boolean performPrepareOptionsMenu(Menu menu) {
        boolean z = false;
        if (this.mHidden) {
            return false;
        }
        if (this.mHasMenu && this.mMenuVisible) {
            onPrepareOptionsMenu(menu);
            z = true;
        }
        FragmentManagerImpl fragmentManagerImpl = this.mChildFragmentManager;
        return fragmentManagerImpl != null ? z | fragmentManagerImpl.dispatchPrepareOptionsMenu(menu) : z;
    }

    boolean performOptionsItemSelected(MenuItem menuItem) {
        if (this.mHidden) {
            return false;
        }
        if (this.mHasMenu && this.mMenuVisible && onOptionsItemSelected(menuItem)) {
            return true;
        }
        FragmentManagerImpl fragmentManagerImpl = this.mChildFragmentManager;
        return fragmentManagerImpl != null && fragmentManagerImpl.dispatchOptionsItemSelected(menuItem);
    }

    boolean performContextItemSelected(MenuItem menuItem) {
        if (this.mHidden) {
            return false;
        }
        if (onContextItemSelected(menuItem)) {
            return true;
        }
        FragmentManagerImpl fragmentManagerImpl = this.mChildFragmentManager;
        return fragmentManagerImpl != null && fragmentManagerImpl.dispatchContextItemSelected(menuItem);
    }

    void performOptionsMenuClosed(Menu menu) {
        if (this.mHidden) {
            return;
        }
        if (this.mHasMenu && this.mMenuVisible) {
            onOptionsMenuClosed(menu);
        }
        FragmentManagerImpl fragmentManagerImpl = this.mChildFragmentManager;
        if (fragmentManagerImpl != null) {
            fragmentManagerImpl.dispatchOptionsMenuClosed(menu);
        }
    }

    void performSaveInstanceState(Bundle bundle) {
        Parcelable parcelableSaveAllState;
        onSaveInstanceState(bundle);
        FragmentManagerImpl fragmentManagerImpl = this.mChildFragmentManager;
        if (fragmentManagerImpl == null || (parcelableSaveAllState = fragmentManagerImpl.saveAllState()) == null) {
            return;
        }
        bundle.putParcelable("android:fragments", parcelableSaveAllState);
    }

    void performPause() {
        FragmentManagerImpl fragmentManagerImpl = this.mChildFragmentManager;
        if (fragmentManagerImpl != null) {
            fragmentManagerImpl.dispatchPause();
        }
        this.mCalled = false;
        onPause();
        if (!this.mCalled) {
            throw new SuperNotCalledException("Fragment " + this + " did not call through to super.onPause()");
        }
    }

    void performStop() {
        FragmentManagerImpl fragmentManagerImpl = this.mChildFragmentManager;
        if (fragmentManagerImpl != null) {
            fragmentManagerImpl.dispatchStop();
        }
        this.mCalled = false;
        onStop();
        if (!this.mCalled) {
            throw new SuperNotCalledException("Fragment " + this + " did not call through to super.onStop()");
        }
        if (this.mLoadersStarted) {
            this.mLoadersStarted = false;
            if (!this.mCheckedForLoaderManager) {
                this.mCheckedForLoaderManager = true;
                this.mLoaderManager = this.mActivity.getLoaderManager(this.mWho, false, false);
            }
            if (this.mLoaderManager != null) {
                Activity activity = this.mActivity;
                if (activity == null || !activity.mChangingConfigurations) {
                    this.mLoaderManager.doStop();
                } else {
                    this.mLoaderManager.doRetain();
                }
            }
        }
    }

    void performDestroyView() {
        FragmentManagerImpl fragmentManagerImpl = this.mChildFragmentManager;
        if (fragmentManagerImpl != null) {
            fragmentManagerImpl.dispatchDestroyView();
        }
        this.mCalled = false;
        onDestroyView();
        if (!this.mCalled) {
            throw new SuperNotCalledException("Fragment " + this + " did not call through to super.onDestroyView()");
        }
        LoaderManagerImpl loaderManagerImpl = this.mLoaderManager;
        if (loaderManagerImpl != null) {
            loaderManagerImpl.doReportNextStart();
        }
    }

    void performDestroy() {
        FragmentManagerImpl fragmentManagerImpl = this.mChildFragmentManager;
        if (fragmentManagerImpl != null) {
            fragmentManagerImpl.dispatchDestroy();
        }
        this.mCalled = false;
        onDestroy();
        if (!this.mCalled) {
            throw new SuperNotCalledException("Fragment " + this + " did not call through to super.onDestroy()");
        }
    }
}
