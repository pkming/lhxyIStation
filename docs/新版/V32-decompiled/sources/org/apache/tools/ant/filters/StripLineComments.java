package org.apache.tools.ant.filters;

import java.io.IOException;
import java.io.Reader;
import java.util.Vector;
import org.apache.tools.ant.types.Parameter;

/* JADX INFO: loaded from: classes3.dex */
public final class StripLineComments extends BaseParamFilterReader implements ChainableReader {
    private static final String COMMENTS_KEY = "comment";
    private Vector<String> comments;
    private String line;

    public StripLineComments() {
        this.comments = new Vector<>();
        this.line = null;
    }

    public StripLineComments(Reader reader) {
        super(reader);
        this.comments = new Vector<>();
        this.line = null;
    }

    @Override // java.io.FilterReader, java.io.Reader
    public int read() throws IOException {
        if (!getInitialized()) {
            initialize();
            setInitialized(true);
        }
        String str = this.line;
        if (str != null) {
            char cCharAt = str.charAt(0);
            if (this.line.length() == 1) {
                this.line = null;
                return cCharAt;
            }
            this.line = this.line.substring(1);
            return cCharAt;
        }
        this.line = readLine();
        int size = this.comments.size();
        while (this.line != null) {
            int i = 0;
            while (true) {
                if (i >= size) {
                    break;
                }
                if (this.line.startsWith(this.comments.elementAt(i))) {
                    this.line = null;
                    break;
                }
                i++;
            }
            if (this.line != null) {
                break;
            }
            this.line = readLine();
        }
        if (this.line != null) {
            return read();
        }
        return -1;
    }

    public void addConfiguredComment(Comment comment) {
        this.comments.addElement(comment.getValue());
    }

    private void setComments(Vector<String> vector) {
        this.comments = vector;
    }

    private Vector<String> getComments() {
        return this.comments;
    }

    @Override // org.apache.tools.ant.filters.ChainableReader
    public Reader chain(Reader reader) {
        StripLineComments stripLineComments = new StripLineComments(reader);
        stripLineComments.setComments(getComments());
        stripLineComments.setInitialized(true);
        return stripLineComments;
    }

    private void initialize() {
        Parameter[] parameters = getParameters();
        if (parameters != null) {
            for (int i = 0; i < parameters.length; i++) {
                if (COMMENTS_KEY.equals(parameters[i].getType())) {
                    this.comments.addElement(parameters[i].getValue());
                }
            }
        }
    }

    public static class Comment {
        private String value;

        public final void setValue(String str) {
            if (this.value != null) {
                throw new IllegalStateException("Comment value already set.");
            }
            this.value = str;
        }

        public final String getValue() {
            return this.value;
        }

        public void addText(String str) {
            setValue(str);
        }
    }
}
