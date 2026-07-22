package android.view;

import android.graphics.Point;
import android.graphics.Rect;
import android.view.ActionMode;
import android.view.accessibility.AccessibilityEvent;

/* JADX INFO: loaded from: classes.dex */
public interface ViewParent {
    void bringChildToFront(View view);

    boolean canResolveLayoutDirection();

    boolean canResolveTextAlignment();

    boolean canResolveTextDirection();

    void childDrawableStateChanged(View view);

    void childHasTransientStateChanged(View view, boolean z);

    void clearChildFocus(View view);

    void createContextMenu(ContextMenu contextMenu);

    View focusSearch(View view, int i);

    void focusableViewAvailable(View view);

    boolean getChildVisibleRect(View view, Rect rect, Point point);

    int getLayoutDirection();

    ViewParent getParent();

    ViewParent getParentForAccessibility();

    int getTextAlignment();

    int getTextDirection();

    void invalidateChild(View view, Rect rect);

    ViewParent invalidateChildInParent(int[] iArr, Rect rect);

    boolean isLayoutDirectionResolved();

    boolean isLayoutRequested();

    boolean isTextAlignmentResolved();

    boolean isTextDirectionResolved();

    void notifySubtreeAccessibilityStateChanged(View view, View view2, int i);

    void recomputeViewAttributes(View view);

    void requestChildFocus(View view, View view2);

    boolean requestChildRectangleOnScreen(View view, Rect rect, boolean z);

    void requestDisallowInterceptTouchEvent(boolean z);

    void requestFitSystemWindows();

    void requestLayout();

    boolean requestSendAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent);

    void requestTransparentRegion(View view);

    boolean showContextMenuForChild(View view);

    ActionMode startActionModeForChild(View view, ActionMode.Callback callback);
}
