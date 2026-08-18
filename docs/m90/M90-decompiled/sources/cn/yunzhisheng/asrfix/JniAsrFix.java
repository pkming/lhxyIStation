package cn.yunzhisheng.asrfix;

import com.unisound.client.ErrorCode;
import com.unisound.client.SpeechConstants;
import com.unisound.common.r;
import com.unisound.sdk.s;
import com.unisound.sdk.u;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/* JADX INFO: loaded from: classes2.dex */
public class JniAsrFix {
    private static JniAsrFix C = null;
    private static List<String> D = null;
    private static final int E = 0;
    private static final int F = -1;
    private static final int G = -2;
    private static final int H = -3;
    private static final int I = 100;
    private static final int J = 16;
    private static ArrayList<Integer> O = null;
    public static final int a = 0;
    public static final int b = 1;
    public static final int c = 2;
    public static final int d = 3;
    public static final int e = -1;
    public static final int f = -2;
    public static final int g = -3;
    public static final int h = -4;
    public static final int i = -5;
    public static final int j = -6;
    public static final int k = -7;
    public static final int l = -8;
    public static final int m = -9;
    public static final int n = -11;
    public static final int o = -12;
    public static final int p = 0;
    public static final int q = 1;
    public static final int r = 2;
    public static final int s = 3;
    public static final int t = 4;
    public static final int u = 5;
    public static final int v = 6;
    public static final int w = 7;
    public static final int x = 8;
    public static final int y = 9;
    private Object B = new Object();
    private boolean K = false;
    private int L = SpeechConstants.ASR_STATUS_LOCAL_IDEL;
    private s M = null;
    private int N = 1;
    boolean z = false;
    private boolean P = true;
    private boolean Q = false;
    protected BlockingQueue<String> A = new LinkedBlockingQueue();
    private boolean R = false;

    static {
        System.loadLibrary("asrfix");
    }

    private JniAsrFix() {
    }

    public static int a(int i2) {
        return (i2 <= -12 || i2 >= 0) ? i2 : i2 + ErrorCode.ASR_FIXENGINE_TRANS_ERROR;
    }

    public static JniAsrFix a() {
        if (C == null) {
            JniAsrFix jniAsrFix = new JniAsrFix();
            C = jniAsrFix;
            jniAsrFix.a((Boolean) true);
            D = new ArrayList();
            O = new ArrayList<>();
        }
        return C;
    }

    public static boolean a(String str) {
        return crcCheck(str) == 0;
    }

    private native int cancel();

    public static native int compileDecodeNet(String str, String str2);

    private static native int crcCheck(String str);

    private native int getOptionInt(int i2);

    private native String getOptionString(int i2, String str);

    private native String getResult();

    public static native String getVersion();

    private native int init(String str, String str2);

    private native int isEngineIdle();

    private native int isactive(byte[] bArr, int i2);

    private native int recognize(byte[] bArr, int i2);

    private native void release();

    /* JADX INFO: Access modifiers changed from: private */
    public native int reset(String str, String str2);

    private native String search(String str, String str2);

    private native int setOptionInt(int i2, int i3);

    private native int setOptionString(int i2, String str);

    private native int start(String str, int i2);

    private native int stop();

    private void u() {
        if (this.R) {
            return;
        }
        a((byte[]) null, 0);
        g();
    }

    public int a(int i2, int i3) {
        if (!this.R) {
            return ErrorCode.ASR_SDK_FIX_RECOGNIZER_NO_INIT;
        }
        int optionInt = setOptionInt(i2, i3);
        return optionInt < 0 ? a(optionInt) : optionInt;
    }

    public int a(int i2, String str) {
        if (!this.R) {
            return ErrorCode.ASR_SDK_FIX_RECOGNIZER_NO_INIT;
        }
        int optionString = setOptionString(i2, str);
        return optionString < 0 ? a(optionString) : optionString;
    }

    public int a(long j2, String str, String str2) {
        if (D.contains(str)) {
            r.e("loadGompiledJsgf failed , the jsgf.dat of this grammarTag is already exists! The grammarTag is " + str);
            return -1;
        }
        int iLoadCompiledJsgf = loadCompiledJsgf(j2, str2);
        if (iLoadCompiledJsgf == 0) {
            D.add(str);
        }
        return iLoadCompiledJsgf;
    }

    public int a(long j2, String str, String str2, String str3) {
        String str4;
        synchronized (C) {
            int i2 = 0;
            this.P = false;
            if (j2 == 0) {
                r.c("compile  compileDynamicUserData fail handle=0");
                return ErrorCode.ASR_SDK_FIX_COMPILE_NO_INIT;
            }
            if (D.contains(str)) {
                str4 = "compileDynamicUserData : grammarDat is loaded so compile directly!";
            } else {
                r.c("compileDynamicUserData  loadedGrammar = + " + str + " grammarPath= " + str2);
                int iLoadCompiledJsgf = loadCompiledJsgf(j2, str2);
                if (iLoadCompiledJsgf != 0) {
                    r.e("compileDynamicUserData loadCompileJsgf error!  loadCompiledJsgfResult = " + iLoadCompiledJsgf);
                    return iLoadCompiledJsgf;
                }
                D.add(str);
                str4 = "compileDynamicUserData loadCompiledJsgf success!";
            }
            r.c(str4);
            while (isEngineIdle() != 1 && i2 < 2000) {
                try {
                    Thread.sleep(50L);
                    i2 += 50;
                } catch (InterruptedException e2) {
                    e2.printStackTrace();
                }
            }
            int iCompileDynamicUserData = compileDynamicUserData(j2, str3, str);
            r.c(iCompileDynamicUserData == 0 ? "compileDynamicUserData : compile success! " + i2 : "compileDynamicUserData : compile failed!" + i2);
            this.P = true;
            return iCompileDynamicUserData;
        }
    }

    public int a(String str, int i2) {
        u();
        if (!this.R) {
            return ErrorCode.ASR_SDK_FIX_RECOGNIZER_NO_INIT;
        }
        r.c("JniAsrFix : start_ -> recognizerStatus = " + this.L);
        if (this.L != 1501) {
            return ErrorCode.ASR_FIXENGINE_UNKNOW_ERROR;
        }
        int iStart = start(str, i2);
        if (iStart == 0) {
            this.K = true;
            this.L = SpeechConstants.ASR_STATUS_LOCAL_RECOGNIZING;
            this.M.a(SpeechConstants.ASR_STATUS_LOCAL_RECOGNIZING);
        }
        return iStart < 0 ? a(iStart) : iStart;
    }

    public int a(String str, String str2, u uVar, String str3) {
        this.P = false;
        if (this.R) {
            r.c("Recognizer.loadModel queue add " + str + " " + str2);
            this.A.add(str);
            this.A.add(str2);
            this.A.add(str3);
            if (!this.Q) {
                new a(this, uVar).start();
            }
        } else {
            r.e("Recognizer.loadModel not init Error");
        }
        return 0;
    }

    public int a(String str, String str2, String str3, u uVar) {
        i();
        this.z = false;
        int iInit = init(str, str2);
        if (iInit == 0) {
            this.L = SpeechConstants.ASR_STATUS_LOCAL_IDEL;
            this.R = true;
            if ("init_asr" == str3) {
                this.N = getOptionInt(100);
                for (int i2 = 0; i2 < this.N; i2++) {
                    int optionInt = setOptionInt(16, i2);
                    r.c("JniAsrFix ", "modelNum = " + this.N + ", modelId = " + optionInt);
                    O.add(Integer.valueOf(optionInt));
                }
                uVar.a(O);
                if (this.N < 2) {
                    int optionInt2 = setOptionInt(16, 0);
                    r.c("JniAsrFix :modelNum = " + this.N + ", defaulltModelId = " + optionInt2);
                    uVar.o(optionInt2);
                    uVar.n(optionInt2);
                }
                this.M.a(SpeechConstants.ASR_EVENT_ENGINE_INIT_DONE, (int) System.currentTimeMillis());
            }
        }
        return a(iInit);
    }

    public int a(String str, String str2, String str3, String str4, String str5, String str6) {
        r.c("compile  initUserDataCompiler");
        long jInitUserDataCompiler = initUserDataCompiler(str4);
        if (jInitUserDataCompiler == 0) {
            return ErrorCode.ASR_SDK_FIX_COMPILE_NO_INIT;
        }
        r.c("compile  compileUserData ===handle,inPartialFile, jsgf, szContent, netDat,outPartialFile = " + jInitUserDataCompiler + " , " + str + " , " + str2 + " , " + str3 + " , " + str5 + " , " + str6);
        int iPartialCompileUserData = partialCompileUserData(jInitUserDataCompiler, str, str2, str3, str5, str6);
        r.c("compile  destroyUserDataCompiler");
        destroyUserDataCompiler(jInitUserDataCompiler);
        if (iPartialCompileUserData == 0) {
            r.c("compile  compileUserData ok");
            return iPartialCompileUserData;
        }
        if (iPartialCompileUserData == -10) {
            r.e("compile compileUserData partialfile error, autofix ok");
            return 0;
        }
        r.e("compile  compileUserData fail code = " + iPartialCompileUserData);
        return a(iPartialCompileUserData);
    }

    public int a(byte[] bArr, int i2) {
        if (!this.R) {
            return ErrorCode.ASR_SDK_FIX_RECOGNIZER_NO_INIT;
        }
        int iIsactive = isactive(bArr, i2);
        return iIsactive < 0 ? a(iIsactive) : iIsactive;
    }

    public String a(String str, String str2) {
        if (this.R) {
            return search(str, str2);
        }
        return null;
    }

    public void a(s sVar) {
        this.M = sVar;
    }

    public void a(Boolean bool) {
        int i2 = bool.booleanValue() ? 1 : 0;
        setOptionInt(12, i2);
        setOptionInt(13, i2);
    }

    public int b(int i2) {
        return setOptionInt(17, i2);
    }

    public int b(byte[] bArr, int i2) {
        return this.R ? recognize(bArr, i2) : ErrorCode.ASR_SDK_FIX_RECOGNIZER_NO_INIT;
    }

    public void b() {
        int iCancel = cancel();
        if (iCancel != 0) {
            r.c("JniAsrFix : cancel failed , result code = " + iCancel);
        }
    }

    public int c(int i2) {
        return setOptionInt(18, i2 / 10);
    }

    public boolean c() {
        return this.z;
    }

    public native int compileDynamicUserData(long j2, String str, String str2);

    public native int compileUserData(long j2, String str, String str2, String str3);

    public void d() {
        this.z = true;
    }

    public native void destroyUserDataCompiler(long j2);

    public int e() {
        if (!this.R) {
            return ErrorCode.ASR_SDK_FIX_RECOGNIZER_NO_INIT;
        }
        int iStop = stop();
        if (iStop == 0) {
            this.L = SpeechConstants.ASR_STATUS_LOCAL_IDEL;
            this.M.a(SpeechConstants.ASR_STATUS_LOCAL_IDEL);
            this.K = false;
        }
        return iStop < 0 ? a(iStop) : iStop;
    }

    public String f() {
        return this.R ? getResult() : "";
    }

    public int g() {
        if (!this.R) {
            return ErrorCode.ASR_SDK_FIX_RECOGNIZER_NO_INIT;
        }
        int iCancel = cancel();
        if (iCancel == 0) {
            this.L = SpeechConstants.ASR_STATUS_LOCAL_IDEL;
            this.M.a(SpeechConstants.ASR_STATUS_LOCAL_IDEL);
            this.K = false;
        }
        return iCancel < 0 ? a(iCancel) : iCancel;
    }

    public native String getTagsInfo(long j2);

    public boolean h() {
        return this.R;
    }

    public void i() {
        if (this.R) {
            r.c("do Release");
            release();
            D.clear();
            this.R = false;
            this.K = false;
            this.P = true;
        }
    }

    public native long initUserDataCompiler(String str);

    public boolean j() {
        return this.K;
    }

    public int k() {
        return this.N;
    }

    public int l() {
        return getOptionInt(101);
    }

    public native int loadCompiledJsgf(long j2, String str);

    public native int loadGrammarStr(String str);

    public List<Integer> m() {
        return O;
    }

    public int n() {
        return getOptionInt(103);
    }

    public String o() {
        return getOptionString(104, "");
    }

    public String p() {
        return getOptionString(105, "");
    }

    public native int partialCompileUserData(long j2, String str, String str2, String str3, String str4, String str5);

    public int q() {
        return getOptionInt(102);
    }

    public int r() {
        return getOptionInt(107);
    }

    public int s() {
        return getOptionInt(106);
    }

    public native int unloadGrammar(String str);
}
