package com.lhxy.istationdevice.android11.domain.module;

import android.content.Context;

import com.lhxy.istationdevice.android11.domain.config.ShellConfig;

/**
 * 业务模块统一接口
 * <p>
 * 这一层只定义模块编排入口，不关心页面。
 * 首页、调试页、自检、导出都从这里拿模块状态。
 */
public interface TerminalBusinessModule {
    /**
     * 模块唯一 key。
     */
    String getKey();

    /**
     * 模块展示名称。
     */
    String getTitle();

    /**
     * 模块职责说明。
     */
    String describePurpose();

    /**
     * 同步当前上下文和配置。
     */
    void updateContext(Context context, ShellConfig shellConfig);

    /**
     * 返回当前模块状态。
     */
    String describeStatus();

    /**
     * 跑一遍当前模块样例链路。
     */
    ModuleRunResult runSample(String traceId);

    /**
     * 执行一个模块动作。
     * <p>
     * 页面层只负责传动作 key，具体业务动作仍然收在模块内部。
     */
    ModuleRunResult runAction(String actionKey, String traceId);
}
