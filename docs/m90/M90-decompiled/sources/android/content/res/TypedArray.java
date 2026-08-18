package android.content.res;

import android.content.res.XmlBlock;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;
import com.android.internal.util.XmlUtils;
import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public class TypedArray {
    int[] mData;
    int[] mIndices;
    int mLength;
    private final Resources mResources;
    int[] mRsrcs;
    TypedValue mValue = new TypedValue();
    XmlBlock.Parser mXml;

    public int length() {
        return this.mLength;
    }

    public int getIndexCount() {
        return this.mIndices[0];
    }

    public int getIndex(int i) {
        return this.mIndices[i + 1];
    }

    public Resources getResources() {
        return this.mResources;
    }

    public CharSequence getText(int i) {
        int i2 = i * 6;
        int i3 = this.mData[i2 + 0];
        if (i3 == 0) {
            return null;
        }
        if (i3 == 3) {
            return loadStringValueAt(i2);
        }
        TypedValue typedValue = this.mValue;
        if (getValueAt(i2, typedValue)) {
            Log.w("Resources", "Converting to string: " + typedValue);
            return typedValue.coerceToString();
        }
        Log.w("Resources", "getString of bad type: 0x" + Integer.toHexString(i3));
        return null;
    }

    public String getString(int i) {
        int i2 = i * 6;
        int i3 = this.mData[i2 + 0];
        if (i3 == 0) {
            return null;
        }
        if (i3 == 3) {
            return loadStringValueAt(i2).toString();
        }
        TypedValue typedValue = this.mValue;
        if (getValueAt(i2, typedValue)) {
            Log.w("Resources", "Converting to string: " + typedValue);
            CharSequence charSequenceCoerceToString = typedValue.coerceToString();
            if (charSequenceCoerceToString != null) {
                return charSequenceCoerceToString.toString();
            }
            return null;
        }
        Log.w("Resources", "getString of bad type: 0x" + Integer.toHexString(i3));
        return null;
    }

    public String getNonResourceString(int i) {
        int i2 = i * 6;
        int[] iArr = this.mData;
        if (iArr[i2 + 0] != 3 || iArr[i2 + 2] >= 0) {
            return null;
        }
        return this.mXml.getPooledString(iArr[i2 + 1]).toString();
    }

    public String getNonConfigurationString(int i, int i2) {
        int i3 = i * 6;
        int[] iArr = this.mData;
        int i4 = iArr[i3 + 0];
        if (((~i2) & iArr[i3 + 4]) != 0 || i4 == 0) {
            return null;
        }
        if (i4 == 3) {
            return loadStringValueAt(i3).toString();
        }
        TypedValue typedValue = this.mValue;
        if (getValueAt(i3, typedValue)) {
            Log.w("Resources", "Converting to string: " + typedValue);
            CharSequence charSequenceCoerceToString = typedValue.coerceToString();
            if (charSequenceCoerceToString != null) {
                return charSequenceCoerceToString.toString();
            }
            return null;
        }
        Log.w("Resources", "getString of bad type: 0x" + Integer.toHexString(i4));
        return null;
    }

    public boolean getBoolean(int i, boolean z) {
        int i2 = i * 6;
        int[] iArr = this.mData;
        int i3 = iArr[i2 + 0];
        if (i3 == 0) {
            return z;
        }
        if (i3 >= 16 && i3 <= 31) {
            return iArr[i2 + 1] != 0;
        }
        TypedValue typedValue = this.mValue;
        if (getValueAt(i2, typedValue)) {
            Log.w("Resources", "Converting to boolean: " + typedValue);
            return XmlUtils.convertValueToBoolean(typedValue.coerceToString(), z);
        }
        Log.w("Resources", "getBoolean of bad type: 0x" + Integer.toHexString(i3));
        return z;
    }

    public int getInt(int i, int i2) {
        int i3 = i * 6;
        int[] iArr = this.mData;
        int i4 = iArr[i3 + 0];
        if (i4 == 0) {
            return i2;
        }
        if (i4 >= 16 && i4 <= 31) {
            return iArr[i3 + 1];
        }
        TypedValue typedValue = this.mValue;
        if (getValueAt(i3, typedValue)) {
            Log.w("Resources", "Converting to int: " + typedValue);
            return XmlUtils.convertValueToInt(typedValue.coerceToString(), i2);
        }
        Log.w("Resources", "getInt of bad type: 0x" + Integer.toHexString(i4));
        return i2;
    }

    public float getFloat(int i, float f) {
        int i2 = i * 6;
        int[] iArr = this.mData;
        int i3 = iArr[i2 + 0];
        if (i3 == 0) {
            return f;
        }
        if (i3 == 4) {
            return Float.intBitsToFloat(iArr[i2 + 1]);
        }
        if (i3 >= 16 && i3 <= 31) {
            return iArr[i2 + 1];
        }
        TypedValue typedValue = this.mValue;
        if (getValueAt(i2, typedValue)) {
            Log.w("Resources", "Converting to float: " + typedValue);
            CharSequence charSequenceCoerceToString = typedValue.coerceToString();
            if (charSequenceCoerceToString != null) {
                return Float.parseFloat(charSequenceCoerceToString.toString());
            }
        }
        Log.w("Resources", "getFloat of bad type: 0x" + Integer.toHexString(i3));
        return f;
    }

    public int getColor(int i, int i2) {
        int i3 = i * 6;
        int[] iArr = this.mData;
        int i4 = iArr[i3 + 0];
        if (i4 == 0) {
            return i2;
        }
        if (i4 >= 16 && i4 <= 31) {
            return iArr[i3 + 1];
        }
        if (i4 == 3) {
            TypedValue typedValue = this.mValue;
            return getValueAt(i3, typedValue) ? this.mResources.loadColorStateList(typedValue, typedValue.resourceId).getDefaultColor() : i2;
        }
        throw new UnsupportedOperationException("Can't convert to color: type=0x" + Integer.toHexString(i4));
    }

    public ColorStateList getColorStateList(int i) {
        TypedValue typedValue = this.mValue;
        if (getValueAt(i * 6, typedValue)) {
            return this.mResources.loadColorStateList(typedValue, typedValue.resourceId);
        }
        return null;
    }

    public int getInteger(int i, int i2) {
        int i3 = i * 6;
        int[] iArr = this.mData;
        int i4 = iArr[i3 + 0];
        if (i4 == 0) {
            return i2;
        }
        if (i4 >= 16 && i4 <= 31) {
            return iArr[i3 + 1];
        }
        throw new UnsupportedOperationException("Can't convert to integer: type=0x" + Integer.toHexString(i4));
    }

    public float getDimension(int i, float f) {
        int i2 = i * 6;
        int[] iArr = this.mData;
        int i3 = iArr[i2 + 0];
        if (i3 == 0) {
            return f;
        }
        if (i3 == 5) {
            return TypedValue.complexToDimension(iArr[i2 + 1], this.mResources.mMetrics);
        }
        throw new UnsupportedOperationException("Can't convert to dimension: type=0x" + Integer.toHexString(i3));
    }

    public int getDimensionPixelOffset(int i, int i2) {
        int i3 = i * 6;
        int[] iArr = this.mData;
        int i4 = iArr[i3 + 0];
        if (i4 == 0) {
            return i2;
        }
        if (i4 == 5) {
            return TypedValue.complexToDimensionPixelOffset(iArr[i3 + 1], this.mResources.mMetrics);
        }
        throw new UnsupportedOperationException("Can't convert to dimension: type=0x" + Integer.toHexString(i4));
    }

    public int getDimensionPixelSize(int i, int i2) {
        int i3 = i * 6;
        int[] iArr = this.mData;
        int i4 = iArr[i3 + 0];
        if (i4 == 0) {
            return i2;
        }
        if (i4 == 5) {
            return TypedValue.complexToDimensionPixelSize(iArr[i3 + 1], this.mResources.mMetrics);
        }
        throw new UnsupportedOperationException("Can't convert to dimension: type=0x" + Integer.toHexString(i4));
    }

    public int getLayoutDimension(int i, String str) {
        int i2 = i * 6;
        int[] iArr = this.mData;
        int i3 = iArr[i2 + 0];
        if (i3 >= 16 && i3 <= 31) {
            return iArr[i2 + 1];
        }
        if (i3 == 5) {
            return TypedValue.complexToDimensionPixelSize(iArr[i2 + 1], this.mResources.mMetrics);
        }
        throw new RuntimeException(getPositionDescription() + ": You must supply a " + str + " attribute.");
    }

    public int getLayoutDimension(int i, int i2) {
        int i3 = i * 6;
        int[] iArr = this.mData;
        int i4 = iArr[i3 + 0];
        if (i4 < 16 || i4 > 31) {
            return i4 == 5 ? TypedValue.complexToDimensionPixelSize(iArr[i3 + 1], this.mResources.mMetrics) : i2;
        }
        return iArr[i3 + 1];
    }

    public float getFraction(int i, int i2, int i3, float f) {
        int i4 = i * 6;
        int[] iArr = this.mData;
        int i5 = iArr[i4 + 0];
        if (i5 == 0) {
            return f;
        }
        if (i5 == 6) {
            return TypedValue.complexToFraction(iArr[i4 + 1], i2, i3);
        }
        throw new UnsupportedOperationException("Can't convert to fraction: type=0x" + Integer.toHexString(i5));
    }

    public int getResourceId(int i, int i2) {
        int i3;
        int i4 = i * 6;
        int[] iArr = this.mData;
        return (iArr[i4 + 0] == 0 || (i3 = iArr[i4 + 3]) == 0) ? i2 : i3;
    }

    public Drawable getDrawable(int i) {
        TypedValue typedValue = this.mValue;
        if (getValueAt(i * 6, typedValue)) {
            return this.mResources.loadDrawable(typedValue, typedValue.resourceId);
        }
        return null;
    }

    public CharSequence[] getTextArray(int i) {
        TypedValue typedValue = this.mValue;
        if (getValueAt(i * 6, typedValue)) {
            return this.mResources.getTextArray(typedValue.resourceId);
        }
        return null;
    }

    public boolean getValue(int i, TypedValue typedValue) {
        return getValueAt(i * 6, typedValue);
    }

    public boolean hasValue(int i) {
        return this.mData[(i * 6) + 0] != 0;
    }

    public TypedValue peekValue(int i) {
        TypedValue typedValue = this.mValue;
        if (getValueAt(i * 6, typedValue)) {
            return typedValue;
        }
        return null;
    }

    public String getPositionDescription() {
        XmlBlock.Parser parser = this.mXml;
        return parser != null ? parser.getPositionDescription() : "<internal>";
    }

    public void recycle() {
        synchronized (this.mResources.mAccessLock) {
            TypedArray typedArray = this.mResources.mCachedStyledAttributes;
            if (typedArray == null || typedArray.mData.length < this.mData.length) {
                this.mXml = null;
                this.mResources.mCachedStyledAttributes = this;
            }
        }
    }

    private boolean getValueAt(int i, TypedValue typedValue) {
        int[] iArr = this.mData;
        int i2 = iArr[i + 0];
        if (i2 == 0) {
            return false;
        }
        typedValue.type = i2;
        typedValue.data = iArr[i + 1];
        typedValue.assetCookie = iArr[i + 2];
        typedValue.resourceId = iArr[i + 3];
        typedValue.changingConfigurations = iArr[i + 4];
        typedValue.density = iArr[i + 5];
        typedValue.string = i2 == 3 ? loadStringValueAt(i) : null;
        return true;
    }

    private CharSequence loadStringValueAt(int i) {
        int[] iArr = this.mData;
        int i2 = iArr[i + 2];
        if (i2 < 0) {
            XmlBlock.Parser parser = this.mXml;
            if (parser != null) {
                return parser.getPooledString(iArr[i + 1]);
            }
            return null;
        }
        return this.mResources.mAssets.getPooledString(i2, iArr[i + 1]);
    }

    TypedArray(Resources resources, int[] iArr, int[] iArr2, int i) {
        this.mResources = resources;
        this.mData = iArr;
        this.mIndices = iArr2;
        this.mLength = i;
    }

    public String toString() {
        return Arrays.toString(this.mData);
    }
}
