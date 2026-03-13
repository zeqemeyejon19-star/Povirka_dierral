package org.apache.poi.sl.draw.binding;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import org.apache.poi.xssf.usermodel.XSSFRelation;

/* JADX INFO: loaded from: classes.dex */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CT_Point3D", namespace = XSSFRelation.NS_DRAWINGML)
public class CTPoint3D {

    @XmlAttribute(required = true)
    protected long x;

    @XmlAttribute(required = true)
    protected long y;

    @XmlAttribute(required = true)
    protected long z;

    public long getX() {
        return this.x;
    }

    public void setX(long value) {
        this.x = value;
    }

    public boolean isSetX() {
        return true;
    }

    public long getY() {
        return this.y;
    }

    public void setY(long value) {
        this.y = value;
    }

    public boolean isSetY() {
        return true;
    }

    public long getZ() {
        return this.z;
    }

    public void setZ(long value) {
        this.z = value;
    }

    public boolean isSetZ() {
        return true;
    }
}
