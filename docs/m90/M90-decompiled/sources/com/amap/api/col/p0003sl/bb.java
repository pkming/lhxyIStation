package com.amap.api.col.p0003sl;

import android.content.Context;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: compiled from: OfflineMapDataVerify.java */
/* JADX INFO: loaded from: classes2.dex */
public final class bb extends Thread {
    private Context a;
    private bn b;

    public bb(Context context) {
        this.a = context;
        this.b = bn.a(context);
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public final void run() {
        try {
            a();
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    private static bi a(File file) throws Throwable {
        String strA = dx.a(file);
        bi biVar = new bi();
        biVar.b(strA);
        return biVar;
    }

    private void a() {
        bi biVarA;
        String strB;
        int iIndexOf;
        boolean z;
        String strB2;
        int iIndexOf2;
        String strB3;
        int iIndexOf3;
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayList<bi> arrayListA = this.b.a();
        a(arrayList, "vmap/");
        a(arrayList, "map/");
        b(arrayList, "map/");
        ArrayList<String> arrayListB = b();
        for (bi biVar : arrayListA) {
            if (biVar != null && biVar.c() != null) {
                boolean z2 = true;
                if (biVar.l == 4 || biVar.l == 7) {
                    boolean zContains = arrayList.contains(biVar.h());
                    if (zContains || (strB = bv.b(biVar.f())) == null || (iIndexOf = arrayList.indexOf(strB)) == -1) {
                        z2 = zContains;
                    } else {
                        arrayList.set(iIndexOf, biVar.h());
                    }
                    if (!z2) {
                        this.b.b(biVar);
                    }
                } else if (biVar.l == 0 || biVar.l == 1) {
                    z = arrayListB.contains(biVar.e()) || arrayListB.contains(biVar.h());
                    if (z || (strB2 = bv.b(biVar.f())) == null || (iIndexOf2 = arrayListB.indexOf(strB2)) == -1) {
                        z2 = z;
                    } else {
                        arrayListB.set(iIndexOf2, biVar.h());
                    }
                    if (!z2) {
                        this.b.b(biVar);
                    }
                } else if (biVar.l == 3 && biVar.g() != 0) {
                    z = arrayListB.contains(biVar.e()) || arrayListB.contains(biVar.h());
                    if (z || (strB3 = bv.b(biVar.f())) == null || (iIndexOf3 = arrayListB.indexOf(strB3)) == -1) {
                        z2 = z;
                    } else {
                        arrayListB.set(iIndexOf3, biVar.h());
                    }
                    if (!z2) {
                        this.b.b(biVar);
                    }
                }
            }
        }
        for (String str : arrayList) {
            if (!a(str, arrayListA) && (biVarA = a(str)) != null) {
                this.b.a(biVarA);
            }
        }
        ay ayVarA = ay.a(this.a);
        if (ayVarA != null) {
            ayVarA.b();
        }
    }

    private bi a(String str) throws Throwable {
        if (str.equals("quanguo")) {
            str = "quanguogaiyaotu";
        }
        ay ayVarA = ay.a(this.a);
        bi biVarA = null;
        if (ayVarA != null) {
            String strG = ayVarA.g(str);
            File[] fileArrListFiles = new File(dx.c(this.a)).listFiles();
            if (fileArrListFiles == null) {
                return null;
            }
            for (File file : fileArrListFiles) {
                if ((file.getName().contains(strG) || file.getName().contains(str)) && file.getName().endsWith(".zip.tmp.dt")) {
                    biVarA = a(file);
                    if (biVarA.c() != null) {
                        return biVarA;
                    }
                }
            }
        }
        return biVarA;
    }

    private static boolean a(String str, ArrayList<bi> arrayList) {
        Iterator<bi> it = arrayList.iterator();
        while (it.hasNext()) {
            if (str.equals(it.next().h())) {
                return true;
            }
        }
        return false;
    }

    private void a(ArrayList<String> arrayList, String str) {
        File[] fileArrListFiles;
        String name;
        int iLastIndexOf;
        File file = new File(dx.b(this.a) + str);
        if (file.exists() && (fileArrListFiles = file.listFiles()) != null) {
            for (File file2 : fileArrListFiles) {
                if (file2.getName().endsWith(".dat") && (iLastIndexOf = (name = file2.getName()).lastIndexOf(46)) >= 0 && iLastIndexOf < name.length()) {
                    String strSubstring = name.substring(0, iLastIndexOf);
                    if (!arrayList.contains(strSubstring)) {
                        arrayList.add(strSubstring);
                    }
                }
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:42:0x008c  */
    /* JADX WARN: Removed duplicated region for block: B:52:0x008f A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void b(java.util.ArrayList<java.lang.String> r14, java.lang.String r15) {
        /*
            r13 = this;
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            android.content.Context r1 = r13.a
            java.lang.String r1 = com.amap.api.col.p0003sl.dx.a(r1)
            r0.append(r1)
            r0.append(r15)
            java.io.File r15 = new java.io.File
            java.lang.String r0 = r0.toString()
            r15.<init>(r0)
            boolean r0 = r15.exists()
            if (r0 != 0) goto L21
            return
        L21:
            java.io.File[] r15 = r15.listFiles()
            if (r15 != 0) goto L28
            return
        L28:
            int r0 = r15.length
            r1 = 0
            r2 = r1
        L2b:
            if (r2 >= r0) goto L92
            r3 = r15[r2]
            boolean r4 = r3.isDirectory()
            if (r4 == 0) goto L8f
            java.lang.String r4 = r3.getName()
            boolean r5 = android.text.TextUtils.isEmpty(r4)
            if (r5 != 0) goto L8f
            java.lang.String[] r3 = r3.list()
            if (r3 == 0) goto L8f
            int r5 = r3.length
            if (r5 <= 0) goto L8f
            boolean r5 = r14.contains(r4)
            if (r5 != 0) goto L8f
            java.lang.String r5 = "a0"
            boolean r5 = r4.equals(r5)
            java.lang.String r6 = "m1.ans"
            r7 = 1
            if (r5 == 0) goto L69
            int r5 = r3.length
            r8 = r1
        L5b:
            if (r8 >= r5) goto L89
            r9 = r3[r8]
            boolean r9 = r6.equals(r9)
            if (r9 == 0) goto L66
            goto L8a
        L66:
            int r8 = r8 + 1
            goto L5b
        L69:
            int r5 = r3.length
            r8 = r1
            r9 = r8
            r10 = r9
        L6d:
            if (r8 >= r5) goto L84
            r11 = r3[r8]
            boolean r12 = r6.equals(r11)
            if (r12 == 0) goto L78
            r9 = r7
        L78:
            java.lang.String r12 = "m3.ans"
            boolean r11 = r12.equals(r11)
            if (r11 == 0) goto L81
            r10 = r7
        L81:
            int r8 = r8 + 1
            goto L6d
        L84:
            if (r9 == 0) goto L89
            if (r10 == 0) goto L89
            goto L8a
        L89:
            r7 = r1
        L8a:
            if (r7 == 0) goto L8f
            r14.add(r4)
        L8f:
            int r2 = r2 + 1
            goto L2b
        L92:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.amap.api.col.p0003sl.bb.b(java.util.ArrayList, java.lang.String):void");
    }

    private ArrayList<String> b() {
        File[] fileArrListFiles;
        String name;
        int iLastIndexOf;
        ArrayList<String> arrayList = new ArrayList<>();
        File file = new File(dx.c(this.a));
        if (!file.exists() || (fileArrListFiles = file.listFiles()) == null) {
            return arrayList;
        }
        for (File file2 : fileArrListFiles) {
            if (file2.getName().endsWith(".zip") && (iLastIndexOf = (name = file2.getName()).lastIndexOf(46)) >= 0 && iLastIndexOf < name.length()) {
                arrayList.add(name.substring(0, iLastIndexOf));
            }
        }
        return arrayList;
    }
}
