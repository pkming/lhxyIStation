package android.graphics.drawable;

import android.content.res.Resources;
import android.graphics.drawable.DrawableContainer;

/* JADX INFO: loaded from: classes.dex */
public class LevelListDrawable extends DrawableContainer {
    private final LevelListState mLevelListState;
    private boolean mMutated;

    public LevelListDrawable() {
        this(null, null);
    }

    public void addLevel(int i, int i2, Drawable drawable) {
        if (drawable != null) {
            this.mLevelListState.addLevel(i, i2, drawable);
            onLevelChange(getLevel());
        }
    }

    @Override // android.graphics.drawable.DrawableContainer, android.graphics.drawable.Drawable
    protected boolean onLevelChange(int i) {
        if (selectDrawable(this.mLevelListState.indexOfLevel(i))) {
            return true;
        }
        return super.onLevelChange(i);
    }

    /* JADX WARN: Code restructure failed: missing block: B:30:0x009e, code lost:
    
        onLevelChange(getLevel());
     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x00a5, code lost:
    
        return;
     */
    @Override // android.graphics.drawable.Drawable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void inflate(android.content.res.Resources r8, org.xmlpull.v1.XmlPullParser r9, android.util.AttributeSet r10) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            r7 = this;
            super.inflate(r8, r9, r10)
            int r0 = r9.getDepth()
            r1 = 1
            int r0 = r0 + r1
        L9:
            int r2 = r9.next()
            if (r2 == r1) goto L9e
            int r3 = r9.getDepth()
            if (r3 >= r0) goto L18
            r4 = 3
            if (r2 == r4) goto L9e
        L18:
            r4 = 2
            if (r2 == r4) goto L1c
            goto L9
        L1c:
            if (r3 > r0) goto L9
            java.lang.String r2 = r9.getName()
            java.lang.String r3 = "item"
            boolean r2 = r2.equals(r3)
            if (r2 != 0) goto L2b
            goto L9
        L2b:
            int[] r2 = com.android.internal.R.styleable.LevelListDrawableItem
            android.content.res.TypedArray r2 = r8.obtainAttributes(r10, r2)
            r3 = 0
            int r5 = r2.getInt(r1, r3)
            int r6 = r2.getInt(r4, r3)
            int r3 = r2.getResourceId(r3, r3)
            r2.recycle()
            if (r6 < 0) goto L81
            if (r3 == 0) goto L4a
            android.graphics.drawable.Drawable r2 = r8.getDrawable(r3)
            goto L58
        L4a:
            int r2 = r9.next()
            r3 = 4
            if (r2 != r3) goto L52
            goto L4a
        L52:
            if (r2 != r4) goto L5e
            android.graphics.drawable.Drawable r2 = android.graphics.drawable.Drawable.createFromXmlInner(r8, r9, r10)
        L58:
            android.graphics.drawable.LevelListDrawable$LevelListState r3 = r7.mLevelListState
            r3.addLevel(r5, r6, r2)
            goto L9
        L5e:
            org.xmlpull.v1.XmlPullParserException r8 = new org.xmlpull.v1.XmlPullParserException
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r9 = r9.getPositionDescription()
            java.lang.StringBuilder r9 = r10.append(r9)
            java.lang.String r10 = ": <item> tag requires a 'drawable' attribute or "
            java.lang.StringBuilder r9 = r9.append(r10)
            java.lang.String r10 = "child tag defining a drawable"
            java.lang.StringBuilder r9 = r9.append(r10)
            java.lang.String r9 = r9.toString()
            r8.<init>(r9)
            throw r8
        L81:
            org.xmlpull.v1.XmlPullParserException r8 = new org.xmlpull.v1.XmlPullParserException
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r9 = r9.getPositionDescription()
            java.lang.StringBuilder r9 = r10.append(r9)
            java.lang.String r10 = ": <item> tag requires a 'maxLevel' attribute"
            java.lang.StringBuilder r9 = r9.append(r10)
            java.lang.String r9 = r9.toString()
            r8.<init>(r9)
            throw r8
        L9e:
            int r8 = r7.getLevel()
            r7.onLevelChange(r8)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.graphics.drawable.LevelListDrawable.inflate(android.content.res.Resources, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet):void");
    }

    @Override // android.graphics.drawable.DrawableContainer, android.graphics.drawable.Drawable
    public Drawable mutate() {
        if (!this.mMutated && super.mutate() == this) {
            LevelListState levelListState = this.mLevelListState;
            levelListState.mLows = (int[]) levelListState.mLows.clone();
            LevelListState levelListState2 = this.mLevelListState;
            levelListState2.mHighs = (int[]) levelListState2.mHighs.clone();
            this.mMutated = true;
        }
        return this;
    }

    private static final class LevelListState extends DrawableContainer.DrawableContainerState {
        private int[] mHighs;
        private int[] mLows;

        LevelListState(LevelListState levelListState, LevelListDrawable levelListDrawable, Resources resources) {
            super(levelListState, levelListDrawable, resources);
            if (levelListState != null) {
                this.mLows = levelListState.mLows;
                this.mHighs = levelListState.mHighs;
            } else {
                this.mLows = new int[getCapacity()];
                this.mHighs = new int[getCapacity()];
            }
        }

        public void addLevel(int i, int i2, Drawable drawable) {
            int iAddChild = addChild(drawable);
            this.mLows[iAddChild] = i;
            this.mHighs[iAddChild] = i2;
        }

        public int indexOfLevel(int i) {
            int[] iArr = this.mLows;
            int[] iArr2 = this.mHighs;
            int childCount = getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                if (i >= iArr[i2] && i <= iArr2[i2]) {
                    return i2;
                }
            }
            return -1;
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable() {
            return new LevelListDrawable(this, null);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable(Resources resources) {
            return new LevelListDrawable(this, resources);
        }

        @Override // android.graphics.drawable.DrawableContainer.DrawableContainerState
        public void growArray(int i, int i2) {
            super.growArray(i, i2);
            int[] iArr = new int[i2];
            System.arraycopy(this.mLows, 0, iArr, 0, i);
            this.mLows = iArr;
            int[] iArr2 = new int[i2];
            System.arraycopy(this.mHighs, 0, iArr2, 0, i);
            this.mHighs = iArr2;
        }
    }

    private LevelListDrawable(LevelListState levelListState, Resources resources) {
        LevelListState levelListState2 = new LevelListState(levelListState, this, resources);
        this.mLevelListState = levelListState2;
        setConstantState(levelListState2);
        onLevelChange(getLevel());
    }
}
