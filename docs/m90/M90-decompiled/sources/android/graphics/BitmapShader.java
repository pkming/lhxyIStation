package android.graphics;

import android.graphics.Shader;

/* JADX INFO: loaded from: classes.dex */
public class BitmapShader extends Shader {
    public final Bitmap mBitmap;
    private Shader.TileMode mTileX;
    private Shader.TileMode mTileY;

    private static native int nativeCreate(int i, int i2, int i3);

    private static native int nativePostCreate(int i, int i2, int i3, int i4);

    public BitmapShader(Bitmap bitmap, Shader.TileMode tileMode, Shader.TileMode tileMode2) {
        this.mBitmap = bitmap;
        this.mTileX = tileMode;
        this.mTileY = tileMode2;
        int iNi = bitmap.ni();
        this.native_instance = nativeCreate(iNi, tileMode.nativeInt, tileMode2.nativeInt);
        this.native_shader = nativePostCreate(this.native_instance, iNi, tileMode.nativeInt, tileMode2.nativeInt);
    }

    @Override // android.graphics.Shader
    protected Shader copy() {
        BitmapShader bitmapShader = new BitmapShader(this.mBitmap, this.mTileX, this.mTileY);
        copyLocalMatrix(bitmapShader);
        return bitmapShader;
    }
}
