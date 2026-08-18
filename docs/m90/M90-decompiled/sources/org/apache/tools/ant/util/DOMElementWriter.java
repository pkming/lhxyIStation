package org.apache.tools.ant.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/* JADX INFO: loaded from: classes3.dex */
public class DOMElementWriter {
    private static final int HEX = 16;
    private static final String NS = "ns";
    private static final String[] WS_ENTITIES = new String[5];
    private static String lSep;
    protected String[] knownEntities;
    private XmlNamespacePolicy namespacePolicy;
    private int nextPrefix;
    private HashMap nsPrefixMap;
    private HashMap nsURIByElement;
    private boolean xmlDeclaration;

    public boolean isLegalCharacter(char c) {
        if (c == '\t' || c == '\n' || c == '\r') {
            return true;
        }
        if (c < ' ') {
            return false;
        }
        if (c <= 55295) {
            return true;
        }
        return c >= 57344 && c <= 65533;
    }

    static {
        for (int i = 9; i < 14; i++) {
            WS_ENTITIES[i - 9] = "&#x" + Integer.toHexString(i) + ";";
        }
        lSep = System.getProperty("line.separator");
    }

    public static class XmlNamespacePolicy {
        public static final XmlNamespacePolicy IGNORE = new XmlNamespacePolicy(false, false);
        public static final XmlNamespacePolicy ONLY_QUALIFY_ELEMENTS = new XmlNamespacePolicy(true, false);
        public static final XmlNamespacePolicy QUALIFY_ALL = new XmlNamespacePolicy(true, true);
        private boolean qualifyAttributes;
        private boolean qualifyElements;

        public XmlNamespacePolicy(boolean z, boolean z2) {
            this.qualifyElements = z;
            this.qualifyAttributes = z2;
        }
    }

    public DOMElementWriter() {
        this.xmlDeclaration = true;
        this.namespacePolicy = XmlNamespacePolicy.IGNORE;
        this.nsPrefixMap = new HashMap();
        this.nextPrefix = 0;
        this.nsURIByElement = new HashMap();
        this.knownEntities = new String[]{"gt", "amp", "lt", "apos", "quot"};
    }

    public DOMElementWriter(boolean z) {
        this(z, XmlNamespacePolicy.IGNORE);
    }

    public DOMElementWriter(boolean z, XmlNamespacePolicy xmlNamespacePolicy) {
        this.xmlDeclaration = true;
        this.namespacePolicy = XmlNamespacePolicy.IGNORE;
        this.nsPrefixMap = new HashMap();
        this.nextPrefix = 0;
        this.nsURIByElement = new HashMap();
        this.knownEntities = new String[]{"gt", "amp", "lt", "apos", "quot"};
        this.xmlDeclaration = z;
        this.namespacePolicy = xmlNamespacePolicy;
    }

    public void write(Element element, OutputStream outputStream) throws IOException {
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF8");
        writeXMLDeclaration(outputStreamWriter);
        write(element, outputStreamWriter, 0, "  ");
        outputStreamWriter.flush();
    }

    public void writeXMLDeclaration(Writer writer) throws IOException {
        if (this.xmlDeclaration) {
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        }
    }

    public void write(Element element, Writer writer, int i, String str) throws IOException {
        NodeList childNodes = element.getChildNodes();
        boolean z = childNodes.getLength() > 0;
        openElement(element, writer, i, str, z);
        if (z) {
            boolean z2 = false;
            for (int i2 = 0; i2 < childNodes.getLength(); i2++) {
                Node nodeItem = childNodes.item(i2);
                short nodeType = nodeItem.getNodeType();
                if (nodeType == 1) {
                    if (i2 == 0) {
                        writer.write(lSep);
                    }
                    write((Element) nodeItem, writer, i + 1, str);
                    z2 = true;
                } else if (nodeType == 3) {
                    writer.write(encode(nodeItem.getNodeValue()));
                } else if (nodeType == 4) {
                    writer.write("<![CDATA[");
                    encodedata(writer, ((Text) nodeItem).getData());
                    writer.write("]]>");
                } else if (nodeType == 5) {
                    writer.write(38);
                    writer.write(nodeItem.getNodeName());
                    writer.write(59);
                } else if (nodeType == 7) {
                    writer.write("<?");
                    writer.write(nodeItem.getNodeName());
                    String nodeValue = nodeItem.getNodeValue();
                    if (nodeValue != null && nodeValue.length() > 0) {
                        writer.write(32);
                        writer.write(nodeValue);
                    }
                    writer.write("?>");
                } else if (nodeType == 8) {
                    writer.write("<!--");
                    writer.write(encode(nodeItem.getNodeValue()));
                    writer.write("-->");
                }
            }
            closeElement(element, writer, i, str, z2);
        }
    }

    public void openElement(Element element, Writer writer, int i, String str) throws IOException {
        openElement(element, writer, i, str, true);
    }

    public void openElement(Element element, Writer writer, int i, String str, boolean z) throws IOException {
        for (int i2 = 0; i2 < i; i2++) {
            writer.write(str);
        }
        writer.write("<");
        if (this.namespacePolicy.qualifyElements) {
            String namespaceURI = getNamespaceURI(element);
            String string = (String) this.nsPrefixMap.get(namespaceURI);
            if (string == null) {
                if (this.nsPrefixMap.isEmpty()) {
                    string = "";
                } else {
                    StringBuilder sbAppend = new StringBuilder().append(NS);
                    int i3 = this.nextPrefix;
                    this.nextPrefix = i3 + 1;
                    string = sbAppend.append(i3).toString();
                }
                this.nsPrefixMap.put(namespaceURI, string);
                addNSDefinition(element, namespaceURI);
            }
            if (!"".equals(string)) {
                writer.write(string);
                writer.write(":");
            }
        }
        writer.write(element.getTagName());
        NamedNodeMap attributes = element.getAttributes();
        for (int i4 = 0; i4 < attributes.getLength(); i4++) {
            Attr attr = (Attr) attributes.item(i4);
            writer.write(" ");
            if (this.namespacePolicy.qualifyAttributes) {
                String namespaceURI2 = getNamespaceURI(attr);
                String string2 = (String) this.nsPrefixMap.get(namespaceURI2);
                if (string2 == null) {
                    StringBuilder sbAppend2 = new StringBuilder().append(NS);
                    int i5 = this.nextPrefix;
                    this.nextPrefix = i5 + 1;
                    string2 = sbAppend2.append(i5).toString();
                    this.nsPrefixMap.put(namespaceURI2, string2);
                    addNSDefinition(element, namespaceURI2);
                }
                writer.write(string2);
                writer.write(":");
            }
            writer.write(attr.getName());
            writer.write("=\"");
            writer.write(encodeAttributeValue(attr.getValue()));
            writer.write("\"");
        }
        ArrayList<String> arrayList = (ArrayList) this.nsURIByElement.get(element);
        if (arrayList != null) {
            for (String str2 : arrayList) {
                String str3 = (String) this.nsPrefixMap.get(str2);
                writer.write(" xmlns");
                if (!"".equals(str3)) {
                    writer.write(":");
                    writer.write(str3);
                }
                writer.write("=\"");
                writer.write(str2);
                writer.write("\"");
            }
        }
        if (z) {
            writer.write(">");
            return;
        }
        removeNSDefinitions(element);
        writer.write(" />");
        writer.write(lSep);
        writer.flush();
    }

    public void closeElement(Element element, Writer writer, int i, String str, boolean z) throws IOException {
        if (z) {
            for (int i2 = 0; i2 < i; i2++) {
                writer.write(str);
            }
        }
        writer.write("</");
        if (this.namespacePolicy.qualifyElements) {
            String str2 = (String) this.nsPrefixMap.get(getNamespaceURI(element));
            if (str2 != null && !"".equals(str2)) {
                writer.write(str2);
                writer.write(":");
            }
            removeNSDefinitions(element);
        }
        writer.write(element.getTagName());
        writer.write(">");
        writer.write(lSep);
        writer.flush();
    }

    public String encode(String str) {
        return encode(str, false);
    }

    public String encodeAttributeValue(String str) {
        return encode(str, true);
    }

    private String encode(String str, boolean z) {
        int length = str.length();
        StringBuffer stringBuffer = new StringBuffer(length);
        for (int i = 0; i < length; i++) {
            char cCharAt = str.charAt(i);
            if (cCharAt == '\t' || cCharAt == '\n' || cCharAt == '\r') {
                if (z) {
                    stringBuffer.append(WS_ENTITIES[cCharAt - '\t']);
                } else {
                    stringBuffer.append(cCharAt);
                }
            } else if (cCharAt == '\"') {
                stringBuffer.append("&quot;");
            } else if (cCharAt == '<') {
                stringBuffer.append("&lt;");
            } else if (cCharAt == '>') {
                stringBuffer.append("&gt;");
            } else if (cCharAt == '&') {
                stringBuffer.append("&amp;");
            } else if (cCharAt == '\'') {
                stringBuffer.append("&apos;");
            } else if (isLegalCharacter(cCharAt)) {
                stringBuffer.append(cCharAt);
            }
        }
        return stringBuffer.substring(0);
    }

    public String encodedata(String str) {
        StringWriter stringWriter = new StringWriter();
        try {
            encodedata(stringWriter, str);
            return stringWriter.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void encodedata(Writer writer, String str) throws IOException {
        int length = str.length();
        int iIndexOf = str.indexOf("]]>");
        int i = 0;
        while (i < length) {
            int i2 = iIndexOf < 0 ? length : iIndexOf;
            while (i < i2) {
                int i3 = i;
                while (i3 < i2 && isLegalCharacter(str.charAt(i3))) {
                    i3++;
                }
                writer.write(str, i, i3 - i);
                i = i3 + 1;
            }
            if (iIndexOf >= 0) {
                writer.write("]]]]><![CDATA[>");
                int i4 = iIndexOf + 3;
                i = i4;
                iIndexOf = str.indexOf("]]>", i4);
            } else {
                i = i2;
            }
        }
    }

    public boolean isReference(String str) {
        if (str.charAt(0) == '&' && str.endsWith(";")) {
            if (str.charAt(1) == '#') {
                if (str.charAt(2) == 'x') {
                    try {
                        Integer.parseInt(str.substring(3, str.length() - 1), 16);
                        return true;
                    } catch (NumberFormatException unused) {
                        return false;
                    }
                }
                try {
                    Integer.parseInt(str.substring(2, str.length() - 1));
                    return true;
                } catch (NumberFormatException unused2) {
                    return false;
                }
            }
            String strSubstring = str.substring(1, str.length() - 1);
            int i = 0;
            while (true) {
                String[] strArr = this.knownEntities;
                if (i >= strArr.length) {
                    break;
                }
                if (strSubstring.equals(strArr[i])) {
                    return true;
                }
                i++;
            }
        }
        return false;
    }

    private void removeNSDefinitions(Element element) {
        ArrayList arrayList = (ArrayList) this.nsURIByElement.get(element);
        if (arrayList != null) {
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                this.nsPrefixMap.remove(it.next());
            }
            this.nsURIByElement.remove(element);
        }
    }

    private void addNSDefinition(Element element, String str) {
        ArrayList arrayList = (ArrayList) this.nsURIByElement.get(element);
        if (arrayList == null) {
            arrayList = new ArrayList();
            this.nsURIByElement.put(element, arrayList);
        }
        arrayList.add(str);
    }

    private static String getNamespaceURI(Node node) {
        String namespaceURI = node.getNamespaceURI();
        return namespaceURI == null ? "" : namespaceURI;
    }
}
