package com.amap.api.col.p0003sl;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;

/* JADX INFO: compiled from: PluginContext.java */
/* JADX INFO: loaded from: classes2.dex */
public final class fa extends ContextThemeWrapper {
    private static final String[] d = {"android.widget", "android.webkit", "android.app"};
    private Resources a;
    private LayoutInflater b;
    private ClassLoader c;
    private a e;
    private LayoutInflater.Factory f;

    public fa(Context context, int i, ClassLoader classLoader) {
        super(context, i);
        this.e = new a();
        this.f = new LayoutInflater.Factory() { // from class: com.amap.api.col.3sl.fa.1
            @Override // android.view.LayoutInflater.Factory
            public final View onCreateView(String str, Context context2, AttributeSet attributeSet) {
                return fa.this.a(str, context2, attributeSet);
            }
        };
        this.a = fb.a();
        this.c = classLoader;
    }

    @Override // android.view.ContextThemeWrapper, android.content.ContextWrapper, android.content.Context
    public final Resources getResources() {
        Resources resources = this.a;
        return resources != null ? resources : super.getResources();
    }

    @Override // android.view.ContextThemeWrapper, android.content.ContextWrapper, android.content.Context
    public final Object getSystemService(String str) {
        if (Context.LAYOUT_INFLATER_SERVICE.equals(str)) {
            if (this.b == null) {
                LayoutInflater layoutInflater = (LayoutInflater) super.getSystemService(str);
                if (layoutInflater != null) {
                    this.b = layoutInflater.cloneInContext(this);
                }
                this.b.setFactory(this.f);
                this.b = this.b.cloneInContext(this);
            }
            return this.b;
        }
        return super.getSystemService(str);
    }

    /* JADX INFO: compiled from: PluginContext.java */
    public class a {
        public HashSet<String> a = new HashSet<>();
        public HashMap<String, Constructor<?>> b = new HashMap<>();

        public a() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:26:0x0067 A[PHI: r5
      0x0067: PHI (r5v1 java.lang.Class<?>) = 
      (r5v0 java.lang.Class<?>)
      (r5v12 java.lang.Class<?>)
      (r5v12 java.lang.Class<?>)
      (r5v12 java.lang.Class<?>)
      (r5v12 java.lang.Class<?>)
     binds: [B:25:0x0066, B:17:0x0054, B:20:0x0059, B:35:0x0067, B:22:0x0061] A[DONT_GENERATE, DONT_INLINE]] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final android.view.View a(java.lang.String r12, android.content.Context r13, android.util.AttributeSet r14) {
        /*
            r11 = this;
            com.amap.api.col.3sl.fa$a r0 = r11.e
            java.util.HashSet<java.lang.String> r0 = r0.a
            boolean r0 = r0.contains(r12)
            r1 = 0
            if (r0 == 0) goto Lc
            return r1
        Lc:
            com.amap.api.col.3sl.fa$a r0 = r11.e
            java.util.HashMap<java.lang.String, java.lang.reflect.Constructor<?>> r0 = r0.b
            java.lang.Object r0 = r0.get(r12)
            java.lang.reflect.Constructor r0 = (java.lang.reflect.Constructor) r0
            r2 = 2
            r3 = 1
            r4 = 0
            if (r0 != 0) goto L87
            java.lang.String r5 = "api.navi"
            boolean r5 = r12.contains(r5)     // Catch: java.lang.Throwable -> L66
            if (r5 == 0) goto L2a
            java.lang.ClassLoader r5 = r11.c     // Catch: java.lang.Throwable -> L66
            java.lang.Class r5 = r5.loadClass(r12)     // Catch: java.lang.Throwable -> L66
            goto L54
        L2a:
            java.lang.String[] r5 = com.amap.api.col.p0003sl.fa.d     // Catch: java.lang.Throwable -> L66
            int r6 = r5.length     // Catch: java.lang.Throwable -> L66
            r7 = r4
        L2e:
            if (r7 >= r6) goto L53
            r8 = r5[r7]     // Catch: java.lang.Throwable -> L66
            java.lang.ClassLoader r9 = r11.c     // Catch: java.lang.Throwable -> L50
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L50
            r10.<init>()     // Catch: java.lang.Throwable -> L50
            java.lang.StringBuilder r8 = r10.append(r8)     // Catch: java.lang.Throwable -> L50
            java.lang.String r10 = "."
            java.lang.StringBuilder r8 = r8.append(r10)     // Catch: java.lang.Throwable -> L50
            java.lang.StringBuilder r8 = r8.append(r12)     // Catch: java.lang.Throwable -> L50
            java.lang.String r8 = r8.toString()     // Catch: java.lang.Throwable -> L50
            java.lang.Class r5 = r9.loadClass(r8)     // Catch: java.lang.Throwable -> L50
            goto L54
        L50:
            int r7 = r7 + 1
            goto L2e
        L53:
            r5 = r1
        L54:
            if (r5 != 0) goto L57
            goto L67
        L57:
            java.lang.Class<android.view.ViewStub> r6 = android.view.ViewStub.class
            if (r5 == r6) goto L67
            java.lang.ClassLoader r6 = r5.getClassLoader()     // Catch: java.lang.Throwable -> L67
            java.lang.ClassLoader r7 = r11.c     // Catch: java.lang.Throwable -> L67
            if (r6 == r7) goto L64
            goto L67
        L64:
            r6 = r3
            goto L68
        L66:
            r5 = r1
        L67:
            r6 = r4
        L68:
            if (r6 != 0) goto L72
            com.amap.api.col.3sl.fa$a r13 = r11.e
            java.util.HashSet<java.lang.String> r13 = r13.a
            r13.add(r12)
            return r1
        L72:
            java.lang.Class[] r6 = new java.lang.Class[r2]     // Catch: java.lang.Throwable -> L87
            java.lang.Class<android.content.Context> r7 = android.content.Context.class
            r6[r4] = r7     // Catch: java.lang.Throwable -> L87
            java.lang.Class<android.util.AttributeSet> r7 = android.util.AttributeSet.class
            r6[r3] = r7     // Catch: java.lang.Throwable -> L87
            java.lang.reflect.Constructor r0 = r5.getConstructor(r6)     // Catch: java.lang.Throwable -> L87
            com.amap.api.col.3sl.fa$a r5 = r11.e     // Catch: java.lang.Throwable -> L87
            java.util.HashMap<java.lang.String, java.lang.reflect.Constructor<?>> r5 = r5.b     // Catch: java.lang.Throwable -> L87
            r5.put(r12, r0)     // Catch: java.lang.Throwable -> L87
        L87:
            if (r0 == 0) goto L97
            java.lang.Object[] r12 = new java.lang.Object[r2]     // Catch: java.lang.Throwable -> L97
            r12[r4] = r13     // Catch: java.lang.Throwable -> L97
            r12[r3] = r14     // Catch: java.lang.Throwable -> L97
            java.lang.Object r12 = r0.newInstance(r12)     // Catch: java.lang.Throwable -> L97
            android.view.View r12 = (android.view.View) r12     // Catch: java.lang.Throwable -> L97
            r1 = r12
        L97:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.amap.api.col.p0003sl.fa.a(java.lang.String, android.content.Context, android.util.AttributeSet):android.view.View");
    }
}
