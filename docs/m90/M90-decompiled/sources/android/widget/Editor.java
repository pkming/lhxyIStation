package android.widget;

import android.R;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.UndoManager;
import android.content.UndoOperation;
import android.content.UndoOwner;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.ExtractEditText;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.provider.Settings;
import android.provider.UserDictionary;
import android.text.DynamicLayout;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Layout;
import android.text.ParcelableSpan;
import android.text.Selection;
import android.text.SpanWatcher;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.method.KeyListener;
import android.text.method.MetaKeyKeyListener;
import android.text.method.MovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.WordIterator;
import android.text.style.EasyEditSpan;
import android.text.style.SuggestionRangeSpan;
import android.text.style.SuggestionSpan;
import android.text.style.TextAppearanceSpan;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ActionMode;
import android.view.DisplayList;
import android.view.DragEvent;
import android.view.HardwareCanvas;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.CorrectionInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.TextView;
import com.android.internal.util.ArrayUtils;
import com.android.internal.widget.EditableInputConnection;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public class Editor {
    static final int BLINK = 500;
    static final boolean DEBUG_UNDO = false;
    private static int DRAG_SHADOW_MAX_TEXT_LENGTH = 20;
    static final int EXTRACT_NOTHING = -2;
    static final int EXTRACT_UNKNOWN = -1;
    private static final String TAG = "Editor";
    private static final float[] TEMP_POSITION = new float[2];
    Blink mBlink;
    CorrectionHighlighter mCorrectionHighlighter;
    boolean mCreatedWithASelection;
    int mCursorCount;
    ActionMode.Callback mCustomSelectionActionModeCallback;
    boolean mDiscardNextActionUp;
    CharSequence mError;
    ErrorPopup mErrorPopup;
    boolean mErrorWasChanged;
    boolean mFrozenWithFocus;
    boolean mIgnoreActionUpEvent;
    boolean mInBatchEditControllers;
    InputContentType mInputContentType;
    InputMethodState mInputMethodState;
    boolean mInsertionControllerEnabled;
    InsertionPointCursorController mInsertionPointCursorController;
    KeyListener mKeyListener;
    float mLastDownPositionX;
    float mLastDownPositionY;
    private PositionListener mPositionListener;
    boolean mPreserveDetachedSelection;
    boolean mSelectAllOnFocus;
    private Drawable mSelectHandleCenter;
    private Drawable mSelectHandleLeft;
    private Drawable mSelectHandleRight;
    ActionMode mSelectionActionMode;
    boolean mSelectionControllerEnabled;
    SelectionModifierCursorController mSelectionModifierCursorController;
    boolean mSelectionMoved;
    long mShowCursor;
    boolean mShowErrorAfterAttach;
    Runnable mShowSuggestionRunnable;
    private SpanController mSpanController;
    SpellChecker mSpellChecker;
    SuggestionRangeSpan mSuggestionRangeSpan;
    SuggestionsPopupWindow mSuggestionsPopupWindow;
    private Rect mTempRect;
    boolean mTemporaryDetach;
    DisplayList[] mTextDisplayLists;
    boolean mTextIsSelectable;
    private TextView mTextView;
    boolean mTouchFocusSelected;
    InputFilter mUndoInputFilter;
    UndoManager mUndoManager;
    UndoOwner mUndoOwner;
    WordIterator mWordIterator;
    int mInputType = 0;
    boolean mCursorVisible = true;
    boolean mShowSoftInputOnFocus = true;
    final Drawable[] mCursorDrawable = new Drawable[2];

    private interface CursorController extends ViewTreeObserver.OnTouchModeChangeListener {
        void hide();

        void onDetached();

        void show();
    }

    private interface EasyEditDeleteListener {
        void onDeleteClick(EasyEditSpan easyEditSpan);
    }

    private interface TextViewPositionListener {
        void updatePosition(int i, int i2, boolean z, boolean z2);
    }

    Editor(TextView textView) {
        this.mTextView = textView;
    }

    void onAttachedToWindow() {
        if (this.mShowErrorAfterAttach) {
            showError();
            this.mShowErrorAfterAttach = false;
        }
        this.mTemporaryDetach = false;
        ViewTreeObserver viewTreeObserver = this.mTextView.getViewTreeObserver();
        InsertionPointCursorController insertionPointCursorController = this.mInsertionPointCursorController;
        if (insertionPointCursorController != null) {
            viewTreeObserver.addOnTouchModeChangeListener(insertionPointCursorController);
        }
        SelectionModifierCursorController selectionModifierCursorController = this.mSelectionModifierCursorController;
        if (selectionModifierCursorController != null) {
            selectionModifierCursorController.resetTouchOffsets();
            viewTreeObserver.addOnTouchModeChangeListener(this.mSelectionModifierCursorController);
        }
        updateSpellCheckSpans(0, this.mTextView.getText().length(), true);
        if (!this.mTextView.hasTransientState() || this.mTextView.getSelectionStart() == this.mTextView.getSelectionEnd()) {
            return;
        }
        this.mTextView.setHasTransientState(false);
        startSelectionActionMode();
    }

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
    void onDetachedFromWindow() {
        if (this.mError != null) {
            hideError();
        }
        Blink blink = this.mBlink;
        if (blink != null) {
            blink.removeCallbacks(blink);
        }
        InsertionPointCursorController insertionPointCursorController = this.mInsertionPointCursorController;
        if (insertionPointCursorController != null) {
            insertionPointCursorController.onDetached();
        }
        SelectionModifierCursorController selectionModifierCursorController = this.mSelectionModifierCursorController;
        if (selectionModifierCursorController != null) {
            selectionModifierCursorController.onDetached();
        }
        Runnable runnable = this.mShowSuggestionRunnable;
        if (runnable != null) {
            this.mTextView.removeCallbacks(runnable);
        }
        invalidateTextDisplayList();
        SpellChecker spellChecker = this.mSpellChecker;
        if (spellChecker != null) {
            spellChecker.closeSession();
            this.mSpellChecker = null;
        }
        this.mPreserveDetachedSelection = true;
        hideControllers();
        this.mPreserveDetachedSelection = false;
        this.mTemporaryDetach = false;
    }

    private void showError() {
        if (this.mTextView.getWindowToken() == null) {
            this.mShowErrorAfterAttach = true;
            return;
        }
        if (this.mErrorPopup == null) {
            TextView textView = (TextView) LayoutInflater.from(this.mTextView.getContext()).inflate(17367217, (ViewGroup) null);
            float f = this.mTextView.getResources().getDisplayMetrics().density;
            ErrorPopup errorPopup = new ErrorPopup(textView, (int) ((200.0f * f) + 0.5f), (int) ((f * 50.0f) + 0.5f));
            this.mErrorPopup = errorPopup;
            errorPopup.setFocusable(false);
            this.mErrorPopup.setInputMethodMode(1);
        }
        TextView textView2 = (TextView) this.mErrorPopup.getContentView();
        chooseSize(this.mErrorPopup, this.mError, textView2);
        textView2.setText(this.mError);
        this.mErrorPopup.showAsDropDown(this.mTextView, getErrorX(), getErrorY());
        ErrorPopup errorPopup2 = this.mErrorPopup;
        errorPopup2.fixDirection(errorPopup2.isAboveAnchor());
    }

    public void setError(CharSequence charSequence, Drawable drawable) {
        CharSequence charSequenceStringOrSpannedString = TextUtils.stringOrSpannedString(charSequence);
        this.mError = charSequenceStringOrSpannedString;
        this.mErrorWasChanged = true;
        if (charSequenceStringOrSpannedString == null) {
            setErrorIcon(null);
            ErrorPopup errorPopup = this.mErrorPopup;
            if (errorPopup != null) {
                if (errorPopup.isShowing()) {
                    this.mErrorPopup.dismiss();
                }
                this.mErrorPopup = null;
                return;
            }
            return;
        }
        setErrorIcon(drawable);
        if (this.mTextView.isFocused()) {
            showError();
        }
    }

    private void setErrorIcon(Drawable drawable) {
        TextView.Drawables drawables = this.mTextView.mDrawables;
        if (drawables == null) {
            TextView textView = this.mTextView;
            TextView.Drawables drawables2 = new TextView.Drawables(this.mTextView.getContext());
            textView.mDrawables = drawables2;
            drawables = drawables2;
        }
        drawables.setErrorDrawable(drawable, this.mTextView);
        this.mTextView.resetResolvedDrawables();
        this.mTextView.invalidate();
        this.mTextView.requestLayout();
    }

    private void hideError() {
        ErrorPopup errorPopup = this.mErrorPopup;
        if (errorPopup != null && errorPopup.isShowing()) {
            this.mErrorPopup.dismiss();
        }
        this.mShowErrorAfterAttach = false;
    }

    private int getErrorX() {
        float f = this.mTextView.getResources().getDisplayMetrics().density;
        TextView.Drawables drawables = this.mTextView.mDrawables;
        if (this.mTextView.getLayoutDirection() != 1) {
            return ((this.mTextView.getWidth() - this.mErrorPopup.getWidth()) - this.mTextView.getPaddingRight()) + ((-(drawables != null ? drawables.mDrawableSizeRight : 0)) / 2) + ((int) ((f * 25.0f) + 0.5f));
        }
        return this.mTextView.getPaddingLeft() + (((drawables != null ? drawables.mDrawableSizeLeft : 0) / 2) - ((int) ((f * 25.0f) + 0.5f)));
    }

    private int getErrorY() {
        int compoundPaddingTop = this.mTextView.getCompoundPaddingTop();
        int bottom = ((this.mTextView.getBottom() - this.mTextView.getTop()) - this.mTextView.getCompoundPaddingBottom()) - compoundPaddingTop;
        TextView.Drawables drawables = this.mTextView.mDrawables;
        int i = 0;
        if (this.mTextView.getLayoutDirection() != 1) {
            if (drawables != null) {
                i = drawables.mDrawableHeightRight;
            }
        } else if (drawables != null) {
            i = drawables.mDrawableHeightLeft;
        }
        return (((compoundPaddingTop + ((bottom - i) / 2)) + i) - this.mTextView.getHeight()) - ((int) ((this.mTextView.getResources().getDisplayMetrics().density * 2.0f) + 0.5f));
    }

    void createInputContentTypeIfNeeded() {
        if (this.mInputContentType == null) {
            this.mInputContentType = new InputContentType();
        }
    }

    void createInputMethodStateIfNeeded() {
        if (this.mInputMethodState == null) {
            this.mInputMethodState = new InputMethodState();
        }
    }

    boolean isCursorVisible() {
        return this.mCursorVisible && this.mTextView.isTextEditable();
    }

    /* JADX WARN: Removed duplicated region for block: B:9:0x0020  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    void prepareCursorControllers() {
        /*
            r5 = this;
            android.widget.TextView r0 = r5.mTextView
            android.view.View r0 = r0.getRootView()
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            boolean r1 = r0 instanceof android.view.WindowManager.LayoutParams
            r2 = 1
            r3 = 0
            if (r1 == 0) goto L20
            android.view.WindowManager$LayoutParams r0 = (android.view.WindowManager.LayoutParams) r0
            int r1 = r0.type
            r4 = 1000(0x3e8, float:1.401E-42)
            if (r1 < r4) goto L1e
            int r0 = r0.type
            r1 = 1999(0x7cf, float:2.801E-42)
            if (r0 <= r1) goto L20
        L1e:
            r0 = r2
            goto L21
        L20:
            r0 = r3
        L21:
            if (r0 == 0) goto L2d
            android.widget.TextView r0 = r5.mTextView
            android.text.Layout r0 = r0.getLayout()
            if (r0 == 0) goto L2d
            r0 = r2
            goto L2e
        L2d:
            r0 = r3
        L2e:
            if (r0 == 0) goto L38
            boolean r1 = r5.isCursorVisible()
            if (r1 == 0) goto L38
            r1 = r2
            goto L39
        L38:
            r1 = r3
        L39:
            r5.mInsertionControllerEnabled = r1
            if (r0 == 0) goto L46
            android.widget.TextView r0 = r5.mTextView
            boolean r0 = r0.textCanBeSelected()
            if (r0 == 0) goto L46
            goto L47
        L46:
            r2 = r3
        L47:
            r5.mSelectionControllerEnabled = r2
            boolean r0 = r5.mInsertionControllerEnabled
            r1 = 0
            if (r0 != 0) goto L5a
            r5.hideInsertionPointCursorController()
            android.widget.Editor$InsertionPointCursorController r0 = r5.mInsertionPointCursorController
            if (r0 == 0) goto L5a
            r0.onDetached()
            r5.mInsertionPointCursorController = r1
        L5a:
            boolean r0 = r5.mSelectionControllerEnabled
            if (r0 != 0) goto L6a
            r5.stopSelectionActionMode()
            android.widget.Editor$SelectionModifierCursorController r0 = r5.mSelectionModifierCursorController
            if (r0 == 0) goto L6a
            r0.onDetached()
            r5.mSelectionModifierCursorController = r1
        L6a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.widget.Editor.prepareCursorControllers():void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideInsertionPointCursorController() {
        InsertionPointCursorController insertionPointCursorController = this.mInsertionPointCursorController;
        if (insertionPointCursorController != null) {
            insertionPointCursorController.hide();
        }
    }

    void hideControllers() {
        hideCursorControllers();
        hideSpanControllers();
    }

    private void hideSpanControllers() {
        SpanController spanController = this.mSpanController;
        if (spanController != null) {
            spanController.hide();
        }
    }

    private void hideCursorControllers() {
        SuggestionsPopupWindow suggestionsPopupWindow = this.mSuggestionsPopupWindow;
        if (suggestionsPopupWindow != null && !suggestionsPopupWindow.isShowingUp()) {
            this.mSuggestionsPopupWindow.hide();
        }
        hideInsertionPointCursorController();
        stopSelectionActionMode();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSpellCheckSpans(int i, int i2, boolean z) {
        this.mTextView.removeAdjacentSuggestionSpans(i);
        this.mTextView.removeAdjacentSuggestionSpans(i2);
        if (this.mTextView.isTextEditable() && this.mTextView.isSuggestionsEnabled() && !(this.mTextView instanceof ExtractEditText)) {
            if (this.mSpellChecker == null && z) {
                this.mSpellChecker = new SpellChecker(this.mTextView);
            }
            SpellChecker spellChecker = this.mSpellChecker;
            if (spellChecker != null) {
                spellChecker.spellCheck(i, i2);
            }
        }
    }

    void onScreenStateChanged(int i) {
        if (i == 0) {
            suspendBlink();
        } else {
            if (i != 1) {
                return;
            }
            resumeBlink();
        }
    }

    private void suspendBlink() {
        Blink blink = this.mBlink;
        if (blink != null) {
            blink.cancel();
        }
    }

    private void resumeBlink() {
        Blink blink = this.mBlink;
        if (blink != null) {
            blink.uncancel();
            makeBlink();
        }
    }

    void adjustInputType(boolean z, boolean z2, boolean z3, boolean z4) {
        int i = this.mInputType;
        if ((i & 15) != 1) {
            if ((i & 15) == 2 && z4) {
                this.mInputType = (i & (-4081)) | 16;
                return;
            }
            return;
        }
        if (z || z2) {
            this.mInputType = (i & (-4081)) | 128;
        }
        if (z3) {
            this.mInputType = (this.mInputType & (-4081)) | 224;
        }
    }

    private void chooseSize(PopupWindow popupWindow, CharSequence charSequence, TextView textView) {
        int paddingLeft = textView.getPaddingLeft() + textView.getPaddingRight();
        int paddingTop = textView.getPaddingTop() + textView.getPaddingBottom();
        StaticLayout staticLayout = new StaticLayout(charSequence, textView.getPaint(), this.mTextView.getResources().getDimensionPixelSize(17104983), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
        float fMax = 0.0f;
        for (int i = 0; i < staticLayout.getLineCount(); i++) {
            fMax = Math.max(fMax, staticLayout.getLineWidth(i));
        }
        popupWindow.setWidth(paddingLeft + ((int) Math.ceil(fMax)));
        popupWindow.setHeight(paddingTop + staticLayout.getHeight());
    }

    void setFrame() {
        ErrorPopup errorPopup = this.mErrorPopup;
        if (errorPopup != null) {
            chooseSize(this.mErrorPopup, this.mError, (TextView) errorPopup.getContentView());
            this.mErrorPopup.update(this.mTextView, getErrorX(), getErrorY(), this.mErrorPopup.getWidth(), this.mErrorPopup.getHeight());
        }
    }

    private boolean canSelectText() {
        return hasSelectionController() && this.mTextView.getText().length() != 0;
    }

    private boolean hasPasswordTransformationMethod() {
        return this.mTextView.getTransformationMethod() instanceof PasswordTransformationMethod;
    }

    private boolean selectCurrentWord() {
        int spanStart;
        int spanEnd;
        if (!canSelectText()) {
            return false;
        }
        if (hasPasswordTransformationMethod()) {
            return this.mTextView.selectAllText();
        }
        int inputType = this.mTextView.getInputType();
        int i = inputType & 15;
        int i2 = inputType & InputType.TYPE_MASK_VARIATION;
        if (i == 2 || i == 3 || i == 4 || i2 == 16 || i2 == 32 || i2 == 208 || i2 == 176) {
            return this.mTextView.selectAllText();
        }
        long lastTouchOffsets = getLastTouchOffsets();
        int iUnpackRangeStartFromLong = TextUtils.unpackRangeStartFromLong(lastTouchOffsets);
        int iUnpackRangeEndFromLong = TextUtils.unpackRangeEndFromLong(lastTouchOffsets);
        if (iUnpackRangeStartFromLong < 0 || iUnpackRangeStartFromLong >= this.mTextView.getText().length() || iUnpackRangeEndFromLong < 0 || iUnpackRangeEndFromLong >= this.mTextView.getText().length()) {
            return false;
        }
        URLSpan[] uRLSpanArr = (URLSpan[]) ((Spanned) this.mTextView.getText()).getSpans(iUnpackRangeStartFromLong, iUnpackRangeEndFromLong, URLSpan.class);
        if (uRLSpanArr.length >= 1) {
            URLSpan uRLSpan = uRLSpanArr[0];
            spanStart = ((Spanned) this.mTextView.getText()).getSpanStart(uRLSpan);
            spanEnd = ((Spanned) this.mTextView.getText()).getSpanEnd(uRLSpan);
        } else {
            WordIterator wordIterator = getWordIterator();
            wordIterator.setCharSequence(this.mTextView.getText(), iUnpackRangeStartFromLong, iUnpackRangeEndFromLong);
            int beginning = wordIterator.getBeginning(iUnpackRangeStartFromLong);
            int end = wordIterator.getEnd(iUnpackRangeEndFromLong);
            if (beginning == -1 || end == -1 || beginning == end) {
                long charRange = getCharRange(iUnpackRangeStartFromLong);
                int iUnpackRangeStartFromLong2 = TextUtils.unpackRangeStartFromLong(charRange);
                int iUnpackRangeEndFromLong2 = TextUtils.unpackRangeEndFromLong(charRange);
                spanStart = iUnpackRangeStartFromLong2;
                spanEnd = iUnpackRangeEndFromLong2;
            } else {
                spanEnd = end;
                spanStart = beginning;
            }
        }
        Selection.setSelection((Spannable) this.mTextView.getText(), spanStart, spanEnd);
        return spanEnd > spanStart;
    }

    void onLocaleChanged() {
        this.mWordIterator = null;
    }

    public WordIterator getWordIterator() {
        if (this.mWordIterator == null) {
            this.mWordIterator = new WordIterator(this.mTextView.getTextServicesLocale());
        }
        return this.mWordIterator;
    }

    private long getCharRange(int i) {
        int length = this.mTextView.getText().length();
        int i2 = i + 1;
        if (i2 < length && Character.isSurrogatePair(this.mTextView.getText().charAt(i), this.mTextView.getText().charAt(i2))) {
            return TextUtils.packRangeInLong(i, i + 2);
        }
        if (i < length) {
            return TextUtils.packRangeInLong(i, i2);
        }
        int i3 = i - 2;
        if (i3 >= 0) {
            if (Character.isSurrogatePair(this.mTextView.getText().charAt(i3), this.mTextView.getText().charAt(i - 1))) {
                return TextUtils.packRangeInLong(i3, i);
            }
        }
        int i4 = i - 1;
        if (i4 >= 0) {
            return TextUtils.packRangeInLong(i4, i);
        }
        return TextUtils.packRangeInLong(i, i);
    }

    private boolean touchPositionIsInSelection() {
        int selectionStart = this.mTextView.getSelectionStart();
        int selectionEnd = this.mTextView.getSelectionEnd();
        if (selectionStart == selectionEnd) {
            return false;
        }
        if (selectionStart > selectionEnd) {
            Selection.setSelection((Spannable) this.mTextView.getText(), selectionEnd, selectionStart);
            selectionEnd = selectionStart;
            selectionStart = selectionEnd;
        }
        SelectionModifierCursorController selectionController = getSelectionController();
        return selectionController.getMinTouchOffset() >= selectionStart && selectionController.getMaxTouchOffset() < selectionEnd;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public PositionListener getPositionListener() {
        if (this.mPositionListener == null) {
            this.mPositionListener = new PositionListener();
        }
        return this.mPositionListener;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isPositionVisible(int i, int i2) {
        float[] fArr = TEMP_POSITION;
        synchronized (fArr) {
            fArr[0] = i;
            fArr[1] = i2;
            View view = this.mTextView;
            while (view != null) {
                if (view != this.mTextView) {
                    fArr[0] = fArr[0] - view.getScrollX();
                    fArr[1] = fArr[1] - view.getScrollY();
                }
                if (fArr[0] >= 0.0f && fArr[1] >= 0.0f && fArr[0] <= view.getWidth() && fArr[1] <= view.getHeight()) {
                    if (!view.getMatrix().isIdentity()) {
                        view.getMatrix().mapPoints(fArr);
                    }
                    fArr[0] = fArr[0] + view.getLeft();
                    fArr[1] = fArr[1] + view.getTop();
                    Object parent = view.getParent();
                    view = parent instanceof View ? (View) parent : null;
                }
                return false;
            }
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isOffsetVisible(int i) {
        Layout layout = this.mTextView.getLayout();
        if (layout == null) {
            return false;
        }
        return isPositionVisible(((int) layout.getPrimaryHorizontal(i)) + this.mTextView.viewportToContentHorizontalOffset(), layout.getLineBottom(layout.getLineForOffset(i)) + this.mTextView.viewportToContentVerticalOffset());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isPositionOnText(float f, float f2) {
        Layout layout = this.mTextView.getLayout();
        if (layout == null) {
            return false;
        }
        int lineAtCoordinate = this.mTextView.getLineAtCoordinate(f2);
        float fConvertToLocalHorizontalCoordinate = this.mTextView.convertToLocalHorizontalCoordinate(f);
        return fConvertToLocalHorizontalCoordinate >= layout.getLineLeft(lineAtCoordinate) && fConvertToLocalHorizontalCoordinate <= layout.getLineRight(lineAtCoordinate);
    }

    public boolean performLongClick(boolean z) {
        boolean z2 = true;
        if (!z && !isPositionOnText(this.mLastDownPositionX, this.mLastDownPositionY) && this.mInsertionControllerEnabled) {
            int offsetForPosition = this.mTextView.getOffsetForPosition(this.mLastDownPositionX, this.mLastDownPositionY);
            stopSelectionActionMode();
            Selection.setSelection((Spannable) this.mTextView.getText(), offsetForPosition);
            getInsertionController().showWithActionPopup();
            z = true;
        }
        if (z || this.mSelectionActionMode == null) {
            z2 = z;
        } else if (touchPositionIsInSelection()) {
            int selectionStart = this.mTextView.getSelectionStart();
            int selectionEnd = this.mTextView.getSelectionEnd();
            CharSequence transformedText = this.mTextView.getTransformedText(selectionStart, selectionEnd);
            this.mTextView.startDrag(ClipData.newPlainText(null, transformedText), getTextThumbnailBuilder(transformedText), new DragLocalState(this.mTextView, selectionStart, selectionEnd), 0);
            stopSelectionActionMode();
        } else {
            getSelectionController().hide();
            selectCurrentWord();
            getSelectionController().show();
        }
        return !z2 ? startSelectionActionMode() : z2;
    }

    private long getLastTouchOffsets() {
        SelectionModifierCursorController selectionController = getSelectionController();
        return TextUtils.packRangeInLong(selectionController.getMinTouchOffset(), selectionController.getMaxTouchOffset());
    }

    void onFocusChanged(boolean z, int i) {
        this.mShowCursor = SystemClock.uptimeMillis();
        ensureEndedBatchEdit();
        if (z) {
            int selectionStart = this.mTextView.getSelectionStart();
            int selectionEnd = this.mTextView.getSelectionEnd();
            this.mCreatedWithASelection = this.mFrozenWithFocus && this.mTextView.hasSelection() && !(this.mSelectAllOnFocus && selectionStart == 0 && selectionEnd == this.mTextView.getText().length());
            if (!this.mFrozenWithFocus || selectionStart < 0 || selectionEnd < 0) {
                int lastTapPosition = getLastTapPosition();
                if (lastTapPosition >= 0) {
                    Selection.setSelection((Spannable) this.mTextView.getText(), lastTapPosition);
                }
                MovementMethod movementMethod = this.mTextView.getMovementMethod();
                if (movementMethod != null) {
                    TextView textView = this.mTextView;
                    movementMethod.onTakeFocus(textView, (Spannable) textView.getText(), i);
                }
                TextView textView2 = this.mTextView;
                if (((textView2 instanceof ExtractEditText) || this.mSelectionMoved) && selectionStart >= 0 && selectionEnd >= 0) {
                    Selection.setSelection((Spannable) textView2.getText(), selectionStart, selectionEnd);
                }
                if (this.mSelectAllOnFocus) {
                    this.mTextView.selectAllText();
                }
                this.mTouchFocusSelected = true;
            }
            this.mFrozenWithFocus = false;
            this.mSelectionMoved = false;
            if (this.mError != null) {
                showError();
            }
            makeBlink();
            return;
        }
        if (this.mError != null) {
            hideError();
        }
        this.mTextView.onEndBatchEdit();
        TextView textView3 = this.mTextView;
        if (textView3 instanceof ExtractEditText) {
            int selectionStart2 = textView3.getSelectionStart();
            int selectionEnd2 = this.mTextView.getSelectionEnd();
            hideControllers();
            Selection.setSelection((Spannable) this.mTextView.getText(), selectionStart2, selectionEnd2);
        } else {
            if (this.mTemporaryDetach) {
                this.mPreserveDetachedSelection = true;
            }
            hideControllers();
            if (this.mTemporaryDetach) {
                this.mPreserveDetachedSelection = false;
            }
            downgradeEasyCorrectionSpans();
        }
        SelectionModifierCursorController selectionModifierCursorController = this.mSelectionModifierCursorController;
        if (selectionModifierCursorController != null) {
            selectionModifierCursorController.resetTouchOffsets();
        }
    }

    private void downgradeEasyCorrectionSpans() {
        CharSequence text = this.mTextView.getText();
        if (text instanceof Spannable) {
            Spannable spannable = (Spannable) text;
            SuggestionSpan[] suggestionSpanArr = (SuggestionSpan[]) spannable.getSpans(0, spannable.length(), SuggestionSpan.class);
            for (int i = 0; i < suggestionSpanArr.length; i++) {
                int flags = suggestionSpanArr[i].getFlags();
                if ((flags & 1) != 0 && (flags & 2) == 0) {
                    suggestionSpanArr[i].setFlags(flags & (-2));
                }
            }
        }
    }

    void sendOnTextChanged(int i, int i2) {
        updateSpellCheckSpans(i, i2 + i, false);
        hideCursorControllers();
    }

    private int getLastTapPosition() {
        int minTouchOffset;
        SelectionModifierCursorController selectionModifierCursorController = this.mSelectionModifierCursorController;
        if (selectionModifierCursorController == null || (minTouchOffset = selectionModifierCursorController.getMinTouchOffset()) < 0) {
            return -1;
        }
        return minTouchOffset > this.mTextView.getText().length() ? this.mTextView.getText().length() : minTouchOffset;
    }

    void onWindowFocusChanged(boolean z) {
        if (z) {
            Blink blink = this.mBlink;
            if (blink != null) {
                blink.uncancel();
                makeBlink();
                return;
            }
            return;
        }
        Blink blink2 = this.mBlink;
        if (blink2 != null) {
            blink2.cancel();
        }
        InputContentType inputContentType = this.mInputContentType;
        if (inputContentType != null) {
            inputContentType.enterDown = false;
        }
        hideControllers();
        SuggestionsPopupWindow suggestionsPopupWindow = this.mSuggestionsPopupWindow;
        if (suggestionsPopupWindow != null) {
            suggestionsPopupWindow.onParentLostFocus();
        }
        ensureEndedBatchEdit();
    }

    void onTouchEvent(MotionEvent motionEvent) {
        if (hasSelectionController()) {
            getSelectionController().onTouchEvent(motionEvent);
        }
        Runnable runnable = this.mShowSuggestionRunnable;
        if (runnable != null) {
            this.mTextView.removeCallbacks(runnable);
            this.mShowSuggestionRunnable = null;
        }
        if (motionEvent.getActionMasked() == 0) {
            this.mLastDownPositionX = motionEvent.getX();
            this.mLastDownPositionY = motionEvent.getY();
            this.mTouchFocusSelected = false;
            this.mIgnoreActionUpEvent = false;
        }
    }

    public void beginBatchEdit() {
        this.mInBatchEditControllers = true;
        InputMethodState inputMethodState = this.mInputMethodState;
        if (inputMethodState != null) {
            int i = inputMethodState.mBatchEditNesting + 1;
            inputMethodState.mBatchEditNesting = i;
            if (i == 1) {
                inputMethodState.mCursorChanged = false;
                inputMethodState.mChangedDelta = 0;
                if (inputMethodState.mContentChanged) {
                    inputMethodState.mChangedStart = 0;
                    inputMethodState.mChangedEnd = this.mTextView.getText().length();
                } else {
                    inputMethodState.mChangedStart = -1;
                    inputMethodState.mChangedEnd = -1;
                    inputMethodState.mContentChanged = false;
                }
                this.mTextView.onBeginBatchEdit();
            }
        }
    }

    public void endBatchEdit() {
        this.mInBatchEditControllers = false;
        InputMethodState inputMethodState = this.mInputMethodState;
        if (inputMethodState != null) {
            int i = inputMethodState.mBatchEditNesting - 1;
            inputMethodState.mBatchEditNesting = i;
            if (i == 0) {
                finishBatchEdit(inputMethodState);
            }
        }
    }

    void ensureEndedBatchEdit() {
        InputMethodState inputMethodState = this.mInputMethodState;
        if (inputMethodState == null || inputMethodState.mBatchEditNesting == 0) {
            return;
        }
        inputMethodState.mBatchEditNesting = 0;
        finishBatchEdit(inputMethodState);
    }

    void finishBatchEdit(InputMethodState inputMethodState) {
        this.mTextView.onEndBatchEdit();
        if (inputMethodState.mContentChanged || inputMethodState.mSelectionModeChanged) {
            this.mTextView.updateAfterEdit();
            reportExtractedText();
        } else if (inputMethodState.mCursorChanged) {
            this.mTextView.invalidateCursor();
        }
        sendUpdateSelection();
    }

    boolean extractText(ExtractedTextRequest extractedTextRequest, ExtractedText extractedText) {
        return extractTextInternal(extractedTextRequest, -1, -1, -1, extractedText);
    }

    private boolean extractTextInternal(ExtractedTextRequest extractedTextRequest, int i, int i2, int i3, ExtractedText extractedText) {
        CharSequence text = this.mTextView.getText();
        if (text == null) {
            return false;
        }
        if (i != -2) {
            int length = text.length();
            if (i < 0) {
                extractedText.partialEndOffset = -1;
                extractedText.partialStartOffset = -1;
                i = 0;
            } else {
                int i4 = i2 + i3;
                if (text instanceof Spanned) {
                    Spanned spanned = (Spanned) text;
                    Object[] spans = spanned.getSpans(i, i4, ParcelableSpan.class);
                    int length2 = spans.length;
                    while (length2 > 0) {
                        length2--;
                        int spanStart = spanned.getSpanStart(spans[length2]);
                        if (spanStart < i) {
                            i = spanStart;
                        }
                        int spanEnd = spanned.getSpanEnd(spans[length2]);
                        if (spanEnd > i4) {
                            i4 = spanEnd;
                        }
                    }
                }
                extractedText.partialStartOffset = i;
                extractedText.partialEndOffset = i4 - i3;
                if (i > length) {
                    i = length;
                } else if (i < 0) {
                    i = 0;
                }
                if (i4 <= length) {
                    length = i4 < 0 ? 0 : i4;
                }
            }
            if ((extractedTextRequest.flags & 1) != 0) {
                extractedText.text = text.subSequence(i, length);
            } else {
                extractedText.text = TextUtils.substring(text, i, length);
            }
        } else {
            extractedText.partialStartOffset = 0;
            extractedText.partialEndOffset = 0;
            extractedText.text = "";
        }
        extractedText.flags = 0;
        if (MetaKeyKeyListener.getMetaState(text, 2048) != 0) {
            extractedText.flags |= 2;
        }
        if (this.mTextView.isSingleLine()) {
            extractedText.flags |= 1;
        }
        extractedText.startOffset = 0;
        extractedText.selectionStart = this.mTextView.getSelectionStart();
        extractedText.selectionEnd = this.mTextView.getSelectionEnd();
        return true;
    }

    boolean reportExtractedText() {
        boolean z;
        InputMethodManager inputMethodManagerPeekInstance;
        InputMethodState inputMethodState = this.mInputMethodState;
        if (inputMethodState != null && ((z = inputMethodState.mContentChanged) || inputMethodState.mSelectionModeChanged)) {
            inputMethodState.mContentChanged = false;
            inputMethodState.mSelectionModeChanged = false;
            ExtractedTextRequest extractedTextRequest = inputMethodState.mExtractedTextRequest;
            if (extractedTextRequest != null && (inputMethodManagerPeekInstance = InputMethodManager.peekInstance()) != null) {
                if (inputMethodState.mChangedStart < 0 && !z) {
                    inputMethodState.mChangedStart = -2;
                }
                if (extractTextInternal(extractedTextRequest, inputMethodState.mChangedStart, inputMethodState.mChangedEnd, inputMethodState.mChangedDelta, inputMethodState.mExtractedText)) {
                    inputMethodManagerPeekInstance.updateExtractedText(this.mTextView, extractedTextRequest.token, inputMethodState.mExtractedText);
                    inputMethodState.mChangedStart = -1;
                    inputMethodState.mChangedEnd = -1;
                    inputMethodState.mChangedDelta = 0;
                    inputMethodState.mContentChanged = false;
                    return true;
                }
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendUpdateSelection() {
        InputMethodManager inputMethodManagerPeekInstance;
        int i;
        int composingSpanEnd;
        InputMethodState inputMethodState = this.mInputMethodState;
        if (inputMethodState == null || inputMethodState.mBatchEditNesting > 0 || (inputMethodManagerPeekInstance = InputMethodManager.peekInstance()) == null) {
            return;
        }
        int selectionStart = this.mTextView.getSelectionStart();
        int selectionEnd = this.mTextView.getSelectionEnd();
        if (this.mTextView.getText() instanceof Spannable) {
            Spannable spannable = (Spannable) this.mTextView.getText();
            int composingSpanStart = EditableInputConnection.getComposingSpanStart(spannable);
            composingSpanEnd = EditableInputConnection.getComposingSpanEnd(spannable);
            i = composingSpanStart;
        } else {
            i = -1;
            composingSpanEnd = -1;
        }
        inputMethodManagerPeekInstance.updateSelection(this.mTextView, selectionStart, selectionEnd, i, composingSpanEnd);
    }

    void onDraw(Canvas canvas, Layout layout, Path path, Paint paint, int i) {
        InputMethodManager inputMethodManagerPeekInstance;
        Path path2 = path;
        int selectionStart = this.mTextView.getSelectionStart();
        int selectionEnd = this.mTextView.getSelectionEnd();
        InputMethodState inputMethodState = this.mInputMethodState;
        if (inputMethodState != null && inputMethodState.mBatchEditNesting == 0 && (inputMethodManagerPeekInstance = InputMethodManager.peekInstance()) != null) {
            if (inputMethodManagerPeekInstance.isActive(this.mTextView) && (inputMethodState.mContentChanged || inputMethodState.mSelectionModeChanged)) {
                reportExtractedText();
            }
            if (inputMethodManagerPeekInstance.isWatchingCursor(this.mTextView) && path2 != null) {
                path2.computeBounds(inputMethodState.mTmpRectF, true);
                float[] fArr = inputMethodState.mTmpOffset;
                inputMethodState.mTmpOffset[1] = 0.0f;
                fArr[0] = 0.0f;
                canvas.getMatrix().mapPoints(inputMethodState.mTmpOffset);
                inputMethodState.mTmpRectF.offset(inputMethodState.mTmpOffset[0], inputMethodState.mTmpOffset[1]);
                inputMethodState.mTmpRectF.offset(0.0f, i);
                inputMethodState.mCursorRectInWindow.set((int) (((double) inputMethodState.mTmpRectF.left) + 0.5d), (int) (((double) inputMethodState.mTmpRectF.top) + 0.5d), (int) (((double) inputMethodState.mTmpRectF.right) + 0.5d), (int) (((double) inputMethodState.mTmpRectF.bottom) + 0.5d));
                inputMethodManagerPeekInstance.updateCursor(this.mTextView, inputMethodState.mCursorRectInWindow.left, inputMethodState.mCursorRectInWindow.top, inputMethodState.mCursorRectInWindow.right, inputMethodState.mCursorRectInWindow.bottom);
            }
        }
        CorrectionHighlighter correctionHighlighter = this.mCorrectionHighlighter;
        if (correctionHighlighter != null) {
            correctionHighlighter.draw(canvas, i);
        }
        if (path2 != null && selectionStart == selectionEnd && this.mCursorCount > 0) {
            drawCursor(canvas, i);
            path2 = null;
        }
        Path path3 = path2;
        if (this.mTextView.canHaveDisplayList() && canvas.isHardwareAccelerated()) {
            drawHardwareAccelerated(canvas, layout, path3, paint, i);
        } else {
            layout.draw(canvas, path3, paint, i);
        }
    }

    private void drawHardwareAccelerated(Canvas canvas, Layout layout, Path path, Paint paint, int i) {
        int[] iArr;
        int[] iArr2;
        int i2;
        int i3;
        int i4;
        Editor editor = this;
        long lineRangeForDraw = layout.getLineRangeForDraw(canvas);
        int iUnpackRangeStartFromLong = TextUtils.unpackRangeStartFromLong(lineRangeForDraw);
        int iUnpackRangeEndFromLong = TextUtils.unpackRangeEndFromLong(lineRangeForDraw);
        if (iUnpackRangeEndFromLong < 0) {
            return;
        }
        layout.drawBackground(canvas, path, paint, i, iUnpackRangeStartFromLong, iUnpackRangeEndFromLong);
        if (layout instanceof DynamicLayout) {
            int i5 = 0;
            if (editor.mTextDisplayLists == null) {
                editor.mTextDisplayLists = new DisplayList[ArrayUtils.idealObjectArraySize(0)];
            }
            DynamicLayout dynamicLayout = (DynamicLayout) layout;
            int[] blockEndLines = dynamicLayout.getBlockEndLines();
            int[] blockIndices = dynamicLayout.getBlockIndices();
            int numberOfBlocks = dynamicLayout.getNumberOfBlocks();
            int indexFirstChangedBlock = dynamicLayout.getIndexFirstChangedBlock();
            int i6 = -1;
            int i7 = 0;
            int i8 = 0;
            int i9 = -1;
            while (i7 < numberOfBlocks) {
                int i10 = blockEndLines[i7];
                int availableDisplayListIndex = blockIndices[i7];
                int i11 = availableDisplayListIndex == i6 ? 1 : i5;
                if (i11 != 0) {
                    availableDisplayListIndex = editor.getAvailableDisplayListIndex(blockIndices, numberOfBlocks, i8);
                    blockIndices[i7] = availableDisplayListIndex;
                    i8 = availableDisplayListIndex + 1;
                }
                DisplayList[] displayListArr = editor.mTextDisplayLists;
                DisplayList displayListCreateDisplayList = displayListArr[availableDisplayListIndex];
                if (displayListCreateDisplayList == null) {
                    iArr = blockEndLines;
                    displayListCreateDisplayList = editor.mTextView.getHardwareRenderer().createDisplayList("Text " + availableDisplayListIndex);
                    displayListArr[availableDisplayListIndex] = displayListCreateDisplayList;
                } else {
                    iArr = blockEndLines;
                    if (i11 != 0) {
                        displayListCreateDisplayList.clear();
                    }
                }
                DisplayList displayList = displayListCreateDisplayList;
                boolean z = !displayList.isValid();
                if (i7 >= indexFirstChangedBlock || z) {
                    int i12 = i9 + 1;
                    int lineTop = layout.getLineTop(i12);
                    int lineBottom = layout.getLineBottom(i10);
                    int width = editor.mTextView.getWidth();
                    iArr2 = blockIndices;
                    if (editor.mTextView.getHorizontallyScrolling()) {
                        float fMin = Float.MAX_VALUE;
                        float fMax = Float.MIN_VALUE;
                        int i13 = i12;
                        while (i13 <= i10) {
                            fMin = Math.min(fMin, layout.getLineLeft(i13));
                            fMax = Math.max(fMax, layout.getLineRight(i13));
                            i13++;
                            indexFirstChangedBlock = indexFirstChangedBlock;
                        }
                        i2 = indexFirstChangedBlock;
                        i3 = (int) fMin;
                        width = (int) (fMax + 0.5f);
                    } else {
                        i2 = indexFirstChangedBlock;
                        i3 = 0;
                    }
                    if (z) {
                        HardwareCanvas hardwareCanvasStart = displayList.start(width - i3, lineBottom - lineTop);
                        try {
                            hardwareCanvasStart.translate(-i3, -lineTop);
                            layout.drawText(hardwareCanvasStart, i12, i10);
                            displayList.end();
                            i4 = 0;
                            displayList.setClipToBounds(false);
                        } catch (Throwable th) {
                            displayList.end();
                            displayList.setClipToBounds(false);
                            throw th;
                        }
                    } else {
                        i4 = 0;
                    }
                    displayList.setLeftTopRightBottom(i3, lineTop, width, lineBottom);
                } else {
                    iArr2 = blockIndices;
                    i2 = indexFirstChangedBlock;
                    i4 = 0;
                }
                ((HardwareCanvas) canvas).drawDisplayList(displayList, null, i4);
                i7++;
                editor = this;
                blockIndices = iArr2;
                i5 = i4;
                i9 = i10;
                indexFirstChangedBlock = i2;
                blockEndLines = iArr;
                i6 = -1;
            }
            dynamicLayout.setIndexFirstChangedBlock(numberOfBlocks);
            return;
        }
        layout.drawText(canvas, iUnpackRangeStartFromLong, iUnpackRangeEndFromLong);
    }

    private int getAvailableDisplayListIndex(int[] iArr, int i, int i2) {
        int length = this.mTextDisplayLists.length;
        while (true) {
            boolean z = false;
            if (i2 >= length) {
                DisplayList[] displayListArr = new DisplayList[ArrayUtils.idealIntArraySize(length + 1)];
                System.arraycopy(this.mTextDisplayLists, 0, displayListArr, 0, length);
                this.mTextDisplayLists = displayListArr;
                return length;
            }
            int i3 = 0;
            while (true) {
                if (i3 >= i) {
                    break;
                }
                if (iArr[i3] == i2) {
                    z = true;
                    break;
                }
                i3++;
            }
            if (!z) {
                return i2;
            }
            i2++;
        }
    }

    private void drawCursor(Canvas canvas, int i) {
        boolean z = i != 0;
        if (z) {
            canvas.translate(0.0f, i);
        }
        for (int i2 = 0; i2 < this.mCursorCount; i2++) {
            this.mCursorDrawable[i2].draw(canvas);
        }
        if (z) {
            canvas.translate(0.0f, -i);
        }
    }

    void invalidateTextDisplayList(Layout layout, int i, int i2) {
        if (this.mTextDisplayLists == null || !(layout instanceof DynamicLayout)) {
            return;
        }
        int lineForOffset = layout.getLineForOffset(i);
        int lineForOffset2 = layout.getLineForOffset(i2);
        DynamicLayout dynamicLayout = (DynamicLayout) layout;
        int[] blockEndLines = dynamicLayout.getBlockEndLines();
        int[] blockIndices = dynamicLayout.getBlockIndices();
        int numberOfBlocks = dynamicLayout.getNumberOfBlocks();
        int i3 = 0;
        while (i3 < numberOfBlocks && blockEndLines[i3] < lineForOffset) {
            i3++;
        }
        while (i3 < numberOfBlocks) {
            int i4 = blockIndices[i3];
            if (i4 != -1) {
                this.mTextDisplayLists[i4].clear();
            }
            if (blockEndLines[i3] >= lineForOffset2) {
                return;
            } else {
                i3++;
            }
        }
    }

    void invalidateTextDisplayList() {
        if (this.mTextDisplayLists == null) {
            return;
        }
        int i = 0;
        while (true) {
            DisplayList[] displayListArr = this.mTextDisplayLists;
            if (i >= displayListArr.length) {
                return;
            }
            if (displayListArr[i] != null) {
                displayListArr[i].clear();
            }
            i++;
        }
    }

    void updateCursorsPositions() {
        if (this.mTextView.mCursorDrawableRes == 0) {
            this.mCursorCount = 0;
            return;
        }
        Layout layout = this.mTextView.getLayout();
        Layout hintLayout = this.mTextView.getHintLayout();
        int selectionStart = this.mTextView.getSelectionStart();
        int lineForOffset = layout.getLineForOffset(selectionStart);
        int lineTop = layout.getLineTop(lineForOffset);
        int lineTop2 = layout.getLineTop(lineForOffset + 1);
        int i = layout.isLevelBoundary(selectionStart) ? 2 : 1;
        this.mCursorCount = i;
        int i2 = i == 2 ? (lineTop + lineTop2) >> 1 : lineTop2;
        boolean zShouldClampCursor = layout.shouldClampCursor(lineForOffset);
        updateCursorPosition(0, lineTop, i2, getPrimaryHorizontal(layout, hintLayout, selectionStart, zShouldClampCursor));
        if (this.mCursorCount == 2) {
            updateCursorPosition(1, i2, lineTop2, layout.getSecondaryHorizontal(selectionStart, zShouldClampCursor));
        }
    }

    private float getPrimaryHorizontal(Layout layout, Layout layout2, int i, boolean z) {
        if (TextUtils.isEmpty(layout.getText()) && layout2 != null && !TextUtils.isEmpty(layout2.getText())) {
            return layout2.getPrimaryHorizontal(i, z);
        }
        return layout.getPrimaryHorizontal(i, z);
    }

    boolean startSelectionActionMode() {
        InputMethodManager inputMethodManagerPeekInstance;
        if (this.mSelectionActionMode != null) {
            return false;
        }
        if (!canSelectText() || !this.mTextView.requestFocus()) {
            Log.w("TextView", "TextView does not support text selection. Action mode cancelled.");
            return false;
        }
        if (!this.mTextView.hasSelection() && !selectCurrentWord()) {
            return false;
        }
        boolean zExtractedTextModeWillBeStarted = extractedTextModeWillBeStarted();
        if (!zExtractedTextModeWillBeStarted) {
            this.mSelectionActionMode = this.mTextView.startActionMode(new SelectionActionModeCallback());
        }
        boolean z = this.mSelectionActionMode != null || zExtractedTextModeWillBeStarted;
        if (z && !this.mTextView.isTextSelectable() && this.mShowSoftInputOnFocus && (inputMethodManagerPeekInstance = InputMethodManager.peekInstance()) != null) {
            inputMethodManagerPeekInstance.showSoftInput(this.mTextView, 0, null);
        }
        return z;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean extractedTextModeWillBeStarted() {
        InputMethodManager inputMethodManagerPeekInstance;
        return ((this.mTextView instanceof ExtractEditText) || (inputMethodManagerPeekInstance = InputMethodManager.peekInstance()) == null || !inputMethodManagerPeekInstance.isFullscreenMode()) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isCursorInsideSuggestionSpan() {
        CharSequence text = this.mTextView.getText();
        return (text instanceof Spannable) && ((SuggestionSpan[]) ((Spannable) text).getSpans(this.mTextView.getSelectionStart(), this.mTextView.getSelectionEnd(), SuggestionSpan.class)).length > 0;
    }

    private boolean isCursorInsideEasyCorrectionSpan() {
        for (SuggestionSpan suggestionSpan : (SuggestionSpan[]) ((Spannable) this.mTextView.getText()).getSpans(this.mTextView.getSelectionStart(), this.mTextView.getSelectionEnd(), SuggestionSpan.class)) {
            if ((suggestionSpan.getFlags() & 1) != 0) {
                return true;
            }
        }
        return false;
    }

    void onTouchUpEvent(MotionEvent motionEvent) {
        boolean z = this.mSelectAllOnFocus && this.mTextView.didTouchFocusSelect();
        hideControllers();
        CharSequence text = this.mTextView.getText();
        if (z || text.length() <= 0) {
            return;
        }
        Selection.setSelection((Spannable) text, this.mTextView.getOffsetForPosition(motionEvent.getX(), motionEvent.getY()));
        SpellChecker spellChecker = this.mSpellChecker;
        if (spellChecker != null) {
            spellChecker.onSelectionChanged();
        }
        if (extractedTextModeWillBeStarted()) {
            return;
        }
        if (isCursorInsideEasyCorrectionSpan()) {
            Runnable runnable = new Runnable() { // from class: android.widget.Editor.1
                @Override // java.lang.Runnable
                public void run() {
                    Editor.this.showSuggestions();
                }
            };
            this.mShowSuggestionRunnable = runnable;
            this.mTextView.postDelayed(runnable, ViewConfiguration.getDoubleTapTimeout());
        } else if (hasInsertionController()) {
            getInsertionController().show();
        }
    }

    protected void stopSelectionActionMode() {
        ActionMode actionMode = this.mSelectionActionMode;
        if (actionMode != null) {
            actionMode.finish();
        }
    }

    boolean hasInsertionController() {
        return this.mInsertionControllerEnabled;
    }

    boolean hasSelectionController() {
        return this.mSelectionControllerEnabled;
    }

    InsertionPointCursorController getInsertionController() {
        if (!this.mInsertionControllerEnabled) {
            return null;
        }
        if (this.mInsertionPointCursorController == null) {
            this.mInsertionPointCursorController = new InsertionPointCursorController();
            this.mTextView.getViewTreeObserver().addOnTouchModeChangeListener(this.mInsertionPointCursorController);
        }
        return this.mInsertionPointCursorController;
    }

    SelectionModifierCursorController getSelectionController() {
        if (!this.mSelectionControllerEnabled) {
            return null;
        }
        if (this.mSelectionModifierCursorController == null) {
            this.mSelectionModifierCursorController = new SelectionModifierCursorController();
            this.mTextView.getViewTreeObserver().addOnTouchModeChangeListener(this.mSelectionModifierCursorController);
        }
        return this.mSelectionModifierCursorController;
    }

    private void updateCursorPosition(int i, int i2, int i3, float f) {
        Drawable[] drawableArr = this.mCursorDrawable;
        if (drawableArr[i] == null) {
            drawableArr[i] = this.mTextView.getResources().getDrawable(this.mTextView.mCursorDrawableRes);
        }
        if (this.mTempRect == null) {
            this.mTempRect = new Rect();
        }
        this.mCursorDrawable[i].getPadding(this.mTempRect);
        int intrinsicWidth = this.mCursorDrawable[i].getIntrinsicWidth();
        int iMax = ((int) Math.max(0.5f, f - 0.5f)) - this.mTempRect.left;
        this.mCursorDrawable[i].setBounds(iMax, i2 - this.mTempRect.top, intrinsicWidth + iMax, i3 + this.mTempRect.bottom);
    }

    public void onCommitCorrection(CorrectionInfo correctionInfo) {
        CorrectionHighlighter correctionHighlighter = this.mCorrectionHighlighter;
        if (correctionHighlighter == null) {
            this.mCorrectionHighlighter = new CorrectionHighlighter();
        } else {
            correctionHighlighter.invalidate(false);
        }
        this.mCorrectionHighlighter.highlight(correctionInfo);
    }

    void showSuggestions() {
        if (this.mSuggestionsPopupWindow == null) {
            this.mSuggestionsPopupWindow = new SuggestionsPopupWindow();
        }
        hideControllers();
        this.mSuggestionsPopupWindow.show();
    }

    boolean areSuggestionsShown() {
        SuggestionsPopupWindow suggestionsPopupWindow = this.mSuggestionsPopupWindow;
        return suggestionsPopupWindow != null && suggestionsPopupWindow.isShowing();
    }

    void onScrollChanged() {
        PositionListener positionListener = this.mPositionListener;
        if (positionListener != null) {
            positionListener.onScrollChanged();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean shouldBlink() {
        int selectionStart;
        int selectionEnd;
        return isCursorVisible() && this.mTextView.isFocused() && (selectionStart = this.mTextView.getSelectionStart()) >= 0 && (selectionEnd = this.mTextView.getSelectionEnd()) >= 0 && selectionStart == selectionEnd;
    }

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
    void makeBlink() {
        if (shouldBlink()) {
            this.mShowCursor = SystemClock.uptimeMillis();
            if (this.mBlink == null) {
                this.mBlink = new Blink();
            }
            Blink blink = this.mBlink;
            blink.removeCallbacks(blink);
            Blink blink2 = this.mBlink;
            blink2.postAtTime(blink2, this.mShowCursor + 500);
            return;
        }
        Blink blink3 = this.mBlink;
        if (blink3 != null) {
            blink3.removeCallbacks(blink3);
        }
    }

    private class Blink extends Handler implements Runnable {
        private boolean mCancelled;

        private Blink() {
        }

        @Override // java.lang.Runnable
        public void run() {
            if (this.mCancelled) {
                return;
            }
            removeCallbacks(this);
            if (Editor.this.shouldBlink()) {
                if (Editor.this.mTextView.getLayout() != null) {
                    Editor.this.mTextView.invalidateCursorPath();
                }
                postAtTime(this, SystemClock.uptimeMillis() + 500);
            }
        }

        void cancel() {
            if (this.mCancelled) {
                return;
            }
            removeCallbacks(this);
            this.mCancelled = true;
        }

        void uncancel() {
            this.mCancelled = false;
        }
    }

    private View.DragShadowBuilder getTextThumbnailBuilder(CharSequence charSequence) {
        TextView textView = (TextView) View.inflate(this.mTextView.getContext(), 17367209, null);
        if (textView == null) {
            throw new IllegalArgumentException("Unable to inflate text drag thumbnail");
        }
        int length = charSequence.length();
        int i = DRAG_SHADOW_MAX_TEXT_LENGTH;
        if (length > i) {
            charSequence = charSequence.subSequence(0, i);
        }
        textView.setText(charSequence);
        textView.setTextColor(this.mTextView.getTextColors());
        textView.setTextAppearance(this.mTextView.getContext(), 16);
        textView.setGravity(17);
        textView.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
        int iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
        textView.measure(iMakeMeasureSpec, iMakeMeasureSpec);
        textView.layout(0, 0, textView.getMeasuredWidth(), textView.getMeasuredHeight());
        textView.invalidate();
        return new View.DragShadowBuilder(textView);
    }

    private static class DragLocalState {
        public int end;
        public TextView sourceTextView;
        public int start;

        public DragLocalState(TextView textView, int i, int i2) {
            this.sourceTextView = textView;
            this.start = i;
            this.end = i2;
        }
    }

    void onDrop(DragEvent dragEvent) {
        StringBuilder sb = new StringBuilder("");
        ClipData clipData = dragEvent.getClipData();
        int itemCount = clipData.getItemCount();
        for (int i = 0; i < itemCount; i++) {
            sb.append(clipData.getItemAt(i).coerceToStyledText(this.mTextView.getContext()));
        }
        int offsetForPosition = this.mTextView.getOffsetForPosition(dragEvent.getX(), dragEvent.getY());
        Object localState = dragEvent.getLocalState();
        DragLocalState dragLocalState = localState instanceof DragLocalState ? (DragLocalState) localState : null;
        boolean z = dragLocalState != null && dragLocalState.sourceTextView == this.mTextView;
        if (!z || offsetForPosition < dragLocalState.start || offsetForPosition >= dragLocalState.end) {
            int length = this.mTextView.getText().length();
            long jPrepareSpacesAroundPaste = this.mTextView.prepareSpacesAroundPaste(offsetForPosition, offsetForPosition, sb);
            int iUnpackRangeStartFromLong = TextUtils.unpackRangeStartFromLong(jPrepareSpacesAroundPaste);
            int iUnpackRangeEndFromLong = TextUtils.unpackRangeEndFromLong(jPrepareSpacesAroundPaste);
            Selection.setSelection((Spannable) this.mTextView.getText(), iUnpackRangeEndFromLong);
            this.mTextView.replaceText_internal(iUnpackRangeStartFromLong, iUnpackRangeEndFromLong, sb);
            if (z) {
                int i2 = dragLocalState.start;
                int i3 = dragLocalState.end;
                if (iUnpackRangeEndFromLong <= i2) {
                    int length2 = this.mTextView.getText().length() - length;
                    i2 += length2;
                    i3 += length2;
                }
                this.mTextView.deleteText_internal(i2, i3);
                int iMax = Math.max(0, i2 - 1);
                int iMin = Math.min(this.mTextView.getText().length(), i2 + 1);
                int i4 = iMax + 1;
                if (iMin > i4) {
                    CharSequence transformedText = this.mTextView.getTransformedText(iMax, iMin);
                    if (Character.isSpaceChar(transformedText.charAt(0)) && Character.isSpaceChar(transformedText.charAt(1))) {
                        this.mTextView.deleteText_internal(iMax, i4);
                    }
                }
            }
        }
    }

    public void addSpanWatchers(Spannable spannable) {
        int length = spannable.length();
        KeyListener keyListener = this.mKeyListener;
        if (keyListener != null) {
            spannable.setSpan(keyListener, 0, length, 18);
        }
        if (this.mSpanController == null) {
            this.mSpanController = new SpanController();
        }
        spannable.setSpan(this.mSpanController, 0, length, 18);
    }

    class SpanController implements SpanWatcher {
        private static final int DISPLAY_TIMEOUT_MS = 3000;
        private Runnable mHidePopup;
        private EasyEditPopupWindow mPopupWindow;

        SpanController() {
        }

        private boolean isNonIntermediateSelectionSpan(Spannable spannable, Object obj) {
            return (Selection.SELECTION_START == obj || Selection.SELECTION_END == obj) && (spannable.getSpanFlags(obj) & 512) == 0;
        }

        @Override // android.text.SpanWatcher
        public void onSpanAdded(Spannable spannable, Object obj, int i, int i2) {
            if (isNonIntermediateSelectionSpan(spannable, obj)) {
                Editor.this.sendUpdateSelection();
                return;
            }
            if (obj instanceof EasyEditSpan) {
                if (this.mPopupWindow == null) {
                    this.mPopupWindow = new EasyEditPopupWindow();
                    this.mHidePopup = new Runnable() { // from class: android.widget.Editor.SpanController.1
                        @Override // java.lang.Runnable
                        public void run() {
                            SpanController.this.hide();
                        }
                    };
                }
                if (this.mPopupWindow.mEasyEditSpan != null) {
                    this.mPopupWindow.mEasyEditSpan.setDeleteEnabled(false);
                }
                this.mPopupWindow.setEasyEditSpan((EasyEditSpan) obj);
                this.mPopupWindow.setOnDeleteListener(new EasyEditDeleteListener() { // from class: android.widget.Editor.SpanController.2
                    @Override // android.widget.Editor.EasyEditDeleteListener
                    public void onDeleteClick(EasyEditSpan easyEditSpan) {
                        Editable editable = (Editable) Editor.this.mTextView.getText();
                        int spanStart = editable.getSpanStart(easyEditSpan);
                        int spanEnd = editable.getSpanEnd(easyEditSpan);
                        if (spanStart >= 0 && spanEnd >= 0) {
                            SpanController.this.sendEasySpanNotification(1, easyEditSpan);
                            Editor.this.mTextView.deleteText_internal(spanStart, spanEnd);
                        }
                        editable.removeSpan(easyEditSpan);
                    }
                });
                if (Editor.this.mTextView.getWindowVisibility() != 0 || Editor.this.mTextView.getLayout() == null || Editor.this.extractedTextModeWillBeStarted()) {
                    return;
                }
                this.mPopupWindow.show();
                Editor.this.mTextView.removeCallbacks(this.mHidePopup);
                Editor.this.mTextView.postDelayed(this.mHidePopup, 3000L);
            }
        }

        @Override // android.text.SpanWatcher
        public void onSpanRemoved(Spannable spannable, Object obj, int i, int i2) {
            if (isNonIntermediateSelectionSpan(spannable, obj)) {
                Editor.this.sendUpdateSelection();
                return;
            }
            EasyEditPopupWindow easyEditPopupWindow = this.mPopupWindow;
            if (easyEditPopupWindow == null || obj != easyEditPopupWindow.mEasyEditSpan) {
                return;
            }
            hide();
        }

        @Override // android.text.SpanWatcher
        public void onSpanChanged(Spannable spannable, Object obj, int i, int i2, int i3, int i4) {
            if (isNonIntermediateSelectionSpan(spannable, obj)) {
                Editor.this.sendUpdateSelection();
            } else {
                if (this.mPopupWindow == null || !(obj instanceof EasyEditSpan)) {
                    return;
                }
                EasyEditSpan easyEditSpan = (EasyEditSpan) obj;
                sendEasySpanNotification(2, easyEditSpan);
                spannable.removeSpan(easyEditSpan);
            }
        }

        public void hide() {
            EasyEditPopupWindow easyEditPopupWindow = this.mPopupWindow;
            if (easyEditPopupWindow != null) {
                easyEditPopupWindow.hide();
                Editor.this.mTextView.removeCallbacks(this.mHidePopup);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void sendEasySpanNotification(int i, EasyEditSpan easyEditSpan) {
            try {
                PendingIntent pendingIntent = easyEditSpan.getPendingIntent();
                if (pendingIntent != null) {
                    Intent intent = new Intent();
                    intent.putExtra(EasyEditSpan.EXTRA_TEXT_CHANGED_TYPE, i);
                    pendingIntent.send(Editor.this.mTextView.getContext(), 0, intent);
                }
            } catch (PendingIntent.CanceledException e) {
                Log.w(Editor.TAG, "PendingIntent for notification cannot be sent", e);
            }
        }
    }

    private class EasyEditPopupWindow extends PinnedPopupWindow implements View.OnClickListener {
        private static final int POPUP_TEXT_LAYOUT = 17367210;
        private TextView mDeleteTextView;
        private EasyEditSpan mEasyEditSpan;
        private EasyEditDeleteListener mOnDeleteListener;

        @Override // android.widget.Editor.PinnedPopupWindow
        protected int clipVertically(int i) {
            return i;
        }

        private EasyEditPopupWindow() {
            super();
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        protected void createPopupWindow() {
            this.mPopupWindow = new PopupWindow(Editor.this.mTextView.getContext(), (AttributeSet) null, R.attr.textSelectHandleWindowStyle);
            this.mPopupWindow.setInputMethodMode(2);
            this.mPopupWindow.setClippingEnabled(true);
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        protected void initContentView() {
            LinearLayout linearLayout = new LinearLayout(Editor.this.mTextView.getContext());
            linearLayout.setOrientation(0);
            this.mContentView = linearLayout;
            this.mContentView.setBackgroundResource(17303092);
            LayoutInflater layoutInflater = (LayoutInflater) Editor.this.mTextView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(-2, -2);
            TextView textView = (TextView) layoutInflater.inflate(POPUP_TEXT_LAYOUT, (ViewGroup) null);
            this.mDeleteTextView = textView;
            textView.setLayoutParams(layoutParams);
            this.mDeleteTextView.setText(17040362);
            this.mDeleteTextView.setOnClickListener(this);
            this.mContentView.addView(this.mDeleteTextView);
        }

        public void setEasyEditSpan(EasyEditSpan easyEditSpan) {
            this.mEasyEditSpan = easyEditSpan;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setOnDeleteListener(EasyEditDeleteListener easyEditDeleteListener) {
            this.mOnDeleteListener = easyEditDeleteListener;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            EasyEditSpan easyEditSpan;
            EasyEditDeleteListener easyEditDeleteListener;
            if (view != this.mDeleteTextView || (easyEditSpan = this.mEasyEditSpan) == null || !easyEditSpan.isDeleteEnabled() || (easyEditDeleteListener = this.mOnDeleteListener) == null) {
                return;
            }
            easyEditDeleteListener.onDeleteClick(this.mEasyEditSpan);
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        public void hide() {
            EasyEditSpan easyEditSpan = this.mEasyEditSpan;
            if (easyEditSpan != null) {
                easyEditSpan.setDeleteEnabled(false);
            }
            this.mOnDeleteListener = null;
            super.hide();
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        protected int getTextOffset() {
            return ((Editable) Editor.this.mTextView.getText()).getSpanEnd(this.mEasyEditSpan);
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        protected int getVerticalLocalPosition(int i) {
            return Editor.this.mTextView.getLayout().getLineBottom(i);
        }
    }

    private class PositionListener implements ViewTreeObserver.OnPreDrawListener {
        private final int MAXIMUM_NUMBER_OF_LISTENERS;
        private boolean[] mCanMove;
        private int mNumberOfListeners;
        private boolean mPositionHasChanged;
        private TextViewPositionListener[] mPositionListeners;
        private int mPositionX;
        private int mPositionY;
        private boolean mScrollHasChanged;
        final int[] mTempCoords;

        private PositionListener() {
            this.MAXIMUM_NUMBER_OF_LISTENERS = 6;
            this.mPositionListeners = new TextViewPositionListener[6];
            this.mCanMove = new boolean[6];
            this.mPositionHasChanged = true;
            this.mTempCoords = new int[2];
        }

        public void addSubscriber(TextViewPositionListener textViewPositionListener, boolean z) {
            if (this.mNumberOfListeners == 0) {
                updatePosition();
                Editor.this.mTextView.getViewTreeObserver().addOnPreDrawListener(this);
            }
            int i = -1;
            for (int i2 = 0; i2 < 6; i2++) {
                TextViewPositionListener textViewPositionListener2 = this.mPositionListeners[i2];
                if (textViewPositionListener2 == textViewPositionListener) {
                    return;
                }
                if (i < 0 && textViewPositionListener2 == null) {
                    i = i2;
                }
            }
            this.mPositionListeners[i] = textViewPositionListener;
            this.mCanMove[i] = z;
            this.mNumberOfListeners++;
        }

        public void removeSubscriber(TextViewPositionListener textViewPositionListener) {
            int i = 0;
            while (true) {
                if (i >= 6) {
                    break;
                }
                TextViewPositionListener[] textViewPositionListenerArr = this.mPositionListeners;
                if (textViewPositionListenerArr[i] == textViewPositionListener) {
                    textViewPositionListenerArr[i] = null;
                    this.mNumberOfListeners--;
                    break;
                }
                i++;
            }
            if (this.mNumberOfListeners == 0) {
                Editor.this.mTextView.getViewTreeObserver().removeOnPreDrawListener(this);
            }
        }

        public int getPositionX() {
            return this.mPositionX;
        }

        public int getPositionY() {
            return this.mPositionY;
        }

        @Override // android.view.ViewTreeObserver.OnPreDrawListener
        public boolean onPreDraw() {
            TextViewPositionListener textViewPositionListener;
            updatePosition();
            for (int i = 0; i < 6; i++) {
                boolean z = this.mPositionHasChanged;
                if ((z || this.mScrollHasChanged || this.mCanMove[i]) && (textViewPositionListener = this.mPositionListeners[i]) != null) {
                    textViewPositionListener.updatePosition(this.mPositionX, this.mPositionY, z, this.mScrollHasChanged);
                }
            }
            this.mScrollHasChanged = false;
            return true;
        }

        private void updatePosition() {
            Editor.this.mTextView.getLocationInWindow(this.mTempCoords);
            int[] iArr = this.mTempCoords;
            this.mPositionHasChanged = (iArr[0] == this.mPositionX && iArr[1] == this.mPositionY) ? false : true;
            this.mPositionX = iArr[0];
            this.mPositionY = iArr[1];
        }

        public void onScrollChanged() {
            this.mScrollHasChanged = true;
        }
    }

    private abstract class PinnedPopupWindow implements TextViewPositionListener {
        protected ViewGroup mContentView;
        protected PopupWindow mPopupWindow;
        int mPositionX;
        int mPositionY;

        protected abstract int clipVertically(int i);

        protected abstract void createPopupWindow();

        protected abstract int getTextOffset();

        protected abstract int getVerticalLocalPosition(int i);

        protected abstract void initContentView();

        public PinnedPopupWindow() {
            createPopupWindow();
            this.mPopupWindow.setWindowLayoutType(1002);
            this.mPopupWindow.setWidth(-2);
            this.mPopupWindow.setHeight(-2);
            initContentView();
            this.mContentView.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
            this.mPopupWindow.setContentView(this.mContentView);
        }

        public void show() {
            Editor.this.getPositionListener().addSubscriber(this, false);
            computeLocalPosition();
            PositionListener positionListener = Editor.this.getPositionListener();
            updatePosition(positionListener.getPositionX(), positionListener.getPositionY());
        }

        protected void measureContent() {
            DisplayMetrics displayMetrics = Editor.this.mTextView.getResources().getDisplayMetrics();
            this.mContentView.measure(View.MeasureSpec.makeMeasureSpec(displayMetrics.widthPixels, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(displayMetrics.heightPixels, Integer.MIN_VALUE));
        }

        private void computeLocalPosition() {
            measureContent();
            int measuredWidth = this.mContentView.getMeasuredWidth();
            int textOffset = getTextOffset();
            int primaryHorizontal = (int) (Editor.this.mTextView.getLayout().getPrimaryHorizontal(textOffset) - (measuredWidth / 2.0f));
            this.mPositionX = primaryHorizontal;
            this.mPositionX = primaryHorizontal + Editor.this.mTextView.viewportToContentHorizontalOffset();
            int verticalLocalPosition = getVerticalLocalPosition(Editor.this.mTextView.getLayout().getLineForOffset(textOffset));
            this.mPositionY = verticalLocalPosition;
            this.mPositionY = verticalLocalPosition + Editor.this.mTextView.viewportToContentVerticalOffset();
        }

        private void updatePosition(int i, int i2) {
            int i3 = i + this.mPositionX;
            int iClipVertically = clipVertically(i2 + this.mPositionY);
            DisplayMetrics displayMetrics = Editor.this.mTextView.getResources().getDisplayMetrics();
            int iMax = Math.max(0, Math.min(displayMetrics.widthPixels - this.mContentView.getMeasuredWidth(), i3));
            if (!isShowing()) {
                this.mPopupWindow.showAtLocation(Editor.this.mTextView, 0, iMax, iClipVertically);
            } else {
                this.mPopupWindow.update(iMax, iClipVertically, -1, -1);
            }
        }

        public void hide() {
            this.mPopupWindow.dismiss();
            Editor.this.getPositionListener().removeSubscriber(this);
        }

        @Override // android.widget.Editor.TextViewPositionListener
        public void updatePosition(int i, int i2, boolean z, boolean z2) {
            if (isShowing() && Editor.this.isOffsetVisible(getTextOffset())) {
                if (z2) {
                    computeLocalPosition();
                }
                updatePosition(i, i2);
                return;
            }
            hide();
        }

        public boolean isShowing() {
            return this.mPopupWindow.isShowing();
        }
    }

    private class SuggestionsPopupWindow extends PinnedPopupWindow implements AdapterView.OnItemClickListener {
        private static final int ADD_TO_DICTIONARY = -1;
        private static final int DELETE_TEXT = -2;
        private static final int MAX_NUMBER_SUGGESTIONS = 5;
        private boolean mCursorWasVisibleBeforeSuggestions;
        private boolean mIsShowingUp;
        private int mNumberOfSuggestions;
        private final HashMap<SuggestionSpan, Integer> mSpansLengths;
        private SuggestionInfo[] mSuggestionInfos;
        private final Comparator<SuggestionSpan> mSuggestionSpanComparator;
        private SuggestionAdapter mSuggestionsAdapter;

        private class CustomPopupWindow extends PopupWindow {
            public CustomPopupWindow(Context context, int i) {
                super(context, (AttributeSet) null, i);
            }

            @Override // android.widget.PopupWindow
            public void dismiss() {
                super.dismiss();
                Editor.this.getPositionListener().removeSubscriber(SuggestionsPopupWindow.this);
                ((Spannable) Editor.this.mTextView.getText()).removeSpan(Editor.this.mSuggestionRangeSpan);
                Editor.this.mTextView.setCursorVisible(SuggestionsPopupWindow.this.mCursorWasVisibleBeforeSuggestions);
                if (Editor.this.hasInsertionController()) {
                    Editor.this.getInsertionController().show();
                }
            }
        }

        public SuggestionsPopupWindow() {
            super();
            this.mIsShowingUp = false;
            this.mCursorWasVisibleBeforeSuggestions = Editor.this.mCursorVisible;
            this.mSuggestionSpanComparator = new SuggestionSpanComparator();
            this.mSpansLengths = new HashMap<>();
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        protected void createPopupWindow() {
            this.mPopupWindow = new CustomPopupWindow(Editor.this.mTextView.getContext(), R.attr.textSuggestionsWindowStyle);
            this.mPopupWindow.setInputMethodMode(2);
            this.mPopupWindow.setFocusable(true);
            this.mPopupWindow.setClippingEnabled(false);
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        protected void initContentView() {
            ListView listView = new ListView(Editor.this.mTextView.getContext());
            SuggestionAdapter suggestionAdapter = new SuggestionAdapter();
            this.mSuggestionsAdapter = suggestionAdapter;
            listView.setAdapter((ListAdapter) suggestionAdapter);
            listView.setOnItemClickListener(this);
            this.mContentView = listView;
            this.mSuggestionInfos = new SuggestionInfo[7];
            int i = 0;
            while (true) {
                SuggestionInfo[] suggestionInfoArr = this.mSuggestionInfos;
                if (i >= suggestionInfoArr.length) {
                    return;
                }
                suggestionInfoArr[i] = new SuggestionInfo();
                i++;
            }
        }

        public boolean isShowingUp() {
            return this.mIsShowingUp;
        }

        public void onParentLostFocus() {
            this.mIsShowingUp = false;
        }

        private class SuggestionInfo {
            TextAppearanceSpan highlightSpan;
            int suggestionEnd;
            int suggestionIndex;
            SuggestionSpan suggestionSpan;
            int suggestionStart;
            SpannableStringBuilder text;

            private SuggestionInfo() {
                this.text = new SpannableStringBuilder();
                this.highlightSpan = new TextAppearanceSpan(Editor.this.mTextView.getContext(), R.style.TextAppearance_SuggestionHighlight);
            }
        }

        private class SuggestionAdapter extends BaseAdapter {
            private LayoutInflater mInflater;

            @Override // android.widget.Adapter
            public long getItemId(int i) {
                return i;
            }

            private SuggestionAdapter() {
                this.mInflater = (LayoutInflater) Editor.this.mTextView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }

            @Override // android.widget.Adapter
            public int getCount() {
                return SuggestionsPopupWindow.this.mNumberOfSuggestions;
            }

            @Override // android.widget.Adapter
            public Object getItem(int i) {
                return SuggestionsPopupWindow.this.mSuggestionInfos[i];
            }

            @Override // android.widget.Adapter
            public View getView(int i, View view, ViewGroup viewGroup) {
                TextView textView = (TextView) view;
                if (textView == null) {
                    textView = (TextView) this.mInflater.inflate(Editor.this.mTextView.mTextEditSuggestionItemLayout, viewGroup, false);
                }
                SuggestionInfo suggestionInfo = SuggestionsPopupWindow.this.mSuggestionInfos[i];
                textView.setText(suggestionInfo.text);
                if (suggestionInfo.suggestionIndex == -1 || suggestionInfo.suggestionIndex == -2) {
                    textView.setBackgroundColor(0);
                } else {
                    textView.setBackgroundColor(-1);
                }
                return textView;
            }
        }

        private class SuggestionSpanComparator implements Comparator<SuggestionSpan> {
            private SuggestionSpanComparator() {
            }

            @Override // java.util.Comparator
            public int compare(SuggestionSpan suggestionSpan, SuggestionSpan suggestionSpan2) {
                int flags = suggestionSpan.getFlags();
                int flags2 = suggestionSpan2.getFlags();
                if (flags != flags2) {
                    boolean z = (flags & 1) != 0;
                    boolean z2 = (flags2 & 1) != 0;
                    boolean z3 = (flags & 2) != 0;
                    boolean z4 = (flags2 & 2) != 0;
                    if (z && !z3) {
                        return -1;
                    }
                    if (z2 && !z4) {
                        return 1;
                    }
                    if (z3) {
                        return -1;
                    }
                    if (z4) {
                        return 1;
                    }
                }
                return ((Integer) SuggestionsPopupWindow.this.mSpansLengths.get(suggestionSpan)).intValue() - ((Integer) SuggestionsPopupWindow.this.mSpansLengths.get(suggestionSpan2)).intValue();
            }
        }

        private SuggestionSpan[] getSuggestionSpans() {
            int selectionStart = Editor.this.mTextView.getSelectionStart();
            Spannable spannable = (Spannable) Editor.this.mTextView.getText();
            SuggestionSpan[] suggestionSpanArr = (SuggestionSpan[]) spannable.getSpans(selectionStart, selectionStart, SuggestionSpan.class);
            this.mSpansLengths.clear();
            for (SuggestionSpan suggestionSpan : suggestionSpanArr) {
                this.mSpansLengths.put(suggestionSpan, Integer.valueOf(spannable.getSpanEnd(suggestionSpan) - spannable.getSpanStart(suggestionSpan)));
            }
            Arrays.sort(suggestionSpanArr, this.mSuggestionSpanComparator);
            return suggestionSpanArr;
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        public void show() {
            if ((Editor.this.mTextView.getText() instanceof Editable) && updateSuggestions()) {
                this.mCursorWasVisibleBeforeSuggestions = Editor.this.mCursorVisible;
                Editor.this.mTextView.setCursorVisible(false);
                this.mIsShowingUp = true;
                super.show();
            }
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        protected void measureContent() {
            DisplayMetrics displayMetrics = Editor.this.mTextView.getResources().getDisplayMetrics();
            int iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(displayMetrics.widthPixels, Integer.MIN_VALUE);
            int iMakeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(displayMetrics.heightPixels, Integer.MIN_VALUE);
            View view = null;
            int iMax = 0;
            for (int i = 0; i < this.mNumberOfSuggestions; i++) {
                view = this.mSuggestionsAdapter.getView(i, view, this.mContentView);
                view.getLayoutParams().width = -2;
                view.measure(iMakeMeasureSpec, iMakeMeasureSpec2);
                iMax = Math.max(iMax, view.getMeasuredWidth());
            }
            this.mContentView.measure(View.MeasureSpec.makeMeasureSpec(iMax, 1073741824), iMakeMeasureSpec2);
            Drawable background = this.mPopupWindow.getBackground();
            if (background != null) {
                if (Editor.this.mTempRect == null) {
                    Editor.this.mTempRect = new Rect();
                }
                background.getPadding(Editor.this.mTempRect);
                iMax += Editor.this.mTempRect.left + Editor.this.mTempRect.right;
            }
            this.mPopupWindow.setWidth(iMax);
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        protected int getTextOffset() {
            return Editor.this.mTextView.getSelectionStart();
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        protected int getVerticalLocalPosition(int i) {
            return Editor.this.mTextView.getLayout().getLineBottom(i);
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        protected int clipVertically(int i) {
            return Math.min(i, Editor.this.mTextView.getResources().getDisplayMetrics().heightPixels - this.mContentView.getMeasuredHeight());
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        public void hide() {
            super.hide();
        }

        private boolean updateSuggestions() {
            SuggestionSpan[] suggestionSpanArr;
            int i;
            int i2;
            int i3;
            boolean z;
            Spannable spannable = (Spannable) Editor.this.mTextView.getText();
            SuggestionSpan[] suggestionSpans = getSuggestionSpans();
            int length = suggestionSpans.length;
            int i4 = 0;
            if (length == 0) {
                return false;
            }
            this.mNumberOfSuggestions = 0;
            int length2 = Editor.this.mTextView.getText().length();
            int i5 = 0;
            int iMax = 0;
            int underlineColor = 0;
            SuggestionSpan suggestionSpan = null;
            while (i5 < length) {
                SuggestionSpan suggestionSpan2 = suggestionSpans[i5];
                int spanStart = spannable.getSpanStart(suggestionSpan2);
                int spanEnd = spannable.getSpanEnd(suggestionSpan2);
                int iMin = Math.min(spanStart, length2);
                iMax = Math.max(spanEnd, iMax);
                if ((suggestionSpan2.getFlags() & 2) != 0) {
                    suggestionSpan = suggestionSpan2;
                }
                if (i5 == 0) {
                    underlineColor = suggestionSpan2.getUnderlineColor();
                }
                String[] suggestions = suggestionSpan2.getSuggestions();
                int length3 = suggestions.length;
                int i6 = i4;
                while (true) {
                    if (i6 >= length3) {
                        suggestionSpanArr = suggestionSpans;
                        i = length;
                        i2 = iMin;
                        i3 = 1;
                        break;
                    }
                    String str = suggestions[i6];
                    suggestionSpanArr = suggestionSpans;
                    i = length;
                    int i7 = 0;
                    while (true) {
                        if (i7 >= this.mNumberOfSuggestions) {
                            i2 = iMin;
                            z = false;
                            break;
                        }
                        if (this.mSuggestionInfos[i7].text.toString().equals(str)) {
                            SuggestionSpan suggestionSpan3 = this.mSuggestionInfos[i7].suggestionSpan;
                            i2 = iMin;
                            int spanStart2 = spannable.getSpanStart(suggestionSpan3);
                            int spanEnd2 = spannable.getSpanEnd(suggestionSpan3);
                            if (spanStart == spanStart2 && spanEnd == spanEnd2) {
                                z = true;
                                break;
                            }
                        } else {
                            i2 = iMin;
                        }
                        i7++;
                        iMin = i2;
                    }
                    if (!z) {
                        SuggestionInfo suggestionInfo = this.mSuggestionInfos[this.mNumberOfSuggestions];
                        suggestionInfo.suggestionSpan = suggestionSpan2;
                        suggestionInfo.suggestionIndex = i6;
                        suggestionInfo.text.replace(0, suggestionInfo.text.length(), (CharSequence) str);
                        i3 = 1;
                        int i8 = this.mNumberOfSuggestions + 1;
                        this.mNumberOfSuggestions = i8;
                        if (i8 == 5) {
                            i5 = i;
                            break;
                        }
                    }
                    i6++;
                    suggestionSpans = suggestionSpanArr;
                    length = i;
                    iMin = i2;
                }
                i5 += i3;
                suggestionSpans = suggestionSpanArr;
                length = i;
                length2 = i2;
                i4 = 0;
            }
            for (int i9 = 0; i9 < this.mNumberOfSuggestions; i9++) {
                highlightTextDifferences(this.mSuggestionInfos[i9], length2, iMax);
            }
            if (suggestionSpan != null) {
                int spanStart3 = spannable.getSpanStart(suggestionSpan);
                int spanEnd3 = spannable.getSpanEnd(suggestionSpan);
                if (spanStart3 >= 0 && spanEnd3 > spanStart3) {
                    SuggestionInfo suggestionInfo2 = this.mSuggestionInfos[this.mNumberOfSuggestions];
                    suggestionInfo2.suggestionSpan = suggestionSpan;
                    suggestionInfo2.suggestionIndex = -1;
                    suggestionInfo2.text.replace(0, suggestionInfo2.text.length(), (CharSequence) Editor.this.mTextView.getContext().getString(17040364));
                    suggestionInfo2.text.setSpan(suggestionInfo2.highlightSpan, 0, 0, 33);
                    this.mNumberOfSuggestions++;
                }
            }
            SuggestionInfo suggestionInfo3 = this.mSuggestionInfos[this.mNumberOfSuggestions];
            suggestionInfo3.suggestionSpan = null;
            suggestionInfo3.suggestionIndex = -2;
            suggestionInfo3.text.replace(0, suggestionInfo3.text.length(), (CharSequence) Editor.this.mTextView.getContext().getString(17040365));
            suggestionInfo3.text.setSpan(suggestionInfo3.highlightSpan, 0, 0, 33);
            this.mNumberOfSuggestions++;
            if (Editor.this.mSuggestionRangeSpan == null) {
                Editor.this.mSuggestionRangeSpan = new SuggestionRangeSpan();
            }
            if (underlineColor == 0) {
                Editor.this.mSuggestionRangeSpan.setBackgroundColor(Editor.this.mTextView.mHighlightColor);
            } else {
                Editor.this.mSuggestionRangeSpan.setBackgroundColor((16777215 & underlineColor) + (((int) (Color.alpha(underlineColor) * 0.4f)) << 24));
            }
            spannable.setSpan(Editor.this.mSuggestionRangeSpan, length2, iMax, 33);
            this.mSuggestionsAdapter.notifyDataSetChanged();
            return true;
        }

        private void highlightTextDifferences(SuggestionInfo suggestionInfo, int i, int i2) {
            Spannable spannable = (Spannable) Editor.this.mTextView.getText();
            int spanStart = spannable.getSpanStart(suggestionInfo.suggestionSpan);
            int spanEnd = spannable.getSpanEnd(suggestionInfo.suggestionSpan);
            suggestionInfo.suggestionStart = spanStart - i;
            suggestionInfo.suggestionEnd = suggestionInfo.suggestionStart + suggestionInfo.text.length();
            suggestionInfo.text.setSpan(suggestionInfo.highlightSpan, 0, suggestionInfo.text.length(), 33);
            String string = spannable.toString();
            suggestionInfo.text.insert(0, (CharSequence) string.substring(i, spanStart));
            suggestionInfo.text.append((CharSequence) string.substring(spanEnd, i2));
        }

        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            Editable editable = (Editable) Editor.this.mTextView.getText();
            SuggestionInfo suggestionInfo = this.mSuggestionInfos[i];
            if (suggestionInfo.suggestionIndex == -2) {
                int spanStart = editable.getSpanStart(Editor.this.mSuggestionRangeSpan);
                int spanEnd = editable.getSpanEnd(Editor.this.mSuggestionRangeSpan);
                if (spanStart >= 0 && spanEnd > spanStart) {
                    if (spanEnd < editable.length() && Character.isSpaceChar(editable.charAt(spanEnd)) && (spanStart == 0 || Character.isSpaceChar(editable.charAt(spanStart - 1)))) {
                        spanEnd++;
                    }
                    Editor.this.mTextView.deleteText_internal(spanStart, spanEnd);
                }
                hide();
                return;
            }
            int spanStart2 = editable.getSpanStart(suggestionInfo.suggestionSpan);
            int spanEnd2 = editable.getSpanEnd(suggestionInfo.suggestionSpan);
            if (spanStart2 < 0 || spanEnd2 <= spanStart2) {
                hide();
                return;
            }
            String strSubstring = editable.toString().substring(spanStart2, spanEnd2);
            if (suggestionInfo.suggestionIndex == -1) {
                Intent intent = new Intent(Settings.ACTION_USER_DICTIONARY_INSERT);
                intent.putExtra(UserDictionary.Words.WORD, strSubstring);
                intent.putExtra(UserDictionary.Words.LOCALE, Editor.this.mTextView.getTextServicesLocale().toString());
                intent.setFlags(intent.getFlags() | 268435456);
                Editor.this.mTextView.getContext().startActivity(intent);
                editable.removeSpan(suggestionInfo.suggestionSpan);
                Selection.setSelection(editable, spanEnd2);
                Editor.this.updateSpellCheckSpans(spanStart2, spanEnd2, false);
            } else {
                SuggestionSpan[] suggestionSpanArr = (SuggestionSpan[]) editable.getSpans(spanStart2, spanEnd2, SuggestionSpan.class);
                int length = suggestionSpanArr.length;
                int[] iArr = new int[length];
                int[] iArr2 = new int[length];
                int[] iArr3 = new int[length];
                for (int i2 = 0; i2 < length; i2++) {
                    SuggestionSpan suggestionSpan = suggestionSpanArr[i2];
                    iArr[i2] = editable.getSpanStart(suggestionSpan);
                    iArr2[i2] = editable.getSpanEnd(suggestionSpan);
                    iArr3[i2] = editable.getSpanFlags(suggestionSpan);
                    int flags = suggestionSpan.getFlags();
                    if ((flags & 2) > 0) {
                        suggestionSpan.setFlags(flags & (-3) & (-2));
                    }
                }
                String string = suggestionInfo.text.subSequence(suggestionInfo.suggestionStart, suggestionInfo.suggestionEnd).toString();
                Editor.this.mTextView.replaceText_internal(spanStart2, spanEnd2, string);
                suggestionInfo.suggestionSpan.notifySelection(Editor.this.mTextView.getContext(), strSubstring, suggestionInfo.suggestionIndex);
                suggestionInfo.suggestionSpan.getSuggestions()[suggestionInfo.suggestionIndex] = strSubstring;
                int length2 = string.length() - (spanEnd2 - spanStart2);
                for (int i3 = 0; i3 < length; i3++) {
                    if (iArr[i3] <= spanStart2 && iArr2[i3] >= spanEnd2) {
                        Editor.this.mTextView.setSpan_internal(suggestionSpanArr[i3], iArr[i3], iArr2[i3] + length2, iArr3[i3]);
                    }
                }
                int i4 = spanEnd2 + length2;
                Editor.this.mTextView.setCursorPosition_internal(i4, i4);
            }
            hide();
        }
    }

    private class SelectionActionModeCallback implements ActionMode.Callback {
        private SelectionActionModeCallback() {
        }

        @Override // android.view.ActionMode.Callback
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            TypedArray typedArrayObtainStyledAttributes = Editor.this.mTextView.getContext().obtainStyledAttributes(com.android.internal.R.styleable.SelectionModeDrawables);
            actionMode.setTitle(Editor.this.mTextView.getContext().getString(17040363));
            actionMode.setSubtitle((CharSequence) null);
            actionMode.setTitleOptionalHint(true);
            menu.add(0, R.id.selectAll, 0, R.string.selectAll).setIcon(typedArrayObtainStyledAttributes.getResourceId(3, 0)).setAlphabeticShortcut(DateFormat.AM_PM).setShowAsAction(6);
            if (Editor.this.mTextView.canCut()) {
                menu.add(0, R.id.cut, 0, R.string.cut).setIcon(typedArrayObtainStyledAttributes.getResourceId(0, 0)).setAlphabeticShortcut('x').setShowAsAction(6);
            }
            if (Editor.this.mTextView.canCopy()) {
                menu.add(0, R.id.copy, 0, R.string.copy).setIcon(typedArrayObtainStyledAttributes.getResourceId(1, 0)).setAlphabeticShortcut('c').setShowAsAction(6);
            }
            if (Editor.this.mTextView.canPaste()) {
                menu.add(0, R.id.paste, 0, R.string.paste).setIcon(typedArrayObtainStyledAttributes.getResourceId(2, 0)).setAlphabeticShortcut('v').setShowAsAction(6);
            }
            typedArrayObtainStyledAttributes.recycle();
            if (Editor.this.mCustomSelectionActionModeCallback != null && !Editor.this.mCustomSelectionActionModeCallback.onCreateActionMode(actionMode, menu)) {
                return false;
            }
            if (!menu.hasVisibleItems() && actionMode.getCustomView() == null) {
                return false;
            }
            Editor.this.getSelectionController().show();
            Editor.this.mTextView.setHasTransientState(true);
            return true;
        }

        @Override // android.view.ActionMode.Callback
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            if (Editor.this.mCustomSelectionActionModeCallback != null) {
                return Editor.this.mCustomSelectionActionModeCallback.onPrepareActionMode(actionMode, menu);
            }
            return true;
        }

        @Override // android.view.ActionMode.Callback
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            if (Editor.this.mCustomSelectionActionModeCallback == null || !Editor.this.mCustomSelectionActionModeCallback.onActionItemClicked(actionMode, menuItem)) {
                return Editor.this.mTextView.onTextContextMenuItem(menuItem.getItemId());
            }
            return true;
        }

        @Override // android.view.ActionMode.Callback
        public void onDestroyActionMode(ActionMode actionMode) {
            if (Editor.this.mCustomSelectionActionModeCallback != null) {
                Editor.this.mCustomSelectionActionModeCallback.onDestroyActionMode(actionMode);
            }
            if (!Editor.this.mPreserveDetachedSelection) {
                Selection.setSelection((Spannable) Editor.this.mTextView.getText(), Editor.this.mTextView.getSelectionEnd());
                Editor.this.mTextView.setHasTransientState(false);
            }
            if (Editor.this.mSelectionModifierCursorController != null) {
                Editor.this.mSelectionModifierCursorController.hide();
            }
            Editor.this.mSelectionActionMode = null;
        }
    }

    private class ActionPopupWindow extends PinnedPopupWindow implements View.OnClickListener {
        private static final int POPUP_TEXT_LAYOUT = 17367210;
        private TextView mPasteTextView;
        private TextView mReplaceTextView;

        private ActionPopupWindow() {
            super();
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        protected void createPopupWindow() {
            this.mPopupWindow = new PopupWindow(Editor.this.mTextView.getContext(), (AttributeSet) null, R.attr.textSelectHandleWindowStyle);
            this.mPopupWindow.setClippingEnabled(true);
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        protected void initContentView() {
            LinearLayout linearLayout = new LinearLayout(Editor.this.mTextView.getContext());
            linearLayout.setOrientation(0);
            this.mContentView = linearLayout;
            this.mContentView.setBackgroundResource(17303091);
            LayoutInflater layoutInflater = (LayoutInflater) Editor.this.mTextView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(-2, -2);
            TextView textView = (TextView) layoutInflater.inflate(POPUP_TEXT_LAYOUT, (ViewGroup) null);
            this.mPasteTextView = textView;
            textView.setLayoutParams(layoutParams);
            this.mContentView.addView(this.mPasteTextView);
            this.mPasteTextView.setText(R.string.paste);
            this.mPasteTextView.setOnClickListener(this);
            TextView textView2 = (TextView) layoutInflater.inflate(POPUP_TEXT_LAYOUT, (ViewGroup) null);
            this.mReplaceTextView = textView2;
            textView2.setLayoutParams(layoutParams);
            this.mContentView.addView(this.mReplaceTextView);
            this.mReplaceTextView.setText(17040361);
            this.mReplaceTextView.setOnClickListener(this);
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        public void show() {
            boolean zCanPaste = Editor.this.mTextView.canPaste();
            boolean z = Editor.this.mTextView.isSuggestionsEnabled() && Editor.this.isCursorInsideSuggestionSpan();
            this.mPasteTextView.setVisibility(zCanPaste ? 0 : 8);
            this.mReplaceTextView.setVisibility(z ? 0 : 8);
            if (zCanPaste || z) {
                super.show();
            }
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (view == this.mPasteTextView && Editor.this.mTextView.canPaste()) {
                Editor.this.mTextView.onTextContextMenuItem(R.id.paste);
                hide();
            } else if (view == this.mReplaceTextView) {
                int selectionStart = (Editor.this.mTextView.getSelectionStart() + Editor.this.mTextView.getSelectionEnd()) / 2;
                Editor.this.stopSelectionActionMode();
                Selection.setSelection((Spannable) Editor.this.mTextView.getText(), selectionStart);
                Editor.this.showSuggestions();
            }
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        protected int getTextOffset() {
            return (Editor.this.mTextView.getSelectionStart() + Editor.this.mTextView.getSelectionEnd()) / 2;
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        protected int getVerticalLocalPosition(int i) {
            return Editor.this.mTextView.getLayout().getLineTop(i) - this.mContentView.getMeasuredHeight();
        }

        @Override // android.widget.Editor.PinnedPopupWindow
        protected int clipVertically(int i) {
            if (i >= 0) {
                return i;
            }
            int textOffset = getTextOffset();
            Layout layout = Editor.this.mTextView.getLayout();
            int lineForOffset = layout.getLineForOffset(textOffset);
            return i + (layout.getLineBottom(lineForOffset) - layout.getLineTop(lineForOffset)) + this.mContentView.getMeasuredHeight() + Editor.this.mTextView.getResources().getDrawable(Editor.this.mTextView.mTextSelectHandleRes).getIntrinsicHeight();
        }
    }

    private abstract class HandleView extends View implements TextViewPositionListener {
        private static final int HISTORY_SIZE = 5;
        private static final int TOUCH_UP_FILTER_DELAY_AFTER = 150;
        private static final int TOUCH_UP_FILTER_DELAY_BEFORE = 350;
        private Runnable mActionPopupShower;
        protected ActionPopupWindow mActionPopupWindow;
        private final PopupWindow mContainer;
        protected Drawable mDrawable;
        protected Drawable mDrawableLtr;
        protected Drawable mDrawableRtl;
        protected int mHotspotX;
        private float mIdealVerticalOffset;
        private boolean mIsDragging;
        private int mLastParentX;
        private int mLastParentY;
        private int mNumberPreviousOffsets;
        private boolean mPositionHasChanged;
        private int mPositionX;
        private int mPositionY;
        private int mPreviousOffset;
        private int mPreviousOffsetIndex;
        private final int[] mPreviousOffsets;
        private final long[] mPreviousOffsetsTimes;
        private float mTouchOffsetY;
        private float mTouchToWindowOffsetX;
        private float mTouchToWindowOffsetY;

        public abstract int getCurrentCursorOffset();

        protected abstract int getHotspotX(Drawable drawable, boolean z);

        public abstract void updatePosition(float f, float f2);

        protected abstract void updateSelection(int i);

        public HandleView(Drawable drawable, Drawable drawable2) {
            super(Editor.this.mTextView.getContext());
            this.mPreviousOffset = -1;
            this.mPositionHasChanged = true;
            this.mPreviousOffsetsTimes = new long[5];
            this.mPreviousOffsets = new int[5];
            this.mPreviousOffsetIndex = 0;
            this.mNumberPreviousOffsets = 0;
            PopupWindow popupWindow = new PopupWindow(Editor.this.mTextView.getContext(), (AttributeSet) null, R.attr.textSelectHandleWindowStyle);
            this.mContainer = popupWindow;
            popupWindow.setSplitTouchEnabled(true);
            popupWindow.setClippingEnabled(false);
            popupWindow.setWindowLayoutType(1002);
            popupWindow.setContentView(this);
            this.mDrawableLtr = drawable;
            this.mDrawableRtl = drawable2;
            updateDrawable();
            float intrinsicHeight = this.mDrawable.getIntrinsicHeight();
            this.mTouchOffsetY = (-0.3f) * intrinsicHeight;
            this.mIdealVerticalOffset = intrinsicHeight * 0.7f;
        }

        protected void updateDrawable() {
            boolean zIsRtlCharAt = Editor.this.mTextView.getLayout().isRtlCharAt(getCurrentCursorOffset());
            Drawable drawable = zIsRtlCharAt ? this.mDrawableRtl : this.mDrawableLtr;
            this.mDrawable = drawable;
            this.mHotspotX = getHotspotX(drawable, zIsRtlCharAt);
        }

        private void startTouchUpFilter(int i) {
            this.mNumberPreviousOffsets = 0;
            addPositionToTouchUpFilter(i);
        }

        private void addPositionToTouchUpFilter(int i) {
            int i2 = (this.mPreviousOffsetIndex + 1) % 5;
            this.mPreviousOffsetIndex = i2;
            this.mPreviousOffsets[i2] = i;
            this.mPreviousOffsetsTimes[i2] = SystemClock.uptimeMillis();
            this.mNumberPreviousOffsets++;
        }

        private void filterOnTouchUp() {
            long jUptimeMillis = SystemClock.uptimeMillis();
            int i = this.mPreviousOffsetIndex;
            int iMin = Math.min(this.mNumberPreviousOffsets, 5);
            int i2 = 0;
            while (i2 < iMin && jUptimeMillis - this.mPreviousOffsetsTimes[i] < 150) {
                i2++;
                i = ((this.mPreviousOffsetIndex - i2) + 5) % 5;
            }
            if (i2 <= 0 || i2 >= iMin || jUptimeMillis - this.mPreviousOffsetsTimes[i] <= 350) {
                return;
            }
            positionAtCursorOffset(this.mPreviousOffsets[i], false);
        }

        public boolean offsetHasBeenChanged() {
            return this.mNumberPreviousOffsets > 1;
        }

        @Override // android.view.View
        protected void onMeasure(int i, int i2) {
            setMeasuredDimension(this.mDrawable.getIntrinsicWidth(), this.mDrawable.getIntrinsicHeight());
        }

        public void show() {
            if (isShowing()) {
                return;
            }
            Editor.this.getPositionListener().addSubscriber(this, true);
            this.mPreviousOffset = -1;
            positionAtCursorOffset(getCurrentCursorOffset(), false);
            hideActionPopupWindow();
        }

        protected void dismiss() {
            this.mIsDragging = false;
            this.mContainer.dismiss();
            onDetached();
        }

        public void hide() {
            dismiss();
            Editor.this.getPositionListener().removeSubscriber(this);
        }

        void showActionPopupWindow(int i) {
            if (this.mActionPopupWindow == null) {
                this.mActionPopupWindow = new ActionPopupWindow();
            }
            if (this.mActionPopupShower != null) {
                Editor.this.mTextView.removeCallbacks(this.mActionPopupShower);
            } else {
                this.mActionPopupShower = new Runnable() { // from class: android.widget.Editor.HandleView.1
                    @Override // java.lang.Runnable
                    public void run() {
                        HandleView.this.mActionPopupWindow.show();
                    }
                };
            }
            Editor.this.mTextView.postDelayed(this.mActionPopupShower, i);
        }

        protected void hideActionPopupWindow() {
            if (this.mActionPopupShower != null) {
                Editor.this.mTextView.removeCallbacks(this.mActionPopupShower);
            }
            ActionPopupWindow actionPopupWindow = this.mActionPopupWindow;
            if (actionPopupWindow != null) {
                actionPopupWindow.hide();
            }
        }

        public boolean isShowing() {
            return this.mContainer.isShowing();
        }

        private boolean isVisible() {
            if (this.mIsDragging) {
                return true;
            }
            if (Editor.this.mTextView.isInBatchEditMode()) {
                return false;
            }
            return Editor.this.isPositionVisible(this.mPositionX + this.mHotspotX, this.mPositionY);
        }

        protected void positionAtCursorOffset(int i, boolean z) {
            Layout layout = Editor.this.mTextView.getLayout();
            if (layout == null) {
                Editor.this.prepareCursorControllers();
                return;
            }
            boolean z2 = i != this.mPreviousOffset;
            if (z2 || z) {
                if (z2) {
                    updateSelection(i);
                    addPositionToTouchUpFilter(i);
                }
                int lineForOffset = layout.getLineForOffset(i);
                this.mPositionX = (int) ((layout.getPrimaryHorizontal(i) - 0.5f) - this.mHotspotX);
                this.mPositionY = layout.getLineBottom(lineForOffset);
                this.mPositionX += Editor.this.mTextView.viewportToContentHorizontalOffset();
                this.mPositionY += Editor.this.mTextView.viewportToContentVerticalOffset();
                this.mPreviousOffset = i;
                this.mPositionHasChanged = true;
            }
        }

        @Override // android.widget.Editor.TextViewPositionListener
        public void updatePosition(int i, int i2, boolean z, boolean z2) {
            positionAtCursorOffset(getCurrentCursorOffset(), z2);
            if (z || this.mPositionHasChanged) {
                if (this.mIsDragging) {
                    if (i != this.mLastParentX || i2 != this.mLastParentY) {
                        this.mTouchToWindowOffsetX += i - r4;
                        this.mTouchToWindowOffsetY += i2 - this.mLastParentY;
                        this.mLastParentX = i;
                        this.mLastParentY = i2;
                    }
                    onHandleMoved();
                }
                if (isVisible()) {
                    int i3 = i + this.mPositionX;
                    int i4 = i2 + this.mPositionY;
                    if (!isShowing()) {
                        this.mContainer.showAtLocation(Editor.this.mTextView, 0, i3, i4);
                    } else {
                        this.mContainer.update(i3, i4, -1, -1);
                    }
                } else if (isShowing()) {
                    dismiss();
                }
                this.mPositionHasChanged = false;
            }
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            this.mDrawable.setBounds(0, 0, this.mRight - this.mLeft, this.mBottom - this.mTop);
            this.mDrawable.draw(canvas);
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent motionEvent) {
            float fMin;
            int actionMasked = motionEvent.getActionMasked();
            if (actionMasked == 0) {
                startTouchUpFilter(getCurrentCursorOffset());
                this.mTouchToWindowOffsetX = motionEvent.getRawX() - this.mPositionX;
                this.mTouchToWindowOffsetY = motionEvent.getRawY() - this.mPositionY;
                PositionListener positionListener = Editor.this.getPositionListener();
                this.mLastParentX = positionListener.getPositionX();
                this.mLastParentY = positionListener.getPositionY();
                this.mIsDragging = true;
            } else if (actionMasked == 1) {
                filterOnTouchUp();
                this.mIsDragging = false;
            } else if (actionMasked == 2) {
                float rawX = motionEvent.getRawX();
                float rawY = motionEvent.getRawY();
                float f = this.mTouchToWindowOffsetY;
                int i = this.mLastParentY;
                float f2 = f - i;
                float f3 = (rawY - this.mPositionY) - i;
                float f4 = this.mIdealVerticalOffset;
                if (f2 < f4) {
                    fMin = Math.max(Math.min(f3, f4), f2);
                } else {
                    fMin = Math.min(Math.max(f3, f4), f2);
                }
                float f5 = fMin + this.mLastParentY;
                this.mTouchToWindowOffsetY = f5;
                updatePosition((rawX - this.mTouchToWindowOffsetX) + this.mHotspotX, (rawY - f5) + this.mTouchOffsetY);
            } else if (actionMasked == 3) {
                this.mIsDragging = false;
            }
            return true;
        }

        public boolean isDragging() {
            return this.mIsDragging;
        }

        void onHandleMoved() {
            hideActionPopupWindow();
        }

        public void onDetached() {
            hideActionPopupWindow();
        }
    }

    private class InsertionHandleView extends HandleView {
        private static final int DELAY_BEFORE_HANDLE_FADES_OUT = 4000;
        private static final int RECENT_CUT_COPY_DURATION = 15000;
        private float mDownPositionX;
        private float mDownPositionY;
        private Runnable mHider;

        public InsertionHandleView(Drawable drawable) {
            super(drawable, drawable);
        }

        @Override // android.widget.Editor.HandleView
        public void show() {
            super.show();
            if (SystemClock.uptimeMillis() - TextView.LAST_CUT_OR_COPY_TIME < 15000) {
                showActionPopupWindow(0);
            }
            hideAfterDelay();
        }

        public void showWithActionPopup() {
            show();
            showActionPopupWindow(0);
        }

        private void hideAfterDelay() {
            if (this.mHider == null) {
                this.mHider = new Runnable() { // from class: android.widget.Editor.InsertionHandleView.1
                    @Override // java.lang.Runnable
                    public void run() {
                        InsertionHandleView.this.hide();
                    }
                };
            } else {
                removeHiderCallback();
            }
            Editor.this.mTextView.postDelayed(this.mHider, 4000L);
        }

        private void removeHiderCallback() {
            if (this.mHider != null) {
                Editor.this.mTextView.removeCallbacks(this.mHider);
            }
        }

        @Override // android.widget.Editor.HandleView
        protected int getHotspotX(Drawable drawable, boolean z) {
            return drawable.getIntrinsicWidth() / 2;
        }

        @Override // android.widget.Editor.HandleView, android.view.View
        public boolean onTouchEvent(MotionEvent motionEvent) {
            boolean zOnTouchEvent = super.onTouchEvent(motionEvent);
            int actionMasked = motionEvent.getActionMasked();
            if (actionMasked == 0) {
                this.mDownPositionX = motionEvent.getRawX();
                this.mDownPositionY = motionEvent.getRawY();
            } else if (actionMasked == 1) {
                if (!offsetHasBeenChanged()) {
                    float rawX = this.mDownPositionX - motionEvent.getRawX();
                    float rawY = this.mDownPositionY - motionEvent.getRawY();
                    float f = (rawX * rawX) + (rawY * rawY);
                    int scaledTouchSlop = ViewConfiguration.get(Editor.this.mTextView.getContext()).getScaledTouchSlop();
                    if (f < scaledTouchSlop * scaledTouchSlop) {
                        if (this.mActionPopupWindow != null && this.mActionPopupWindow.isShowing()) {
                            this.mActionPopupWindow.hide();
                        } else {
                            showWithActionPopup();
                        }
                    }
                }
                hideAfterDelay();
            } else if (actionMasked == 3) {
                hideAfterDelay();
            }
            return zOnTouchEvent;
        }

        @Override // android.widget.Editor.HandleView
        public int getCurrentCursorOffset() {
            return Editor.this.mTextView.getSelectionStart();
        }

        @Override // android.widget.Editor.HandleView
        public void updateSelection(int i) {
            Selection.setSelection((Spannable) Editor.this.mTextView.getText(), i);
        }

        @Override // android.widget.Editor.HandleView
        public void updatePosition(float f, float f2) {
            positionAtCursorOffset(Editor.this.mTextView.getOffsetForPosition(f, f2), false);
        }

        @Override // android.widget.Editor.HandleView
        void onHandleMoved() {
            super.onHandleMoved();
            removeHiderCallback();
        }

        @Override // android.widget.Editor.HandleView
        public void onDetached() {
            super.onDetached();
            removeHiderCallback();
        }
    }

    private class SelectionStartHandleView extends HandleView {
        public SelectionStartHandleView(Drawable drawable, Drawable drawable2) {
            super(drawable, drawable2);
        }

        @Override // android.widget.Editor.HandleView
        protected int getHotspotX(Drawable drawable, boolean z) {
            if (z) {
                return drawable.getIntrinsicWidth() / 4;
            }
            return (drawable.getIntrinsicWidth() * 3) / 4;
        }

        @Override // android.widget.Editor.HandleView
        public int getCurrentCursorOffset() {
            return Editor.this.mTextView.getSelectionStart();
        }

        @Override // android.widget.Editor.HandleView
        public void updateSelection(int i) {
            Selection.setSelection((Spannable) Editor.this.mTextView.getText(), i, Editor.this.mTextView.getSelectionEnd());
            updateDrawable();
        }

        @Override // android.widget.Editor.HandleView
        public void updatePosition(float f, float f2) {
            int offsetForPosition = Editor.this.mTextView.getOffsetForPosition(f, f2);
            int selectionEnd = Editor.this.mTextView.getSelectionEnd();
            if (offsetForPosition >= selectionEnd) {
                offsetForPosition = Math.max(0, selectionEnd - 1);
            }
            positionAtCursorOffset(offsetForPosition, false);
        }

        public ActionPopupWindow getActionPopupWindow() {
            return this.mActionPopupWindow;
        }
    }

    private class SelectionEndHandleView extends HandleView {
        public SelectionEndHandleView(Drawable drawable, Drawable drawable2) {
            super(drawable, drawable2);
        }

        @Override // android.widget.Editor.HandleView
        protected int getHotspotX(Drawable drawable, boolean z) {
            if (z) {
                return (drawable.getIntrinsicWidth() * 3) / 4;
            }
            return drawable.getIntrinsicWidth() / 4;
        }

        @Override // android.widget.Editor.HandleView
        public int getCurrentCursorOffset() {
            return Editor.this.mTextView.getSelectionEnd();
        }

        @Override // android.widget.Editor.HandleView
        public void updateSelection(int i) {
            Selection.setSelection((Spannable) Editor.this.mTextView.getText(), Editor.this.mTextView.getSelectionStart(), i);
            updateDrawable();
        }

        @Override // android.widget.Editor.HandleView
        public void updatePosition(float f, float f2) {
            int offsetForPosition = Editor.this.mTextView.getOffsetForPosition(f, f2);
            int selectionStart = Editor.this.mTextView.getSelectionStart();
            if (offsetForPosition <= selectionStart) {
                offsetForPosition = Math.min(selectionStart + 1, Editor.this.mTextView.getText().length());
            }
            positionAtCursorOffset(offsetForPosition, false);
        }

        public void setActionPopupWindow(ActionPopupWindow actionPopupWindow) {
            this.mActionPopupWindow = actionPopupWindow;
        }
    }

    private class InsertionPointCursorController implements CursorController {
        private InsertionHandleView mHandle;

        private InsertionPointCursorController() {
        }

        @Override // android.widget.Editor.CursorController
        public void show() {
            getHandle().show();
        }

        public void showWithActionPopup() {
            getHandle().showWithActionPopup();
        }

        @Override // android.widget.Editor.CursorController
        public void hide() {
            InsertionHandleView insertionHandleView = this.mHandle;
            if (insertionHandleView != null) {
                insertionHandleView.hide();
            }
        }

        @Override // android.view.ViewTreeObserver.OnTouchModeChangeListener
        public void onTouchModeChanged(boolean z) {
            if (z) {
                return;
            }
            hide();
        }

        private InsertionHandleView getHandle() {
            if (Editor.this.mSelectHandleCenter == null) {
                Editor editor = Editor.this;
                editor.mSelectHandleCenter = editor.mTextView.getResources().getDrawable(Editor.this.mTextView.mTextSelectHandleRes);
            }
            if (this.mHandle == null) {
                Editor editor2 = Editor.this;
                this.mHandle = editor2.new InsertionHandleView(editor2.mSelectHandleCenter);
            }
            return this.mHandle;
        }

        @Override // android.widget.Editor.CursorController
        public void onDetached() {
            Editor.this.mTextView.getViewTreeObserver().removeOnTouchModeChangeListener(this);
            InsertionHandleView insertionHandleView = this.mHandle;
            if (insertionHandleView != null) {
                insertionHandleView.onDetached();
            }
        }
    }

    class SelectionModifierCursorController implements CursorController {
        private static final int DELAY_BEFORE_REPLACE_ACTION = 200;
        private float mDownPositionX;
        private float mDownPositionY;
        private SelectionEndHandleView mEndHandle;
        private boolean mGestureStayedInTapRegion;
        private int mMaxTouchOffset;
        private int mMinTouchOffset;
        private long mPreviousTapUpTime = 0;
        private SelectionStartHandleView mStartHandle;

        SelectionModifierCursorController() {
            resetTouchOffsets();
        }

        @Override // android.widget.Editor.CursorController
        public void show() {
            if (Editor.this.mTextView.isInBatchEditMode()) {
                return;
            }
            initDrawables();
            initHandles();
            Editor.this.hideInsertionPointCursorController();
        }

        private void initDrawables() {
            if (Editor.this.mSelectHandleLeft == null) {
                Editor editor = Editor.this;
                editor.mSelectHandleLeft = editor.mTextView.getContext().getResources().getDrawable(Editor.this.mTextView.mTextSelectHandleLeftRes);
            }
            if (Editor.this.mSelectHandleRight == null) {
                Editor editor2 = Editor.this;
                editor2.mSelectHandleRight = editor2.mTextView.getContext().getResources().getDrawable(Editor.this.mTextView.mTextSelectHandleRightRes);
            }
        }

        private void initHandles() {
            if (this.mStartHandle == null) {
                Editor editor = Editor.this;
                this.mStartHandle = editor.new SelectionStartHandleView(editor.mSelectHandleLeft, Editor.this.mSelectHandleRight);
            }
            if (this.mEndHandle == null) {
                Editor editor2 = Editor.this;
                this.mEndHandle = editor2.new SelectionEndHandleView(editor2.mSelectHandleRight, Editor.this.mSelectHandleLeft);
            }
            this.mStartHandle.show();
            this.mEndHandle.show();
            this.mStartHandle.showActionPopupWindow(200);
            this.mEndHandle.setActionPopupWindow(this.mStartHandle.getActionPopupWindow());
            Editor.this.hideInsertionPointCursorController();
        }

        @Override // android.widget.Editor.CursorController
        public void hide() {
            SelectionStartHandleView selectionStartHandleView = this.mStartHandle;
            if (selectionStartHandleView != null) {
                selectionStartHandleView.hide();
            }
            SelectionEndHandleView selectionEndHandleView = this.mEndHandle;
            if (selectionEndHandleView != null) {
                selectionEndHandleView.hide();
            }
        }

        public void onTouchEvent(MotionEvent motionEvent) {
            int actionMasked = motionEvent.getActionMasked();
            if (actionMasked == 0) {
                float x = motionEvent.getX();
                float y = motionEvent.getY();
                int offsetForPosition = Editor.this.mTextView.getOffsetForPosition(x, y);
                this.mMaxTouchOffset = offsetForPosition;
                this.mMinTouchOffset = offsetForPosition;
                if (this.mGestureStayedInTapRegion && SystemClock.uptimeMillis() - this.mPreviousTapUpTime <= ViewConfiguration.getDoubleTapTimeout()) {
                    float f = x - this.mDownPositionX;
                    float f2 = y - this.mDownPositionY;
                    float f3 = (f * f) + (f2 * f2);
                    int scaledDoubleTapSlop = ViewConfiguration.get(Editor.this.mTextView.getContext()).getScaledDoubleTapSlop();
                    if ((f3 < ((float) (scaledDoubleTapSlop * scaledDoubleTapSlop))) && Editor.this.isPositionOnText(x, y)) {
                        Editor.this.startSelectionActionMode();
                        Editor.this.mDiscardNextActionUp = true;
                    }
                }
                this.mDownPositionX = x;
                this.mDownPositionY = y;
                this.mGestureStayedInTapRegion = true;
                return;
            }
            if (actionMasked == 1) {
                this.mPreviousTapUpTime = SystemClock.uptimeMillis();
                return;
            }
            if (actionMasked != 2) {
                if ((actionMasked == 5 || actionMasked == 6) && Editor.this.mTextView.getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH_DISTINCT)) {
                    updateMinAndMaxOffsets(motionEvent);
                    return;
                }
                return;
            }
            if (this.mGestureStayedInTapRegion) {
                float x2 = motionEvent.getX() - this.mDownPositionX;
                float y2 = motionEvent.getY() - this.mDownPositionY;
                float f4 = (x2 * x2) + (y2 * y2);
                int scaledDoubleTapTouchSlop = ViewConfiguration.get(Editor.this.mTextView.getContext()).getScaledDoubleTapTouchSlop();
                if (f4 > scaledDoubleTapTouchSlop * scaledDoubleTapTouchSlop) {
                    this.mGestureStayedInTapRegion = false;
                }
            }
        }

        private void updateMinAndMaxOffsets(MotionEvent motionEvent) {
            int pointerCount = motionEvent.getPointerCount();
            for (int i = 0; i < pointerCount; i++) {
                int offsetForPosition = Editor.this.mTextView.getOffsetForPosition(motionEvent.getX(i), motionEvent.getY(i));
                if (offsetForPosition < this.mMinTouchOffset) {
                    this.mMinTouchOffset = offsetForPosition;
                }
                if (offsetForPosition > this.mMaxTouchOffset) {
                    this.mMaxTouchOffset = offsetForPosition;
                }
            }
        }

        public int getMinTouchOffset() {
            return this.mMinTouchOffset;
        }

        public int getMaxTouchOffset() {
            return this.mMaxTouchOffset;
        }

        public void resetTouchOffsets() {
            this.mMaxTouchOffset = -1;
            this.mMinTouchOffset = -1;
        }

        public boolean isSelectionStartDragged() {
            SelectionStartHandleView selectionStartHandleView = this.mStartHandle;
            return selectionStartHandleView != null && selectionStartHandleView.isDragging();
        }

        @Override // android.view.ViewTreeObserver.OnTouchModeChangeListener
        public void onTouchModeChanged(boolean z) {
            if (z) {
                return;
            }
            hide();
        }

        @Override // android.widget.Editor.CursorController
        public void onDetached() {
            Editor.this.mTextView.getViewTreeObserver().removeOnTouchModeChangeListener(this);
            SelectionStartHandleView selectionStartHandleView = this.mStartHandle;
            if (selectionStartHandleView != null) {
                selectionStartHandleView.onDetached();
            }
            SelectionEndHandleView selectionEndHandleView = this.mEndHandle;
            if (selectionEndHandleView != null) {
                selectionEndHandleView.onDetached();
            }
        }
    }

    private class CorrectionHighlighter {
        private static final int FADE_OUT_DURATION = 400;
        private int mEnd;
        private long mFadingStartTime;
        private final Paint mPaint;
        private final Path mPath = new Path();
        private int mStart;
        private RectF mTempRectF;

        public CorrectionHighlighter() {
            Paint paint = new Paint(1);
            this.mPaint = paint;
            paint.setCompatibilityScaling(Editor.this.mTextView.getResources().getCompatibilityInfo().applicationScale);
            paint.setStyle(Paint.Style.FILL);
        }

        public void highlight(CorrectionInfo correctionInfo) {
            int offset = correctionInfo.getOffset();
            this.mStart = offset;
            this.mEnd = offset + correctionInfo.getNewText().length();
            this.mFadingStartTime = SystemClock.uptimeMillis();
            if (this.mStart < 0 || this.mEnd < 0) {
                stopAnimation();
            }
        }

        public void draw(Canvas canvas, int i) {
            if (updatePath() && updatePaint()) {
                if (i != 0) {
                    canvas.translate(0.0f, i);
                }
                canvas.drawPath(this.mPath, this.mPaint);
                if (i != 0) {
                    canvas.translate(0.0f, -i);
                }
                invalidate(true);
                return;
            }
            stopAnimation();
            invalidate(false);
        }

        private boolean updatePaint() {
            long jUptimeMillis = SystemClock.uptimeMillis() - this.mFadingStartTime;
            if (jUptimeMillis > 400) {
                return false;
            }
            this.mPaint.setColor((Editor.this.mTextView.mHighlightColor & 16777215) + (((int) (Color.alpha(Editor.this.mTextView.mHighlightColor) * (1.0f - (jUptimeMillis / 400.0f)))) << 24));
            return true;
        }

        private boolean updatePath() {
            Layout layout = Editor.this.mTextView.getLayout();
            if (layout == null) {
                return false;
            }
            int length = Editor.this.mTextView.getText().length();
            int iMin = Math.min(length, this.mStart);
            int iMin2 = Math.min(length, this.mEnd);
            this.mPath.reset();
            layout.getSelectionPath(iMin, iMin2, this.mPath);
            return true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void invalidate(boolean z) {
            if (Editor.this.mTextView.getLayout() == null) {
                return;
            }
            if (this.mTempRectF == null) {
                this.mTempRectF = new RectF();
            }
            this.mPath.computeBounds(this.mTempRectF, false);
            int compoundPaddingLeft = Editor.this.mTextView.getCompoundPaddingLeft();
            int extendedPaddingTop = Editor.this.mTextView.getExtendedPaddingTop() + Editor.this.mTextView.getVerticalOffset(true);
            if (z) {
                Editor.this.mTextView.postInvalidateOnAnimation(((int) this.mTempRectF.left) + compoundPaddingLeft, ((int) this.mTempRectF.top) + extendedPaddingTop, compoundPaddingLeft + ((int) this.mTempRectF.right), extendedPaddingTop + ((int) this.mTempRectF.bottom));
            } else {
                Editor.this.mTextView.postInvalidate((int) this.mTempRectF.left, (int) this.mTempRectF.top, (int) this.mTempRectF.right, (int) this.mTempRectF.bottom);
            }
        }

        private void stopAnimation() {
            Editor.this.mCorrectionHighlighter = null;
        }
    }

    private static class ErrorPopup extends PopupWindow {
        private boolean mAbove;
        private int mPopupInlineErrorAboveBackgroundId;
        private int mPopupInlineErrorBackgroundId;
        private final TextView mView;

        ErrorPopup(TextView textView, int i, int i2) {
            super(textView, i, i2);
            this.mAbove = false;
            this.mPopupInlineErrorBackgroundId = 0;
            this.mPopupInlineErrorAboveBackgroundId = 0;
            this.mView = textView;
            int resourceId = getResourceId(0, 225);
            this.mPopupInlineErrorBackgroundId = resourceId;
            textView.setBackgroundResource(resourceId);
        }

        void fixDirection(boolean z) {
            this.mAbove = z;
            if (z) {
                this.mPopupInlineErrorAboveBackgroundId = getResourceId(this.mPopupInlineErrorAboveBackgroundId, 226);
            } else {
                this.mPopupInlineErrorBackgroundId = getResourceId(this.mPopupInlineErrorBackgroundId, 225);
            }
            this.mView.setBackgroundResource(z ? this.mPopupInlineErrorAboveBackgroundId : this.mPopupInlineErrorBackgroundId);
        }

        private int getResourceId(int i, int i2) {
            if (i != 0) {
                return i;
            }
            TypedArray typedArrayObtainStyledAttributes = this.mView.getContext().obtainStyledAttributes(R.styleable.Theme);
            int resourceId = typedArrayObtainStyledAttributes.getResourceId(i2, 0);
            typedArrayObtainStyledAttributes.recycle();
            return resourceId;
        }

        @Override // android.widget.PopupWindow
        public void update(int i, int i2, int i3, int i4, boolean z) {
            super.update(i, i2, i3, i4, z);
            boolean zIsAboveAnchor = isAboveAnchor();
            if (zIsAboveAnchor != this.mAbove) {
                fixDirection(zIsAboveAnchor);
            }
        }
    }

    static class InputContentType {
        boolean enterDown;
        Bundle extras;
        int imeActionId;
        CharSequence imeActionLabel;
        int imeOptions = 0;
        TextView.OnEditorActionListener onEditorActionListener;
        String privateImeOptions;

        InputContentType() {
        }
    }

    static class InputMethodState {
        int mBatchEditNesting;
        int mChangedDelta;
        int mChangedEnd;
        int mChangedStart;
        boolean mContentChanged;
        boolean mCursorChanged;
        ExtractedTextRequest mExtractedTextRequest;
        boolean mSelectionModeChanged;
        Rect mCursorRectInWindow = new Rect();
        RectF mTmpRectF = new RectF();
        float[] mTmpOffset = new float[2];
        final ExtractedText mExtractedText = new ExtractedText();

        InputMethodState() {
        }
    }

    public static class UndoInputFilter implements InputFilter {
        final Editor mEditor;

        public UndoInputFilter(Editor editor) {
            this.mEditor = editor;
        }

        @Override // android.text.InputFilter
        public CharSequence filter(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
            SpannableStringBuilder spannableStringBuilder;
            UndoManager undoManager = this.mEditor.mUndoManager;
            if (undoManager.isInUndo()) {
                return null;
            }
            undoManager.beginUpdate("Edit text");
            TextModifyOperation textModifyOperation = (TextModifyOperation) undoManager.getLastOperation(TextModifyOperation.class, this.mEditor.mUndoOwner, 1);
            if (textModifyOperation != null) {
                if (textModifyOperation.mOldText == null) {
                    if (i < i2 && ((i3 >= textModifyOperation.mRangeStart && i4 <= textModifyOperation.mRangeEnd) || (i3 == textModifyOperation.mRangeEnd && i4 == textModifyOperation.mRangeEnd))) {
                        textModifyOperation.mRangeEnd = i3 + (i2 - i);
                        undoManager.endUpdate();
                        return null;
                    }
                } else if (i == i2 && i4 == textModifyOperation.mRangeStart - 1) {
                    if (textModifyOperation.mOldText instanceof SpannableString) {
                        spannableStringBuilder = (SpannableStringBuilder) textModifyOperation.mOldText;
                    } else {
                        spannableStringBuilder = new SpannableStringBuilder(textModifyOperation.mOldText);
                    }
                    spannableStringBuilder.insert(0, (CharSequence) spanned, i3, i4);
                    textModifyOperation.mRangeStart = i3;
                    textModifyOperation.mOldText = spannableStringBuilder;
                    undoManager.endUpdate();
                    return null;
                }
                undoManager.commitState(null);
                undoManager.setUndoLabel("Edit text");
            }
            TextModifyOperation textModifyOperation2 = new TextModifyOperation(this.mEditor.mUndoOwner);
            textModifyOperation2.mRangeStart = i3;
            if (i < i2) {
                textModifyOperation2.mRangeEnd = (i2 - i) + i3;
            } else {
                textModifyOperation2.mRangeEnd = i3;
            }
            if (i3 < i4) {
                textModifyOperation2.mOldText = spanned.subSequence(i3, i4);
            }
            undoManager.addOperation(textModifyOperation2, 0);
            undoManager.endUpdate();
            return null;
        }
    }

    public static class TextModifyOperation extends UndoOperation<TextView> {
        public static final Parcelable.ClassLoaderCreator<TextModifyOperation> CREATOR = new Parcelable.ClassLoaderCreator<TextModifyOperation>() { // from class: android.widget.Editor.TextModifyOperation.1
            @Override // android.os.Parcelable.Creator
            public TextModifyOperation createFromParcel(Parcel parcel) {
                return new TextModifyOperation(parcel, null);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.ClassLoaderCreator
            public TextModifyOperation createFromParcel(Parcel parcel, ClassLoader classLoader) {
                return new TextModifyOperation(parcel, classLoader);
            }

            @Override // android.os.Parcelable.Creator
            public TextModifyOperation[] newArray(int i) {
                return new TextModifyOperation[i];
            }
        };
        CharSequence mOldText;
        int mRangeEnd;
        int mRangeStart;

        @Override // android.content.UndoOperation
        public void commit() {
        }

        public TextModifyOperation(UndoOwner undoOwner) {
            super(undoOwner);
        }

        public TextModifyOperation(Parcel parcel, ClassLoader classLoader) {
            super(parcel, classLoader);
            this.mRangeStart = parcel.readInt();
            this.mRangeEnd = parcel.readInt();
            this.mOldText = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
        }

        @Override // android.content.UndoOperation
        public void undo() {
            swapText();
        }

        @Override // android.content.UndoOperation
        public void redo() {
            swapText();
        }

        private void swapText() {
            Editable editable = (Editable) getOwnerData().getText();
            int i = this.mRangeStart;
            int i2 = this.mRangeEnd;
            CharSequence charSequenceSubSequence = i >= i2 ? null : editable.subSequence(i, i2);
            CharSequence charSequence = this.mOldText;
            if (charSequence == null) {
                editable.delete(this.mRangeStart, this.mRangeEnd);
                this.mRangeEnd = this.mRangeStart;
            } else {
                editable.replace(this.mRangeStart, this.mRangeEnd, charSequence);
                this.mRangeEnd = this.mRangeStart + this.mOldText.length();
            }
            this.mOldText = charSequenceSubSequence;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.mRangeStart);
            parcel.writeInt(this.mRangeEnd);
            TextUtils.writeToParcel(this.mOldText, parcel, i);
        }
    }
}
