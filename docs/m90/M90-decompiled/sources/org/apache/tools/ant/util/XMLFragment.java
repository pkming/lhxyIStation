package org.apache.tools.ant.util;

import org.apache.tools.ant.DynamicConfiguratorNS;
import org.apache.tools.ant.DynamicElementNS;
import org.apache.tools.ant.ProjectComponent;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: classes3.dex */
public class XMLFragment extends ProjectComponent implements DynamicElementNS {
    private Document doc;
    private DocumentFragment fragment;

    public XMLFragment() {
        Document documentNewDocument = JAXPUtils.getDocumentBuilder().newDocument();
        this.doc = documentNewDocument;
        this.fragment = documentNewDocument.createDocumentFragment();
    }

    public DocumentFragment getFragment() {
        return this.fragment;
    }

    public void addText(String str) {
        addText(this.fragment, str);
    }

    @Override // org.apache.tools.ant.DynamicElementNS
    public Object createDynamicElement(String str, String str2, String str3) {
        Element elementCreateElementNS;
        if (str.equals("")) {
            elementCreateElementNS = this.doc.createElement(str2);
        } else {
            elementCreateElementNS = this.doc.createElementNS(str, str3);
        }
        this.fragment.appendChild(elementCreateElementNS);
        return new Child(elementCreateElementNS);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addText(Node node, String str) {
        String strReplaceProperties = getProject().replaceProperties(str);
        if (strReplaceProperties == null || strReplaceProperties.trim().equals("")) {
            return;
        }
        node.appendChild(this.doc.createTextNode(strReplaceProperties.trim()));
    }

    public class Child implements DynamicConfiguratorNS {
        private Element e;

        Child(Element element) {
            this.e = element;
        }

        public void addText(String str) {
            XMLFragment.this.addText(this.e, str);
        }

        @Override // org.apache.tools.ant.DynamicAttributeNS
        public void setDynamicAttribute(String str, String str2, String str3, String str4) {
            if (str.equals("")) {
                this.e.setAttribute(str2, str4);
            } else {
                this.e.setAttributeNS(str, str3, str4);
            }
        }

        @Override // org.apache.tools.ant.DynamicElementNS
        public Object createDynamicElement(String str, String str2, String str3) {
            Element elementCreateElement = str.equals("") ? XMLFragment.this.doc.createElement(str2) : XMLFragment.this.doc.createElementNS(str, str3);
            this.e.appendChild(elementCreateElement);
            return XMLFragment.this.new Child(elementCreateElement);
        }
    }
}
