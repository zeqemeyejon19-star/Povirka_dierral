package org.apache.poi.sl.draw.binding;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.poi.xssf.usermodel.XSSFRelation;

/* JADX INFO: loaded from: classes.dex */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CT_Transform2D", namespace = XSSFRelation.NS_DRAWINGML, propOrder = {"off", "ext"})
public class CTTransform2D {

    @XmlElement(namespace = XSSFRelation.NS_DRAWINGML)
    protected CTPositiveSize2D ext;

    @XmlAttribute
    protected Boolean flipH;

    @XmlAttribute
    protected Boolean flipV;

    @XmlElement(namespace = XSSFRelation.NS_DRAWINGML)
    protected CTPoint2D off;

    @XmlAttribute
    protected Integer rot;

    public CTPoint2D getOff() {
        return this.off;
    }

    public void setOff(CTPoint2D value) {
        this.off = value;
    }

    public boolean isSetOff() {
        return this.off != null;
    }

    public CTPositiveSize2D getExt() {
        return this.ext;
    }

    public void setExt(CTPositiveSize2D value) {
        this.ext = value;
    }

    public boolean isSetExt() {
        return this.ext != null;
    }

    public int getRot() {
        Integer num = this.rot;
        if (num == null) {
            return 0;
        }
        return num.intValue();
    }

    public void setRot(int value) {
        this.rot = Integer.valueOf(value);
    }

    public boolean isSetRot() {
        return this.rot != null;
    }

    public void unsetRot() {
        this.rot = null;
    }

    public boolean isFlipH() {
        Boolean bool = this.flipH;
        if (bool == null) {
            return false;
        }
        return bool.booleanValue();
    }

    public void setFlipH(boolean value) {
        this.flipH = Boolean.valueOf(value);
    }

    public boolean isSetFlipH() {
        return this.flipH != null;
    }

    public void unsetFlipH() {
        this.flipH = null;
    }

    public boolean isFlipV() {
        Boolean bool = this.flipV;
        if (bool == null) {
            return false;
        }
        return bool.booleanValue();
    }

    public void setFlipV(boolean value) {
        this.flipV = Boolean.valueOf(value);
    }

    public boolean isSetFlipV() {
        return this.flipV != null;
    }

    public void unsetFlipV() {
        this.flipV = null;
    }
}
