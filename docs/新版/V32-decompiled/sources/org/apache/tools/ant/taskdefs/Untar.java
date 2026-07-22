package org.apache.tools.ant.taskdefs;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.bzip2.CBZip2InputStream;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;

/* JADX INFO: loaded from: classes3.dex */
public class Untar extends Expand {
    private UntarCompressionMethod compression = new UntarCompressionMethod();

    public void setCompression(UntarCompressionMethod untarCompressionMethod) {
        this.compression = untarCompressionMethod;
    }

    @Override // org.apache.tools.ant.taskdefs.Expand
    public void setEncoding(String str) {
        throw new BuildException("The " + getTaskName() + " task doesn't support the encoding attribute", getLocation());
    }

    @Override // org.apache.tools.ant.taskdefs.Expand
    public void setScanForUnicodeExtraFields(boolean z) {
        throw new BuildException("The " + getTaskName() + " task doesn't support the encoding attribute", getLocation());
    }

    @Override // org.apache.tools.ant.taskdefs.Expand
    protected void expandFile(FileUtils fileUtils, File file, File file2) throws Throwable {
        FileInputStream fileInputStream;
        Throwable th;
        IOException e;
        if (!file.exists()) {
            throw new BuildException("Unable to untar " + file + " as the file does not exist", getLocation());
        }
        try {
            fileInputStream = new FileInputStream(file);
            try {
                try {
                    expandStream(file.getPath(), fileInputStream, file2);
                    FileUtils.close(fileInputStream);
                } catch (IOException e2) {
                    e = e2;
                    throw new BuildException("Error while expanding " + file.getPath() + "\n" + e.toString(), e, getLocation());
                }
            } catch (Throwable th2) {
                th = th2;
                FileUtils.close(fileInputStream);
                throw th;
            }
        } catch (IOException e3) {
            fileInputStream = null;
            e = e3;
        } catch (Throwable th3) {
            fileInputStream = null;
            th = th3;
            FileUtils.close(fileInputStream);
            throw th;
        }
    }

    @Override // org.apache.tools.ant.taskdefs.Expand
    protected void expandResource(Resource resource, File file) {
        if (!resource.isExists()) {
            throw new BuildException("Unable to untar " + resource.getName() + " as the it does not exist", getLocation());
        }
        InputStream inputStream = null;
        try {
            try {
                inputStream = resource.getInputStream();
                expandStream(resource.getName(), inputStream, file);
            } catch (IOException e) {
                throw new BuildException("Error while expanding " + resource.getName(), e, getLocation());
            }
        } finally {
            FileUtils.close(inputStream);
        }
    }

    private void expandStream(String str, InputStream inputStream, File file) throws Throwable {
        TarInputStream tarInputStream = null;
        try {
            TarInputStream tarInputStream2 = new TarInputStream(this.compression.decompress(str, new BufferedInputStream(inputStream)));
            try {
                log("Expanding: " + str + " into " + file, 2);
                boolean z = true;
                FileNameMapper mapper = getMapper();
                while (true) {
                    TarEntry nextEntry = tarInputStream2.getNextEntry();
                    if (nextEntry == null) {
                        break;
                    }
                    z = false;
                    extractFile(FileUtils.getFileUtils(), null, file, tarInputStream2, nextEntry.getName(), nextEntry.getModTime(), nextEntry.isDirectory(), mapper);
                }
                if (z && getFailOnEmptyArchive()) {
                    throw new BuildException("archive '" + str + "' is empty");
                }
                log("expand complete", 3);
                FileUtils.close(tarInputStream2);
            } catch (Throwable th) {
                th = th;
                tarInputStream = tarInputStream2;
                FileUtils.close(tarInputStream);
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public static final class UntarCompressionMethod extends EnumeratedAttribute {
        private static final String BZIP2 = "bzip2";
        private static final String GZIP = "gzip";
        private static final String NONE = "none";

        public UntarCompressionMethod() {
            setValue("none");
        }

        @Override // org.apache.tools.ant.types.EnumeratedAttribute
        public String[] getValues() {
            return new String[]{"none", GZIP, BZIP2};
        }

        public InputStream decompress(String str, InputStream inputStream) throws IOException, BuildException {
            String value = getValue();
            if (GZIP.equals(value)) {
                return new GZIPInputStream(inputStream);
            }
            if (!BZIP2.equals(value)) {
                return inputStream;
            }
            char[] cArr = {'B', 'Z'};
            for (int i = 0; i < 2; i++) {
                if (inputStream.read() != cArr[i]) {
                    throw new BuildException("Invalid bz2 file." + str);
                }
            }
            return new CBZip2InputStream(inputStream);
        }
    }
}
