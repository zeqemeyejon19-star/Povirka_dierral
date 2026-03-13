package org.apache.poi.xdgf.usermodel;

import com.microsoft.schemas.office.visio.x2012.main.ShapeSheetType;
import com.microsoft.schemas.office.visio.x2012.main.TextType;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import org.apache.poi.POIXMLException;
import org.apache.poi.util.Internal;
import org.apache.poi.xdgf.exceptions.XDGFException;
import org.apache.poi.xdgf.usermodel.section.CombinedIterable;
import org.apache.poi.xdgf.usermodel.section.GeometrySection;
import org.apache.poi.xdgf.usermodel.section.XDGFSection;
import org.apache.poi.xdgf.usermodel.shape.ShapeVisitor;
import org.apache.poi.xdgf.usermodel.shape.exceptions.StopVisitingThisBranch;

/* JADX INFO: loaded from: classes.dex */
public class XDGFShape extends XDGFSheet {
    Double _angle;
    Double _beginX;
    Double _beginY;
    Double _endX;
    Double _endY;
    Boolean _flipX;
    Boolean _flipY;
    Double _height;
    Double _locPinX;
    Double _locPinY;
    XDGFMaster _master;
    XDGFShape _masterShape;
    XDGFShape _parent;
    XDGFBaseContents _parentPage;
    Double _pinX;
    Double _pinY;
    Double _rotationXAngle;
    Double _rotationYAngle;
    Double _rotationZAngle;
    List<XDGFShape> _shapes;
    XDGFText _text;
    Double _txtAngle;
    Double _txtHeight;
    Double _txtLocPinX;
    Double _txtLocPinY;
    Double _txtPinX;
    Double _txtPinY;
    Double _txtWidth;
    Double _width;

    public XDGFShape(ShapeSheetType shapeSheet, XDGFBaseContents parentPage, XDGFDocument document) {
        this(null, shapeSheet, parentPage, document);
    }

    public XDGFShape(XDGFShape parent, ShapeSheetType shapeSheet, XDGFBaseContents parentPage, XDGFDocument document) {
        super(shapeSheet, document);
        this._master = null;
        this._masterShape = null;
        this._text = null;
        this._shapes = null;
        this._pinX = null;
        this._pinY = null;
        this._width = null;
        this._height = null;
        this._locPinX = null;
        this._locPinY = null;
        this._beginX = null;
        this._beginY = null;
        this._endX = null;
        this._endY = null;
        this._angle = null;
        this._rotationXAngle = null;
        this._rotationYAngle = null;
        this._rotationZAngle = null;
        this._flipX = null;
        this._flipY = null;
        this._txtPinX = null;
        this._txtPinY = null;
        this._txtLocPinX = null;
        this._txtLocPinY = null;
        this._txtAngle = null;
        this._txtWidth = null;
        this._txtHeight = null;
        this._parent = parent;
        this._parentPage = parentPage;
        TextType text = shapeSheet.getText();
        if (text != null) {
            this._text = new XDGFText(text, this);
        }
        if (shapeSheet.isSetShapes()) {
            this._shapes = new ArrayList();
            ShapeSheetType[] arr$ = shapeSheet.getShapes().getShapeArray();
            for (ShapeSheetType shape : arr$) {
                this._shapes.add(new XDGFShape(this, shape, parentPage, document));
            }
        }
        readProperties();
    }

    public String toString() {
        if (this._parentPage instanceof XDGFMasterContents) {
            return this._parentPage + ": <Shape ID=\"" + getID() + "\">";
        }
        return "<Shape ID=\"" + getID() + "\">";
    }

    protected void readProperties() {
        this._pinX = XDGFCell.maybeGetDouble(this._cells, "PinX");
        this._pinY = XDGFCell.maybeGetDouble(this._cells, "PinY");
        this._width = XDGFCell.maybeGetDouble(this._cells, "Width");
        this._height = XDGFCell.maybeGetDouble(this._cells, "Height");
        this._locPinX = XDGFCell.maybeGetDouble(this._cells, "LocPinX");
        this._locPinY = XDGFCell.maybeGetDouble(this._cells, "LocPinY");
        this._beginX = XDGFCell.maybeGetDouble(this._cells, "BeginX");
        this._beginY = XDGFCell.maybeGetDouble(this._cells, "BeginY");
        this._endX = XDGFCell.maybeGetDouble(this._cells, "EndX");
        this._endY = XDGFCell.maybeGetDouble(this._cells, "EndY");
        this._angle = XDGFCell.maybeGetDouble(this._cells, "Angle");
        this._rotationXAngle = XDGFCell.maybeGetDouble(this._cells, "RotationXAngle");
        this._rotationYAngle = XDGFCell.maybeGetDouble(this._cells, "RotationYAngle");
        this._rotationZAngle = XDGFCell.maybeGetDouble(this._cells, "RotationZAngle");
        this._flipX = XDGFCell.maybeGetBoolean(this._cells, "FlipX");
        this._flipY = XDGFCell.maybeGetBoolean(this._cells, "FlipY");
        this._txtPinX = XDGFCell.maybeGetDouble(this._cells, "TxtPinX");
        this._txtPinY = XDGFCell.maybeGetDouble(this._cells, "TxtPinY");
        this._txtLocPinX = XDGFCell.maybeGetDouble(this._cells, "TxtLocPinX");
        this._txtLocPinY = XDGFCell.maybeGetDouble(this._cells, "TxtLocPinY");
        this._txtWidth = XDGFCell.maybeGetDouble(this._cells, "TxtWidth");
        this._txtHeight = XDGFCell.maybeGetDouble(this._cells, "TxtHeight");
        this._txtAngle = XDGFCell.maybeGetDouble(this._cells, "TxtAngle");
    }

    protected void setupMaster(XDGFPageContents pageContents, XDGFMasterContents master) {
        ShapeSheetType obj = mo24getXmlObject();
        if (obj.isSetMaster()) {
            XDGFMaster masterById = pageContents.getMasterById(obj.getMaster());
            this._master = masterById;
            if (masterById == null) {
                throw XDGFException.error("refers to non-existant master " + obj.getMaster(), this);
            }
            Collection<XDGFShape> masterShapes = masterById.getContent().getTopLevelShapes();
            int size = masterShapes.size();
            if (size == 0) {
                throw XDGFException.error("Could not retrieve master shape from " + this._master, this);
            }
            if (size == 1) {
                this._masterShape = masterShapes.iterator().next();
            }
        } else if (obj.isSetMasterShape()) {
            XDGFShape shapeById = master.getShapeById(obj.getMasterShape());
            this._masterShape = shapeById;
            if (shapeById == null) {
                throw XDGFException.error("refers to non-existant master shape " + obj.getMasterShape(), this);
            }
        }
        setupSectionMasters();
        List<XDGFShape> list = this._shapes;
        if (list != null) {
            for (XDGFShape shape : list) {
                XDGFMaster xDGFMaster = this._master;
                shape.setupMaster(pageContents, xDGFMaster == null ? master : xDGFMaster.getContent());
            }
        }
    }

    protected void setupSectionMasters() {
        if (this._masterShape == null) {
            return;
        }
        try {
            for (Map.Entry<String, XDGFSection> section : this._sections.entrySet()) {
                XDGFSection master = this._masterShape.getSection(section.getKey());
                if (master != null) {
                    section.getValue().setupMaster(master);
                }
            }
            for (Map.Entry<Long, GeometrySection> section2 : this._geometry.entrySet()) {
                GeometrySection master2 = this._masterShape.getGeometryByIdx(section2.getKey().longValue());
                if (master2 != null) {
                    section2.getValue().setupMaster(master2);
                }
            }
        } catch (POIXMLException e) {
            throw XDGFException.wrap(toString(), e);
        }
    }

    @Override // org.apache.poi.xdgf.usermodel.XDGFSheet
    @Internal
    /* JADX INFO: renamed from: getXmlObject, reason: merged with bridge method [inline-methods] */
    public ShapeSheetType mo24getXmlObject() {
        return this._sheet;
    }

    public long getID() {
        return mo24getXmlObject().getID();
    }

    public String getType() {
        return mo24getXmlObject().getType();
    }

    public String getTextAsString() {
        XDGFText text = getText();
        if (text == null) {
            return "";
        }
        return text.getTextContent();
    }

    public boolean hasText() {
        XDGFShape xDGFShape;
        return (this._text == null && ((xDGFShape = this._masterShape) == null || xDGFShape._text == null)) ? false : true;
    }

    @Override // org.apache.poi.xdgf.usermodel.XDGFSheet
    public XDGFCell getCell(String cellName) {
        XDGFShape xDGFShape;
        XDGFCell _cell = super.getCell(cellName);
        if (_cell == null && (xDGFShape = this._masterShape) != null) {
            return xDGFShape.getCell(cellName);
        }
        return _cell;
    }

    public GeometrySection getGeometryByIdx(long idx) {
        return this._geometry.get(Long.valueOf(idx));
    }

    public List<XDGFShape> getShapes() {
        return this._shapes;
    }

    public String getName() {
        String name = mo24getXmlObject().getName();
        if (name == null) {
            return "";
        }
        return name;
    }

    public String getShapeType() {
        String type = mo24getXmlObject().getType();
        if (type == null) {
            return "";
        }
        return type;
    }

    public String getSymbolName() {
        String name;
        XDGFMaster xDGFMaster = this._master;
        return (xDGFMaster == null || (name = xDGFMaster.getName()) == null) ? "" : name;
    }

    public XDGFShape getMasterShape() {
        return this._masterShape;
    }

    public XDGFShape getParentShape() {
        return this._parent;
    }

    public XDGFShape getTopmostParentShape() {
        XDGFShape xDGFShape = this._parent;
        if (xDGFShape == null) {
            return null;
        }
        XDGFShape top = xDGFShape.getTopmostParentShape();
        if (top == null) {
            return this._parent;
        }
        return top;
    }

    public boolean hasMaster() {
        return this._master != null;
    }

    public boolean hasMasterShape() {
        return this._masterShape != null;
    }

    public boolean hasParent() {
        return this._parent != null;
    }

    public boolean hasShapes() {
        return this._shapes != null;
    }

    public boolean isTopmost() {
        return this._parent == null;
    }

    public boolean isShape1D() {
        return getBeginX() != null;
    }

    public boolean isDeleted() {
        if (mo24getXmlObject().isSetDel()) {
            return mo24getXmlObject().getDel();
        }
        return false;
    }

    public XDGFText getText() {
        XDGFShape xDGFShape;
        XDGFText xDGFText = this._text;
        if (xDGFText == null && (xDGFShape = this._masterShape) != null) {
            return xDGFShape.getText();
        }
        return xDGFText;
    }

    public Double getPinX() {
        XDGFShape xDGFShape;
        Double d = this._pinX;
        if (d == null && (xDGFShape = this._masterShape) != null) {
            return xDGFShape.getPinX();
        }
        if (d == null) {
            throw XDGFException.error("PinX not set!", this);
        }
        return d;
    }

    public Double getPinY() {
        XDGFShape xDGFShape;
        Double d = this._pinY;
        if (d == null && (xDGFShape = this._masterShape) != null) {
            return xDGFShape.getPinY();
        }
        if (d == null) {
            throw XDGFException.error("PinY not specified!", this);
        }
        return d;
    }

    public Double getWidth() {
        XDGFShape xDGFShape;
        Double d = this._width;
        if (d == null && (xDGFShape = this._masterShape) != null) {
            return xDGFShape.getWidth();
        }
        if (d == null) {
            throw XDGFException.error("Width not specified!", this);
        }
        return d;
    }

    public Double getHeight() {
        XDGFShape xDGFShape;
        Double d = this._height;
        if (d == null && (xDGFShape = this._masterShape) != null) {
            return xDGFShape.getHeight();
        }
        if (d == null) {
            throw XDGFException.error("Height not specified!", this);
        }
        return d;
    }

    public Double getLocPinX() {
        XDGFShape xDGFShape;
        Double d = this._locPinX;
        if (d == null && (xDGFShape = this._masterShape) != null) {
            return xDGFShape.getLocPinX();
        }
        if (d == null) {
            throw XDGFException.error("LocPinX not specified!", this);
        }
        return d;
    }

    public Double getLocPinY() {
        XDGFShape xDGFShape;
        Double d = this._locPinY;
        if (d == null && (xDGFShape = this._masterShape) != null) {
            return xDGFShape.getLocPinY();
        }
        if (d == null) {
            throw XDGFException.error("LocPinY not specified!", this);
        }
        return d;
    }

    public Double getBeginX() {
        XDGFShape xDGFShape;
        Double d = this._beginX;
        if (d == null && (xDGFShape = this._masterShape) != null) {
            return xDGFShape.getBeginX();
        }
        return d;
    }

    public Double getBeginY() {
        XDGFShape xDGFShape;
        Double d = this._beginY;
        if (d == null && (xDGFShape = this._masterShape) != null) {
            return xDGFShape.getBeginY();
        }
        return d;
    }

    public Double getEndX() {
        XDGFShape xDGFShape;
        Double d = this._endX;
        if (d == null && (xDGFShape = this._masterShape) != null) {
            return xDGFShape.getEndX();
        }
        return d;
    }

    public Double getEndY() {
        XDGFShape xDGFShape;
        Double d = this._endY;
        if (d == null && (xDGFShape = this._masterShape) != null) {
            return xDGFShape.getEndY();
        }
        return d;
    }

    public Double getAngle() {
        XDGFShape xDGFShape;
        Double d = this._angle;
        if (d == null && (xDGFShape = this._masterShape) != null) {
            return xDGFShape.getAngle();
        }
        return d;
    }

    public Boolean getFlipX() {
        XDGFShape xDGFShape;
        Boolean bool = this._flipX;
        if (bool == null && (xDGFShape = this._masterShape) != null) {
            return xDGFShape.getFlipX();
        }
        return bool;
    }

    public Boolean getFlipY() {
        XDGFShape xDGFShape;
        Boolean bool = this._flipY;
        if (bool == null && (xDGFShape = this._masterShape) != null) {
            return xDGFShape.getFlipY();
        }
        return bool;
    }

    public Double getTxtPinX() {
        XDGFShape xDGFShape;
        Double d;
        Double d2 = this._txtPinX;
        if (d2 == null && (xDGFShape = this._masterShape) != null && (d = xDGFShape._txtPinX) != null) {
            return d;
        }
        if (d2 == null) {
            return Double.valueOf(getWidth().doubleValue() * 0.5d);
        }
        return d2;
    }

    public Double getTxtPinY() {
        XDGFShape xDGFShape;
        Double d;
        if (this._txtLocPinY == null && (xDGFShape = this._masterShape) != null && (d = xDGFShape._txtLocPinY) != null) {
            return d;
        }
        Double d2 = this._txtPinY;
        if (d2 == null) {
            return Double.valueOf(getHeight().doubleValue() * 0.5d);
        }
        return d2;
    }

    public Double getTxtLocPinX() {
        XDGFShape xDGFShape;
        Double d;
        Double d2 = this._txtLocPinX;
        if (d2 == null && (xDGFShape = this._masterShape) != null && (d = xDGFShape._txtLocPinX) != null) {
            return d;
        }
        if (d2 == null) {
            return Double.valueOf(getTxtWidth().doubleValue() * 0.5d);
        }
        return d2;
    }

    public Double getTxtLocPinY() {
        XDGFShape xDGFShape;
        Double d;
        Double d2 = this._txtLocPinY;
        if (d2 == null && (xDGFShape = this._masterShape) != null && (d = xDGFShape._txtLocPinY) != null) {
            return d;
        }
        if (d2 == null) {
            return Double.valueOf(getTxtHeight().doubleValue() * 0.5d);
        }
        return d2;
    }

    public Double getTxtAngle() {
        XDGFShape xDGFShape;
        Double d = this._txtAngle;
        if (d == null && (xDGFShape = this._masterShape) != null) {
            return xDGFShape.getTxtAngle();
        }
        return d;
    }

    public Double getTxtWidth() {
        XDGFShape xDGFShape;
        Double d;
        Double d2 = this._txtWidth;
        if (d2 == null && (xDGFShape = this._masterShape) != null && (d = xDGFShape._txtWidth) != null) {
            return d;
        }
        if (d2 == null) {
            return getWidth();
        }
        return d2;
    }

    public Double getTxtHeight() {
        XDGFShape xDGFShape;
        Double d;
        Double d2 = this._txtHeight;
        if (d2 == null && (xDGFShape = this._masterShape) != null && (d = xDGFShape._txtHeight) != null) {
            return d;
        }
        if (d2 == null) {
            return getHeight();
        }
        return d2;
    }

    @Override // org.apache.poi.xdgf.usermodel.XDGFSheet
    public Integer getLineCap() {
        Integer lineCap = super.getLineCap();
        if (lineCap != null) {
            return lineCap;
        }
        XDGFShape xDGFShape = this._masterShape;
        if (xDGFShape != null) {
            return xDGFShape.getLineCap();
        }
        return this._document.getDefaultLineStyle().getLineCap();
    }

    @Override // org.apache.poi.xdgf.usermodel.XDGFSheet
    public Color getLineColor() {
        Color lineColor = super.getLineColor();
        if (lineColor != null) {
            return lineColor;
        }
        XDGFShape xDGFShape = this._masterShape;
        if (xDGFShape != null) {
            return xDGFShape.getLineColor();
        }
        return this._document.getDefaultLineStyle().getLineColor();
    }

    @Override // org.apache.poi.xdgf.usermodel.XDGFSheet
    public Integer getLinePattern() {
        Integer linePattern = super.getLinePattern();
        if (linePattern != null) {
            return linePattern;
        }
        XDGFShape xDGFShape = this._masterShape;
        if (xDGFShape != null) {
            return xDGFShape.getLinePattern();
        }
        return this._document.getDefaultLineStyle().getLinePattern();
    }

    @Override // org.apache.poi.xdgf.usermodel.XDGFSheet
    public Double getLineWeight() {
        Double lineWeight = super.getLineWeight();
        if (lineWeight != null) {
            return lineWeight;
        }
        XDGFShape xDGFShape = this._masterShape;
        if (xDGFShape != null) {
            return xDGFShape.getLineWeight();
        }
        return this._document.getDefaultLineStyle().getLineWeight();
    }

    @Override // org.apache.poi.xdgf.usermodel.XDGFSheet
    public Color getFontColor() {
        Color fontColor = super.getFontColor();
        if (fontColor != null) {
            return fontColor;
        }
        XDGFShape xDGFShape = this._masterShape;
        if (xDGFShape != null) {
            return xDGFShape.getFontColor();
        }
        return this._document.getDefaultTextStyle().getFontColor();
    }

    @Override // org.apache.poi.xdgf.usermodel.XDGFSheet
    public Double getFontSize() {
        Double fontSize = super.getFontSize();
        if (fontSize != null) {
            return fontSize;
        }
        XDGFShape xDGFShape = this._masterShape;
        if (xDGFShape != null) {
            return xDGFShape.getFontSize();
        }
        return this._document.getDefaultTextStyle().getFontSize();
    }

    public Stroke getStroke() {
        int cap;
        float[] dash;
        float lineWeight = getLineWeight().floatValue();
        int iIntValue = getLineCap().intValue();
        if (iIntValue == 0) {
            cap = 1;
        } else if (iIntValue == 1) {
            cap = 2;
        } else if (iIntValue == 2) {
            cap = 0;
        } else {
            throw new POIXMLException("Invalid line cap specified");
        }
        int iIntValue2 = getLinePattern().intValue();
        if (iIntValue2 == 254) {
            throw new POIXMLException("Unsupported line pattern value");
        }
        switch (iIntValue2) {
            case 0:
            case 1:
                dash = null;
                break;
            case 2:
                float[] dash2 = {5.0f, 3.0f};
                dash = dash2;
                break;
            case 3:
                float[] dash3 = {1.0f, 4.0f};
                dash = dash3;
                break;
            case 4:
                float[] dash4 = {6.0f, 3.0f, 1.0f, 3.0f};
                dash = dash4;
                break;
            case 5:
                float[] dash5 = {6.0f, 3.0f, 1.0f, 3.0f, 1.0f, 3.0f};
                dash = dash5;
                break;
            case 6:
                float[] dash6 = {1.0f, 3.0f, 6.0f, 3.0f, 6.0f, 3.0f};
                dash = dash6;
                break;
            case 7:
                float[] dash7 = {15.0f, 3.0f, 6.0f, 3.0f};
                dash = dash7;
                break;
            case 8:
                float[] dash8 = {6.0f, 3.0f, 6.0f, 3.0f};
                dash = dash8;
                break;
            case 9:
                float[] dash9 = {3.0f, 2.0f};
                dash = dash9;
                break;
            case 10:
                float[] dash10 = {1.0f, 2.0f};
                dash = dash10;
                break;
            case 11:
                float[] dash11 = {3.0f, 2.0f, 1.0f, 2.0f};
                dash = dash11;
                break;
            case 12:
                float[] dash12 = {3.0f, 2.0f, 1.0f, 2.0f, 1.0f};
                dash = dash12;
                break;
            case 13:
                float[] dash13 = {1.0f, 2.0f, 3.0f, 2.0f, 3.0f, 2.0f};
                dash = dash13;
                break;
            case 14:
                float[] dash14 = {3.0f, 2.0f, 7.0f, 2.0f};
                dash = dash14;
                break;
            case 15:
                float[] dash15 = {7.0f, 2.0f, 3.0f, 2.0f, 3.0f, 2.0f};
                dash = dash15;
                break;
            case 16:
                float[] dash16 = {12.0f, 6.0f};
                dash = dash16;
                break;
            case 17:
                float[] dash17 = {1.0f, 6.0f};
                dash = dash17;
                break;
            case 18:
                float[] dash18 = {1.0f, 6.0f, 12.0f, 6.0f};
                dash = dash18;
                break;
            case 19:
                float[] dash19 = {1.0f, 6.0f, 1.0f, 6.0f, 12.0f, 6.0f};
                dash = dash19;
                break;
            case 20:
                float[] dash20 = {1.0f, 6.0f, 12.0f, 6.0f, 12.0f, 6.0f};
                dash = dash20;
                break;
            case 21:
                float[] dash21 = {30.0f, 6.0f, 12.0f, 6.0f};
                dash = dash21;
                break;
            case 22:
                float[] dash22 = {30.0f, 6.0f, 12.0f, 6.0f, 12.0f, 6.0f};
                dash = dash22;
                break;
            case 23:
                float[] dash23 = {1.0f};
                dash = dash23;
                break;
            default:
                throw new POIXMLException("Invalid line pattern value");
        }
        if (dash != null) {
            for (int i = 0; i < dash.length; i++) {
                dash[i] = dash[i] * lineWeight;
            }
        }
        return new BasicStroke(lineWeight, cap, 0, 10.0f, dash, 0.0f);
    }

    public Iterable<GeometrySection> getGeometrySections() {
        SortedMap<Long, GeometrySection> sortedMap = this._geometry;
        XDGFShape xDGFShape = this._masterShape;
        return new CombinedIterable(sortedMap, xDGFShape != null ? xDGFShape._geometry : null);
    }

    public Rectangle2D.Double getBounds() {
        return new Rectangle2D.Double(0.0d, 0.0d, getWidth().doubleValue(), getHeight().doubleValue());
    }

    public Path2D.Double getBoundsAsPath() {
        Double w = getWidth();
        Double h = getHeight();
        Path2D.Double bounds = new Path2D.Double();
        bounds.moveTo(0.0d, 0.0d);
        bounds.lineTo(w.doubleValue(), 0.0d);
        bounds.lineTo(w.doubleValue(), h.doubleValue());
        bounds.lineTo(0.0d, h.doubleValue());
        bounds.lineTo(0.0d, 0.0d);
        return bounds;
    }

    public Path2D.Double getPath() {
        for (GeometrySection geoSection : getGeometrySections()) {
            if (!geoSection.getNoShow().booleanValue()) {
                return geoSection.getPath(this);
            }
        }
        return null;
    }

    public boolean hasGeometry() {
        for (GeometrySection geoSection : getGeometrySections()) {
            if (!geoSection.getNoShow().booleanValue()) {
                return true;
            }
        }
        return false;
    }

    protected AffineTransform getParentTransform() {
        AffineTransform tr = new AffineTransform();
        Double locX = getLocPinX();
        Double locY = getLocPinY();
        Boolean flipX = getFlipX();
        Boolean flipY = getFlipY();
        Double angle = getAngle();
        tr.translate(-locX.doubleValue(), -locY.doubleValue());
        tr.translate(getPinX().doubleValue(), getPinY().doubleValue());
        if (angle != null && Math.abs(angle.doubleValue()) > 0.001d) {
            tr.rotate(angle.doubleValue(), locX.doubleValue(), locY.doubleValue());
        }
        if (flipX != null && flipX.booleanValue()) {
            tr.scale(-1.0d, 1.0d);
            tr.translate(-getWidth().doubleValue(), 0.0d);
        }
        if (flipY != null && flipY.booleanValue()) {
            tr.scale(1.0d, -1.0d);
            tr.translate(0.0d, -getHeight().doubleValue());
        }
        return tr;
    }

    public void visitShapes(ShapeVisitor visitor, AffineTransform tr, int level) {
        AffineTransform tr2 = (AffineTransform) tr.clone();
        tr2.concatenate(getParentTransform());
        try {
            if (visitor.accept(this)) {
                visitor.visit(this, tr2, level);
            }
            List<XDGFShape> list = this._shapes;
            if (list != null) {
                for (XDGFShape shape : list) {
                    shape.visitShapes(visitor, tr2, level + 1);
                }
            }
        } catch (POIXMLException e) {
            throw XDGFException.wrap(toString(), e);
        } catch (StopVisitingThisBranch e2) {
        }
    }

    public void visitShapes(ShapeVisitor visitor, int level) {
        try {
            if (visitor.accept(this)) {
                visitor.visit(this, null, level);
            }
            List<XDGFShape> list = this._shapes;
            if (list != null) {
                for (XDGFShape shape : list) {
                    shape.visitShapes(visitor, level + 1);
                }
            }
        } catch (POIXMLException e) {
            throw XDGFException.wrap(toString(), e);
        } catch (StopVisitingThisBranch e2) {
        }
    }
}
