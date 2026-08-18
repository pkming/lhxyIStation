package org.apache.tools.ant.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PushbackReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

/* JADX INFO: loaded from: classes3.dex */
public class LayoutPreservingProperties extends Properties {
    private String LS;
    private HashMap keyedPairLines;
    private ArrayList logicalLines;
    private boolean removeComments;

    public LayoutPreservingProperties() {
        this.LS = StringUtils.LINE_SEP;
        this.logicalLines = new ArrayList();
        this.keyedPairLines = new HashMap();
    }

    public LayoutPreservingProperties(Properties properties) {
        super(properties);
        this.LS = StringUtils.LINE_SEP;
        this.logicalLines = new ArrayList();
        this.keyedPairLines = new HashMap();
    }

    public boolean isRemoveComments() {
        return this.removeComments;
    }

    public void setRemoveComments(boolean z) {
        this.removeComments = z;
    }

    @Override // java.util.Properties
    public void load(InputStream inputStream) throws IOException {
        super.load(new ByteArrayInputStream(readLines(inputStream).getBytes("ISO-8859-1")));
    }

    @Override // java.util.Hashtable, java.util.Dictionary, java.util.Map
    public Object put(Object obj, Object obj2) throws NullPointerException {
        Object objPut = super.put(obj, obj2);
        innerSetProperty(obj.toString(), obj2.toString());
        return objPut;
    }

    @Override // java.util.Properties
    public Object setProperty(String str, String str2) throws NullPointerException {
        Object property = super.setProperty(str, str2);
        innerSetProperty(str, str2);
        return property;
    }

    private void innerSetProperty(String str, String str2) {
        String strEscapeValue = escapeValue(str2);
        if (this.keyedPairLines.containsKey(str)) {
            ((Pair) this.logicalLines.get(((Integer) this.keyedPairLines.get(str)).intValue())).setValue(strEscapeValue);
            return;
        }
        String strEscapeName = escapeName(str);
        Pair pair = new Pair(strEscapeName, strEscapeValue);
        pair.setNew(true);
        this.keyedPairLines.put(strEscapeName, new Integer(this.logicalLines.size()));
        this.logicalLines.add(pair);
    }

    @Override // java.util.Hashtable, java.util.Map
    public void clear() {
        super.clear();
        this.keyedPairLines.clear();
        this.logicalLines.clear();
    }

    @Override // java.util.Hashtable, java.util.Dictionary, java.util.Map
    public Object remove(Object obj) {
        Object objRemove = super.remove(obj);
        Integer num = (Integer) this.keyedPairLines.remove(obj);
        if (num != null) {
            if (this.removeComments) {
                removeCommentsEndingAt(num.intValue());
            }
            this.logicalLines.set(num.intValue(), null);
        }
        return objRemove;
    }

    @Override // java.util.Hashtable
    public Object clone() {
        LayoutPreservingProperties layoutPreservingProperties = (LayoutPreservingProperties) super.clone();
        layoutPreservingProperties.keyedPairLines = (HashMap) this.keyedPairLines.clone();
        ArrayList arrayList = (ArrayList) this.logicalLines.clone();
        layoutPreservingProperties.logicalLines = arrayList;
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            LogicalLine logicalLine = (LogicalLine) layoutPreservingProperties.logicalLines.get(i);
            if (logicalLine instanceof Pair) {
                layoutPreservingProperties.logicalLines.set(i, ((Pair) logicalLine).clone());
            }
        }
        return layoutPreservingProperties;
    }

    public void listLines(PrintStream printStream) {
        printStream.println("-- logical lines --");
        for (LogicalLine logicalLine : this.logicalLines) {
            if (logicalLine instanceof Blank) {
                printStream.println("blank:   \"" + logicalLine + "\"");
            } else if (logicalLine instanceof Comment) {
                printStream.println("comment: \"" + logicalLine + "\"");
            } else if (logicalLine instanceof Pair) {
                printStream.println("pair:    \"" + logicalLine + "\"");
            }
        }
    }

    public void saveAs(File file) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        store(fileOutputStream, (String) null);
        fileOutputStream.close();
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x004f  */
    @Override // java.util.Properties
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void store(java.io.OutputStream r7, java.lang.String r8) throws java.io.IOException {
        /*
            Method dump skipped, instruction units count: 244
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tools.ant.util.LayoutPreservingProperties.store(java.io.OutputStream, java.lang.String):void");
    }

    private String readLines(InputStream inputStream) throws IOException {
        Object pair;
        PushbackReader pushbackReader = new PushbackReader(new InputStreamReader(inputStream, "ISO-8859-1"), 1);
        if (this.logicalLines.size() > 0) {
            this.logicalLines.add(new Blank());
        }
        String firstLine = readFirstLine(pushbackReader);
        BufferedReader bufferedReader = new BufferedReader(pushbackReader);
        StringBuffer stringBuffer = new StringBuffer();
        StringBuffer stringBuffer2 = new StringBuffer();
        boolean zRequiresContinuation = false;
        boolean zMatches = false;
        while (firstLine != null) {
            stringBuffer.append(firstLine).append(this.LS);
            if (zRequiresContinuation) {
                firstLine = "\n" + firstLine;
            } else {
                zMatches = firstLine.matches("^( |\t|\f)*(#|!).*");
            }
            if (!zMatches) {
                zRequiresContinuation = requiresContinuation(firstLine);
            }
            stringBuffer2.append(firstLine);
            if (!zRequiresContinuation) {
                if (zMatches) {
                    pair = new Comment(stringBuffer2.toString());
                } else if (stringBuffer2.toString().trim().length() == 0) {
                    pair = new Blank();
                } else {
                    pair = new Pair(stringBuffer2.toString());
                    String strUnescape = unescape(((Pair) pair).getName());
                    if (this.keyedPairLines.containsKey(strUnescape)) {
                        remove(strUnescape);
                    }
                    this.keyedPairLines.put(strUnescape, new Integer(this.logicalLines.size()));
                }
                this.logicalLines.add(pair);
                stringBuffer2.setLength(0);
            }
            firstLine = bufferedReader.readLine();
        }
        return stringBuffer.toString();
    }

    private String readFirstLine(PushbackReader pushbackReader) throws IOException {
        StringBuffer stringBuffer = new StringBuffer(80);
        int i = pushbackReader.read();
        this.LS = StringUtils.LINE_SEP;
        boolean z = false;
        while (true) {
            if (i >= 0) {
                if (z && i != 10) {
                    pushbackReader.unread(i);
                    break;
                }
                if (i == 13) {
                    this.LS = "\r";
                    z = true;
                } else if (i == 10) {
                    this.LS = z ? "\r\n" : "\n";
                } else {
                    stringBuffer.append((char) i);
                }
                i = pushbackReader.read();
            } else {
                break;
            }
        }
        return stringBuffer.toString();
    }

    private boolean requiresContinuation(String str) {
        char[] charArray = str.toCharArray();
        int length = charArray.length - 1;
        while (length > 0 && charArray[length] == '\\') {
            length--;
        }
        return ((charArray.length - length) - 1) % 2 == 1;
    }

    private String unescape(String str) {
        int length = str.length() + 1;
        char[] cArr = new char[length];
        int i = 0;
        str.getChars(0, str.length(), cArr, 0);
        cArr[str.length()] = '\n';
        StringBuffer stringBuffer = new StringBuffer(str.length());
        while (i < length) {
            char c = cArr[i];
            if (c == '\n') {
                break;
            }
            if (c == '\\') {
                i++;
                char c2 = cArr[i];
                if (c2 == 'n') {
                    stringBuffer.append('\n');
                } else if (c2 == 'r') {
                    stringBuffer.append('\r');
                } else if (c2 == 'f') {
                    stringBuffer.append('\f');
                } else if (c2 == 't') {
                    stringBuffer.append('\t');
                } else if (c2 == 'u') {
                    char cUnescapeUnicode = unescapeUnicode(cArr, i + 1);
                    i += 4;
                    stringBuffer.append(cUnescapeUnicode);
                } else {
                    stringBuffer.append(c2);
                }
            } else {
                stringBuffer.append(c);
            }
            i++;
        }
        return stringBuffer.toString();
    }

    private char unescapeUnicode(char[] cArr, int i) {
        return (char) Integer.parseInt(new String(cArr, i, 4), 16);
    }

    private String escapeValue(String str) {
        return escape(str, false);
    }

    private String escapeName(String str) {
        return escape(str, true);
    }

    private String escape(String str, boolean z) {
        if (str == null) {
            return null;
        }
        int length = str.length();
        char[] cArr = new char[length];
        str.getChars(0, str.length(), cArr, 0);
        StringBuffer stringBuffer = new StringBuffer(str.length());
        boolean z2 = true;
        for (int i = 0; i < length; i++) {
            char c = cArr[i];
            if (c != ' ') {
                z2 = false;
            } else if (z || z2) {
                stringBuffer.append("\\");
            }
            int iIndexOf = "\t\f\r\n\\:=#!".indexOf(c);
            if (iIndexOf != -1) {
                stringBuffer.append("\\").append("tfrn\\:=#!".substring(iIndexOf, iIndexOf + 1));
            } else if (c < ' ' || c > '~') {
                stringBuffer.append(escapeUnicode(c));
            } else {
                stringBuffer.append(c);
            }
        }
        return stringBuffer.toString();
    }

    private String escapeUnicode(char c) {
        return "\\" + ((Object) UnicodeUtil.EscapeUnicode(c));
    }

    private void removeCommentsEndingAt(int i) {
        int i2 = i - 1;
        int i3 = i2;
        while (i3 > 0 && (this.logicalLines.get(i3) instanceof Blank)) {
            i3--;
        }
        if (!(this.logicalLines.get(i3) instanceof Comment)) {
            return;
        }
        while (i3 >= 0 && (this.logicalLines.get(i3) instanceof Comment)) {
            i3--;
        }
        while (true) {
            i3++;
            if (i3 > i2) {
                return;
            } else {
                this.logicalLines.set(i3, null);
            }
        }
    }

    private static abstract class LogicalLine {
        private String text;

        public LogicalLine(String str) {
            this.text = str;
        }

        public void setText(String str) {
            this.text = str;
        }

        public String toString() {
            return this.text;
        }
    }

    private static class Blank extends LogicalLine {
        public Blank() {
            super("");
        }
    }

    private class Comment extends LogicalLine {
        public Comment(String str) {
            super(str);
        }
    }

    private static class Pair extends LogicalLine implements Cloneable {
        private boolean added;
        private String name;
        private String value;

        public Pair(String str) {
            super(str);
            parsePair(str);
        }

        public Pair(String str, String str2) {
            this(str + "=" + str2);
        }

        public String getName() {
            return this.name;
        }

        public String getValue() {
            return this.value;
        }

        public void setValue(String str) {
            this.value = str;
            setText(this.name + "=" + str);
        }

        public boolean isNew() {
            return this.added;
        }

        public void setNew(boolean z) {
            this.added = z;
        }

        public Object clone() {
            try {
                return super.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                return null;
            }
        }

        private void parsePair(String str) {
            int iFindFirstSeparator = findFirstSeparator(str);
            if (iFindFirstSeparator == -1) {
                this.name = str;
                this.value = null;
            } else {
                this.name = str.substring(0, iFindFirstSeparator);
                this.value = str.substring(iFindFirstSeparator + 1, str.length());
            }
            this.name = stripStart(this.name, " \t\f");
        }

        private String stripStart(String str, String str2) {
            if (str == null) {
                return null;
            }
            int i = 0;
            while (i < str.length() && str2.indexOf(str.charAt(i)) != -1) {
                i++;
            }
            return i == str.length() ? "" : str.substring(i);
        }

        private int findFirstSeparator(String str) {
            return indexOfAny(str.replaceAll("\\\\\\\\", "__").replaceAll("\\\\=", "__").replaceAll("\\\\:", "__").replaceAll("\\\\ ", "__").replaceAll("\\\\t", "__"), " :=\t");
        }

        private int indexOfAny(String str, String str2) {
            if (str == null || str2 == null) {
                return -1;
            }
            int length = str.length() + 1;
            for (int i = 0; i < str2.length(); i++) {
                int iIndexOf = str.indexOf(str2.charAt(i));
                if (iIndexOf != -1 && iIndexOf < length) {
                    length = iIndexOf;
                }
            }
            if (length == str.length() + 1) {
                return -1;
            }
            return length;
        }
    }
}
