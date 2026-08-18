package android.text;

import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.provider.UserDictionary;
import android.view.View;
import com.android.internal.util.XmlUtils;
import java.io.IOException;
import java.util.Locale;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes.dex */
public class AutoText {
    private static final int DEFAULT = 14337;
    private static final int INCREMENT = 1024;
    private static final int RIGHT = 9300;
    private static final int TRIE_C = 0;
    private static final int TRIE_CHILD = 2;
    private static final int TRIE_NEXT = 3;
    private static final char TRIE_NULL = 65535;
    private static final int TRIE_OFF = 1;
    private static final int TRIE_ROOT = 0;
    private static final int TRIE_SIZEOF = 4;
    private static AutoText sInstance = new AutoText(Resources.getSystem());
    private static Object sLock = new Object();
    private Locale mLocale;
    private int mSize;
    private String mText;
    private char[] mTrie;
    private char mTrieUsed;

    private AutoText(Resources resources) {
        this.mLocale = resources.getConfiguration().locale;
        init(resources);
    }

    private static AutoText getInstance(View view) {
        AutoText autoText;
        Resources resources = view.getContext().getResources();
        Locale locale = resources.getConfiguration().locale;
        synchronized (sLock) {
            autoText = sInstance;
            if (!locale.equals(autoText.mLocale)) {
                autoText = new AutoText(resources);
                sInstance = autoText;
            }
        }
        return autoText;
    }

    public static String get(CharSequence charSequence, int i, int i2, View view) {
        return getInstance(view).lookup(charSequence, i, i2);
    }

    public static int getSize(View view) {
        return getInstance(view).getSize();
    }

    private int getSize() {
        return this.mSize;
    }

    private String lookup(CharSequence charSequence, int i, int i2) {
        char c = this.mTrie[0];
        while (i < i2) {
            char cCharAt = charSequence.charAt(i);
            while (true) {
                if (c == 65535) {
                    break;
                }
                char[] cArr = this.mTrie;
                if (cCharAt != cArr[c + 0]) {
                    c = cArr[c + 3];
                } else {
                    if (i == i2 - 1) {
                        int i3 = c + 1;
                        if (cArr[i3] != 65535) {
                            char c2 = cArr[i3];
                            char cCharAt2 = this.mText.charAt(c2);
                            int i4 = c2 + 1;
                            return this.mText.substring(i4, cCharAt2 + i4);
                        }
                    }
                    c = cArr[c + 2];
                }
            }
            if (c == 65535) {
                return null;
            }
            i++;
        }
        return null;
    }

    private void init(Resources resources) {
        char length;
        XmlResourceParser xml = resources.getXml(17760258);
        StringBuilder sb = new StringBuilder(RIGHT);
        char[] cArr = new char[14337];
        this.mTrie = cArr;
        cArr[0] = TRIE_NULL;
        this.mTrieUsed = (char) 1;
        try {
            try {
                try {
                    XmlUtils.beginDocument(xml, "words");
                    while (true) {
                        XmlUtils.nextElement(xml);
                        String name = xml.getName();
                        if (name == null || !name.equals(UserDictionary.Words.WORD)) {
                            break;
                        }
                        String attributeValue = xml.getAttributeValue(null, "src");
                        if (xml.next() == 4) {
                            String text = xml.getText();
                            if (text.equals("")) {
                                length = 0;
                            } else {
                                length = (char) sb.length();
                                sb.append((char) text.length());
                                sb.append(text);
                            }
                            add(attributeValue, length);
                        }
                    }
                    resources.flushLayoutCache();
                    xml.close();
                    this.mText = sb.toString();
                } catch (XmlPullParserException e) {
                    throw new RuntimeException(e);
                }
            } catch (IOException e2) {
                throw new RuntimeException(e2);
            }
        } catch (Throwable th) {
            xml.close();
            throw th;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:16:0x0039, code lost:
    
        if (r6 != false) goto L28;
     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x003b, code lost:
    
        r6 = newTrieNode();
        r7 = r9.mTrie;
        r7[r4] = r6;
        r7[r7[r4] + 0] = r5;
        r7[r7[r4] + 1] = android.text.AutoText.TRIE_NULL;
        r7[r7[r4] + 3] = android.text.AutoText.TRIE_NULL;
        r7[r7[r4] + 2] = android.text.AutoText.TRIE_NULL;
     */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x005b, code lost:
    
        if (r3 != (r0 - 1)) goto L21;
     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x005d, code lost:
    
        r7[r7[r4] + 1] = r11;
     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x0062, code lost:
    
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x0063, code lost:
    
        r4 = r7[r4] + 2;
     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x0067, code lost:
    
        r3 = r3 + 1;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void add(java.lang.String r10, char r11) {
        /*
            r9 = this;
            int r0 = r10.length()
            int r1 = r9.mSize
            r2 = 1
            int r1 = r1 + r2
            r9.mSize = r1
            r1 = 0
            r3 = r1
            r4 = r3
        Ld:
            if (r3 >= r0) goto L6a
            char r5 = r10.charAt(r3)
        L13:
            char[] r6 = r9.mTrie
            char r7 = r6[r4]
            r8 = 65535(0xffff, float:9.1834E-41)
            if (r7 == r8) goto L38
            char r7 = r6[r4]
            int r7 = r7 + r1
            char r7 = r6[r7]
            if (r5 != r7) goto L33
            int r7 = r0 + (-1)
            if (r3 != r7) goto L2d
            char r10 = r6[r4]
            int r10 = r10 + r2
            r6[r10] = r11
            return
        L2d:
            char r4 = r6[r4]
            int r4 = r4 + 2
            r6 = r2
            goto L39
        L33:
            char r4 = r6[r4]
            int r4 = r4 + 3
            goto L13
        L38:
            r6 = r1
        L39:
            if (r6 != 0) goto L67
            char r6 = r9.newTrieNode()
            char[] r7 = r9.mTrie
            r7[r4] = r6
            char r6 = r7[r4]
            int r6 = r6 + r1
            r7[r6] = r5
            char r5 = r7[r4]
            int r5 = r5 + r2
            r7[r5] = r8
            char r5 = r7[r4]
            int r5 = r5 + 3
            r7[r5] = r8
            char r5 = r7[r4]
            int r5 = r5 + 2
            r7[r5] = r8
            int r5 = r0 + (-1)
            if (r3 != r5) goto L63
            char r10 = r7[r4]
            int r10 = r10 + r2
            r7[r10] = r11
            return
        L63:
            char r4 = r7[r4]
            int r4 = r4 + 2
        L67:
            int r3 = r3 + 1
            goto Ld
        L6a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.text.AutoText.add(java.lang.String, char):void");
    }

    private char newTrieNode() {
        int i = this.mTrieUsed + 4;
        char[] cArr = this.mTrie;
        if (i > cArr.length) {
            char[] cArr2 = new char[cArr.length + 1024];
            System.arraycopy(cArr, 0, cArr2, 0, cArr.length);
            this.mTrie = cArr2;
        }
        char c = this.mTrieUsed;
        this.mTrieUsed = (char) (c + 4);
        return c;
    }
}
