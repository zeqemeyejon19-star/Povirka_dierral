package org.apache.poi.hssf.usermodel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.poi.ddf.EscherBoolProperty;
import org.apache.poi.ddf.EscherChildAnchorRecord;
import org.apache.poi.ddf.EscherClientAnchorRecord;
import org.apache.poi.ddf.EscherComplexProperty;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherOptRecord;
import org.apache.poi.ddf.EscherProperties;
import org.apache.poi.ddf.EscherProperty;
import org.apache.poi.ddf.EscherRGBProperty;
import org.apache.poi.ddf.EscherSimpleProperty;
import org.apache.poi.ddf.EscherSpRecord;
import org.apache.poi.hssf.record.CommonObjectDataSubRecord;
import org.apache.poi.hssf.record.ObjRecord;
import org.apache.poi.ss.usermodel.Shape;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.StringUtil;

/* JADX INFO: loaded from: classes.dex */
public abstract class HSSFShape implements Shape {
    public static final int FILL__FILLCOLOR_DEFAULT = 134217737;
    public static final int LINESTYLE_DASHDOTDOTSYS = 4;
    public static final int LINESTYLE_DASHDOTGEL = 8;
    public static final int LINESTYLE_DASHDOTSYS = 3;
    public static final int LINESTYLE_DASHGEL = 6;
    public static final int LINESTYLE_DASHSYS = 1;
    public static final int LINESTYLE_DEFAULT = -1;
    public static final int LINESTYLE_DOTGEL = 5;
    public static final int LINESTYLE_DOTSYS = 2;
    public static final int LINESTYLE_LONGDASHDOTDOTGEL = 10;
    public static final int LINESTYLE_LONGDASHDOTGEL = 9;
    public static final int LINESTYLE_LONGDASHGEL = 7;
    public static final int LINESTYLE_NONE = -1;
    public static final int LINESTYLE_SOLID = 0;
    public static final int LINESTYLE__COLOR_DEFAULT = 134217792;
    public static final int LINEWIDTH_DEFAULT = 9525;
    public static final int LINEWIDTH_ONE_PT = 12700;
    private static final POILogger LOG = POILogFactory.getLogger((Class<?>) HSSFShape.class);
    public static final int NO_FILLHITTEST_FALSE = 65536;
    public static final int NO_FILLHITTEST_TRUE = 1114112;
    public static final boolean NO_FILL_DEFAULT = true;
    private final EscherContainerRecord _escherContainer;
    private final ObjRecord _objRecord;
    private final EscherOptRecord _optRecord;
    private HSSFPatriarch _patriarch;
    HSSFAnchor anchor;
    private HSSFShape parent;

    abstract void afterInsert(HSSFPatriarch hSSFPatriarch);

    protected abstract void afterRemove(HSSFPatriarch hSSFPatriarch);

    protected abstract HSSFShape cloneShape();

    protected abstract ObjRecord createObjRecord();

    protected abstract EscherContainerRecord createSpContainer();

    public HSSFShape(EscherContainerRecord spContainer, ObjRecord objRecord) {
        this._escherContainer = spContainer;
        this._objRecord = objRecord;
        this._optRecord = (EscherOptRecord) spContainer.getChildById(EscherOptRecord.RECORD_ID);
        this.anchor = HSSFAnchor.createAnchorFromEscher(spContainer);
    }

    public HSSFShape(HSSFShape parent, HSSFAnchor anchor) {
        this.parent = parent;
        this.anchor = anchor;
        EscherContainerRecord escherContainerRecordCreateSpContainer = createSpContainer();
        this._escherContainer = escherContainerRecordCreateSpContainer;
        this._optRecord = (EscherOptRecord) escherContainerRecordCreateSpContainer.getChildById(EscherOptRecord.RECORD_ID);
        this._objRecord = createObjRecord();
    }

    void setShapeId(int shapeId) {
        EscherSpRecord spRecord = (EscherSpRecord) this._escherContainer.getChildById(EscherSpRecord.RECORD_ID);
        spRecord.setShapeId(shapeId);
        CommonObjectDataSubRecord cod = (CommonObjectDataSubRecord) this._objRecord.getSubRecords().get(0);
        cod.setObjectId((short) (shapeId % 1024));
    }

    int getShapeId() {
        return ((EscherSpRecord) this._escherContainer.getChildById(EscherSpRecord.RECORD_ID)).getShapeId();
    }

    protected EscherContainerRecord getEscherContainer() {
        return this._escherContainer;
    }

    protected ObjRecord getObjRecord() {
        return this._objRecord;
    }

    public EscherOptRecord getOptRecord() {
        return this._optRecord;
    }

    @Override // org.apache.poi.ss.usermodel.Shape
    public HSSFShape getParent() {
        return this.parent;
    }

    @Override // org.apache.poi.ss.usermodel.Shape
    public HSSFAnchor getAnchor() {
        return this.anchor;
    }

    public void setAnchor(HSSFAnchor anchor) {
        int recordId = -1;
        if (this.parent == null) {
            if (anchor instanceof HSSFChildAnchor) {
                throw new IllegalArgumentException("Must use client anchors for shapes directly attached to sheet.");
            }
            EscherClientAnchorRecord anch = (EscherClientAnchorRecord) this._escherContainer.getChildById(EscherClientAnchorRecord.RECORD_ID);
            if (anch != null) {
                for (int i = 0; i < this._escherContainer.getChildRecords().size(); i++) {
                    if (this._escherContainer.getChild(i).getRecordId() == -4080 && i != this._escherContainer.getChildRecords().size() - 1) {
                        recordId = this._escherContainer.getChild(i + 1).getRecordId();
                    }
                }
                this._escherContainer.removeChildRecord(anch);
            }
        } else {
            if (anchor instanceof HSSFClientAnchor) {
                throw new IllegalArgumentException("Must use child anchors for shapes attached to groups.");
            }
            EscherChildAnchorRecord anch2 = (EscherChildAnchorRecord) this._escherContainer.getChildById(EscherChildAnchorRecord.RECORD_ID);
            if (anch2 != null) {
                for (int i2 = 0; i2 < this._escherContainer.getChildRecords().size(); i2++) {
                    if (this._escherContainer.getChild(i2).getRecordId() == -4081 && i2 != this._escherContainer.getChildRecords().size() - 1) {
                        recordId = this._escherContainer.getChild(i2 + 1).getRecordId();
                    }
                }
                this._escherContainer.removeChildRecord(anch2);
            }
        }
        if (-1 == recordId) {
            this._escherContainer.addChildRecord(anchor.getEscherAnchor());
        } else {
            this._escherContainer.addChildBefore(anchor.getEscherAnchor(), recordId);
        }
        this.anchor = anchor;
    }

    public int getLineStyleColor() {
        EscherRGBProperty rgbProperty = (EscherRGBProperty) this._optRecord.lookup(448);
        return rgbProperty == null ? LINESTYLE__COLOR_DEFAULT : rgbProperty.getRgbColor();
    }

    public void setLineStyleColor(int lineStyleColor) {
        setPropertyValue(new EscherRGBProperty(EscherProperties.LINESTYLE__COLOR, lineStyleColor));
    }

    @Override // org.apache.poi.ss.usermodel.Shape
    public void setLineStyleColor(int red, int green, int blue) {
        int lineStyleColor = (blue << 16) | (green << 8) | red;
        setPropertyValue(new EscherRGBProperty(EscherProperties.LINESTYLE__COLOR, lineStyleColor));
    }

    public int getFillColor() {
        EscherRGBProperty rgbProperty = (EscherRGBProperty) this._optRecord.lookup(385);
        return rgbProperty == null ? FILL__FILLCOLOR_DEFAULT : rgbProperty.getRgbColor();
    }

    public void setFillColor(int fillColor) {
        setPropertyValue(new EscherRGBProperty(EscherProperties.FILL__FILLCOLOR, fillColor));
    }

    @Override // org.apache.poi.ss.usermodel.Shape
    public void setFillColor(int red, int green, int blue) {
        int fillColor = (blue << 16) | (green << 8) | red;
        setPropertyValue(new EscherRGBProperty(EscherProperties.FILL__FILLCOLOR, fillColor));
    }

    public int getLineWidth() {
        EscherSimpleProperty property = (EscherSimpleProperty) this._optRecord.lookup(459);
        if (property == null) {
            return 9525;
        }
        return property.getPropertyValue();
    }

    public void setLineWidth(int lineWidth) {
        setPropertyValue(new EscherSimpleProperty(EscherProperties.LINESTYLE__LINEWIDTH, lineWidth));
    }

    public int getLineStyle() {
        EscherSimpleProperty property = (EscherSimpleProperty) this._optRecord.lookup(462);
        if (property == null) {
            return -1;
        }
        return property.getPropertyValue();
    }

    public void setLineStyle(int lineStyle) {
        setPropertyValue(new EscherSimpleProperty(EscherProperties.LINESTYLE__LINEDASHING, lineStyle));
        if (getLineStyle() != 0) {
            setPropertyValue(new EscherSimpleProperty(EscherProperties.LINESTYLE__LINEENDCAPSTYLE, 0));
            if (getLineStyle() == -1) {
                setPropertyValue(new EscherBoolProperty(EscherProperties.LINESTYLE__NOLINEDRAWDASH, 524288));
            } else {
                setPropertyValue(new EscherBoolProperty(EscherProperties.LINESTYLE__NOLINEDRAWDASH, 524296));
            }
        }
    }

    @Override // org.apache.poi.ss.usermodel.Shape
    public boolean isNoFill() {
        EscherBoolProperty property = (EscherBoolProperty) this._optRecord.lookup(447);
        return property == null || property.getPropertyValue() == 1114112;
    }

    @Override // org.apache.poi.ss.usermodel.Shape
    public void setNoFill(boolean noFill) {
        setPropertyValue(new EscherBoolProperty(EscherProperties.FILL__NOFILLHITTEST, noFill ? NO_FILLHITTEST_TRUE : 65536));
    }

    protected void setPropertyValue(EscherProperty property) {
        this._optRecord.setEscherProperty(property);
    }

    public void setFlipVertical(boolean value) {
        EscherSpRecord sp = (EscherSpRecord) getEscherContainer().getChildById(EscherSpRecord.RECORD_ID);
        if (value) {
            sp.setFlags(sp.getFlags() | 128);
        } else {
            sp.setFlags(sp.getFlags() & 2147483519);
        }
    }

    public void setFlipHorizontal(boolean value) {
        EscherSpRecord sp = (EscherSpRecord) getEscherContainer().getChildById(EscherSpRecord.RECORD_ID);
        if (value) {
            sp.setFlags(sp.getFlags() | 64);
        } else {
            sp.setFlags(sp.getFlags() & 2147483583);
        }
    }

    public boolean isFlipVertical() {
        EscherSpRecord sp = (EscherSpRecord) getEscherContainer().getChildById(EscherSpRecord.RECORD_ID);
        return (sp.getFlags() & 128) != 0;
    }

    public boolean isFlipHorizontal() {
        EscherSpRecord sp = (EscherSpRecord) getEscherContainer().getChildById(EscherSpRecord.RECORD_ID);
        return (sp.getFlags() & 64) != 0;
    }

    public int getRotationDegree() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        EscherSimpleProperty property = (EscherSimpleProperty) getOptRecord().lookup(4);
        if (property == null) {
            return 0;
        }
        try {
            LittleEndian.putInt(property.getPropertyValue(), bos);
            return LittleEndian.getShort(bos.toByteArray(), 2);
        } catch (IOException e) {
            LOG.log(7, "can't determine rotation degree", e);
            return 0;
        }
    }

    public void setRotationDegree(short value) {
        setPropertyValue(new EscherSimpleProperty((short) 4, value << 16));
    }

    public int countOfAllChildren() {
        return 1;
    }

    protected void setPatriarch(HSSFPatriarch _patriarch) {
        this._patriarch = _patriarch;
    }

    public HSSFPatriarch getPatriarch() {
        return this._patriarch;
    }

    protected void setParent(HSSFShape parent) {
        this.parent = parent;
    }

    @Override // org.apache.poi.ss.usermodel.Shape
    public String getShapeName() {
        EscherOptRecord eor = getOptRecord();
        if (eor == null) {
            return null;
        }
        EscherProperty ep = eor.lookup(896);
        if (!(ep instanceof EscherComplexProperty)) {
            return null;
        }
        return StringUtil.getFromUnicodeLE(((EscherComplexProperty) ep).getComplexData());
    }
}
