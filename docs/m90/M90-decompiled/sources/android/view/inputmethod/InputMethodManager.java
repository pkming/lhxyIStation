package android.view.inputmethod;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ServiceManager;
import android.os.Trace;
import android.text.style.SuggestionSpan;
import android.util.Log;
import android.util.Pools;
import android.util.PrintWriterPrinter;
import android.util.SparseArray;
import android.view.InputChannel;
import android.view.InputEvent;
import android.view.InputEventSender;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewRootImpl;
import com.android.internal.os.SomeArgs;
import com.android.internal.view.IInputConnectionWrapper;
import com.android.internal.view.IInputContext;
import com.android.internal.view.IInputMethodClient;
import com.android.internal.view.IInputMethodManager;
import com.android.internal.view.IInputMethodSession;
import com.android.internal.view.InputBindResult;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes.dex */
public final class InputMethodManager {
    public static final int CONTROL_START_INITIAL = 256;
    public static final int CONTROL_WINDOW_FIRST = 4;
    public static final int CONTROL_WINDOW_IS_TEXT_EDITOR = 2;
    public static final int CONTROL_WINDOW_VIEW_HAS_FOCUS = 1;
    static final boolean DEBUG = false;
    public static final int DISPATCH_HANDLED = 1;
    public static final int DISPATCH_IN_PROGRESS = -1;
    public static final int DISPATCH_NOT_HANDLED = 0;
    public static final int HIDE_IMPLICIT_ONLY = 1;
    public static final int HIDE_NOT_ALWAYS = 2;
    static final long INPUT_METHOD_NOT_RESPONDING_TIMEOUT = 2500;
    static final int MSG_BIND = 2;
    static final int MSG_DUMP = 1;
    static final int MSG_FLUSH_INPUT_EVENT = 7;
    static final int MSG_SEND_INPUT_EVENT = 5;
    static final int MSG_SET_ACTIVE = 4;
    static final int MSG_TIMEOUT_INPUT_EVENT = 6;
    static final int MSG_UNBIND = 3;
    static final String PENDING_EVENT_COUNTER = "aq:imm";
    public static final int RESULT_HIDDEN = 3;
    public static final int RESULT_SHOWN = 2;
    public static final int RESULT_UNCHANGED_HIDDEN = 1;
    public static final int RESULT_UNCHANGED_SHOWN = 0;
    public static final int SHOW_FORCED = 2;
    public static final int SHOW_IMPLICIT = 1;
    static final String TAG = "InputMethodManager";
    static InputMethodManager sInstance;
    CompletionInfo[] mCompletions;
    InputChannel mCurChannel;
    String mCurId;
    IInputMethodSession mCurMethod;
    View mCurRootView;
    ImeInputEventSender mCurSender;
    EditorInfo mCurrentTextBoxAttribute;
    int mCursorCandEnd;
    int mCursorCandStart;
    int mCursorSelEnd;
    int mCursorSelStart;
    final InputConnection mDummyInputConnection;
    boolean mFullscreenMode;
    final H mH;
    final IInputContext mIInputContext;
    final Looper mMainLooper;
    View mNextServedView;
    boolean mServedConnecting;
    InputConnection mServedInputConnection;
    ControlledInputConnectionWrapper mServedInputConnectionWrapper;
    View mServedView;
    final IInputMethodManager mService;
    boolean mActive = false;
    boolean mHasBeenInactive = true;
    Rect mTmpCursorRect = new Rect();
    Rect mCursorRect = new Rect();
    int mBindSequence = -1;
    final Pools.Pool<PendingEvent> mPendingEventPool = new Pools.SimplePool(20);
    final SparseArray<PendingEvent> mPendingEvents = new SparseArray<>(20);
    final IInputMethodClient.Stub mClient = new IInputMethodClient.Stub() { // from class: android.view.inputmethod.InputMethodManager.1
        public void setUsingInputMethod(boolean z) {
        }

        protected void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            SomeArgs someArgsObtain = SomeArgs.obtain();
            someArgsObtain.arg1 = fileDescriptor;
            someArgsObtain.arg2 = printWriter;
            someArgsObtain.arg3 = strArr;
            someArgsObtain.arg4 = countDownLatch;
            InputMethodManager.this.mH.sendMessage(InputMethodManager.this.mH.obtainMessage(1, someArgsObtain));
            try {
                if (countDownLatch.await(5L, TimeUnit.SECONDS)) {
                    return;
                }
                printWriter.println("Timeout waiting for dump");
            } catch (InterruptedException unused) {
                printWriter.println("Interrupted waiting for dump");
            }
        }

        public void onBindMethod(InputBindResult inputBindResult) {
            InputMethodManager.this.mH.sendMessage(InputMethodManager.this.mH.obtainMessage(2, inputBindResult));
        }

        public void onUnbindMethod(int i) {
            InputMethodManager.this.mH.sendMessage(InputMethodManager.this.mH.obtainMessage(3, i, 0));
        }

        public void setActive(boolean z) {
            InputMethodManager.this.mH.sendMessage(InputMethodManager.this.mH.obtainMessage(4, z ? 1 : 0, 0));
        }
    };

    public interface FinishedInputEventCallback {
        void onFinishedInputEvent(Object obj, boolean z);
    }

    public boolean isWatchingCursor(View view) {
        return false;
    }

    class H extends Handler {
        H(Looper looper) {
            super(looper, null, true);
        }

        /* JADX WARN: Removed duplicated region for block: B:45:0x0095  */
        @Override // android.os.Handler
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void handleMessage(android.os.Message r6) {
            /*
                Method dump skipped, instruction units count: 368
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: android.view.inputmethod.InputMethodManager.H.handleMessage(android.os.Message):void");
        }
    }

    private static class ControlledInputConnectionWrapper extends IInputConnectionWrapper {
        private boolean mActive;
        private final InputMethodManager mParentInputMethodManager;

        public ControlledInputConnectionWrapper(Looper looper, InputConnection inputConnection, InputMethodManager inputMethodManager) {
            super(looper, inputConnection);
            this.mParentInputMethodManager = inputMethodManager;
            this.mActive = true;
        }

        public boolean isActive() {
            return this.mParentInputMethodManager.mActive && this.mActive;
        }

        void deactivate() {
            this.mActive = false;
        }
    }

    InputMethodManager(IInputMethodManager iInputMethodManager, Looper looper) {
        BaseInputConnection baseInputConnection = new BaseInputConnection(this, false);
        this.mDummyInputConnection = baseInputConnection;
        this.mService = iInputMethodManager;
        this.mMainLooper = looper;
        this.mH = new H(looper);
        this.mIInputContext = new ControlledInputConnectionWrapper(looper, baseInputConnection, this);
    }

    public static InputMethodManager getInstance() {
        InputMethodManager inputMethodManager;
        synchronized (InputMethodManager.class) {
            if (sInstance == null) {
                sInstance = new InputMethodManager(IInputMethodManager.Stub.asInterface(ServiceManager.getService(Context.INPUT_METHOD_SERVICE)), Looper.getMainLooper());
            }
            inputMethodManager = sInstance;
        }
        return inputMethodManager;
    }

    public static InputMethodManager peekInstance() {
        return sInstance;
    }

    public IInputMethodClient getClient() {
        return this.mClient;
    }

    public IInputContext getInputContext() {
        return this.mIInputContext;
    }

    public List<InputMethodInfo> getInputMethodList() {
        try {
            return this.mService.getInputMethodList();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public List<InputMethodInfo> getEnabledInputMethodList() {
        try {
            return this.mService.getEnabledInputMethodList();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public List<InputMethodSubtype> getEnabledInputMethodSubtypeList(InputMethodInfo inputMethodInfo, boolean z) {
        try {
            return this.mService.getEnabledInputMethodSubtypeList(inputMethodInfo == null ? null : inputMethodInfo.getId(), z);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void showStatusIcon(IBinder iBinder, String str, int i) {
        try {
            this.mService.updateStatusIcon(iBinder, str, i);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void hideStatusIcon(IBinder iBinder) {
        try {
            this.mService.updateStatusIcon(iBinder, (String) null, 0);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void setImeWindowStatus(IBinder iBinder, int i, int i2) {
        try {
            this.mService.setImeWindowStatus(iBinder, i, i2);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void setFullscreenMode(boolean z) {
        this.mFullscreenMode = z;
    }

    public void registerSuggestionSpansForNotification(SuggestionSpan[] suggestionSpanArr) {
        try {
            this.mService.registerSuggestionSpansForNotification(suggestionSpanArr);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void notifySuggestionPicked(SuggestionSpan suggestionSpan, String str, int i) {
        try {
            this.mService.notifySuggestionPicked(suggestionSpan, str, i);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isFullscreenMode() {
        return this.mFullscreenMode;
    }

    public boolean isActive(View view) {
        boolean z;
        checkFocus();
        synchronized (this.mH) {
            View view2 = this.mServedView;
            z = (view2 == view || (view2 != null && view2.checkInputConnectionProxy(view))) && this.mCurrentTextBoxAttribute != null;
        }
        return z;
    }

    public boolean isActive() {
        boolean z;
        checkFocus();
        synchronized (this.mH) {
            z = (this.mServedView == null || this.mCurrentTextBoxAttribute == null) ? false : true;
        }
        return z;
    }

    public boolean isAcceptingText() {
        checkFocus();
        return this.mServedInputConnection != null;
    }

    void clearBindingLocked() {
        clearConnectionLocked();
        setInputChannelLocked(null);
        this.mBindSequence = -1;
        this.mCurId = null;
        this.mCurMethod = null;
    }

    void setInputChannelLocked(InputChannel inputChannel) {
        if (this.mCurChannel != inputChannel) {
            if (this.mCurSender != null) {
                flushPendingEventsLocked();
                this.mCurSender.dispose();
                this.mCurSender = null;
            }
            InputChannel inputChannel2 = this.mCurChannel;
            if (inputChannel2 != null) {
                inputChannel2.dispose();
            }
            this.mCurChannel = inputChannel;
        }
    }

    void clearConnectionLocked() {
        this.mCurrentTextBoxAttribute = null;
        this.mServedInputConnection = null;
        ControlledInputConnectionWrapper controlledInputConnectionWrapper = this.mServedInputConnectionWrapper;
        if (controlledInputConnectionWrapper != null) {
            controlledInputConnectionWrapper.deactivate();
            this.mServedInputConnectionWrapper = null;
        }
    }

    void finishInputLocked() {
        this.mCurRootView = null;
        this.mNextServedView = null;
        if (this.mServedView != null) {
            if (this.mCurrentTextBoxAttribute != null) {
                try {
                    this.mService.finishInput(this.mClient);
                } catch (RemoteException unused) {
                }
            }
            notifyInputConnectionFinished();
            this.mServedView = null;
            this.mCompletions = null;
            this.mServedConnecting = false;
            clearConnectionLocked();
        }
    }

    private void notifyInputConnectionFinished() {
        ViewRootImpl viewRootImpl;
        View view = this.mServedView;
        if (view == null || this.mServedInputConnection == null || (viewRootImpl = view.getViewRootImpl()) == null) {
            return;
        }
        viewRootImpl.dispatchFinishInputConnection(this.mServedInputConnection);
    }

    public void reportFinishInputConnection(InputConnection inputConnection) {
        if (this.mServedInputConnection != inputConnection) {
            inputConnection.finishComposingText();
            if (inputConnection instanceof BaseInputConnection) {
                ((BaseInputConnection) inputConnection).reportFinish();
            }
        }
    }

    public void displayCompletions(View view, CompletionInfo[] completionInfoArr) {
        checkFocus();
        synchronized (this.mH) {
            View view2 = this.mServedView;
            if (view2 == view || (view2 != null && view2.checkInputConnectionProxy(view))) {
                this.mCompletions = completionInfoArr;
                IInputMethodSession iInputMethodSession = this.mCurMethod;
                if (iInputMethodSession != null) {
                    try {
                        iInputMethodSession.displayCompletions(completionInfoArr);
                    } catch (RemoteException unused) {
                    }
                }
            }
        }
    }

    public void updateExtractedText(View view, int i, ExtractedText extractedText) {
        checkFocus();
        synchronized (this.mH) {
            View view2 = this.mServedView;
            if (view2 == view || (view2 != null && view2.checkInputConnectionProxy(view))) {
                IInputMethodSession iInputMethodSession = this.mCurMethod;
                if (iInputMethodSession != null) {
                    try {
                        iInputMethodSession.updateExtractedText(i, extractedText);
                    } catch (RemoteException unused) {
                    }
                }
            }
        }
    }

    public boolean showSoftInput(View view, int i) {
        return showSoftInput(view, i, null);
    }

    public boolean showSoftInput(View view, int i, ResultReceiver resultReceiver) {
        checkFocus();
        synchronized (this.mH) {
            View view2 = this.mServedView;
            if (view2 != view && (view2 == null || !view2.checkInputConnectionProxy(view))) {
                return false;
            }
            try {
                return this.mService.showSoftInput(this.mClient, i, resultReceiver);
            } catch (RemoteException unused) {
                return false;
            }
        }
    }

    public void showSoftInputUnchecked(int i, ResultReceiver resultReceiver) {
        try {
            this.mService.showSoftInput(this.mClient, i, resultReceiver);
        } catch (RemoteException unused) {
        }
    }

    public boolean hideSoftInputFromWindow(IBinder iBinder, int i) {
        return hideSoftInputFromWindow(iBinder, i, null);
    }

    public boolean hideSoftInputFromWindow(IBinder iBinder, int i, ResultReceiver resultReceiver) {
        checkFocus();
        synchronized (this.mH) {
            View view = this.mServedView;
            if (view == null || view.getWindowToken() != iBinder) {
                return false;
            }
            try {
                return this.mService.hideSoftInput(this.mClient, i, resultReceiver);
            } catch (RemoteException unused) {
                return false;
            }
        }
    }

    public void toggleSoftInputFromWindow(IBinder iBinder, int i, int i2) {
        synchronized (this.mH) {
            View view = this.mServedView;
            if (view != null && view.getWindowToken() == iBinder) {
                IInputMethodSession iInputMethodSession = this.mCurMethod;
                if (iInputMethodSession != null) {
                    try {
                        iInputMethodSession.toggleSoftInput(i, i2);
                    } catch (RemoteException unused) {
                    }
                }
            }
        }
    }

    public void toggleSoftInput(int i, int i2) {
        IInputMethodSession iInputMethodSession = this.mCurMethod;
        if (iInputMethodSession != null) {
            try {
                iInputMethodSession.toggleSoftInput(i, i2);
            } catch (RemoteException unused) {
            }
        }
    }

    public void restartInput(View view) {
        checkFocus();
        synchronized (this.mH) {
            View view2 = this.mServedView;
            if (view2 == view || (view2 != null && view2.checkInputConnectionProxy(view))) {
                this.mServedConnecting = true;
                startInputInner(null, 0, 0, 0);
            }
        }
    }

    boolean startInputInner(IBinder iBinder, int i, int i2, int i3) {
        ControlledInputConnectionWrapper controlledInputConnectionWrapper;
        InputBindResult inputBindResultStartInput;
        synchronized (this.mH) {
            View view = this.mServedView;
            if (view == null) {
                return false;
            }
            Handler handler = view.getHandler();
            if (handler == null) {
                closeCurrentInput();
                return false;
            }
            if (handler.getLooper() != Looper.myLooper()) {
                handler.post(new Runnable() { // from class: android.view.inputmethod.InputMethodManager.2
                    @Override // java.lang.Runnable
                    public void run() {
                        InputMethodManager.this.startInputInner(null, 0, 0, 0);
                    }
                });
                return false;
            }
            EditorInfo editorInfo = new EditorInfo();
            editorInfo.packageName = view.getContext().getPackageName();
            editorInfo.fieldId = view.getId();
            InputConnection inputConnectionOnCreateInputConnection = view.onCreateInputConnection(editorInfo);
            synchronized (this.mH) {
                if (this.mServedView == view && this.mServedConnecting) {
                    int i4 = this.mCurrentTextBoxAttribute == null ? i | 256 : i;
                    this.mCurrentTextBoxAttribute = editorInfo;
                    this.mServedConnecting = false;
                    notifyInputConnectionFinished();
                    this.mServedInputConnection = inputConnectionOnCreateInputConnection;
                    if (inputConnectionOnCreateInputConnection != null) {
                        this.mCursorSelStart = editorInfo.initialSelStart;
                        this.mCursorSelEnd = editorInfo.initialSelEnd;
                        this.mCursorCandStart = -1;
                        this.mCursorCandEnd = -1;
                        this.mCursorRect.setEmpty();
                        controlledInputConnectionWrapper = new ControlledInputConnectionWrapper(handler.getLooper(), inputConnectionOnCreateInputConnection, this);
                    } else {
                        controlledInputConnectionWrapper = null;
                    }
                    ControlledInputConnectionWrapper controlledInputConnectionWrapper2 = controlledInputConnectionWrapper;
                    ControlledInputConnectionWrapper controlledInputConnectionWrapper3 = this.mServedInputConnectionWrapper;
                    if (controlledInputConnectionWrapper3 != null) {
                        controlledInputConnectionWrapper3.deactivate();
                    }
                    this.mServedInputConnectionWrapper = controlledInputConnectionWrapper2;
                    try {
                        if (iBinder != null) {
                            inputBindResultStartInput = this.mService.windowGainedFocus(this.mClient, iBinder, i4, i2, i3, editorInfo, controlledInputConnectionWrapper2);
                        } else {
                            inputBindResultStartInput = this.mService.startInput(this.mClient, controlledInputConnectionWrapper2, editorInfo, i4);
                        }
                        if (inputBindResultStartInput != null) {
                            if (inputBindResultStartInput.id != null) {
                                setInputChannelLocked(inputBindResultStartInput.channel);
                                this.mBindSequence = inputBindResultStartInput.sequence;
                                this.mCurMethod = inputBindResultStartInput.method;
                                this.mCurId = inputBindResultStartInput.id;
                            } else {
                                if (inputBindResultStartInput.channel != null && inputBindResultStartInput.channel != this.mCurChannel) {
                                    inputBindResultStartInput.channel.dispose();
                                }
                                if (this.mCurMethod == null) {
                                    return true;
                                }
                            }
                        }
                        IInputMethodSession iInputMethodSession = this.mCurMethod;
                        if (iInputMethodSession != null) {
                            CompletionInfo[] completionInfoArr = this.mCompletions;
                            if (completionInfoArr != null) {
                                try {
                                    iInputMethodSession.displayCompletions(completionInfoArr);
                                } catch (RemoteException unused) {
                                }
                            }
                        }
                    } catch (RemoteException e) {
                        Log.w(TAG, "IME died: " + this.mCurId, e);
                    }
                    return true;
                }
                return false;
            }
        }
    }

    public void windowDismissed(IBinder iBinder) {
        checkFocus();
        synchronized (this.mH) {
            View view = this.mServedView;
            if (view != null && view.getWindowToken() == iBinder) {
                finishInputLocked();
            }
        }
    }

    public void focusIn(View view) {
        synchronized (this.mH) {
            focusInLocked(view);
        }
    }

    void focusInLocked(View view) {
        if (this.mCurRootView != view.getRootView()) {
            return;
        }
        this.mNextServedView = view;
        scheduleCheckFocusLocked(view);
    }

    public void focusOut(View view) {
        synchronized (this.mH) {
        }
    }

    static void scheduleCheckFocusLocked(View view) {
        ViewRootImpl viewRootImpl = view.getViewRootImpl();
        if (viewRootImpl != null) {
            viewRootImpl.dispatchCheckFocus();
        }
    }

    public void checkFocus() {
        if (checkFocusNoStartInput(false, true)) {
            startInputInner(null, 0, 0, 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean checkFocusNoStartInput(boolean z, boolean z2) {
        if (this.mServedView == this.mNextServedView && !z) {
            return false;
        }
        synchronized (this.mH) {
            View view = this.mServedView;
            View view2 = this.mNextServedView;
            if (view == view2 && !z) {
                return false;
            }
            if (view2 == null) {
                finishInputLocked();
                closeCurrentInput();
                return false;
            }
            InputConnection inputConnection = this.mServedInputConnection;
            this.mServedView = view2;
            this.mCurrentTextBoxAttribute = null;
            this.mCompletions = null;
            this.mServedConnecting = true;
            if (z2 && inputConnection != null) {
                inputConnection.finishComposingText();
            }
            return true;
        }
    }

    void closeCurrentInput() {
        try {
            this.mService.hideSoftInput(this.mClient, 2, (ResultReceiver) null);
        } catch (RemoteException unused) {
        }
    }

    public void onWindowFocus(View view, View view2, int i, boolean z, int i2) {
        int i3;
        boolean z2;
        synchronized (this.mH) {
            i3 = 0;
            if (this.mHasBeenInactive) {
                this.mHasBeenInactive = false;
                z2 = true;
            } else {
                z2 = false;
            }
            focusInLocked(view2 != null ? view2 : view);
        }
        if (view2 != null) {
            i3 = view2.onCheckIsTextEditor() ? 3 : 1;
        }
        if (z) {
            i3 |= 4;
        }
        int i4 = i3;
        if (checkFocusNoStartInput(z2, true) && startInputInner(view.getWindowToken(), i4, i, i2)) {
            return;
        }
        synchronized (this.mH) {
            try {
                this.mService.windowGainedFocus(this.mClient, view.getWindowToken(), i4, i, i2, (EditorInfo) null, (IInputContext) null);
            } catch (RemoteException unused) {
            }
        }
    }

    public void startGettingWindowFocus(View view) {
        synchronized (this.mH) {
            this.mCurRootView = view;
        }
    }

    public void updateSelection(View view, int i, int i2, int i3, int i4) {
        IInputMethodSession iInputMethodSession;
        checkFocus();
        synchronized (this.mH) {
            View view2 = this.mServedView;
            if ((view2 == view || (view2 != null && view2.checkInputConnectionProxy(view))) && this.mCurrentTextBoxAttribute != null && (iInputMethodSession = this.mCurMethod) != null) {
                int i5 = this.mCursorSelStart;
                if (i5 != i || this.mCursorSelEnd != i2 || this.mCursorCandStart != i3 || this.mCursorCandEnd != i4) {
                    try {
                        int i6 = this.mCursorSelEnd;
                        this.mCursorSelStart = i;
                        this.mCursorSelEnd = i2;
                        this.mCursorCandStart = i3;
                        this.mCursorCandEnd = i4;
                        iInputMethodSession.updateSelection(i5, i6, i, i2, i3, i4);
                    } catch (RemoteException e) {
                        Log.w(TAG, "IME died: " + this.mCurId, e);
                    }
                }
            }
        }
    }

    public void viewClicked(View view) {
        IInputMethodSession iInputMethodSession;
        boolean z = this.mServedView != this.mNextServedView;
        checkFocus();
        synchronized (this.mH) {
            View view2 = this.mServedView;
            if ((view2 != view && (view2 == null || !view2.checkInputConnectionProxy(view))) || this.mCurrentTextBoxAttribute == null || (iInputMethodSession = this.mCurMethod) == null) {
                return;
            }
            try {
                iInputMethodSession.viewClicked(z);
            } catch (RemoteException e) {
                Log.w(TAG, "IME died: " + this.mCurId, e);
            }
        }
    }

    public void updateCursor(View view, int i, int i2, int i3, int i4) {
        checkFocus();
        synchronized (this.mH) {
            View view2 = this.mServedView;
            if ((view2 == view || (view2 != null && view2.checkInputConnectionProxy(view))) && this.mCurrentTextBoxAttribute != null && this.mCurMethod != null) {
                this.mTmpCursorRect.set(i, i2, i3, i4);
                if (!this.mCursorRect.equals(this.mTmpCursorRect)) {
                    try {
                        this.mCurMethod.updateCursor(this.mTmpCursorRect);
                        this.mCursorRect.set(this.mTmpCursorRect);
                    } catch (RemoteException e) {
                        Log.w(TAG, "IME died: " + this.mCurId, e);
                    }
                }
            }
        }
    }

    public void sendAppPrivateCommand(View view, String str, Bundle bundle) {
        IInputMethodSession iInputMethodSession;
        checkFocus();
        synchronized (this.mH) {
            View view2 = this.mServedView;
            if ((view2 != view && (view2 == null || !view2.checkInputConnectionProxy(view))) || this.mCurrentTextBoxAttribute == null || (iInputMethodSession = this.mCurMethod) == null) {
                return;
            }
            try {
                iInputMethodSession.appPrivateCommand(str, bundle);
            } catch (RemoteException e) {
                Log.w(TAG, "IME died: " + this.mCurId, e);
            }
        }
    }

    public void setInputMethod(IBinder iBinder, String str) {
        try {
            this.mService.setInputMethod(iBinder, str);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void setInputMethodAndSubtype(IBinder iBinder, String str, InputMethodSubtype inputMethodSubtype) {
        try {
            this.mService.setInputMethodAndSubtype(iBinder, str, inputMethodSubtype);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void hideSoftInputFromInputMethod(IBinder iBinder, int i) {
        try {
            this.mService.hideMySoftInput(iBinder, i);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void showSoftInputFromInputMethod(IBinder iBinder, int i) {
        try {
            this.mService.showMySoftInput(iBinder, i);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public int dispatchInputEvent(InputEvent inputEvent, Object obj, FinishedInputEventCallback finishedInputEventCallback, Handler handler) {
        synchronized (this.mH) {
            if (this.mCurMethod == null) {
                return 0;
            }
            if (inputEvent instanceof KeyEvent) {
                KeyEvent keyEvent = (KeyEvent) inputEvent;
                if (keyEvent.getAction() == 0 && keyEvent.getKeyCode() == 63 && keyEvent.getRepeatCount() == 0) {
                    showInputMethodPickerLocked();
                    return 1;
                }
            }
            PendingEvent pendingEventObtainPendingEventLocked = obtainPendingEventLocked(inputEvent, obj, this.mCurId, finishedInputEventCallback, handler);
            if (this.mMainLooper.isCurrentThread()) {
                return sendInputEventOnMainLooperLocked(pendingEventObtainPendingEventLocked);
            }
            Message messageObtainMessage = this.mH.obtainMessage(5, pendingEventObtainPendingEventLocked);
            messageObtainMessage.setAsynchronous(true);
            this.mH.sendMessage(messageObtainMessage);
            return -1;
        }
    }

    void sendInputEventAndReportResultOnMainLooper(PendingEvent pendingEvent) {
        synchronized (this.mH) {
            int iSendInputEventOnMainLooperLocked = sendInputEventOnMainLooperLocked(pendingEvent);
            if (iSendInputEventOnMainLooperLocked == -1) {
                return;
            }
            boolean z = true;
            if (iSendInputEventOnMainLooperLocked != 1) {
                z = false;
            }
            invokeFinishedInputEventCallback(pendingEvent, z);
        }
    }

    int sendInputEventOnMainLooperLocked(PendingEvent pendingEvent) {
        if (this.mCurChannel == null) {
            return 0;
        }
        if (this.mCurSender == null) {
            this.mCurSender = new ImeInputEventSender(this.mCurChannel, this.mH.getLooper());
        }
        InputEvent inputEvent = pendingEvent.mEvent;
        int sequenceNumber = inputEvent.getSequenceNumber();
        if (this.mCurSender.sendInputEvent(sequenceNumber, inputEvent)) {
            this.mPendingEvents.put(sequenceNumber, pendingEvent);
            Trace.traceCounter(4L, PENDING_EVENT_COUNTER, this.mPendingEvents.size());
            Message messageObtainMessage = this.mH.obtainMessage(6, pendingEvent);
            messageObtainMessage.setAsynchronous(true);
            this.mH.sendMessageDelayed(messageObtainMessage, INPUT_METHOD_NOT_RESPONDING_TIMEOUT);
            return -1;
        }
        Log.w(TAG, "Unable to send input event to IME: " + this.mCurId + " dropping: " + inputEvent);
        return 0;
    }

    void finishedInputEvent(int i, boolean z, boolean z2) {
        synchronized (this.mH) {
            int iIndexOfKey = this.mPendingEvents.indexOfKey(i);
            if (iIndexOfKey < 0) {
                return;
            }
            PendingEvent pendingEventValueAt = this.mPendingEvents.valueAt(iIndexOfKey);
            this.mPendingEvents.removeAt(iIndexOfKey);
            Trace.traceCounter(4L, PENDING_EVENT_COUNTER, this.mPendingEvents.size());
            if (z2) {
                Log.w(TAG, "Timeout waiting for IME to handle input event after 2500 ms: " + pendingEventValueAt.mInputMethodId);
            } else {
                this.mH.removeMessages(6, pendingEventValueAt);
            }
            invokeFinishedInputEventCallback(pendingEventValueAt, z);
        }
    }

    void invokeFinishedInputEventCallback(PendingEvent pendingEvent, boolean z) {
        pendingEvent.mHandled = z;
        if (pendingEvent.mHandler.getLooper().isCurrentThread()) {
            pendingEvent.run();
            return;
        }
        Message messageObtain = Message.obtain(pendingEvent.mHandler, pendingEvent);
        messageObtain.setAsynchronous(true);
        messageObtain.sendToTarget();
    }

    private void flushPendingEventsLocked() {
        this.mH.removeMessages(7);
        int size = this.mPendingEvents.size();
        for (int i = 0; i < size; i++) {
            Message messageObtainMessage = this.mH.obtainMessage(7, this.mPendingEvents.keyAt(i), 0);
            messageObtainMessage.setAsynchronous(true);
            messageObtainMessage.sendToTarget();
        }
    }

    private PendingEvent obtainPendingEventLocked(InputEvent inputEvent, Object obj, String str, FinishedInputEventCallback finishedInputEventCallback, Handler handler) {
        PendingEvent pendingEventAcquire = this.mPendingEventPool.acquire();
        if (pendingEventAcquire == null) {
            pendingEventAcquire = new PendingEvent();
        }
        pendingEventAcquire.mEvent = inputEvent;
        pendingEventAcquire.mToken = obj;
        pendingEventAcquire.mInputMethodId = str;
        pendingEventAcquire.mCallback = finishedInputEventCallback;
        pendingEventAcquire.mHandler = handler;
        return pendingEventAcquire;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void recyclePendingEventLocked(PendingEvent pendingEvent) {
        pendingEvent.recycle();
        this.mPendingEventPool.release(pendingEvent);
    }

    public void showInputMethodPicker() {
        synchronized (this.mH) {
            showInputMethodPickerLocked();
        }
    }

    private void showInputMethodPickerLocked() {
        try {
            this.mService.showInputMethodPickerFromClient(this.mClient);
        } catch (RemoteException e) {
            Log.w(TAG, "IME died: " + this.mCurId, e);
        }
    }

    public void showInputMethodAndSubtypeEnabler(String str) {
        synchronized (this.mH) {
            try {
                this.mService.showInputMethodAndSubtypeEnablerFromClient(this.mClient, str);
            } catch (RemoteException e) {
                Log.w(TAG, "IME died: " + this.mCurId, e);
            }
        }
    }

    public InputMethodSubtype getCurrentInputMethodSubtype() {
        InputMethodSubtype currentInputMethodSubtype;
        synchronized (this.mH) {
            try {
                try {
                    currentInputMethodSubtype = this.mService.getCurrentInputMethodSubtype();
                } catch (RemoteException e) {
                    Log.w(TAG, "IME died: " + this.mCurId, e);
                    return null;
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return currentInputMethodSubtype;
    }

    public boolean setCurrentInputMethodSubtype(InputMethodSubtype inputMethodSubtype) {
        boolean currentInputMethodSubtype;
        synchronized (this.mH) {
            try {
                try {
                    currentInputMethodSubtype = this.mService.setCurrentInputMethodSubtype(inputMethodSubtype);
                } catch (RemoteException e) {
                    Log.w(TAG, "IME died: " + this.mCurId, e);
                    return false;
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return currentInputMethodSubtype;
    }

    /* JADX WARN: Code restructure failed: missing block: B:14:0x0028, code lost:
    
        android.util.Log.e(android.view.inputmethod.InputMethodManager.TAG, "IMI list already contains the same InputMethod.");
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public java.util.Map<android.view.inputmethod.InputMethodInfo, java.util.List<android.view.inputmethod.InputMethodSubtype>> getShortcutInputMethodsAndSubtypes() {
        /*
            r8 = this;
            android.view.inputmethod.InputMethodManager$H r0 = r8.mH
            monitor-enter(r0)
            java.util.HashMap r1 = new java.util.HashMap     // Catch: java.lang.Throwable -> L66
            r1.<init>()     // Catch: java.lang.Throwable -> L66
            com.android.internal.view.IInputMethodManager r2 = r8.mService     // Catch: android.os.RemoteException -> L49 java.lang.Throwable -> L66
            java.util.List r2 = r2.getShortcutInputMethodsAndSubtypes()     // Catch: android.os.RemoteException -> L49 java.lang.Throwable -> L66
            r3 = 0
            int r4 = r2.size()     // Catch: android.os.RemoteException -> L49 java.lang.Throwable -> L66
            if (r2 == 0) goto L64
            if (r4 <= 0) goto L64
            r5 = 0
        L18:
            if (r5 >= r4) goto L64
            java.lang.Object r6 = r2.get(r5)     // Catch: android.os.RemoteException -> L49 java.lang.Throwable -> L66
            boolean r7 = r6 instanceof android.view.inputmethod.InputMethodInfo     // Catch: android.os.RemoteException -> L49 java.lang.Throwable -> L66
            if (r7 == 0) goto L3b
            boolean r3 = r1.containsKey(r6)     // Catch: android.os.RemoteException -> L49 java.lang.Throwable -> L66
            if (r3 == 0) goto L30
            java.lang.String r2 = "InputMethodManager"
            java.lang.String r3 = "IMI list already contains the same InputMethod."
            android.util.Log.e(r2, r3)     // Catch: android.os.RemoteException -> L49 java.lang.Throwable -> L66
            goto L64
        L30:
            java.util.ArrayList r3 = new java.util.ArrayList     // Catch: android.os.RemoteException -> L49 java.lang.Throwable -> L66
            r3.<init>()     // Catch: android.os.RemoteException -> L49 java.lang.Throwable -> L66
            android.view.inputmethod.InputMethodInfo r6 = (android.view.inputmethod.InputMethodInfo) r6     // Catch: android.os.RemoteException -> L49 java.lang.Throwable -> L66
            r1.put(r6, r3)     // Catch: android.os.RemoteException -> L49 java.lang.Throwable -> L66
            goto L46
        L3b:
            if (r3 == 0) goto L46
            boolean r7 = r6 instanceof android.view.inputmethod.InputMethodSubtype     // Catch: android.os.RemoteException -> L49 java.lang.Throwable -> L66
            if (r7 == 0) goto L46
            android.view.inputmethod.InputMethodSubtype r6 = (android.view.inputmethod.InputMethodSubtype) r6     // Catch: android.os.RemoteException -> L49 java.lang.Throwable -> L66
            r3.add(r6)     // Catch: android.os.RemoteException -> L49 java.lang.Throwable -> L66
        L46:
            int r5 = r5 + 1
            goto L18
        L49:
            r2 = move-exception
            java.lang.String r3 = "InputMethodManager"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L66
            r4.<init>()     // Catch: java.lang.Throwable -> L66
            java.lang.String r5 = "IME died: "
            java.lang.StringBuilder r4 = r4.append(r5)     // Catch: java.lang.Throwable -> L66
            java.lang.String r5 = r8.mCurId     // Catch: java.lang.Throwable -> L66
            java.lang.StringBuilder r4 = r4.append(r5)     // Catch: java.lang.Throwable -> L66
            java.lang.String r4 = r4.toString()     // Catch: java.lang.Throwable -> L66
            android.util.Log.w(r3, r4, r2)     // Catch: java.lang.Throwable -> L66
        L64:
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L66
            return r1
        L66:
            r1 = move-exception
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L66
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: android.view.inputmethod.InputMethodManager.getShortcutInputMethodsAndSubtypes():java.util.Map");
    }

    public boolean switchToLastInputMethod(IBinder iBinder) {
        boolean zSwitchToLastInputMethod;
        synchronized (this.mH) {
            try {
                try {
                    zSwitchToLastInputMethod = this.mService.switchToLastInputMethod(iBinder);
                } catch (RemoteException e) {
                    Log.w(TAG, "IME died: " + this.mCurId, e);
                    return false;
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return zSwitchToLastInputMethod;
    }

    public boolean switchToNextInputMethod(IBinder iBinder, boolean z) {
        boolean zSwitchToNextInputMethod;
        synchronized (this.mH) {
            try {
                try {
                    zSwitchToNextInputMethod = this.mService.switchToNextInputMethod(iBinder, z);
                } catch (RemoteException e) {
                    Log.w(TAG, "IME died: " + this.mCurId, e);
                    return false;
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return zSwitchToNextInputMethod;
    }

    public boolean shouldOfferSwitchingToNextInputMethod(IBinder iBinder) {
        boolean zShouldOfferSwitchingToNextInputMethod;
        synchronized (this.mH) {
            try {
                try {
                    zShouldOfferSwitchingToNextInputMethod = this.mService.shouldOfferSwitchingToNextInputMethod(iBinder);
                } catch (RemoteException e) {
                    Log.w(TAG, "IME died: " + this.mCurId, e);
                    return false;
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return zShouldOfferSwitchingToNextInputMethod;
    }

    public void setAdditionalInputMethodSubtypes(String str, InputMethodSubtype[] inputMethodSubtypeArr) {
        synchronized (this.mH) {
            try {
                this.mService.setAdditionalInputMethodSubtypes(str, inputMethodSubtypeArr);
            } catch (RemoteException e) {
                Log.w(TAG, "IME died: " + this.mCurId, e);
            }
        }
    }

    public InputMethodSubtype getLastInputMethodSubtype() {
        InputMethodSubtype lastInputMethodSubtype;
        synchronized (this.mH) {
            try {
                try {
                    lastInputMethodSubtype = this.mService.getLastInputMethodSubtype();
                } catch (RemoteException e) {
                    Log.w(TAG, "IME died: " + this.mCurId, e);
                    return null;
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return lastInputMethodSubtype;
    }

    void doDump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        PrintWriterPrinter printWriterPrinter = new PrintWriterPrinter(printWriter);
        printWriterPrinter.println("Input method client state for " + this + ":");
        printWriterPrinter.println("  mService=" + this.mService);
        printWriterPrinter.println("  mMainLooper=" + this.mMainLooper);
        printWriterPrinter.println("  mIInputContext=" + this.mIInputContext);
        printWriterPrinter.println("  mActive=" + this.mActive + " mHasBeenInactive=" + this.mHasBeenInactive + " mBindSequence=" + this.mBindSequence + " mCurId=" + this.mCurId);
        printWriterPrinter.println("  mCurMethod=" + this.mCurMethod);
        printWriterPrinter.println("  mCurRootView=" + this.mCurRootView);
        printWriterPrinter.println("  mServedView=" + this.mServedView);
        printWriterPrinter.println("  mNextServedView=" + this.mNextServedView);
        printWriterPrinter.println("  mServedConnecting=" + this.mServedConnecting);
        if (this.mCurrentTextBoxAttribute != null) {
            printWriterPrinter.println("  mCurrentTextBoxAttribute:");
            this.mCurrentTextBoxAttribute.dump(printWriterPrinter, "    ");
        } else {
            printWriterPrinter.println("  mCurrentTextBoxAttribute: null");
        }
        printWriterPrinter.println("  mServedInputConnection=" + this.mServedInputConnection);
        printWriterPrinter.println("  mCompletions=" + this.mCompletions);
        printWriterPrinter.println("  mCursorRect=" + this.mCursorRect);
        printWriterPrinter.println("  mCursorSelStart=" + this.mCursorSelStart + " mCursorSelEnd=" + this.mCursorSelEnd + " mCursorCandStart=" + this.mCursorCandStart + " mCursorCandEnd=" + this.mCursorCandEnd);
    }

    private final class ImeInputEventSender extends InputEventSender {
        public ImeInputEventSender(InputChannel inputChannel, Looper looper) {
            super(inputChannel, looper);
        }

        @Override // android.view.InputEventSender
        public void onInputEventFinished(int i, boolean z) {
            InputMethodManager.this.finishedInputEvent(i, z, false);
        }
    }

    private final class PendingEvent implements Runnable {
        public FinishedInputEventCallback mCallback;
        public InputEvent mEvent;
        public boolean mHandled;
        public Handler mHandler;
        public String mInputMethodId;
        public Object mToken;

        private PendingEvent() {
        }

        public void recycle() {
            this.mEvent = null;
            this.mToken = null;
            this.mInputMethodId = null;
            this.mCallback = null;
            this.mHandler = null;
            this.mHandled = false;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.mCallback.onFinishedInputEvent(this.mToken, this.mHandled);
            synchronized (InputMethodManager.this.mH) {
                InputMethodManager.this.recyclePendingEventLocked(this);
            }
        }
    }
}
