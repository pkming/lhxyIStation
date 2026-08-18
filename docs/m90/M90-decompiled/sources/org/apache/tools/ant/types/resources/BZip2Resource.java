package org.apache.tools.ant.types.resources;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.bzip2.CBZip2InputStream;
import org.apache.tools.bzip2.CBZip2OutputStream;

/* JADX INFO: loaded from: classes3.dex */
public class BZip2Resource extends CompressedResource {
    private static final char[] MAGIC = {'B', 'Z'};

    @Override // org.apache.tools.ant.types.resources.CompressedResource
    protected String getCompressionName() {
        return "Bzip2";
    }

    public BZip2Resource() {
    }

    public BZip2Resource(ResourceCollection resourceCollection) {
        super(resourceCollection);
    }

    @Override // org.apache.tools.ant.types.resources.ContentTransformingResource
    protected InputStream wrapStream(InputStream inputStream) throws IOException {
        int i = 0;
        while (true) {
            char[] cArr = MAGIC;
            if (i < cArr.length) {
                if (inputStream.read() != cArr[i]) {
                    throw new IOException("Invalid bz2 stream.");
                }
                i++;
            } else {
                return new CBZip2InputStream(inputStream);
            }
        }
    }

    @Override // org.apache.tools.ant.types.resources.ContentTransformingResource
    protected OutputStream wrapStream(OutputStream outputStream) throws IOException {
        int i = 0;
        while (true) {
            char[] cArr = MAGIC;
            if (i < cArr.length) {
                outputStream.write(cArr[i]);
                i++;
            } else {
                return new CBZip2OutputStream(outputStream);
            }
        }
    }
}
