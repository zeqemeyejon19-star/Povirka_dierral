package org.apache.poi.xssf.usermodel.charts;

import org.apache.poi.ss.usermodel.charts.ChartSeries;
import org.apache.poi.ss.usermodel.charts.TitleType;
import org.apache.poi.ss.util.CellReference;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTSerTx;

/* JADX INFO: loaded from: classes.dex */
public abstract class AbstractXSSFChartSeries implements ChartSeries {
    private CellReference titleRef;
    private TitleType titleType;
    private String titleValue;

    @Override // org.apache.poi.ss.usermodel.charts.ChartSeries
    public void setTitle(CellReference titleReference) {
        this.titleType = TitleType.CELL_REFERENCE;
        this.titleRef = titleReference;
    }

    @Override // org.apache.poi.ss.usermodel.charts.ChartSeries
    public void setTitle(String title) {
        this.titleType = TitleType.STRING;
        this.titleValue = title;
    }

    @Override // org.apache.poi.ss.usermodel.charts.ChartSeries
    public CellReference getTitleCellReference() {
        if (TitleType.CELL_REFERENCE.equals(this.titleType)) {
            return this.titleRef;
        }
        throw new IllegalStateException("Title type is not CellReference.");
    }

    @Override // org.apache.poi.ss.usermodel.charts.ChartSeries
    public String getTitleString() {
        if (TitleType.STRING.equals(this.titleType)) {
            return this.titleValue;
        }
        throw new IllegalStateException("Title type is not String.");
    }

    @Override // org.apache.poi.ss.usermodel.charts.ChartSeries
    public TitleType getTitleType() {
        return this.titleType;
    }

    protected boolean isTitleSet() {
        return this.titleType != null;
    }

    /* JADX INFO: renamed from: org.apache.poi.xssf.usermodel.charts.AbstractXSSFChartSeries$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$ss$usermodel$charts$TitleType;

        static {
            int[] iArr = new int[TitleType.values().length];
            $SwitchMap$org$apache$poi$ss$usermodel$charts$TitleType = iArr;
            try {
                iArr[TitleType.CELL_REFERENCE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$charts$TitleType[TitleType.STRING.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    protected CTSerTx getCTSerTx() {
        CTSerTx tx = CTSerTx.Factory.newInstance();
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$ss$usermodel$charts$TitleType[this.titleType.ordinal()];
        if (i == 1) {
            tx.addNewStrRef().setF(this.titleRef.formatAsString());
            return tx;
        }
        if (i == 2) {
            tx.setV(this.titleValue);
            return tx;
        }
        throw new IllegalStateException("Unkown title type: " + this.titleType);
    }
}
