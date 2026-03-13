package org.apache.poi.hssf.usermodel;

import org.apache.poi.ddf.EscherBlipRecord;
import org.apache.poi.openxml4j.opc.ContentTypes;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.util.PngUtils;

/* JADX INFO: loaded from: classes.dex */
public class HSSFPictureData implements PictureData {
    public static final short FORMAT_MASK = -16;
    public static final short MSOBI_DIB = 31360;
    public static final short MSOBI_EMF = 15680;
    public static final short MSOBI_JPEG = 18080;
    public static final short MSOBI_PICT = 21536;
    public static final short MSOBI_PNG = 28160;
    public static final short MSOBI_WMF = 8544;
    private EscherBlipRecord blip;

    public HSSFPictureData(EscherBlipRecord blip) {
        this.blip = blip;
    }

    @Override // org.apache.poi.ss.usermodel.PictureData
    public byte[] getData() {
        byte[] pictureData = this.blip.getPicturedata();
        if (PngUtils.matchesPngHeader(pictureData, 16)) {
            byte[] png = new byte[pictureData.length - 16];
            System.arraycopy(pictureData, 16, png, 0, png.length);
            return png;
        }
        return pictureData;
    }

    public int getFormat() {
        return this.blip.getRecordId() + 4072;
    }

    @Override // org.apache.poi.ss.usermodel.PictureData
    public String suggestFileExtension() {
        switch (this.blip.getRecordId()) {
            case -4070:
                return "emf";
            case -4069:
                return "wmf";
            case -4068:
                return "pict";
            case -4067:
                return ContentTypes.EXTENSION_JPG_2;
            case -4066:
                return ContentTypes.EXTENSION_PNG;
            case -4065:
                return "dib";
            default:
                return "";
        }
    }

    @Override // org.apache.poi.ss.usermodel.PictureData
    public String getMimeType() {
        switch (this.blip.getRecordId()) {
            case -4070:
                return "image/x-emf";
            case -4069:
                return "image/x-wmf";
            case -4068:
                return "image/x-pict";
            case -4067:
                return ContentTypes.IMAGE_JPEG;
            case -4066:
                return ContentTypes.IMAGE_PNG;
            case -4065:
                return "image/bmp";
            default:
                return "image/unknown";
        }
    }

    @Override // org.apache.poi.ss.usermodel.PictureData
    public int getPictureType() {
        switch (this.blip.getRecordId()) {
            case -4070:
                return 2;
            case -4069:
                return 3;
            case -4068:
                return 4;
            case -4067:
                return 5;
            case -4066:
                return 6;
            case -4065:
                return 7;
            default:
                return -1;
        }
    }
}
