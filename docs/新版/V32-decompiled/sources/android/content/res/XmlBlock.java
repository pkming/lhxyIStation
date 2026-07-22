package android.content.res;

import android.app.Instrumentation;
import android.util.TypedValue;
import com.android.internal.util.XmlUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes.dex */
final class XmlBlock {
    private static final boolean DEBUG = false;
    private final AssetManager mAssets;
    private final int mNative;
    private boolean mOpen;
    private int mOpenCount;
    final StringBlock mStrings;

    private static final native int nativeCreate(byte[] bArr, int i, int i2);

    private static final native int nativeCreateParseState(int i);

    private static final native void nativeDestroy(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public static final native void nativeDestroyParseState(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public static final native int nativeGetAttributeCount(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public static final native int nativeGetAttributeData(int i, int i2);

    /* JADX INFO: Access modifiers changed from: private */
    public static final native int nativeGetAttributeDataType(int i, int i2);

    /* JADX INFO: Access modifiers changed from: private */
    public static final native int nativeGetAttributeIndex(int i, String str, String str2);

    /* JADX INFO: Access modifiers changed from: private */
    public static final native int nativeGetAttributeName(int i, int i2);

    /* JADX INFO: Access modifiers changed from: private */
    public static final native int nativeGetAttributeNamespace(int i, int i2);

    /* JADX INFO: Access modifiers changed from: private */
    public static final native int nativeGetAttributeResource(int i, int i2);

    /* JADX INFO: Access modifiers changed from: private */
    public static final native int nativeGetAttributeStringValue(int i, int i2);

    /* JADX INFO: Access modifiers changed from: private */
    public static final native int nativeGetClassAttribute(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public static final native int nativeGetIdAttribute(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public static final native int nativeGetLineNumber(int i);

    static final native int nativeGetName(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public static final native int nativeGetNamespace(int i);

    private static final native int nativeGetStringBlock(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public static final native int nativeGetStyleAttribute(int i);

    /* JADX INFO: Access modifiers changed from: private */
    public static final native int nativeGetText(int i);

    static final native int nativeNext(int i);

    static /* synthetic */ int access$008(XmlBlock xmlBlock) {
        int i = xmlBlock.mOpenCount;
        xmlBlock.mOpenCount = i + 1;
        return i;
    }

    public XmlBlock(byte[] bArr) {
        this.mOpen = true;
        this.mOpenCount = 1;
        this.mAssets = null;
        int iNativeCreate = nativeCreate(bArr, 0, bArr.length);
        this.mNative = iNativeCreate;
        this.mStrings = new StringBlock(nativeGetStringBlock(iNativeCreate), false);
    }

    public XmlBlock(byte[] bArr, int i, int i2) {
        this.mOpen = true;
        this.mOpenCount = 1;
        this.mAssets = null;
        int iNativeCreate = nativeCreate(bArr, i, i2);
        this.mNative = iNativeCreate;
        this.mStrings = new StringBlock(nativeGetStringBlock(iNativeCreate), false);
    }

    public void close() {
        synchronized (this) {
            if (this.mOpen) {
                this.mOpen = false;
                decOpenCountLocked();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void decOpenCountLocked() {
        int i = this.mOpenCount - 1;
        this.mOpenCount = i;
        if (i == 0) {
            nativeDestroy(this.mNative);
            AssetManager assetManager = this.mAssets;
            if (assetManager != null) {
                assetManager.xmlBlockGone(hashCode());
            }
        }
    }

    public XmlResourceParser newParser() {
        synchronized (this) {
            if (this.mNative == 0) {
                return null;
            }
            return new Parser(nativeCreateParseState(this.mNative), this);
        }
    }

    final class Parser implements XmlResourceParser {
        private final XmlBlock mBlock;
        int mParseState;
        private boolean mStarted = false;
        private boolean mDecNextDepth = false;
        private int mDepth = 0;
        private int mEventType = 0;

        @Override // org.xmlpull.v1.XmlPullParser
        public String getAttributeType(int i) {
            return "CDATA";
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public int getColumnNumber() {
            return -1;
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public String getInputEncoding() {
            return null;
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public Object getProperty(String str) {
            return null;
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public boolean isAttributeDefault(int i) {
            return false;
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public boolean isEmptyElementTag() throws XmlPullParserException {
            return false;
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public boolean isWhitespace() throws XmlPullParserException {
            return false;
        }

        Parser(int i, XmlBlock xmlBlock) {
            this.mParseState = i;
            this.mBlock = xmlBlock;
            XmlBlock.access$008(xmlBlock);
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public void setFeature(String str, boolean z) throws XmlPullParserException {
            if ("http://xmlpull.org/v1/doc/features.html#process-namespaces".equals(str) && z) {
                return;
            }
            if (!"http://xmlpull.org/v1/doc/features.html#report-namespace-prefixes".equals(str) || !z) {
                throw new XmlPullParserException("Unsupported feature: " + str);
            }
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public boolean getFeature(String str) {
            return "http://xmlpull.org/v1/doc/features.html#process-namespaces".equals(str) || "http://xmlpull.org/v1/doc/features.html#report-namespace-prefixes".equals(str);
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public void setProperty(String str, Object obj) throws XmlPullParserException {
            throw new XmlPullParserException("setProperty() not supported");
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public void setInput(Reader reader) throws XmlPullParserException {
            throw new XmlPullParserException("setInput() not supported");
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public void setInput(InputStream inputStream, String str) throws XmlPullParserException {
            throw new XmlPullParserException("setInput() not supported");
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public void defineEntityReplacementText(String str, String str2) throws XmlPullParserException {
            throw new XmlPullParserException("defineEntityReplacementText() not supported");
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public String getNamespacePrefix(int i) throws XmlPullParserException {
            throw new XmlPullParserException("getNamespacePrefix() not supported");
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public String getNamespace(String str) {
            throw new RuntimeException("getNamespace() not supported");
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public int getNamespaceCount(int i) throws XmlPullParserException {
            throw new XmlPullParserException("getNamespaceCount() not supported");
        }

        @Override // org.xmlpull.v1.XmlPullParser, android.util.AttributeSet
        public String getPositionDescription() {
            return "Binary XML file line #" + getLineNumber();
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public String getNamespaceUri(int i) throws XmlPullParserException {
            throw new XmlPullParserException("getNamespaceUri() not supported");
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public int getDepth() {
            return this.mDepth;
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public String getText() {
            int iNativeGetText = XmlBlock.nativeGetText(this.mParseState);
            if (iNativeGetText >= 0) {
                return XmlBlock.this.mStrings.get(iNativeGetText).toString();
            }
            return null;
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public int getLineNumber() {
            return XmlBlock.nativeGetLineNumber(this.mParseState);
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public int getEventType() throws XmlPullParserException {
            return this.mEventType;
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public String getPrefix() {
            throw new RuntimeException("getPrefix not supported");
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public char[] getTextCharacters(int[] iArr) {
            String text = getText();
            if (text == null) {
                return null;
            }
            iArr[0] = 0;
            iArr[1] = text.length();
            char[] cArr = new char[text.length()];
            text.getChars(0, text.length(), cArr, 0);
            return cArr;
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public String getNamespace() {
            int iNativeGetNamespace = XmlBlock.nativeGetNamespace(this.mParseState);
            return iNativeGetNamespace >= 0 ? XmlBlock.this.mStrings.get(iNativeGetNamespace).toString() : "";
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public String getName() {
            int iNativeGetName = XmlBlock.nativeGetName(this.mParseState);
            if (iNativeGetName >= 0) {
                return XmlBlock.this.mStrings.get(iNativeGetName).toString();
            }
            return null;
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public String getAttributeNamespace(int i) {
            int iNativeGetAttributeNamespace = XmlBlock.nativeGetAttributeNamespace(this.mParseState, i);
            if (iNativeGetAttributeNamespace >= 0) {
                return XmlBlock.this.mStrings.get(iNativeGetAttributeNamespace).toString();
            }
            if (iNativeGetAttributeNamespace == -1) {
                return "";
            }
            throw new IndexOutOfBoundsException(String.valueOf(i));
        }

        @Override // org.xmlpull.v1.XmlPullParser, android.util.AttributeSet
        public String getAttributeName(int i) {
            int iNativeGetAttributeName = XmlBlock.nativeGetAttributeName(this.mParseState, i);
            if (iNativeGetAttributeName >= 0) {
                return XmlBlock.this.mStrings.get(iNativeGetAttributeName).toString();
            }
            throw new IndexOutOfBoundsException(String.valueOf(i));
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public String getAttributePrefix(int i) {
            throw new RuntimeException("getAttributePrefix not supported");
        }

        @Override // org.xmlpull.v1.XmlPullParser, android.util.AttributeSet
        public int getAttributeCount() {
            if (this.mEventType == 2) {
                return XmlBlock.nativeGetAttributeCount(this.mParseState);
            }
            return -1;
        }

        @Override // org.xmlpull.v1.XmlPullParser, android.util.AttributeSet
        public String getAttributeValue(int i) {
            int iNativeGetAttributeStringValue = XmlBlock.nativeGetAttributeStringValue(this.mParseState, i);
            if (iNativeGetAttributeStringValue < 0) {
                int iNativeGetAttributeDataType = XmlBlock.nativeGetAttributeDataType(this.mParseState, i);
                if (iNativeGetAttributeDataType == 0) {
                    throw new IndexOutOfBoundsException(String.valueOf(i));
                }
                return TypedValue.coerceToString(iNativeGetAttributeDataType, XmlBlock.nativeGetAttributeData(this.mParseState, i));
            }
            return XmlBlock.this.mStrings.get(iNativeGetAttributeStringValue).toString();
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public int nextToken() throws XmlPullParserException, IOException {
            return next();
        }

        @Override // org.xmlpull.v1.XmlPullParser, android.util.AttributeSet
        public String getAttributeValue(String str, String str2) {
            int iNativeGetAttributeIndex = XmlBlock.nativeGetAttributeIndex(this.mParseState, str, str2);
            if (iNativeGetAttributeIndex >= 0) {
                return getAttributeValue(iNativeGetAttributeIndex);
            }
            return null;
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public int next() throws XmlPullParserException, IOException {
            if (!this.mStarted) {
                this.mStarted = true;
                return 0;
            }
            int i = this.mParseState;
            if (i == 0) {
                return 1;
            }
            int iNativeNext = XmlBlock.nativeNext(i);
            if (this.mDecNextDepth) {
                this.mDepth--;
                this.mDecNextDepth = false;
            }
            if (iNativeNext == 2) {
                this.mDepth++;
            } else if (iNativeNext == 3) {
                this.mDecNextDepth = true;
            }
            this.mEventType = iNativeNext;
            if (iNativeNext == 1) {
                close();
            }
            return iNativeNext;
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public void require(int i, String str, String str2) throws XmlPullParserException, IOException {
            if (i != getEventType() || ((str != null && !str.equals(getNamespace())) || (str2 != null && !str2.equals(getName())))) {
                throw new XmlPullParserException("expected " + TYPES[i] + getPositionDescription());
            }
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public String nextText() throws XmlPullParserException, IOException {
            if (getEventType() != 2) {
                throw new XmlPullParserException(getPositionDescription() + ": parser must be on START_TAG to read next text", this, null);
            }
            int next = next();
            if (next != 4) {
                if (next == 3) {
                    return "";
                }
                throw new XmlPullParserException(getPositionDescription() + ": parser must be on START_TAG or TEXT to read text", this, null);
            }
            String text = getText();
            if (next() == 3) {
                return text;
            }
            throw new XmlPullParserException(getPositionDescription() + ": event TEXT it must be immediately followed by END_TAG", this, null);
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public int nextTag() throws XmlPullParserException, IOException {
            int next = next();
            if (next == 4 && isWhitespace()) {
                next = next();
            }
            if (next == 2 || next == 3) {
                return next;
            }
            throw new XmlPullParserException(getPositionDescription() + ": expected start or end tag", this, null);
        }

        @Override // android.util.AttributeSet
        public int getAttributeNameResource(int i) {
            return XmlBlock.nativeGetAttributeResource(this.mParseState, i);
        }

        @Override // android.util.AttributeSet
        public int getAttributeListValue(String str, String str2, String[] strArr, int i) {
            int iNativeGetAttributeIndex = XmlBlock.nativeGetAttributeIndex(this.mParseState, str, str2);
            return iNativeGetAttributeIndex >= 0 ? getAttributeListValue(iNativeGetAttributeIndex, strArr, i) : i;
        }

        @Override // android.util.AttributeSet
        public boolean getAttributeBooleanValue(String str, String str2, boolean z) {
            int iNativeGetAttributeIndex = XmlBlock.nativeGetAttributeIndex(this.mParseState, str, str2);
            return iNativeGetAttributeIndex >= 0 ? getAttributeBooleanValue(iNativeGetAttributeIndex, z) : z;
        }

        @Override // android.util.AttributeSet
        public int getAttributeResourceValue(String str, String str2, int i) {
            int iNativeGetAttributeIndex = XmlBlock.nativeGetAttributeIndex(this.mParseState, str, str2);
            return iNativeGetAttributeIndex >= 0 ? getAttributeResourceValue(iNativeGetAttributeIndex, i) : i;
        }

        @Override // android.util.AttributeSet
        public int getAttributeIntValue(String str, String str2, int i) {
            int iNativeGetAttributeIndex = XmlBlock.nativeGetAttributeIndex(this.mParseState, str, str2);
            return iNativeGetAttributeIndex >= 0 ? getAttributeIntValue(iNativeGetAttributeIndex, i) : i;
        }

        @Override // android.util.AttributeSet
        public int getAttributeUnsignedIntValue(String str, String str2, int i) {
            int iNativeGetAttributeIndex = XmlBlock.nativeGetAttributeIndex(this.mParseState, str, str2);
            return iNativeGetAttributeIndex >= 0 ? getAttributeUnsignedIntValue(iNativeGetAttributeIndex, i) : i;
        }

        @Override // android.util.AttributeSet
        public float getAttributeFloatValue(String str, String str2, float f) {
            int iNativeGetAttributeIndex = XmlBlock.nativeGetAttributeIndex(this.mParseState, str, str2);
            return iNativeGetAttributeIndex >= 0 ? getAttributeFloatValue(iNativeGetAttributeIndex, f) : f;
        }

        @Override // android.util.AttributeSet
        public int getAttributeListValue(int i, String[] strArr, int i2) {
            int iNativeGetAttributeDataType = XmlBlock.nativeGetAttributeDataType(this.mParseState, i);
            int iNativeGetAttributeData = XmlBlock.nativeGetAttributeData(this.mParseState, i);
            return iNativeGetAttributeDataType == 3 ? XmlUtils.convertValueToList(XmlBlock.this.mStrings.get(iNativeGetAttributeData), strArr, i2) : iNativeGetAttributeData;
        }

        @Override // android.util.AttributeSet
        public boolean getAttributeBooleanValue(int i, boolean z) {
            int iNativeGetAttributeDataType = XmlBlock.nativeGetAttributeDataType(this.mParseState, i);
            return (iNativeGetAttributeDataType < 16 || iNativeGetAttributeDataType > 31) ? z : XmlBlock.nativeGetAttributeData(this.mParseState, i) != 0;
        }

        @Override // android.util.AttributeSet
        public int getAttributeResourceValue(int i, int i2) {
            return XmlBlock.nativeGetAttributeDataType(this.mParseState, i) == 1 ? XmlBlock.nativeGetAttributeData(this.mParseState, i) : i2;
        }

        @Override // android.util.AttributeSet
        public int getAttributeIntValue(int i, int i2) {
            int iNativeGetAttributeDataType = XmlBlock.nativeGetAttributeDataType(this.mParseState, i);
            return (iNativeGetAttributeDataType < 16 || iNativeGetAttributeDataType > 31) ? i2 : XmlBlock.nativeGetAttributeData(this.mParseState, i);
        }

        @Override // android.util.AttributeSet
        public int getAttributeUnsignedIntValue(int i, int i2) {
            int iNativeGetAttributeDataType = XmlBlock.nativeGetAttributeDataType(this.mParseState, i);
            return (iNativeGetAttributeDataType < 16 || iNativeGetAttributeDataType > 31) ? i2 : XmlBlock.nativeGetAttributeData(this.mParseState, i);
        }

        @Override // android.util.AttributeSet
        public float getAttributeFloatValue(int i, float f) {
            if (XmlBlock.nativeGetAttributeDataType(this.mParseState, i) == 4) {
                return Float.intBitsToFloat(XmlBlock.nativeGetAttributeData(this.mParseState, i));
            }
            throw new RuntimeException("not a float!");
        }

        @Override // android.util.AttributeSet
        public String getIdAttribute() {
            int iNativeGetIdAttribute = XmlBlock.nativeGetIdAttribute(this.mParseState);
            if (iNativeGetIdAttribute >= 0) {
                return XmlBlock.this.mStrings.get(iNativeGetIdAttribute).toString();
            }
            return null;
        }

        @Override // android.util.AttributeSet
        public String getClassAttribute() {
            int iNativeGetClassAttribute = XmlBlock.nativeGetClassAttribute(this.mParseState);
            if (iNativeGetClassAttribute >= 0) {
                return XmlBlock.this.mStrings.get(iNativeGetClassAttribute).toString();
            }
            return null;
        }

        @Override // android.util.AttributeSet
        public int getIdAttributeResourceValue(int i) {
            return getAttributeResourceValue(null, Instrumentation.REPORT_KEY_IDENTIFIER, i);
        }

        @Override // android.util.AttributeSet
        public int getStyleAttribute() {
            return XmlBlock.nativeGetStyleAttribute(this.mParseState);
        }

        @Override // android.content.res.XmlResourceParser, java.lang.AutoCloseable
        public void close() {
            synchronized (this.mBlock) {
                int i = this.mParseState;
                if (i != 0) {
                    XmlBlock.nativeDestroyParseState(i);
                    this.mParseState = 0;
                    this.mBlock.decOpenCountLocked();
                }
            }
        }

        protected void finalize() throws Throwable {
            close();
        }

        final CharSequence getPooledString(int i) {
            return XmlBlock.this.mStrings.get(i);
        }
    }

    protected void finalize() throws Throwable {
        close();
    }

    XmlBlock(AssetManager assetManager, int i) {
        this.mOpen = true;
        this.mOpenCount = 1;
        this.mAssets = assetManager;
        this.mNative = i;
        this.mStrings = new StringBlock(nativeGetStringBlock(i), false);
    }
}
