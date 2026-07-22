package com.wuxiaolong.androidutils.library;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import java.io.File;
import java.io.FileOutputStream;

/* JADX INFO: loaded from: classes2.dex */
public class BitmapCompressUtil {
    private Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
    private int compressQuality = 50;
    private Context context;
    private String mFileOutputPath;

    public interface BitmapCompressCallback {
        void onCompressFailure(String str);

        void onCompressSuccess(String str);
    }

    public void bitmapCompress(String str, BitmapCompressCallback bitmapCompressCallback) {
        new BitmapCropTask(bitmapCompressCallback).execute(str);
    }

    class BitmapCropTask extends AsyncTask<String, Void, Exception> {
        BitmapCompressCallback bitmapCompressCallback;

        BitmapCropTask(BitmapCompressCallback bitmapCompressCallback) {
            this.bitmapCompressCallback = bitmapCompressCallback;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Exception doInBackground(String... strArr) {
            return compress(decodeFile(strArr[0]));
        }

        private Bitmap decodeFile(String str) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            int i = options.outWidth;
            int i2 = options.outHeight;
            WindowManager windowManager = (WindowManager) BitmapCompressUtil.this.context.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
            int i3 = displayMetrics.widthPixels;
            int i4 = displayMetrics.heightPixels;
            options.inSampleSize = 1;
            if (i > i2) {
                if (i > i3) {
                    options.inSampleSize = i / i3;
                }
            } else if (i2 > i4) {
                options.inSampleSize = i2 / i4;
            }
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile(str, options);
        }

        private Exception compress(Bitmap bitmap) {
            File externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if (!externalStoragePublicDirectory.exists()) {
                externalStoragePublicDirectory.mkdirs();
            }
            File file = new File(externalStoragePublicDirectory, System.currentTimeMillis() + ".jpg");
            BitmapCompressUtil.this.mFileOutputPath = file.getAbsolutePath();
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                bitmap.compress(BitmapCompressUtil.this.compressFormat, BitmapCompressUtil.this.compressQuality, fileOutputStream);
                bitmap.recycle();
                fileOutputStream.flush();
                fileOutputStream.close();
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return e;
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Exception exc) {
            if (exc == null) {
                this.bitmapCompressCallback.onCompressSuccess(BitmapCompressUtil.this.mFileOutputPath);
            } else {
                this.bitmapCompressCallback.onCompressFailure(exc.getMessage());
            }
            super.onPostExecute(exc);
        }
    }

    public void setCompressFormat(Bitmap.CompressFormat compressFormat) {
        this.compressFormat = compressFormat;
    }

    public void setCompressQuality(int i) {
        this.compressQuality = i;
    }

    public BitmapCompressUtil(Context context) {
        this.context = context;
    }
}
