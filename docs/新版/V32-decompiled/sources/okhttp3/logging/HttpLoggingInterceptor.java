package okhttp3.logging;

import android.net.LinkQualityInfo;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.Platform;
import okhttp3.internal.http.HttpEngine;
import okio.Buffer;
import okio.BufferedSource;

/* JADX INFO: loaded from: classes2.dex */
public final class HttpLoggingInterceptor implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");
    private volatile Level level;
    private final Logger logger;

    public enum Level {
        NONE,
        BASIC,
        HEADERS,
        BODY
    }

    public interface Logger {
        public static final Logger DEFAULT = new Logger() { // from class: okhttp3.logging.HttpLoggingInterceptor.Logger.1
            @Override // okhttp3.logging.HttpLoggingInterceptor.Logger
            public void log(String str) {
                Platform.get().log(str);
            }
        };

        void log(String str);
    }

    public HttpLoggingInterceptor() {
        this(Logger.DEFAULT);
    }

    public HttpLoggingInterceptor(Logger logger) {
        this.level = Level.NONE;
        this.logger = logger;
    }

    public HttpLoggingInterceptor setLevel(Level level) {
        Objects.requireNonNull(level, "level == null. Use Level.NONE instead.");
        this.level = level;
        return this;
    }

    public Level getLevel() {
        return this.level;
    }

    @Override // okhttp3.Interceptor
    public Response intercept(Interceptor.Chain chain) throws IOException {
        int i;
        Level level = this.level;
        Request request = chain.request();
        if (level == Level.NONE) {
            return chain.proceed(request);
        }
        boolean z = level == Level.BODY;
        boolean z2 = z || level == Level.HEADERS;
        RequestBody requestBodyBody = request.body();
        boolean z3 = requestBodyBody != null;
        Connection connection = chain.connection();
        String str = "--> " + request.method() + ' ' + request.url() + ' ' + (connection != null ? connection.protocol() : Protocol.HTTP_1_1);
        if (!z2 && z3) {
            str = str + " (" + requestBodyBody.contentLength() + "-byte body)";
        }
        this.logger.log(str);
        if (z2) {
            if (z3) {
                if (requestBodyBody.contentType() != null) {
                    this.logger.log("Content-Type: " + requestBodyBody.contentType());
                }
                if (requestBodyBody.contentLength() != -1) {
                    this.logger.log("Content-Length: " + requestBodyBody.contentLength());
                }
            }
            Headers headers = request.headers();
            int size = headers.size();
            int i2 = 0;
            while (i2 < size) {
                String strName = headers.name(i2);
                if ("Content-Type".equalsIgnoreCase(strName) || "Content-Length".equalsIgnoreCase(strName)) {
                    i = size;
                } else {
                    i = size;
                    this.logger.log(strName + ": " + headers.value(i2));
                }
                i2++;
                size = i;
            }
            if (!z || !z3) {
                this.logger.log("--> END " + request.method());
            } else if (bodyEncoded(request.headers())) {
                this.logger.log("--> END " + request.method() + " (encoded body omitted)");
            } else {
                Buffer buffer = new Buffer();
                requestBodyBody.writeTo(buffer);
                Charset charset = UTF8;
                MediaType mediaTypeContentType = requestBodyBody.contentType();
                if (mediaTypeContentType != null) {
                    charset = mediaTypeContentType.charset(charset);
                }
                this.logger.log("");
                this.logger.log(buffer.readString(charset));
                this.logger.log("--> END " + request.method() + " (" + requestBodyBody.contentLength() + "-byte body)");
            }
        }
        long jNanoTime = System.nanoTime();
        Response responseProceed = chain.proceed(request);
        long millis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - jNanoTime);
        ResponseBody responseBodyBody = responseProceed.body();
        long jContentLength = responseBodyBody.contentLength();
        this.logger.log("<-- " + responseProceed.code() + ' ' + responseProceed.message() + ' ' + responseProceed.request().url() + " (" + millis + "ms" + (!z2 ? ", " + (jContentLength != -1 ? jContentLength + "-byte" : "unknown-length") + " body" : "") + ')');
        if (z2) {
            Headers headers2 = responseProceed.headers();
            int size2 = headers2.size();
            for (int i3 = 0; i3 < size2; i3++) {
                this.logger.log(headers2.name(i3) + ": " + headers2.value(i3));
            }
            if (!z || !HttpEngine.hasBody(responseProceed)) {
                this.logger.log("<-- END HTTP");
            } else if (bodyEncoded(responseProceed.headers())) {
                this.logger.log("<-- END HTTP (encoded body omitted)");
            } else {
                BufferedSource bufferedSourceSource = responseBodyBody.source();
                bufferedSourceSource.request(LinkQualityInfo.UNKNOWN_LONG);
                Buffer buffer2 = bufferedSourceSource.buffer();
                Charset charset2 = UTF8;
                MediaType mediaTypeContentType2 = responseBodyBody.contentType();
                if (mediaTypeContentType2 != null) {
                    try {
                        charset2 = mediaTypeContentType2.charset(charset2);
                    } catch (UnsupportedCharsetException unused) {
                        this.logger.log("");
                        this.logger.log("Couldn't decode the response body; charset is likely malformed.");
                        this.logger.log("<-- END HTTP");
                        return responseProceed;
                    }
                }
                if (jContentLength != 0) {
                    this.logger.log("");
                    this.logger.log(buffer2.clone().readString(charset2));
                }
                this.logger.log("<-- END HTTP (" + buffer2.size() + "-byte body)");
            }
        }
        return responseProceed;
    }

    private boolean bodyEncoded(Headers headers) {
        String str = headers.get("Content-Encoding");
        return (str == null || str.equalsIgnoreCase("identity")) ? false : true;
    }
}
