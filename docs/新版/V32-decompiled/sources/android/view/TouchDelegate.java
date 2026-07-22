package android.view;

import android.graphics.Rect;

/* JADX INFO: loaded from: classes.dex */
public class TouchDelegate {
    public static final int ABOVE = 1;
    public static final int BELOW = 2;
    public static final int TO_LEFT = 4;
    public static final int TO_RIGHT = 8;
    private Rect mBounds;
    private boolean mDelegateTargeted;
    private View mDelegateView;
    private int mSlop;
    private Rect mSlopBounds;

    public TouchDelegate(Rect rect, View view) {
        this.mBounds = rect;
        this.mSlop = ViewConfiguration.get(view.getContext()).getScaledTouchSlop();
        Rect rect2 = new Rect(rect);
        this.mSlopBounds = rect2;
        int i = this.mSlop;
        rect2.inset(-i, -i);
        this.mDelegateView = view;
    }

    /* JADX WARN: Removed duplicated region for block: B:17:0x003c  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean onTouchEvent(android.view.MotionEvent r8) {
        /*
            r7 = this;
            float r0 = r8.getX()
            int r0 = (int) r0
            float r1 = r8.getY()
            int r1 = (int) r1
            int r2 = r8.getAction()
            r3 = 0
            r4 = 2
            r5 = 1
            if (r2 == 0) goto L30
            if (r2 == r5) goto L23
            if (r2 == r4) goto L23
            r0 = 3
            if (r2 == r0) goto L1b
            goto L3c
        L1b:
            boolean r0 = r7.mDelegateTargeted
            r7.mDelegateTargeted = r3
            r6 = r5
            r5 = r0
            r0 = r6
            goto L3e
        L23:
            boolean r2 = r7.mDelegateTargeted
            if (r2 == 0) goto L2d
            android.graphics.Rect r5 = r7.mSlopBounds
            boolean r5 = r5.contains(r0, r1)
        L2d:
            r0 = r5
            r5 = r2
            goto L3e
        L30:
            android.graphics.Rect r2 = r7.mBounds
            boolean r0 = r2.contains(r0, r1)
            if (r0 == 0) goto L3c
            r7.mDelegateTargeted = r5
            r0 = r5
            goto L3e
        L3c:
            r0 = r5
            r5 = r3
        L3e:
            if (r5 == 0) goto L60
            android.view.View r1 = r7.mDelegateView
            if (r0 == 0) goto L54
            int r0 = r1.getWidth()
            int r0 = r0 / r4
            float r0 = (float) r0
            int r2 = r1.getHeight()
            int r2 = r2 / r4
            float r2 = (float) r2
            r8.setLocation(r0, r2)
            goto L5c
        L54:
            int r0 = r7.mSlop
            int r0 = r0 * r4
            int r0 = -r0
            float r0 = (float) r0
            r8.setLocation(r0, r0)
        L5c:
            boolean r3 = r1.dispatchTouchEvent(r8)
        L60:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: android.view.TouchDelegate.onTouchEvent(android.view.MotionEvent):boolean");
    }
}
