package com.amap.api.col.p0003sl;

import com.amap.api.col.p0003sl.ip;

/* JADX INFO: compiled from: PrivacyError.java */
/* JADX INFO: loaded from: classes2.dex */
public final class iq {
    public final ip.c a;
    public final String b;

    iq(ip.c cVar, is isVar) {
        String str;
        this.a = cVar;
        switch (AnonymousClass1.a[cVar.ordinal()]) {
            case 1:
                str = String.format("***确保调用SDK任何接口前先调用更新隐私合规updatePrivacyShow、updatePrivacyAgree两个接口并且参数值都为true，若未正确设置有崩溃风险***\n使用%s SDK 功能前请设置隐私权政策是否弹窗告知用户", isVar.a());
                break;
            case 2:
                str = String.format("***确保调用SDK任何接口前先调用更新隐私合规updatePrivacyShow、updatePrivacyAgree两个接口并且参数值都为true，若未正确设置有崩溃风险***\n使用%s SDK 功能前请确保隐私权政策已弹窗告知用户", isVar.a());
                break;
            case 3:
                str = String.format("***确保调用SDK任何接口前先调用更新隐私合规updatePrivacyShow、updatePrivacyAgree两个接口并且参数值都为true，若未正确设置有崩溃风险***\n使用%s SDK 功能前请确保设置隐私权政策是否包含高德开平隐私权政策", isVar.a());
                break;
            case 4:
                str = String.format("***确保调用SDK任何接口前先调用更新隐私合规updatePrivacyShow、updatePrivacyAgree两个接口并且参数值都为true，若未正确设置有崩溃风险***\n使用%s SDK 功能前请确保隐私权政策已经包含高德开平隐私权政策", isVar.a());
                break;
            case 5:
                str = String.format("***确保调用SDK任何接口前先调用更新隐私合规updatePrivacyShow、updatePrivacyAgree两个接口并且参数值都为true，若未正确设置有崩溃风险***\n使用%s SDK 功能前请确保设置隐私权政策是否取得用户同意", isVar.a());
                break;
            case 6:
                str = String.format("***确保调用SDK任何接口前先调用更新隐私合规updatePrivacyShow、updatePrivacyAgree两个接口并且参数值都为true，若未正确设置有崩溃风险***\n使用%s SDK 功能前请确保隐私权政策已取得用户同意", isVar.a());
                break;
            case 7:
                str = String.format("***确保调用SDK任何接口前先调用更新隐私合规updatePrivacyShow、updatePrivacyAgree两个接口并且参数值都为true，若未正确设置有崩溃风险***\n使用%s SDK 功能使用前请确保已经正确设置apiKey，如有疑问请在高德开放平台官网中搜索【INVALID_USER_KEY】相关内容进行解决。", isVar.a());
                break;
            case 8:
                str = String.format("***确保调用SDK任何接口前先调用更新隐私合规updatePrivacyShow、updatePrivacyAgree两个接口并且参数值都为true，若未正确设置有崩溃风险***\n参数非法，context 或 sdkInfo为空", new Object[0]);
                break;
            case 9:
                str = String.format("设置隐私政策成功", new Object[0]);
                break;
            default:
                str = "";
                break;
        }
        this.b = str;
    }

    /* JADX INFO: renamed from: com.amap.api.col.3sl.iq$1, reason: invalid class name */
    /* JADX INFO: compiled from: PrivacyError.java */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] a;

        static {
            int[] iArr = new int[ip.c.values().length];
            a = iArr;
            try {
                iArr[ip.c.ShowUnknowCode.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                a[ip.c.ShowNoShowCode.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                a[ip.c.InfoUnknowCode.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                a[ip.c.InfoNotContainCode.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                a[ip.c.AgreeUnknowCode.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                a[ip.c.AgreeNotAgreeCode.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                a[ip.c.InvaildUserKeyCode.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                a[ip.c.IllegalArgument.ordinal()] = 8;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                a[ip.c.SuccessCode.ordinal()] = 9;
            } catch (NoSuchFieldError unused9) {
            }
        }
    }
}
