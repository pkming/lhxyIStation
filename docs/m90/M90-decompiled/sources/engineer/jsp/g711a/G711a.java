package engineer.jsp.g711a;

/* JADX INFO: loaded from: classes2.dex */
public class G711a {
    public static native void G711aDecode(byte[] bArr, Result result);

    public static native void G711aEncode(byte[] bArr, Result result);

    static {
        System.loadLibrary("G711a");
    }

    public static class Result {
        private byte[] dataArr;

        public void setDataArr(byte[] bArr) {
            this.dataArr = bArr;
        }

        public byte[] getDataArr() {
            return this.dataArr;
        }
    }
}
