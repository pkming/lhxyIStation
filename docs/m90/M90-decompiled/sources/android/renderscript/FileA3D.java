package android.renderscript;

import android.content.res.AssetManager;
import android.content.res.Resources;
import java.io.File;
import java.io.InputStream;

/* JADX INFO: loaded from: classes.dex */
public class FileA3D extends BaseObj {
    IndexEntry[] mFileEntries;
    InputStream mInputStream;

    public enum EntryType {
        UNKNOWN(0),
        MESH(1);

        int mID;

        EntryType(int i) {
            this.mID = i;
        }

        static EntryType toEntryType(int i) {
            return values()[i];
        }
    }

    public static class IndexEntry {
        EntryType mEntryType;
        int mID;
        int mIndex;
        BaseObj mLoadedObj = null;
        String mName;
        RenderScript mRS;

        public String getName() {
            return this.mName;
        }

        public EntryType getEntryType() {
            return this.mEntryType;
        }

        public BaseObj getObject() {
            this.mRS.validate();
            return internalCreate(this.mRS, this);
        }

        public Mesh getMesh() {
            return (Mesh) getObject();
        }

        static synchronized BaseObj internalCreate(RenderScript renderScript, IndexEntry indexEntry) {
            BaseObj baseObj = indexEntry.mLoadedObj;
            if (baseObj != null) {
                return baseObj;
            }
            if (indexEntry.mEntryType == EntryType.UNKNOWN) {
                return null;
            }
            int iNFileA3DGetEntryByIndex = renderScript.nFileA3DGetEntryByIndex(indexEntry.mID, indexEntry.mIndex);
            if (iNFileA3DGetEntryByIndex == 0) {
                return null;
            }
            if (AnonymousClass1.$SwitchMap$android$renderscript$FileA3D$EntryType[indexEntry.mEntryType.ordinal()] == 1) {
                indexEntry.mLoadedObj = new Mesh(iNFileA3DGetEntryByIndex, renderScript);
            }
            indexEntry.mLoadedObj.updateFromNative();
            return indexEntry.mLoadedObj;
        }

        IndexEntry(RenderScript renderScript, int i, int i2, String str, EntryType entryType) {
            this.mRS = renderScript;
            this.mIndex = i;
            this.mID = i2;
            this.mName = str;
            this.mEntryType = entryType;
        }
    }

    /* JADX INFO: renamed from: android.renderscript.FileA3D$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$android$renderscript$FileA3D$EntryType;

        static {
            int[] iArr = new int[EntryType.values().length];
            $SwitchMap$android$renderscript$FileA3D$EntryType = iArr;
            try {
                iArr[EntryType.MESH.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
        }
    }

    FileA3D(int i, RenderScript renderScript, InputStream inputStream) {
        super(i, renderScript);
        this.mInputStream = inputStream;
    }

    private void initEntries() {
        int iNFileA3DGetNumIndexEntries = this.mRS.nFileA3DGetNumIndexEntries(getID(this.mRS));
        if (iNFileA3DGetNumIndexEntries <= 0) {
            return;
        }
        this.mFileEntries = new IndexEntry[iNFileA3DGetNumIndexEntries];
        int[] iArr = new int[iNFileA3DGetNumIndexEntries];
        String[] strArr = new String[iNFileA3DGetNumIndexEntries];
        this.mRS.nFileA3DGetIndexEntries(getID(this.mRS), iNFileA3DGetNumIndexEntries, iArr, strArr);
        for (int i = 0; i < iNFileA3DGetNumIndexEntries; i++) {
            this.mFileEntries[i] = new IndexEntry(this.mRS, i, getID(this.mRS), strArr[i], EntryType.toEntryType(iArr[i]));
        }
    }

    public int getIndexEntryCount() {
        IndexEntry[] indexEntryArr = this.mFileEntries;
        if (indexEntryArr == null) {
            return 0;
        }
        return indexEntryArr.length;
    }

    public IndexEntry getIndexEntry(int i) {
        if (getIndexEntryCount() == 0 || i < 0) {
            return null;
        }
        IndexEntry[] indexEntryArr = this.mFileEntries;
        if (i >= indexEntryArr.length) {
            return null;
        }
        return indexEntryArr[i];
    }

    public static FileA3D createFromAsset(RenderScript renderScript, AssetManager assetManager, String str) {
        renderScript.validate();
        int iNFileA3DCreateFromAsset = renderScript.nFileA3DCreateFromAsset(assetManager, str);
        if (iNFileA3DCreateFromAsset == 0) {
            throw new RSRuntimeException("Unable to create a3d file from asset " + str);
        }
        FileA3D fileA3D = new FileA3D(iNFileA3DCreateFromAsset, renderScript, null);
        fileA3D.initEntries();
        return fileA3D;
    }

    public static FileA3D createFromFile(RenderScript renderScript, String str) {
        int iNFileA3DCreateFromFile = renderScript.nFileA3DCreateFromFile(str);
        if (iNFileA3DCreateFromFile == 0) {
            throw new RSRuntimeException("Unable to create a3d file from " + str);
        }
        FileA3D fileA3D = new FileA3D(iNFileA3DCreateFromFile, renderScript, null);
        fileA3D.initEntries();
        return fileA3D;
    }

    public static FileA3D createFromFile(RenderScript renderScript, File file) {
        return createFromFile(renderScript, file.getAbsolutePath());
    }

    public static FileA3D createFromResource(RenderScript renderScript, Resources resources, int i) {
        renderScript.validate();
        try {
            InputStream inputStreamOpenRawResource = resources.openRawResource(i);
            if (inputStreamOpenRawResource instanceof AssetManager.AssetInputStream) {
                int iNFileA3DCreateFromAssetStream = renderScript.nFileA3DCreateFromAssetStream(((AssetManager.AssetInputStream) inputStreamOpenRawResource).getAssetInt());
                if (iNFileA3DCreateFromAssetStream == 0) {
                    throw new RSRuntimeException("Unable to create a3d file from resource " + i);
                }
                FileA3D fileA3D = new FileA3D(iNFileA3DCreateFromAssetStream, renderScript, inputStreamOpenRawResource);
                fileA3D.initEntries();
                return fileA3D;
            }
            throw new RSRuntimeException("Unsupported asset stream");
        } catch (Exception unused) {
            throw new RSRuntimeException("Unable to open resource " + i);
        }
    }
}
