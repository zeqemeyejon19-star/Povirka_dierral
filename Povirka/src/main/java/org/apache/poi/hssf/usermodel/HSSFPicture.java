package org.apache.poi.hssf.usermodel;

import androidx.core.app.FrameMetricsAggregator;
import java.awt.Dimension;
import java.io.ByteArrayInputStream;
import org.apache.poi.ddf.DefaultEscherRecordFactory;
import org.apache.poi.ddf.EscherBSERecord;
import org.apache.poi.ddf.EscherBlipRecord;
import org.apache.poi.ddf.EscherClientDataRecord;
import org.apache.poi.ddf.EscherComplexProperty;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherOptRecord;
import org.apache.poi.ddf.EscherProperties;
import org.apache.poi.ddf.EscherSimpleProperty;
import org.apache.poi.ddf.EscherTextboxRecord;
import org.apache.poi.hssf.model.InternalWorkbook;
import org.apache.poi.hssf.record.CommonObjectDataSubRecord;
import org.apache.poi.hssf.record.EscherAggregate;
import org.apache.poi.hssf.record.ObjRecord;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.util.ImageUtils;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.StringUtil;

/* JADX INFO: loaded from: classes.dex */
public class HSSFPicture extends HSSFSimpleShape implements Picture {
    public static final int PICTURE_TYPE_DIB = 7;
    public static final int PICTURE_TYPE_EMF = 2;
    public static final int PICTURE_TYPE_JPEG = 5;
    public static final int PICTURE_TYPE_PICT = 4;
    public static final int PICTURE_TYPE_PNG = 6;
    public static final int PICTURE_TYPE_WMF = 3;
    private static POILogger logger = POILogFactory.getLogger((Class<?>) HSSFPicture.class);

    public HSSFPicture(EscherContainerRecord spContainer, ObjRecord objRecord) {
        super(spContainer, objRecord);
    }

    public HSSFPicture(HSSFShape parent, HSSFAnchor anchor) {
        super(parent, anchor);
        super.setShapeType(75);
        CommonObjectDataSubRecord cod = (CommonObjectDataSubRecord) getObjRecord().getSubRecords().get(0);
        cod.setObjectType((short) 8);
    }

    public int getPictureIndex() {
        EscherSimpleProperty property = (EscherSimpleProperty) getOptRecord().lookup(260);
        if (property == null) {
            return -1;
        }
        return property.getPropertyValue();
    }

    public void setPictureIndex(int pictureIndex) {
        setPropertyValue(new EscherSimpleProperty(EscherProperties.BLIP__BLIPTODISPLAY, false, true, pictureIndex));
    }

    @Override // org.apache.poi.hssf.usermodel.HSSFSimpleShape, org.apache.poi.hssf.usermodel.HSSFShape
    protected EscherContainerRecord createSpContainer() {
        EscherContainerRecord spContainer = super.createSpContainer();
        EscherOptRecord opt = (EscherOptRecord) spContainer.getChildById(EscherOptRecord.RECORD_ID);
        opt.removeEscherProperty(462);
        opt.removeEscherProperty(FrameMetricsAggregator.EVERY_DURATION);
        spContainer.removeChildRecord(spContainer.getChildById(EscherTextboxRecord.RECORD_ID));
        return spContainer;
    }

    @Override // org.apache.poi.ss.usermodel.Picture
    public void resize() {
        resize(Double.MAX_VALUE);
    }

    @Override // org.apache.poi.ss.usermodel.Picture
    public void resize(double scale) {
        resize(scale, scale);
    }

    @Override // org.apache.poi.ss.usermodel.Picture
    public void resize(double scaleX, double scaleY) {
        HSSFClientAnchor anchor = getClientAnchor();
        anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_DONT_RESIZE);
        HSSFClientAnchor pref = getPreferredSize(scaleX, scaleY);
        int row2 = anchor.getRow1() + (pref.getRow2() - pref.getRow1());
        int col2 = anchor.getCol1() + (pref.getCol2() - pref.getCol1());
        anchor.setCol2((short) col2);
        anchor.setDx2(pref.getDx2());
        anchor.setRow2(row2);
        anchor.setDy2(pref.getDy2());
    }

    @Override // org.apache.poi.ss.usermodel.Picture
    public HSSFClientAnchor getPreferredSize() {
        return getPreferredSize(1.0d);
    }

    public HSSFClientAnchor getPreferredSize(double scale) {
        return getPreferredSize(scale, scale);
    }

    @Override // org.apache.poi.ss.usermodel.Picture
    public HSSFClientAnchor getPreferredSize(double scaleX, double scaleY) {
        ImageUtils.setPreferredSize(this, scaleX, scaleY);
        return getClientAnchor();
    }

    @Override // org.apache.poi.ss.usermodel.Picture
    public Dimension getImageDimension() {
        InternalWorkbook iwb = getPatriarch().getSheet().getWorkbook().getWorkbook();
        EscherBSERecord bse = iwb.getBSERecord(getPictureIndex());
        byte[] data = bse.getBlipRecord().getPicturedata();
        int type = bse.getBlipTypeWin32();
        return ImageUtils.getImageDimension(new ByteArrayInputStream(data), type);
    }

    @Override // org.apache.poi.ss.usermodel.Picture
    public HSSFPictureData getPictureData() {
        int picIdx = getPictureIndex();
        if (picIdx == -1) {
            return null;
        }
        HSSFPatriarch patriarch = getPatriarch();
        for (HSSFShape parent = getParent(); patriarch == null && parent != null; parent = parent.getParent()) {
            patriarch = parent.getPatriarch();
        }
        if (patriarch == null) {
            throw new IllegalStateException("Could not find a patriarch for a HSSPicture");
        }
        InternalWorkbook iwb = patriarch.getSheet().getWorkbook().getWorkbook();
        EscherBSERecord bse = iwb.getBSERecord(picIdx);
        EscherBlipRecord blipRecord = bse.getBlipRecord();
        return new HSSFPictureData(blipRecord);
    }

    @Override // org.apache.poi.hssf.usermodel.HSSFSimpleShape, org.apache.poi.hssf.usermodel.HSSFShape
    void afterInsert(HSSFPatriarch patriarch) {
        EscherAggregate agg = patriarch.getBoundAggregate();
        agg.associateShapeToObjRecord(getEscherContainer().getChildById(EscherClientDataRecord.RECORD_ID), getObjRecord());
        if (getPictureIndex() != -1) {
            EscherBSERecord bse = patriarch.getSheet().getWorkbook().getWorkbook().getBSERecord(getPictureIndex());
            bse.setRef(bse.getRef() + 1);
        }
    }

    public String getFileName() {
        EscherComplexProperty propFile = (EscherComplexProperty) getOptRecord().lookup(261);
        return propFile == null ? "" : StringUtil.getFromUnicodeLE(propFile.getComplexData()).trim();
    }

    public void setFileName(String data) {
        byte[] bytes = StringUtil.getToUnicodeLE(data);
        EscherComplexProperty prop = new EscherComplexProperty(EscherProperties.BLIP__BLIPFILENAME, true, bytes);
        setPropertyValue(prop);
    }

    @Override // org.apache.poi.hssf.usermodel.HSSFSimpleShape
    public void setShapeType(int shapeType) {
        throw new IllegalStateException("Shape type can not be changed in " + getClass().getSimpleName());
    }

    @Override // org.apache.poi.hssf.usermodel.HSSFSimpleShape, org.apache.poi.hssf.usermodel.HSSFShape
    protected HSSFShape cloneShape() {
        EscherContainerRecord spContainer = new EscherContainerRecord();
        byte[] inSp = getEscherContainer().serialize();
        spContainer.fillFields(inSp, 0, new DefaultEscherRecordFactory());
        ObjRecord obj = (ObjRecord) getObjRecord().cloneViaReserialise();
        return new HSSFPicture(spContainer, obj);
    }

    @Override // org.apache.poi.ss.usermodel.Picture
    public HSSFClientAnchor getClientAnchor() {
        HSSFAnchor a = getAnchor();
        if (a instanceof HSSFClientAnchor) {
            return (HSSFClientAnchor) a;
        }
        return null;
    }

    @Override // org.apache.poi.ss.usermodel.Picture
    public HSSFSheet getSheet() {
        return getPatriarch().getSheet();
    }
}
