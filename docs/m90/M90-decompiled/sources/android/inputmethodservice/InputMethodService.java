package android.inputmethodservice;

import android.R;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.Region;
import android.inputmethodservice.AbstractInputMethodService;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.Layout;
import android.text.Spannable;
import android.text.method.MovementMethod;
import android.util.Log;
import android.util.PrintWriterPrinter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputBinding;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/* JADX INFO: loaded from: classes.dex */
public class InputMethodService extends AbstractInputMethodService {
    public static final int BACK_DISPOSITION_DEFAULT = 0;
    public static final int BACK_DISPOSITION_WILL_DISMISS = 2;
    public static final int BACK_DISPOSITION_WILL_NOT_DISMISS = 1;
    static final boolean DEBUG = false;
    public static final int IME_ACTIVE = 1;
    public static final int IME_VISIBLE = 2;
    static final int MOVEMENT_DOWN = -1;
    static final int MOVEMENT_UP = -2;
    static final String TAG = "InputMethodService";
    int mBackDisposition;
    FrameLayout mCandidatesFrame;
    boolean mCandidatesViewStarted;
    int mCandidatesVisibility;
    CompletionInfo[] mCurCompletions;
    ViewGroup mExtractAccessories;
    Button mExtractAction;
    ExtractEditText mExtractEditText;
    FrameLayout mExtractFrame;
    View mExtractView;
    boolean mExtractViewHidden;
    ExtractedText mExtractedText;
    int mExtractedToken;
    boolean mFullscreenApplied;
    ViewGroup mFullscreenArea;
    InputMethodManager mImm;
    boolean mInShowWindow;
    LayoutInflater mInflater;
    boolean mInitialized;
    InputBinding mInputBinding;
    InputConnection mInputConnection;
    EditorInfo mInputEditorInfo;
    FrameLayout mInputFrame;
    boolean mInputStarted;
    View mInputView;
    boolean mInputViewStarted;
    boolean mIsFullscreen;
    boolean mIsInputViewShown;
    boolean mLastShowInputRequested;
    View mRootView;
    int mShowInputFlags;
    boolean mShowInputForced;
    boolean mShowInputRequested;
    InputConnection mStartedInputConnection;
    int mStatusIcon;
    TypedArray mThemeAttrs;
    IBinder mToken;
    SoftInputWindow mWindow;
    boolean mWindowAdded;
    boolean mWindowCreated;
    boolean mWindowVisible;
    boolean mWindowWasVisible;
    int mTheme = 0;
    boolean mHardwareAccelerated = false;
    final Insets mTmpInsets = new Insets();
    final int[] mTmpLocation = new int[2];
    final ViewTreeObserver.OnComputeInternalInsetsListener mInsetsComputer = new ViewTreeObserver.OnComputeInternalInsetsListener() { // from class: android.inputmethodservice.InputMethodService.1
        @Override // android.view.ViewTreeObserver.OnComputeInternalInsetsListener
        public void onComputeInternalInsets(ViewTreeObserver.InternalInsetsInfo internalInsetsInfo) {
            if (InputMethodService.this.isExtractViewShown()) {
                View decorView = InputMethodService.this.getWindow().getWindow().getDecorView();
                Rect rect = internalInsetsInfo.contentInsets;
                Rect rect2 = internalInsetsInfo.visibleInsets;
                int height = decorView.getHeight();
                rect2.top = height;
                rect.top = height;
                internalInsetsInfo.touchableRegion.setEmpty();
                internalInsetsInfo.setTouchableInsets(0);
                return;
            }
            InputMethodService inputMethodService = InputMethodService.this;
            inputMethodService.onComputeInsets(inputMethodService.mTmpInsets);
            internalInsetsInfo.contentInsets.top = InputMethodService.this.mTmpInsets.contentTopInsets;
            internalInsetsInfo.visibleInsets.top = InputMethodService.this.mTmpInsets.visibleTopInsets;
            internalInsetsInfo.touchableRegion.set(InputMethodService.this.mTmpInsets.touchableRegion);
            internalInsetsInfo.setTouchableInsets(InputMethodService.this.mTmpInsets.touchableInsets);
        }
    };
    final View.OnClickListener mActionClickListener = new View.OnClickListener() { // from class: android.inputmethodservice.InputMethodService.2
        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            EditorInfo currentInputEditorInfo = InputMethodService.this.getCurrentInputEditorInfo();
            InputConnection currentInputConnection = InputMethodService.this.getCurrentInputConnection();
            if (currentInputEditorInfo == null || currentInputConnection == null) {
                return;
            }
            if (currentInputEditorInfo.actionId != 0) {
                currentInputConnection.performEditorAction(currentInputEditorInfo.actionId);
            } else if ((currentInputEditorInfo.imeOptions & 255) != 1) {
                currentInputConnection.performEditorAction(currentInputEditorInfo.imeOptions & 255);
            }
        }
    };

    public static final class Insets {
        public static final int TOUCHABLE_INSETS_CONTENT = 1;
        public static final int TOUCHABLE_INSETS_FRAME = 0;
        public static final int TOUCHABLE_INSETS_REGION = 3;
        public static final int TOUCHABLE_INSETS_VISIBLE = 2;
        public int contentTopInsets;
        public int touchableInsets;
        public final Region touchableRegion = new Region();
        public int visibleTopInsets;
    }

    public void onAppPrivateCommand(String str, Bundle bundle) {
    }

    public void onBindInput() {
    }

    public View onCreateCandidatesView() {
        return null;
    }

    public View onCreateInputView() {
        return null;
    }

    protected void onCurrentInputMethodSubtypeChanged(InputMethodSubtype inputMethodSubtype) {
    }

    public void onDisplayCompletions(CompletionInfo[] completionInfoArr) {
    }

    @Override // android.inputmethodservice.AbstractInputMethodService
    public boolean onGenericMotionEvent(MotionEvent motionEvent) {
        return false;
    }

    public void onInitializeInterface() {
    }

    @Override // android.view.KeyEvent.Callback
    public boolean onKeyLongPress(int i, KeyEvent keyEvent) {
        return false;
    }

    public void onStartCandidatesView(EditorInfo editorInfo, boolean z) {
    }

    public void onStartInput(EditorInfo editorInfo, boolean z) {
    }

    public void onStartInputView(EditorInfo editorInfo, boolean z) {
    }

    @Override // android.inputmethodservice.AbstractInputMethodService
    public boolean onTrackballEvent(MotionEvent motionEvent) {
        return false;
    }

    public void onUnbindInput() {
    }

    public void onUpdateCursor(Rect rect) {
    }

    public void onViewClicked(boolean z) {
    }

    public void onWindowHidden() {
    }

    public void onWindowShown() {
    }

    public class InputMethodImpl extends AbstractInputMethodService.AbstractInputMethodImpl {
        public InputMethodImpl() {
            super();
        }

        @Override // android.view.inputmethod.InputMethod
        public void attachToken(IBinder iBinder) {
            if (InputMethodService.this.mToken == null) {
                InputMethodService.this.mToken = iBinder;
                InputMethodService.this.mWindow.setToken(iBinder);
            }
        }

        @Override // android.view.inputmethod.InputMethod
        public void bindInput(InputBinding inputBinding) {
            InputMethodService.this.mInputBinding = inputBinding;
            InputMethodService.this.mInputConnection = inputBinding.getConnection();
            InputConnection currentInputConnection = InputMethodService.this.getCurrentInputConnection();
            if (currentInputConnection != null) {
                currentInputConnection.reportFullscreenMode(InputMethodService.this.mIsFullscreen);
            }
            InputMethodService.this.initialize();
            InputMethodService.this.onBindInput();
        }

        @Override // android.view.inputmethod.InputMethod
        public void unbindInput() {
            InputMethodService.this.onUnbindInput();
            InputMethodService.this.mInputBinding = null;
            InputMethodService.this.mInputConnection = null;
        }

        @Override // android.view.inputmethod.InputMethod
        public void startInput(InputConnection inputConnection, EditorInfo editorInfo) {
            InputMethodService.this.doStartInput(inputConnection, editorInfo, false);
        }

        @Override // android.view.inputmethod.InputMethod
        public void restartInput(InputConnection inputConnection, EditorInfo editorInfo) {
            InputMethodService.this.doStartInput(inputConnection, editorInfo, true);
        }

        @Override // android.view.inputmethod.InputMethod
        public void hideSoftInput(int i, ResultReceiver resultReceiver) {
            boolean zIsInputViewShown = InputMethodService.this.isInputViewShown();
            int i2 = 0;
            InputMethodService.this.mShowInputFlags = 0;
            InputMethodService.this.mShowInputRequested = false;
            InputMethodService.this.mShowInputForced = false;
            InputMethodService.this.doHideWindow();
            if (resultReceiver != null) {
                if (zIsInputViewShown != InputMethodService.this.isInputViewShown()) {
                    i2 = 3;
                } else if (!zIsInputViewShown) {
                    i2 = 1;
                }
                resultReceiver.send(i2, null);
            }
        }

        @Override // android.view.inputmethod.InputMethod
        public void showSoftInput(int i, ResultReceiver resultReceiver) {
            boolean zIsInputViewShown = InputMethodService.this.isInputViewShown();
            int i2 = 0;
            InputMethodService.this.mShowInputFlags = 0;
            if (InputMethodService.this.onShowInputRequested(i, false)) {
                try {
                    InputMethodService.this.showWindow(true);
                } catch (WindowManager.BadTokenException unused) {
                    InputMethodService.this.mWindowVisible = false;
                    InputMethodService.this.mWindowAdded = false;
                }
            }
            InputMethodService.this.mImm.setImeWindowStatus(InputMethodService.this.mToken, (InputMethodService.this.isInputViewShown() ? 2 : 0) | 1, InputMethodService.this.mBackDisposition);
            if (resultReceiver != null) {
                if (zIsInputViewShown != InputMethodService.this.isInputViewShown()) {
                    i2 = 2;
                } else if (!zIsInputViewShown) {
                    i2 = 1;
                }
                resultReceiver.send(i2, null);
            }
        }

        @Override // android.view.inputmethod.InputMethod
        public void changeInputMethodSubtype(InputMethodSubtype inputMethodSubtype) {
            InputMethodService.this.onCurrentInputMethodSubtypeChanged(inputMethodSubtype);
        }
    }

    public class InputMethodSessionImpl extends AbstractInputMethodService.AbstractInputMethodSessionImpl {
        public InputMethodSessionImpl() {
            super();
        }

        @Override // android.view.inputmethod.InputMethodSession
        public void finishInput() {
            if (isEnabled()) {
                InputMethodService.this.doFinishInput();
            }
        }

        @Override // android.view.inputmethod.InputMethodSession
        public void displayCompletions(CompletionInfo[] completionInfoArr) {
            if (isEnabled()) {
                InputMethodService.this.mCurCompletions = completionInfoArr;
                InputMethodService.this.onDisplayCompletions(completionInfoArr);
            }
        }

        @Override // android.view.inputmethod.InputMethodSession
        public void updateExtractedText(int i, ExtractedText extractedText) {
            if (isEnabled()) {
                InputMethodService.this.onUpdateExtractedText(i, extractedText);
            }
        }

        @Override // android.view.inputmethod.InputMethodSession
        public void updateSelection(int i, int i2, int i3, int i4, int i5, int i6) {
            if (isEnabled()) {
                InputMethodService.this.onUpdateSelection(i, i2, i3, i4, i5, i6);
            }
        }

        @Override // android.view.inputmethod.InputMethodSession
        public void viewClicked(boolean z) {
            if (isEnabled()) {
                InputMethodService.this.onViewClicked(z);
            }
        }

        @Override // android.view.inputmethod.InputMethodSession
        public void updateCursor(Rect rect) {
            if (isEnabled()) {
                InputMethodService.this.onUpdateCursor(rect);
            }
        }

        @Override // android.view.inputmethod.InputMethodSession
        public void appPrivateCommand(String str, Bundle bundle) {
            if (isEnabled()) {
                InputMethodService.this.onAppPrivateCommand(str, bundle);
            }
        }

        @Override // android.view.inputmethod.InputMethodSession
        public void toggleSoftInput(int i, int i2) {
            InputMethodService.this.onToggleSoftInput(i, i2);
        }
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public void setTheme(int i) {
        if (this.mWindow != null) {
            throw new IllegalStateException("Must be called before onCreate()");
        }
        this.mTheme = i;
    }

    public boolean enableHardwareAcceleration() {
        if (this.mWindow != null) {
            throw new IllegalStateException("Must be called before onCreate()");
        }
        if (!ActivityManager.isHighEndGfx()) {
            return false;
        }
        this.mHardwareAccelerated = true;
        return true;
    }

    @Override // android.app.Service
    public void onCreate() {
        int iSelectSystemTheme = Resources.selectSystemTheme(this.mTheme, getApplicationInfo().targetSdkVersion, R.style.Theme_InputMethod, R.style.Theme_Holo_InputMethod, R.style.Theme_DeviceDefault_InputMethod);
        this.mTheme = iSelectSystemTheme;
        super.setTheme(iSelectSystemTheme);
        super.onCreate();
        this.mImm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        this.mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        SoftInputWindow softInputWindow = new SoftInputWindow(this, this.mTheme, this.mDispatcherState);
        this.mWindow = softInputWindow;
        if (this.mHardwareAccelerated) {
            softInputWindow.getWindow().addFlags(16777216);
        }
        initViews();
        this.mWindow.getWindow().setLayout(-1, -2);
    }

    void initialize() {
        if (this.mInitialized) {
            return;
        }
        this.mInitialized = true;
        onInitializeInterface();
    }

    void initViews() {
        this.mInitialized = false;
        this.mWindowCreated = false;
        this.mShowInputRequested = false;
        this.mShowInputForced = false;
        this.mThemeAttrs = obtainStyledAttributes(R.styleable.InputMethodService);
        View viewInflate = this.mInflater.inflate(17367116, (ViewGroup) null);
        this.mRootView = viewInflate;
        viewInflate.setSystemUiVisibility(768);
        this.mWindow.setContentView(this.mRootView);
        this.mRootView.getViewTreeObserver().addOnComputeInternalInsetsListener(this.mInsetsComputer);
        if (Settings.Global.getInt(getContentResolver(), Settings.Global.FANCY_IME_ANIMATIONS, 0) != 0) {
            this.mWindow.getWindow().setWindowAnimations(16974321);
        }
        this.mFullscreenArea = (ViewGroup) this.mRootView.findViewById(16908984);
        this.mExtractViewHidden = false;
        this.mExtractFrame = (FrameLayout) this.mRootView.findViewById(R.id.extractArea);
        this.mExtractView = null;
        this.mExtractEditText = null;
        this.mExtractAccessories = null;
        this.mExtractAction = null;
        this.mFullscreenApplied = false;
        this.mCandidatesFrame = (FrameLayout) this.mRootView.findViewById(R.id.candidatesArea);
        this.mInputFrame = (FrameLayout) this.mRootView.findViewById(R.id.inputArea);
        this.mInputView = null;
        this.mIsInputViewShown = false;
        this.mExtractFrame.setVisibility(8);
        int candidatesHiddenVisibility = getCandidatesHiddenVisibility();
        this.mCandidatesVisibility = candidatesHiddenVisibility;
        this.mCandidatesFrame.setVisibility(candidatesHiddenVisibility);
        this.mInputFrame.setVisibility(8);
    }

    @Override // android.app.Service
    public void onDestroy() {
        super.onDestroy();
        this.mRootView.getViewTreeObserver().removeOnComputeInternalInsetsListener(this.mInsetsComputer);
        doFinishInput();
        if (this.mWindowAdded) {
            this.mWindow.getWindow().setWindowAnimations(0);
            this.mWindow.dismiss();
        }
    }

    @Override // android.app.Service, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        boolean z = this.mWindowVisible;
        int i = this.mShowInputFlags;
        boolean z2 = this.mShowInputRequested;
        CompletionInfo[] completionInfoArr = this.mCurCompletions;
        initViews();
        this.mInputViewStarted = false;
        this.mCandidatesViewStarted = false;
        if (this.mInputStarted) {
            doStartInput(getCurrentInputConnection(), getCurrentInputEditorInfo(), true);
        }
        if (z) {
            if (z2) {
                if (onShowInputRequested(i, true)) {
                    showWindow(true);
                    if (completionInfoArr != null) {
                        this.mCurCompletions = completionInfoArr;
                        onDisplayCompletions(completionInfoArr);
                    }
                } else {
                    doHideWindow();
                }
            } else if (this.mCandidatesVisibility == 0) {
                showWindow(false);
            } else {
                doHideWindow();
            }
            this.mImm.setImeWindowStatus(this.mToken, (onEvaluateInputViewShown() ? 2 : 0) | 1, this.mBackDisposition);
        }
    }

    @Override // android.inputmethodservice.AbstractInputMethodService
    public AbstractInputMethodService.AbstractInputMethodImpl onCreateInputMethodInterface() {
        return new InputMethodImpl();
    }

    @Override // android.inputmethodservice.AbstractInputMethodService
    public AbstractInputMethodService.AbstractInputMethodSessionImpl onCreateInputMethodSessionInterface() {
        return new InputMethodSessionImpl();
    }

    public LayoutInflater getLayoutInflater() {
        return this.mInflater;
    }

    public Dialog getWindow() {
        return this.mWindow;
    }

    public void setBackDisposition(int i) {
        this.mBackDisposition = i;
    }

    public int getBackDisposition() {
        return this.mBackDisposition;
    }

    public int getMaxWidth() {
        return ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
    }

    public InputBinding getCurrentInputBinding() {
        return this.mInputBinding;
    }

    public InputConnection getCurrentInputConnection() {
        InputConnection inputConnection = this.mStartedInputConnection;
        return inputConnection != null ? inputConnection : this.mInputConnection;
    }

    public boolean getCurrentInputStarted() {
        return this.mInputStarted;
    }

    public EditorInfo getCurrentInputEditorInfo() {
        return this.mInputEditorInfo;
    }

    public void updateFullscreenMode() {
        View viewOnCreateExtractTextView;
        boolean z = this.mShowInputRequested && onEvaluateFullscreenMode();
        boolean z2 = this.mLastShowInputRequested != this.mShowInputRequested;
        if (this.mIsFullscreen != z || !this.mFullscreenApplied) {
            this.mIsFullscreen = z;
            InputConnection currentInputConnection = getCurrentInputConnection();
            if (currentInputConnection != null) {
                currentInputConnection.reportFullscreenMode(z);
            }
            this.mFullscreenApplied = true;
            initialize();
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mFullscreenArea.getLayoutParams();
            if (z) {
                this.mFullscreenArea.setBackgroundDrawable(this.mThemeAttrs.getDrawable(0));
                layoutParams.height = 0;
                layoutParams.weight = 1.0f;
            } else {
                this.mFullscreenArea.setBackgroundDrawable(null);
                layoutParams.height = -2;
                layoutParams.weight = 0.0f;
            }
            ((ViewGroup) this.mFullscreenArea.getParent()).updateViewLayout(this.mFullscreenArea, layoutParams);
            if (z) {
                if (this.mExtractView == null && (viewOnCreateExtractTextView = onCreateExtractTextView()) != null) {
                    setExtractView(viewOnCreateExtractTextView);
                }
                startExtractingText(false);
            }
            updateExtractFrameVisibility();
            z2 = true;
        }
        if (z2) {
            onConfigureWindow(this.mWindow.getWindow(), z, true ^ this.mShowInputRequested);
            this.mLastShowInputRequested = this.mShowInputRequested;
        }
    }

    public void onConfigureWindow(Window window, boolean z, boolean z2) {
        int i = this.mWindow.getWindow().getAttributes().height;
        int i2 = z ? -1 : -2;
        if (this.mIsInputViewShown && i != i2) {
            Log.w(TAG, "Window size has been changed. This may cause jankiness of resizing window: " + i + " -> " + i2);
        }
        this.mWindow.getWindow().setLayout(-1, i2);
    }

    public boolean isFullscreenMode() {
        return this.mIsFullscreen;
    }

    public boolean onEvaluateFullscreenMode() {
        if (getResources().getConfiguration().orientation != 2) {
            return false;
        }
        EditorInfo editorInfo = this.mInputEditorInfo;
        return editorInfo == null || (editorInfo.imeOptions & 33554432) == 0;
    }

    public void setExtractViewShown(boolean z) {
        if (this.mExtractViewHidden == z) {
            this.mExtractViewHidden = !z;
            updateExtractFrameVisibility();
        }
    }

    public boolean isExtractViewShown() {
        return this.mIsFullscreen && !this.mExtractViewHidden;
    }

    void updateExtractFrameVisibility() {
        int i;
        if (isFullscreenMode()) {
            i = this.mExtractViewHidden ? 4 : 0;
            this.mExtractFrame.setVisibility(i);
        } else {
            this.mExtractFrame.setVisibility(8);
            i = 0;
        }
        updateCandidatesVisibility(this.mCandidatesVisibility == 0);
        if (this.mWindowWasVisible && this.mFullscreenArea.getVisibility() != i) {
            int resourceId = this.mThemeAttrs.getResourceId(i != 0 ? 2 : 1, 0);
            if (resourceId != 0) {
                this.mFullscreenArea.startAnimation(AnimationUtils.loadAnimation(this, resourceId));
            }
        }
        this.mFullscreenArea.setVisibility(i);
    }

    public void onComputeInsets(Insets insets) {
        int[] iArr = this.mTmpLocation;
        if (this.mInputFrame.getVisibility() == 0) {
            this.mInputFrame.getLocationInWindow(iArr);
        } else {
            iArr[1] = getWindow().getWindow().getDecorView().getHeight();
        }
        if (isFullscreenMode()) {
            insets.contentTopInsets = getWindow().getWindow().getDecorView().getHeight();
        } else {
            insets.contentTopInsets = iArr[1];
        }
        if (this.mCandidatesFrame.getVisibility() == 0) {
            this.mCandidatesFrame.getLocationInWindow(iArr);
        }
        insets.visibleTopInsets = iArr[1];
        insets.touchableInsets = 2;
        insets.touchableRegion.setEmpty();
    }

    public void updateInputViewShown() {
        boolean z = this.mShowInputRequested && onEvaluateInputViewShown();
        if (this.mIsInputViewShown == z || !this.mWindowVisible) {
            return;
        }
        this.mIsInputViewShown = z;
        this.mInputFrame.setVisibility(z ? 0 : 8);
        if (this.mInputView == null) {
            initialize();
            View viewOnCreateInputView = onCreateInputView();
            if (viewOnCreateInputView != null) {
                setInputView(viewOnCreateInputView);
            }
        }
    }

    public boolean isShowInputRequested() {
        return this.mShowInputRequested;
    }

    public boolean isInputViewShown() {
        return this.mIsInputViewShown && this.mWindowVisible;
    }

    public boolean onEvaluateInputViewShown() {
        Configuration configuration = getResources().getConfiguration();
        return configuration.keyboard == 1 || configuration.hardKeyboardHidden == 2;
    }

    public void setCandidatesViewShown(boolean z) {
        updateCandidatesVisibility(z);
        if (this.mShowInputRequested || this.mWindowVisible == z) {
            return;
        }
        if (z) {
            showWindow(false);
        } else {
            doHideWindow();
        }
    }

    void updateCandidatesVisibility(boolean z) {
        int candidatesHiddenVisibility = z ? 0 : getCandidatesHiddenVisibility();
        if (this.mCandidatesVisibility != candidatesHiddenVisibility) {
            this.mCandidatesFrame.setVisibility(candidatesHiddenVisibility);
            this.mCandidatesVisibility = candidatesHiddenVisibility;
        }
    }

    public int getCandidatesHiddenVisibility() {
        return isExtractViewShown() ? 8 : 4;
    }

    public void showStatusIcon(int i) {
        this.mStatusIcon = i;
        this.mImm.showStatusIcon(this.mToken, getPackageName(), i);
    }

    public void hideStatusIcon() {
        this.mStatusIcon = 0;
        this.mImm.hideStatusIcon(this.mToken);
    }

    public void switchInputMethod(String str) {
        this.mImm.setInputMethod(this.mToken, str);
    }

    public void setExtractView(View view) {
        this.mExtractFrame.removeAllViews();
        this.mExtractFrame.addView(view, new FrameLayout.LayoutParams(-1, -1));
        this.mExtractView = view;
        if (view != null) {
            ExtractEditText extractEditText = (ExtractEditText) view.findViewById(R.id.inputExtractEditText);
            this.mExtractEditText = extractEditText;
            extractEditText.setIME(this);
            Button button = (Button) view.findViewById(16908986);
            this.mExtractAction = button;
            if (button != null) {
                this.mExtractAccessories = (ViewGroup) view.findViewById(16908985);
            }
            startExtractingText(false);
            return;
        }
        this.mExtractEditText = null;
        this.mExtractAccessories = null;
        this.mExtractAction = null;
    }

    public void setCandidatesView(View view) {
        this.mCandidatesFrame.removeAllViews();
        this.mCandidatesFrame.addView(view, new FrameLayout.LayoutParams(-1, -2));
    }

    public void setInputView(View view) {
        this.mInputFrame.removeAllViews();
        this.mInputFrame.addView(view, new FrameLayout.LayoutParams(-1, -2));
        this.mInputView = view;
    }

    public View onCreateExtractTextView() {
        return this.mInflater.inflate(17367117, (ViewGroup) null);
    }

    public void onFinishInputView(boolean z) {
        InputConnection currentInputConnection;
        if (z || (currentInputConnection = getCurrentInputConnection()) == null) {
            return;
        }
        currentInputConnection.finishComposingText();
    }

    public void onFinishCandidatesView(boolean z) {
        InputConnection currentInputConnection;
        if (z || (currentInputConnection = getCurrentInputConnection()) == null) {
            return;
        }
        currentInputConnection.finishComposingText();
    }

    public boolean onShowInputRequested(int i, boolean z) {
        if (!onEvaluateInputViewShown()) {
            return false;
        }
        if ((i & 1) == 0 && ((!z && onEvaluateFullscreenMode()) || getResources().getConfiguration().keyboard != 1)) {
            return false;
        }
        if ((i & 2) != 0) {
            this.mShowInputForced = true;
        }
        return true;
    }

    public void showWindow(boolean z) {
        if (this.mInShowWindow) {
            Log.w(TAG, "Re-entrance in to showWindow");
            return;
        }
        try {
            this.mWindowWasVisible = this.mWindowVisible;
            this.mInShowWindow = true;
            showWindowInner(z);
        } finally {
            this.mWindowWasVisible = true;
            this.mInShowWindow = false;
        }
    }

    void showWindowInner(boolean z) {
        boolean z2;
        boolean z3 = this.mWindowVisible;
        this.mWindowVisible = true;
        if (!this.mShowInputRequested && this.mInputStarted && z) {
            this.mShowInputRequested = true;
            z2 = true;
        } else {
            z2 = false;
        }
        initialize();
        updateFullscreenMode();
        updateInputViewShown();
        if (!this.mWindowAdded || !this.mWindowCreated) {
            this.mWindowAdded = true;
            this.mWindowCreated = true;
            initialize();
            View viewOnCreateCandidatesView = onCreateCandidatesView();
            if (viewOnCreateCandidatesView != null) {
                setCandidatesView(viewOnCreateCandidatesView);
            }
        }
        if (this.mShowInputRequested) {
            if (!this.mInputViewStarted) {
                this.mInputViewStarted = true;
                onStartInputView(this.mInputEditorInfo, false);
            }
        } else if (!this.mCandidatesViewStarted) {
            this.mCandidatesViewStarted = true;
            onStartCandidatesView(this.mInputEditorInfo, false);
        }
        if (z2) {
            startExtractingText(false);
        }
        if (z3) {
            return;
        }
        this.mImm.setImeWindowStatus(this.mToken, 1, this.mBackDisposition);
        onWindowShown();
        this.mWindow.show();
    }

    private void finishViews() {
        if (this.mInputViewStarted) {
            onFinishInputView(false);
        } else if (this.mCandidatesViewStarted) {
            onFinishCandidatesView(false);
        }
        this.mInputViewStarted = false;
        this.mCandidatesViewStarted = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doHideWindow() {
        this.mImm.setImeWindowStatus(this.mToken, 0, this.mBackDisposition);
        hideWindow();
    }

    public void hideWindow() {
        finishViews();
        if (this.mWindowVisible) {
            this.mWindow.hide();
            this.mWindowVisible = false;
            onWindowHidden();
            this.mWindowWasVisible = false;
        }
    }

    void doFinishInput() {
        if (this.mInputViewStarted) {
            onFinishInputView(true);
        } else if (this.mCandidatesViewStarted) {
            onFinishCandidatesView(true);
        }
        this.mInputViewStarted = false;
        this.mCandidatesViewStarted = false;
        if (this.mInputStarted) {
            onFinishInput();
        }
        this.mInputStarted = false;
        this.mStartedInputConnection = null;
        this.mCurCompletions = null;
    }

    void doStartInput(InputConnection inputConnection, EditorInfo editorInfo, boolean z) {
        if (!z) {
            doFinishInput();
        }
        this.mInputStarted = true;
        this.mStartedInputConnection = inputConnection;
        this.mInputEditorInfo = editorInfo;
        initialize();
        onStartInput(editorInfo, z);
        if (this.mWindowVisible) {
            if (this.mShowInputRequested) {
                this.mInputViewStarted = true;
                onStartInputView(this.mInputEditorInfo, z);
                startExtractingText(true);
            } else if (this.mCandidatesVisibility == 0) {
                this.mCandidatesViewStarted = true;
                onStartCandidatesView(this.mInputEditorInfo, z);
            }
        }
    }

    public void onFinishInput() {
        InputConnection currentInputConnection = getCurrentInputConnection();
        if (currentInputConnection != null) {
            currentInputConnection.finishComposingText();
        }
    }

    public void onUpdateExtractedText(int i, ExtractedText extractedText) {
        ExtractEditText extractEditText;
        if (this.mExtractedToken != i || extractedText == null || (extractEditText = this.mExtractEditText) == null) {
            return;
        }
        this.mExtractedText = extractedText;
        extractEditText.setExtractedText(extractedText);
    }

    public void onUpdateSelection(int i, int i2, int i3, int i4, int i5, int i6) {
        ExtractedText extractedText;
        ExtractEditText extractEditText = this.mExtractEditText;
        if (extractEditText == null || !isFullscreenMode() || (extractedText = this.mExtractedText) == null) {
            return;
        }
        int i7 = extractedText.startOffset;
        extractEditText.startInternalChanges();
        int i8 = i3 - i7;
        int i9 = i4 - i7;
        int length = extractEditText.getText().length();
        if (i8 < 0) {
            i8 = 0;
        } else if (i8 > length) {
            i8 = length;
        }
        if (i9 < 0) {
            i9 = 0;
        } else if (i9 > length) {
            i9 = length;
        }
        extractEditText.setSelection(i8, i9);
        extractEditText.finishInternalChanges();
    }

    public void requestHideSelf(int i) {
        this.mImm.hideSoftInputFromInputMethod(this.mToken, i);
    }

    private void requestShowSelf(int i) {
        this.mImm.showSoftInputFromInputMethod(this.mToken, i);
    }

    private boolean handleBack(boolean z) {
        if (this.mShowInputRequested) {
            if (isExtractViewShown()) {
                View view = this.mExtractView;
                if (view instanceof ExtractEditLayout) {
                    ExtractEditLayout extractEditLayout = (ExtractEditLayout) view;
                    if (extractEditLayout.isActionModeStarted()) {
                        if (z) {
                            extractEditLayout.finishActionMode();
                        }
                        return true;
                    }
                }
            }
            if (z) {
                requestHideSelf(0);
            }
            return true;
        }
        if (!this.mWindowVisible) {
            return false;
        }
        if (this.mCandidatesVisibility == 0) {
            if (z) {
                setCandidatesViewShown(false);
            }
        } else if (z) {
            doHideWindow();
        }
        return true;
    }

    @Override // android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == 4) {
            if (!handleBack(false)) {
                return false;
            }
            keyEvent.startTracking();
            return true;
        }
        return doMovementKey(i, keyEvent, -1);
    }

    @Override // android.view.KeyEvent.Callback
    public boolean onKeyMultiple(int i, int i2, KeyEvent keyEvent) {
        return doMovementKey(i, keyEvent, i2);
    }

    @Override // android.view.KeyEvent.Callback
    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == 4 && keyEvent.isTracking() && !keyEvent.isCanceled()) {
            return handleBack(true);
        }
        return doMovementKey(i, keyEvent, -2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onToggleSoftInput(int i, int i2) {
        if (isInputViewShown()) {
            requestHideSelf(i2);
        } else {
            requestShowSelf(i);
        }
    }

    void reportExtractedMovement(int i, int i2) {
        int i3 = 0;
        switch (i) {
            case 19:
                i2 = -i2;
            case 20:
                i3 = i2;
                i2 = 0;
                break;
            case 21:
                i2 = -i2;
                break;
            case 22:
                break;
            default:
                i2 = 0;
                break;
        }
        onExtractedCursorMovement(i2, i3);
    }

    boolean doMovementKey(int i, KeyEvent keyEvent, int i2) {
        TextView textView = this.mExtractEditText;
        if (isExtractViewShown() && isInputViewShown() && textView != null) {
            MovementMethod movementMethod = textView.getMovementMethod();
            Layout layout = textView.getLayout();
            if (movementMethod != null && layout != null) {
                if (i2 == -1) {
                    if (movementMethod.onKeyDown(textView, textView.getText(), i, keyEvent)) {
                        reportExtractedMovement(i, 1);
                        return true;
                    }
                } else if (i2 == -2) {
                    if (movementMethod.onKeyUp(textView, textView.getText(), i, keyEvent)) {
                        return true;
                    }
                } else if (movementMethod.onKeyOther(textView, textView.getText(), keyEvent)) {
                    reportExtractedMovement(i, i2);
                } else {
                    KeyEvent keyEventChangeAction = KeyEvent.changeAction(keyEvent, 0);
                    if (movementMethod.onKeyDown(textView, textView.getText(), i, keyEventChangeAction)) {
                        KeyEvent keyEventChangeAction2 = KeyEvent.changeAction(keyEvent, 1);
                        movementMethod.onKeyUp(textView, textView.getText(), i, keyEventChangeAction2);
                        while (true) {
                            i2--;
                            if (i2 <= 0) {
                                break;
                            }
                            movementMethod.onKeyDown(textView, textView.getText(), i, keyEventChangeAction);
                            movementMethod.onKeyUp(textView, textView.getText(), i, keyEventChangeAction2);
                        }
                        reportExtractedMovement(i, i2);
                    }
                }
            }
            switch (i) {
            }
            return true;
        }
        return false;
    }

    public void sendDownUpKeyEvents(int i) {
        InputConnection currentInputConnection = getCurrentInputConnection();
        if (currentInputConnection == null) {
            return;
        }
        long jUptimeMillis = SystemClock.uptimeMillis();
        currentInputConnection.sendKeyEvent(new KeyEvent(jUptimeMillis, jUptimeMillis, 0, i, 0, 0, -1, 0, 6));
        currentInputConnection.sendKeyEvent(new KeyEvent(jUptimeMillis, SystemClock.uptimeMillis(), 1, i, 0, 0, -1, 0, 6));
    }

    public boolean sendDefaultEditorAction(boolean z) {
        EditorInfo currentInputEditorInfo = getCurrentInputEditorInfo();
        if (currentInputEditorInfo == null) {
            return false;
        }
        if ((z && (currentInputEditorInfo.imeOptions & 1073741824) != 0) || (currentInputEditorInfo.imeOptions & 255) == 1) {
            return false;
        }
        InputConnection currentInputConnection = getCurrentInputConnection();
        if (currentInputConnection != null) {
            currentInputConnection.performEditorAction(currentInputEditorInfo.imeOptions & 255);
        }
        return true;
    }

    public void sendKeyChar(char c) {
        if (c == '\n') {
            if (sendDefaultEditorAction(true)) {
                return;
            }
            sendDownUpKeyEvents(66);
        } else {
            if (c >= '0' && c <= '9') {
                sendDownUpKeyEvents((c - '0') + 7);
                return;
            }
            InputConnection currentInputConnection = getCurrentInputConnection();
            if (currentInputConnection != null) {
                currentInputConnection.commitText(String.valueOf(c), 1);
            }
        }
    }

    public void onExtractedSelectionChanged(int i, int i2) {
        InputConnection currentInputConnection = getCurrentInputConnection();
        if (currentInputConnection != null) {
            currentInputConnection.setSelection(i, i2);
        }
    }

    public void onExtractedDeleteText(int i, int i2) {
        InputConnection currentInputConnection = getCurrentInputConnection();
        if (currentInputConnection != null) {
            currentInputConnection.setSelection(i, i);
            currentInputConnection.deleteSurroundingText(0, i2 - i);
        }
    }

    public void onExtractedReplaceText(int i, int i2, CharSequence charSequence) {
        InputConnection currentInputConnection = getCurrentInputConnection();
        if (currentInputConnection != null) {
            currentInputConnection.setComposingRegion(i, i2);
            currentInputConnection.commitText(charSequence, 1);
        }
    }

    public void onExtractedSetSpan(Object obj, int i, int i2, int i3) {
        InputConnection currentInputConnection = getCurrentInputConnection();
        if (currentInputConnection == null || !currentInputConnection.setSelection(i, i2)) {
            return;
        }
        CharSequence selectedText = currentInputConnection.getSelectedText(1);
        if (selectedText instanceof Spannable) {
            ((Spannable) selectedText).setSpan(obj, 0, selectedText.length(), i3);
            currentInputConnection.setComposingRegion(i, i2);
            currentInputConnection.commitText(selectedText, 1);
        }
    }

    public void onExtractedTextClicked() {
        ExtractEditText extractEditText = this.mExtractEditText;
        if (extractEditText != null && extractEditText.hasVerticalScrollBar()) {
            setCandidatesViewShown(false);
        }
    }

    public void onExtractedCursorMovement(int i, int i2) {
        ExtractEditText extractEditText = this.mExtractEditText;
        if (extractEditText == null || i2 == 0 || !extractEditText.hasVerticalScrollBar()) {
            return;
        }
        setCandidatesViewShown(false);
    }

    public boolean onExtractTextContextMenuItem(int i) {
        InputConnection currentInputConnection = getCurrentInputConnection();
        if (currentInputConnection == null) {
            return true;
        }
        currentInputConnection.performContextMenuAction(i);
        return true;
    }

    public CharSequence getTextForImeAction(int i) {
        switch (i & 255) {
            case 1:
                return null;
            case 2:
                return getText(17040544);
            case 3:
                return getText(17040545);
            case 4:
                return getText(17040546);
            case 5:
                return getText(17040547);
            case 6:
                return getText(17040548);
            case 7:
                return getText(17040549);
            default:
                return getText(17040550);
        }
    }

    public void onUpdateExtractingVisibility(EditorInfo editorInfo) {
        if (editorInfo.inputType == 0 || (editorInfo.imeOptions & 268435456) != 0) {
            setExtractViewShown(false);
        } else {
            setExtractViewShown(true);
        }
    }

    public void onUpdateExtractingViews(EditorInfo editorInfo) {
        if (isExtractViewShown() && this.mExtractAccessories != null) {
            boolean z = true;
            if (editorInfo.actionLabel == null && ((editorInfo.imeOptions & 255) == 1 || (editorInfo.imeOptions & 536870912) != 0 || editorInfo.inputType == 0)) {
                z = false;
            }
            if (z) {
                this.mExtractAccessories.setVisibility(0);
                if (this.mExtractAction != null) {
                    if (editorInfo.actionLabel != null) {
                        this.mExtractAction.setText(editorInfo.actionLabel);
                    } else {
                        this.mExtractAction.setText(getTextForImeAction(editorInfo.imeOptions));
                    }
                    this.mExtractAction.setOnClickListener(this.mActionClickListener);
                    return;
                }
                return;
            }
            this.mExtractAccessories.setVisibility(8);
            Button button = this.mExtractAction;
            if (button != null) {
                button.setOnClickListener(null);
            }
        }
    }

    public void onExtractingInputChanged(EditorInfo editorInfo) {
        if (editorInfo.inputType == 0) {
            requestHideSelf(2);
        }
    }

    void startExtractingText(boolean z) {
        ExtractEditText extractEditText = this.mExtractEditText;
        if (extractEditText != null && getCurrentInputStarted() && isFullscreenMode()) {
            this.mExtractedToken++;
            ExtractedTextRequest extractedTextRequest = new ExtractedTextRequest();
            extractedTextRequest.token = this.mExtractedToken;
            extractedTextRequest.flags = 1;
            extractedTextRequest.hintMaxLines = 10;
            extractedTextRequest.hintMaxChars = 10000;
            InputConnection currentInputConnection = getCurrentInputConnection();
            ExtractedText extractedText = currentInputConnection == null ? null : currentInputConnection.getExtractedText(extractedTextRequest, 1);
            this.mExtractedText = extractedText;
            if (extractedText == null || currentInputConnection == null) {
                Log.e(TAG, "Unexpected null in startExtractingText : mExtractedText = " + this.mExtractedText + ", input connection = " + currentInputConnection);
            }
            EditorInfo currentInputEditorInfo = getCurrentInputEditorInfo();
            try {
                extractEditText.startInternalChanges();
                onUpdateExtractingVisibility(currentInputEditorInfo);
                onUpdateExtractingViews(currentInputEditorInfo);
                int i = currentInputEditorInfo.inputType;
                if ((i & 15) == 1 && (262144 & i) != 0) {
                    i |= 131072;
                }
                extractEditText.setInputType(i);
                extractEditText.setHint(currentInputEditorInfo.hintText);
                if (this.mExtractedText != null) {
                    extractEditText.setEnabled(true);
                    extractEditText.setExtractedText(this.mExtractedText);
                } else {
                    extractEditText.setEnabled(false);
                    extractEditText.setText("");
                }
                if (z) {
                    onExtractingInputChanged(currentInputEditorInfo);
                }
            } finally {
                extractEditText.finishInternalChanges();
            }
        }
    }

    @Override // android.inputmethodservice.AbstractInputMethodService, android.app.Service
    protected void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        PrintWriterPrinter printWriterPrinter = new PrintWriterPrinter(printWriter);
        printWriterPrinter.println("Input method service state for " + this + ":");
        printWriterPrinter.println("  mWindowCreated=" + this.mWindowCreated + " mWindowAdded=" + this.mWindowAdded);
        printWriterPrinter.println("  mWindowVisible=" + this.mWindowVisible + " mWindowWasVisible=" + this.mWindowWasVisible + " mInShowWindow=" + this.mInShowWindow);
        printWriterPrinter.println("  Configuration=" + getResources().getConfiguration());
        printWriterPrinter.println("  mToken=" + this.mToken);
        printWriterPrinter.println("  mInputBinding=" + this.mInputBinding);
        printWriterPrinter.println("  mInputConnection=" + this.mInputConnection);
        printWriterPrinter.println("  mStartedInputConnection=" + this.mStartedInputConnection);
        printWriterPrinter.println("  mInputStarted=" + this.mInputStarted + " mInputViewStarted=" + this.mInputViewStarted + " mCandidatesViewStarted=" + this.mCandidatesViewStarted);
        if (this.mInputEditorInfo != null) {
            printWriterPrinter.println("  mInputEditorInfo:");
            this.mInputEditorInfo.dump(printWriterPrinter, "    ");
        } else {
            printWriterPrinter.println("  mInputEditorInfo: null");
        }
        printWriterPrinter.println("  mShowInputRequested=" + this.mShowInputRequested + " mLastShowInputRequested=" + this.mLastShowInputRequested + " mShowInputForced=" + this.mShowInputForced + " mShowInputFlags=0x" + Integer.toHexString(this.mShowInputFlags));
        printWriterPrinter.println("  mCandidatesVisibility=" + this.mCandidatesVisibility + " mFullscreenApplied=" + this.mFullscreenApplied + " mIsFullscreen=" + this.mIsFullscreen + " mExtractViewHidden=" + this.mExtractViewHidden);
        if (this.mExtractedText != null) {
            printWriterPrinter.println("  mExtractedText:");
            printWriterPrinter.println("    text=" + this.mExtractedText.text.length() + " chars startOffset=" + this.mExtractedText.startOffset);
            printWriterPrinter.println("    selectionStart=" + this.mExtractedText.selectionStart + " selectionEnd=" + this.mExtractedText.selectionEnd + " flags=0x" + Integer.toHexString(this.mExtractedText.flags));
        } else {
            printWriterPrinter.println("  mExtractedText: null");
        }
        printWriterPrinter.println("  mExtractedToken=" + this.mExtractedToken);
        printWriterPrinter.println("  mIsInputViewShown=" + this.mIsInputViewShown + " mStatusIcon=" + this.mStatusIcon);
        printWriterPrinter.println("Last computed insets:");
        printWriterPrinter.println("  contentTopInsets=" + this.mTmpInsets.contentTopInsets + " visibleTopInsets=" + this.mTmpInsets.visibleTopInsets + " touchableInsets=" + this.mTmpInsets.touchableInsets + " touchableRegion=" + this.mTmpInsets.touchableRegion);
    }
}
