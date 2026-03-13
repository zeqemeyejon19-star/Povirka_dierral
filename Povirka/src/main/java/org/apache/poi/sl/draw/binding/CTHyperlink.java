package org.apache.poi.sl.draw.binding;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.poi.openxml4j.opc.PackageRelationshipTypes;
import org.apache.poi.xssf.usermodel.XSSFRelation;

/* JADX INFO: loaded from: classes.dex */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CT_Hyperlink", namespace = XSSFRelation.NS_DRAWINGML, propOrder = {"snd", "extLst"})
public class CTHyperlink {

    @XmlAttribute
    protected String action;

    @XmlAttribute
    protected Boolean endSnd;

    @XmlElement(namespace = XSSFRelation.NS_DRAWINGML)
    protected CTOfficeArtExtensionList extLst;

    @XmlAttribute
    protected Boolean highlightClick;

    @XmlAttribute
    protected Boolean history;

    @XmlAttribute(namespace = PackageRelationshipTypes.CORE_PROPERTIES_ECMA376_NS)
    protected String id;

    @XmlAttribute
    protected String invalidUrl;

    @XmlElement(namespace = XSSFRelation.NS_DRAWINGML)
    protected CTEmbeddedWAVAudioFile snd;

    @XmlAttribute
    protected String tgtFrame;

    @XmlAttribute
    protected String tooltip;

    public CTEmbeddedWAVAudioFile getSnd() {
        return this.snd;
    }

    public void setSnd(CTEmbeddedWAVAudioFile value) {
        this.snd = value;
    }

    public boolean isSetSnd() {
        return this.snd != null;
    }

    public CTOfficeArtExtensionList getExtLst() {
        return this.extLst;
    }

    public void setExtLst(CTOfficeArtExtensionList value) {
        this.extLst = value;
    }

    public boolean isSetExtLst() {
        return this.extLst != null;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String value) {
        this.id = value;
    }

    public boolean isSetId() {
        return this.id != null;
    }

    public String getInvalidUrl() {
        String str = this.invalidUrl;
        if (str == null) {
            return "";
        }
        return str;
    }

    public void setInvalidUrl(String value) {
        this.invalidUrl = value;
    }

    public boolean isSetInvalidUrl() {
        return this.invalidUrl != null;
    }

    public String getAction() {
        String str = this.action;
        if (str == null) {
            return "";
        }
        return str;
    }

    public void setAction(String value) {
        this.action = value;
    }

    public boolean isSetAction() {
        return this.action != null;
    }

    public String getTgtFrame() {
        String str = this.tgtFrame;
        if (str == null) {
            return "";
        }
        return str;
    }

    public void setTgtFrame(String value) {
        this.tgtFrame = value;
    }

    public boolean isSetTgtFrame() {
        return this.tgtFrame != null;
    }

    public String getTooltip() {
        String str = this.tooltip;
        if (str == null) {
            return "";
        }
        return str;
    }

    public void setTooltip(String value) {
        this.tooltip = value;
    }

    public boolean isSetTooltip() {
        return this.tooltip != null;
    }

    public boolean isHistory() {
        Boolean bool = this.history;
        if (bool == null) {
            return true;
        }
        return bool.booleanValue();
    }

    public void setHistory(boolean value) {
        this.history = Boolean.valueOf(value);
    }

    public boolean isSetHistory() {
        return this.history != null;
    }

    public void unsetHistory() {
        this.history = null;
    }

    public boolean isHighlightClick() {
        Boolean bool = this.highlightClick;
        if (bool == null) {
            return false;
        }
        return bool.booleanValue();
    }

    public void setHighlightClick(boolean value) {
        this.highlightClick = Boolean.valueOf(value);
    }

    public boolean isSetHighlightClick() {
        return this.highlightClick != null;
    }

    public void unsetHighlightClick() {
        this.highlightClick = null;
    }

    public boolean isEndSnd() {
        Boolean bool = this.endSnd;
        if (bool == null) {
            return false;
        }
        return bool.booleanValue();
    }

    public void setEndSnd(boolean value) {
        this.endSnd = Boolean.valueOf(value);
    }

    public boolean isSetEndSnd() {
        return this.endSnd != null;
    }

    public void unsetEndSnd() {
        this.endSnd = null;
    }
}
