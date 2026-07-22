package android.provider;

import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.OperationCanceledException;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import libcore.io.ErrnoException;
import libcore.io.IoUtils;
import libcore.io.Libcore;
import libcore.io.OsConstants;

/* JADX INFO: loaded from: classes.dex */
public final class DocumentsContract {
    public static final String ACTION_MANAGE_DOCUMENT = "android.provider.action.MANAGE_DOCUMENT";
    public static final String ACTION_MANAGE_ROOT = "android.provider.action.MANAGE_ROOT";
    public static final String EXTRA_ERROR = "error";
    public static final String EXTRA_INFO = "info";
    public static final String EXTRA_LOADING = "loading";
    public static final String EXTRA_ORIENTATION = "android.content.extra.ORIENTATION";
    public static final String EXTRA_PACKAGE_NAME = "android.content.extra.PACKAGE_NAME";
    public static final String EXTRA_SHOW_ADVANCED = "android.content.extra.SHOW_ADVANCED";
    public static final String EXTRA_THUMBNAIL_SIZE = "thumbnail_size";
    public static final String METHOD_CREATE_DOCUMENT = "android:createDocument";
    public static final String METHOD_DELETE_DOCUMENT = "android:deleteDocument";
    private static final String PARAM_MANAGE = "manage";
    private static final String PARAM_QUERY = "query";
    private static final String PATH_CHILDREN = "children";
    private static final String PATH_DOCUMENT = "document";
    private static final String PATH_RECENT = "recent";
    private static final String PATH_ROOT = "root";
    private static final String PATH_SEARCH = "search";
    public static final String PROVIDER_INTERFACE = "android.content.action.DOCUMENTS_PROVIDER";
    private static final String TAG = "Documents";
    private static final int THUMBNAIL_BUFFER_SIZE = 131072;

    private DocumentsContract() {
    }

    public static final class Document {
        public static final String COLUMN_DISPLAY_NAME = "_display_name";
        public static final String COLUMN_DOCUMENT_ID = "document_id";
        public static final String COLUMN_FLAGS = "flags";
        public static final String COLUMN_ICON = "icon";
        public static final String COLUMN_LAST_MODIFIED = "last_modified";
        public static final String COLUMN_MIME_TYPE = "mime_type";
        public static final String COLUMN_SIZE = "_size";
        public static final String COLUMN_SUMMARY = "summary";
        public static final int FLAG_DIR_HIDE_GRID_TITLES = 65536;
        public static final int FLAG_DIR_PREFERS_GRID = 16;
        public static final int FLAG_DIR_PREFERS_LAST_MODIFIED = 32;
        public static final int FLAG_DIR_SUPPORTS_CREATE = 8;
        public static final int FLAG_SUPPORTS_DELETE = 4;
        public static final int FLAG_SUPPORTS_THUMBNAIL = 1;
        public static final int FLAG_SUPPORTS_WRITE = 2;
        public static final String MIME_TYPE_DIR = "vnd.android.document/directory";

        private Document() {
        }
    }

    public static final class Root {
        public static final String COLUMN_AVAILABLE_BYTES = "available_bytes";
        public static final String COLUMN_DOCUMENT_ID = "document_id";
        public static final String COLUMN_FLAGS = "flags";
        public static final String COLUMN_ICON = "icon";
        public static final String COLUMN_MIME_TYPES = "mime_types";
        public static final String COLUMN_ROOT_ID = "root_id";
        public static final String COLUMN_SUMMARY = "summary";
        public static final String COLUMN_TITLE = "title";
        public static final int FLAG_ADVANCED = 131072;
        public static final int FLAG_EMPTY = 65536;
        public static final int FLAG_LOCAL_ONLY = 2;
        public static final int FLAG_SUPPORTS_CREATE = 1;
        public static final int FLAG_SUPPORTS_RECENTS = 4;
        public static final int FLAG_SUPPORTS_SEARCH = 8;
        public static final String MIME_TYPE_ITEM = "vnd.android.document/root";

        private Root() {
        }
    }

    public static Uri buildRootsUri(String str) {
        return new Uri.Builder().scheme("content").authority(str).appendPath(PATH_ROOT).build();
    }

    public static Uri buildRootUri(String str, String str2) {
        return new Uri.Builder().scheme("content").authority(str).appendPath(PATH_ROOT).appendPath(str2).build();
    }

    public static Uri buildRecentDocumentsUri(String str, String str2) {
        return new Uri.Builder().scheme("content").authority(str).appendPath(PATH_ROOT).appendPath(str2).appendPath(PATH_RECENT).build();
    }

    public static Uri buildDocumentUri(String str, String str2) {
        return new Uri.Builder().scheme("content").authority(str).appendPath(PATH_DOCUMENT).appendPath(str2).build();
    }

    public static Uri buildChildDocumentsUri(String str, String str2) {
        return new Uri.Builder().scheme("content").authority(str).appendPath(PATH_DOCUMENT).appendPath(str2).appendPath(PATH_CHILDREN).build();
    }

    public static Uri buildSearchDocumentsUri(String str, String str2, String str3) {
        return new Uri.Builder().scheme("content").authority(str).appendPath(PATH_ROOT).appendPath(str2).appendPath("search").appendQueryParameter("query", str3).build();
    }

    public static boolean isDocumentUri(Context context, Uri uri) {
        List<String> pathSegments = uri.getPathSegments();
        if (pathSegments.size() < 2 || !PATH_DOCUMENT.equals(pathSegments.get(0))) {
            return false;
        }
        Iterator<ResolveInfo> it = context.getPackageManager().queryIntentContentProviders(new Intent(PROVIDER_INTERFACE), 0).iterator();
        while (it.hasNext()) {
            if (uri.getAuthority().equals(it.next().providerInfo.authority)) {
                return true;
            }
        }
        return false;
    }

    public static String getRootId(Uri uri) {
        List<String> pathSegments = uri.getPathSegments();
        if (pathSegments.size() < 2) {
            throw new IllegalArgumentException("Not a root: " + uri);
        }
        if (!PATH_ROOT.equals(pathSegments.get(0))) {
            throw new IllegalArgumentException("Not a root: " + uri);
        }
        return pathSegments.get(1);
    }

    public static String getDocumentId(Uri uri) {
        List<String> pathSegments = uri.getPathSegments();
        if (pathSegments.size() < 2) {
            throw new IllegalArgumentException("Not a document: " + uri);
        }
        if (!PATH_DOCUMENT.equals(pathSegments.get(0))) {
            throw new IllegalArgumentException("Not a document: " + uri);
        }
        return pathSegments.get(1);
    }

    public static String getSearchDocumentsQuery(Uri uri) {
        return uri.getQueryParameter("query");
    }

    public static Uri setManageMode(Uri uri) {
        return uri.buildUpon().appendQueryParameter(PARAM_MANAGE, "true").build();
    }

    public static boolean isManageMode(Uri uri) {
        return uri.getBooleanQueryParameter(PARAM_MANAGE, false);
    }

    public static Bitmap getDocumentThumbnail(ContentResolver contentResolver, Uri uri, Point point, CancellationSignal cancellationSignal) {
        ContentProviderClient contentProviderClientAcquireUnstableContentProviderClient = contentResolver.acquireUnstableContentProviderClient(uri.getAuthority());
        try {
            return getDocumentThumbnail(contentProviderClientAcquireUnstableContentProviderClient, uri, point, cancellationSignal);
        } catch (Exception e) {
            if (!(e instanceof OperationCanceledException)) {
                Log.w(TAG, "Failed to load thumbnail for " + uri + ": " + e);
            }
            return null;
        } finally {
            ContentProviderClient.releaseQuietly(contentProviderClientAcquireUnstableContentProviderClient);
        }
    }

    public static Bitmap getDocumentThumbnail(ContentProviderClient contentProviderClient, Uri uri, Point point, CancellationSignal cancellationSignal) throws Throwable {
        BufferedInputStream bufferedInputStream;
        Bitmap bitmapDecodeFileDescriptor;
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_THUMBNAIL_SIZE, point);
        AssetFileDescriptor assetFileDescriptor = null;
        try {
            AssetFileDescriptor assetFileDescriptorOpenTypedAssetFileDescriptor = contentProviderClient.openTypedAssetFileDescriptor(uri, "image/*", bundle, cancellationSignal);
            try {
                FileDescriptor fileDescriptor = assetFileDescriptorOpenTypedAssetFileDescriptor.getFileDescriptor();
                long startOffset = assetFileDescriptorOpenTypedAssetFileDescriptor.getStartOffset();
                try {
                    Libcore.os.lseek(fileDescriptor, startOffset, OsConstants.SEEK_SET);
                    bufferedInputStream = null;
                } catch (ErrnoException unused) {
                    bufferedInputStream = new BufferedInputStream(new FileInputStream(fileDescriptor), 131072);
                    bufferedInputStream.mark(131072);
                }
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                if (bufferedInputStream != null) {
                    BitmapFactory.decodeStream(bufferedInputStream, null, options);
                } else {
                    BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
                }
                int i = options.outWidth / point.x;
                int i2 = options.outHeight / point.y;
                options.inJustDecodeBounds = false;
                options.inSampleSize = Math.min(i, i2);
                if (bufferedInputStream != null) {
                    bufferedInputStream.reset();
                    bitmapDecodeFileDescriptor = BitmapFactory.decodeStream(bufferedInputStream, null, options);
                } else {
                    try {
                        Libcore.os.lseek(fileDescriptor, startOffset, OsConstants.SEEK_SET);
                    } catch (ErrnoException e) {
                        e.rethrowAsIOException();
                    }
                    bitmapDecodeFileDescriptor = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
                }
                Bitmap bitmapCreateBitmap = bitmapDecodeFileDescriptor;
                Bundle extras = assetFileDescriptorOpenTypedAssetFileDescriptor.getExtras();
                int i3 = extras != null ? extras.getInt(EXTRA_ORIENTATION, 0) : 0;
                if (i3 != 0) {
                    int width = bitmapCreateBitmap.getWidth();
                    int height = bitmapCreateBitmap.getHeight();
                    Matrix matrix = new Matrix();
                    matrix.setRotate(i3, width / 2, height / 2);
                    bitmapCreateBitmap = Bitmap.createBitmap(bitmapCreateBitmap, 0, 0, width, height, matrix, false);
                }
                IoUtils.closeQuietly(assetFileDescriptorOpenTypedAssetFileDescriptor);
                return bitmapCreateBitmap;
            } catch (Throwable th) {
                th = th;
                assetFileDescriptor = assetFileDescriptorOpenTypedAssetFileDescriptor;
                IoUtils.closeQuietly(assetFileDescriptor);
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public static Uri createDocument(ContentResolver contentResolver, Uri uri, String str, String str2) {
        ContentProviderClient contentProviderClientAcquireUnstableContentProviderClient = contentResolver.acquireUnstableContentProviderClient(uri.getAuthority());
        try {
            return createDocument(contentProviderClientAcquireUnstableContentProviderClient, uri, str, str2);
        } catch (Exception e) {
            Log.w(TAG, "Failed to create document", e);
            return null;
        } finally {
            ContentProviderClient.releaseQuietly(contentProviderClientAcquireUnstableContentProviderClient);
        }
    }

    public static Uri createDocument(ContentProviderClient contentProviderClient, Uri uri, String str, String str2) throws RemoteException {
        Bundle bundle = new Bundle();
        bundle.putString("document_id", getDocumentId(uri));
        bundle.putString("mime_type", str);
        bundle.putString("_display_name", str2);
        return buildDocumentUri(uri.getAuthority(), contentProviderClient.call(METHOD_CREATE_DOCUMENT, null, bundle).getString("document_id"));
    }

    public static boolean deleteDocument(ContentResolver contentResolver, Uri uri) {
        boolean z;
        ContentProviderClient contentProviderClientAcquireUnstableContentProviderClient = contentResolver.acquireUnstableContentProviderClient(uri.getAuthority());
        try {
            try {
                deleteDocument(contentProviderClientAcquireUnstableContentProviderClient, uri);
                z = true;
            } catch (Exception e) {
                Log.w(TAG, "Failed to delete document", e);
                z = false;
            }
            return z;
        } finally {
            ContentProviderClient.releaseQuietly(contentProviderClientAcquireUnstableContentProviderClient);
        }
    }

    public static void deleteDocument(ContentProviderClient contentProviderClient, Uri uri) throws RemoteException {
        Bundle bundle = new Bundle();
        bundle.putString("document_id", getDocumentId(uri));
        contentProviderClient.call(METHOD_DELETE_DOCUMENT, null, bundle);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v0 */
    /* JADX WARN: Type inference failed for: r1v1 */
    /* JADX WARN: Type inference failed for: r1v3 */
    /* JADX WARN: Type inference failed for: r7v0 */
    /* JADX WARN: Type inference failed for: r7v1, types: [android.os.Bundle] */
    /* JADX WARN: Type inference failed for: r7v2 */
    /* JADX WARN: Type inference failed for: r7v3, types: [android.os.Bundle] */
    /* JADX WARN: Type inference failed for: r9v10 */
    /* JADX WARN: Type inference failed for: r9v11 */
    /* JADX WARN: Type inference failed for: r9v12 */
    /* JADX WARN: Type inference failed for: r9v13 */
    /* JADX WARN: Type inference failed for: r9v4, types: [int] */
    /* JADX WARN: Type inference failed for: r9v5 */
    /* JADX WARN: Type inference failed for: r9v7 */
    public static AssetFileDescriptor openImageThumbnail(File file) throws FileNotFoundException {
        ?? r7;
        ExifInterface exifInterface;
        ?? attributeInt;
        ParcelFileDescriptor parcelFileDescriptorOpen = ParcelFileDescriptor.open(file, 268435456);
        ?? r1 = 0;
        try {
            exifInterface = new ExifInterface(file.getAbsolutePath());
            attributeInt = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
        } catch (IOException unused) {
        }
        try {
            if (attributeInt == 3) {
                Bundle bundle = new Bundle(1);
                bundle.putInt(EXTRA_ORIENTATION, 180);
                attributeInt = bundle;
            } else if (attributeInt == 6) {
                Bundle bundle2 = new Bundle(1);
                bundle2.putInt(EXTRA_ORIENTATION, 90);
                attributeInt = bundle2;
            } else if (attributeInt != 8) {
                attributeInt = 0;
            } else {
                Bundle bundle3 = new Bundle(1);
                bundle3.putInt(EXTRA_ORIENTATION, 270);
                attributeInt = bundle3;
            }
            long[] thumbnailRange = exifInterface.getThumbnailRange();
            if (thumbnailRange != null) {
                return new AssetFileDescriptor(parcelFileDescriptorOpen, thumbnailRange[0], thumbnailRange[1], attributeInt);
            }
            r7 = attributeInt;
        } catch (IOException unused2) {
            r1 = attributeInt;
            r7 = r1;
        }
        return new AssetFileDescriptor(parcelFileDescriptorOpen, 0L, -1L, r7);
    }
}
