package android.text;

import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.AlignmentSpan;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.ParagraphStyle;
import android.text.style.QuoteSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.TypefaceSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import com.android.internal.util.ArrayUtils;
import org.ccil.cowan.tagsoup.HTMLSchema;
import org.ccil.cowan.tagsoup.Parser;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

/* JADX INFO: loaded from: classes.dex */
public class Html {

    public interface ImageGetter {
        Drawable getDrawable(String str);
    }

    public interface TagHandler {
        void handleTag(boolean z, String str, Editable editable, XMLReader xMLReader);
    }

    private Html() {
    }

    public static Spanned fromHtml(String str) {
        return fromHtml(str, null, null);
    }

    private static class HtmlParser {
        private static final HTMLSchema schema = new HTMLSchema();

        private HtmlParser() {
        }
    }

    public static Spanned fromHtml(String str, ImageGetter imageGetter, TagHandler tagHandler) {
        Parser parser = new Parser();
        try {
            parser.setProperty("http://www.ccil.org/~cowan/tagsoup/properties/schema", HtmlParser.schema);
            return new HtmlToSpannedConverter(str, imageGetter, tagHandler, parser).convert();
        } catch (SAXNotRecognizedException e) {
            throw new RuntimeException(e);
        } catch (SAXNotSupportedException e2) {
            throw new RuntimeException(e2);
        }
    }

    public static String toHtml(Spanned spanned) {
        StringBuilder sb = new StringBuilder();
        withinHtml(sb, spanned);
        return sb.toString();
    }

    public static String escapeHtml(CharSequence charSequence) {
        StringBuilder sb = new StringBuilder();
        withinStyle(sb, charSequence, 0, charSequence.length());
        return sb.toString();
    }

    private static void withinHtml(StringBuilder sb, Spanned spanned) {
        int length = spanned.length();
        int i = 0;
        while (i < spanned.length()) {
            int iNextSpanTransition = spanned.nextSpanTransition(i, length, ParagraphStyle.class);
            ParagraphStyle[] paragraphStyleArr = (ParagraphStyle[]) spanned.getSpans(i, iNextSpanTransition, ParagraphStyle.class);
            String str = " ";
            boolean z = false;
            for (int i2 = 0; i2 < paragraphStyleArr.length; i2++) {
                if (paragraphStyleArr[i2] instanceof AlignmentSpan) {
                    Layout.Alignment alignment = ((AlignmentSpan) paragraphStyleArr[i2]).getAlignment();
                    if (alignment == Layout.Alignment.ALIGN_CENTER) {
                        str = "align=\"center\" " + str;
                    } else if (alignment == Layout.Alignment.ALIGN_OPPOSITE) {
                        str = "align=\"right\" " + str;
                    } else {
                        str = "align=\"left\" " + str;
                    }
                    z = true;
                }
            }
            if (z) {
                sb.append("<div ").append(str).append(">");
            }
            withinDiv(sb, spanned, i, iNextSpanTransition);
            if (z) {
                sb.append("</div>");
            }
            i = iNextSpanTransition;
        }
    }

    private static void withinDiv(StringBuilder sb, Spanned spanned, int i, int i2) {
        while (i < i2) {
            int iNextSpanTransition = spanned.nextSpanTransition(i, i2, QuoteSpan.class);
            QuoteSpan[] quoteSpanArr = (QuoteSpan[]) spanned.getSpans(i, iNextSpanTransition, QuoteSpan.class);
            for (QuoteSpan quoteSpan : quoteSpanArr) {
                sb.append("<blockquote>");
            }
            withinBlockquote(sb, spanned, i, iNextSpanTransition);
            for (QuoteSpan quoteSpan2 : quoteSpanArr) {
                sb.append("</blockquote>\n");
            }
            i = iNextSpanTransition;
        }
    }

    private static String getOpenParaTagWithDirection(Spanned spanned, int i, int i2) {
        int i3 = i2 - i;
        byte[] bArr = new byte[ArrayUtils.idealByteArraySize(i3)];
        char[] cArrObtain = TextUtils.obtain(i3);
        TextUtils.getChars(spanned, i, i2, cArrObtain, 0);
        return AndroidBidi.bidi(2, cArrObtain, bArr, i3, false) != -1 ? "<p dir=\"ltr\">" : "<p dir=\"rtl\">";
    }

    private static void withinBlockquote(StringBuilder sb, Spanned spanned, int i, int i2) {
        sb.append(getOpenParaTagWithDirection(spanned, i, i2));
        int i3 = i;
        while (i3 < i2) {
            int iIndexOf = TextUtils.indexOf((CharSequence) spanned, '\n', i3, i2);
            if (iIndexOf < 0) {
                iIndexOf = i2;
            }
            int i4 = 0;
            while (iIndexOf < i2 && spanned.charAt(iIndexOf) == '\n') {
                i4++;
                iIndexOf++;
            }
            withinParagraph(sb, spanned, i3, iIndexOf - i4, i4, iIndexOf == i2);
            i3 = iIndexOf;
        }
        sb.append("</p>\n");
    }

    private static void withinParagraph(StringBuilder sb, Spanned spanned, int i, int i2, int i3, boolean z) {
        int i4 = i;
        while (i4 < i2) {
            int iNextSpanTransition = spanned.nextSpanTransition(i4, i2, CharacterStyle.class);
            CharacterStyle[] characterStyleArr = (CharacterStyle[]) spanned.getSpans(i4, iNextSpanTransition, CharacterStyle.class);
            for (int i5 = 0; i5 < characterStyleArr.length; i5++) {
                if (characterStyleArr[i5] instanceof StyleSpan) {
                    int style = ((StyleSpan) characterStyleArr[i5]).getStyle();
                    if ((style & 1) != 0) {
                        sb.append("<b>");
                    }
                    if ((style & 2) != 0) {
                        sb.append("<i>");
                    }
                }
                if ((characterStyleArr[i5] instanceof TypefaceSpan) && ((TypefaceSpan) characterStyleArr[i5]).getFamily().equals("monospace")) {
                    sb.append("<tt>");
                }
                if (characterStyleArr[i5] instanceof SuperscriptSpan) {
                    sb.append("<sup>");
                }
                if (characterStyleArr[i5] instanceof SubscriptSpan) {
                    sb.append("<sub>");
                }
                if (characterStyleArr[i5] instanceof UnderlineSpan) {
                    sb.append("<u>");
                }
                if (characterStyleArr[i5] instanceof StrikethroughSpan) {
                    sb.append("<strike>");
                }
                if (characterStyleArr[i5] instanceof URLSpan) {
                    sb.append("<a href=\"");
                    sb.append(((URLSpan) characterStyleArr[i5]).getURL());
                    sb.append("\">");
                }
                if (characterStyleArr[i5] instanceof ImageSpan) {
                    sb.append("<img src=\"");
                    sb.append(((ImageSpan) characterStyleArr[i5]).getSource());
                    sb.append("\">");
                    i4 = iNextSpanTransition;
                }
                if (characterStyleArr[i5] instanceof AbsoluteSizeSpan) {
                    sb.append("<font size =\"");
                    sb.append(((AbsoluteSizeSpan) characterStyleArr[i5]).getSize() / 6);
                    sb.append("\">");
                }
                if (characterStyleArr[i5] instanceof ForegroundColorSpan) {
                    sb.append("<font color =\"#");
                    String hexString = Integer.toHexString(((ForegroundColorSpan) characterStyleArr[i5]).getForegroundColor() + 16777216);
                    while (hexString.length() < 6) {
                        hexString = "0" + hexString;
                    }
                    sb.append(hexString);
                    sb.append("\">");
                }
            }
            withinStyle(sb, spanned, i4, iNextSpanTransition);
            for (int length = characterStyleArr.length - 1; length >= 0; length--) {
                if (characterStyleArr[length] instanceof ForegroundColorSpan) {
                    sb.append("</font>");
                }
                if (characterStyleArr[length] instanceof AbsoluteSizeSpan) {
                    sb.append("</font>");
                }
                if (characterStyleArr[length] instanceof URLSpan) {
                    sb.append("</a>");
                }
                if (characterStyleArr[length] instanceof StrikethroughSpan) {
                    sb.append("</strike>");
                }
                if (characterStyleArr[length] instanceof UnderlineSpan) {
                    sb.append("</u>");
                }
                if (characterStyleArr[length] instanceof SubscriptSpan) {
                    sb.append("</sub>");
                }
                if (characterStyleArr[length] instanceof SuperscriptSpan) {
                    sb.append("</sup>");
                }
                if ((characterStyleArr[length] instanceof TypefaceSpan) && ((TypefaceSpan) characterStyleArr[length]).getFamily().equals("monospace")) {
                    sb.append("</tt>");
                }
                if (characterStyleArr[length] instanceof StyleSpan) {
                    int style2 = ((StyleSpan) characterStyleArr[length]).getStyle();
                    if ((style2 & 1) != 0) {
                        sb.append("</b>");
                    }
                    if ((style2 & 2) != 0) {
                        sb.append("</i>");
                    }
                }
            }
            i4 = iNextSpanTransition;
        }
        String str = z ? "" : "</p>\n" + getOpenParaTagWithDirection(spanned, i, i2);
        if (i3 == 1) {
            sb.append("<br>\n");
            return;
        }
        if (i3 == 2) {
            sb.append(str);
            return;
        }
        for (int i6 = 2; i6 < i3; i6++) {
            sb.append("<br>");
        }
        sb.append(str);
    }

    private static void withinStyle(StringBuilder sb, CharSequence charSequence, int i, int i2) {
        int i3;
        char cCharAt;
        while (i < i2) {
            char cCharAt2 = charSequence.charAt(i);
            if (cCharAt2 == '<') {
                sb.append("&lt;");
            } else if (cCharAt2 == '>') {
                sb.append("&gt;");
            } else if (cCharAt2 == '&') {
                sb.append("&amp;");
            } else if (cCharAt2 < 55296 || cCharAt2 > 57343) {
                if (cCharAt2 > '~' || cCharAt2 < ' ') {
                    sb.append("&#").append((int) cCharAt2).append(";");
                } else if (cCharAt2 == ' ') {
                    while (true) {
                        int i4 = i + 1;
                        if (i4 >= i2 || charSequence.charAt(i4) != ' ') {
                            break;
                        }
                        sb.append("&nbsp;");
                        i = i4;
                    }
                    sb.append(' ');
                } else {
                    sb.append(cCharAt2);
                }
            } else if (cCharAt2 < 56320 && (i3 = i + 1) < i2 && (cCharAt = charSequence.charAt(i3)) >= 56320 && cCharAt <= 57343) {
                sb.append("&#").append(65536 | ((cCharAt2 - 55296) << 10) | (cCharAt - 56320)).append(";");
                i = i3;
            }
            i++;
        }
    }
}
