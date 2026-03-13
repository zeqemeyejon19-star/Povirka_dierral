package org.apache.poi.xslf.usermodel;

import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import org.apache.poi.sl.usermodel.FreeformShape;
import org.apache.poi.util.Units;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAdjPoint2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTCustomGeometry2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGeomRect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPath2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPath2DClose;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPath2DCubicBezierTo;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPath2DLineTo;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPath2DMoveTo;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPath2DQuadBezierTo;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.presentationml.x2006.main.CTShape;
import org.openxmlformats.schemas.presentationml.x2006.main.CTShapeNonVisual;

/* JADX INFO: loaded from: classes.dex */
public class XSLFFreeformShape extends XSLFAutoShape implements FreeformShape<XSLFShape, XSLFTextParagraph> {
    XSLFFreeformShape(CTShape shape, XSLFSheet sheet) {
        super(shape, sheet);
    }

    @Override // org.apache.poi.sl.usermodel.FreeformShape
    public int setPath(Path2D.Double path) {
        int y0;
        PathIterator it;
        CTPath2D ctPath = CTPath2D.Factory.newInstance();
        Rectangle2D bounds = path.getBounds2D();
        int x0 = Units.toEMU(bounds.getX());
        int y02 = Units.toEMU(bounds.getY());
        PathIterator it2 = path.getPathIterator(new AffineTransform());
        int numPoints = 0;
        ctPath.setH(Units.toEMU(bounds.getHeight()));
        ctPath.setW(Units.toEMU(bounds.getWidth()));
        CTAdjPoint2D mv = null;
        CTAdjPoint2D qp1 = null;
        CTAdjPoint2D qp2 = null;
        CTPath2DQuadBezierTo cTPath2DQuadBezierTo = null;
        CTAdjPoint2D p1 = null;
        CTPath2DCubicBezierTo cTPath2DCubicBezierTo = null;
        CTAdjPoint2D p3 = null;
        CTAdjPoint2D p32 = null;
        CTAdjPoint2D cTAdjPoint2D = null;
        while (!it2.isDone()) {
            double[] vals = new double[6];
            Rectangle2D bounds2 = bounds;
            int type = it2.currentSegment(vals);
            if (type != 0) {
                it = it2;
                if (type == 1) {
                    y0 = y02;
                    CTAdjPoint2D ln = ctPath.addNewLnTo().addNewPt();
                    ln.setX(Integer.valueOf(Units.toEMU(vals[0]) - x0));
                    ln.setY(Integer.valueOf(Units.toEMU(vals[1]) - y0));
                    numPoints++;
                    mv = mv;
                    qp1 = ln;
                } else if (type != 2) {
                    y0 = y02;
                    if (type != 3) {
                        CTPath2DCubicBezierTo bez = cTPath2DCubicBezierTo;
                        if (type != 4) {
                            throw new IllegalStateException("Unrecognized path segment type: " + type);
                        }
                        CTAdjPoint2D qp22 = qp2;
                        CTPath2DQuadBezierTo qbez = cTPath2DQuadBezierTo;
                        CTAdjPoint2D qp12 = p1;
                        CTAdjPoint2D p12 = p3;
                        numPoints++;
                        ctPath.addNewClose();
                        cTPath2DCubicBezierTo = bez;
                        p3 = p12;
                        p1 = qp12;
                        cTPath2DQuadBezierTo = qbez;
                        qp2 = qp22;
                    } else {
                        CTAdjPoint2D qp23 = qp2;
                        CTPath2DQuadBezierTo qbez2 = cTPath2DQuadBezierTo;
                        CTAdjPoint2D qp13 = p1;
                        CTPath2DCubicBezierTo bez2 = ctPath.addNewCubicBezTo();
                        CTAdjPoint2D p13 = bez2.addNewPt();
                        p13.setX(Integer.valueOf(Units.toEMU(vals[0]) - x0));
                        p13.setY(Integer.valueOf(Units.toEMU(vals[1]) - y0));
                        CTAdjPoint2D p2 = bez2.addNewPt();
                        p2.setX(Integer.valueOf(Units.toEMU(vals[2]) - x0));
                        p2.setY(Integer.valueOf(Units.toEMU(vals[3]) - y0));
                        CTAdjPoint2D p33 = bez2.addNewPt();
                        p33.setX(Integer.valueOf(Units.toEMU(vals[4]) - x0));
                        p33.setY(Integer.valueOf(Units.toEMU(vals[5]) - y0));
                        numPoints += 3;
                        cTAdjPoint2D = p33;
                        p32 = p2;
                        p3 = p13;
                        cTPath2DCubicBezierTo = bez2;
                        p1 = qp13;
                        cTPath2DQuadBezierTo = qbez2;
                        qp2 = qp23;
                    }
                } else {
                    y0 = y02;
                    CTAdjPoint2D ln2 = qp1;
                    CTAdjPoint2D mv2 = mv;
                    CTPath2DQuadBezierTo qbez3 = ctPath.addNewQuadBezTo();
                    CTAdjPoint2D qp14 = qbez3.addNewPt();
                    qp14.setX(Integer.valueOf(Units.toEMU(vals[0]) - x0));
                    qp14.setY(Integer.valueOf(Units.toEMU(vals[1]) - y0));
                    qp2 = qbez3.addNewPt();
                    qp2.setX(Integer.valueOf(Units.toEMU(vals[2]) - x0));
                    qp2.setY(Integer.valueOf(Units.toEMU(vals[3]) - y0));
                    numPoints += 2;
                    cTPath2DQuadBezierTo = qbez3;
                    p1 = qp14;
                    qp1 = ln2;
                    mv = mv2;
                }
            } else {
                y0 = y02;
                it = it2;
                CTAdjPoint2D mv3 = ctPath.addNewMoveTo().addNewPt();
                mv3.setX(Integer.valueOf(Units.toEMU(vals[0]) - x0));
                mv3.setY(Integer.valueOf(Units.toEMU(vals[1]) - y0));
                numPoints++;
                mv = mv3;
            }
            it.next();
            bounds = bounds2;
            it2 = it;
            y02 = y0;
        }
        Rectangle2D bounds3 = bounds;
        CTShapeProperties shapeProperties = getShapeProperties();
        if (!(shapeProperties instanceof CTShapeProperties)) {
            return -1;
        }
        shapeProperties.getCustGeom().getPathLst().setPathArray(new CTPath2D[]{ctPath});
        setAnchor(bounds3);
        return numPoints;
    }

    @Override // org.apache.poi.sl.usermodel.FreeformShape
    public Path2D.Double getPath() {
        int i$;
        CTPath2DMoveTo[] cTPath2DMoveToArr;
        int len$;
        XmlObject xo;
        int i$2;
        CTPath2D spPath;
        CTPath2D[] arr$;
        int len$2;
        Path2D.Double path = new Path2D.Double();
        Rectangle2D bounds = getAnchor();
        XmlObject xo2 = getShapeProperties();
        if (!(xo2 instanceof CTShapeProperties)) {
            return null;
        }
        CTCustomGeometry2D geom = ((CTShapeProperties) xo2).getCustGeom();
        CTPath2D[] arr$2 = geom.getPathLst().getPathArray();
        int len$3 = arr$2.length;
        int i$3 = 0;
        while (i$3 < len$3) {
            CTPath2D spPath2 = arr$2[i$3];
            double scaleW = bounds.getWidth() / Units.toPoints(spPath2.getW());
            double scaleH = bounds.getHeight() / Units.toPoints(spPath2.getH());
            CTPath2DMoveTo[] cTPath2DMoveToArrSelectPath = spPath2.selectPath("*");
            int len$4 = cTPath2DMoveToArrSelectPath.length;
            int i$4 = 0;
            while (i$4 < len$4) {
                CTPath2DMoveTo cTPath2DMoveTo = cTPath2DMoveToArrSelectPath[i$4];
                if (cTPath2DMoveTo instanceof CTPath2DMoveTo) {
                    CTAdjPoint2D pt = cTPath2DMoveTo.getPt();
                    path.moveTo((float) (Units.toPoints(((Long) pt.getX()).longValue()) * scaleW), (float) (Units.toPoints(((Long) pt.getY()).longValue()) * scaleH));
                    i$ = i$4;
                    cTPath2DMoveToArr = cTPath2DMoveToArrSelectPath;
                    len$ = len$4;
                    i$2 = i$3;
                    spPath = spPath2;
                    arr$ = arr$2;
                    len$2 = len$3;
                    xo = xo2;
                } else if (cTPath2DMoveTo instanceof CTPath2DLineTo) {
                    CTAdjPoint2D pt2 = ((CTPath2DLineTo) cTPath2DMoveTo).getPt();
                    path.lineTo((float) Units.toPoints(((Long) pt2.getX()).longValue()), (float) Units.toPoints(((Long) pt2.getY()).longValue()));
                    i$ = i$4;
                    cTPath2DMoveToArr = cTPath2DMoveToArrSelectPath;
                    len$ = len$4;
                    i$2 = i$3;
                    spPath = spPath2;
                    arr$ = arr$2;
                    len$2 = len$3;
                    xo = xo2;
                } else if (cTPath2DMoveTo instanceof CTPath2DQuadBezierTo) {
                    CTPath2DQuadBezierTo bez = (CTPath2DQuadBezierTo) cTPath2DMoveTo;
                    CTAdjPoint2D pt1 = bez.getPtArray(0);
                    CTAdjPoint2D pt22 = bez.getPtArray(1);
                    xo = xo2;
                    i$ = i$4;
                    cTPath2DMoveToArr = cTPath2DMoveToArrSelectPath;
                    len$ = len$4;
                    path.quadTo((float) (Units.toPoints(((Long) pt1.getX()).longValue()) * scaleW), (float) (Units.toPoints(((Long) pt1.getY()).longValue()) * scaleH), (float) (Units.toPoints(((Long) pt22.getX()).longValue()) * scaleW), (float) (Units.toPoints(((Long) pt22.getY()).longValue()) * scaleH));
                    i$2 = i$3;
                    spPath = spPath2;
                    arr$ = arr$2;
                    len$2 = len$3;
                } else {
                    i$ = i$4;
                    cTPath2DMoveToArr = cTPath2DMoveToArrSelectPath;
                    len$ = len$4;
                    xo = xo2;
                    if (cTPath2DMoveTo instanceof CTPath2DCubicBezierTo) {
                        CTPath2DCubicBezierTo bez2 = (CTPath2DCubicBezierTo) cTPath2DMoveTo;
                        CTAdjPoint2D pt12 = bez2.getPtArray(0);
                        CTAdjPoint2D pt23 = bez2.getPtArray(1);
                        CTAdjPoint2D pt3 = bez2.getPtArray(2);
                        i$2 = i$3;
                        spPath = spPath2;
                        arr$ = arr$2;
                        len$2 = len$3;
                        path.curveTo((float) (Units.toPoints(((Long) pt12.getX()).longValue()) * scaleW), (float) (Units.toPoints(((Long) pt12.getY()).longValue()) * scaleH), (float) (Units.toPoints(((Long) pt23.getX()).longValue()) * scaleW), (float) (Units.toPoints(((Long) pt23.getY()).longValue()) * scaleH), (float) (Units.toPoints(((Long) pt3.getX()).longValue()) * scaleW), (float) (Units.toPoints(((Long) pt3.getY()).longValue()) * scaleH));
                    } else {
                        i$2 = i$3;
                        spPath = spPath2;
                        arr$ = arr$2;
                        len$2 = len$3;
                        if (cTPath2DMoveTo instanceof CTPath2DClose) {
                            path.closePath();
                        }
                    }
                }
                i$4 = i$ + 1;
                xo2 = xo;
                cTPath2DMoveToArrSelectPath = cTPath2DMoveToArr;
                len$4 = len$;
                i$3 = i$2;
                spPath2 = spPath;
                arr$2 = arr$;
                len$3 = len$2;
            }
            i$3++;
        }
        AffineTransform at = new AffineTransform();
        at.translate(bounds.getX(), bounds.getY());
        return new Path2D.Double(at.createTransformedShape(path));
    }

    static CTShape prototype(int shapeId) {
        CTShape ct = CTShape.Factory.newInstance();
        CTShapeNonVisual nvSpPr = ct.addNewNvSpPr();
        CTNonVisualDrawingProps cnv = nvSpPr.addNewCNvPr();
        cnv.setName("Freeform " + shapeId);
        cnv.setId(shapeId + 1);
        nvSpPr.addNewCNvSpPr();
        nvSpPr.addNewNvPr();
        CTShapeProperties spPr = ct.addNewSpPr();
        CTCustomGeometry2D geom = spPr.addNewCustGeom();
        geom.addNewAvLst();
        geom.addNewGdLst();
        geom.addNewAhLst();
        geom.addNewCxnLst();
        CTGeomRect rect = geom.addNewRect();
        rect.setR("r");
        rect.setB("b");
        rect.setT("t");
        rect.setL("l");
        geom.addNewPathLst();
        return ct;
    }
}
