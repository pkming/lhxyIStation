package android.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;
import com.android.internal.telephony.CallerInfo;
import com.android.internal.telephony.PhoneConstants;

/* JADX INFO: loaded from: classes.dex */
public class CallLog {
    public static final String AUTHORITY = "call_log";
    public static final Uri CONTENT_URI = Uri.parse("content://call_log");

    public static class Calls implements BaseColumns {
        public static final String ALLOW_VOICEMAILS_PARAM_KEY = "allow_voicemails";
        public static final String CACHED_FORMATTED_NUMBER = "formatted_number";
        public static final String CACHED_LOOKUP_URI = "lookup_uri";
        public static final String CACHED_MATCHED_NUMBER = "matched_number";
        public static final String CACHED_NAME = "name";
        public static final String CACHED_NORMALIZED_NUMBER = "normalized_number";
        public static final String CACHED_NUMBER_LABEL = "numberlabel";
        public static final String CACHED_NUMBER_TYPE = "numbertype";
        public static final String CACHED_PHOTO_ID = "photo_id";
        public static final Uri CONTENT_FILTER_URI;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/calls";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/calls";
        public static final Uri CONTENT_URI;
        public static final Uri CONTENT_URI_WITH_VOICEMAIL;
        public static final String COUNTRY_ISO = "countryiso";
        public static final String DATE = "date";
        public static final String DEFAULT_SORT_ORDER = "date DESC";
        public static final String DURATION = "duration";
        public static final String GEOCODED_LOCATION = "geocoded_location";
        public static final int INCOMING_TYPE = 1;
        public static final String IS_READ = "is_read";
        public static final String LIMIT_PARAM_KEY = "limit";
        public static final int MISSED_TYPE = 3;
        public static final String NEW = "new";
        public static final String NUMBER = "number";
        public static final String NUMBER_PRESENTATION = "presentation";
        public static final String OFFSET_PARAM_KEY = "offset";
        public static final int OUTGOING_TYPE = 2;
        public static final int PRESENTATION_ALLOWED = 1;
        public static final int PRESENTATION_PAYPHONE = 4;
        public static final int PRESENTATION_RESTRICTED = 2;
        public static final int PRESENTATION_UNKNOWN = 3;
        public static final String TYPE = "type";
        public static final int VOICEMAIL_TYPE = 4;
        public static final String VOICEMAIL_URI = "voicemail_uri";

        static {
            Uri uri = Uri.parse("content://call_log/calls");
            CONTENT_URI = uri;
            CONTENT_FILTER_URI = Uri.parse("content://call_log/calls/filter");
            CONTENT_URI_WITH_VOICEMAIL = uri.buildUpon().appendQueryParameter(ALLOW_VOICEMAILS_PARAM_KEY, "true").build();
        }

        public static Uri addCall(CallerInfo callerInfo, Context context, String str, int i, int i2, long j, int i3) {
            int i4;
            Cursor cursorQuery;
            ContentResolver contentResolver = context.getContentResolver();
            if (i == PhoneConstants.PRESENTATION_RESTRICTED) {
                i4 = 2;
            } else if (i == PhoneConstants.PRESENTATION_PAYPHONE) {
                i4 = 4;
            } else {
                i4 = (TextUtils.isEmpty(str) || i == PhoneConstants.PRESENTATION_UNKNOWN) ? 3 : 1;
            }
            if (i4 != 1) {
                if (callerInfo != null) {
                    callerInfo.name = "";
                }
                str = "";
            }
            ContentValues contentValues = new ContentValues(6);
            contentValues.put("number", str);
            contentValues.put(NUMBER_PRESENTATION, Integer.valueOf(i4));
            contentValues.put("type", Integer.valueOf(i2));
            contentValues.put("date", Long.valueOf(j));
            contentValues.put("duration", Long.valueOf(i3));
            contentValues.put(NEW, (Integer) 1);
            if (i2 == 3) {
                contentValues.put("is_read", (Integer) 0);
            }
            if (callerInfo != null) {
                contentValues.put("name", callerInfo.name);
                contentValues.put(CACHED_NUMBER_TYPE, Integer.valueOf(callerInfo.numberType));
                contentValues.put(CACHED_NUMBER_LABEL, callerInfo.numberLabel);
            }
            if (callerInfo != null && callerInfo.person_id > 0) {
                if (callerInfo.normalizedNumber != null) {
                    cursorQuery = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[]{"_id"}, "contact_id =? AND data4 =?", new String[]{String.valueOf(callerInfo.person_id), callerInfo.normalizedNumber}, null);
                } else {
                    if (callerInfo.phoneNumber != null) {
                        str = callerInfo.phoneNumber;
                    }
                    cursorQuery = contentResolver.query(Uri.withAppendedPath(ContactsContract.CommonDataKinds.Callable.CONTENT_FILTER_URI, Uri.encode(str)), new String[]{"_id"}, "contact_id =?", new String[]{String.valueOf(callerInfo.person_id)}, null);
                }
                if (cursorQuery != null) {
                    try {
                        if (cursorQuery.getCount() > 0 && cursorQuery.moveToFirst()) {
                            contentResolver.update(ContactsContract.DataUsageFeedback.FEEDBACK_URI.buildUpon().appendPath(cursorQuery.getString(0)).appendQueryParameter("type", "call").build(), new ContentValues(), null, null);
                        }
                    } finally {
                        cursorQuery.close();
                    }
                }
            }
            Uri uriInsert = contentResolver.insert(CONTENT_URI, contentValues);
            removeExpiredEntries(context);
            return uriInsert;
        }

        public static String getLastOutgoingCall(Context context) {
            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursorQuery = null;
            try {
                cursorQuery = contentResolver.query(CONTENT_URI, new String[]{"number"}, "type = 2", null, "date DESC LIMIT 1");
                if (cursorQuery != null && cursorQuery.moveToFirst()) {
                    return cursorQuery.getString(0);
                }
                if (cursorQuery != null) {
                    cursorQuery.close();
                }
                return "";
            } finally {
                if (cursorQuery != null) {
                    cursorQuery.close();
                }
            }
        }

        private static void removeExpiredEntries(Context context) {
            context.getContentResolver().delete(CONTENT_URI, "_id IN (SELECT _id FROM calls ORDER BY date DESC LIMIT -1 OFFSET 500)", null);
        }
    }
}
