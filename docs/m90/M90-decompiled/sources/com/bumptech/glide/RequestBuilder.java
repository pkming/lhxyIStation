package com.bumptech.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.ErrorRequestCoordinator;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestCoordinator;
import com.bumptech.glide.request.RequestFutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.SingleRequest;
import com.bumptech.glide.request.ThumbnailRequestCoordinator;
import com.bumptech.glide.request.target.PreloadTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;
import com.bumptech.glide.signature.ApplicationVersionSignature;
import com.bumptech.glide.util.Preconditions;
import com.bumptech.glide.util.Util;
import java.io.File;
import java.net.URL;

/* JADX INFO: loaded from: classes2.dex */
public class RequestBuilder<TranscodeType> implements Cloneable, ModelTypes<RequestBuilder<TranscodeType>> {
    protected static final RequestOptions DOWNLOAD_ONLY_OPTIONS = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA).priority(Priority.LOW).skipMemoryCache(true);
    private final Context context;
    private final RequestOptions defaultRequestOptions;
    private RequestBuilder<TranscodeType> errorBuilder;
    private final Glide glide;
    private final GlideContext glideContext;
    private boolean isDefaultTransitionOptionsSet;
    private boolean isModelSet;
    private boolean isThumbnailBuilt;
    private Object model;
    private RequestListener<TranscodeType> requestListener;
    private final RequestManager requestManager;
    protected RequestOptions requestOptions;
    private Float thumbSizeMultiplier;
    private RequestBuilder<TranscodeType> thumbnailBuilder;
    private final Class<TranscodeType> transcodeClass;
    private TransitionOptions<?, ? super TranscodeType> transitionOptions;

    protected RequestBuilder(Glide glide, RequestManager requestManager, Class<TranscodeType> cls, Context context) {
        this.isDefaultTransitionOptionsSet = true;
        this.glide = glide;
        this.requestManager = requestManager;
        this.transcodeClass = cls;
        RequestOptions defaultRequestOptions = requestManager.getDefaultRequestOptions();
        this.defaultRequestOptions = defaultRequestOptions;
        this.context = context;
        this.transitionOptions = requestManager.getDefaultTransitionOptions(cls);
        this.requestOptions = defaultRequestOptions;
        this.glideContext = glide.getGlideContext();
    }

    protected RequestBuilder(Class<TranscodeType> cls, RequestBuilder<?> requestBuilder) {
        this(requestBuilder.glide, requestBuilder.requestManager, cls, requestBuilder.context);
        this.model = requestBuilder.model;
        this.isModelSet = requestBuilder.isModelSet;
        this.requestOptions = requestBuilder.requestOptions;
    }

    public RequestBuilder<TranscodeType> apply(RequestOptions requestOptions) {
        Preconditions.checkNotNull(requestOptions);
        this.requestOptions = getMutableOptions().apply(requestOptions);
        return this;
    }

    protected RequestOptions getMutableOptions() {
        RequestOptions requestOptions = this.defaultRequestOptions;
        RequestOptions requestOptions2 = this.requestOptions;
        return requestOptions == requestOptions2 ? requestOptions2.m74clone() : requestOptions2;
    }

    public RequestBuilder<TranscodeType> transition(TransitionOptions<?, ? super TranscodeType> transitionOptions) {
        this.transitionOptions = (TransitionOptions) Preconditions.checkNotNull(transitionOptions);
        this.isDefaultTransitionOptionsSet = false;
        return this;
    }

    public RequestBuilder<TranscodeType> listener(RequestListener<TranscodeType> requestListener) {
        this.requestListener = requestListener;
        return this;
    }

    public RequestBuilder<TranscodeType> error(RequestBuilder<TranscodeType> requestBuilder) {
        this.errorBuilder = requestBuilder;
        return this;
    }

    public RequestBuilder<TranscodeType> thumbnail(RequestBuilder<TranscodeType> requestBuilder) {
        this.thumbnailBuilder = requestBuilder;
        return this;
    }

    public RequestBuilder<TranscodeType> thumbnail(RequestBuilder<TranscodeType>... requestBuilderArr) {
        RequestBuilder<TranscodeType> requestBuilderThumbnail = null;
        if (requestBuilderArr == null || requestBuilderArr.length == 0) {
            return thumbnail((RequestBuilder) null);
        }
        for (int length = requestBuilderArr.length - 1; length >= 0; length--) {
            RequestBuilder<TranscodeType> requestBuilder = requestBuilderArr[length];
            if (requestBuilder != null) {
                requestBuilderThumbnail = requestBuilderThumbnail == null ? requestBuilder : requestBuilder.thumbnail(requestBuilderThumbnail);
            }
        }
        return thumbnail(requestBuilderThumbnail);
    }

    public RequestBuilder<TranscodeType> thumbnail(float f) {
        if (f < 0.0f || f > 1.0f) {
            throw new IllegalArgumentException("sizeMultiplier must be between 0 and 1");
        }
        this.thumbSizeMultiplier = Float.valueOf(f);
        return this;
    }

    @Override // com.bumptech.glide.ModelTypes
    public RequestBuilder<TranscodeType> load(Object obj) {
        return loadGeneric(obj);
    }

    private RequestBuilder<TranscodeType> loadGeneric(Object obj) {
        this.model = obj;
        this.isModelSet = true;
        return this;
    }

    @Override // com.bumptech.glide.ModelTypes
    public RequestBuilder<TranscodeType> load(Bitmap bitmap) {
        return loadGeneric(bitmap).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE));
    }

    @Override // com.bumptech.glide.ModelTypes
    public RequestBuilder<TranscodeType> load(Drawable drawable) {
        return loadGeneric(drawable).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE));
    }

    @Override // com.bumptech.glide.ModelTypes
    public RequestBuilder<TranscodeType> load(String str) {
        return loadGeneric(str);
    }

    @Override // com.bumptech.glide.ModelTypes
    public RequestBuilder<TranscodeType> load(Uri uri) {
        return loadGeneric(uri);
    }

    @Override // com.bumptech.glide.ModelTypes
    public RequestBuilder<TranscodeType> load(File file) {
        return loadGeneric(file);
    }

    @Override // com.bumptech.glide.ModelTypes
    public RequestBuilder<TranscodeType> load(Integer num) {
        return loadGeneric(num).apply(RequestOptions.signatureOf(ApplicationVersionSignature.obtain(this.context)));
    }

    @Override // com.bumptech.glide.ModelTypes
    @Deprecated
    public RequestBuilder<TranscodeType> load(URL url) {
        return loadGeneric(url);
    }

    @Override // com.bumptech.glide.ModelTypes
    public RequestBuilder<TranscodeType> load(byte[] bArr) {
        RequestBuilder<TranscodeType> requestBuilderLoadGeneric = loadGeneric(bArr);
        if (!requestBuilderLoadGeneric.requestOptions.isDiskCacheStrategySet()) {
            requestBuilderLoadGeneric = requestBuilderLoadGeneric.apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE));
        }
        return !requestBuilderLoadGeneric.requestOptions.isSkipMemoryCacheSet() ? requestBuilderLoadGeneric.apply(RequestOptions.skipMemoryCacheOf(true)) : requestBuilderLoadGeneric;
    }

    /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
    public RequestBuilder<TranscodeType> m72clone() {
        try {
            RequestBuilder<TranscodeType> requestBuilder = (RequestBuilder) super.clone();
            requestBuilder.requestOptions = requestBuilder.requestOptions.m74clone();
            requestBuilder.transitionOptions = requestBuilder.transitionOptions.m73clone();
            return requestBuilder;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public <Y extends Target<TranscodeType>> Y into(Y y) {
        return (Y) into(y, (RequestListener) null);
    }

    <Y extends Target<TranscodeType>> Y into(Y y, RequestListener<TranscodeType> requestListener) {
        return (Y) into(y, requestListener, getMutableOptions());
    }

    private <Y extends Target<TranscodeType>> Y into(Y y, RequestListener<TranscodeType> requestListener, RequestOptions requestOptions) {
        Util.assertMainThread();
        Preconditions.checkNotNull(y);
        if (!this.isModelSet) {
            throw new IllegalArgumentException("You must call #load() before calling #into()");
        }
        RequestOptions requestOptionsAutoClone = requestOptions.autoClone();
        Request requestBuildRequest = buildRequest(y, requestListener, requestOptionsAutoClone);
        Request request = y.getRequest();
        if (requestBuildRequest.isEquivalentTo(request) && !isSkipMemoryCacheWithCompletePreviousRequest(requestOptionsAutoClone, request)) {
            requestBuildRequest.recycle();
            if (!((Request) Preconditions.checkNotNull(request)).isRunning()) {
                request.begin();
            }
            return y;
        }
        this.requestManager.clear((Target<?>) y);
        y.setRequest(requestBuildRequest);
        this.requestManager.track(y, requestBuildRequest);
        return y;
    }

    private boolean isSkipMemoryCacheWithCompletePreviousRequest(RequestOptions requestOptions, Request request) {
        return !requestOptions.isMemoryCacheable() && request.isComplete();
    }

    public ViewTarget<ImageView, TranscodeType> into(ImageView imageView) {
        Util.assertMainThread();
        Preconditions.checkNotNull(imageView);
        RequestOptions requestOptionsOptionalCenterCrop = this.requestOptions;
        if (!requestOptionsOptionalCenterCrop.isTransformationSet() && requestOptionsOptionalCenterCrop.isTransformationAllowed() && imageView.getScaleType() != null) {
            switch (AnonymousClass2.$SwitchMap$android$widget$ImageView$ScaleType[imageView.getScaleType().ordinal()]) {
                case 1:
                    requestOptionsOptionalCenterCrop = requestOptionsOptionalCenterCrop.m74clone().optionalCenterCrop();
                    break;
                case 2:
                    requestOptionsOptionalCenterCrop = requestOptionsOptionalCenterCrop.m74clone().optionalCenterInside();
                    break;
                case 3:
                case 4:
                case 5:
                    requestOptionsOptionalCenterCrop = requestOptionsOptionalCenterCrop.m74clone().optionalFitCenter();
                    break;
                case 6:
                    requestOptionsOptionalCenterCrop = requestOptionsOptionalCenterCrop.m74clone().optionalCenterInside();
                    break;
            }
        }
        return (ViewTarget) into(this.glideContext.buildImageViewTarget(imageView, this.transcodeClass), null, requestOptionsOptionalCenterCrop);
    }

    @Deprecated
    public FutureTarget<TranscodeType> into(int i, int i2) {
        return submit(i, i2);
    }

    public FutureTarget<TranscodeType> submit() {
        return submit(Integer.MIN_VALUE, Integer.MIN_VALUE);
    }

    public FutureTarget<TranscodeType> submit(int i, int i2) {
        final RequestFutureTarget requestFutureTarget = new RequestFutureTarget(this.glideContext.getMainHandler(), i, i2);
        if (Util.isOnBackgroundThread()) {
            this.glideContext.getMainHandler().post(new Runnable() { // from class: com.bumptech.glide.RequestBuilder.1
                @Override // java.lang.Runnable
                public void run() {
                    if (requestFutureTarget.isCancelled()) {
                        return;
                    }
                    RequestBuilder requestBuilder = RequestBuilder.this;
                    RequestFutureTarget requestFutureTarget2 = requestFutureTarget;
                    requestBuilder.into(requestFutureTarget2, requestFutureTarget2);
                }
            });
        } else {
            into(requestFutureTarget, requestFutureTarget);
        }
        return requestFutureTarget;
    }

    public Target<TranscodeType> preload(int i, int i2) {
        return into(PreloadTarget.obtain(this.requestManager, i, i2));
    }

    public Target<TranscodeType> preload() {
        return preload(Integer.MIN_VALUE, Integer.MIN_VALUE);
    }

    @Deprecated
    public <Y extends Target<File>> Y downloadOnly(Y y) {
        return (Y) getDownloadOnlyRequest().into(y);
    }

    @Deprecated
    public FutureTarget<File> downloadOnly(int i, int i2) {
        return getDownloadOnlyRequest().submit(i, i2);
    }

    protected RequestBuilder<File> getDownloadOnlyRequest() {
        return new RequestBuilder(File.class, this).apply(DOWNLOAD_ONLY_OPTIONS);
    }

    /* JADX INFO: renamed from: com.bumptech.glide.RequestBuilder$2, reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$android$widget$ImageView$ScaleType;
        static final /* synthetic */ int[] $SwitchMap$com$bumptech$glide$Priority;

        static {
            int[] iArr = new int[Priority.values().length];
            $SwitchMap$com$bumptech$glide$Priority = iArr;
            try {
                iArr[Priority.LOW.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$bumptech$glide$Priority[Priority.NORMAL.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$bumptech$glide$Priority[Priority.HIGH.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$bumptech$glide$Priority[Priority.IMMEDIATE.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            int[] iArr2 = new int[ImageView.ScaleType.values().length];
            $SwitchMap$android$widget$ImageView$ScaleType = iArr2;
            try {
                iArr2[ImageView.ScaleType.CENTER_CROP.ordinal()] = 1;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ImageView.ScaleType.CENTER_INSIDE.ordinal()] = 2;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ImageView.ScaleType.FIT_CENTER.ordinal()] = 3;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ImageView.ScaleType.FIT_START.ordinal()] = 4;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ImageView.ScaleType.FIT_END.ordinal()] = 5;
            } catch (NoSuchFieldError unused9) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ImageView.ScaleType.FIT_XY.ordinal()] = 6;
            } catch (NoSuchFieldError unused10) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ImageView.ScaleType.CENTER.ordinal()] = 7;
            } catch (NoSuchFieldError unused11) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ImageView.ScaleType.MATRIX.ordinal()] = 8;
            } catch (NoSuchFieldError unused12) {
            }
        }
    }

    private Priority getThumbnailPriority(Priority priority) {
        int i = AnonymousClass2.$SwitchMap$com$bumptech$glide$Priority[priority.ordinal()];
        if (i == 1) {
            return Priority.NORMAL;
        }
        if (i == 2) {
            return Priority.HIGH;
        }
        if (i == 3 || i == 4) {
            return Priority.IMMEDIATE;
        }
        throw new IllegalArgumentException("unknown priority: " + this.requestOptions.getPriority());
    }

    private Request buildRequest(Target<TranscodeType> target, RequestListener<TranscodeType> requestListener, RequestOptions requestOptions) {
        return buildRequestRecursive(target, requestListener, null, this.transitionOptions, requestOptions.getPriority(), requestOptions.getOverrideWidth(), requestOptions.getOverrideHeight(), requestOptions);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private Request buildRequestRecursive(Target<TranscodeType> target, RequestListener<TranscodeType> requestListener, RequestCoordinator requestCoordinator, TransitionOptions<?, ? super TranscodeType> transitionOptions, Priority priority, int i, int i2, RequestOptions requestOptions) {
        RequestCoordinator requestCoordinator2;
        RequestCoordinator errorRequestCoordinator;
        if (this.errorBuilder != null) {
            errorRequestCoordinator = new ErrorRequestCoordinator(requestCoordinator);
            requestCoordinator2 = errorRequestCoordinator;
        } else {
            requestCoordinator2 = null;
            errorRequestCoordinator = requestCoordinator;
        }
        Request requestBuildThumbnailRequestRecursive = buildThumbnailRequestRecursive(target, requestListener, errorRequestCoordinator, transitionOptions, priority, i, i2, requestOptions);
        if (requestCoordinator2 == null) {
            return requestBuildThumbnailRequestRecursive;
        }
        int overrideWidth = this.errorBuilder.requestOptions.getOverrideWidth();
        int overrideHeight = this.errorBuilder.requestOptions.getOverrideHeight();
        if (Util.isValidDimensions(i, i2) && !this.errorBuilder.requestOptions.isValidOverride()) {
            overrideWidth = requestOptions.getOverrideWidth();
            overrideHeight = requestOptions.getOverrideHeight();
        }
        RequestBuilder<TranscodeType> requestBuilder = this.errorBuilder;
        ErrorRequestCoordinator errorRequestCoordinator2 = requestCoordinator2;
        errorRequestCoordinator2.setRequests(requestBuildThumbnailRequestRecursive, requestBuilder.buildRequestRecursive(target, requestListener, requestCoordinator2, requestBuilder.transitionOptions, requestBuilder.requestOptions.getPriority(), overrideWidth, overrideHeight, this.errorBuilder.requestOptions));
        return errorRequestCoordinator2;
    }

    private Request buildThumbnailRequestRecursive(Target<TranscodeType> target, RequestListener<TranscodeType> requestListener, RequestCoordinator requestCoordinator, TransitionOptions<?, ? super TranscodeType> transitionOptions, Priority priority, int i, int i2, RequestOptions requestOptions) {
        RequestBuilder<TranscodeType> requestBuilder = this.thumbnailBuilder;
        if (requestBuilder != null) {
            if (this.isThumbnailBuilt) {
                throw new IllegalStateException("You cannot use a request as both the main request and a thumbnail, consider using clone() on the request(s) passed to thumbnail()");
            }
            TransitionOptions<?, ? super TranscodeType> transitionOptions2 = requestBuilder.isDefaultTransitionOptionsSet ? transitionOptions : requestBuilder.transitionOptions;
            Priority priority2 = requestBuilder.requestOptions.isPrioritySet() ? this.thumbnailBuilder.requestOptions.getPriority() : getThumbnailPriority(priority);
            int overrideWidth = this.thumbnailBuilder.requestOptions.getOverrideWidth();
            int overrideHeight = this.thumbnailBuilder.requestOptions.getOverrideHeight();
            if (Util.isValidDimensions(i, i2) && !this.thumbnailBuilder.requestOptions.isValidOverride()) {
                overrideWidth = requestOptions.getOverrideWidth();
                overrideHeight = requestOptions.getOverrideHeight();
            }
            ThumbnailRequestCoordinator thumbnailRequestCoordinator = new ThumbnailRequestCoordinator(requestCoordinator);
            Request requestObtainRequest = obtainRequest(target, requestListener, requestOptions, thumbnailRequestCoordinator, transitionOptions, priority, i, i2);
            this.isThumbnailBuilt = true;
            RequestBuilder<TranscodeType> requestBuilder2 = this.thumbnailBuilder;
            Request requestBuildRequestRecursive = requestBuilder2.buildRequestRecursive(target, requestListener, thumbnailRequestCoordinator, transitionOptions2, priority2, overrideWidth, overrideHeight, requestBuilder2.requestOptions);
            this.isThumbnailBuilt = false;
            thumbnailRequestCoordinator.setRequests(requestObtainRequest, requestBuildRequestRecursive);
            return thumbnailRequestCoordinator;
        }
        if (this.thumbSizeMultiplier != null) {
            ThumbnailRequestCoordinator thumbnailRequestCoordinator2 = new ThumbnailRequestCoordinator(requestCoordinator);
            thumbnailRequestCoordinator2.setRequests(obtainRequest(target, requestListener, requestOptions, thumbnailRequestCoordinator2, transitionOptions, priority, i, i2), obtainRequest(target, requestListener, requestOptions.m74clone().sizeMultiplier(this.thumbSizeMultiplier.floatValue()), thumbnailRequestCoordinator2, transitionOptions, getThumbnailPriority(priority), i, i2));
            return thumbnailRequestCoordinator2;
        }
        return obtainRequest(target, requestListener, requestOptions, requestCoordinator, transitionOptions, priority, i, i2);
    }

    private Request obtainRequest(Target<TranscodeType> target, RequestListener<TranscodeType> requestListener, RequestOptions requestOptions, RequestCoordinator requestCoordinator, TransitionOptions<?, ? super TranscodeType> transitionOptions, Priority priority, int i, int i2) {
        Context context = this.context;
        GlideContext glideContext = this.glideContext;
        return SingleRequest.obtain(context, glideContext, this.model, this.transcodeClass, requestOptions, i, i2, priority, target, requestListener, this.requestListener, requestCoordinator, glideContext.getEngine(), transitionOptions.getTransitionFactory());
    }
}
