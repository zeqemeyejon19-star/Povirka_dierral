package org.apache.poi.sl.draw;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.EventFilter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.apache.poi.sl.draw.binding.CTCustomGeometry2D;
import org.apache.poi.sl.draw.geom.Context;
import org.apache.poi.sl.draw.geom.CustomGeometry;
import org.apache.poi.sl.draw.geom.Outline;
import org.apache.poi.sl.draw.geom.Path;
import org.apache.poi.sl.usermodel.LineDecoration;
import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.sl.usermodel.SimpleShape;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.StaxHelper;
import org.apache.poi.util.Units;

/* JADX INFO: loaded from: classes.dex */
public class DrawSimpleShape extends DrawShape {
    private static final double DECO_SIZE_POW = 1.5d;

    public DrawSimpleShape(SimpleShape<?, ?> shape) {
        super(shape);
    }

    @Override // org.apache.poi.sl.draw.DrawShape, org.apache.poi.sl.draw.Drawable
    public void draw(Graphics2D graphics) {
        Paint fillMod;
        DrawPaint drawPaint = DrawFactory.getInstance(graphics).getPaint(getShape());
        Paint fill = drawPaint.getPaint(graphics, getShape().getFillStyle().getPaint());
        Paint line = drawPaint.getPaint(graphics, getShape().getStrokeStyle().getPaint());
        BasicStroke stroke = getStroke();
        graphics.setStroke(stroke);
        Collection<Outline> elems = computeOutlines(graphics);
        drawShadow(graphics, elems, fill, line);
        if (fill != null) {
            for (Outline o : elems) {
                if (o.getPath().isFilled() && (fillMod = drawPaint.getPaint(graphics, getShape().getFillStyle().getPaint(), o.getPath().getFill())) != null) {
                    graphics.setPaint(fillMod);
                    Shape s = o.getOutline();
                    graphics.setRenderingHint(Drawable.GRADIENT_SHAPE, s);
                    graphics.fill(s);
                }
            }
        }
        drawContent(graphics);
        if (line != null) {
            graphics.setPaint(line);
            graphics.setStroke(stroke);
            for (Outline o2 : elems) {
                if (o2.getPath().isStroked()) {
                    Shape s2 = o2.getOutline();
                    graphics.setRenderingHint(Drawable.GRADIENT_SHAPE, s2);
                    graphics.draw(s2);
                }
            }
        }
        drawDecoration(graphics, line, stroke);
    }

    protected void drawDecoration(Graphics2D graphics, Paint line, BasicStroke stroke) {
        if (line == null) {
            return;
        }
        graphics.setPaint(line);
        List<Outline> lst = new ArrayList<>();
        LineDecoration deco = getShape().getLineDecoration();
        Outline head = getHeadDecoration(graphics, deco, stroke);
        if (head != null) {
            lst.add(head);
        }
        Outline tail = getTailDecoration(graphics, deco, stroke);
        if (tail != null) {
            lst.add(tail);
        }
        for (Outline o : lst) {
            Shape s = o.getOutline();
            Path p = o.getPath();
            graphics.setRenderingHint(Drawable.GRADIENT_SHAPE, s);
            if (p.isFilled()) {
                graphics.fill(s);
            }
            if (p.isStroked()) {
                graphics.draw(s);
            }
        }
    }

    protected Outline getTailDecoration(Graphics2D graphics, LineDecoration deco, BasicStroke stroke) {
        AffineTransform at;
        if (deco == null || stroke == null) {
            return null;
        }
        LineDecoration.DecorationSize tailLength = deco.getTailLength();
        if (tailLength == null) {
            tailLength = LineDecoration.DecorationSize.MEDIUM;
        }
        LineDecoration.DecorationSize tailWidth = deco.getTailWidth();
        if (tailWidth == null) {
            tailWidth = LineDecoration.DecorationSize.MEDIUM;
        }
        double lineWidth = Math.max(2.5d, stroke.getLineWidth());
        Rectangle2D anchor = getAnchor(graphics, getShape());
        double x2 = anchor.getX() + anchor.getWidth();
        double y2 = anchor.getY() + anchor.getHeight();
        double alpha = Math.atan(anchor.getHeight() / anchor.getWidth());
        AffineTransform at2 = new AffineTransform();
        Shape tailShape = null;
        Path p = null;
        double scaleY = Math.pow(DECO_SIZE_POW, ((double) tailWidth.ordinal()) + 1.0d);
        double scaleX = Math.pow(DECO_SIZE_POW, ((double) tailLength.ordinal()) + 1.0d);
        LineDecoration.DecorationShape tailShapeEnum = deco.getTailShape();
        if (tailShapeEnum == null) {
            return null;
        }
        int i = AnonymousClass2.$SwitchMap$org$apache$poi$sl$usermodel$LineDecoration$DecorationShape[tailShapeEnum.ordinal()];
        if (i == 1) {
            Path p2 = new Path();
            Shape tailShape2 = new Ellipse2D.Double(0.0d, 0.0d, lineWidth * scaleX, lineWidth * scaleY);
            Rectangle2D bounds = tailShape2.getBounds2D();
            at2.translate(x2 - (bounds.getWidth() / 2.0d), y2 - (bounds.getHeight() / 2.0d));
            at = at2;
            at2.rotate(alpha, bounds.getX() + (bounds.getWidth() / 2.0d), bounds.getY() + (bounds.getHeight() / 2.0d));
            p = p2;
            tailShape = tailShape2;
        } else if (i == 2 || i == 3) {
            p = new Path(false, true);
            Shape shape = new Path2D.Double();
            shape.moveTo((-lineWidth) * scaleX, ((-lineWidth) * scaleY) / 2.0d);
            shape.lineTo(0.0d, 0.0d);
            shape.lineTo((-lineWidth) * scaleX, (lineWidth * scaleY) / 2.0d);
            tailShape = shape;
            at2.translate(x2, y2);
            at2.rotate(alpha);
            at = at2;
        } else if (i != 4) {
            at = at2;
        } else {
            p = new Path();
            Shape shape2 = new Path2D.Double();
            shape2.moveTo((-lineWidth) * scaleX, ((-lineWidth) * scaleY) / 2.0d);
            shape2.lineTo(0.0d, 0.0d);
            shape2.lineTo((-lineWidth) * scaleX, (lineWidth * scaleY) / 2.0d);
            shape2.closePath();
            tailShape = shape2;
            at2.translate(x2, y2);
            at2.rotate(alpha);
            at = at2;
        }
        if (tailShape != null) {
            tailShape = at.createTransformedShape(tailShape);
        }
        if (tailShape == null) {
            return null;
        }
        return new Outline(tailShape, p);
    }

    /* JADX INFO: renamed from: org.apache.poi.sl.draw.DrawSimpleShape$2, reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$sl$usermodel$LineDecoration$DecorationShape;

        static {
            int[] iArr = new int[LineDecoration.DecorationShape.values().length];
            $SwitchMap$org$apache$poi$sl$usermodel$LineDecoration$DecorationShape = iArr;
            try {
                iArr[LineDecoration.DecorationShape.OVAL.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$LineDecoration$DecorationShape[LineDecoration.DecorationShape.STEALTH.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$LineDecoration$DecorationShape[LineDecoration.DecorationShape.ARROW.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$LineDecoration$DecorationShape[LineDecoration.DecorationShape.TRIANGLE.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    protected Outline getHeadDecoration(Graphics2D graphics, LineDecoration deco, BasicStroke stroke) {
        AffineTransform at;
        if (deco == null || stroke == null) {
            return null;
        }
        LineDecoration.DecorationSize headLength = deco.getHeadLength();
        if (headLength == null) {
            headLength = LineDecoration.DecorationSize.MEDIUM;
        }
        LineDecoration.DecorationSize headWidth = deco.getHeadWidth();
        if (headWidth == null) {
            headWidth = LineDecoration.DecorationSize.MEDIUM;
        }
        double lineWidth = Math.max(2.5d, stroke.getLineWidth());
        Rectangle2D anchor = getAnchor(graphics, getShape());
        double x1 = anchor.getX();
        double y1 = anchor.getY();
        double alpha = Math.atan(anchor.getHeight() / anchor.getWidth());
        AffineTransform at2 = new AffineTransform();
        Shape headShape = null;
        Path p = null;
        double scaleY = Math.pow(DECO_SIZE_POW, ((double) headWidth.ordinal()) + 1.0d);
        double scaleX = Math.pow(DECO_SIZE_POW, ((double) headLength.ordinal()) + 1.0d);
        LineDecoration.DecorationShape headShapeEnum = deco.getHeadShape();
        if (headShapeEnum == null) {
            return null;
        }
        int i = AnonymousClass2.$SwitchMap$org$apache$poi$sl$usermodel$LineDecoration$DecorationShape[headShapeEnum.ordinal()];
        if (i == 1) {
            Path p2 = new Path();
            Shape headShape2 = new Ellipse2D.Double(0.0d, 0.0d, lineWidth * scaleX, lineWidth * scaleY);
            Rectangle2D bounds = headShape2.getBounds2D();
            at2.translate(x1 - (bounds.getWidth() / 2.0d), y1 - (bounds.getHeight() / 2.0d));
            at = at2;
            at2.rotate(alpha, bounds.getX() + (bounds.getWidth() / 2.0d), bounds.getY() + (bounds.getHeight() / 2.0d));
            p = p2;
            headShape = headShape2;
        } else if (i == 2 || i == 3) {
            p = new Path(false, true);
            Shape shape = new Path2D.Double();
            shape.moveTo(lineWidth * scaleX, ((-lineWidth) * scaleY) / 2.0d);
            shape.lineTo(0.0d, 0.0d);
            shape.lineTo(lineWidth * scaleX, (lineWidth * scaleY) / 2.0d);
            headShape = shape;
            at2.translate(x1, y1);
            at2.rotate(alpha);
            at = at2;
        } else if (i != 4) {
            at = at2;
        } else {
            p = new Path();
            Shape shape2 = new Path2D.Double();
            shape2.moveTo(lineWidth * scaleX, ((-lineWidth) * scaleY) / 2.0d);
            shape2.lineTo(0.0d, 0.0d);
            shape2.lineTo(lineWidth * scaleX, (lineWidth * scaleY) / 2.0d);
            shape2.closePath();
            headShape = shape2;
            at2.translate(x1, y1);
            at2.rotate(alpha);
            at = at2;
        }
        if (headShape != null) {
            headShape = at.createTransformedShape(headShape);
        }
        if (headShape == null) {
            return null;
        }
        return new Outline(headShape, p);
    }

    public BasicStroke getStroke() {
        return getStroke(getShape().getStrokeStyle());
    }

    protected void drawShadow(Graphics2D graphics, Collection<Outline> outlines, Paint fill, Paint line) {
        Shape s = getShape().getShadow();
        if (s != null) {
            if (fill == null && line == null) {
                return;
            }
            PaintStyle.SolidPaint shadowPaint = s.getFillStyle();
            Color shadowColor = DrawPaint.applyColorTransform(shadowPaint.getSolidColor());
            double shapeRotation = getShape().getRotation();
            if (getShape().getFlipVertical()) {
                shapeRotation += 180.0d;
            }
            double angle = s.getAngle() - shapeRotation;
            double dist = s.getDistance();
            double dx = Math.cos(Math.toRadians(angle)) * dist;
            double dy = Math.sin(Math.toRadians(angle)) * dist;
            graphics.translate(dx, dy);
            for (Outline o : outlines) {
                Shape shape = s;
                Shape s2 = o.getOutline();
                Path p = o.getPath();
                PaintStyle.SolidPaint shadowPaint2 = shadowPaint;
                graphics.setRenderingHint(Drawable.GRADIENT_SHAPE, s2);
                graphics.setPaint(shadowColor);
                if (fill != null && p.isFilled()) {
                    graphics.fill(s2);
                } else if (line != null && p.isStroked()) {
                    graphics.draw(s2);
                }
                s = shape;
                shadowPaint = shadowPaint2;
            }
            graphics.translate(-dx, -dy);
        }
    }

    protected static CustomGeometry getCustomGeometry(String name) {
        return getCustomGeometry(name, null);
    }

    protected static CustomGeometry getCustomGeometry(String name, Graphics2D graphics) {
        Map<String, CustomGeometry> presets = graphics == null ? null : (Map) graphics.getRenderingHint(Drawable.PRESET_GEOMETRY_CACHE);
        if (presets == null) {
            presets = new HashMap<>();
            if (graphics != null) {
                graphics.setRenderingHint(Drawable.PRESET_GEOMETRY_CACHE, presets);
            }
            InputStream presetIS = Drawable.class.getResourceAsStream("presetShapeDefinitions.xml");
            EventFilter startElementFilter = new EventFilter() { // from class: org.apache.poi.sl.draw.DrawSimpleShape.1
                public boolean accept(XMLEvent event) {
                    return event.isStartElement();
                }
            };
            try {
                try {
                    XMLInputFactory staxFactory = StaxHelper.newXMLInputFactory();
                    XMLEventReader staxReader = staxFactory.createXMLEventReader(presetIS);
                    XMLEventReader staxFiltRd = staxFactory.createFilteredReader(staxReader, startElementFilter);
                    staxFiltRd.nextEvent();
                    JAXBContext jaxbContext = JAXBContext.newInstance("org.apache.poi.sl.draw.binding");
                    Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                    while (staxFiltRd.peek() != null) {
                        StartElement evRoot = staxFiltRd.peek();
                        String cusName = evRoot.getName().getLocalPart();
                        JAXBElement<CTCustomGeometry2D> el = unmarshaller.unmarshal(staxReader, CTCustomGeometry2D.class);
                        CTCustomGeometry2D cusGeom = (CTCustomGeometry2D) el.getValue();
                        presets.put(cusName, new CustomGeometry(cusGeom));
                    }
                    staxFiltRd.close();
                    staxReader.close();
                } catch (Exception e) {
                    throw new RuntimeException("Unable to load preset geometries.", e);
                }
            } finally {
                IOUtils.closeQuietly(presetIS);
            }
        }
        return presets.get(name);
    }

    protected Collection<Outline> computeOutlines(Graphics2D graphics) {
        SimpleShape<?, ?> sh = getShape();
        List<Outline> lst = new ArrayList<>();
        CustomGeometry geom = sh.getGeometry();
        if (geom == null) {
            return lst;
        }
        Rectangle2D anchor = getAnchor(graphics, sh);
        for (Path p : geom) {
            double w = p.getW();
            double h = p.getH();
            double scaleX = Units.toPoints(1L);
            double scaleY = scaleX;
            if (w == -1.0d) {
                w = Units.toEMU(anchor.getWidth());
            } else {
                scaleX = anchor.getWidth() / w;
            }
            if (h == -1.0d) {
                h = Units.toEMU(anchor.getHeight());
            } else {
                scaleY = anchor.getHeight() / h;
            }
            Context ctx = new Context(geom, new Rectangle2D.Double(0.0d, 0.0d, w, h), sh);
            SimpleShape<?, ?> sh2 = sh;
            Path2D.Double path = p.getPath(ctx);
            AffineTransform at = new AffineTransform();
            CustomGeometry geom2 = geom;
            at.translate(anchor.getX(), anchor.getY());
            at.scale(scaleX, scaleY);
            Shape canvasShape = at.createTransformedShape(path);
            lst.add(new Outline(canvasShape, p));
            sh = sh2;
            geom = geom2;
            anchor = anchor;
        }
        return lst;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.poi.sl.draw.DrawShape
    public SimpleShape<?, ?> getShape() {
        return (SimpleShape) this.shape;
    }
}
