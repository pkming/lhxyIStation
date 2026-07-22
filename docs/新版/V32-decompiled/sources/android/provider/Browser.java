package android.provider;

import android.content.ActivityNotFoundException;
import android.content.ClipDescription;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.provider.BrowserContract;
import android.util.Log;
import android.webkit.WebIconDatabase;

/* JADX INFO: loaded from: classes.dex */
public class Browser {
    public static final String EXTRA_APPLICATION_ID = "com.android.browser.application_id";
    public static final String EXTRA_CREATE_NEW_TAB = "create_new_tab";
    public static final String EXTRA_HEADERS = "com.android.browser.headers";
    public static final String EXTRA_SHARE_FAVICON = "share_favicon";
    public static final String EXTRA_SHARE_SCREENSHOT = "share_screenshot";
    public static final int HISTORY_PROJECTION_BOOKMARK_INDEX = 4;
    public static final int HISTORY_PROJECTION_DATE_INDEX = 3;
    public static final int HISTORY_PROJECTION_FAVICON_INDEX = 6;
    public static final int HISTORY_PROJECTION_ID_INDEX = 0;
    public static final int HISTORY_PROJECTION_THUMBNAIL_INDEX = 7;
    public static final int HISTORY_PROJECTION_TITLE_INDEX = 5;
    public static final int HISTORY_PROJECTION_TOUCH_ICON_INDEX = 8;
    public static final int HISTORY_PROJECTION_URL_INDEX = 1;
    public static final int HISTORY_PROJECTION_VISITS_INDEX = 2;
    public static final String INITIAL_ZOOM_LEVEL = "browser.initialZoomLevel";
    private static final String LOGTAG = "browser";
    private static final int MAX_HISTORY_COUNT = 250;
    public static final int SEARCHES_PROJECTION_DATE_INDEX = 2;
    public static final int SEARCHES_PROJECTION_SEARCH_INDEX = 1;
    public static final int TRUNCATE_HISTORY_PROJECTION_ID_INDEX = 0;
    public static final int TRUNCATE_N_OLDEST = 5;
    public static final Uri BOOKMARKS_URI = Uri.parse("content://browser/bookmarks");
    public static final String[] HISTORY_PROJECTION = {"_id", "url", "visits", "date", "bookmark", "title", "favicon", "thumbnail", "touch_icon", "user_entered"};
    public static final String[] TRUNCATE_HISTORY_PROJECTION = {"_id", "date"};
    public static final Uri SEARCHES_URI = Uri.parse("content://browser/searches");
    public static final String[] SEARCHES_PROJECTION = {"_id", "search", "date"};

    public static class BookmarkColumns implements BaseColumns {
        public static final String BOOKMARK = "bookmark";
        public static final String CREATED = "created";
        public static final String DATE = "date";
        public static final String FAVICON = "favicon";
        public static final String THUMBNAIL = "thumbnail";
        public static final String TITLE = "title";
        public static final String TOUCH_ICON = "touch_icon";
        public static final String URL = "url";
        public static final String USER_ENTERED = "user_entered";
        public static final String VISITS = "visits";
    }

    public static class SearchColumns implements BaseColumns {
        public static final String DATE = "date";
        public static final String SEARCH = "search";

        @Deprecated
        public static final String URL = "url";
    }

    public static final void saveBookmark(Context context, String str, String str2) {
        Intent intent = new Intent("android.intent.action.INSERT", BOOKMARKS_URI);
        intent.putExtra("title", str);
        intent.putExtra("url", str2);
        context.startActivity(intent);
    }

    public static final void sendString(Context context, String str) {
        sendString(context, str, context.getString(17040414));
    }

    public static final void sendString(Context context, String str, String str2) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(ClipDescription.MIMETYPE_TEXT_PLAIN);
        intent.putExtra(Intent.EXTRA_TEXT, str);
        try {
            Intent intentCreateChooser = Intent.createChooser(intent, str2);
            intentCreateChooser.setFlags(268435456);
            context.startActivity(intentCreateChooser);
        } catch (ActivityNotFoundException unused) {
        }
    }

    public static final Cursor getAllBookmarks(ContentResolver contentResolver) throws IllegalStateException {
        return contentResolver.query(BrowserContract.Bookmarks.CONTENT_URI, new String[]{"url"}, "folder = 0", null, null);
    }

    public static final Cursor getAllVisitedUrls(ContentResolver contentResolver) throws IllegalStateException {
        return contentResolver.query(BrowserContract.Combined.CONTENT_URI, new String[]{"url"}, null, null, "created ASC");
    }

    private static final void addOrUrlEquals(StringBuilder sb) {
        sb.append(" OR url = ");
    }

    private static final Cursor getVisitedLike(ContentResolver contentResolver, String str) {
        StringBuilder sb;
        boolean z = false;
        if (str.startsWith("http://")) {
            str = str.substring(7);
        } else if (str.startsWith("https://")) {
            str = str.substring(8);
            z = true;
        }
        if (str.startsWith("www.")) {
            str = str.substring(4);
        }
        if (z) {
            sb = new StringBuilder("url = ");
            DatabaseUtils.appendEscapedSQLString(sb, "https://" + str);
            addOrUrlEquals(sb);
            DatabaseUtils.appendEscapedSQLString(sb, "https://www." + str);
        } else {
            StringBuilder sb2 = new StringBuilder("url = ");
            DatabaseUtils.appendEscapedSQLString(sb2, str);
            addOrUrlEquals(sb2);
            String str2 = "www." + str;
            DatabaseUtils.appendEscapedSQLString(sb2, str2);
            addOrUrlEquals(sb2);
            DatabaseUtils.appendEscapedSQLString(sb2, "http://" + str);
            addOrUrlEquals(sb2);
            DatabaseUtils.appendEscapedSQLString(sb2, "http://" + str2);
            sb = sb2;
        }
        return contentResolver.query(BrowserContract.History.CONTENT_URI, new String[]{"_id", "visits"}, sb.toString(), null, null);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v0 */
    /* JADX WARN: Type inference failed for: r2v1 */
    /* JADX WARN: Type inference failed for: r2v10 */
    /* JADX WARN: Type inference failed for: r2v11 */
    /* JADX WARN: Type inference failed for: r2v2, types: [android.database.Cursor] */
    /* JADX WARN: Type inference failed for: r2v3, types: [android.database.Cursor] */
    /* JADX WARN: Type inference failed for: r2v4 */
    /* JADX WARN: Type inference failed for: r2v5 */
    /* JADX WARN: Type inference failed for: r2v6 */
    /* JADX WARN: Type inference failed for: r2v8 */
    /* JADX WARN: Type inference failed for: r2v9 */
    public static final void updateVisitedHistory(ContentResolver contentResolver, String str, boolean z) throws Throwable {
        Cursor visitedLike;
        int i;
        long jCurrentTimeMillis = System.currentTimeMillis();
        ?? r2 = 0;
        r2 = 0;
        r2 = 0;
        try {
            try {
                visitedLike = getVisitedLike(contentResolver, str);
            } catch (Throwable th) {
                th = th;
            }
        } catch (IllegalStateException e) {
            e = e;
        }
        try {
            int i2 = 1;
            if (visitedLike.moveToFirst()) {
                ContentValues contentValues = new ContentValues();
                if (z) {
                    contentValues.put("visits", Integer.valueOf(visitedLike.getInt(1) + 1));
                } else {
                    contentValues.put("user_entered", (Integer) 1);
                }
                contentValues.put("date", Long.valueOf(jCurrentTimeMillis));
                contentResolver.update(ContentUris.withAppendedId(BrowserContract.History.CONTENT_URI, visitedLike.getLong(0)), contentValues, null, null);
            } else {
                truncateHistory(contentResolver);
                ContentValues contentValues2 = new ContentValues();
                if (z) {
                    i = 0;
                } else {
                    i = 1;
                    i2 = 0;
                }
                contentValues2.put("url", str);
                contentValues2.put("visits", Integer.valueOf(i2));
                contentValues2.put("date", Long.valueOf(jCurrentTimeMillis));
                contentValues2.put("title", str);
                contentValues2.put("created", (Integer) 0);
                contentValues2.put("user_entered", Integer.valueOf(i));
                contentResolver.insert(BrowserContract.History.CONTENT_URI, contentValues2);
                r2 = contentValues2;
            }
            if (visitedLike != null) {
                visitedLike.close();
            }
        } catch (IllegalStateException e2) {
            e = e2;
            r2 = visitedLike;
            Log.e(LOGTAG, "updateVisitedHistory", e);
            if (r2 != 0) {
                r2.close();
            }
        } catch (Throwable th2) {
            th = th2;
            r2 = visitedLike;
            if (r2 != 0) {
                r2.close();
            }
            throw th;
        }
    }

    public static final String[] getVisitedHistory(ContentResolver contentResolver) {
        String[] strArr;
        Cursor cursorQuery = null;
        try {
            try {
                cursorQuery = contentResolver.query(BrowserContract.History.CONTENT_URI, new String[]{"url"}, "visits > 0", null, null);
            } catch (IllegalStateException e) {
                Log.e(LOGTAG, "getVisitedHistory", e);
                strArr = new String[0];
                if (0 != 0) {
                }
            }
            if (cursorQuery == null) {
                return new String[0];
            }
            strArr = new String[cursorQuery.getCount()];
            int i = 0;
            while (cursorQuery.moveToNext()) {
                strArr[i] = cursorQuery.getString(0);
                i++;
            }
            if (cursorQuery != null) {
                cursorQuery.close();
            }
            return strArr;
        } finally {
            if (0 != 0) {
                cursorQuery.close();
            }
        }
    }

    public static final void truncateHistory(ContentResolver contentResolver) throws Throwable {
        Cursor cursorQuery;
        Cursor cursor = null;
        try {
            try {
                cursorQuery = contentResolver.query(BrowserContract.History.CONTENT_URI, new String[]{"_id", "url", "date"}, null, null, "date ASC");
            } catch (Throwable th) {
                th = th;
            }
        } catch (IllegalStateException e) {
            e = e;
        }
        try {
            if (cursorQuery.moveToFirst() && cursorQuery.getCount() >= 250) {
                WebIconDatabase webIconDatabase = WebIconDatabase.getInstance();
                for (int i = 0; i < 5; i++) {
                    contentResolver.delete(ContentUris.withAppendedId(BrowserContract.History.CONTENT_URI, cursorQuery.getLong(0)), null, null);
                    webIconDatabase.releaseIconForPageUrl(cursorQuery.getString(1));
                    if (!cursorQuery.moveToNext()) {
                        break;
                    }
                }
            }
            if (cursorQuery != null) {
                cursorQuery.close();
            }
        } catch (IllegalStateException e2) {
            e = e2;
            cursor = cursorQuery;
            Log.e(LOGTAG, "truncateHistory", e);
            if (cursor != null) {
                cursor.close();
            }
        } catch (Throwable th2) {
            th = th2;
            cursor = cursorQuery;
            if (cursor != null) {
                cursor.close();
            }
            throw th;
        }
    }

    public static final boolean canClearHistory(ContentResolver contentResolver) {
        Cursor cursorQuery = null;
        try {
            try {
                cursorQuery = contentResolver.query(BrowserContract.History.CONTENT_URI, new String[]{"_id", "visits"}, null, null, null);
                z = cursorQuery.getCount() > 0;
            } catch (IllegalStateException e) {
                Log.e(LOGTAG, "canClearHistory", e);
                if (cursorQuery != null) {
                }
            }
            if (cursorQuery != null) {
                cursorQuery.close();
            }
            return z;
        } catch (Throwable th) {
            if (cursorQuery != null) {
                cursorQuery.close();
            }
            throw th;
        }
    }

    public static final void clearHistory(ContentResolver contentResolver) throws Throwable {
        deleteHistoryWhere(contentResolver, null);
    }

    private static final void deleteHistoryWhere(ContentResolver contentResolver, String str) throws Throwable {
        Cursor cursorQuery;
        Cursor cursor = null;
        try {
            try {
                cursorQuery = contentResolver.query(BrowserContract.History.CONTENT_URI, new String[]{"url"}, str, null, null);
            } catch (Throwable th) {
                th = th;
            }
        } catch (IllegalStateException e) {
            e = e;
        }
        try {
            if (cursorQuery.moveToFirst()) {
                WebIconDatabase webIconDatabase = WebIconDatabase.getInstance();
                do {
                    webIconDatabase.releaseIconForPageUrl(cursorQuery.getString(0));
                } while (cursorQuery.moveToNext());
                contentResolver.delete(BrowserContract.History.CONTENT_URI, str, null);
            }
            if (cursorQuery != null) {
                cursorQuery.close();
            }
        } catch (IllegalStateException e2) {
            e = e2;
            cursor = cursorQuery;
            Log.e(LOGTAG, "deleteHistoryWhere", e);
            if (cursor != null) {
                cursor.close();
            }
        } catch (Throwable th2) {
            th = th2;
            cursor = cursorQuery;
            if (cursor != null) {
                cursor.close();
            }
            throw th;
        }
    }

    public static final void deleteHistoryTimeFrame(ContentResolver contentResolver, long j, long j2) throws Throwable {
        String str;
        if (-1 == j) {
            if (-1 == j2) {
                clearHistory(contentResolver);
                return;
            }
            str = "date < " + Long.toString(j2);
        } else if (-1 == j2) {
            str = "date >= " + Long.toString(j);
        } else {
            str = "date >= " + Long.toString(j) + " AND date < " + Long.toString(j2);
        }
        deleteHistoryWhere(contentResolver, str);
    }

    public static final void deleteFromHistory(ContentResolver contentResolver, String str) {
        contentResolver.delete(BrowserContract.History.CONTENT_URI, "url=?", new String[]{str});
    }

    public static final void addSearchUrl(ContentResolver contentResolver, String str) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("search", str);
        contentValues.put("date", Long.valueOf(System.currentTimeMillis()));
        contentResolver.insert(BrowserContract.Searches.CONTENT_URI, contentValues);
    }

    public static final void clearSearches(ContentResolver contentResolver) {
        try {
            contentResolver.delete(BrowserContract.Searches.CONTENT_URI, null, null);
        } catch (IllegalStateException e) {
            Log.e(LOGTAG, "clearSearches", e);
        }
    }

    public static final void requestAllIcons(ContentResolver contentResolver, String str, WebIconDatabase.IconListener iconListener) {
        WebIconDatabase.getInstance().bulkRequestIconForPageUrl(contentResolver, str, iconListener);
    }
}
