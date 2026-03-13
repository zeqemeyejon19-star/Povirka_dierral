package org.apache.poi.sl.draw;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.InvalidObjectException;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.apache.poi.common.usermodel.fonts.FontGroup;
import org.apache.poi.common.usermodel.fonts.FontInfo;
import org.apache.poi.sl.usermodel.AutoNumberingScheme;
import org.apache.poi.sl.usermodel.Hyperlink;
import org.apache.poi.sl.usermodel.Insets2D;
import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.sl.usermodel.PlaceableShape;
import org.apache.poi.sl.usermodel.ShapeContainer;
import org.apache.poi.sl.usermodel.Sheet;
import org.apache.poi.sl.usermodel.Slide;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.sl.usermodel.TextRun;
import org.apache.poi.sl.usermodel.TextShape;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.Units;

/* JADX INFO: loaded from: classes.dex */
public class DrawTextParagraph implements Drawable {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    protected int autoNbrIdx;
    protected DrawTextFragment bullet;
    protected List<DrawTextFragment> lines = new ArrayList();
    protected double maxLineHeight;
    protected TextParagraph<?, ?, ?> paragraph;
    protected String rawText;
    double x;
    double y;
    private static final POILogger LOG = POILogFactory.getLogger((Class<?>) DrawTextParagraph.class);
    public static final XlinkAttribute HYPERLINK_HREF = new XlinkAttribute("href");
    public static final XlinkAttribute HYPERLINK_LABEL = new XlinkAttribute("label");

    private static class XlinkAttribute extends AttributedCharacterIterator.Attribute {
        XlinkAttribute(String name) {
            super(name);
        }

        @Override // java.text.AttributedCharacterIterator.Attribute
        protected Object readResolve() throws InvalidObjectException {
            if (DrawTextParagraph.HYPERLINK_HREF.getName().equals(getName())) {
                return DrawTextParagraph.HYPERLINK_HREF;
            }
            if (DrawTextParagraph.HYPERLINK_LABEL.getName().equals(getName())) {
                return DrawTextParagraph.HYPERLINK_LABEL;
            }
            throw new InvalidObjectException("unknown attribute name");
        }
    }

    public DrawTextParagraph(TextParagraph<?, ?, ?> paragraph) {
        this.paragraph = paragraph;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getY() {
        return this.y;
    }

    public void setAutoNumberingIdx(int index) {
        this.autoNbrIdx = index;
    }

    @Override // org.apache.poi.sl.draw.Drawable
    public void draw(Graphics2D graphics) {
        int indentLevel;
        Iterator<DrawTextFragment> it;
        Double indent;
        double penX;
        DrawTextParagraph drawTextParagraph = this;
        Graphics2D graphics2D = graphics;
        if (drawTextParagraph.lines.isEmpty()) {
            return;
        }
        double penY = drawTextParagraph.y;
        boolean firstLine = true;
        int indentLevel2 = drawTextParagraph.paragraph.getIndentLevel();
        Double leftMargin = drawTextParagraph.paragraph.getLeftMargin();
        if (leftMargin == null) {
            leftMargin = Double.valueOf(Units.toPoints(((long) indentLevel2) * 347663));
        }
        Double indent2 = drawTextParagraph.paragraph.getIndent();
        if (indent2 == null) {
            indent2 = Double.valueOf(Units.toPoints(((long) indentLevel2) * 347663));
        }
        if (isHSLF()) {
            indent2 = Double.valueOf(indent2.doubleValue() - leftMargin.doubleValue());
        }
        Double spacing = drawTextParagraph.paragraph.getLineSpacing();
        if (spacing == null) {
            spacing = Double.valueOf(100.0d);
        }
        Iterator<DrawTextFragment> it2 = drawTextParagraph.lines.iterator();
        while (it2.hasNext()) {
            DrawTextFragment line = it2.next();
            if (firstLine) {
                if (!isEmptyParagraph()) {
                    drawTextParagraph.bullet = drawTextParagraph.getBullet(graphics2D, line.getAttributedString().getIterator());
                }
                DrawTextFragment drawTextFragment = drawTextParagraph.bullet;
                if (drawTextFragment != null) {
                    drawTextFragment.setPosition(drawTextParagraph.x + leftMargin.doubleValue() + indent2.doubleValue(), penY);
                    drawTextParagraph.bullet.draw(graphics2D);
                    double bulletWidth = drawTextParagraph.bullet.getLayout().getAdvance() + 1.0f;
                    indentLevel = indentLevel2;
                    it = it2;
                    indent = indent2;
                    penX = drawTextParagraph.x + Math.max(leftMargin.doubleValue(), leftMargin.doubleValue() + indent2.doubleValue() + bulletWidth);
                } else {
                    indentLevel = indentLevel2;
                    it = it2;
                    indent = indent2;
                    penX = drawTextParagraph.x + leftMargin.doubleValue();
                }
            } else {
                indentLevel = indentLevel2;
                it = it2;
                indent = indent2;
                penX = drawTextParagraph.x + leftMargin.doubleValue();
            }
            Rectangle2D anchor = DrawShape.getAnchor(graphics2D, drawTextParagraph.paragraph.getParentShape());
            Insets2D insets = drawTextParagraph.paragraph.getParentShape().getInsets();
            double leftInset = insets.left;
            double rightInset = insets.right;
            TextParagraph.TextAlign ta = drawTextParagraph.paragraph.getTextAlign();
            if (ta == null) {
                ta = TextParagraph.TextAlign.LEFT;
            }
            int i = AnonymousClass2.$SwitchMap$org$apache$poi$sl$usermodel$TextParagraph$TextAlign[ta.ordinal()];
            int indentLevel3 = indentLevel;
            if (i == 1) {
                penX += ((((anchor.getWidth() - ((double) line.getWidth())) - leftInset) - rightInset) - leftMargin.doubleValue()) / 2.0d;
            } else if (i == 2) {
                penX += ((anchor.getWidth() - ((double) line.getWidth())) - leftInset) - rightInset;
            }
            line.setPosition(penX, penY);
            line.draw(graphics);
            if (spacing.doubleValue() > 0.0d) {
                penY += spacing.doubleValue() * 0.01d * ((double) line.getHeight());
            } else {
                penY += -spacing.doubleValue();
            }
            firstLine = false;
            drawTextParagraph = this;
            graphics2D = graphics;
            indentLevel2 = indentLevel3;
            indent2 = indent;
            it2 = it;
        }
        this.y = penY - this.y;
    }

    public float getFirstLineLeading() {
        if (this.lines.isEmpty()) {
            return 0.0f;
        }
        return this.lines.get(0).getLeading();
    }

    public float getFirstLineHeight() {
        if (this.lines.isEmpty()) {
            return 0.0f;
        }
        return this.lines.get(0).getHeight();
    }

    public float getLastLineHeight() {
        if (this.lines.isEmpty()) {
            return 0.0f;
        }
        return this.lines.get(r0.size() - 1).getHeight();
    }

    public boolean isEmptyParagraph() {
        return this.lines.isEmpty() || this.rawText.trim().isEmpty();
    }

    @Override // org.apache.poi.sl.draw.Drawable
    public void applyTransform(Graphics2D graphics) {
    }

    @Override // org.apache.poi.sl.draw.Drawable
    public void drawContent(Graphics2D graphics) {
    }

    protected void breakText(Graphics2D graphics) {
        Graphics2D graphics2D = graphics;
        this.lines.clear();
        DrawFactory fact = DrawFactory.getInstance(graphics);
        fact.fixFonts(graphics2D);
        StringBuilder text = new StringBuilder();
        AttributedString at = getAttributedString(graphics2D, text);
        boolean emptyParagraph = "".equals(text.toString().trim());
        AttributedCharacterIterator it = at.getIterator();
        LineBreakMeasurer measurer = new LineBreakMeasurer(it, graphics.getFontRenderContext());
        while (true) {
            int startIndex = measurer.getPosition();
            double wrappingWidth = getWrappingWidth(this.lines.isEmpty(), graphics2D) + 1.0d;
            if (wrappingWidth < 0.0d) {
                wrappingWidth = 1.0d;
            }
            int nextBreak = text.indexOf("\n", startIndex + 1);
            if (nextBreak == -1) {
                nextBreak = it.getEndIndex();
            }
            TextLayout layout = measurer.nextLayout((float) wrappingWidth, nextBreak, true);
            if (layout == null) {
                layout = measurer.nextLayout((float) wrappingWidth, nextBreak, false);
            }
            if (layout == null) {
                break;
            }
            int endIndex = measurer.getPosition();
            if (endIndex < it.getEndIndex() && text.charAt(endIndex) == '\n') {
                measurer.setPosition(endIndex + 1);
            }
            TextParagraph.TextAlign hAlign = this.paragraph.getTextAlign();
            if (hAlign == TextParagraph.TextAlign.JUSTIFY || hAlign == TextParagraph.TextAlign.JUSTIFY_LOW) {
                layout = layout.getJustifiedLayout((float) wrappingWidth);
            }
            AttributedString str = emptyParagraph ? null : new AttributedString(it, startIndex, endIndex);
            DrawTextFragment line = fact.getTextFragment(layout, str);
            DrawFactory fact2 = fact;
            this.lines.add(line);
            AttributedString at2 = at;
            boolean emptyParagraph2 = emptyParagraph;
            this.maxLineHeight = Math.max(this.maxLineHeight, line.getHeight());
            if (endIndex == it.getEndIndex()) {
                break;
            }
            graphics2D = graphics;
            fact = fact2;
            emptyParagraph = emptyParagraph2;
            at = at2;
        }
        this.rawText = text.toString();
    }

    protected DrawTextFragment getBullet(Graphics2D graphics, AttributedCharacterIterator firstLineAttr) {
        String buCharacter;
        Paint fgPaint;
        float fontSize;
        TextParagraph.BulletStyle bulletStyle = this.paragraph.getBulletStyle();
        if (bulletStyle == null) {
            return null;
        }
        AutoNumberingScheme ans = bulletStyle.getAutoNumberingScheme();
        if (ans != null) {
            buCharacter = ans.format(this.autoNbrIdx);
        } else {
            buCharacter = bulletStyle.getBulletCharacter();
        }
        if (buCharacter == null) {
            return null;
        }
        PlaceableShape<?, ?> ps = getParagraphShape();
        PaintStyle fgPaintStyle = bulletStyle.getBulletFontColor();
        if (fgPaintStyle == null) {
            fgPaint = (Paint) firstLineAttr.getAttribute(TextAttribute.FOREGROUND);
        } else {
            fgPaint = new DrawPaint(ps).getPaint(graphics, fgPaintStyle);
        }
        float fontSize2 = ((Float) firstLineAttr.getAttribute(TextAttribute.SIZE)).floatValue();
        Double buSz = bulletStyle.getBulletFontSize();
        if (buSz == null) {
            buSz = Double.valueOf(100.0d);
        }
        if (buSz.doubleValue() > 0.0d) {
            fontSize = (float) (((double) fontSize2) * buSz.doubleValue() * 0.01d);
        } else {
            fontSize = (float) (-buSz.doubleValue());
        }
        String buFontStr = bulletStyle.getBulletFont();
        if (buFontStr == null) {
            buFontStr = this.paragraph.getDefaultFontFamily();
        }
        if (buFontStr == null) {
            throw new AssertionError();
        }
        FontInfo buFont = new DrawFontInfo(buFontStr);
        DrawFontManager dfm = DrawFactory.getInstance(graphics).getFontManager(graphics);
        FontInfo buFont2 = dfm.getMappedFont(graphics, buFont);
        AttributedString str = new AttributedString(dfm.mapFontCharset(graphics, buFont2, buCharacter));
        str.addAttribute(TextAttribute.FOREGROUND, fgPaint);
        str.addAttribute(TextAttribute.FAMILY, buFont2.getTypeface());
        str.addAttribute(TextAttribute.SIZE, Float.valueOf(fontSize));
        TextLayout layout = new TextLayout(str.getIterator(), graphics.getFontRenderContext());
        DrawFactory fact = DrawFactory.getInstance(graphics);
        return fact.getTextFragment(layout, str);
    }

    protected String getRenderableText(Graphics2D graphics, TextRun tr) {
        if (tr.getFieldType() == TextRun.FieldType.SLIDE_NUMBER) {
            Slide<?, ?> slide = (Slide) graphics.getRenderingHint(Drawable.CURRENT_SLIDE);
            return slide == null ? "" : Integer.toString(slide.getSlideNumber());
        }
        StringBuilder buf = new StringBuilder();
        TextRun.TextCap cap = tr.getTextCap();
        String tabs = null;
        char[] arr$ = tr.getRawText().toCharArray();
        for (char c : arr$) {
            if (c == '\t') {
                if (tabs == null) {
                    tabs = tab2space(tr);
                }
                buf.append(tabs);
            } else if (c == 11) {
                buf.append('\n');
            } else {
                int i = AnonymousClass2.$SwitchMap$org$apache$poi$sl$usermodel$TextRun$TextCap[cap.ordinal()];
                if (i == 1) {
                    c = Character.toUpperCase(c);
                } else if (i == 2) {
                    c = Character.toLowerCase(c);
                }
                buf.append(c);
            }
        }
        return buf.toString();
    }

    private String tab2space(TextRun tr) {
        AttributedString string = new AttributedString(" ");
        String fontFamily = tr.getFontFamily();
        if (fontFamily == null) {
            fontFamily = "Lucida Sans";
        }
        string.addAttribute(TextAttribute.FAMILY, fontFamily);
        Double fs = tr.getFontSize();
        if (fs == null) {
            fs = Double.valueOf(12.0d);
        }
        string.addAttribute(TextAttribute.SIZE, Float.valueOf(fs.floatValue()));
        TextLayout l = new TextLayout(string.getIterator(), new FontRenderContext((AffineTransform) null, true, true));
        double wspace = l.getAdvance();
        Double tabSz = this.paragraph.getDefaultTabSize();
        if (tabSz == null) {
            tabSz = Double.valueOf(4.0d * wspace);
        }
        int numSpaces = (int) Math.ceil(tabSz.doubleValue() / wspace);
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < numSpaces; i++) {
            buf.append(' ');
        }
        return buf.toString();
    }

    protected double getWrappingWidth(boolean firstLine, Graphics2D graphics) {
        double width;
        double width2;
        TextShape<S, P> parentShape = this.paragraph.getParentShape();
        Insets2D insets = parentShape.getInsets();
        double leftInset = insets.left;
        double rightInset = insets.right;
        int indentLevel = this.paragraph.getIndentLevel();
        if (indentLevel == -1) {
            indentLevel = 0;
        }
        Double leftMargin = this.paragraph.getLeftMargin();
        if (leftMargin == null) {
            leftMargin = Double.valueOf(Units.toPoints(((long) (indentLevel + 1)) * 347663));
        }
        Double indent = this.paragraph.getIndent();
        if (indent == null) {
            indent = Double.valueOf(Units.toPoints(((long) indentLevel) * 347663));
        }
        Double rightMargin = this.paragraph.getRightMargin();
        if (rightMargin == null) {
            rightMargin = Double.valueOf(0.0d);
        }
        Rectangle2D anchor = DrawShape.getAnchor(graphics, parentShape);
        TextShape.TextDirection textDir = parentShape.getTextDirection();
        if (!parentShape.getWordWrap()) {
            Dimension pageDim = parentShape.getSheet().getSlideShow().getPageSize();
            int i = AnonymousClass2.$SwitchMap$org$apache$poi$sl$usermodel$TextShape$TextDirection[textDir.ordinal()];
            if (i == 1) {
                width2 = pageDim.getHeight() - anchor.getX();
            } else if (i != 2) {
                width2 = pageDim.getWidth() - anchor.getX();
            } else {
                width2 = anchor.getX();
            }
            return width2;
        }
        int i2 = AnonymousClass2.$SwitchMap$org$apache$poi$sl$usermodel$TextShape$TextDirection[textDir.ordinal()];
        if (i2 != 1 && i2 != 2) {
            width = (((anchor.getWidth() - leftInset) - rightInset) - leftMargin.doubleValue()) - rightMargin.doubleValue();
        } else {
            width = (((anchor.getHeight() - leftInset) - rightInset) - leftMargin.doubleValue()) - rightMargin.doubleValue();
        }
        if (firstLine && !isHSLF()) {
            if (this.bullet != null) {
                if (indent.doubleValue() > 0.0d) {
                    return width - indent.doubleValue();
                }
                return width;
            }
            if (indent.doubleValue() > 0.0d) {
                return width - indent.doubleValue();
            }
            if (indent.doubleValue() < 0.0d) {
                return width + leftMargin.doubleValue();
            }
            return width;
        }
        return width;
    }

    /* JADX INFO: renamed from: org.apache.poi.sl.draw.DrawTextParagraph$2, reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$sl$usermodel$TextParagraph$TextAlign;
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$sl$usermodel$TextRun$TextCap;
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$sl$usermodel$TextShape$TextDirection;

        static {
            int[] iArr = new int[TextShape.TextDirection.values().length];
            $SwitchMap$org$apache$poi$sl$usermodel$TextShape$TextDirection = iArr;
            try {
                iArr[TextShape.TextDirection.VERTICAL.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$TextShape$TextDirection[TextShape.TextDirection.VERTICAL_270.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            int[] iArr2 = new int[TextRun.TextCap.values().length];
            $SwitchMap$org$apache$poi$sl$usermodel$TextRun$TextCap = iArr2;
            try {
                iArr2[TextRun.TextCap.ALL.ordinal()] = 1;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$TextRun$TextCap[TextRun.TextCap.SMALL.ordinal()] = 2;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$TextRun$TextCap[TextRun.TextCap.NONE.ordinal()] = 3;
            } catch (NoSuchFieldError e5) {
            }
            int[] iArr3 = new int[TextParagraph.TextAlign.values().length];
            $SwitchMap$org$apache$poi$sl$usermodel$TextParagraph$TextAlign = iArr3;
            try {
                iArr3[TextParagraph.TextAlign.CENTER.ordinal()] = 1;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$TextParagraph$TextAlign[TextParagraph.TextAlign.RIGHT.ordinal()] = 2;
            } catch (NoSuchFieldError e7) {
            }
        }
    }

    private static class AttributedStringData {
        AttributedCharacterIterator.Attribute attribute;
        int beginIndex;
        int endIndex;
        Object value;

        AttributedStringData(AttributedCharacterIterator.Attribute attribute, Object value, int beginIndex, int endIndex) {
            this.attribute = attribute;
            this.value = value;
            this.beginIndex = beginIndex;
            this.endIndex = endIndex;
        }
    }

    private PlaceableShape<?, ?> getParagraphShape() {
        return new PlaceableShape() { // from class: org.apache.poi.sl.draw.DrawTextParagraph.1
            @Override // org.apache.poi.sl.usermodel.PlaceableShape
            public ShapeContainer<?, ?> getParent() {
                return null;
            }

            @Override // org.apache.poi.sl.usermodel.PlaceableShape
            public Rectangle2D getAnchor() {
                return DrawTextParagraph.this.paragraph.getParentShape().getAnchor();
            }

            @Override // org.apache.poi.sl.usermodel.PlaceableShape
            public void setAnchor(Rectangle2D anchor) {
            }

            @Override // org.apache.poi.sl.usermodel.PlaceableShape
            public double getRotation() {
                return 0.0d;
            }

            @Override // org.apache.poi.sl.usermodel.PlaceableShape
            public void setRotation(double theta) {
            }

            @Override // org.apache.poi.sl.usermodel.PlaceableShape
            public void setFlipHorizontal(boolean flip) {
            }

            @Override // org.apache.poi.sl.usermodel.PlaceableShape
            public void setFlipVertical(boolean flip) {
            }

            @Override // org.apache.poi.sl.usermodel.PlaceableShape
            public boolean getFlipHorizontal() {
                return false;
            }

            @Override // org.apache.poi.sl.usermodel.PlaceableShape
            public boolean getFlipVertical() {
                return false;
            }

            @Override // org.apache.poi.sl.usermodel.PlaceableShape
            public Sheet<?, ?> getSheet() {
                return DrawTextParagraph.this.paragraph.getParentShape().getSheet();
            }
        };
    }

    protected AttributedString getAttributedString(Graphics2D graphics, StringBuilder text) {
        StringBuilder text2;
        Double fontSz;
        List<AttributedStringData> attList = new ArrayList<>();
        if (text != null) {
            text2 = text;
        } else {
            text2 = new StringBuilder();
        }
        PlaceableShape<?, ?> ps = getParagraphShape();
        DrawFontManager dfm = DrawFactory.getInstance(graphics).getFontManager(graphics);
        if (dfm == null) {
            throw new AssertionError();
        }
        Iterator<?> it = this.paragraph.iterator();
        while (it.hasNext()) {
            TextRun run = (TextRun) it.next();
            String runText = getRenderableText(graphics, run);
            if (!runText.isEmpty()) {
                String runText2 = dfm.mapFontCharset(graphics, run.getFontInfo(null), runText);
                int beginIndex = text2.length();
                text2.append(runText2);
                int endIndex = text2.length();
                PaintStyle fgPaintStyle = run.getFontColor();
                Paint fgPaint = new DrawPaint(ps).getPaint(graphics, fgPaintStyle);
                attList.add(new AttributedStringData(TextAttribute.FOREGROUND, fgPaint, beginIndex, endIndex));
                Double fontSz2 = run.getFontSize();
                if (fontSz2 != null) {
                    fontSz = fontSz2;
                } else {
                    Double fontSz3 = this.paragraph.getDefaultFontSize();
                    fontSz = fontSz3;
                }
                attList.add(new AttributedStringData(TextAttribute.SIZE, Float.valueOf(fontSz.floatValue()), beginIndex, endIndex));
                if (run.isBold()) {
                    attList.add(new AttributedStringData(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD, beginIndex, endIndex));
                }
                if (run.isItalic()) {
                    attList.add(new AttributedStringData(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE, beginIndex, endIndex));
                }
                if (run.isUnderlined()) {
                    attList.add(new AttributedStringData(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON, beginIndex, endIndex));
                    attList.add(new AttributedStringData(TextAttribute.INPUT_METHOD_UNDERLINE, TextAttribute.UNDERLINE_LOW_TWO_PIXEL, beginIndex, endIndex));
                }
                if (run.isStrikethrough()) {
                    attList.add(new AttributedStringData(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON, beginIndex, endIndex));
                }
                if (run.isSubscript()) {
                    attList.add(new AttributedStringData(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB, beginIndex, endIndex));
                }
                if (run.isSuperscript()) {
                    attList.add(new AttributedStringData(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUPER, beginIndex, endIndex));
                }
                Hyperlink<?, ?> hl = run.getHyperlink();
                if (hl != null) {
                    attList.add(new AttributedStringData(HYPERLINK_HREF, hl.getAddress(), beginIndex, endIndex));
                    attList.add(new AttributedStringData(HYPERLINK_LABEL, hl.getLabel(), beginIndex, endIndex));
                }
                processGlyphs(graphics, dfm, attList, beginIndex, run, runText2);
            }
        }
        if (text2.length() == 0) {
            Double fontSz4 = this.paragraph.getDefaultFontSize();
            text2.append(" ");
            attList.add(new AttributedStringData(TextAttribute.SIZE, Float.valueOf(fontSz4.floatValue()), 0, 1));
        }
        AttributedString string = new AttributedString(text2.toString());
        for (AttributedStringData asd : attList) {
            string.addAttribute(asd.attribute, asd.value, asd.beginIndex, asd.endIndex);
        }
        return string;
    }

    private void processGlyphs(Graphics2D graphics, DrawFontManager dfm, List<AttributedStringData> attList, int beginIndex, TextRun run, String runText) {
        FontInfo fiRun;
        FontInfo fiMapped;
        List<FontGroup.FontGroupRange> ttrList;
        Graphics2D graphics2D = graphics;
        DrawFontManager drawFontManager = dfm;
        TextRun textRun = run;
        List<FontGroup.FontGroupRange> ttrList2 = FontGroup.getFontGroupRanges(runText);
        int rangeBegin = 0;
        for (FontGroup.FontGroupRange ttr : ttrList2) {
            FontInfo fiRun2 = textRun.getFontInfo(ttr.getFontGroup());
            if (fiRun2 != null) {
                fiRun = fiRun2;
            } else {
                fiRun = textRun.getFontInfo(FontGroup.LATIN);
            }
            FontInfo fiMapped2 = drawFontManager.getMappedFont(graphics2D, fiRun);
            FontInfo fiFallback = drawFontManager.getFallbackFont(graphics2D, fiRun);
            if (fiFallback == null) {
                throw new AssertionError();
            }
            if (fiMapped2 == null) {
                fiMapped2 = drawFontManager.getMappedFont(graphics2D, new DrawFontInfo(this.paragraph.getDefaultFontFamily()));
            }
            if (fiMapped2 != null) {
                fiMapped = fiMapped2;
            } else {
                fiMapped = fiFallback;
            }
            Font fontMapped = dfm.createAWTFont(graphics, fiMapped, 10.0d, run.isBold(), run.isItalic());
            Font fontFallback = dfm.createAWTFont(graphics, fiFallback, 10.0d, run.isBold(), run.isItalic());
            int rangeLen = ttr.getLength();
            int partEnd = rangeBegin;
            while (partEnd < rangeBegin + rangeLen) {
                int partBegin = partEnd;
                int partEnd2 = nextPart(fontMapped, runText, partBegin, rangeBegin + rangeLen, true);
                if (partBegin >= partEnd2) {
                    ttrList = ttrList2;
                } else {
                    ttrList = ttrList2;
                    attList.add(new AttributedStringData(TextAttribute.FAMILY, fontMapped.getFontName(Locale.ROOT), beginIndex + partBegin, beginIndex + partEnd2));
                    POILogger pOILogger = LOG;
                    if (pOILogger.check(1)) {
                        pOILogger.log(1, "mapped: ", fontMapped.getFontName(Locale.ROOT), " ", Integer.valueOf(beginIndex + partBegin), " ", Integer.valueOf(beginIndex + partEnd2), " - ", runText.substring(beginIndex + partBegin, beginIndex + partEnd2));
                    }
                }
                partEnd = nextPart(fontMapped, runText, partEnd2, rangeBegin + rangeLen, false);
                if (partEnd2 < partEnd) {
                    attList.add(new AttributedStringData(TextAttribute.FAMILY, fontFallback.getFontName(Locale.ROOT), beginIndex + partEnd2, beginIndex + partEnd));
                    POILogger pOILogger2 = LOG;
                    if (pOILogger2.check(1)) {
                        pOILogger2.log(1, "fallback: ", fontFallback.getFontName(Locale.ROOT), " ", Integer.valueOf(beginIndex + partEnd2), " ", Integer.valueOf(beginIndex + partEnd), " - ", runText.substring(beginIndex + partEnd2, beginIndex + partEnd));
                    }
                }
                ttrList2 = ttrList;
            }
            rangeBegin += rangeLen;
            graphics2D = graphics;
            drawFontManager = dfm;
            textRun = run;
        }
    }

    private static int nextPart(Font fontMapped, String runText, int beginPart, int endPart, boolean isDisplayed) {
        int rIdx = beginPart;
        while (rIdx < endPart) {
            int codepoint = runText.codePointAt(rIdx);
            if (fontMapped.canDisplay(codepoint) != isDisplayed) {
                break;
            }
            rIdx += Character.charCount(codepoint);
        }
        return rIdx;
    }

    protected boolean isHSLF() {
        return DrawShape.isHSLF(this.paragraph.getParentShape());
    }
}
