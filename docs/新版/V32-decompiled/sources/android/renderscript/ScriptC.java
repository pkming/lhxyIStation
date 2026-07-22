package android.renderscript;

import android.content.res.Resources;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: classes.dex */
public class ScriptC extends Script {
    private static final String CACHE_PATH = "com.android.renderscript.cache";
    private static final String TAG = "ScriptC";
    static String mCachePath;

    protected ScriptC(int i, RenderScript renderScript) {
        super(i, renderScript);
    }

    protected ScriptC(RenderScript renderScript, Resources resources, int i) {
        super(0, renderScript);
        int iInternalCreate = internalCreate(renderScript, resources, i);
        if (iInternalCreate == 0) {
            throw new RSRuntimeException("Loading of ScriptC script failed.");
        }
        setID(iInternalCreate);
    }

    private static synchronized int internalCreate(RenderScript renderScript, Resources resources, int i) {
        byte[] bArr;
        int i2;
        String resourceEntryName;
        InputStream inputStreamOpenRawResource = resources.openRawResource(i);
        try {
            try {
                bArr = new byte[1024];
                i2 = 0;
                while (true) {
                    int length = bArr.length - i2;
                    if (length == 0) {
                        int length2 = bArr.length * 2;
                        byte[] bArr2 = new byte[length2];
                        System.arraycopy(bArr, 0, bArr2, 0, bArr.length);
                        length = length2 - i2;
                        bArr = bArr2;
                    }
                    int i3 = inputStreamOpenRawResource.read(bArr, i2, length);
                    if (i3 <= 0) {
                        break;
                    }
                    i2 += i3;
                }
                resourceEntryName = resources.getResourceEntryName(i);
                if (mCachePath == null) {
                    File file = new File(RenderScript.mCacheDir, CACHE_PATH);
                    mCachePath = file.getAbsolutePath();
                    file.mkdirs();
                }
            } finally {
                inputStreamOpenRawResource.close();
            }
        } catch (IOException unused) {
            throw new Resources.NotFoundException();
        }
        return renderScript.nScriptCCreate(resourceEntryName, mCachePath, bArr, i2);
    }
}
