package org.apache.poi.hssf.usermodel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public class HSSFPatriarch implements HSSFShapeContainer {
    HSSFSheet sheet;
    List shapes = new ArrayList();
    int x1 = 0;
    int y1 = 0;
    int x2 = 1023;
    int y2 = 255;

    HSSFPatriarch(HSSFSheet hSSFSheet) {
        this.sheet = hSSFSheet;
    }

    public HSSFShapeGroup createGroup(HSSFClientAnchor hSSFClientAnchor) {
        HSSFShapeGroup hSSFShapeGroup = new HSSFShapeGroup(null, hSSFClientAnchor);
        hSSFShapeGroup.anchor = hSSFClientAnchor;
        this.shapes.add(hSSFShapeGroup);
        return hSSFShapeGroup;
    }

    public HSSFSimpleShape createSimpleShape(HSSFClientAnchor hSSFClientAnchor) {
        HSSFSimpleShape hSSFSimpleShape = new HSSFSimpleShape(null, hSSFClientAnchor);
        hSSFSimpleShape.anchor = hSSFClientAnchor;
        this.shapes.add(hSSFSimpleShape);
        return hSSFSimpleShape;
    }

    public HSSFPolygon createPolygon(HSSFClientAnchor hSSFClientAnchor) {
        HSSFPolygon hSSFPolygon = new HSSFPolygon(null, hSSFClientAnchor);
        hSSFPolygon.anchor = hSSFClientAnchor;
        this.shapes.add(hSSFPolygon);
        return hSSFPolygon;
    }

    public HSSFTextbox createTextbox(HSSFClientAnchor hSSFClientAnchor) {
        HSSFTextbox hSSFTextbox = new HSSFTextbox(null, hSSFClientAnchor);
        hSSFTextbox.anchor = hSSFClientAnchor;
        this.shapes.add(hSSFTextbox);
        return hSSFTextbox;
    }

    @Override // org.apache.poi.hssf.usermodel.HSSFShapeContainer
    public List getChildren() {
        return this.shapes;
    }

    public int countOfAllChildren() {
        int size = this.shapes.size();
        Iterator it = this.shapes.iterator();
        while (it.hasNext()) {
            size += ((HSSFShape) it.next()).countOfAllChildren();
        }
        return size;
    }

    public void setCoordinates(int i, int i2, int i3, int i4) {
        this.x1 = i;
        this.y1 = i2;
        this.x2 = i3;
        this.y2 = i4;
    }

    public int getX1() {
        return this.x1;
    }

    public int getY1() {
        return this.y1;
    }

    public int getX2() {
        return this.x2;
    }

    public int getY2() {
        return this.y2;
    }
}
