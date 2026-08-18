package android.text;

import android.content.res.Resources;
import android.media.AudioSystem;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemProperties;
import android.provider.Settings;
import android.telephony.PhoneNumberUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.AlignmentSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.BulletSpan;
import android.text.style.CharacterStyle;
import android.text.style.EasyEditSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.LocaleSpan;
import android.text.style.MetricAffectingSpan;
import android.text.style.QuoteSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.ReplacementSpan;
import android.text.style.ScaleXSpan;
import android.text.style.SpellCheckSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuggestionRangeSpan;
import android.text.style.SuggestionSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.TextAppearanceSpan;
import android.text.style.TypefaceSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.Printer;
import android.widget.ExpandableListView;
import com.android.internal.util.ArrayUtils;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Pattern;
import libcore.icu.ICU;

/* JADX INFO: loaded from: classes.dex */
public class TextUtils {
    public static final int ABSOLUTE_SIZE_SPAN = 16;
    public static final int ALIGNMENT_SPAN = 1;
    public static final int ANNOTATION = 18;
    public static final int BACKGROUND_COLOR_SPAN = 12;
    public static final int BULLET_SPAN = 8;
    public static final int CAP_MODE_CHARACTERS = 4096;
    public static final int CAP_MODE_SENTENCES = 16384;
    public static final int CAP_MODE_WORDS = 8192;
    public static final int EASY_EDIT_SPAN = 22;
    private static final char FIRST_RIGHT_TO_LEFT = 1424;
    public static final int FIRST_SPAN = 1;
    public static final int FOREGROUND_COLOR_SPAN = 2;
    public static final int LAST_SPAN = 23;
    public static final int LEADING_MARGIN_SPAN = 10;
    public static final int LOCALE_SPAN = 23;
    public static final int QUOTE_SPAN = 9;
    public static final int RELATIVE_SIZE_SPAN = 3;
    public static final int SCALE_X_SPAN = 4;
    public static final int SPELL_CHECK_SPAN = 20;
    public static final int STRIKETHROUGH_SPAN = 5;
    public static final int STYLE_SPAN = 7;
    public static final int SUBSCRIPT_SPAN = 15;
    public static final int SUGGESTION_RANGE_SPAN = 21;
    public static final int SUGGESTION_SPAN = 19;
    public static final int SUPERSCRIPT_SPAN = 14;
    private static final String TAG = "TextUtils";
    public static final int TEXT_APPEARANCE_SPAN = 17;
    public static final int TYPEFACE_SPAN = 13;
    public static final int UNDERLINE_SPAN = 6;
    public static final int URL_SPAN = 11;
    private static final char ZWNBS_CHAR = 65279;
    public static final Parcelable.Creator<CharSequence> CHAR_SEQUENCE_CREATOR = new Parcelable.Creator<CharSequence>() { // from class: android.text.TextUtils.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CharSequence createFromParcel(Parcel parcel) {
            int i = parcel.readInt();
            String string = parcel.readString();
            if (string == null) {
                return null;
            }
            if (i == 1) {
                return string;
            }
            SpannableString spannableString = new SpannableString(string);
            while (true) {
                int i2 = parcel.readInt();
                if (i2 == 0) {
                    return spannableString;
                }
                switch (i2) {
                    case 1:
                        TextUtils.readSpan(parcel, spannableString, new AlignmentSpan.Standard(parcel));
                        break;
                    case 2:
                        TextUtils.readSpan(parcel, spannableString, new ForegroundColorSpan(parcel));
                        break;
                    case 3:
                        TextUtils.readSpan(parcel, spannableString, new RelativeSizeSpan(parcel));
                        break;
                    case 4:
                        TextUtils.readSpan(parcel, spannableString, new ScaleXSpan(parcel));
                        break;
                    case 5:
                        TextUtils.readSpan(parcel, spannableString, new StrikethroughSpan(parcel));
                        break;
                    case 6:
                        TextUtils.readSpan(parcel, spannableString, new UnderlineSpan(parcel));
                        break;
                    case 7:
                        TextUtils.readSpan(parcel, spannableString, new StyleSpan(parcel));
                        break;
                    case 8:
                        TextUtils.readSpan(parcel, spannableString, new BulletSpan(parcel));
                        break;
                    case 9:
                        TextUtils.readSpan(parcel, spannableString, new QuoteSpan(parcel));
                        break;
                    case 10:
                        TextUtils.readSpan(parcel, spannableString, new LeadingMarginSpan.Standard(parcel));
                        break;
                    case 11:
                        TextUtils.readSpan(parcel, spannableString, new URLSpan(parcel));
                        break;
                    case 12:
                        TextUtils.readSpan(parcel, spannableString, new BackgroundColorSpan(parcel));
                        break;
                    case 13:
                        TextUtils.readSpan(parcel, spannableString, new TypefaceSpan(parcel));
                        break;
                    case 14:
                        TextUtils.readSpan(parcel, spannableString, new SuperscriptSpan(parcel));
                        break;
                    case 15:
                        TextUtils.readSpan(parcel, spannableString, new SubscriptSpan(parcel));
                        break;
                    case 16:
                        TextUtils.readSpan(parcel, spannableString, new AbsoluteSizeSpan(parcel));
                        break;
                    case 17:
                        TextUtils.readSpan(parcel, spannableString, new TextAppearanceSpan(parcel));
                        break;
                    case 18:
                        TextUtils.readSpan(parcel, spannableString, new Annotation(parcel));
                        break;
                    case 19:
                        TextUtils.readSpan(parcel, spannableString, new SuggestionSpan(parcel));
                        break;
                    case 20:
                        TextUtils.readSpan(parcel, spannableString, new SpellCheckSpan(parcel));
                        break;
                    case 21:
                        TextUtils.readSpan(parcel, spannableString, new SuggestionRangeSpan(parcel));
                        break;
                    case 22:
                        TextUtils.readSpan(parcel, spannableString, new EasyEditSpan(parcel));
                        break;
                    case 23:
                        TextUtils.readSpan(parcel, spannableString, new LocaleSpan(parcel));
                        break;
                    default:
                        throw new RuntimeException("bogus span encoding " + i2);
                }
            }
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CharSequence[] newArray(int i) {
            return new CharSequence[i];
        }
    };
    private static Object sLock = new Object();
    private static char[] sTemp = null;
    private static String[] EMPTY_STRING_ARRAY = new String[0];
    private static String ARAB_SCRIPT_SUBTAG = "Arab";
    private static String HEBR_SCRIPT_SUBTAG = "Hebr";

    public interface EllipsizeCallback {
        void ellipsized(int i, int i2);
    }

    public interface StringSplitter extends Iterable<String> {
        void setString(String str);
    }

    public enum TruncateAt {
        START,
        MIDDLE,
        END,
        MARQUEE,
        END_SMALL
    }

    public static boolean isPrintableAscii(char c) {
        return (' ' <= c && c <= '~') || c == '\r' || c == '\n';
    }

    public static long packRangeInLong(int i, int i2) {
        return ((long) i2) | (((long) i) << 32);
    }

    public static int unpackRangeEndFromLong(long j) {
        return (int) (j & ExpandableListView.PACKED_POSITION_VALUE_NULL);
    }

    public static int unpackRangeStartFromLong(long j) {
        return (int) (j >>> 32);
    }

    private TextUtils() {
    }

    public static void getChars(CharSequence charSequence, int i, int i2, char[] cArr, int i3) {
        Class<?> cls = charSequence.getClass();
        if (cls == String.class) {
            ((String) charSequence).getChars(i, i2, cArr, i3);
            return;
        }
        if (cls == StringBuffer.class) {
            ((StringBuffer) charSequence).getChars(i, i2, cArr, i3);
            return;
        }
        if (cls == StringBuilder.class) {
            ((StringBuilder) charSequence).getChars(i, i2, cArr, i3);
            return;
        }
        if (charSequence instanceof GetChars) {
            ((GetChars) charSequence).getChars(i, i2, cArr, i3);
            return;
        }
        while (i < i2) {
            cArr[i3] = charSequence.charAt(i);
            i++;
            i3++;
        }
    }

    public static int indexOf(CharSequence charSequence, char c) {
        return indexOf(charSequence, c, 0);
    }

    public static int indexOf(CharSequence charSequence, char c, int i) {
        if (charSequence.getClass() == String.class) {
            return ((String) charSequence).indexOf(c, i);
        }
        return indexOf(charSequence, c, i, charSequence.length());
    }

    public static int indexOf(CharSequence charSequence, char c, int i, int i2) {
        Class<?> cls = charSequence.getClass();
        if (!(charSequence instanceof GetChars) && cls != StringBuffer.class && cls != StringBuilder.class && cls != String.class) {
            while (i < i2) {
                if (charSequence.charAt(i) == c) {
                    return i;
                }
                i++;
            }
            return -1;
        }
        char[] cArrObtain = obtain(500);
        while (i < i2) {
            int i3 = i + 500;
            if (i3 > i2) {
                i3 = i2;
            }
            getChars(charSequence, i, i3, cArrObtain, 0);
            int i4 = i3 - i;
            for (int i5 = 0; i5 < i4; i5++) {
                if (cArrObtain[i5] == c) {
                    recycle(cArrObtain);
                    return i5 + i;
                }
            }
            i = i3;
        }
        recycle(cArrObtain);
        return -1;
    }

    public static int lastIndexOf(CharSequence charSequence, char c) {
        return lastIndexOf(charSequence, c, charSequence.length() - 1);
    }

    public static int lastIndexOf(CharSequence charSequence, char c, int i) {
        if (charSequence.getClass() == String.class) {
            return ((String) charSequence).lastIndexOf(c, i);
        }
        return lastIndexOf(charSequence, c, 0, i);
    }

    public static int lastIndexOf(CharSequence charSequence, char c, int i, int i2) {
        if (i2 < 0) {
            return -1;
        }
        if (i2 >= charSequence.length()) {
            i2 = charSequence.length() - 1;
        }
        int i3 = i2 + 1;
        Class<?> cls = charSequence.getClass();
        if (!(charSequence instanceof GetChars) && cls != StringBuffer.class && cls != StringBuilder.class && cls != String.class) {
            for (int i4 = i3 - 1; i4 >= i; i4--) {
                if (charSequence.charAt(i4) == c) {
                    return i4;
                }
            }
            return -1;
        }
        char[] cArrObtain = obtain(500);
        while (i < i3) {
            int i5 = i3 - 500;
            if (i5 < i) {
                i5 = i;
            }
            getChars(charSequence, i5, i3, cArrObtain, 0);
            for (int i6 = (i3 - i5) - 1; i6 >= 0; i6--) {
                if (cArrObtain[i6] == c) {
                    recycle(cArrObtain);
                    return i6 + i5;
                }
            }
            i3 = i5;
        }
        recycle(cArrObtain);
        return -1;
    }

    public static int indexOf(CharSequence charSequence, CharSequence charSequence2) {
        return indexOf(charSequence, charSequence2, 0, charSequence.length());
    }

    public static int indexOf(CharSequence charSequence, CharSequence charSequence2, int i) {
        return indexOf(charSequence, charSequence2, i, charSequence.length());
    }

    public static int indexOf(CharSequence charSequence, CharSequence charSequence2, int i, int i2) {
        int length = charSequence2.length();
        if (length == 0) {
            return i;
        }
        char cCharAt = charSequence2.charAt(0);
        while (true) {
            int iIndexOf = indexOf(charSequence, cCharAt, i);
            if (iIndexOf > i2 - length || iIndexOf < 0) {
                return -1;
            }
            if (regionMatches(charSequence, iIndexOf, charSequence2, 0, length)) {
                return iIndexOf;
            }
            i = iIndexOf + 1;
        }
    }

    public static boolean regionMatches(CharSequence charSequence, int i, CharSequence charSequence2, int i2, int i3) {
        char[] cArrObtain = obtain(i3 * 2);
        boolean z = false;
        getChars(charSequence, i, i + i3, cArrObtain, 0);
        getChars(charSequence2, i2, i2 + i3, cArrObtain, i3);
        int i4 = 0;
        while (true) {
            if (i4 >= i3) {
                z = true;
                break;
            }
            if (cArrObtain[i4] != cArrObtain[i4 + i3]) {
                break;
            }
            i4++;
        }
        recycle(cArrObtain);
        return z;
    }

    public static String substring(CharSequence charSequence, int i, int i2) {
        if (charSequence instanceof String) {
            return ((String) charSequence).substring(i, i2);
        }
        if (charSequence instanceof StringBuilder) {
            return ((StringBuilder) charSequence).substring(i, i2);
        }
        if (charSequence instanceof StringBuffer) {
            return ((StringBuffer) charSequence).substring(i, i2);
        }
        int i3 = i2 - i;
        char[] cArrObtain = obtain(i3);
        getChars(charSequence, i, i2, cArrObtain, 0);
        String str = new String(cArrObtain, 0, i3);
        recycle(cArrObtain);
        return str;
    }

    public static CharSequence join(Iterable<CharSequence> iterable) {
        return join(Resources.getSystem().getText(17040699), iterable);
    }

    public static String join(CharSequence charSequence, Object[] objArr) {
        StringBuilder sb = new StringBuilder();
        boolean z = true;
        for (Object obj : objArr) {
            if (z) {
                z = false;
            } else {
                sb.append(charSequence);
            }
            sb.append(obj);
        }
        return sb.toString();
    }

    public static String join(CharSequence charSequence, Iterable iterable) {
        StringBuilder sb = new StringBuilder();
        boolean z = true;
        for (Object obj : iterable) {
            if (z) {
                z = false;
            } else {
                sb.append(charSequence);
            }
            sb.append(obj);
        }
        return sb.toString();
    }

    public static String[] split(String str, String str2) {
        if (str.length() == 0) {
            return EMPTY_STRING_ARRAY;
        }
        return str.split(str2, -1);
    }

    public static String[] split(String str, Pattern pattern) {
        if (str.length() == 0) {
            return EMPTY_STRING_ARRAY;
        }
        return pattern.split(str, -1);
    }

    public static class SimpleStringSplitter implements StringSplitter, Iterator<String> {
        private char mDelimiter;
        private int mLength;
        private int mPosition;
        private String mString;

        @Override // java.lang.Iterable
        public Iterator<String> iterator() {
            return this;
        }

        public SimpleStringSplitter(char c) {
            this.mDelimiter = c;
        }

        @Override // android.text.TextUtils.StringSplitter
        public void setString(String str) {
            this.mString = str;
            this.mPosition = 0;
            this.mLength = str.length();
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.mPosition < this.mLength;
        }

        @Override // java.util.Iterator
        public String next() {
            int iIndexOf = this.mString.indexOf(this.mDelimiter, this.mPosition);
            if (iIndexOf == -1) {
                iIndexOf = this.mLength;
            }
            String strSubstring = this.mString.substring(this.mPosition, iIndexOf);
            this.mPosition = iIndexOf + 1;
            return strSubstring;
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public static CharSequence stringOrSpannedString(CharSequence charSequence) {
        if (charSequence == null) {
            return null;
        }
        if (charSequence instanceof SpannedString) {
            return charSequence;
        }
        if (charSequence instanceof Spanned) {
            return new SpannedString(charSequence);
        }
        return charSequence.toString();
    }

    public static boolean isEmpty(CharSequence charSequence) {
        return charSequence == null || charSequence.length() == 0;
    }

    public static int getTrimmedLength(CharSequence charSequence) {
        int length = charSequence.length();
        int i = 0;
        while (i < length && charSequence.charAt(i) <= ' ') {
            i++;
        }
        while (length > i && charSequence.charAt(length - 1) <= ' ') {
            length--;
        }
        return length - i;
    }

    public static boolean equals(CharSequence charSequence, CharSequence charSequence2) {
        int length;
        if (charSequence == charSequence2) {
            return true;
        }
        if (charSequence == null || charSequence2 == null || (length = charSequence.length()) != charSequence2.length()) {
            return false;
        }
        if ((charSequence instanceof String) && (charSequence2 instanceof String)) {
            return charSequence.equals(charSequence2);
        }
        for (int i = 0; i < length; i++) {
            if (charSequence.charAt(i) != charSequence2.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    public static CharSequence getReverse(CharSequence charSequence, int i, int i2) {
        return new Reverser(charSequence, i, i2);
    }

    private static class Reverser implements CharSequence, GetChars {
        private int mEnd;
        private CharSequence mSource;
        private int mStart;

        public Reverser(CharSequence charSequence, int i, int i2) {
            this.mSource = charSequence;
            this.mStart = i;
            this.mEnd = i2;
        }

        @Override // java.lang.CharSequence
        public int length() {
            return this.mEnd - this.mStart;
        }

        @Override // java.lang.CharSequence
        public CharSequence subSequence(int i, int i2) {
            char[] cArr = new char[i2 - i];
            getChars(i, i2, cArr, 0);
            return new String(cArr);
        }

        @Override // java.lang.CharSequence
        public String toString() {
            return subSequence(0, length()).toString();
        }

        @Override // java.lang.CharSequence
        public char charAt(int i) {
            return AndroidCharacter.getMirror(this.mSource.charAt((this.mEnd - 1) - i));
        }

        @Override // android.text.GetChars
        public void getChars(int i, int i2, char[] cArr, int i3) {
            CharSequence charSequence = this.mSource;
            int i4 = this.mStart;
            TextUtils.getChars(charSequence, i + i4, i4 + i2, cArr, i3);
            int i5 = i2 - i;
            AndroidCharacter.mirror(cArr, 0, i5);
            int i6 = i5 / 2;
            for (int i7 = 0; i7 < i6; i7++) {
                int i8 = i3 + i7;
                char c = cArr[i8];
                int i9 = ((i3 + i5) - i7) - 1;
                cArr[i8] = cArr[i9];
                cArr[i9] = c;
            }
        }
    }

    public static void writeToParcel(CharSequence charSequence, Parcel parcel, int i) {
        if (charSequence instanceof Spanned) {
            parcel.writeInt(0);
            parcel.writeString(charSequence.toString());
            Spanned spanned = (Spanned) charSequence;
            Object[] spans = spanned.getSpans(0, charSequence.length(), Object.class);
            for (int i2 = 0; i2 < spans.length; i2++) {
                Object obj = spans[i2];
                Object underlying = spans[i2];
                if (underlying instanceof CharacterStyle) {
                    underlying = ((CharacterStyle) underlying).getUnderlying();
                }
                if (underlying instanceof ParcelableSpan) {
                    ParcelableSpan parcelableSpan = (ParcelableSpan) underlying;
                    int spanTypeId = parcelableSpan.getSpanTypeId();
                    if (spanTypeId < 1 || spanTypeId > 23) {
                        Log.e(TAG, "external class \"" + parcelableSpan.getClass().getSimpleName() + "\" is attempting to use the frameworks-only ParcelableSpan interface");
                    } else {
                        parcel.writeInt(spanTypeId);
                        parcelableSpan.writeToParcel(parcel, i);
                        writeWhere(parcel, spanned, obj);
                    }
                }
            }
            parcel.writeInt(0);
            return;
        }
        parcel.writeInt(1);
        if (charSequence != null) {
            parcel.writeString(charSequence.toString());
        } else {
            parcel.writeString(null);
        }
    }

    private static void writeWhere(Parcel parcel, Spanned spanned, Object obj) {
        parcel.writeInt(spanned.getSpanStart(obj));
        parcel.writeInt(spanned.getSpanEnd(obj));
        parcel.writeInt(spanned.getSpanFlags(obj));
    }

    public static void dumpSpans(CharSequence charSequence, Printer printer, String str) {
        if (charSequence instanceof Spanned) {
            Spanned spanned = (Spanned) charSequence;
            for (Object obj : spanned.getSpans(0, charSequence.length(), Object.class)) {
                printer.println(str + ((Object) charSequence.subSequence(spanned.getSpanStart(obj), spanned.getSpanEnd(obj))) + ": " + Integer.toHexString(System.identityHashCode(obj)) + " " + obj.getClass().getCanonicalName() + " (" + spanned.getSpanStart(obj) + "-" + spanned.getSpanEnd(obj) + ") fl=#" + spanned.getSpanFlags(obj));
            }
            return;
        }
        printer.println(str + ((Object) charSequence) + ": (no spans)");
    }

    public static CharSequence replace(CharSequence charSequence, String[] strArr, CharSequence[] charSequenceArr) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence);
        for (int i = 0; i < strArr.length; i++) {
            int iIndexOf = indexOf(spannableStringBuilder, strArr[i]);
            if (iIndexOf >= 0) {
                spannableStringBuilder.setSpan(strArr[i], iIndexOf, strArr[i].length() + iIndexOf, 33);
            }
        }
        for (int i2 = 0; i2 < strArr.length; i2++) {
            int spanStart = spannableStringBuilder.getSpanStart(strArr[i2]);
            int spanEnd = spannableStringBuilder.getSpanEnd(strArr[i2]);
            if (spanStart >= 0) {
                spannableStringBuilder.replace(spanStart, spanEnd, charSequenceArr[i2]);
            }
        }
        return spannableStringBuilder;
    }

    public static CharSequence expandTemplate(CharSequence charSequence, CharSequence... charSequenceArr) {
        if (charSequenceArr.length > 9) {
            throw new IllegalArgumentException("max of 9 values are supported");
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence);
        int length = 0;
        while (length < spannableStringBuilder.length()) {
            try {
                if (spannableStringBuilder.charAt(length) == '^') {
                    int i = length + 1;
                    char cCharAt = spannableStringBuilder.charAt(i);
                    if (cCharAt == '^') {
                        spannableStringBuilder.delete(i, length + 2);
                        length = i;
                    } else if (Character.isDigit(cCharAt)) {
                        int numericValue = Character.getNumericValue(cCharAt) - 1;
                        if (numericValue < 0) {
                            throw new IllegalArgumentException("template requests value ^" + (numericValue + 1));
                        }
                        if (numericValue >= charSequenceArr.length) {
                            throw new IllegalArgumentException("template requests value ^" + (numericValue + 1) + "; only " + charSequenceArr.length + " provided");
                        }
                        spannableStringBuilder.replace(length, length + 2, charSequenceArr[numericValue]);
                        length += charSequenceArr[numericValue].length();
                    }
                }
                length++;
            } catch (IndexOutOfBoundsException unused) {
            }
        }
        return spannableStringBuilder;
    }

    public static int getOffsetBefore(CharSequence charSequence, int i) {
        char cCharAt;
        if (i == 0 || i == 1) {
            return 0;
        }
        char cCharAt2 = charSequence.charAt(i - 1);
        int i2 = (cCharAt2 < 56320 || cCharAt2 > 57343 || (cCharAt = charSequence.charAt(i + (-2))) < 55296 || cCharAt > 56319) ? i - 1 : i - 2;
        if (charSequence instanceof Spanned) {
            Spanned spanned = (Spanned) charSequence;
            ReplacementSpan[] replacementSpanArr = (ReplacementSpan[]) spanned.getSpans(i2, i2, ReplacementSpan.class);
            for (int i3 = 0; i3 < replacementSpanArr.length; i3++) {
                int spanStart = spanned.getSpanStart(replacementSpanArr[i3]);
                int spanEnd = spanned.getSpanEnd(replacementSpanArr[i3]);
                if (spanStart < i2 && spanEnd > i2) {
                    i2 = spanStart;
                }
            }
        }
        return i2;
    }

    public static int getOffsetAfter(CharSequence charSequence, int i) {
        int i2;
        int length = charSequence.length();
        if (i == length || i == length - 1) {
            return length;
        }
        char cCharAt = charSequence.charAt(i);
        if (cCharAt < 55296 || cCharAt > 56319) {
            i2 = i + 1;
        } else {
            i2 = i + 1;
            char cCharAt2 = charSequence.charAt(i2);
            if (cCharAt2 >= 56320 && cCharAt2 <= 57343) {
                i2 = i + 2;
            }
        }
        if (charSequence instanceof Spanned) {
            Spanned spanned = (Spanned) charSequence;
            ReplacementSpan[] replacementSpanArr = (ReplacementSpan[]) spanned.getSpans(i2, i2, ReplacementSpan.class);
            for (int i3 = 0; i3 < replacementSpanArr.length; i3++) {
                int spanStart = spanned.getSpanStart(replacementSpanArr[i3]);
                int spanEnd = spanned.getSpanEnd(replacementSpanArr[i3]);
                if (spanStart < i2 && spanEnd > i2) {
                    i2 = spanEnd;
                }
            }
        }
        return i2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void readSpan(Parcel parcel, Spannable spannable, Object obj) {
        spannable.setSpan(obj, parcel.readInt(), parcel.readInt(), parcel.readInt());
    }

    public static void copySpansFrom(Spanned spanned, int i, int i2, Class cls, Spannable spannable, int i3) {
        if (cls == null) {
            cls = Object.class;
        }
        Object[] spans = spanned.getSpans(i, i2, cls);
        for (int i4 = 0; i4 < spans.length; i4++) {
            int spanStart = spanned.getSpanStart(spans[i4]);
            int spanEnd = spanned.getSpanEnd(spans[i4]);
            int spanFlags = spanned.getSpanFlags(spans[i4]);
            if (spanStart < i) {
                spanStart = i;
            }
            if (spanEnd > i2) {
                spanEnd = i2;
            }
            spannable.setSpan(spans[i4], (spanStart - i) + i3, (spanEnd - i) + i3, spanFlags);
        }
    }

    public static CharSequence ellipsize(CharSequence charSequence, TextPaint textPaint, float f, TruncateAt truncateAt) {
        return ellipsize(charSequence, textPaint, f, truncateAt, false, null);
    }

    public static CharSequence ellipsize(CharSequence charSequence, TextPaint textPaint, float f, TruncateAt truncateAt, boolean z, EllipsizeCallback ellipsizeCallback) {
        Resources system;
        int i;
        if (truncateAt == TruncateAt.END_SMALL) {
            system = Resources.getSystem();
            i = 17039442;
        } else {
            system = Resources.getSystem();
            i = 17039441;
        }
        return ellipsize(charSequence, textPaint, f, truncateAt, z, ellipsizeCallback, TextDirectionHeuristics.FIRSTSTRONG_LTR, system.getString(i));
    }

    /* JADX WARN: Removed duplicated region for block: B:24:0x0060 A[Catch: all -> 0x00e0, TryCatch #0 {all -> 0x00e0, blocks: (B:3:0x0009, B:6:0x001c, B:9:0x0023, B:24:0x0060, B:25:0x0063, B:27:0x0069, B:29:0x006e, B:32:0x0076, B:34:0x0081, B:35:0x0089, B:39:0x0094, B:47:0x00b2, B:50:0x00ce, B:13:0x0030, B:15:0x0034, B:16:0x003b, B:18:0x0040, B:21:0x0045, B:22:0x0059), top: B:56:0x0009 }] */
    /* JADX WARN: Removed duplicated region for block: B:27:0x0069 A[Catch: all -> 0x00e0, TryCatch #0 {all -> 0x00e0, blocks: (B:3:0x0009, B:6:0x001c, B:9:0x0023, B:24:0x0060, B:25:0x0063, B:27:0x0069, B:29:0x006e, B:32:0x0076, B:34:0x0081, B:35:0x0089, B:39:0x0094, B:47:0x00b2, B:50:0x00ce, B:13:0x0030, B:15:0x0034, B:16:0x003b, B:18:0x0040, B:21:0x0045, B:22:0x0059), top: B:56:0x0009 }] */
    /* JADX WARN: Removed duplicated region for block: B:28:0x006d  */
    /* JADX WARN: Removed duplicated region for block: B:31:0x0074  */
    /* JADX WARN: Removed duplicated region for block: B:42:0x00a8  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static java.lang.CharSequence ellipsize(java.lang.CharSequence r8, android.text.TextPaint r9, float r10, android.text.TextUtils.TruncateAt r11, boolean r12, android.text.TextUtils.EllipsizeCallback r13, android.text.TextDirectionHeuristic r14, java.lang.String r15) {
        /*
            Method dump skipped, instruction units count: 229
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.text.TextUtils.ellipsize(java.lang.CharSequence, android.text.TextPaint, float, android.text.TextUtils$TruncateAt, boolean, android.text.TextUtils$EllipsizeCallback, android.text.TextDirectionHeuristic, java.lang.String):java.lang.CharSequence");
    }

    public static CharSequence commaEllipsize(CharSequence charSequence, TextPaint textPaint, float f, String str, String str2) {
        return commaEllipsize(charSequence, textPaint, f, str, str2, TextDirectionHeuristics.FIRSTSTRONG_LTR);
    }

    public static CharSequence commaEllipsize(CharSequence charSequence, TextPaint textPaint, float f, String str, String str2, TextDirectionHeuristic textDirectionHeuristic) {
        char c;
        char[] cArr;
        String string;
        MeasuredText measuredTextObtain = MeasuredText.obtain();
        try {
            int length = charSequence.length();
            if (setPara(measuredTextObtain, textPaint, charSequence, 0, length, textDirectionHeuristic) <= f) {
                return charSequence;
            }
            char[] cArr2 = measuredTextObtain.mChars;
            int i = 0;
            int i2 = 0;
            int i3 = 0;
            while (true) {
                c = PhoneNumberUtils.PAUSE;
                if (i2 >= length) {
                    break;
                }
                if (cArr2[i2] == ',') {
                    i3++;
                }
                i2++;
            }
            int i4 = 1;
            int i5 = i3 + 1;
            String str3 = "";
            float[] fArr = measuredTextObtain.mWidths;
            MeasuredText measuredTextObtain2 = MeasuredText.obtain();
            int i6 = 0;
            int i7 = 0;
            int i8 = 0;
            while (i6 < length) {
                i7 = (int) (i7 + fArr[i6]);
                if (cArr2[i6] == c) {
                    i5--;
                    if (i5 == i4) {
                        string = " " + str;
                    } else {
                        StringBuilder sbAppend = new StringBuilder().append(" ");
                        Object[] objArr = new Object[i4];
                        objArr[i] = Integer.valueOf(i5);
                        string = sbAppend.append(String.format(str2, objArr)).toString();
                    }
                    cArr = cArr2;
                    measuredTextObtain2.setPara(string, i, string.length(), textDirectionHeuristic);
                    if (i7 + measuredTextObtain2.addStyleRun(textPaint, measuredTextObtain2.mLen, null) <= f) {
                        i8 = i6 + 1;
                        str3 = string;
                    }
                } else {
                    cArr = cArr2;
                }
                i6++;
                cArr2 = cArr;
                i = 0;
                i4 = 1;
                c = PhoneNumberUtils.PAUSE;
            }
            MeasuredText.recycle(measuredTextObtain2);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str3);
            spannableStringBuilder.insert(0, charSequence, 0, i8);
            return spannableStringBuilder;
        } finally {
            MeasuredText.recycle(measuredTextObtain);
        }
    }

    private static float setPara(MeasuredText measuredText, TextPaint textPaint, CharSequence charSequence, int i, int i2, TextDirectionHeuristic textDirectionHeuristic) {
        measuredText.setPara(charSequence, i, i2, textDirectionHeuristic);
        Spanned spanned = charSequence instanceof Spanned ? (Spanned) charSequence : null;
        int i3 = i2 - i;
        if (spanned == null) {
            return measuredText.addStyleRun(textPaint, i3, null);
        }
        float fAddStyleRun = 0.0f;
        int i4 = 0;
        while (i4 < i3) {
            int iNextSpanTransition = spanned.nextSpanTransition(i4, i3, MetricAffectingSpan.class);
            fAddStyleRun += measuredText.addStyleRun(textPaint, (MetricAffectingSpan[]) removeEmptySpans((MetricAffectingSpan[]) spanned.getSpans(i4, iNextSpanTransition, MetricAffectingSpan.class), spanned, MetricAffectingSpan.class), iNextSpanTransition - i4, null);
            i4 = iNextSpanTransition;
        }
        return fAddStyleRun;
    }

    static boolean doesNotNeedBidi(CharSequence charSequence, int i, int i2) {
        while (i < i2) {
            if (charSequence.charAt(i) >= 1424) {
                return false;
            }
            i++;
        }
        return true;
    }

    static boolean doesNotNeedBidi(char[] cArr, int i, int i2) {
        int i3 = i2 + i;
        while (i < i3) {
            if (cArr[i] >= 1424) {
                return false;
            }
            i++;
        }
        return true;
    }

    static char[] obtain(int i) {
        char[] cArr;
        synchronized (sLock) {
            cArr = sTemp;
            sTemp = null;
        }
        return (cArr == null || cArr.length < i) ? new char[ArrayUtils.idealCharArraySize(i)] : cArr;
    }

    static void recycle(char[] cArr) {
        if (cArr.length > 1000) {
            return;
        }
        synchronized (sLock) {
            sTemp = cArr;
        }
    }

    public static String htmlEncode(String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char cCharAt = str.charAt(i);
            if (cCharAt == '\"') {
                sb.append("&quot;");
            } else if (cCharAt == '<') {
                sb.append("&lt;");
            } else if (cCharAt == '>') {
                sb.append("&gt;");
            } else if (cCharAt == '&') {
                sb.append("&amp;");
            } else if (cCharAt == '\'') {
                sb.append("&#39;");
            } else {
                sb.append(cCharAt);
            }
        }
        return sb.toString();
    }

    public static CharSequence concat(CharSequence... charSequenceArr) {
        if (charSequenceArr.length == 0) {
            return "";
        }
        boolean z = true;
        if (charSequenceArr.length == 1) {
            return charSequenceArr[0];
        }
        int i = 0;
        while (true) {
            if (i >= charSequenceArr.length) {
                z = false;
                break;
            }
            if (charSequenceArr[i] instanceof Spanned) {
                break;
            }
            i++;
        }
        StringBuilder sb = new StringBuilder();
        for (CharSequence charSequence : charSequenceArr) {
            sb.append(charSequence);
        }
        if (!z) {
            return sb.toString();
        }
        SpannableString spannableString = new SpannableString(sb);
        int i2 = 0;
        for (int i3 = 0; i3 < charSequenceArr.length; i3++) {
            int length = charSequenceArr[i3].length();
            if (charSequenceArr[i3] instanceof Spanned) {
                copySpansFrom((Spanned) charSequenceArr[i3], 0, length, Object.class, spannableString, i2);
            }
            i2 += length;
        }
        return new SpannedString(spannableString);
    }

    public static boolean isGraphic(CharSequence charSequence) {
        int length = charSequence.length();
        for (int i = 0; i < length; i++) {
            int type = Character.getType(charSequence.charAt(i));
            if (type != 15 && type != 16 && type != 19 && type != 0 && type != 13 && type != 14 && type != 12) {
                return true;
            }
        }
        return false;
    }

    public static boolean isGraphic(char c) {
        int type = Character.getType(c);
        return (type == 15 || type == 16 || type == 19 || type == 0 || type == 13 || type == 14 || type == 12) ? false : true;
    }

    public static boolean isDigitsOnly(CharSequence charSequence) {
        int length = charSequence.length();
        for (int i = 0; i < length; i++) {
            if (!Character.isDigit(charSequence.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isPrintableAsciiOnly(CharSequence charSequence) {
        int length = charSequence.length();
        for (int i = 0; i < length; i++) {
            if (!isPrintableAscii(charSequence.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static int getCapsMode(CharSequence charSequence, int i, int i2) {
        char cCharAt;
        char cCharAt2;
        if (i < 0) {
            return 0;
        }
        int i3 = (i2 & 4096) != 0 ? 4096 : 0;
        if ((i2 & AudioSystem.DEVICE_OUT_ALL_USB) == 0) {
            return i3;
        }
        while (i > 0 && ((cCharAt2 = charSequence.charAt(i - 1)) == '\"' || cCharAt2 == '\'' || Character.getType(cCharAt2) == 21)) {
            i--;
        }
        int i4 = i;
        while (i4 > 0) {
            char cCharAt3 = charSequence.charAt(i4 - 1);
            if (cCharAt3 != ' ' && cCharAt3 != '\t') {
                break;
            }
            i4--;
        }
        if (i4 == 0 || charSequence.charAt(i4 - 1) == '\n') {
            return i3 | 8192;
        }
        if ((i2 & 16384) == 0) {
            return i != i4 ? i3 | 8192 : i3;
        }
        if (i == i4) {
            return i3;
        }
        while (i4 > 0) {
            char cCharAt4 = charSequence.charAt(i4 - 1);
            if (cCharAt4 != '\"' && cCharAt4 != '\'' && Character.getType(cCharAt4) != 22) {
                break;
            }
            i4--;
        }
        if (i4 <= 0 || !((cCharAt = charSequence.charAt(i4 - 1)) == '.' || cCharAt == '?' || cCharAt == '!')) {
            return i3;
        }
        if (cCharAt == '.') {
            for (int i5 = i4 - 2; i5 >= 0; i5--) {
                char cCharAt5 = charSequence.charAt(i5);
                if (cCharAt5 == '.') {
                    return i3;
                }
                if (!Character.isLetter(cCharAt5)) {
                    break;
                }
            }
        }
        return i3 | 16384;
    }

    public static boolean delimitedStringContains(String str, char c, String str2) {
        if (!isEmpty(str) && !isEmpty(str2)) {
            int length = str.length();
            int iIndexOf = -1;
            while (true) {
                iIndexOf = str.indexOf(str2, iIndexOf + 1);
                if (iIndexOf == -1) {
                    break;
                }
                if (iIndexOf <= 0 || str.charAt(iIndexOf - 1) == c) {
                    int length2 = str2.length() + iIndexOf;
                    if (length2 == length || str.charAt(length2) == c) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static <T> T[] removeEmptySpans(T[] tArr, Spanned spanned, Class<T> cls) {
        Object[] objArr = null;
        int i = 0;
        for (int i2 = 0; i2 < tArr.length; i2++) {
            T t = tArr[i2];
            if (spanned.getSpanStart(t) == spanned.getSpanEnd(t)) {
                if (objArr == null) {
                    objArr = (Object[]) Array.newInstance((Class<?>) cls, tArr.length - 1);
                    System.arraycopy(tArr, 0, objArr, 0, i2);
                    i = i2;
                }
            } else if (objArr != null) {
                objArr[i] = t;
                i++;
            }
        }
        if (objArr == null) {
            return tArr;
        }
        T[] tArr2 = (T[]) ((Object[]) Array.newInstance((Class<?>) cls, i));
        System.arraycopy(objArr, 0, tArr2, 0, i);
        return tArr2;
    }

    public static int getLayoutDirectionFromLocale(Locale locale) {
        if (locale != null && !locale.equals(Locale.ROOT)) {
            String script = ICU.getScript(ICU.addLikelySubtags(locale.toString()));
            if (script == null) {
                return getLayoutDirectionFromFirstChar(locale);
            }
            if (script.equalsIgnoreCase(ARAB_SCRIPT_SUBTAG) || script.equalsIgnoreCase(HEBR_SCRIPT_SUBTAG)) {
                return 1;
            }
        }
        return SystemProperties.getBoolean(Settings.Global.DEVELOPMENT_FORCE_RTL, false) ? 1 : 0;
    }

    private static int getLayoutDirectionFromFirstChar(Locale locale) {
        byte directionality = Character.getDirectionality(locale.getDisplayName(locale).charAt(0));
        return (directionality == 1 || directionality == 2) ? 1 : 0;
    }
}
