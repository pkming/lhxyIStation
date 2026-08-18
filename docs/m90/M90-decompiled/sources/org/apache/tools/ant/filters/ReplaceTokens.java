package org.apache.tools.ant.filters;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Parameter;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.util.FileUtils;

/* JADX INFO: loaded from: classes3.dex */
public final class ReplaceTokens extends BaseParamFilterReader implements ChainableReader {
    private static final char DEFAULT_BEGIN_TOKEN = '@';
    private static final char DEFAULT_END_TOKEN = '@';
    private char beginToken;
    private char endToken;
    private Hashtable<String, String> hash;
    private int queueIndex;
    private String queuedData;
    private String replaceData;
    private int replaceIndex;

    public ReplaceTokens() {
        this.queuedData = null;
        this.replaceData = null;
        this.replaceIndex = -1;
        this.queueIndex = -1;
        this.hash = new Hashtable<>();
        this.beginToken = '@';
        this.endToken = '@';
    }

    public ReplaceTokens(Reader reader) {
        super(reader);
        this.queuedData = null;
        this.replaceData = null;
        this.replaceIndex = -1;
        this.queueIndex = -1;
        this.hash = new Hashtable<>();
        this.beginToken = '@';
        this.endToken = '@';
    }

    private int getNextChar() throws IOException {
        int i = this.queueIndex;
        if (i != -1) {
            String str = this.queuedData;
            this.queueIndex = i + 1;
            char cCharAt = str.charAt(i);
            if (this.queueIndex >= this.queuedData.length()) {
                this.queueIndex = -1;
            }
            return cCharAt;
        }
        return this.in.read();
    }

    @Override // java.io.FilterReader, java.io.Reader
    public int read() throws IOException {
        int nextChar;
        if (!getInitialized()) {
            initialize();
            setInitialized(true);
        }
        int i = this.replaceIndex;
        if (i != -1) {
            String str = this.replaceData;
            this.replaceIndex = i + 1;
            char cCharAt = str.charAt(i);
            if (this.replaceIndex >= this.replaceData.length()) {
                this.replaceIndex = -1;
            }
            return cCharAt;
        }
        int nextChar2 = getNextChar();
        if (nextChar2 != this.beginToken) {
            return nextChar2;
        }
        StringBuffer stringBuffer = new StringBuffer("");
        do {
            nextChar = getNextChar();
            if (nextChar == -1) {
                break;
            }
            stringBuffer.append((char) nextChar);
        } while (nextChar != this.endToken);
        if (nextChar == -1) {
            if (this.queuedData == null || this.queueIndex == -1) {
                this.queuedData = stringBuffer.toString();
            } else {
                this.queuedData = stringBuffer.toString() + this.queuedData.substring(this.queueIndex);
            }
            if (this.queuedData.length() > 0) {
                this.queueIndex = 0;
            } else {
                this.queueIndex = -1;
            }
            return this.beginToken;
        }
        stringBuffer.setLength(stringBuffer.length() - 1);
        String str2 = this.hash.get(stringBuffer.toString());
        if (str2 != null) {
            if (str2.length() > 0) {
                this.replaceData = str2;
                this.replaceIndex = 0;
            }
            return read();
        }
        String str3 = stringBuffer.toString() + this.endToken;
        if (this.queuedData == null || this.queueIndex == -1) {
            this.queuedData = str3;
        } else {
            this.queuedData = str3 + this.queuedData.substring(this.queueIndex);
        }
        this.queueIndex = 0;
        return this.beginToken;
    }

    public void setBeginToken(char c) {
        this.beginToken = c;
    }

    private char getBeginToken() {
        return this.beginToken;
    }

    public void setEndToken(char c) {
        this.endToken = c;
    }

    private char getEndToken() {
        return this.endToken;
    }

    public void setPropertiesResource(Resource resource) {
        makeTokensFromProperties(resource);
    }

    public void addConfiguredToken(Token token) {
        this.hash.put(token.getKey(), token.getValue());
    }

    private Properties getProperties(Resource resource) {
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            try {
                inputStream = resource.getInputStream();
                properties.load(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return properties;
        } finally {
            FileUtils.close(inputStream);
        }
    }

    private void setTokens(Hashtable<String, String> hashtable) {
        this.hash = hashtable;
    }

    private Hashtable<String, String> getTokens() {
        return this.hash;
    }

    @Override // org.apache.tools.ant.filters.ChainableReader
    public Reader chain(Reader reader) {
        ReplaceTokens replaceTokens = new ReplaceTokens(reader);
        replaceTokens.setBeginToken(getBeginToken());
        replaceTokens.setEndToken(getEndToken());
        replaceTokens.setTokens(getTokens());
        replaceTokens.setInitialized(true);
        return replaceTokens;
    }

    private void initialize() {
        Parameter[] parameters = getParameters();
        if (parameters != null) {
            for (int i = 0; i < parameters.length; i++) {
                if (parameters[i] != null) {
                    String type = parameters[i].getType();
                    if ("tokenchar".equals(type)) {
                        String name = parameters[i].getName();
                        String value = parameters[i].getValue();
                        if ("begintoken".equals(name)) {
                            if (value.length() == 0) {
                                throw new BuildException("Begin token cannot be empty");
                            }
                            this.beginToken = parameters[i].getValue().charAt(0);
                        } else if (!"endtoken".equals(name)) {
                            continue;
                        } else {
                            if (value.length() == 0) {
                                throw new BuildException("End token cannot be empty");
                            }
                            this.endToken = parameters[i].getValue().charAt(0);
                        }
                    } else if ("token".equals(type)) {
                        this.hash.put(parameters[i].getName(), parameters[i].getValue());
                    } else if ("propertiesfile".equals(type)) {
                        makeTokensFromProperties(new FileResource(new File(parameters[i].getValue())));
                    }
                }
            }
        }
    }

    private void makeTokensFromProperties(Resource resource) {
        Properties properties = getProperties(resource);
        Enumeration enumerationKeys = properties.keys();
        while (enumerationKeys.hasMoreElements()) {
            String str = (String) enumerationKeys.nextElement();
            this.hash.put(str, properties.getProperty(str));
        }
    }

    public static class Token {
        private String key;
        private String value;

        public final void setKey(String str) {
            this.key = str;
        }

        public final void setValue(String str) {
            this.value = str;
        }

        public final String getKey() {
            return this.key;
        }

        public final String getValue() {
            return this.value;
        }
    }
}
