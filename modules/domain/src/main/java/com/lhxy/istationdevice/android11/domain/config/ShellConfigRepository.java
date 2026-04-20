package com.lhxy.istationdevice.android11.domain.config;

import android.content.Context;

import java.io.File;

/**
 * 终端配置仓库
 * <p>
 * 统一缓存当前生效的配置，避免每个页面自己读 assets。
 */
public final class ShellConfigRepository {
    private static volatile ShellConfig shellConfig;

    private ShellConfigRepository() {
    }

    /**
     * 获取当前配置。
     */
    public static ShellConfig get(Context context) {
        if (shellConfig == null) {
            synchronized (ShellConfigRepository.class) {
                if (shellConfig == null) {
                    shellConfig = ShellConfigLoader.loadFromAssets(context == null ? null : context.getApplicationContext());
                }
            }
        }
        return shellConfig;
    }

    /**
     * 调试时重新加载配置。
     */
    public static ShellConfig reload(Context context) {
        shellConfig = ShellConfigLoader.loadFromAssets(context == null ? null : context.getApplicationContext());
        return shellConfig;
    }

    /**
     * 保存运行期配置并刷新缓存。
     */
    public static ShellConfig save(Context context, ShellConfig updatedConfig) throws Exception {
        Context appContext = context == null ? null : context.getApplicationContext();
        ShellConfigLoader.saveRuntimeConfig(appContext, updatedConfig);
        shellConfig = updatedConfig;
        return updatedConfig;
    }

    /**
     * 返回当前运行期配置文件。
     */
    public static File getRuntimeConfigFile(Context context) {
        return ShellConfigLoader.getRuntimeConfigFile(context == null ? null : context.getApplicationContext());
    }
}
