package org.apache.poi.sl.draw.binding;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import org.apache.poi.openxml4j.opc.PackageRelationshipTypes;
import org.apache.poi.xssf.usermodel.XSSFRelation;

/* JADX INFO: loaded from: classes.dex */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CT_EmbeddedWAVAudioFile", namespace = XSSFRelation.NS_DRAWINGML)
public class CTEmbeddedWAVAudioFile {

    @XmlAttribute
    protected Boolean builtIn;

    @XmlAttribute(namespace = PackageRelationshipTypes.CORE_PROPERTIES_ECMA376_NS, required = true)
    protected String embed;

    @XmlAttribute
    protected String name;

    public String getEmbed() {
        return this.embed;
    }

    public void setEmbed(String value) {
        this.embed = value;
    }

    public boolean isSetEmbed() {
        return this.embed != null;
    }

    public String getName() {
        String str = this.name;
        if (str == null) {
            return "";
        }
        return str;
    }

    public void setName(String value) {
        this.name = value;
    }

    public boolean isSetName() {
        return this.name != null;
    }

    public boolean isBuiltIn() {
        Boolean bool = this.builtIn;
        if (bool == null) {
            return false;
        }
        return bool.booleanValue();
    }

    public void setBuiltIn(boolean value) {
        this.builtIn = Boolean.valueOf(value);
    }

    public boolean isSetBuiltIn() {
        return this.builtIn != null;
    }

    public void unsetBuiltIn() {
        this.builtIn = null;
    }
}
