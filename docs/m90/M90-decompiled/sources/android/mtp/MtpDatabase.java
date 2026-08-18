package android.mtp;

import android.content.ContentValues;
import android.content.Context;
import android.content.IContentProvider;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaScanner;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import java.io.File;
import java.util.HashMap;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class MtpDatabase {
    static final int[] ALL_PROPERTIES;
    static final int[] AUDIO_PROPERTIES;
    private static final int DEVICE_PROPERTIES_DATABASE_VERSION = 1;
    static final int[] FILE_PROPERTIES;
    private static final String FORMAT_PARENT_WHERE = "format=? AND parent=?";
    private static final String FORMAT_WHERE = "format=?";
    private static final String ID_WHERE = "_id=?";
    static final int[] IMAGE_PROPERTIES;
    private static final String PARENT_WHERE = "parent=?";
    private static final String PATH_WHERE = "_data=?";
    private static final String STORAGE_FORMAT_PARENT_WHERE = "storage_id=? AND format=? AND parent=?";
    private static final String STORAGE_FORMAT_WHERE = "storage_id=? AND format=?";
    private static final String STORAGE_PARENT_WHERE = "storage_id=? AND parent=?";
    private static final String STORAGE_WHERE = "storage_id=?";
    private static final String TAG = "MtpDatabase";
    static final int[] VIDEO_PROPERTIES;
    private final Context mContext;
    private boolean mDatabaseModified;
    private SharedPreferences mDeviceProperties;
    private final IContentProvider mMediaProvider;
    private final MediaScanner mMediaScanner;
    private final String mMediaStoragePath;
    private int mNativeContext;
    private final Uri mObjectsUri;
    private final String mPackageName;
    private final String[] mSubDirectories;
    private String mSubDirectoriesWhere;
    private String[] mSubDirectoriesWhereArgs;
    private final String mVolumeName;
    private static final String[] ID_PROJECTION = {"_id"};
    private static final String[] PATH_PROJECTION = {"_id", "_data"};
    private static final String[] PATH_FORMAT_PROJECTION = {"_id", "_data", MediaStore.Files.FileColumns.FORMAT};
    private static final String[] OBJECT_INFO_PROJECTION = {"_id", MediaStore.Files.FileColumns.STORAGE_ID, MediaStore.Files.FileColumns.FORMAT, "parent", "_data", "date_added", "date_modified"};
    private final HashMap<String, MtpStorage> mStorageMap = new HashMap<>();
    private final HashMap<Integer, MtpPropertyGroup> mPropertyGroupsByProperty = new HashMap<>();
    private final HashMap<Integer, MtpPropertyGroup> mPropertyGroupsByFormat = new HashMap<>();

    private int[] getSupportedCaptureFormats() {
        return null;
    }

    private final native void native_finalize();

    private final native void native_setup();

    static {
        System.loadLibrary("media_jni");
        FILE_PROPERTIES = new int[]{MtpConstants.PROPERTY_STORAGE_ID, MtpConstants.PROPERTY_OBJECT_FORMAT, MtpConstants.PROPERTY_PROTECTION_STATUS, MtpConstants.PROPERTY_OBJECT_SIZE, MtpConstants.PROPERTY_OBJECT_FILE_NAME, MtpConstants.PROPERTY_DATE_MODIFIED, MtpConstants.PROPERTY_PARENT_OBJECT, MtpConstants.PROPERTY_PERSISTENT_UID, MtpConstants.PROPERTY_NAME, MtpConstants.PROPERTY_DATE_ADDED};
        AUDIO_PROPERTIES = new int[]{MtpConstants.PROPERTY_STORAGE_ID, MtpConstants.PROPERTY_OBJECT_FORMAT, MtpConstants.PROPERTY_PROTECTION_STATUS, MtpConstants.PROPERTY_OBJECT_SIZE, MtpConstants.PROPERTY_OBJECT_FILE_NAME, MtpConstants.PROPERTY_DATE_MODIFIED, MtpConstants.PROPERTY_PARENT_OBJECT, MtpConstants.PROPERTY_PERSISTENT_UID, MtpConstants.PROPERTY_NAME, MtpConstants.PROPERTY_DISPLAY_NAME, MtpConstants.PROPERTY_DATE_ADDED, MtpConstants.PROPERTY_ARTIST, MtpConstants.PROPERTY_ALBUM_NAME, MtpConstants.PROPERTY_ALBUM_ARTIST, MtpConstants.PROPERTY_TRACK, MtpConstants.PROPERTY_ORIGINAL_RELEASE_DATE, MtpConstants.PROPERTY_DURATION, MtpConstants.PROPERTY_GENRE, MtpConstants.PROPERTY_COMPOSER};
        VIDEO_PROPERTIES = new int[]{MtpConstants.PROPERTY_STORAGE_ID, MtpConstants.PROPERTY_OBJECT_FORMAT, MtpConstants.PROPERTY_PROTECTION_STATUS, MtpConstants.PROPERTY_OBJECT_SIZE, MtpConstants.PROPERTY_OBJECT_FILE_NAME, MtpConstants.PROPERTY_DATE_MODIFIED, MtpConstants.PROPERTY_PARENT_OBJECT, MtpConstants.PROPERTY_PERSISTENT_UID, MtpConstants.PROPERTY_NAME, MtpConstants.PROPERTY_DISPLAY_NAME, MtpConstants.PROPERTY_DATE_ADDED, MtpConstants.PROPERTY_ARTIST, MtpConstants.PROPERTY_ALBUM_NAME, MtpConstants.PROPERTY_DURATION, MtpConstants.PROPERTY_DESCRIPTION};
        IMAGE_PROPERTIES = new int[]{MtpConstants.PROPERTY_STORAGE_ID, MtpConstants.PROPERTY_OBJECT_FORMAT, MtpConstants.PROPERTY_PROTECTION_STATUS, MtpConstants.PROPERTY_OBJECT_SIZE, MtpConstants.PROPERTY_OBJECT_FILE_NAME, MtpConstants.PROPERTY_DATE_MODIFIED, MtpConstants.PROPERTY_PARENT_OBJECT, MtpConstants.PROPERTY_PERSISTENT_UID, MtpConstants.PROPERTY_NAME, MtpConstants.PROPERTY_DISPLAY_NAME, MtpConstants.PROPERTY_DATE_ADDED, MtpConstants.PROPERTY_DESCRIPTION};
        ALL_PROPERTIES = new int[]{MtpConstants.PROPERTY_STORAGE_ID, MtpConstants.PROPERTY_OBJECT_FORMAT, MtpConstants.PROPERTY_PROTECTION_STATUS, MtpConstants.PROPERTY_OBJECT_SIZE, MtpConstants.PROPERTY_OBJECT_FILE_NAME, MtpConstants.PROPERTY_DATE_MODIFIED, MtpConstants.PROPERTY_PARENT_OBJECT, MtpConstants.PROPERTY_PERSISTENT_UID, MtpConstants.PROPERTY_NAME, MtpConstants.PROPERTY_DISPLAY_NAME, MtpConstants.PROPERTY_DATE_ADDED, MtpConstants.PROPERTY_DESCRIPTION, MtpConstants.PROPERTY_ARTIST, MtpConstants.PROPERTY_ALBUM_NAME, MtpConstants.PROPERTY_ALBUM_ARTIST, MtpConstants.PROPERTY_TRACK, MtpConstants.PROPERTY_ORIGINAL_RELEASE_DATE, MtpConstants.PROPERTY_DURATION, MtpConstants.PROPERTY_GENRE, MtpConstants.PROPERTY_COMPOSER, MtpConstants.PROPERTY_ARTIST, MtpConstants.PROPERTY_ALBUM_NAME, MtpConstants.PROPERTY_DURATION, MtpConstants.PROPERTY_DESCRIPTION, MtpConstants.PROPERTY_DESCRIPTION};
    }

    public MtpDatabase(Context context, String str, String str2, String[] strArr) throws Throwable {
        native_setup();
        this.mContext = context;
        this.mPackageName = context.getPackageName();
        this.mMediaProvider = context.getContentResolver().acquireProvider(MediaStore.AUTHORITY);
        this.mVolumeName = str;
        this.mMediaStoragePath = str2;
        this.mObjectsUri = MediaStore.Files.getMtpObjectsUri(str);
        this.mMediaScanner = new MediaScanner(context);
        this.mSubDirectories = strArr;
        if (strArr != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("(");
            int length = strArr.length;
            for (int i = 0; i < length; i++) {
                sb.append("_data=? OR _data LIKE ?");
                if (i != length - 1) {
                    sb.append(" OR ");
                }
            }
            sb.append(")");
            this.mSubDirectoriesWhere = sb.toString();
            this.mSubDirectoriesWhereArgs = new String[length * 2];
            int i2 = 0;
            for (String str3 : strArr) {
                String[] strArr2 = this.mSubDirectoriesWhereArgs;
                int i3 = i2 + 1;
                strArr2[i2] = str3;
                i2 = i3 + 1;
                strArr2[i3] = str3 + "/%";
            }
        }
        Locale locale = context.getResources().getConfiguration().locale;
        if (locale != null) {
            String language = locale.getLanguage();
            String country = locale.getCountry();
            if (language != null) {
                if (country != null) {
                    this.mMediaScanner.setLocale(language + "_" + country);
                } else {
                    this.mMediaScanner.setLocale(language);
                }
            }
        }
        initDeviceProperties(context);
    }

    protected void finalize() throws Throwable {
        try {
            native_finalize();
        } finally {
            super.finalize();
        }
    }

    public void addStorage(MtpStorage mtpStorage) {
        this.mStorageMap.put(mtpStorage.getPath(), mtpStorage);
    }

    public void removeStorage(MtpStorage mtpStorage) {
        this.mStorageMap.remove(mtpStorage.getPath());
    }

    /* JADX WARN: Removed duplicated region for block: B:33:0x007a  */
    /* JADX WARN: Removed duplicated region for block: B:35:0x007f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void initDeviceProperties(android.content.Context r12) throws java.lang.Throwable {
        /*
            r11 = this;
            java.lang.String r0 = "device-properties"
            r1 = 0
            android.content.SharedPreferences r2 = r12.getSharedPreferences(r0, r1)
            r11.mDeviceProperties = r2
            java.io.File r2 = r12.getDatabasePath(r0)
            boolean r2 = r2.exists()
            if (r2 == 0) goto L83
            r2 = 0
            android.database.sqlite.SQLiteDatabase r1 = r12.openOrCreateDatabase(r0, r1, r2)     // Catch: java.lang.Throwable -> L5f java.lang.Exception -> L62
            if (r1 == 0) goto L54
            java.lang.String r4 = "properties"
            java.lang.String r3 = "_id"
            java.lang.String r5 = "code"
            java.lang.String r6 = "value"
            java.lang.String[] r5 = new java.lang.String[]{r3, r5, r6}     // Catch: java.lang.Exception -> L52 java.lang.Throwable -> L77
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            r3 = r1
            android.database.Cursor r2 = r3.query(r4, r5, r6, r7, r8, r9, r10)     // Catch: java.lang.Exception -> L52 java.lang.Throwable -> L77
            if (r2 == 0) goto L54
            android.content.SharedPreferences r3 = r11.mDeviceProperties     // Catch: java.lang.Exception -> L52 java.lang.Throwable -> L77
            android.content.SharedPreferences$Editor r3 = r3.edit()     // Catch: java.lang.Exception -> L52 java.lang.Throwable -> L77
        L3a:
            boolean r4 = r2.moveToNext()     // Catch: java.lang.Exception -> L52 java.lang.Throwable -> L77
            if (r4 == 0) goto L4e
            r4 = 1
            java.lang.String r4 = r2.getString(r4)     // Catch: java.lang.Exception -> L52 java.lang.Throwable -> L77
            r5 = 2
            java.lang.String r5 = r2.getString(r5)     // Catch: java.lang.Exception -> L52 java.lang.Throwable -> L77
            r3.putString(r4, r5)     // Catch: java.lang.Exception -> L52 java.lang.Throwable -> L77
            goto L3a
        L4e:
            r3.commit()     // Catch: java.lang.Exception -> L52 java.lang.Throwable -> L77
            goto L54
        L52:
            r3 = move-exception
            goto L64
        L54:
            if (r2 == 0) goto L59
            r2.close()
        L59:
            if (r1 == 0) goto L73
        L5b:
            r1.close()
            goto L73
        L5f:
            r12 = move-exception
            r1 = r2
            goto L78
        L62:
            r3 = move-exception
            r1 = r2
        L64:
            java.lang.String r4 = "MtpDatabase"
            java.lang.String r5 = "failed to migrate device properties"
            android.util.Log.e(r4, r5, r3)     // Catch: java.lang.Throwable -> L77
            if (r2 == 0) goto L70
            r2.close()
        L70:
            if (r1 == 0) goto L73
            goto L5b
        L73:
            r12.deleteDatabase(r0)
            goto L83
        L77:
            r12 = move-exception
        L78:
            if (r2 == 0) goto L7d
            r2.close()
        L7d:
            if (r1 == 0) goto L82
            r1.close()
        L82:
            throw r12
        L83:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.mtp.MtpDatabase.initDeviceProperties(android.content.Context):void");
    }

    private boolean inStorageSubDirectory(String str) {
        if (this.mSubDirectories == null) {
            return true;
        }
        int i = 0;
        if (str == null) {
            return false;
        }
        int length = str.length();
        boolean z = false;
        while (true) {
            String[] strArr = this.mSubDirectories;
            if (i >= strArr.length || z) {
                break;
            }
            String str2 = strArr[i];
            int length2 = str2.length();
            if (length2 < length && str.charAt(length2) == '/' && str.startsWith(str2)) {
                z = true;
            }
            i++;
        }
        return z;
    }

    private boolean isStorageSubDirectory(String str) {
        if (this.mSubDirectories == null) {
            return false;
        }
        int i = 0;
        while (true) {
            String[] strArr = this.mSubDirectories;
            if (i >= strArr.length) {
                return false;
            }
            if (str.equals(strArr[i])) {
                return true;
            }
            i++;
        }
    }

    private int beginSendObject(String str, int i, int i2, int i3, long j, long j2) {
        if (!inStorageSubDirectory(str)) {
            return -1;
        }
        if (str != null) {
            Cursor cursorQuery = null;
            try {
                try {
                    cursorQuery = this.mMediaProvider.query(this.mPackageName, this.mObjectsUri, ID_PROJECTION, PATH_WHERE, new String[]{str}, null, null);
                } catch (RemoteException e) {
                    Log.e(TAG, "RemoteException in beginSendObject", e);
                    if (cursorQuery != null) {
                    }
                }
                if (cursorQuery != null && cursorQuery.getCount() > 0) {
                    Log.w(TAG, "file already exists in beginSendObject: " + str);
                    if (cursorQuery != null) {
                        cursorQuery.close();
                    }
                    return -1;
                }
                if (cursorQuery != null) {
                    cursorQuery.close();
                }
            } catch (Throwable th) {
                if (cursorQuery != null) {
                    cursorQuery.close();
                }
                throw th;
            }
        }
        this.mDatabaseModified = true;
        ContentValues contentValues = new ContentValues();
        contentValues.put("_data", str);
        contentValues.put(MediaStore.Files.FileColumns.FORMAT, Integer.valueOf(i));
        contentValues.put("parent", Integer.valueOf(i2));
        contentValues.put(MediaStore.Files.FileColumns.STORAGE_ID, Integer.valueOf(i3));
        contentValues.put("_size", Long.valueOf(j));
        contentValues.put("date_modified", Long.valueOf(j2));
        try {
            Uri uriInsert = this.mMediaProvider.insert(this.mPackageName, this.mObjectsUri, contentValues);
            if (uriInsert != null) {
                return Integer.parseInt(uriInsert.getPathSegments().get(2));
            }
            return -1;
        } catch (RemoteException e2) {
            Log.e(TAG, "RemoteException in beginSendObject", e2);
            return -1;
        }
    }

    private void endSendObject(String str, int i, int i2, boolean z) throws Throwable {
        if (!z) {
            deleteFile(i);
            return;
        }
        if (i2 == 47621) {
            int iLastIndexOf = str.lastIndexOf(47);
            String strSubstring = iLastIndexOf >= 0 ? str.substring(iLastIndexOf + 1) : str;
            if (strSubstring.endsWith(".pla")) {
                strSubstring = strSubstring.substring(0, strSubstring.length() - 4);
            }
            ContentValues contentValues = new ContentValues(1);
            contentValues.put("_data", str);
            contentValues.put("name", strSubstring);
            contentValues.put(MediaStore.Files.FileColumns.FORMAT, Integer.valueOf(i2));
            contentValues.put("date_modified", Long.valueOf(System.currentTimeMillis() / 1000));
            contentValues.put(MediaStore.MediaColumns.MEDIA_SCANNER_NEW_OBJECT_ID, Integer.valueOf(i));
            try {
                this.mMediaProvider.insert(this.mPackageName, MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, contentValues);
                return;
            } catch (RemoteException e) {
                Log.e(TAG, "RemoteException in endSendObject", e);
                return;
            }
        }
        this.mMediaScanner.scanMtpFile(str, this.mVolumeName, i, i2);
    }

    /* JADX WARN: Removed duplicated region for block: B:29:0x00a0  */
    /* JADX WARN: Removed duplicated region for block: B:39:0x00e2  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private android.database.Cursor createObjectQuery(int r17, int r18, int r19) throws android.os.RemoteException {
        /*
            Method dump skipped, instruction units count: 243
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.mtp.MtpDatabase.createObjectQuery(int, int, int):android.database.Cursor");
    }

    /* JADX WARN: Removed duplicated region for block: B:32:0x0046  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private int[] getObjectList(int r5, int r6, int r7) throws java.lang.Throwable {
        /*
            r4 = this;
            r0 = 0
            android.database.Cursor r5 = r4.createObjectQuery(r5, r6, r7)     // Catch: java.lang.Throwable -> L33 android.os.RemoteException -> L35
            if (r5 != 0) goto Ld
            if (r5 == 0) goto Lc
            r5.close()
        Lc:
            return r0
        Ld:
            int r6 = r5.getCount()     // Catch: android.os.RemoteException -> L31 java.lang.Throwable -> L42
            if (r6 <= 0) goto L2b
            int[] r7 = new int[r6]     // Catch: android.os.RemoteException -> L31 java.lang.Throwable -> L42
            r1 = 0
            r2 = r1
        L17:
            if (r2 >= r6) goto L25
            r5.moveToNext()     // Catch: android.os.RemoteException -> L31 java.lang.Throwable -> L42
            int r3 = r5.getInt(r1)     // Catch: android.os.RemoteException -> L31 java.lang.Throwable -> L42
            r7[r2] = r3     // Catch: android.os.RemoteException -> L31 java.lang.Throwable -> L42
            int r2 = r2 + 1
            goto L17
        L25:
            if (r5 == 0) goto L2a
            r5.close()
        L2a:
            return r7
        L2b:
            if (r5 == 0) goto L41
        L2d:
            r5.close()
            goto L41
        L31:
            r6 = move-exception
            goto L37
        L33:
            r6 = move-exception
            goto L44
        L35:
            r6 = move-exception
            r5 = r0
        L37:
            java.lang.String r7 = "MtpDatabase"
            java.lang.String r1 = "RemoteException in getObjectList"
            android.util.Log.e(r7, r1, r6)     // Catch: java.lang.Throwable -> L42
            if (r5 == 0) goto L41
            goto L2d
        L41:
            return r0
        L42:
            r6 = move-exception
            r0 = r5
        L44:
            if (r0 == 0) goto L49
            r0.close()
        L49:
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: android.mtp.MtpDatabase.getObjectList(int, int, int):int[]");
    }

    private int getNumObjects(int i, int i2, int i3) {
        Cursor cursorCreateObjectQuery = null;
        try {
            try {
                cursorCreateObjectQuery = createObjectQuery(i, i2, i3);
            } catch (RemoteException e) {
                Log.e(TAG, "RemoteException in getNumObjects", e);
                if (cursorCreateObjectQuery == null) {
                    return -1;
                }
            }
            if (cursorCreateObjectQuery != null) {
                int count = cursorCreateObjectQuery.getCount();
                if (cursorCreateObjectQuery != null) {
                    cursorCreateObjectQuery.close();
                }
                return count;
            }
            if (cursorCreateObjectQuery == null) {
                return -1;
            }
            cursorCreateObjectQuery.close();
            return -1;
        } catch (Throwable th) {
            if (cursorCreateObjectQuery != null) {
                cursorCreateObjectQuery.close();
            }
            throw th;
        }
    }

    private int[] getSupportedPlaybackFormats() {
        return new int[]{12288, 12289, 12292, 12293, 12296, 12297, 12299, MtpConstants.FORMAT_EXIF_JPEG, MtpConstants.FORMAT_TIFF_EP, MtpConstants.FORMAT_BMP, MtpConstants.FORMAT_GIF, MtpConstants.FORMAT_JFIF, MtpConstants.FORMAT_PNG, MtpConstants.FORMAT_TIFF, MtpConstants.FORMAT_WMA, MtpConstants.FORMAT_OGG, MtpConstants.FORMAT_AAC, MtpConstants.FORMAT_MP4_CONTAINER, MtpConstants.FORMAT_MP2, MtpConstants.FORMAT_3GP_CONTAINER, MtpConstants.FORMAT_ABSTRACT_AV_PLAYLIST, MtpConstants.FORMAT_WPL_PLAYLIST, MtpConstants.FORMAT_M3U_PLAYLIST, MtpConstants.FORMAT_PLS_PLAYLIST, MtpConstants.FORMAT_XML_DOCUMENT, MtpConstants.FORMAT_FLAC};
    }

    private int[] getSupportedObjectProperties(int i) {
        if (i != 0) {
            if (i != 12299) {
                if (i == 14337 || i == 14340 || i == 14343 || i == 14347) {
                    return IMAGE_PROPERTIES;
                }
                if (i != 47489 && i != 47492) {
                    if (i != 12296 && i != 12297) {
                        switch (i) {
                            case MtpConstants.FORMAT_WMA /* 47361 */:
                            case MtpConstants.FORMAT_OGG /* 47362 */:
                            case MtpConstants.FORMAT_AAC /* 47363 */:
                                break;
                            default:
                                return FILE_PROPERTIES;
                        }
                    }
                    return AUDIO_PROPERTIES;
                }
            }
            return VIDEO_PROPERTIES;
        }
        return ALL_PROPERTIES;
    }

    private int[] getSupportedDeviceProperties() {
        return new int[]{MtpConstants.DEVICE_PROPERTY_SYNCHRONIZATION_PARTNER, MtpConstants.DEVICE_PROPERTY_DEVICE_FRIENDLY_NAME, MtpConstants.DEVICE_PROPERTY_IMAGE_SIZE};
    }

    private MtpPropertyList getObjectPropertyList(long j, int i, long j2, int i2, int i3) {
        MtpPropertyGroup mtpPropertyGroup;
        if (i2 != 0) {
            return new MtpPropertyList(0, MtpConstants.RESPONSE_SPECIFICATION_BY_GROUP_UNSUPPORTED);
        }
        if (j2 == ExpandableListView.PACKED_POSITION_VALUE_NULL) {
            mtpPropertyGroup = this.mPropertyGroupsByFormat.get(Integer.valueOf(i));
            if (mtpPropertyGroup == null) {
                mtpPropertyGroup = new MtpPropertyGroup(this, this.mMediaProvider, this.mPackageName, this.mVolumeName, getSupportedObjectProperties(i));
                this.mPropertyGroupsByFormat.put(new Integer(i), mtpPropertyGroup);
            }
        } else {
            MtpPropertyGroup mtpPropertyGroup2 = this.mPropertyGroupsByProperty.get(Long.valueOf(j2));
            if (mtpPropertyGroup2 == null) {
                int i4 = (int) j2;
                MtpPropertyGroup mtpPropertyGroup3 = new MtpPropertyGroup(this, this.mMediaProvider, this.mPackageName, this.mVolumeName, new int[]{i4});
                this.mPropertyGroupsByProperty.put(new Integer(i4), mtpPropertyGroup3);
                mtpPropertyGroup = mtpPropertyGroup3;
            } else {
                mtpPropertyGroup = mtpPropertyGroup2;
            }
        }
        return mtpPropertyGroup.getPropertyList((int) j, i, i3);
    }

    /* JADX WARN: Removed duplicated region for block: B:13:0x0037  */
    /* JADX WARN: Removed duplicated region for block: B:71:0x0180  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private int renameFile(int r17, java.lang.String r18) throws java.lang.Throwable {
        /*
            Method dump skipped, instruction units count: 388
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.mtp.MtpDatabase.renameFile(int, java.lang.String):int");
    }

    private int setObjectProperty(int i, int i2, long j, String str) {
        return i2 != 56327 ? MtpConstants.RESPONSE_OBJECT_PROP_NOT_SUPPORTED : renameFile(i, str);
    }

    private int getDeviceProperty(int i, long[] jArr, char[] cArr) {
        if (i != 20483) {
            switch (i) {
                case MtpConstants.DEVICE_PROPERTY_SYNCHRONIZATION_PARTNER /* 54273 */:
                case MtpConstants.DEVICE_PROPERTY_DEVICE_FRIENDLY_NAME /* 54274 */:
                    String string = this.mDeviceProperties.getString(Integer.toString(i), "");
                    int length = string.length();
                    if (length > 255) {
                        length = 255;
                    }
                    string.getChars(0, length, cArr, 0);
                    cArr[length] = 0;
                    break;
            }
            return MtpConstants.RESPONSE_OK;
        }
        Display defaultDisplay = ((WindowManager) this.mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        String str = Integer.toString(defaultDisplay.getMaximumSizeDimension()) + "x" + Integer.toString(defaultDisplay.getMaximumSizeDimension());
        str.getChars(0, str.length(), cArr, 0);
        cArr[str.length()] = 0;
        return MtpConstants.RESPONSE_OK;
    }

    private int setDeviceProperty(int i, long j, String str) {
        switch (i) {
            case MtpConstants.DEVICE_PROPERTY_SYNCHRONIZATION_PARTNER /* 54273 */:
            case MtpConstants.DEVICE_PROPERTY_DEVICE_FRIENDLY_NAME /* 54274 */:
                SharedPreferences.Editor editorEdit = this.mDeviceProperties.edit();
                editorEdit.putString(Integer.toString(i), str);
                if (editorEdit.commit()) {
                    return MtpConstants.RESPONSE_OK;
                }
                return 8194;
            default:
                return MtpConstants.RESPONSE_DEVICE_PROP_NOT_SUPPORTED;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:27:0x0088 A[PHI: r1
      0x0088: PHI (r1v3 android.database.Cursor) = (r1v2 android.database.Cursor), (r1v4 android.database.Cursor) binds: [B:26:0x0086, B:20:0x0079] A[DONT_GENERATE, DONT_INLINE]] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean getObjectInfo(int r12, int[] r13, char[] r14, long[] r15) {
        /*
            r11 = this;
            r0 = 0
            r1 = 0
            android.content.IContentProvider r2 = r11.mMediaProvider     // Catch: java.lang.Throwable -> L7c android.os.RemoteException -> L7e
            java.lang.String r3 = r11.mPackageName     // Catch: java.lang.Throwable -> L7c android.os.RemoteException -> L7e
            android.net.Uri r4 = r11.mObjectsUri     // Catch: java.lang.Throwable -> L7c android.os.RemoteException -> L7e
            java.lang.String[] r5 = android.mtp.MtpDatabase.OBJECT_INFO_PROJECTION     // Catch: java.lang.Throwable -> L7c android.os.RemoteException -> L7e
            java.lang.String r6 = "_id=?"
            r10 = 1
            java.lang.String[] r7 = new java.lang.String[r10]     // Catch: java.lang.Throwable -> L7c android.os.RemoteException -> L7e
            java.lang.String r12 = java.lang.Integer.toString(r12)     // Catch: java.lang.Throwable -> L7c android.os.RemoteException -> L7e
            r7[r0] = r12     // Catch: java.lang.Throwable -> L7c android.os.RemoteException -> L7e
            r8 = 0
            r9 = 0
            android.database.Cursor r1 = r2.query(r3, r4, r5, r6, r7, r8, r9)     // Catch: java.lang.Throwable -> L7c android.os.RemoteException -> L7e
            if (r1 == 0) goto L79
            boolean r12 = r1.moveToNext()     // Catch: java.lang.Throwable -> L7c android.os.RemoteException -> L7e
            if (r12 == 0) goto L79
            int r12 = r1.getInt(r10)     // Catch: java.lang.Throwable -> L7c android.os.RemoteException -> L7e
            r13[r0] = r12     // Catch: java.lang.Throwable -> L7c android.os.RemoteException -> L7e
            r12 = 2
            int r2 = r1.getInt(r12)     // Catch: java.lang.Throwable -> L7c android.os.RemoteException -> L7e
            r13[r10] = r2     // Catch: java.lang.Throwable -> L7c android.os.RemoteException -> L7e
            r2 = 3
            int r2 = r1.getInt(r2)     // Catch: java.lang.Throwable -> L7c android.os.RemoteException -> L7e
            r13[r12] = r2     // Catch: java.lang.Throwable -> L7c android.os.RemoteException -> L7e
            r12 = 4
            java.lang.String r12 = r1.getString(r12)     // Catch: java.lang.Throwable -> L7c android.os.RemoteException -> L7e
            r13 = 47
            int r13 = r12.lastIndexOf(r13)     // Catch: java.lang.Throwable -> L7c android.os.RemoteException -> L7e
            if (r13 < 0) goto L46
            int r13 = r13 + r10
            goto L47
        L46:
            r13 = r0
        L47:
            int r2 = r12.length()     // Catch: java.lang.Throwable -> L7c android.os.RemoteException -> L7e
            int r3 = r2 - r13
            r4 = 255(0xff, float:3.57E-43)
            if (r3 <= r4) goto L53
            int r2 = r13 + 255
        L53:
            r12.getChars(r13, r2, r14, r0)     // Catch: java.lang.Throwable -> L7c android.os.RemoteException -> L7e
            int r2 = r2 - r13
            r14[r2] = r0     // Catch: java.lang.Throwable -> L7c android.os.RemoteException -> L7e
            r12 = 5
            long r12 = r1.getLong(r12)     // Catch: java.lang.Throwable -> L7c android.os.RemoteException -> L7e
            r15[r0] = r12     // Catch: java.lang.Throwable -> L7c android.os.RemoteException -> L7e
            r12 = 6
            long r12 = r1.getLong(r12)     // Catch: java.lang.Throwable -> L7c android.os.RemoteException -> L7e
            r15[r10] = r12     // Catch: java.lang.Throwable -> L7c android.os.RemoteException -> L7e
            r12 = r15[r0]     // Catch: java.lang.Throwable -> L7c android.os.RemoteException -> L7e
            r2 = 0
            int r12 = (r12 > r2 ? 1 : (r12 == r2 ? 0 : -1))
            if (r12 != 0) goto L73
            r12 = r15[r10]     // Catch: java.lang.Throwable -> L7c android.os.RemoteException -> L7e
            r15[r0] = r12     // Catch: java.lang.Throwable -> L7c android.os.RemoteException -> L7e
        L73:
            if (r1 == 0) goto L78
            r1.close()
        L78:
            return r10
        L79:
            if (r1 == 0) goto L8b
            goto L88
        L7c:
            r12 = move-exception
            goto L8c
        L7e:
            r12 = move-exception
            java.lang.String r13 = "MtpDatabase"
            java.lang.String r14 = "RemoteException in getObjectInfo"
            android.util.Log.e(r13, r14, r12)     // Catch: java.lang.Throwable -> L7c
            if (r1 == 0) goto L8b
        L88:
            r1.close()
        L8b:
            return r0
        L8c:
            if (r1 == 0) goto L91
            r1.close()
        L91:
            throw r12
        */
        throw new UnsupportedOperationException("Method not decompiled: android.mtp.MtpDatabase.getObjectInfo(int, int[], char[], long[]):boolean");
    }

    private int getObjectFilePath(int i, char[] cArr, long[] jArr) {
        if (i == 0) {
            String str = this.mMediaStoragePath;
            str.getChars(0, str.length(), cArr, 0);
            cArr[this.mMediaStoragePath.length()] = 0;
            jArr[0] = 0;
            jArr[1] = 12289;
            return MtpConstants.RESPONSE_OK;
        }
        Cursor cursorQuery = null;
        try {
            try {
                cursorQuery = this.mMediaProvider.query(this.mPackageName, this.mObjectsUri, PATH_FORMAT_PROJECTION, ID_WHERE, new String[]{Integer.toString(i)}, null, null);
                if (cursorQuery == null || !cursorQuery.moveToNext()) {
                    if (cursorQuery != null) {
                        cursorQuery.close();
                    }
                    return MtpConstants.RESPONSE_INVALID_OBJECT_HANDLE;
                }
                String string = cursorQuery.getString(1);
                string.getChars(0, string.length(), cArr, 0);
                cArr[string.length()] = 0;
                jArr[0] = new File(string).length();
                jArr[1] = cursorQuery.getLong(2);
                if (cursorQuery != null) {
                    cursorQuery.close();
                }
                return MtpConstants.RESPONSE_OK;
            } catch (RemoteException e) {
                Log.e(TAG, "RemoteException in getObjectFilePath", e);
                if (cursorQuery != null) {
                    cursorQuery.close();
                }
                return 8194;
            }
        } catch (Throwable th) {
            if (cursorQuery != null) {
                cursorQuery.close();
            }
            throw th;
        }
    }

    private int deleteFile(int i) throws Throwable {
        Cursor cursor;
        Cursor cursorQuery;
        this.mDatabaseModified = true;
        try {
            cursorQuery = this.mMediaProvider.query(this.mPackageName, this.mObjectsUri, PATH_FORMAT_PROJECTION, ID_WHERE, new String[]{Integer.toString(i)}, null, null);
        } catch (RemoteException e) {
            e = e;
            cursor = null;
        } catch (Throwable th) {
            th = th;
            cursor = null;
        }
        if (cursorQuery != null) {
            try {
                try {
                } catch (Throwable th2) {
                    th = th2;
                    cursor = cursorQuery;
                }
                if (cursorQuery.moveToNext()) {
                    String string = cursorQuery.getString(1);
                    int i2 = cursorQuery.getInt(2);
                    if (string != null && i2 != 0) {
                        if (isStorageSubDirectory(string)) {
                            if (cursorQuery != null) {
                                cursorQuery.close();
                            }
                            return MtpConstants.RESPONSE_OBJECT_WRITE_PROTECTED;
                        }
                        if (i2 == 12289) {
                            this.mMediaProvider.delete(this.mPackageName, MediaStore.Files.getMtpObjectsUri(this.mVolumeName), "_data LIKE ?1 AND lower(substr(_data,1,?2))=lower(?3)", new String[]{string + "/%", Integer.toString(string.length() + 1), string + "/"});
                        }
                        if (this.mMediaProvider.delete(this.mPackageName, MediaStore.Files.getMtpObjectsUri(this.mVolumeName, i), null, null) <= 0) {
                            if (cursorQuery == null) {
                                return MtpConstants.RESPONSE_INVALID_OBJECT_HANDLE;
                            }
                            cursorQuery.close();
                            return MtpConstants.RESPONSE_INVALID_OBJECT_HANDLE;
                        }
                        if (i2 != 12289 && string.toLowerCase(Locale.US).endsWith("/.nomedia")) {
                            try {
                                this.mMediaProvider.call(this.mPackageName, MediaStore.UNHIDE_CALL, string.substring(0, string.lastIndexOf("/")), null);
                            } catch (RemoteException unused) {
                                Log.e(TAG, "failed to unhide/rescan for " + string);
                            }
                        }
                        if (cursorQuery != null) {
                            cursorQuery.close();
                        }
                        return MtpConstants.RESPONSE_OK;
                        if (cursor != null) {
                            cursor.close();
                        }
                        throw th;
                    }
                    if (cursorQuery == null) {
                        return 8194;
                    }
                    cursorQuery.close();
                    return 8194;
                }
            } catch (RemoteException e2) {
                e = e2;
                cursor = cursorQuery;
                try {
                    Log.e(TAG, "RemoteException in deleteFile", e);
                    if (cursor == null) {
                        return 8194;
                    }
                    cursor.close();
                    return 8194;
                } catch (Throwable th3) {
                    th = th3;
                }
            }
        }
        if (cursorQuery == null) {
            return MtpConstants.RESPONSE_INVALID_OBJECT_HANDLE;
        }
        cursorQuery.close();
        return MtpConstants.RESPONSE_INVALID_OBJECT_HANDLE;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:27:0x0051 A[PHI: r0
      0x0051: PHI (r0v6 android.database.Cursor) = (r0v5 android.database.Cursor), (r0v7 android.database.Cursor) binds: [B:26:0x004f, B:17:0x003c] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Removed duplicated region for block: B:31:0x0058  */
    /* JADX WARN: Type inference failed for: r0v0, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r0v2 */
    /* JADX WARN: Type inference failed for: r0v4, types: [android.database.Cursor] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private int[] getObjectReferences(int r13) throws java.lang.Throwable {
        /*
            r12 = this;
            java.lang.String r0 = r12.mVolumeName
            long r1 = (long) r13
            android.net.Uri r5 = android.provider.MediaStore.Files.getMtpReferencesUri(r0, r1)
            r13 = 0
            android.content.IContentProvider r3 = r12.mMediaProvider     // Catch: java.lang.Throwable -> L41 android.os.RemoteException -> L46
            java.lang.String r4 = r12.mPackageName     // Catch: java.lang.Throwable -> L41 android.os.RemoteException -> L46
            java.lang.String[] r6 = android.mtp.MtpDatabase.ID_PROJECTION     // Catch: java.lang.Throwable -> L41 android.os.RemoteException -> L46
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            android.database.Cursor r0 = r3.query(r4, r5, r6, r7, r8, r9, r10)     // Catch: java.lang.Throwable -> L41 android.os.RemoteException -> L46
            if (r0 != 0) goto L1e
            if (r0 == 0) goto L1d
            r0.close()
        L1d:
            return r13
        L1e:
            int r1 = r0.getCount()     // Catch: android.os.RemoteException -> L3f java.lang.Throwable -> L55
            if (r1 <= 0) goto L3c
            int[] r2 = new int[r1]     // Catch: android.os.RemoteException -> L3f java.lang.Throwable -> L55
            r3 = 0
            r4 = r3
        L28:
            if (r4 >= r1) goto L36
            r0.moveToNext()     // Catch: android.os.RemoteException -> L3f java.lang.Throwable -> L55
            int r5 = r0.getInt(r3)     // Catch: android.os.RemoteException -> L3f java.lang.Throwable -> L55
            r2[r4] = r5     // Catch: android.os.RemoteException -> L3f java.lang.Throwable -> L55
            int r4 = r4 + 1
            goto L28
        L36:
            if (r0 == 0) goto L3b
            r0.close()
        L3b:
            return r2
        L3c:
            if (r0 == 0) goto L54
            goto L51
        L3f:
            r1 = move-exception
            goto L48
        L41:
            r0 = move-exception
            r11 = r0
            r0 = r13
            r13 = r11
            goto L56
        L46:
            r1 = move-exception
            r0 = r13
        L48:
            java.lang.String r2 = "MtpDatabase"
            java.lang.String r3 = "RemoteException in getObjectList"
            android.util.Log.e(r2, r3, r1)     // Catch: java.lang.Throwable -> L55
            if (r0 == 0) goto L54
        L51:
            r0.close()
        L54:
            return r13
        L55:
            r13 = move-exception
        L56:
            if (r0 == 0) goto L5b
            r0.close()
        L5b:
            throw r13
        */
        throw new UnsupportedOperationException("Method not decompiled: android.mtp.MtpDatabase.getObjectReferences(int):int[]");
    }

    private int setObjectReferences(int i, int[] iArr) {
        this.mDatabaseModified = true;
        Uri mtpReferencesUri = MediaStore.Files.getMtpReferencesUri(this.mVolumeName, i);
        int length = iArr.length;
        ContentValues[] contentValuesArr = new ContentValues[length];
        for (int i2 = 0; i2 < length; i2++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("_id", Integer.valueOf(iArr[i2]));
            contentValuesArr[i2] = contentValues;
        }
        try {
            if (this.mMediaProvider.bulkInsert(this.mPackageName, mtpReferencesUri, contentValuesArr) > 0) {
                return MtpConstants.RESPONSE_OK;
            }
            return 8194;
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException in setObjectReferences", e);
            return 8194;
        }
    }

    private void sessionStarted() {
        this.mDatabaseModified = false;
    }

    private void sessionEnded() {
        if (this.mDatabaseModified) {
            this.mContext.sendBroadcast(new Intent(MediaStore.ACTION_MTP_SESSION_END));
            this.mDatabaseModified = false;
        }
    }
}
