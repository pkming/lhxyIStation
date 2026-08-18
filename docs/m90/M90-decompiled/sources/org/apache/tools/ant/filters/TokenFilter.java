package org.apache.tools.ant.filters;

import java.io.IOException;
import java.io.Reader;
import java.util.Enumeration;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.types.RegularExpression;
import org.apache.tools.ant.types.Substitution;
import org.apache.tools.ant.util.LineTokenizer;
import org.apache.tools.ant.util.StringUtils;
import org.apache.tools.ant.util.Tokenizer;
import org.apache.tools.ant.util.regexp.Regexp;
import org.apache.tools.ant.util.regexp.RegexpUtil;

/* JADX INFO: loaded from: classes3.dex */
public class TokenFilter extends BaseFilterReader implements ChainableReader {
    private String delimOutput;
    private Vector<Filter> filters;
    private String line;
    private int linePos;
    private Tokenizer tokenizer;

    public static class FileTokenizer extends org.apache.tools.ant.util.FileTokenizer {
    }

    public interface Filter {
        String filter(String str);
    }

    public static class StringTokenizer extends org.apache.tools.ant.util.StringTokenizer {
    }

    public TokenFilter() {
        this.filters = new Vector<>();
        this.tokenizer = null;
        this.delimOutput = null;
        this.line = null;
        this.linePos = 0;
    }

    public TokenFilter(Reader reader) {
        super(reader);
        this.filters = new Vector<>();
        this.tokenizer = null;
        this.delimOutput = null;
        this.line = null;
        this.linePos = 0;
    }

    @Override // java.io.FilterReader, java.io.Reader
    public int read() throws IOException {
        if (this.tokenizer == null) {
            this.tokenizer = new LineTokenizer();
        }
        while (true) {
            String str = this.line;
            if (str == null || str.length() == 0) {
                String token = this.tokenizer.getToken(this.in);
                this.line = token;
                if (token == null) {
                    return -1;
                }
                Enumeration<Filter> enumerationElements = this.filters.elements();
                while (enumerationElements.hasMoreElements()) {
                    String strFilter = enumerationElements.nextElement().filter(this.line);
                    this.line = strFilter;
                    if (strFilter == null) {
                        break;
                    }
                }
                this.linePos = 0;
                if (this.line != null && this.tokenizer.getPostToken().length() != 0) {
                    if (this.delimOutput != null) {
                        this.line += this.delimOutput;
                    } else {
                        this.line += this.tokenizer.getPostToken();
                    }
                }
            } else {
                char cCharAt = this.line.charAt(this.linePos);
                int i = this.linePos + 1;
                this.linePos = i;
                if (i == this.line.length()) {
                    this.line = null;
                }
                return cCharAt;
            }
        }
    }

    @Override // org.apache.tools.ant.filters.ChainableReader
    public final Reader chain(Reader reader) {
        TokenFilter tokenFilter = new TokenFilter(reader);
        tokenFilter.filters = this.filters;
        tokenFilter.tokenizer = this.tokenizer;
        tokenFilter.delimOutput = this.delimOutput;
        tokenFilter.setProject(getProject());
        return tokenFilter;
    }

    public void setDelimOutput(String str) {
        this.delimOutput = resolveBackSlash(str);
    }

    public void addLineTokenizer(LineTokenizer lineTokenizer) {
        add(lineTokenizer);
    }

    public void addStringTokenizer(StringTokenizer stringTokenizer) {
        add(stringTokenizer);
    }

    public void addFileTokenizer(FileTokenizer fileTokenizer) {
        add(fileTokenizer);
    }

    public void add(Tokenizer tokenizer) {
        if (this.tokenizer != null) {
            throw new BuildException("Only one tokenizer allowed");
        }
        this.tokenizer = tokenizer;
    }

    public void addReplaceString(ReplaceString replaceString) {
        this.filters.addElement(replaceString);
    }

    public void addContainsString(ContainsString containsString) {
        this.filters.addElement(containsString);
    }

    public void addReplaceRegex(ReplaceRegex replaceRegex) {
        this.filters.addElement(replaceRegex);
    }

    public void addContainsRegex(ContainsRegex containsRegex) {
        this.filters.addElement(containsRegex);
    }

    public void addTrim(Trim trim) {
        this.filters.addElement(trim);
    }

    public void addIgnoreBlank(IgnoreBlank ignoreBlank) {
        this.filters.addElement(ignoreBlank);
    }

    public void addDeleteCharacters(DeleteCharacters deleteCharacters) {
        this.filters.addElement(deleteCharacters);
    }

    public void add(Filter filter) {
        this.filters.addElement(filter);
    }

    public static abstract class ChainableReaderFilter extends ProjectComponent implements ChainableReader, Filter {
        private boolean byLine = true;

        public void setByLine(boolean z) {
            this.byLine = z;
        }

        @Override // org.apache.tools.ant.filters.ChainableReader
        public Reader chain(Reader reader) {
            TokenFilter tokenFilter = new TokenFilter(reader);
            if (!this.byLine) {
                tokenFilter.add(new FileTokenizer());
            }
            tokenFilter.add(this);
            return tokenFilter;
        }
    }

    public static class ReplaceString extends ChainableReaderFilter {
        private String from;
        private String to;

        public void setFrom(String str) {
            this.from = str;
        }

        public void setTo(String str) {
            this.to = str;
        }

        @Override // org.apache.tools.ant.filters.TokenFilter.Filter
        public String filter(String str) {
            if (this.from == null) {
                throw new BuildException("Missing from in stringreplace");
            }
            StringBuffer stringBuffer = new StringBuffer();
            int length = 0;
            int iIndexOf = str.indexOf(this.from);
            while (iIndexOf >= 0) {
                if (iIndexOf > length) {
                    stringBuffer.append(str.substring(length, iIndexOf));
                }
                String str2 = this.to;
                if (str2 != null) {
                    stringBuffer.append(str2);
                }
                length = this.from.length() + iIndexOf;
                iIndexOf = str.indexOf(this.from, length);
            }
            if (str.length() > length) {
                stringBuffer.append(str.substring(length, str.length()));
            }
            return stringBuffer.toString();
        }
    }

    public static class ContainsString extends ProjectComponent implements Filter {
        private String contains;

        public void setContains(String str) {
            this.contains = str;
        }

        @Override // org.apache.tools.ant.filters.TokenFilter.Filter
        public String filter(String str) {
            String str2 = this.contains;
            if (str2 == null) {
                throw new BuildException("Missing contains in containsstring");
            }
            if (str.indexOf(str2) > -1) {
                return str;
            }
            return null;
        }
    }

    public static class ReplaceRegex extends ChainableReaderFilter {
        private String from;
        private int options;
        private Regexp regexp;
        private RegularExpression regularExpression;
        private Substitution substitution;
        private String to;
        private boolean initialized = false;
        private String flags = "";

        public void setPattern(String str) {
            this.from = str;
        }

        public void setReplace(String str) {
            this.to = str;
        }

        public void setFlags(String str) {
            this.flags = str;
        }

        private void initialize() {
            if (this.initialized) {
                return;
            }
            this.options = TokenFilter.convertRegexOptions(this.flags);
            if (this.from == null) {
                throw new BuildException("Missing pattern in replaceregex");
            }
            RegularExpression regularExpression = new RegularExpression();
            this.regularExpression = regularExpression;
            regularExpression.setPattern(this.from);
            this.regexp = this.regularExpression.getRegexp(getProject());
            if (this.to == null) {
                this.to = "";
            }
            Substitution substitution = new Substitution();
            this.substitution = substitution;
            substitution.setExpression(this.to);
        }

        @Override // org.apache.tools.ant.filters.TokenFilter.Filter
        public String filter(String str) {
            initialize();
            return !this.regexp.matches(str, this.options) ? str : this.regexp.substitute(str, this.substitution.getExpression(getProject()), this.options);
        }
    }

    public static class ContainsRegex extends ChainableReaderFilter {
        private String from;
        private int options;
        private Regexp regexp;
        private RegularExpression regularExpression;
        private Substitution substitution;
        private String to;
        private boolean initialized = false;
        private String flags = "";

        public void setPattern(String str) {
            this.from = str;
        }

        public void setReplace(String str) {
            this.to = str;
        }

        public void setFlags(String str) {
            this.flags = str;
        }

        private void initialize() {
            if (this.initialized) {
                return;
            }
            this.options = TokenFilter.convertRegexOptions(this.flags);
            if (this.from == null) {
                throw new BuildException("Missing from in containsregex");
            }
            RegularExpression regularExpression = new RegularExpression();
            this.regularExpression = regularExpression;
            regularExpression.setPattern(this.from);
            this.regexp = this.regularExpression.getRegexp(getProject());
            if (this.to == null) {
                return;
            }
            Substitution substitution = new Substitution();
            this.substitution = substitution;
            substitution.setExpression(this.to);
        }

        @Override // org.apache.tools.ant.filters.TokenFilter.Filter
        public String filter(String str) {
            initialize();
            if (!this.regexp.matches(str, this.options)) {
                return null;
            }
            Substitution substitution = this.substitution;
            return substitution == null ? str : this.regexp.substitute(str, substitution.getExpression(getProject()), this.options);
        }
    }

    public static class Trim extends ChainableReaderFilter {
        @Override // org.apache.tools.ant.filters.TokenFilter.Filter
        public String filter(String str) {
            return str.trim();
        }
    }

    public static class IgnoreBlank extends ChainableReaderFilter {
        @Override // org.apache.tools.ant.filters.TokenFilter.Filter
        public String filter(String str) {
            if (str.trim().length() == 0) {
                return null;
            }
            return str;
        }
    }

    public static class DeleteCharacters extends ProjectComponent implements Filter, ChainableReader {
        private String deleteChars = "";

        public void setChars(String str) {
            this.deleteChars = TokenFilter.resolveBackSlash(str);
        }

        @Override // org.apache.tools.ant.filters.TokenFilter.Filter
        public String filter(String str) {
            StringBuffer stringBuffer = new StringBuffer(str.length());
            for (int i = 0; i < str.length(); i++) {
                char cCharAt = str.charAt(i);
                if (!isDeleteCharacter(cCharAt)) {
                    stringBuffer.append(cCharAt);
                }
            }
            return stringBuffer.toString();
        }

        @Override // org.apache.tools.ant.filters.ChainableReader
        public Reader chain(Reader reader) {
            return new BaseFilterReader(reader) { // from class: org.apache.tools.ant.filters.TokenFilter.DeleteCharacters.1
                @Override // java.io.FilterReader, java.io.Reader
                public int read() throws IOException {
                    int i;
                    do {
                        i = this.in.read();
                        if (i == -1) {
                            return i;
                        }
                    } while (DeleteCharacters.this.isDeleteCharacter((char) i));
                    return i;
                }
            };
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean isDeleteCharacter(char c) {
            for (int i = 0; i < this.deleteChars.length(); i++) {
                if (this.deleteChars.charAt(i) == c) {
                    return true;
                }
            }
            return false;
        }
    }

    public static String resolveBackSlash(String str) {
        return StringUtils.resolveBackSlash(str);
    }

    public static int convertRegexOptions(String str) {
        return RegexpUtil.asOptions(str);
    }
}
