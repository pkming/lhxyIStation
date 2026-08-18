package com.bumptech.glide;

import com.bumptech.glide.manager.RequestManagerRetriever;
import com.bumptech.glide.module.AppGlideModule;
import java.util.Set;

/* JADX INFO: loaded from: classes2.dex */
abstract class GeneratedAppGlideModule extends AppGlideModule {
    abstract Set<Class<?>> getExcludedModuleClasses();

    RequestManagerRetriever.RequestManagerFactory getRequestManagerFactory() {
        return null;
    }

    GeneratedAppGlideModule() {
    }
}
