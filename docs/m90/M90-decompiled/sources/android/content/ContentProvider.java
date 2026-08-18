package android.content;

import android.app.AppOpsManager;
import android.app.backup.FullBackup;
import android.content.pm.PathPermission;
import android.content.pm.ProviderInfo;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ICancellationSignal;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.os.UserHandle;
import android.util.Log;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public abstract class ContentProvider implements ComponentCallbacks2 {
    private static final String TAG = "ContentProvider";
    private final ThreadLocal<String> mCallingPackage;
    private Context mContext;
    private boolean mExported;
    private int mMyUid;
    private boolean mNoPerms;
    private PathPermission[] mPathPermissions;
    private String mReadPermission;
    private Transport mTransport;
    private String mWritePermission;

    public interface PipeDataWriter<T> {
        void writeDataToPipe(ParcelFileDescriptor parcelFileDescriptor, Uri uri, String str, Bundle bundle, T t);
    }

    public Bundle call(String str, String str2, Bundle bundle) {
        return null;
    }

    public Uri canonicalize(Uri uri) {
        return null;
    }

    public abstract int delete(Uri uri, String str, String[] strArr);

    public String[] getStreamTypes(Uri uri, String str) {
        return null;
    }

    public abstract String getType(Uri uri);

    public abstract Uri insert(Uri uri, ContentValues contentValues);

    protected boolean isTemporary() {
        return false;
    }

    @Override // android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
    }

    public abstract boolean onCreate();

    @Override // android.content.ComponentCallbacks
    public void onLowMemory() {
    }

    @Override // android.content.ComponentCallbacks2
    public void onTrimMemory(int i) {
    }

    public abstract Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2);

    public Uri uncanonicalize(Uri uri) {
        return uri;
    }

    public abstract int update(Uri uri, ContentValues contentValues, String str, String[] strArr);

    public ContentProvider() {
        this.mContext = null;
        this.mCallingPackage = new ThreadLocal<>();
        this.mTransport = new Transport();
    }

    public ContentProvider(Context context, String str, String str2, PathPermission[] pathPermissionArr) {
        this.mContext = null;
        this.mCallingPackage = new ThreadLocal<>();
        this.mTransport = new Transport();
        this.mContext = context;
        this.mReadPermission = str;
        this.mWritePermission = str2;
        this.mPathPermissions = pathPermissionArr;
    }

    public static ContentProvider coerceToLocalContentProvider(IContentProvider iContentProvider) {
        if (iContentProvider instanceof Transport) {
            return ((Transport) iContentProvider).getContentProvider();
        }
        return null;
    }

    class Transport extends ContentProviderNative {
        AppOpsManager mAppOpsManager = null;
        int mReadOp = -1;
        int mWriteOp = -1;

        Transport() {
        }

        ContentProvider getContentProvider() {
            return ContentProvider.this;
        }

        @Override // android.content.ContentProviderNative
        public String getProviderName() {
            return getContentProvider().getClass().getName();
        }

        @Override // android.content.IContentProvider
        public Cursor query(String str, Uri uri, String[] strArr, String str2, String[] strArr2, String str3, ICancellationSignal iCancellationSignal) {
            if (enforceReadPermission(str, uri) == 0) {
                String callingPackage = ContentProvider.this.setCallingPackage(str);
                try {
                    return ContentProvider.this.query(uri, strArr, str2, strArr2, str3, CancellationSignal.fromTransport(iCancellationSignal));
                } finally {
                    ContentProvider.this.setCallingPackage(callingPackage);
                }
            }
            return ContentProvider.this.rejectQuery(uri, strArr, str2, strArr2, str3, CancellationSignal.fromTransport(iCancellationSignal));
        }

        @Override // android.content.IContentProvider
        public String getType(Uri uri) {
            return ContentProvider.this.getType(uri);
        }

        @Override // android.content.IContentProvider
        public Uri insert(String str, Uri uri, ContentValues contentValues) {
            if (enforceWritePermission(str, uri) == 0) {
                String callingPackage = ContentProvider.this.setCallingPackage(str);
                try {
                    return ContentProvider.this.insert(uri, contentValues);
                } finally {
                    ContentProvider.this.setCallingPackage(callingPackage);
                }
            }
            return ContentProvider.this.rejectInsert(uri, contentValues);
        }

        @Override // android.content.IContentProvider
        public int bulkInsert(String str, Uri uri, ContentValues[] contentValuesArr) {
            if (enforceWritePermission(str, uri) != 0) {
                return 0;
            }
            String callingPackage = ContentProvider.this.setCallingPackage(str);
            try {
                return ContentProvider.this.bulkInsert(uri, contentValuesArr);
            } finally {
                ContentProvider.this.setCallingPackage(callingPackage);
            }
        }

        @Override // android.content.IContentProvider
        public ContentProviderResult[] applyBatch(String str, ArrayList<ContentProviderOperation> arrayList) throws OperationApplicationException {
            for (ContentProviderOperation contentProviderOperation : arrayList) {
                if (contentProviderOperation.isReadOperation() && enforceReadPermission(str, contentProviderOperation.getUri()) != 0) {
                    throw new OperationApplicationException("App op not allowed", 0);
                }
                if (contentProviderOperation.isWriteOperation() && enforceWritePermission(str, contentProviderOperation.getUri()) != 0) {
                    throw new OperationApplicationException("App op not allowed", 0);
                }
            }
            String callingPackage = ContentProvider.this.setCallingPackage(str);
            try {
                return ContentProvider.this.applyBatch(arrayList);
            } finally {
                ContentProvider.this.setCallingPackage(callingPackage);
            }
        }

        @Override // android.content.IContentProvider
        public int delete(String str, Uri uri, String str2, String[] strArr) {
            if (enforceWritePermission(str, uri) != 0) {
                return 0;
            }
            String callingPackage = ContentProvider.this.setCallingPackage(str);
            try {
                return ContentProvider.this.delete(uri, str2, strArr);
            } finally {
                ContentProvider.this.setCallingPackage(callingPackage);
            }
        }

        @Override // android.content.IContentProvider
        public int update(String str, Uri uri, ContentValues contentValues, String str2, String[] strArr) {
            if (enforceWritePermission(str, uri) != 0) {
                return 0;
            }
            String callingPackage = ContentProvider.this.setCallingPackage(str);
            try {
                return ContentProvider.this.update(uri, contentValues, str2, strArr);
            } finally {
                ContentProvider.this.setCallingPackage(callingPackage);
            }
        }

        @Override // android.content.IContentProvider
        public ParcelFileDescriptor openFile(String str, Uri uri, String str2, ICancellationSignal iCancellationSignal) throws FileNotFoundException {
            enforceFilePermission(str, uri, str2);
            String callingPackage = ContentProvider.this.setCallingPackage(str);
            try {
                return ContentProvider.this.openFile(uri, str2, CancellationSignal.fromTransport(iCancellationSignal));
            } finally {
                ContentProvider.this.setCallingPackage(callingPackage);
            }
        }

        @Override // android.content.IContentProvider
        public AssetFileDescriptor openAssetFile(String str, Uri uri, String str2, ICancellationSignal iCancellationSignal) throws FileNotFoundException {
            enforceFilePermission(str, uri, str2);
            String callingPackage = ContentProvider.this.setCallingPackage(str);
            try {
                return ContentProvider.this.openAssetFile(uri, str2, CancellationSignal.fromTransport(iCancellationSignal));
            } finally {
                ContentProvider.this.setCallingPackage(callingPackage);
            }
        }

        @Override // android.content.IContentProvider
        public Bundle call(String str, String str2, String str3, Bundle bundle) {
            String callingPackage = ContentProvider.this.setCallingPackage(str);
            try {
                return ContentProvider.this.call(str2, str3, bundle);
            } finally {
                ContentProvider.this.setCallingPackage(callingPackage);
            }
        }

        @Override // android.content.IContentProvider
        public String[] getStreamTypes(Uri uri, String str) {
            return ContentProvider.this.getStreamTypes(uri, str);
        }

        @Override // android.content.IContentProvider
        public AssetFileDescriptor openTypedAssetFile(String str, Uri uri, String str2, Bundle bundle, ICancellationSignal iCancellationSignal) throws FileNotFoundException {
            enforceFilePermission(str, uri, FullBackup.ROOT_TREE_TOKEN);
            String callingPackage = ContentProvider.this.setCallingPackage(str);
            try {
                return ContentProvider.this.openTypedAssetFile(uri, str2, bundle, CancellationSignal.fromTransport(iCancellationSignal));
            } finally {
                ContentProvider.this.setCallingPackage(callingPackage);
            }
        }

        @Override // android.content.IContentProvider
        public ICancellationSignal createCancellationSignal() {
            return CancellationSignal.createTransport();
        }

        @Override // android.content.IContentProvider
        public Uri canonicalize(String str, Uri uri) {
            if (enforceReadPermission(str, uri) != 0) {
                return null;
            }
            String callingPackage = ContentProvider.this.setCallingPackage(str);
            try {
                return ContentProvider.this.canonicalize(uri);
            } finally {
                ContentProvider.this.setCallingPackage(callingPackage);
            }
        }

        @Override // android.content.IContentProvider
        public Uri uncanonicalize(String str, Uri uri) {
            if (enforceReadPermission(str, uri) != 0) {
                return null;
            }
            String callingPackage = ContentProvider.this.setCallingPackage(str);
            try {
                return ContentProvider.this.uncanonicalize(uri);
            } finally {
                ContentProvider.this.setCallingPackage(callingPackage);
            }
        }

        private void enforceFilePermission(String str, Uri uri, String str2) throws SecurityException, FileNotFoundException {
            if (str2 != null && str2.indexOf(119) != -1) {
                if (enforceWritePermission(str, uri) != 0) {
                    throw new FileNotFoundException("App op not allowed");
                }
            } else if (enforceReadPermission(str, uri) != 0) {
                throw new FileNotFoundException("App op not allowed");
            }
        }

        private int enforceReadPermission(String str, Uri uri) throws SecurityException {
            ContentProvider.this.enforceReadPermissionInner(uri);
            int i = this.mReadOp;
            if (i != -1) {
                return this.mAppOpsManager.noteOp(i, Binder.getCallingUid(), str);
            }
            return 0;
        }

        private int enforceWritePermission(String str, Uri uri) throws SecurityException {
            ContentProvider.this.enforceWritePermissionInner(uri);
            int i = this.mWriteOp;
            if (i != -1) {
                return this.mAppOpsManager.noteOp(i, Binder.getCallingUid(), str);
            }
            return 0;
        }
    }

    protected void enforceReadPermissionInner(Uri uri) throws SecurityException {
        Context context = getContext();
        int callingPid = Binder.getCallingPid();
        int callingUid = Binder.getCallingUid();
        if (UserHandle.isSameApp(callingUid, this.mMyUid)) {
            return;
        }
        String str = null;
        if (this.mExported) {
            String readPermission = getReadPermission();
            if (readPermission != null) {
                if (context.checkPermission(readPermission, callingPid, callingUid) == 0) {
                    return;
                } else {
                    str = readPermission;
                }
            }
            boolean z = readPermission == null;
            PathPermission[] pathPermissions = getPathPermissions();
            if (pathPermissions != null) {
                String path = uri.getPath();
                for (PathPermission pathPermission : pathPermissions) {
                    String readPermission2 = pathPermission.getReadPermission();
                    if (readPermission2 != null && pathPermission.match(path)) {
                        if (context.checkPermission(readPermission2, callingPid, callingUid) == 0) {
                            return;
                        }
                        z = false;
                        str = readPermission2;
                    }
                }
            }
            if (z) {
                return;
            }
        }
        if (context.checkUriPermission(uri, callingPid, callingUid, 1) != 0) {
            throw new SecurityException("Permission Denial: reading " + getClass().getName() + " uri " + uri + " from pid=" + callingPid + ", uid=" + callingUid + (this.mExported ? " requires " + str + ", or grantUriPermission()" : " requires the provider be exported, or grantUriPermission()"));
        }
    }

    protected void enforceWritePermissionInner(Uri uri) throws SecurityException {
        Context context = getContext();
        int callingPid = Binder.getCallingPid();
        int callingUid = Binder.getCallingUid();
        if (UserHandle.isSameApp(callingUid, this.mMyUid)) {
            return;
        }
        String str = null;
        if (this.mExported) {
            String writePermission = getWritePermission();
            if (writePermission != null) {
                if (context.checkPermission(writePermission, callingPid, callingUid) == 0) {
                    return;
                } else {
                    str = writePermission;
                }
            }
            boolean z = writePermission == null;
            PathPermission[] pathPermissions = getPathPermissions();
            if (pathPermissions != null) {
                String path = uri.getPath();
                for (PathPermission pathPermission : pathPermissions) {
                    String writePermission2 = pathPermission.getWritePermission();
                    if (writePermission2 != null && pathPermission.match(path)) {
                        if (context.checkPermission(writePermission2, callingPid, callingUid) == 0) {
                            return;
                        }
                        z = false;
                        str = writePermission2;
                    }
                }
            }
            if (z) {
                return;
            }
        }
        if (context.checkUriPermission(uri, callingPid, callingUid, 2) != 0) {
            throw new SecurityException("Permission Denial: writing " + getClass().getName() + " uri " + uri + " from pid=" + callingPid + ", uid=" + callingUid + (this.mExported ? " requires " + str + ", or grantUriPermission()" : " requires the provider be exported, or grantUriPermission()"));
        }
    }

    public final Context getContext() {
        return this.mContext;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String setCallingPackage(String str) {
        String str2 = this.mCallingPackage.get();
        this.mCallingPackage.set(str);
        return str2;
    }

    public final String getCallingPackage() {
        String str = this.mCallingPackage.get();
        if (str != null) {
            this.mTransport.mAppOpsManager.checkPackage(Binder.getCallingUid(), str);
        }
        return str;
    }

    protected final void setReadPermission(String str) {
        this.mReadPermission = str;
    }

    public final String getReadPermission() {
        return this.mReadPermission;
    }

    protected final void setWritePermission(String str) {
        this.mWritePermission = str;
    }

    public final String getWritePermission() {
        return this.mWritePermission;
    }

    protected final void setPathPermissions(PathPermission[] pathPermissionArr) {
        this.mPathPermissions = pathPermissionArr;
    }

    public final PathPermission[] getPathPermissions() {
        return this.mPathPermissions;
    }

    public final void setAppOps(int i, int i2) {
        if (this.mNoPerms) {
            return;
        }
        this.mTransport.mReadOp = i;
        this.mTransport.mWriteOp = i2;
    }

    public AppOpsManager getAppOpsManager() {
        return this.mTransport.mAppOpsManager;
    }

    public Cursor rejectQuery(Uri uri, String[] strArr, String str, String[] strArr2, String str2, CancellationSignal cancellationSignal) {
        return query(uri, strArr, (str == null || str.isEmpty()) ? "'A' = 'B'" : "'A' = 'B' AND (" + str + ")", strArr2, str2, cancellationSignal);
    }

    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2, CancellationSignal cancellationSignal) {
        return query(uri, strArr, str, strArr2, str2);
    }

    public Uri rejectInsert(Uri uri, ContentValues contentValues) {
        return uri.buildUpon().appendPath("0").build();
    }

    public int bulkInsert(Uri uri, ContentValues[] contentValuesArr) {
        int length = contentValuesArr.length;
        for (ContentValues contentValues : contentValuesArr) {
            insert(uri, contentValues);
        }
        return length;
    }

    public ParcelFileDescriptor openFile(Uri uri, String str) throws FileNotFoundException {
        throw new FileNotFoundException("No files supported by provider at " + uri);
    }

    public ParcelFileDescriptor openFile(Uri uri, String str, CancellationSignal cancellationSignal) throws FileNotFoundException {
        return openFile(uri, str);
    }

    public AssetFileDescriptor openAssetFile(Uri uri, String str) throws FileNotFoundException {
        ParcelFileDescriptor parcelFileDescriptorOpenFile = openFile(uri, str);
        if (parcelFileDescriptorOpenFile != null) {
            return new AssetFileDescriptor(parcelFileDescriptorOpenFile, 0L, -1L);
        }
        return null;
    }

    public AssetFileDescriptor openAssetFile(Uri uri, String str, CancellationSignal cancellationSignal) throws FileNotFoundException {
        return openAssetFile(uri, str);
    }

    protected final ParcelFileDescriptor openFileHelper(Uri uri, String str) throws FileNotFoundException {
        Cursor cursorQuery = query(uri, new String[]{"_data"}, null, null, null);
        int count = cursorQuery != null ? cursorQuery.getCount() : 0;
        if (count != 1) {
            if (cursorQuery != null) {
                cursorQuery.close();
            }
            if (count == 0) {
                throw new FileNotFoundException("No entry for " + uri);
            }
            throw new FileNotFoundException("Multiple items at " + uri);
        }
        cursorQuery.moveToFirst();
        int columnIndex = cursorQuery.getColumnIndex("_data");
        String string = columnIndex >= 0 ? cursorQuery.getString(columnIndex) : null;
        cursorQuery.close();
        if (string == null) {
            throw new FileNotFoundException("Column _data not found.");
        }
        return ParcelFileDescriptor.open(new File(string), ParcelFileDescriptor.parseMode(str));
    }

    public AssetFileDescriptor openTypedAssetFile(Uri uri, String str, Bundle bundle) throws FileNotFoundException {
        if ("*/*".equals(str)) {
            return openAssetFile(uri, FullBackup.ROOT_TREE_TOKEN);
        }
        String type = getType(uri);
        if (type != null && ClipDescription.compareMimeTypes(type, str)) {
            return openAssetFile(uri, FullBackup.ROOT_TREE_TOKEN);
        }
        throw new FileNotFoundException("Can't open " + uri + " as type " + str);
    }

    public AssetFileDescriptor openTypedAssetFile(Uri uri, String str, Bundle bundle, CancellationSignal cancellationSignal) throws FileNotFoundException {
        return openTypedAssetFile(uri, str, bundle);
    }

    public <T> ParcelFileDescriptor openPipeHelper(final Uri uri, final String str, final Bundle bundle, final T t, final PipeDataWriter<T> pipeDataWriter) throws FileNotFoundException {
        try {
            final ParcelFileDescriptor[] parcelFileDescriptorArrCreatePipe = ParcelFileDescriptor.createPipe();
            new AsyncTask<Object, Object, Object>() { // from class: android.content.ContentProvider.1
                @Override // android.os.AsyncTask
                protected Object doInBackground(Object... objArr) {
                    pipeDataWriter.writeDataToPipe(parcelFileDescriptorArrCreatePipe[1], uri, str, bundle, t);
                    try {
                        parcelFileDescriptorArrCreatePipe[1].close();
                        return null;
                    } catch (IOException e) {
                        Log.w(ContentProvider.TAG, "Failure closing pipe", e);
                        return null;
                    }
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Object[]) null);
            return parcelFileDescriptorArrCreatePipe[0];
        } catch (IOException unused) {
            throw new FileNotFoundException("failure making pipe");
        }
    }

    public IContentProvider getIContentProvider() {
        return this.mTransport;
    }

    public void attachInfoForTesting(Context context, ProviderInfo providerInfo) {
        attachInfo(context, providerInfo, true);
    }

    public void attachInfo(Context context, ProviderInfo providerInfo) {
        attachInfo(context, providerInfo, false);
    }

    private void attachInfo(Context context, ProviderInfo providerInfo, boolean z) {
        AsyncTask.init();
        this.mNoPerms = z;
        if (this.mContext == null) {
            this.mContext = context;
            if (context != null) {
                this.mTransport.mAppOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            }
            this.mMyUid = Process.myUid();
            if (providerInfo != null) {
                setReadPermission(providerInfo.readPermission);
                setWritePermission(providerInfo.writePermission);
                setPathPermissions(providerInfo.pathPermissions);
                this.mExported = providerInfo.exported;
            }
            onCreate();
        }
    }

    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> arrayList) throws OperationApplicationException {
        int size = arrayList.size();
        ContentProviderResult[] contentProviderResultArr = new ContentProviderResult[size];
        for (int i = 0; i < size; i++) {
            contentProviderResultArr[i] = arrayList.get(i).apply(this, contentProviderResultArr, i);
        }
        return contentProviderResultArr;
    }

    public void shutdown() {
        Log.w(TAG, "implement ContentProvider shutdown() to make sure all database connections are gracefully shutdown");
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("nothing to dump");
    }
}
