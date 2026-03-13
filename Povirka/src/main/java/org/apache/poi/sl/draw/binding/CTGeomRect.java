package org.apache.poi.sl.draw.binding;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import org.apache.poi.xssf.usermodel.XSSFRelation;

/* JADX INFO: loaded from: classes.dex */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CT_GeomRect", namespace = XSSFRelation.NS_DRAWINGML)
public class CTGeomRect {

    @XmlAttribute(required = true)
    protected String b;

    @XmlAttribute(required = true)
    protected String l;

    @XmlAttribute(required = true)
    protected String r;

    @XmlAttribute(required = true)
    protected String t;

    public String getL() {
        return this.l;
    }

    public void setL(String value) {
        this.l = value;
    }

    public boolean isSetL() {
        return this.l != null;
    }

    public String getT() {
        return this.t;
    }

    public void setT(String value) {
        this.t = value;
    }

    public boolean isSetT() {
        return this.t != null;
    }

    public String getR() {
        return this.r;
    }

    public void setR(String value) {
        this.r = value;
    }

    public boolean isSetR() {
        return this.r != null;
    }

    public String getB() {
        return this.b;
    }

    public void setB(String value) {
        this.b = value;
    }

    public boolean isSetB() {
        return this.b != null;
    }
}
