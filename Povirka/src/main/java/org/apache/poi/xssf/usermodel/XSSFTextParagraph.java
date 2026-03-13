package org.apache.poi.xssf.usermodel;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.util.Internal;
import org.apache.poi.util.Units;
import org.apache.poi.xssf.model.ParagraphPropertyFetcher;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTRegularTextRun;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSRgbColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextAutonumberBullet;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBulletSizePercent;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBulletSizePoint;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharBullet;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharacterProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextField;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextFont;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextLineBreak;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextNormalAutofit;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraph;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraphProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextSpacing;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextTabStop;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextTabStopList;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextAlignType;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextAutonumberScheme;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextFontAlignType;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTShape;

/* JADX INFO: loaded from: classes.dex */
public class XSSFTextParagraph implements Iterable<XSSFTextRun> {
    private final CTTextParagraph _p;
    private final List<XSSFTextRun> _runs = new ArrayList();
    private final CTShape _shape;

    XSSFTextParagraph(CTTextParagraph p, CTShape ctShape) {
        this._p = p;
        this._shape = ctShape;
        for (CTRegularTextRun r : p.selectPath("*")) {
            if (r instanceof CTRegularTextRun) {
                this._runs.add(new XSSFTextRun(r, this));
            } else if (r instanceof CTTextLineBreak) {
                CTTextLineBreak br = (CTTextLineBreak) r;
                CTRegularTextRun r2 = CTRegularTextRun.Factory.newInstance();
                r2.setRPr(br.getRPr());
                r2.setT("\n");
                this._runs.add(new XSSFTextRun(r2, this));
            } else if (r instanceof CTTextField) {
                CTTextField f = (CTTextField) r;
                CTRegularTextRun r3 = CTRegularTextRun.Factory.newInstance();
                r3.setRPr(f.getRPr());
                r3.setT(f.getT());
                this._runs.add(new XSSFTextRun(r3, this));
            }
        }
    }

    public String getText() {
        StringBuilder out = new StringBuilder();
        for (XSSFTextRun r : this._runs) {
            out.append(r.getText());
        }
        return out.toString();
    }

    @Internal
    public CTTextParagraph getXmlObject() {
        return this._p;
    }

    @Internal
    public CTShape getParentShape() {
        return this._shape;
    }

    public List<XSSFTextRun> getTextRuns() {
        return this._runs;
    }

    @Override // java.lang.Iterable
    public Iterator<XSSFTextRun> iterator() {
        return this._runs.iterator();
    }

    public XSSFTextRun addNewTextRun() {
        CTRegularTextRun r = this._p.addNewR();
        CTTextCharacterProperties rPr = r.addNewRPr();
        rPr.setLang("en-US");
        XSSFTextRun run = new XSSFTextRun(r, this);
        this._runs.add(run);
        return run;
    }

    public XSSFTextRun addLineBreak() {
        CTTextLineBreak br = this._p.addNewBr();
        CTTextCharacterProperties brProps = br.addNewRPr();
        if (this._runs.size() > 0) {
            CTTextCharacterProperties prevRun = this._runs.get(r2.size() - 1).getRPr();
            brProps.set(prevRun);
        }
        CTRegularTextRun r = CTRegularTextRun.Factory.newInstance();
        r.setRPr(brProps);
        r.setT("\n");
        XSSFTextRun run = new XSSFLineBreak(r, this, brProps);
        this._runs.add(run);
        return run;
    }

    public TextAlign getTextAlign() {
        ParagraphPropertyFetcher<TextAlign> fetcher = new ParagraphPropertyFetcher<TextAlign>(getLevel()) { // from class: org.apache.poi.xssf.usermodel.XSSFTextParagraph.1
            @Override // org.apache.poi.xssf.model.ParagraphPropertyFetcher
            public boolean fetch(CTTextParagraphProperties props) {
                if (props.isSetAlgn()) {
                    TextAlign val = TextAlign.values()[props.getAlgn().intValue() - 1];
                    setValue(val);
                    return true;
                }
                return false;
            }
        };
        fetchParagraphProperty(fetcher);
        return fetcher.getValue() == null ? TextAlign.LEFT : fetcher.getValue();
    }

    public void setTextAlign(TextAlign align) {
        CTTextParagraphProperties pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        if (align == null) {
            if (pr.isSetAlgn()) {
                pr.unsetAlgn();
                return;
            }
            return;
        }
        pr.setAlgn(STTextAlignType.Enum.forInt(align.ordinal() + 1));
    }

    public TextFontAlign getTextFontAlign() {
        ParagraphPropertyFetcher<TextFontAlign> fetcher = new ParagraphPropertyFetcher<TextFontAlign>(getLevel()) { // from class: org.apache.poi.xssf.usermodel.XSSFTextParagraph.2
            @Override // org.apache.poi.xssf.model.ParagraphPropertyFetcher
            public boolean fetch(CTTextParagraphProperties props) {
                if (props.isSetFontAlgn()) {
                    TextFontAlign val = TextFontAlign.values()[props.getFontAlgn().intValue() - 1];
                    setValue(val);
                    return true;
                }
                return false;
            }
        };
        fetchParagraphProperty(fetcher);
        return fetcher.getValue() == null ? TextFontAlign.BASELINE : fetcher.getValue();
    }

    public void setTextFontAlign(TextFontAlign align) {
        CTTextParagraphProperties pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        if (align == null) {
            if (pr.isSetFontAlgn()) {
                pr.unsetFontAlgn();
                return;
            }
            return;
        }
        pr.setFontAlgn(STTextFontAlignType.Enum.forInt(align.ordinal() + 1));
    }

    public String getBulletFont() {
        ParagraphPropertyFetcher<String> fetcher = new ParagraphPropertyFetcher<String>(getLevel()) { // from class: org.apache.poi.xssf.usermodel.XSSFTextParagraph.3
            @Override // org.apache.poi.xssf.model.ParagraphPropertyFetcher
            public boolean fetch(CTTextParagraphProperties props) {
                if (props.isSetBuFont()) {
                    setValue(props.getBuFont().getTypeface());
                    return true;
                }
                return false;
            }
        };
        fetchParagraphProperty(fetcher);
        return fetcher.getValue();
    }

    public void setBulletFont(String typeface) {
        CTTextParagraphProperties pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        CTTextFont font = pr.isSetBuFont() ? pr.getBuFont() : pr.addNewBuFont();
        font.setTypeface(typeface);
    }

    public String getBulletCharacter() {
        ParagraphPropertyFetcher<String> fetcher = new ParagraphPropertyFetcher<String>(getLevel()) { // from class: org.apache.poi.xssf.usermodel.XSSFTextParagraph.4
            @Override // org.apache.poi.xssf.model.ParagraphPropertyFetcher
            public boolean fetch(CTTextParagraphProperties props) {
                if (props.isSetBuChar()) {
                    setValue(props.getBuChar().getChar());
                    return true;
                }
                return false;
            }
        };
        fetchParagraphProperty(fetcher);
        return fetcher.getValue();
    }

    public void setBulletCharacter(String str) {
        CTTextParagraphProperties pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        CTTextCharBullet c = pr.isSetBuChar() ? pr.getBuChar() : pr.addNewBuChar();
        c.setChar(str);
    }

    public Color getBulletFontColor() {
        ParagraphPropertyFetcher<Color> fetcher = new ParagraphPropertyFetcher<Color>(getLevel()) { // from class: org.apache.poi.xssf.usermodel.XSSFTextParagraph.5
            @Override // org.apache.poi.xssf.model.ParagraphPropertyFetcher
            public boolean fetch(CTTextParagraphProperties props) {
                if (!props.isSetBuClr() || !props.getBuClr().isSetSrgbClr()) {
                    return false;
                }
                CTSRgbColor clr = props.getBuClr().getSrgbClr();
                byte[] rgb = clr.getVal();
                setValue(new Color(rgb[0] & 255, rgb[1] & 255, rgb[2] & 255));
                return true;
            }
        };
        fetchParagraphProperty(fetcher);
        return fetcher.getValue();
    }

    public void setBulletFontColor(Color color) {
        CTTextParagraphProperties pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        CTColor c = pr.isSetBuClr() ? pr.getBuClr() : pr.addNewBuClr();
        CTSRgbColor clr = c.isSetSrgbClr() ? c.getSrgbClr() : c.addNewSrgbClr();
        clr.setVal(new byte[]{(byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue()});
    }

    public double getBulletFontSize() {
        ParagraphPropertyFetcher<Double> fetcher = new ParagraphPropertyFetcher<Double>(getLevel()) { // from class: org.apache.poi.xssf.usermodel.XSSFTextParagraph.6
            @Override // org.apache.poi.xssf.model.ParagraphPropertyFetcher
            public boolean fetch(CTTextParagraphProperties props) {
                if (props.isSetBuSzPct()) {
                    setValue(Double.valueOf(((double) props.getBuSzPct().getVal()) * 0.001d));
                    return true;
                }
                if (props.isSetBuSzPts()) {
                    setValue(Double.valueOf(((double) (-props.getBuSzPts().getVal())) * 0.01d));
                    return true;
                }
                return false;
            }
        };
        fetchParagraphProperty(fetcher);
        if (fetcher.getValue() == null) {
            return 100.0d;
        }
        return fetcher.getValue().doubleValue();
    }

    public void setBulletFontSize(double bulletSize) {
        CTTextParagraphProperties pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        if (bulletSize >= 0.0d) {
            CTTextBulletSizePercent pt = pr.isSetBuSzPct() ? pr.getBuSzPct() : pr.addNewBuSzPct();
            pt.setVal((int) (1000.0d * bulletSize));
            if (pr.isSetBuSzPts()) {
                pr.unsetBuSzPts();
                return;
            }
            return;
        }
        CTTextBulletSizePoint pt2 = pr.isSetBuSzPts() ? pr.getBuSzPts() : pr.addNewBuSzPts();
        pt2.setVal((int) ((-bulletSize) * 100.0d));
        if (pr.isSetBuSzPct()) {
            pr.unsetBuSzPct();
        }
    }

    public void setIndent(double value) {
        CTTextParagraphProperties pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        if (value == -1.0d) {
            if (pr.isSetIndent()) {
                pr.unsetIndent();
                return;
            }
            return;
        }
        pr.setIndent(Units.toEMU(value));
    }

    public double getIndent() {
        ParagraphPropertyFetcher<Double> fetcher = new ParagraphPropertyFetcher<Double>(getLevel()) { // from class: org.apache.poi.xssf.usermodel.XSSFTextParagraph.7
            @Override // org.apache.poi.xssf.model.ParagraphPropertyFetcher
            public boolean fetch(CTTextParagraphProperties props) {
                if (props.isSetIndent()) {
                    setValue(Double.valueOf(Units.toPoints(props.getIndent())));
                    return true;
                }
                return false;
            }
        };
        fetchParagraphProperty(fetcher);
        if (fetcher.getValue() == null) {
            return 0.0d;
        }
        return fetcher.getValue().doubleValue();
    }

    public void setLeftMargin(double value) {
        CTTextParagraphProperties pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        if (value == -1.0d) {
            if (pr.isSetMarL()) {
                pr.unsetMarL();
                return;
            }
            return;
        }
        pr.setMarL(Units.toEMU(value));
    }

    public double getLeftMargin() {
        ParagraphPropertyFetcher<Double> fetcher = new ParagraphPropertyFetcher<Double>(getLevel()) { // from class: org.apache.poi.xssf.usermodel.XSSFTextParagraph.8
            @Override // org.apache.poi.xssf.model.ParagraphPropertyFetcher
            public boolean fetch(CTTextParagraphProperties props) {
                if (props.isSetMarL()) {
                    double val = Units.toPoints(props.getMarL());
                    setValue(Double.valueOf(val));
                    return true;
                }
                return false;
            }
        };
        fetchParagraphProperty(fetcher);
        if (fetcher.getValue() == null) {
            return 0.0d;
        }
        return fetcher.getValue().doubleValue();
    }

    public void setRightMargin(double value) {
        CTTextParagraphProperties pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        if (value == -1.0d) {
            if (pr.isSetMarR()) {
                pr.unsetMarR();
                return;
            }
            return;
        }
        pr.setMarR(Units.toEMU(value));
    }

    public double getRightMargin() {
        ParagraphPropertyFetcher<Double> fetcher = new ParagraphPropertyFetcher<Double>(getLevel()) { // from class: org.apache.poi.xssf.usermodel.XSSFTextParagraph.9
            @Override // org.apache.poi.xssf.model.ParagraphPropertyFetcher
            public boolean fetch(CTTextParagraphProperties props) {
                if (props.isSetMarR()) {
                    double val = Units.toPoints(props.getMarR());
                    setValue(Double.valueOf(val));
                    return true;
                }
                return false;
            }
        };
        fetchParagraphProperty(fetcher);
        if (fetcher.getValue() == null) {
            return 0.0d;
        }
        return fetcher.getValue().doubleValue();
    }

    public double getDefaultTabSize() {
        ParagraphPropertyFetcher<Double> fetcher = new ParagraphPropertyFetcher<Double>(getLevel()) { // from class: org.apache.poi.xssf.usermodel.XSSFTextParagraph.10
            @Override // org.apache.poi.xssf.model.ParagraphPropertyFetcher
            public boolean fetch(CTTextParagraphProperties props) {
                if (props.isSetDefTabSz()) {
                    double val = Units.toPoints(props.getDefTabSz());
                    setValue(Double.valueOf(val));
                    return true;
                }
                return false;
            }
        };
        fetchParagraphProperty(fetcher);
        if (fetcher.getValue() == null) {
            return 0.0d;
        }
        return fetcher.getValue().doubleValue();
    }

    public double getTabStop(final int idx) {
        ParagraphPropertyFetcher<Double> fetcher = new ParagraphPropertyFetcher<Double>(getLevel()) { // from class: org.apache.poi.xssf.usermodel.XSSFTextParagraph.11
            @Override // org.apache.poi.xssf.model.ParagraphPropertyFetcher
            public boolean fetch(CTTextParagraphProperties props) {
                if (props.isSetTabLst()) {
                    CTTextTabStopList tabStops = props.getTabLst();
                    if (idx < tabStops.sizeOfTabArray()) {
                        CTTextTabStop ts = tabStops.getTabArray(idx);
                        double val = Units.toPoints(ts.getPos());
                        setValue(Double.valueOf(val));
                        return true;
                    }
                    return false;
                }
                return false;
            }
        };
        fetchParagraphProperty(fetcher);
        if (fetcher.getValue() == null) {
            return 0.0d;
        }
        return fetcher.getValue().doubleValue();
    }

    public void addTabStop(double value) {
        CTTextParagraphProperties pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        CTTextTabStopList tabStops = pr.isSetTabLst() ? pr.getTabLst() : pr.addNewTabLst();
        tabStops.addNewTab().setPos(Units.toEMU(value));
    }

    public void setLineSpacing(double linespacing) {
        CTTextParagraphProperties pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        CTTextSpacing spc = CTTextSpacing.Factory.newInstance();
        if (linespacing >= 0.0d) {
            spc.addNewSpcPct().setVal((int) (1000.0d * linespacing));
        } else {
            spc.addNewSpcPts().setVal((int) ((-linespacing) * 100.0d));
        }
        pr.setLnSpc(spc);
    }

    public double getLineSpacing() {
        CTTextNormalAutofit normAutofit;
        ParagraphPropertyFetcher<Double> fetcher = new ParagraphPropertyFetcher<Double>(getLevel()) { // from class: org.apache.poi.xssf.usermodel.XSSFTextParagraph.12
            @Override // org.apache.poi.xssf.model.ParagraphPropertyFetcher
            public boolean fetch(CTTextParagraphProperties props) {
                if (props.isSetLnSpc()) {
                    CTTextSpacing spc = props.getLnSpc();
                    if (!spc.isSetSpcPct()) {
                        if (spc.isSetSpcPts()) {
                            setValue(Double.valueOf(((double) (-spc.getSpcPts().getVal())) * 0.01d));
                            return true;
                        }
                        return true;
                    }
                    setValue(Double.valueOf(((double) spc.getSpcPct().getVal()) * 0.001d));
                    return true;
                }
                return false;
            }
        };
        fetchParagraphProperty(fetcher);
        double lnSpc = fetcher.getValue() == null ? 100.0d : fetcher.getValue().doubleValue();
        if (lnSpc > 0.0d && (normAutofit = this._shape.getTxBody().getBodyPr().getNormAutofit()) != null) {
            double scale = 1.0d - (((double) normAutofit.getLnSpcReduction()) / 100000.0d);
            return lnSpc * scale;
        }
        return lnSpc;
    }

    public void setSpaceBefore(double spaceBefore) {
        CTTextParagraphProperties pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        CTTextSpacing spc = CTTextSpacing.Factory.newInstance();
        if (spaceBefore >= 0.0d) {
            spc.addNewSpcPct().setVal((int) (1000.0d * spaceBefore));
        } else {
            spc.addNewSpcPts().setVal((int) ((-spaceBefore) * 100.0d));
        }
        pr.setSpcBef(spc);
    }

    public double getSpaceBefore() {
        ParagraphPropertyFetcher<Double> fetcher = new ParagraphPropertyFetcher<Double>(getLevel()) { // from class: org.apache.poi.xssf.usermodel.XSSFTextParagraph.13
            @Override // org.apache.poi.xssf.model.ParagraphPropertyFetcher
            public boolean fetch(CTTextParagraphProperties props) {
                if (props.isSetSpcBef()) {
                    CTTextSpacing spc = props.getSpcBef();
                    if (!spc.isSetSpcPct()) {
                        if (spc.isSetSpcPts()) {
                            setValue(Double.valueOf(((double) (-spc.getSpcPts().getVal())) * 0.01d));
                            return true;
                        }
                        return true;
                    }
                    setValue(Double.valueOf(((double) spc.getSpcPct().getVal()) * 0.001d));
                    return true;
                }
                return false;
            }
        };
        fetchParagraphProperty(fetcher);
        if (fetcher.getValue() == null) {
            return 0.0d;
        }
        double spcBef = fetcher.getValue().doubleValue();
        return spcBef;
    }

    public void setSpaceAfter(double spaceAfter) {
        CTTextParagraphProperties pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        CTTextSpacing spc = CTTextSpacing.Factory.newInstance();
        if (spaceAfter >= 0.0d) {
            spc.addNewSpcPct().setVal((int) (1000.0d * spaceAfter));
        } else {
            spc.addNewSpcPts().setVal((int) ((-spaceAfter) * 100.0d));
        }
        pr.setSpcAft(spc);
    }

    public double getSpaceAfter() {
        ParagraphPropertyFetcher<Double> fetcher = new ParagraphPropertyFetcher<Double>(getLevel()) { // from class: org.apache.poi.xssf.usermodel.XSSFTextParagraph.14
            @Override // org.apache.poi.xssf.model.ParagraphPropertyFetcher
            public boolean fetch(CTTextParagraphProperties props) {
                if (props.isSetSpcAft()) {
                    CTTextSpacing spc = props.getSpcAft();
                    if (!spc.isSetSpcPct()) {
                        if (spc.isSetSpcPts()) {
                            setValue(Double.valueOf(((double) (-spc.getSpcPts().getVal())) * 0.01d));
                            return true;
                        }
                        return true;
                    }
                    setValue(Double.valueOf(((double) spc.getSpcPct().getVal()) * 0.001d));
                    return true;
                }
                return false;
            }
        };
        fetchParagraphProperty(fetcher);
        if (fetcher.getValue() == null) {
            return 0.0d;
        }
        return fetcher.getValue().doubleValue();
    }

    public void setLevel(int level) {
        CTTextParagraphProperties pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        pr.setLvl(level);
    }

    public int getLevel() {
        CTTextParagraphProperties pr = this._p.getPPr();
        if (pr == null) {
            return 0;
        }
        return pr.getLvl();
    }

    public boolean isBullet() {
        ParagraphPropertyFetcher<Boolean> fetcher = new ParagraphPropertyFetcher<Boolean>(getLevel()) { // from class: org.apache.poi.xssf.usermodel.XSSFTextParagraph.15
            @Override // org.apache.poi.xssf.model.ParagraphPropertyFetcher
            public boolean fetch(CTTextParagraphProperties props) {
                if (props.isSetBuNone()) {
                    setValue(false);
                    return true;
                }
                if (!props.isSetBuFont() || (!props.isSetBuChar() && !props.isSetBuAutoNum())) {
                    return false;
                }
                setValue(true);
                return true;
            }
        };
        fetchParagraphProperty(fetcher);
        if (fetcher.getValue() == null) {
            return false;
        }
        return fetcher.getValue().booleanValue();
    }

    public void setBullet(boolean flag) {
        if (isBullet() == flag) {
            return;
        }
        CTTextParagraphProperties pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        if (!flag) {
            pr.addNewBuNone();
            if (pr.isSetBuAutoNum()) {
                pr.unsetBuAutoNum();
            }
            if (pr.isSetBuBlip()) {
                pr.unsetBuBlip();
            }
            if (pr.isSetBuChar()) {
                pr.unsetBuChar();
            }
            if (pr.isSetBuClr()) {
                pr.unsetBuClr();
            }
            if (pr.isSetBuClrTx()) {
                pr.unsetBuClrTx();
            }
            if (pr.isSetBuFont()) {
                pr.unsetBuFont();
            }
            if (pr.isSetBuFontTx()) {
                pr.unsetBuFontTx();
            }
            if (pr.isSetBuSzPct()) {
                pr.unsetBuSzPct();
            }
            if (pr.isSetBuSzPts()) {
                pr.unsetBuSzPts();
            }
            if (pr.isSetBuSzTx()) {
                pr.unsetBuSzTx();
                return;
            }
            return;
        }
        if (pr.isSetBuNone()) {
            pr.unsetBuNone();
        }
        if (!pr.isSetBuFont()) {
            pr.addNewBuFont().setTypeface(HSSFFont.FONT_ARIAL);
        }
        if (!pr.isSetBuAutoNum()) {
            pr.addNewBuChar().setChar("•");
        }
    }

    public void setBullet(ListAutoNumber scheme, int startAt) {
        if (startAt < 1) {
            throw new IllegalArgumentException("Start Number must be greater or equal that 1");
        }
        CTTextParagraphProperties pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        CTTextAutonumberBullet lst = pr.isSetBuAutoNum() ? pr.getBuAutoNum() : pr.addNewBuAutoNum();
        lst.setType(STTextAutonumberScheme.Enum.forInt(scheme.ordinal() + 1));
        lst.setStartAt(startAt);
        if (!pr.isSetBuFont()) {
            pr.addNewBuFont().setTypeface(HSSFFont.FONT_ARIAL);
        }
        if (pr.isSetBuNone()) {
            pr.unsetBuNone();
        }
        if (pr.isSetBuBlip()) {
            pr.unsetBuBlip();
        }
        if (pr.isSetBuChar()) {
            pr.unsetBuChar();
        }
    }

    public void setBullet(ListAutoNumber scheme) {
        CTTextParagraphProperties pr = this._p.isSetPPr() ? this._p.getPPr() : this._p.addNewPPr();
        CTTextAutonumberBullet lst = pr.isSetBuAutoNum() ? pr.getBuAutoNum() : pr.addNewBuAutoNum();
        lst.setType(STTextAutonumberScheme.Enum.forInt(scheme.ordinal() + 1));
        if (!pr.isSetBuFont()) {
            pr.addNewBuFont().setTypeface(HSSFFont.FONT_ARIAL);
        }
        if (pr.isSetBuNone()) {
            pr.unsetBuNone();
        }
        if (pr.isSetBuBlip()) {
            pr.unsetBuBlip();
        }
        if (pr.isSetBuChar()) {
            pr.unsetBuChar();
        }
    }

    public boolean isBulletAutoNumber() {
        ParagraphPropertyFetcher<Boolean> fetcher = new ParagraphPropertyFetcher<Boolean>(getLevel()) { // from class: org.apache.poi.xssf.usermodel.XSSFTextParagraph.16
            @Override // org.apache.poi.xssf.model.ParagraphPropertyFetcher
            public boolean fetch(CTTextParagraphProperties props) {
                if (props.isSetBuAutoNum()) {
                    setValue(true);
                    return true;
                }
                return false;
            }
        };
        fetchParagraphProperty(fetcher);
        if (fetcher.getValue() == null) {
            return false;
        }
        return fetcher.getValue().booleanValue();
    }

    public int getBulletAutoNumberStart() {
        ParagraphPropertyFetcher<Integer> fetcher = new ParagraphPropertyFetcher<Integer>(getLevel()) { // from class: org.apache.poi.xssf.usermodel.XSSFTextParagraph.17
            @Override // org.apache.poi.xssf.model.ParagraphPropertyFetcher
            public boolean fetch(CTTextParagraphProperties props) {
                if (props.isSetBuAutoNum() && props.getBuAutoNum().isSetStartAt()) {
                    setValue(Integer.valueOf(props.getBuAutoNum().getStartAt()));
                    return true;
                }
                return false;
            }
        };
        fetchParagraphProperty(fetcher);
        if (fetcher.getValue() == null) {
            return 0;
        }
        return fetcher.getValue().intValue();
    }

    public ListAutoNumber getBulletAutoNumberScheme() {
        ParagraphPropertyFetcher<ListAutoNumber> fetcher = new ParagraphPropertyFetcher<ListAutoNumber>(getLevel()) { // from class: org.apache.poi.xssf.usermodel.XSSFTextParagraph.18
            @Override // org.apache.poi.xssf.model.ParagraphPropertyFetcher
            public boolean fetch(CTTextParagraphProperties props) {
                if (props.isSetBuAutoNum()) {
                    setValue(ListAutoNumber.values()[props.getBuAutoNum().getType().intValue() - 1]);
                    return true;
                }
                return false;
            }
        };
        fetchParagraphProperty(fetcher);
        return fetcher.getValue() == null ? ListAutoNumber.ARABIC_PLAIN : fetcher.getValue();
    }

    private boolean fetchParagraphProperty(ParagraphPropertyFetcher visitor) {
        boolean ok = this._p.isSetPPr() ? visitor.fetch(this._p.getPPr()) : false;
        if (!ok) {
            return visitor.fetch(this._shape);
        }
        return ok;
    }

    public String toString() {
        return "[" + getClass() + "]" + getText();
    }
}
