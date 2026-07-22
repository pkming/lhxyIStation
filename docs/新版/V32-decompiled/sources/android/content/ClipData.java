package android.content;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.StrictMode;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.util.Log;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public class ClipData implements Parcelable {
    final ClipDescription mClipDescription;
    final Bitmap mIcon;
    final ArrayList<Item> mItems;
    static final String[] MIMETYPES_TEXT_PLAIN = {ClipDescription.MIMETYPE_TEXT_PLAIN};
    static final String[] MIMETYPES_TEXT_HTML = {ClipDescription.MIMETYPE_TEXT_HTML};
    static final String[] MIMETYPES_TEXT_URILIST = {ClipDescription.MIMETYPE_TEXT_URILIST};
    static final String[] MIMETYPES_TEXT_INTENT = {ClipDescription.MIMETYPE_TEXT_INTENT};
    public static final Parcelable.Creator<ClipData> CREATOR = new Parcelable.Creator<ClipData>() { // from class: android.content.ClipData.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ClipData createFromParcel(Parcel parcel) {
            return new ClipData(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ClipData[] newArray(int i) {
            return new ClipData[i];
        }
    };

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public static class Item {
        final String mHtmlText;
        final Intent mIntent;
        final CharSequence mText;
        final Uri mUri;

        public Item(CharSequence charSequence) {
            this.mText = charSequence;
            this.mHtmlText = null;
            this.mIntent = null;
            this.mUri = null;
        }

        public Item(CharSequence charSequence, String str) {
            this.mText = charSequence;
            this.mHtmlText = str;
            this.mIntent = null;
            this.mUri = null;
        }

        public Item(Intent intent) {
            this.mText = null;
            this.mHtmlText = null;
            this.mIntent = intent;
            this.mUri = null;
        }

        public Item(Uri uri) {
            this.mText = null;
            this.mHtmlText = null;
            this.mIntent = null;
            this.mUri = uri;
        }

        public Item(CharSequence charSequence, Intent intent, Uri uri) {
            this.mText = charSequence;
            this.mHtmlText = null;
            this.mIntent = intent;
            this.mUri = uri;
        }

        public Item(CharSequence charSequence, String str, Intent intent, Uri uri) {
            if (str != null && charSequence == null) {
                throw new IllegalArgumentException("Plain text must be supplied if HTML text is supplied");
            }
            this.mText = charSequence;
            this.mHtmlText = str;
            this.mIntent = intent;
            this.mUri = uri;
        }

        public CharSequence getText() {
            return this.mText;
        }

        public String getHtmlText() {
            return this.mHtmlText;
        }

        public Intent getIntent() {
            return this.mIntent;
        }

        public Uri getUri() {
            return this.mUri;
        }

        public CharSequence coerceToText(Context context) {
            CharSequence text = getText();
            if (text != null) {
                return text;
            }
            Uri uri = getUri();
            if (uri != null) {
                FileInputStream fileInputStreamCreateInputStream = null;
                try {
                    try {
                        fileInputStreamCreateInputStream = context.getContentResolver().openTypedAssetFileDescriptor(uri, "text/*", null).createInputStream();
                        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStreamCreateInputStream, "UTF-8");
                        StringBuilder sb = new StringBuilder(128);
                        char[] cArr = new char[8192];
                        while (true) {
                            int i = inputStreamReader.read(cArr);
                            if (i <= 0) {
                                break;
                            }
                            sb.append(cArr, 0, i);
                        }
                        String string = sb.toString();
                        if (fileInputStreamCreateInputStream != null) {
                            try {
                                fileInputStreamCreateInputStream.close();
                            } catch (IOException unused) {
                            }
                        }
                        return string;
                    } catch (Throwable th) {
                        if (fileInputStreamCreateInputStream != null) {
                            try {
                                fileInputStreamCreateInputStream.close();
                            } catch (IOException unused2) {
                            }
                        }
                        throw th;
                    }
                } catch (FileNotFoundException unused3) {
                    if (fileInputStreamCreateInputStream != null) {
                        try {
                            fileInputStreamCreateInputStream.close();
                        } catch (IOException unused4) {
                        }
                    }
                    return uri.toString();
                } catch (IOException e) {
                    Log.w("ClippedData", "Failure loading text", e);
                    String string2 = e.toString();
                    if (fileInputStreamCreateInputStream != null) {
                        try {
                            fileInputStreamCreateInputStream.close();
                        } catch (IOException unused5) {
                        }
                    }
                    return string2;
                }
            }
            Intent intent = getIntent();
            return intent != null ? intent.toUri(1) : "";
        }

        public CharSequence coerceToStyledText(Context context) {
            CharSequence text = getText();
            if (text instanceof Spanned) {
                return text;
            }
            String htmlText = getHtmlText();
            if (htmlText != null) {
                try {
                    Spanned spannedFromHtml = Html.fromHtml(htmlText);
                    if (spannedFromHtml != null) {
                        return spannedFromHtml;
                    }
                } catch (RuntimeException unused) {
                }
            }
            return text != null ? text : coerceToHtmlOrStyledText(context, true);
        }

        public String coerceToHtmlText(Context context) {
            String htmlText = getHtmlText();
            if (htmlText != null) {
                return htmlText;
            }
            CharSequence text = getText();
            if (text != null) {
                if (text instanceof Spanned) {
                    return Html.toHtml((Spanned) text);
                }
                return Html.escapeHtml(text);
            }
            CharSequence charSequenceCoerceToHtmlOrStyledText = coerceToHtmlOrStyledText(context, false);
            if (charSequenceCoerceToHtmlOrStyledText != null) {
                return charSequenceCoerceToHtmlOrStyledText.toString();
            }
            return null;
        }

        private CharSequence coerceToHtmlOrStyledText(Context context, boolean z) {
            boolean z2;
            boolean z3;
            if (this.mUri == null) {
                Intent intent = this.mIntent;
                return intent != null ? z ? uriToStyledText(intent.toUri(1)) : uriToHtml(intent.toUri(1)) : "";
            }
            String[] streamTypes = context.getContentResolver().getStreamTypes(this.mUri, "text/*");
            String str = ClipDescription.MIMETYPE_TEXT_HTML;
            if (streamTypes != null) {
                z2 = false;
                z3 = false;
                for (String str2 : streamTypes) {
                    if (ClipDescription.MIMETYPE_TEXT_HTML.equals(str2)) {
                        z2 = true;
                    } else if (str2.startsWith("text/")) {
                        z3 = true;
                    }
                }
            } else {
                z2 = false;
                z3 = false;
            }
            if (z2 || z3) {
                FileInputStream fileInputStream = null;
                try {
                    try {
                        ContentResolver contentResolver = context.getContentResolver();
                        Uri uri = this.mUri;
                        if (!z2) {
                            str = ClipDescription.MIMETYPE_TEXT_PLAIN;
                        }
                        FileInputStream fileInputStreamCreateInputStream = contentResolver.openTypedAssetFileDescriptor(uri, str, null).createInputStream();
                        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStreamCreateInputStream, "UTF-8");
                        StringBuilder sb = new StringBuilder(128);
                        char[] cArr = new char[8192];
                        while (true) {
                            int i = inputStreamReader.read(cArr);
                            if (i <= 0) {
                                break;
                            }
                            sb.append(cArr, 0, i);
                        }
                        String string = sb.toString();
                        if (!z2) {
                            if (z) {
                                if (fileInputStreamCreateInputStream != null) {
                                    try {
                                        fileInputStreamCreateInputStream.close();
                                    } catch (IOException unused) {
                                    }
                                }
                                return string;
                            }
                            String strEscapeHtml = Html.escapeHtml(string);
                            if (fileInputStreamCreateInputStream != null) {
                                try {
                                    fileInputStreamCreateInputStream.close();
                                } catch (IOException unused2) {
                                }
                            }
                            return strEscapeHtml;
                        }
                        if (!z) {
                            String string2 = string.toString();
                            if (fileInputStreamCreateInputStream != null) {
                                try {
                                    fileInputStreamCreateInputStream.close();
                                } catch (IOException unused3) {
                                }
                            }
                            return string2;
                        }
                        try {
                            Spanned spannedFromHtml = Html.fromHtml(string);
                            CharSequence charSequence = string;
                            if (spannedFromHtml != null) {
                                charSequence = spannedFromHtml;
                            }
                            if (fileInputStreamCreateInputStream != null) {
                                try {
                                    fileInputStreamCreateInputStream.close();
                                } catch (IOException unused4) {
                                }
                            }
                            return charSequence;
                        } catch (RuntimeException unused5) {
                            if (fileInputStreamCreateInputStream != null) {
                                try {
                                    fileInputStreamCreateInputStream.close();
                                } catch (IOException unused6) {
                                }
                            }
                            return string;
                        }
                    } catch (Throwable th) {
                        if (0 != 0) {
                            try {
                                fileInputStream.close();
                            } catch (IOException unused7) {
                            }
                        }
                        throw th;
                    }
                } catch (FileNotFoundException unused8) {
                    if (0 != 0) {
                        try {
                            fileInputStream.close();
                        } catch (IOException unused9) {
                        }
                    }
                } catch (IOException e) {
                    Log.w("ClippedData", "Failure loading text", e);
                    String strEscapeHtml2 = Html.escapeHtml(e.toString());
                    if (0 != 0) {
                        try {
                            fileInputStream.close();
                        } catch (IOException unused10) {
                        }
                    }
                    return strEscapeHtml2;
                }
            }
            return z ? uriToStyledText(this.mUri.toString()) : uriToHtml(this.mUri.toString());
        }

        private String uriToHtml(String str) {
            StringBuilder sb = new StringBuilder(256);
            sb.append("<a href=\"");
            sb.append(Html.escapeHtml(str));
            sb.append("\">");
            sb.append(Html.escapeHtml(str));
            sb.append("</a>");
            return sb.toString();
        }

        private CharSequence uriToStyledText(String str) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            spannableStringBuilder.append((CharSequence) str);
            spannableStringBuilder.setSpan(new URLSpan(str), 0, spannableStringBuilder.length(), 33);
            return spannableStringBuilder;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(128);
            sb.append("ClipData.Item { ");
            toShortString(sb);
            sb.append(" }");
            return sb.toString();
        }

        public void toShortString(StringBuilder sb) {
            if (this.mHtmlText != null) {
                sb.append("H:");
                sb.append(this.mHtmlText);
                return;
            }
            if (this.mText != null) {
                sb.append("T:");
                sb.append(this.mText);
            } else if (this.mUri != null) {
                sb.append("U:");
                sb.append(this.mUri);
            } else if (this.mIntent != null) {
                sb.append("I:");
                this.mIntent.toShortString(sb, true, true, true, true);
            } else {
                sb.append("NULL");
            }
        }
    }

    public ClipData(CharSequence charSequence, String[] strArr, Item item) {
        this.mClipDescription = new ClipDescription(charSequence, strArr);
        Objects.requireNonNull(item, "item is null");
        this.mIcon = null;
        ArrayList<Item> arrayList = new ArrayList<>();
        this.mItems = arrayList;
        arrayList.add(item);
    }

    public ClipData(ClipDescription clipDescription, Item item) {
        this.mClipDescription = clipDescription;
        Objects.requireNonNull(item, "item is null");
        this.mIcon = null;
        ArrayList<Item> arrayList = new ArrayList<>();
        this.mItems = arrayList;
        arrayList.add(item);
    }

    public ClipData(ClipData clipData) {
        this.mClipDescription = clipData.mClipDescription;
        this.mIcon = clipData.mIcon;
        this.mItems = new ArrayList<>(clipData.mItems);
    }

    public static ClipData newPlainText(CharSequence charSequence, CharSequence charSequence2) {
        return new ClipData(charSequence, MIMETYPES_TEXT_PLAIN, new Item(charSequence2));
    }

    public static ClipData newHtmlText(CharSequence charSequence, CharSequence charSequence2, String str) {
        return new ClipData(charSequence, MIMETYPES_TEXT_HTML, new Item(charSequence2, str));
    }

    public static ClipData newIntent(CharSequence charSequence, Intent intent) {
        return new ClipData(charSequence, MIMETYPES_TEXT_INTENT, new Item(intent));
    }

    public static ClipData newUri(ContentResolver contentResolver, CharSequence charSequence, Uri uri) {
        String[] streamTypes;
        Item item = new Item(uri);
        if ("content".equals(uri.getScheme())) {
            String type = contentResolver.getType(uri);
            streamTypes = contentResolver.getStreamTypes(uri, "*/*");
            int i = 1;
            if (streamTypes != null) {
                String[] strArr = new String[streamTypes.length + (type == null ? 1 : 2)];
                if (type != null) {
                    strArr[0] = type;
                } else {
                    i = 0;
                }
                System.arraycopy(streamTypes, 0, strArr, i, streamTypes.length);
                strArr[i + streamTypes.length] = ClipDescription.MIMETYPE_TEXT_URILIST;
                streamTypes = strArr;
            } else if (type != null) {
                streamTypes = new String[]{type, ClipDescription.MIMETYPE_TEXT_URILIST};
            }
        } else {
            streamTypes = null;
        }
        if (streamTypes == null) {
            streamTypes = MIMETYPES_TEXT_URILIST;
        }
        return new ClipData(charSequence, streamTypes, item);
    }

    public static ClipData newRawUri(CharSequence charSequence, Uri uri) {
        return new ClipData(charSequence, MIMETYPES_TEXT_URILIST, new Item(uri));
    }

    public ClipDescription getDescription() {
        return this.mClipDescription;
    }

    public void addItem(Item item) {
        Objects.requireNonNull(item, "item is null");
        this.mItems.add(item);
    }

    public Bitmap getIcon() {
        return this.mIcon;
    }

    public int getItemCount() {
        return this.mItems.size();
    }

    public Item getItemAt(int i) {
        return this.mItems.get(i);
    }

    public void prepareToLeaveProcess() {
        int size = this.mItems.size();
        for (int i = 0; i < size; i++) {
            Item item = this.mItems.get(i);
            if (item.mIntent != null) {
                item.mIntent.prepareToLeaveProcess();
            }
            if (item.mUri != null && StrictMode.vmFileUriExposureEnabled()) {
                item.mUri.checkFileUriExposed("ClipData.Item.getUri()");
            }
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("ClipData { ");
        toShortString(sb);
        sb.append(" }");
        return sb.toString();
    }

    public void toShortString(StringBuilder sb) {
        ClipDescription clipDescription = this.mClipDescription;
        boolean shortString = clipDescription != null ? true ^ clipDescription.toShortString(sb) : true;
        if (this.mIcon != null) {
            if (!shortString) {
                sb.append(' ');
            }
            sb.append("I:");
            sb.append(this.mIcon.getWidth());
            sb.append('x');
            sb.append(this.mIcon.getHeight());
            shortString = false;
        }
        int i = 0;
        while (i < this.mItems.size()) {
            if (!shortString) {
                sb.append(' ');
            }
            sb.append('{');
            this.mItems.get(i).toShortString(sb);
            sb.append('}');
            i++;
            shortString = false;
        }
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        this.mClipDescription.writeToParcel(parcel, i);
        if (this.mIcon != null) {
            parcel.writeInt(1);
            this.mIcon.writeToParcel(parcel, i);
        } else {
            parcel.writeInt(0);
        }
        int size = this.mItems.size();
        parcel.writeInt(size);
        for (int i2 = 0; i2 < size; i2++) {
            Item item = this.mItems.get(i2);
            TextUtils.writeToParcel(item.mText, parcel, i);
            parcel.writeString(item.mHtmlText);
            if (item.mIntent != null) {
                parcel.writeInt(1);
                item.mIntent.writeToParcel(parcel, i);
            } else {
                parcel.writeInt(0);
            }
            if (item.mUri != null) {
                parcel.writeInt(1);
                item.mUri.writeToParcel(parcel, i);
            } else {
                parcel.writeInt(0);
            }
        }
    }

    ClipData(Parcel parcel) {
        this.mClipDescription = new ClipDescription(parcel);
        if (parcel.readInt() != 0) {
            this.mIcon = Bitmap.CREATOR.createFromParcel(parcel);
        } else {
            this.mIcon = null;
        }
        this.mItems = new ArrayList<>();
        int i = parcel.readInt();
        for (int i2 = 0; i2 < i; i2++) {
            this.mItems.add(new Item(TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel), parcel.readString(), parcel.readInt() != 0 ? Intent.CREATOR.createFromParcel(parcel) : null, parcel.readInt() != 0 ? Uri.CREATOR.createFromParcel(parcel) : null));
        }
    }
}
