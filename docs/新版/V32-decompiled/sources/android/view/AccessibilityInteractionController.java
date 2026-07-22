package android.view;

import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.util.SparseLongArray;
import android.view.View;
import android.view.accessibility.AccessibilityInteractionClient;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import android.view.accessibility.IAccessibilityInteractionConnectionCallback;
import com.android.internal.os.SomeArgs;
import com.android.internal.util.Predicate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
final class AccessibilityInteractionController {
    private AddNodeInfosForViewId mAddNodeInfosForViewId;
    private final Handler mHandler;
    private final long mMyLooperThreadId;
    private final int mMyProcessId;
    private final AccessibilityNodePrefetcher mPrefetcher;
    private final ArrayList<AccessibilityNodeInfo> mTempAccessibilityNodeInfoList = new ArrayList<>();
    private final ArrayList<View> mTempArrayList = new ArrayList<>();
    private final Point mTempPoint = new Point();
    private final Rect mTempRect = new Rect();
    private final Rect mTempRect1 = new Rect();
    private final Rect mTempRect2 = new Rect();
    private final ViewRootImpl mViewRootImpl;

    public AccessibilityInteractionController(ViewRootImpl viewRootImpl) {
        Looper looper = viewRootImpl.mHandler.getLooper();
        this.mMyLooperThreadId = looper.getThread().getId();
        this.mMyProcessId = Process.myPid();
        this.mHandler = new PrivateHandler(looper);
        this.mViewRootImpl = viewRootImpl;
        this.mPrefetcher = new AccessibilityNodePrefetcher();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isShown(View view) {
        return view.mAttachInfo != null && view.mAttachInfo.mWindowVisibility == 0 && view.isShown();
    }

    public void findAccessibilityNodeInfoByAccessibilityIdClientThread(long j, int i, IAccessibilityInteractionConnectionCallback iAccessibilityInteractionConnectionCallback, int i2, int i3, long j2, MagnificationSpec magnificationSpec) {
        Message messageObtainMessage = this.mHandler.obtainMessage();
        messageObtainMessage.what = 2;
        messageObtainMessage.arg1 = i2;
        SomeArgs someArgsObtain = SomeArgs.obtain();
        someArgsObtain.argi1 = AccessibilityNodeInfo.getAccessibilityViewId(j);
        someArgsObtain.argi2 = AccessibilityNodeInfo.getVirtualDescendantId(j);
        someArgsObtain.argi3 = i;
        someArgsObtain.arg1 = iAccessibilityInteractionConnectionCallback;
        someArgsObtain.arg2 = magnificationSpec;
        messageObtainMessage.obj = someArgsObtain;
        if (i3 == this.mMyProcessId && j2 == this.mMyLooperThreadId) {
            AccessibilityInteractionClient.getInstanceForThread(j2).setSameThreadMessage(messageObtainMessage);
        } else {
            this.mHandler.sendMessage(messageObtainMessage);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void findAccessibilityNodeInfoByAccessibilityIdUiThread(Message message) {
        View viewFindViewByAccessibilityId;
        int i = message.arg1;
        SomeArgs someArgs = (SomeArgs) message.obj;
        int i2 = someArgs.argi1;
        int i3 = someArgs.argi2;
        int i4 = someArgs.argi3;
        IAccessibilityInteractionConnectionCallback iAccessibilityInteractionConnectionCallback = (IAccessibilityInteractionConnectionCallback) someArgs.arg1;
        MagnificationSpec magnificationSpec = (MagnificationSpec) someArgs.arg2;
        someArgs.recycle();
        ArrayList<AccessibilityNodeInfo> arrayList = this.mTempAccessibilityNodeInfoList;
        arrayList.clear();
        try {
            if (this.mViewRootImpl.mView != null && this.mViewRootImpl.mAttachInfo != null) {
                this.mViewRootImpl.mAttachInfo.mAccessibilityFetchFlags = i;
                if (i2 == -1) {
                    viewFindViewByAccessibilityId = this.mViewRootImpl.mView;
                } else {
                    viewFindViewByAccessibilityId = findViewByAccessibilityId(i2);
                }
                if (viewFindViewByAccessibilityId != null && isShown(viewFindViewByAccessibilityId)) {
                    this.mPrefetcher.prefetchAccessibilityNodeInfos(viewFindViewByAccessibilityId, i3, i, arrayList);
                }
                try {
                    return;
                } catch (RemoteException unused) {
                    return;
                }
            }
            try {
                this.mViewRootImpl.mAttachInfo.mAccessibilityFetchFlags = 0;
                applyAppScaleAndMagnificationSpecIfNeeded(arrayList, magnificationSpec);
                if (magnificationSpec != null) {
                    magnificationSpec.recycle();
                }
                iAccessibilityInteractionConnectionCallback.setFindAccessibilityNodeInfosResult(arrayList, i4);
                arrayList.clear();
            } catch (RemoteException unused2) {
            }
        } finally {
            try {
                this.mViewRootImpl.mAttachInfo.mAccessibilityFetchFlags = 0;
                applyAppScaleAndMagnificationSpecIfNeeded(arrayList, magnificationSpec);
                if (magnificationSpec != null) {
                    magnificationSpec.recycle();
                }
                iAccessibilityInteractionConnectionCallback.setFindAccessibilityNodeInfosResult(arrayList, i4);
                arrayList.clear();
            } catch (RemoteException unused3) {
            }
        }
    }

    public void findAccessibilityNodeInfosByViewIdClientThread(long j, String str, int i, IAccessibilityInteractionConnectionCallback iAccessibilityInteractionConnectionCallback, int i2, int i3, long j2, MagnificationSpec magnificationSpec) {
        Message messageObtainMessage = this.mHandler.obtainMessage();
        messageObtainMessage.what = 3;
        messageObtainMessage.arg1 = i2;
        messageObtainMessage.arg2 = AccessibilityNodeInfo.getAccessibilityViewId(j);
        SomeArgs someArgsObtain = SomeArgs.obtain();
        someArgsObtain.argi1 = i;
        someArgsObtain.arg1 = iAccessibilityInteractionConnectionCallback;
        someArgsObtain.arg2 = magnificationSpec;
        someArgsObtain.arg3 = str;
        messageObtainMessage.obj = someArgsObtain;
        if (i3 == this.mMyProcessId && j2 == this.mMyLooperThreadId) {
            AccessibilityInteractionClient.getInstanceForThread(j2).setSameThreadMessage(messageObtainMessage);
        } else {
            this.mHandler.sendMessage(messageObtainMessage);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void findAccessibilityNodeInfosByViewIdUiThread(Message message) {
        int i = message.arg1;
        int i2 = message.arg2;
        SomeArgs someArgs = (SomeArgs) message.obj;
        int i3 = someArgs.argi1;
        IAccessibilityInteractionConnectionCallback iAccessibilityInteractionConnectionCallback = (IAccessibilityInteractionConnectionCallback) someArgs.arg1;
        MagnificationSpec magnificationSpec = (MagnificationSpec) someArgs.arg2;
        String str = (String) someArgs.arg3;
        someArgs.recycle();
        ArrayList<AccessibilityNodeInfo> arrayList = this.mTempAccessibilityNodeInfoList;
        arrayList.clear();
        try {
            if (this.mViewRootImpl.mView != null && this.mViewRootImpl.mAttachInfo != null) {
                this.mViewRootImpl.mAttachInfo.mAccessibilityFetchFlags = i;
                View viewFindViewByAccessibilityId = i2 != -1 ? findViewByAccessibilityId(i2) : this.mViewRootImpl.mView;
                if (viewFindViewByAccessibilityId != null) {
                    int identifier = viewFindViewByAccessibilityId.getContext().getResources().getIdentifier(str, null, null);
                    if (identifier <= 0) {
                        try {
                            return;
                        } catch (RemoteException unused) {
                            return;
                        }
                    }
                    if (this.mAddNodeInfosForViewId == null) {
                        this.mAddNodeInfosForViewId = new AddNodeInfosForViewId();
                    }
                    this.mAddNodeInfosForViewId.init(identifier, arrayList);
                    viewFindViewByAccessibilityId.findViewByPredicate(this.mAddNodeInfosForViewId);
                    this.mAddNodeInfosForViewId.reset();
                }
                try {
                    this.mViewRootImpl.mAttachInfo.mAccessibilityFetchFlags = 0;
                    applyAppScaleAndMagnificationSpecIfNeeded(arrayList, magnificationSpec);
                    if (magnificationSpec != null) {
                        magnificationSpec.recycle();
                    }
                    iAccessibilityInteractionConnectionCallback.setFindAccessibilityNodeInfosResult(arrayList, i3);
                    return;
                } catch (RemoteException unused2) {
                    return;
                }
            }
            try {
                this.mViewRootImpl.mAttachInfo.mAccessibilityFetchFlags = 0;
                applyAppScaleAndMagnificationSpecIfNeeded(arrayList, magnificationSpec);
                if (magnificationSpec != null) {
                    magnificationSpec.recycle();
                }
                iAccessibilityInteractionConnectionCallback.setFindAccessibilityNodeInfosResult(arrayList, i3);
            } catch (RemoteException unused3) {
            }
        } finally {
            try {
                this.mViewRootImpl.mAttachInfo.mAccessibilityFetchFlags = 0;
                applyAppScaleAndMagnificationSpecIfNeeded(arrayList, magnificationSpec);
                if (magnificationSpec != null) {
                    magnificationSpec.recycle();
                }
                iAccessibilityInteractionConnectionCallback.setFindAccessibilityNodeInfosResult(arrayList, i3);
            } catch (RemoteException unused4) {
            }
        }
    }

    public void findAccessibilityNodeInfosByTextClientThread(long j, String str, int i, IAccessibilityInteractionConnectionCallback iAccessibilityInteractionConnectionCallback, int i2, int i3, long j2, MagnificationSpec magnificationSpec) {
        Message messageObtainMessage = this.mHandler.obtainMessage();
        messageObtainMessage.what = 4;
        messageObtainMessage.arg1 = i2;
        SomeArgs someArgsObtain = SomeArgs.obtain();
        someArgsObtain.arg1 = str;
        someArgsObtain.arg2 = iAccessibilityInteractionConnectionCallback;
        someArgsObtain.arg3 = magnificationSpec;
        someArgsObtain.argi1 = AccessibilityNodeInfo.getAccessibilityViewId(j);
        someArgsObtain.argi2 = AccessibilityNodeInfo.getVirtualDescendantId(j);
        someArgsObtain.argi3 = i;
        messageObtainMessage.obj = someArgsObtain;
        if (i3 == this.mMyProcessId && j2 == this.mMyLooperThreadId) {
            AccessibilityInteractionClient.getInstanceForThread(j2).setSameThreadMessage(messageObtainMessage);
        } else {
            this.mHandler.sendMessage(messageObtainMessage);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void findAccessibilityNodeInfosByTextUiThread(Message message) {
        View viewFindViewByAccessibilityId;
        int i = message.arg1;
        SomeArgs someArgs = (SomeArgs) message.obj;
        String str = (String) someArgs.arg1;
        IAccessibilityInteractionConnectionCallback iAccessibilityInteractionConnectionCallback = (IAccessibilityInteractionConnectionCallback) someArgs.arg2;
        MagnificationSpec magnificationSpec = (MagnificationSpec) someArgs.arg3;
        int i2 = someArgs.argi1;
        int i3 = someArgs.argi2;
        int i4 = someArgs.argi3;
        someArgs.recycle();
        List<AccessibilityNodeInfo> listFindAccessibilityNodeInfosByText = null;
        try {
            if (this.mViewRootImpl.mView != null && this.mViewRootImpl.mAttachInfo != null) {
                this.mViewRootImpl.mAttachInfo.mAccessibilityFetchFlags = i;
                if (i2 != -1) {
                    viewFindViewByAccessibilityId = findViewByAccessibilityId(i2);
                } else {
                    viewFindViewByAccessibilityId = this.mViewRootImpl.mView;
                }
                if (viewFindViewByAccessibilityId != null && isShown(viewFindViewByAccessibilityId)) {
                    AccessibilityNodeProvider accessibilityNodeProvider = viewFindViewByAccessibilityId.getAccessibilityNodeProvider();
                    if (accessibilityNodeProvider != null) {
                        listFindAccessibilityNodeInfosByText = accessibilityNodeProvider.findAccessibilityNodeInfosByText(str, i3);
                    } else if (i3 == -1) {
                        ArrayList<View> arrayList = this.mTempArrayList;
                        arrayList.clear();
                        viewFindViewByAccessibilityId.findViewsWithText(arrayList, str, 7);
                        if (!arrayList.isEmpty()) {
                            listFindAccessibilityNodeInfosByText = this.mTempAccessibilityNodeInfoList;
                            listFindAccessibilityNodeInfosByText.clear();
                            int size = arrayList.size();
                            for (int i5 = 0; i5 < size; i5++) {
                                View view = arrayList.get(i5);
                                if (isShown(view)) {
                                    AccessibilityNodeProvider accessibilityNodeProvider2 = view.getAccessibilityNodeProvider();
                                    if (accessibilityNodeProvider2 != null) {
                                        List<AccessibilityNodeInfo> listFindAccessibilityNodeInfosByText2 = accessibilityNodeProvider2.findAccessibilityNodeInfosByText(str, -1);
                                        if (listFindAccessibilityNodeInfosByText2 != null) {
                                            listFindAccessibilityNodeInfosByText.addAll(listFindAccessibilityNodeInfosByText2);
                                        }
                                    } else {
                                        listFindAccessibilityNodeInfosByText.add(view.createAccessibilityNodeInfo());
                                    }
                                }
                            }
                        }
                    }
                }
                try {
                    return;
                } catch (RemoteException unused) {
                    return;
                }
            }
            try {
                this.mViewRootImpl.mAttachInfo.mAccessibilityFetchFlags = 0;
                applyAppScaleAndMagnificationSpecIfNeeded((List<AccessibilityNodeInfo>) null, magnificationSpec);
                if (magnificationSpec != null) {
                    magnificationSpec.recycle();
                }
                iAccessibilityInteractionConnectionCallback.setFindAccessibilityNodeInfosResult(null, i4);
            } catch (RemoteException unused2) {
            }
        } finally {
            try {
                this.mViewRootImpl.mAttachInfo.mAccessibilityFetchFlags = 0;
                applyAppScaleAndMagnificationSpecIfNeeded((List<AccessibilityNodeInfo>) null, magnificationSpec);
                if (magnificationSpec != null) {
                    magnificationSpec.recycle();
                }
                iAccessibilityInteractionConnectionCallback.setFindAccessibilityNodeInfosResult(null, i4);
            } catch (RemoteException unused3) {
            }
        }
    }

    public void findFocusClientThread(long j, int i, int i2, IAccessibilityInteractionConnectionCallback iAccessibilityInteractionConnectionCallback, int i3, int i4, long j2, MagnificationSpec magnificationSpec) {
        Message messageObtainMessage = this.mHandler.obtainMessage();
        messageObtainMessage.what = 5;
        messageObtainMessage.arg1 = i3;
        messageObtainMessage.arg2 = i;
        SomeArgs someArgsObtain = SomeArgs.obtain();
        someArgsObtain.argi1 = i2;
        someArgsObtain.argi2 = AccessibilityNodeInfo.getAccessibilityViewId(j);
        someArgsObtain.argi3 = AccessibilityNodeInfo.getVirtualDescendantId(j);
        someArgsObtain.arg1 = iAccessibilityInteractionConnectionCallback;
        someArgsObtain.arg2 = magnificationSpec;
        messageObtainMessage.obj = someArgsObtain;
        if (i4 == this.mMyProcessId && j2 == this.mMyLooperThreadId) {
            AccessibilityInteractionClient.getInstanceForThread(j2).setSameThreadMessage(messageObtainMessage);
        } else {
            this.mHandler.sendMessage(messageObtainMessage);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void findFocusUiThread(Message message) {
        View viewFindViewByAccessibilityId;
        AccessibilityNodeInfo accessibilityNodeInfoCreateAccessibilityNodeInfo;
        int i = message.arg1;
        int i2 = message.arg2;
        SomeArgs someArgs = (SomeArgs) message.obj;
        int i3 = someArgs.argi1;
        int i4 = someArgs.argi2;
        int i5 = someArgs.argi3;
        IAccessibilityInteractionConnectionCallback iAccessibilityInteractionConnectionCallback = (IAccessibilityInteractionConnectionCallback) someArgs.arg1;
        MagnificationSpec magnificationSpec = (MagnificationSpec) someArgs.arg2;
        someArgs.recycle();
        try {
            if (this.mViewRootImpl.mView != null && this.mViewRootImpl.mAttachInfo != null) {
                this.mViewRootImpl.mAttachInfo.mAccessibilityFetchFlags = i;
                if (i4 != -1) {
                    viewFindViewByAccessibilityId = findViewByAccessibilityId(i4);
                } else {
                    viewFindViewByAccessibilityId = this.mViewRootImpl.mView;
                }
                if (viewFindViewByAccessibilityId != null && isShown(viewFindViewByAccessibilityId)) {
                    if (i2 == 1) {
                        View viewFindFocus = viewFindViewByAccessibilityId.findFocus();
                        if (viewFindFocus != null && isShown(viewFindFocus)) {
                            AccessibilityNodeProvider accessibilityNodeProvider = viewFindFocus.getAccessibilityNodeProvider();
                            accessibilityNodeInfoFindFocus = accessibilityNodeProvider != null ? accessibilityNodeProvider.findFocus(i2) : null;
                            if (accessibilityNodeInfoFindFocus == null) {
                                accessibilityNodeInfoCreateAccessibilityNodeInfo = viewFindFocus.createAccessibilityNodeInfo();
                                accessibilityNodeInfoFindFocus = accessibilityNodeInfoCreateAccessibilityNodeInfo;
                            }
                        }
                    } else if (i2 == 2) {
                        View view = this.mViewRootImpl.mAccessibilityFocusedHost;
                        if (view != null && ViewRootImpl.isViewDescendantOf(view, viewFindViewByAccessibilityId) && isShown(view)) {
                            if (view.getAccessibilityNodeProvider() != null) {
                                if (this.mViewRootImpl.mAccessibilityFocusedVirtualView != null) {
                                    accessibilityNodeInfoCreateAccessibilityNodeInfo = AccessibilityNodeInfo.obtain(this.mViewRootImpl.mAccessibilityFocusedVirtualView);
                                    accessibilityNodeInfoFindFocus = accessibilityNodeInfoCreateAccessibilityNodeInfo;
                                }
                            } else if (i5 == -1) {
                                accessibilityNodeInfoCreateAccessibilityNodeInfo = view.createAccessibilityNodeInfo();
                                accessibilityNodeInfoFindFocus = accessibilityNodeInfoCreateAccessibilityNodeInfo;
                            }
                        }
                    } else {
                        throw new IllegalArgumentException("Unknown focus type: " + i2);
                    }
                }
                try {
                    return;
                } catch (RemoteException unused) {
                    return;
                }
            }
            try {
                this.mViewRootImpl.mAttachInfo.mAccessibilityFetchFlags = 0;
                applyAppScaleAndMagnificationSpecIfNeeded((AccessibilityNodeInfo) null, magnificationSpec);
                if (magnificationSpec != null) {
                    magnificationSpec.recycle();
                }
                iAccessibilityInteractionConnectionCallback.setFindAccessibilityNodeInfoResult(null, i3);
            } catch (RemoteException unused2) {
            }
        } finally {
            try {
                this.mViewRootImpl.mAttachInfo.mAccessibilityFetchFlags = 0;
                applyAppScaleAndMagnificationSpecIfNeeded((AccessibilityNodeInfo) null, magnificationSpec);
                if (magnificationSpec != null) {
                    magnificationSpec.recycle();
                }
                iAccessibilityInteractionConnectionCallback.setFindAccessibilityNodeInfoResult(null, i3);
            } catch (RemoteException unused3) {
            }
        }
    }

    public void focusSearchClientThread(long j, int i, int i2, IAccessibilityInteractionConnectionCallback iAccessibilityInteractionConnectionCallback, int i3, int i4, long j2, MagnificationSpec magnificationSpec) {
        Message messageObtainMessage = this.mHandler.obtainMessage();
        messageObtainMessage.what = 6;
        messageObtainMessage.arg1 = i3;
        messageObtainMessage.arg2 = AccessibilityNodeInfo.getAccessibilityViewId(j);
        SomeArgs someArgsObtain = SomeArgs.obtain();
        someArgsObtain.argi2 = i;
        someArgsObtain.argi3 = i2;
        someArgsObtain.arg1 = iAccessibilityInteractionConnectionCallback;
        someArgsObtain.arg2 = magnificationSpec;
        messageObtainMessage.obj = someArgsObtain;
        if (i4 == this.mMyProcessId && j2 == this.mMyLooperThreadId) {
            AccessibilityInteractionClient.getInstanceForThread(j2).setSameThreadMessage(messageObtainMessage);
        } else {
            this.mHandler.sendMessage(messageObtainMessage);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void focusSearchUiThread(Message message) {
        View viewFindViewByAccessibilityId;
        View viewFocusSearch;
        int i = message.arg1;
        int i2 = message.arg2;
        SomeArgs someArgs = (SomeArgs) message.obj;
        int i3 = someArgs.argi2;
        int i4 = someArgs.argi3;
        IAccessibilityInteractionConnectionCallback iAccessibilityInteractionConnectionCallback = (IAccessibilityInteractionConnectionCallback) someArgs.arg1;
        MagnificationSpec magnificationSpec = (MagnificationSpec) someArgs.arg2;
        someArgs.recycle();
        AccessibilityNodeInfo accessibilityNodeInfoCreateAccessibilityNodeInfo = null;
        try {
            if (this.mViewRootImpl.mView != null && this.mViewRootImpl.mAttachInfo != null) {
                this.mViewRootImpl.mAttachInfo.mAccessibilityFetchFlags = i;
                if (i2 != -1) {
                    viewFindViewByAccessibilityId = findViewByAccessibilityId(i2);
                } else {
                    viewFindViewByAccessibilityId = this.mViewRootImpl.mView;
                }
                if (viewFindViewByAccessibilityId != null && isShown(viewFindViewByAccessibilityId) && (viewFocusSearch = viewFindViewByAccessibilityId.focusSearch(i3)) != null) {
                    accessibilityNodeInfoCreateAccessibilityNodeInfo = viewFocusSearch.createAccessibilityNodeInfo();
                }
                try {
                    return;
                } catch (RemoteException unused) {
                    return;
                }
            }
            try {
                this.mViewRootImpl.mAttachInfo.mAccessibilityFetchFlags = 0;
                applyAppScaleAndMagnificationSpecIfNeeded((AccessibilityNodeInfo) null, magnificationSpec);
                if (magnificationSpec != null) {
                    magnificationSpec.recycle();
                }
                iAccessibilityInteractionConnectionCallback.setFindAccessibilityNodeInfoResult(null, i4);
            } catch (RemoteException unused2) {
            }
        } finally {
            try {
                this.mViewRootImpl.mAttachInfo.mAccessibilityFetchFlags = 0;
                applyAppScaleAndMagnificationSpecIfNeeded((AccessibilityNodeInfo) null, magnificationSpec);
                if (magnificationSpec != null) {
                    magnificationSpec.recycle();
                }
                iAccessibilityInteractionConnectionCallback.setFindAccessibilityNodeInfoResult(null, i4);
            } catch (RemoteException unused3) {
            }
        }
    }

    public void performAccessibilityActionClientThread(long j, int i, Bundle bundle, int i2, IAccessibilityInteractionConnectionCallback iAccessibilityInteractionConnectionCallback, int i3, int i4, long j2) {
        Message messageObtainMessage = this.mHandler.obtainMessage();
        messageObtainMessage.what = 1;
        messageObtainMessage.arg1 = i3;
        messageObtainMessage.arg2 = AccessibilityNodeInfo.getAccessibilityViewId(j);
        SomeArgs someArgsObtain = SomeArgs.obtain();
        someArgsObtain.argi1 = AccessibilityNodeInfo.getVirtualDescendantId(j);
        someArgsObtain.argi2 = i;
        someArgsObtain.argi3 = i2;
        someArgsObtain.arg1 = iAccessibilityInteractionConnectionCallback;
        someArgsObtain.arg2 = bundle;
        messageObtainMessage.obj = someArgsObtain;
        if (i4 == this.mMyProcessId && j2 == this.mMyLooperThreadId) {
            AccessibilityInteractionClient.getInstanceForThread(j2).setSameThreadMessage(messageObtainMessage);
        } else {
            this.mHandler.sendMessage(messageObtainMessage);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:21:0x0053  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void perfromAccessibilityActionUiThread(android.os.Message r9) {
        /*
            r8 = this;
            int r0 = r9.arg1
            int r1 = r9.arg2
            java.lang.Object r9 = r9.obj
            com.android.internal.os.SomeArgs r9 = (com.android.internal.os.SomeArgs) r9
            int r2 = r9.argi1
            int r3 = r9.argi2
            int r4 = r9.argi3
            java.lang.Object r5 = r9.arg1
            android.view.accessibility.IAccessibilityInteractionConnectionCallback r5 = (android.view.accessibility.IAccessibilityInteractionConnectionCallback) r5
            java.lang.Object r6 = r9.arg2
            android.os.Bundle r6 = (android.os.Bundle) r6
            r9.recycle()
            r9 = 0
            android.view.ViewRootImpl r7 = r8.mViewRootImpl     // Catch: java.lang.Throwable -> L68
            android.view.View r7 = r7.mView     // Catch: java.lang.Throwable -> L68
            if (r7 == 0) goto L5e
            android.view.ViewRootImpl r7 = r8.mViewRootImpl     // Catch: java.lang.Throwable -> L68
            android.view.View$AttachInfo r7 = r7.mAttachInfo     // Catch: java.lang.Throwable -> L68
            if (r7 != 0) goto L27
            goto L5e
        L27:
            android.view.ViewRootImpl r7 = r8.mViewRootImpl     // Catch: java.lang.Throwable -> L68
            android.view.View$AttachInfo r7 = r7.mAttachInfo     // Catch: java.lang.Throwable -> L68
            r7.mAccessibilityFetchFlags = r0     // Catch: java.lang.Throwable -> L68
            r0 = -1
            if (r1 == r0) goto L35
            android.view.View r1 = r8.findViewByAccessibilityId(r1)     // Catch: java.lang.Throwable -> L68
            goto L39
        L35:
            android.view.ViewRootImpl r1 = r8.mViewRootImpl     // Catch: java.lang.Throwable -> L68
            android.view.View r1 = r1.mView     // Catch: java.lang.Throwable -> L68
        L39:
            if (r1 == 0) goto L53
            boolean r7 = r8.isShown(r1)     // Catch: java.lang.Throwable -> L68
            if (r7 == 0) goto L53
            android.view.accessibility.AccessibilityNodeProvider r7 = r1.getAccessibilityNodeProvider()     // Catch: java.lang.Throwable -> L68
            if (r7 == 0) goto L4c
            boolean r0 = r7.performAction(r2, r3, r6)     // Catch: java.lang.Throwable -> L68
            goto L54
        L4c:
            if (r2 != r0) goto L53
            boolean r0 = r1.performAccessibilityAction(r3, r6)     // Catch: java.lang.Throwable -> L68
            goto L54
        L53:
            r0 = r9
        L54:
            android.view.ViewRootImpl r1 = r8.mViewRootImpl     // Catch: android.os.RemoteException -> L5d
            android.view.View$AttachInfo r1 = r1.mAttachInfo     // Catch: android.os.RemoteException -> L5d
            r1.mAccessibilityFetchFlags = r9     // Catch: android.os.RemoteException -> L5d
            r5.setPerformAccessibilityActionResult(r0, r4)     // Catch: android.os.RemoteException -> L5d
        L5d:
            return
        L5e:
            android.view.ViewRootImpl r0 = r8.mViewRootImpl     // Catch: android.os.RemoteException -> L67
            android.view.View$AttachInfo r0 = r0.mAttachInfo     // Catch: android.os.RemoteException -> L67
            r0.mAccessibilityFetchFlags = r9     // Catch: android.os.RemoteException -> L67
            r5.setPerformAccessibilityActionResult(r9, r4)     // Catch: android.os.RemoteException -> L67
        L67:
            return
        L68:
            r0 = move-exception
            android.view.ViewRootImpl r1 = r8.mViewRootImpl     // Catch: android.os.RemoteException -> L72
            android.view.View$AttachInfo r1 = r1.mAttachInfo     // Catch: android.os.RemoteException -> L72
            r1.mAccessibilityFetchFlags = r9     // Catch: android.os.RemoteException -> L72
            r5.setPerformAccessibilityActionResult(r9, r4)     // Catch: android.os.RemoteException -> L72
        L72:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: android.view.AccessibilityInteractionController.perfromAccessibilityActionUiThread(android.os.Message):void");
    }

    private View findViewByAccessibilityId(int i) {
        View view = this.mViewRootImpl.mView;
        if (view == null) {
            return null;
        }
        View viewFindViewByAccessibilityId = view.findViewByAccessibilityId(i);
        if (viewFindViewByAccessibilityId == null || isShown(viewFindViewByAccessibilityId)) {
            return viewFindViewByAccessibilityId;
        }
        return null;
    }

    private void applyAppScaleAndMagnificationSpecIfNeeded(List<AccessibilityNodeInfo> list, MagnificationSpec magnificationSpec) {
        if (list != null && shouldApplyAppScaleAndMagnificationSpec(this.mViewRootImpl.mAttachInfo.mApplicationScale, magnificationSpec)) {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                applyAppScaleAndMagnificationSpecIfNeeded(list.get(i), magnificationSpec);
            }
        }
    }

    private void applyAppScaleAndMagnificationSpecIfNeeded(AccessibilityNodeInfo accessibilityNodeInfo, MagnificationSpec magnificationSpec) {
        if (accessibilityNodeInfo == null) {
            return;
        }
        float f = this.mViewRootImpl.mAttachInfo.mApplicationScale;
        if (shouldApplyAppScaleAndMagnificationSpec(f, magnificationSpec)) {
            Rect rect = this.mTempRect;
            Rect rect2 = this.mTempRect1;
            accessibilityNodeInfo.getBoundsInParent(rect);
            accessibilityNodeInfo.getBoundsInScreen(rect2);
            if (f != 1.0f) {
                rect.scale(f);
                rect2.scale(f);
            }
            if (magnificationSpec != null) {
                rect.scale(magnificationSpec.scale);
                rect2.scale(magnificationSpec.scale);
                rect2.offset((int) magnificationSpec.offsetX, (int) magnificationSpec.offsetY);
            }
            accessibilityNodeInfo.setBoundsInParent(rect);
            accessibilityNodeInfo.setBoundsInScreen(rect2);
            if (magnificationSpec != null) {
                View.AttachInfo attachInfo = this.mViewRootImpl.mAttachInfo;
                if (attachInfo.mDisplay == null) {
                    return;
                }
                float f2 = attachInfo.mApplicationScale * magnificationSpec.scale;
                Rect rect3 = this.mTempRect1;
                rect3.left = (int) ((attachInfo.mWindowLeft * f2) + magnificationSpec.offsetX);
                rect3.top = (int) ((attachInfo.mWindowTop * f2) + magnificationSpec.offsetY);
                rect3.right = (int) (rect3.left + (this.mViewRootImpl.mWidth * f2));
                rect3.bottom = (int) (rect3.top + (this.mViewRootImpl.mHeight * f2));
                attachInfo.mDisplay.getRealSize(this.mTempPoint);
                int i = this.mTempPoint.x;
                int i2 = this.mTempPoint.y;
                Rect rect4 = this.mTempRect2;
                rect4.set(0, 0, i, i2);
                rect3.intersect(rect4);
                if (rect3.intersects(rect2.left, rect2.top, rect2.right, rect2.bottom)) {
                    return;
                }
                accessibilityNodeInfo.setVisibleToUser(false);
            }
        }
    }

    private boolean shouldApplyAppScaleAndMagnificationSpec(float f, MagnificationSpec magnificationSpec) {
        return (f == 1.0f && (magnificationSpec == null || magnificationSpec.isNop())) ? false : true;
    }

    private class AccessibilityNodePrefetcher {
        private static final int MAX_ACCESSIBILITY_NODE_INFO_BATCH_SIZE = 50;
        private final ArrayList<View> mTempViewList;

        private AccessibilityNodePrefetcher() {
            this.mTempViewList = new ArrayList<>();
        }

        public void prefetchAccessibilityNodeInfos(View view, int i, int i2, List<AccessibilityNodeInfo> list) {
            AccessibilityNodeProvider accessibilityNodeProvider = view.getAccessibilityNodeProvider();
            if (accessibilityNodeProvider == null) {
                AccessibilityNodeInfo accessibilityNodeInfoCreateAccessibilityNodeInfo = view.createAccessibilityNodeInfo();
                if (accessibilityNodeInfoCreateAccessibilityNodeInfo != null) {
                    list.add(accessibilityNodeInfoCreateAccessibilityNodeInfo);
                    if ((i2 & 1) != 0) {
                        prefetchPredecessorsOfRealNode(view, list);
                    }
                    if ((i2 & 2) != 0) {
                        prefetchSiblingsOfRealNode(view, list);
                    }
                    if ((i2 & 4) != 0) {
                        prefetchDescendantsOfRealNode(view, list);
                        return;
                    }
                    return;
                }
                return;
            }
            AccessibilityNodeInfo accessibilityNodeInfoCreateAccessibilityNodeInfo2 = accessibilityNodeProvider.createAccessibilityNodeInfo(i);
            if (accessibilityNodeInfoCreateAccessibilityNodeInfo2 != null) {
                list.add(accessibilityNodeInfoCreateAccessibilityNodeInfo2);
                if ((i2 & 1) != 0) {
                    prefetchPredecessorsOfVirtualNode(accessibilityNodeInfoCreateAccessibilityNodeInfo2, view, accessibilityNodeProvider, list);
                }
                if ((i2 & 2) != 0) {
                    prefetchSiblingsOfVirtualNode(accessibilityNodeInfoCreateAccessibilityNodeInfo2, view, accessibilityNodeProvider, list);
                }
                if ((i2 & 4) != 0) {
                    prefetchDescendantsOfVirtualNode(accessibilityNodeInfoCreateAccessibilityNodeInfo2, accessibilityNodeProvider, list);
                }
            }
        }

        private void prefetchPredecessorsOfRealNode(View view, List<AccessibilityNodeInfo> list) {
            for (ViewParent parentForAccessibility = view.getParentForAccessibility(); (parentForAccessibility instanceof View) && list.size() < 50; parentForAccessibility = parentForAccessibility.getParentForAccessibility()) {
                AccessibilityNodeInfo accessibilityNodeInfoCreateAccessibilityNodeInfo = ((View) parentForAccessibility).createAccessibilityNodeInfo();
                if (accessibilityNodeInfoCreateAccessibilityNodeInfo != null) {
                    list.add(accessibilityNodeInfoCreateAccessibilityNodeInfo);
                }
            }
        }

        private void prefetchSiblingsOfRealNode(View view, List<AccessibilityNodeInfo> list) {
            AccessibilityNodeInfo accessibilityNodeInfoCreateAccessibilityNodeInfo;
            ViewParent parentForAccessibility = view.getParentForAccessibility();
            if (parentForAccessibility instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) parentForAccessibility;
                ArrayList<View> arrayList = this.mTempViewList;
                arrayList.clear();
                try {
                    viewGroup.addChildrenForAccessibility(arrayList);
                    int size = arrayList.size();
                    for (int i = 0; i < size; i++) {
                        if (list.size() >= 50) {
                            return;
                        }
                        View view2 = arrayList.get(i);
                        if (view2.getAccessibilityViewId() != view.getAccessibilityViewId() && AccessibilityInteractionController.this.isShown(view2)) {
                            AccessibilityNodeProvider accessibilityNodeProvider = view2.getAccessibilityNodeProvider();
                            if (accessibilityNodeProvider == null) {
                                accessibilityNodeInfoCreateAccessibilityNodeInfo = view2.createAccessibilityNodeInfo();
                            } else {
                                accessibilityNodeInfoCreateAccessibilityNodeInfo = accessibilityNodeProvider.createAccessibilityNodeInfo(-1);
                            }
                            if (accessibilityNodeInfoCreateAccessibilityNodeInfo != null) {
                                list.add(accessibilityNodeInfoCreateAccessibilityNodeInfo);
                            }
                        }
                    }
                } finally {
                    arrayList.clear();
                }
            }
        }

        private void prefetchDescendantsOfRealNode(View view, List<AccessibilityNodeInfo> list) {
            if (view instanceof ViewGroup) {
                HashMap map = new HashMap();
                ArrayList<View> arrayList = this.mTempViewList;
                arrayList.clear();
                try {
                    view.addChildrenForAccessibility(arrayList);
                    int size = arrayList.size();
                    for (int i = 0; i < size; i++) {
                        if (list.size() >= 50) {
                            return;
                        }
                        View view2 = arrayList.get(i);
                        if (AccessibilityInteractionController.this.isShown(view2)) {
                            AccessibilityNodeProvider accessibilityNodeProvider = view2.getAccessibilityNodeProvider();
                            if (accessibilityNodeProvider == null) {
                                AccessibilityNodeInfo accessibilityNodeInfoCreateAccessibilityNodeInfo = view2.createAccessibilityNodeInfo();
                                if (accessibilityNodeInfoCreateAccessibilityNodeInfo != null) {
                                    list.add(accessibilityNodeInfoCreateAccessibilityNodeInfo);
                                    map.put(view2, null);
                                }
                            } else {
                                AccessibilityNodeInfo accessibilityNodeInfoCreateAccessibilityNodeInfo2 = accessibilityNodeProvider.createAccessibilityNodeInfo(-1);
                                if (accessibilityNodeInfoCreateAccessibilityNodeInfo2 != null) {
                                    list.add(accessibilityNodeInfoCreateAccessibilityNodeInfo2);
                                    map.put(view2, accessibilityNodeInfoCreateAccessibilityNodeInfo2);
                                }
                            }
                        }
                    }
                    arrayList.clear();
                    if (list.size() < 50) {
                        for (Map.Entry entry : map.entrySet()) {
                            View view3 = (View) entry.getKey();
                            AccessibilityNodeInfo accessibilityNodeInfo = (AccessibilityNodeInfo) entry.getValue();
                            if (accessibilityNodeInfo == null) {
                                prefetchDescendantsOfRealNode(view3, list);
                            } else {
                                prefetchDescendantsOfVirtualNode(accessibilityNodeInfo, view3.getAccessibilityNodeProvider(), list);
                            }
                        }
                    }
                } finally {
                    arrayList.clear();
                }
            }
        }

        private void prefetchPredecessorsOfVirtualNode(AccessibilityNodeInfo accessibilityNodeInfo, View view, AccessibilityNodeProvider accessibilityNodeProvider, List<AccessibilityNodeInfo> list) {
            long parentNodeId = accessibilityNodeInfo.getParentNodeId();
            int accessibilityViewId = AccessibilityNodeInfo.getAccessibilityViewId(parentNodeId);
            while (accessibilityViewId != -1 && list.size() < 50) {
                int virtualDescendantId = AccessibilityNodeInfo.getVirtualDescendantId(parentNodeId);
                if (virtualDescendantId != -1 || accessibilityViewId == view.getAccessibilityViewId()) {
                    AccessibilityNodeInfo accessibilityNodeInfoCreateAccessibilityNodeInfo = accessibilityNodeProvider.createAccessibilityNodeInfo(virtualDescendantId);
                    if (accessibilityNodeInfoCreateAccessibilityNodeInfo != null) {
                        list.add(accessibilityNodeInfoCreateAccessibilityNodeInfo);
                    }
                    parentNodeId = accessibilityNodeInfoCreateAccessibilityNodeInfo.getParentNodeId();
                    accessibilityViewId = AccessibilityNodeInfo.getAccessibilityViewId(parentNodeId);
                } else {
                    prefetchPredecessorsOfRealNode(view, list);
                    return;
                }
            }
        }

        private void prefetchSiblingsOfVirtualNode(AccessibilityNodeInfo accessibilityNodeInfo, View view, AccessibilityNodeProvider accessibilityNodeProvider, List<AccessibilityNodeInfo> list) {
            AccessibilityNodeInfo accessibilityNodeInfoCreateAccessibilityNodeInfo;
            long parentNodeId = accessibilityNodeInfo.getParentNodeId();
            int accessibilityViewId = AccessibilityNodeInfo.getAccessibilityViewId(parentNodeId);
            int virtualDescendantId = AccessibilityNodeInfo.getVirtualDescendantId(parentNodeId);
            if (virtualDescendantId != -1 || accessibilityViewId == view.getAccessibilityViewId()) {
                AccessibilityNodeInfo accessibilityNodeInfoCreateAccessibilityNodeInfo2 = accessibilityNodeProvider.createAccessibilityNodeInfo(virtualDescendantId);
                if (accessibilityNodeInfoCreateAccessibilityNodeInfo2 != null) {
                    SparseLongArray childNodeIds = accessibilityNodeInfoCreateAccessibilityNodeInfo2.getChildNodeIds();
                    int size = childNodeIds.size();
                    for (int i = 0; i < size && list.size() < 50; i++) {
                        long j = childNodeIds.get(i);
                        if (j != accessibilityNodeInfo.getSourceNodeId() && (accessibilityNodeInfoCreateAccessibilityNodeInfo = accessibilityNodeProvider.createAccessibilityNodeInfo(AccessibilityNodeInfo.getVirtualDescendantId(j))) != null) {
                            list.add(accessibilityNodeInfoCreateAccessibilityNodeInfo);
                        }
                    }
                    return;
                }
                return;
            }
            prefetchSiblingsOfRealNode(view, list);
        }

        private void prefetchDescendantsOfVirtualNode(AccessibilityNodeInfo accessibilityNodeInfo, AccessibilityNodeProvider accessibilityNodeProvider, List<AccessibilityNodeInfo> list) {
            SparseLongArray childNodeIds = accessibilityNodeInfo.getChildNodeIds();
            int size = list.size();
            int size2 = childNodeIds.size();
            for (int i = 0; i < size2; i++) {
                if (list.size() >= 50) {
                    return;
                }
                AccessibilityNodeInfo accessibilityNodeInfoCreateAccessibilityNodeInfo = accessibilityNodeProvider.createAccessibilityNodeInfo(AccessibilityNodeInfo.getVirtualDescendantId(childNodeIds.get(i)));
                if (accessibilityNodeInfoCreateAccessibilityNodeInfo != null) {
                    list.add(accessibilityNodeInfoCreateAccessibilityNodeInfo);
                }
            }
            if (list.size() < 50) {
                int size3 = list.size() - size;
                for (int i2 = 0; i2 < size3; i2++) {
                    prefetchDescendantsOfVirtualNode(list.get(size + i2), accessibilityNodeProvider, list);
                }
            }
        }
    }

    private class PrivateHandler extends Handler {
        private static final int MSG_FIND_ACCESSIBLITY_NODE_INFOS_BY_VIEW_ID = 3;
        private static final int MSG_FIND_ACCESSIBLITY_NODE_INFO_BY_ACCESSIBILITY_ID = 2;
        private static final int MSG_FIND_ACCESSIBLITY_NODE_INFO_BY_TEXT = 4;
        private static final int MSG_FIND_FOCUS = 5;
        private static final int MSG_FOCUS_SEARCH = 6;
        private static final int MSG_PERFORM_ACCESSIBILITY_ACTION = 1;

        public PrivateHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public String getMessageName(Message message) {
            int i = message.what;
            switch (i) {
                case 1:
                    return "MSG_PERFORM_ACCESSIBILITY_ACTION";
                case 2:
                    return "MSG_FIND_ACCESSIBLITY_NODE_INFO_BY_ACCESSIBILITY_ID";
                case 3:
                    return "MSG_FIND_ACCESSIBLITY_NODE_INFOS_BY_VIEW_ID";
                case 4:
                    return "MSG_FIND_ACCESSIBLITY_NODE_INFO_BY_TEXT";
                case 5:
                    return "MSG_FIND_FOCUS";
                case 6:
                    return "MSG_FOCUS_SEARCH";
                default:
                    throw new IllegalArgumentException("Unknown message type: " + i);
            }
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            switch (i) {
                case 1:
                    AccessibilityInteractionController.this.perfromAccessibilityActionUiThread(message);
                    return;
                case 2:
                    AccessibilityInteractionController.this.findAccessibilityNodeInfoByAccessibilityIdUiThread(message);
                    return;
                case 3:
                    AccessibilityInteractionController.this.findAccessibilityNodeInfosByViewIdUiThread(message);
                    return;
                case 4:
                    AccessibilityInteractionController.this.findAccessibilityNodeInfosByTextUiThread(message);
                    return;
                case 5:
                    AccessibilityInteractionController.this.findFocusUiThread(message);
                    return;
                case 6:
                    AccessibilityInteractionController.this.focusSearchUiThread(message);
                    return;
                default:
                    throw new IllegalArgumentException("Unknown message type: " + i);
            }
        }
    }

    private final class AddNodeInfosForViewId implements Predicate<View> {
        private List<AccessibilityNodeInfo> mInfos;
        private int mViewId;

        private AddNodeInfosForViewId() {
            this.mViewId = -1;
        }

        public void init(int i, List<AccessibilityNodeInfo> list) {
            this.mViewId = i;
            this.mInfos = list;
        }

        public void reset() {
            this.mViewId = -1;
            this.mInfos = null;
        }

        public boolean apply(View view) {
            if (view.getId() != this.mViewId || !AccessibilityInteractionController.this.isShown(view)) {
                return false;
            }
            this.mInfos.add(view.createAccessibilityNodeInfo());
            return false;
        }
    }
}
