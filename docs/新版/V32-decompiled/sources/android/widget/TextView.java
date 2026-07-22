package android.widget;

import android.R;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.UndoManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Canvas;
import android.graphics.Insets;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.ExtractEditText;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.BoringLayout;
import android.text.DynamicLayout;
import android.text.Editable;
import android.text.GetChars;
import android.text.GraphicsOperations;
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
import android.text.SpannedString;
import android.text.StaticLayout;
import android.text.TextDirectionHeuristic;
import android.text.TextDirectionHeuristics;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.AllCapsTransformationMethod;
import android.text.method.ArrowKeyMovementMethod;
import android.text.method.DateKeyListener;
import android.text.method.DateTimeKeyListener;
import android.text.method.DialerKeyListener;
import android.text.method.DigitsKeyListener;
import android.text.method.KeyListener;
import android.text.method.LinkMovementMethod;
import android.text.method.MetaKeyKeyListener;
import android.text.method.MovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.text.method.TextKeyListener;
import android.text.method.TimeKeyListener;
import android.text.method.TransformationMethod;
import android.text.method.TransformationMethod2;
import android.text.method.WordIterator;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.ParagraphStyle;
import android.text.style.SpellCheckSpan;
import android.text.style.SuggestionSpan;
import android.text.style.URLSpan;
import android.text.style.UpdateAppearance;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.util.TypedValue;
import android.view.AccessibilityIterators;
import android.view.ActionMode;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.RemotableViewMethod;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewDebug;
import android.view.ViewRootImpl;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.CorrectionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.view.textservice.SpellCheckerSubtype;
import android.view.textservice.TextServicesManager;
import android.widget.AccessibilityIterators;
import android.widget.Editor;
import android.widget.RemoteViews;
import com.android.internal.util.FastMath;
import com.android.internal.widget.EditableInputConnection;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes.dex */
@RemoteViews.RemoteView
public class TextView extends View implements ViewTreeObserver.OnPreDrawListener {
    private static final int ANIMATED_SCROLL_GAP = 250;
    private static final int CHANGE_WATCHER_PRIORITY = 100;
    static final boolean DEBUG_EXTRACT = false;
    private static final int DECIMAL = 4;
    private static final int EMS = 1;
    static final int ID_COPY = 16908321;
    static final int ID_CUT = 16908320;
    static final int ID_PASTE = 16908322;
    static final int ID_SELECT_ALL = 16908319;
    static long LAST_CUT_OR_COPY_TIME = 0;
    private static final int LINES = 1;
    static final String LOG_TAG = "TextView";
    private static final int MARQUEE_FADE_NORMAL = 0;
    private static final int MARQUEE_FADE_SWITCH_SHOW_ELLIPSIS = 1;
    private static final int MARQUEE_FADE_SWITCH_SHOW_FADE = 2;
    private static final int MONOSPACE = 3;
    private static final int PIXELS = 2;
    private static final int SANS = 1;
    private static final int SERIF = 2;
    private static final int SIGNED = 2;
    private static final BoringLayout.Metrics UNKNOWN_BORING;
    private static final int VERY_WIDE = 1048576;
    private boolean mAllowTransformationLengthChange;
    private int mAutoLinkMask;
    private BoringLayout.Metrics mBoring;
    private BufferType mBufferType;
    private ChangeWatcher mChangeWatcher;
    private CharWrapper mCharWrapper;
    private int mCurHintTextColor;
    private int mCurTextColor;
    private volatile Locale mCurrentSpellCheckerLocaleCache;
    int mCursorDrawableRes;
    private int mDeferScroll;
    private int mDesiredHeightAtMeasure;
    private boolean mDispatchTemporaryDetach;
    Drawables mDrawables;
    private Editable.Factory mEditableFactory;
    private Editor mEditor;
    private TextUtils.TruncateAt mEllipsize;
    private InputFilter[] mFilters;
    private boolean mFreezesText;
    private int mGravity;
    int mHighlightColor;
    private final Paint mHighlightPaint;
    private Path mHighlightPath;
    private boolean mHighlightPathBogus;
    private CharSequence mHint;
    private BoringLayout.Metrics mHintBoring;
    private Layout mHintLayout;
    private ColorStateList mHintTextColor;
    private boolean mHorizontallyScrolling;
    private boolean mIncludePad;
    private int mLastLayoutDirection;
    private long mLastScroll;
    private Layout mLayout;
    private ColorStateList mLinkTextColor;
    private boolean mLinksClickable;
    private ArrayList<TextWatcher> mListeners;
    private Marquee mMarquee;
    private int mMarqueeFadeMode;
    private int mMarqueeRepeatLimit;
    private int mMaxMode;
    private int mMaxWidth;
    private int mMaxWidthMode;
    private int mMaximum;
    private int mMinMode;
    private int mMinWidth;
    private int mMinWidthMode;
    private int mMinimum;
    private MovementMethod mMovement;
    private int mOldMaxMode;
    private int mOldMaximum;
    private boolean mPreDrawRegistered;
    private boolean mPreventDefaultMovement;
    private boolean mRestartMarquee;
    private BoringLayout mSavedHintLayout;
    private BoringLayout mSavedLayout;
    private Layout mSavedMarqueeModeLayout;
    private Scroller mScroller;
    private float mShadowDx;
    private float mShadowDy;
    private float mShadowRadius;
    private boolean mSingleLine;
    private float mSpacingAdd;
    private float mSpacingMult;
    private Spannable.Factory mSpannableFactory;
    private Rect mTempRect;
    private boolean mTemporaryDetach;

    @ViewDebug.ExportedProperty(category = "text")
    private CharSequence mText;
    private ColorStateList mTextColor;
    private TextDirectionHeuristic mTextDir;
    int mTextEditSuggestionItemLayout;
    private final TextPaint mTextPaint;
    int mTextSelectHandleLeftRes;
    int mTextSelectHandleRes;
    int mTextSelectHandleRightRes;
    private TransformationMethod mTransformation;
    private CharSequence mTransformed;
    private boolean mUserSetTextScaleX;
    private static final RectF TEMP_RECTF = new RectF();
    private static final InputFilter[] NO_FILTERS = new InputFilter[0];
    private static final Spanned EMPTY_SPANNED = new SpannedString("");
    private static final int[] MULTILINE_STATE_SET = {R.attr.state_multiline};

    public enum BufferType {
        NORMAL,
        SPANNABLE,
        EDITABLE
    }

    public interface OnEditorActionListener {
        boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent);
    }

    private static boolean isMultilineInputType(int i) {
        return (i & 131087) == 131073;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isPasswordInputType(int i) {
        int i2 = i & 4095;
        return i2 == 129 || i2 == 225 || i2 == 18;
    }

    private static boolean isVisiblePasswordInputType(int i) {
        return (i & 4095) == 145;
    }

    protected boolean getDefaultEditable() {
        return false;
    }

    protected MovementMethod getDefaultMovementMethod() {
        return null;
    }

    public int getHorizontalOffsetForDrawables() {
        return 0;
    }

    @Override // android.view.View
    public boolean isAccessibilitySelectionExtendable() {
        return true;
    }

    public void onBeginBatchEdit() {
    }

    public void onCommitCompletion(CompletionInfo completionInfo) {
    }

    public void onEndBatchEdit() {
    }

    public boolean onPrivateIMECommand(String str, Bundle bundle) {
        return false;
    }

    protected void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    static {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.measureText("H");
        UNKNOWN_BORING = new BoringLayout.Metrics();
    }

    static class Drawables {
        static final int DRAWABLE_LEFT = 1;
        static final int DRAWABLE_NONE = -1;
        static final int DRAWABLE_RIGHT = 0;
        Drawable mDrawableBottom;
        Drawable mDrawableEnd;
        Drawable mDrawableError;
        int mDrawableHeightEnd;
        int mDrawableHeightError;
        int mDrawableHeightLeft;
        int mDrawableHeightRight;
        int mDrawableHeightStart;
        int mDrawableHeightTemp;
        Drawable mDrawableLeft;
        Drawable mDrawableLeftInitial;
        int mDrawablePadding;
        Drawable mDrawableRight;
        Drawable mDrawableRightInitial;
        int mDrawableSizeBottom;
        int mDrawableSizeEnd;
        int mDrawableSizeError;
        int mDrawableSizeLeft;
        int mDrawableSizeRight;
        int mDrawableSizeStart;
        int mDrawableSizeTemp;
        int mDrawableSizeTop;
        Drawable mDrawableStart;
        Drawable mDrawableTemp;
        Drawable mDrawableTop;
        int mDrawableWidthBottom;
        int mDrawableWidthTop;
        boolean mIsRtlCompatibilityMode;
        boolean mOverride;
        final Rect mCompoundRect = new Rect();
        int mDrawableSaved = -1;

        public Drawables(Context context) {
            this.mIsRtlCompatibilityMode = context.getApplicationInfo().targetSdkVersion < 17 || !context.getApplicationInfo().hasRtlSupport();
            this.mOverride = false;
        }

        public void resolveWithLayoutDirection(int i) {
            Drawable drawable = this.mDrawableLeftInitial;
            this.mDrawableLeft = drawable;
            Drawable drawable2 = this.mDrawableRightInitial;
            this.mDrawableRight = drawable2;
            if (this.mIsRtlCompatibilityMode) {
                Drawable drawable3 = this.mDrawableStart;
                if (drawable3 != null && drawable == null) {
                    this.mDrawableLeft = drawable3;
                    this.mDrawableSizeLeft = this.mDrawableSizeStart;
                    this.mDrawableHeightLeft = this.mDrawableHeightStart;
                }
                Drawable drawable4 = this.mDrawableEnd;
                if (drawable4 != null && drawable2 == null) {
                    this.mDrawableRight = drawable4;
                    this.mDrawableSizeRight = this.mDrawableSizeEnd;
                    this.mDrawableHeightRight = this.mDrawableHeightEnd;
                }
            } else if (i == 1) {
                if (this.mOverride) {
                    this.mDrawableRight = this.mDrawableStart;
                    this.mDrawableSizeRight = this.mDrawableSizeStart;
                    this.mDrawableHeightRight = this.mDrawableHeightStart;
                    this.mDrawableLeft = this.mDrawableEnd;
                    this.mDrawableSizeLeft = this.mDrawableSizeEnd;
                    this.mDrawableHeightLeft = this.mDrawableHeightEnd;
                }
            } else if (this.mOverride) {
                this.mDrawableLeft = this.mDrawableStart;
                this.mDrawableSizeLeft = this.mDrawableSizeStart;
                this.mDrawableHeightLeft = this.mDrawableHeightStart;
                this.mDrawableRight = this.mDrawableEnd;
                this.mDrawableSizeRight = this.mDrawableSizeEnd;
                this.mDrawableHeightRight = this.mDrawableHeightEnd;
            }
            applyErrorDrawableIfNeeded(i);
            updateDrawablesLayoutDirection(i);
        }

        private void updateDrawablesLayoutDirection(int i) {
            Drawable drawable = this.mDrawableLeft;
            if (drawable != null) {
                drawable.setLayoutDirection(i);
            }
            Drawable drawable2 = this.mDrawableRight;
            if (drawable2 != null) {
                drawable2.setLayoutDirection(i);
            }
            Drawable drawable3 = this.mDrawableTop;
            if (drawable3 != null) {
                drawable3.setLayoutDirection(i);
            }
            Drawable drawable4 = this.mDrawableBottom;
            if (drawable4 != null) {
                drawable4.setLayoutDirection(i);
            }
        }

        public void setErrorDrawable(Drawable drawable, TextView textView) {
            Drawable drawable2 = this.mDrawableError;
            if (drawable2 != drawable && drawable2 != null) {
                drawable2.setCallback(null);
            }
            this.mDrawableError = drawable;
            Rect rect = this.mCompoundRect;
            int[] drawableState = textView.getDrawableState();
            Drawable drawable3 = this.mDrawableError;
            if (drawable3 != null) {
                drawable3.setState(drawableState);
                this.mDrawableError.copyBounds(rect);
                this.mDrawableError.setCallback(textView);
                this.mDrawableSizeError = rect.width();
                this.mDrawableHeightError = rect.height();
                return;
            }
            this.mDrawableHeightError = 0;
            this.mDrawableSizeError = 0;
        }

        private void applyErrorDrawableIfNeeded(int i) {
            int i2 = this.mDrawableSaved;
            if (i2 == 0) {
                this.mDrawableRight = this.mDrawableTemp;
                this.mDrawableSizeRight = this.mDrawableSizeTemp;
                this.mDrawableHeightRight = this.mDrawableHeightTemp;
            } else if (i2 == 1) {
                this.mDrawableLeft = this.mDrawableTemp;
                this.mDrawableSizeLeft = this.mDrawableSizeTemp;
                this.mDrawableHeightLeft = this.mDrawableHeightTemp;
            }
            Drawable drawable = this.mDrawableError;
            if (drawable != null) {
                if (i == 1) {
                    this.mDrawableSaved = 1;
                    this.mDrawableTemp = this.mDrawableLeft;
                    this.mDrawableSizeTemp = this.mDrawableSizeLeft;
                    this.mDrawableHeightTemp = this.mDrawableHeightLeft;
                    this.mDrawableLeft = drawable;
                    this.mDrawableSizeLeft = this.mDrawableSizeError;
                    this.mDrawableHeightLeft = this.mDrawableHeightError;
                    return;
                }
                this.mDrawableSaved = 0;
                this.mDrawableTemp = this.mDrawableRight;
                this.mDrawableSizeTemp = this.mDrawableSizeRight;
                this.mDrawableHeightTemp = this.mDrawableHeightRight;
                this.mDrawableRight = drawable;
                this.mDrawableSizeRight = this.mDrawableSizeError;
                this.mDrawableHeightRight = this.mDrawableHeightError;
            }
        }
    }

    public TextView(Context context) {
        this(context, null);
    }

    public TextView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.textViewStyle);
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    /* JADX WARN: Removed duplicated region for block: B:221:0x0756  */
    /* JADX WARN: Removed duplicated region for block: B:223:0x075b  */
    /* JADX WARN: Removed duplicated region for block: B:228:0x0786  */
    /* JADX WARN: Removed duplicated region for block: B:232:0x0790  */
    /* JADX WARN: Removed duplicated region for block: B:235:0x0794  */
    /* JADX WARN: Removed duplicated region for block: B:249:0x07c8  */
    /* JADX WARN: Removed duplicated region for block: B:252:0x07d1  */
    /* JADX WARN: Removed duplicated region for block: B:255:0x07e8  */
    /* JADX WARN: Removed duplicated region for block: B:258:0x07f5  */
    /* JADX WARN: Removed duplicated region for block: B:260:0x0803 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:270:0x081e  */
    /* JADX WARN: Removed duplicated region for block: B:274:0x0831  */
    /* JADX WARN: Removed duplicated region for block: B:276:0x083c  */
    /* JADX WARN: Removed duplicated region for block: B:277:0x084d  */
    /* JADX WARN: Removed duplicated region for block: B:280:0x085b  */
    /* JADX WARN: Removed duplicated region for block: B:283:0x086f  */
    /* JADX WARN: Removed duplicated region for block: B:287:0x0878  */
    /* JADX WARN: Removed duplicated region for block: B:290:0x0882  */
    /* JADX WARN: Removed duplicated region for block: B:303:0x08b4  */
    /* JADX WARN: Removed duplicated region for block: B:306:0x08bd  */
    /* JADX WARN: Removed duplicated region for block: B:340:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public TextView(android.content.Context r49, android.util.AttributeSet r50, int r51) {
        /*
            Method dump skipped, instruction units count: 2428
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.widget.TextView.<init>(android.content.Context, android.util.AttributeSet, int):void");
    }

    private void setTypefaceFromAttrs(String str, int i, int i2) {
        Typeface typefaceCreate;
        if (str != null) {
            typefaceCreate = Typeface.create(str, i2);
            if (typefaceCreate != null) {
                setTypeface(typefaceCreate);
                return;
            }
        } else {
            typefaceCreate = null;
        }
        if (i == 1) {
            typefaceCreate = Typeface.SANS_SERIF;
        } else if (i == 2) {
            typefaceCreate = Typeface.SERIF;
        } else if (i == 3) {
            typefaceCreate = Typeface.MONOSPACE;
        }
        setTypeface(typefaceCreate, i2);
    }

    private void setRelativeDrawablesIfNeeded(Drawable drawable, Drawable drawable2) {
        if ((drawable == null && drawable2 == null) ? false : true) {
            Drawables drawables = this.mDrawables;
            if (drawables == null) {
                drawables = new Drawables(getContext());
                this.mDrawables = drawables;
            }
            this.mDrawables.mOverride = true;
            Rect rect = drawables.mCompoundRect;
            int[] drawableState = getDrawableState();
            if (drawable != null) {
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                drawable.setState(drawableState);
                drawable.copyBounds(rect);
                drawable.setCallback(this);
                drawables.mDrawableStart = drawable;
                drawables.mDrawableSizeStart = rect.width();
                drawables.mDrawableHeightStart = rect.height();
            } else {
                drawables.mDrawableHeightStart = 0;
                drawables.mDrawableSizeStart = 0;
            }
            if (drawable2 != null) {
                drawable2.setBounds(0, 0, drawable2.getIntrinsicWidth(), drawable2.getIntrinsicHeight());
                drawable2.setState(drawableState);
                drawable2.copyBounds(rect);
                drawable2.setCallback(this);
                drawables.mDrawableEnd = drawable2;
                drawables.mDrawableSizeEnd = rect.width();
                drawables.mDrawableHeightEnd = rect.height();
            } else {
                drawables.mDrawableHeightEnd = 0;
                drawables.mDrawableSizeEnd = 0;
            }
            resetResolvedDrawables();
            resolveDrawables();
        }
    }

    @Override // android.view.View
    public void setEnabled(boolean z) {
        InputMethodManager inputMethodManagerPeekInstance;
        InputMethodManager inputMethodManagerPeekInstance2;
        if (z == isEnabled()) {
            return;
        }
        if (!z && (inputMethodManagerPeekInstance2 = InputMethodManager.peekInstance()) != null && inputMethodManagerPeekInstance2.isActive(this)) {
            inputMethodManagerPeekInstance2.hideSoftInputFromWindow(getWindowToken(), 0);
        }
        super.setEnabled(z);
        if (z && (inputMethodManagerPeekInstance = InputMethodManager.peekInstance()) != null) {
            inputMethodManagerPeekInstance.restartInput(this);
        }
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.invalidateTextDisplayList();
            this.mEditor.prepareCursorControllers();
            this.mEditor.makeBlink();
        }
    }

    public void setTypeface(Typeface typeface, int i) {
        Typeface typefaceCreate;
        if (i > 0) {
            if (typeface == null) {
                typefaceCreate = Typeface.defaultFromStyle(i);
            } else {
                typefaceCreate = Typeface.create(typeface, i);
            }
            setTypeface(typefaceCreate);
            int i2 = (~(typefaceCreate != null ? typefaceCreate.getStyle() : 0)) & i;
            this.mTextPaint.setFakeBoldText((i2 & 1) != 0);
            this.mTextPaint.setTextSkewX((i2 & 2) != 0 ? -0.25f : 0.0f);
            return;
        }
        this.mTextPaint.setFakeBoldText(false);
        this.mTextPaint.setTextSkewX(0.0f);
        setTypeface(typeface);
    }

    @ViewDebug.CapturedViewProperty
    public CharSequence getText() {
        return this.mText;
    }

    public int length() {
        return this.mText.length();
    }

    public Editable getEditableText() {
        CharSequence charSequence = this.mText;
        if (charSequence instanceof Editable) {
            return (Editable) charSequence;
        }
        return null;
    }

    public int getLineHeight() {
        return FastMath.round((this.mTextPaint.getFontMetricsInt(null) * this.mSpacingMult) + this.mSpacingAdd);
    }

    public final Layout getLayout() {
        return this.mLayout;
    }

    final Layout getHintLayout() {
        return this.mHintLayout;
    }

    public final UndoManager getUndoManager() {
        Editor editor = this.mEditor;
        if (editor == null) {
            return null;
        }
        return editor.mUndoManager;
    }

    public final void setUndoManager(UndoManager undoManager, String str) {
        if (undoManager != null) {
            createEditorIfNeeded();
            this.mEditor.mUndoManager = undoManager;
            this.mEditor.mUndoOwner = undoManager.getOwner(str, this);
            this.mEditor.mUndoInputFilter = new Editor.UndoInputFilter(this.mEditor);
            CharSequence charSequence = this.mText;
            if (!(charSequence instanceof Editable)) {
                setText(charSequence, BufferType.EDITABLE);
            }
            setFilters((Editable) this.mText, this.mFilters);
            return;
        }
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.mUndoManager = null;
            this.mEditor.mUndoOwner = null;
            this.mEditor.mUndoInputFilter = null;
        }
    }

    public final KeyListener getKeyListener() {
        Editor editor = this.mEditor;
        if (editor == null) {
            return null;
        }
        return editor.mKeyListener;
    }

    public void setKeyListener(KeyListener keyListener) {
        setKeyListenerOnly(keyListener);
        fixFocusableAndClickableSettings();
        if (keyListener != null) {
            createEditorIfNeeded();
            try {
                Editor editor = this.mEditor;
                editor.mInputType = editor.mKeyListener.getInputType();
            } catch (IncompatibleClassChangeError unused) {
                this.mEditor.mInputType = 1;
            }
            setInputTypeSingleLine(this.mSingleLine);
        } else {
            Editor editor2 = this.mEditor;
            if (editor2 != null) {
                editor2.mInputType = 0;
            }
        }
        InputMethodManager inputMethodManagerPeekInstance = InputMethodManager.peekInstance();
        if (inputMethodManagerPeekInstance != null) {
            inputMethodManagerPeekInstance.restartInput(this);
        }
    }

    private void setKeyListenerOnly(KeyListener keyListener) {
        if (this.mEditor == null && keyListener == null) {
            return;
        }
        createEditorIfNeeded();
        if (this.mEditor.mKeyListener != keyListener) {
            this.mEditor.mKeyListener = keyListener;
            if (keyListener != null) {
                CharSequence charSequence = this.mText;
                if (!(charSequence instanceof Editable)) {
                    setText(charSequence);
                }
            }
            setFilters((Editable) this.mText, this.mFilters);
        }
    }

    public final MovementMethod getMovementMethod() {
        return this.mMovement;
    }

    public final void setMovementMethod(MovementMethod movementMethod) {
        if (this.mMovement != movementMethod) {
            this.mMovement = movementMethod;
            if (movementMethod != null) {
                CharSequence charSequence = this.mText;
                if (!(charSequence instanceof Spannable)) {
                    setText(charSequence);
                }
            }
            fixFocusableAndClickableSettings();
            Editor editor = this.mEditor;
            if (editor != null) {
                editor.prepareCursorControllers();
            }
        }
    }

    private void fixFocusableAndClickableSettings() {
        Editor editor;
        if (this.mMovement != null || ((editor = this.mEditor) != null && editor.mKeyListener != null)) {
            setFocusable(true);
            setClickable(true);
            setLongClickable(true);
        } else {
            setFocusable(false);
            setClickable(false);
            setLongClickable(false);
        }
    }

    public final TransformationMethod getTransformationMethod() {
        return this.mTransformation;
    }

    public final void setTransformationMethod(TransformationMethod transformationMethod) {
        TransformationMethod transformationMethod2 = this.mTransformation;
        if (transformationMethod == transformationMethod2) {
            return;
        }
        if (transformationMethod2 != null) {
            CharSequence charSequence = this.mText;
            if (charSequence instanceof Spannable) {
                ((Spannable) charSequence).removeSpan(transformationMethod2);
            }
        }
        this.mTransformation = transformationMethod;
        if (transformationMethod instanceof TransformationMethod2) {
            TransformationMethod2 transformationMethod22 = (TransformationMethod2) transformationMethod;
            boolean z = (isTextSelectable() || (this.mText instanceof Editable)) ? false : true;
            this.mAllowTransformationLengthChange = z;
            transformationMethod22.setLengthChangesAllowed(z);
        } else {
            this.mAllowTransformationLengthChange = false;
        }
        setText(this.mText);
        if (hasPasswordTransformationMethod()) {
            notifyViewAccessibilityStateChangedIfNeeded(0);
        }
    }

    public int getCompoundPaddingTop() {
        Drawables drawables = this.mDrawables;
        if (drawables == null || drawables.mDrawableTop == null) {
            return this.mPaddingTop;
        }
        return this.mPaddingTop + drawables.mDrawablePadding + drawables.mDrawableSizeTop;
    }

    public int getCompoundPaddingBottom() {
        Drawables drawables = this.mDrawables;
        if (drawables == null || drawables.mDrawableBottom == null) {
            return this.mPaddingBottom;
        }
        return this.mPaddingBottom + drawables.mDrawablePadding + drawables.mDrawableSizeBottom;
    }

    public int getCompoundPaddingLeft() {
        Drawables drawables = this.mDrawables;
        if (drawables == null || drawables.mDrawableLeft == null) {
            return this.mPaddingLeft;
        }
        return this.mPaddingLeft + drawables.mDrawablePadding + drawables.mDrawableSizeLeft;
    }

    public int getCompoundPaddingRight() {
        Drawables drawables = this.mDrawables;
        if (drawables == null || drawables.mDrawableRight == null) {
            return this.mPaddingRight;
        }
        return this.mPaddingRight + drawables.mDrawablePadding + drawables.mDrawableSizeRight;
    }

    public int getCompoundPaddingStart() {
        resolveDrawables();
        if (getLayoutDirection() != 1) {
            return getCompoundPaddingLeft();
        }
        return getCompoundPaddingRight();
    }

    public int getCompoundPaddingEnd() {
        resolveDrawables();
        if (getLayoutDirection() != 1) {
            return getCompoundPaddingRight();
        }
        return getCompoundPaddingLeft();
    }

    public int getExtendedPaddingTop() {
        int i;
        if (this.mMaxMode != 1) {
            return getCompoundPaddingTop();
        }
        if (this.mLayout.getLineCount() <= this.mMaximum) {
            return getCompoundPaddingTop();
        }
        int compoundPaddingTop = getCompoundPaddingTop();
        int height = (getHeight() - compoundPaddingTop) - getCompoundPaddingBottom();
        int lineTop = this.mLayout.getLineTop(this.mMaximum);
        return (lineTop < height && (i = this.mGravity & 112) != 48) ? i == 80 ? (compoundPaddingTop + height) - lineTop : compoundPaddingTop + ((height - lineTop) / 2) : compoundPaddingTop;
    }

    public int getExtendedPaddingBottom() {
        if (this.mMaxMode != 1) {
            return getCompoundPaddingBottom();
        }
        if (this.mLayout.getLineCount() <= this.mMaximum) {
            return getCompoundPaddingBottom();
        }
        int compoundPaddingTop = getCompoundPaddingTop();
        int compoundPaddingBottom = getCompoundPaddingBottom();
        int height = (getHeight() - compoundPaddingTop) - compoundPaddingBottom;
        int lineTop = this.mLayout.getLineTop(this.mMaximum);
        if (lineTop >= height) {
            return compoundPaddingBottom;
        }
        int i = this.mGravity & 112;
        return i == 48 ? (compoundPaddingBottom + height) - lineTop : i == 80 ? compoundPaddingBottom : compoundPaddingBottom + ((height - lineTop) / 2);
    }

    public int getTotalPaddingLeft() {
        return getCompoundPaddingLeft();
    }

    public int getTotalPaddingRight() {
        return getCompoundPaddingRight();
    }

    public int getTotalPaddingStart() {
        return getCompoundPaddingStart();
    }

    public int getTotalPaddingEnd() {
        return getCompoundPaddingEnd();
    }

    public int getTotalPaddingTop() {
        return getExtendedPaddingTop() + getVerticalOffset(true);
    }

    public int getTotalPaddingBottom() {
        return getExtendedPaddingBottom() + getBottomVerticalOffset(true);
    }

    public void setCompoundDrawables(Drawable drawable, Drawable drawable2, Drawable drawable3, Drawable drawable4) {
        Drawables drawables = this.mDrawables;
        if ((drawable == null && drawable2 == null && drawable3 == null && drawable4 == null) ? false : true) {
            if (drawables == null) {
                drawables = new Drawables(getContext());
                this.mDrawables = drawables;
            }
            this.mDrawables.mOverride = false;
            if (drawables.mDrawableLeft != drawable && drawables.mDrawableLeft != null) {
                drawables.mDrawableLeft.setCallback(null);
            }
            drawables.mDrawableLeft = drawable;
            if (drawables.mDrawableTop != drawable2 && drawables.mDrawableTop != null) {
                drawables.mDrawableTop.setCallback(null);
            }
            drawables.mDrawableTop = drawable2;
            if (drawables.mDrawableRight != drawable3 && drawables.mDrawableRight != null) {
                drawables.mDrawableRight.setCallback(null);
            }
            drawables.mDrawableRight = drawable3;
            if (drawables.mDrawableBottom != drawable4 && drawables.mDrawableBottom != null) {
                drawables.mDrawableBottom.setCallback(null);
            }
            drawables.mDrawableBottom = drawable4;
            Rect rect = drawables.mCompoundRect;
            int[] drawableState = getDrawableState();
            if (drawable != null) {
                drawable.setState(drawableState);
                drawable.copyBounds(rect);
                drawable.setCallback(this);
                drawables.mDrawableSizeLeft = rect.width();
                drawables.mDrawableHeightLeft = rect.height();
            } else {
                drawables.mDrawableHeightLeft = 0;
                drawables.mDrawableSizeLeft = 0;
            }
            if (drawable3 != null) {
                drawable3.setState(drawableState);
                drawable3.copyBounds(rect);
                drawable3.setCallback(this);
                drawables.mDrawableSizeRight = rect.width();
                drawables.mDrawableHeightRight = rect.height();
            } else {
                drawables.mDrawableHeightRight = 0;
                drawables.mDrawableSizeRight = 0;
            }
            if (drawable2 != null) {
                drawable2.setState(drawableState);
                drawable2.copyBounds(rect);
                drawable2.setCallback(this);
                drawables.mDrawableSizeTop = rect.height();
                drawables.mDrawableWidthTop = rect.width();
            } else {
                drawables.mDrawableWidthTop = 0;
                drawables.mDrawableSizeTop = 0;
            }
            if (drawable4 != null) {
                drawable4.setState(drawableState);
                drawable4.copyBounds(rect);
                drawable4.setCallback(this);
                drawables.mDrawableSizeBottom = rect.height();
                drawables.mDrawableWidthBottom = rect.width();
            } else {
                drawables.mDrawableWidthBottom = 0;
                drawables.mDrawableSizeBottom = 0;
            }
        } else if (drawables != null) {
            if (drawables.mDrawablePadding == 0) {
                this.mDrawables = null;
            } else {
                if (drawables.mDrawableLeft != null) {
                    drawables.mDrawableLeft.setCallback(null);
                }
                drawables.mDrawableLeft = null;
                if (drawables.mDrawableTop != null) {
                    drawables.mDrawableTop.setCallback(null);
                }
                drawables.mDrawableTop = null;
                if (drawables.mDrawableRight != null) {
                    drawables.mDrawableRight.setCallback(null);
                }
                drawables.mDrawableRight = null;
                if (drawables.mDrawableBottom != null) {
                    drawables.mDrawableBottom.setCallback(null);
                }
                drawables.mDrawableBottom = null;
                drawables.mDrawableHeightLeft = 0;
                drawables.mDrawableSizeLeft = 0;
                drawables.mDrawableHeightRight = 0;
                drawables.mDrawableSizeRight = 0;
                drawables.mDrawableWidthTop = 0;
                drawables.mDrawableSizeTop = 0;
                drawables.mDrawableWidthBottom = 0;
                drawables.mDrawableSizeBottom = 0;
            }
        }
        if (drawables != null) {
            drawables.mDrawableLeftInitial = drawable;
            drawables.mDrawableRightInitial = drawable3;
        }
        resetResolvedDrawables();
        resolveDrawables();
        invalidate();
        requestLayout();
    }

    @RemotableViewMethod
    public void setCompoundDrawablesWithIntrinsicBounds(int i, int i2, int i3, int i4) {
        Resources resources = getContext().getResources();
        setCompoundDrawablesWithIntrinsicBounds(i != 0 ? resources.getDrawable(i) : null, i2 != 0 ? resources.getDrawable(i2) : null, i3 != 0 ? resources.getDrawable(i3) : null, i4 != 0 ? resources.getDrawable(i4) : null);
    }

    public void setCompoundDrawablesWithIntrinsicBounds(Drawable drawable, Drawable drawable2, Drawable drawable3, Drawable drawable4) {
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        }
        if (drawable3 != null) {
            drawable3.setBounds(0, 0, drawable3.getIntrinsicWidth(), drawable3.getIntrinsicHeight());
        }
        if (drawable2 != null) {
            drawable2.setBounds(0, 0, drawable2.getIntrinsicWidth(), drawable2.getIntrinsicHeight());
        }
        if (drawable4 != null) {
            drawable4.setBounds(0, 0, drawable4.getIntrinsicWidth(), drawable4.getIntrinsicHeight());
        }
        setCompoundDrawables(drawable, drawable2, drawable3, drawable4);
    }

    public void setCompoundDrawablesRelative(Drawable drawable, Drawable drawable2, Drawable drawable3, Drawable drawable4) {
        Drawables drawables = this.mDrawables;
        if ((drawable == null && drawable2 == null && drawable3 == null && drawable4 == null) ? false : true) {
            if (drawables == null) {
                drawables = new Drawables(getContext());
                this.mDrawables = drawables;
            }
            this.mDrawables.mOverride = true;
            if (drawables.mDrawableStart != drawable && drawables.mDrawableStart != null) {
                drawables.mDrawableStart.setCallback(null);
            }
            drawables.mDrawableStart = drawable;
            if (drawables.mDrawableTop != drawable2 && drawables.mDrawableTop != null) {
                drawables.mDrawableTop.setCallback(null);
            }
            drawables.mDrawableTop = drawable2;
            if (drawables.mDrawableEnd != drawable3 && drawables.mDrawableEnd != null) {
                drawables.mDrawableEnd.setCallback(null);
            }
            drawables.mDrawableEnd = drawable3;
            if (drawables.mDrawableBottom != drawable4 && drawables.mDrawableBottom != null) {
                drawables.mDrawableBottom.setCallback(null);
            }
            drawables.mDrawableBottom = drawable4;
            Rect rect = drawables.mCompoundRect;
            int[] drawableState = getDrawableState();
            if (drawable != null) {
                drawable.setState(drawableState);
                drawable.copyBounds(rect);
                drawable.setCallback(this);
                drawables.mDrawableSizeStart = rect.width();
                drawables.mDrawableHeightStart = rect.height();
            } else {
                drawables.mDrawableHeightStart = 0;
                drawables.mDrawableSizeStart = 0;
            }
            if (drawable3 != null) {
                drawable3.setState(drawableState);
                drawable3.copyBounds(rect);
                drawable3.setCallback(this);
                drawables.mDrawableSizeEnd = rect.width();
                drawables.mDrawableHeightEnd = rect.height();
            } else {
                drawables.mDrawableHeightEnd = 0;
                drawables.mDrawableSizeEnd = 0;
            }
            if (drawable2 != null) {
                drawable2.setState(drawableState);
                drawable2.copyBounds(rect);
                drawable2.setCallback(this);
                drawables.mDrawableSizeTop = rect.height();
                drawables.mDrawableWidthTop = rect.width();
            } else {
                drawables.mDrawableWidthTop = 0;
                drawables.mDrawableSizeTop = 0;
            }
            if (drawable4 != null) {
                drawable4.setState(drawableState);
                drawable4.copyBounds(rect);
                drawable4.setCallback(this);
                drawables.mDrawableSizeBottom = rect.height();
                drawables.mDrawableWidthBottom = rect.width();
            } else {
                drawables.mDrawableWidthBottom = 0;
                drawables.mDrawableSizeBottom = 0;
            }
        } else if (drawables != null) {
            if (drawables.mDrawablePadding == 0) {
                this.mDrawables = null;
            } else {
                if (drawables.mDrawableStart != null) {
                    drawables.mDrawableStart.setCallback(null);
                }
                drawables.mDrawableStart = null;
                if (drawables.mDrawableTop != null) {
                    drawables.mDrawableTop.setCallback(null);
                }
                drawables.mDrawableTop = null;
                if (drawables.mDrawableEnd != null) {
                    drawables.mDrawableEnd.setCallback(null);
                }
                drawables.mDrawableEnd = null;
                if (drawables.mDrawableBottom != null) {
                    drawables.mDrawableBottom.setCallback(null);
                }
                drawables.mDrawableBottom = null;
                drawables.mDrawableHeightStart = 0;
                drawables.mDrawableSizeStart = 0;
                drawables.mDrawableHeightEnd = 0;
                drawables.mDrawableSizeEnd = 0;
                drawables.mDrawableWidthTop = 0;
                drawables.mDrawableSizeTop = 0;
                drawables.mDrawableWidthBottom = 0;
                drawables.mDrawableSizeBottom = 0;
            }
        }
        resetResolvedDrawables();
        resolveDrawables();
        invalidate();
        requestLayout();
    }

    @RemotableViewMethod
    public void setCompoundDrawablesRelativeWithIntrinsicBounds(int i, int i2, int i3, int i4) {
        Resources resources = getContext().getResources();
        setCompoundDrawablesRelativeWithIntrinsicBounds(i != 0 ? resources.getDrawable(i) : null, i2 != 0 ? resources.getDrawable(i2) : null, i3 != 0 ? resources.getDrawable(i3) : null, i4 != 0 ? resources.getDrawable(i4) : null);
    }

    public void setCompoundDrawablesRelativeWithIntrinsicBounds(Drawable drawable, Drawable drawable2, Drawable drawable3, Drawable drawable4) {
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        }
        if (drawable3 != null) {
            drawable3.setBounds(0, 0, drawable3.getIntrinsicWidth(), drawable3.getIntrinsicHeight());
        }
        if (drawable2 != null) {
            drawable2.setBounds(0, 0, drawable2.getIntrinsicWidth(), drawable2.getIntrinsicHeight());
        }
        if (drawable4 != null) {
            drawable4.setBounds(0, 0, drawable4.getIntrinsicWidth(), drawable4.getIntrinsicHeight());
        }
        setCompoundDrawablesRelative(drawable, drawable2, drawable3, drawable4);
    }

    public Drawable[] getCompoundDrawables() {
        Drawables drawables = this.mDrawables;
        return drawables != null ? new Drawable[]{drawables.mDrawableLeft, drawables.mDrawableTop, drawables.mDrawableRight, drawables.mDrawableBottom} : new Drawable[]{null, null, null, null};
    }

    public Drawable[] getCompoundDrawablesRelative() {
        Drawables drawables = this.mDrawables;
        return drawables != null ? new Drawable[]{drawables.mDrawableStart, drawables.mDrawableTop, drawables.mDrawableEnd, drawables.mDrawableBottom} : new Drawable[]{null, null, null, null};
    }

    @RemotableViewMethod
    public void setCompoundDrawablePadding(int i) {
        Drawables drawables = this.mDrawables;
        if (i != 0) {
            if (drawables == null) {
                drawables = new Drawables(getContext());
                this.mDrawables = drawables;
            }
            drawables.mDrawablePadding = i;
        } else if (drawables != null) {
            drawables.mDrawablePadding = i;
        }
        invalidate();
        requestLayout();
    }

    public int getCompoundDrawablePadding() {
        Drawables drawables = this.mDrawables;
        if (drawables != null) {
            return drawables.mDrawablePadding;
        }
        return 0;
    }

    @Override // android.view.View
    public void setPadding(int i, int i2, int i3, int i4) {
        if (i != this.mPaddingLeft || i3 != this.mPaddingRight || i2 != this.mPaddingTop || i4 != this.mPaddingBottom) {
            nullLayouts();
        }
        super.setPadding(i, i2, i3, i4);
        invalidate();
    }

    @Override // android.view.View
    public void setPaddingRelative(int i, int i2, int i3, int i4) {
        if (i != getPaddingStart() || i3 != getPaddingEnd() || i2 != this.mPaddingTop || i4 != this.mPaddingBottom) {
            nullLayouts();
        }
        super.setPaddingRelative(i, i2, i3, i4);
        invalidate();
    }

    public final int getAutoLinkMask() {
        return this.mAutoLinkMask;
    }

    public void setTextAppearance(Context context, int i) {
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(i, com.android.internal.R.styleable.TextAppearance);
        int color = typedArrayObtainStyledAttributes.getColor(4, 0);
        if (color != 0) {
            setHighlightColor(color);
        }
        ColorStateList colorStateList = typedArrayObtainStyledAttributes.getColorStateList(3);
        if (colorStateList != null) {
            setTextColor(colorStateList);
        }
        int dimensionPixelSize = typedArrayObtainStyledAttributes.getDimensionPixelSize(0, 0);
        if (dimensionPixelSize != 0) {
            setRawTextSize(dimensionPixelSize);
        }
        ColorStateList colorStateList2 = typedArrayObtainStyledAttributes.getColorStateList(5);
        if (colorStateList2 != null) {
            setHintTextColor(colorStateList2);
        }
        ColorStateList colorStateList3 = typedArrayObtainStyledAttributes.getColorStateList(6);
        if (colorStateList3 != null) {
            setLinkTextColor(colorStateList3);
        }
        setTypefaceFromAttrs(typedArrayObtainStyledAttributes.getString(12), typedArrayObtainStyledAttributes.getInt(1, -1), typedArrayObtainStyledAttributes.getInt(2, -1));
        int i2 = typedArrayObtainStyledAttributes.getInt(7, 0);
        if (i2 != 0) {
            setShadowLayer(typedArrayObtainStyledAttributes.getFloat(10, 0.0f), typedArrayObtainStyledAttributes.getFloat(8, 0.0f), typedArrayObtainStyledAttributes.getFloat(9, 0.0f), i2);
        }
        if (typedArrayObtainStyledAttributes.getBoolean(11, false)) {
            setTransformationMethod(new AllCapsTransformationMethod(getContext()));
        }
        typedArrayObtainStyledAttributes.recycle();
    }

    public Locale getTextLocale() {
        return this.mTextPaint.getTextLocale();
    }

    public void setTextLocale(Locale locale) {
        this.mTextPaint.setTextLocale(locale);
    }

    @ViewDebug.ExportedProperty(category = "text")
    public float getTextSize() {
        return this.mTextPaint.getTextSize();
    }

    @RemotableViewMethod
    public void setTextSize(float f) {
        setTextSize(2, f);
    }

    public void setTextSize(int i, float f) {
        Resources resources;
        Context context = getContext();
        if (context == null) {
            resources = Resources.getSystem();
        } else {
            resources = context.getResources();
        }
        setRawTextSize(TypedValue.applyDimension(i, f, resources.getDisplayMetrics()));
    }

    private void setRawTextSize(float f) {
        if (f != this.mTextPaint.getTextSize()) {
            this.mTextPaint.setTextSize(f);
            if (this.mLayout != null) {
                nullLayouts();
                requestLayout();
                invalidate();
            }
        }
    }

    public float getTextScaleX() {
        return this.mTextPaint.getTextScaleX();
    }

    @RemotableViewMethod
    public void setTextScaleX(float f) {
        if (f != this.mTextPaint.getTextScaleX()) {
            this.mUserSetTextScaleX = true;
            this.mTextPaint.setTextScaleX(f);
            if (this.mLayout != null) {
                nullLayouts();
                requestLayout();
                invalidate();
            }
        }
    }

    public void setTypeface(Typeface typeface) {
        if (this.mTextPaint.getTypeface() != typeface) {
            this.mTextPaint.setTypeface(typeface);
            if (this.mLayout != null) {
                nullLayouts();
                requestLayout();
                invalidate();
            }
        }
    }

    public Typeface getTypeface() {
        return this.mTextPaint.getTypeface();
    }

    @RemotableViewMethod
    public void setTextColor(int i) {
        this.mTextColor = ColorStateList.valueOf(i);
        updateTextColors();
    }

    public void setTextColor(ColorStateList colorStateList) {
        Objects.requireNonNull(colorStateList);
        this.mTextColor = colorStateList;
        updateTextColors();
    }

    public final ColorStateList getTextColors() {
        return this.mTextColor;
    }

    public final int getCurrentTextColor() {
        return this.mCurTextColor;
    }

    @RemotableViewMethod
    public void setHighlightColor(int i) {
        if (this.mHighlightColor != i) {
            this.mHighlightColor = i;
            invalidate();
        }
    }

    public int getHighlightColor() {
        return this.mHighlightColor;
    }

    @RemotableViewMethod
    public final void setShowSoftInputOnFocus(boolean z) {
        createEditorIfNeeded();
        this.mEditor.mShowSoftInputOnFocus = z;
    }

    public final boolean getShowSoftInputOnFocus() {
        Editor editor = this.mEditor;
        return editor == null || editor.mShowSoftInputOnFocus;
    }

    public void setShadowLayer(float f, float f2, float f3, int i) {
        this.mTextPaint.setShadowLayer(f, f2, f3, i);
        this.mShadowRadius = f;
        this.mShadowDx = f2;
        this.mShadowDy = f3;
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.invalidateTextDisplayList();
        }
        invalidate();
    }

    public float getShadowRadius() {
        return this.mShadowRadius;
    }

    public float getShadowDx() {
        return this.mShadowDx;
    }

    public float getShadowDy() {
        return this.mShadowDy;
    }

    public int getShadowColor() {
        return this.mTextPaint.shadowColor;
    }

    public TextPaint getPaint() {
        return this.mTextPaint;
    }

    @RemotableViewMethod
    public final void setAutoLinkMask(int i) {
        this.mAutoLinkMask = i;
    }

    @RemotableViewMethod
    public final void setLinksClickable(boolean z) {
        this.mLinksClickable = z;
    }

    public final boolean getLinksClickable() {
        return this.mLinksClickable;
    }

    public URLSpan[] getUrls() {
        CharSequence charSequence = this.mText;
        return charSequence instanceof Spanned ? (URLSpan[]) ((Spanned) charSequence).getSpans(0, charSequence.length(), URLSpan.class) : new URLSpan[0];
    }

    @RemotableViewMethod
    public final void setHintTextColor(int i) {
        this.mHintTextColor = ColorStateList.valueOf(i);
        updateTextColors();
    }

    public final void setHintTextColor(ColorStateList colorStateList) {
        this.mHintTextColor = colorStateList;
        updateTextColors();
    }

    public final ColorStateList getHintTextColors() {
        return this.mHintTextColor;
    }

    public final int getCurrentHintTextColor() {
        return this.mHintTextColor != null ? this.mCurHintTextColor : this.mCurTextColor;
    }

    @RemotableViewMethod
    public final void setLinkTextColor(int i) {
        this.mLinkTextColor = ColorStateList.valueOf(i);
        updateTextColors();
    }

    public final void setLinkTextColor(ColorStateList colorStateList) {
        this.mLinkTextColor = colorStateList;
        updateTextColors();
    }

    public final ColorStateList getLinkTextColors() {
        return this.mLinkTextColor;
    }

    public void setGravity(int i) {
        if ((i & 8388615) == 0) {
            i |= 8388611;
        }
        if ((i & 112) == 0) {
            i |= 48;
        }
        int i2 = i & 8388615;
        int i3 = this.mGravity;
        boolean z = i2 != (8388615 & i3);
        if (i != i3) {
            invalidate();
        }
        this.mGravity = i;
        Layout layout = this.mLayout;
        if (layout == null || !z) {
            return;
        }
        int width = layout.getWidth();
        Layout layout2 = this.mHintLayout;
        int width2 = layout2 != null ? layout2.getWidth() : 0;
        BoringLayout.Metrics metrics = UNKNOWN_BORING;
        makeNewLayout(width, width2, metrics, metrics, ((this.mRight - this.mLeft) - getCompoundPaddingLeft()) - getCompoundPaddingRight(), true);
    }

    public int getGravity() {
        return this.mGravity;
    }

    public int getPaintFlags() {
        return this.mTextPaint.getFlags();
    }

    @RemotableViewMethod
    public void setPaintFlags(int i) {
        if (this.mTextPaint.getFlags() != i) {
            this.mTextPaint.setFlags(i);
            if (this.mLayout != null) {
                nullLayouts();
                requestLayout();
                invalidate();
            }
        }
    }

    public void setHorizontallyScrolling(boolean z) {
        if (this.mHorizontallyScrolling != z) {
            this.mHorizontallyScrolling = z;
            if (this.mLayout != null) {
                nullLayouts();
                requestLayout();
                invalidate();
            }
        }
    }

    public boolean getHorizontallyScrolling() {
        return this.mHorizontallyScrolling;
    }

    @RemotableViewMethod
    public void setMinLines(int i) {
        this.mMinimum = i;
        this.mMinMode = 1;
        requestLayout();
        invalidate();
    }

    public int getMinLines() {
        if (this.mMinMode == 1) {
            return this.mMinimum;
        }
        return -1;
    }

    @RemotableViewMethod
    public void setMinHeight(int i) {
        this.mMinimum = i;
        this.mMinMode = 2;
        requestLayout();
        invalidate();
    }

    public int getMinHeight() {
        if (this.mMinMode == 2) {
            return this.mMinimum;
        }
        return -1;
    }

    @RemotableViewMethod
    public void setMaxLines(int i) {
        this.mMaximum = i;
        this.mMaxMode = 1;
        requestLayout();
        invalidate();
    }

    public int getMaxLines() {
        if (this.mMaxMode == 1) {
            return this.mMaximum;
        }
        return -1;
    }

    @RemotableViewMethod
    public void setMaxHeight(int i) {
        this.mMaximum = i;
        this.mMaxMode = 2;
        requestLayout();
        invalidate();
    }

    public int getMaxHeight() {
        if (this.mMaxMode == 2) {
            return this.mMaximum;
        }
        return -1;
    }

    @RemotableViewMethod
    public void setLines(int i) {
        this.mMinimum = i;
        this.mMaximum = i;
        this.mMinMode = 1;
        this.mMaxMode = 1;
        requestLayout();
        invalidate();
    }

    @RemotableViewMethod
    public void setHeight(int i) {
        this.mMinimum = i;
        this.mMaximum = i;
        this.mMinMode = 2;
        this.mMaxMode = 2;
        requestLayout();
        invalidate();
    }

    @RemotableViewMethod
    public void setMinEms(int i) {
        this.mMinWidth = i;
        this.mMinWidthMode = 1;
        requestLayout();
        invalidate();
    }

    public int getMinEms() {
        if (this.mMinWidthMode == 1) {
            return this.mMinWidth;
        }
        return -1;
    }

    @RemotableViewMethod
    public void setMinWidth(int i) {
        this.mMinWidth = i;
        this.mMinWidthMode = 2;
        requestLayout();
        invalidate();
    }

    public int getMinWidth() {
        if (this.mMinWidthMode == 2) {
            return this.mMinWidth;
        }
        return -1;
    }

    @RemotableViewMethod
    public void setMaxEms(int i) {
        this.mMaxWidth = i;
        this.mMaxWidthMode = 1;
        requestLayout();
        invalidate();
    }

    public int getMaxEms() {
        if (this.mMaxWidthMode == 1) {
            return this.mMaxWidth;
        }
        return -1;
    }

    @RemotableViewMethod
    public void setMaxWidth(int i) {
        this.mMaxWidth = i;
        this.mMaxWidthMode = 2;
        requestLayout();
        invalidate();
    }

    public int getMaxWidth() {
        if (this.mMaxWidthMode == 2) {
            return this.mMaxWidth;
        }
        return -1;
    }

    @RemotableViewMethod
    public void setEms(int i) {
        this.mMinWidth = i;
        this.mMaxWidth = i;
        this.mMinWidthMode = 1;
        this.mMaxWidthMode = 1;
        requestLayout();
        invalidate();
    }

    @RemotableViewMethod
    public void setWidth(int i) {
        this.mMinWidth = i;
        this.mMaxWidth = i;
        this.mMinWidthMode = 2;
        this.mMaxWidthMode = 2;
        requestLayout();
        invalidate();
    }

    public void setLineSpacing(float f, float f2) {
        if (this.mSpacingAdd == f && this.mSpacingMult == f2) {
            return;
        }
        this.mSpacingAdd = f;
        this.mSpacingMult = f2;
        if (this.mLayout != null) {
            nullLayouts();
            requestLayout();
            invalidate();
        }
    }

    public float getLineSpacingMultiplier() {
        return this.mSpacingMult;
    }

    public float getLineSpacingExtra() {
        return this.mSpacingAdd;
    }

    public final void append(CharSequence charSequence) {
        append(charSequence, 0, charSequence.length());
    }

    public void append(CharSequence charSequence, int i, int i2) {
        CharSequence charSequence2 = this.mText;
        if (!(charSequence2 instanceof Editable)) {
            setText(charSequence2, BufferType.EDITABLE);
        }
        ((Editable) this.mText).append(charSequence, i, i2);
    }

    private void updateTextColors() {
        boolean z;
        int colorForState;
        int colorForState2;
        int colorForState3 = this.mTextColor.getColorForState(getDrawableState(), 0);
        boolean z2 = true;
        if (colorForState3 != this.mCurTextColor) {
            this.mCurTextColor = colorForState3;
            z = true;
        } else {
            z = false;
        }
        ColorStateList colorStateList = this.mLinkTextColor;
        if (colorStateList != null && (colorForState2 = colorStateList.getColorForState(getDrawableState(), 0)) != this.mTextPaint.linkColor) {
            this.mTextPaint.linkColor = colorForState2;
            z = true;
        }
        ColorStateList colorStateList2 = this.mHintTextColor;
        if (colorStateList2 == null || (colorForState = colorStateList2.getColorForState(getDrawableState(), 0)) == this.mCurHintTextColor || this.mText.length() != 0) {
            z2 = z;
        } else {
            this.mCurHintTextColor = colorForState;
        }
        if (z2) {
            Editor editor = this.mEditor;
            if (editor != null) {
                editor.invalidateTextDisplayList();
            }
            invalidate();
        }
    }

    @Override // android.view.View
    protected void drawableStateChanged() {
        ColorStateList colorStateList;
        ColorStateList colorStateList2;
        super.drawableStateChanged();
        ColorStateList colorStateList3 = this.mTextColor;
        if ((colorStateList3 != null && colorStateList3.isStateful()) || (((colorStateList = this.mHintTextColor) != null && colorStateList.isStateful()) || ((colorStateList2 = this.mLinkTextColor) != null && colorStateList2.isStateful()))) {
            updateTextColors();
        }
        Drawables drawables = this.mDrawables;
        if (drawables != null) {
            int[] drawableState = getDrawableState();
            if (drawables.mDrawableTop != null && drawables.mDrawableTop.isStateful()) {
                drawables.mDrawableTop.setState(drawableState);
            }
            if (drawables.mDrawableBottom != null && drawables.mDrawableBottom.isStateful()) {
                drawables.mDrawableBottom.setState(drawableState);
            }
            if (drawables.mDrawableLeft != null && drawables.mDrawableLeft.isStateful()) {
                drawables.mDrawableLeft.setState(drawableState);
            }
            if (drawables.mDrawableRight != null && drawables.mDrawableRight.isStateful()) {
                drawables.mDrawableRight.setState(drawableState);
            }
            if (drawables.mDrawableStart != null && drawables.mDrawableStart.isStateful()) {
                drawables.mDrawableStart.setState(drawableState);
            }
            if (drawables.mDrawableEnd == null || !drawables.mDrawableEnd.isStateful()) {
                return;
            }
            drawables.mDrawableEnd.setState(drawableState);
        }
    }

    @Override // android.view.View
    public Parcelable onSaveInstanceState() {
        int selectionEnd;
        Parcelable parcelableOnSaveInstanceState = super.onSaveInstanceState();
        boolean z = this.mFreezesText;
        int selectionStart = 0;
        if (this.mText != null) {
            selectionStart = getSelectionStart();
            selectionEnd = getSelectionEnd();
            if (selectionStart >= 0 || selectionEnd >= 0) {
                z = true;
            }
        } else {
            selectionEnd = 0;
        }
        if (!z) {
            return parcelableOnSaveInstanceState;
        }
        SavedState savedState = new SavedState(parcelableOnSaveInstanceState);
        savedState.selStart = selectionStart;
        savedState.selEnd = selectionEnd;
        CharSequence charSequence = this.mText;
        if (charSequence instanceof Spanned) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(this.mText);
            if (this.mEditor != null) {
                removeMisspelledSpans(spannableStringBuilder);
                spannableStringBuilder.removeSpan(this.mEditor.mSuggestionRangeSpan);
            }
            savedState.text = spannableStringBuilder;
        } else {
            savedState.text = charSequence.toString();
        }
        if (isFocused() && selectionStart >= 0 && selectionEnd >= 0) {
            savedState.frozenWithFocus = true;
        }
        savedState.error = getError();
        return savedState;
    }

    void removeMisspelledSpans(Spannable spannable) {
        SuggestionSpan[] suggestionSpanArr = (SuggestionSpan[]) spannable.getSpans(0, spannable.length(), SuggestionSpan.class);
        for (int i = 0; i < suggestionSpanArr.length; i++) {
            int flags = suggestionSpanArr[i].getFlags();
            if ((flags & 1) != 0 && (flags & 2) != 0) {
                spannable.removeSpan(suggestionSpanArr[i]);
            }
        }
    }

    @Override // android.view.View
    public void onRestoreInstanceState(Parcelable parcelable) {
        if (!(parcelable instanceof SavedState)) {
            super.onRestoreInstanceState(parcelable);
            return;
        }
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        if (savedState.text != null) {
            setText(savedState.text);
        }
        if (savedState.selStart >= 0 && savedState.selEnd >= 0) {
            CharSequence charSequence = this.mText;
            if (charSequence instanceof Spannable) {
                int length = charSequence.length();
                if (savedState.selStart > length || savedState.selEnd > length) {
                    Log.e(LOG_TAG, "Saved cursor position " + savedState.selStart + "/" + savedState.selEnd + " out of range for " + (savedState.text != null ? "(restored) " : "") + "text " + ((Object) this.mText));
                } else {
                    Selection.setSelection((Spannable) this.mText, savedState.selStart, savedState.selEnd);
                    if (savedState.frozenWithFocus) {
                        createEditorIfNeeded();
                        this.mEditor.mFrozenWithFocus = true;
                    }
                }
            }
        }
        if (savedState.error != null) {
            final CharSequence charSequence2 = savedState.error;
            post(new Runnable() { // from class: android.widget.TextView.1
                @Override // java.lang.Runnable
                public void run() {
                    TextView.this.setError(charSequence2);
                }
            });
        }
    }

    @RemotableViewMethod
    public void setFreezesText(boolean z) {
        this.mFreezesText = z;
    }

    public boolean getFreezesText() {
        return this.mFreezesText;
    }

    public final void setEditableFactory(Editable.Factory factory) {
        this.mEditableFactory = factory;
        setText(this.mText);
    }

    public final void setSpannableFactory(Spannable.Factory factory) {
        this.mSpannableFactory = factory;
        setText(this.mText);
    }

    @RemotableViewMethod
    public final void setText(CharSequence charSequence) {
        setText(charSequence, this.mBufferType);
    }

    @RemotableViewMethod
    public final void setTextKeepState(CharSequence charSequence) {
        setTextKeepState(charSequence, this.mBufferType);
    }

    public void setText(CharSequence charSequence, BufferType bufferType) {
        setText(charSequence, bufferType, true, 0);
        CharWrapper charWrapper = this.mCharWrapper;
        if (charWrapper != null) {
            charWrapper.mChars = null;
        }
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
    private void setText(CharSequence charSequence, BufferType bufferType, boolean z, int i) {
        int length;
        CharSequence charSequenceNewSpannable;
        Spannable spannableNewSpannable;
        BufferType bufferType2 = bufferType;
        CharSequence charSequence2 = charSequence == null ? "" : charSequence;
        CharSequence charSequenceRemoveSuggestionSpans = charSequence2;
        if (!isSuggestionsEnabled()) {
            charSequenceRemoveSuggestionSpans = removeSuggestionSpans(charSequence2);
        }
        if (!this.mUserSetTextScaleX) {
            this.mTextPaint.setTextScaleX(1.0f);
        }
        if ((charSequenceRemoveSuggestionSpans instanceof Spanned) && ((Spanned) charSequenceRemoveSuggestionSpans).getSpanStart(TextUtils.TruncateAt.MARQUEE) >= 0) {
            if (ViewConfiguration.get(this.mContext).isFadingMarqueeEnabled()) {
                setHorizontalFadingEdgeEnabled(true);
                this.mMarqueeFadeMode = 0;
            } else {
                setHorizontalFadingEdgeEnabled(false);
                this.mMarqueeFadeMode = 1;
            }
            setEllipsize(TextUtils.TruncateAt.MARQUEE);
        }
        int length2 = this.mFilters.length;
        int i2 = 0;
        CharSequence charSequence3 = charSequenceRemoveSuggestionSpans;
        while (i2 < length2) {
            CharSequence charSequenceFilter = this.mFilters[i2].filter(charSequence3, 0, charSequence3.length(), EMPTY_SPANNED, 0, 0);
            if (charSequenceFilter != null) {
                charSequence3 = charSequenceFilter;
            }
            i2++;
            charSequence3 = charSequence3;
        }
        if (z) {
            CharSequence charSequence4 = this.mText;
            if (charSequence4 != null) {
                length = charSequence4.length();
                sendBeforeTextChanged(this.mText, 0, length, charSequence3.length());
            } else {
                sendBeforeTextChanged("", 0, 0, charSequence3.length());
                length = i;
            }
        } else {
            length = i;
        }
        ArrayList<TextWatcher> arrayList = this.mListeners;
        boolean z2 = (arrayList == null || arrayList.size() == 0) ? false : true;
        if (bufferType2 == BufferType.EDITABLE || getKeyListener() != null || z2) {
            createEditorIfNeeded();
            Editable editableNewEditable = this.mEditableFactory.newEditable(charSequence3);
            setFilters(editableNewEditable, this.mFilters);
            InputMethodManager inputMethodManagerPeekInstance = InputMethodManager.peekInstance();
            charSequenceNewSpannable = editableNewEditable;
            if (inputMethodManagerPeekInstance != null) {
                inputMethodManagerPeekInstance.restartInput(this);
                charSequenceNewSpannable = editableNewEditable;
            }
        } else if (bufferType2 == BufferType.SPANNABLE || this.mMovement != null) {
            charSequenceNewSpannable = this.mSpannableFactory.newSpannable(charSequence3);
        } else {
            boolean z3 = charSequence3 instanceof CharWrapper;
            charSequenceNewSpannable = charSequence3;
            if (!z3) {
                charSequenceNewSpannable = TextUtils.stringOrSpannedString(charSequence3);
            }
        }
        CharSequence charSequence5 = charSequenceNewSpannable;
        if (this.mAutoLinkMask != 0) {
            if (bufferType2 == BufferType.EDITABLE || (charSequenceNewSpannable instanceof Spannable)) {
                spannableNewSpannable = (Spannable) charSequenceNewSpannable;
            } else {
                spannableNewSpannable = this.mSpannableFactory.newSpannable(charSequenceNewSpannable);
            }
            charSequence5 = charSequenceNewSpannable;
            if (Linkify.addLinks(spannableNewSpannable, this.mAutoLinkMask)) {
                bufferType2 = bufferType2 == BufferType.EDITABLE ? BufferType.EDITABLE : BufferType.SPANNABLE;
                this.mText = spannableNewSpannable;
                if (this.mLinksClickable && !textCanBeSelected()) {
                    setMovementMethod(LinkMovementMethod.getInstance());
                }
                charSequence5 = spannableNewSpannable;
            }
        }
        this.mBufferType = bufferType2;
        this.mText = charSequence5;
        TransformationMethod transformationMethod = this.mTransformation;
        if (transformationMethod == null) {
            this.mTransformed = charSequence5;
        } else {
            this.mTransformed = transformationMethod.getTransformation(charSequence5, this);
        }
        int length3 = charSequence5.length();
        if ((charSequence5 instanceof Spannable) && !this.mAllowTransformationLengthChange) {
            Spannable spannable = (Spannable) charSequence5;
            for (ChangeWatcher changeWatcher : (ChangeWatcher[]) spannable.getSpans(0, spannable.length(), ChangeWatcher.class)) {
                spannable.removeSpan(changeWatcher);
            }
            if (this.mChangeWatcher == null) {
                this.mChangeWatcher = new ChangeWatcher();
            }
            spannable.setSpan(this.mChangeWatcher, 0, length3, 6553618);
            Editor editor = this.mEditor;
            if (editor != null) {
                editor.addSpanWatchers(spannable);
            }
            TransformationMethod transformationMethod2 = this.mTransformation;
            if (transformationMethod2 != null) {
                spannable.setSpan(transformationMethod2, 0, length3, 18);
            }
            MovementMethod movementMethod = this.mMovement;
            if (movementMethod != null) {
                movementMethod.initialize(this, spannable);
                Editor editor2 = this.mEditor;
                if (editor2 != null) {
                    editor2.mSelectionMoved = false;
                }
            }
        }
        if (this.mLayout != null) {
            checkForRelayout();
        }
        sendOnTextChanged(charSequence5, 0, length, length3);
        onTextChanged(charSequence5, 0, length, length3);
        notifyViewAccessibilityStateChangedIfNeeded(2);
        if (z2) {
            sendAfterTextChanged((Editable) charSequence5);
        }
        Editor editor3 = this.mEditor;
        if (editor3 != null) {
            editor3.prepareCursorControllers();
        }
    }

    public final void setText(char[] cArr, int i, int i2) {
        int length;
        if (i < 0 || i2 < 0 || i + i2 > cArr.length) {
            throw new IndexOutOfBoundsException(i + ", " + i2);
        }
        CharSequence charSequence = this.mText;
        if (charSequence != null) {
            length = charSequence.length();
            sendBeforeTextChanged(this.mText, 0, length, i2);
        } else {
            sendBeforeTextChanged("", 0, 0, i2);
            length = 0;
        }
        CharWrapper charWrapper = this.mCharWrapper;
        if (charWrapper == null) {
            this.mCharWrapper = new CharWrapper(cArr, i, i2);
        } else {
            charWrapper.set(cArr, i, i2);
        }
        setText(this.mCharWrapper, this.mBufferType, false, length);
    }

    public final void setTextKeepState(CharSequence charSequence, BufferType bufferType) {
        int selectionStart = getSelectionStart();
        int selectionEnd = getSelectionEnd();
        int length = charSequence.length();
        setText(charSequence, bufferType);
        if (selectionStart >= 0 || selectionEnd >= 0) {
            CharSequence charSequence2 = this.mText;
            if (charSequence2 instanceof Spannable) {
                Selection.setSelection((Spannable) charSequence2, Math.max(0, Math.min(selectionStart, length)), Math.max(0, Math.min(selectionEnd, length)));
            }
        }
    }

    @RemotableViewMethod
    public final void setText(int i) {
        setText(getContext().getResources().getText(i));
    }

    public final void setText(int i, BufferType bufferType) {
        setText(getContext().getResources().getText(i), bufferType);
    }

    @RemotableViewMethod
    public final void setHint(CharSequence charSequence) {
        this.mHint = TextUtils.stringOrSpannedString(charSequence);
        if (this.mLayout != null) {
            checkForRelayout();
        }
        if (this.mText.length() == 0) {
            invalidate();
        }
        if (this.mEditor == null || this.mText.length() != 0 || this.mHint == null) {
            return;
        }
        this.mEditor.invalidateTextDisplayList();
    }

    @RemotableViewMethod
    public final void setHint(int i) {
        setHint(getContext().getResources().getText(i));
    }

    @ViewDebug.CapturedViewProperty
    public CharSequence getHint() {
        return this.mHint;
    }

    boolean isSingleLine() {
        return this.mSingleLine;
    }

    CharSequence removeSuggestionSpans(CharSequence charSequence) {
        Spannable spannableString;
        if (charSequence instanceof Spanned) {
            if (charSequence instanceof Spannable) {
                spannableString = (Spannable) charSequence;
            } else {
                spannableString = new SpannableString(charSequence);
                charSequence = spannableString;
            }
            for (SuggestionSpan suggestionSpan : (SuggestionSpan[]) spannableString.getSpans(0, charSequence.length(), SuggestionSpan.class)) {
                spannableString.removeSpan(suggestionSpan);
            }
        }
        return charSequence;
    }

    public void setInputType(int i) {
        boolean zIsPasswordInputType = isPasswordInputType(getInputType());
        boolean zIsVisiblePasswordInputType = isVisiblePasswordInputType(getInputType());
        boolean z = false;
        setInputType(i, false);
        boolean zIsPasswordInputType2 = isPasswordInputType(i);
        boolean zIsVisiblePasswordInputType2 = isVisiblePasswordInputType(i);
        if (zIsPasswordInputType2) {
            setTransformationMethod(PasswordTransformationMethod.getInstance());
            setTypefaceFromAttrs(null, 3, 0);
        } else if (zIsVisiblePasswordInputType2) {
            boolean z2 = this.mTransformation == PasswordTransformationMethod.getInstance();
            setTypefaceFromAttrs(null, 3, 0);
            z = z2;
        } else if (zIsPasswordInputType || zIsVisiblePasswordInputType) {
            setTypefaceFromAttrs(null, -1, -1);
            if (this.mTransformation == PasswordTransformationMethod.getInstance()) {
                z = true;
            }
        }
        boolean z3 = !isMultilineInputType(i);
        if (this.mSingleLine != z3 || z) {
            applySingleLine(z3, !zIsPasswordInputType2, true);
        }
        if (!isSuggestionsEnabled()) {
            this.mText = removeSuggestionSpans(this.mText);
        }
        InputMethodManager inputMethodManagerPeekInstance = InputMethodManager.peekInstance();
        if (inputMethodManagerPeekInstance != null) {
            inputMethodManagerPeekInstance.restartInput(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean hasPasswordTransformationMethod() {
        return this.mTransformation instanceof PasswordTransformationMethod;
    }

    public void setRawInputType(int i) {
        if (i == 0 && this.mEditor == null) {
            return;
        }
        createEditorIfNeeded();
        this.mEditor.mInputType = i;
    }

    private void setInputType(int i, boolean z) {
        KeyListener textKeyListener;
        TextKeyListener.Capitalize capitalize;
        int i2 = i & 15;
        if (i2 == 1) {
            boolean z2 = (32768 & i) != 0;
            if ((i & 4096) != 0) {
                capitalize = TextKeyListener.Capitalize.CHARACTERS;
            } else if ((i & 8192) != 0) {
                capitalize = TextKeyListener.Capitalize.WORDS;
            } else if ((i & 16384) != 0) {
                capitalize = TextKeyListener.Capitalize.SENTENCES;
            } else {
                capitalize = TextKeyListener.Capitalize.NONE;
            }
            textKeyListener = TextKeyListener.getInstance(z2, capitalize);
        } else if (i2 == 2) {
            textKeyListener = DigitsKeyListener.getInstance((i & 4096) != 0, (i & 8192) != 0);
        } else if (i2 == 4) {
            int i3 = i & InputType.TYPE_MASK_VARIATION;
            if (i3 == 16) {
                textKeyListener = DateKeyListener.getInstance();
            } else if (i3 == 32) {
                textKeyListener = TimeKeyListener.getInstance();
            } else {
                textKeyListener = DateTimeKeyListener.getInstance();
            }
        } else if (i2 == 3) {
            textKeyListener = DialerKeyListener.getInstance();
        } else {
            textKeyListener = TextKeyListener.getInstance();
        }
        setRawInputType(i);
        if (z) {
            createEditorIfNeeded();
            this.mEditor.mKeyListener = textKeyListener;
        } else {
            setKeyListenerOnly(textKeyListener);
        }
    }

    public int getInputType() {
        Editor editor = this.mEditor;
        if (editor == null) {
            return 0;
        }
        return editor.mInputType;
    }

    public void setImeOptions(int i) {
        createEditorIfNeeded();
        this.mEditor.createInputContentTypeIfNeeded();
        this.mEditor.mInputContentType.imeOptions = i;
    }

    public int getImeOptions() {
        Editor editor = this.mEditor;
        if (editor == null || editor.mInputContentType == null) {
            return 0;
        }
        return this.mEditor.mInputContentType.imeOptions;
    }

    public void setImeActionLabel(CharSequence charSequence, int i) {
        createEditorIfNeeded();
        this.mEditor.createInputContentTypeIfNeeded();
        this.mEditor.mInputContentType.imeActionLabel = charSequence;
        this.mEditor.mInputContentType.imeActionId = i;
    }

    public CharSequence getImeActionLabel() {
        Editor editor = this.mEditor;
        if (editor == null || editor.mInputContentType == null) {
            return null;
        }
        return this.mEditor.mInputContentType.imeActionLabel;
    }

    public int getImeActionId() {
        Editor editor = this.mEditor;
        if (editor == null || editor.mInputContentType == null) {
            return 0;
        }
        return this.mEditor.mInputContentType.imeActionId;
    }

    public void setOnEditorActionListener(OnEditorActionListener onEditorActionListener) {
        createEditorIfNeeded();
        this.mEditor.createInputContentTypeIfNeeded();
        this.mEditor.mInputContentType.onEditorActionListener = onEditorActionListener;
    }

    public void onEditorAction(int i) {
        Editor editor = this.mEditor;
        Editor.InputContentType inputContentType = editor == null ? null : editor.mInputContentType;
        if (inputContentType != null) {
            if (inputContentType.onEditorActionListener != null && inputContentType.onEditorActionListener.onEditorAction(this, i, null)) {
                return;
            }
            if (i == 5) {
                View viewFocusSearch = focusSearch(2);
                if (viewFocusSearch != null && !viewFocusSearch.requestFocus(2)) {
                    throw new IllegalStateException("focus search returned a view that wasn't able to take focus!");
                }
                return;
            }
            if (i == 7) {
                View viewFocusSearch2 = focusSearch(1);
                if (viewFocusSearch2 != null && !viewFocusSearch2.requestFocus(1)) {
                    throw new IllegalStateException("focus search returned a view that wasn't able to take focus!");
                }
                return;
            }
            if (i == 6) {
                InputMethodManager inputMethodManagerPeekInstance = InputMethodManager.peekInstance();
                if (inputMethodManagerPeekInstance == null || !inputMethodManagerPeekInstance.isActive(this)) {
                    return;
                }
                inputMethodManagerPeekInstance.hideSoftInputFromWindow(getWindowToken(), 0);
                return;
            }
        }
        ViewRootImpl viewRootImpl = getViewRootImpl();
        if (viewRootImpl != null) {
            long jUptimeMillis = SystemClock.uptimeMillis();
            viewRootImpl.dispatchKeyFromIme(new KeyEvent(jUptimeMillis, jUptimeMillis, 0, 66, 0, 0, -1, 0, 22));
            viewRootImpl.dispatchKeyFromIme(new KeyEvent(SystemClock.uptimeMillis(), jUptimeMillis, 1, 66, 0, 0, -1, 0, 22));
        }
    }

    public void setPrivateImeOptions(String str) {
        createEditorIfNeeded();
        this.mEditor.createInputContentTypeIfNeeded();
        this.mEditor.mInputContentType.privateImeOptions = str;
    }

    public String getPrivateImeOptions() {
        Editor editor = this.mEditor;
        if (editor == null || editor.mInputContentType == null) {
            return null;
        }
        return this.mEditor.mInputContentType.privateImeOptions;
    }

    public void setInputExtras(int i) throws XmlPullParserException, IOException {
        createEditorIfNeeded();
        XmlResourceParser xml = getResources().getXml(i);
        this.mEditor.createInputContentTypeIfNeeded();
        this.mEditor.mInputContentType.extras = new Bundle();
        getResources().parseBundleExtras(xml, this.mEditor.mInputContentType.extras);
    }

    public Bundle getInputExtras(boolean z) {
        if (this.mEditor == null && !z) {
            return null;
        }
        createEditorIfNeeded();
        if (this.mEditor.mInputContentType == null) {
            if (!z) {
                return null;
            }
            this.mEditor.createInputContentTypeIfNeeded();
        }
        if (this.mEditor.mInputContentType.extras == null) {
            if (!z) {
                return null;
            }
            this.mEditor.mInputContentType.extras = new Bundle();
        }
        return this.mEditor.mInputContentType.extras;
    }

    public CharSequence getError() {
        Editor editor = this.mEditor;
        if (editor == null) {
            return null;
        }
        return editor.mError;
    }

    @RemotableViewMethod
    public void setError(CharSequence charSequence) {
        if (charSequence == null) {
            setError(null, null);
            return;
        }
        Drawable drawable = getContext().getResources().getDrawable(17302448);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        setError(charSequence, drawable);
    }

    public void setError(CharSequence charSequence, Drawable drawable) {
        createEditorIfNeeded();
        this.mEditor.setError(charSequence, drawable);
        notifyViewAccessibilityStateChangedIfNeeded(0);
    }

    @Override // android.view.View
    protected boolean setFrame(int i, int i2, int i3, int i4) {
        boolean frame = super.setFrame(i, i2, i3, i4);
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.setFrame();
        }
        restartMarqueeIfNeeded();
        return frame;
    }

    private void restartMarqueeIfNeeded() {
        if (this.mRestartMarquee && this.mEllipsize == TextUtils.TruncateAt.MARQUEE) {
            this.mRestartMarquee = false;
            startMarquee();
        }
    }

    public void setFilters(InputFilter[] inputFilterArr) {
        if (inputFilterArr == null) {
            throw new IllegalArgumentException();
        }
        this.mFilters = inputFilterArr;
        CharSequence charSequence = this.mText;
        if (charSequence instanceof Editable) {
            setFilters((Editable) charSequence, inputFilterArr);
        }
    }

    private void setFilters(Editable editable, InputFilter[] inputFilterArr) {
        Editor editor = this.mEditor;
        if (editor != null) {
            int i = 1;
            boolean z = editor.mUndoInputFilter != null;
            boolean z2 = this.mEditor.mKeyListener instanceof InputFilter;
            int i2 = z ? 1 : 0;
            if (z2) {
                i2++;
            }
            if (i2 > 0) {
                InputFilter[] inputFilterArr2 = new InputFilter[inputFilterArr.length + i2];
                System.arraycopy(inputFilterArr, 0, inputFilterArr2, 0, inputFilterArr.length);
                if (z) {
                    inputFilterArr2[inputFilterArr.length] = this.mEditor.mUndoInputFilter;
                } else {
                    i = 0;
                }
                if (z2) {
                    inputFilterArr2[inputFilterArr.length + i] = (InputFilter) this.mEditor.mKeyListener;
                }
                editable.setFilters(inputFilterArr2);
                return;
            }
        }
        editable.setFilters(inputFilterArr);
    }

    public InputFilter[] getFilters() {
        return this.mFilters;
    }

    private int getBoxHeight(Layout layout) {
        int extendedPaddingTop;
        int extendedPaddingBottom;
        Insets opticalInsets = isLayoutModeOptical(this.mParent) ? getOpticalInsets() : Insets.NONE;
        if (layout == this.mHintLayout) {
            extendedPaddingTop = getCompoundPaddingTop();
            extendedPaddingBottom = getCompoundPaddingBottom();
        } else {
            extendedPaddingTop = getExtendedPaddingTop();
            extendedPaddingBottom = getExtendedPaddingBottom();
        }
        return (getMeasuredHeight() - (extendedPaddingTop + extendedPaddingBottom)) + opticalInsets.top + opticalInsets.bottom;
    }

    int getVerticalOffset(boolean z) {
        int boxHeight;
        int height;
        Layout layout;
        int i = this.mGravity & 112;
        Layout layout2 = this.mLayout;
        if (!z && this.mText.length() == 0 && (layout = this.mHintLayout) != null) {
            layout2 = layout;
        }
        if (i == 48 || (height = layout2.getHeight()) >= (boxHeight = getBoxHeight(layout2))) {
            return 0;
        }
        return i == 80 ? boxHeight - height : (boxHeight - height) >> 1;
    }

    private int getBottomVerticalOffset(boolean z) {
        int boxHeight;
        int height;
        Layout layout;
        int i = this.mGravity & 112;
        Layout layout2 = this.mLayout;
        if (!z && this.mText.length() == 0 && (layout = this.mHintLayout) != null) {
            layout2 = layout;
        }
        if (i == 80 || (height = layout2.getHeight()) >= (boxHeight = getBoxHeight(layout2))) {
            return 0;
        }
        return i == 48 ? boxHeight - height : (boxHeight - height) >> 1;
    }

    void invalidateCursorPath() {
        if (this.mHighlightPathBogus) {
            invalidateCursor();
            return;
        }
        int compoundPaddingLeft = getCompoundPaddingLeft();
        int extendedPaddingTop = getExtendedPaddingTop() + getVerticalOffset(true);
        if (this.mEditor.mCursorCount == 0) {
            RectF rectF = TEMP_RECTF;
            synchronized (rectF) {
                float fCeil = FloatMath.ceil(this.mTextPaint.getStrokeWidth());
                if (fCeil < 1.0f) {
                    fCeil = 1.0f;
                }
                float f = fCeil / 2.0f;
                this.mHighlightPath.computeBounds(rectF, false);
                float f2 = compoundPaddingLeft;
                float f3 = extendedPaddingTop;
                invalidate((int) FloatMath.floor((rectF.left + f2) - f), (int) FloatMath.floor((rectF.top + f3) - f), (int) FloatMath.ceil(f2 + rectF.right + f), (int) FloatMath.ceil(f3 + rectF.bottom + f));
            }
            return;
        }
        for (int i = 0; i < this.mEditor.mCursorCount; i++) {
            Rect bounds = this.mEditor.mCursorDrawable[i].getBounds();
            invalidate(bounds.left + compoundPaddingLeft, bounds.top + extendedPaddingTop, bounds.right + compoundPaddingLeft, bounds.bottom + extendedPaddingTop);
        }
    }

    void invalidateCursor() {
        int selectionEnd = getSelectionEnd();
        invalidateCursor(selectionEnd, selectionEnd, selectionEnd);
    }

    private void invalidateCursor(int i, int i2, int i3) {
        if (i >= 0 || i2 >= 0 || i3 >= 0) {
            invalidateRegion(Math.min(Math.min(i, i2), i3), Math.max(Math.max(i, i2), i3), true);
        }
    }

    void invalidateRegion(int i, int i2, boolean z) {
        int width;
        Layout layout = this.mLayout;
        if (layout == null) {
            invalidate();
            return;
        }
        int lineForOffset = layout.getLineForOffset(i);
        int lineTop = this.mLayout.getLineTop(lineForOffset);
        if (lineForOffset > 0) {
            lineTop -= this.mLayout.getLineDescent(lineForOffset - 1);
        }
        int lineForOffset2 = i == i2 ? lineForOffset : this.mLayout.getLineForOffset(i2);
        int lineBottom = this.mLayout.getLineBottom(lineForOffset2);
        if (z && this.mEditor != null) {
            for (int i3 = 0; i3 < this.mEditor.mCursorCount; i3++) {
                Rect bounds = this.mEditor.mCursorDrawable[i3].getBounds();
                lineTop = Math.min(lineTop, bounds.top);
                lineBottom = Math.max(lineBottom, bounds.bottom);
            }
        }
        int compoundPaddingLeft = getCompoundPaddingLeft();
        int extendedPaddingTop = getExtendedPaddingTop() + getVerticalOffset(true);
        if (lineForOffset == lineForOffset2 && !z) {
            int primaryHorizontal = (int) this.mLayout.getPrimaryHorizontal(i);
            width = ((int) (((double) this.mLayout.getPrimaryHorizontal(i2)) + 1.0d)) + compoundPaddingLeft;
            compoundPaddingLeft = primaryHorizontal + compoundPaddingLeft;
        } else {
            width = getWidth() - getCompoundPaddingRight();
        }
        invalidate(this.mScrollX + compoundPaddingLeft, lineTop + extendedPaddingTop, this.mScrollX + width, extendedPaddingTop + lineBottom);
    }

    private void registerForPreDraw() {
        if (this.mPreDrawRegistered) {
            return;
        }
        getViewTreeObserver().addOnPreDrawListener(this);
        this.mPreDrawRegistered = true;
    }

    @Override // android.view.ViewTreeObserver.OnPreDrawListener
    public boolean onPreDraw() {
        Editor editor;
        if (this.mLayout == null) {
            assumeLayout();
        }
        if (this.mMovement != null) {
            int selectionEnd = getSelectionEnd();
            Editor editor2 = this.mEditor;
            if (editor2 != null && editor2.mSelectionModifierCursorController != null && this.mEditor.mSelectionModifierCursorController.isSelectionStartDragged()) {
                selectionEnd = getSelectionStart();
            }
            if (selectionEnd < 0 && (this.mGravity & 112) == 80) {
                selectionEnd = this.mText.length();
            }
            if (selectionEnd >= 0) {
                bringPointIntoView(selectionEnd);
            }
        } else {
            bringTextIntoView();
        }
        Editor editor3 = this.mEditor;
        if (editor3 != null && editor3.mCreatedWithASelection) {
            this.mEditor.startSelectionActionMode();
            this.mEditor.mCreatedWithASelection = false;
        }
        if ((this instanceof ExtractEditText) && hasSelection() && (editor = this.mEditor) != null) {
            editor.startSelectionActionMode();
        }
        getViewTreeObserver().removeOnPreDrawListener(this);
        this.mPreDrawRegistered = false;
        return true;
    }

    @Override // android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mTemporaryDetach = false;
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.onAttachedToWindow();
        }
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mPreDrawRegistered) {
            getViewTreeObserver().removeOnPreDrawListener(this);
            this.mPreDrawRegistered = false;
        }
        resetResolvedDrawables();
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.onDetachedFromWindow();
        }
    }

    @Override // android.view.View
    public void onScreenStateChanged(int i) {
        super.onScreenStateChanged(i);
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.onScreenStateChanged(i);
        }
    }

    @Override // android.view.View
    protected boolean isPaddingOffsetRequired() {
        return (this.mShadowRadius == 0.0f && this.mDrawables == null) ? false : true;
    }

    @Override // android.view.View
    protected int getLeftPaddingOffset() {
        return (getCompoundPaddingLeft() - this.mPaddingLeft) + ((int) Math.min(0.0f, this.mShadowDx - this.mShadowRadius));
    }

    @Override // android.view.View
    protected int getTopPaddingOffset() {
        return (int) Math.min(0.0f, this.mShadowDy - this.mShadowRadius);
    }

    @Override // android.view.View
    protected int getBottomPaddingOffset() {
        return (int) Math.max(0.0f, this.mShadowDy + this.mShadowRadius);
    }

    @Override // android.view.View
    protected int getRightPaddingOffset() {
        return (-(getCompoundPaddingRight() - this.mPaddingRight)) + ((int) Math.max(0.0f, this.mShadowDx + this.mShadowRadius));
    }

    @Override // android.view.View
    protected boolean verifyDrawable(Drawable drawable) {
        Drawables drawables;
        boolean zVerifyDrawable = super.verifyDrawable(drawable);
        return (zVerifyDrawable || (drawables = this.mDrawables) == null) ? zVerifyDrawable : drawable == drawables.mDrawableLeft || drawable == this.mDrawables.mDrawableTop || drawable == this.mDrawables.mDrawableRight || drawable == this.mDrawables.mDrawableBottom || drawable == this.mDrawables.mDrawableStart || drawable == this.mDrawables.mDrawableEnd;
    }

    @Override // android.view.View
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        Drawables drawables = this.mDrawables;
        if (drawables != null) {
            if (drawables.mDrawableLeft != null) {
                this.mDrawables.mDrawableLeft.jumpToCurrentState();
            }
            if (this.mDrawables.mDrawableTop != null) {
                this.mDrawables.mDrawableTop.jumpToCurrentState();
            }
            if (this.mDrawables.mDrawableRight != null) {
                this.mDrawables.mDrawableRight.jumpToCurrentState();
            }
            if (this.mDrawables.mDrawableBottom != null) {
                this.mDrawables.mDrawableBottom.jumpToCurrentState();
            }
            if (this.mDrawables.mDrawableStart != null) {
                this.mDrawables.mDrawableStart.jumpToCurrentState();
            }
            if (this.mDrawables.mDrawableEnd != null) {
                this.mDrawables.mDrawableEnd.jumpToCurrentState();
            }
        }
    }

    @Override // android.view.View, android.graphics.drawable.Drawable.Callback
    public void invalidateDrawable(Drawable drawable) {
        int i;
        int compoundPaddingTop;
        int i2;
        if (verifyDrawable(drawable)) {
            Rect bounds = drawable.getBounds();
            int compoundPaddingRight = this.mScrollX;
            int i3 = this.mScrollY;
            Drawables drawables = this.mDrawables;
            if (drawables != null) {
                if (drawable == drawables.mDrawableLeft) {
                    compoundPaddingTop = getCompoundPaddingTop();
                    int compoundPaddingBottom = ((this.mBottom - this.mTop) - getCompoundPaddingBottom()) - compoundPaddingTop;
                    compoundPaddingRight += this.mPaddingLeft;
                    i2 = (compoundPaddingBottom - drawables.mDrawableHeightLeft) / 2;
                } else if (drawable == drawables.mDrawableRight) {
                    compoundPaddingTop = getCompoundPaddingTop();
                    int compoundPaddingBottom2 = ((this.mBottom - this.mTop) - getCompoundPaddingBottom()) - compoundPaddingTop;
                    compoundPaddingRight += ((this.mRight - this.mLeft) - this.mPaddingRight) - drawables.mDrawableSizeRight;
                    i2 = (compoundPaddingBottom2 - drawables.mDrawableHeightRight) / 2;
                } else {
                    if (drawable == drawables.mDrawableTop) {
                        int compoundPaddingLeft = getCompoundPaddingLeft();
                        compoundPaddingRight += compoundPaddingLeft + (((((this.mRight - this.mLeft) - getCompoundPaddingRight()) - compoundPaddingLeft) - drawables.mDrawableWidthTop) / 2);
                        i = this.mPaddingTop;
                    } else if (drawable == drawables.mDrawableBottom) {
                        int compoundPaddingLeft2 = getCompoundPaddingLeft();
                        compoundPaddingRight += compoundPaddingLeft2 + (((((this.mRight - this.mLeft) - getCompoundPaddingRight()) - compoundPaddingLeft2) - drawables.mDrawableWidthBottom) / 2);
                        i = ((this.mBottom - this.mTop) - this.mPaddingBottom) - drawables.mDrawableSizeBottom;
                    }
                    i3 += i;
                }
                i = compoundPaddingTop + i2;
                i3 += i;
            }
            invalidate(bounds.left + compoundPaddingRight, bounds.top + i3, bounds.right + compoundPaddingRight, bounds.bottom + i3);
        }
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return !(getBackground() == null || getBackground().getCurrent() == null) || (this.mText instanceof Spannable) || hasSelection() || isHorizontalFadingEdgeEnabled();
    }

    public boolean isTextSelectable() {
        Editor editor = this.mEditor;
        if (editor == null) {
            return false;
        }
        return editor.mTextIsSelectable;
    }

    public void setTextIsSelectable(boolean z) {
        if (z || this.mEditor != null) {
            createEditorIfNeeded();
            if (this.mEditor.mTextIsSelectable == z) {
                return;
            }
            this.mEditor.mTextIsSelectable = z;
            setFocusableInTouchMode(z);
            setFocusable(z);
            setClickable(z);
            setLongClickable(z);
            setMovementMethod(z ? ArrowKeyMovementMethod.getInstance() : null);
            setText(this.mText, z ? BufferType.SPANNABLE : BufferType.NORMAL);
            this.mEditor.prepareCursorControllers();
        }
    }

    @Override // android.view.View
    protected int[] onCreateDrawableState(int i) {
        int[] iArrOnCreateDrawableState;
        if (this.mSingleLine) {
            iArrOnCreateDrawableState = super.onCreateDrawableState(i);
        } else {
            iArrOnCreateDrawableState = super.onCreateDrawableState(i + 1);
            mergeDrawableStates(iArrOnCreateDrawableState, MULTILINE_STATE_SET);
        }
        if (isTextSelectable()) {
            int length = iArrOnCreateDrawableState.length;
            for (int i2 = 0; i2 < length; i2++) {
                if (iArrOnCreateDrawableState[i2] == 16842919) {
                    int[] iArr = new int[length - 1];
                    System.arraycopy(iArrOnCreateDrawableState, 0, iArr, 0, i2);
                    System.arraycopy(iArrOnCreateDrawableState, i2 + 1, iArr, i2, (length - i2) - 1);
                    return iArr;
                }
            }
        }
        return iArrOnCreateDrawableState;
    }

    private Path getUpdatedHighlightPath() {
        Paint paint = this.mHighlightPaint;
        int selectionStart = getSelectionStart();
        int selectionEnd = getSelectionEnd();
        if (this.mMovement != null && ((isFocused() || isPressed()) && selectionStart >= 0)) {
            if (selectionStart == selectionEnd) {
                Editor editor = this.mEditor;
                if (editor != null && editor.isCursorVisible() && (SystemClock.uptimeMillis() - this.mEditor.mShowCursor) % 1000 < 500) {
                    if (this.mHighlightPathBogus) {
                        if (this.mHighlightPath == null) {
                            this.mHighlightPath = new Path();
                        }
                        this.mHighlightPath.reset();
                        this.mLayout.getCursorPath(selectionStart, this.mHighlightPath, this.mText);
                        this.mEditor.updateCursorsPositions();
                        this.mHighlightPathBogus = false;
                    }
                    paint.setColor(this.mCurTextColor);
                    paint.setStyle(Paint.Style.STROKE);
                    return this.mHighlightPath;
                }
            } else {
                if (this.mHighlightPathBogus) {
                    if (this.mHighlightPath == null) {
                        this.mHighlightPath = new Path();
                    }
                    this.mHighlightPath.reset();
                    this.mLayout.getSelectionPath(selectionStart, selectionEnd, this.mHighlightPath);
                    this.mHighlightPathBogus = false;
                }
                paint.setColor(this.mHighlightColor);
                paint.setStyle(Paint.Style.FILL);
                return this.mHighlightPath;
            }
        }
        return null;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int i;
        boolean z;
        int i2;
        int verticalOffset;
        restartMarqueeIfNeeded();
        super.onDraw(canvas);
        int compoundPaddingLeft = getCompoundPaddingLeft();
        int compoundPaddingTop = getCompoundPaddingTop();
        int compoundPaddingRight = getCompoundPaddingRight();
        int compoundPaddingBottom = getCompoundPaddingBottom();
        int i3 = this.mScrollX;
        int i4 = this.mScrollY;
        int i5 = this.mRight;
        int i6 = this.mLeft;
        int i7 = this.mBottom;
        int i8 = this.mTop;
        boolean zIsLayoutRtl = isLayoutRtl();
        int horizontalOffsetForDrawables = getHorizontalOffsetForDrawables();
        int i9 = zIsLayoutRtl ? 0 : horizontalOffsetForDrawables;
        if (!zIsLayoutRtl) {
            horizontalOffsetForDrawables = 0;
        }
        Drawables drawables = this.mDrawables;
        if (drawables != null) {
            int i10 = ((i7 - i8) - compoundPaddingBottom) - compoundPaddingTop;
            int i11 = ((i5 - i6) - compoundPaddingRight) - compoundPaddingLeft;
            z = zIsLayoutRtl;
            if (drawables.mDrawableLeft != null) {
                canvas.save();
                i = compoundPaddingRight;
                canvas.translate(this.mPaddingLeft + i3 + i9, i4 + compoundPaddingTop + ((i10 - drawables.mDrawableHeightLeft) / 2));
                drawables.mDrawableLeft.draw(canvas);
                canvas.restore();
            } else {
                i = compoundPaddingRight;
            }
            if (drawables.mDrawableRight != null) {
                canvas.save();
                canvas.translate(((((i3 + i5) - i6) - this.mPaddingRight) - drawables.mDrawableSizeRight) - horizontalOffsetForDrawables, i4 + compoundPaddingTop + ((i10 - drawables.mDrawableHeightRight) / 2));
                drawables.mDrawableRight.draw(canvas);
                canvas.restore();
            }
            if (drawables.mDrawableTop != null) {
                canvas.save();
                canvas.translate(i3 + compoundPaddingLeft + ((i11 - drawables.mDrawableWidthTop) / 2), this.mPaddingTop + i4);
                drawables.mDrawableTop.draw(canvas);
                canvas.restore();
            }
            if (drawables.mDrawableBottom != null) {
                canvas.save();
                canvas.translate(i3 + compoundPaddingLeft + ((i11 - drawables.mDrawableWidthBottom) / 2), (((i4 + i7) - i8) - this.mPaddingBottom) - drawables.mDrawableSizeBottom);
                drawables.mDrawableBottom.draw(canvas);
                canvas.restore();
            }
        } else {
            i = compoundPaddingRight;
            z = zIsLayoutRtl;
        }
        int i12 = this.mCurTextColor;
        if (this.mLayout == null) {
            assumeLayout();
        }
        Layout layout = this.mLayout;
        if (this.mHint != null && this.mText.length() == 0) {
            if (this.mHintTextColor != null) {
                i12 = this.mCurHintTextColor;
            }
            layout = this.mHintLayout;
        }
        this.mTextPaint.setColor(i12);
        this.mTextPaint.drawableState = getDrawableState();
        canvas.save();
        int extendedPaddingTop = getExtendedPaddingTop();
        int extendedPaddingBottom = getExtendedPaddingBottom();
        int height = this.mLayout.getHeight() - (((this.mBottom - this.mTop) - compoundPaddingBottom) - compoundPaddingTop);
        float fMin = compoundPaddingLeft + i3;
        float fMin2 = i4 == 0 ? 0.0f : extendedPaddingTop + i4;
        float fMax = ((i5 - i6) - i) + i3;
        int i13 = (i7 - i8) + i4;
        if (i4 == height) {
            extendedPaddingBottom = 0;
        }
        float fMax2 = i13 - extendedPaddingBottom;
        float f = this.mShadowRadius;
        if (f != 0.0f) {
            fMin += Math.min(0.0f, this.mShadowDx - f);
            fMax += Math.max(0.0f, this.mShadowDx + this.mShadowRadius);
            fMin2 += Math.min(0.0f, this.mShadowDy - this.mShadowRadius);
            fMax2 += Math.max(0.0f, this.mShadowDy + this.mShadowRadius);
        }
        canvas.clipRect(fMin, fMin2, fMax, fMax2);
        if ((this.mGravity & 112) != 48) {
            int verticalOffset2 = getVerticalOffset(false);
            verticalOffset = getVerticalOffset(true);
            i2 = verticalOffset2;
        } else {
            i2 = 0;
            verticalOffset = 0;
        }
        canvas.translate(compoundPaddingLeft, extendedPaddingTop + i2);
        int absoluteGravity = Gravity.getAbsoluteGravity(this.mGravity, getLayoutDirection());
        if (this.mEllipsize == TextUtils.TruncateAt.MARQUEE && this.mMarqueeFadeMode != 1) {
            if (!this.mSingleLine && getLineCount() == 1 && canMarquee() && (absoluteGravity & 7) != 3) {
                float lineRight = this.mLayout.getLineRight(0) - ((this.mRight - this.mLeft) - (getCompoundPaddingLeft() + getCompoundPaddingRight()));
                if (z) {
                    lineRight = -lineRight;
                }
                canvas.translate(lineRight, 0.0f);
            }
            Marquee marquee = this.mMarquee;
            if (marquee != null && marquee.isRunning()) {
                float f2 = -this.mMarquee.getScroll();
                if (z) {
                    f2 = -f2;
                }
                canvas.translate(f2, 0.0f);
            }
        }
        int i14 = verticalOffset - i2;
        Path updatedHighlightPath = getUpdatedHighlightPath();
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.onDraw(canvas, layout, updatedHighlightPath, this.mHighlightPaint, i14);
        } else {
            layout.draw(canvas, updatedHighlightPath, this.mHighlightPaint, i14);
        }
        Marquee marquee2 = this.mMarquee;
        if (marquee2 != null && marquee2.shouldDrawGhost()) {
            int ghostOffset = (int) this.mMarquee.getGhostOffset();
            if (z) {
                ghostOffset = -ghostOffset;
            }
            canvas.translate(ghostOffset, 0.0f);
            layout.draw(canvas, updatedHighlightPath, this.mHighlightPaint, i14);
        }
        canvas.restore();
    }

    @Override // android.view.View
    public void getFocusedRect(Rect rect) {
        if (this.mLayout == null) {
            super.getFocusedRect(rect);
            return;
        }
        int selectionEnd = getSelectionEnd();
        if (selectionEnd < 0) {
            super.getFocusedRect(rect);
            return;
        }
        int selectionStart = getSelectionStart();
        if (selectionStart < 0 || selectionStart >= selectionEnd) {
            int lineForOffset = this.mLayout.getLineForOffset(selectionEnd);
            rect.top = this.mLayout.getLineTop(lineForOffset);
            rect.bottom = this.mLayout.getLineBottom(lineForOffset);
            rect.left = ((int) this.mLayout.getPrimaryHorizontal(selectionEnd)) - 2;
            rect.right = rect.left + 4;
        } else {
            int lineForOffset2 = this.mLayout.getLineForOffset(selectionStart);
            int lineForOffset3 = this.mLayout.getLineForOffset(selectionEnd);
            rect.top = this.mLayout.getLineTop(lineForOffset2);
            rect.bottom = this.mLayout.getLineBottom(lineForOffset3);
            if (lineForOffset2 == lineForOffset3) {
                rect.left = (int) this.mLayout.getPrimaryHorizontal(selectionStart);
                rect.right = (int) this.mLayout.getPrimaryHorizontal(selectionEnd);
            } else {
                if (this.mHighlightPathBogus) {
                    if (this.mHighlightPath == null) {
                        this.mHighlightPath = new Path();
                    }
                    this.mHighlightPath.reset();
                    this.mLayout.getSelectionPath(selectionStart, selectionEnd, this.mHighlightPath);
                    this.mHighlightPathBogus = false;
                }
                RectF rectF = TEMP_RECTF;
                synchronized (rectF) {
                    this.mHighlightPath.computeBounds(rectF, true);
                    rect.left = ((int) rectF.left) - 1;
                    rect.right = ((int) rectF.right) + 1;
                }
            }
        }
        int compoundPaddingLeft = getCompoundPaddingLeft();
        int extendedPaddingTop = getExtendedPaddingTop();
        if ((this.mGravity & 112) != 48) {
            extendedPaddingTop += getVerticalOffset(false);
        }
        rect.offset(compoundPaddingLeft, extendedPaddingTop);
        rect.bottom += getExtendedPaddingBottom();
    }

    public int getLineCount() {
        Layout layout = this.mLayout;
        if (layout != null) {
            return layout.getLineCount();
        }
        return 0;
    }

    public int getLineBounds(int i, Rect rect) {
        Layout layout = this.mLayout;
        if (layout == null) {
            if (rect != null) {
                rect.set(0, 0, 0, 0);
            }
            return 0;
        }
        int lineBounds = layout.getLineBounds(i, rect);
        int extendedPaddingTop = getExtendedPaddingTop();
        if ((this.mGravity & 112) != 48) {
            extendedPaddingTop += getVerticalOffset(true);
        }
        if (rect != null) {
            rect.offset(getCompoundPaddingLeft(), extendedPaddingTop);
        }
        return lineBounds + extendedPaddingTop;
    }

    @Override // android.view.View
    public int getBaseline() {
        if (this.mLayout == null) {
            return super.getBaseline();
        }
        int verticalOffset = (this.mGravity & 112) != 48 ? getVerticalOffset(true) : 0;
        if (isLayoutModeOptical(this.mParent)) {
            verticalOffset -= getOpticalInsets().top;
        }
        return getExtendedPaddingTop() + verticalOffset + this.mLayout.getLineBaseline(0);
    }

    @Override // android.view.View
    protected int getFadeTop(boolean z) {
        if (this.mLayout == null) {
            return 0;
        }
        int verticalOffset = (this.mGravity & 112) != 48 ? getVerticalOffset(true) : 0;
        if (z) {
            verticalOffset += getTopPaddingOffset();
        }
        return getExtendedPaddingTop() + verticalOffset;
    }

    @Override // android.view.View
    protected int getFadeHeight(boolean z) {
        Layout layout = this.mLayout;
        if (layout != null) {
            return layout.getHeight();
        }
        return 0;
    }

    @Override // android.view.View
    public boolean onKeyPreIme(int i, KeyEvent keyEvent) {
        if (i == 4) {
            Editor editor = this.mEditor;
            if ((editor == null || editor.mSelectionActionMode == null) ? false : true) {
                if (keyEvent.getAction() == 0 && keyEvent.getRepeatCount() == 0) {
                    KeyEvent.DispatcherState keyDispatcherState = getKeyDispatcherState();
                    if (keyDispatcherState != null) {
                        keyDispatcherState.startTracking(keyEvent, this);
                    }
                    return true;
                }
                if (keyEvent.getAction() == 1) {
                    KeyEvent.DispatcherState keyDispatcherState2 = getKeyDispatcherState();
                    if (keyDispatcherState2 != null) {
                        keyDispatcherState2.handleUpEvent(keyEvent);
                    }
                    if (keyEvent.isTracking() && !keyEvent.isCanceled()) {
                        stopSelectionActionMode();
                        return true;
                    }
                }
            }
        }
        return super.onKeyPreIme(i, keyEvent);
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (doKeyDown(i, keyEvent, null) == 0) {
            return super.onKeyDown(i, keyEvent);
        }
        return true;
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyMultiple(int i, int i2, KeyEvent keyEvent) {
        KeyEvent keyEventChangeAction = KeyEvent.changeAction(keyEvent, 0);
        int iDoKeyDown = doKeyDown(i, keyEventChangeAction, keyEvent);
        if (iDoKeyDown == 0) {
            return super.onKeyMultiple(i, i2, keyEvent);
        }
        if (iDoKeyDown == -1) {
            return true;
        }
        int i3 = i2 - 1;
        KeyEvent keyEventChangeAction2 = KeyEvent.changeAction(keyEvent, 1);
        if (iDoKeyDown == 1) {
            this.mEditor.mKeyListener.onKeyUp(this, (Editable) this.mText, i, keyEventChangeAction2);
            while (true) {
                i3--;
                if (i3 <= 0) {
                    break;
                }
                this.mEditor.mKeyListener.onKeyDown(this, (Editable) this.mText, i, keyEventChangeAction);
                this.mEditor.mKeyListener.onKeyUp(this, (Editable) this.mText, i, keyEventChangeAction2);
            }
            hideErrorIfUnchanged();
        } else if (iDoKeyDown == 2) {
            this.mMovement.onKeyUp(this, (Spannable) this.mText, i, keyEventChangeAction2);
            while (true) {
                i3--;
                if (i3 <= 0) {
                    break;
                }
                this.mMovement.onKeyDown(this, (Spannable) this.mText, i, keyEventChangeAction);
                this.mMovement.onKeyUp(this, (Spannable) this.mText, i, keyEventChangeAction2);
            }
        }
        return true;
    }

    private boolean shouldAdvanceFocusOnEnter() {
        int i;
        if (getKeyListener() == null) {
            return false;
        }
        if (this.mSingleLine) {
            return true;
        }
        Editor editor = this.mEditor;
        return editor != null && (editor.mInputType & 15) == 1 && ((i = this.mEditor.mInputType & InputType.TYPE_MASK_VARIATION) == 32 || i == 48);
    }

    private boolean shouldAdvanceFocusOnTab() {
        Editor editor;
        int i;
        return getKeyListener() == null || this.mSingleLine || (editor = this.mEditor) == null || (editor.mInputType & 15) != 1 || !((i = this.mEditor.mInputType & InputType.TYPE_MASK_VARIATION) == 262144 || i == 131072);
    }

    /* JADX WARN: Removed duplicated region for block: B:74:0x00c8  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private int doKeyDown(int r6, android.view.KeyEvent r7, android.view.KeyEvent r8) {
        /*
            Method dump skipped, instruction units count: 290
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.widget.TextView.doKeyDown(int, android.view.KeyEvent, android.view.KeyEvent):int");
    }

    public void resetErrorChangedFlag() {
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.mErrorWasChanged = false;
        }
    }

    public void hideErrorIfUnchanged() {
        Editor editor = this.mEditor;
        if (editor == null || editor.mError == null || this.mEditor.mErrorWasChanged) {
            return;
        }
        setError(null, null);
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        InputMethodManager inputMethodManagerPeekInstance;
        if (!isEnabled()) {
            return super.onKeyUp(i, keyEvent);
        }
        if (!KeyEvent.isModifierKey(i)) {
            this.mPreventDefaultMovement = false;
        }
        if (i == 23) {
            if (keyEvent.hasNoModifiers() && !hasOnClickListeners() && this.mMovement != null && (this.mText instanceof Editable) && this.mLayout != null && onCheckIsTextEditor()) {
                InputMethodManager inputMethodManagerPeekInstance2 = InputMethodManager.peekInstance();
                viewClicked(inputMethodManagerPeekInstance2);
                if (inputMethodManagerPeekInstance2 != null && getShowSoftInputOnFocus()) {
                    inputMethodManagerPeekInstance2.showSoftInput(this, 0);
                }
            }
            return super.onKeyUp(i, keyEvent);
        }
        if (i == 66 && keyEvent.hasNoModifiers()) {
            Editor editor = this.mEditor;
            if (editor != null && editor.mInputContentType != null && this.mEditor.mInputContentType.onEditorActionListener != null && this.mEditor.mInputContentType.enterDown) {
                this.mEditor.mInputContentType.enterDown = false;
                if (this.mEditor.mInputContentType.onEditorActionListener.onEditorAction(this, 0, keyEvent)) {
                    return true;
                }
            }
            if (((keyEvent.getFlags() & 16) != 0 || shouldAdvanceFocusOnEnter()) && !hasOnClickListeners()) {
                View viewFocusSearch = focusSearch(130);
                if (viewFocusSearch != null) {
                    if (!viewFocusSearch.requestFocus(130)) {
                        throw new IllegalStateException("focus search returned a view that wasn't able to take focus!");
                    }
                    super.onKeyUp(i, keyEvent);
                    return true;
                }
                if ((keyEvent.getFlags() & 16) != 0 && (inputMethodManagerPeekInstance = InputMethodManager.peekInstance()) != null && inputMethodManagerPeekInstance.isActive(this)) {
                    inputMethodManagerPeekInstance.hideSoftInputFromWindow(getWindowToken(), 0);
                }
            }
            return super.onKeyUp(i, keyEvent);
        }
        Editor editor2 = this.mEditor;
        if (editor2 != null && editor2.mKeyListener != null && this.mEditor.mKeyListener.onKeyUp(this, (Editable) this.mText, i, keyEvent)) {
            return true;
        }
        MovementMethod movementMethod = this.mMovement;
        if (movementMethod == null || this.mLayout == null || !movementMethod.onKeyUp(this, (Spannable) this.mText, i, keyEvent)) {
            return super.onKeyUp(i, keyEvent);
        }
        return true;
    }

    @Override // android.view.View
    public boolean onCheckIsTextEditor() {
        Editor editor = this.mEditor;
        return (editor == null || editor.mInputType == 0) ? false : true;
    }

    @Override // android.view.View
    public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
        if (!onCheckIsTextEditor() || !isEnabled()) {
            return null;
        }
        this.mEditor.createInputMethodStateIfNeeded();
        editorInfo.inputType = getInputType();
        if (this.mEditor.mInputContentType != null) {
            editorInfo.imeOptions = this.mEditor.mInputContentType.imeOptions;
            editorInfo.privateImeOptions = this.mEditor.mInputContentType.privateImeOptions;
            editorInfo.actionLabel = this.mEditor.mInputContentType.imeActionLabel;
            editorInfo.actionId = this.mEditor.mInputContentType.imeActionId;
            editorInfo.extras = this.mEditor.mInputContentType.extras;
        } else {
            editorInfo.imeOptions = 0;
        }
        if (focusSearch(130) != null) {
            editorInfo.imeOptions |= 134217728;
        }
        if (focusSearch(33) != null) {
            editorInfo.imeOptions |= 67108864;
        }
        if ((editorInfo.imeOptions & 255) == 0) {
            if ((editorInfo.imeOptions & 134217728) != 0) {
                editorInfo.imeOptions |= 5;
            } else {
                editorInfo.imeOptions |= 6;
            }
            if (!shouldAdvanceFocusOnEnter()) {
                editorInfo.imeOptions |= 1073741824;
            }
        }
        if (isMultilineInputType(editorInfo.inputType)) {
            editorInfo.imeOptions |= 1073741824;
        }
        editorInfo.hintText = this.mHint;
        if (!(this.mText instanceof Editable)) {
            return null;
        }
        EditableInputConnection editableInputConnection = new EditableInputConnection(this);
        editorInfo.initialSelStart = getSelectionStart();
        editorInfo.initialSelEnd = getSelectionEnd();
        editorInfo.initialCapsMode = editableInputConnection.getCursorCapsMode(getInputType());
        return editableInputConnection;
    }

    public boolean extractText(ExtractedTextRequest extractedTextRequest, ExtractedText extractedText) {
        createEditorIfNeeded();
        return this.mEditor.extractText(extractedTextRequest, extractedText);
    }

    static void removeParcelableSpans(Spannable spannable, int i, int i2) {
        Object[] spans = spannable.getSpans(i, i2, ParcelableSpan.class);
        int length = spans.length;
        while (length > 0) {
            length--;
            spannable.removeSpan(spans[length]);
        }
    }

    public void setExtractedText(ExtractedText extractedText) {
        Editable editableText = getEditableText();
        if (extractedText.text != null) {
            if (editableText == null) {
                setText(extractedText.text, BufferType.EDITABLE);
            } else if (extractedText.partialStartOffset < 0) {
                removeParcelableSpans(editableText, 0, editableText.length());
                editableText.replace(0, editableText.length(), extractedText.text);
            } else {
                int length = editableText.length();
                int i = extractedText.partialStartOffset;
                if (i > length) {
                    i = length;
                }
                int i2 = extractedText.partialEndOffset;
                if (i2 <= length) {
                    length = i2;
                }
                removeParcelableSpans(editableText, i, length);
                editableText.replace(i, length, extractedText.text);
            }
        }
        Spannable spannable = (Spannable) getText();
        int length2 = spannable.length();
        int i3 = extractedText.selectionStart;
        if (i3 < 0) {
            i3 = 0;
        } else if (i3 > length2) {
            i3 = length2;
        }
        int i4 = extractedText.selectionEnd;
        Selection.setSelection(spannable, i3, i4 >= 0 ? i4 > length2 ? length2 : i4 : 0);
        if ((extractedText.flags & 2) != 0) {
            MetaKeyKeyListener.startSelecting(this, spannable);
        } else {
            MetaKeyKeyListener.stopSelecting(this, spannable);
        }
    }

    public void setExtracting(ExtractedTextRequest extractedTextRequest) {
        if (this.mEditor.mInputMethodState != null) {
            this.mEditor.mInputMethodState.mExtractedTextRequest = extractedTextRequest;
        }
        this.mEditor.hideControllers();
    }

    public void onCommitCorrection(CorrectionInfo correctionInfo) {
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.onCommitCorrection(correctionInfo);
        }
    }

    public void beginBatchEdit() {
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.beginBatchEdit();
        }
    }

    public void endBatchEdit() {
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.endBatchEdit();
        }
    }

    private void nullLayouts() {
        Layout layout = this.mLayout;
        if ((layout instanceof BoringLayout) && this.mSavedLayout == null) {
            this.mSavedLayout = (BoringLayout) layout;
        }
        Layout layout2 = this.mHintLayout;
        if ((layout2 instanceof BoringLayout) && this.mSavedHintLayout == null) {
            this.mSavedHintLayout = (BoringLayout) layout2;
        }
        this.mHintLayout = null;
        this.mLayout = null;
        this.mSavedMarqueeModeLayout = null;
        this.mHintBoring = null;
        this.mBoring = null;
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.prepareCursorControllers();
        }
    }

    private void assumeLayout() {
        int compoundPaddingLeft = ((this.mRight - this.mLeft) - getCompoundPaddingLeft()) - getCompoundPaddingRight();
        if (compoundPaddingLeft < 1) {
            compoundPaddingLeft = 0;
        }
        int i = compoundPaddingLeft;
        int i2 = this.mHorizontallyScrolling ? 1048576 : i;
        BoringLayout.Metrics metrics = UNKNOWN_BORING;
        makeNewLayout(i2, i, metrics, metrics, i, false);
    }

    private Layout.Alignment getLayoutAlignment() {
        switch (getTextAlignment()) {
            case 1:
                int i = this.mGravity & 8388615;
                if (i == 1) {
                    return Layout.Alignment.ALIGN_CENTER;
                }
                if (i == 3) {
                    return Layout.Alignment.ALIGN_LEFT;
                }
                if (i == 5) {
                    return Layout.Alignment.ALIGN_RIGHT;
                }
                if (i == 8388611) {
                    return Layout.Alignment.ALIGN_NORMAL;
                }
                if (i == 8388613) {
                    return Layout.Alignment.ALIGN_OPPOSITE;
                }
                return Layout.Alignment.ALIGN_NORMAL;
            case 2:
                return Layout.Alignment.ALIGN_NORMAL;
            case 3:
                return Layout.Alignment.ALIGN_OPPOSITE;
            case 4:
                return Layout.Alignment.ALIGN_CENTER;
            case 5:
                return getLayoutDirection() == 1 ? Layout.Alignment.ALIGN_RIGHT : Layout.Alignment.ALIGN_LEFT;
            case 6:
                return getLayoutDirection() == 1 ? Layout.Alignment.ALIGN_LEFT : Layout.Alignment.ALIGN_RIGHT;
            default:
                return Layout.Alignment.ALIGN_NORMAL;
        }
    }

    protected void makeNewLayout(int i, int i2, BoringLayout.Metrics metrics, BoringLayout.Metrics metrics2, int i3, boolean z) {
        int i4;
        boolean z2;
        int i5;
        BoringLayout.Metrics metrics3;
        boolean z3;
        int i6;
        stopMarquee();
        this.mOldMaximum = this.mMaximum;
        this.mOldMaxMode = this.mMaxMode;
        this.mHighlightPathBogus = true;
        int i7 = i < 0 ? 0 : i;
        int i8 = i2 < 0 ? 0 : i2;
        Layout.Alignment layoutAlignment = getLayoutAlignment();
        boolean z4 = this.mSingleLine && this.mLayout != null && (layoutAlignment == Layout.Alignment.ALIGN_NORMAL || layoutAlignment == Layout.Alignment.ALIGN_OPPOSITE);
        int paragraphDirection = z4 ? this.mLayout.getParagraphDirection(0) : 0;
        boolean z5 = this.mEllipsize != null && getKeyListener() == null;
        boolean z6 = this.mEllipsize == TextUtils.TruncateAt.MARQUEE && this.mMarqueeFadeMode != 0;
        TextUtils.TruncateAt truncateAt = this.mEllipsize;
        if (truncateAt == TextUtils.TruncateAt.MARQUEE && this.mMarqueeFadeMode == 1) {
            truncateAt = TextUtils.TruncateAt.END_SMALL;
        }
        TextUtils.TruncateAt truncateAt2 = truncateAt;
        if (this.mTextDir == null) {
            this.mTextDir = getTextDirectionHeuristic();
        }
        this.mLayout = makeSingleLayout(i7, metrics, i3, layoutAlignment, z5, truncateAt2, truncateAt2 == this.mEllipsize);
        if (z6) {
            this.mSavedMarqueeModeLayout = makeSingleLayout(i7, metrics, i3, layoutAlignment, z5, truncateAt2 == TextUtils.TruncateAt.MARQUEE ? TextUtils.TruncateAt.END : TextUtils.TruncateAt.MARQUEE, truncateAt2 != this.mEllipsize);
        }
        boolean z7 = this.mEllipsize != null;
        this.mHintLayout = null;
        CharSequence charSequence = this.mHint;
        if (charSequence == null) {
            i4 = paragraphDirection;
            z2 = true;
            i5 = i3;
        } else {
            int i9 = z7 ? i7 : i8;
            if (metrics2 == UNKNOWN_BORING) {
                BoringLayout.Metrics metricsIsBoring = BoringLayout.isBoring(charSequence, this.mTextPaint, this.mTextDir, this.mHintBoring);
                if (metricsIsBoring != null) {
                    this.mHintBoring = metricsIsBoring;
                }
                metrics3 = metricsIsBoring;
            } else {
                metrics3 = metrics2;
            }
            if (metrics3 == null) {
                i4 = paragraphDirection;
                z2 = true;
                i5 = i3;
                if (z7) {
                    CharSequence charSequence2 = this.mHint;
                    this.mHintLayout = new StaticLayout(charSequence2, 0, charSequence2.length(), this.mTextPaint, i9, layoutAlignment, this.mTextDir, this.mSpacingMult, this.mSpacingAdd, this.mIncludePad, this.mEllipsize, i3, this.mMaxMode == 1 ? this.mMaximum : Integer.MAX_VALUE);
                } else {
                    this.mHintLayout = new StaticLayout(this.mHint, this.mTextPaint, i9, layoutAlignment, this.mTextDir, this.mSpacingMult, this.mSpacingAdd, this.mIncludePad);
                }
            } else if (metrics3.width <= i9 && (!z7 || metrics3.width <= i3)) {
                BoringLayout boringLayout = this.mSavedHintLayout;
                if (boringLayout != null) {
                    this.mHintLayout = boringLayout.replaceOrMake(this.mHint, this.mTextPaint, i9, layoutAlignment, this.mSpacingMult, this.mSpacingAdd, metrics3, this.mIncludePad);
                } else {
                    this.mHintLayout = BoringLayout.make(this.mHint, this.mTextPaint, i9, layoutAlignment, this.mSpacingMult, this.mSpacingAdd, metrics3, this.mIncludePad);
                }
                this.mSavedHintLayout = (BoringLayout) this.mHintLayout;
                i4 = paragraphDirection;
                z2 = true;
                i5 = i3;
            } else if (!z7 || metrics3.width > i9) {
                i4 = paragraphDirection;
                z2 = true;
                i5 = i3;
                if (z7) {
                    CharSequence charSequence3 = this.mHint;
                    int length = charSequence3.length();
                    TextPaint textPaint = this.mTextPaint;
                    TextDirectionHeuristic textDirectionHeuristic = this.mTextDir;
                    float f = this.mSpacingMult;
                    float f2 = this.mSpacingAdd;
                    boolean z8 = this.mIncludePad;
                    TextUtils.TruncateAt truncateAt3 = this.mEllipsize;
                    if (this.mMaxMode == 1) {
                        i6 = this.mMaximum;
                        z3 = z8;
                    } else {
                        z3 = z8;
                        i6 = Integer.MAX_VALUE;
                    }
                    this.mHintLayout = new StaticLayout(charSequence3, 0, length, textPaint, i9, layoutAlignment, textDirectionHeuristic, f, f2, z3, truncateAt3, i3, i6);
                } else {
                    this.mHintLayout = new StaticLayout(this.mHint, this.mTextPaint, i9, layoutAlignment, this.mTextDir, this.mSpacingMult, this.mSpacingAdd, this.mIncludePad);
                }
            } else {
                BoringLayout boringLayout2 = this.mSavedHintLayout;
                if (boringLayout2 != null) {
                    i4 = paragraphDirection;
                    this.mHintLayout = boringLayout2.replaceOrMake(this.mHint, this.mTextPaint, i9, layoutAlignment, this.mSpacingMult, this.mSpacingAdd, metrics3, this.mIncludePad, this.mEllipsize, i3);
                    i5 = i3;
                    z2 = true;
                } else {
                    i4 = paragraphDirection;
                    z2 = true;
                    this.mHintLayout = BoringLayout.make(this.mHint, this.mTextPaint, i9, layoutAlignment, this.mSpacingMult, this.mSpacingAdd, metrics3, this.mIncludePad, this.mEllipsize, i3);
                    i5 = i3;
                }
            }
        }
        if (z || (z4 && i4 != this.mLayout.getParagraphDirection(0))) {
            registerForPreDraw();
        }
        if (this.mEllipsize == TextUtils.TruncateAt.MARQUEE && !compressText(i5)) {
            int i10 = this.mLayoutParams.height;
            if (i10 != -2 && i10 != -1) {
                startMarquee();
            } else {
                this.mRestartMarquee = z2;
            }
        }
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.prepareCursorControllers();
        }
    }

    private Layout makeSingleLayout(int i, BoringLayout.Metrics metrics, int i2, Layout.Alignment alignment, boolean z, TextUtils.TruncateAt truncateAt, boolean z2) {
        BoringLayout.Metrics metrics2;
        StaticLayout staticLayout;
        BoringLayout boringLayout;
        BoringLayout boringLayoutMake;
        BoringLayout boringLayout2;
        if (this.mText instanceof Spannable) {
            return new DynamicLayout(this.mText, this.mTransformed, this.mTextPaint, i, alignment, this.mTextDir, this.mSpacingMult, this.mSpacingAdd, this.mIncludePad, getKeyListener() == null ? truncateAt : null, i2);
        }
        if (metrics == UNKNOWN_BORING) {
            BoringLayout.Metrics metricsIsBoring = BoringLayout.isBoring(this.mTransformed, this.mTextPaint, this.mTextDir, this.mBoring);
            if (metricsIsBoring != null) {
                this.mBoring = metricsIsBoring;
            }
            metrics2 = metricsIsBoring;
        } else {
            metrics2 = metrics;
        }
        if (metrics2 != null) {
            if (metrics2.width <= i && (truncateAt == null || metrics2.width <= i2)) {
                if (z2 && (boringLayout2 = this.mSavedLayout) != null) {
                    boringLayoutMake = boringLayout2.replaceOrMake(this.mTransformed, this.mTextPaint, i, alignment, this.mSpacingMult, this.mSpacingAdd, metrics2, this.mIncludePad);
                } else {
                    boringLayoutMake = BoringLayout.make(this.mTransformed, this.mTextPaint, i, alignment, this.mSpacingMult, this.mSpacingAdd, metrics2, this.mIncludePad);
                }
                BoringLayout boringLayout3 = boringLayoutMake;
                if (!z2) {
                    return boringLayout3;
                }
                this.mSavedLayout = boringLayout3;
                return boringLayout3;
            }
            if (z && metrics2.width <= i) {
                if (z2 && (boringLayout = this.mSavedLayout) != null) {
                    return boringLayout.replaceOrMake(this.mTransformed, this.mTextPaint, i, alignment, this.mSpacingMult, this.mSpacingAdd, metrics2, this.mIncludePad, truncateAt, i2);
                }
                return BoringLayout.make(this.mTransformed, this.mTextPaint, i, alignment, this.mSpacingMult, this.mSpacingAdd, metrics2, this.mIncludePad, truncateAt, i2);
            }
            if (z) {
                CharSequence charSequence = this.mTransformed;
                staticLayout = new StaticLayout(charSequence, 0, charSequence.length(), this.mTextPaint, i, alignment, this.mTextDir, this.mSpacingMult, this.mSpacingAdd, this.mIncludePad, truncateAt, i2, this.mMaxMode == 1 ? this.mMaximum : Integer.MAX_VALUE);
            } else {
                return new StaticLayout(this.mTransformed, this.mTextPaint, i, alignment, this.mTextDir, this.mSpacingMult, this.mSpacingAdd, this.mIncludePad);
            }
        } else if (z) {
            CharSequence charSequence2 = this.mTransformed;
            staticLayout = new StaticLayout(charSequence2, 0, charSequence2.length(), this.mTextPaint, i, alignment, this.mTextDir, this.mSpacingMult, this.mSpacingAdd, this.mIncludePad, truncateAt, i2, this.mMaxMode == 1 ? this.mMaximum : Integer.MAX_VALUE);
        } else {
            return new StaticLayout(this.mTransformed, this.mTextPaint, i, alignment, this.mTextDir, this.mSpacingMult, this.mSpacingAdd, this.mIncludePad);
        }
        return staticLayout;
    }

    private boolean compressText(float f) {
        if (!isHardwareAccelerated() && f > 0.0f && this.mLayout != null && getLineCount() == 1 && !this.mUserSetTextScaleX && this.mTextPaint.getTextScaleX() == 1.0f) {
            float lineWidth = ((this.mLayout.getLineWidth(0) + 1.0f) - f) / f;
            if (lineWidth > 0.0f && lineWidth <= 0.07f) {
                this.mTextPaint.setTextScaleX((1.0f - lineWidth) - 0.005f);
                post(new Runnable() { // from class: android.widget.TextView.2
                    @Override // java.lang.Runnable
                    public void run() {
                        TextView.this.requestLayout();
                    }
                });
                return true;
            }
        }
        return false;
    }

    private static int desired(Layout layout) {
        int lineCount = layout.getLineCount();
        CharSequence text = layout.getText();
        for (int i = 0; i < lineCount - 1; i++) {
            if (text.charAt(layout.getLineEnd(i) - 1) != '\n') {
                return -1;
            }
        }
        float fMax = 0.0f;
        for (int i2 = 0; i2 < lineCount; i2++) {
            fMax = Math.max(fMax, layout.getLineWidth(i2));
        }
        return (int) FloatMath.ceil(fMax);
    }

    public void setIncludeFontPadding(boolean z) {
        if (this.mIncludePad != z) {
            this.mIncludePad = z;
            if (this.mLayout != null) {
                nullLayouts();
                requestLayout();
                invalidate();
            }
        }
    }

    public boolean getIncludeFontPadding() {
        return this.mIncludePad;
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        BoringLayout.Metrics metricsIsBoring;
        boolean z;
        int iMax;
        int iMin;
        int iMax2;
        int i3;
        BoringLayout.Metrics metrics;
        int iMin2;
        BoringLayout.Metrics metrics2;
        BoringLayout.Metrics metricsIsBoring2;
        int i4;
        int mode = View.MeasureSpec.getMode(i);
        int mode2 = View.MeasureSpec.getMode(i2);
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        BoringLayout.Metrics metrics3 = UNKNOWN_BORING;
        if (this.mTextDir == null) {
            this.mTextDir = getTextDirectionHeuristic();
        }
        if (mode == 1073741824) {
            iMin2 = size;
            metrics = metrics3;
            metrics2 = metrics;
            z = false;
            i3 = -1;
        } else {
            Layout layout = this.mLayout;
            int iDesired = (layout == null || this.mEllipsize != null) ? -1 : desired(layout);
            if (iDesired < 0) {
                metricsIsBoring = BoringLayout.isBoring(this.mTransformed, this.mTextPaint, this.mTextDir, this.mBoring);
                if (metricsIsBoring != null) {
                    this.mBoring = metricsIsBoring;
                }
                z = false;
            } else {
                metricsIsBoring = metrics3;
                z = true;
            }
            if (metricsIsBoring == null || metricsIsBoring == metrics3) {
                if (iDesired < 0) {
                    iDesired = (int) FloatMath.ceil(Layout.getDesiredWidth(this.mTransformed, this.mTextPaint));
                }
                iMax = iDesired;
            } else {
                iMax = metricsIsBoring.width;
            }
            Drawables drawables = this.mDrawables;
            if (drawables != null) {
                iMax = Math.max(Math.max(iMax, drawables.mDrawableWidthTop), drawables.mDrawableWidthBottom);
            }
            if (this.mHint != null) {
                Layout layout2 = this.mHintLayout;
                int iDesired2 = (layout2 == null || this.mEllipsize != null) ? -1 : desired(layout2);
                if (iDesired2 < 0) {
                    metricsIsBoring2 = BoringLayout.isBoring(this.mHint, this.mTextPaint, this.mTextDir, this.mHintBoring);
                    if (metricsIsBoring2 != null) {
                        this.mHintBoring = metricsIsBoring2;
                    }
                } else {
                    metricsIsBoring2 = metrics3;
                }
                if (metricsIsBoring2 == null || metricsIsBoring2 == metrics3) {
                    if (iDesired2 < 0) {
                        iDesired2 = (int) FloatMath.ceil(Layout.getDesiredWidth(this.mHint, this.mTextPaint));
                    }
                    i4 = iDesired2;
                } else {
                    i4 = metricsIsBoring2.width;
                }
                if (i4 > iMax) {
                    iMax = i4;
                }
                metrics3 = metricsIsBoring2;
            }
            int compoundPaddingLeft = iMax + getCompoundPaddingLeft() + getCompoundPaddingRight();
            if (this.mMaxWidthMode == 1) {
                iMin = Math.min(compoundPaddingLeft, this.mMaxWidth * getLineHeight());
            } else {
                iMin = Math.min(compoundPaddingLeft, this.mMaxWidth);
            }
            if (this.mMinWidthMode == 1) {
                iMax2 = Math.max(iMin, this.mMinWidth * getLineHeight());
            } else {
                iMax2 = Math.max(iMin, this.mMinWidth);
            }
            int iMax3 = Math.max(iMax2, getSuggestedMinimumWidth());
            if (mode == Integer.MIN_VALUE) {
                iMin2 = Math.min(size, iMax3);
                i3 = iDesired;
                metrics = metricsIsBoring;
            } else {
                i3 = iDesired;
                metrics = metricsIsBoring;
                iMin2 = iMax3;
            }
            metrics2 = metrics3;
        }
        int compoundPaddingLeft2 = (iMin2 - getCompoundPaddingLeft()) - getCompoundPaddingRight();
        int i5 = this.mHorizontallyScrolling ? 1048576 : compoundPaddingLeft2;
        Layout layout3 = this.mHintLayout;
        int width = layout3 == null ? i5 : layout3.getWidth();
        Layout layout4 = this.mLayout;
        if (layout4 == null) {
            makeNewLayout(i5, i5, metrics, metrics2, (iMin2 - getCompoundPaddingLeft()) - getCompoundPaddingRight(), false);
        } else {
            boolean z2 = (layout4.getWidth() == i5 && width == i5 && this.mLayout.getEllipsizedWidth() == (iMin2 - getCompoundPaddingLeft()) - getCompoundPaddingRight()) ? false : true;
            boolean z3 = this.mHint == null && this.mEllipsize == null && i5 > this.mLayout.getWidth() && ((this.mLayout instanceof BoringLayout) || (z && i3 >= 0 && i3 <= i5));
            boolean z4 = (this.mMaxMode == this.mOldMaxMode && this.mMaximum == this.mOldMaximum) ? false : true;
            if (z2 || z4) {
                if (!z4 && z3) {
                    this.mLayout.increaseWidthTo(i5);
                } else {
                    makeNewLayout(i5, i5, metrics, metrics2, (iMin2 - getCompoundPaddingLeft()) - getCompoundPaddingRight(), false);
                }
            }
        }
        if (mode2 == 1073741824) {
            this.mDesiredHeightAtMeasure = -1;
        } else {
            int desiredHeight = getDesiredHeight();
            this.mDesiredHeightAtMeasure = desiredHeight;
            size2 = mode2 == Integer.MIN_VALUE ? Math.min(desiredHeight, size2) : desiredHeight;
        }
        int compoundPaddingTop = (size2 - getCompoundPaddingTop()) - getCompoundPaddingBottom();
        if (this.mMaxMode == 1) {
            int lineCount = this.mLayout.getLineCount();
            int i6 = this.mMaximum;
            if (lineCount > i6) {
                compoundPaddingTop = Math.min(compoundPaddingTop, this.mLayout.getLineTop(i6));
            }
        }
        if (this.mMovement != null || this.mLayout.getWidth() > compoundPaddingLeft2 || this.mLayout.getHeight() > compoundPaddingTop) {
            registerForPreDraw();
        } else {
            scrollTo(0, 0);
        }
        setMeasuredDimension(iMin2, size2);
    }

    private int getDesiredHeight() {
        return Math.max(getDesiredHeight(this.mLayout, true), getDesiredHeight(this.mHintLayout, this.mEllipsize != null));
    }

    private int getDesiredHeight(Layout layout, boolean z) {
        int i;
        if (layout == null) {
            return 0;
        }
        int lineCount = layout.getLineCount();
        int compoundPaddingTop = getCompoundPaddingTop() + getCompoundPaddingBottom();
        int lineTop = layout.getLineTop(lineCount);
        Drawables drawables = this.mDrawables;
        if (drawables != null) {
            lineTop = Math.max(Math.max(lineTop, drawables.mDrawableHeightLeft), drawables.mDrawableHeightRight);
        }
        int iMin = lineTop + compoundPaddingTop;
        if (this.mMaxMode != 1) {
            iMin = Math.min(iMin, this.mMaximum);
        } else if (z && lineCount > (i = this.mMaximum)) {
            int lineTop2 = layout.getLineTop(i);
            if (drawables != null) {
                lineTop2 = Math.max(Math.max(lineTop2, drawables.mDrawableHeightLeft), drawables.mDrawableHeightRight);
            }
            iMin = lineTop2 + compoundPaddingTop;
            lineCount = this.mMaximum;
        }
        if (this.mMinMode == 1) {
            if (lineCount < this.mMinimum) {
                iMin += getLineHeight() * (this.mMinimum - lineCount);
            }
        } else {
            iMin = Math.max(iMin, this.mMinimum);
        }
        return Math.max(iMin, getSuggestedMinimumHeight());
    }

    /* JADX WARN: Removed duplicated region for block: B:19:0x0036 A[PHI: r2
      0x0036: PHI (r2v1 boolean) = (r2v0 boolean), (r2v2 boolean), (r2v2 boolean), (r2v2 boolean), (r2v2 boolean) binds: [B:3:0x0004, B:13:0x0027, B:15:0x002b, B:17:0x0033, B:10:0x001f] A[DONT_GENERATE, DONT_INLINE]] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void checkForResize() {
        /*
            r4 = this;
            android.text.Layout r0 = r4.mLayout
            r1 = 1
            r2 = 0
            if (r0 == 0) goto L36
            android.view.ViewGroup$LayoutParams r0 = r4.mLayoutParams
            int r0 = r0.width
            r3 = -2
            if (r0 != r3) goto L11
            r4.invalidate()
            r2 = r1
        L11:
            android.view.ViewGroup$LayoutParams r0 = r4.mLayoutParams
            int r0 = r0.height
            if (r0 != r3) goto L22
            int r0 = r4.getDesiredHeight()
            int r3 = r4.getHeight()
            if (r0 == r3) goto L36
            goto L37
        L22:
            android.view.ViewGroup$LayoutParams r0 = r4.mLayoutParams
            int r0 = r0.height
            r3 = -1
            if (r0 != r3) goto L36
            int r0 = r4.mDesiredHeightAtMeasure
            if (r0 < 0) goto L36
            int r0 = r4.getDesiredHeight()
            int r3 = r4.mDesiredHeightAtMeasure
            if (r0 == r3) goto L36
            goto L37
        L36:
            r1 = r2
        L37:
            if (r1 == 0) goto L3c
            r4.requestLayout()
        L3c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.widget.TextView.checkForResize():void");
    }

    private void checkForRelayout() {
        Layout layout;
        if ((this.mLayoutParams.width != -2 || (this.mMaxWidthMode == this.mMinWidthMode && this.mMaxWidth == this.mMinWidth)) && ((this.mHint == null || this.mHintLayout != null) && ((this.mRight - this.mLeft) - getCompoundPaddingLeft()) - getCompoundPaddingRight() > 0)) {
            int height = this.mLayout.getHeight();
            int width = this.mLayout.getWidth();
            Layout layout2 = this.mHintLayout;
            int width2 = layout2 == null ? 0 : layout2.getWidth();
            BoringLayout.Metrics metrics = UNKNOWN_BORING;
            makeNewLayout(width, width2, metrics, metrics, ((this.mRight - this.mLeft) - getCompoundPaddingLeft()) - getCompoundPaddingRight(), false);
            if (this.mEllipsize != TextUtils.TruncateAt.MARQUEE) {
                if (this.mLayoutParams.height != -2 && this.mLayoutParams.height != -1) {
                    invalidate();
                    return;
                } else if (this.mLayout.getHeight() == height && ((layout = this.mHintLayout) == null || layout.getHeight() == height)) {
                    invalidate();
                    return;
                }
            }
            requestLayout();
            invalidate();
            return;
        }
        nullLayouts();
        requestLayout();
        invalidate();
    }

    @Override // android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        int i5 = this.mDeferScroll;
        if (i5 >= 0) {
            this.mDeferScroll = -1;
            bringPointIntoView(Math.min(i5, this.mText.length()));
        }
    }

    private boolean isShowingHint() {
        return TextUtils.isEmpty(this.mText) && !TextUtils.isEmpty(this.mHint);
    }

    private boolean bringTextIntoView() {
        int iFloor;
        int iCeil;
        Layout layout = isShowingHint() ? this.mHintLayout : this.mLayout;
        int lineCount = (this.mGravity & 112) == 80 ? layout.getLineCount() - 1 : 0;
        Layout.Alignment paragraphAlignment = layout.getParagraphAlignment(lineCount);
        int paragraphDirection = layout.getParagraphDirection(lineCount);
        int compoundPaddingLeft = ((this.mRight - this.mLeft) - getCompoundPaddingLeft()) - getCompoundPaddingRight();
        int extendedPaddingTop = ((this.mBottom - this.mTop) - getExtendedPaddingTop()) - getExtendedPaddingBottom();
        int height = layout.getHeight();
        if (paragraphAlignment == Layout.Alignment.ALIGN_NORMAL) {
            paragraphAlignment = paragraphDirection == 1 ? Layout.Alignment.ALIGN_LEFT : Layout.Alignment.ALIGN_RIGHT;
        } else if (paragraphAlignment == Layout.Alignment.ALIGN_OPPOSITE) {
            paragraphAlignment = paragraphDirection == 1 ? Layout.Alignment.ALIGN_RIGHT : Layout.Alignment.ALIGN_LEFT;
        }
        if (paragraphAlignment == Layout.Alignment.ALIGN_CENTER) {
            iFloor = (int) FloatMath.floor(layout.getLineLeft(lineCount));
            iCeil = (int) FloatMath.ceil(layout.getLineRight(lineCount));
            if (iCeil - iFloor < compoundPaddingLeft) {
                iCeil = (iCeil + iFloor) / 2;
                compoundPaddingLeft /= 2;
            } else if (paragraphDirection < 0) {
            }
            iFloor = iCeil - compoundPaddingLeft;
        } else if (paragraphAlignment == Layout.Alignment.ALIGN_RIGHT) {
            iCeil = (int) FloatMath.ceil(layout.getLineRight(lineCount));
            iFloor = iCeil - compoundPaddingLeft;
        } else {
            iFloor = (int) FloatMath.floor(layout.getLineLeft(lineCount));
        }
        int i = (height >= extendedPaddingTop && (this.mGravity & 112) == 80) ? height - extendedPaddingTop : 0;
        if (iFloor == this.mScrollX && i == this.mScrollY) {
            return false;
        }
        scrollTo(iFloor, i);
        return true;
    }

    /* JADX WARN: Removed duplicated region for block: B:66:0x00e7  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean bringPointIntoView(int r17) {
        /*
            Method dump skipped, instruction units count: 427
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.widget.TextView.bringPointIntoView(int):boolean");
    }

    /* JADX INFO: renamed from: android.widget.TextView$4, reason: invalid class name */
    static /* synthetic */ class AnonymousClass4 {
        static final /* synthetic */ int[] $SwitchMap$android$text$Layout$Alignment;

        static {
            int[] iArr = new int[Layout.Alignment.values().length];
            $SwitchMap$android$text$Layout$Alignment = iArr;
            try {
                iArr[Layout.Alignment.ALIGN_LEFT.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$android$text$Layout$Alignment[Layout.Alignment.ALIGN_RIGHT.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$android$text$Layout$Alignment[Layout.Alignment.ALIGN_NORMAL.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$android$text$Layout$Alignment[Layout.Alignment.ALIGN_OPPOSITE.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$android$text$Layout$Alignment[Layout.Alignment.ALIGN_CENTER.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
        }
    }

    public boolean moveCursorToVisibleOffset() {
        int selectionStart;
        if (!(this.mText instanceof Spannable) || (selectionStart = getSelectionStart()) != getSelectionEnd()) {
            return false;
        }
        int lineForOffset = this.mLayout.getLineForOffset(selectionStart);
        int lineTop = this.mLayout.getLineTop(lineForOffset);
        int lineTop2 = this.mLayout.getLineTop(lineForOffset + 1);
        int extendedPaddingTop = ((this.mBottom - this.mTop) - getExtendedPaddingTop()) - getExtendedPaddingBottom();
        int i = lineTop2 - lineTop;
        int i2 = i / 2;
        int i3 = extendedPaddingTop / 4;
        if (i2 > i3) {
            i2 = i3;
        }
        int i4 = this.mScrollY;
        int i5 = i4 + i2;
        if (lineTop < i5) {
            lineForOffset = this.mLayout.getLineForVertical(i5 + i);
        } else {
            int i6 = (extendedPaddingTop + i4) - i2;
            if (lineTop2 > i6) {
                lineForOffset = this.mLayout.getLineForVertical(i6 - i);
            }
        }
        int compoundPaddingLeft = ((this.mRight - this.mLeft) - getCompoundPaddingLeft()) - getCompoundPaddingRight();
        int offsetForHorizontal = this.mLayout.getOffsetForHorizontal(lineForOffset, this.mScrollX);
        int offsetForHorizontal2 = this.mLayout.getOffsetForHorizontal(lineForOffset, compoundPaddingLeft + r4);
        int i7 = offsetForHorizontal < offsetForHorizontal2 ? offsetForHorizontal : offsetForHorizontal2;
        if (offsetForHorizontal <= offsetForHorizontal2) {
            offsetForHorizontal = offsetForHorizontal2;
        }
        if (selectionStart >= i7) {
            i7 = selectionStart > offsetForHorizontal ? offsetForHorizontal : selectionStart;
        }
        if (i7 == selectionStart) {
            return false;
        }
        Selection.setSelection((Spannable) this.mText, i7);
        return true;
    }

    @Override // android.view.View
    public void computeScroll() {
        Scroller scroller = this.mScroller;
        if (scroller == null || !scroller.computeScrollOffset()) {
            return;
        }
        this.mScrollX = this.mScroller.getCurrX();
        this.mScrollY = this.mScroller.getCurrY();
        invalidateParentCaches();
        postInvalidate();
    }

    private void getInterestingRect(Rect rect, int i) {
        convertFromViewportToContentCoordinates(rect);
        if (i == 0) {
            rect.top -= getExtendedPaddingTop();
        }
        if (i == this.mLayout.getLineCount() - 1) {
            rect.bottom += getExtendedPaddingBottom();
        }
    }

    private void convertFromViewportToContentCoordinates(Rect rect) {
        int iViewportToContentHorizontalOffset = viewportToContentHorizontalOffset();
        rect.left += iViewportToContentHorizontalOffset;
        rect.right += iViewportToContentHorizontalOffset;
        int iViewportToContentVerticalOffset = viewportToContentVerticalOffset();
        rect.top += iViewportToContentVerticalOffset;
        rect.bottom += iViewportToContentVerticalOffset;
    }

    int viewportToContentHorizontalOffset() {
        return getCompoundPaddingLeft() - this.mScrollX;
    }

    int viewportToContentVerticalOffset() {
        int extendedPaddingTop = getExtendedPaddingTop() - this.mScrollY;
        return (this.mGravity & 112) != 48 ? extendedPaddingTop + getVerticalOffset(false) : extendedPaddingTop;
    }

    @Override // android.view.View
    public void debug(int i) {
        String str;
        super.debug(i);
        String str2 = debugIndent(i) + "frame={" + this.mLeft + ", " + this.mTop + ", " + this.mRight + ", " + this.mBottom + "} scroll={" + this.mScrollX + ", " + this.mScrollY + "} ";
        if (this.mText != null) {
            str = str2 + "mText=\"" + ((Object) this.mText) + "\" ";
            if (this.mLayout != null) {
                str = str + "mLayout width=" + this.mLayout.getWidth() + " height=" + this.mLayout.getHeight();
            }
        } else {
            str = str2 + "mText=NULL";
        }
        Log.d("View", str);
    }

    @ViewDebug.ExportedProperty(category = "text")
    public int getSelectionStart() {
        return Selection.getSelectionStart(getText());
    }

    @ViewDebug.ExportedProperty(category = "text")
    public int getSelectionEnd() {
        return Selection.getSelectionEnd(getText());
    }

    public boolean hasSelection() {
        int selectionStart = getSelectionStart();
        return selectionStart >= 0 && selectionStart != getSelectionEnd();
    }

    public void setSingleLine() {
        setSingleLine(true);
    }

    public void setAllCaps(boolean z) {
        if (z) {
            setTransformationMethod(new AllCapsTransformationMethod(getContext()));
        } else {
            setTransformationMethod(null);
        }
    }

    @RemotableViewMethod
    public void setSingleLine(boolean z) {
        setInputTypeSingleLine(z);
        applySingleLine(z, true, true);
    }

    private void setInputTypeSingleLine(boolean z) {
        Editor editor = this.mEditor;
        if (editor == null || (editor.mInputType & 15) != 1) {
            return;
        }
        if (z) {
            this.mEditor.mInputType &= -131073;
        } else {
            this.mEditor.mInputType |= 131072;
        }
    }

    private void applySingleLine(boolean z, boolean z2, boolean z3) {
        this.mSingleLine = z;
        if (z) {
            setLines(1);
            setHorizontallyScrolling(true);
            if (z2) {
                setTransformationMethod(SingleLineTransformationMethod.getInstance());
                return;
            }
            return;
        }
        if (z3) {
            setMaxLines(Integer.MAX_VALUE);
        }
        setHorizontallyScrolling(false);
        if (z2) {
            setTransformationMethod(null);
        }
    }

    public void setEllipsize(TextUtils.TruncateAt truncateAt) {
        if (this.mEllipsize != truncateAt) {
            this.mEllipsize = truncateAt;
            if (this.mLayout != null) {
                nullLayouts();
                requestLayout();
                invalidate();
            }
        }
    }

    public void setMarqueeRepeatLimit(int i) {
        this.mMarqueeRepeatLimit = i;
    }

    public int getMarqueeRepeatLimit() {
        return this.mMarqueeRepeatLimit;
    }

    @ViewDebug.ExportedProperty
    public TextUtils.TruncateAt getEllipsize() {
        return this.mEllipsize;
    }

    @RemotableViewMethod
    public void setSelectAllOnFocus(boolean z) {
        createEditorIfNeeded();
        this.mEditor.mSelectAllOnFocus = z;
        if (z) {
            CharSequence charSequence = this.mText;
            if (charSequence instanceof Spannable) {
                return;
            }
            setText(charSequence, BufferType.SPANNABLE);
        }
    }

    @RemotableViewMethod
    public void setCursorVisible(boolean z) {
        if (z && this.mEditor == null) {
            return;
        }
        createEditorIfNeeded();
        if (this.mEditor.mCursorVisible != z) {
            this.mEditor.mCursorVisible = z;
            invalidate();
            this.mEditor.makeBlink();
            this.mEditor.prepareCursorControllers();
        }
    }

    public boolean isCursorVisible() {
        Editor editor = this.mEditor;
        if (editor == null) {
            return true;
        }
        return editor.mCursorVisible;
    }

    private boolean canMarquee() {
        Layout layout;
        int compoundPaddingLeft = ((this.mRight - this.mLeft) - getCompoundPaddingLeft()) - getCompoundPaddingRight();
        if (compoundPaddingLeft <= 0) {
            return false;
        }
        float f = compoundPaddingLeft;
        return this.mLayout.getLineWidth(0) > f || !(this.mMarqueeFadeMode == 0 || (layout = this.mSavedMarqueeModeLayout) == null || layout.getLineWidth(0) <= f);
    }

    private void startMarquee() {
        if (getKeyListener() == null && !compressText((getWidth() - getCompoundPaddingLeft()) - getCompoundPaddingRight())) {
            Marquee marquee = this.mMarquee;
            if (marquee == null || marquee.isStopped()) {
                if ((isFocused() || isSelected()) && getLineCount() == 1 && canMarquee()) {
                    if (this.mMarqueeFadeMode == 1) {
                        this.mMarqueeFadeMode = 2;
                        Layout layout = this.mLayout;
                        this.mLayout = this.mSavedMarqueeModeLayout;
                        this.mSavedMarqueeModeLayout = layout;
                        setHorizontalFadingEdgeEnabled(true);
                        requestLayout();
                        invalidate();
                    }
                    if (this.mMarquee == null) {
                        this.mMarquee = new Marquee(this);
                    }
                    this.mMarquee.start(this.mMarqueeRepeatLimit);
                }
            }
        }
    }

    private void stopMarquee() {
        Marquee marquee = this.mMarquee;
        if (marquee != null && !marquee.isStopped()) {
            this.mMarquee.stop();
        }
        if (this.mMarqueeFadeMode == 2) {
            this.mMarqueeFadeMode = 1;
            Layout layout = this.mSavedMarqueeModeLayout;
            this.mSavedMarqueeModeLayout = this.mLayout;
            this.mLayout = layout;
            setHorizontalFadingEdgeEnabled(false);
            requestLayout();
            invalidate();
        }
    }

    private void startStopMarquee(boolean z) {
        if (this.mEllipsize == TextUtils.TruncateAt.MARQUEE) {
            if (z) {
                startMarquee();
            } else {
                stopMarquee();
            }
        }
    }

    protected void onSelectionChanged(int i, int i2) {
        sendAccessibilityEvent(8192);
    }

    public void addTextChangedListener(TextWatcher textWatcher) {
        if (this.mListeners == null) {
            this.mListeners = new ArrayList<>();
        }
        this.mListeners.add(textWatcher);
    }

    public void removeTextChangedListener(TextWatcher textWatcher) {
        int iIndexOf;
        ArrayList<TextWatcher> arrayList = this.mListeners;
        if (arrayList == null || (iIndexOf = arrayList.indexOf(textWatcher)) < 0) {
            return;
        }
        this.mListeners.remove(iIndexOf);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendBeforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        ArrayList<TextWatcher> arrayList = this.mListeners;
        if (arrayList != null) {
            int size = arrayList.size();
            for (int i4 = 0; i4 < size; i4++) {
                arrayList.get(i4).beforeTextChanged(charSequence, i, i2, i3);
            }
        }
        int i5 = i2 + i;
        removeIntersectingNonAdjacentSpans(i, i5, SpellCheckSpan.class);
        removeIntersectingNonAdjacentSpans(i, i5, SuggestionSpan.class);
    }

    private <T> void removeIntersectingNonAdjacentSpans(int i, int i2, Class<T> cls) {
        CharSequence charSequence = this.mText;
        if (charSequence instanceof Editable) {
            Editable editable = (Editable) charSequence;
            Object[] spans = editable.getSpans(i, i2, cls);
            int length = spans.length;
            for (int i3 = 0; i3 < length; i3++) {
                int spanStart = editable.getSpanStart(spans[i3]);
                if (editable.getSpanEnd(spans[i3]) == i || spanStart == i2) {
                    return;
                }
                editable.removeSpan(spans[i3]);
            }
        }
    }

    void removeAdjacentSuggestionSpans(int i) {
        CharSequence charSequence = this.mText;
        if (charSequence instanceof Editable) {
            Editable editable = (Editable) charSequence;
            SuggestionSpan[] suggestionSpanArr = (SuggestionSpan[]) editable.getSpans(i, i, SuggestionSpan.class);
            int length = suggestionSpanArr.length;
            for (int i2 = 0; i2 < length; i2++) {
                int spanStart = editable.getSpanStart(suggestionSpanArr[i2]);
                int spanEnd = editable.getSpanEnd(suggestionSpanArr[i2]);
                if ((spanEnd == i || spanStart == i) && SpellChecker.haveWordBoundariesChanged(editable, i, i, spanStart, spanEnd)) {
                    editable.removeSpan(suggestionSpanArr[i2]);
                }
            }
        }
    }

    void sendOnTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        ArrayList<TextWatcher> arrayList = this.mListeners;
        if (arrayList != null) {
            int size = arrayList.size();
            for (int i4 = 0; i4 < size; i4++) {
                arrayList.get(i4).onTextChanged(charSequence, i, i2, i3);
            }
        }
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.sendOnTextChanged(i, i3);
        }
    }

    void sendAfterTextChanged(Editable editable) {
        ArrayList<TextWatcher> arrayList = this.mListeners;
        if (arrayList != null) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                arrayList.get(i).afterTextChanged(editable);
            }
        }
    }

    void updateAfterEdit() {
        invalidate();
        int selectionStart = getSelectionStart();
        if (selectionStart >= 0 || (this.mGravity & 112) == 80) {
            registerForPreDraw();
        }
        checkForResize();
        if (selectionStart >= 0) {
            this.mHighlightPathBogus = true;
            Editor editor = this.mEditor;
            if (editor != null) {
                editor.makeBlink();
            }
            bringPointIntoView(selectionStart);
        }
    }

    void handleTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        Editor editor = this.mEditor;
        Editor.InputMethodState inputMethodState = editor == null ? null : editor.mInputMethodState;
        if (inputMethodState == null || inputMethodState.mBatchEditNesting == 0) {
            updateAfterEdit();
        }
        if (inputMethodState != null) {
            inputMethodState.mContentChanged = true;
            if (inputMethodState.mChangedStart < 0) {
                inputMethodState.mChangedStart = i;
                inputMethodState.mChangedEnd = i + i2;
            } else {
                inputMethodState.mChangedStart = Math.min(inputMethodState.mChangedStart, i);
                inputMethodState.mChangedEnd = Math.max(inputMethodState.mChangedEnd, (i + i2) - inputMethodState.mChangedDelta);
            }
            inputMethodState.mChangedDelta += i3 - i2;
        }
        sendOnTextChanged(charSequence, i, i2, i3);
        onTextChanged(charSequence, i, i2, i3);
    }

    void spanChange(Spanned spanned, Object obj, int i, int i2, int i3, int i4) {
        boolean z;
        int selectionEnd;
        Editor editor = this.mEditor;
        Editor.InputMethodState inputMethodState = editor == null ? null : editor.mInputMethodState;
        int selectionStart = -1;
        if (obj == Selection.SELECTION_END) {
            if (i >= 0 || i2 >= 0) {
                invalidateCursor(Selection.getSelectionStart(spanned), i, i2);
                checkForResize();
                registerForPreDraw();
                Editor editor2 = this.mEditor;
                if (editor2 != null) {
                    editor2.makeBlink();
                }
            }
            selectionEnd = i2;
            z = true;
        } else {
            z = false;
            selectionEnd = -1;
        }
        if (obj == Selection.SELECTION_START) {
            if (i >= 0 || i2 >= 0) {
                invalidateCursor(Selection.getSelectionEnd(spanned), i, i2);
            }
            selectionStart = i2;
            z = true;
        }
        if (z) {
            this.mHighlightPathBogus = true;
            if (this.mEditor != null && !isFocused()) {
                this.mEditor.mSelectionMoved = true;
            }
            if ((spanned.getSpanFlags(obj) & 512) == 0) {
                if (selectionStart < 0) {
                    selectionStart = Selection.getSelectionStart(spanned);
                }
                if (selectionEnd < 0) {
                    selectionEnd = Selection.getSelectionEnd(spanned);
                }
                onSelectionChanged(selectionStart, selectionEnd);
            }
        }
        if ((obj instanceof UpdateAppearance) || (obj instanceof ParagraphStyle) || (obj instanceof CharacterStyle)) {
            if (inputMethodState == null || inputMethodState.mBatchEditNesting == 0) {
                invalidate();
                this.mHighlightPathBogus = true;
                checkForResize();
            } else {
                inputMethodState.mContentChanged = true;
            }
            Editor editor3 = this.mEditor;
            if (editor3 != null) {
                if (i >= 0) {
                    editor3.invalidateTextDisplayList(this.mLayout, i, i3);
                }
                if (i2 >= 0) {
                    this.mEditor.invalidateTextDisplayList(this.mLayout, i2, i4);
                }
            }
        }
        if (MetaKeyKeyListener.isMetaTracker(spanned, obj)) {
            this.mHighlightPathBogus = true;
            if (inputMethodState != null && MetaKeyKeyListener.isSelectingMetaTracker(spanned, obj)) {
                inputMethodState.mSelectionModeChanged = true;
            }
            if (Selection.getSelectionStart(spanned) >= 0) {
                if (inputMethodState == null || inputMethodState.mBatchEditNesting == 0) {
                    invalidateCursor();
                } else {
                    inputMethodState.mCursorChanged = true;
                }
            }
        }
        if ((obj instanceof ParcelableSpan) && inputMethodState != null && inputMethodState.mExtractedTextRequest != null) {
            if (inputMethodState.mBatchEditNesting != 0) {
                if (i >= 0) {
                    if (inputMethodState.mChangedStart > i) {
                        inputMethodState.mChangedStart = i;
                    }
                    if (inputMethodState.mChangedStart > i3) {
                        inputMethodState.mChangedStart = i3;
                    }
                }
                if (i2 >= 0) {
                    if (inputMethodState.mChangedStart > i2) {
                        inputMethodState.mChangedStart = i2;
                    }
                    if (inputMethodState.mChangedStart > i4) {
                        inputMethodState.mChangedStart = i4;
                    }
                }
            } else {
                inputMethodState.mContentChanged = true;
            }
        }
        Editor editor4 = this.mEditor;
        if (editor4 == null || editor4.mSpellChecker == null || i2 >= 0 || !(obj instanceof SpellCheckSpan)) {
            return;
        }
        this.mEditor.mSpellChecker.onSpellCheckSpanRemoved((SpellCheckSpan) obj);
    }

    @Override // android.view.View
    public void dispatchFinishTemporaryDetach() {
        this.mDispatchTemporaryDetach = true;
        super.dispatchFinishTemporaryDetach();
        this.mDispatchTemporaryDetach = false;
    }

    @Override // android.view.View
    public void onStartTemporaryDetach() {
        super.onStartTemporaryDetach();
        if (!this.mDispatchTemporaryDetach) {
            this.mTemporaryDetach = true;
        }
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.mTemporaryDetach = true;
        }
    }

    @Override // android.view.View
    public void onFinishTemporaryDetach() {
        super.onFinishTemporaryDetach();
        if (!this.mDispatchTemporaryDetach) {
            this.mTemporaryDetach = false;
        }
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.mTemporaryDetach = false;
        }
    }

    @Override // android.view.View
    protected void onFocusChanged(boolean z, int i, Rect rect) {
        if (this.mTemporaryDetach) {
            super.onFocusChanged(z, i, rect);
            return;
        }
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.onFocusChanged(z, i);
        }
        if (z) {
            CharSequence charSequence = this.mText;
            if (charSequence instanceof Spannable) {
                MetaKeyKeyListener.resetMetaState((Spannable) charSequence);
            }
        }
        startStopMarquee(z);
        TransformationMethod transformationMethod = this.mTransformation;
        if (transformationMethod != null) {
            transformationMethod.onFocusChanged(this, this.mText, z, i, rect);
        }
        super.onFocusChanged(z, i, rect);
    }

    @Override // android.view.View
    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.onWindowFocusChanged(z);
        }
        startStopMarquee(z);
    }

    @Override // android.view.View
    protected void onVisibilityChanged(View view, int i) {
        super.onVisibilityChanged(view, i);
        Editor editor = this.mEditor;
        if (editor == null || i == 0) {
            return;
        }
        editor.hideControllers();
    }

    public void clearComposingText() {
        CharSequence charSequence = this.mText;
        if (charSequence instanceof Spannable) {
            BaseInputConnection.removeComposingSpans((Spannable) charSequence);
        }
    }

    @Override // android.view.View
    public void setSelected(boolean z) {
        boolean zIsSelected = isSelected();
        super.setSelected(z);
        if (z == zIsSelected || this.mEllipsize != TextUtils.TruncateAt.MARQUEE) {
            return;
        }
        if (z) {
            startMarquee();
        } else {
            stopMarquee();
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        Editor editor;
        int actionMasked = motionEvent.getActionMasked();
        Editor editor2 = this.mEditor;
        if (editor2 != null) {
            editor2.onTouchEvent(motionEvent);
        }
        boolean zOnTouchEvent = super.onTouchEvent(motionEvent);
        Editor editor3 = this.mEditor;
        if (editor3 != null && editor3.mDiscardNextActionUp && actionMasked == 1) {
            this.mEditor.mDiscardNextActionUp = false;
            return zOnTouchEvent;
        }
        boolean z = actionMasked == 1 && ((editor = this.mEditor) == null || !editor.mIgnoreActionUpEvent) && isFocused();
        if ((this.mMovement != null || onCheckIsTextEditor()) && isEnabled()) {
            CharSequence charSequence = this.mText;
            if ((charSequence instanceof Spannable) && this.mLayout != null) {
                MovementMethod movementMethod = this.mMovement;
                boolean zOnTouchEvent2 = movementMethod != null ? movementMethod.onTouchEvent(this, (Spannable) charSequence, motionEvent) | false : false;
                boolean zIsTextSelectable = isTextSelectable();
                if (z && this.mLinksClickable && this.mAutoLinkMask != 0 && zIsTextSelectable) {
                    ClickableSpan[] clickableSpanArr = (ClickableSpan[]) ((Spannable) this.mText).getSpans(getSelectionStart(), getSelectionEnd(), ClickableSpan.class);
                    if (clickableSpanArr.length > 0) {
                        clickableSpanArr[0].onClick(this);
                        zOnTouchEvent2 = true;
                    }
                }
                if (z && (isTextEditable() || zIsTextSelectable)) {
                    InputMethodManager inputMethodManagerPeekInstance = InputMethodManager.peekInstance();
                    viewClicked(inputMethodManagerPeekInstance);
                    if (!zIsTextSelectable && this.mEditor.mShowSoftInputOnFocus && inputMethodManagerPeekInstance != null) {
                        inputMethodManagerPeekInstance.showSoftInput(this, 0);
                    }
                    this.mEditor.onTouchUpEvent(motionEvent);
                    zOnTouchEvent2 = true;
                }
                if (zOnTouchEvent2) {
                    return true;
                }
            }
        }
        return zOnTouchEvent;
    }

    @Override // android.view.View
    public boolean onGenericMotionEvent(MotionEvent motionEvent) {
        MovementMethod movementMethod = this.mMovement;
        if (movementMethod != null) {
            CharSequence charSequence = this.mText;
            if ((charSequence instanceof Spannable) && this.mLayout != null) {
                try {
                    if (movementMethod.onGenericMotionEvent(this, (Spannable) charSequence, motionEvent)) {
                        return true;
                    }
                } catch (AbstractMethodError unused) {
                }
            }
        }
        return super.onGenericMotionEvent(motionEvent);
    }

    boolean isTextEditable() {
        return (this.mText instanceof Editable) && onCheckIsTextEditor() && isEnabled();
    }

    public boolean didTouchFocusSelect() {
        Editor editor = this.mEditor;
        return editor != null && editor.mTouchFocusSelected;
    }

    @Override // android.view.View
    public void cancelLongPress() {
        super.cancelLongPress();
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.mIgnoreActionUpEvent = true;
        }
    }

    @Override // android.view.View
    public boolean onTrackballEvent(MotionEvent motionEvent) {
        MovementMethod movementMethod = this.mMovement;
        if (movementMethod != null) {
            CharSequence charSequence = this.mText;
            if ((charSequence instanceof Spannable) && this.mLayout != null && movementMethod.onTrackballEvent(this, (Spannable) charSequence, motionEvent)) {
                return true;
            }
        }
        return super.onTrackballEvent(motionEvent);
    }

    public void setScroller(Scroller scroller) {
        this.mScroller = scroller;
    }

    /* JADX WARN: Code restructure failed: missing block: B:24:0x0045, code lost:
    
        if (r0 != 7) goto L32;
     */
    @Override // android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected float getLeftFadingEdgeStrength() {
        /*
            r6 = this;
            android.text.TextUtils$TruncateAt r0 = r6.mEllipsize
            android.text.TextUtils$TruncateAt r1 = android.text.TextUtils.TruncateAt.MARQUEE
            if (r0 != r1) goto L9c
            int r0 = r6.mMarqueeFadeMode
            r1 = 1
            if (r0 == r1) goto L9c
            android.widget.TextView$Marquee r0 = r6.mMarquee
            r2 = 0
            if (r0 == 0) goto L2a
            boolean r0 = r0.isStopped()
            if (r0 != 0) goto L2a
            android.widget.TextView$Marquee r0 = r6.mMarquee
            boolean r1 = r0.shouldDrawLeftFade()
            if (r1 == 0) goto L29
            float r0 = r0.getScroll()
            int r1 = r6.getHorizontalFadingEdgeLength()
        L26:
            float r1 = (float) r1
            float r0 = r0 / r1
            return r0
        L29:
            return r2
        L2a:
            int r0 = r6.getLineCount()
            if (r0 != r1) goto L9c
            int r0 = r6.getLayoutDirection()
            int r3 = r6.mGravity
            int r0 = android.view.Gravity.getAbsoluteGravity(r3, r0)
            r3 = 7
            r0 = r0 & r3
            r4 = 0
            if (r0 == r1) goto L6e
            r5 = 3
            if (r0 == r5) goto L6d
            r5 = 5
            if (r0 == r5) goto L48
            if (r0 == r3) goto L6e
            goto L9c
        L48:
            android.text.Layout r0 = r6.mLayout
            float r0 = r0.getLineRight(r4)
            int r1 = r6.mRight
            int r2 = r6.mLeft
            int r1 = r1 - r2
            float r1 = (float) r1
            float r0 = r0 - r1
            int r1 = r6.getCompoundPaddingLeft()
            float r1 = (float) r1
            float r0 = r0 - r1
            int r1 = r6.getCompoundPaddingRight()
            float r1 = (float) r1
            float r0 = r0 - r1
            android.text.Layout r1 = r6.mLayout
            float r1 = r1.getLineLeft(r4)
            float r0 = r0 - r1
            int r1 = r6.getHorizontalFadingEdgeLength()
            goto L26
        L6d:
            return r2
        L6e:
            android.text.Layout r0 = r6.mLayout
            int r0 = r0.getParagraphDirection(r4)
            if (r0 != r1) goto L77
            return r2
        L77:
            android.text.Layout r0 = r6.mLayout
            float r0 = r0.getLineRight(r4)
            int r1 = r6.mRight
            int r2 = r6.mLeft
            int r1 = r1 - r2
            float r1 = (float) r1
            float r0 = r0 - r1
            int r1 = r6.getCompoundPaddingLeft()
            float r1 = (float) r1
            float r0 = r0 - r1
            int r1 = r6.getCompoundPaddingRight()
            float r1 = (float) r1
            float r0 = r0 - r1
            android.text.Layout r1 = r6.mLayout
            float r1 = r1.getLineLeft(r4)
            float r0 = r0 - r1
            int r1 = r6.getHorizontalFadingEdgeLength()
            goto L26
        L9c:
            float r0 = super.getLeftFadingEdgeStrength()
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: android.widget.TextView.getLeftFadingEdgeStrength():float");
    }

    /* JADX WARN: Code restructure failed: missing block: B:21:0x0043, code lost:
    
        if (r0 != 7) goto L30;
     */
    @Override // android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected float getRightFadingEdgeStrength() {
        /*
            r5 = this;
            android.text.TextUtils$TruncateAt r0 = r5.mEllipsize
            android.text.TextUtils$TruncateAt r1 = android.text.TextUtils.TruncateAt.MARQUEE
            if (r0 != r1) goto L8b
            int r0 = r5.mMarqueeFadeMode
            r1 = 1
            if (r0 == r1) goto L8b
            android.widget.TextView$Marquee r0 = r5.mMarquee
            if (r0 == 0) goto L27
            boolean r0 = r0.isStopped()
            if (r0 != 0) goto L27
            android.widget.TextView$Marquee r0 = r5.mMarquee
            float r1 = r0.getMaxFadeScroll()
            float r0 = r0.getScroll()
            float r1 = r1 - r0
            int r0 = r5.getHorizontalFadingEdgeLength()
        L24:
            float r0 = (float) r0
            float r1 = r1 / r0
            return r1
        L27:
            int r0 = r5.getLineCount()
            if (r0 != r1) goto L8b
            int r0 = r5.getLayoutDirection()
            int r2 = r5.mGravity
            int r0 = android.view.Gravity.getAbsoluteGravity(r2, r0)
            r2 = 7
            r0 = r0 & r2
            r3 = 0
            r4 = 0
            if (r0 == r1) goto L63
            r1 = 3
            if (r0 == r1) goto L47
            r1 = 5
            if (r0 == r1) goto L46
            if (r0 == r2) goto L63
            goto L8b
        L46:
            return r3
        L47:
            int r0 = r5.mRight
            int r1 = r5.mLeft
            int r0 = r0 - r1
            int r1 = r5.getCompoundPaddingLeft()
            int r0 = r0 - r1
            int r1 = r5.getCompoundPaddingRight()
            int r0 = r0 - r1
            android.text.Layout r1 = r5.mLayout
            float r1 = r1.getLineWidth(r4)
            float r0 = (float) r0
            float r1 = r1 - r0
            int r0 = r5.getHorizontalFadingEdgeLength()
            goto L24
        L63:
            android.text.Layout r0 = r5.mLayout
            int r0 = r0.getParagraphDirection(r4)
            r1 = -1
            if (r0 != r1) goto L6d
            return r3
        L6d:
            android.text.Layout r0 = r5.mLayout
            float r0 = r0.getLineWidth(r4)
            int r1 = r5.mRight
            int r2 = r5.mLeft
            int r1 = r1 - r2
            int r2 = r5.getCompoundPaddingLeft()
            int r1 = r1 - r2
            int r2 = r5.getCompoundPaddingRight()
            int r1 = r1 - r2
            float r1 = (float) r1
            float r0 = r0 - r1
            int r1 = r5.getHorizontalFadingEdgeLength()
            float r1 = (float) r1
            float r0 = r0 / r1
            return r0
        L8b:
            float r0 = super.getRightFadingEdgeStrength()
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: android.widget.TextView.getRightFadingEdgeStrength():float");
    }

    @Override // android.view.View
    protected int computeHorizontalScrollRange() {
        Layout layout = this.mLayout;
        if (layout != null) {
            return (this.mSingleLine && (this.mGravity & 7) == 3) ? (int) layout.getLineWidth(0) : layout.getWidth();
        }
        return super.computeHorizontalScrollRange();
    }

    @Override // android.view.View
    protected int computeVerticalScrollRange() {
        Layout layout = this.mLayout;
        if (layout != null) {
            return layout.getHeight();
        }
        return super.computeVerticalScrollRange();
    }

    @Override // android.view.View
    protected int computeVerticalScrollExtent() {
        return (getHeight() - getCompoundPaddingTop()) - getCompoundPaddingBottom();
    }

    @Override // android.view.View
    public void findViewsWithText(ArrayList<View> arrayList, CharSequence charSequence, int i) {
        super.findViewsWithText(arrayList, charSequence, i);
        if (arrayList.contains(this) || (i & 1) == 0 || TextUtils.isEmpty(charSequence) || TextUtils.isEmpty(this.mText)) {
            return;
        }
        if (this.mText.toString().toLowerCase().contains(charSequence.toString().toLowerCase())) {
            arrayList.add(this);
        }
    }

    public static ColorStateList getTextColors(Context context, TypedArray typedArray) {
        int resourceId;
        ColorStateList colorStateList = typedArray.getColorStateList(5);
        if (colorStateList != null || (resourceId = typedArray.getResourceId(1, -1)) == -1) {
            return colorStateList;
        }
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(resourceId, com.android.internal.R.styleable.TextAppearance);
        ColorStateList colorStateList2 = typedArrayObtainStyledAttributes.getColorStateList(3);
        typedArrayObtainStyledAttributes.recycle();
        return colorStateList2;
    }

    public static int getTextColor(Context context, TypedArray typedArray, int i) {
        ColorStateList textColors = getTextColors(context, typedArray);
        return textColors == null ? i : textColors.getDefaultColor();
    }

    @Override // android.view.View
    public boolean onKeyShortcut(int i, KeyEvent keyEvent) {
        if (KeyEvent.metaStateHasNoModifiers(keyEvent.getMetaState() & (-28673))) {
            if (i != 29) {
                if (i != 31) {
                    if (i != 50) {
                        if (i == 52 && canCut()) {
                            return onTextContextMenuItem(16908320);
                        }
                    } else if (canPaste()) {
                        return onTextContextMenuItem(16908322);
                    }
                } else if (canCopy()) {
                    return onTextContextMenuItem(16908321);
                }
            } else if (canSelectText()) {
                return onTextContextMenuItem(16908319);
            }
        }
        return super.onKeyShortcut(i, keyEvent);
    }

    private boolean canSelectText() {
        Editor editor;
        return (this.mText.length() == 0 || (editor = this.mEditor) == null || !editor.hasSelectionController()) ? false : true;
    }

    boolean textCanBeSelected() {
        MovementMethod movementMethod = this.mMovement;
        if (movementMethod == null || !movementMethod.canSelectArbitrarily()) {
            return false;
        }
        return isTextEditable() || (isTextSelectable() && (this.mText instanceof Spannable) && isEnabled());
    }

    private Locale getTextServicesLocale(boolean z) {
        updateTextServicesLocaleAsync();
        return (this.mCurrentSpellCheckerLocaleCache != null || z) ? this.mCurrentSpellCheckerLocaleCache : Locale.getDefault();
    }

    public Locale getTextServicesLocale() {
        return getTextServicesLocale(false);
    }

    public Locale getSpellCheckerLocale() {
        return getTextServicesLocale(true);
    }

    private void updateTextServicesLocaleAsync() {
        AsyncTask.execute(new Runnable() { // from class: android.widget.TextView.3
            @Override // java.lang.Runnable
            public void run() {
                TextView.this.updateTextServicesLocaleLocked();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateTextServicesLocaleLocked() {
        SpellCheckerSubtype currentSpellCheckerSubtype = ((TextServicesManager) this.mContext.getSystemService(Context.TEXT_SERVICES_MANAGER_SERVICE)).getCurrentSpellCheckerSubtype(true);
        this.mCurrentSpellCheckerLocaleCache = currentSpellCheckerSubtype != null ? SpellCheckerSubtype.constructLocaleFromString(currentSpellCheckerSubtype.getLocale()) : null;
    }

    void onLocaleChanged() {
        this.mEditor.mWordIterator = null;
    }

    public WordIterator getWordIterator() {
        Editor editor = this.mEditor;
        if (editor != null) {
            return editor.getWordIterator();
        }
        return null;
    }

    @Override // android.view.View
    public void onPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onPopulateAccessibilityEvent(accessibilityEvent);
        if (!hasPasswordTransformationMethod() || shouldSpeakPasswordsForAccessibility()) {
            CharSequence textForAccessibility = getTextForAccessibility();
            if (TextUtils.isEmpty(textForAccessibility)) {
                return;
            }
            accessibilityEvent.getText().add(textForAccessibility);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean shouldSpeakPasswordsForAccessibility() {
        return Settings.Secure.getInt(this.mContext.getContentResolver(), Settings.Secure.ACCESSIBILITY_SPEAK_PASSWORD, 0) == 1;
    }

    @Override // android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        accessibilityEvent.setClassName(TextView.class.getName());
        accessibilityEvent.setPassword(hasPasswordTransformationMethod());
        if (accessibilityEvent.getEventType() == 8192) {
            accessibilityEvent.setFromIndex(Selection.getSelectionStart(this.mText));
            accessibilityEvent.setToIndex(Selection.getSelectionEnd(this.mText));
            accessibilityEvent.setItemCount(this.mText.length());
        }
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName(TextView.class.getName());
        boolean zHasPasswordTransformationMethod = hasPasswordTransformationMethod();
        accessibilityNodeInfo.setPassword(zHasPasswordTransformationMethod);
        if (!zHasPasswordTransformationMethod) {
            accessibilityNodeInfo.setText(getTextForAccessibility());
        }
        if (this.mBufferType == BufferType.EDITABLE) {
            accessibilityNodeInfo.setEditable(true);
        }
        Editor editor = this.mEditor;
        if (editor != null) {
            accessibilityNodeInfo.setInputType(editor.mInputType);
            if (this.mEditor.mError != null) {
                accessibilityNodeInfo.setContentInvalid(true);
            }
        }
        if (!TextUtils.isEmpty(this.mText)) {
            accessibilityNodeInfo.addAction(256);
            accessibilityNodeInfo.addAction(512);
            accessibilityNodeInfo.setMovementGranularities(31);
        }
        if (isFocused()) {
            if (canSelectText()) {
                accessibilityNodeInfo.addAction(131072);
            }
            if (canCopy()) {
                accessibilityNodeInfo.addAction(16384);
            }
            if (canPaste()) {
                accessibilityNodeInfo.addAction(32768);
            }
            if (canCut()) {
                accessibilityNodeInfo.addAction(65536);
            }
        }
        if (isSingleLine()) {
            return;
        }
        accessibilityNodeInfo.setMultiLine(true);
    }

    @Override // android.view.View
    public boolean performAccessibilityAction(int i, Bundle bundle) {
        CharSequence iterableTextForAccessibility;
        if (i == 16384) {
            return isFocused() && canCopy() && onTextContextMenuItem(16908321);
        }
        if (i == 32768) {
            return isFocused() && canPaste() && onTextContextMenuItem(16908322);
        }
        if (i == 65536) {
            return isFocused() && canCut() && onTextContextMenuItem(16908320);
        }
        if (i == 131072) {
            if (!isFocused() || !canSelectText() || (iterableTextForAccessibility = getIterableTextForAccessibility()) == null) {
                return false;
            }
            int i2 = bundle != null ? bundle.getInt("ACTION_ARGUMENT_SELECTION_START_INT", -1) : -1;
            int i3 = bundle != null ? bundle.getInt("ACTION_ARGUMENT_SELECTION_END_INT", -1) : -1;
            if (getSelectionStart() != i2 || getSelectionEnd() != i3) {
                if (i2 == i3 && i3 == -1) {
                    Selection.removeSelection((Spannable) iterableTextForAccessibility);
                    return true;
                }
                if (i2 >= 0 && i2 <= i3 && i3 <= iterableTextForAccessibility.length()) {
                    Selection.setSelection((Spannable) iterableTextForAccessibility, i2, i3);
                    Editor editor = this.mEditor;
                    if (editor != null) {
                        editor.startSelectionActionMode();
                    }
                    return true;
                }
            }
            return false;
        }
        return super.performAccessibilityAction(i, bundle);
    }

    @Override // android.view.View, android.view.accessibility.AccessibilityEventSource
    public void sendAccessibilityEvent(int i) {
        if (i == 4096) {
            return;
        }
        super.sendAccessibilityEvent(i);
    }

    public CharSequence getTextForAccessibility() {
        CharSequence text = getText();
        return TextUtils.isEmpty(text) ? getHint() : text;
    }

    void sendAccessibilityEventTypeViewTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        AccessibilityEvent accessibilityEventObtain = AccessibilityEvent.obtain(16);
        accessibilityEventObtain.setFromIndex(i);
        accessibilityEventObtain.setRemovedCount(i2);
        accessibilityEventObtain.setAddedCount(i3);
        accessibilityEventObtain.setBeforeText(charSequence);
        sendAccessibilityEventUnchecked(accessibilityEventObtain);
    }

    public boolean isInputMethodTarget() {
        InputMethodManager inputMethodManagerPeekInstance = InputMethodManager.peekInstance();
        return inputMethodManagerPeekInstance != null && inputMethodManagerPeekInstance.isActive(this);
    }

    public boolean onTextContextMenuItem(int i) {
        int iMax;
        int length = this.mText.length();
        if (isFocused()) {
            int selectionStart = getSelectionStart();
            int selectionEnd = getSelectionEnd();
            iMax = Math.max(0, Math.min(selectionStart, selectionEnd));
            length = Math.max(0, Math.max(selectionStart, selectionEnd));
        } else {
            iMax = 0;
        }
        switch (i) {
            case 16908319:
                selectAllText();
                return true;
            case 16908320:
                setPrimaryClip(ClipData.newPlainText(null, getTransformedText(iMax, length)));
                deleteText_internal(iMax, length);
                stopSelectionActionMode();
                return true;
            case 16908321:
                setPrimaryClip(ClipData.newPlainText(null, getTransformedText(iMax, length)));
                stopSelectionActionMode();
                return true;
            case 16908322:
                paste(iMax, length);
                return true;
            default:
                return false;
        }
    }

    CharSequence getTransformedText(int i, int i2) {
        return removeSuggestionSpans(this.mTransformed.subSequence(i, i2));
    }

    @Override // android.view.View
    public boolean performLongClick() {
        boolean zPerformLongClick = super.performLongClick();
        Editor editor = this.mEditor;
        if (editor != null) {
            zPerformLongClick |= editor.performLongClick(zPerformLongClick);
        }
        if (zPerformLongClick) {
            performHapticFeedback(0);
            Editor editor2 = this.mEditor;
            if (editor2 != null) {
                editor2.mDiscardNextActionUp = true;
            }
        }
        return zPerformLongClick;
    }

    @Override // android.view.View
    protected void onScrollChanged(int i, int i2, int i3, int i4) {
        super.onScrollChanged(i, i2, i3, i4);
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.onScrollChanged();
        }
    }

    public boolean isSuggestionsEnabled() {
        Editor editor = this.mEditor;
        if (editor == null || (editor.mInputType & 15) != 1 || (this.mEditor.mInputType & 524288) > 0) {
            return false;
        }
        int i = this.mEditor.mInputType & InputType.TYPE_MASK_VARIATION;
        return i == 0 || i == 48 || i == 80 || i == 64 || i == 160;
    }

    public void setCustomSelectionActionModeCallback(ActionMode.Callback callback) {
        createEditorIfNeeded();
        this.mEditor.mCustomSelectionActionModeCallback = callback;
    }

    public ActionMode.Callback getCustomSelectionActionModeCallback() {
        Editor editor = this.mEditor;
        if (editor == null) {
            return null;
        }
        return editor.mCustomSelectionActionModeCallback;
    }

    protected void stopSelectionActionMode() {
        this.mEditor.stopSelectionActionMode();
    }

    boolean canCut() {
        Editor editor;
        return !hasPasswordTransformationMethod() && this.mText.length() > 0 && hasSelection() && (this.mText instanceof Editable) && (editor = this.mEditor) != null && editor.mKeyListener != null;
    }

    boolean canCopy() {
        return !hasPasswordTransformationMethod() && this.mText.length() > 0 && hasSelection();
    }

    boolean canPaste() {
        Editor editor;
        return (this.mText instanceof Editable) && (editor = this.mEditor) != null && editor.mKeyListener != null && getSelectionStart() >= 0 && getSelectionEnd() >= 0 && ((ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE)).hasPrimaryClip();
    }

    boolean selectAllText() {
        int length = this.mText.length();
        Selection.setSelection((Spannable) this.mText, 0, length);
        return length > 0;
    }

    long prepareSpacesAroundPaste(int i, int i2, CharSequence charSequence) {
        int length;
        int length2;
        if (charSequence.length() > 0) {
            if (i > 0) {
                int i3 = i - 1;
                char cCharAt = this.mTransformed.charAt(i3);
                char cCharAt2 = charSequence.charAt(0);
                if (Character.isSpaceChar(cCharAt) && Character.isSpaceChar(cCharAt2)) {
                    length = this.mText.length();
                    deleteText_internal(i3, i);
                    length2 = this.mText.length();
                } else if (!Character.isSpaceChar(cCharAt) && cCharAt != '\n' && !Character.isSpaceChar(cCharAt2) && cCharAt2 != '\n') {
                    length = this.mText.length();
                    replaceText_internal(i, i, " ");
                    length2 = this.mText.length();
                }
                int i4 = length2 - length;
                i += i4;
                i2 += i4;
            }
            if (i2 < this.mText.length()) {
                char cCharAt3 = charSequence.charAt(charSequence.length() - 1);
                char cCharAt4 = this.mTransformed.charAt(i2);
                if (Character.isSpaceChar(cCharAt3) && Character.isSpaceChar(cCharAt4)) {
                    deleteText_internal(i2, i2 + 1);
                } else if (!Character.isSpaceChar(cCharAt3) && cCharAt3 != '\n' && !Character.isSpaceChar(cCharAt4) && cCharAt4 != '\n') {
                    replaceText_internal(i2, i2, " ");
                }
            }
        }
        return TextUtils.packRangeInLong(i, i2);
    }

    private void paste(int i, int i2) {
        ClipData primaryClip = ((ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE)).getPrimaryClip();
        if (primaryClip != null) {
            boolean z = false;
            for (int i3 = 0; i3 < primaryClip.getItemCount(); i3++) {
                CharSequence charSequenceCoerceToStyledText = primaryClip.getItemAt(i3).coerceToStyledText(getContext());
                if (charSequenceCoerceToStyledText != null) {
                    if (!z) {
                        long jPrepareSpacesAroundPaste = prepareSpacesAroundPaste(i, i2, charSequenceCoerceToStyledText);
                        int iUnpackRangeStartFromLong = TextUtils.unpackRangeStartFromLong(jPrepareSpacesAroundPaste);
                        int iUnpackRangeEndFromLong = TextUtils.unpackRangeEndFromLong(jPrepareSpacesAroundPaste);
                        Selection.setSelection((Spannable) this.mText, iUnpackRangeEndFromLong);
                        ((Editable) this.mText).replace(iUnpackRangeStartFromLong, iUnpackRangeEndFromLong, charSequenceCoerceToStyledText);
                        i2 = iUnpackRangeEndFromLong;
                        i = iUnpackRangeStartFromLong;
                        z = true;
                    } else {
                        ((Editable) this.mText).insert(getSelectionEnd(), "\n");
                        ((Editable) this.mText).insert(getSelectionEnd(), charSequenceCoerceToStyledText);
                    }
                }
            }
            stopSelectionActionMode();
            LAST_CUT_OR_COPY_TIME = 0L;
        }
    }

    private void setPrimaryClip(ClipData clipData) {
        ((ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(clipData);
        LAST_CUT_OR_COPY_TIME = SystemClock.uptimeMillis();
    }

    public int getOffsetForPosition(float f, float f2) {
        if (getLayout() == null) {
            return -1;
        }
        return getOffsetAtCoordinate(getLineAtCoordinate(f2), f);
    }

    float convertToLocalHorizontalCoordinate(float f) {
        return Math.min((getWidth() - getTotalPaddingRight()) - 1, Math.max(0.0f, f - getTotalPaddingLeft())) + getScrollX();
    }

    int getLineAtCoordinate(float f) {
        return getLayout().getLineForVertical((int) (Math.min((getHeight() - getTotalPaddingBottom()) - 1, Math.max(0.0f, f - getTotalPaddingTop())) + getScrollY()));
    }

    private int getOffsetAtCoordinate(int i, float f) {
        return getLayout().getOffsetForHorizontal(i, convertToLocalHorizontalCoordinate(f));
    }

    @Override // android.view.View
    public boolean onDragEvent(DragEvent dragEvent) {
        int action = dragEvent.getAction();
        if (action == 1) {
            Editor editor = this.mEditor;
            return editor != null && editor.hasInsertionController();
        }
        if (action == 2) {
            Selection.setSelection((Spannable) this.mText, getOffsetForPosition(dragEvent.getX(), dragEvent.getY()));
            return true;
        }
        if (action != 3) {
            if (action != 5) {
                return true;
            }
            requestFocus();
            return true;
        }
        Editor editor2 = this.mEditor;
        if (editor2 != null) {
            editor2.onDrop(dragEvent);
        }
        return true;
    }

    boolean isInBatchEditMode() {
        Editor editor = this.mEditor;
        if (editor == null) {
            return false;
        }
        Editor.InputMethodState inputMethodState = editor.mInputMethodState;
        if (inputMethodState != null) {
            return inputMethodState.mBatchEditNesting > 0;
        }
        return this.mEditor.mInBatchEditControllers;
    }

    @Override // android.view.View
    public void onRtlPropertiesChanged(int i) {
        super.onRtlPropertiesChanged(i);
        this.mTextDir = getTextDirectionHeuristic();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public TextDirectionHeuristic getTextDirectionHeuristic() {
        if (hasPasswordTransformationMethod()) {
            return TextDirectionHeuristics.LTR;
        }
        boolean z = getLayoutDirection() == 1;
        int textDirection = getTextDirection();
        if (textDirection == 2) {
            return TextDirectionHeuristics.ANYRTL_LTR;
        }
        if (textDirection == 3) {
            return TextDirectionHeuristics.LTR;
        }
        if (textDirection == 4) {
            return TextDirectionHeuristics.RTL;
        }
        if (textDirection != 5) {
            return z ? TextDirectionHeuristics.FIRSTSTRONG_RTL : TextDirectionHeuristics.FIRSTSTRONG_LTR;
        }
        return TextDirectionHeuristics.LOCALE;
    }

    @Override // android.view.View
    public void onResolveDrawables(int i) {
        if (this.mLastLayoutDirection == i) {
            return;
        }
        this.mLastLayoutDirection = i;
        Drawables drawables = this.mDrawables;
        if (drawables != null) {
            drawables.resolveWithLayoutDirection(i);
        }
    }

    @Override // android.view.View
    protected void resetResolvedDrawables() {
        super.resetResolvedDrawables();
        this.mLastLayoutDirection = -1;
    }

    protected void viewClicked(InputMethodManager inputMethodManager) {
        if (inputMethodManager != null) {
            inputMethodManager.viewClicked(this);
        }
    }

    protected void deleteText_internal(int i, int i2) {
        ((Editable) this.mText).delete(i, i2);
    }

    protected void replaceText_internal(int i, int i2, CharSequence charSequence) {
        ((Editable) this.mText).replace(i, i2, charSequence);
    }

    protected void setSpan_internal(Object obj, int i, int i2, int i3) {
        ((Editable) this.mText).setSpan(obj, i, i2, i3);
    }

    protected void setCursorPosition_internal(int i, int i2) {
        Selection.setSelection((Editable) this.mText, i, i2);
    }

    private void createEditorIfNeeded() {
        if (this.mEditor == null) {
            this.mEditor = new Editor(this);
        }
    }

    @Override // android.view.View
    public CharSequence getIterableTextForAccessibility() {
        CharSequence charSequence = this.mText;
        if (!(charSequence instanceof Spannable)) {
            setText(charSequence, BufferType.SPANNABLE);
        }
        return this.mText;
    }

    @Override // android.view.View
    public AccessibilityIterators.TextSegmentIterator getIteratorForGranularity(int i) {
        if (i == 4) {
            Spannable spannable = (Spannable) getIterableTextForAccessibility();
            if (!TextUtils.isEmpty(spannable) && getLayout() != null) {
                AccessibilityIterators.LineTextSegmentIterator lineTextSegmentIterator = AccessibilityIterators.LineTextSegmentIterator.getInstance();
                lineTextSegmentIterator.initialize(spannable, getLayout());
                return lineTextSegmentIterator;
            }
        } else if (i == 16 && !TextUtils.isEmpty((Spannable) getIterableTextForAccessibility()) && getLayout() != null) {
            AccessibilityIterators.PageTextSegmentIterator pageTextSegmentIterator = AccessibilityIterators.PageTextSegmentIterator.getInstance();
            pageTextSegmentIterator.initialize(this);
            return pageTextSegmentIterator;
        }
        return super.getIteratorForGranularity(i);
    }

    @Override // android.view.View
    public int getAccessibilitySelectionStart() {
        return getSelectionStart();
    }

    @Override // android.view.View
    public int getAccessibilitySelectionEnd() {
        return getSelectionEnd();
    }

    @Override // android.view.View
    public void setAccessibilitySelection(int i, int i2) {
        if (getAccessibilitySelectionStart() == i && getAccessibilitySelectionEnd() == i2) {
            return;
        }
        Editor editor = this.mEditor;
        if (editor != null) {
            editor.hideControllers();
        }
        CharSequence iterableTextForAccessibility = getIterableTextForAccessibility();
        if (Math.min(i, i2) >= 0 && Math.max(i, i2) <= iterableTextForAccessibility.length()) {
            Selection.setSelection((Spannable) iterableTextForAccessibility, i, i2);
        } else {
            Selection.removeSelection((Spannable) iterableTextForAccessibility);
        }
    }

    public static class SavedState extends View.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: android.widget.TextView.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        CharSequence error;
        boolean frozenWithFocus;
        int selEnd;
        int selStart;
        CharSequence text;

        SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        @Override // android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.selStart);
            parcel.writeInt(this.selEnd);
            parcel.writeInt(this.frozenWithFocus ? 1 : 0);
            TextUtils.writeToParcel(this.text, parcel, i);
            if (this.error == null) {
                parcel.writeInt(0);
            } else {
                parcel.writeInt(1);
                TextUtils.writeToParcel(this.error, parcel, i);
            }
        }

        public String toString() {
            String str = "TextView.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " start=" + this.selStart + " end=" + this.selEnd;
            if (this.text != null) {
                str = str + " text=" + ((Object) this.text);
            }
            return str + "}";
        }

        private SavedState(Parcel parcel) {
            super(parcel);
            this.selStart = parcel.readInt();
            this.selEnd = parcel.readInt();
            this.frozenWithFocus = parcel.readInt() != 0;
            this.text = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
            if (parcel.readInt() != 0) {
                this.error = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
            }
        }
    }

    private static class CharWrapper implements CharSequence, GetChars, GraphicsOperations {
        private char[] mChars;
        private int mLength;
        private int mStart;

        public CharWrapper(char[] cArr, int i, int i2) {
            this.mChars = cArr;
            this.mStart = i;
            this.mLength = i2;
        }

        void set(char[] cArr, int i, int i2) {
            this.mChars = cArr;
            this.mStart = i;
            this.mLength = i2;
        }

        @Override // java.lang.CharSequence
        public int length() {
            return this.mLength;
        }

        @Override // java.lang.CharSequence
        public char charAt(int i) {
            return this.mChars[i + this.mStart];
        }

        @Override // java.lang.CharSequence
        public String toString() {
            return new String(this.mChars, this.mStart, this.mLength);
        }

        @Override // java.lang.CharSequence
        public CharSequence subSequence(int i, int i2) {
            int i3;
            if (i < 0 || i2 < 0 || i > (i3 = this.mLength) || i2 > i3) {
                throw new IndexOutOfBoundsException(i + ", " + i2);
            }
            return new String(this.mChars, this.mStart + i, i2 - i);
        }

        @Override // android.text.GetChars
        public void getChars(int i, int i2, char[] cArr, int i3) {
            int i4;
            if (i < 0 || i2 < 0 || i > (i4 = this.mLength) || i2 > i4) {
                throw new IndexOutOfBoundsException(i + ", " + i2);
            }
            System.arraycopy(this.mChars, this.mStart + i, cArr, i3, i2 - i);
        }

        @Override // android.text.GraphicsOperations
        public void drawText(Canvas canvas, int i, int i2, float f, float f2, Paint paint) {
            canvas.drawText(this.mChars, i + this.mStart, i2 - i, f, f2, paint);
        }

        @Override // android.text.GraphicsOperations
        public void drawTextRun(Canvas canvas, int i, int i2, int i3, int i4, float f, float f2, int i5, Paint paint) {
            char[] cArr = this.mChars;
            int i6 = this.mStart;
            canvas.drawTextRun(cArr, i + i6, i2 - i, i3 + i6, i4 - i3, f, f2, i5, paint);
        }

        @Override // android.text.GraphicsOperations
        public float measureText(int i, int i2, Paint paint) {
            return paint.measureText(this.mChars, this.mStart + i, i2 - i);
        }

        @Override // android.text.GraphicsOperations
        public int getTextWidths(int i, int i2, float[] fArr, Paint paint) {
            return paint.getTextWidths(this.mChars, this.mStart + i, i2 - i, fArr);
        }

        @Override // android.text.GraphicsOperations
        public float getTextRunAdvances(int i, int i2, int i3, int i4, int i5, float[] fArr, int i6, Paint paint) {
            char[] cArr = this.mChars;
            int i7 = this.mStart;
            return paint.getTextRunAdvances(cArr, i + i7, i2 - i, i3 + i7, i4 - i3, i5, fArr, i6);
        }

        @Override // android.text.GraphicsOperations
        public int getTextRunCursor(int i, int i2, int i3, int i4, int i5, Paint paint) {
            int i6 = i2 - i;
            char[] cArr = this.mChars;
            int i7 = this.mStart;
            return paint.getTextRunCursor(cArr, i + i7, i6, i3, i4 + i7, i5);
        }
    }

    private static final class Marquee extends Handler {
        private static final int MARQUEE_DELAY = 1200;
        private static final float MARQUEE_DELTA_MAX = 0.07f;
        private static final int MARQUEE_PIXELS_PER_SECOND = 30;
        private static final int MARQUEE_RESOLUTION = 33;
        private static final int MARQUEE_RESTART_DELAY = 1200;
        private static final byte MARQUEE_RUNNING = 2;
        private static final byte MARQUEE_STARTING = 1;
        private static final byte MARQUEE_STOPPED = 0;
        private static final int MESSAGE_RESTART = 3;
        private static final int MESSAGE_START = 1;
        private static final int MESSAGE_TICK = 2;
        private float mFadeStop;
        private float mGhostOffset;
        private float mGhostStart;
        private float mMaxFadeScroll;
        private float mMaxScroll;
        private int mRepeatLimit;
        private float mScroll;
        private final float mScrollUnit;
        private byte mStatus = 0;
        private final WeakReference<TextView> mView;

        Marquee(TextView textView) {
            this.mScrollUnit = (textView.getContext().getResources().getDisplayMetrics().density * 30.0f) / 33.0f;
            this.mView = new WeakReference<>(textView);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                this.mStatus = (byte) 2;
                tick();
            } else {
                if (i == 2) {
                    tick();
                    return;
                }
                if (i == 3 && this.mStatus == 2) {
                    int i2 = this.mRepeatLimit;
                    if (i2 >= 0) {
                        this.mRepeatLimit = i2 - 1;
                    }
                    start(this.mRepeatLimit);
                }
            }
        }

        void tick() {
            if (this.mStatus != 2) {
                return;
            }
            removeMessages(2);
            TextView textView = this.mView.get();
            if (textView != null) {
                if (textView.isFocused() || textView.isSelected()) {
                    float f = this.mScroll + this.mScrollUnit;
                    this.mScroll = f;
                    float f2 = this.mMaxScroll;
                    if (f > f2) {
                        this.mScroll = f2;
                        sendEmptyMessageDelayed(3, 1200L);
                    } else {
                        sendEmptyMessageDelayed(2, 33L);
                    }
                    textView.invalidate();
                }
            }
        }

        void stop() {
            this.mStatus = (byte) 0;
            removeMessages(1);
            removeMessages(3);
            removeMessages(2);
            resetScroll();
        }

        private void resetScroll() {
            this.mScroll = 0.0f;
            TextView textView = this.mView.get();
            if (textView != null) {
                textView.invalidate();
            }
        }

        void start(int i) {
            if (i == 0) {
                stop();
                return;
            }
            this.mRepeatLimit = i;
            TextView textView = this.mView.get();
            if (textView == null || textView.mLayout == null) {
                return;
            }
            this.mStatus = (byte) 1;
            this.mScroll = 0.0f;
            int width = (textView.getWidth() - textView.getCompoundPaddingLeft()) - textView.getCompoundPaddingRight();
            float lineWidth = textView.mLayout.getLineWidth(0);
            float f = width;
            float f2 = f / 3.0f;
            float f3 = (lineWidth - f) + f2;
            this.mGhostStart = f3;
            this.mMaxScroll = f3 + f;
            this.mGhostOffset = f2 + lineWidth;
            this.mFadeStop = (f / 6.0f) + lineWidth;
            this.mMaxFadeScroll = f3 + lineWidth + lineWidth;
            textView.invalidate();
            sendEmptyMessageDelayed(1, 1200L);
        }

        float getGhostOffset() {
            return this.mGhostOffset;
        }

        float getScroll() {
            return this.mScroll;
        }

        float getMaxFadeScroll() {
            return this.mMaxFadeScroll;
        }

        boolean shouldDrawLeftFade() {
            return this.mScroll <= this.mFadeStop;
        }

        boolean shouldDrawGhost() {
            return this.mStatus == 2 && this.mScroll > this.mGhostStart;
        }

        boolean isRunning() {
            return this.mStatus == 2;
        }

        boolean isStopped() {
            return this.mStatus == 0;
        }
    }

    private class ChangeWatcher implements TextWatcher, SpanWatcher {
        private CharSequence mBeforeText;

        private ChangeWatcher() {
        }

        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (AccessibilityManager.getInstance(TextView.this.mContext).isEnabled() && ((!TextView.isPasswordInputType(TextView.this.getInputType()) && !TextView.this.hasPasswordTransformationMethod()) || TextView.this.shouldSpeakPasswordsForAccessibility())) {
                this.mBeforeText = charSequence.toString();
            }
            TextView.this.sendBeforeTextChanged(charSequence, i, i2, i3);
        }

        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            TextView.this.handleTextChanged(charSequence, i, i2, i3);
            if (AccessibilityManager.getInstance(TextView.this.mContext).isEnabled()) {
                if (TextView.this.isFocused() || (TextView.this.isSelected() && TextView.this.isShown())) {
                    TextView.this.sendAccessibilityEventTypeViewTextChanged(this.mBeforeText, i, i2, i3);
                    this.mBeforeText = null;
                }
            }
        }

        @Override // android.text.TextWatcher
        public void afterTextChanged(Editable editable) {
            TextView.this.sendAfterTextChanged(editable);
            if (MetaKeyKeyListener.getMetaState(editable, 2048) != 0) {
                MetaKeyKeyListener.stopSelecting(TextView.this, editable);
            }
        }

        @Override // android.text.SpanWatcher
        public void onSpanChanged(Spannable spannable, Object obj, int i, int i2, int i3, int i4) {
            TextView.this.spanChange(spannable, obj, i, i3, i2, i4);
        }

        @Override // android.text.SpanWatcher
        public void onSpanAdded(Spannable spannable, Object obj, int i, int i2) {
            TextView.this.spanChange(spannable, obj, -1, i, -1, i2);
        }

        @Override // android.text.SpanWatcher
        public void onSpanRemoved(Spannable spannable, Object obj, int i, int i2) {
            TextView.this.spanChange(spannable, obj, i, -1, i2, -1);
        }
    }
}
