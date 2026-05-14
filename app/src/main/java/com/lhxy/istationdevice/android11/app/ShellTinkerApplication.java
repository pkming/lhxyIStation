package com.lhxy.istationdevice.android11.app;

import com.tencent.tinker.loader.TinkerLoader;
import com.tencent.tinker.loader.app.TinkerApplication;
import com.tencent.tinker.loader.shareutil.ShareConstants;

public final class ShellTinkerApplication extends TinkerApplication {
    public ShellTinkerApplication() {
        super(
                ShareConstants.TINKER_ENABLE_ALL,
                ShellApplication.class.getName(),
                TinkerLoader.class.getName(),
                false
        );
    }
}