package android.text;

import android.telephony.PhoneNumberUtils;
import java.util.Locale;
import org.apache.poi.hssf.record.formula.GreaterThanPtg;

/* JADX INFO: loaded from: classes.dex */
public final class BidiFormatter {
    private static final int DEFAULT_FLAGS = 2;
    private static final int DIR_LTR = -1;
    private static final int DIR_RTL = 1;
    private static final int DIR_UNKNOWN = 0;
    private static final String EMPTY_STRING = "";
    private static final int FLAG_STEREO_RESET = 2;
    private static final char LRE = 8234;
    private static final char PDF = 8236;
    private static final char RLE = 8235;
    private final TextDirectionHeuristic mDefaultTextDirectionHeuristic;
    private final int mFlags;
    private final boolean mIsRtlContext;
    private static TextDirectionHeuristic DEFAULT_TEXT_DIRECTION_HEURISTIC = TextDirectionHeuristics.FIRSTSTRONG_LTR;
    private static final char LRM = 8206;
    private static final String LRM_STRING = Character.toString(LRM);
    private static final char RLM = 8207;
    private static final String RLM_STRING = Character.toString(RLM);
    private static final BidiFormatter DEFAULT_LTR_INSTANCE = new BidiFormatter(false, 2, DEFAULT_TEXT_DIRECTION_HEURISTIC);
    private static final BidiFormatter DEFAULT_RTL_INSTANCE = new BidiFormatter(true, 2, DEFAULT_TEXT_DIRECTION_HEURISTIC);

    public static final class Builder {
        private int mFlags;
        private boolean mIsRtlContext;
        private TextDirectionHeuristic mTextDirectionHeuristic;

        public Builder() {
            initialize(BidiFormatter.isRtlLocale(Locale.getDefault()));
        }

        public Builder(boolean z) {
            initialize(z);
        }

        public Builder(Locale locale) {
            initialize(BidiFormatter.isRtlLocale(locale));
        }

        private void initialize(boolean z) {
            this.mIsRtlContext = z;
            this.mTextDirectionHeuristic = BidiFormatter.DEFAULT_TEXT_DIRECTION_HEURISTIC;
            this.mFlags = 2;
        }

        public Builder stereoReset(boolean z) {
            if (z) {
                this.mFlags |= 2;
            } else {
                this.mFlags &= -3;
            }
            return this;
        }

        public Builder setTextDirectionHeuristic(TextDirectionHeuristic textDirectionHeuristic) {
            this.mTextDirectionHeuristic = textDirectionHeuristic;
            return this;
        }

        private static BidiFormatter getDefaultInstanceFromContext(boolean z) {
            return z ? BidiFormatter.DEFAULT_RTL_INSTANCE : BidiFormatter.DEFAULT_LTR_INSTANCE;
        }

        public BidiFormatter build() {
            if (this.mFlags == 2 && this.mTextDirectionHeuristic == BidiFormatter.DEFAULT_TEXT_DIRECTION_HEURISTIC) {
                return getDefaultInstanceFromContext(this.mIsRtlContext);
            }
            return new BidiFormatter(this.mIsRtlContext, this.mFlags, this.mTextDirectionHeuristic);
        }
    }

    public static BidiFormatter getInstance() {
        return new Builder().build();
    }

    public static BidiFormatter getInstance(boolean z) {
        return new Builder(z).build();
    }

    public static BidiFormatter getInstance(Locale locale) {
        return new Builder(locale).build();
    }

    private BidiFormatter(boolean z, int i, TextDirectionHeuristic textDirectionHeuristic) {
        this.mIsRtlContext = z;
        this.mFlags = i;
        this.mDefaultTextDirectionHeuristic = textDirectionHeuristic;
    }

    public boolean isRtlContext() {
        return this.mIsRtlContext;
    }

    public boolean getStereoReset() {
        return (this.mFlags & 2) != 0;
    }

    public String markAfter(String str, TextDirectionHeuristic textDirectionHeuristic) {
        boolean zIsRtl = textDirectionHeuristic.isRtl(str, 0, str.length());
        if (this.mIsRtlContext || !(zIsRtl || getExitDir(str) == 1)) {
            return this.mIsRtlContext ? (!zIsRtl || getExitDir(str) == -1) ? RLM_STRING : "" : "";
        }
        return LRM_STRING;
    }

    public String markBefore(String str, TextDirectionHeuristic textDirectionHeuristic) {
        boolean zIsRtl = textDirectionHeuristic.isRtl(str, 0, str.length());
        if (this.mIsRtlContext || !(zIsRtl || getEntryDir(str) == 1)) {
            return this.mIsRtlContext ? (!zIsRtl || getEntryDir(str) == -1) ? RLM_STRING : "" : "";
        }
        return LRM_STRING;
    }

    public boolean isRtl(String str) {
        return this.mDefaultTextDirectionHeuristic.isRtl(str, 0, str.length());
    }

    public String unicodeWrap(String str, TextDirectionHeuristic textDirectionHeuristic, boolean z) {
        boolean zIsRtl = textDirectionHeuristic.isRtl(str, 0, str.length());
        StringBuilder sb = new StringBuilder();
        if (getStereoReset() && z) {
            sb.append(markBefore(str, zIsRtl ? TextDirectionHeuristics.RTL : TextDirectionHeuristics.LTR));
        }
        if (zIsRtl != this.mIsRtlContext) {
            sb.append(zIsRtl ? RLE : LRE);
            sb.append(str);
            sb.append(PDF);
        } else {
            sb.append(str);
        }
        if (z) {
            sb.append(markAfter(str, zIsRtl ? TextDirectionHeuristics.RTL : TextDirectionHeuristics.LTR));
        }
        return sb.toString();
    }

    public String unicodeWrap(String str, TextDirectionHeuristic textDirectionHeuristic) {
        return unicodeWrap(str, textDirectionHeuristic, true);
    }

    public String unicodeWrap(String str, boolean z) {
        return unicodeWrap(str, this.mDefaultTextDirectionHeuristic, z);
    }

    public String unicodeWrap(String str) {
        return unicodeWrap(str, this.mDefaultTextDirectionHeuristic, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isRtlLocale(Locale locale) {
        return TextUtils.getLayoutDirectionFromLocale(locale) == 1;
    }

    private static int getExitDir(String str) {
        return new DirectionalityEstimator(str, false).getExitDir();
    }

    private static int getEntryDir(String str) {
        return new DirectionalityEstimator(str, false).getEntryDir();
    }

    private static class DirectionalityEstimator {
        private static final byte[] DIR_TYPE_CACHE = new byte[1792];
        private static final int DIR_TYPE_CACHE_SIZE = 1792;
        private int charIndex;
        private final boolean isHtml;
        private char lastChar;
        private final int length;
        private final String text;

        static {
            for (int i = 0; i < 1792; i++) {
                DIR_TYPE_CACHE[i] = Character.getDirectionality(i);
            }
        }

        DirectionalityEstimator(String str, boolean z) {
            this.text = str;
            this.isHtml = z;
            this.length = str.length();
        }

        int getEntryDir() {
            this.charIndex = 0;
            int i = 0;
            int i2 = 0;
            int i3 = 0;
            while (this.charIndex < this.length && i == 0) {
                byte bDirTypeForward = dirTypeForward();
                if (bDirTypeForward != 0) {
                    if (bDirTypeForward == 1 || bDirTypeForward == 2) {
                        if (i3 == 0) {
                            return 1;
                        }
                    } else if (bDirTypeForward != 9) {
                        switch (bDirTypeForward) {
                            case 14:
                            case 15:
                                i3++;
                                i2 = -1;
                                continue;
                            case 16:
                            case 17:
                                i3++;
                                i2 = 1;
                                continue;
                            case 18:
                                i3--;
                                i2 = 0;
                                continue;
                        }
                    }
                } else if (i3 == 0) {
                    return -1;
                }
                i = i3;
            }
            if (i == 0) {
                return 0;
            }
            if (i2 != 0) {
                return i2;
            }
            while (this.charIndex > 0) {
                switch (dirTypeBackward()) {
                    case 14:
                    case 15:
                        if (i == i3) {
                            return -1;
                        }
                        break;
                    case 16:
                    case 17:
                        if (i == i3) {
                            return 1;
                        }
                        break;
                    case 18:
                        i3++;
                        continue;
                }
                i3--;
            }
            return 0;
        }

        int getExitDir() {
            this.charIndex = this.length;
            int i = 0;
            while (true) {
                int i2 = i;
                while (this.charIndex > 0) {
                    byte bDirTypeBackward = dirTypeBackward();
                    if (bDirTypeBackward == 0) {
                        if (i == 0) {
                            return -1;
                        }
                        if (i2 == 0) {
                            break;
                        }
                    } else if (bDirTypeBackward == 1 || bDirTypeBackward == 2) {
                        if (i == 0) {
                            return 1;
                        }
                        if (i2 == 0) {
                            break;
                        }
                    } else if (bDirTypeBackward != 9) {
                        switch (bDirTypeBackward) {
                            case 14:
                            case 15:
                                if (i2 == i) {
                                    return -1;
                                }
                                i--;
                                break;
                            case 16:
                            case 17:
                                if (i2 == i) {
                                    return 1;
                                }
                                i--;
                                break;
                            case 18:
                                i++;
                                break;
                            default:
                                if (i2 != 0) {
                                }
                                break;
                        }
                    } else {
                        continue;
                    }
                }
                return 0;
            }
        }

        private static byte getCachedDirectionality(char c) {
            return c < 1792 ? DIR_TYPE_CACHE[c] : Character.getDirectionality(c);
        }

        byte dirTypeForward() {
            char cCharAt = this.text.charAt(this.charIndex);
            this.lastChar = cCharAt;
            if (Character.isHighSurrogate(cCharAt)) {
                int iCodePointAt = Character.codePointAt(this.text, this.charIndex);
                this.charIndex += Character.charCount(iCodePointAt);
                return Character.getDirectionality(iCodePointAt);
            }
            this.charIndex++;
            byte cachedDirectionality = getCachedDirectionality(this.lastChar);
            if (!this.isHtml) {
                return cachedDirectionality;
            }
            char c = this.lastChar;
            if (c == '<') {
                return skipTagForward();
            }
            return c == '&' ? skipEntityForward() : cachedDirectionality;
        }

        byte dirTypeBackward() {
            char cCharAt = this.text.charAt(this.charIndex - 1);
            this.lastChar = cCharAt;
            if (Character.isLowSurrogate(cCharAt)) {
                int iCodePointBefore = Character.codePointBefore(this.text, this.charIndex);
                this.charIndex -= Character.charCount(iCodePointBefore);
                return Character.getDirectionality(iCodePointBefore);
            }
            this.charIndex--;
            byte cachedDirectionality = getCachedDirectionality(this.lastChar);
            if (!this.isHtml) {
                return cachedDirectionality;
            }
            char c = this.lastChar;
            if (c == '>') {
                return skipTagBackward();
            }
            return c == ';' ? skipEntityBackward() : cachedDirectionality;
        }

        private byte skipTagForward() {
            char cCharAt;
            int i = this.charIndex;
            while (true) {
                int i2 = this.charIndex;
                if (i2 < this.length) {
                    String str = this.text;
                    this.charIndex = i2 + 1;
                    char cCharAt2 = str.charAt(i2);
                    this.lastChar = cCharAt2;
                    if (cCharAt2 == '>') {
                        return (byte) 12;
                    }
                    if (cCharAt2 == '\"' || cCharAt2 == '\'') {
                        do {
                            int i3 = this.charIndex;
                            if (i3 < this.length) {
                                String str2 = this.text;
                                this.charIndex = i3 + 1;
                                cCharAt = str2.charAt(i3);
                                this.lastChar = cCharAt;
                            }
                        } while (cCharAt != cCharAt2);
                    }
                } else {
                    this.charIndex = i;
                    this.lastChar = '<';
                    return GreaterThanPtg.sid;
                }
            }
        }

        private byte skipTagBackward() {
            char cCharAt;
            int i = this.charIndex;
            while (true) {
                int i2 = this.charIndex;
                if (i2 <= 0) {
                    break;
                }
                String str = this.text;
                int i3 = i2 - 1;
                this.charIndex = i3;
                char cCharAt2 = str.charAt(i3);
                this.lastChar = cCharAt2;
                if (cCharAt2 == '<') {
                    return (byte) 12;
                }
                if (cCharAt2 == '>') {
                    break;
                }
                if (cCharAt2 == '\"' || cCharAt2 == '\'') {
                    do {
                        int i4 = this.charIndex;
                        if (i4 > 0) {
                            String str2 = this.text;
                            int i5 = i4 - 1;
                            this.charIndex = i5;
                            cCharAt = str2.charAt(i5);
                            this.lastChar = cCharAt;
                        }
                    } while (cCharAt != cCharAt2);
                }
            }
            this.charIndex = i;
            this.lastChar = '>';
            return GreaterThanPtg.sid;
        }

        private byte skipEntityForward() {
            char cCharAt;
            do {
                int i = this.charIndex;
                if (i >= this.length) {
                    return (byte) 12;
                }
                String str = this.text;
                this.charIndex = i + 1;
                cCharAt = str.charAt(i);
                this.lastChar = cCharAt;
            } while (cCharAt != ';');
            return (byte) 12;
        }

        private byte skipEntityBackward() {
            char cCharAt;
            int i = this.charIndex;
            do {
                int i2 = this.charIndex;
                if (i2 <= 0) {
                    break;
                }
                String str = this.text;
                int i3 = i2 - 1;
                this.charIndex = i3;
                cCharAt = str.charAt(i3);
                this.lastChar = cCharAt;
                if (cCharAt == '&') {
                    return (byte) 12;
                }
            } while (cCharAt != ';');
            this.charIndex = i;
            this.lastChar = PhoneNumberUtils.WAIT;
            return GreaterThanPtg.sid;
        }
    }
}
