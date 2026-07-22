package android.print;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.print.IPrintDocumentAdapter;
import android.print.IPrintJobStateChangeListener;
import android.print.PrintDocumentAdapter;
import android.printservice.PrintServiceInfo;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import com.android.internal.os.SomeArgs;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import libcore.io.IoUtils;

/* JADX INFO: loaded from: classes.dex */
public final class PrintManager {
    public static final String ACTION_PRINT_DIALOG = "android.print.PRINT_DIALOG";
    public static final int APP_ID_ANY = -2;
    private static final boolean DEBUG = false;
    public static final String EXTRA_PRINT_DIALOG_INTENT = "android.print.intent.extra.EXTRA_PRINT_DIALOG_INTENT";
    public static final String EXTRA_PRINT_DOCUMENT_ADAPTER = "android.print.intent.extra.EXTRA_PRINT_DOCUMENT_ADAPTER";
    public static final String EXTRA_PRINT_JOB = "android.print.intent.extra.EXTRA_PRINT_JOB";
    private static final String LOG_TAG = "PrintManager";
    private static final int MSG_NOTIFY_PRINT_JOB_STATE_CHANGED = 1;
    private final int mAppId;
    private final Context mContext;
    private final Handler mHandler;
    private Map<PrintJobStateChangeListener, PrintJobStateChangeListenerWrapper> mPrintJobStateChangeListeners;
    private final IPrintManager mService;
    private final int mUserId;

    public interface PrintJobStateChangeListener {
        void onPrintJobStateChanged(PrintJobId printJobId);
    }

    public PrintManager(Context context, IPrintManager iPrintManager, int i, int i2) {
        this.mContext = context;
        this.mService = iPrintManager;
        this.mUserId = i;
        this.mAppId = i2;
        this.mHandler = new Handler(context.getMainLooper(), null, false) { // from class: android.print.PrintManager.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                if (message.what != 1) {
                    return;
                }
                SomeArgs someArgs = (SomeArgs) message.obj;
                PrintJobStateChangeListener listener = ((PrintJobStateChangeListenerWrapper) someArgs.arg1).getListener();
                if (listener != null) {
                    listener.onPrintJobStateChanged((PrintJobId) someArgs.arg2);
                }
                someArgs.recycle();
            }
        };
    }

    public PrintManager getGlobalPrintManagerForUser(int i) {
        return new PrintManager(this.mContext, this.mService, i, -2);
    }

    PrintJobInfo getPrintJobInfo(PrintJobId printJobId) {
        try {
            return this.mService.getPrintJobInfo(printJobId, this.mAppId, this.mUserId);
        } catch (RemoteException e) {
            Log.e(LOG_TAG, "Error getting a print job info:" + printJobId, e);
            return null;
        }
    }

    public void addPrintJobStateChangeListener(PrintJobStateChangeListener printJobStateChangeListener) {
        if (this.mPrintJobStateChangeListeners == null) {
            this.mPrintJobStateChangeListeners = new ArrayMap();
        }
        PrintJobStateChangeListenerWrapper printJobStateChangeListenerWrapper = new PrintJobStateChangeListenerWrapper(printJobStateChangeListener, this.mHandler);
        try {
            this.mService.addPrintJobStateChangeListener(printJobStateChangeListenerWrapper, this.mAppId, this.mUserId);
            this.mPrintJobStateChangeListeners.put(printJobStateChangeListener, printJobStateChangeListenerWrapper);
        } catch (RemoteException e) {
            Log.e(LOG_TAG, "Error adding print job state change listener", e);
        }
    }

    public void removePrintJobStateChangeListener(PrintJobStateChangeListener printJobStateChangeListener) {
        PrintJobStateChangeListenerWrapper printJobStateChangeListenerWrapperRemove;
        Map<PrintJobStateChangeListener, PrintJobStateChangeListenerWrapper> map = this.mPrintJobStateChangeListeners;
        if (map == null || (printJobStateChangeListenerWrapperRemove = map.remove(printJobStateChangeListener)) == null) {
            return;
        }
        if (this.mPrintJobStateChangeListeners.isEmpty()) {
            this.mPrintJobStateChangeListeners = null;
        }
        printJobStateChangeListenerWrapperRemove.destroy();
        try {
            this.mService.removePrintJobStateChangeListener(printJobStateChangeListenerWrapperRemove, this.mUserId);
        } catch (RemoteException e) {
            Log.e(LOG_TAG, "Error removing print job state change listener", e);
        }
    }

    public PrintJob getPrintJob(PrintJobId printJobId) {
        try {
            PrintJobInfo printJobInfo = this.mService.getPrintJobInfo(printJobId, this.mAppId, this.mUserId);
            if (printJobInfo != null) {
                return new PrintJob(printJobInfo, this);
            }
            return null;
        } catch (RemoteException e) {
            Log.e(LOG_TAG, "Error getting print job", e);
            return null;
        }
    }

    public List<PrintJob> getPrintJobs() {
        try {
            List<PrintJobInfo> printJobInfos = this.mService.getPrintJobInfos(this.mAppId, this.mUserId);
            if (printJobInfos == null) {
                return Collections.emptyList();
            }
            int size = printJobInfos.size();
            ArrayList arrayList = new ArrayList(size);
            for (int i = 0; i < size; i++) {
                arrayList.add(new PrintJob(printJobInfos.get(i), this));
            }
            return arrayList;
        } catch (RemoteException e) {
            Log.e(LOG_TAG, "Error getting print jobs", e);
            return Collections.emptyList();
        }
    }

    void cancelPrintJob(PrintJobId printJobId) {
        try {
            this.mService.cancelPrintJob(printJobId, this.mAppId, this.mUserId);
        } catch (RemoteException e) {
            Log.e(LOG_TAG, "Error cancleing a print job: " + printJobId, e);
        }
    }

    void restartPrintJob(PrintJobId printJobId) {
        try {
            this.mService.restartPrintJob(printJobId, this.mAppId, this.mUserId);
        } catch (RemoteException e) {
            Log.e(LOG_TAG, "Error restarting a print job: " + printJobId, e);
        }
    }

    public PrintJob print(String str, PrintDocumentAdapter printDocumentAdapter, PrintAttributes printAttributes) {
        if (!(this.mContext instanceof Activity)) {
            throw new IllegalStateException("Can print only from an activity");
        }
        if (TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException("printJobName cannot be empty");
        }
        if (printDocumentAdapter == null) {
            throw new IllegalArgumentException("documentAdapter cannot be null");
        }
        try {
            Bundle bundlePrint = this.mService.print(str, new PrintDocumentAdapterDelegate((Activity) this.mContext, printDocumentAdapter), printAttributes, this.mContext.getPackageName(), this.mAppId, this.mUserId);
            if (bundlePrint != null) {
                PrintJobInfo printJobInfo = (PrintJobInfo) bundlePrint.getParcelable(EXTRA_PRINT_JOB);
                IntentSender intentSender = (IntentSender) bundlePrint.getParcelable(EXTRA_PRINT_DIALOG_INTENT);
                if (printJobInfo == null || intentSender == null) {
                    return null;
                }
                try {
                    this.mContext.startIntentSender(intentSender, null, 0, 0, 0);
                    return new PrintJob(printJobInfo, this);
                } catch (IntentSender.SendIntentException e) {
                    Log.e(LOG_TAG, "Couldn't start print job config activity.", e);
                    return null;
                }
            }
        } catch (RemoteException e2) {
            Log.e(LOG_TAG, "Error creating a print job", e2);
        }
        return null;
    }

    public List<PrintServiceInfo> getEnabledPrintServices() {
        try {
            List<PrintServiceInfo> enabledPrintServices = this.mService.getEnabledPrintServices(this.mUserId);
            if (enabledPrintServices != null) {
                return enabledPrintServices;
            }
        } catch (RemoteException e) {
            Log.e(LOG_TAG, "Error getting the enabled print services", e);
        }
        return Collections.emptyList();
    }

    public List<PrintServiceInfo> getInstalledPrintServices() {
        try {
            List<PrintServiceInfo> installedPrintServices = this.mService.getInstalledPrintServices(this.mUserId);
            if (installedPrintServices != null) {
                return installedPrintServices;
            }
        } catch (RemoteException e) {
            Log.e(LOG_TAG, "Error getting the installed print services", e);
        }
        return Collections.emptyList();
    }

    public PrinterDiscoverySession createPrinterDiscoverySession() {
        return new PrinterDiscoverySession(this.mService, this.mContext, this.mUserId);
    }

    private static final class PrintDocumentAdapterDelegate extends IPrintDocumentAdapter.Stub implements Application.ActivityLifecycleCallbacks {
        private Activity mActivity;
        private boolean mDestroyed;
        private PrintDocumentAdapter mDocumentAdapter;
        private boolean mFinishRequested;
        private boolean mFinished;
        private Handler mHandler;
        private LayoutSpec mLastLayoutSpec;
        private WriteSpec mLastWriteSpec;
        private CancellationSignal mLayoutOrWriteCancellation;
        private final Object mLock = new Object();
        private IPrintDocumentAdapterObserver mObserver;
        private boolean mStartReqeusted;
        private boolean mStarted;

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivityCreated(Activity activity, Bundle bundle) {
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivityPaused(Activity activity) {
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivityResumed(Activity activity) {
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivityStarted(Activity activity) {
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivityStopped(Activity activity) {
        }

        public PrintDocumentAdapterDelegate(Activity activity, PrintDocumentAdapter printDocumentAdapter) {
            this.mActivity = activity;
            this.mDocumentAdapter = printDocumentAdapter;
            this.mHandler = new MyHandler(this.mActivity.getMainLooper());
            this.mActivity.getApplication().registerActivityLifecycleCallbacks(this);
        }

        @Override // android.print.IPrintDocumentAdapter
        public void setObserver(IPrintDocumentAdapterObserver iPrintDocumentAdapterObserver) {
            boolean z;
            synchronized (this.mLock) {
                z = this.mDestroyed;
                if (!z) {
                    this.mObserver = iPrintDocumentAdapterObserver;
                }
            }
            if (z) {
                try {
                    iPrintDocumentAdapterObserver.onDestroy();
                } catch (RemoteException e) {
                    Log.e(PrintManager.LOG_TAG, "Error announcing destroyed state", e);
                }
            }
        }

        @Override // android.print.IPrintDocumentAdapter
        public void start() {
            synchronized (this.mLock) {
                if (!this.mStartReqeusted && !this.mFinishRequested && !this.mDestroyed) {
                    this.mStartReqeusted = true;
                    doPendingWorkLocked();
                }
            }
        }

        @Override // android.print.IPrintDocumentAdapter
        public void layout(PrintAttributes printAttributes, PrintAttributes printAttributes2, ILayoutResultCallback iLayoutResultCallback, Bundle bundle, int i) {
            synchronized (this.mLock) {
                boolean z = this.mDestroyed;
                if (this.mStartReqeusted && !this.mFinishRequested && !z) {
                    WriteSpec writeSpec = this.mLastWriteSpec;
                    if (writeSpec != null) {
                        IoUtils.closeQuietly(writeSpec.fd);
                        this.mLastWriteSpec = null;
                    }
                    LayoutSpec layoutSpec = new LayoutSpec();
                    this.mLastLayoutSpec = layoutSpec;
                    layoutSpec.callback = iLayoutResultCallback;
                    this.mLastLayoutSpec.oldAttributes = printAttributes;
                    this.mLastLayoutSpec.newAttributes = printAttributes2;
                    this.mLastLayoutSpec.metadata = bundle;
                    this.mLastLayoutSpec.sequence = i;
                    if (cancelPreviousCancellableOperationLocked()) {
                        return;
                    } else {
                        doPendingWorkLocked();
                    }
                }
                if (z) {
                    try {
                        iLayoutResultCallback.onLayoutFailed(null, i);
                    } catch (RemoteException e) {
                        Log.i(PrintManager.LOG_TAG, "Error notifying for cancelled layout", e);
                    }
                }
            }
        }

        @Override // android.print.IPrintDocumentAdapter
        public void write(PageRange[] pageRangeArr, ParcelFileDescriptor parcelFileDescriptor, IWriteResultCallback iWriteResultCallback, int i) {
            synchronized (this.mLock) {
                boolean z = this.mDestroyed;
                if (this.mStartReqeusted && !this.mFinishRequested && !z) {
                    WriteSpec writeSpec = this.mLastWriteSpec;
                    if (writeSpec != null) {
                        IoUtils.closeQuietly(writeSpec.fd);
                        this.mLastWriteSpec = null;
                    }
                    WriteSpec writeSpec2 = new WriteSpec();
                    this.mLastWriteSpec = writeSpec2;
                    writeSpec2.callback = iWriteResultCallback;
                    this.mLastWriteSpec.pages = pageRangeArr;
                    this.mLastWriteSpec.fd = parcelFileDescriptor;
                    this.mLastWriteSpec.sequence = i;
                    if (cancelPreviousCancellableOperationLocked()) {
                        return;
                    } else {
                        doPendingWorkLocked();
                    }
                }
                if (z) {
                    try {
                        iWriteResultCallback.onWriteFailed(null, i);
                    } catch (RemoteException e) {
                        Log.i(PrintManager.LOG_TAG, "Error notifying for cancelled write", e);
                    }
                }
            }
        }

        @Override // android.print.IPrintDocumentAdapter
        public void finish() {
            synchronized (this.mLock) {
                if (this.mStartReqeusted && !this.mFinishRequested && !this.mDestroyed) {
                    this.mFinishRequested = true;
                    if (this.mLastLayoutSpec == null && this.mLastWriteSpec == null) {
                        doPendingWorkLocked();
                    }
                }
            }
        }

        @Override // android.print.IPrintDocumentAdapter
        public void cancel() {
            if (!this.mStartReqeusted || this.mFinishRequested || this.mDestroyed) {
                return;
            }
            synchronized (this.mLock) {
                cancelPreviousCancellableOperationLocked();
            }
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivityDestroyed(Activity activity) {
            IPrintDocumentAdapterObserver iPrintDocumentAdapterObserver;
            synchronized (this.mLock) {
                iPrintDocumentAdapterObserver = null;
                if (activity == this.mActivity) {
                    this.mDestroyed = true;
                    iPrintDocumentAdapterObserver = this.mObserver;
                    clearLocked();
                } else {
                    activity = null;
                }
            }
            if (iPrintDocumentAdapterObserver != null) {
                activity.getApplication().unregisterActivityLifecycleCallbacks(this);
                try {
                    iPrintDocumentAdapterObserver.onDestroy();
                } catch (RemoteException e) {
                    Log.e(PrintManager.LOG_TAG, "Error announcing destroyed state", e);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean isFinished() {
            return this.mDocumentAdapter == null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearLocked() {
            this.mActivity = null;
            this.mDocumentAdapter = null;
            this.mHandler = null;
            this.mLayoutOrWriteCancellation = null;
            this.mLastLayoutSpec = null;
            WriteSpec writeSpec = this.mLastWriteSpec;
            if (writeSpec != null) {
                IoUtils.closeQuietly(writeSpec.fd);
                this.mLastWriteSpec = null;
            }
        }

        private boolean cancelPreviousCancellableOperationLocked() {
            CancellationSignal cancellationSignal = this.mLayoutOrWriteCancellation;
            if (cancellationSignal == null) {
                return false;
            }
            cancellationSignal.cancel();
            return true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void doPendingWorkLocked() {
            if (this.mStartReqeusted && !this.mStarted) {
                this.mStarted = true;
                this.mHandler.sendEmptyMessage(1);
                return;
            }
            if (this.mLastLayoutSpec != null) {
                this.mHandler.sendEmptyMessage(2);
                return;
            }
            if (this.mLastWriteSpec != null) {
                this.mHandler.sendEmptyMessage(3);
            } else {
                if (!this.mFinishRequested || this.mFinished) {
                    return;
                }
                this.mFinished = true;
                this.mHandler.sendEmptyMessage(4);
            }
        }

        private class LayoutSpec {
            ILayoutResultCallback callback;
            Bundle metadata;
            PrintAttributes newAttributes;
            PrintAttributes oldAttributes;
            int sequence;

            private LayoutSpec() {
            }
        }

        private class WriteSpec {
            IWriteResultCallback callback;
            ParcelFileDescriptor fd;
            PageRange[] pages;
            int sequence;

            private WriteSpec() {
            }
        }

        private final class MyHandler extends Handler {
            public static final int MSG_FINISH = 4;
            public static final int MSG_LAYOUT = 2;
            public static final int MSG_START = 1;
            public static final int MSG_WRITE = 3;

            public MyHandler(Looper looper) {
                super(looper, null, true);
            }

            @Override // android.os.Handler
            public void handleMessage(Message message) {
                PrintDocumentAdapter printDocumentAdapter;
                PrintDocumentAdapter printDocumentAdapter2;
                LayoutSpec layoutSpec;
                CancellationSignal cancellationSignal;
                PrintDocumentAdapter printDocumentAdapter3;
                WriteSpec writeSpec;
                CancellationSignal cancellationSignal2;
                PrintDocumentAdapter printDocumentAdapter4;
                Activity activity;
                if (PrintDocumentAdapterDelegate.this.isFinished()) {
                    return;
                }
                int i = message.what;
                if (i == 1) {
                    synchronized (PrintDocumentAdapterDelegate.this.mLock) {
                        printDocumentAdapter = PrintDocumentAdapterDelegate.this.mDocumentAdapter;
                    }
                    if (printDocumentAdapter != null) {
                        printDocumentAdapter.onStart();
                        return;
                    }
                    return;
                }
                if (i == 2) {
                    synchronized (PrintDocumentAdapterDelegate.this.mLock) {
                        printDocumentAdapter2 = PrintDocumentAdapterDelegate.this.mDocumentAdapter;
                        layoutSpec = PrintDocumentAdapterDelegate.this.mLastLayoutSpec;
                        PrintDocumentAdapterDelegate.this.mLastLayoutSpec = null;
                        cancellationSignal = new CancellationSignal();
                        PrintDocumentAdapterDelegate.this.mLayoutOrWriteCancellation = cancellationSignal;
                    }
                    if (layoutSpec == null || printDocumentAdapter2 == null) {
                        return;
                    }
                    printDocumentAdapter2.onLayout(layoutSpec.oldAttributes, layoutSpec.newAttributes, cancellationSignal, PrintDocumentAdapterDelegate.this.new MyLayoutResultCallback(layoutSpec.callback, layoutSpec.sequence), layoutSpec.metadata);
                    return;
                }
                if (i == 3) {
                    synchronized (PrintDocumentAdapterDelegate.this.mLock) {
                        printDocumentAdapter3 = PrintDocumentAdapterDelegate.this.mDocumentAdapter;
                        writeSpec = PrintDocumentAdapterDelegate.this.mLastWriteSpec;
                        PrintDocumentAdapterDelegate.this.mLastWriteSpec = null;
                        cancellationSignal2 = new CancellationSignal();
                        PrintDocumentAdapterDelegate.this.mLayoutOrWriteCancellation = cancellationSignal2;
                    }
                    if (writeSpec == null || printDocumentAdapter3 == null) {
                        return;
                    }
                    printDocumentAdapter3.onWrite(writeSpec.pages, writeSpec.fd, cancellationSignal2, PrintDocumentAdapterDelegate.this.new MyWriteResultCallback(writeSpec.callback, writeSpec.fd, writeSpec.sequence));
                    return;
                }
                if (i == 4) {
                    synchronized (PrintDocumentAdapterDelegate.this.mLock) {
                        printDocumentAdapter4 = PrintDocumentAdapterDelegate.this.mDocumentAdapter;
                        activity = PrintDocumentAdapterDelegate.this.mActivity;
                        PrintDocumentAdapterDelegate.this.clearLocked();
                    }
                    if (printDocumentAdapter4 != null) {
                        printDocumentAdapter4.onFinish();
                    }
                    if (activity != null) {
                        activity.getApplication().unregisterActivityLifecycleCallbacks(PrintDocumentAdapterDelegate.this);
                        return;
                    }
                    return;
                }
                throw new IllegalArgumentException("Unknown message: " + message.what);
            }
        }

        private final class MyLayoutResultCallback extends PrintDocumentAdapter.LayoutResultCallback {
            private ILayoutResultCallback mCallback;
            private final int mSequence;

            public MyLayoutResultCallback(ILayoutResultCallback iLayoutResultCallback, int i) {
                this.mCallback = iLayoutResultCallback;
                this.mSequence = i;
            }

            @Override // android.print.PrintDocumentAdapter.LayoutResultCallback
            public void onLayoutFinished(PrintDocumentInfo printDocumentInfo, boolean z) {
                Objects.requireNonNull(printDocumentInfo, "document info cannot be null");
                synchronized (PrintDocumentAdapterDelegate.this.mLock) {
                    if (PrintDocumentAdapterDelegate.this.mDestroyed) {
                        Log.e(PrintManager.LOG_TAG, "PrintDocumentAdapter is destroyed. Did you finish the printing activity before print completion?");
                        return;
                    }
                    ILayoutResultCallback iLayoutResultCallback = this.mCallback;
                    clearLocked();
                    if (iLayoutResultCallback != null) {
                        try {
                            iLayoutResultCallback.onLayoutFinished(printDocumentInfo, z, this.mSequence);
                        } catch (RemoteException e) {
                            Log.e(PrintManager.LOG_TAG, "Error calling onLayoutFinished", e);
                        }
                    }
                }
            }

            @Override // android.print.PrintDocumentAdapter.LayoutResultCallback
            public void onLayoutFailed(CharSequence charSequence) {
                synchronized (PrintDocumentAdapterDelegate.this.mLock) {
                    if (PrintDocumentAdapterDelegate.this.mDestroyed) {
                        Log.e(PrintManager.LOG_TAG, "PrintDocumentAdapter is destroyed. Did you finish the printing activity before print completion?");
                        return;
                    }
                    ILayoutResultCallback iLayoutResultCallback = this.mCallback;
                    clearLocked();
                    if (iLayoutResultCallback != null) {
                        try {
                            iLayoutResultCallback.onLayoutFailed(charSequence, this.mSequence);
                        } catch (RemoteException e) {
                            Log.e(PrintManager.LOG_TAG, "Error calling onLayoutFailed", e);
                        }
                    }
                }
            }

            @Override // android.print.PrintDocumentAdapter.LayoutResultCallback
            public void onLayoutCancelled() {
                synchronized (PrintDocumentAdapterDelegate.this.mLock) {
                    if (PrintDocumentAdapterDelegate.this.mDestroyed) {
                        Log.e(PrintManager.LOG_TAG, "PrintDocumentAdapter is destroyed. Did you finish the printing activity before print completion?");
                    } else {
                        clearLocked();
                    }
                }
            }

            private void clearLocked() {
                PrintDocumentAdapterDelegate.this.mLayoutOrWriteCancellation = null;
                this.mCallback = null;
                PrintDocumentAdapterDelegate.this.doPendingWorkLocked();
            }
        }

        private final class MyWriteResultCallback extends PrintDocumentAdapter.WriteResultCallback {
            private IWriteResultCallback mCallback;
            private ParcelFileDescriptor mFd;
            private int mSequence;

            public MyWriteResultCallback(IWriteResultCallback iWriteResultCallback, ParcelFileDescriptor parcelFileDescriptor, int i) {
                this.mFd = parcelFileDescriptor;
                this.mSequence = i;
                this.mCallback = iWriteResultCallback;
            }

            @Override // android.print.PrintDocumentAdapter.WriteResultCallback
            public void onWriteFinished(PageRange[] pageRangeArr) {
                synchronized (PrintDocumentAdapterDelegate.this.mLock) {
                    if (PrintDocumentAdapterDelegate.this.mDestroyed) {
                        Log.e(PrintManager.LOG_TAG, "PrintDocumentAdapter is destroyed. Did you finish the printing activity before print completion?");
                        return;
                    }
                    IWriteResultCallback iWriteResultCallback = this.mCallback;
                    clearLocked();
                    if (pageRangeArr == null) {
                        throw new IllegalArgumentException("pages cannot be null");
                    }
                    if (pageRangeArr.length == 0) {
                        throw new IllegalArgumentException("pages cannot be empty");
                    }
                    if (iWriteResultCallback != null) {
                        try {
                            iWriteResultCallback.onWriteFinished(pageRangeArr, this.mSequence);
                        } catch (RemoteException e) {
                            Log.e(PrintManager.LOG_TAG, "Error calling onWriteFinished", e);
                        }
                    }
                }
            }

            @Override // android.print.PrintDocumentAdapter.WriteResultCallback
            public void onWriteFailed(CharSequence charSequence) {
                synchronized (PrintDocumentAdapterDelegate.this.mLock) {
                    if (PrintDocumentAdapterDelegate.this.mDestroyed) {
                        Log.e(PrintManager.LOG_TAG, "PrintDocumentAdapter is destroyed. Did you finish the printing activity before print completion?");
                        return;
                    }
                    IWriteResultCallback iWriteResultCallback = this.mCallback;
                    clearLocked();
                    if (iWriteResultCallback != null) {
                        try {
                            iWriteResultCallback.onWriteFailed(charSequence, this.mSequence);
                        } catch (RemoteException e) {
                            Log.e(PrintManager.LOG_TAG, "Error calling onWriteFailed", e);
                        }
                    }
                }
            }

            @Override // android.print.PrintDocumentAdapter.WriteResultCallback
            public void onWriteCancelled() {
                synchronized (PrintDocumentAdapterDelegate.this.mLock) {
                    if (PrintDocumentAdapterDelegate.this.mDestroyed) {
                        Log.e(PrintManager.LOG_TAG, "PrintDocumentAdapter is destroyed. Did you finish the printing activity before print completion?");
                    } else {
                        clearLocked();
                    }
                }
            }

            private void clearLocked() {
                PrintDocumentAdapterDelegate.this.mLayoutOrWriteCancellation = null;
                IoUtils.closeQuietly(this.mFd);
                this.mCallback = null;
                this.mFd = null;
                PrintDocumentAdapterDelegate.this.doPendingWorkLocked();
            }
        }
    }

    private static final class PrintJobStateChangeListenerWrapper extends IPrintJobStateChangeListener.Stub {
        private final WeakReference<Handler> mWeakHandler;
        private final WeakReference<PrintJobStateChangeListener> mWeakListener;

        public PrintJobStateChangeListenerWrapper(PrintJobStateChangeListener printJobStateChangeListener, Handler handler) {
            this.mWeakListener = new WeakReference<>(printJobStateChangeListener);
            this.mWeakHandler = new WeakReference<>(handler);
        }

        @Override // android.print.IPrintJobStateChangeListener
        public void onPrintJobStateChanged(PrintJobId printJobId) {
            Handler handler = this.mWeakHandler.get();
            PrintJobStateChangeListener printJobStateChangeListener = this.mWeakListener.get();
            if (handler == null || printJobStateChangeListener == null) {
                return;
            }
            SomeArgs someArgsObtain = SomeArgs.obtain();
            someArgsObtain.arg1 = this;
            someArgsObtain.arg2 = printJobId;
            handler.obtainMessage(1, someArgsObtain).sendToTarget();
        }

        public void destroy() {
            this.mWeakListener.clear();
        }

        public PrintJobStateChangeListener getListener() {
            return this.mWeakListener.get();
        }
    }
}
