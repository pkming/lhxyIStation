package com.bumptech.glide.load.resource.drawable;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class ResourceDrawableDecoder implements ResourceDecoder<Uri, Drawable> {
    private static final int ID_PATH_SEGMENTS = 1;
    private static final int NAME_PATH_SEGMENT_INDEX = 1;
    private static final int NAME_URI_PATH_SEGMENTS = 2;
    private static final int RESOURCE_ID_SEGMENT_INDEX = 0;
    private static final int TYPE_PATH_SEGMENT_INDEX = 0;
    private final Context context;

    public ResourceDrawableDecoder(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override // com.bumptech.glide.load.ResourceDecoder
    public boolean handles(Uri uri, Options options) {
        return uri.getScheme().equals(ContentResolver.SCHEME_ANDROID_RESOURCE);
    }

    @Override // com.bumptech.glide.load.ResourceDecoder
    public Resource<Drawable> decode(Uri uri, int i, int i2, Options options) {
        int iLoadResourceIdFromUri = loadResourceIdFromUri(uri);
        String authority = uri.getAuthority();
        return NonOwnedDrawableResource.newInstance(DrawableDecoderCompat.getDrawable(authority.equals(this.context.getPackageName()) ? this.context : getContextForPackage(uri, authority), iLoadResourceIdFromUri));
    }

    private Context getContextForPackage(Uri uri, String str) {
        try {
            return this.context.createPackageContext(str, 0);
        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalArgumentException("Failed to obtain context or unrecognized Uri format for: " + uri, e);
        }
    }

    private int loadResourceIdFromUri(Uri uri) {
        Integer numValueOf;
        List<String> pathSegments = uri.getPathSegments();
        if (pathSegments.size() == 2) {
            String authority = uri.getAuthority();
            numValueOf = Integer.valueOf(this.context.getResources().getIdentifier(pathSegments.get(1), pathSegments.get(0), authority));
        } else if (pathSegments.size() == 1) {
            try {
                numValueOf = Integer.valueOf(pathSegments.get(0));
            } catch (NumberFormatException unused) {
                numValueOf = null;
            }
        } else {
            numValueOf = null;
        }
        if (numValueOf == null) {
            throw new IllegalArgumentException("Unrecognized Uri format: " + uri);
        }
        if (numValueOf.intValue() == 0) {
            throw new IllegalArgumentException("Failed to obtain resource id for: " + uri);
        }
        return numValueOf.intValue();
    }
}
