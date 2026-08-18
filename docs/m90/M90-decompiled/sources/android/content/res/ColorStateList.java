package android.content.res;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.StateSet;
import android.util.Xml;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes.dex */
public class ColorStateList implements Parcelable {
    private int[] mColors;
    private int mDefaultColor;
    private int[][] mStateSpecs;
    private static final int[][] EMPTY = {new int[0]};
    private static final SparseArray<WeakReference<ColorStateList>> sCache = new SparseArray<>();
    public static final Parcelable.Creator<ColorStateList> CREATOR = new Parcelable.Creator<ColorStateList>() { // from class: android.content.res.ColorStateList.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ColorStateList[] newArray(int i) {
            return new ColorStateList[i];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ColorStateList createFromParcel(Parcel parcel) {
            int i = parcel.readInt();
            int[][] iArr = new int[i][];
            for (int i2 = 0; i2 < i; i2++) {
                iArr[i2] = parcel.createIntArray();
            }
            return new ColorStateList(iArr, parcel.createIntArray());
        }
    };

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    private ColorStateList() {
        this.mDefaultColor = -65536;
    }

    public ColorStateList(int[][] iArr, int[] iArr2) {
        this.mDefaultColor = -65536;
        this.mStateSpecs = iArr;
        this.mColors = iArr2;
        if (iArr.length > 0) {
            this.mDefaultColor = iArr2[0];
            for (int i = 0; i < iArr.length; i++) {
                if (iArr[i].length == 0) {
                    this.mDefaultColor = iArr2[i];
                }
            }
        }
    }

    public static ColorStateList valueOf(int i) {
        SparseArray<WeakReference<ColorStateList>> sparseArray = sCache;
        synchronized (sparseArray) {
            WeakReference<ColorStateList> weakReference = sparseArray.get(i);
            ColorStateList colorStateList = weakReference != null ? weakReference.get() : null;
            if (colorStateList != null) {
                return colorStateList;
            }
            ColorStateList colorStateList2 = new ColorStateList(EMPTY, new int[]{i});
            sparseArray.put(i, new WeakReference<>(colorStateList2));
            return colorStateList2;
        }
    }

    public static ColorStateList createFromXml(Resources resources, XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        int next;
        AttributeSet attributeSetAsAttributeSet = Xml.asAttributeSet(xmlPullParser);
        do {
            next = xmlPullParser.next();
            if (next == 2) {
                break;
            }
        } while (next != 1);
        if (next != 2) {
            throw new XmlPullParserException("No start tag found");
        }
        return createFromXmlInner(resources, xmlPullParser, attributeSetAsAttributeSet);
    }

    private static ColorStateList createFromXmlInner(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet) throws XmlPullParserException, IOException {
        String name = xmlPullParser.getName();
        if (name.equals("selector")) {
            ColorStateList colorStateList = new ColorStateList();
            colorStateList.inflate(resources, xmlPullParser, attributeSet);
            return colorStateList;
        }
        throw new XmlPullParserException(xmlPullParser.getPositionDescription() + ": invalid drawable tag " + name);
    }

    public ColorStateList withAlpha(int i) {
        int length = this.mColors.length;
        int[] iArr = new int[length];
        for (int i2 = 0; i2 < length; i2++) {
            iArr[i2] = (this.mColors[i2] & 16777215) | (i << 24);
        }
        return new ColorStateList(this.mStateSpecs, iArr);
    }

    /* JADX WARN: Code restructure failed: missing block: B:49:0x00cc, code lost:
    
        r1 = new int[r8];
        r18.mColors = r1;
        r18.mStateSpecs = new int[r8][];
        java.lang.System.arraycopy(r5, 0, r1, 0, r8);
        java.lang.System.arraycopy(r6, 0, r18.mStateSpecs, 0, r8);
     */
    /* JADX WARN: Code restructure failed: missing block: B:50:0x00dc, code lost:
    
        return;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void inflate(android.content.res.Resources r19, org.xmlpull.v1.XmlPullParser r20, android.util.AttributeSet r21) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            Method dump skipped, instruction units count: 221
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.content.res.ColorStateList.inflate(android.content.res.Resources, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet):void");
    }

    public boolean isStateful() {
        return this.mStateSpecs.length > 1;
    }

    public int getColorForState(int[] iArr, int i) {
        int length = this.mStateSpecs.length;
        for (int i2 = 0; i2 < length; i2++) {
            if (StateSet.stateSetMatches(this.mStateSpecs[i2], iArr)) {
                return this.mColors[i2];
            }
        }
        return i;
    }

    public int getDefaultColor() {
        return this.mDefaultColor;
    }

    public String toString() {
        return "ColorStateList{mStateSpecs=" + Arrays.deepToString(this.mStateSpecs) + "mColors=" + Arrays.toString(this.mColors) + "mDefaultColor=" + this.mDefaultColor + '}';
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        int length = this.mStateSpecs.length;
        parcel.writeInt(length);
        for (int i2 = 0; i2 < length; i2++) {
            parcel.writeIntArray(this.mStateSpecs[i2]);
        }
        parcel.writeIntArray(this.mColors);
    }
}
