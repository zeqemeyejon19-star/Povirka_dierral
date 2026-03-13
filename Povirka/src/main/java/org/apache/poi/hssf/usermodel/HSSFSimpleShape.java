package org.apache.poi.hssf.usermodel;

import org.apache.poi.ddf.DefaultEscherRecordFactory;
import org.apache.poi.ddf.EscherBoolProperty;
import org.apache.poi.ddf.EscherClientDataRecord;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherOptRecord;
import org.apache.poi.ddf.EscherProperties;
import org.apache.poi.ddf.EscherRGBProperty;
import org.apache.poi.ddf.EscherShapePathProperty;
import org.apache.poi.ddf.EscherSimpleProperty;
import org.apache.poi.ddf.EscherSpRecord;
import org.apache.poi.ddf.EscherTextboxRecord;
import org.apache.poi.hssf.record.CommonObjectDataSubRecord;
import org.apache.poi.hssf.record.EndSubRecord;
import org.apache.poi.hssf.record.EscherAggregate;
import org.apache.poi.hssf.record.ObjRecord;
import org.apache.poi.hssf.record.TextObjectRecord;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.SimpleShape;

/* JADX INFO: loaded from: classes.dex */
public class HSSFSimpleShape extends HSSFShape implements SimpleShape {
    public static final short OBJECT_TYPE_ARC = 19;
    public static final short OBJECT_TYPE_COMBO_BOX = 201;
    public static final short OBJECT_TYPE_COMMENT = 202;
    public static final short OBJECT_TYPE_LINE = 20;
    public static final short OBJECT_TYPE_MICROSOFT_OFFICE_DRAWING = 30;
    public static final short OBJECT_TYPE_OVAL = 3;
    public static final short OBJECT_TYPE_PICTURE = 75;
    public static final short OBJECT_TYPE_RECTANGLE = 1;
    public static final int WRAP_BY_POINTS = 1;
    public static final int WRAP_NONE = 2;
    public static final int WRAP_SQUARE = 0;
    private TextObjectRecord _textObjectRecord;

    public HSSFSimpleShape(EscherContainerRecord spContainer, ObjRecord objRecord, TextObjectRecord textObjectRecord) {
        super(spContainer, objRecord);
        this._textObjectRecord = textObjectRecord;
    }

    public HSSFSimpleShape(EscherContainerRecord spContainer, ObjRecord objRecord) {
        super(spContainer, objRecord);
    }

    public HSSFSimpleShape(HSSFShape parent, HSSFAnchor anchor) {
        super(parent, anchor);
        this._textObjectRecord = createTextObjRecord();
    }

    protected TextObjectRecord getTextObjectRecord() {
        return this._textObjectRecord;
    }

    protected TextObjectRecord createTextObjRecord() {
        TextObjectRecord obj = new TextObjectRecord();
        obj.setHorizontalTextAlignment(2);
        obj.setVerticalTextAlignment(2);
        obj.setTextLocked(true);
        obj.setTextOrientation(0);
        obj.setStr(new HSSFRichTextString(""));
        return obj;
    }

    @Override // org.apache.poi.hssf.usermodel.HSSFShape
    protected EscherContainerRecord createSpContainer() {
        EscherContainerRecord spContainer = new EscherContainerRecord();
        spContainer.setRecordId(EscherContainerRecord.SP_CONTAINER);
        spContainer.setOptions((short) 15);
        EscherSpRecord sp = new EscherSpRecord();
        sp.setRecordId(EscherSpRecord.RECORD_ID);
        sp.setFlags(2560);
        sp.setVersion((short) 2);
        EscherClientDataRecord clientData = new EscherClientDataRecord();
        clientData.setRecordId(EscherClientDataRecord.RECORD_ID);
        clientData.setOptions((short) 0);
        EscherOptRecord optRecord = new EscherOptRecord();
        optRecord.setEscherProperty(new EscherSimpleProperty(EscherProperties.LINESTYLE__LINEDASHING, 0));
        optRecord.setEscherProperty(new EscherBoolProperty(EscherProperties.LINESTYLE__NOLINEDRAWDASH, 524296));
        optRecord.setEscherProperty(new EscherRGBProperty(EscherProperties.FILL__FILLCOLOR, HSSFShape.FILL__FILLCOLOR_DEFAULT));
        optRecord.setEscherProperty(new EscherRGBProperty(EscherProperties.LINESTYLE__COLOR, HSSFShape.LINESTYLE__COLOR_DEFAULT));
        optRecord.setEscherProperty(new EscherBoolProperty(EscherProperties.FILL__NOFILLHITTEST, 65536));
        optRecord.setEscherProperty(new EscherBoolProperty(EscherProperties.LINESTYLE__NOLINEDRAWDASH, 524296));
        optRecord.setEscherProperty(new EscherShapePathProperty(EscherProperties.GEOMETRY__SHAPEPATH, 4));
        optRecord.setEscherProperty(new EscherBoolProperty((short) 959, 524288));
        optRecord.setRecordId(EscherOptRecord.RECORD_ID);
        EscherTextboxRecord escherTextbox = new EscherTextboxRecord();
        escherTextbox.setRecordId(EscherTextboxRecord.RECORD_ID);
        escherTextbox.setOptions((short) 0);
        spContainer.addChildRecord(sp);
        spContainer.addChildRecord(optRecord);
        spContainer.addChildRecord(getAnchor().getEscherAnchor());
        spContainer.addChildRecord(clientData);
        spContainer.addChildRecord(escherTextbox);
        return spContainer;
    }

    @Override // org.apache.poi.hssf.usermodel.HSSFShape
    protected ObjRecord createObjRecord() {
        ObjRecord obj = new ObjRecord();
        CommonObjectDataSubRecord c = new CommonObjectDataSubRecord();
        c.setLocked(true);
        c.setPrintable(true);
        c.setAutofill(true);
        c.setAutoline(true);
        EndSubRecord e = new EndSubRecord();
        obj.addSubRecord(c);
        obj.addSubRecord(e);
        return obj;
    }

    @Override // org.apache.poi.hssf.usermodel.HSSFShape
    protected void afterRemove(HSSFPatriarch patriarch) {
        patriarch.getBoundAggregate().removeShapeToObjRecord(getEscherContainer().getChildById(EscherClientDataRecord.RECORD_ID));
        if (getEscherContainer().getChildById(EscherTextboxRecord.RECORD_ID) != null) {
            patriarch.getBoundAggregate().removeShapeToObjRecord(getEscherContainer().getChildById(EscherTextboxRecord.RECORD_ID));
        }
    }

    public HSSFRichTextString getString() {
        return this._textObjectRecord.getStr();
    }

    public void setString(RichTextString string) {
        if (getShapeType() == 0 || getShapeType() == 20) {
            throw new IllegalStateException("Cannot set text for shape type: " + getShapeType());
        }
        HSSFRichTextString rtr = (HSSFRichTextString) string;
        if (rtr.numFormattingRuns() == 0) {
            rtr.applyFont((short) 0);
        }
        TextObjectRecord txo = getOrCreateTextObjRecord();
        txo.setStr(rtr);
        if (string.getString() != null) {
            setPropertyValue(new EscherSimpleProperty((short) 128, string.getString().hashCode()));
        }
    }

    @Override // org.apache.poi.hssf.usermodel.HSSFShape
    void afterInsert(HSSFPatriarch patriarch) {
        EscherAggregate agg = patriarch.getBoundAggregate();
        agg.associateShapeToObjRecord(getEscherContainer().getChildById(EscherClientDataRecord.RECORD_ID), getObjRecord());
        if (getTextObjectRecord() != null) {
            agg.associateShapeToObjRecord(getEscherContainer().getChildById(EscherTextboxRecord.RECORD_ID), getTextObjectRecord());
        }
    }

    @Override // org.apache.poi.hssf.usermodel.HSSFShape
    protected HSSFShape cloneShape() {
        TextObjectRecord txo = null;
        EscherContainerRecord spContainer = new EscherContainerRecord();
        byte[] inSp = getEscherContainer().serialize();
        spContainer.fillFields(inSp, 0, new DefaultEscherRecordFactory());
        ObjRecord obj = (ObjRecord) getObjRecord().cloneViaReserialise();
        if (getTextObjectRecord() != null && getString() != null && getString().getString() != null) {
            txo = (TextObjectRecord) getTextObjectRecord().cloneViaReserialise();
        }
        return new HSSFSimpleShape(spContainer, obj, txo);
    }

    public int getShapeType() {
        EscherSpRecord spRecord = (EscherSpRecord) getEscherContainer().getChildById(EscherSpRecord.RECORD_ID);
        return spRecord.getShapeType();
    }

    public int getWrapText() {
        EscherSimpleProperty property = (EscherSimpleProperty) getOptRecord().lookup(133);
        if (property == null) {
            return 0;
        }
        return property.getPropertyValue();
    }

    public void setWrapText(int value) {
        setPropertyValue(new EscherSimpleProperty((short) 133, false, false, value));
    }

    public void setShapeType(int value) {
        CommonObjectDataSubRecord cod = (CommonObjectDataSubRecord) getObjRecord().getSubRecords().get(0);
        cod.setObjectType((short) 30);
        EscherSpRecord spRecord = (EscherSpRecord) getEscherContainer().getChildById(EscherSpRecord.RECORD_ID);
        spRecord.setShapeType((short) value);
    }

    private TextObjectRecord getOrCreateTextObjRecord() {
        if (getTextObjectRecord() == null) {
            this._textObjectRecord = createTextObjRecord();
        }
        if (((EscherTextboxRecord) getEscherContainer().getChildById(EscherTextboxRecord.RECORD_ID)) == null) {
            EscherTextboxRecord escherTextbox = new EscherTextboxRecord();
            escherTextbox.setRecordId(EscherTextboxRecord.RECORD_ID);
            escherTextbox.setOptions((short) 0);
            getEscherContainer().addChildRecord(escherTextbox);
            getPatriarch().getBoundAggregate().associateShapeToObjRecord(escherTextbox, this._textObjectRecord);
        }
        return this._textObjectRecord;
    }

    @Override // org.apache.poi.hssf.usermodel.HSSFShape
    public int getShapeId() {
        return super.getShapeId();
    }
}
