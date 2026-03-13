package org.apache.poi.xslf.usermodel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import org.apache.poi.POIXMLException;
import org.apache.poi.sl.usermodel.Background;
import org.apache.poi.sl.usermodel.Placeholder;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSolidColorFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTransform2D;
import org.openxmlformats.schemas.presentationml.x2006.main.CTBackground;
import org.openxmlformats.schemas.presentationml.x2006.main.CTBackgroundProperties;

/* JADX INFO: loaded from: classes.dex */
public class XSLFBackground extends XSLFSimpleShape implements Background<XSLFShape, XSLFTextParagraph> {
    XSLFBackground(CTBackground shape, XSLFSheet sheet) {
        super(shape, sheet);
    }

    @Override // org.apache.poi.xslf.usermodel.XSLFSimpleShape, org.apache.poi.sl.usermodel.Shape, org.apache.poi.sl.usermodel.PlaceableShape
    public Rectangle2D getAnchor() {
        Dimension pg = getSheet().getSlideShow().getPageSize();
        return new Rectangle2D.Double(0.0d, 0.0d, pg.getWidth(), pg.getHeight());
    }

    @Override // org.apache.poi.xslf.usermodel.XSLFSimpleShape
    protected CTTransform2D getXfrm(boolean create) {
        return null;
    }

    @Override // org.apache.poi.xslf.usermodel.XSLFSimpleShape, org.apache.poi.xslf.usermodel.XSLFShape, org.apache.poi.sl.usermodel.SimpleShape
    public void setPlaceholder(Placeholder placeholder) {
        throw new POIXMLException("Can't set a placeholder for a background");
    }

    protected CTBackgroundProperties getBgPr(boolean create) {
        CTBackground bg = getXmlObject();
        if (!bg.isSetBgPr() && create) {
            if (bg.isSetBgRef()) {
                bg.unsetBgRef();
            }
            return bg.addNewBgPr();
        }
        return bg.getBgPr();
    }

    @Override // org.apache.poi.xslf.usermodel.XSLFSimpleShape, org.apache.poi.sl.usermodel.SimpleShape
    public void setFillColor(Color color) {
        CTBackgroundProperties bgPr = getBgPr(true);
        if (color == null) {
            if (bgPr.isSetSolidFill()) {
                bgPr.unsetSolidFill();
            }
            if (!bgPr.isSetNoFill()) {
                bgPr.addNewNoFill();
                return;
            }
            return;
        }
        if (bgPr.isSetNoFill()) {
            bgPr.unsetNoFill();
        }
        CTSolidColorFillProperties fill = bgPr.isSetSolidFill() ? bgPr.getSolidFill() : bgPr.addNewSolidFill();
        XSLFColor col = new XSLFColor(fill, getSheet().getTheme(), fill.getSchemeClr());
        col.setColor(color);
    }

    @Override // org.apache.poi.xslf.usermodel.XSLFShape
    protected XmlObject getShapeProperties() {
        CTBackground bg = getXmlObject();
        if (bg.isSetBgPr()) {
            return bg.getBgPr();
        }
        if (bg.isSetBgRef()) {
            return bg.getBgRef();
        }
        return null;
    }
}
