package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.util.DOMElementWriter;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.XMLFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: classes3.dex */
public class EchoXML extends XMLFragment {
    private static final String ERROR_NO_XML = "No nested XML specified";
    private boolean append;
    private File file;
    private NamespacePolicy namespacePolicy = NamespacePolicy.DEFAULT;

    public void setFile(File file) {
        this.file = file;
    }

    public void setNamespacePolicy(NamespacePolicy namespacePolicy) {
        this.namespacePolicy = namespacePolicy;
    }

    public void setAppend(boolean z) {
        this.append = z;
    }

    public void execute() {
        OutputStream logOutputStream;
        DOMElementWriter dOMElementWriter = new DOMElementWriter(!this.append, this.namespacePolicy.getPolicy());
        try {
            try {
                try {
                    if (this.file != null) {
                        logOutputStream = new FileOutputStream(this.file.getAbsolutePath(), this.append);
                    } else {
                        logOutputStream = new LogOutputStream(this, 2);
                    }
                    OutputStream outputStream = logOutputStream;
                    Node firstChild = getFragment().getFirstChild();
                    if (firstChild == null) {
                        throw new BuildException(ERROR_NO_XML);
                    }
                    dOMElementWriter.write((Element) firstChild, outputStream);
                    FileUtils.close(outputStream);
                } catch (Exception e) {
                    throw new BuildException(e);
                }
            } catch (BuildException e2) {
                throw e2;
            }
        } catch (Throwable th) {
            FileUtils.close((OutputStream) null);
            throw th;
        }
    }

    public static class NamespacePolicy extends EnumeratedAttribute {
        private static final String ALL = "all";
        public static final NamespacePolicy DEFAULT = new NamespacePolicy("ignore");
        private static final String ELEMENTS = "elementsOnly";
        private static final String IGNORE = "ignore";

        public NamespacePolicy() {
        }

        public NamespacePolicy(String str) {
            setValue(str);
        }

        @Override // org.apache.tools.ant.types.EnumeratedAttribute
        public String[] getValues() {
            return new String[]{"ignore", ELEMENTS, "all"};
        }

        public DOMElementWriter.XmlNamespacePolicy getPolicy() {
            String value = getValue();
            if ("ignore".equalsIgnoreCase(value)) {
                return DOMElementWriter.XmlNamespacePolicy.IGNORE;
            }
            if (ELEMENTS.equalsIgnoreCase(value)) {
                return DOMElementWriter.XmlNamespacePolicy.ONLY_QUALIFY_ELEMENTS;
            }
            if ("all".equalsIgnoreCase(value)) {
                return DOMElementWriter.XmlNamespacePolicy.QUALIFY_ALL;
            }
            throw new BuildException("Invalid namespace policy: " + value);
        }
    }
}
