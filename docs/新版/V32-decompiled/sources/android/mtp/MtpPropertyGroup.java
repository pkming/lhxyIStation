package android.mtp;

import android.content.IContentProvider;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
class MtpPropertyGroup {
    private static final String FORMAT_WHERE = "format=?";
    private static final String ID_FORMAT_WHERE = "_id=? AND format=?";
    private static final String ID_WHERE = "_id=?";
    private static final String PARENT_FORMAT_WHERE = "parent=? AND format=?";
    private static final String PARENT_WHERE = "parent=?";
    private static final String TAG = "MtpPropertyGroup";
    private String[] mColumns;
    private final MtpDatabase mDatabase;
    private final String mPackageName;
    private final Property[] mProperties;
    private final IContentProvider mProvider;
    private final Uri mUri;
    private final String mVolumeName;

    private native String format_date_time(long j);

    private class Property {
        int code;
        int column;
        int type;

        Property(int i, int i2, int i3) {
            this.code = i;
            this.type = i2;
            this.column = i3;
        }
    }

    public MtpPropertyGroup(MtpDatabase mtpDatabase, IContentProvider iContentProvider, String str, String str2, int[] iArr) {
        this.mDatabase = mtpDatabase;
        this.mProvider = iContentProvider;
        this.mPackageName = str;
        this.mVolumeName = str2;
        this.mUri = MediaStore.Files.getMtpObjectsUri(str2);
        int length = iArr.length;
        ArrayList<String> arrayList = new ArrayList<>(length);
        arrayList.add("_id");
        this.mProperties = new Property[length];
        for (int i = 0; i < length; i++) {
            this.mProperties[i] = createProperty(iArr[i], arrayList);
        }
        int size = arrayList.size();
        this.mColumns = new String[size];
        for (int i2 = 0; i2 < size; i2++) {
            this.mColumns[i2] = arrayList.get(i2);
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private Property createProperty(int i, ArrayList<String> arrayList) {
        String str = MediaStore.Files.FileColumns.STORAGE_ID;
        int i2 = 4;
        switch (i) {
            case MtpConstants.PROPERTY_STORAGE_ID /* 56321 */:
                i2 = 6;
                break;
            case MtpConstants.PROPERTY_OBJECT_FORMAT /* 56322 */:
                str = MediaStore.Files.FileColumns.FORMAT;
                break;
            case MtpConstants.PROPERTY_PROTECTION_STATUS /* 56323 */:
                str = null;
                break;
            case MtpConstants.PROPERTY_OBJECT_SIZE /* 56324 */:
                i2 = 8;
                str = "_size";
                break;
            case MtpConstants.PROPERTY_OBJECT_FILE_NAME /* 56327 */:
                str = "_data";
                i2 = 65535;
                break;
            case MtpConstants.PROPERTY_DATE_MODIFIED /* 56329 */:
                str = "date_modified";
                i2 = 65535;
                break;
            case MtpConstants.PROPERTY_PARENT_OBJECT /* 56331 */:
                str = "parent";
                i2 = 6;
                break;
            case MtpConstants.PROPERTY_PERSISTENT_UID /* 56385 */:
                i2 = 10;
                break;
            case MtpConstants.PROPERTY_NAME /* 56388 */:
                str = "title";
                i2 = 65535;
                break;
            case MtpConstants.PROPERTY_ARTIST /* 56390 */:
            case MtpConstants.PROPERTY_GENRE /* 56460 */:
            case MtpConstants.PROPERTY_ALBUM_NAME /* 56474 */:
                i2 = 65535;
                str = null;
                break;
            case MtpConstants.PROPERTY_DESCRIPTION /* 56392 */:
                str = "description";
                i2 = 65535;
                break;
            case MtpConstants.PROPERTY_DATE_ADDED /* 56398 */:
                str = "date_added";
                i2 = 65535;
                break;
            case MtpConstants.PROPERTY_DURATION /* 56457 */:
                str = "duration";
                i2 = 6;
                break;
            case MtpConstants.PROPERTY_TRACK /* 56459 */:
                str = MediaStore.Audio.AudioColumns.TRACK;
                break;
            case MtpConstants.PROPERTY_COMPOSER /* 56470 */:
                str = MediaStore.Audio.AudioColumns.COMPOSER;
                i2 = 65535;
                break;
            case MtpConstants.PROPERTY_ORIGINAL_RELEASE_DATE /* 56473 */:
                str = MediaStore.Audio.AudioColumns.YEAR;
                i2 = 65535;
                break;
            case MtpConstants.PROPERTY_ALBUM_ARTIST /* 56475 */:
                str = MediaStore.Audio.AudioColumns.ALBUM_ARTIST;
                i2 = 65535;
                break;
            case MtpConstants.PROPERTY_DISPLAY_NAME /* 56544 */:
                str = "_display_name";
                i2 = 65535;
                break;
            default:
                i2 = 0;
                Log.e(TAG, "unsupported property " + i);
                str = null;
                break;
        }
        if (str != null) {
            arrayList.add(str);
            return new Property(i, i2, arrayList.size() - 1);
        }
        return new Property(i, i2, -1);
    }

    private String queryString(int i, String str) throws Throwable {
        Cursor cursorQuery;
        Cursor cursor = null;
        try {
            cursorQuery = this.mProvider.query(this.mPackageName, this.mUri, new String[]{"_id", str}, ID_WHERE, new String[]{Integer.toString(i)}, null, null);
            if (cursorQuery != null) {
                try {
                    if (cursorQuery.moveToNext()) {
                        String string = cursorQuery.getString(1);
                        if (cursorQuery != null) {
                            cursorQuery.close();
                        }
                        return string;
                    }
                } catch (Exception unused) {
                    if (cursorQuery != null) {
                        cursorQuery.close();
                    }
                    return null;
                } catch (Throwable th) {
                    th = th;
                    cursor = cursorQuery;
                    if (cursor != null) {
                        cursor.close();
                    }
                    throw th;
                }
            }
            if (cursorQuery != null) {
                cursorQuery.close();
            }
            return "";
        } catch (Exception unused2) {
            cursorQuery = null;
        } catch (Throwable th2) {
            th = th2;
        }
    }

    private String queryAudio(int i, String str) throws Throwable {
        Cursor cursorQuery;
        Cursor cursor = null;
        try {
            cursorQuery = this.mProvider.query(this.mPackageName, MediaStore.Audio.Media.getContentUri(this.mVolumeName), new String[]{"_id", str}, ID_WHERE, new String[]{Integer.toString(i)}, null, null);
            if (cursorQuery != null) {
                try {
                    if (cursorQuery.moveToNext()) {
                        String string = cursorQuery.getString(1);
                        if (cursorQuery != null) {
                            cursorQuery.close();
                        }
                        return string;
                    }
                } catch (Exception unused) {
                    if (cursorQuery != null) {
                        cursorQuery.close();
                    }
                    return null;
                } catch (Throwable th) {
                    th = th;
                    cursor = cursorQuery;
                    if (cursor != null) {
                        cursor.close();
                    }
                    throw th;
                }
            }
            if (cursorQuery != null) {
                cursorQuery.close();
            }
            return "";
        } catch (Exception unused2) {
            cursorQuery = null;
        } catch (Throwable th2) {
            th = th2;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:27:0x0050  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private java.lang.String queryGenre(int r12) throws java.lang.Throwable {
        /*
            r11 = this;
            r0 = 0
            java.lang.String r1 = r11.mVolumeName     // Catch: java.lang.Throwable -> L38 java.lang.Exception -> L3d
            android.net.Uri r4 = android.provider.MediaStore.Audio.Genres.getContentUriForAudioId(r1, r12)     // Catch: java.lang.Throwable -> L38 java.lang.Exception -> L3d
            android.content.IContentProvider r2 = r11.mProvider     // Catch: java.lang.Throwable -> L38 java.lang.Exception -> L3d
            java.lang.String r3 = r11.mPackageName     // Catch: java.lang.Throwable -> L38 java.lang.Exception -> L3d
            java.lang.String r12 = "_id"
            java.lang.String r1 = "name"
            java.lang.String[] r5 = new java.lang.String[]{r12, r1}     // Catch: java.lang.Throwable -> L38 java.lang.Exception -> L3d
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            android.database.Cursor r12 = r2.query(r3, r4, r5, r6, r7, r8, r9)     // Catch: java.lang.Throwable -> L38 java.lang.Exception -> L3d
            if (r12 == 0) goto L2e
            boolean r1 = r12.moveToNext()     // Catch: java.lang.Exception -> L36 java.lang.Throwable -> L4d
            if (r1 == 0) goto L2e
            r1 = 1
            java.lang.String r0 = r12.getString(r1)     // Catch: java.lang.Exception -> L36 java.lang.Throwable -> L4d
            if (r12 == 0) goto L2d
            r12.close()
        L2d:
            return r0
        L2e:
            java.lang.String r0 = ""
            if (r12 == 0) goto L35
            r12.close()
        L35:
            return r0
        L36:
            r1 = move-exception
            goto L3f
        L38:
            r12 = move-exception
            r10 = r0
            r0 = r12
            r12 = r10
            goto L4e
        L3d:
            r1 = move-exception
            r12 = r0
        L3f:
            java.lang.String r2 = "MtpPropertyGroup"
            java.lang.String r3 = "queryGenre exception"
            android.util.Log.e(r2, r3, r1)     // Catch: java.lang.Throwable -> L4d
            if (r12 == 0) goto L4c
            r12.close()
        L4c:
            return r0
        L4d:
            r0 = move-exception
        L4e:
            if (r12 == 0) goto L53
            r12.close()
        L53:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: android.mtp.MtpPropertyGroup.queryGenre(int):java.lang.String");
    }

    /* JADX WARN: Removed duplicated region for block: B:21:0x004a A[PHI: r11
      0x004a: PHI (r11v3 android.database.Cursor) = (r11v2 android.database.Cursor), (r11v5 android.database.Cursor) binds: [B:20:0x0048, B:13:0x003d] A[DONT_GENERATE, DONT_INLINE]] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private java.lang.Long queryLong(int r11, java.lang.String r12) throws java.lang.Throwable {
        /*
            r10 = this;
            r0 = 0
            android.content.IContentProvider r1 = r10.mProvider     // Catch: java.lang.Throwable -> L40 java.lang.Exception -> L47
            java.lang.String r2 = r10.mPackageName     // Catch: java.lang.Throwable -> L40 java.lang.Exception -> L47
            android.net.Uri r3 = r10.mUri     // Catch: java.lang.Throwable -> L40 java.lang.Exception -> L47
            r4 = 2
            java.lang.String[] r4 = new java.lang.String[r4]     // Catch: java.lang.Throwable -> L40 java.lang.Exception -> L47
            java.lang.String r5 = "_id"
            r6 = 0
            r4[r6] = r5     // Catch: java.lang.Throwable -> L40 java.lang.Exception -> L47
            r9 = 1
            r4[r9] = r12     // Catch: java.lang.Throwable -> L40 java.lang.Exception -> L47
            java.lang.String r5 = "_id=?"
            java.lang.String[] r12 = new java.lang.String[r9]     // Catch: java.lang.Throwable -> L40 java.lang.Exception -> L47
            java.lang.String r11 = java.lang.Integer.toString(r11)     // Catch: java.lang.Throwable -> L40 java.lang.Exception -> L47
            r12[r6] = r11     // Catch: java.lang.Throwable -> L40 java.lang.Exception -> L47
            r7 = 0
            r8 = 0
            r6 = r12
            android.database.Cursor r11 = r1.query(r2, r3, r4, r5, r6, r7, r8)     // Catch: java.lang.Throwable -> L40 java.lang.Exception -> L47
            if (r11 == 0) goto L3d
            boolean r12 = r11.moveToNext()     // Catch: java.lang.Throwable -> L3a java.lang.Exception -> L48
            if (r12 == 0) goto L3d
            java.lang.Long r12 = new java.lang.Long     // Catch: java.lang.Throwable -> L3a java.lang.Exception -> L48
            long r1 = r11.getLong(r9)     // Catch: java.lang.Throwable -> L3a java.lang.Exception -> L48
            r12.<init>(r1)     // Catch: java.lang.Throwable -> L3a java.lang.Exception -> L48
            if (r11 == 0) goto L39
            r11.close()
        L39:
            return r12
        L3a:
            r12 = move-exception
            r0 = r11
            goto L41
        L3d:
            if (r11 == 0) goto L4d
            goto L4a
        L40:
            r12 = move-exception
        L41:
            if (r0 == 0) goto L46
            r0.close()
        L46:
            throw r12
        L47:
            r11 = r0
        L48:
            if (r11 == 0) goto L4d
        L4a:
            r11.close()
        L4d:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: android.mtp.MtpPropertyGroup.queryLong(int, java.lang.String):java.lang.Long");
    }

    private static String nameFromPath(String str) {
        int iLastIndexOf = str.lastIndexOf(47);
        int i = iLastIndexOf >= 0 ? iLastIndexOf + 1 : 0;
        int length = str.length();
        if (length - i > 255) {
            length = i + 255;
        }
        return str.substring(i, length);
    }

    /* JADX WARN: Removed duplicated region for block: B:26:0x005f A[Catch: all -> 0x0196, RemoteException -> 0x0198, TryCatch #1 {RemoteException -> 0x0198, blocks: (B:24:0x005a, B:34:0x007e, B:35:0x0082, B:38:0x0092, B:40:0x009b, B:42:0x00a0, B:43:0x00a6, B:44:0x00a9, B:72:0x0167, B:73:0x016f, B:75:0x0173, B:76:0x017d, B:45:0x00ad, B:46:0x00b8, B:47:0x00d8, B:49:0x00de, B:50:0x00e3, B:51:0x00e8, B:52:0x00f7, B:53:0x0102, B:55:0x0108, B:57:0x0110, B:59:0x0118, B:61:0x011e, B:62:0x0123, B:63:0x0128, B:64:0x013a, B:65:0x0147, B:67:0x014d, B:68:0x0155, B:69:0x0159, B:26:0x005f, B:28:0x0070), top: B:92:0x005a, outer: #0 }] */
    /* JADX WARN: Removed duplicated region for block: B:28:0x0070 A[Catch: all -> 0x0196, RemoteException -> 0x0198, TRY_LEAVE, TryCatch #1 {RemoteException -> 0x0198, blocks: (B:24:0x005a, B:34:0x007e, B:35:0x0082, B:38:0x0092, B:40:0x009b, B:42:0x00a0, B:43:0x00a6, B:44:0x00a9, B:72:0x0167, B:73:0x016f, B:75:0x0173, B:76:0x017d, B:45:0x00ad, B:46:0x00b8, B:47:0x00d8, B:49:0x00de, B:50:0x00e3, B:51:0x00e8, B:52:0x00f7, B:53:0x0102, B:55:0x0108, B:57:0x0110, B:59:0x0118, B:61:0x011e, B:62:0x0123, B:63:0x0128, B:64:0x013a, B:65:0x0147, B:67:0x014d, B:68:0x0155, B:69:0x0159, B:26:0x005f, B:28:0x0070), top: B:92:0x005a, outer: #0 }] */
    /* JADX WARN: Removed duplicated region for block: B:34:0x007e A[Catch: all -> 0x0196, RemoteException -> 0x0198, TRY_ENTER, TryCatch #1 {RemoteException -> 0x0198, blocks: (B:24:0x005a, B:34:0x007e, B:35:0x0082, B:38:0x0092, B:40:0x009b, B:42:0x00a0, B:43:0x00a6, B:44:0x00a9, B:72:0x0167, B:73:0x016f, B:75:0x0173, B:76:0x017d, B:45:0x00ad, B:46:0x00b8, B:47:0x00d8, B:49:0x00de, B:50:0x00e3, B:51:0x00e8, B:52:0x00f7, B:53:0x0102, B:55:0x0108, B:57:0x0110, B:59:0x0118, B:61:0x011e, B:62:0x0123, B:63:0x0128, B:64:0x013a, B:65:0x0147, B:67:0x014d, B:68:0x0155, B:69:0x0159, B:26:0x005f, B:28:0x0070), top: B:92:0x005a, outer: #0 }] */
    /* JADX WARN: Removed duplicated region for block: B:37:0x0090  */
    /* JADX WARN: Removed duplicated region for block: B:80:0x0192  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    android.mtp.MtpPropertyList getPropertyList(int r18, int r19, int r20) {
        /*
            Method dump skipped, instruction units count: 474
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.mtp.MtpPropertyGroup.getPropertyList(int, int, int):android.mtp.MtpPropertyList");
    }
}
