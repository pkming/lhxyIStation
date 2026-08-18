package android.view.accessibility;

import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pools;
import android.util.SparseLongArray;
import android.view.View;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class AccessibilityNodeInfo implements Parcelable {
    public static final int ACTION_ACCESSIBILITY_FOCUS = 64;
    public static final String ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN = "ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN";
    public static final String ACTION_ARGUMENT_HTML_ELEMENT_STRING = "ACTION_ARGUMENT_HTML_ELEMENT_STRING";
    public static final String ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT = "ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT";
    public static final String ACTION_ARGUMENT_SELECTION_END_INT = "ACTION_ARGUMENT_SELECTION_END_INT";
    public static final String ACTION_ARGUMENT_SELECTION_START_INT = "ACTION_ARGUMENT_SELECTION_START_INT";
    public static final int ACTION_CLEAR_ACCESSIBILITY_FOCUS = 128;
    public static final int ACTION_CLEAR_FOCUS = 2;
    public static final int ACTION_CLEAR_SELECTION = 8;
    public static final int ACTION_CLICK = 16;
    public static final int ACTION_COLLAPSE = 524288;
    public static final int ACTION_COPY = 16384;
    public static final int ACTION_CUT = 65536;
    public static final int ACTION_DISMISS = 1048576;
    public static final int ACTION_EXPAND = 262144;
    public static final int ACTION_FOCUS = 1;
    public static final int ACTION_LONG_CLICK = 32;
    public static final int ACTION_NEXT_AT_MOVEMENT_GRANULARITY = 256;
    public static final int ACTION_NEXT_HTML_ELEMENT = 1024;
    public static final int ACTION_PASTE = 32768;
    public static final int ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY = 512;
    public static final int ACTION_PREVIOUS_HTML_ELEMENT = 2048;
    public static final int ACTION_SCROLL_BACKWARD = 8192;
    public static final int ACTION_SCROLL_FORWARD = 4096;
    public static final int ACTION_SELECT = 4;
    public static final int ACTION_SET_SELECTION = 131072;
    public static final int ACTIVE_WINDOW_ID = -1;
    private static final int BOOLEAN_PROPERTY_ACCESSIBILITY_FOCUSED = 1024;
    private static final int BOOLEAN_PROPERTY_CHECKABLE = 1;
    private static final int BOOLEAN_PROPERTY_CHECKED = 2;
    private static final int BOOLEAN_PROPERTY_CLICKABLE = 32;
    private static final int BOOLEAN_PROPERTY_CONTENT_INVALID = 65536;
    private static final int BOOLEAN_PROPERTY_DISMISSABLE = 16384;
    private static final int BOOLEAN_PROPERTY_EDITABLE = 4096;
    private static final int BOOLEAN_PROPERTY_ENABLED = 128;
    private static final int BOOLEAN_PROPERTY_FOCUSABLE = 4;
    private static final int BOOLEAN_PROPERTY_FOCUSED = 8;
    private static final int BOOLEAN_PROPERTY_LONG_CLICKABLE = 64;
    private static final int BOOLEAN_PROPERTY_MULTI_LINE = 32768;
    private static final int BOOLEAN_PROPERTY_OPENS_POPUP = 8192;
    private static final int BOOLEAN_PROPERTY_PASSWORD = 256;
    private static final int BOOLEAN_PROPERTY_SCROLLABLE = 512;
    private static final int BOOLEAN_PROPERTY_SELECTED = 16;
    private static final int BOOLEAN_PROPERTY_VISIBLE_TO_USER = 2048;
    private static final boolean DEBUG = false;
    public static final int FLAG_INCLUDE_NOT_IMPORTANT_VIEWS = 8;
    public static final int FLAG_PREFETCH_DESCENDANTS = 4;
    public static final int FLAG_PREFETCH_PREDECESSORS = 1;
    public static final int FLAG_PREFETCH_SIBLINGS = 2;
    public static final int FLAG_REPORT_VIEW_IDS = 16;
    public static final int FOCUS_ACCESSIBILITY = 2;
    public static final int FOCUS_INPUT = 1;
    private static final int MAX_POOL_SIZE = 50;
    public static final int MOVEMENT_GRANULARITY_CHARACTER = 1;
    public static final int MOVEMENT_GRANULARITY_LINE = 4;
    public static final int MOVEMENT_GRANULARITY_PAGE = 16;
    public static final int MOVEMENT_GRANULARITY_PARAGRAPH = 8;
    public static final int MOVEMENT_GRANULARITY_WORD = 2;
    public static final int UNDEFINED = -1;
    private static final long VIRTUAL_DESCENDANT_ID_MASK = -4294967296L;
    private static final int VIRTUAL_DESCENDANT_ID_SHIFT = 32;
    private int mActions;
    private int mBooleanProperties;
    private final Rect mBoundsInParent;
    private final Rect mBoundsInScreen;
    private final SparseLongArray mChildNodeIds;
    private CharSequence mClassName;
    private CollectionInfo mCollectionInfo;
    private CollectionItemInfo mCollectionItemInfo;
    private int mConnectionId;
    private CharSequence mContentDescription;
    private Bundle mExtras;
    private int mInputType;
    private long mLabelForId;
    private long mLabeledById;
    private int mLiveRegion;
    private int mMovementGranularities;
    private CharSequence mPackageName;
    private long mParentNodeId;
    private RangeInfo mRangeInfo;
    private boolean mSealed;
    private long mSourceNodeId;
    private CharSequence mText;
    private int mTextSelectionEnd;
    private int mTextSelectionStart;
    private String mViewIdResourceName;
    private int mWindowId = -1;
    public static final long ROOT_NODE_ID = makeNodeId(-1, -1);
    private static final Pools.SynchronizedPool<AccessibilityNodeInfo> sPool = new Pools.SynchronizedPool<>(50);
    public static final Parcelable.Creator<AccessibilityNodeInfo> CREATOR = new Parcelable.Creator<AccessibilityNodeInfo>() { // from class: android.view.accessibility.AccessibilityNodeInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AccessibilityNodeInfo createFromParcel(Parcel parcel) {
            AccessibilityNodeInfo accessibilityNodeInfoObtain = AccessibilityNodeInfo.obtain();
            accessibilityNodeInfoObtain.initFromParcel(parcel);
            return accessibilityNodeInfoObtain;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AccessibilityNodeInfo[] newArray(int i) {
            return new AccessibilityNodeInfo[i];
        }
    };

    public static int getAccessibilityViewId(long j) {
        return (int) j;
    }

    private static String getActionSymbolicName(int i) {
        if (i == 1) {
            return "ACTION_FOCUS";
        }
        if (i == 2) {
            return "ACTION_CLEAR_FOCUS";
        }
        switch (i) {
            case 4:
                return "ACTION_SELECT";
            case 8:
                return "ACTION_CLEAR_SELECTION";
            case 16:
                return "ACTION_CLICK";
            case 32:
                return "ACTION_LONG_CLICK";
            case 64:
                return "ACTION_ACCESSIBILITY_FOCUS";
            case 128:
                return "ACTION_CLEAR_ACCESSIBILITY_FOCUS";
            case 256:
                return "ACTION_NEXT_AT_MOVEMENT_GRANULARITY";
            case 512:
                return "ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY";
            case 1024:
                return "ACTION_NEXT_HTML_ELEMENT";
            case 2048:
                return "ACTION_PREVIOUS_HTML_ELEMENT";
            case 4096:
                return "ACTION_SCROLL_FORWARD";
            case 8192:
                return "ACTION_SCROLL_BACKWARD";
            case 16384:
                return "ACTION_COPY";
            case 32768:
                return "ACTION_PASTE";
            case 65536:
                return "ACTION_CUT";
            case 131072:
                return "ACTION_SET_SELECTION";
            default:
                return "ACTION_UNKNOWN";
        }
    }

    public static int getVirtualDescendantId(long j) {
        return (int) ((j & VIRTUAL_DESCENDANT_ID_MASK) >> 32);
    }

    public static long makeNodeId(int i, int i2) {
        return ((long) i) | (((long) i2) << 32);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    private AccessibilityNodeInfo() {
        long j = ROOT_NODE_ID;
        this.mSourceNodeId = j;
        this.mParentNodeId = j;
        this.mLabelForId = j;
        this.mLabeledById = j;
        this.mBoundsInParent = new Rect();
        this.mBoundsInScreen = new Rect();
        this.mChildNodeIds = new SparseLongArray();
        this.mTextSelectionStart = -1;
        this.mTextSelectionEnd = -1;
        this.mInputType = 0;
        this.mLiveRegion = 0;
        this.mConnectionId = -1;
    }

    public void setSource(View view) {
        setSource(view, -1);
    }

    public void setSource(View view, int i) {
        enforceNotSealed();
        this.mWindowId = view != null ? view.getAccessibilityWindowId() : -1;
        this.mSourceNodeId = makeNodeId(view != null ? view.getAccessibilityViewId() : -1, i);
    }

    public AccessibilityNodeInfo findFocus(int i) {
        enforceSealed();
        enforceValidFocusType(i);
        if (canPerformRequestOverConnection(this.mSourceNodeId)) {
            return AccessibilityInteractionClient.getInstance().findFocus(this.mConnectionId, this.mWindowId, this.mSourceNodeId, i);
        }
        return null;
    }

    public AccessibilityNodeInfo focusSearch(int i) {
        enforceSealed();
        enforceValidFocusDirection(i);
        if (canPerformRequestOverConnection(this.mSourceNodeId)) {
            return AccessibilityInteractionClient.getInstance().focusSearch(this.mConnectionId, this.mWindowId, this.mSourceNodeId, i);
        }
        return null;
    }

    public int getWindowId() {
        return this.mWindowId;
    }

    public boolean refresh(boolean z) {
        AccessibilityNodeInfo accessibilityNodeInfoFindAccessibilityNodeInfoByAccessibilityId;
        enforceSealed();
        if (!canPerformRequestOverConnection(this.mSourceNodeId) || (accessibilityNodeInfoFindAccessibilityNodeInfoByAccessibilityId = AccessibilityInteractionClient.getInstance().findAccessibilityNodeInfoByAccessibilityId(this.mConnectionId, this.mWindowId, this.mSourceNodeId, z, 0)) == null) {
            return false;
        }
        init(accessibilityNodeInfoFindAccessibilityNodeInfoByAccessibilityId);
        accessibilityNodeInfoFindAccessibilityNodeInfoByAccessibilityId.recycle();
        return true;
    }

    public boolean refresh() {
        return refresh(false);
    }

    public SparseLongArray getChildNodeIds() {
        return this.mChildNodeIds;
    }

    public int getChildCount() {
        return this.mChildNodeIds.size();
    }

    public AccessibilityNodeInfo getChild(int i) {
        enforceSealed();
        if (!canPerformRequestOverConnection(this.mSourceNodeId)) {
            return null;
        }
        return AccessibilityInteractionClient.getInstance().findAccessibilityNodeInfoByAccessibilityId(this.mConnectionId, this.mWindowId, this.mChildNodeIds.get(i), false, 4);
    }

    public void addChild(View view) {
        addChild(view, -1);
    }

    public void addChild(View view, int i) {
        enforceNotSealed();
        this.mChildNodeIds.put(this.mChildNodeIds.size(), makeNodeId(view != null ? view.getAccessibilityViewId() : -1, i));
    }

    public int getActions() {
        return this.mActions;
    }

    public void addAction(int i) {
        enforceNotSealed();
        this.mActions = i | this.mActions;
    }

    public void setMovementGranularities(int i) {
        enforceNotSealed();
        this.mMovementGranularities = i;
    }

    public int getMovementGranularities() {
        return this.mMovementGranularities;
    }

    public boolean performAction(int i) {
        enforceSealed();
        if (canPerformRequestOverConnection(this.mSourceNodeId)) {
            return AccessibilityInteractionClient.getInstance().performAccessibilityAction(this.mConnectionId, this.mWindowId, this.mSourceNodeId, i, null);
        }
        return false;
    }

    public boolean performAction(int i, Bundle bundle) {
        enforceSealed();
        if (canPerformRequestOverConnection(this.mSourceNodeId)) {
            return AccessibilityInteractionClient.getInstance().performAccessibilityAction(this.mConnectionId, this.mWindowId, this.mSourceNodeId, i, bundle);
        }
        return false;
    }

    public List<AccessibilityNodeInfo> findAccessibilityNodeInfosByText(String str) {
        enforceSealed();
        if (!canPerformRequestOverConnection(this.mSourceNodeId)) {
            return Collections.emptyList();
        }
        return AccessibilityInteractionClient.getInstance().findAccessibilityNodeInfosByText(this.mConnectionId, this.mWindowId, this.mSourceNodeId, str);
    }

    public List<AccessibilityNodeInfo> findAccessibilityNodeInfosByViewId(String str) {
        enforceSealed();
        if (!canPerformRequestOverConnection(this.mSourceNodeId)) {
            return Collections.emptyList();
        }
        return AccessibilityInteractionClient.getInstance().findAccessibilityNodeInfosByViewId(this.mConnectionId, this.mWindowId, this.mSourceNodeId, str);
    }

    public AccessibilityNodeInfo getParent() {
        enforceSealed();
        if (canPerformRequestOverConnection(this.mParentNodeId)) {
            return AccessibilityInteractionClient.getInstance().findAccessibilityNodeInfoByAccessibilityId(this.mConnectionId, this.mWindowId, this.mParentNodeId, false, 6);
        }
        return null;
    }

    public long getParentNodeId() {
        return this.mParentNodeId;
    }

    public void setParent(View view) {
        setParent(view, -1);
    }

    public void setParent(View view, int i) {
        enforceNotSealed();
        this.mParentNodeId = makeNodeId(view != null ? view.getAccessibilityViewId() : -1, i);
    }

    public void getBoundsInParent(Rect rect) {
        rect.set(this.mBoundsInParent.left, this.mBoundsInParent.top, this.mBoundsInParent.right, this.mBoundsInParent.bottom);
    }

    public void setBoundsInParent(Rect rect) {
        enforceNotSealed();
        this.mBoundsInParent.set(rect.left, rect.top, rect.right, rect.bottom);
    }

    public void getBoundsInScreen(Rect rect) {
        rect.set(this.mBoundsInScreen.left, this.mBoundsInScreen.top, this.mBoundsInScreen.right, this.mBoundsInScreen.bottom);
    }

    public void setBoundsInScreen(Rect rect) {
        enforceNotSealed();
        this.mBoundsInScreen.set(rect.left, rect.top, rect.right, rect.bottom);
    }

    public boolean isCheckable() {
        return getBooleanProperty(1);
    }

    public void setCheckable(boolean z) {
        setBooleanProperty(1, z);
    }

    public boolean isChecked() {
        return getBooleanProperty(2);
    }

    public void setChecked(boolean z) {
        setBooleanProperty(2, z);
    }

    public boolean isFocusable() {
        return getBooleanProperty(4);
    }

    public void setFocusable(boolean z) {
        setBooleanProperty(4, z);
    }

    public boolean isFocused() {
        return getBooleanProperty(8);
    }

    public void setFocused(boolean z) {
        setBooleanProperty(8, z);
    }

    public boolean isVisibleToUser() {
        return getBooleanProperty(2048);
    }

    public void setVisibleToUser(boolean z) {
        setBooleanProperty(2048, z);
    }

    public boolean isAccessibilityFocused() {
        return getBooleanProperty(1024);
    }

    public void setAccessibilityFocused(boolean z) {
        setBooleanProperty(1024, z);
    }

    public boolean isSelected() {
        return getBooleanProperty(16);
    }

    public void setSelected(boolean z) {
        setBooleanProperty(16, z);
    }

    public boolean isClickable() {
        return getBooleanProperty(32);
    }

    public void setClickable(boolean z) {
        setBooleanProperty(32, z);
    }

    public boolean isLongClickable() {
        return getBooleanProperty(64);
    }

    public void setLongClickable(boolean z) {
        setBooleanProperty(64, z);
    }

    public boolean isEnabled() {
        return getBooleanProperty(128);
    }

    public void setEnabled(boolean z) {
        setBooleanProperty(128, z);
    }

    public boolean isPassword() {
        return getBooleanProperty(256);
    }

    public void setPassword(boolean z) {
        setBooleanProperty(256, z);
    }

    public boolean isScrollable() {
        return getBooleanProperty(512);
    }

    public void setScrollable(boolean z) {
        setBooleanProperty(512, z);
    }

    public boolean isEditable() {
        return getBooleanProperty(4096);
    }

    public void setEditable(boolean z) {
        setBooleanProperty(4096, z);
    }

    public CollectionInfo getCollectionInfo() {
        return this.mCollectionInfo;
    }

    public void setCollectionInfo(CollectionInfo collectionInfo) {
        enforceNotSealed();
        this.mCollectionInfo = collectionInfo;
    }

    public CollectionItemInfo getCollectionItemInfo() {
        return this.mCollectionItemInfo;
    }

    public void setCollectionItemInfo(CollectionItemInfo collectionItemInfo) {
        enforceNotSealed();
        this.mCollectionItemInfo = collectionItemInfo;
    }

    public RangeInfo getRangeInfo() {
        return this.mRangeInfo;
    }

    public void setRangeInfo(RangeInfo rangeInfo) {
        enforceNotSealed();
        this.mRangeInfo = rangeInfo;
    }

    public boolean isContentInvalid() {
        return getBooleanProperty(65536);
    }

    public void setContentInvalid(boolean z) {
        setBooleanProperty(65536, z);
    }

    public int getLiveRegion() {
        return this.mLiveRegion;
    }

    public void setLiveRegion(int i) {
        enforceNotSealed();
        this.mLiveRegion = i;
    }

    public boolean isMultiLine() {
        return getBooleanProperty(32768);
    }

    public void setMultiLine(boolean z) {
        setBooleanProperty(32768, z);
    }

    public boolean canOpenPopup() {
        return getBooleanProperty(8192);
    }

    public void setCanOpenPopup(boolean z) {
        enforceNotSealed();
        setBooleanProperty(8192, z);
    }

    public boolean isDismissable() {
        return getBooleanProperty(16384);
    }

    public void setDismissable(boolean z) {
        setBooleanProperty(16384, z);
    }

    public CharSequence getPackageName() {
        return this.mPackageName;
    }

    public void setPackageName(CharSequence charSequence) {
        enforceNotSealed();
        this.mPackageName = charSequence;
    }

    public CharSequence getClassName() {
        return this.mClassName;
    }

    public void setClassName(CharSequence charSequence) {
        enforceNotSealed();
        this.mClassName = charSequence;
    }

    public CharSequence getText() {
        return this.mText;
    }

    public void setText(CharSequence charSequence) {
        enforceNotSealed();
        this.mText = charSequence;
    }

    public CharSequence getContentDescription() {
        return this.mContentDescription;
    }

    public void setContentDescription(CharSequence charSequence) {
        enforceNotSealed();
        this.mContentDescription = charSequence;
    }

    public void setLabelFor(View view) {
        setLabelFor(view, -1);
    }

    public void setLabelFor(View view, int i) {
        enforceNotSealed();
        this.mLabelForId = makeNodeId(view != null ? view.getAccessibilityViewId() : -1, i);
    }

    public AccessibilityNodeInfo getLabelFor() {
        enforceSealed();
        if (canPerformRequestOverConnection(this.mLabelForId)) {
            return AccessibilityInteractionClient.getInstance().findAccessibilityNodeInfoByAccessibilityId(this.mConnectionId, this.mWindowId, this.mLabelForId, false, 6);
        }
        return null;
    }

    public void setLabeledBy(View view) {
        setLabeledBy(view, -1);
    }

    public void setLabeledBy(View view, int i) {
        enforceNotSealed();
        this.mLabeledById = makeNodeId(view != null ? view.getAccessibilityViewId() : -1, i);
    }

    public AccessibilityNodeInfo getLabeledBy() {
        enforceSealed();
        if (canPerformRequestOverConnection(this.mLabeledById)) {
            return AccessibilityInteractionClient.getInstance().findAccessibilityNodeInfoByAccessibilityId(this.mConnectionId, this.mWindowId, this.mLabeledById, false, 6);
        }
        return null;
    }

    public void setViewIdResourceName(String str) {
        enforceNotSealed();
        this.mViewIdResourceName = str;
    }

    public String getViewIdResourceName() {
        return this.mViewIdResourceName;
    }

    public int getTextSelectionStart() {
        return this.mTextSelectionStart;
    }

    public int getTextSelectionEnd() {
        return this.mTextSelectionEnd;
    }

    public void setTextSelection(int i, int i2) {
        enforceNotSealed();
        this.mTextSelectionStart = i;
        this.mTextSelectionEnd = i2;
    }

    public int getInputType() {
        return this.mInputType;
    }

    public void setInputType(int i) {
        enforceNotSealed();
        this.mInputType = i;
    }

    public Bundle getExtras() {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        return this.mExtras;
    }

    private boolean getBooleanProperty(int i) {
        return (i & this.mBooleanProperties) != 0;
    }

    private void setBooleanProperty(int i, boolean z) {
        enforceNotSealed();
        if (z) {
            this.mBooleanProperties = i | this.mBooleanProperties;
        } else {
            this.mBooleanProperties = (~i) & this.mBooleanProperties;
        }
    }

    public void setConnectionId(int i) {
        enforceNotSealed();
        this.mConnectionId = i;
    }

    public long getSourceNodeId() {
        return this.mSourceNodeId;
    }

    public void setSealed(boolean z) {
        this.mSealed = z;
    }

    public boolean isSealed() {
        return this.mSealed;
    }

    protected void enforceSealed() {
        if (!isSealed()) {
            throw new IllegalStateException("Cannot perform this action on a not sealed instance.");
        }
    }

    private void enforceValidFocusDirection(int i) {
        if (i != 1 && i != 2 && i != 17 && i != 33 && i != 66 && i != 130) {
            throw new IllegalArgumentException("Unknown direction: " + i);
        }
    }

    private void enforceValidFocusType(int i) {
        if (i != 1 && i != 2) {
            throw new IllegalArgumentException("Unknown focus type: " + i);
        }
    }

    protected void enforceNotSealed() {
        if (isSealed()) {
            throw new IllegalStateException("Cannot perform this action on a sealed instance.");
        }
    }

    public static AccessibilityNodeInfo obtain(View view) {
        AccessibilityNodeInfo accessibilityNodeInfoObtain = obtain();
        accessibilityNodeInfoObtain.setSource(view);
        return accessibilityNodeInfoObtain;
    }

    public static AccessibilityNodeInfo obtain(View view, int i) {
        AccessibilityNodeInfo accessibilityNodeInfoObtain = obtain();
        accessibilityNodeInfoObtain.setSource(view, i);
        return accessibilityNodeInfoObtain;
    }

    public static AccessibilityNodeInfo obtain() {
        AccessibilityNodeInfo accessibilityNodeInfoAcquire = sPool.acquire();
        return accessibilityNodeInfoAcquire != null ? accessibilityNodeInfoAcquire : new AccessibilityNodeInfo();
    }

    public static AccessibilityNodeInfo obtain(AccessibilityNodeInfo accessibilityNodeInfo) {
        AccessibilityNodeInfo accessibilityNodeInfoObtain = obtain();
        accessibilityNodeInfoObtain.init(accessibilityNodeInfo);
        return accessibilityNodeInfoObtain;
    }

    public void recycle() {
        clear();
        sPool.release(this);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(isSealed() ? 1 : 0);
        parcel.writeLong(this.mSourceNodeId);
        parcel.writeInt(this.mWindowId);
        parcel.writeLong(this.mParentNodeId);
        parcel.writeLong(this.mLabelForId);
        parcel.writeLong(this.mLabeledById);
        parcel.writeInt(this.mConnectionId);
        SparseLongArray sparseLongArray = this.mChildNodeIds;
        int size = sparseLongArray.size();
        parcel.writeInt(size);
        for (int i2 = 0; i2 < size; i2++) {
            parcel.writeLong(sparseLongArray.valueAt(i2));
        }
        parcel.writeInt(this.mBoundsInParent.top);
        parcel.writeInt(this.mBoundsInParent.bottom);
        parcel.writeInt(this.mBoundsInParent.left);
        parcel.writeInt(this.mBoundsInParent.right);
        parcel.writeInt(this.mBoundsInScreen.top);
        parcel.writeInt(this.mBoundsInScreen.bottom);
        parcel.writeInt(this.mBoundsInScreen.left);
        parcel.writeInt(this.mBoundsInScreen.right);
        parcel.writeInt(this.mActions);
        parcel.writeInt(this.mMovementGranularities);
        parcel.writeInt(this.mBooleanProperties);
        parcel.writeCharSequence(this.mPackageName);
        parcel.writeCharSequence(this.mClassName);
        parcel.writeCharSequence(this.mText);
        parcel.writeCharSequence(this.mContentDescription);
        parcel.writeString(this.mViewIdResourceName);
        parcel.writeInt(this.mTextSelectionStart);
        parcel.writeInt(this.mTextSelectionEnd);
        parcel.writeInt(this.mInputType);
        parcel.writeInt(this.mLiveRegion);
        if (this.mExtras != null) {
            parcel.writeInt(1);
            parcel.writeBundle(this.mExtras);
        } else {
            parcel.writeInt(0);
        }
        if (this.mRangeInfo != null) {
            parcel.writeInt(1);
            parcel.writeInt(this.mRangeInfo.getType());
            parcel.writeFloat(this.mRangeInfo.getMin());
            parcel.writeFloat(this.mRangeInfo.getMax());
            parcel.writeFloat(this.mRangeInfo.getCurrent());
        } else {
            parcel.writeInt(0);
        }
        if (this.mCollectionInfo != null) {
            parcel.writeInt(1);
            parcel.writeInt(this.mCollectionInfo.getRowCount());
            parcel.writeInt(this.mCollectionInfo.getColumnCount());
            parcel.writeInt(this.mCollectionInfo.isHierarchical() ? 1 : 0);
        } else {
            parcel.writeInt(0);
        }
        if (this.mCollectionItemInfo != null) {
            parcel.writeInt(1);
            parcel.writeInt(this.mCollectionItemInfo.getColumnIndex());
            parcel.writeInt(this.mCollectionItemInfo.getColumnSpan());
            parcel.writeInt(this.mCollectionItemInfo.getRowIndex());
            parcel.writeInt(this.mCollectionItemInfo.getRowSpan());
            parcel.writeInt(this.mCollectionItemInfo.isHeading() ? 1 : 0);
        } else {
            parcel.writeInt(0);
        }
        recycle();
    }

    private void init(AccessibilityNodeInfo accessibilityNodeInfo) {
        this.mSealed = accessibilityNodeInfo.mSealed;
        this.mSourceNodeId = accessibilityNodeInfo.mSourceNodeId;
        this.mParentNodeId = accessibilityNodeInfo.mParentNodeId;
        this.mLabelForId = accessibilityNodeInfo.mLabelForId;
        this.mLabeledById = accessibilityNodeInfo.mLabeledById;
        this.mWindowId = accessibilityNodeInfo.mWindowId;
        this.mConnectionId = accessibilityNodeInfo.mConnectionId;
        this.mBoundsInParent.set(accessibilityNodeInfo.mBoundsInParent);
        this.mBoundsInScreen.set(accessibilityNodeInfo.mBoundsInScreen);
        this.mPackageName = accessibilityNodeInfo.mPackageName;
        this.mClassName = accessibilityNodeInfo.mClassName;
        this.mText = accessibilityNodeInfo.mText;
        this.mContentDescription = accessibilityNodeInfo.mContentDescription;
        this.mViewIdResourceName = accessibilityNodeInfo.mViewIdResourceName;
        this.mActions = accessibilityNodeInfo.mActions;
        this.mBooleanProperties = accessibilityNodeInfo.mBooleanProperties;
        this.mMovementGranularities = accessibilityNodeInfo.mMovementGranularities;
        int size = accessibilityNodeInfo.mChildNodeIds.size();
        for (int i = 0; i < size; i++) {
            this.mChildNodeIds.put(i, accessibilityNodeInfo.mChildNodeIds.valueAt(i));
        }
        this.mTextSelectionStart = accessibilityNodeInfo.mTextSelectionStart;
        this.mTextSelectionEnd = accessibilityNodeInfo.mTextSelectionEnd;
        this.mInputType = accessibilityNodeInfo.mInputType;
        this.mLiveRegion = accessibilityNodeInfo.mLiveRegion;
        Bundle bundle = accessibilityNodeInfo.mExtras;
        if (bundle != null && !bundle.isEmpty()) {
            getExtras().putAll(accessibilityNodeInfo.mExtras);
        }
        RangeInfo rangeInfo = accessibilityNodeInfo.mRangeInfo;
        this.mRangeInfo = rangeInfo != null ? RangeInfo.obtain(rangeInfo) : null;
        CollectionInfo collectionInfo = accessibilityNodeInfo.mCollectionInfo;
        this.mCollectionInfo = collectionInfo != null ? CollectionInfo.obtain(collectionInfo) : null;
        CollectionItemInfo collectionItemInfo = accessibilityNodeInfo.mCollectionItemInfo;
        this.mCollectionItemInfo = collectionItemInfo != null ? CollectionItemInfo.obtain(collectionItemInfo) : null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initFromParcel(Parcel parcel) {
        this.mSealed = parcel.readInt() == 1;
        this.mSourceNodeId = parcel.readLong();
        this.mWindowId = parcel.readInt();
        this.mParentNodeId = parcel.readLong();
        this.mLabelForId = parcel.readLong();
        this.mLabeledById = parcel.readLong();
        this.mConnectionId = parcel.readInt();
        SparseLongArray sparseLongArray = this.mChildNodeIds;
        int i = parcel.readInt();
        for (int i2 = 0; i2 < i; i2++) {
            sparseLongArray.put(i2, parcel.readLong());
        }
        this.mBoundsInParent.top = parcel.readInt();
        this.mBoundsInParent.bottom = parcel.readInt();
        this.mBoundsInParent.left = parcel.readInt();
        this.mBoundsInParent.right = parcel.readInt();
        this.mBoundsInScreen.top = parcel.readInt();
        this.mBoundsInScreen.bottom = parcel.readInt();
        this.mBoundsInScreen.left = parcel.readInt();
        this.mBoundsInScreen.right = parcel.readInt();
        this.mActions = parcel.readInt();
        this.mMovementGranularities = parcel.readInt();
        this.mBooleanProperties = parcel.readInt();
        this.mPackageName = parcel.readCharSequence();
        this.mClassName = parcel.readCharSequence();
        this.mText = parcel.readCharSequence();
        this.mContentDescription = parcel.readCharSequence();
        this.mViewIdResourceName = parcel.readString();
        this.mTextSelectionStart = parcel.readInt();
        this.mTextSelectionEnd = parcel.readInt();
        this.mInputType = parcel.readInt();
        this.mLiveRegion = parcel.readInt();
        if (parcel.readInt() == 1) {
            getExtras().putAll(parcel.readBundle());
        }
        if (parcel.readInt() == 1) {
            this.mRangeInfo = RangeInfo.obtain(parcel.readInt(), parcel.readFloat(), parcel.readFloat(), parcel.readFloat());
        }
        if (parcel.readInt() == 1) {
            this.mCollectionInfo = CollectionInfo.obtain(parcel.readInt(), parcel.readInt(), parcel.readInt() == 1);
        }
        if (parcel.readInt() == 1) {
            this.mCollectionItemInfo = CollectionItemInfo.obtain(parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt() == 1);
        }
    }

    private void clear() {
        this.mSealed = false;
        long j = ROOT_NODE_ID;
        this.mSourceNodeId = j;
        this.mParentNodeId = j;
        this.mLabelForId = j;
        this.mLabeledById = j;
        this.mWindowId = -1;
        this.mConnectionId = -1;
        this.mMovementGranularities = 0;
        this.mChildNodeIds.clear();
        this.mBoundsInParent.set(0, 0, 0, 0);
        this.mBoundsInScreen.set(0, 0, 0, 0);
        this.mBooleanProperties = 0;
        this.mPackageName = null;
        this.mClassName = null;
        this.mText = null;
        this.mContentDescription = null;
        this.mViewIdResourceName = null;
        this.mActions = 0;
        this.mTextSelectionStart = -1;
        this.mTextSelectionEnd = -1;
        this.mInputType = 0;
        this.mLiveRegion = 0;
        Bundle bundle = this.mExtras;
        if (bundle != null) {
            bundle.clear();
        }
        RangeInfo rangeInfo = this.mRangeInfo;
        if (rangeInfo != null) {
            rangeInfo.recycle();
            this.mRangeInfo = null;
        }
        CollectionInfo collectionInfo = this.mCollectionInfo;
        if (collectionInfo != null) {
            collectionInfo.recycle();
            this.mCollectionInfo = null;
        }
        CollectionItemInfo collectionItemInfo = this.mCollectionItemInfo;
        if (collectionItemInfo != null) {
            collectionItemInfo.recycle();
            this.mCollectionItemInfo = null;
        }
    }

    private static String getMovementGranularitySymbolicName(int i) {
        if (i == 1) {
            return "MOVEMENT_GRANULARITY_CHARACTER";
        }
        if (i == 2) {
            return "MOVEMENT_GRANULARITY_WORD";
        }
        if (i == 4) {
            return "MOVEMENT_GRANULARITY_LINE";
        }
        if (i == 8) {
            return "MOVEMENT_GRANULARITY_PARAGRAPH";
        }
        if (i == 16) {
            return "MOVEMENT_GRANULARITY_PAGE";
        }
        throw new IllegalArgumentException("Unknown movement granularity: " + i);
    }

    private boolean canPerformRequestOverConnection(long j) {
        return (this.mWindowId == -1 || getAccessibilityViewId(j) == -1 || this.mConnectionId == -1) ? false : true;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AccessibilityNodeInfo accessibilityNodeInfo = (AccessibilityNodeInfo) obj;
        return this.mSourceNodeId == accessibilityNodeInfo.mSourceNodeId && this.mWindowId == accessibilityNodeInfo.mWindowId;
    }

    public int hashCode() {
        return ((((getAccessibilityViewId(this.mSourceNodeId) + 31) * 31) + getVirtualDescendantId(this.mSourceNodeId)) * 31) + this.mWindowId;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append("; boundsInParent: " + this.mBoundsInParent);
        sb.append("; boundsInScreen: " + this.mBoundsInScreen);
        sb.append("; packageName: ").append(this.mPackageName);
        sb.append("; className: ").append(this.mClassName);
        sb.append("; text: ").append(this.mText);
        sb.append("; contentDescription: ").append(this.mContentDescription);
        sb.append("; viewIdResName: ").append(this.mViewIdResourceName);
        sb.append("; checkable: ").append(isCheckable());
        sb.append("; checked: ").append(isChecked());
        sb.append("; focusable: ").append(isFocusable());
        sb.append("; focused: ").append(isFocused());
        sb.append("; selected: ").append(isSelected());
        sb.append("; clickable: ").append(isClickable());
        sb.append("; longClickable: ").append(isLongClickable());
        sb.append("; enabled: ").append(isEnabled());
        sb.append("; password: ").append(isPassword());
        sb.append("; scrollable: " + isScrollable());
        sb.append("; [");
        int i = this.mActions;
        while (i != 0) {
            int iNumberOfTrailingZeros = 1 << Integer.numberOfTrailingZeros(i);
            i &= ~iNumberOfTrailingZeros;
            sb.append(getActionSymbolicName(iNumberOfTrailingZeros));
            if (i != 0) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public static final class RangeInfo {
        private static final int MAX_POOL_SIZE = 10;
        public static final int RANGE_TYPE_FLOAT = 1;
        public static final int RANGE_TYPE_INT = 0;
        public static final int RANGE_TYPE_PERCENT = 2;
        private static final Pools.SynchronizedPool<RangeInfo> sPool = new Pools.SynchronizedPool<>(10);
        private float mCurrent;
        private float mMax;
        private float mMin;
        private int mType;

        public static RangeInfo obtain(RangeInfo rangeInfo) {
            return obtain(rangeInfo.mType, rangeInfo.mMin, rangeInfo.mMax, rangeInfo.mCurrent);
        }

        public static RangeInfo obtain(int i, float f, float f2, float f3) {
            RangeInfo rangeInfoAcquire = sPool.acquire();
            return rangeInfoAcquire != null ? rangeInfoAcquire : new RangeInfo(i, f, f2, f3);
        }

        private RangeInfo(int i, float f, float f2, float f3) {
            this.mType = i;
            this.mMin = f;
            this.mMax = f2;
            this.mCurrent = f3;
        }

        public int getType() {
            return this.mType;
        }

        public float getMin() {
            return this.mMin;
        }

        public float getMax() {
            return this.mMax;
        }

        public float getCurrent() {
            return this.mCurrent;
        }

        void recycle() {
            clear();
            sPool.release(this);
        }

        private void clear() {
            this.mType = 0;
            this.mMin = 0.0f;
            this.mMax = 0.0f;
            this.mCurrent = 0.0f;
        }
    }

    public static final class CollectionInfo {
        private static final int MAX_POOL_SIZE = 20;
        private static final Pools.SynchronizedPool<CollectionInfo> sPool = new Pools.SynchronizedPool<>(20);
        private int mColumnCount;
        private boolean mHierarchical;
        private int mRowCount;

        public static CollectionInfo obtain(CollectionInfo collectionInfo) {
            return obtain(collectionInfo.mRowCount, collectionInfo.mColumnCount, collectionInfo.mHierarchical);
        }

        public static CollectionInfo obtain(int i, int i2, boolean z) {
            CollectionInfo collectionInfoAcquire = sPool.acquire();
            return collectionInfoAcquire != null ? collectionInfoAcquire : new CollectionInfo(i, i2, z);
        }

        private CollectionInfo(int i, int i2, boolean z) {
            this.mRowCount = i;
            this.mColumnCount = i2;
            this.mHierarchical = z;
        }

        public int getRowCount() {
            return this.mRowCount;
        }

        public int getColumnCount() {
            return this.mColumnCount;
        }

        public boolean isHierarchical() {
            return this.mHierarchical;
        }

        void recycle() {
            clear();
            sPool.release(this);
        }

        private void clear() {
            this.mRowCount = 0;
            this.mColumnCount = 0;
            this.mHierarchical = false;
        }
    }

    public static final class CollectionItemInfo {
        private static final int MAX_POOL_SIZE = 20;
        private static final Pools.SynchronizedPool<CollectionItemInfo> sPool = new Pools.SynchronizedPool<>(20);
        private int mColumnIndex;
        private int mColumnSpan;
        private boolean mHeading;
        private int mRowIndex;
        private int mRowSpan;

        public static CollectionItemInfo obtain(CollectionItemInfo collectionItemInfo) {
            return obtain(collectionItemInfo.mRowIndex, collectionItemInfo.mRowSpan, collectionItemInfo.mColumnIndex, collectionItemInfo.mColumnSpan, collectionItemInfo.mHeading);
        }

        public static CollectionItemInfo obtain(int i, int i2, int i3, int i4, boolean z) {
            CollectionItemInfo collectionItemInfoAcquire = sPool.acquire();
            return collectionItemInfoAcquire != null ? collectionItemInfoAcquire : new CollectionItemInfo(i, i2, i3, i4, z);
        }

        private CollectionItemInfo(int i, int i2, int i3, int i4, boolean z) {
            this.mRowIndex = i;
            this.mRowSpan = i2;
            this.mColumnIndex = i3;
            this.mColumnSpan = i4;
            this.mHeading = z;
        }

        public int getColumnIndex() {
            return this.mColumnIndex;
        }

        public int getRowIndex() {
            return this.mRowIndex;
        }

        public int getColumnSpan() {
            return this.mColumnSpan;
        }

        public int getRowSpan() {
            return this.mRowSpan;
        }

        public boolean isHeading() {
            return this.mHeading;
        }

        void recycle() {
            clear();
            sPool.release(this);
        }

        private void clear() {
            this.mColumnIndex = 0;
            this.mColumnSpan = 0;
            this.mRowIndex = 0;
            this.mRowSpan = 0;
            this.mHeading = false;
        }
    }
}
