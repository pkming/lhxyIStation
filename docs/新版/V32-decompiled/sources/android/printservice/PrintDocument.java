package android.printservice;

import android.print.PrintDocumentInfo;
import android.print.PrintJobId;

/* JADX INFO: loaded from: classes.dex */
public final class PrintDocument {
    private static final String LOG_TAG = "PrintDocument";
    private final PrintDocumentInfo mInfo;
    private final PrintJobId mPrintJobId;
    private final IPrintServiceClient mPrintServiceClient;

    PrintDocument(PrintJobId printJobId, IPrintServiceClient iPrintServiceClient, PrintDocumentInfo printDocumentInfo) {
        this.mPrintJobId = printJobId;
        this.mPrintServiceClient = iPrintServiceClient;
        this.mInfo = printDocumentInfo;
    }

    public PrintDocumentInfo getInfo() {
        PrintService.throwIfNotCalledOnMainThread();
        return this.mInfo;
    }

    /* JADX WARN: Not initialized variable reg: 3, insn: 0x0020: MOVE (r2 I:??[OBJECT, ARRAY]) = (r3 I:??[OBJECT, ARRAY]), block:B:9:0x0020 */
    /* JADX WARN: Removed duplicated region for block: B:34:0x003e A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:38:0x002f A[EXC_TOP_SPLITTER, PHI: r3
      0x002f: PHI (r3v4 android.os.ParcelFileDescriptor) = (r3v3 android.os.ParcelFileDescriptor), (r3v5 android.os.ParcelFileDescriptor) binds: [B:25:0x0038, B:19:0x002d] A[DONT_GENERATE, DONT_INLINE], SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public android.os.ParcelFileDescriptor getData() throws java.lang.Throwable {
        /*
            r7 = this;
            java.lang.String r0 = "Error calling getting print job data!"
            java.lang.String r1 = "PrintDocument"
            android.printservice.PrintService.throwIfNotCalledOnMainThread()
            r2 = 0
            android.os.ParcelFileDescriptor[] r3 = android.os.ParcelFileDescriptor.createPipe()     // Catch: java.lang.Throwable -> L26 android.os.RemoteException -> L28 java.io.IOException -> L33
            r4 = 0
            r4 = r3[r4]     // Catch: java.lang.Throwable -> L26 android.os.RemoteException -> L28 java.io.IOException -> L33
            r5 = 1
            r3 = r3[r5]     // Catch: java.lang.Throwable -> L26 android.os.RemoteException -> L28 java.io.IOException -> L33
            android.printservice.IPrintServiceClient r5 = r7.mPrintServiceClient     // Catch: java.lang.Throwable -> L1f android.os.RemoteException -> L22 java.io.IOException -> L24
            android.print.PrintJobId r6 = r7.mPrintJobId     // Catch: java.lang.Throwable -> L1f android.os.RemoteException -> L22 java.io.IOException -> L24
            r5.writePrintJobData(r3, r6)     // Catch: java.lang.Throwable -> L1f android.os.RemoteException -> L22 java.io.IOException -> L24
            if (r3 == 0) goto L1e
            r3.close()     // Catch: java.io.IOException -> L1e
        L1e:
            return r4
        L1f:
            r0 = move-exception
            r2 = r3
            goto L3c
        L22:
            r4 = move-exception
            goto L2a
        L24:
            r4 = move-exception
            goto L35
        L26:
            r0 = move-exception
            goto L3c
        L28:
            r4 = move-exception
            r3 = r2
        L2a:
            android.util.Log.e(r1, r0, r4)     // Catch: java.lang.Throwable -> L1f
            if (r3 == 0) goto L3b
        L2f:
            r3.close()     // Catch: java.io.IOException -> L3b
            goto L3b
        L33:
            r4 = move-exception
            r3 = r2
        L35:
            android.util.Log.e(r1, r0, r4)     // Catch: java.lang.Throwable -> L1f
            if (r3 == 0) goto L3b
            goto L2f
        L3b:
            return r2
        L3c:
            if (r2 == 0) goto L41
            r2.close()     // Catch: java.io.IOException -> L41
        L41:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: android.printservice.PrintDocument.getData():android.os.ParcelFileDescriptor");
    }
}
