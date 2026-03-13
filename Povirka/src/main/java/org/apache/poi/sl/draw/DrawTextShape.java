package org.apache.poi.sl.draw;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.sl.usermodel.TextRun;
import org.apache.poi.sl.usermodel.TextShape;
import org.apache.poi.sl.usermodel.VerticalAlignment;

/* JADX INFO: loaded from: classes.dex */
public class DrawTextShape extends DrawSimpleShape {
    public DrawTextShape(TextShape<?, ?> shape) {
        super(shape);
    }

    /* JADX WARN: Incorrect condition in loop: B:4:0x0036 */
    @Override // org.apache.poi.sl.draw.DrawShape, org.apache.poi.sl.draw.Drawable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void drawContent(java.awt.Graphics2D r31) {
        /*
            Method dump skipped, instruction units count: 328
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.poi.sl.draw.DrawTextShape.drawContent(java.awt.Graphics2D):void");
    }

    /* JADX INFO: renamed from: org.apache.poi.sl.draw.DrawTextShape$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$sl$usermodel$VerticalAlignment;

        static {
            int[] iArr = new int[VerticalAlignment.values().length];
            $SwitchMap$org$apache$poi$sl$usermodel$VerticalAlignment = iArr;
            try {
                iArr[VerticalAlignment.TOP.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$VerticalAlignment[VerticalAlignment.BOTTOM.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$VerticalAlignment[VerticalAlignment.MIDDLE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public double drawParagraphs(Graphics2D graphics, double x, double y) {
        double y2;
        DrawFactory fact;
        double d;
        Graphics2D graphics2D = graphics;
        DrawFactory fact2 = DrawFactory.getInstance(graphics);
        Iterator<? extends TextParagraph<?, ?, ? extends TextRun>> paragraphs = getShape().iterator();
        boolean isFirstLine = true;
        int autoNbrIdx = 0;
        double y3 = y;
        while (paragraphs.hasNext()) {
            TextParagraph<?, ?, ? extends TextRun> p = paragraphs.next();
            DrawTextParagraph dp = fact2.getDrawable(p);
            TextParagraph.BulletStyle bs = p.getBulletStyle();
            if (bs == null || bs.getAutoNumberingScheme() == null) {
                autoNbrIdx = -1;
            } else {
                Integer startAt = bs.getAutoNumberingStartAt();
                if (startAt == null) {
                    startAt = 1;
                }
                if (startAt.intValue() > autoNbrIdx) {
                    autoNbrIdx = startAt.intValue();
                }
            }
            dp.setAutoNumberingIdx(autoNbrIdx);
            dp.breakText(graphics2D);
            if (isFirstLine) {
                y2 = y3 + ((double) dp.getFirstLineLeading());
            } else {
                Double spaceBefore = p.getSpaceBefore();
                if (spaceBefore == null) {
                    spaceBefore = Double.valueOf(0.0d);
                }
                if (spaceBefore.doubleValue() > 0.0d) {
                    y2 = y3 + (spaceBefore.doubleValue() * 0.01d * ((double) dp.getFirstLineHeight()));
                } else {
                    y2 = y3 + (-spaceBefore.doubleValue());
                }
            }
            isFirstLine = false;
            dp.setPosition(x, y2);
            dp.draw(graphics2D);
            y3 = y2 + dp.getY();
            if (!paragraphs.hasNext()) {
                fact = fact2;
            } else {
                Double spaceAfter = p.getSpaceAfter();
                if (spaceAfter == null) {
                    d = 0.0d;
                    spaceAfter = Double.valueOf(0.0d);
                } else {
                    d = 0.0d;
                }
                if (spaceAfter.doubleValue() > d) {
                    fact = fact2;
                    y3 += spaceAfter.doubleValue() * 0.01d * ((double) dp.getLastLineHeight());
                } else {
                    fact = fact2;
                    y3 += -spaceAfter.doubleValue();
                }
            }
            autoNbrIdx++;
            graphics2D = graphics;
            fact2 = fact;
        }
        return y3 - y;
    }

    public double getTextHeight() {
        return getTextHeight(null);
    }

    public double getTextHeight(Graphics2D oldGraphics) {
        BufferedImage img = new BufferedImage(1, 1, 1);
        Graphics2D graphics = img.createGraphics();
        if (oldGraphics != null) {
            graphics.addRenderingHints(oldGraphics.getRenderingHints());
            graphics.setTransform(oldGraphics.getTransform());
        }
        DrawFactory.getInstance(graphics).fixFonts(graphics);
        return drawParagraphs(graphics, 0.0d, 0.0d);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.poi.sl.draw.DrawSimpleShape, org.apache.poi.sl.draw.DrawShape
    public TextShape<?, ? extends TextParagraph<?, ?, ? extends TextRun>> getShape() {
        return (TextShape) this.shape;
    }
}
