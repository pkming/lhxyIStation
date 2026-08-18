package android.text;

import com.android.internal.util.ArrayUtils;
import java.lang.reflect.Array;

/* JADX INFO: Access modifiers changed from: package-private */
/* JADX INFO: loaded from: classes.dex */
public abstract class SpannableStringInternal {
    private static final int COLUMNS = 3;
    static final Object[] EMPTY = new Object[0];
    private static final int END = 1;
    private static final int FLAGS = 2;
    private static final int START = 0;
    private int mSpanCount;
    private int[] mSpanData;
    private Object[] mSpans;
    private String mText;

    SpannableStringInternal(CharSequence charSequence, int i, int i2) {
        if (i == 0 && i2 == charSequence.length()) {
            this.mText = charSequence.toString();
        } else {
            this.mText = charSequence.toString().substring(i, i2);
        }
        int iIdealIntArraySize = ArrayUtils.idealIntArraySize(0);
        this.mSpans = new Object[iIdealIntArraySize];
        this.mSpanData = new int[iIdealIntArraySize * 3];
        if (charSequence instanceof Spanned) {
            Spanned spanned = (Spanned) charSequence;
            Object[] spans = spanned.getSpans(i, i2, Object.class);
            for (int i3 = 0; i3 < spans.length; i3++) {
                int spanStart = spanned.getSpanStart(spans[i3]);
                int spanEnd = spanned.getSpanEnd(spans[i3]);
                int spanFlags = spanned.getSpanFlags(spans[i3]);
                spanStart = spanStart < i ? i : spanStart;
                if (spanEnd > i2) {
                    spanEnd = i2;
                }
                setSpan(spans[i3], spanStart - i, spanEnd - i, spanFlags);
            }
        }
    }

    public final int length() {
        return this.mText.length();
    }

    public final char charAt(int i) {
        return this.mText.charAt(i);
    }

    public final String toString() {
        return this.mText;
    }

    public final void getChars(int i, int i2, char[] cArr, int i3) {
        this.mText.getChars(i, i2, cArr, i3);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setSpan(Object obj, int i, int i2, int i3) {
        char cCharAt;
        char cCharAt2;
        checkRange("setSpan", i, i2);
        if ((i3 & 51) == 51) {
            if (i != 0 && i != length() && (cCharAt2 = charAt(i - 1)) != '\n') {
                throw new RuntimeException("PARAGRAPH span must start at paragraph boundary (" + i + " follows " + cCharAt2 + ")");
            }
            if (i2 != 0 && i2 != length() && (cCharAt = charAt(i2 - 1)) != '\n') {
                throw new RuntimeException("PARAGRAPH span must end at paragraph boundary (" + i2 + " follows " + cCharAt + ")");
            }
        }
        int i4 = this.mSpanCount;
        Object[] objArr = this.mSpans;
        int[] iArr = this.mSpanData;
        for (int i5 = 0; i5 < i4; i5++) {
            if (objArr[i5] == obj) {
                int i6 = i5 * 3;
                int i7 = i6 + 0;
                int i8 = iArr[i7];
                int i9 = i6 + 1;
                int i10 = iArr[i9];
                iArr[i7] = i;
                iArr[i9] = i2;
                iArr[i6 + 2] = i3;
                sendSpanChanged(obj, i8, i10, i, i2);
                return;
            }
        }
        int i11 = this.mSpanCount;
        if (i11 + 1 >= this.mSpans.length) {
            int iIdealIntArraySize = ArrayUtils.idealIntArraySize(i11 + 1);
            Object[] objArr2 = new Object[iIdealIntArraySize];
            int[] iArr2 = new int[iIdealIntArraySize * 3];
            System.arraycopy(this.mSpans, 0, objArr2, 0, this.mSpanCount);
            System.arraycopy(this.mSpanData, 0, iArr2, 0, this.mSpanCount * 3);
            this.mSpans = objArr2;
            this.mSpanData = iArr2;
        }
        Object[] objArr3 = this.mSpans;
        int i12 = this.mSpanCount;
        objArr3[i12] = obj;
        int[] iArr3 = this.mSpanData;
        iArr3[(i12 * 3) + 0] = i;
        iArr3[(i12 * 3) + 1] = i2;
        iArr3[(i12 * 3) + 2] = i3;
        this.mSpanCount = i12 + 1;
        if (this instanceof Spannable) {
            sendSpanAdded(obj, i, i2);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeSpan(Object obj) {
        int i = this.mSpanCount;
        Object[] objArr = this.mSpans;
        int[] iArr = this.mSpanData;
        for (int i2 = i - 1; i2 >= 0; i2--) {
            if (objArr[i2] == obj) {
                int i3 = i2 * 3;
                int i4 = iArr[i3 + 0];
                int i5 = iArr[i3 + 1];
                int i6 = i2 + 1;
                int i7 = i - i6;
                System.arraycopy(objArr, i6, objArr, i2, i7);
                System.arraycopy(iArr, i6 * 3, iArr, i3, i7 * 3);
                this.mSpanCount--;
                sendSpanRemoved(obj, i4, i5);
                return;
            }
        }
    }

    public int getSpanStart(Object obj) {
        int i = this.mSpanCount;
        Object[] objArr = this.mSpans;
        int[] iArr = this.mSpanData;
        for (int i2 = i - 1; i2 >= 0; i2--) {
            if (objArr[i2] == obj) {
                return iArr[(i2 * 3) + 0];
            }
        }
        return -1;
    }

    public int getSpanEnd(Object obj) {
        int i = this.mSpanCount;
        Object[] objArr = this.mSpans;
        int[] iArr = this.mSpanData;
        for (int i2 = i - 1; i2 >= 0; i2--) {
            if (objArr[i2] == obj) {
                return iArr[(i2 * 3) + 1];
            }
        }
        return -1;
    }

    public int getSpanFlags(Object obj) {
        int i = this.mSpanCount;
        Object[] objArr = this.mSpans;
        int[] iArr = this.mSpanData;
        for (int i2 = i - 1; i2 >= 0; i2--) {
            if (objArr[i2] == obj) {
                return iArr[(i2 * 3) + 2];
            }
        }
        return 0;
    }

    public <T> T[] getSpans(int i, int i2, Class<T> cls) {
        int i3 = this.mSpanCount;
        Object[] objArr = this.mSpans;
        int[] iArr = this.mSpanData;
        Object[] objArr2 = null;
        Object obj = null;
        int i4 = 0;
        for (int i5 = 0; i5 < i3; i5++) {
            if (cls == null || cls.isInstance(objArr[i5])) {
                int i6 = i5 * 3;
                int i7 = iArr[i6 + 0];
                int i8 = iArr[i6 + 1];
                if (i7 <= i2 && i8 >= i && (i7 == i8 || i == i2 || (i7 != i2 && i8 != i))) {
                    if (i4 == 0) {
                        obj = objArr[i5];
                    } else {
                        if (i4 == 1) {
                            objArr2 = (Object[]) Array.newInstance((Class<?>) cls, (i3 - i5) + 1);
                            objArr2[0] = obj;
                        }
                        int i9 = iArr[i6 + 2] & Spanned.SPAN_PRIORITY;
                        if (i9 != 0) {
                            int i10 = 0;
                            while (i10 < i4 && i9 <= (getSpanFlags(objArr2[i10]) & Spanned.SPAN_PRIORITY)) {
                                i10++;
                            }
                            System.arraycopy(objArr2, i10, objArr2, i10 + 1, i4 - i10);
                            objArr2[i10] = objArr[i5];
                        } else {
                            objArr2[i4] = objArr[i5];
                            i4++;
                        }
                    }
                    i4++;
                }
            }
        }
        if (i4 == 0) {
            return (T[]) ArrayUtils.emptyArray(cls);
        }
        if (i4 == 1) {
            Object[] objArr3 = (Object[]) Array.newInstance((Class<?>) cls, 1);
            objArr3[0] = obj;
            return (T[]) objArr3;
        }
        if (i4 == objArr2.length) {
            return (T[]) objArr2;
        }
        Object[] objArr4 = (Object[]) Array.newInstance((Class<?>) cls, i4);
        System.arraycopy(objArr2, 0, objArr4, 0, i4);
        return (T[]) objArr4;
    }

    public int nextSpanTransition(int i, int i2, Class cls) {
        int i3 = this.mSpanCount;
        Object[] objArr = this.mSpans;
        int[] iArr = this.mSpanData;
        if (cls == null) {
            cls = Object.class;
        }
        for (int i4 = 0; i4 < i3; i4++) {
            int i5 = i4 * 3;
            int i6 = iArr[i5 + 0];
            int i7 = iArr[i5 + 1];
            if (i6 > i && i6 < i2 && cls.isInstance(objArr[i4])) {
                i2 = i6;
            }
            if (i7 > i && i7 < i2 && cls.isInstance(objArr[i4])) {
                i2 = i7;
            }
        }
        return i2;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void sendSpanAdded(Object obj, int i, int i2) {
        for (SpanWatcher spanWatcher : (SpanWatcher[]) getSpans(i, i2, SpanWatcher.class)) {
            spanWatcher.onSpanAdded((Spannable) this, obj, i, i2);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void sendSpanRemoved(Object obj, int i, int i2) {
        for (SpanWatcher spanWatcher : (SpanWatcher[]) getSpans(i, i2, SpanWatcher.class)) {
            spanWatcher.onSpanRemoved((Spannable) this, obj, i, i2);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void sendSpanChanged(Object obj, int i, int i2, int i3, int i4) {
        for (SpanWatcher spanWatcher : (SpanWatcher[]) getSpans(Math.min(i, i3), Math.max(i2, i4), SpanWatcher.class)) {
            spanWatcher.onSpanChanged((Spannable) this, obj, i, i2, i3, i4);
        }
    }

    private static String region(int i, int i2) {
        return "(" + i + " ... " + i2 + ")";
    }

    private void checkRange(String str, int i, int i2) {
        if (i2 < i) {
            throw new IndexOutOfBoundsException(str + " " + region(i, i2) + " has end before start");
        }
        int length = length();
        if (i > length || i2 > length) {
            throw new IndexOutOfBoundsException(str + " " + region(i, i2) + " ends beyond length " + length);
        }
        if (i < 0 || i2 < 0) {
            throw new IndexOutOfBoundsException(str + " " + region(i, i2) + " starts before 0");
        }
    }

    public boolean equals(Object obj) {
        if ((obj instanceof Spanned) && toString().equals(obj.toString())) {
            Spanned spanned = (Spanned) obj;
            Object[] spans = spanned.getSpans(0, spanned.length(), Object.class);
            if (this.mSpanCount == spans.length) {
                for (int i = 0; i < this.mSpanCount; i++) {
                    Object obj2 = this.mSpans[i];
                    Object obj3 = spans[i];
                    if (obj2 == this) {
                        if (spanned != obj3 || getSpanStart(obj2) != spanned.getSpanStart(obj3) || getSpanEnd(obj2) != spanned.getSpanEnd(obj3) || getSpanFlags(obj2) != spanned.getSpanFlags(obj3)) {
                            return false;
                        }
                    } else if (!obj2.equals(obj3) || getSpanStart(obj2) != spanned.getSpanStart(obj3) || getSpanEnd(obj2) != spanned.getSpanEnd(obj3) || getSpanFlags(obj2) != spanned.getSpanFlags(obj3)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        int iHashCode = (toString().hashCode() * 31) + this.mSpanCount;
        for (int i = 0; i < this.mSpanCount; i++) {
            Object obj = this.mSpans[i];
            if (obj != this) {
                iHashCode = (iHashCode * 31) + obj.hashCode();
            }
            iHashCode = (((((iHashCode * 31) + getSpanStart(obj)) * 31) + getSpanEnd(obj)) * 31) + getSpanFlags(obj);
        }
        return iHashCode;
    }
}
