package android.graphics;

import android.graphics.PorterDuff;

/* JADX INFO: loaded from: classes.dex */
public class ComposeShader extends Shader {
    private static final int TYPE_PORTERDUFFMODE = 2;
    private static final int TYPE_XFERMODE = 1;
    private PorterDuff.Mode mPorterDuffMode;
    private final Shader mShaderA;
    private final Shader mShaderB;
    private int mType = 2;
    private Xfermode mXferMode;

    private static native int nativeCreate1(int i, int i2, int i3);

    private static native int nativeCreate2(int i, int i2, int i3);

    private static native int nativePostCreate1(int i, int i2, int i3, int i4);

    private static native int nativePostCreate2(int i, int i2, int i3, int i4);

    public ComposeShader(Shader shader, Shader shader2, Xfermode xfermode) {
        this.mShaderA = shader;
        this.mShaderB = shader2;
        this.mXferMode = xfermode;
        this.native_instance = nativeCreate1(shader.native_instance, shader2.native_instance, xfermode != null ? xfermode.native_instance : 0);
        if (!(xfermode instanceof PorterDuffXfermode)) {
            this.native_shader = nativePostCreate1(this.native_instance, shader.native_shader, shader2.native_shader, xfermode != null ? xfermode.native_instance : 0);
        } else {
            PorterDuff.Mode mode = ((PorterDuffXfermode) xfermode).mode;
            this.native_shader = nativePostCreate2(this.native_instance, shader.native_shader, shader2.native_shader, mode != null ? mode.nativeInt : 0);
        }
    }

    public ComposeShader(Shader shader, Shader shader2, PorterDuff.Mode mode) {
        this.mShaderA = shader;
        this.mShaderB = shader2;
        this.mPorterDuffMode = mode;
        this.native_instance = nativeCreate2(shader.native_instance, shader2.native_instance, mode.nativeInt);
        this.native_shader = nativePostCreate2(this.native_instance, shader.native_shader, shader2.native_shader, mode.nativeInt);
    }

    @Override // android.graphics.Shader
    protected Shader copy() {
        ComposeShader composeShader;
        int i = this.mType;
        if (i == 1) {
            composeShader = new ComposeShader(this.mShaderA.copy(), this.mShaderB.copy(), this.mXferMode);
        } else if (i == 2) {
            composeShader = new ComposeShader(this.mShaderA.copy(), this.mShaderB.copy(), this.mPorterDuffMode);
        } else {
            throw new IllegalArgumentException("ComposeShader should be created with either Xfermode or PorterDuffMode");
        }
        copyLocalMatrix(composeShader);
        return composeShader;
    }
}
