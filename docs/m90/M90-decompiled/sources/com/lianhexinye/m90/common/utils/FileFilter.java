package com.lianhexinye.m90.common.utils;

import java.io.File;
import java.io.FilenameFilter;

/* JADX INFO: loaded from: classes2.dex */
public class FileFilter implements FilenameFilter {
    String filterString;

    public FileFilter(String str) {
        this.filterString = str;
    }

    @Override // java.io.FilenameFilter
    public boolean accept(File file, String str) {
        return str.endsWith(this.filterString);
    }
}
