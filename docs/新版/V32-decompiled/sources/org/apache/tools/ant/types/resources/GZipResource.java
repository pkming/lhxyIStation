package org.apache.tools.ant.types.resources;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.tools.ant.types.ResourceCollection;

/* JADX INFO: loaded from: classes3.dex */
public class GZipResource extends CompressedResource {
    @Override // org.apache.tools.ant.types.resources.CompressedResource
    protected String getCompressionName() {
        return "GZip";
    }

    public GZipResource() {
    }

    public GZipResource(ResourceCollection resourceCollection) {
        super(resourceCollection);
    }

    @Override // org.apache.tools.ant.types.resources.ContentTransformingResource
    protected InputStream wrapStream(InputStream inputStream) throws IOException {
        return new GZIPInputStream(inputStream);
    }

    @Override // org.apache.tools.ant.types.resources.ContentTransformingResource
    protected OutputStream wrapStream(OutputStream outputStream) throws IOException {
        return new GZIPOutputStream(outputStream);
    }
}
