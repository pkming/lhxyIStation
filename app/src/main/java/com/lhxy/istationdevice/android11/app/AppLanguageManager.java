package com.lhxy.istationdevice.android11.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

import java.util.Locale;

/**
 * 应用级语言切换入口。
 */
public final class AppLanguageManager {
    private AppLanguageManager() {
    }

    public static void apply(@NonNull String languageCode) {
        AppCompatDelegate.setApplicationLocales(resolveLocales(languageCode));
    }

    public static LocaleListCompat resolveLocales(String languageCode) {
        String normalized = languageCode == null ? "auto" : languageCode.trim().toLowerCase(Locale.ROOT);
        if ("zh_cn".equals(normalized)) {
            return LocaleListCompat.forLanguageTags("zh-CN");
        }
        if ("zh_tw".equals(normalized)) {
            return LocaleListCompat.forLanguageTags("zh-TW");
        }
        if ("en".equals(normalized)) {
            return LocaleListCompat.forLanguageTags("en");
        }
        if ("ko".equals(normalized)) {
            return LocaleListCompat.forLanguageTags("ko");
        }
        if ("es".equals(normalized)) {
            return LocaleListCompat.forLanguageTags("es");
        }
        return LocaleListCompat.getEmptyLocaleList();
    }
}