package org.apache.poi.hssf.model;

import org.apache.poi.hssf.record.formula.Ptg;

/* JADX INFO: compiled from: FormulaParser.java */
/* JADX INFO: loaded from: classes3.dex */
class Node {
    private Node[] children = new Node[0];
    private int numChild = 0;
    private Ptg value;

    public Node(Ptg ptg) {
        this.value = null;
        this.value = ptg;
    }

    public void setChildren(Node[] nodeArr) {
        this.children = nodeArr;
        this.numChild = nodeArr.length;
    }

    public int getNumChildren() {
        return this.numChild;
    }

    public Node getChild(int i) {
        return this.children[i];
    }

    public Ptg getValue() {
        return this.value;
    }
}
