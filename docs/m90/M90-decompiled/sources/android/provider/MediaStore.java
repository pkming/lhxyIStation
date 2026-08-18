package android.provider;

import android.app.backup.FullBackup;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.Contacts;
import android.util.Log;
import com.unisound.common.r;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* JADX INFO: loaded from: classes.dex */
public final class MediaStore {
    public static final String ACTION_IMAGE_CAPTURE = "android.media.action.IMAGE_CAPTURE";
    public static final String ACTION_IMAGE_CAPTURE_SECURE = "android.media.action.IMAGE_CAPTURE_SECURE";
    public static final String ACTION_MTP_SESSION_END = "android.provider.action.MTP_SESSION_END";
    public static final String ACTION_VIDEO_CAPTURE = "android.media.action.VIDEO_CAPTURE";
    public static final String AUTHORITY = "media";
    private static final String CONTENT_AUTHORITY_SLASH = "content://media/";
    public static final String EXTRA_BD_FOLDER_PLAY_MODE = "android.intent.extra.bdfolderplaymode";
    public static final String EXTRA_DURATION_LIMIT = "android.intent.extra.durationLimit";
    public static final String EXTRA_FINISH_ON_COMPLETION = "android.intent.extra.finishOnCompletion";
    public static final String EXTRA_FULL_SCREEN = "android.intent.extra.fullScreen";
    public static final String EXTRA_MEDIA_ALBUM = "android.intent.extra.album";
    public static final String EXTRA_MEDIA_ARTIST = "android.intent.extra.artist";
    public static final String EXTRA_MEDIA_FOCUS = "android.intent.extra.focus";
    public static final String EXTRA_MEDIA_TITLE = "android.intent.extra.title";
    public static final String EXTRA_OUTPUT = "output";
    public static final String EXTRA_SCREEN_ORIENTATION = "android.intent.extra.screenOrientation";
    public static final String EXTRA_SHOW_ACTION_ICONS = "android.intent.extra.showActionIcons";
    public static final String EXTRA_SIZE_LIMIT = "android.intent.extra.sizeLimit";
    public static final String EXTRA_VIDEO_QUALITY = "android.intent.extra.videoQuality";
    public static final String INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH = "android.media.action.MEDIA_PLAY_FROM_SEARCH";
    public static final String INTENT_ACTION_MEDIA_SEARCH = "android.intent.action.MEDIA_SEARCH";

    @Deprecated
    public static final String INTENT_ACTION_MUSIC_PLAYER = "android.intent.action.MUSIC_PLAYER";
    public static final String INTENT_ACTION_STILL_IMAGE_CAMERA = "android.media.action.STILL_IMAGE_CAMERA";
    public static final String INTENT_ACTION_STILL_IMAGE_CAMERA_SECURE = "android.media.action.STILL_IMAGE_CAMERA_SECURE";
    public static final String INTENT_ACTION_TEXT_OPEN_FROM_SEARCH = "android.media.action.TEXT_OPEN_FROM_SEARCH";
    public static final String INTENT_ACTION_VIDEO_CAMERA = "android.media.action.VIDEO_CAMERA";
    public static final String INTENT_ACTION_VIDEO_PLAY_FROM_SEARCH = "android.media.action.VIDEO_PLAY_FROM_SEARCH";
    public static final String MEDIA_IGNORE_FILENAME = ".nomedia";
    public static final String MEDIA_SCANNER_VOLUME = "volume";
    public static final String PARAM_DELETE_DATA = "deletedata";
    public static final String PLAYLIST_TYPE = "android.intent.extra.playListType";
    public static final String PLAYLIST_TYPE_CUR_FOLDER = "curFolder";
    public static final String PLAYLIST_TYPE_MEDIA_PROVIDER = "mediaProvider";
    private static final String TAG = "MediaStore";
    public static final String UNHIDE_CALL = "unhide";
    public static final String UNKNOWN_STRING = "<unknown>";

    public interface MediaColumns extends BaseColumns {
        public static final String DATA = "_data";
        public static final String DATE_ADDED = "date_added";
        public static final String DATE_MODIFIED = "date_modified";
        public static final String DISPLAY_NAME = "_display_name";
        public static final String HEIGHT = "height";
        public static final String IS_DRM = "is_drm";
        public static final String MEDIA_SCANNER_NEW_OBJECT_ID = "media_scanner_new_object_id";
        public static final String MIME_TYPE = "mime_type";
        public static final String SIZE = "_size";
        public static final String TITLE = "title";
        public static final String WIDTH = "width";
    }

    public static final class Files {

        public interface FileColumns extends MediaColumns {
            public static final String FORMAT = "format";
            public static final String MEDIA_TYPE = "media_type";
            public static final int MEDIA_TYPE_AUDIO = 2;
            public static final int MEDIA_TYPE_IMAGE = 1;
            public static final int MEDIA_TYPE_NONE = 0;
            public static final int MEDIA_TYPE_PLAYLIST = 4;
            public static final int MEDIA_TYPE_VIDEO = 3;
            public static final String MIME_TYPE = "mime_type";
            public static final String PARENT = "parent";
            public static final String STORAGE_ID = "storage_id";
            public static final String TITLE = "title";
        }

        public static Uri getContentUri(String str) {
            return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + str + "/file");
        }

        public static final Uri getContentUri(String str, long j) {
            return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + str + "/file/" + j);
        }

        public static Uri getMtpObjectsUri(String str) {
            return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + str + "/object");
        }

        public static final Uri getMtpObjectsUri(String str, long j) {
            return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + str + "/object/" + j);
        }

        public static final Uri getMtpReferencesUri(String str, long j) {
            return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + str + "/object/" + j + "/references");
        }
    }

    private static class InternalThumbnails implements BaseColumns {
        static final int DEFAULT_GROUP_ID = 0;
        private static final int FULL_SCREEN_KIND = 2;
        private static final int MICRO_KIND = 3;
        private static final int MINI_KIND = 1;
        private static byte[] sThumbBuf;
        private static final String[] PROJECTION = {"_id", "_data"};
        private static final Object sThumbBufLock = new Object();

        private InternalThumbnails() {
        }

        private static Bitmap getMiniThumbFromFile(Cursor cursor, Uri uri, ContentResolver contentResolver, BitmapFactory.Options options) {
            Bitmap bitmap;
            Uri uriWithAppendedId;
            Uri uri2 = null;
            bitmapDecodeFileDescriptor = null;
            bitmapDecodeFileDescriptor = null;
            Bitmap bitmapDecodeFileDescriptor = null;
            Uri uri3 = null;
            Uri uri4 = null;
            try {
                long j = cursor.getLong(0);
                cursor.getString(1);
                uriWithAppendedId = ContentUris.withAppendedId(uri, j);
            } catch (FileNotFoundException e) {
                e = e;
                bitmap = null;
            } catch (IOException e2) {
                e = e2;
                bitmap = null;
            } catch (OutOfMemoryError e3) {
                e = e3;
                bitmap = null;
            }
            try {
                ParcelFileDescriptor parcelFileDescriptorOpenFileDescriptor = contentResolver.openFileDescriptor(uriWithAppendedId, FullBackup.ROOT_TREE_TOKEN);
                bitmapDecodeFileDescriptor = BitmapFactory.decodeFileDescriptor(parcelFileDescriptorOpenFileDescriptor.getFileDescriptor(), null, options);
                parcelFileDescriptorOpenFileDescriptor.close();
                return bitmapDecodeFileDescriptor;
            } catch (FileNotFoundException e4) {
                e = e4;
                Bitmap bitmap2 = bitmapDecodeFileDescriptor;
                uri3 = uriWithAppendedId;
                bitmap = bitmap2;
                Log.e(MediaStore.TAG, "couldn't open thumbnail " + uri3 + "; " + e);
                return bitmap;
            } catch (IOException e5) {
                e = e5;
                Bitmap bitmap3 = bitmapDecodeFileDescriptor;
                uri4 = uriWithAppendedId;
                bitmap = bitmap3;
                Log.e(MediaStore.TAG, "couldn't open thumbnail " + uri4 + "; " + e);
                return bitmap;
            } catch (OutOfMemoryError e6) {
                e = e6;
                Bitmap bitmap4 = bitmapDecodeFileDescriptor;
                uri2 = uriWithAppendedId;
                bitmap = bitmap4;
                Log.e(MediaStore.TAG, "failed to allocate memory for thumbnail " + uri2 + "; " + e);
                return bitmap;
            }
        }

        static void cancelThumbnailRequest(ContentResolver contentResolver, long j, Uri uri, long j2) {
            Cursor cursorQuery = contentResolver.query(uri.buildUpon().appendQueryParameter(r.z, "1").appendQueryParameter("orig_id", String.valueOf(j)).appendQueryParameter(Contacts.GroupMembership.GROUP_ID, String.valueOf(j2)).build(), PROJECTION, null, null, null);
            if (cursorQuery != null) {
                cursorQuery.close();
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Removed duplicated region for block: B:139:0x0212  */
        /* JADX WARN: Removed duplicated region for block: B:143:0x021b  */
        /* JADX WARN: Removed duplicated region for block: B:153:0x0101 A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:51:0x00b5  */
        /* JADX WARN: Removed duplicated region for block: B:52:0x00ba  */
        /* JADX WARN: Removed duplicated region for block: B:55:0x00e6 A[Catch: all -> 0x01fe, SQLiteException -> 0x0202, TryCatch #19 {SQLiteException -> 0x0202, all -> 0x01fe, blocks: (B:53:0x00bf, B:55:0x00e6, B:56:0x00e9), top: B:163:0x00bf }] */
        /* JADX WARN: Removed duplicated region for block: B:58:0x00f8  */
        /* JADX WARN: Type inference failed for: r18v0 */
        /* JADX WARN: Type inference failed for: r18v1, types: [android.graphics.Bitmap] */
        /* JADX WARN: Type inference failed for: r18v10 */
        /* JADX WARN: Type inference failed for: r18v17 */
        /* JADX WARN: Type inference failed for: r18v19 */
        /* JADX WARN: Type inference failed for: r18v2 */
        /* JADX WARN: Type inference failed for: r18v22 */
        /* JADX WARN: Type inference failed for: r18v23 */
        /* JADX WARN: Type inference failed for: r18v24 */
        /* JADX WARN: Type inference failed for: r18v25 */
        /* JADX WARN: Type inference failed for: r18v30 */
        /* JADX WARN: Type inference failed for: r18v31 */
        /* JADX WARN: Type inference failed for: r18v32 */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        static android.graphics.Bitmap getThumbnail(android.content.ContentResolver r20, long r21, long r23, int r25, android.graphics.BitmapFactory.Options r26, android.net.Uri r27, boolean r28) throws java.lang.Throwable {
            /*
                Method dump skipped, instruction units count: 546
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: android.provider.MediaStore.InternalThumbnails.getThumbnail(android.content.ContentResolver, long, long, int, android.graphics.BitmapFactory$Options, android.net.Uri, boolean):android.graphics.Bitmap");
        }
    }

    public static final class Images {

        public interface ImageColumns extends MediaColumns {
            public static final String BUCKET_DISPLAY_NAME = "bucket_display_name";
            public static final String BUCKET_ID = "bucket_id";
            public static final String DATE_TAKEN = "datetaken";
            public static final String DESCRIPTION = "description";
            public static final String IS_PRIVATE = "isprivate";
            public static final String LATITUDE = "latitude";
            public static final String LONGITUDE = "longitude";
            public static final String MINI_THUMB_MAGIC = "mini_thumb_magic";
            public static final String ORIENTATION = "orientation";
            public static final String PICASA_ID = "picasa_id";
        }

        public static final class Media implements ImageColumns {
            public static final String CONTENT_TYPE = "vnd.android.cursor.dir/image";
            public static final String DEFAULT_SORT_ORDER = "bucket_display_name";
            public static final Uri INTERNAL_CONTENT_URI = getContentUri("internal");
            public static final Uri EXTERNAL_CONTENT_URI = getContentUri("external");

            public static final Cursor query(ContentResolver contentResolver, Uri uri, String[] strArr) {
                return contentResolver.query(uri, strArr, null, null, "bucket_display_name");
            }

            public static final Cursor query(ContentResolver contentResolver, Uri uri, String[] strArr, String str, String str2) {
                if (str2 == null) {
                    str2 = "bucket_display_name";
                }
                return contentResolver.query(uri, strArr, str, null, str2);
            }

            public static final Cursor query(ContentResolver contentResolver, Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
                if (str2 == null) {
                    str2 = "bucket_display_name";
                }
                return contentResolver.query(uri, strArr, str, strArr2, str2);
            }

            public static final Bitmap getBitmap(ContentResolver contentResolver, Uri uri) throws Throwable {
                InputStream inputStreamOpenInputStream = contentResolver.openInputStream(uri);
                Bitmap bitmapDecodeStream = BitmapFactory.decodeStream(inputStreamOpenInputStream);
                inputStreamOpenInputStream.close();
                return bitmapDecodeStream;
            }

            public static final String insertImage(ContentResolver contentResolver, String str, String str2, String str3) throws FileNotFoundException {
                FileInputStream fileInputStream = new FileInputStream(str);
                try {
                    Bitmap bitmapDecodeFile = BitmapFactory.decodeFile(str);
                    String strInsertImage = insertImage(contentResolver, bitmapDecodeFile, str2, str3);
                    bitmapDecodeFile.recycle();
                    return strInsertImage;
                } finally {
                    try {
                        fileInputStream.close();
                    } catch (IOException unused) {
                    }
                }
            }

            private static final Bitmap StoreThumbnail(ContentResolver contentResolver, Bitmap bitmap, long j, float f, float f2, int i) {
                Matrix matrix = new Matrix();
                matrix.setScale(f / bitmap.getWidth(), f2 / bitmap.getHeight());
                Bitmap bitmapCreateBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                ContentValues contentValues = new ContentValues(4);
                contentValues.put("kind", Integer.valueOf(i));
                contentValues.put("image_id", Integer.valueOf((int) j));
                contentValues.put("height", Integer.valueOf(bitmapCreateBitmap.getHeight()));
                contentValues.put("width", Integer.valueOf(bitmapCreateBitmap.getWidth()));
                try {
                    OutputStream outputStreamOpenOutputStream = contentResolver.openOutputStream(contentResolver.insert(Thumbnails.EXTERNAL_CONTENT_URI, contentValues));
                    bitmapCreateBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStreamOpenOutputStream);
                    outputStreamOpenOutputStream.close();
                    return bitmapCreateBitmap;
                } catch (FileNotFoundException | IOException unused) {
                    return null;
                }
            }

            public static final String insertImage(ContentResolver contentResolver, Bitmap bitmap, String str, String str2) {
                Uri uriInsert;
                ContentValues contentValues = new ContentValues();
                contentValues.put("title", str);
                contentValues.put("description", str2);
                contentValues.put("mime_type", "image/jpeg");
                try {
                    uriInsert = contentResolver.insert(EXTERNAL_CONTENT_URI, contentValues);
                    try {
                    } catch (Exception e) {
                        e = e;
                        Log.e(MediaStore.TAG, "Failed to insert image", e);
                        if (uriInsert != null) {
                            contentResolver.delete(uriInsert, null, null);
                            uriInsert = null;
                        }
                    }
                } catch (Exception e2) {
                    e = e2;
                    uriInsert = null;
                }
                if (bitmap != null) {
                    OutputStream outputStreamOpenOutputStream = contentResolver.openOutputStream(uriInsert);
                    try {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStreamOpenOutputStream);
                        outputStreamOpenOutputStream.close();
                        long id = ContentUris.parseId(uriInsert);
                        StoreThumbnail(contentResolver, Thumbnails.getThumbnail(contentResolver, id, 1, null), id, 50.0f, 50.0f, 3);
                    } catch (Throwable th) {
                        outputStreamOpenOutputStream.close();
                        throw th;
                    }
                } else {
                    Log.e(MediaStore.TAG, "Failed to create thumbnail, removing original");
                    contentResolver.delete(uriInsert, null, null);
                    uriInsert = null;
                }
                if (uriInsert != null) {
                    return uriInsert.toString();
                }
                return null;
            }

            public static Uri getContentUri(String str) {
                return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + str + "/images/media");
            }
        }

        public static class Thumbnails implements BaseColumns {
            public static final String DATA = "_data";
            public static final String DEFAULT_SORT_ORDER = "image_id ASC";
            public static final int FULL_SCREEN_KIND = 2;
            public static final String HEIGHT = "height";
            public static final String IMAGE_ID = "image_id";
            public static final String KIND = "kind";
            public static final int MICRO_KIND = 3;
            public static final int MINI_KIND = 1;
            public static final String THUMB_DATA = "thumb_data";
            public static final String WIDTH = "width";
            public static final Uri INTERNAL_CONTENT_URI = getContentUri("internal");
            public static final Uri EXTERNAL_CONTENT_URI = getContentUri("external");

            public static final Cursor query(ContentResolver contentResolver, Uri uri, String[] strArr) {
                return contentResolver.query(uri, strArr, null, null, DEFAULT_SORT_ORDER);
            }

            public static final Cursor queryMiniThumbnails(ContentResolver contentResolver, Uri uri, int i, String[] strArr) {
                return contentResolver.query(uri, strArr, "kind = " + i, null, DEFAULT_SORT_ORDER);
            }

            public static final Cursor queryMiniThumbnail(ContentResolver contentResolver, long j, int i, String[] strArr) {
                return contentResolver.query(EXTERNAL_CONTENT_URI, strArr, "image_id = " + j + " AND kind = " + i, null, null);
            }

            public static void cancelThumbnailRequest(ContentResolver contentResolver, long j) {
                InternalThumbnails.cancelThumbnailRequest(contentResolver, j, EXTERNAL_CONTENT_URI, 0L);
            }

            public static Bitmap getThumbnail(ContentResolver contentResolver, long j, int i, BitmapFactory.Options options) {
                return InternalThumbnails.getThumbnail(contentResolver, j, 0L, i, options, EXTERNAL_CONTENT_URI, false);
            }

            public static void cancelThumbnailRequest(ContentResolver contentResolver, long j, long j2) {
                InternalThumbnails.cancelThumbnailRequest(contentResolver, j, EXTERNAL_CONTENT_URI, j2);
            }

            public static Bitmap getThumbnail(ContentResolver contentResolver, long j, long j2, int i, BitmapFactory.Options options) {
                return InternalThumbnails.getThumbnail(contentResolver, j, j2, i, options, EXTERNAL_CONTENT_URI, false);
            }

            public static Uri getContentUri(String str) {
                return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + str + "/images/thumbnails");
            }
        }
    }

    public static final class Audio {

        public interface AlbumColumns {
            public static final String ALBUM = "album";
            public static final String ALBUM_ART = "album_art";
            public static final String ALBUM_ID = "album_id";
            public static final String ALBUM_KEY = "album_key";
            public static final String ARTIST = "artist";
            public static final String FIRST_YEAR = "minyear";
            public static final String LAST_YEAR = "maxyear";
            public static final String NUMBER_OF_SONGS = "numsongs";
            public static final String NUMBER_OF_SONGS_FOR_ARTIST = "numsongs_by_artist";
        }

        public interface ArtistColumns {
            public static final String ARTIST = "artist";
            public static final String ARTIST_KEY = "artist_key";
            public static final String NUMBER_OF_ALBUMS = "number_of_albums";
            public static final String NUMBER_OF_TRACKS = "number_of_tracks";
        }

        public interface AudioColumns extends MediaColumns {
            public static final String ALBUM = "album";
            public static final String ALBUM_ARTIST = "album_artist";
            public static final String ALBUM_ID = "album_id";
            public static final String ALBUM_KEY = "album_key";
            public static final String ARTIST = "artist";
            public static final String ARTIST_ID = "artist_id";
            public static final String ARTIST_KEY = "artist_key";
            public static final String BOOKMARK = "bookmark";
            public static final String COMPILATION = "compilation";
            public static final String COMPOSER = "composer";
            public static final String DURATION = "duration";
            public static final String GENRE = "genre";
            public static final String IS_ALARM = "is_alarm";
            public static final String IS_MUSIC = "is_music";
            public static final String IS_NOTIFICATION = "is_notification";
            public static final String IS_PODCAST = "is_podcast";
            public static final String IS_RINGTONE = "is_ringtone";
            public static final String TITLE_KEY = "title_key";
            public static final String TRACK = "track";
            public static final String YEAR = "year";
        }

        public interface GenresColumns {
            public static final String NAME = "name";
        }

        public interface PlaylistsColumns {
            public static final String DATA = "_data";
            public static final String DATE_ADDED = "date_added";
            public static final String DATE_MODIFIED = "date_modified";
            public static final String NAME = "name";
        }

        public static String keyFor(String str) {
            if (str == null) {
                return null;
            }
            if (str.equals(MediaStore.UNKNOWN_STRING)) {
                return "\u0001";
            }
            boolean zStartsWith = str.startsWith("\u0001");
            String lowerCase = str.trim().toLowerCase();
            if (lowerCase.startsWith("the ")) {
                lowerCase = lowerCase.substring(4);
            }
            if (lowerCase.startsWith("an ")) {
                lowerCase = lowerCase.substring(3);
            }
            if (lowerCase.startsWith("a ")) {
                lowerCase = lowerCase.substring(2);
            }
            if (lowerCase.endsWith(", the") || lowerCase.endsWith(",the") || lowerCase.endsWith(", an") || lowerCase.endsWith(",an") || lowerCase.endsWith(", a") || lowerCase.endsWith(",a")) {
                lowerCase = lowerCase.substring(0, lowerCase.lastIndexOf(44));
            }
            String strTrim = lowerCase.replaceAll("[\\[\\]\\(\\)\"'.,?!]", "").trim();
            if (strTrim.length() <= 0) {
                return "";
            }
            StringBuilder sb = new StringBuilder();
            sb.append('.');
            int length = strTrim.length();
            for (int i = 0; i < length; i++) {
                sb.append(strTrim.charAt(i));
                sb.append('.');
            }
            String collationKey = DatabaseUtils.getCollationKey(sb.toString());
            return zStartsWith ? "\u0001" + collationKey : collationKey;
        }

        public static final class Media implements AudioColumns {
            public static final String CONTENT_TYPE = "vnd.android.cursor.dir/audio";
            public static final String DEFAULT_SORT_ORDER = "title_key";
            public static final Uri EXTERNAL_CONTENT_URI;
            private static final String[] EXTERNAL_PATHS;
            public static final String EXTRA_MAX_BYTES = "android.provider.MediaStore.extra.MAX_BYTES";
            public static final Uri INTERNAL_CONTENT_URI;
            public static final String RECORD_SOUND_ACTION = "android.provider.MediaStore.RECORD_SOUND";

            static {
                String str = System.getenv("SECONDARY_STORAGE");
                if (str != null) {
                    EXTERNAL_PATHS = str.split(":");
                } else {
                    EXTERNAL_PATHS = new String[0];
                }
                INTERNAL_CONTENT_URI = getContentUri("internal");
                EXTERNAL_CONTENT_URI = getContentUri("external");
            }

            public static Uri getContentUri(String str) {
                return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + str + "/audio/media");
            }

            public static Uri getContentUriForPath(String str) {
                for (String str2 : EXTERNAL_PATHS) {
                    if (str.startsWith(str2)) {
                        return EXTERNAL_CONTENT_URI;
                    }
                }
                return str.startsWith(Environment.getExternalStorageDirectory().getPath()) ? EXTERNAL_CONTENT_URI : INTERNAL_CONTENT_URI;
            }
        }

        public static final class Genres implements BaseColumns, GenresColumns {
            public static final String CONTENT_TYPE = "vnd.android.cursor.dir/genre";
            public static final String DEFAULT_SORT_ORDER = "name";
            public static final String ENTRY_CONTENT_TYPE = "vnd.android.cursor.item/genre";
            public static final Uri INTERNAL_CONTENT_URI = getContentUri("internal");
            public static final Uri EXTERNAL_CONTENT_URI = getContentUri("external");

            public static Uri getContentUri(String str) {
                return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + str + "/audio/genres");
            }

            public static Uri getContentUriForAudioId(String str, int i) {
                return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + str + "/audio/media/" + i + "/genres");
            }

            public static final class Members implements AudioColumns {
                public static final String AUDIO_ID = "audio_id";
                public static final String CONTENT_DIRECTORY = "members";
                public static final String DEFAULT_SORT_ORDER = "title_key";
                public static final String GENRE_ID = "genre_id";

                public static final Uri getContentUri(String str, long j) {
                    return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + str + "/audio/genres/" + j + "/members");
                }
            }
        }

        public static final class Playlists implements BaseColumns, PlaylistsColumns {
            public static final String CONTENT_TYPE = "vnd.android.cursor.dir/playlist";
            public static final String DEFAULT_SORT_ORDER = "name";
            public static final String ENTRY_CONTENT_TYPE = "vnd.android.cursor.item/playlist";
            public static final Uri INTERNAL_CONTENT_URI = getContentUri("internal");
            public static final Uri EXTERNAL_CONTENT_URI = getContentUri("external");

            public static Uri getContentUri(String str) {
                return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + str + "/audio/playlists");
            }

            public static final class Members implements AudioColumns {
                public static final String AUDIO_ID = "audio_id";
                public static final String CONTENT_DIRECTORY = "members";
                public static final String DEFAULT_SORT_ORDER = "play_order";
                public static final String PLAYLIST_ID = "playlist_id";
                public static final String PLAY_ORDER = "play_order";
                public static final String _ID = "_id";

                public static final Uri getContentUri(String str, long j) {
                    return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + str + "/audio/playlists/" + j + "/members");
                }

                public static final boolean moveItem(ContentResolver contentResolver, long j, int i, int i2) {
                    Uri uriBuild = getContentUri("external", j).buildUpon().appendEncodedPath(String.valueOf(i)).appendQueryParameter("move", "true").build();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("play_order", Integer.valueOf(i2));
                    return contentResolver.update(uriBuild, contentValues, null, null) != 0;
                }
            }
        }

        public static final class Artists implements BaseColumns, ArtistColumns {
            public static final String CONTENT_TYPE = "vnd.android.cursor.dir/artists";
            public static final String DEFAULT_SORT_ORDER = "artist_key";
            public static final String ENTRY_CONTENT_TYPE = "vnd.android.cursor.item/artist";
            public static final Uri INTERNAL_CONTENT_URI = getContentUri("internal");
            public static final Uri EXTERNAL_CONTENT_URI = getContentUri("external");

            public static Uri getContentUri(String str) {
                return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + str + "/audio/artists");
            }

            public static final class Albums implements AlbumColumns {
                public static final Uri getContentUri(String str, long j) {
                    return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + str + "/audio/artists/" + j + "/albums");
                }
            }
        }

        public static final class Albums implements BaseColumns, AlbumColumns {
            public static final String CONTENT_TYPE = "vnd.android.cursor.dir/albums";
            public static final String DEFAULT_SORT_ORDER = "album_key";
            public static final String ENTRY_CONTENT_TYPE = "vnd.android.cursor.item/album";
            public static final Uri INTERNAL_CONTENT_URI = getContentUri("internal");
            public static final Uri EXTERNAL_CONTENT_URI = getContentUri("external");

            public static Uri getContentUri(String str) {
                return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + str + "/audio/albums");
            }
        }
    }

    public static final class Video {
        public static final String DEFAULT_SORT_ORDER = "_display_name";

        public interface VideoColumns extends MediaColumns {
            public static final String ALBUM = "album";
            public static final String ARTIST = "artist";
            public static final String BOOKMARK = "bookmark";
            public static final String BUCKET_DISPLAY_NAME = "bucket_display_name";
            public static final String BUCKET_ID = "bucket_id";
            public static final String CATEGORY = "category";
            public static final String DATE_TAKEN = "datetaken";
            public static final String DESCRIPTION = "description";
            public static final String DURATION = "duration";
            public static final String IS_PRIVATE = "isprivate";
            public static final String LANGUAGE = "language";
            public static final String LATITUDE = "latitude";
            public static final String LONGITUDE = "longitude";
            public static final String MINI_THUMB_MAGIC = "mini_thumb_magic";
            public static final String RESOLUTION = "resolution";
            public static final String TAGS = "tags";
        }

        public static final Cursor query(ContentResolver contentResolver, Uri uri, String[] strArr) {
            return contentResolver.query(uri, strArr, null, null, "_display_name");
        }

        public static final class Media implements VideoColumns {
            public static final String CONTENT_TYPE = "vnd.android.cursor.dir/video";
            public static final String DEFAULT_SORT_ORDER = "title";
            public static final Uri INTERNAL_CONTENT_URI = getContentUri("internal");
            public static final Uri EXTERNAL_CONTENT_URI = getContentUri("external");

            public static Uri getContentUri(String str) {
                return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + str + "/video/media");
            }
        }

        public static class Thumbnails implements BaseColumns {
            public static final String DATA = "_data";
            public static final String DEFAULT_SORT_ORDER = "video_id ASC";
            public static final int FULL_SCREEN_KIND = 2;
            public static final String HEIGHT = "height";
            public static final String KIND = "kind";
            public static final int MICRO_KIND = 3;
            public static final int MINI_KIND = 1;
            public static final String VIDEO_ID = "video_id";
            public static final String WIDTH = "width";
            public static final Uri INTERNAL_CONTENT_URI = getContentUri("internal");
            public static final Uri EXTERNAL_CONTENT_URI = getContentUri("external");

            public static void cancelThumbnailRequest(ContentResolver contentResolver, long j) {
                InternalThumbnails.cancelThumbnailRequest(contentResolver, j, EXTERNAL_CONTENT_URI, 0L);
            }

            public static Bitmap getThumbnail(ContentResolver contentResolver, long j, int i, BitmapFactory.Options options) {
                return InternalThumbnails.getThumbnail(contentResolver, j, 0L, i, options, EXTERNAL_CONTENT_URI, true);
            }

            public static Bitmap getThumbnail(ContentResolver contentResolver, long j, long j2, int i, BitmapFactory.Options options) {
                return InternalThumbnails.getThumbnail(contentResolver, j, j2, i, options, EXTERNAL_CONTENT_URI, true);
            }

            public static void cancelThumbnailRequest(ContentResolver contentResolver, long j, long j2) {
                InternalThumbnails.cancelThumbnailRequest(contentResolver, j, EXTERNAL_CONTENT_URI, j2);
            }

            public static Uri getContentUri(String str) {
                return Uri.parse(MediaStore.CONTENT_AUTHORITY_SLASH + str + "/video/thumbnails");
            }
        }
    }

    public static Uri getMediaScannerUri() {
        return Uri.parse("content://media/none/media_scanner");
    }

    public static String getVersion(Context context) {
        Cursor cursorQuery = context.getContentResolver().query(Uri.parse("content://media/none/version"), null, null, null, null);
        if (cursorQuery == null) {
            return null;
        }
        try {
            if (cursorQuery.moveToFirst()) {
                return cursorQuery.getString(0);
            }
            return null;
        } finally {
            cursorQuery.close();
        }
    }
}
