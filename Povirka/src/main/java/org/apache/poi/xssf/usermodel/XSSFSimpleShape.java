package org.apache.poi.xssf.usermodel;

import com.poverka.httpFileClient.activity.MainActivity;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.SimpleShape;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.util.Internal;
import org.apache.poi.util.Units;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPoint2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPresetGeometry2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTRegularTextRun;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSRgbColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSolidColorFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBodyProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharacterProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextFont;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraph;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTransform2D;
import org.openxmlformats.schemas.drawingml.x2006.main.STShapeType;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextAlignType;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextAnchoringType;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextHorzOverflowType;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextUnderlineType;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextVertOverflowType;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextVerticalType;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextWrappingType;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTShape;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTShapeNonVisual;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColor;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRElt;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRPrElt;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STUnderlineValues;

/* JADX INFO: loaded from: classes.dex */
public class XSSFSimpleShape extends XSSFShape implements Iterable<XSSFTextParagraph>, SimpleShape {
    private final List<XSSFTextParagraph> _paragraphs;
    private CTShape ctShape;
    private static CTShape prototype = null;
    private static String[] _romanChars = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
    private static int[] _romanAlphaValues = {1000, 900, MainActivity.REQUEST_DELAY, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};

    protected XSSFSimpleShape(XSSFDrawing drawing, CTShape ctShape) {
        this.drawing = drawing;
        this.ctShape = ctShape;
        this._paragraphs = new ArrayList();
        CTTextBody body = ctShape.getTxBody();
        if (body != null) {
            for (int i = 0; i < body.sizeOfPArray(); i++) {
                this._paragraphs.add(new XSSFTextParagraph(body.getPArray(i), ctShape));
            }
        }
    }

    protected static CTShape prototype() {
        if (prototype == null) {
            CTShape shape = CTShape.Factory.newInstance();
            CTShapeNonVisual nv = shape.addNewNvSpPr();
            CTNonVisualDrawingProps nvp = nv.addNewCNvPr();
            nvp.setId(1L);
            nvp.setName("Shape 1");
            nv.addNewCNvSpPr();
            CTShapeProperties sp = shape.addNewSpPr();
            CTTransform2D t2d = sp.addNewXfrm();
            CTPositiveSize2D p1 = t2d.addNewExt();
            p1.setCx(0L);
            p1.setCy(0L);
            CTPoint2D p2 = t2d.addNewOff();
            p2.setX(0L);
            p2.setY(0L);
            CTPresetGeometry2D geom = sp.addNewPrstGeom();
            geom.setPrst(STShapeType.RECT);
            geom.addNewAvLst();
            CTTextBody body = shape.addNewTxBody();
            CTTextBodyProperties bodypr = body.addNewBodyPr();
            bodypr.setAnchor(STTextAnchoringType.T);
            bodypr.setRtlCol(false);
            CTTextParagraph p = body.addNewP();
            p.addNewPPr().setAlgn(STTextAlignType.L);
            CTTextCharacterProperties endPr = p.addNewEndParaRPr();
            endPr.setLang("en-US");
            endPr.setSz(1100);
            CTSolidColorFillProperties scfpr = endPr.addNewSolidFill();
            scfpr.addNewSrgbClr().setVal(new byte[]{0, 0, 0});
            body.addNewLstStyle();
            prototype = shape;
        }
        return prototype;
    }

    @Internal
    public CTShape getCTShape() {
        return this.ctShape;
    }

    @Override // java.lang.Iterable
    public Iterator<XSSFTextParagraph> iterator() {
        return this._paragraphs.iterator();
    }

    public String getText() {
        StringBuilder out = new StringBuilder();
        List<Integer> levelCount = new ArrayList<>(9);
        for (int k = 0; k < 9; k++) {
            levelCount.add(0);
        }
        int i = 0;
        while (i < this._paragraphs.size()) {
            if (out.length() > 0) {
                out.append('\n');
            }
            XSSFTextParagraph p = this._paragraphs.get(i);
            if (p.isBullet() && p.getText().length() > 0) {
                int level = Math.min(p.getLevel(), 8);
                if (p.isBulletAutoNumber()) {
                    i = processAutoNumGroup(i, level, levelCount, out);
                } else {
                    for (int j = 0; j < level; j++) {
                        out.append('\t');
                    }
                    String character = p.getBulletCharacter();
                    out.append(character.length() > 0 ? character + " " : "- ");
                    out.append(p.getText());
                }
            } else {
                out.append(p.getText());
                for (int k2 = 0; k2 < 9; k2++) {
                    levelCount.set(k2, 0);
                }
            }
            i++;
        }
        return out.toString();
    }

    private int processAutoNumGroup(int index, int level, List<Integer> levelCount, StringBuilder out) {
        XSSFTextParagraph p = this._paragraphs.get(index);
        int startAt = p.getBulletAutoNumberStart();
        ListAutoNumber scheme = p.getBulletAutoNumberScheme();
        if (levelCount.get(level).intValue() == 0) {
            levelCount.set(level, Integer.valueOf(startAt == 0 ? 1 : startAt));
        }
        for (int j = 0; j < level; j++) {
            out.append('\t');
        }
        if (p.getText().length() > 0) {
            out.append(getBulletPrefix(scheme, levelCount.get(level).intValue()));
            out.append(p.getText());
        }
        while (true) {
            XSSFTextParagraph nextp = index + 1 == this._paragraphs.size() ? null : this._paragraphs.get(index + 1);
            if (nextp == null || !nextp.isBullet() || !p.isBulletAutoNumber()) {
                break;
            }
            if (nextp.getLevel() > level) {
                if (out.length() > 0) {
                    out.append('\n');
                }
                index = processAutoNumGroup(index + 1, nextp.getLevel(), levelCount, out);
            } else {
                if (nextp.getLevel() < level) {
                    break;
                }
                ListAutoNumber nextScheme = nextp.getBulletAutoNumberScheme();
                int nextStartAt = nextp.getBulletAutoNumberStart();
                if (nextScheme != scheme || nextStartAt != startAt) {
                    break;
                }
                index++;
                if (out.length() > 0) {
                    out.append('\n');
                }
                for (int j2 = 0; j2 < level; j2++) {
                    out.append('\t');
                }
                if (nextp.getText().length() > 0) {
                    levelCount.set(level, Integer.valueOf(levelCount.get(level).intValue() + 1));
                    out.append(getBulletPrefix(nextScheme, levelCount.get(level).intValue()));
                    out.append(nextp.getText());
                }
            }
        }
        levelCount.set(level, 0);
        return index;
    }

    private String getBulletPrefix(ListAutoNumber scheme, int value) {
        StringBuilder out = new StringBuilder();
        switch (AnonymousClass1.$SwitchMap$org$apache$poi$xssf$usermodel$ListAutoNumber[scheme.ordinal()]) {
            case 1:
            case 2:
                if (scheme == ListAutoNumber.ALPHA_LC_PARENT_BOTH) {
                    out.append('(');
                }
                out.append(valueToAlpha(value).toLowerCase(Locale.ROOT));
                out.append(')');
                break;
            case 3:
            case 4:
                if (scheme == ListAutoNumber.ALPHA_UC_PARENT_BOTH) {
                    out.append('(');
                }
                out.append(valueToAlpha(value));
                out.append(')');
                break;
            case 5:
                out.append(valueToAlpha(value).toLowerCase(Locale.ROOT));
                out.append('.');
                break;
            case 6:
                out.append(valueToAlpha(value));
                out.append('.');
                break;
            case 7:
            case 8:
                if (scheme == ListAutoNumber.ARABIC_PARENT_BOTH) {
                    out.append('(');
                }
                out.append(value);
                out.append(')');
                break;
            case 9:
                out.append(value);
                out.append('.');
                break;
            case 10:
                out.append(value);
                break;
            case 11:
            case 12:
                if (scheme == ListAutoNumber.ROMAN_LC_PARENT_BOTH) {
                    out.append('(');
                }
                out.append(valueToRoman(value).toLowerCase(Locale.ROOT));
                out.append(')');
                break;
            case 13:
            case 14:
                if (scheme == ListAutoNumber.ROMAN_UC_PARENT_BOTH) {
                    out.append('(');
                }
                out.append(valueToRoman(value));
                out.append(')');
                break;
            case 15:
                out.append(valueToRoman(value).toLowerCase(Locale.ROOT));
                out.append('.');
                break;
            case 16:
                out.append(valueToRoman(value));
                out.append('.');
                break;
            default:
                out.append((char) 8226);
                break;
        }
        out.append(" ");
        return out.toString();
    }

    private String valueToAlpha(int value) {
        String alpha = "";
        while (value > 0) {
            int modulo = (value - 1) % 26;
            alpha = ((char) (modulo + 65)) + alpha;
            value = (value - modulo) / 26;
        }
        return alpha;
    }

    private String valueToRoman(int value) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; value > 0 && i < _romanChars.length; i++) {
            while (_romanAlphaValues[i] <= value) {
                out.append(_romanChars[i]);
                value -= _romanAlphaValues[i];
            }
        }
        return out.toString();
    }

    public void clearText() {
        this._paragraphs.clear();
        CTTextBody txBody = this.ctShape.getTxBody();
        txBody.setPArray((CTTextParagraph[]) null);
    }

    public void setText(String text) {
        clearText();
        addNewTextParagraph().addNewTextRun().setText(text);
    }

    public void setText(XSSFRichTextString str) {
        XSSFWorkbook wb = (XSSFWorkbook) getDrawing().getParent().getParent();
        str.setStylesTableReference(wb.getStylesSource());
        CTTextParagraph p = CTTextParagraph.Factory.newInstance();
        if (str.numFormattingRuns() == 0) {
            CTRegularTextRun r = p.addNewR();
            CTTextCharacterProperties rPr = r.addNewRPr();
            rPr.setLang("en-US");
            rPr.setSz(1100);
            r.setT(str.getString());
        } else {
            for (int i = 0; i < str.getCTRst().sizeOfRArray(); i++) {
                CTRElt lt = str.getCTRst().getRArray(i);
                CTRPrElt ltPr = lt.getRPr();
                if (ltPr == null) {
                    ltPr = lt.addNewRPr();
                }
                CTRegularTextRun r2 = p.addNewR();
                CTTextCharacterProperties rPr2 = r2.addNewRPr();
                rPr2.setLang("en-US");
                applyAttributes(ltPr, rPr2);
                r2.setT(lt.getT());
            }
        }
        clearText();
        this.ctShape.getTxBody().setPArray(new CTTextParagraph[]{p});
        this._paragraphs.add(new XSSFTextParagraph(this.ctShape.getTxBody().getPArray(0), this.ctShape));
    }

    public List<XSSFTextParagraph> getTextParagraphs() {
        return this._paragraphs;
    }

    public XSSFTextParagraph addNewTextParagraph() {
        CTTextBody txBody = this.ctShape.getTxBody();
        CTTextParagraph p = txBody.addNewP();
        XSSFTextParagraph paragraph = new XSSFTextParagraph(p, this.ctShape);
        this._paragraphs.add(paragraph);
        return paragraph;
    }

    public XSSFTextParagraph addNewTextParagraph(String text) {
        XSSFTextParagraph paragraph = addNewTextParagraph();
        paragraph.addNewTextRun().setText(text);
        return paragraph;
    }

    public XSSFTextParagraph addNewTextParagraph(XSSFRichTextString str) {
        CTTextBody txBody = this.ctShape.getTxBody();
        CTTextParagraph p = txBody.addNewP();
        if (str.numFormattingRuns() == 0) {
            CTRegularTextRun r = p.addNewR();
            CTTextCharacterProperties rPr = r.addNewRPr();
            rPr.setLang("en-US");
            rPr.setSz(1100);
            r.setT(str.getString());
        } else {
            for (int i = 0; i < str.getCTRst().sizeOfRArray(); i++) {
                CTRElt lt = str.getCTRst().getRArray(i);
                CTRPrElt ltPr = lt.getRPr();
                if (ltPr == null) {
                    ltPr = lt.addNewRPr();
                }
                CTRegularTextRun r2 = p.addNewR();
                CTTextCharacterProperties rPr2 = r2.addNewRPr();
                rPr2.setLang("en-US");
                applyAttributes(ltPr, rPr2);
                r2.setT(lt.getT());
            }
        }
        XSSFTextParagraph paragraph = new XSSFTextParagraph(p, this.ctShape);
        this._paragraphs.add(paragraph);
        return paragraph;
    }

    public void setTextHorizontalOverflow(TextHorizontalOverflow overflow) {
        CTTextBodyProperties bodyPr = this.ctShape.getTxBody().getBodyPr();
        if (bodyPr != null) {
            if (overflow == null) {
                if (bodyPr.isSetHorzOverflow()) {
                    bodyPr.unsetHorzOverflow();
                    return;
                }
                return;
            }
            bodyPr.setHorzOverflow(STTextHorzOverflowType.Enum.forInt(overflow.ordinal() + 1));
        }
    }

    public TextHorizontalOverflow getTextHorizontalOverflow() {
        CTTextBodyProperties bodyPr = this.ctShape.getTxBody().getBodyPr();
        if (bodyPr != null && bodyPr.isSetHorzOverflow()) {
            return TextHorizontalOverflow.values()[bodyPr.getHorzOverflow().intValue() - 1];
        }
        return TextHorizontalOverflow.OVERFLOW;
    }

    public void setTextVerticalOverflow(TextVerticalOverflow overflow) {
        CTTextBodyProperties bodyPr = this.ctShape.getTxBody().getBodyPr();
        if (bodyPr != null) {
            if (overflow == null) {
                if (bodyPr.isSetVertOverflow()) {
                    bodyPr.unsetVertOverflow();
                    return;
                }
                return;
            }
            bodyPr.setVertOverflow(STTextVertOverflowType.Enum.forInt(overflow.ordinal() + 1));
        }
    }

    public TextVerticalOverflow getTextVerticalOverflow() {
        CTTextBodyProperties bodyPr = this.ctShape.getTxBody().getBodyPr();
        if (bodyPr != null && bodyPr.isSetVertOverflow()) {
            return TextVerticalOverflow.values()[bodyPr.getVertOverflow().intValue() - 1];
        }
        return TextVerticalOverflow.OVERFLOW;
    }

    public void setVerticalAlignment(VerticalAlignment anchor) {
        CTTextBodyProperties bodyPr = this.ctShape.getTxBody().getBodyPr();
        if (bodyPr != null) {
            if (anchor == null) {
                if (bodyPr.isSetAnchor()) {
                    bodyPr.unsetAnchor();
                    return;
                }
                return;
            }
            bodyPr.setAnchor(STTextAnchoringType.Enum.forInt(anchor.ordinal() + 1));
        }
    }

    public VerticalAlignment getVerticalAlignment() {
        CTTextBodyProperties bodyPr = this.ctShape.getTxBody().getBodyPr();
        if (bodyPr != null && bodyPr.isSetAnchor()) {
            return VerticalAlignment.values()[bodyPr.getAnchor().intValue() - 1];
        }
        return VerticalAlignment.TOP;
    }

    public void setTextDirection(TextDirection orientation) {
        CTTextBodyProperties bodyPr = this.ctShape.getTxBody().getBodyPr();
        if (bodyPr != null) {
            if (orientation == null) {
                if (bodyPr.isSetVert()) {
                    bodyPr.unsetVert();
                    return;
                }
                return;
            }
            bodyPr.setVert(STTextVerticalType.Enum.forInt(orientation.ordinal() + 1));
        }
    }

    public TextDirection getTextDirection() {
        STTextVerticalType.Enum val;
        CTTextBodyProperties bodyPr = this.ctShape.getTxBody().getBodyPr();
        if (bodyPr != null && (val = bodyPr.getVert()) != null) {
            return TextDirection.values()[val.intValue() - 1];
        }
        return TextDirection.HORIZONTAL;
    }

    public double getBottomInset() {
        CTTextBodyProperties bodyPr = this.ctShape.getTxBody().getBodyPr();
        if (bodyPr != null && bodyPr.isSetBIns()) {
            return Units.toPoints(bodyPr.getBIns());
        }
        return 3.6d;
    }

    public double getLeftInset() {
        CTTextBodyProperties bodyPr = this.ctShape.getTxBody().getBodyPr();
        if (bodyPr != null && bodyPr.isSetLIns()) {
            return Units.toPoints(bodyPr.getLIns());
        }
        return 3.6d;
    }

    public double getRightInset() {
        CTTextBodyProperties bodyPr = this.ctShape.getTxBody().getBodyPr();
        if (bodyPr != null && bodyPr.isSetRIns()) {
            return Units.toPoints(bodyPr.getRIns());
        }
        return 3.6d;
    }

    public double getTopInset() {
        CTTextBodyProperties bodyPr = this.ctShape.getTxBody().getBodyPr();
        if (bodyPr != null && bodyPr.isSetTIns()) {
            return Units.toPoints(bodyPr.getTIns());
        }
        return 3.6d;
    }

    public void setBottomInset(double margin) {
        CTTextBodyProperties bodyPr = this.ctShape.getTxBody().getBodyPr();
        if (bodyPr != null) {
            if (margin == -1.0d) {
                if (bodyPr.isSetBIns()) {
                    bodyPr.unsetBIns();
                    return;
                }
                return;
            }
            bodyPr.setBIns(Units.toEMU(margin));
        }
    }

    public void setLeftInset(double margin) {
        CTTextBodyProperties bodyPr = this.ctShape.getTxBody().getBodyPr();
        if (bodyPr != null) {
            if (margin == -1.0d) {
                if (bodyPr.isSetLIns()) {
                    bodyPr.unsetLIns();
                    return;
                }
                return;
            }
            bodyPr.setLIns(Units.toEMU(margin));
        }
    }

    public void setRightInset(double margin) {
        CTTextBodyProperties bodyPr = this.ctShape.getTxBody().getBodyPr();
        if (bodyPr != null) {
            if (margin == -1.0d) {
                if (bodyPr.isSetRIns()) {
                    bodyPr.unsetRIns();
                    return;
                }
                return;
            }
            bodyPr.setRIns(Units.toEMU(margin));
        }
    }

    public void setTopInset(double margin) {
        CTTextBodyProperties bodyPr = this.ctShape.getTxBody().getBodyPr();
        if (bodyPr != null) {
            if (margin == -1.0d) {
                if (bodyPr.isSetTIns()) {
                    bodyPr.unsetTIns();
                    return;
                }
                return;
            }
            bodyPr.setTIns(Units.toEMU(margin));
        }
    }

    public boolean getWordWrap() {
        CTTextBodyProperties bodyPr = this.ctShape.getTxBody().getBodyPr();
        return bodyPr == null || !bodyPr.isSetWrap() || bodyPr.getWrap() == STTextWrappingType.SQUARE;
    }

    public void setWordWrap(boolean wrap) {
        CTTextBodyProperties bodyPr = this.ctShape.getTxBody().getBodyPr();
        if (bodyPr != null) {
            bodyPr.setWrap(wrap ? STTextWrappingType.SQUARE : STTextWrappingType.NONE);
        }
    }

    public void setTextAutofit(TextAutofit value) {
        CTTextBodyProperties bodyPr = this.ctShape.getTxBody().getBodyPr();
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
            int i = AnonymousClass1.$SwitchMap$org$apache$poi$xssf$usermodel$TextAutofit[value.ordinal()];
            if (i == 1) {
                bodyPr.addNewNoAutofit();
            } else if (i == 2) {
                bodyPr.addNewNormAutofit();
            } else if (i == 3) {
                bodyPr.addNewSpAutoFit();
            }
        }
    }

    /* JADX INFO: renamed from: org.apache.poi.xssf.usermodel.XSSFSimpleShape$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$xssf$usermodel$ListAutoNumber;
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$xssf$usermodel$TextAutofit;

        static {
            int[] iArr = new int[TextAutofit.values().length];
            $SwitchMap$org$apache$poi$xssf$usermodel$TextAutofit = iArr;
            try {
                iArr[TextAutofit.NONE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$usermodel$TextAutofit[TextAutofit.NORMAL.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$usermodel$TextAutofit[TextAutofit.SHAPE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            int[] iArr2 = new int[ListAutoNumber.values().length];
            $SwitchMap$org$apache$poi$xssf$usermodel$ListAutoNumber = iArr2;
            try {
                iArr2[ListAutoNumber.ALPHA_LC_PARENT_BOTH.ordinal()] = 1;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$usermodel$ListAutoNumber[ListAutoNumber.ALPHA_LC_PARENT_R.ordinal()] = 2;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$usermodel$ListAutoNumber[ListAutoNumber.ALPHA_UC_PARENT_BOTH.ordinal()] = 3;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$usermodel$ListAutoNumber[ListAutoNumber.ALPHA_UC_PARENT_R.ordinal()] = 4;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$usermodel$ListAutoNumber[ListAutoNumber.ALPHA_LC_PERIOD.ordinal()] = 5;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$usermodel$ListAutoNumber[ListAutoNumber.ALPHA_UC_PERIOD.ordinal()] = 6;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$usermodel$ListAutoNumber[ListAutoNumber.ARABIC_PARENT_BOTH.ordinal()] = 7;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$usermodel$ListAutoNumber[ListAutoNumber.ARABIC_PARENT_R.ordinal()] = 8;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$usermodel$ListAutoNumber[ListAutoNumber.ARABIC_PERIOD.ordinal()] = 9;
            } catch (NoSuchFieldError e12) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$usermodel$ListAutoNumber[ListAutoNumber.ARABIC_PLAIN.ordinal()] = 10;
            } catch (NoSuchFieldError e13) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$usermodel$ListAutoNumber[ListAutoNumber.ROMAN_LC_PARENT_BOTH.ordinal()] = 11;
            } catch (NoSuchFieldError e14) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$usermodel$ListAutoNumber[ListAutoNumber.ROMAN_LC_PARENT_R.ordinal()] = 12;
            } catch (NoSuchFieldError e15) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$usermodel$ListAutoNumber[ListAutoNumber.ROMAN_UC_PARENT_BOTH.ordinal()] = 13;
            } catch (NoSuchFieldError e16) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$usermodel$ListAutoNumber[ListAutoNumber.ROMAN_UC_PARENT_R.ordinal()] = 14;
            } catch (NoSuchFieldError e17) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$usermodel$ListAutoNumber[ListAutoNumber.ROMAN_LC_PERIOD.ordinal()] = 15;
            } catch (NoSuchFieldError e18) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$usermodel$ListAutoNumber[ListAutoNumber.ROMAN_UC_PERIOD.ordinal()] = 16;
            } catch (NoSuchFieldError e19) {
            }
        }
    }

    public TextAutofit getTextAutofit() {
        CTTextBodyProperties bodyPr = this.ctShape.getTxBody().getBodyPr();
        if (bodyPr != null) {
            if (bodyPr.isSetNoAutofit()) {
                return TextAutofit.NONE;
            }
            if (bodyPr.isSetNormAutofit()) {
                return TextAutofit.NORMAL;
            }
            if (bodyPr.isSetSpAutoFit()) {
                return TextAutofit.SHAPE;
            }
        }
        return TextAutofit.NORMAL;
    }

    public int getShapeType() {
        return this.ctShape.getSpPr().getPrstGeom().getPrst().intValue();
    }

    public void setShapeType(int type) {
        this.ctShape.getSpPr().getPrstGeom().setPrst(STShapeType.Enum.forInt(type));
    }

    @Override // org.apache.poi.xssf.usermodel.XSSFShape
    protected CTShapeProperties getShapeProperties() {
        return this.ctShape.getSpPr();
    }

    private static void applyAttributes(CTRPrElt pr, CTTextCharacterProperties rPr) {
        HSSFColor indexed;
        if (pr.sizeOfBArray() > 0) {
            rPr.setB(pr.getBArray(0).getVal());
        }
        if (pr.sizeOfUArray() > 0) {
            STUnderlineValues.Enum u1 = pr.getUArray(0).getVal();
            if (u1 == STUnderlineValues.SINGLE) {
                rPr.setU(STTextUnderlineType.SNG);
            } else if (u1 == STUnderlineValues.DOUBLE) {
                rPr.setU(STTextUnderlineType.DBL);
            } else if (u1 == STUnderlineValues.NONE) {
                rPr.setU(STTextUnderlineType.NONE);
            }
        }
        if (pr.sizeOfIArray() > 0) {
            rPr.setI(pr.getIArray(0).getVal());
        }
        if (pr.sizeOfRFontArray() > 0) {
            CTTextFont rFont = rPr.isSetLatin() ? rPr.getLatin() : rPr.addNewLatin();
            rFont.setTypeface(pr.getRFontArray(0).getVal());
        }
        if (pr.sizeOfSzArray() > 0) {
            int sz = (int) (pr.getSzArray(0).getVal() * 100.0d);
            rPr.setSz(sz);
        }
        int sz2 = pr.sizeOfColorArray();
        if (sz2 > 0) {
            CTSolidColorFillProperties fill = rPr.isSetSolidFill() ? rPr.getSolidFill() : rPr.addNewSolidFill();
            CTColor xlsColor = pr.getColorArray(0);
            if (xlsColor.isSetRgb()) {
                CTSRgbColor clr = fill.isSetSrgbClr() ? fill.getSrgbClr() : fill.addNewSrgbClr();
                clr.setVal(xlsColor.getRgb());
            } else if (xlsColor.isSetIndexed() && (indexed = HSSFColor.getIndexHash().get(Integer.valueOf((int) xlsColor.getIndexed()))) != null) {
                byte[] rgb = {(byte) indexed.getTriplet()[0], (byte) indexed.getTriplet()[1], (byte) indexed.getTriplet()[2]};
                CTSRgbColor clr2 = fill.isSetSrgbClr() ? fill.getSrgbClr() : fill.addNewSrgbClr();
                clr2.setVal(rgb);
            }
        }
    }

    @Override // org.apache.poi.ss.usermodel.Shape
    public String getShapeName() {
        return this.ctShape.getNvSpPr().getCNvPr().getName();
    }

    @Override // org.apache.poi.ss.usermodel.SimpleShape
    public int getShapeId() {
        return (int) this.ctShape.getNvSpPr().getCNvPr().getId();
    }
}
