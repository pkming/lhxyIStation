package com.amap.api.col.p0003sl;

import android.os.Build;
import android.text.TextUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* JADX INFO: compiled from: RomIdentifier.java */
/* JADX INFO: loaded from: classes2.dex */
public final class js {
    private static volatile jr a;
    private static Properties b = b();

    private js() {
    }

    public static jr a() {
        if (a == null) {
            synchronized (js.class) {
                if (a == null) {
                    try {
                        jr jrVarA = a(Build.MANUFACTURER);
                        if ("".equals(jrVarA.a())) {
                            Iterator it = Arrays.asList(jr.MIUI.a(), jr.Flyme.a(), jr.RH.a(), jr.ColorOS.a(), jr.FuntouchOS.a(), jr.SmartisanOS.a(), jr.AmigoOS.a(), jr.Sense.a(), jr.LG.a(), jr.Google.a(), jr.NubiaUI.a()).iterator();
                            while (true) {
                                if (it.hasNext()) {
                                    jr jrVarA2 = a((String) it.next());
                                    if (!"".equals(jrVarA2.a())) {
                                        jrVarA = jrVarA2;
                                        break;
                                    }
                                } else {
                                    jrVarA = jr.Other;
                                    break;
                                }
                            }
                        }
                        a = jrVarA;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return a;
    }

    private static jr a(String str) {
        if (str == null || str.length() <= 0) {
            return jr.Other;
        }
        if (str.equals(jr.MIUI.a())) {
            jr jrVar = jr.MIUI;
            if (a(jrVar)) {
                return jrVar;
            }
        } else if (str.equals(jr.Flyme.a())) {
            jr jrVar2 = jr.Flyme;
            if (b(jrVar2)) {
                return jrVar2;
            }
        } else if (str.equals(jr.RH.a())) {
            jr jrVar3 = jr.RH;
            if (c(jrVar3)) {
                return jrVar3;
            }
        } else if (str.equals(jr.ColorOS.a())) {
            jr jrVar4 = jr.ColorOS;
            if (d(jrVar4)) {
                return jrVar4;
            }
        } else if (str.equals(jr.FuntouchOS.a())) {
            jr jrVar5 = jr.FuntouchOS;
            if (e(jrVar5)) {
                return jrVar5;
            }
        } else if (str.equals(jr.SmartisanOS.a())) {
            jr jrVar6 = jr.SmartisanOS;
            if (f(jrVar6)) {
                return jrVar6;
            }
        } else if (str.equals(jr.AmigoOS.a())) {
            jr jrVar7 = jr.AmigoOS;
            if (g(jrVar7)) {
                return jrVar7;
            }
        } else if (str.equals(jr.EUI.a())) {
            jr jrVar8 = jr.EUI;
            if (h(jrVar8)) {
                return jrVar8;
            }
        } else if (str.equals(jr.Sense.a())) {
            jr jrVar9 = jr.Sense;
            if (i(jrVar9)) {
                return jrVar9;
            }
        } else if (str.equals(jr.LG.a())) {
            jr jrVar10 = jr.LG;
            if (j(jrVar10)) {
                return jrVar10;
            }
        } else if (str.equals(jr.Google.a())) {
            jr jrVar11 = jr.Google;
            if (k(jrVar11)) {
                return jrVar11;
            }
        } else if (str.equals(jr.NubiaUI.a())) {
            jr jrVar12 = jr.NubiaUI;
            if (l(jrVar12)) {
                return jrVar12;
            }
        }
        return jr.Other;
    }

    private static void a(jr jrVar, String str) {
        Matcher matcher = Pattern.compile("([\\d.]+)[^\\d]*").matcher(str);
        if (matcher.find()) {
            try {
                String strGroup = matcher.group(1);
                jrVar.a(strGroup);
                jrVar.a(Integer.parseInt(strGroup.split("\\.")[0]));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static String b(String str) {
        String property = b.getProperty("[" + str + "]", null);
        if (TextUtils.isEmpty(property)) {
            return c(str);
        }
        return property.replace("[", "").replace("]", "");
    }

    private static String c(String str) throws Throwable {
        BufferedReader bufferedReader;
        BufferedReader bufferedReader2 = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("getprop ".concat(String.valueOf(str))).getInputStream()), 1024);
            try {
                String line = bufferedReader.readLine();
                bufferedReader.close();
                try {
                    bufferedReader.close();
                } catch (IOException unused) {
                }
                return line;
            } catch (IOException unused2) {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException unused3) {
                    }
                }
                return null;
            } catch (Throwable th) {
                th = th;
                bufferedReader2 = bufferedReader;
                if (bufferedReader2 != null) {
                    try {
                        bufferedReader2.close();
                    } catch (IOException unused4) {
                    }
                }
                throw th;
            }
        } catch (IOException unused5) {
            bufferedReader = null;
        } catch (Throwable th2) {
            th = th2;
        }
    }

    private static Properties b() {
        Properties properties = new Properties();
        try {
            properties.load(Runtime.getRuntime().exec("getprop").getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
    }

    private static boolean a(jr jrVar) {
        if (TextUtils.isEmpty(b("ro.miui.ui.version.name"))) {
            return false;
        }
        String strB = b("ro.build.version.incremental");
        a(jrVar, strB);
        jrVar.b(strB);
        return true;
    }

    private static boolean b(jr jrVar) {
        String strB = b("ro.flyme.published");
        String strB2 = b("ro.meizu.setupwizard.flyme");
        if (TextUtils.isEmpty(strB) && TextUtils.isEmpty(strB2)) {
            return false;
        }
        String strB3 = b("ro.build.display.id");
        a(jrVar, strB3);
        jrVar.b(strB3);
        return true;
    }

    private static boolean c(jr jrVar) {
        String strB = b("ro.build.version.emui");
        if (TextUtils.isEmpty(strB)) {
            return false;
        }
        a(jrVar, strB);
        jrVar.b(strB);
        return true;
    }

    private static boolean d(jr jrVar) {
        String strB = b("ro.build.version.opporom");
        if (TextUtils.isEmpty(strB)) {
            return false;
        }
        a(jrVar, strB);
        jrVar.b(strB);
        return true;
    }

    private static boolean e(jr jrVar) {
        String strB = b("ro.vivo.os.build.display.id");
        if (TextUtils.isEmpty(strB)) {
            return false;
        }
        a(jrVar, strB);
        jrVar.b(strB);
        return true;
    }

    private static boolean f(jr jrVar) {
        String strB = b("ro.smartisan.version");
        if (TextUtils.isEmpty(strB)) {
            return false;
        }
        a(jrVar, strB);
        jrVar.b(strB);
        return true;
    }

    private static boolean g(jr jrVar) {
        String strB = b("ro.build.display.id");
        if (TextUtils.isEmpty(strB) || !strB.matches("amigo([\\d.]+)[a-zA-Z]*")) {
            return false;
        }
        a(jrVar, strB);
        jrVar.b(strB);
        return true;
    }

    private static boolean h(jr jrVar) {
        String strB = b("ro.letv.release.version");
        if (TextUtils.isEmpty(strB)) {
            return false;
        }
        a(jrVar, strB);
        jrVar.b(strB);
        return true;
    }

    private static boolean i(jr jrVar) {
        String strB = b("ro.build.sense.version");
        if (TextUtils.isEmpty(strB)) {
            return false;
        }
        a(jrVar, strB);
        jrVar.b(strB);
        return true;
    }

    private static boolean j(jr jrVar) {
        String strB = b("sys.lge.lgmdm_version");
        if (TextUtils.isEmpty(strB)) {
            return false;
        }
        a(jrVar, strB);
        jrVar.b(strB);
        return true;
    }

    private static boolean k(jr jrVar) {
        if (!"android-google".equals(b("ro.com.google.clientidbase"))) {
            return false;
        }
        String strB = b("ro.build.version.release");
        jrVar.a(Build.VERSION.SDK_INT);
        jrVar.b(strB);
        return true;
    }

    private static boolean l(jr jrVar) {
        String strB = b("ro.build.nubia.rom.code");
        if (TextUtils.isEmpty(strB)) {
            return false;
        }
        a(jrVar, strB);
        jrVar.b(strB);
        return true;
    }
}
