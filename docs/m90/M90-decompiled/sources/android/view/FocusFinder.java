package android.view;

import android.graphics.Rect;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/* JADX INFO: loaded from: classes.dex */
public class FocusFinder {
    private static final ThreadLocal<FocusFinder> tlFocusFinder = new ThreadLocal<FocusFinder>() { // from class: android.view.FocusFinder.1
        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.lang.ThreadLocal
        public FocusFinder initialValue() {
            return new FocusFinder();
        }
    };
    final Rect mBestCandidateRect;
    final Rect mFocusedRect;
    final Rect mOtherRect;
    final SequentialFocusComparator mSequentialFocusComparator;
    private final ArrayList<View> mTempList;

    int getWeightedDistanceFor(int i, int i2) {
        return (i * 13 * i) + (i2 * i2);
    }

    public static FocusFinder getInstance() {
        return tlFocusFinder.get();
    }

    private FocusFinder() {
        this.mFocusedRect = new Rect();
        this.mOtherRect = new Rect();
        this.mBestCandidateRect = new Rect();
        this.mSequentialFocusComparator = new SequentialFocusComparator();
        this.mTempList = new ArrayList<>();
    }

    public final View findNextFocus(ViewGroup viewGroup, View view, int i) {
        return findNextFocus(viewGroup, view, null, i);
    }

    public View findNextFocusFromRect(ViewGroup viewGroup, Rect rect, int i) {
        this.mFocusedRect.set(rect);
        return findNextFocus(viewGroup, null, this.mFocusedRect, i);
    }

    private View findNextFocus(ViewGroup viewGroup, View view, Rect rect, int i) {
        View viewFindNextUserSpecifiedFocus = view != null ? findNextUserSpecifiedFocus(viewGroup, view, i) : null;
        if (viewFindNextUserSpecifiedFocus != null) {
            return viewFindNextUserSpecifiedFocus;
        }
        ArrayList<View> arrayList = this.mTempList;
        try {
            arrayList.clear();
            viewGroup.addFocusables(arrayList, i);
            if (!arrayList.isEmpty()) {
                viewFindNextUserSpecifiedFocus = findNextFocus(viewGroup, view, rect, i, arrayList);
            }
            return viewFindNextUserSpecifiedFocus;
        } finally {
            arrayList.clear();
        }
    }

    private View findNextUserSpecifiedFocus(ViewGroup viewGroup, View view, int i) {
        View viewFindUserSetNextFocus = view.findUserSetNextFocus(viewGroup, i);
        if (viewFindUserSetNextFocus == null || !viewFindUserSetNextFocus.isFocusable()) {
            return null;
        }
        if (!viewFindUserSetNextFocus.isInTouchMode() || viewFindUserSetNextFocus.isFocusableInTouchMode()) {
            return viewFindUserSetNextFocus;
        }
        return null;
    }

    private View findNextFocus(ViewGroup viewGroup, View view, Rect rect, int i, ArrayList<View> arrayList) {
        Rect rect2;
        if (view != null) {
            rect2 = rect == null ? this.mFocusedRect : rect;
            view.getFocusedRect(rect2);
            viewGroup.offsetDescendantRectToMyCoords(view, rect2);
        } else if (rect == null) {
            rect2 = this.mFocusedRect;
            if (i != 1) {
                if (i != 2) {
                    if (i == 17 || i == 33) {
                        setFocusBottomRight(viewGroup, rect2);
                    } else if (i == 66 || i == 130) {
                        setFocusTopLeft(viewGroup, rect2);
                    }
                } else if (viewGroup.isLayoutRtl()) {
                    setFocusBottomRight(viewGroup, rect2);
                } else {
                    setFocusTopLeft(viewGroup, rect2);
                }
            } else if (viewGroup.isLayoutRtl()) {
                setFocusTopLeft(viewGroup, rect2);
            } else {
                setFocusBottomRight(viewGroup, rect2);
            }
        } else {
            rect2 = rect;
        }
        if (i == 1 || i == 2) {
            return findNextFocusInRelativeDirection(arrayList, viewGroup, view, rect2, i);
        }
        if (i == 17 || i == 33 || i == 66 || i == 130) {
            return findNextFocusInAbsoluteDirection(arrayList, viewGroup, view, rect2, i);
        }
        throw new IllegalArgumentException("Unknown direction: " + i);
    }

    private View findNextFocusInRelativeDirection(ArrayList<View> arrayList, ViewGroup viewGroup, View view, Rect rect, int i) {
        try {
            this.mSequentialFocusComparator.setRoot(viewGroup);
            this.mSequentialFocusComparator.setIsLayoutRtl(viewGroup.isLayoutRtl());
            Collections.sort(arrayList, this.mSequentialFocusComparator);
            this.mSequentialFocusComparator.recycle();
            int size = arrayList.size();
            if (i == 1) {
                return getPreviousFocusable(view, arrayList, size);
            }
            if (i == 2) {
                return getNextFocusable(view, arrayList, size);
            }
            return arrayList.get(size - 1);
        } catch (Throwable th) {
            this.mSequentialFocusComparator.recycle();
            throw th;
        }
    }

    private void setFocusBottomRight(ViewGroup viewGroup, Rect rect) {
        int scrollY = viewGroup.getScrollY() + viewGroup.getHeight();
        int scrollX = viewGroup.getScrollX() + viewGroup.getWidth();
        rect.set(scrollX, scrollY, scrollX, scrollY);
    }

    private void setFocusTopLeft(ViewGroup viewGroup, Rect rect) {
        int scrollY = viewGroup.getScrollY();
        int scrollX = viewGroup.getScrollX();
        rect.set(scrollX, scrollY, scrollX, scrollY);
    }

    View findNextFocusInAbsoluteDirection(ArrayList<View> arrayList, ViewGroup viewGroup, View view, Rect rect, int i) {
        this.mBestCandidateRect.set(rect);
        if (i == 17) {
            this.mBestCandidateRect.offset(rect.width() + 1, 0);
        } else if (i == 33) {
            this.mBestCandidateRect.offset(0, rect.height() + 1);
        } else if (i == 66) {
            this.mBestCandidateRect.offset(-(rect.width() + 1), 0);
        } else if (i == 130) {
            this.mBestCandidateRect.offset(0, -(rect.height() + 1));
        }
        View view2 = null;
        int size = arrayList.size();
        for (int i2 = 0; i2 < size; i2++) {
            View view3 = arrayList.get(i2);
            if (view3 != view && view3 != viewGroup) {
                view3.getFocusedRect(this.mOtherRect);
                viewGroup.offsetDescendantRectToMyCoords(view3, this.mOtherRect);
                if (isBetterCandidate(i, rect, this.mOtherRect, this.mBestCandidateRect)) {
                    this.mBestCandidateRect.set(this.mOtherRect);
                    view2 = view3;
                }
            }
        }
        return view2;
    }

    private static View getNextFocusable(View view, ArrayList<View> arrayList, int i) {
        int iLastIndexOf;
        int i2;
        if (view != null && (iLastIndexOf = arrayList.lastIndexOf(view)) >= 0 && (i2 = iLastIndexOf + 1) < i) {
            return arrayList.get(i2);
        }
        if (arrayList.isEmpty()) {
            return null;
        }
        return arrayList.get(0);
    }

    private static View getPreviousFocusable(View view, ArrayList<View> arrayList, int i) {
        int iIndexOf;
        if (view != null && (iIndexOf = arrayList.indexOf(view)) > 0) {
            return arrayList.get(iIndexOf - 1);
        }
        if (arrayList.isEmpty()) {
            return null;
        }
        return arrayList.get(i - 1);
    }

    boolean isBetterCandidate(int i, Rect rect, Rect rect2, Rect rect3) {
        if (!isCandidate(rect, rect2, i)) {
            return false;
        }
        if (isCandidate(rect, rect3, i) && !beamBeats(i, rect, rect2, rect3)) {
            return !beamBeats(i, rect, rect3, rect2) && getWeightedDistanceFor(majorAxisDistance(i, rect, rect2), minorAxisDistance(i, rect, rect2)) < getWeightedDistanceFor(majorAxisDistance(i, rect, rect3), minorAxisDistance(i, rect, rect3));
        }
        return true;
    }

    boolean beamBeats(int i, Rect rect, Rect rect2, Rect rect3) {
        boolean zBeamsOverlap = beamsOverlap(i, rect, rect2);
        if (beamsOverlap(i, rect, rect3) || !zBeamsOverlap) {
            return false;
        }
        return !isToDirectionOf(i, rect, rect3) || i == 17 || i == 66 || majorAxisDistance(i, rect, rect2) < majorAxisDistanceToFarEdge(i, rect, rect3);
    }

    boolean isCandidate(Rect rect, Rect rect2, int i) {
        if (i == 17) {
            return (rect.right > rect2.right || rect.left >= rect2.right) && rect.left > rect2.left;
        }
        if (i == 33) {
            return (rect.bottom > rect2.bottom || rect.top >= rect2.bottom) && rect.top > rect2.top;
        }
        if (i == 66) {
            return (rect.left < rect2.left || rect.right <= rect2.left) && rect.right < rect2.right;
        }
        if (i == 130) {
            return (rect.top < rect2.top || rect.bottom <= rect2.top) && rect.bottom < rect2.bottom;
        }
        throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
    }

    boolean beamsOverlap(int i, Rect rect, Rect rect2) {
        if (i != 17) {
            if (i != 33) {
                if (i != 66) {
                    if (i != 130) {
                        throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
                    }
                }
            }
            return rect2.right >= rect.left && rect2.left <= rect.right;
        }
        return rect2.bottom >= rect.top && rect2.top <= rect.bottom;
    }

    boolean isToDirectionOf(int i, Rect rect, Rect rect2) {
        if (i == 17) {
            return rect.left >= rect2.right;
        }
        if (i == 33) {
            return rect.top >= rect2.bottom;
        }
        if (i == 66) {
            return rect.right <= rect2.left;
        }
        if (i == 130) {
            return rect.bottom <= rect2.top;
        }
        throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
    }

    static int majorAxisDistance(int i, Rect rect, Rect rect2) {
        return Math.max(0, majorAxisDistanceRaw(i, rect, rect2));
    }

    static int majorAxisDistanceRaw(int i, Rect rect, Rect rect2) {
        int i2;
        int i3;
        if (i == 17) {
            i2 = rect.left;
            i3 = rect2.right;
        } else if (i == 33) {
            i2 = rect.top;
            i3 = rect2.bottom;
        } else if (i == 66) {
            i2 = rect2.left;
            i3 = rect.right;
        } else if (i == 130) {
            i2 = rect2.top;
            i3 = rect.bottom;
        } else {
            throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
        }
        return i2 - i3;
    }

    static int majorAxisDistanceToFarEdge(int i, Rect rect, Rect rect2) {
        return Math.max(1, majorAxisDistanceToFarEdgeRaw(i, rect, rect2));
    }

    static int majorAxisDistanceToFarEdgeRaw(int i, Rect rect, Rect rect2) {
        int i2;
        int i3;
        if (i == 17) {
            i2 = rect.left;
            i3 = rect2.left;
        } else if (i == 33) {
            i2 = rect.top;
            i3 = rect2.top;
        } else if (i == 66) {
            i2 = rect2.right;
            i3 = rect.right;
        } else if (i == 130) {
            i2 = rect2.bottom;
            i3 = rect.bottom;
        } else {
            throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
        }
        return i2 - i3;
    }

    static int minorAxisDistance(int i, Rect rect, Rect rect2) {
        if (i != 17) {
            if (i != 33) {
                if (i != 66) {
                    if (i != 130) {
                        throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
                    }
                }
            }
            return Math.abs((rect.left + (rect.width() / 2)) - (rect2.left + (rect2.width() / 2)));
        }
        return Math.abs((rect.top + (rect.height() / 2)) - (rect2.top + (rect2.height() / 2)));
    }

    /* JADX WARN: Removed duplicated region for block: B:6:0x003d  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public android.view.View findNearestTouchable(android.view.ViewGroup r20, int r21, int r22, int r23, int[] r24) {
        /*
            r19 = this;
            r0 = r19
            r1 = r20
            r2 = r21
            r3 = r22
            r4 = r23
            java.util.ArrayList r5 = r20.getTouchables()
            int r6 = r5.size()
            android.content.Context r7 = r1.mContext
            android.view.ViewConfiguration r7 = android.view.ViewConfiguration.get(r7)
            int r7 = r7.getScaledEdgeSlop()
            android.graphics.Rect r8 = new android.graphics.Rect
            r8.<init>()
            android.graphics.Rect r9 = r0.mOtherRect
            r12 = 0
            r13 = 0
            r14 = 2147483647(0x7fffffff, float:NaN)
        L28:
            if (r13 >= r6) goto Lab
            java.lang.Object r15 = r5.get(r13)
            android.view.View r15 = (android.view.View) r15
            r15.getDrawingRect(r9)
            r10 = 1
            r1.offsetRectBetweenParentAndChild(r15, r9, r10, r10)
            boolean r16 = r0.isTouchCandidate(r2, r3, r9, r4)
            if (r16 != 0) goto L41
        L3d:
            r16 = 0
            goto La7
        L41:
            r11 = 33
            r10 = 17
            if (r4 == r10) goto L62
            if (r4 == r11) goto L5b
            r11 = 66
            if (r4 == r11) goto L58
            r11 = 130(0x82, float:1.82E-43)
            if (r4 == r11) goto L55
            r11 = 2147483647(0x7fffffff, float:NaN)
            goto L6a
        L55:
            int r11 = r9.top
            goto L6a
        L58:
            int r11 = r9.left
            goto L6a
        L5b:
            int r11 = r9.bottom
            int r11 = r3 - r11
            r17 = 1
            goto L68
        L62:
            r17 = 1
            int r11 = r9.right
            int r11 = r2 - r11
        L68:
            int r11 = r11 + 1
        L6a:
            if (r11 >= r7) goto L3d
            if (r12 == 0) goto L7c
            boolean r18 = r8.contains(r9)
            if (r18 != 0) goto L7c
            boolean r18 = r9.contains(r8)
            if (r18 != 0) goto L3d
            if (r11 >= r14) goto L3d
        L7c:
            r8.set(r9)
            if (r4 == r10) goto La0
            r10 = 33
            if (r4 == r10) goto L99
            r10 = 66
            if (r4 == r10) goto L94
            r10 = 130(0x82, float:1.82E-43)
            if (r4 == r10) goto L90
        L8d:
            r16 = 0
            goto La5
        L90:
            r10 = 1
            r24[r10] = r11
            goto L8d
        L94:
            r16 = 0
            r24[r16] = r11
            goto La5
        L99:
            r10 = 1
            r16 = 0
            int r12 = -r11
            r24[r10] = r12
            goto La5
        La0:
            r16 = 0
            int r10 = -r11
            r24[r16] = r10
        La5:
            r14 = r11
            r12 = r15
        La7:
            int r13 = r13 + 1
            goto L28
        Lab:
            return r12
        */
        throw new UnsupportedOperationException("Method not decompiled: android.view.FocusFinder.findNearestTouchable(android.view.ViewGroup, int, int, int, int[]):android.view.View");
    }

    private boolean isTouchCandidate(int i, int i2, Rect rect, int i3) {
        if (i3 == 17) {
            return rect.left <= i && rect.top <= i2 && i2 <= rect.bottom;
        }
        if (i3 == 33) {
            return rect.top <= i2 && rect.left <= i && i <= rect.right;
        }
        if (i3 == 66) {
            return rect.left >= i && rect.top <= i2 && i2 <= rect.bottom;
        }
        if (i3 == 130) {
            return rect.top >= i2 && rect.left <= i && i <= rect.right;
        }
        throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
    }

    private static final class SequentialFocusComparator implements Comparator<View> {
        private final Rect mFirstRect;
        private boolean mIsLayoutRtl;
        private ViewGroup mRoot;
        private final Rect mSecondRect;

        private SequentialFocusComparator() {
            this.mFirstRect = new Rect();
            this.mSecondRect = new Rect();
        }

        public void recycle() {
            this.mRoot = null;
        }

        public void setRoot(ViewGroup viewGroup) {
            this.mRoot = viewGroup;
        }

        public void setIsLayoutRtl(boolean z) {
            this.mIsLayoutRtl = z;
        }

        @Override // java.util.Comparator
        public int compare(View view, View view2) {
            if (view == view2) {
                return 0;
            }
            getRect(view, this.mFirstRect);
            getRect(view2, this.mSecondRect);
            if (this.mFirstRect.top < this.mSecondRect.top) {
                return -1;
            }
            if (this.mFirstRect.top > this.mSecondRect.top) {
                return 1;
            }
            if (this.mFirstRect.left < this.mSecondRect.left) {
                return this.mIsLayoutRtl ? 1 : -1;
            }
            if (this.mFirstRect.left > this.mSecondRect.left) {
                return this.mIsLayoutRtl ? -1 : 1;
            }
            if (this.mFirstRect.bottom < this.mSecondRect.bottom) {
                return -1;
            }
            if (this.mFirstRect.bottom > this.mSecondRect.bottom) {
                return 1;
            }
            if (this.mFirstRect.right < this.mSecondRect.right) {
                return this.mIsLayoutRtl ? 1 : -1;
            }
            if (this.mFirstRect.right > this.mSecondRect.right) {
                return this.mIsLayoutRtl ? -1 : 1;
            }
            return 0;
        }

        private void getRect(View view, Rect rect) {
            view.getDrawingRect(rect);
            this.mRoot.offsetDescendantRectToMyCoords(view, rect);
        }
    }
}
