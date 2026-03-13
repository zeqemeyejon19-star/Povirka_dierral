package org.apache.poi.hssf.usermodel;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.poi.ddf.EscherClientDataRecord;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherOptRecord;
import org.apache.poi.ddf.EscherProperty;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.hssf.record.CommonObjectDataSubRecord;
import org.apache.poi.hssf.record.EmbeddedObjectRefSubRecord;
import org.apache.poi.hssf.record.EscherAggregate;
import org.apache.poi.hssf.record.ObjRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.SubRecord;
import org.apache.poi.hssf.record.TextObjectRecord;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.util.RecordFormatException;

/* JADX INFO: loaded from: classes.dex */
public class HSSFShapeFactory {
    public static void createShapeTree(EscherContainerRecord container, EscherAggregate agg, HSSFShapeContainer out, DirectoryNode root) {
        HSSFShape shape;
        EscherOptRecord optRecord;
        if (container.getRecordId() == -4093) {
            EscherClientDataRecord clientData = (EscherClientDataRecord) ((EscherContainerRecord) container.getChild(0)).getChildById(EscherClientDataRecord.RECORD_ID);
            ObjRecord obj = clientData != null ? (ObjRecord) agg.getShapeToObjMapping().get(clientData) : null;
            HSSFShapeGroup group = new HSSFShapeGroup(container, obj);
            List<EscherContainerRecord> children = container.getChildContainers();
            for (int i = 0; i < children.size(); i++) {
                EscherContainerRecord spContainer = children.get(i);
                if (i != 0) {
                    createShapeTree(spContainer, agg, group, root);
                }
            }
            out.addShape(group);
            return;
        }
        if (container.getRecordId() == -4092) {
            Map<EscherRecord, Record> shapeToObj = agg.getShapeToObjMapping();
            ObjRecord objRecord = null;
            TextObjectRecord txtRecord = null;
            Iterator<EscherRecord> it = container.iterator();
            EscherRecord record = null;
            while (it.hasNext()) {
                record = it.next();
                short recordId = record.getRecordId();
                if (recordId == -4083) {
                    txtRecord = (TextObjectRecord) shapeToObj.get(record);
                } else if (recordId == -4079) {
                    objRecord = (ObjRecord) shapeToObj.get(record);
                }
            }
            if (objRecord == null) {
                throw new RecordFormatException("EscherClientDataRecord can't be found.");
            }
            if (isEmbeddedObject(objRecord)) {
                HSSFObjectData objectData = new HSSFObjectData(container, objRecord, root);
                out.addShape(objectData);
                return;
            }
            CommonObjectDataSubRecord cmo = (CommonObjectDataSubRecord) objRecord.getSubRecords().get(0);
            short objectType = cmo.getObjectType();
            if (objectType == 1) {
                shape = new HSSFSimpleShape(container, objRecord);
            } else if (objectType == 2) {
                shape = new HSSFSimpleShape(container, objRecord, txtRecord);
            } else if (objectType == 6) {
                shape = new HSSFTextbox(container, objRecord, txtRecord);
            } else if (objectType == 8) {
                shape = new HSSFPicture(container, objRecord);
            } else if (objectType == 20) {
                shape = new HSSFCombobox(container, objRecord);
            } else if (objectType == 25) {
                shape = new HSSFComment(container, objRecord, txtRecord, agg.getNoteRecordByObj(objRecord));
            } else if (objectType == 30 && (optRecord = (EscherOptRecord) container.getChildById(EscherOptRecord.RECORD_ID)) != null) {
                EscherProperty property = optRecord.lookup(325);
                shape = property != null ? new HSSFPolygon(container, objRecord, txtRecord) : new HSSFSimpleShape(container, objRecord, txtRecord);
            } else {
                shape = new HSSFSimpleShape(container, objRecord, txtRecord);
            }
            out.addShape(shape);
        }
    }

    private static boolean isEmbeddedObject(ObjRecord obj) {
        for (SubRecord sub : obj.getSubRecords()) {
            if (sub instanceof EmbeddedObjectRefSubRecord) {
                return true;
            }
        }
        return false;
    }
}
