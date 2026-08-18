package android.app;

import android.app.IWallpaperManager;
import android.app.IWallpaperManagerCallback;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.view.WindowManagerGlobal;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: classes.dex */
public class WallpaperManager {
    public static final String ACTION_CHANGE_LIVE_WALLPAPER = "android.service.wallpaper.CHANGE_LIVE_WALLPAPER";
    public static final String ACTION_CROP_AND_SET_WALLPAPER = "android.service.wallpaper.CROP_AND_SET_WALLPAPER";
    public static final String ACTION_LIVE_WALLPAPER_CHOOSER = "android.service.wallpaper.LIVE_WALLPAPER_CHOOSER";
    public static final String COMMAND_DROP = "android.home.drop";
    public static final String COMMAND_SECONDARY_TAP = "android.wallpaper.secondaryTap";
    public static final String COMMAND_TAP = "android.wallpaper.tap";
    private static boolean DEBUG = false;
    public static final String EXTRA_LIVE_WALLPAPER_COMPONENT = "android.service.wallpaper.extra.LIVE_WALLPAPER_COMPONENT";
    private static String TAG = "WallpaperManager";
    public static final String WALLPAPER_PREVIEW_META_DATA = "android.wallpaper.preview";
    private static Globals sGlobals;
    private static final Object sSync = new Object[0];
    private final Context mContext;
    private float mWallpaperXStep = -1.0f;
    private float mWallpaperYStep = -1.0f;

    static class FastBitmapDrawable extends Drawable {
        private final Bitmap mBitmap;
        private int mDrawLeft;
        private int mDrawTop;
        private final int mHeight;
        private final Paint mPaint;
        private final int mWidth;

        @Override // android.graphics.drawable.Drawable
        public int getOpacity() {
            return -1;
        }

        private FastBitmapDrawable(Bitmap bitmap) {
            this.mBitmap = bitmap;
            int width = bitmap.getWidth();
            this.mWidth = width;
            int height = bitmap.getHeight();
            this.mHeight = height;
            setBounds(0, 0, width, height);
            Paint paint = new Paint();
            this.mPaint = paint;
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        }

        @Override // android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            canvas.drawBitmap(this.mBitmap, this.mDrawLeft, this.mDrawTop, this.mPaint);
        }

        @Override // android.graphics.drawable.Drawable
        public void setBounds(int i, int i2, int i3, int i4) {
            this.mDrawLeft = i + (((i3 - i) - this.mWidth) / 2);
            this.mDrawTop = i2 + (((i4 - i2) - this.mHeight) / 2);
        }

        @Override // android.graphics.drawable.Drawable
        public void setAlpha(int i) {
            throw new UnsupportedOperationException("Not supported with this drawable");
        }

        @Override // android.graphics.drawable.Drawable
        public void setColorFilter(ColorFilter colorFilter) {
            throw new UnsupportedOperationException("Not supported with this drawable");
        }

        @Override // android.graphics.drawable.Drawable
        public void setDither(boolean z) {
            throw new UnsupportedOperationException("Not supported with this drawable");
        }

        @Override // android.graphics.drawable.Drawable
        public void setFilterBitmap(boolean z) {
            throw new UnsupportedOperationException("Not supported with this drawable");
        }

        @Override // android.graphics.drawable.Drawable
        public int getIntrinsicWidth() {
            return this.mWidth;
        }

        @Override // android.graphics.drawable.Drawable
        public int getIntrinsicHeight() {
            return this.mHeight;
        }

        @Override // android.graphics.drawable.Drawable
        public int getMinimumWidth() {
            return this.mWidth;
        }

        @Override // android.graphics.drawable.Drawable
        public int getMinimumHeight() {
            return this.mHeight;
        }
    }

    static class Globals extends IWallpaperManagerCallback.Stub {
        private static final int MSG_CLEAR_WALLPAPER = 1;
        private Bitmap mDefaultWallpaper;
        private final Handler mHandler;
        private IWallpaperManager mService = IWallpaperManager.Stub.asInterface(ServiceManager.getService(Context.WALLPAPER_SERVICE));
        private Bitmap mWallpaper;

        Globals(Looper looper) {
            this.mHandler = new Handler(looper) { // from class: android.app.WallpaperManager.Globals.1
                @Override // android.os.Handler
                public void handleMessage(Message message) {
                    if (message.what != 1) {
                        return;
                    }
                    synchronized (this) {
                        Globals.this.mWallpaper = null;
                        Globals.this.mDefaultWallpaper = null;
                    }
                }
            };
        }

        @Override // android.app.IWallpaperManagerCallback
        public void onWallpaperChanged() {
            this.mHandler.sendEmptyMessage(1);
        }

        public Bitmap peekWallpaperBitmap(Context context, boolean z) {
            synchronized (this) {
                Bitmap bitmap = this.mWallpaper;
                if (bitmap != null) {
                    return bitmap;
                }
                Bitmap bitmap2 = this.mDefaultWallpaper;
                if (bitmap2 != null) {
                    return bitmap2;
                }
                this.mWallpaper = null;
                try {
                    this.mWallpaper = getCurrentWallpaperLocked(context);
                } catch (OutOfMemoryError e) {
                    Log.w(WallpaperManager.TAG, "No memory load current wallpaper", e);
                }
                if (z) {
                    if (this.mWallpaper == null) {
                        Bitmap defaultWallpaperLocked = getDefaultWallpaperLocked(context);
                        this.mDefaultWallpaper = defaultWallpaperLocked;
                        return defaultWallpaperLocked;
                    }
                    this.mDefaultWallpaper = null;
                }
                return this.mWallpaper;
            }
        }

        public void forgetLoadedWallpaper() {
            synchronized (this) {
                this.mWallpaper = null;
                this.mDefaultWallpaper = null;
                this.mHandler.removeMessages(1);
            }
        }

        private Bitmap getCurrentWallpaperLocked(Context context) {
            try {
                Bundle bundle = new Bundle();
                ParcelFileDescriptor wallpaper = this.mService.getWallpaper(this, bundle);
                if (wallpaper != null) {
                    try {
                        try {
                            return WallpaperManager.generateBitmap(context, BitmapFactory.decodeFileDescriptor(wallpaper.getFileDescriptor(), null, new BitmapFactory.Options()), bundle.getInt("width", 0), bundle.getInt("height", 0));
                        } finally {
                            try {
                                wallpaper.close();
                            } catch (IOException unused) {
                            }
                        }
                    } catch (OutOfMemoryError e) {
                        Log.w(WallpaperManager.TAG, "Can't decode file", e);
                        wallpaper.close();
                    }
                }
            } catch (RemoteException | IOException unused2) {
            }
            return null;
        }

        private Bitmap getDefaultWallpaperLocked(Context context) {
            try {
                InputStream inputStreamOpenRawResource = context.getResources().openRawResource(17302048);
                if (inputStreamOpenRawResource != null) {
                    try {
                        try {
                            return WallpaperManager.generateBitmap(context, BitmapFactory.decodeStream(inputStreamOpenRawResource, null, new BitmapFactory.Options()), this.mService.getWidthHint(), this.mService.getHeightHint());
                        } catch (OutOfMemoryError e) {
                            Log.w(WallpaperManager.TAG, "Can't decode stream", e);
                            inputStreamOpenRawResource.close();
                            return null;
                        }
                    } finally {
                        try {
                            inputStreamOpenRawResource.close();
                        } catch (IOException unused) {
                        }
                    }
                }
            } catch (RemoteException | IOException unused2) {
            }
            return null;
        }
    }

    static void initGlobals(Looper looper) {
        synchronized (sSync) {
            if (sGlobals == null) {
                sGlobals = new Globals(looper);
            }
        }
    }

    WallpaperManager(Context context, Handler handler) {
        this.mContext = context;
        initGlobals(context.getMainLooper());
    }

    public static WallpaperManager getInstance(Context context) {
        return (WallpaperManager) context.getSystemService(Context.WALLPAPER_SERVICE);
    }

    public IWallpaperManager getIWallpaperManager() {
        return sGlobals.mService;
    }

    public Drawable getDrawable() {
        Bitmap bitmapPeekWallpaperBitmap = sGlobals.peekWallpaperBitmap(this.mContext, true);
        if (bitmapPeekWallpaperBitmap == null) {
            return null;
        }
        BitmapDrawable bitmapDrawable = new BitmapDrawable(this.mContext.getResources(), bitmapPeekWallpaperBitmap);
        bitmapDrawable.setDither(false);
        return bitmapDrawable;
    }

    public Drawable getBuiltInDrawable() {
        return getBuiltInDrawable(0, 0, false, 0.0f, 0.0f);
    }

    public Drawable getBuiltInDrawable(int i, int i2, boolean z, float f, float f2) {
        RectF rectF;
        BitmapRegionDecoder bitmapRegionDecoderNewInstance;
        Bitmap bitmapCreateBitmap;
        if (sGlobals.mService == null) {
            Log.w(TAG, "WallpaperService not running");
            return null;
        }
        Resources resources = this.mContext.getResources();
        float fMax = Math.max(0.0f, Math.min(1.0f, f));
        float fMax2 = Math.max(0.0f, Math.min(1.0f, f2));
        BufferedInputStream bufferedInputStream = new BufferedInputStream(resources.openRawResource(17302048));
        if (i <= 0 || i2 <= 0) {
            return new BitmapDrawable(resources, BitmapFactory.decodeStream(bufferedInputStream, null, null));
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(bufferedInputStream, null, options);
        if (options.outWidth != 0 && options.outHeight != 0) {
            int i3 = options.outWidth;
            int i4 = options.outHeight;
            BufferedInputStream bufferedInputStream2 = new BufferedInputStream(resources.openRawResource(17302048));
            int iMin = Math.min(i3, i);
            int iMin2 = Math.min(i4, i2);
            if (z) {
                rectF = getMaxCropRect(i3, i4, iMin, iMin2, fMax, fMax2);
            } else {
                float f3 = (i3 - iMin) * fMax;
                float f4 = (i4 - iMin2) * fMax2;
                rectF = new RectF(f3, f4, iMin + f3, iMin2 + f4);
            }
            Rect rect = new Rect();
            rectF.roundOut(rect);
            if (rect.width() <= 0 || rect.height() <= 0) {
                Log.w(TAG, "crop has bad values for full size image");
                return null;
            }
            int iMin3 = Math.min(rect.width() / iMin, rect.height() / iMin2);
            try {
                bitmapRegionDecoderNewInstance = BitmapRegionDecoder.newInstance((InputStream) bufferedInputStream2, true);
            } catch (IOException unused) {
                Log.w(TAG, "cannot open region decoder for default wallpaper");
                bitmapRegionDecoderNewInstance = null;
            }
            if (bitmapRegionDecoderNewInstance != null) {
                BitmapFactory.Options options2 = new BitmapFactory.Options();
                if (iMin3 > 1) {
                    options2.inSampleSize = iMin3;
                }
                bitmapCreateBitmap = bitmapRegionDecoderNewInstance.decodeRegion(rect, options2);
                bitmapRegionDecoderNewInstance.recycle();
            } else {
                bitmapCreateBitmap = null;
            }
            if (bitmapCreateBitmap == null) {
                BufferedInputStream bufferedInputStream3 = new BufferedInputStream(resources.openRawResource(17302048));
                BitmapFactory.Options options3 = new BitmapFactory.Options();
                if (iMin3 > 1) {
                    options3.inSampleSize = iMin3;
                }
                Bitmap bitmapDecodeStream = BitmapFactory.decodeStream(bufferedInputStream3, null, options3);
                if (bitmapDecodeStream != null) {
                    bitmapCreateBitmap = Bitmap.createBitmap(bitmapDecodeStream, rect.left, rect.top, rect.width(), rect.height());
                }
            }
            if (bitmapCreateBitmap == null) {
                Log.w(TAG, "cannot decode default wallpaper");
                return null;
            }
            if (iMin > 0 && iMin2 > 0 && (bitmapCreateBitmap.getWidth() != iMin || bitmapCreateBitmap.getHeight() != iMin2)) {
                Matrix matrix = new Matrix();
                RectF rectF2 = new RectF(0.0f, 0.0f, bitmapCreateBitmap.getWidth(), bitmapCreateBitmap.getHeight());
                RectF rectF3 = new RectF(0.0f, 0.0f, iMin, iMin2);
                matrix.setRectToRect(rectF2, rectF3, Matrix.ScaleToFit.FILL);
                Bitmap bitmapCreateBitmap2 = Bitmap.createBitmap((int) rectF3.width(), (int) rectF3.height(), Bitmap.Config.ARGB_8888);
                if (bitmapCreateBitmap2 != null) {
                    Canvas canvas = new Canvas(bitmapCreateBitmap2);
                    Paint paint = new Paint();
                    paint.setFilterBitmap(true);
                    canvas.drawBitmap(bitmapCreateBitmap, matrix, paint);
                    bitmapCreateBitmap = bitmapCreateBitmap2;
                }
            }
            return new BitmapDrawable(resources, bitmapCreateBitmap);
        }
        Log.e(TAG, "default wallpaper dimensions are 0");
        return null;
    }

    private static RectF getMaxCropRect(int i, int i2, int i3, int i4, float f, float f2) {
        RectF rectF = new RectF();
        float f3 = i;
        float f4 = i2;
        float f5 = i3;
        float f6 = i4;
        if (f3 / f4 > f5 / f6) {
            rectF.top = 0.0f;
            rectF.bottom = f4;
            float f7 = f5 * (f4 / f6);
            rectF.left = (f3 - f7) * f;
            rectF.right = rectF.left + f7;
        } else {
            rectF.left = 0.0f;
            rectF.right = f3;
            float f8 = f6 * (f3 / f5);
            rectF.top = (f4 - f8) * f2;
            rectF.bottom = rectF.top + f8;
        }
        return rectF;
    }

    public Drawable peekDrawable() {
        Bitmap bitmapPeekWallpaperBitmap = sGlobals.peekWallpaperBitmap(this.mContext, false);
        if (bitmapPeekWallpaperBitmap == null) {
            return null;
        }
        BitmapDrawable bitmapDrawable = new BitmapDrawable(this.mContext.getResources(), bitmapPeekWallpaperBitmap);
        bitmapDrawable.setDither(false);
        return bitmapDrawable;
    }

    public Drawable getFastDrawable() {
        Bitmap bitmapPeekWallpaperBitmap = sGlobals.peekWallpaperBitmap(this.mContext, true);
        if (bitmapPeekWallpaperBitmap != null) {
            return new FastBitmapDrawable(bitmapPeekWallpaperBitmap);
        }
        return null;
    }

    public Drawable peekFastDrawable() {
        Bitmap bitmapPeekWallpaperBitmap = sGlobals.peekWallpaperBitmap(this.mContext, false);
        if (bitmapPeekWallpaperBitmap != null) {
            return new FastBitmapDrawable(bitmapPeekWallpaperBitmap);
        }
        return null;
    }

    public Bitmap getBitmap() {
        return sGlobals.peekWallpaperBitmap(this.mContext, true);
    }

    public void forgetLoadedWallpaper() {
        sGlobals.forgetLoadedWallpaper();
    }

    public WallpaperInfo getWallpaperInfo() {
        try {
            if (sGlobals.mService != null) {
                return sGlobals.mService.getWallpaperInfo();
            }
            Log.w(TAG, "WallpaperService not running");
            return null;
        } catch (RemoteException unused) {
            return null;
        }
    }

    public Intent getCropAndSetWallpaperIntent(Uri uri) {
        if (!"content".equals(uri.getScheme())) {
            throw new IllegalArgumentException("Image URI must be of the content scheme type");
        }
        PackageManager packageManager = this.mContext.getPackageManager();
        Intent intent = new Intent(ACTION_CROP_AND_SET_WALLPAPER, uri);
        intent.addFlags(1);
        ResolveInfo resolveInfoResolveActivity = packageManager.resolveActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME), 65536);
        if (resolveInfoResolveActivity != null) {
            intent.setPackage(resolveInfoResolveActivity.activityInfo.packageName);
            if (packageManager.queryIntentActivities(intent, 0).size() > 0) {
                return intent;
            }
        }
        intent.setPackage("com.android.wallpapercropper");
        if (packageManager.queryIntentActivities(intent, 0).size() > 0) {
            return intent;
        }
        throw new IllegalArgumentException("Cannot use passed URI to set wallpaper; check that the type returned by ContentProvider matches image/*");
    }

    public void setResource(int i) throws Throwable {
        if (sGlobals.mService == null) {
            Log.w(TAG, "WallpaperService not running");
            return;
        }
        try {
            Resources resources = this.mContext.getResources();
            ParcelFileDescriptor wallpaper = sGlobals.mService.setWallpaper("res:" + resources.getResourceName(i));
            if (wallpaper == null) {
                return;
            }
            ParcelFileDescriptor.AutoCloseOutputStream autoCloseOutputStream = null;
            try {
                ParcelFileDescriptor.AutoCloseOutputStream autoCloseOutputStream2 = new ParcelFileDescriptor.AutoCloseOutputStream(wallpaper);
                try {
                    setWallpaper(resources.openRawResource(i), autoCloseOutputStream2);
                    autoCloseOutputStream2.close();
                } catch (Throwable th) {
                    th = th;
                    autoCloseOutputStream = autoCloseOutputStream2;
                    if (autoCloseOutputStream != null) {
                        autoCloseOutputStream.close();
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (RemoteException unused) {
        }
    }

    public void setBitmap(Bitmap bitmap) throws IOException {
        ParcelFileDescriptor.AutoCloseOutputStream autoCloseOutputStream;
        if (sGlobals.mService != null) {
            try {
                ParcelFileDescriptor.AutoCloseOutputStream autoCloseOutputStream2 = null;
                ParcelFileDescriptor wallpaper = sGlobals.mService.setWallpaper(null);
                if (wallpaper == null) {
                    return;
                }
                try {
                    autoCloseOutputStream = new ParcelFileDescriptor.AutoCloseOutputStream(wallpaper);
                } catch (Throwable th) {
                    th = th;
                }
                try {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, autoCloseOutputStream);
                    autoCloseOutputStream.close();
                    return;
                } catch (Throwable th2) {
                    th = th2;
                    autoCloseOutputStream2 = autoCloseOutputStream;
                    if (autoCloseOutputStream2 != null) {
                        autoCloseOutputStream2.close();
                    }
                    throw th;
                }
            } catch (RemoteException unused) {
                return;
            }
        }
        Log.w(TAG, "WallpaperService not running");
    }

    public void setStream(InputStream inputStream) throws IOException {
        if (sGlobals.mService != null) {
            try {
                ParcelFileDescriptor.AutoCloseOutputStream autoCloseOutputStream = null;
                ParcelFileDescriptor wallpaper = sGlobals.mService.setWallpaper(null);
                if (wallpaper == null) {
                    return;
                }
                try {
                    ParcelFileDescriptor.AutoCloseOutputStream autoCloseOutputStream2 = new ParcelFileDescriptor.AutoCloseOutputStream(wallpaper);
                    try {
                        setWallpaper(inputStream, autoCloseOutputStream2);
                        autoCloseOutputStream2.close();
                    } catch (Throwable th) {
                        th = th;
                        autoCloseOutputStream = autoCloseOutputStream2;
                        if (autoCloseOutputStream != null) {
                            autoCloseOutputStream.close();
                        }
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                }
            } catch (RemoteException unused) {
            }
        } else {
            Log.w(TAG, "WallpaperService not running");
        }
    }

    private void setWallpaper(InputStream inputStream, FileOutputStream fileOutputStream) throws IOException {
        byte[] bArr = new byte[32768];
        while (true) {
            int i = inputStream.read(bArr);
            if (i <= 0) {
                return;
            } else {
                fileOutputStream.write(bArr, 0, i);
            }
        }
    }

    public boolean hasResourceWallpaper(int i) {
        if (sGlobals.mService == null) {
            Log.w(TAG, "WallpaperService not running");
            return false;
        }
        try {
            return sGlobals.mService.hasNamedWallpaper("res:" + this.mContext.getResources().getResourceName(i));
        } catch (RemoteException unused) {
            return false;
        }
    }

    public int getDesiredMinimumWidth() {
        if (sGlobals.mService != null) {
            try {
                return sGlobals.mService.getWidthHint();
            } catch (RemoteException unused) {
                return 0;
            }
        }
        Log.w(TAG, "WallpaperService not running");
        return 0;
    }

    public int getDesiredMinimumHeight() {
        if (sGlobals.mService != null) {
            try {
                return sGlobals.mService.getHeightHint();
            } catch (RemoteException unused) {
                return 0;
            }
        }
        Log.w(TAG, "WallpaperService not running");
        return 0;
    }

    public void suggestDesiredDimensions(int i, int i2) {
        try {
            if (sGlobals.mService != null) {
                sGlobals.mService.setDimensionHints(i, i2);
            } else {
                Log.w(TAG, "WallpaperService not running");
            }
        } catch (RemoteException unused) {
        }
    }

    public void setWallpaperOffsets(IBinder iBinder, float f, float f2) {
        try {
            WindowManagerGlobal.getWindowSession().setWallpaperPosition(iBinder, f, f2, this.mWallpaperXStep, this.mWallpaperYStep);
        } catch (RemoteException unused) {
        }
    }

    public void setWallpaperOffsetSteps(float f, float f2) {
        this.mWallpaperXStep = f;
        this.mWallpaperYStep = f2;
    }

    public void sendWallpaperCommand(IBinder iBinder, String str, int i, int i2, int i3, Bundle bundle) {
        try {
            WindowManagerGlobal.getWindowSession().sendWallpaperCommand(iBinder, str, i, i2, i3, bundle, false);
        } catch (RemoteException unused) {
        }
    }

    public void clearWallpaperOffsets(IBinder iBinder) {
        try {
            WindowManagerGlobal.getWindowSession().setWallpaperPosition(iBinder, -1.0f, -1.0f, -1.0f, -1.0f);
        } catch (RemoteException unused) {
        }
    }

    public void clear() throws IOException {
        setResource(17302048);
    }

    static Bitmap generateBitmap(Context context, Bitmap bitmap, int i, int i2) {
        float f;
        int i3;
        if (bitmap == null) {
            return null;
        }
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        bitmap.setDensity(displayMetrics.noncompatDensityDpi);
        if (i > 0 && i2 > 0 && (bitmap.getWidth() != i || bitmap.getHeight() != i2)) {
            try {
                Bitmap bitmapCreateBitmap = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
                bitmapCreateBitmap.setDensity(displayMetrics.noncompatDensityDpi);
                Canvas canvas = new Canvas(bitmapCreateBitmap);
                Rect rect = new Rect();
                rect.right = bitmap.getWidth();
                rect.bottom = bitmap.getHeight();
                int i4 = i - rect.right;
                int i5 = i2 - rect.bottom;
                if (i4 > 0 || i5 > 0) {
                    if (i4 > i5) {
                        f = i;
                        i3 = rect.right;
                    } else {
                        f = i2;
                        i3 = rect.bottom;
                    }
                    float f2 = f / i3;
                    rect.right = (int) (rect.right * f2);
                    rect.bottom = (int) (rect.bottom * f2);
                    i4 = i - rect.right;
                    i5 = i2 - rect.bottom;
                }
                rect.offset(i4 / 2, i5 / 2);
                Paint paint = new Paint();
                paint.setFilterBitmap(true);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
                canvas.drawBitmap(bitmap, (Rect) null, rect, paint);
                bitmap.recycle();
                canvas.setBitmap(null);
                return bitmapCreateBitmap;
            } catch (OutOfMemoryError e) {
                Log.w(TAG, "Can't generate default bitmap", e);
            }
        }
        return bitmap;
    }
}
