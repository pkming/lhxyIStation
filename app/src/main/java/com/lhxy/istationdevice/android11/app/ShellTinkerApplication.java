package com.lhxy.istationdevice.android11.app;

import com.tencent.tinker.loader.app.TinkerApplication;
import com.tencent.tinker.loader.shareutil.ShareConstants;

public final class ShellTinkerApplication extends TinkerApplication {
    // ApplicationLike 与 TinkerLoader 用字符串字面量传入，避免在 loader 类里产生指向
    // ShellApplication（ApplicationLike）的类型引用。否则 ShellApplication 必须列为 loader，
    // 而它的 installTinker() 又引用了 com.tencent.tinker.lib.*，会触发 Tinker 的
    // “loader 类引用非 loader 类”致命校验，导致任何 Java 补丁都无法生成。
    // getName() 返回的就是这两个字面量，运行时行为与原写法完全一致。
    public ShellTinkerApplication() {
        super(
                ShareConstants.TINKER_ENABLE_ALL,
                "com.lhxy.istationdevice.android11.app.ShellApplication",
                "com.tencent.tinker.loader.TinkerLoader",
                false
        );
    }
}