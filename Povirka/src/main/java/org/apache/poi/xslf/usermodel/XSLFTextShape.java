package org.apache.poi.xslf.usermodel;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.POIXMLException;
import org.apache.poi.sl.draw.DrawFactory;
import org.apache.poi.sl.draw.DrawTextShape;
import org.apache.poi.sl.usermodel.Insets2D;
import org.apache.poi.sl.usermodel.Placeholder;
import org.apache.poi.sl.usermodel.TextShape;
import org.apache.poi.sl.usermodel.VerticalAlignment;
import org.apache.poi.util.Units;
import org.apache.poi.xslf.model.PropertyFetcher;
import org.apache.poi.xslf.model.TextBodyPropertyFetcher;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBodyProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharacterProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraph;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraphProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextAnchoringType;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextVerticalType;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextWrappingType;
import org.openxmlformats.schemas.presentationml.x2006.main.CTPlaceholder;

/* JADX INFO: loaded from: classes.dex */
public abstract class XSLFTextShape extends XSLFSimpleShape implements TextShape<XSLFShape, XSLFTextParagraph> {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private final List<XSLFTextParagraph> _paragraphs;

    protected abstract CTTextBody getTextBody(boolean z);

    XSLFTextShape(XmlObject shape, XSLFSheet sheet) {
        super(shape, sheet);
        this._paragraphs = new ArrayList();
        CTTextBody txBody = getTextBody(false);
        if (txBody != null) {
            CTTextParagraph[] arr$ = txBody.getPArray();
            for (CTTextParagraph p : arr$) {
                this._paragraphs.add(newTextParagraph(p));
            }
        }
    }

    @Override // java.lang.Iterable
    public Iterator<XSLFTextParagraph> iterator() {
        return getTextParagraphs().iterator();
    }

    @Override // org.apache.poi.sl.usermodel.TextShape
    public String getText() {
        StringBuilder out = new StringBuilder();
        for (XSLFTextParagraph p : this._paragraphs) {
            if (out.length() > 0) {
                out.append('\n');
            }
            out.append(p.getText());
        }
        return out.toString();
    }

    public void clearText() {
        this._paragraphs.clear();
        CTTextBody txBody = getTextBody(true);
        txBody.setPArray((CTTextParagraph[]) null);
    }

    @Override // org.apache.poi.sl.usermodel.TextShape
    public XSLFTextRun setText(String text) {
        if (!this._paragraphs.isEmpty()) {
            CTTextBody txBody = getTextBody(false);
            int cntPs = txBody.sizeOfPArray();
            for (int i = cntPs; i > 1; i--) {
                txBody.removeP(i - 1);
                this._paragraphs.remove(i - 1);
            }
            this._paragraphs.get(0).clearButKeepProperties();
        }
        return appendText(text, false);
    }

    @Override // org.apache.poi.sl.usermodel.TextShape
    public XSLFTextRun appendText(String text, boolean newParagraph) {
        boolean firstPara;
        XSLFTextParagraph para;
        CTTextParagraph ctp;
        CTTextCharacterProperties unexpectedRPr;
        if (text == null) {
            return null;
        }
        CTTextParagraphProperties otherPPr = null;
        CTTextCharacterProperties otherRPr = null;
        if (this._paragraphs.isEmpty()) {
            firstPara = false;
            para = null;
        } else {
            firstPara = !newParagraph;
            List<XSLFTextParagraph> list = this._paragraphs;
            para = list.get(list.size() - 1);
            CTTextParagraph ctp2 = para.getXmlObject();
            otherPPr = ctp2.getPPr();
            List<XSLFTextRun> runs = para.getTextRuns();
            if (!runs.isEmpty()) {
                XSLFTextRun r0 = runs.get(runs.size() - 1);
                otherRPr = r0.getRPr(false);
                if (otherRPr == null) {
                    otherRPr = ctp2.getEndParaRPr();
                }
            }
        }
        XSLFTextRun run = null;
        String[] arr$ = text.split("\\r\\n?|\\n");
        for (String lineTxt : arr$) {
            if (!firstPara) {
                if (para != null && (unexpectedRPr = (ctp = para.getXmlObject()).getEndParaRPr()) != null && unexpectedRPr != otherRPr) {
                    ctp.unsetEndParaRPr();
                }
                para = addNewTextParagraph();
                if (otherPPr != null) {
                    para.getXmlObject().setPPr(otherPPr);
                }
            }
            boolean firstRun = true;
            String[] arr$2 = lineTxt.split("[\u000b]");
            for (String runText : arr$2) {
                if (!firstRun) {
                    para.addLineBreak();
                }
                run = para.addNewTextRun();
                run.setText(runText);
                if (otherRPr != null) {
                    run.getRPr(true).set(otherRPr);
                }
                firstRun = false;
            }
            firstPara = false;
        }
        if (run == null) {
            throw new AssertionError();
        }
        return run;
    }

    @Override // org.apache.poi.sl.usermodel.TextShape
    public List<XSLFTextParagraph> getTextParagraphs() {
        return this._paragraphs;
    }

    public XSLFTextParagraph addNewTextParagraph() {
        CTTextParagraph p;
        CTTextBody txBody = getTextBody(false);
        if (txBody == null) {
            p = getTextBody(true).getPArray(0);
            p.removeR(0);
        } else {
            p = txBody.addNewP();
        }
        XSLFTextParagraph paragraph = newTextParagraph(p);
        this._paragraphs.add(paragraph);
        return paragraph;
    }

    public void setVerticalAlignment(VerticalAlignment anchor) {
        CTTextBodyProperties bodyPr = getTextBodyPr(true);
        if (bodyPr != null) {
            if (anchor != null) {
                bodyPr.setAnchor(STTextAnchoringType.Enum.forInt(anchor.ordinal() + 1));
            } else if (bodyPr.isSetAnchor()) {
                bodyPr.unsetAnchor();
            }
        }
    }

    public VerticalAlignment getVerticalAlignment() {
        PropertyFetcher<VerticalAlignment> fetcher = new TextBodyPropertyFetcher<VerticalAlignment>() { // from class: org.apache.poi.xslf.usermodel.XSLFTextShape.1
            @Override // org.apache.poi.xslf.model.TextBodyPropertyFetcher
            public boolean fetch(CTTextBodyProperties props) {
                if (props.isSetAnchor()) {
                    int val = props.getAnchor().intValue();
                    setValue(VerticalAlignment.values()[val - 1]);
                    return true;
                }
                return false;
            }
        };
        fetchShapeProperty(fetcher);
        return fetcher.getValue() == null ? VerticalAlignment.TOP : fetcher.getValue();
    }

    @Override // org.apache.poi.sl.usermodel.TextShape
    public void setHorizontalCentered(Boolean isCentered) {
        CTTextBodyProperties bodyPr = getTextBodyPr(true);
        if (bodyPr != null) {
            if (isCentered == null) {
                if (bodyPr.isSetAnchorCtr()) {
                    bodyPr.unsetAnchorCtr();
                    return;
                }
                return;
            }
            bodyPr.setAnchorCtr(isCentered.booleanValue());
        }
    }

    @Override // org.apache.poi.sl.usermodel.TextShape
    public boolean isHorizontalCentered() {
        PropertyFetcher<Boolean> fetcher = new TextBodyPropertyFetcher<Boolean>() { // from class: org.apache.poi.xslf.usermodel.XSLFTextShape.2
            @Override // org.apache.poi.xslf.model.TextBodyPropertyFetcher
            public boolean fetch(CTTextBodyProperties props) {
                if (props.isSetAnchorCtr()) {
                    setValue(Boolean.valueOf(props.getAnchorCtr()));
                    return true;
                }
                return false;
            }
        };
        fetchShapeProperty(fetcher);
        if (fetcher.getValue() == null) {
            return false;
        }
        return fetcher.getValue().booleanValue();
    }

    public void setTextDirection(TextShape.TextDirection orientation) {
        CTTextBodyProperties bodyPr = getTextBodyPr(true);
        if (bodyPr != null) {
            if (orientation != null) {
                bodyPr.setVert(STTextVerticalType.Enum.forInt(orientation.ordinal() + 1));
            } else if (bodyPr.isSetVert()) {
                bodyPr.unsetVert();
            }
        }
    }

    public TextShape.TextDirection getTextDirection() {
        STTextVerticalType.Enum val;
        CTTextBodyProperties bodyPr = getTextBodyPr();
        if (bodyPr != null && (val = bodyPr.getVert()) != null) {
            switch (val.intValue()) {
                case 2:
                case 5:
                case 6:
                    return TextShape.TextDirection.VERTICAL;
                case 3:
                    return TextShape.TextDirection.VERTICAL_270;
                case 4:
                case 7:
                    return TextShape.TextDirection.STACKED;
                default:
                    return TextShape.TextDirection.HORIZONTAL;
            }
        }
        return TextShape.TextDirection.HORIZONTAL;
    }

    @Override // org.apache.poi.sl.usermodel.TextShape
    public Double getTextRotation() {
        CTTextBodyProperties bodyPr = getTextBodyPr();
        if (bodyPr != null && bodyPr.isSetRot()) {
            return Double.valueOf(((double) bodyPr.getRot()) / 60000.0d);
        }
        return null;
    }

    @Override // org.apache.poi.sl.usermodel.TextShape
    public void setTextRotation(Double rotation) {
        CTTextBodyProperties bodyPr = getTextBodyPr(true);
        if (bodyPr != null) {
            bodyPr.setRot((int) (rotation.doubleValue() * 60000.0d));
        }
    }

    public double getBottomInset() {
        PropertyFetcher<Double> fetcher = new TextBodyPropertyFetcher<Double>() { // from class: org.apache.poi.xslf.usermodel.XSLFTextShape.3
            @Override // org.apache.poi.xslf.model.TextBodyPropertyFetcher
            public boolean fetch(CTTextBodyProperties props) {
                if (props.isSetBIns()) {
                    double val = Units.toPoints(props.getBIns());
                    setValue(Double.valueOf(val));
                    return true;
                }
                return false;
            }
        };
        fetchShapeProperty(fetcher);
        if (fetcher.getValue() == null) {
            return 3.6d;
        }
        return fetcher.getValue().doubleValue();
    }

    public double getLeftInset() {
        PropertyFetcher<Double> fetcher = new TextBodyPropertyFetcher<Double>() { // from class: org.apache.poi.xslf.usermodel.XSLFTextShape.4
            @Override // org.apache.poi.xslf.model.TextBodyPropertyFetcher
            public boolean fetch(CTTextBodyProperties props) {
                if (props.isSetLIns()) {
                    double val = Units.toPoints(props.getLIns());
                    setValue(Double.valueOf(val));
                    return true;
                }
                return false;
            }
        };
        fetchShapeProperty(fetcher);
        if (fetcher.getValue() == null) {
            return 7.2d;
        }
        return fetcher.getValue().doubleValue();
    }

    public double getRightInset() {
        PropertyFetcher<Double> fetcher = new TextBodyPropertyFetcher<Double>() { // from class: org.apache.poi.xslf.usermodel.XSLFTextShape.5
            @Override // org.apache.poi.xslf.model.TextBodyPropertyFetcher
            public boolean fetch(CTTextBodyProperties props) {
                if (props.isSetRIns()) {
                    double val = Units.toPoints(props.getRIns());
                    setValue(Double.valueOf(val));
                    return true;
                }
                return false;
            }
        };
        fetchShapeProperty(fetcher);
        if (fetcher.getValue() == null) {
            return 7.2d;
        }
        return fetcher.getValue().doubleValue();
    }

    public double getTopInset() {
        PropertyFetcher<Double> fetcher = new TextBodyPropertyFetcher<Double>() { // from class: org.apache.poi.xslf.usermodel.XSLFTextShape.6
            @Override // org.apache.poi.xslf.model.TextBodyPropertyFetcher
            public boolean fetch(CTTextBodyProperties props) {
                if (props.isSetTIns()) {
                    double val = Units.toPoints(props.getTIns());
                    setValue(Double.valueOf(val));
                    return true;
                }
                return false;
            }
        };
        fetchShapeProperty(fetcher);
        if (fetcher.getValue() == null) {
            return 3.6d;
        }
        return fetcher.getValue().doubleValue();
    }

    public void setBottomInset(double margin) {
        CTTextBodyProperties bodyPr = getTextBodyPr(true);
        if (bodyPr != null) {
            if (margin == -1.0d) {
                bodyPr.unsetBIns();
            } else {
                bodyPr.setBIns(Units.toEMU(margin));
            }
        }
    }

    public void setLeftInset(double margin) {
        CTTextBodyProperties bodyPr = getTextBodyPr(true);
        if (bodyPr != null) {
            if (margin == -1.0d) {
                bodyPr.unsetLIns();
            } else {
                bodyPr.setLIns(Units.toEMU(margin));
            }
        }
    }

    public void setRightInset(double margin) {
        CTTextBodyProperties bodyPr = getTextBodyPr(true);
        if (bodyPr != null) {
            if (margin == -1.0d) {
                bodyPr.unsetRIns();
            } else {
                bodyPr.setRIns(Units.toEMU(margin));
            }
        }
    }

    public void setTopInset(double margin) {
        CTTextBodyProperties bodyPr = getTextBodyPr(true);
        if (bodyPr != null) {
            if (margin == -1.0d) {
                bodyPr.unsetTIns();
            } else {
                bodyPr.setTIns(Units.toEMU(margin));
            }
        }
    }

    @Override // org.apache.poi.sl.usermodel.TextShape
    public Insets2D getInsets() {
        Insets2D insets = new Insets2D(getTopInset(), getLeftInset(), getBottomInset(), getRightInset());
        return insets;
    }

    @Override // org.apache.poi.sl.usermodel.TextShape
    public void setInsets(Insets2D insets) {
        setTopInset(insets.top);
        setLeftInset(insets.left);
        setBottomInset(insets.bottom);
        setRightInset(insets.right);
    }

    @Override // org.apache.poi.sl.usermodel.TextShape
    public boolean getWordWrap() {
        PropertyFetcher<Boolean> fetcher = new TextBodyPropertyFetcher<Boolean>() { // from class: org.apache.poi.xslf.usermodel.XSLFTextShape.7
            @Override // org.apache.poi.xslf.model.TextBodyPropertyFetcher
            public boolean fetch(CTTextBodyProperties props) {
                if (!props.isSetWrap()) {
                    return false;
                }
                setValue(Boolean.valueOf(props.getWrap() == STTextWrappingType.SQUARE));
                return true;
            }
        };
        fetchShapeProperty(fetcher);
        if (fetcher.getValue() == null) {
            return true;
        }
        return fetcher.getValue().booleanValue();
    }

    @Override // org.apache.poi.sl.usermodel.TextShape
    public void setWordWrap(boolean wrap) {
        CTTextBodyProperties bodyPr = getTextBodyPr(true);
        if (bodyPr != null) {
            bodyPr.setWrap(wrap ? STTextWrappingType.SQUARE : STTextWrappingType.NONE);
        }
    }

    public void setTextAutofit(TextShape.TextAutofit value) {
        CTTextBodyProperties bodyPr = getTextBodyPr(true);
        if (bodyPr != null) {
            if (bodyPr.isSetSpAutoFit()) {
                bodyPr.unsetSpAutoFit();
            }
            if (bodyPr.isSetNoAutofit()) {
                bodyPr.unsetNoAutofit();
            }
            if (bodyPr.isSetNormAutofit()) {
                bodyPr.unsetNormAutofit();
            }
            int i = AnonymousClass8.$SwitchMap$org$apache$poi$sl$usermodel$TextShape$TextAutofit[value.ordinal()];
            if (i == 1) {
                bodyPr.addNewNoAutofit();
            } else if (i == 2) {
                bodyPr.addNewNormAutofit();
            } else if (i == 3) {
                bodyPr.addNewSpAutoFit();
            }
        }
    }

    public TextShape.TextAutofit getTextAutofit() {
        CTTextBodyProperties bodyPr = getTextBodyPr();
        if (bodyPr != null) {
            if (bodyPr.isSetNoAutofit()) {
                return TextShape.TextAutofit.NONE;
            }
            if (bodyPr.isSetNormAutofit()) {
                return TextShape.TextAutofit.NORMAL;
            }
            if (bodyPr.isSetSpAutoFit()) {
                return TextShape.TextAutofit.SHAPE;
            }
        }
        return TextShape.TextAutofit.NORMAL;
    }

    protected CTTextBodyProperties getTextBodyPr() {
        return getTextBodyPr(false);
    }

    protected CTTextBodyProperties getTextBodyPr(boolean create) {
        CTTextBody textBody = getTextBody(create);
        if (textBody == null) {
            return null;
        }
        CTTextBodyProperties textBodyPr = textBody.getBodyPr();
        if (textBodyPr == null && create) {
            return textBody.addNewBodyPr();
        }
        return textBodyPr;
    }

    @Override // org.apache.poi.xslf.usermodel.XSLFSimpleShape, org.apache.poi.xslf.usermodel.XSLFShape, org.apache.poi.sl.usermodel.SimpleShape
    public void setPlaceholder(Placeholder placeholder) {
        super.setPlaceholder(placeholder);
    }

    public Placeholder getTextType() {
        CTPlaceholder ph = getCTPlaceholder();
        if (ph == null) {
            return null;
        }
        int val = ph.getType().intValue();
        return Placeholder.lookupOoxml(val);
    }

    @Override // org.apache.poi.sl.usermodel.TextShape
    public double getTextHeight() {
        return getTextHeight(null);
    }

    @Override // org.apache.poi.sl.usermodel.TextShape
    public double getTextHeight(Graphics2D graphics) {
        DrawFactory drawFact = DrawFactory.getInstance(graphics);
        DrawTextShape dts = drawFact.getDrawable((TextShape<?, ?>) this);
        return dts.getTextHeight(graphics);
    }

    @Override // org.apache.poi.sl.usermodel.TextShape
    public Rectangle2D resizeToFitText() {
        return resizeToFitText(null);
    }

    @Override // org.apache.poi.sl.usermodel.TextShape
    public Rectangle2D resizeToFitText(Graphics2D graphics) {
        Rectangle2D anchor = getAnchor();
        if (anchor.getWidth() == 0.0d) {
            throw new POIXMLException("Anchor of the shape was not set.");
        }
        double height = getTextHeight(graphics);
        Insets2D insets = getInsets();
        anchor.setRect(anchor.getX(), anchor.getY(), anchor.getWidth(), insets.top + height + 1.0d + insets.bottom);
        setAnchor(anchor);
        return anchor;
    }

    @Override // org.apache.poi.xslf.usermodel.XSLFSimpleShape, org.apache.poi.xslf.usermodel.XSLFShape
    void copy(XSLFShape other) {
        super.copy(other);
        XSLFTextShape otherTS = (XSLFTextShape) other;
        CTTextBody otherTB = otherTS.getTextBody(false);
        CTTextBody thisTB = getTextBody(true);
        if (otherTB == null) {
            return;
        }
        thisTB.setBodyPr(otherTB.getBodyPr().copy());
        if (thisTB.isSetLstStyle()) {
            thisTB.unsetLstStyle();
        }
        if (otherTB.isSetLstStyle()) {
            thisTB.setLstStyle(otherTB.getLstStyle().copy());
        }
        boolean srcWordWrap = otherTS.getWordWrap();
        if (srcWordWrap != getWordWrap()) {
            setWordWrap(srcWordWrap);
        }
        double leftInset = otherTS.getLeftInset();
        if (leftInset != getLeftInset()) {
            setLeftInset(leftInset);
        }
        double rightInset = otherTS.getRightInset();
        if (rightInset != getRightInset()) {
            setRightInset(rightInset);
        }
        double topInset = otherTS.getTopInset();
        if (topInset != getTopInset()) {
            setTopInset(topInset);
        }
        double bottomInset = otherTS.getBottomInset();
        if (bottomInset != getBottomInset()) {
            setBottomInset(bottomInset);
        }
        VerticalAlignment vAlign = otherTS.getVerticalAlignment();
        if (vAlign != getVerticalAlignment()) {
            setVerticalAlignment(vAlign);
        }
        clearText();
        for (XSLFTextParagraph srcP : otherTS.getTextParagraphs()) {
            XSLFTextParagraph tgtP = addNewTextParagraph();
            tgtP.copy(srcP);
        }
    }

    @Override // org.apache.poi.sl.usermodel.TextShape
    public void setTextPlaceholder(TextShape.TextPlaceholder placeholder) {
        int i = AnonymousClass8.$SwitchMap$org$apache$poi$sl$usermodel$TextShape$TextPlaceholder[placeholder.ordinal()];
        if (i == 5) {
            setPlaceholder(Placeholder.TITLE);
            return;
        }
        if (i == 6) {
            setPlaceholder(Placeholder.BODY);
            setHorizontalCentered(true);
        } else if (i == 7) {
            setPlaceholder(Placeholder.CENTERED_TITLE);
        } else if (i != 8) {
            setPlaceholder(Placeholder.BODY);
        } else {
            setPlaceholder(Placeholder.CONTENT);
        }
    }

    @Override // org.apache.poi.sl.usermodel.TextShape
    public TextShape.TextPlaceholder getTextPlaceholder() {
        Placeholder ph = getTextType();
        if (ph == null) {
            return TextShape.TextPlaceholder.BODY;
        }
        int i = AnonymousClass8.$SwitchMap$org$apache$poi$sl$usermodel$Placeholder[ph.ordinal()];
        if (i == 1) {
            return TextShape.TextPlaceholder.BODY;
        }
        if (i == 2) {
            return TextShape.TextPlaceholder.TITLE;
        }
        if (i == 3) {
            return TextShape.TextPlaceholder.CENTER_TITLE;
        }
        return TextShape.TextPlaceholder.OTHER;
    }

    /* JADX INFO: renamed from: org.apache.poi.xslf.usermodel.XSLFTextShape$8, reason: invalid class name */
    static /* synthetic */ class AnonymousClass8 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$sl$usermodel$Placeholder;
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$sl$usermodel$TextShape$TextAutofit;
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$sl$usermodel$TextShape$TextPlaceholder;

        static {
            int[] iArr = new int[Placeholder.values().length];
            $SwitchMap$org$apache$poi$sl$usermodel$Placeholder = iArr;
            try {
                iArr[Placeholder.BODY.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$Placeholder[Placeholder.TITLE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$Placeholder[Placeholder.CENTERED_TITLE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$Placeholder[Placeholder.CONTENT.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            int[] iArr2 = new int[TextShape.TextPlaceholder.values().length];
            $SwitchMap$org$apache$poi$sl$usermodel$TextShape$TextPlaceholder = iArr2;
            try {
                iArr2[TextShape.TextPlaceholder.NOTES.ordinal()] = 1;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$TextShape$TextPlaceholder[TextShape.TextPlaceholder.HALF_BODY.ordinal()] = 2;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$TextShape$TextPlaceholder[TextShape.TextPlaceholder.QUARTER_BODY.ordinal()] = 3;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$TextShape$TextPlaceholder[TextShape.TextPlaceholder.BODY.ordinal()] = 4;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$TextShape$TextPlaceholder[TextShape.TextPlaceholder.TITLE.ordinal()] = 5;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$TextShape$TextPlaceholder[TextShape.TextPlaceholder.CENTER_BODY.ordinal()] = 6;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$TextShape$TextPlaceholder[TextShape.TextPlaceholder.CENTER_TITLE.ordinal()] = 7;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$TextShape$TextPlaceholder[TextShape.TextPlaceholder.OTHER.ordinal()] = 8;
            } catch (NoSuchFieldError e12) {
            }
            int[] iArr3 = new int[TextShape.TextAutofit.values().length];
            $SwitchMap$org$apache$poi$sl$usermodel$TextShape$TextAutofit = iArr3;
            try {
                iArr3[TextShape.TextAutofit.NONE.ordinal()] = 1;
            } catch (NoSuchFieldError e13) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$TextShape$TextAutofit[TextShape.TextAutofit.NORMAL.ordinal()] = 2;
            } catch (NoSuchFieldError e14) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$TextShape$TextAutofit[TextShape.TextAutofit.SHAPE.ordinal()] = 3;
            } catch (NoSuchFieldError e15) {
            }
        }
    }

    protected XSLFTextParagraph newTextParagraph(CTTextParagraph p) {
        return new XSLFTextParagraph(p, this);
    }
}
