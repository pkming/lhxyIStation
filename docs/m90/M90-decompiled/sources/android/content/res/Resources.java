package android.content.res;

import android.R;
import android.content.pm.ActivityInfo;
import android.content.res.XmlBlock;
import android.graphics.Movie;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.nfc.cardemulation.CardEmulation;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Trace;
import android.provider.CalendarContract;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.TypedValue;
import com.android.internal.util.XmlUtils;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Locale;
import java.util.Objects;
import libcore.icu.NativePluralRules;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes.dex */
public class Resources {
    private static final boolean DEBUG_ATTRIBUTES_CACHE = false;
    private static final boolean DEBUG_CONFIG = false;
    private static final boolean DEBUG_LOAD = false;
    private static final int ID_OTHER = 16777220;
    static final String TAG = "Resources";
    private static final boolean TRACE_FOR_MISS_PRELOAD = false;
    private static final boolean TRACE_FOR_PRELOAD = false;
    static Resources mSystem;
    private static boolean sPreloaded;
    private static int sPreloadedDensity;
    final Object mAccessLock;
    final AssetManager mAssets;
    TypedArray mCachedStyledAttributes;
    private final int[] mCachedXmlBlockIds;
    private final XmlBlock[] mCachedXmlBlocks;
    final LongSparseArray<WeakReference<Drawable.ConstantState>> mColorDrawableCache;
    final LongSparseArray<WeakReference<ColorStateList>> mColorStateListCache;
    private CompatibilityInfo mCompatibilityInfo;
    private final Configuration mConfiguration;
    final LongSparseArray<WeakReference<Drawable.ConstantState>> mDrawableCache;
    private int mLastCachedXmlBlockIndex;
    RuntimeException mLastRetrievedAttrs;
    final DisplayMetrics mMetrics;
    private NativePluralRules mPluralRule;
    boolean mPreloading;
    final Configuration mTmpConfig;
    TypedValue mTmpValue;
    private WeakReference<IBinder> mToken;
    private static final Object sSync = new Object();
    private static final LongSparseArray<Drawable.ConstantState> sPreloadedColorDrawables = new LongSparseArray<>();
    private static final LongSparseArray<ColorStateList> sPreloadedColorStateLists = new LongSparseArray<>();
    private static final LongSparseArray<Drawable.ConstantState>[] sPreloadedDrawables = {new LongSparseArray<>(), new LongSparseArray<>()};
    private static final int LAYOUT_DIR_CONFIG = ActivityInfo.activityInfoConfigToNative(8192);

    private static int attrForQuantityCode(int i) {
        if (i == 0) {
            return 16777221;
        }
        if (i == 1) {
            return 16777222;
        }
        if (i == 2) {
            return 16777223;
        }
        if (i == 3) {
            return 16777224;
        }
        if (i != 4) {
            return ID_OTHER;
        }
        return 16777225;
    }

    public static boolean resourceHasPackage(int i) {
        return (i >>> 24) != 0;
    }

    public static int selectSystemTheme(int i, int i2, int i3, int i4, int i5) {
        return i != 0 ? i : i2 < 11 ? i3 : i2 < 14 ? i4 : i5;
    }

    private static String stringForQuantityCode(int i) {
        return i != 0 ? i != 1 ? i != 2 ? i != 3 ? i != 4 ? CardEmulation.CATEGORY_OTHER : "many" : "few" : "two" : "one" : "zero";
    }

    public static int selectDefaultTheme(int i, int i2) {
        return selectSystemTheme(i, i2, R.style.Theme, R.style.Theme_Holo, R.style.Theme_DeviceDefault);
    }

    public static class NotFoundException extends RuntimeException {
        public NotFoundException() {
        }

        public NotFoundException(String str) {
            super(str);
        }
    }

    public Resources(AssetManager assetManager, DisplayMetrics displayMetrics, Configuration configuration) {
        this(assetManager, displayMetrics, configuration, CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO, null);
    }

    public Resources(AssetManager assetManager, DisplayMetrics displayMetrics, Configuration configuration, CompatibilityInfo compatibilityInfo, IBinder iBinder) {
        this.mAccessLock = new Object();
        this.mTmpConfig = new Configuration();
        this.mTmpValue = new TypedValue();
        this.mDrawableCache = new LongSparseArray<>(0);
        this.mColorStateListCache = new LongSparseArray<>(0);
        this.mColorDrawableCache = new LongSparseArray<>(0);
        this.mCachedStyledAttributes = null;
        this.mLastRetrievedAttrs = null;
        this.mLastCachedXmlBlockIndex = -1;
        this.mCachedXmlBlockIds = new int[]{0, 0, 0, 0};
        this.mCachedXmlBlocks = new XmlBlock[4];
        this.mConfiguration = new Configuration();
        DisplayMetrics displayMetrics2 = new DisplayMetrics();
        this.mMetrics = displayMetrics2;
        this.mCompatibilityInfo = CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO;
        this.mAssets = assetManager;
        displayMetrics2.setToDefaults();
        if (compatibilityInfo != null) {
            this.mCompatibilityInfo = compatibilityInfo;
        }
        this.mToken = new WeakReference<>(iBinder);
        updateConfiguration(configuration, displayMetrics);
        assetManager.ensureStringBlocks();
    }

    public static Resources getSystem() {
        Resources resources;
        synchronized (sSync) {
            resources = mSystem;
            if (resources == null) {
                resources = new Resources();
                mSystem = resources;
            }
        }
        return resources;
    }

    public CharSequence getText(int i) throws NotFoundException {
        CharSequence resourceText = this.mAssets.getResourceText(i);
        if (resourceText != null) {
            return resourceText;
        }
        throw new NotFoundException("String resource ID #0x" + Integer.toHexString(i));
    }

    public CharSequence getQuantityText(int i, int i2) throws NotFoundException {
        NativePluralRules pluralRule = getPluralRule();
        CharSequence resourceBagText = this.mAssets.getResourceBagText(i, attrForQuantityCode(pluralRule.quantityForInt(i2)));
        if (resourceBagText != null) {
            return resourceBagText;
        }
        CharSequence resourceBagText2 = this.mAssets.getResourceBagText(i, ID_OTHER);
        if (resourceBagText2 != null) {
            return resourceBagText2;
        }
        throw new NotFoundException("Plural resource ID #0x" + Integer.toHexString(i) + " quantity=" + i2 + " item=" + stringForQuantityCode(pluralRule.quantityForInt(i2)));
    }

    private NativePluralRules getPluralRule() {
        NativePluralRules nativePluralRules;
        synchronized (sSync) {
            if (this.mPluralRule == null) {
                this.mPluralRule = NativePluralRules.forLocale(this.mConfiguration.locale);
            }
            nativePluralRules = this.mPluralRule;
        }
        return nativePluralRules;
    }

    public String getString(int i) throws NotFoundException {
        CharSequence text = getText(i);
        if (text != null) {
            return text.toString();
        }
        throw new NotFoundException("String resource ID #0x" + Integer.toHexString(i));
    }

    public String getString(int i, Object... objArr) throws NotFoundException {
        return String.format(this.mConfiguration.locale, getString(i), objArr);
    }

    public String getQuantityString(int i, int i2, Object... objArr) throws NotFoundException {
        return String.format(this.mConfiguration.locale, getQuantityText(i, i2).toString(), objArr);
    }

    public String getQuantityString(int i, int i2) throws NotFoundException {
        return getQuantityText(i, i2).toString();
    }

    public CharSequence getText(int i, CharSequence charSequence) {
        CharSequence resourceText = i != 0 ? this.mAssets.getResourceText(i) : null;
        return resourceText != null ? resourceText : charSequence;
    }

    public CharSequence[] getTextArray(int i) throws NotFoundException {
        CharSequence[] resourceTextArray = this.mAssets.getResourceTextArray(i);
        if (resourceTextArray != null) {
            return resourceTextArray;
        }
        throw new NotFoundException("Text array resource ID #0x" + Integer.toHexString(i));
    }

    public String[] getStringArray(int i) throws NotFoundException {
        String[] resourceStringArray = this.mAssets.getResourceStringArray(i);
        if (resourceStringArray != null) {
            return resourceStringArray;
        }
        throw new NotFoundException("String array resource ID #0x" + Integer.toHexString(i));
    }

    public int[] getIntArray(int i) throws NotFoundException {
        int[] arrayIntResource = this.mAssets.getArrayIntResource(i);
        if (arrayIntResource != null) {
            return arrayIntResource;
        }
        throw new NotFoundException("Int array resource ID #0x" + Integer.toHexString(i));
    }

    public TypedArray obtainTypedArray(int i) throws NotFoundException {
        int arraySize = this.mAssets.getArraySize(i);
        if (arraySize < 0) {
            throw new NotFoundException("Array resource ID #0x" + Integer.toHexString(i));
        }
        TypedArray cachedStyledAttributes = getCachedStyledAttributes(arraySize);
        cachedStyledAttributes.mLength = this.mAssets.retrieveArray(i, cachedStyledAttributes.mData);
        cachedStyledAttributes.mIndices[0] = 0;
        return cachedStyledAttributes;
    }

    public float getDimension(int i) throws NotFoundException {
        float fComplexToDimension;
        synchronized (this.mAccessLock) {
            TypedValue typedValue = this.mTmpValue;
            if (typedValue == null) {
                typedValue = new TypedValue();
                this.mTmpValue = typedValue;
            }
            getValue(i, typedValue, true);
            if (typedValue.type == 5) {
                fComplexToDimension = TypedValue.complexToDimension(typedValue.data, this.mMetrics);
            } else {
                throw new NotFoundException("Resource ID #0x" + Integer.toHexString(i) + " type #0x" + Integer.toHexString(typedValue.type) + " is not valid");
            }
        }
        return fComplexToDimension;
    }

    public int getDimensionPixelOffset(int i) throws NotFoundException {
        int iComplexToDimensionPixelOffset;
        synchronized (this.mAccessLock) {
            TypedValue typedValue = this.mTmpValue;
            if (typedValue == null) {
                typedValue = new TypedValue();
                this.mTmpValue = typedValue;
            }
            getValue(i, typedValue, true);
            if (typedValue.type == 5) {
                iComplexToDimensionPixelOffset = TypedValue.complexToDimensionPixelOffset(typedValue.data, this.mMetrics);
            } else {
                throw new NotFoundException("Resource ID #0x" + Integer.toHexString(i) + " type #0x" + Integer.toHexString(typedValue.type) + " is not valid");
            }
        }
        return iComplexToDimensionPixelOffset;
    }

    public int getDimensionPixelSize(int i) throws NotFoundException {
        int iComplexToDimensionPixelSize;
        synchronized (this.mAccessLock) {
            TypedValue typedValue = this.mTmpValue;
            if (typedValue == null) {
                typedValue = new TypedValue();
                this.mTmpValue = typedValue;
            }
            getValue(i, typedValue, true);
            if (typedValue.type == 5) {
                iComplexToDimensionPixelSize = TypedValue.complexToDimensionPixelSize(typedValue.data, this.mMetrics);
            } else {
                throw new NotFoundException("Resource ID #0x" + Integer.toHexString(i) + " type #0x" + Integer.toHexString(typedValue.type) + " is not valid");
            }
        }
        return iComplexToDimensionPixelSize;
    }

    public float getFraction(int i, int i2, int i3) {
        float fComplexToFraction;
        synchronized (this.mAccessLock) {
            TypedValue typedValue = this.mTmpValue;
            if (typedValue == null) {
                typedValue = new TypedValue();
                this.mTmpValue = typedValue;
            }
            getValue(i, typedValue, true);
            if (typedValue.type == 6) {
                fComplexToFraction = TypedValue.complexToFraction(typedValue.data, i2, i3);
            } else {
                throw new NotFoundException("Resource ID #0x" + Integer.toHexString(i) + " type #0x" + Integer.toHexString(typedValue.type) + " is not valid");
            }
        }
        return fComplexToFraction;
    }

    public Drawable getDrawable(int i) throws NotFoundException {
        TypedValue typedValue;
        synchronized (this.mAccessLock) {
            typedValue = this.mTmpValue;
            if (typedValue == null) {
                typedValue = new TypedValue();
            } else {
                this.mTmpValue = null;
            }
            getValue(i, typedValue, true);
        }
        Drawable drawableLoadDrawable = loadDrawable(typedValue, i);
        synchronized (this.mAccessLock) {
            if (this.mTmpValue == null) {
                this.mTmpValue = typedValue;
            }
        }
        return drawableLoadDrawable;
    }

    public Drawable getDrawableForDensity(int i, int i2) throws NotFoundException {
        TypedValue typedValue;
        synchronized (this.mAccessLock) {
            typedValue = this.mTmpValue;
            if (typedValue == null) {
                typedValue = new TypedValue();
            } else {
                this.mTmpValue = null;
            }
            getValueForDensity(i, i2, typedValue, true);
            if (typedValue.density > 0 && typedValue.density != 65535) {
                if (typedValue.density == i2) {
                    typedValue.density = this.mMetrics.densityDpi;
                } else {
                    typedValue.density = (typedValue.density * this.mMetrics.densityDpi) / i2;
                }
            }
        }
        Drawable drawableLoadDrawable = loadDrawable(typedValue, i);
        synchronized (this.mAccessLock) {
            if (this.mTmpValue == null) {
                this.mTmpValue = typedValue;
            }
        }
        return drawableLoadDrawable;
    }

    public Movie getMovie(int i) throws NotFoundException {
        InputStream inputStreamOpenRawResource = openRawResource(i);
        Movie movieDecodeStream = Movie.decodeStream(inputStreamOpenRawResource);
        try {
            inputStreamOpenRawResource.close();
        } catch (IOException unused) {
        }
        return movieDecodeStream;
    }

    public int getColor(int i) throws NotFoundException {
        synchronized (this.mAccessLock) {
            TypedValue typedValue = this.mTmpValue;
            if (typedValue == null) {
                typedValue = new TypedValue();
            }
            getValue(i, typedValue, true);
            if (typedValue.type >= 16 && typedValue.type <= 31) {
                this.mTmpValue = typedValue;
                return typedValue.data;
            }
            if (typedValue.type != 3) {
                throw new NotFoundException("Resource ID #0x" + Integer.toHexString(i) + " type #0x" + Integer.toHexString(typedValue.type) + " is not valid");
            }
            this.mTmpValue = null;
            ColorStateList colorStateListLoadColorStateList = loadColorStateList(typedValue, i);
            synchronized (this.mAccessLock) {
                if (this.mTmpValue == null) {
                    this.mTmpValue = typedValue;
                }
            }
            return colorStateListLoadColorStateList.getDefaultColor();
        }
    }

    public ColorStateList getColorStateList(int i) throws NotFoundException {
        TypedValue typedValue;
        synchronized (this.mAccessLock) {
            typedValue = this.mTmpValue;
            if (typedValue == null) {
                typedValue = new TypedValue();
            } else {
                this.mTmpValue = null;
            }
            getValue(i, typedValue, true);
        }
        ColorStateList colorStateListLoadColorStateList = loadColorStateList(typedValue, i);
        synchronized (this.mAccessLock) {
            if (this.mTmpValue == null) {
                this.mTmpValue = typedValue;
            }
        }
        return colorStateListLoadColorStateList;
    }

    public boolean getBoolean(int i) throws NotFoundException {
        boolean z;
        synchronized (this.mAccessLock) {
            TypedValue typedValue = this.mTmpValue;
            if (typedValue == null) {
                typedValue = new TypedValue();
                this.mTmpValue = typedValue;
            }
            z = true;
            getValue(i, typedValue, true);
            if (typedValue.type >= 16 && typedValue.type <= 31) {
                if (typedValue.data == 0) {
                    z = false;
                }
            } else {
                throw new NotFoundException("Resource ID #0x" + Integer.toHexString(i) + " type #0x" + Integer.toHexString(typedValue.type) + " is not valid");
            }
        }
        return z;
    }

    public int getInteger(int i) throws NotFoundException {
        int i2;
        synchronized (this.mAccessLock) {
            TypedValue typedValue = this.mTmpValue;
            if (typedValue == null) {
                typedValue = new TypedValue();
                this.mTmpValue = typedValue;
            }
            getValue(i, typedValue, true);
            if (typedValue.type >= 16 && typedValue.type <= 31) {
                i2 = typedValue.data;
            } else {
                throw new NotFoundException("Resource ID #0x" + Integer.toHexString(i) + " type #0x" + Integer.toHexString(typedValue.type) + " is not valid");
            }
        }
        return i2;
    }

    public XmlResourceParser getLayout(int i) throws NotFoundException {
        return loadXmlResourceParser(i, "layout");
    }

    public XmlResourceParser getAnimation(int i) throws NotFoundException {
        return loadXmlResourceParser(i, "anim");
    }

    public XmlResourceParser getXml(int i) throws NotFoundException {
        return loadXmlResourceParser(i, "xml");
    }

    public InputStream openRawResource(int i) throws NotFoundException {
        TypedValue typedValue;
        synchronized (this.mAccessLock) {
            typedValue = this.mTmpValue;
            if (typedValue == null) {
                typedValue = new TypedValue();
            } else {
                this.mTmpValue = null;
            }
        }
        InputStream inputStreamOpenRawResource = openRawResource(i, typedValue);
        synchronized (this.mAccessLock) {
            if (this.mTmpValue == null) {
                this.mTmpValue = typedValue;
            }
        }
        return inputStreamOpenRawResource;
    }

    public InputStream openRawResource(int i, TypedValue typedValue) throws NotFoundException {
        getValue(i, typedValue, true);
        try {
            return this.mAssets.openNonAsset(typedValue.assetCookie, typedValue.string.toString(), 2);
        } catch (Exception e) {
            NotFoundException notFoundException = new NotFoundException("File " + typedValue.string.toString() + " from drawable resource ID #0x" + Integer.toHexString(i));
            notFoundException.initCause(e);
            throw notFoundException;
        }
    }

    public AssetFileDescriptor openRawResourceFd(int i) throws NotFoundException {
        TypedValue typedValue;
        synchronized (this.mAccessLock) {
            typedValue = this.mTmpValue;
            if (typedValue == null) {
                typedValue = new TypedValue();
            } else {
                this.mTmpValue = null;
            }
            getValue(i, typedValue, true);
        }
        try {
            try {
                AssetFileDescriptor assetFileDescriptorOpenNonAssetFd = this.mAssets.openNonAssetFd(typedValue.assetCookie, typedValue.string.toString());
                synchronized (this.mAccessLock) {
                    if (this.mTmpValue == null) {
                        this.mTmpValue = typedValue;
                    }
                }
                return assetFileDescriptorOpenNonAssetFd;
            } catch (Exception e) {
                NotFoundException notFoundException = new NotFoundException("File " + typedValue.string.toString() + " from drawable resource ID #0x" + Integer.toHexString(i));
                notFoundException.initCause(e);
                throw notFoundException;
            }
        } catch (Throwable th) {
            synchronized (this.mAccessLock) {
                if (this.mTmpValue == null) {
                    this.mTmpValue = typedValue;
                }
                throw th;
            }
        }
    }

    public void getValue(int i, TypedValue typedValue, boolean z) throws NotFoundException {
        if (!this.mAssets.getResourceValue(i, 0, typedValue, z)) {
            throw new NotFoundException("Resource ID #0x" + Integer.toHexString(i));
        }
    }

    public void getValueForDensity(int i, int i2, TypedValue typedValue, boolean z) throws NotFoundException {
        if (!this.mAssets.getResourceValue(i, i2, typedValue, z)) {
            throw new NotFoundException("Resource ID #0x" + Integer.toHexString(i));
        }
    }

    public void getValue(String str, TypedValue typedValue, boolean z) throws NotFoundException {
        int identifier = getIdentifier(str, "string", null);
        if (identifier != 0) {
            getValue(identifier, typedValue, z);
            return;
        }
        throw new NotFoundException("String resource name " + str);
    }

    public final class Theme {
        private final AssetManager mAssets;
        private final int mTheme;

        public void applyStyle(int i, boolean z) {
            AssetManager.applyThemeStyle(this.mTheme, i, z);
        }

        public void setTo(Theme theme) {
            AssetManager.copyTheme(this.mTheme, theme.mTheme);
        }

        public TypedArray obtainStyledAttributes(int[] iArr) {
            TypedArray cachedStyledAttributes = Resources.this.getCachedStyledAttributes(iArr.length);
            cachedStyledAttributes.mRsrcs = iArr;
            AssetManager.applyStyle(this.mTheme, 0, 0, 0, iArr, cachedStyledAttributes.mData, cachedStyledAttributes.mIndices);
            return cachedStyledAttributes;
        }

        public TypedArray obtainStyledAttributes(int i, int[] iArr) throws NotFoundException {
            TypedArray cachedStyledAttributes = Resources.this.getCachedStyledAttributes(iArr.length);
            cachedStyledAttributes.mRsrcs = iArr;
            AssetManager.applyStyle(this.mTheme, 0, i, 0, iArr, cachedStyledAttributes.mData, cachedStyledAttributes.mIndices);
            return cachedStyledAttributes;
        }

        public TypedArray obtainStyledAttributes(AttributeSet attributeSet, int[] iArr, int i, int i2) {
            TypedArray cachedStyledAttributes = Resources.this.getCachedStyledAttributes(iArr.length);
            XmlBlock.Parser parser = (XmlBlock.Parser) attributeSet;
            AssetManager.applyStyle(this.mTheme, i, i2, parser != null ? parser.mParseState : 0, iArr, cachedStyledAttributes.mData, cachedStyledAttributes.mIndices);
            cachedStyledAttributes.mRsrcs = iArr;
            cachedStyledAttributes.mXml = parser;
            return cachedStyledAttributes;
        }

        public boolean resolveAttribute(int i, TypedValue typedValue, boolean z) {
            return this.mAssets.getThemeValue(this.mTheme, i, typedValue, z);
        }

        public void dump(int i, String str, String str2) {
            AssetManager.dumpTheme(this.mTheme, i, str, str2);
        }

        protected void finalize() throws Throwable {
            super.finalize();
            this.mAssets.releaseTheme(this.mTheme);
        }

        Theme() {
            AssetManager assetManager = Resources.this.mAssets;
            this.mAssets = assetManager;
            this.mTheme = assetManager.createTheme();
        }
    }

    public final Theme newTheme() {
        return new Theme();
    }

    public TypedArray obtainAttributes(AttributeSet attributeSet, int[] iArr) {
        TypedArray cachedStyledAttributes = getCachedStyledAttributes(iArr.length);
        XmlBlock.Parser parser = (XmlBlock.Parser) attributeSet;
        this.mAssets.retrieveAttributes(parser.mParseState, iArr, cachedStyledAttributes.mData, cachedStyledAttributes.mIndices);
        cachedStyledAttributes.mRsrcs = iArr;
        cachedStyledAttributes.mXml = parser;
        return cachedStyledAttributes;
    }

    public void updateConfiguration(Configuration configuration, DisplayMetrics displayMetrics) {
        updateConfiguration(configuration, displayMetrics, null);
    }

    public void updateConfiguration(Configuration configuration, DisplayMetrics displayMetrics, CompatibilityInfo compatibilityInfo) {
        int i;
        int i2;
        synchronized (this.mAccessLock) {
            if (compatibilityInfo != null) {
                try {
                    this.mCompatibilityInfo = compatibilityInfo;
                } catch (Throwable th) {
                    throw th;
                }
            }
            if (displayMetrics != null) {
                this.mMetrics.setTo(displayMetrics);
            }
            this.mCompatibilityInfo.applyToDisplayMetrics(this.mMetrics);
            int iActivityInfoConfigToNative = 268435455;
            if (configuration != null) {
                this.mTmpConfig.setTo(configuration);
                int i3 = configuration.densityDpi;
                if (i3 == 0) {
                    i3 = this.mMetrics.noncompatDensityDpi;
                }
                this.mCompatibilityInfo.applyToConfiguration(i3, this.mTmpConfig);
                if (this.mTmpConfig.locale == null) {
                    this.mTmpConfig.locale = Locale.getDefault();
                    Configuration configuration2 = this.mTmpConfig;
                    configuration2.setLayoutDirection(configuration2.locale);
                }
                iActivityInfoConfigToNative = ActivityInfo.activityInfoConfigToNative(this.mConfiguration.updateFrom(this.mTmpConfig));
            }
            if (this.mConfiguration.locale == null) {
                this.mConfiguration.locale = Locale.getDefault();
                Configuration configuration3 = this.mConfiguration;
                configuration3.setLayoutDirection(configuration3.locale);
            }
            if (this.mConfiguration.densityDpi != 0) {
                this.mMetrics.densityDpi = this.mConfiguration.densityDpi;
                this.mMetrics.density = this.mConfiguration.densityDpi * 0.00625f;
            }
            DisplayMetrics displayMetrics2 = this.mMetrics;
            displayMetrics2.scaledDensity = displayMetrics2.density * this.mConfiguration.fontScale;
            String language = null;
            if (this.mConfiguration.locale != null) {
                language = this.mConfiguration.locale.getLanguage();
                if (this.mConfiguration.locale.getCountry() != null) {
                    language = language + "-" + this.mConfiguration.locale.getCountry();
                }
            }
            String str = language;
            if (this.mMetrics.widthPixels >= this.mMetrics.heightPixels) {
                i = this.mMetrics.widthPixels;
                i2 = this.mMetrics.heightPixels;
            } else {
                i = this.mMetrics.heightPixels;
                i2 = this.mMetrics.widthPixels;
            }
            int i4 = i;
            int i5 = i2;
            int i6 = this.mConfiguration.keyboardHidden;
            if (i6 == 1 && this.mConfiguration.hardKeyboardHidden == 2) {
                i6 = 3;
            }
            int i7 = iActivityInfoConfigToNative;
            this.mAssets.setConfiguration(this.mConfiguration.mcc, this.mConfiguration.mnc, str, this.mConfiguration.orientation, this.mConfiguration.touchscreen, this.mConfiguration.densityDpi, this.mConfiguration.keyboard, i6, this.mConfiguration.navigation, i4, i5, this.mConfiguration.smallestScreenWidthDp, this.mConfiguration.screenWidthDp, this.mConfiguration.screenHeightDp, this.mConfiguration.screenLayout, this.mConfiguration.uiMode, Build.VERSION.RESOURCES_SDK_INT);
            clearDrawableCacheLocked(this.mDrawableCache, i7);
            clearDrawableCacheLocked(this.mColorDrawableCache, i7);
            this.mColorStateListCache.clear();
            flushLayoutCache();
        }
        synchronized (sSync) {
            if (this.mPluralRule != null) {
                this.mPluralRule = NativePluralRules.forLocale(configuration.locale);
            }
        }
    }

    private void clearDrawableCacheLocked(LongSparseArray<WeakReference<Drawable.ConstantState>> longSparseArray, int i) {
        Drawable.ConstantState constantState;
        int size = longSparseArray.size();
        for (int i2 = 0; i2 < size; i2++) {
            WeakReference<Drawable.ConstantState> weakReferenceValueAt = longSparseArray.valueAt(i2);
            if (weakReferenceValueAt != null && (constantState = weakReferenceValueAt.get()) != null && Configuration.needNewResources(i, constantState.getChangingConfigurations())) {
                longSparseArray.setValueAt(i2, null);
            }
        }
    }

    public static void updateSystemConfiguration(Configuration configuration, DisplayMetrics displayMetrics, CompatibilityInfo compatibilityInfo) {
        Resources resources = mSystem;
        if (resources != null) {
            resources.updateConfiguration(configuration, displayMetrics, compatibilityInfo);
        }
    }

    public DisplayMetrics getDisplayMetrics() {
        return this.mMetrics;
    }

    public Configuration getConfiguration() {
        return this.mConfiguration;
    }

    public CompatibilityInfo getCompatibilityInfo() {
        return this.mCompatibilityInfo;
    }

    public void setCompatibilityInfo(CompatibilityInfo compatibilityInfo) {
        if (compatibilityInfo != null) {
            this.mCompatibilityInfo = compatibilityInfo;
            updateConfiguration(this.mConfiguration, this.mMetrics);
        }
    }

    public int getIdentifier(String str, String str2, String str3) {
        Objects.requireNonNull(str, "name is null");
        try {
            return Integer.parseInt(str);
        } catch (Exception unused) {
            return this.mAssets.getResourceIdentifier(str, str2, str3);
        }
    }

    public String getResourceName(int i) throws NotFoundException {
        String resourceName = this.mAssets.getResourceName(i);
        if (resourceName != null) {
            return resourceName;
        }
        throw new NotFoundException("Unable to find resource ID #0x" + Integer.toHexString(i));
    }

    public String getResourcePackageName(int i) throws NotFoundException {
        String resourcePackageName = this.mAssets.getResourcePackageName(i);
        if (resourcePackageName != null) {
            return resourcePackageName;
        }
        throw new NotFoundException("Unable to find resource ID #0x" + Integer.toHexString(i));
    }

    public String getResourceTypeName(int i) throws NotFoundException {
        String resourceTypeName = this.mAssets.getResourceTypeName(i);
        if (resourceTypeName != null) {
            return resourceTypeName;
        }
        throw new NotFoundException("Unable to find resource ID #0x" + Integer.toHexString(i));
    }

    public String getResourceEntryName(int i) throws NotFoundException {
        String resourceEntryName = this.mAssets.getResourceEntryName(i);
        if (resourceEntryName != null) {
            return resourceEntryName;
        }
        throw new NotFoundException("Unable to find resource ID #0x" + Integer.toHexString(i));
    }

    public void parseBundleExtras(XmlResourceParser xmlResourceParser, Bundle bundle) throws XmlPullParserException, IOException {
        int depth = xmlResourceParser.getDepth();
        while (true) {
            int next = xmlResourceParser.next();
            if (next == 1) {
                return;
            }
            if (next == 3 && xmlResourceParser.getDepth() <= depth) {
                return;
            }
            if (next != 3 && next != 4) {
                if (xmlResourceParser.getName().equals("extra")) {
                    parseBundleExtra("extra", xmlResourceParser, bundle);
                    XmlUtils.skipCurrentTag(xmlResourceParser);
                } else {
                    XmlUtils.skipCurrentTag(xmlResourceParser);
                }
            }
        }
    }

    public void parseBundleExtra(String str, AttributeSet attributeSet, Bundle bundle) throws XmlPullParserException {
        TypedArray typedArrayObtainAttributes = obtainAttributes(attributeSet, com.android.internal.R.styleable.Extra);
        String string = typedArrayObtainAttributes.getString(0);
        if (string == null) {
            typedArrayObtainAttributes.recycle();
            throw new XmlPullParserException("<" + str + "> requires an android:name attribute at " + attributeSet.getPositionDescription());
        }
        TypedValue typedValuePeekValue = typedArrayObtainAttributes.peekValue(1);
        if (typedValuePeekValue != null) {
            if (typedValuePeekValue.type == 3) {
                bundle.putCharSequence(string, typedValuePeekValue.coerceToString());
            } else if (typedValuePeekValue.type == 18) {
                bundle.putBoolean(string, typedValuePeekValue.data != 0);
            } else if (typedValuePeekValue.type >= 16 && typedValuePeekValue.type <= 31) {
                bundle.putInt(string, typedValuePeekValue.data);
            } else if (typedValuePeekValue.type == 4) {
                bundle.putFloat(string, typedValuePeekValue.getFloat());
            } else {
                typedArrayObtainAttributes.recycle();
                throw new XmlPullParserException("<" + str + "> only supports string, integer, float, color, and boolean at " + attributeSet.getPositionDescription());
            }
            typedArrayObtainAttributes.recycle();
            return;
        }
        typedArrayObtainAttributes.recycle();
        throw new XmlPullParserException("<" + str + "> requires an android:value or android:resource attribute at " + attributeSet.getPositionDescription());
    }

    public final AssetManager getAssets() {
        return this.mAssets;
    }

    public final void flushLayoutCache() {
        synchronized (this.mCachedXmlBlockIds) {
            int length = this.mCachedXmlBlockIds.length;
            for (int i = 0; i < length; i++) {
                this.mCachedXmlBlockIds[i] = 0;
                XmlBlock xmlBlock = this.mCachedXmlBlocks[i];
                if (xmlBlock != null) {
                    xmlBlock.close();
                }
                this.mCachedXmlBlocks[i] = null;
            }
        }
    }

    public final void startPreloading() {
        synchronized (sSync) {
            if (sPreloaded) {
                throw new IllegalStateException("Resources already preloaded");
            }
            sPreloaded = true;
            this.mPreloading = true;
            int i = DisplayMetrics.DENSITY_DEVICE;
            sPreloadedDensity = i;
            this.mConfiguration.densityDpi = i;
            updateConfiguration(null, null);
        }
    }

    public final void finishPreloading() {
        if (this.mPreloading) {
            this.mPreloading = false;
            flushLayoutCache();
        }
    }

    public LongSparseArray<Drawable.ConstantState> getPreloadedDrawables() {
        return sPreloadedDrawables[0];
    }

    private boolean verifyPreloadConfig(int i, int i2, int i3, String str) {
        String resourceName;
        if ((i & (-1073745921) & (~i2)) == 0) {
            return true;
        }
        try {
            resourceName = getResourceName(i3);
        } catch (NotFoundException unused) {
            resourceName = "?";
        }
        Log.w(TAG, "Preloaded " + str + " resource #0x" + Integer.toHexString(i3) + " (" + resourceName + ") that varies with configuration!!");
        return false;
    }

    Drawable loadDrawable(TypedValue typedValue, int i) throws NotFoundException {
        Drawable.ConstantState constantState;
        Drawable drawableNewDrawable;
        Drawable drawableCreateFromResourceStream;
        boolean z = typedValue.type >= 28 && typedValue.type <= 31;
        long j = z ? typedValue.data : (((long) typedValue.assetCookie) << 32) | ((long) typedValue.data);
        Drawable cachedDrawable = getCachedDrawable(z ? this.mColorDrawableCache : this.mDrawableCache, j);
        if (cachedDrawable != null) {
            return cachedDrawable;
        }
        if (z) {
            constantState = sPreloadedColorDrawables.get(j);
        } else {
            constantState = sPreloadedDrawables[this.mConfiguration.getLayoutDirection()].get(j);
        }
        if (constantState != null) {
            drawableNewDrawable = constantState.newDrawable(this);
        } else {
            if (z) {
                cachedDrawable = new ColorDrawable(typedValue.data);
            }
            if (cachedDrawable != null) {
                drawableNewDrawable = cachedDrawable;
            } else {
                if (typedValue.string == null) {
                    throw new NotFoundException("Resource is not a Drawable (color or path): " + typedValue);
                }
                String string = typedValue.string.toString();
                if (string.endsWith(".xml")) {
                    Trace.traceBegin(Trace.TRACE_TAG_RESOURCES, string);
                    try {
                        XmlResourceParser xmlResourceParserLoadXmlResourceParser = loadXmlResourceParser(string, i, typedValue.assetCookie, "drawable");
                        drawableCreateFromResourceStream = Drawable.createFromXml(this, xmlResourceParserLoadXmlResourceParser);
                        xmlResourceParserLoadXmlResourceParser.close();
                        Trace.traceEnd(Trace.TRACE_TAG_RESOURCES);
                    } catch (Exception e) {
                        Trace.traceEnd(Trace.TRACE_TAG_RESOURCES);
                        NotFoundException notFoundException = new NotFoundException("File " + string + " from drawable resource ID #0x" + Integer.toHexString(i));
                        notFoundException.initCause(e);
                        throw notFoundException;
                    }
                } else {
                    Trace.traceBegin(Trace.TRACE_TAG_RESOURCES, string);
                    try {
                        InputStream inputStreamOpenNonAsset = this.mAssets.openNonAsset(typedValue.assetCookie, string, 2);
                        drawableCreateFromResourceStream = Drawable.createFromResourceStream(this, typedValue, inputStreamOpenNonAsset, string, null);
                        inputStreamOpenNonAsset.close();
                        Trace.traceEnd(Trace.TRACE_TAG_RESOURCES);
                    } catch (Exception e2) {
                        Trace.traceEnd(Trace.TRACE_TAG_RESOURCES);
                        NotFoundException notFoundException2 = new NotFoundException("File " + string + " from drawable resource ID #0x" + Integer.toHexString(i));
                        notFoundException2.initCause(e2);
                        throw notFoundException2;
                    }
                }
                drawableNewDrawable = drawableCreateFromResourceStream;
            }
        }
        if (drawableNewDrawable != null) {
            drawableNewDrawable.setChangingConfigurations(typedValue.changingConfigurations);
            Drawable.ConstantState constantState2 = drawableNewDrawable.getConstantState();
            if (constantState2 != null) {
                if (this.mPreloading) {
                    int changingConfigurations = constantState2.getChangingConfigurations();
                    if (z) {
                        if (verifyPreloadConfig(changingConfigurations, 0, typedValue.resourceId, "drawable")) {
                            sPreloadedColorDrawables.put(j, constantState2);
                        }
                    } else {
                        int i2 = LAYOUT_DIR_CONFIG;
                        if (verifyPreloadConfig(changingConfigurations, i2, typedValue.resourceId, "drawable")) {
                            if ((changingConfigurations & i2) == 0) {
                                LongSparseArray<Drawable.ConstantState>[] longSparseArrayArr = sPreloadedDrawables;
                                longSparseArrayArr[0].put(j, constantState2);
                                longSparseArrayArr[1].put(j, constantState2);
                            } else {
                                sPreloadedDrawables[this.mConfiguration.getLayoutDirection()].put(j, constantState2);
                            }
                        }
                    }
                } else {
                    synchronized (this.mAccessLock) {
                        if (z) {
                            this.mColorDrawableCache.put(j, new WeakReference<>(constantState2));
                        } else {
                            this.mDrawableCache.put(j, new WeakReference<>(constantState2));
                        }
                    }
                }
            }
        }
        return drawableNewDrawable;
    }

    private Drawable getCachedDrawable(LongSparseArray<WeakReference<Drawable.ConstantState>> longSparseArray, long j) {
        synchronized (this.mAccessLock) {
            WeakReference<Drawable.ConstantState> weakReference = longSparseArray.get(j);
            if (weakReference != null) {
                Drawable.ConstantState constantState = weakReference.get();
                if (constantState != null) {
                    return constantState.newDrawable(this);
                }
                longSparseArray.delete(j);
            }
            return null;
        }
    }

    ColorStateList loadColorStateList(TypedValue typedValue, int i) throws NotFoundException {
        long j = (((long) typedValue.assetCookie) << 32) | ((long) typedValue.data);
        if (typedValue.type >= 28 && typedValue.type <= 31) {
            LongSparseArray<ColorStateList> longSparseArray = sPreloadedColorStateLists;
            ColorStateList colorStateList = longSparseArray.get(j);
            if (colorStateList != null) {
                return colorStateList;
            }
            ColorStateList colorStateListValueOf = ColorStateList.valueOf(typedValue.data);
            if (this.mPreloading && verifyPreloadConfig(typedValue.changingConfigurations, 0, typedValue.resourceId, CalendarContract.ColorsColumns.COLOR)) {
                longSparseArray.put(j, colorStateListValueOf);
            }
            return colorStateListValueOf;
        }
        ColorStateList cachedColorStateList = getCachedColorStateList(j);
        if (cachedColorStateList != null) {
            return cachedColorStateList;
        }
        LongSparseArray<ColorStateList> longSparseArray2 = sPreloadedColorStateLists;
        ColorStateList colorStateList2 = longSparseArray2.get(j);
        if (colorStateList2 != null) {
            return colorStateList2;
        }
        if (typedValue.string == null) {
            throw new NotFoundException("Resource is not a ColorStateList (color or path): " + typedValue);
        }
        String string = typedValue.string.toString();
        if (string.endsWith(".xml")) {
            Trace.traceBegin(Trace.TRACE_TAG_RESOURCES, string);
            try {
                XmlResourceParser xmlResourceParserLoadXmlResourceParser = loadXmlResourceParser(string, i, typedValue.assetCookie, "colorstatelist");
                ColorStateList colorStateListCreateFromXml = ColorStateList.createFromXml(this, xmlResourceParserLoadXmlResourceParser);
                xmlResourceParserLoadXmlResourceParser.close();
                Trace.traceEnd(Trace.TRACE_TAG_RESOURCES);
                if (colorStateListCreateFromXml != null) {
                    if (this.mPreloading) {
                        if (verifyPreloadConfig(typedValue.changingConfigurations, 0, typedValue.resourceId, CalendarContract.ColorsColumns.COLOR)) {
                            longSparseArray2.put(j, colorStateListCreateFromXml);
                        }
                    } else {
                        synchronized (this.mAccessLock) {
                            this.mColorStateListCache.put(j, new WeakReference<>(colorStateListCreateFromXml));
                        }
                    }
                }
                return colorStateListCreateFromXml;
            } catch (Exception e) {
                Trace.traceEnd(Trace.TRACE_TAG_RESOURCES);
                NotFoundException notFoundException = new NotFoundException("File " + string + " from color state list resource ID #0x" + Integer.toHexString(i));
                notFoundException.initCause(e);
                throw notFoundException;
            }
        }
        throw new NotFoundException("File " + string + " from drawable resource ID #0x" + Integer.toHexString(i) + ": .xml extension required");
    }

    private ColorStateList getCachedColorStateList(long j) {
        synchronized (this.mAccessLock) {
            WeakReference<ColorStateList> weakReference = this.mColorStateListCache.get(j);
            if (weakReference != null) {
                ColorStateList colorStateList = weakReference.get();
                if (colorStateList != null) {
                    return colorStateList;
                }
                this.mColorStateListCache.delete(j);
            }
            return null;
        }
    }

    XmlResourceParser loadXmlResourceParser(int i, String str) throws NotFoundException {
        XmlResourceParser xmlResourceParserLoadXmlResourceParser;
        synchronized (this.mAccessLock) {
            TypedValue typedValue = this.mTmpValue;
            if (typedValue == null) {
                typedValue = new TypedValue();
                this.mTmpValue = typedValue;
            }
            getValue(i, typedValue, true);
            if (typedValue.type == 3) {
                xmlResourceParserLoadXmlResourceParser = loadXmlResourceParser(typedValue.string.toString(), i, typedValue.assetCookie, str);
            } else {
                throw new NotFoundException("Resource ID #0x" + Integer.toHexString(i) + " type #0x" + Integer.toHexString(typedValue.type) + " is not valid");
            }
        }
        return xmlResourceParserLoadXmlResourceParser;
    }

    XmlResourceParser loadXmlResourceParser(String str, int i, int i2, String str2) throws NotFoundException {
        if (i != 0) {
            try {
                synchronized (this.mCachedXmlBlockIds) {
                    int length = this.mCachedXmlBlockIds.length;
                    int i3 = 0;
                    for (int i4 = 0; i4 < length; i4++) {
                        if (this.mCachedXmlBlockIds[i4] == i) {
                            return this.mCachedXmlBlocks[i4].newParser();
                        }
                    }
                    XmlBlock xmlBlockOpenXmlBlockAsset = this.mAssets.openXmlBlockAsset(i2, str);
                    if (xmlBlockOpenXmlBlockAsset != null) {
                        int i5 = this.mLastCachedXmlBlockIndex + 1;
                        if (i5 < length) {
                            i3 = i5;
                        }
                        this.mLastCachedXmlBlockIndex = i3;
                        XmlBlock xmlBlock = this.mCachedXmlBlocks[i3];
                        if (xmlBlock != null) {
                            xmlBlock.close();
                        }
                        this.mCachedXmlBlockIds[i3] = i;
                        this.mCachedXmlBlocks[i3] = xmlBlockOpenXmlBlockAsset;
                        return xmlBlockOpenXmlBlockAsset.newParser();
                    }
                }
            } catch (Exception e) {
                NotFoundException notFoundException = new NotFoundException("File " + str + " from xml type " + str2 + " resource ID #0x" + Integer.toHexString(i));
                notFoundException.initCause(e);
                throw notFoundException;
            }
        }
        throw new NotFoundException("File " + str + " from xml type " + str2 + " resource ID #0x" + Integer.toHexString(i));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public TypedArray getCachedStyledAttributes(int i) {
        synchronized (this.mAccessLock) {
            TypedArray typedArray = this.mCachedStyledAttributes;
            if (typedArray != null) {
                this.mCachedStyledAttributes = null;
                typedArray.mLength = i;
                int i2 = i * 6;
                if (typedArray.mData.length >= i2) {
                    return typedArray;
                }
                typedArray.mData = new int[i2];
                typedArray.mIndices = new int[i + 1];
                return typedArray;
            }
            return new TypedArray(this, new int[i * 6], new int[i + 1], i);
        }
    }

    private Resources() {
        this.mAccessLock = new Object();
        this.mTmpConfig = new Configuration();
        this.mTmpValue = new TypedValue();
        this.mDrawableCache = new LongSparseArray<>(0);
        this.mColorStateListCache = new LongSparseArray<>(0);
        this.mColorDrawableCache = new LongSparseArray<>(0);
        this.mCachedStyledAttributes = null;
        this.mLastRetrievedAttrs = null;
        this.mLastCachedXmlBlockIndex = -1;
        this.mCachedXmlBlockIds = new int[]{0, 0, 0, 0};
        this.mCachedXmlBlocks = new XmlBlock[4];
        Configuration configuration = new Configuration();
        this.mConfiguration = configuration;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.mMetrics = displayMetrics;
        this.mCompatibilityInfo = CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO;
        AssetManager system = AssetManager.getSystem();
        this.mAssets = system;
        configuration.setToDefaults();
        displayMetrics.setToDefaults();
        updateConfiguration(null, null);
        system.ensureStringBlocks();
    }
}
