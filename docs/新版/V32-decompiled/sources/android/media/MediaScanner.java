package android.media;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.IContentProvider;
import android.database.Cursor;
import android.database.SQLException;
import android.drm.DrmManagerClient;
import android.graphics.BitmapFactory;
import android.media.MediaFile;
import android.mtp.MtpConstants;
import android.net.Uri;
import android.os.Environment;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.provider.MediaStore;
import android.provider.Settings;
import android.sax.ElementListener;
import android.sax.RootElement;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import libcore.io.ErrnoException;
import libcore.io.Libcore;
import libcore.io.OsConstants;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: classes.dex */
public class MediaScanner {
    private static final String ALARMS_DIR = "/alarms/";
    private static final int DATE_MODIFIED_PLAYLISTS_COLUMN_INDEX = 2;
    private static final String DEFAULT_RINGTONE_PROPERTY_PREFIX = "ro.config.";
    private static final boolean ENABLE_BULK_INSERTS = true;
    private static final int FILES_PRESCAN_DATE_MODIFIED_COLUMN_INDEX = 3;
    private static final int FILES_PRESCAN_FORMAT_COLUMN_INDEX = 2;
    private static final int FILES_PRESCAN_ID_COLUMN_INDEX = 0;
    private static final int FILES_PRESCAN_PATH_COLUMN_INDEX = 1;
    private static final String[] FILES_PRESCAN_PROJECTION;
    private static final String[] ID3_GENRES;
    private static final int ID_PLAYLISTS_COLUMN_INDEX = 0;
    private static final String[] ID_PROJECTION;
    private static final String MUSIC_DIR = "/music/";
    private static final String NOTIFICATIONS_DIR = "/notifications/";
    private static final int PATH_PLAYLISTS_COLUMN_INDEX = 1;
    private static final String[] PLAYLIST_MEMBERS_PROJECTION;
    private static final String PODCAST_DIR = "/podcasts/";
    private static final String RINGTONES_DIR = "/ringtones/";
    private static final String TAG = "MediaScanner";
    private Uri mAudioUri;
    private final BitmapFactory.Options mBitmapOptions;
    private boolean mCaseInsensitivePaths;
    private final MyMediaScannerClient mClient;
    private Context mContext;
    private String mDefaultAlarmAlertFilename;
    private boolean mDefaultAlarmSet;
    private String mDefaultNotificationFilename;
    private boolean mDefaultNotificationSet;
    private String mDefaultRingtoneFilename;
    private boolean mDefaultRingtoneSet;
    private DrmManagerClient mDrmManagerClient;
    private final boolean mExternalIsEmulated;
    private final String mExternalStoragePath;
    private Uri mFilesUri;
    private Uri mFilesUriNoNotify;
    private Uri mImagesUri;
    private MediaInserter mMediaInserter;
    private IContentProvider mMediaProvider;
    private int mMtpObjectHandle;
    private int mNativeContext;
    private int mOriginalCount;
    private String mPackageName;
    private ArrayList<FileEntry> mPlayLists;
    private ArrayList<PlaylistEntry> mPlaylistEntries;
    private Uri mPlaylistsUri;
    private boolean mProcessGenres;
    private boolean mProcessPlaylists;
    private Uri mThumbsUri;
    private Uri mVideoUri;
    private boolean mWasEmptyPriorToScan = false;

    private final native void native_finalize();

    private static final native void native_init();

    private final native void native_setup();

    private native void processDirectory(String str, MediaScannerClient mediaScannerClient);

    /* JADX INFO: Access modifiers changed from: private */
    public native void processFile(String str, String str2, MediaScannerClient mediaScannerClient);

    public native byte[] extractAlbumArt(FileDescriptor fileDescriptor);

    public native void setLocale(String str);

    static {
        System.loadLibrary("media_jni");
        native_init();
        FILES_PRESCAN_PROJECTION = new String[]{"_id", "_data", MediaStore.Files.FileColumns.FORMAT, "date_modified"};
        ID_PROJECTION = new String[]{"_id"};
        PLAYLIST_MEMBERS_PROJECTION = new String[]{MediaStore.Audio.Playlists.Members.PLAYLIST_ID};
        ID3_GENRES = new String[]{"Blues", "Classic Rock", "Country", "Dance", "Disco", "Funk", "Grunge", "Hip-Hop", "Jazz", "Metal", "New Age", "Oldies", "Other", "Pop", "R&B", "Rap", "Reggae", "Rock", "Techno", "Industrial", "Alternative", "Ska", "Death Metal", "Pranks", "Soundtrack", "Euro-Techno", "Ambient", "Trip-Hop", "Vocal", "Jazz+Funk", "Fusion", "Trance", "Classical", "Instrumental", "Acid", "House", "Game", "Sound Clip", "Gospel", "Noise", "AlternRock", "Bass", "Soul", "Punk", "Space", "Meditative", "Instrumental Pop", "Instrumental Rock", "Ethnic", "Gothic", "Darkwave", "Techno-Industrial", "Electronic", "Pop-Folk", "Eurodance", "Dream", "Southern Rock", "Comedy", "Cult", "Gangsta", "Top 40", "Christian Rap", "Pop/Funk", "Jungle", "Native American", "Cabaret", "New Wave", "Psychadelic", "Rave", "Showtunes", "Trailer", "Lo-Fi", "Tribal", "Acid Punk", "Acid Jazz", "Polka", "Retro", "Musical", "Rock & Roll", "Hard Rock", "Folk", "Folk-Rock", "National Folk", "Swing", "Fast Fusion", "Bebob", "Latin", "Revival", "Celtic", "Bluegrass", "Avantgarde", "Gothic Rock", "Progressive Rock", "Psychedelic Rock", "Symphonic Rock", "Slow Rock", "Big Band", "Chorus", "Easy Listening", "Acoustic", "Humour", "Speech", "Chanson", "Opera", "Chamber Music", "Sonata", "Symphony", "Booty Bass", "Primus", "Porn Groove", "Satire", "Slow Jam", "Club", "Tango", "Samba", "Folklore", "Ballad", "Power Ballad", "Rhythmic Soul", "Freestyle", "Duet", "Punk Rock", "Drum Solo", "A capella", "Euro-House", "Dance Hall", "Goa", "Drum & Bass", "Club-House", "Hardcore", "Terror", "Indie", "Britpop", null, "Polsk Punk", "Beat", "Christian Gangsta", "Heavy Metal", "Black Metal", "Crossover", "Contemporary Christian", "Christian Rock", "Merengue", "Salsa", "Thrash Metal", "Anime", "JPop", "Synthpop"};
    }

    private static class FileEntry {
        int mFormat;
        long mLastModified;
        boolean mLastModifiedChanged = false;
        String mPath;
        long mRowId;

        FileEntry(long j, String str, long j2, int i) {
            this.mRowId = j;
            this.mPath = str;
            this.mLastModified = j2;
            this.mFormat = i;
        }

        public String toString() {
            return this.mPath + " mRowId: " + this.mRowId;
        }
    }

    private static class PlaylistEntry {
        long bestmatchid;
        int bestmatchlevel;
        String path;

        private PlaylistEntry() {
        }
    }

    public MediaScanner(Context context) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        this.mBitmapOptions = options;
        this.mPlaylistEntries = new ArrayList<>();
        this.mDrmManagerClient = null;
        this.mClient = new MyMediaScannerClient();
        native_setup();
        this.mContext = context;
        this.mPackageName = context.getPackageName();
        options.inSampleSize = 1;
        options.inJustDecodeBounds = true;
        setDefaultRingtoneFileNames();
        this.mExternalStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        this.mExternalIsEmulated = Environment.isExternalStorageEmulated();
    }

    private void setDefaultRingtoneFileNames() {
        this.mDefaultRingtoneFilename = SystemProperties.get("ro.config.ringtone");
        this.mDefaultNotificationFilename = SystemProperties.get("ro.config.notification_sound");
        this.mDefaultAlarmAlertFilename = SystemProperties.get("ro.config.alarm_alert");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isDrmEnabled() {
        String str = SystemProperties.get("drm.service.enabled");
        return str != null && str.equals("true");
    }

    private class MyMediaScannerClient implements MediaScannerClient {
        private String mAlbum;
        private String mAlbumArtist;
        private String mArtist;
        private int mCompilation;
        private String mComposer;
        private int mDuration;
        private long mFileSize;
        private int mFileType;
        private String mGenre;
        private int mHeight;
        private boolean mIsDrm;
        private long mLastModified;
        private String mMimeType;
        private boolean mNoMedia;
        private String mPath;
        private String mTitle;
        private int mTrack;
        private int mWidth;
        private String mWriter;
        private int mYear;

        private MyMediaScannerClient() {
        }

        public FileEntry beginFile(String str, String str2, long j, long j2, boolean z, boolean z2) throws Throwable {
            MediaFile.MediaFileType fileType;
            this.mMimeType = str2;
            this.mFileType = 0;
            this.mFileSize = j2;
            this.mIsDrm = false;
            if (!z) {
                this.mNoMedia = (z2 || !MediaScanner.isNoMediaFile(str)) ? z2 : true;
                if (str2 != null) {
                    this.mFileType = MediaFile.getFileTypeForMimeType(str2);
                }
                if (this.mFileType == 0 && (fileType = MediaFile.getFileType(str)) != null) {
                    this.mFileType = fileType.fileType;
                    if (this.mMimeType == null) {
                        this.mMimeType = fileType.mimeType;
                    }
                }
                if (MediaScanner.this.isDrmEnabled() && MediaFile.isDrmFileType(this.mFileType)) {
                    this.mFileType = getFileTypeFromDrm(str);
                }
            }
            FileEntry fileEntryMakeEntryFor = MediaScanner.this.makeEntryFor(str);
            long j3 = fileEntryMakeEntryFor != null ? j - fileEntryMakeEntryFor.mLastModified : 0L;
            boolean z3 = j3 > 1 || j3 < -1;
            if (fileEntryMakeEntryFor == null || z3) {
                if (z3 && fileEntryMakeEntryFor != null) {
                    fileEntryMakeEntryFor.mLastModified = j;
                } else {
                    fileEntryMakeEntryFor = new FileEntry(0L, str, j, z ? 12289 : 0);
                }
                fileEntryMakeEntryFor.mLastModifiedChanged = true;
            }
            if (MediaScanner.this.mProcessPlaylists && MediaFile.isPlayListFileType(this.mFileType)) {
                MediaScanner.this.mPlayLists.add(fileEntryMakeEntryFor);
                return null;
            }
            this.mArtist = null;
            this.mAlbumArtist = null;
            this.mAlbum = null;
            this.mTitle = null;
            this.mComposer = null;
            this.mGenre = null;
            this.mTrack = 0;
            this.mYear = 0;
            this.mDuration = 0;
            this.mPath = str;
            this.mLastModified = j;
            this.mWriter = null;
            this.mCompilation = 0;
            this.mWidth = 0;
            this.mHeight = 0;
            return fileEntryMakeEntryFor;
        }

        @Override // android.media.MediaScannerClient
        public void scanFile(String str, long j, long j2, boolean z, boolean z2) throws Throwable {
            doScanFile(str, null, j, j2, z, false, z2);
        }

        /* JADX WARN: Removed duplicated region for block: B:47:0x00e2  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public android.net.Uri doScanFile(java.lang.String r15, java.lang.String r16, long r17, long r19, boolean r21, boolean r22, boolean r23) throws java.lang.Throwable {
            /*
                Method dump skipped, instruction units count: 270
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: android.media.MediaScanner.MyMediaScannerClient.doScanFile(java.lang.String, java.lang.String, long, long, boolean, boolean, boolean):android.net.Uri");
        }

        private int parseSubstring(String str, int i, int i2) {
            int length = str.length();
            if (i == length) {
                return i2;
            }
            int i3 = i + 1;
            char cCharAt = str.charAt(i);
            if (cCharAt < '0' || cCharAt > '9') {
                return i2;
            }
            int i4 = cCharAt - '0';
            while (i3 < length) {
                int i5 = i3 + 1;
                char cCharAt2 = str.charAt(i3);
                if (cCharAt2 < '0' || cCharAt2 > '9') {
                    break;
                }
                i4 = (i4 * 10) + (cCharAt2 - '0');
                i3 = i5;
            }
            return i4;
        }

        @Override // android.media.MediaScannerClient
        public void handleStringTag(String str, String str2) {
            if (str.equalsIgnoreCase("title") || str.startsWith("title;")) {
                this.mTitle = str2;
                return;
            }
            if (str.equalsIgnoreCase("artist") || str.startsWith("artist;")) {
                this.mArtist = str2.trim();
                return;
            }
            if (str.equalsIgnoreCase("albumartist") || str.startsWith("albumartist;") || str.equalsIgnoreCase("band") || str.startsWith("band;")) {
                this.mAlbumArtist = str2.trim();
                return;
            }
            if (str.equalsIgnoreCase("album") || str.startsWith("album;")) {
                this.mAlbum = str2.trim();
                return;
            }
            if (!str.equalsIgnoreCase(MediaStore.Audio.AudioColumns.COMPOSER) && !str.startsWith("composer;")) {
                if (MediaScanner.this.mProcessGenres && (str.equalsIgnoreCase(MediaStore.Audio.AudioColumns.GENRE) || str.startsWith("genre;"))) {
                    this.mGenre = getGenreName(str2);
                    return;
                }
                if (str.equalsIgnoreCase(MediaStore.Audio.AudioColumns.YEAR) || str.startsWith("year;")) {
                    this.mYear = parseSubstring(str2, 0, 0);
                    return;
                }
                if (str.equalsIgnoreCase("tracknumber") || str.startsWith("tracknumber;")) {
                    this.mTrack = ((this.mTrack / 1000) * 1000) + parseSubstring(str2, 0, 0);
                    return;
                }
                if (str.equalsIgnoreCase("discnumber") || str.equals("set") || str.startsWith("set;")) {
                    this.mTrack = (parseSubstring(str2, 0, 0) * 1000) + (this.mTrack % 1000);
                    return;
                }
                if (str.equalsIgnoreCase("duration")) {
                    this.mDuration = parseSubstring(str2, 0, 0);
                    return;
                }
                if (str.equalsIgnoreCase("writer") || str.startsWith("writer;")) {
                    this.mWriter = str2.trim();
                    return;
                }
                if (str.equalsIgnoreCase(MediaStore.Audio.AudioColumns.COMPILATION)) {
                    this.mCompilation = parseSubstring(str2, 0, 0);
                    return;
                }
                if (str.equalsIgnoreCase("isdrm")) {
                    this.mIsDrm = parseSubstring(str2, 0, 0) == 1;
                    return;
                } else if (str.equalsIgnoreCase("width")) {
                    this.mWidth = parseSubstring(str2, 0, 0);
                    return;
                } else {
                    if (str.equalsIgnoreCase("height")) {
                        this.mHeight = parseSubstring(str2, 0, 0);
                        return;
                    }
                    return;
                }
            }
            this.mComposer = str2.trim();
        }

        private boolean convertGenreCode(String str, String str2) {
            String genreName = getGenreName(str);
            if (genreName.equals(str2)) {
                return true;
            }
            Log.d(MediaScanner.TAG, "'" + str + "' -> '" + genreName + "', expected '" + str2 + "'");
            return false;
        }

        private void testGenreNameConverter() {
            convertGenreCode("2", "Country");
            convertGenreCode("(2)", "Country");
            convertGenreCode("(2", "(2");
            convertGenreCode("2 Foo", "Country");
            convertGenreCode("(2) Foo", "Country");
            convertGenreCode("(2 Foo", "(2 Foo");
            convertGenreCode("2Foo", "2Foo");
            convertGenreCode("(2)Foo", "Country");
            convertGenreCode("200 Foo", "Foo");
            convertGenreCode("(200) Foo", "Foo");
            convertGenreCode("200Foo", "200Foo");
            convertGenreCode("(200)Foo", "Foo");
            convertGenreCode("200)Foo", "200)Foo");
            convertGenreCode("200) Foo", "200) Foo");
        }

        public String getGenreName(String str) {
            int i;
            if (str == null) {
                return null;
            }
            int length = str.length();
            if (length <= 0) {
                return str;
            }
            StringBuffer stringBuffer = new StringBuffer();
            int i2 = 0;
            boolean z = false;
            while (i2 < length) {
                char cCharAt = str.charAt(i2);
                if (i2 != 0 || cCharAt != '(') {
                    if (!Character.isDigit(cCharAt)) {
                        break;
                    }
                    stringBuffer.append(cCharAt);
                } else {
                    z = true;
                }
                i2++;
            }
            char cCharAt2 = i2 < length ? str.charAt(i2) : ' ';
            if (!(z && cCharAt2 == ')') && (z || !Character.isWhitespace(cCharAt2))) {
                return str;
            }
            try {
                short s = Short.parseShort(stringBuffer.toString());
                if (s < 0) {
                    return str;
                }
                if (s < MediaScanner.ID3_GENRES.length && MediaScanner.ID3_GENRES[s] != null) {
                    return MediaScanner.ID3_GENRES[s];
                }
                if (s == 255) {
                    return null;
                }
                if (s < 255 && (i = i2 + 1) < length) {
                    if (z && cCharAt2 == ')') {
                        i2 = i;
                    }
                    String strTrim = str.substring(i2).trim();
                    return strTrim.length() != 0 ? strTrim : str;
                }
                return stringBuffer.toString();
            } catch (NumberFormatException unused) {
                return str;
            }
        }

        private void processImageFile(String str) {
            try {
                MediaScanner.this.mBitmapOptions.outWidth = 0;
                MediaScanner.this.mBitmapOptions.outHeight = 0;
                BitmapFactory.decodeFile(str, MediaScanner.this.mBitmapOptions);
                this.mWidth = MediaScanner.this.mBitmapOptions.outWidth;
                this.mHeight = MediaScanner.this.mBitmapOptions.outHeight;
            } catch (Throwable unused) {
            }
        }

        @Override // android.media.MediaScannerClient
        public void setMimeType(String str) {
            if ("audio/mp4".equals(this.mMimeType) && str.startsWith("video")) {
                return;
            }
            this.mMimeType = str;
            this.mFileType = MediaFile.getFileTypeForMimeType(str);
        }

        private ContentValues toValues() {
            String str;
            ContentValues contentValues = new ContentValues();
            contentValues.put("_data", this.mPath);
            contentValues.put("title", this.mTitle);
            contentValues.put("date_modified", Long.valueOf(this.mLastModified));
            contentValues.put("_size", Long.valueOf(this.mFileSize));
            contentValues.put("mime_type", this.mMimeType);
            contentValues.put(MediaStore.MediaColumns.IS_DRM, Boolean.valueOf(this.mIsDrm));
            int i = this.mWidth;
            String str2 = null;
            if (i <= 0 || this.mHeight <= 0) {
                str = null;
            } else {
                contentValues.put("width", Integer.valueOf(i));
                contentValues.put("height", Integer.valueOf(this.mHeight));
                str = this.mWidth + "x" + this.mHeight;
            }
            if (!this.mNoMedia) {
                boolean zIsVideoFileType = MediaFile.isVideoFileType(this.mFileType);
                String str3 = MediaStore.UNKNOWN_STRING;
                if (zIsVideoFileType) {
                    String str4 = this.mArtist;
                    contentValues.put("artist", (str4 == null || str4.length() <= 0) ? MediaStore.UNKNOWN_STRING : this.mArtist);
                    String str5 = this.mAlbum;
                    if (str5 != null && str5.length() > 0) {
                        str3 = this.mAlbum;
                    }
                    contentValues.put("album", str3);
                    contentValues.put("duration", Integer.valueOf(this.mDuration));
                    if (str != null) {
                        contentValues.put(MediaStore.Video.VideoColumns.RESOLUTION, str);
                    }
                } else if (!MediaFile.isImageFileType(this.mFileType) && MediaFile.isAudioFileType(this.mFileType)) {
                    String str6 = this.mArtist;
                    contentValues.put("artist", (str6 == null || str6.length() <= 0) ? MediaStore.UNKNOWN_STRING : this.mArtist);
                    String str7 = this.mAlbumArtist;
                    if (str7 != null && str7.length() > 0) {
                        str2 = this.mAlbumArtist;
                    }
                    contentValues.put(MediaStore.Audio.AudioColumns.ALBUM_ARTIST, str2);
                    String str8 = this.mAlbum;
                    if (str8 != null && str8.length() > 0) {
                        str3 = this.mAlbum;
                    }
                    contentValues.put("album", str3);
                    contentValues.put(MediaStore.Audio.AudioColumns.COMPOSER, this.mComposer);
                    contentValues.put(MediaStore.Audio.AudioColumns.GENRE, this.mGenre);
                    int i2 = this.mYear;
                    if (i2 != 0) {
                        contentValues.put(MediaStore.Audio.AudioColumns.YEAR, Integer.valueOf(i2));
                    }
                    contentValues.put(MediaStore.Audio.AudioColumns.TRACK, Integer.valueOf(this.mTrack));
                    contentValues.put("duration", Integer.valueOf(this.mDuration));
                    contentValues.put(MediaStore.Audio.AudioColumns.COMPILATION, Integer.valueOf(this.mCompilation));
                }
            }
            return contentValues;
        }

        private Uri endFile(FileEntry fileEntry, boolean z, boolean z2, boolean z3, boolean z4, boolean z5) throws RemoteException {
            ExifInterface exifInterface;
            String asString;
            int iLastIndexOf;
            int i;
            String str = this.mArtist;
            if (str == null || str.length() == 0) {
                this.mArtist = this.mAlbumArtist;
            }
            ContentValues values = toValues();
            String asString2 = values.getAsString("title");
            if (asString2 == null || TextUtils.isEmpty(asString2.trim())) {
                values.put("title", MediaFile.getFileTitle(values.getAsString("_data")));
            }
            boolean z6 = false;
            if (MediaStore.UNKNOWN_STRING.equals(values.getAsString("album")) && (iLastIndexOf = (asString = values.getAsString("_data")).lastIndexOf(47)) >= 0) {
                int i2 = 0;
                while (true) {
                    i = i2 + 1;
                    int iIndexOf = asString.indexOf(47, i);
                    if (iIndexOf < 0 || iIndexOf >= iLastIndexOf) {
                        break;
                    }
                    i2 = iIndexOf;
                }
                if (i2 != 0) {
                    values.put("album", asString.substring(i, iLastIndexOf));
                }
            }
            long id = fileEntry.mRowId;
            int i3 = 3;
            Uri uriInsert = null;
            if (MediaFile.isAudioFileType(this.mFileType) && (id == 0 || MediaScanner.this.mMtpObjectHandle != 0)) {
                values.put(MediaStore.Audio.AudioColumns.IS_RINGTONE, Boolean.valueOf(z));
                values.put(MediaStore.Audio.AudioColumns.IS_NOTIFICATION, Boolean.valueOf(z2));
                values.put(MediaStore.Audio.AudioColumns.IS_ALARM, Boolean.valueOf(z3));
                values.put(MediaStore.Audio.AudioColumns.IS_MUSIC, Boolean.valueOf(z4));
                values.put(MediaStore.Audio.AudioColumns.IS_PODCAST, Boolean.valueOf(z5));
            } else if (this.mFileType == 31 && !this.mNoMedia) {
                try {
                    exifInterface = new ExifInterface(fileEntry.mPath);
                } catch (IOException unused) {
                    exifInterface = null;
                }
                if (exifInterface != null) {
                    float[] fArr = new float[2];
                    if (exifInterface.getLatLong(fArr)) {
                        values.put("latitude", Float.valueOf(fArr[0]));
                        values.put("longitude", Float.valueOf(fArr[1]));
                    }
                    long gpsDateTime = exifInterface.getGpsDateTime();
                    if (gpsDateTime != -1) {
                        values.put("datetaken", Long.valueOf(gpsDateTime));
                    } else {
                        long dateTime = exifInterface.getDateTime();
                        if (dateTime != -1 && Math.abs((this.mLastModified * 1000) - dateTime) >= 86400000) {
                            values.put("datetaken", Long.valueOf(dateTime));
                        }
                    }
                    int attributeInt = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
                    if (attributeInt != -1) {
                        values.put(MediaStore.Images.ImageColumns.ORIENTATION, Integer.valueOf(attributeInt != 3 ? attributeInt != 6 ? attributeInt != 8 ? 0 : 270 : 90 : 180));
                    }
                }
            }
            Uri uri = MediaScanner.this.mFilesUri;
            MediaInserter mediaInserter = MediaScanner.this.mMediaInserter;
            if (!this.mNoMedia) {
                if (MediaFile.isVideoFileType(this.mFileType)) {
                    uri = MediaScanner.this.mVideoUri;
                } else if (MediaFile.isImageFileType(this.mFileType)) {
                    uri = MediaScanner.this.mImagesUri;
                } else if (MediaFile.isAudioFileType(this.mFileType)) {
                    uri = MediaScanner.this.mAudioUri;
                }
            }
            if (id == 0) {
                if (MediaScanner.this.mMtpObjectHandle != 0) {
                    values.put(MediaStore.MediaColumns.MEDIA_SCANNER_NEW_OBJECT_ID, Integer.valueOf(MediaScanner.this.mMtpObjectHandle));
                }
                if (uri == MediaScanner.this.mFilesUri) {
                    int formatCode = fileEntry.mFormat;
                    if (formatCode == 0) {
                        formatCode = MediaFile.getFormatCode(fileEntry.mPath, this.mMimeType);
                    }
                    values.put(MediaStore.Files.FileColumns.FORMAT, Integer.valueOf(formatCode));
                }
                if (MediaScanner.this.mWasEmptyPriorToScan && (!z2 || MediaScanner.this.mDefaultNotificationSet ? !(!z || MediaScanner.this.mDefaultRingtoneSet ? !z3 || MediaScanner.this.mDefaultAlarmSet || (!TextUtils.isEmpty(MediaScanner.this.mDefaultAlarmAlertFilename) && !doesPathHaveFilename(fileEntry.mPath, MediaScanner.this.mDefaultAlarmAlertFilename)) : !TextUtils.isEmpty(MediaScanner.this.mDefaultRingtoneFilename) && !doesPathHaveFilename(fileEntry.mPath, MediaScanner.this.mDefaultRingtoneFilename)) : !(!TextUtils.isEmpty(MediaScanner.this.mDefaultNotificationFilename) && !doesPathHaveFilename(fileEntry.mPath, MediaScanner.this.mDefaultNotificationFilename)))) {
                    z6 = true;
                }
                if (mediaInserter == null || z6) {
                    if (mediaInserter != null) {
                        mediaInserter.flushAll();
                    }
                    uriInsert = MediaScanner.this.mMediaProvider.insert(MediaScanner.this.mPackageName, uri, values);
                } else if (fileEntry.mFormat == 12289) {
                    mediaInserter.insertwithPriority(uri, values);
                } else {
                    mediaInserter.insert(uri, values);
                }
                if (uriInsert != null) {
                    id = ContentUris.parseId(uriInsert);
                    fileEntry.mRowId = id;
                }
            } else {
                Uri uriWithAppendedId = ContentUris.withAppendedId(uri, id);
                values.remove("_data");
                if (!MediaScanner.isNoMediaPath(fileEntry.mPath)) {
                    int fileTypeForMimeType = MediaFile.getFileTypeForMimeType(this.mMimeType);
                    if (MediaFile.isAudioFileType(fileTypeForMimeType)) {
                        i3 = 2;
                    } else if (!MediaFile.isVideoFileType(fileTypeForMimeType)) {
                        if (MediaFile.isImageFileType(fileTypeForMimeType)) {
                            i3 = 1;
                        } else {
                            i3 = MediaFile.isPlayListFileType(fileTypeForMimeType) ? 4 : 0;
                        }
                    }
                    values.put("media_type", Integer.valueOf(i3));
                }
                MediaScanner.this.mMediaProvider.update(MediaScanner.this.mPackageName, uriWithAppendedId, values, null, null);
                uriInsert = uriWithAppendedId;
            }
            if (z6) {
                if (z2) {
                    setSettingIfNotSet(Settings.System.NOTIFICATION_SOUND, uri, id);
                    MediaScanner.this.mDefaultNotificationSet = true;
                } else if (z) {
                    setSettingIfNotSet(Settings.System.RINGTONE, uri, id);
                    MediaScanner.this.mDefaultRingtoneSet = true;
                } else if (z3) {
                    setSettingIfNotSet(Settings.System.ALARM_ALERT, uri, id);
                    MediaScanner.this.mDefaultAlarmSet = true;
                }
            }
            return uriInsert;
        }

        private boolean doesPathHaveFilename(String str, String str2) {
            int iLastIndexOf = str.lastIndexOf(File.separatorChar) + 1;
            int length = str2.length();
            return str.regionMatches(iLastIndexOf, str2, 0, length) && iLastIndexOf + length == str.length();
        }

        private void setSettingIfNotSet(String str, Uri uri, long j) {
            if (TextUtils.isEmpty(Settings.System.getString(MediaScanner.this.mContext.getContentResolver(), str))) {
                Settings.System.putString(MediaScanner.this.mContext.getContentResolver(), str, ContentUris.withAppendedId(uri, j).toString());
            }
        }

        private int getFileTypeFromDrm(String str) throws Throwable {
            if (!MediaScanner.this.isDrmEnabled()) {
                return 0;
            }
            if (MediaScanner.this.mDrmManagerClient == null) {
                MediaScanner.this.mDrmManagerClient = new DrmManagerClient(MediaScanner.this.mContext);
            }
            if (!MediaScanner.this.mDrmManagerClient.canHandle(str, (String) null)) {
                return 0;
            }
            this.mIsDrm = true;
            String originalMimeType = MediaScanner.this.mDrmManagerClient.getOriginalMimeType(str);
            if (originalMimeType == null) {
                return 0;
            }
            this.mMimeType = originalMimeType;
            return MediaFile.getFileTypeForMimeType(originalMimeType);
        }
    }

    private void prescan(String str, boolean z) throws Throwable {
        String[] strArr;
        String str2;
        Cursor cursor;
        boolean zAccess;
        ArrayList<FileEntry> arrayList = this.mPlayLists;
        if (arrayList == null) {
            this.mPlayLists = new ArrayList<>();
        } else {
            arrayList.clear();
        }
        if (str != null) {
            strArr = new String[]{"", str};
            str2 = "_id>? AND _data=?";
        } else {
            strArr = new String[]{""};
            str2 = "_id>?";
        }
        Uri.Builder builderBuildUpon = this.mFilesUri.buildUpon();
        builderBuildUpon.appendQueryParameter(MediaStore.PARAM_DELETE_DATA, "false");
        MediaBulkDeleter mediaBulkDeleter = new MediaBulkDeleter(this.mMediaProvider, this.mPackageName, builderBuildUpon.build());
        Cursor cursor2 = null;
        if (z) {
            try {
                Uri uriBuild = this.mFilesUri.buildUpon().appendQueryParameter("limit", "1000").build();
                this.mWasEmptyPriorToScan = true;
                long j = Long.MIN_VALUE;
                Cursor cursorQuery = null;
                while (true) {
                    try {
                        strArr[0] = "" + j;
                        if (cursorQuery != null) {
                            cursorQuery.close();
                            cursor = cursor2;
                        } else {
                            cursor = cursorQuery;
                        }
                        try {
                            long j2 = j;
                            cursorQuery = this.mMediaProvider.query(this.mPackageName, uriBuild, FILES_PRESCAN_PROJECTION, str2, strArr, "_id", null);
                            if (cursorQuery == null || cursorQuery.getCount() == 0) {
                                break;
                            }
                            this.mWasEmptyPriorToScan = false;
                            j = j2;
                            while (cursorQuery.moveToNext()) {
                                j = cursorQuery.getLong(0);
                                String string = cursorQuery.getString(1);
                                int i = cursorQuery.getInt(2);
                                cursorQuery.getLong(3);
                                if (string != null && string.startsWith("/")) {
                                    try {
                                        zAccess = Libcore.os.access(string, OsConstants.F_OK);
                                    } catch (ErrnoException unused) {
                                        zAccess = false;
                                    }
                                    if (!zAccess && !MtpConstants.isAbstractObject(i)) {
                                        MediaFile.MediaFileType fileType = MediaFile.getFileType(string);
                                        if (!MediaFile.isPlayListFileType(fileType == null ? 0 : fileType.fileType)) {
                                            mediaBulkDeleter.delete(j);
                                            if (string.toLowerCase(Locale.US).endsWith("/.nomedia")) {
                                                mediaBulkDeleter.flush();
                                                this.mMediaProvider.call(this.mPackageName, MediaStore.UNHIDE_CALL, new File(string).getParent(), null);
                                            }
                                        }
                                    }
                                }
                            }
                            cursor2 = null;
                        } catch (Throwable th) {
                            th = th;
                            cursor2 = cursor;
                            if (cursor2 != null) {
                                cursor2.close();
                            }
                            mediaBulkDeleter.flush();
                            throw th;
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        cursor2 = cursorQuery;
                    }
                }
                cursor2 = cursorQuery;
            } catch (Throwable th3) {
                th = th3;
            }
        }
        if (cursor2 != null) {
            cursor2.close();
        }
        mediaBulkDeleter.flush();
        this.mOriginalCount = 0;
        Cursor cursorQuery2 = this.mMediaProvider.query(this.mPackageName, this.mImagesUri, ID_PROJECTION, null, null, null, null);
        if (cursorQuery2 != null) {
            this.mOriginalCount = cursorQuery2.getCount();
            cursorQuery2.close();
        }
    }

    private boolean inScanDirectory(String str, String[] strArr) {
        for (String str2 : strArr) {
            if (str.startsWith(str2)) {
                return true;
            }
        }
        return false;
    }

    private void pruneDeadThumbnailFiles() {
        HashSet hashSet = new HashSet();
        String[] list = new File("/sdcard/DCIM/.thumbnails").list();
        if (list == null) {
            list = new String[0];
        }
        for (String str : list) {
            hashSet.add("/sdcard/DCIM/.thumbnails/" + str);
        }
        try {
            Cursor cursorQuery = this.mMediaProvider.query(this.mPackageName, this.mThumbsUri, new String[]{"_data"}, null, null, null, null);
            Log.v(TAG, "pruneDeadThumbnailFiles... " + cursorQuery);
            if (cursorQuery != null && cursorQuery.moveToFirst()) {
                do {
                    hashSet.remove(cursorQuery.getString(0));
                } while (cursorQuery.moveToNext());
            }
            Iterator it = hashSet.iterator();
            while (it.hasNext()) {
                try {
                    new File((String) it.next()).delete();
                } catch (SecurityException unused) {
                }
            }
            Log.v(TAG, "/pruneDeadThumbnailFiles... " + cursorQuery);
            if (cursorQuery != null) {
                cursorQuery.close();
            }
        } catch (RemoteException unused2) {
        }
    }

    static class MediaBulkDeleter {
        final Uri mBaseUri;
        final String mPackageName;
        final IContentProvider mProvider;
        StringBuilder whereClause = new StringBuilder();
        ArrayList<String> whereArgs = new ArrayList<>(100);

        public MediaBulkDeleter(IContentProvider iContentProvider, String str, Uri uri) {
            this.mProvider = iContentProvider;
            this.mPackageName = str;
            this.mBaseUri = uri;
        }

        public void delete(long j) throws RemoteException {
            if (this.whereClause.length() != 0) {
                this.whereClause.append(",");
            }
            this.whereClause.append("?");
            this.whereArgs.add("" + j);
            if (this.whereArgs.size() > 100) {
                flush();
            }
        }

        public void flush() throws RemoteException {
            int size = this.whereArgs.size();
            if (size > 0) {
                this.mProvider.delete(this.mPackageName, this.mBaseUri, "_id IN (" + this.whereClause.toString() + ")", (String[]) this.whereArgs.toArray(new String[size]));
                this.whereClause.setLength(0);
                this.whereArgs.clear();
            }
        }
    }

    private void postscan(String[] strArr) throws RemoteException {
        if (this.mProcessPlaylists) {
            processPlayLists();
        }
        if (this.mOriginalCount == 0 && this.mImagesUri.equals(MediaStore.Images.Media.getContentUri("external"))) {
            pruneDeadThumbnailFiles();
        }
        this.mPlayLists = null;
        this.mMediaProvider = null;
    }

    private void initialize(String str) {
        this.mMediaProvider = this.mContext.getContentResolver().acquireProvider(MediaStore.AUTHORITY);
        this.mAudioUri = MediaStore.Audio.Media.getContentUri(str);
        this.mVideoUri = MediaStore.Video.Media.getContentUri(str);
        this.mImagesUri = MediaStore.Images.Media.getContentUri(str);
        this.mThumbsUri = MediaStore.Images.Thumbnails.getContentUri(str);
        Uri contentUri = MediaStore.Files.getContentUri(str);
        this.mFilesUri = contentUri;
        this.mFilesUriNoNotify = contentUri.buildUpon().appendQueryParameter("nonotify", "1").build();
        if (str.equals("internal")) {
            return;
        }
        this.mProcessPlaylists = true;
        this.mProcessGenres = true;
        this.mPlaylistsUri = MediaStore.Audio.Playlists.getContentUri(str);
        this.mCaseInsensitivePaths = true;
    }

    public void scanDirectories(String[] strArr, String str) throws Throwable {
        try {
            System.currentTimeMillis();
            initialize(str);
            prescan(null, true);
            System.currentTimeMillis();
            this.mMediaInserter = new MediaInserter(this.mMediaProvider, this.mPackageName, 500);
            for (String str2 : strArr) {
                processDirectory(str2, this.mClient);
            }
            this.mMediaInserter.flushAll();
            this.mMediaInserter = null;
            System.currentTimeMillis();
            postscan(strArr);
            System.currentTimeMillis();
        } catch (SQLException e) {
            Log.e(TAG, "SQLException in MediaScanner.scan()", e);
        } catch (RemoteException e2) {
            Log.e(TAG, "RemoteException in MediaScanner.scan()", e2);
        } catch (UnsupportedOperationException e3) {
            Log.e(TAG, "UnsupportedOperationException in MediaScanner.scan()", e3);
        }
    }

    public Uri scanSingleFile(String str, String str2, String str3) throws Throwable {
        try {
            initialize(str2);
            prescan(str, true);
            File file = new File(str);
            if (!file.exists()) {
                return null;
            }
            return this.mClient.doScanFile(str, str3, file.lastModified() / 1000, file.length(), false, true, isNoMediaPath(str));
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException in MediaScanner.scanFile()", e);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isNoMediaFile(String str) {
        int iLastIndexOf;
        int length;
        if (!new File(str).isDirectory() && (iLastIndexOf = str.lastIndexOf(47)) >= 0 && iLastIndexOf + 2 < str.length()) {
            int i = iLastIndexOf + 1;
            if (str.regionMatches(i, "._", 0, 2)) {
                return true;
            }
            if (str.regionMatches(true, str.length() - 4, ".jpg", 0, 4) && (str.regionMatches(true, i, "AlbumArt_{", 0, 10) || str.regionMatches(true, i, "AlbumArt.", 0, 9) || (((length = (str.length() - iLastIndexOf) - 1) == 17 && str.regionMatches(true, i, "AlbumArtSmall", 0, 13)) || (length == 10 && str.regionMatches(true, i, "Folder", 0, 6))))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNoMediaPath(String str) {
        if (str == null) {
            return false;
        }
        if (str.indexOf("/.") >= 0) {
            return true;
        }
        int i = 1;
        while (i >= 0) {
            int iIndexOf = str.indexOf(47, i);
            if (iIndexOf > i) {
                iIndexOf++;
                if (new File(str.substring(0, iIndexOf) + MediaStore.MEDIA_IGNORE_FILENAME).exists()) {
                    return true;
                }
            }
            i = iIndexOf;
        }
        return isNoMediaFile(str);
    }

    public void scanMtpFile(String str, String str2, int i, int i2) {
        initialize(str2);
        MediaFile.MediaFileType fileType = MediaFile.getFileType(str);
        int i3 = fileType == null ? 0 : fileType.fileType;
        File file = new File(str);
        long jLastModified = file.lastModified() / 1000;
        boolean z = true;
        if (!MediaFile.isAudioFileType(i3) && !MediaFile.isVideoFileType(i3) && !MediaFile.isImageFileType(i3) && !MediaFile.isPlayListFileType(i3) && !MediaFile.isDrmFileType(i3)) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("_size", Long.valueOf(file.length()));
            contentValues.put("date_modified", Long.valueOf(jLastModified));
            try {
                this.mMediaProvider.update(this.mPackageName, MediaStore.Files.getMtpObjectsUri(str2), contentValues, "_id=?", new String[]{Integer.toString(i)});
                return;
            } catch (RemoteException e) {
                Log.e(TAG, "RemoteException in scanMtpFile", e);
                return;
            }
        }
        this.mMtpObjectHandle = i;
        Cursor cursorQuery = null;
        try {
            try {
                if (MediaFile.isPlayListFileType(i3)) {
                    prescan(null, true);
                    FileEntry fileEntryMakeEntryFor = makeEntryFor(str);
                    if (fileEntryMakeEntryFor != null) {
                        cursorQuery = this.mMediaProvider.query(this.mPackageName, this.mFilesUri, FILES_PRESCAN_PROJECTION, null, null, null, null);
                        processPlayList(fileEntryMakeEntryFor, cursorQuery);
                    }
                } else {
                    prescan(str, false);
                    MyMediaScannerClient myMediaScannerClient = this.mClient;
                    String str3 = fileType.mimeType;
                    long length = file.length();
                    if (i2 != 12289) {
                        z = false;
                    }
                    myMediaScannerClient.doScanFile(str, str3, jLastModified, length, z, true, isNoMediaPath(str));
                }
                this.mMtpObjectHandle = 0;
                if (cursorQuery == null) {
                    return;
                }
            } catch (RemoteException e2) {
                Log.e(TAG, "RemoteException in MediaScanner.scanFile()", e2);
                this.mMtpObjectHandle = 0;
                if (0 == 0) {
                    return;
                }
            }
            cursorQuery.close();
        } catch (Throwable th) {
            this.mMtpObjectHandle = 0;
            if (0 != 0) {
                cursorQuery.close();
            }
            throw th;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:20:0x004b A[PHI: r3
      0x004b: PHI (r3v2 android.database.Cursor) = (r3v1 android.database.Cursor), (r3v4 android.database.Cursor) binds: [B:19:0x0049, B:10:0x003b] A[DONT_GENERATE, DONT_INLINE]] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    android.media.MediaScanner.FileEntry makeEntryFor(java.lang.String r17) throws java.lang.Throwable {
        /*
            r16 = this;
            r1 = r16
            r2 = 0
            java.lang.String r7 = "_data=?"
            r0 = 1
            java.lang.String[] r8 = new java.lang.String[r0]     // Catch: java.lang.Throwable -> L41 android.os.RemoteException -> L48
            r0 = 0
            r8[r0] = r17     // Catch: java.lang.Throwable -> L41 android.os.RemoteException -> L48
            android.content.IContentProvider r3 = r1.mMediaProvider     // Catch: java.lang.Throwable -> L41 android.os.RemoteException -> L48
            java.lang.String r4 = r1.mPackageName     // Catch: java.lang.Throwable -> L41 android.os.RemoteException -> L48
            android.net.Uri r5 = r1.mFilesUriNoNotify     // Catch: java.lang.Throwable -> L41 android.os.RemoteException -> L48
            java.lang.String[] r6 = android.media.MediaScanner.FILES_PRESCAN_PROJECTION     // Catch: java.lang.Throwable -> L41 android.os.RemoteException -> L48
            r9 = 0
            r10 = 0
            android.database.Cursor r3 = r3.query(r4, r5, r6, r7, r8, r9, r10)     // Catch: java.lang.Throwable -> L41 android.os.RemoteException -> L48
            boolean r4 = r3.moveToFirst()     // Catch: java.lang.Throwable -> L3e android.os.RemoteException -> L49
            if (r4 == 0) goto L3b
            long r10 = r3.getLong(r0)     // Catch: java.lang.Throwable -> L3e android.os.RemoteException -> L49
            r0 = 2
            int r15 = r3.getInt(r0)     // Catch: java.lang.Throwable -> L3e android.os.RemoteException -> L49
            r0 = 3
            long r13 = r3.getLong(r0)     // Catch: java.lang.Throwable -> L3e android.os.RemoteException -> L49
            android.media.MediaScanner$FileEntry r0 = new android.media.MediaScanner$FileEntry     // Catch: java.lang.Throwable -> L3e android.os.RemoteException -> L49
            r9 = r0
            r12 = r17
            r9.<init>(r10, r12, r13, r15)     // Catch: java.lang.Throwable -> L3e android.os.RemoteException -> L49
            if (r3 == 0) goto L3a
            r3.close()
        L3a:
            return r0
        L3b:
            if (r3 == 0) goto L4e
            goto L4b
        L3e:
            r0 = move-exception
            r2 = r3
            goto L42
        L41:
            r0 = move-exception
        L42:
            if (r2 == 0) goto L47
            r2.close()
        L47:
            throw r0
        L48:
            r3 = r2
        L49:
            if (r3 == 0) goto L4e
        L4b:
            r3.close()
        L4e:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: android.media.MediaScanner.makeEntryFor(java.lang.String):android.media.MediaScanner$FileEntry");
    }

    private int matchPaths(String str, String str2) {
        int length = str.length();
        int length2 = str2.length();
        int i = 0;
        while (length > 0 && length2 > 0) {
            int i2 = length - 1;
            int iLastIndexOf = str.lastIndexOf(47, i2);
            int i3 = length2 - 1;
            int iLastIndexOf2 = str2.lastIndexOf(47, i3);
            int iLastIndexOf3 = str.lastIndexOf(92, i2);
            int iLastIndexOf4 = str2.lastIndexOf(92, i3);
            if (iLastIndexOf <= iLastIndexOf3) {
                iLastIndexOf = iLastIndexOf3;
            }
            if (iLastIndexOf2 <= iLastIndexOf4) {
                iLastIndexOf2 = iLastIndexOf4;
            }
            int i4 = iLastIndexOf < 0 ? 0 : iLastIndexOf + 1;
            int i5 = iLastIndexOf2 < 0 ? 0 : iLastIndexOf2 + 1;
            int i6 = length - i4;
            if (length2 - i5 != i6 || !str.regionMatches(true, i4, str2, i5, i6)) {
                break;
            }
            i++;
            length = i4 - 1;
            length2 = i5 - 1;
        }
        return i;
    }

    private boolean matchEntries(long j, String str) {
        int size = this.mPlaylistEntries.size();
        boolean z = true;
        for (int i = 0; i < size; i++) {
            PlaylistEntry playlistEntry = this.mPlaylistEntries.get(i);
            if (playlistEntry.bestmatchlevel != Integer.MAX_VALUE) {
                if (str.equalsIgnoreCase(playlistEntry.path)) {
                    playlistEntry.bestmatchid = j;
                    playlistEntry.bestmatchlevel = Integer.MAX_VALUE;
                } else {
                    int iMatchPaths = matchPaths(str, playlistEntry.path);
                    if (iMatchPaths > playlistEntry.bestmatchlevel) {
                        playlistEntry.bestmatchid = j;
                        playlistEntry.bestmatchlevel = iMatchPaths;
                    }
                }
                z = false;
            }
        }
        return z;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cachePlaylistEntry(String str, String str2) {
        PlaylistEntry playlistEntry = new PlaylistEntry();
        int length = str.length();
        while (length > 0 && Character.isWhitespace(str.charAt(length - 1))) {
            length--;
        }
        if (length < 3) {
            return;
        }
        boolean z = false;
        if (length < str.length()) {
            str = str.substring(0, length);
        }
        char cCharAt = str.charAt(0);
        if (cCharAt == '/' || (Character.isLetter(cCharAt) && str.charAt(1) == ':' && str.charAt(2) == '\\')) {
            z = true;
        }
        if (!z) {
            str = str2 + str;
        }
        playlistEntry.path = str;
        this.mPlaylistEntries.add(playlistEntry);
    }

    private void processCachedPlaylist(Cursor cursor, ContentValues contentValues, Uri uri) {
        int i;
        cursor.moveToPosition(-1);
        do {
            if (!cursor.moveToNext()) {
                break;
            }
        } while (!matchEntries(cursor.getLong(0), cursor.getString(1)));
        int size = this.mPlaylistEntries.size();
        int i2 = 0;
        for (i = 0; i < size; i++) {
            PlaylistEntry playlistEntry = this.mPlaylistEntries.get(i);
            if (playlistEntry.bestmatchlevel > 0) {
                try {
                    contentValues.clear();
                    contentValues.put("play_order", Integer.valueOf(i2));
                    contentValues.put("audio_id", Long.valueOf(playlistEntry.bestmatchid));
                    this.mMediaProvider.insert(this.mPackageName, uri, contentValues);
                    i2++;
                } catch (RemoteException e) {
                    Log.e(TAG, "RemoteException in MediaScanner.processCachedPlaylist()", e);
                    return;
                }
            }
        }
        this.mPlaylistEntries.clear();
    }

    private void processM3uPlayList(String str, String str2, Uri uri, ContentValues contentValues, Cursor cursor) throws Throwable {
        BufferedReader bufferedReader = null;
        try {
            try {
                try {
                    File file = new File(str);
                    if (file.exists()) {
                        BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(new FileInputStream(file)), 8192);
                        try {
                            this.mPlaylistEntries.clear();
                            for (String line = bufferedReader2.readLine(); line != null; line = bufferedReader2.readLine()) {
                                if (line.length() > 0 && line.charAt(0) != '#') {
                                    cachePlaylistEntry(line, str2);
                                }
                            }
                            processCachedPlaylist(cursor, contentValues, uri);
                            bufferedReader = bufferedReader2;
                        } catch (IOException e) {
                            e = e;
                            bufferedReader = bufferedReader2;
                            Log.e(TAG, "IOException in MediaScanner.processM3uPlayList()", e);
                            if (bufferedReader == null) {
                                return;
                            } else {
                                bufferedReader.close();
                            }
                        } catch (Throwable th) {
                            th = th;
                            bufferedReader = bufferedReader2;
                            if (bufferedReader != null) {
                                try {
                                    bufferedReader.close();
                                } catch (IOException e2) {
                                    Log.e(TAG, "IOException in MediaScanner.processM3uPlayList()", e2);
                                }
                            }
                            throw th;
                        }
                    }
                } catch (IOException e3) {
                    Log.e(TAG, "IOException in MediaScanner.processM3uPlayList()", e3);
                    return;
                }
            } catch (IOException e4) {
                e = e4;
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    private void processPlsPlayList(String str, String str2, Uri uri, ContentValues contentValues, Cursor cursor) throws Throwable {
        int iIndexOf;
        BufferedReader bufferedReader = null;
        try {
            try {
                try {
                    File file = new File(str);
                    if (file.exists()) {
                        BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(new FileInputStream(file)), 8192);
                        try {
                            this.mPlaylistEntries.clear();
                            for (String line = bufferedReader2.readLine(); line != null; line = bufferedReader2.readLine()) {
                                if (line.startsWith("File") && (iIndexOf = line.indexOf(61)) > 0) {
                                    cachePlaylistEntry(line.substring(iIndexOf + 1), str2);
                                }
                            }
                            processCachedPlaylist(cursor, contentValues, uri);
                            bufferedReader = bufferedReader2;
                        } catch (IOException e) {
                            e = e;
                            bufferedReader = bufferedReader2;
                            Log.e(TAG, "IOException in MediaScanner.processPlsPlayList()", e);
                            if (bufferedReader == null) {
                                return;
                            } else {
                                bufferedReader.close();
                            }
                        } catch (Throwable th) {
                            th = th;
                            bufferedReader = bufferedReader2;
                            if (bufferedReader != null) {
                                try {
                                    bufferedReader.close();
                                } catch (IOException e2) {
                                    Log.e(TAG, "IOException in MediaScanner.processPlsPlayList()", e2);
                                }
                            }
                            throw th;
                        }
                    }
                } catch (Throwable th2) {
                    th = th2;
                }
            } catch (IOException e3) {
                e = e3;
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        } catch (IOException e4) {
            Log.e(TAG, "IOException in MediaScanner.processPlsPlayList()", e4);
        }
    }

    class WplHandler implements ElementListener {
        final ContentHandler handler;
        String playListDirectory;

        @Override // android.sax.EndElementListener
        public void end() {
        }

        public WplHandler(String str, Uri uri, Cursor cursor) {
            this.playListDirectory = str;
            RootElement rootElement = new RootElement("smil");
            rootElement.getChild("body").getChild("seq").getChild(MediaStore.AUTHORITY).setElementListener(this);
            this.handler = rootElement.getContentHandler();
        }

        @Override // android.sax.StartElementListener
        public void start(Attributes attributes) {
            String value = attributes.getValue("", "src");
            if (value != null) {
                MediaScanner.this.cachePlaylistEntry(value, this.playListDirectory);
            }
        }

        ContentHandler getContentHandler() {
            return this.handler;
        }
    }

    private void processWplPlayList(String str, String str2, Uri uri, ContentValues contentValues, Cursor cursor) throws Throwable {
        FileInputStream fileInputStream = null;
        try {
            try {
                try {
                    File file = new File(str);
                    if (file.exists()) {
                        FileInputStream fileInputStream2 = new FileInputStream(file);
                        try {
                            this.mPlaylistEntries.clear();
                            Xml.parse(fileInputStream2, Xml.findEncodingByName("UTF-8"), new WplHandler(str2, uri, cursor).getContentHandler());
                            processCachedPlaylist(cursor, contentValues, uri);
                            fileInputStream = fileInputStream2;
                        } catch (IOException e) {
                            e = e;
                            fileInputStream = fileInputStream2;
                            e.printStackTrace();
                            if (fileInputStream == null) {
                                return;
                            } else {
                                fileInputStream.close();
                            }
                        } catch (SAXException e2) {
                            e = e2;
                            fileInputStream = fileInputStream2;
                            e.printStackTrace();
                            if (fileInputStream == null) {
                                return;
                            } else {
                                fileInputStream.close();
                            }
                        } catch (Throwable th) {
                            th = th;
                            fileInputStream = fileInputStream2;
                            if (fileInputStream != null) {
                                try {
                                    fileInputStream.close();
                                } catch (IOException e3) {
                                    Log.e(TAG, "IOException in MediaScanner.processWplPlayList()", e3);
                                }
                            }
                            throw th;
                        }
                    }
                } catch (Throwable th2) {
                    th = th2;
                }
            } catch (IOException e4) {
                e = e4;
            } catch (SAXException e5) {
                e = e5;
            }
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        } catch (IOException e6) {
            Log.e(TAG, "IOException in MediaScanner.processWplPlayList()", e6);
        }
    }

    private void processPlayList(FileEntry fileEntry, Cursor cursor) throws Throwable {
        Uri uriWithAppendedPath;
        String str = fileEntry.mPath;
        ContentValues contentValues = new ContentValues();
        int iLastIndexOf = str.lastIndexOf(47);
        if (iLastIndexOf < 0) {
            throw new IllegalArgumentException("bad path " + str);
        }
        long j = fileEntry.mRowId;
        String asString = contentValues.getAsString("name");
        if (asString == null && (asString = contentValues.getAsString("title")) == null) {
            int iLastIndexOf2 = str.lastIndexOf(46);
            asString = iLastIndexOf2 < 0 ? str.substring(iLastIndexOf + 1) : str.substring(iLastIndexOf + 1, iLastIndexOf2);
        }
        contentValues.put("name", asString);
        contentValues.put("date_modified", Long.valueOf(fileEntry.mLastModified));
        if (j == 0) {
            contentValues.put("_data", str);
            Uri uriInsert = this.mMediaProvider.insert(this.mPackageName, this.mPlaylistsUri, contentValues);
            ContentUris.parseId(uriInsert);
            uriWithAppendedPath = Uri.withAppendedPath(uriInsert, "members");
        } else {
            Uri uriWithAppendedId = ContentUris.withAppendedId(this.mPlaylistsUri, j);
            this.mMediaProvider.update(this.mPackageName, uriWithAppendedId, contentValues, null, null);
            uriWithAppendedPath = Uri.withAppendedPath(uriWithAppendedId, "members");
            this.mMediaProvider.delete(this.mPackageName, uriWithAppendedPath, null, null);
        }
        Uri uri = uriWithAppendedPath;
        String strSubstring = str.substring(0, iLastIndexOf + 1);
        MediaFile.MediaFileType fileType = MediaFile.getFileType(str);
        int i = fileType != null ? fileType.fileType : 0;
        if (i == 41) {
            processM3uPlayList(str, strSubstring, uri, contentValues, cursor);
        } else if (i == 42) {
            processPlsPlayList(str, strSubstring, uri, contentValues, cursor);
        } else if (i == 43) {
            processWplPlayList(str, strSubstring, uri, contentValues, cursor);
        }
    }

    private void processPlayLists() throws RemoteException {
        Cursor cursorQuery = null;
        try {
            cursorQuery = this.mMediaProvider.query(this.mPackageName, this.mFilesUri, FILES_PRESCAN_PROJECTION, "media_type=2", null, null, null);
            for (FileEntry fileEntry : this.mPlayLists) {
                if (fileEntry.mLastModifiedChanged) {
                    processPlayList(fileEntry, cursorQuery);
                }
            }
            if (cursorQuery == null) {
                return;
            }
        } catch (RemoteException unused) {
            if (cursorQuery == null) {
                return;
            }
        } catch (Throwable th) {
            if (cursorQuery != null) {
                cursorQuery.close();
            }
            throw th;
        }
        cursorQuery.close();
    }

    public void release() {
        native_finalize();
    }

    protected void finalize() {
        this.mContext.getContentResolver().releaseProvider(this.mMediaProvider);
        native_finalize();
    }
}
