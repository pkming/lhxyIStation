package com.wuxiaolong.androidutils.library;

import java.math.BigDecimal;

/* JADX INFO: loaded from: classes2.dex */
public class ArithUtil {
    private static final int DEF_DIV_SCALE = 10;

    private ArithUtil() {
    }

    public static double add(double d, double d2) {
        return new BigDecimal(Double.toString(d)).add(new BigDecimal(Double.toString(d2))).doubleValue();
    }

    public static double sub(double d, double d2) {
        return new BigDecimal(Double.toString(d)).subtract(new BigDecimal(Double.toString(d2))).doubleValue();
    }

    public static double mul(double d, double d2) {
        return new BigDecimal(Double.toString(d)).multiply(new BigDecimal(Double.toString(d2))).doubleValue();
    }

    public static double div(double d, double d2) {
        return div(d, d2, 10);
    }

    public static double div(double d, double d2, int i) {
        if (i < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        return new BigDecimal(Double.toString(d)).divide(new BigDecimal(Double.toString(d2)), i, 4).doubleValue();
    }

    public static double round(double d, int i) {
        if (i < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        return new BigDecimal(Double.toString(d)).divide(new BigDecimal("1"), i, 4).doubleValue();
    }
}
