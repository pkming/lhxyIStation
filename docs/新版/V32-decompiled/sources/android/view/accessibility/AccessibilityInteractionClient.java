package android.view.accessibility;

import android.accessibilityservice.IAccessibilityServiceConnection;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseLongArray;
import android.view.accessibility.IAccessibilityInteractionConnectionCallback;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/* JADX INFO: loaded from: classes.dex */
public final class AccessibilityInteractionClient extends IAccessibilityInteractionConnectionCallback.Stub {
    private static final boolean CHECK_INTEGRITY = true;
    private static final boolean DEBUG = false;
    private static final String LOG_TAG = "AccessibilityInteractionClient";
    public static final int NO_ID = -1;
    private static final long TIMEOUT_INTERACTION_MILLIS = 5000;
    private AccessibilityNodeInfo mFindAccessibilityNodeInfoResult;
    private List<AccessibilityNodeInfo> mFindAccessibilityNodeInfosResult;
    private boolean mPerformAccessibilityActionResult;
    private Message mSameThreadMessage;
    private static final Object sStaticLock = new Object();
    private static final LongSparseArray<AccessibilityInteractionClient> sClients = new LongSparseArray<>();
    private static final SparseArray<IAccessibilityServiceConnection> sConnectionCache = new SparseArray<>();
    private static final AccessibilityNodeInfoCache sAccessibilityNodeInfoCache = new AccessibilityNodeInfoCache();
    private final AtomicInteger mInteractionIdCounter = new AtomicInteger();
    private final Object mInstanceLock = new Object();
    private volatile int mInteractionId = -1;

    public static AccessibilityInteractionClient getInstance() {
        return getInstanceForThread(Thread.currentThread().getId());
    }

    public static AccessibilityInteractionClient getInstanceForThread(long j) {
        AccessibilityInteractionClient accessibilityInteractionClient;
        synchronized (sStaticLock) {
            LongSparseArray<AccessibilityInteractionClient> longSparseArray = sClients;
            accessibilityInteractionClient = longSparseArray.get(j);
            if (accessibilityInteractionClient == null) {
                accessibilityInteractionClient = new AccessibilityInteractionClient();
                longSparseArray.put(j, accessibilityInteractionClient);
            }
        }
        return accessibilityInteractionClient;
    }

    private AccessibilityInteractionClient() {
    }

    public void setSameThreadMessage(Message message) {
        synchronized (this.mInstanceLock) {
            this.mSameThreadMessage = message;
            this.mInstanceLock.notifyAll();
        }
    }

    public AccessibilityNodeInfo getRootInActiveWindow(int i) {
        return findAccessibilityNodeInfoByAccessibilityId(i, -1, AccessibilityNodeInfo.ROOT_NODE_ID, false, 4);
    }

    public AccessibilityNodeInfo findAccessibilityNodeInfoByAccessibilityId(int i, int i2, long j, boolean z, int i3) {
        AccessibilityNodeInfo accessibilityNodeInfo;
        try {
            IAccessibilityServiceConnection connection = getConnection(i);
            if (connection == null) {
                return null;
            }
            if (!z && (accessibilityNodeInfo = sAccessibilityNodeInfoCache.get(j)) != null) {
                return accessibilityNodeInfo;
            }
            int andIncrement = this.mInteractionIdCounter.getAndIncrement();
            if (!connection.findAccessibilityNodeInfoByAccessibilityId(i2, j, andIncrement, this, i3, Thread.currentThread().getId())) {
                return null;
            }
            List<AccessibilityNodeInfo> findAccessibilityNodeInfosResultAndClear = getFindAccessibilityNodeInfosResultAndClear(andIncrement);
            finalizeAndCacheAccessibilityNodeInfos(findAccessibilityNodeInfosResultAndClear, i);
            if (findAccessibilityNodeInfosResultAndClear == null || findAccessibilityNodeInfosResultAndClear.isEmpty()) {
                return null;
            }
            return findAccessibilityNodeInfosResultAndClear.get(0);
        } catch (RemoteException unused) {
            return null;
        }
    }

    public List<AccessibilityNodeInfo> findAccessibilityNodeInfosByViewId(int i, int i2, long j, String str) {
        List<AccessibilityNodeInfo> findAccessibilityNodeInfosResultAndClear;
        try {
            IAccessibilityServiceConnection connection = getConnection(i);
            if (connection != null) {
                int andIncrement = this.mInteractionIdCounter.getAndIncrement();
                if (connection.findAccessibilityNodeInfosByViewId(i2, j, str, andIncrement, this, Thread.currentThread().getId()) && (findAccessibilityNodeInfosResultAndClear = getFindAccessibilityNodeInfosResultAndClear(andIncrement)) != null) {
                    finalizeAndCacheAccessibilityNodeInfos(findAccessibilityNodeInfosResultAndClear, i);
                    return findAccessibilityNodeInfosResultAndClear;
                }
            }
        } catch (RemoteException unused) {
        }
        return Collections.emptyList();
    }

    public List<AccessibilityNodeInfo> findAccessibilityNodeInfosByText(int i, int i2, long j, String str) {
        List<AccessibilityNodeInfo> findAccessibilityNodeInfosResultAndClear;
        try {
            IAccessibilityServiceConnection connection = getConnection(i);
            if (connection != null) {
                int andIncrement = this.mInteractionIdCounter.getAndIncrement();
                if (connection.findAccessibilityNodeInfosByText(i2, j, str, andIncrement, this, Thread.currentThread().getId()) && (findAccessibilityNodeInfosResultAndClear = getFindAccessibilityNodeInfosResultAndClear(andIncrement)) != null) {
                    finalizeAndCacheAccessibilityNodeInfos(findAccessibilityNodeInfosResultAndClear, i);
                    return findAccessibilityNodeInfosResultAndClear;
                }
            }
        } catch (RemoteException unused) {
        }
        return Collections.emptyList();
    }

    public AccessibilityNodeInfo findFocus(int i, int i2, long j, int i3) {
        try {
            IAccessibilityServiceConnection connection = getConnection(i);
            if (connection == null) {
                return null;
            }
            int andIncrement = this.mInteractionIdCounter.getAndIncrement();
            if (!connection.findFocus(i2, j, i3, andIncrement, this, Thread.currentThread().getId())) {
                return null;
            }
            AccessibilityNodeInfo findAccessibilityNodeInfoResultAndClear = getFindAccessibilityNodeInfoResultAndClear(andIncrement);
            finalizeAndCacheAccessibilityNodeInfo(findAccessibilityNodeInfoResultAndClear, i);
            return findAccessibilityNodeInfoResultAndClear;
        } catch (RemoteException unused) {
            return null;
        }
    }

    public AccessibilityNodeInfo focusSearch(int i, int i2, long j, int i3) {
        try {
            IAccessibilityServiceConnection connection = getConnection(i);
            if (connection == null) {
                return null;
            }
            int andIncrement = this.mInteractionIdCounter.getAndIncrement();
            if (!connection.focusSearch(i2, j, i3, andIncrement, this, Thread.currentThread().getId())) {
                return null;
            }
            AccessibilityNodeInfo findAccessibilityNodeInfoResultAndClear = getFindAccessibilityNodeInfoResultAndClear(andIncrement);
            finalizeAndCacheAccessibilityNodeInfo(findAccessibilityNodeInfoResultAndClear, i);
            return findAccessibilityNodeInfoResultAndClear;
        } catch (RemoteException unused) {
            return null;
        }
    }

    public boolean performAccessibilityAction(int i, int i2, long j, int i3, Bundle bundle) {
        try {
            IAccessibilityServiceConnection connection = getConnection(i);
            if (connection == null) {
                return false;
            }
            int andIncrement = this.mInteractionIdCounter.getAndIncrement();
            if (connection.performAccessibilityAction(i2, j, i3, bundle, andIncrement, this, Thread.currentThread().getId())) {
                return getPerformAccessibilityActionResultAndClear(andIncrement);
            }
            return false;
        } catch (RemoteException unused) {
            return false;
        }
    }

    public void clearCache() {
        sAccessibilityNodeInfoCache.clear();
    }

    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        sAccessibilityNodeInfoCache.onAccessibilityEvent(accessibilityEvent);
    }

    private AccessibilityNodeInfo getFindAccessibilityNodeInfoResultAndClear(int i) {
        AccessibilityNodeInfo accessibilityNodeInfo;
        synchronized (this.mInstanceLock) {
            accessibilityNodeInfo = waitForResultTimedLocked(i) ? this.mFindAccessibilityNodeInfoResult : null;
            clearResultLocked();
        }
        return accessibilityNodeInfo;
    }

    @Override // android.view.accessibility.IAccessibilityInteractionConnectionCallback
    public void setFindAccessibilityNodeInfoResult(AccessibilityNodeInfo accessibilityNodeInfo, int i) {
        synchronized (this.mInstanceLock) {
            if (i > this.mInteractionId) {
                this.mFindAccessibilityNodeInfoResult = accessibilityNodeInfo;
                this.mInteractionId = i;
            }
            this.mInstanceLock.notifyAll();
        }
    }

    private List<AccessibilityNodeInfo> getFindAccessibilityNodeInfosResultAndClear(int i) {
        List<AccessibilityNodeInfo> listEmptyList;
        synchronized (this.mInstanceLock) {
            if (waitForResultTimedLocked(i)) {
                listEmptyList = this.mFindAccessibilityNodeInfosResult;
            } else {
                listEmptyList = Collections.emptyList();
            }
            clearResultLocked();
            if (Build.IS_DEBUGGABLE) {
                checkFindAccessibilityNodeInfoResultIntegrity(listEmptyList);
            }
        }
        return listEmptyList;
    }

    @Override // android.view.accessibility.IAccessibilityInteractionConnectionCallback
    public void setFindAccessibilityNodeInfosResult(List<AccessibilityNodeInfo> list, int i) {
        synchronized (this.mInstanceLock) {
            if (i > this.mInteractionId) {
                if (list != null) {
                    if (!(Binder.getCallingPid() != Process.myPid())) {
                        this.mFindAccessibilityNodeInfosResult = new ArrayList(list);
                    } else {
                        this.mFindAccessibilityNodeInfosResult = list;
                    }
                } else {
                    this.mFindAccessibilityNodeInfosResult = Collections.emptyList();
                }
                this.mInteractionId = i;
            }
            this.mInstanceLock.notifyAll();
        }
    }

    private boolean getPerformAccessibilityActionResultAndClear(int i) {
        boolean z;
        synchronized (this.mInstanceLock) {
            z = waitForResultTimedLocked(i) ? this.mPerformAccessibilityActionResult : false;
            clearResultLocked();
        }
        return z;
    }

    @Override // android.view.accessibility.IAccessibilityInteractionConnectionCallback
    public void setPerformAccessibilityActionResult(boolean z, int i) {
        synchronized (this.mInstanceLock) {
            if (i > this.mInteractionId) {
                this.mPerformAccessibilityActionResult = z;
                this.mInteractionId = i;
            }
            this.mInstanceLock.notifyAll();
        }
    }

    private void clearResultLocked() {
        this.mInteractionId = -1;
        this.mFindAccessibilityNodeInfoResult = null;
        this.mFindAccessibilityNodeInfosResult = null;
        this.mPerformAccessibilityActionResult = false;
    }

    private boolean waitForResultTimedLocked(int i) {
        long jUptimeMillis = SystemClock.uptimeMillis();
        while (true) {
            try {
                Message sameProcessMessageAndClear = getSameProcessMessageAndClear();
                if (sameProcessMessageAndClear != null) {
                    sameProcessMessageAndClear.getTarget().handleMessage(sameProcessMessageAndClear);
                }
            } catch (InterruptedException unused) {
            }
            if (this.mInteractionId == i) {
                return true;
            }
            if (this.mInteractionId > i) {
                return false;
            }
            long jUptimeMillis2 = 5000 - (SystemClock.uptimeMillis() - jUptimeMillis);
            if (jUptimeMillis2 <= 0) {
                return false;
            }
            this.mInstanceLock.wait(jUptimeMillis2);
        }
    }

    private void finalizeAndCacheAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo, int i) {
        if (accessibilityNodeInfo != null) {
            accessibilityNodeInfo.setConnectionId(i);
            accessibilityNodeInfo.setSealed(true);
            sAccessibilityNodeInfoCache.add(accessibilityNodeInfo);
        }
    }

    private void finalizeAndCacheAccessibilityNodeInfos(List<AccessibilityNodeInfo> list, int i) {
        if (list != null) {
            int size = list.size();
            for (int i2 = 0; i2 < size; i2++) {
                finalizeAndCacheAccessibilityNodeInfo(list.get(i2), i);
            }
        }
    }

    private Message getSameProcessMessageAndClear() {
        Message message;
        synchronized (this.mInstanceLock) {
            message = this.mSameThreadMessage;
            this.mSameThreadMessage = null;
        }
        return message;
    }

    public IAccessibilityServiceConnection getConnection(int i) {
        IAccessibilityServiceConnection iAccessibilityServiceConnection;
        SparseArray<IAccessibilityServiceConnection> sparseArray = sConnectionCache;
        synchronized (sparseArray) {
            iAccessibilityServiceConnection = sparseArray.get(i);
        }
        return iAccessibilityServiceConnection;
    }

    public void addConnection(int i, IAccessibilityServiceConnection iAccessibilityServiceConnection) {
        SparseArray<IAccessibilityServiceConnection> sparseArray = sConnectionCache;
        synchronized (sparseArray) {
            sparseArray.put(i, iAccessibilityServiceConnection);
        }
    }

    public void removeConnection(int i) {
        SparseArray<IAccessibilityServiceConnection> sparseArray = sConnectionCache;
        synchronized (sparseArray) {
            sparseArray.remove(i);
        }
    }

    private void checkFindAccessibilityNodeInfoResultIntegrity(List<AccessibilityNodeInfo> list) {
        if (list.size() == 0) {
            return;
        }
        AccessibilityNodeInfo accessibilityNodeInfo = list.get(0);
        int size = list.size();
        for (int i = 1; i < size; i++) {
            int i2 = i;
            while (true) {
                if (i2 < size) {
                    AccessibilityNodeInfo accessibilityNodeInfo2 = list.get(i2);
                    if (accessibilityNodeInfo.getParentNodeId() == accessibilityNodeInfo2.getSourceNodeId()) {
                        accessibilityNodeInfo = accessibilityNodeInfo2;
                        break;
                    }
                    i2++;
                }
            }
        }
        if (accessibilityNodeInfo == null) {
            Log.e(LOG_TAG, "No root.");
        }
        HashSet hashSet = new HashSet();
        LinkedList linkedList = new LinkedList();
        linkedList.add(accessibilityNodeInfo);
        while (!linkedList.isEmpty()) {
            AccessibilityNodeInfo accessibilityNodeInfo3 = (AccessibilityNodeInfo) linkedList.poll();
            if (!hashSet.add(accessibilityNodeInfo3)) {
                Log.e(LOG_TAG, "Duplicate node.");
                return;
            }
            SparseLongArray childNodeIds = accessibilityNodeInfo3.getChildNodeIds();
            int size2 = childNodeIds.size();
            for (int i3 = 0; i3 < size2; i3++) {
                long jValueAt = childNodeIds.valueAt(i3);
                for (int i4 = 0; i4 < size; i4++) {
                    AccessibilityNodeInfo accessibilityNodeInfo4 = list.get(i4);
                    if (accessibilityNodeInfo4.getSourceNodeId() == jValueAt) {
                        linkedList.add(accessibilityNodeInfo4);
                    }
                }
            }
        }
        int size3 = list.size() - hashSet.size();
        if (size3 > 0) {
            Log.e(LOG_TAG, size3 + " Disconnected nodes.");
        }
    }
}
