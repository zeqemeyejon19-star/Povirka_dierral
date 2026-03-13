package org.apache.poi.sl.draw.binding;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.poi.xssf.usermodel.XSSFRelation;

/* JADX INFO: loaded from: classes.dex */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CT_Scale2D", namespace = XSSFRelation.NS_DRAWINGML, propOrder = {"sx", "sy"})
public class CTScale2D {

    @XmlElement(namespace = XSSFRelation.NS_DRAWINGML, required = true)
    protected CTRatio sx;

    @XmlElement(namespace = XSSFRelation.NS_DRAWINGML, required = true)
    protected CTRatio sy;

    public CTRatio getSx() {
        return this.sx;
    }

    public void setSx(CTRatio value) {
        this.sx = value;
    }

    public boolean isSetSx() {
        return this.sx != null;
    }

    public CTRatio getSy() {
        return this.sy;
    }

    public void setSy(CTRatio value) {
        this.sy = value;
    }

    public boolean isSetSy() {
        return this.sy != null;
    }
}
