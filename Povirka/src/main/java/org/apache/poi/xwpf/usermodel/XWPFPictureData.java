package org.apache.poi.xwpf.usermodel;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.POIXMLException;
import org.apache.poi.POIXMLRelation;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.util.IOUtils;

/* JADX INFO: loaded from: classes.dex */
public class XWPFPictureData extends POIXMLDocumentPart {
    protected static final POIXMLRelation[] RELATIONS;
    private Long checksum;

    static {
        POIXMLRelation[] pOIXMLRelationArr = new POIXMLRelation[13];
        RELATIONS = pOIXMLRelationArr;
        pOIXMLRelationArr[2] = XWPFRelation.IMAGE_EMF;
        pOIXMLRelationArr[3] = XWPFRelation.IMAGE_WMF;
        pOIXMLRelationArr[4] = XWPFRelation.IMAGE_PICT;
        pOIXMLRelationArr[5] = XWPFRelation.IMAGE_JPEG;
        pOIXMLRelationArr[6] = XWPFRelation.IMAGE_PNG;
        pOIXMLRelationArr[7] = XWPFRelation.IMAGE_DIB;
        pOIXMLRelationArr[8] = XWPFRelation.IMAGE_GIF;
        pOIXMLRelationArr[9] = XWPFRelation.IMAGE_TIFF;
        pOIXMLRelationArr[10] = XWPFRelation.IMAGE_EPS;
        pOIXMLRelationArr[11] = XWPFRelation.IMAGE_BMP;
        pOIXMLRelationArr[12] = XWPFRelation.IMAGE_WPG;
    }

    protected XWPFPictureData() {
    }

    public XWPFPictureData(PackagePart part) {
        super(part);
    }

    @Override // org.apache.poi.POIXMLDocumentPart
    protected void onDocumentRead() throws IOException {
        super.onDocumentRead();
    }

    public byte[] getData() {
        try {
            return IOUtils.toByteArray(getPackagePart().getInputStream());
        } catch (IOException e) {
            throw new POIXMLException(e);
        }
    }

    public String getFileName() {
        String name = getPackagePart().getPartName().getName();
        return name.substring(name.lastIndexOf(47) + 1);
    }

    public String suggestFileExtension() {
        return getPackagePart().getPartName().getExtension();
    }

    public int getPictureType() {
        String contentType = getPackagePart().getContentType();
        int i = 0;
        while (true) {
            POIXMLRelation[] pOIXMLRelationArr = RELATIONS;
            if (i < pOIXMLRelationArr.length) {
                if (pOIXMLRelationArr[i] == null || !pOIXMLRelationArr[i].getContentType().equals(contentType)) {
                    i++;
                } else {
                    return i;
                }
            } else {
                return 0;
            }
        }
    }

    public Long getChecksum() throws IOException {
        if (this.checksum == null) {
            InputStream is = null;
            try {
                is = getPackagePart().getInputStream();
                byte[] data = IOUtils.toByteArray(is);
                IOUtils.closeQuietly(is);
                this.checksum = Long.valueOf(IOUtils.calculateChecksum(data));
            } catch (IOException e) {
                try {
                    throw new POIXMLException(e);
                } catch (Throwable th) {
                    e = th;
                    IOUtils.closeQuietly(is);
                    throw e;
                }
            } catch (Throwable th2) {
                e = th2;
                IOUtils.closeQuietly(is);
                throw e;
            }
        }
        return this.checksum;
    }

    public boolean equals(Object obj) throws IOException {
        if (obj == this) {
            return true;
        }
        if (obj == null || !(obj instanceof XWPFPictureData)) {
            return false;
        }
        XWPFPictureData picData = (XWPFPictureData) obj;
        PackagePart foreignPackagePart = picData.getPackagePart();
        PackagePart ownPackagePart = getPackagePart();
        if ((foreignPackagePart != null && ownPackagePart == null) || (foreignPackagePart == null && ownPackagePart != null)) {
            return false;
        }
        if (ownPackagePart != null) {
            OPCPackage foreignPackage = foreignPackagePart.getPackage();
            OPCPackage ownPackage = ownPackagePart.getPackage();
            if ((foreignPackage != null && ownPackage == null) || (foreignPackage == null && ownPackage != null)) {
                return false;
            }
            if (ownPackage != null && !ownPackage.equals(foreignPackage)) {
                return false;
            }
        }
        Long foreignChecksum = picData.getChecksum();
        Long localChecksum = getChecksum();
        if (!localChecksum.equals(foreignChecksum)) {
            return false;
        }
        return Arrays.equals(getData(), picData.getData());
    }

    public int hashCode() {
        return getChecksum().hashCode();
    }

    @Override // org.apache.poi.POIXMLDocumentPart
    protected void prepareForCommit() {
    }
}
