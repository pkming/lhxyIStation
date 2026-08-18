package retrofit2;

import java.io.IOException;
import java.util.Objects;
import javax.annotation.Nullable;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;

/* JADX INFO: loaded from: classes3.dex */
final class OkHttpCall<T> implements Call<T> {

    @Nullable
    private final Object[] args;
    private volatile boolean canceled;

    @Nullable
    private Throwable creationFailure;
    private boolean executed;

    @Nullable
    private okhttp3.Call rawCall;
    private final ServiceMethod<T, ?> serviceMethod;

    OkHttpCall(ServiceMethod<T, ?> serviceMethod, @Nullable Object[] objArr) {
        this.serviceMethod = serviceMethod;
        this.args = objArr;
    }

    @Override // retrofit2.Call
    public OkHttpCall<T> clone() {
        return new OkHttpCall<>(this.serviceMethod, this.args);
    }

    @Override // retrofit2.Call
    public synchronized Request request() {
        okhttp3.Call call = this.rawCall;
        if (call != null) {
            return call.request();
        }
        Throwable th = this.creationFailure;
        if (th != null) {
            if (th instanceof IOException) {
                throw new RuntimeException("Unable to create request.", this.creationFailure);
            }
            throw ((RuntimeException) th);
        }
        try {
            okhttp3.Call callCreateRawCall = createRawCall();
            this.rawCall = callCreateRawCall;
            return callCreateRawCall.request();
        } catch (IOException e) {
            this.creationFailure = e;
            throw new RuntimeException("Unable to create request.", e);
        } catch (RuntimeException e2) {
            this.creationFailure = e2;
            throw e2;
        }
    }

    @Override // retrofit2.Call
    public void enqueue(final Callback<T> callback) {
        okhttp3.Call call;
        Throwable th;
        Utils.checkNotNull(callback, "callback == null");
        synchronized (this) {
            if (this.executed) {
                throw new IllegalStateException("Already executed.");
            }
            this.executed = true;
            call = this.rawCall;
            th = this.creationFailure;
            if (call == null && th == null) {
                try {
                    okhttp3.Call callCreateRawCall = createRawCall();
                    this.rawCall = callCreateRawCall;
                    call = callCreateRawCall;
                } catch (Throwable th2) {
                    th = th2;
                    this.creationFailure = th;
                }
            }
        }
        if (th != null) {
            callback.onFailure(this, th);
            return;
        }
        if (this.canceled) {
            call.cancel();
        }
        call.enqueue(new okhttp3.Callback() { // from class: retrofit2.OkHttpCall.1
            @Override // okhttp3.Callback
            public void onResponse(okhttp3.Call call2, okhttp3.Response response) throws IOException {
                try {
                    callSuccess(OkHttpCall.this.parseResponse(response));
                } catch (Throwable th3) {
                    callFailure(th3);
                }
            }

            @Override // okhttp3.Callback
            public void onFailure(okhttp3.Call call2, IOException iOException) {
                try {
                    callback.onFailure(OkHttpCall.this, iOException);
                } catch (Throwable th3) {
                    th3.printStackTrace();
                }
            }

            private void callFailure(Throwable th3) {
                try {
                    callback.onFailure(OkHttpCall.this, th3);
                } catch (Throwable th4) {
                    th4.printStackTrace();
                }
            }

            private void callSuccess(Response<T> response) {
                try {
                    callback.onResponse(OkHttpCall.this, response);
                } catch (Throwable th3) {
                    th3.printStackTrace();
                }
            }
        });
    }

    @Override // retrofit2.Call
    public synchronized boolean isExecuted() {
        return this.executed;
    }

    @Override // retrofit2.Call
    public Response<T> execute() throws IOException {
        okhttp3.Call callCreateRawCall;
        synchronized (this) {
            if (this.executed) {
                throw new IllegalStateException("Already executed.");
            }
            this.executed = true;
            Throwable th = this.creationFailure;
            if (th != null) {
                if (th instanceof IOException) {
                    throw ((IOException) th);
                }
                throw ((RuntimeException) th);
            }
            callCreateRawCall = this.rawCall;
            if (callCreateRawCall == null) {
                try {
                    callCreateRawCall = createRawCall();
                    this.rawCall = callCreateRawCall;
                } catch (IOException | RuntimeException e) {
                    this.creationFailure = e;
                    throw e;
                }
            }
        }
        if (this.canceled) {
            callCreateRawCall.cancel();
        }
        return parseResponse(callCreateRawCall.execute());
    }

    private okhttp3.Call createRawCall() throws IOException {
        okhttp3.Call callNewCall = this.serviceMethod.callFactory.newCall(this.serviceMethod.toRequest(this.args));
        Objects.requireNonNull(callNewCall, "Call.Factory returned null.");
        return callNewCall;
    }

    Response<T> parseResponse(okhttp3.Response response) throws IOException {
        ResponseBody responseBodyBody = response.body();
        okhttp3.Response responseBuild = response.newBuilder().body(new NoContentResponseBody(responseBodyBody.contentType(), responseBodyBody.contentLength())).build();
        int iCode = responseBuild.code();
        if (iCode < 200 || iCode >= 300) {
            try {
                return Response.error(Utils.buffer(responseBodyBody), responseBuild);
            } finally {
                responseBodyBody.close();
            }
        }
        if (iCode == 204 || iCode == 205) {
            responseBodyBody.close();
            return Response.success((Object) null, responseBuild);
        }
        ExceptionCatchingRequestBody exceptionCatchingRequestBody = new ExceptionCatchingRequestBody(responseBodyBody);
        try {
            return Response.success(this.serviceMethod.toResponse(exceptionCatchingRequestBody), responseBuild);
        } catch (RuntimeException e) {
            exceptionCatchingRequestBody.throwIfCaught();
            throw e;
        }
    }

    @Override // retrofit2.Call
    public void cancel() {
        okhttp3.Call call;
        this.canceled = true;
        synchronized (this) {
            call = this.rawCall;
        }
        if (call != null) {
            call.cancel();
        }
    }

    @Override // retrofit2.Call
    public boolean isCanceled() {
        boolean z = true;
        if (this.canceled) {
            return true;
        }
        synchronized (this) {
            okhttp3.Call call = this.rawCall;
            if (call == null || !call.isCanceled()) {
                z = false;
            }
        }
        return z;
    }

    static final class NoContentResponseBody extends ResponseBody {
        private final long contentLength;
        private final MediaType contentType;

        NoContentResponseBody(MediaType mediaType, long j) {
            this.contentType = mediaType;
            this.contentLength = j;
        }

        @Override // okhttp3.ResponseBody
        public MediaType contentType() {
            return this.contentType;
        }

        @Override // okhttp3.ResponseBody
        public long contentLength() {
            return this.contentLength;
        }

        @Override // okhttp3.ResponseBody
        public BufferedSource source() {
            throw new IllegalStateException("Cannot read raw response body of a converted body.");
        }
    }

    static final class ExceptionCatchingRequestBody extends ResponseBody {
        private final ResponseBody delegate;
        IOException thrownException;

        ExceptionCatchingRequestBody(ResponseBody responseBody) {
            this.delegate = responseBody;
        }

        @Override // okhttp3.ResponseBody
        public MediaType contentType() {
            return this.delegate.contentType();
        }

        @Override // okhttp3.ResponseBody
        public long contentLength() {
            return this.delegate.contentLength();
        }

        @Override // okhttp3.ResponseBody
        public BufferedSource source() {
            return Okio.buffer(new ForwardingSource(this.delegate.source()) { // from class: retrofit2.OkHttpCall.ExceptionCatchingRequestBody.1
                @Override // okio.ForwardingSource, okio.Source
                public long read(Buffer buffer, long j) throws IOException {
                    try {
                        return super.read(buffer, j);
                    } catch (IOException e) {
                        ExceptionCatchingRequestBody.this.thrownException = e;
                        throw e;
                    }
                }
            });
        }

        @Override // okhttp3.ResponseBody, java.io.Closeable, java.lang.AutoCloseable
        public void close() {
            this.delegate.close();
        }

        void throwIfCaught() throws IOException {
            IOException iOException = this.thrownException;
            if (iOException != null) {
                throw iOException;
            }
        }
    }
}
