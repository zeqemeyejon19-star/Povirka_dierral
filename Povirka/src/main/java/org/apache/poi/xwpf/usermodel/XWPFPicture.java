package org.apache.poi.xwpf.usermodel;

import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlipFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.picture.CTPicture;

/* JADX INFO: loaded from: classes.dex */
public class XWPFPicture {
    private CTPicture ctPic;
    private String description;
    private XWPFRun run;

    public XWPFPicture(CTPicture ctPic, XWPFRun run) {
        this.run = run;
        this.ctPic = ctPic;
        this.description = ctPic.getNvPicPr().getCNvPr().getDescr();
    }

    public void setPictureReference(PackageRelationship rel) {
        this.ctPic.getBlipFill().getBlip().setEmbed(rel.getId());
    }

    public CTPicture getCTPicture() {
        return this.ctPic;
    }

    public XWPFPictureData getPictureData() {
        CTBlipFillProperties blipProps = this.ctPic.getBlipFill();
        if (blipProps == null || !blipProps.isSetBlip()) {
            return null;
        }
        String blipId = blipProps.getBlip().getEmbed();
        POIXMLDocumentPart part = this.run.getParent().getPart();
        if (part != null) {
            POIXMLDocumentPart relatedPart = part.getRelationById(blipId);
            if (relatedPart instanceof XWPFPictureData) {
                return (XWPFPictureData) relatedPart;
            }
        }
        return null;
    }

    public String getDescription() {
        return this.description;
    }
}
