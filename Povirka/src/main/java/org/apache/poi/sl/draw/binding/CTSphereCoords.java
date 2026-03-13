package org.apache.poi.sl.draw.binding;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import org.apache.poi.xssf.usermodel.XSSFRelation;

/* JADX INFO: loaded from: classes.dex */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CT_SphereCoords", namespace = XSSFRelation.NS_DRAWINGML)
public class CTSphereCoords {

    @XmlAttribute(required = true)
    protected int lat;

    @XmlAttribute(required = true)
    protected int lon;

    @XmlAttribute(required = true)
    protected int rev;

    public int getLat() {
        return this.lat;
    }

    public void setLat(int value) {
        this.lat = value;
    }

    public boolean isSetLat() {
        return true;
    }

    public int getLon() {
        return this.lon;
    }

    public void setLon(int value) {
        this.lon = value;
    }

    public boolean isSetLon() {
        return true;
    }

    public int getRev() {
        return this.rev;
    }

    public void setRev(int value) {
        this.rev = value;
    }

    public boolean isSetRev() {
        return true;
    }
}
