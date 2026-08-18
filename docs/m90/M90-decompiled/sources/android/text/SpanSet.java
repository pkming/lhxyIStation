package android.text;

import java.lang.reflect.Array;

/* JADX INFO: loaded from: classes.dex */
public class SpanSet<E> {
    private final Class<? extends E> classType;
    int numberOfSpans = 0;
    int[] spanEnds;
    int[] spanFlags;
    int[] spanStarts;
    E[] spans;

    SpanSet(Class<? extends E> cls) {
        this.classType = cls;
    }

    public void init(Spanned spanned, int i, int i2) {
        E[] eArr;
        Object[] spans = spanned.getSpans(i, i2, this.classType);
        int length = spans.length;
        if (length > 0 && ((eArr = this.spans) == null || eArr.length < length)) {
            this.spans = (E[]) ((Object[]) Array.newInstance(this.classType, length));
            this.spanStarts = new int[length];
            this.spanEnds = new int[length];
            this.spanFlags = new int[length];
        }
        this.numberOfSpans = 0;
        for (Object obj : spans) {
            int spanStart = spanned.getSpanStart(obj);
            int spanEnd = spanned.getSpanEnd(obj);
            if (spanStart != spanEnd) {
                int spanFlags = spanned.getSpanFlags(obj);
                Object[] objArr = (E[]) this.spans;
                int i3 = this.numberOfSpans;
                objArr[i3] = obj;
                this.spanStarts[i3] = spanStart;
                this.spanEnds[i3] = spanEnd;
                this.spanFlags[i3] = spanFlags;
                this.numberOfSpans = i3 + 1;
            }
        }
    }

    public boolean hasSpansIntersecting(int i, int i2) {
        for (int i3 = 0; i3 < this.numberOfSpans; i3++) {
            if (this.spanStarts[i3] < i2 && this.spanEnds[i3] > i) {
                return true;
            }
        }
        return false;
    }

    int getNextTransition(int i, int i2) {
        for (int i3 = 0; i3 < this.numberOfSpans; i3++) {
            int i4 = this.spanStarts[i3];
            int i5 = this.spanEnds[i3];
            if (i4 > i && i4 < i2) {
                i2 = i4;
            }
            if (i5 > i && i5 < i2) {
                i2 = i5;
            }
        }
        return i2;
    }

    public void recycle() {
        for (int i = 0; i < this.numberOfSpans; i++) {
            this.spans[i] = null;
        }
    }
}
