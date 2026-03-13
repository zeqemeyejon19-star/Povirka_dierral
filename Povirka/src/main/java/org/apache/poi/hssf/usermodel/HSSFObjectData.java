package org.apache.poi.hssf.usermodel;

import java.io.IOException;
import org.apache.poi.ddf.DefaultEscherRecordFactory;
import org.apache.poi.ddf.EscherBSERecord;
import org.apache.poi.ddf.EscherClientDataRecord;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.hssf.record.EmbeddedObjectRefSubRecord;
import org.apache.poi.hssf.record.EscherAggregate;
import org.apache.poi.hssf.record.ObjRecord;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.Entry;
import org.apache.poi.ss.usermodel.ObjectData;
import org.apache.poi.util.HexDump;

/* JADX INFO: loaded from: classes.dex */
public final class HSSFObjectData extends HSSFPicture implements ObjectData {
    private final DirectoryEntry _root;

    public HSSFObjectData(EscherContainerRecord spContainer, ObjRecord objRecord, DirectoryEntry _root) {
        super(spContainer, objRecord);
        this._root = _root;
    }

    @Override // org.apache.poi.ss.usermodel.ObjectData
    public String getOLE2ClassName() {
        return findObjectRecord().getOLEClassName();
    }

    @Override // org.apache.poi.ss.usermodel.ObjectData
    public DirectoryEntry getDirectory() throws IOException {
        EmbeddedObjectRefSubRecord subRecord = findObjectRecord();
        int streamId = subRecord.getStreamId().intValue();
        String streamName = "MBD" + HexDump.toHex(streamId);
        Entry entry = this._root.getEntry(streamName);
        if (entry instanceof DirectoryEntry) {
            return (DirectoryEntry) entry;
        }
        throw new IOException("Stream " + streamName + " was not an OLE2 directory");
    }

    @Override // org.apache.poi.ss.usermodel.ObjectData
    public byte[] getObjectData() {
        return findObjectRecord().getObjectData();
    }

    @Override // org.apache.poi.ss.usermodel.ObjectData
    public boolean hasDirectoryEntry() {
        EmbeddedObjectRefSubRecord subRecord = findObjectRecord();
        Integer streamId = subRecord.getStreamId();
        return (streamId == null || streamId.intValue() == 0) ? false : true;
    }

    protected EmbeddedObjectRefSubRecord findObjectRecord() {
        for (Object subRecord : getObjRecord().getSubRecords()) {
            if (subRecord instanceof EmbeddedObjectRefSubRecord) {
                return (EmbeddedObjectRefSubRecord) subRecord;
            }
        }
        throw new IllegalStateException("Object data does not contain a reference to an embedded object OLE2 directory");
    }

    @Override // org.apache.poi.hssf.usermodel.HSSFPicture, org.apache.poi.hssf.usermodel.HSSFSimpleShape, org.apache.poi.hssf.usermodel.HSSFShape
    protected EscherContainerRecord createSpContainer() {
        throw new IllegalStateException("HSSFObjectData cannot be created from scratch");
    }

    @Override // org.apache.poi.hssf.usermodel.HSSFSimpleShape, org.apache.poi.hssf.usermodel.HSSFShape
    protected ObjRecord createObjRecord() {
        throw new IllegalStateException("HSSFObjectData cannot be created from scratch");
    }

    @Override // org.apache.poi.hssf.usermodel.HSSFSimpleShape, org.apache.poi.hssf.usermodel.HSSFShape
    protected void afterRemove(HSSFPatriarch patriarch) {
        throw new IllegalStateException("HSSFObjectData cannot be created from scratch");
    }

    @Override // org.apache.poi.hssf.usermodel.HSSFPicture, org.apache.poi.hssf.usermodel.HSSFSimpleShape, org.apache.poi.hssf.usermodel.HSSFShape
    void afterInsert(HSSFPatriarch patriarch) {
        EscherAggregate agg = patriarch.getBoundAggregate();
        agg.associateShapeToObjRecord(getEscherContainer().getChildById(EscherClientDataRecord.RECORD_ID), getObjRecord());
        EscherBSERecord bse = patriarch.getSheet().getWorkbook().getWorkbook().getBSERecord(getPictureIndex());
        bse.setRef(bse.getRef() + 1);
    }

    @Override // org.apache.poi.hssf.usermodel.HSSFPicture, org.apache.poi.hssf.usermodel.HSSFSimpleShape, org.apache.poi.hssf.usermodel.HSSFShape
    protected HSSFShape cloneShape() {
        EscherContainerRecord spContainer = new EscherContainerRecord();
        byte[] inSp = getEscherContainer().serialize();
        spContainer.fillFields(inSp, 0, new DefaultEscherRecordFactory());
        ObjRecord obj = (ObjRecord) getObjRecord().cloneViaReserialise();
        return new HSSFObjectData(spContainer, obj, this._root);
    }
}
