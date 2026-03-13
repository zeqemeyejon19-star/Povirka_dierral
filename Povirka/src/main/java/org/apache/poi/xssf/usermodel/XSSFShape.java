package org.apache.poi.xssf.usermodel;

import org.apache.poi.ss.usermodel.Shape;
import org.apache.poi.util.Removal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLineProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNoFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPresetLineDashProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSRgbColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSolidColorFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.STPresetLineDashVal;

/* JADX INFO: loaded from: classes.dex */
public abstract class XSSFShape implements Shape {

    @Removal(version = "3.19")
    public static final int EMU_PER_PIXEL = 9525;

    @Removal(version = "3.19")
    public static final int EMU_PER_POINT = 12700;

    @Removal(version = "3.19")
    public static final int PIXEL_DPI = 96;

    @Removal(version = "3.19")
    public static final int POINT_DPI = 72;
    protected XSSFAnchor anchor;
    protected XSSFDrawing drawing;
    protected XSSFShapeGroup parent;

    protected abstract CTShapeProperties getShapeProperties();

    public XSSFDrawing getDrawing() {
        return this.drawing;
    }

    @Override // org.apache.poi.ss.usermodel.Shape
    public XSSFShapeGroup getParent() {
        return this.parent;
    }

    @Override // org.apache.poi.ss.usermodel.Shape
    public XSSFAnchor getAnchor() {
        return this.anchor;
    }

    @Override // org.apache.poi.ss.usermodel.Shape
    public boolean isNoFill() {
        return getShapeProperties().isSetNoFill();
    }

    @Override // org.apache.poi.ss.usermodel.Shape
    public void setNoFill(boolean noFill) {
        CTShapeProperties props = getShapeProperties();
        if (props.isSetPattFill()) {
            props.unsetPattFill();
        }
        if (props.isSetSolidFill()) {
            props.unsetSolidFill();
        }
        props.setNoFill(CTNoFillProperties.Factory.newInstance());
    }

    @Override // org.apache.poi.ss.usermodel.Shape
    public void setFillColor(int red, int green, int blue) {
        CTShapeProperties props = getShapeProperties();
        CTSolidColorFillProperties fill = props.isSetSolidFill() ? props.getSolidFill() : props.addNewSolidFill();
        CTSRgbColor rgb = CTSRgbColor.Factory.newInstance();
        rgb.setVal(new byte[]{(byte) red, (byte) green, (byte) blue});
        fill.setSrgbClr(rgb);
    }

    @Override // org.apache.poi.ss.usermodel.Shape
    public void setLineStyleColor(int red, int green, int blue) {
        CTShapeProperties props = getShapeProperties();
        CTLineProperties ln = props.isSetLn() ? props.getLn() : props.addNewLn();
        CTSolidColorFillProperties fill = ln.isSetSolidFill() ? ln.getSolidFill() : ln.addNewSolidFill();
        CTSRgbColor rgb = CTSRgbColor.Factory.newInstance();
        rgb.setVal(new byte[]{(byte) red, (byte) green, (byte) blue});
        fill.setSrgbClr(rgb);
    }

    public void setLineWidth(double lineWidth) {
        CTShapeProperties props = getShapeProperties();
        CTLineProperties ln = props.isSetLn() ? props.getLn() : props.addNewLn();
        ln.setW((int) (12700.0d * lineWidth));
    }

    public void setLineStyle(int lineStyle) {
        CTShapeProperties props = getShapeProperties();
        CTLineProperties ln = props.isSetLn() ? props.getLn() : props.addNewLn();
        CTPresetLineDashProperties dashStyle = CTPresetLineDashProperties.Factory.newInstance();
        dashStyle.setVal(STPresetLineDashVal.Enum.forInt(lineStyle + 1));
        ln.setPrstDash(dashStyle);
    }
}
