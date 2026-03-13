package org.apache.poi.xssf.usermodel.charts;

import org.apache.poi.ss.usermodel.charts.AxisCrosses;
import org.apache.poi.ss.usermodel.charts.AxisOrientation;
import org.apache.poi.ss.usermodel.charts.AxisPosition;
import org.apache.poi.ss.usermodel.charts.AxisTickMark;
import org.apache.poi.ss.usermodel.charts.ChartAxis;
import org.apache.poi.util.Internal;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxPos;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChartLines;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTCrosses;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLogBase;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumFmt;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTOrientation;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTScaling;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTTickMark;
import org.openxmlformats.schemas.drawingml.x2006.chart.STAxPos;
import org.openxmlformats.schemas.drawingml.x2006.chart.STCrosses;
import org.openxmlformats.schemas.drawingml.x2006.chart.STOrientation;
import org.openxmlformats.schemas.drawingml.x2006.chart.STTickMark;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;

/* JADX INFO: loaded from: classes.dex */
public abstract class XSSFChartAxis implements ChartAxis {
    private static final double MAX_LOG_BASE = 1000.0d;
    private static final double MIN_LOG_BASE = 2.0d;
    protected XSSFChart chart;

    protected abstract CTAxPos getCTAxPos();

    protected abstract CTCrosses getCTCrosses();

    protected abstract CTNumFmt getCTNumFmt();

    protected abstract CTScaling getCTScaling();

    protected abstract CTBoolean getDelete();

    @Internal
    public abstract CTShapeProperties getLine();

    protected abstract CTTickMark getMajorCTTickMark();

    @Internal
    public abstract CTChartLines getMajorGridLines();

    protected abstract CTTickMark getMinorCTTickMark();

    protected XSSFChartAxis(XSSFChart chart) {
        this.chart = chart;
    }

    @Override // org.apache.poi.ss.usermodel.charts.ChartAxis
    public AxisPosition getPosition() {
        return toAxisPosition(getCTAxPos());
    }

    @Override // org.apache.poi.ss.usermodel.charts.ChartAxis
    public void setPosition(AxisPosition position) {
        getCTAxPos().setVal(fromAxisPosition(position));
    }

    @Override // org.apache.poi.ss.usermodel.charts.ChartAxis
    public void setNumberFormat(String format) {
        getCTNumFmt().setFormatCode(format);
        getCTNumFmt().setSourceLinked(true);
    }

    @Override // org.apache.poi.ss.usermodel.charts.ChartAxis
    public String getNumberFormat() {
        return getCTNumFmt().getFormatCode();
    }

    @Override // org.apache.poi.ss.usermodel.charts.ChartAxis
    public boolean isSetLogBase() {
        return getCTScaling().isSetLogBase();
    }

    @Override // org.apache.poi.ss.usermodel.charts.ChartAxis
    public void setLogBase(double logBase) {
        if (logBase < MIN_LOG_BASE || MAX_LOG_BASE < logBase) {
            throw new IllegalArgumentException("Axis log base must be between 2 and 1000 (inclusive), got: " + logBase);
        }
        CTScaling scaling = getCTScaling();
        if (scaling.isSetLogBase()) {
            scaling.getLogBase().setVal(logBase);
        } else {
            scaling.addNewLogBase().setVal(logBase);
        }
    }

    @Override // org.apache.poi.ss.usermodel.charts.ChartAxis
    public double getLogBase() {
        CTLogBase logBase = getCTScaling().getLogBase();
        if (logBase != null) {
            return logBase.getVal();
        }
        return 0.0d;
    }

    @Override // org.apache.poi.ss.usermodel.charts.ChartAxis
    public boolean isSetMinimum() {
        return getCTScaling().isSetMin();
    }

    @Override // org.apache.poi.ss.usermodel.charts.ChartAxis
    public void setMinimum(double min) {
        CTScaling scaling = getCTScaling();
        if (scaling.isSetMin()) {
            scaling.getMin().setVal(min);
        } else {
            scaling.addNewMin().setVal(min);
        }
    }

    @Override // org.apache.poi.ss.usermodel.charts.ChartAxis
    public double getMinimum() {
        CTScaling scaling = getCTScaling();
        if (scaling.isSetMin()) {
            return scaling.getMin().getVal();
        }
        return 0.0d;
    }

    @Override // org.apache.poi.ss.usermodel.charts.ChartAxis
    public boolean isSetMaximum() {
        return getCTScaling().isSetMax();
    }

    @Override // org.apache.poi.ss.usermodel.charts.ChartAxis
    public void setMaximum(double max) {
        CTScaling scaling = getCTScaling();
        if (scaling.isSetMax()) {
            scaling.getMax().setVal(max);
        } else {
            scaling.addNewMax().setVal(max);
        }
    }

    @Override // org.apache.poi.ss.usermodel.charts.ChartAxis
    public double getMaximum() {
        CTScaling scaling = getCTScaling();
        if (scaling.isSetMax()) {
            return scaling.getMax().getVal();
        }
        return 0.0d;
    }

    @Override // org.apache.poi.ss.usermodel.charts.ChartAxis
    public AxisOrientation getOrientation() {
        return toAxisOrientation(getCTScaling().getOrientation());
    }

    @Override // org.apache.poi.ss.usermodel.charts.ChartAxis
    public void setOrientation(AxisOrientation orientation) {
        CTScaling scaling = getCTScaling();
        STOrientation.Enum stOrientation = fromAxisOrientation(orientation);
        if (scaling.isSetOrientation()) {
            scaling.getOrientation().setVal(stOrientation);
        } else {
            getCTScaling().addNewOrientation().setVal(stOrientation);
        }
    }

    @Override // org.apache.poi.ss.usermodel.charts.ChartAxis
    public AxisCrosses getCrosses() {
        return toAxisCrosses(getCTCrosses());
    }

    @Override // org.apache.poi.ss.usermodel.charts.ChartAxis
    public void setCrosses(AxisCrosses crosses) {
        getCTCrosses().setVal(fromAxisCrosses(crosses));
    }

    @Override // org.apache.poi.ss.usermodel.charts.ChartAxis
    public boolean isVisible() {
        return !getDelete().getVal();
    }

    @Override // org.apache.poi.ss.usermodel.charts.ChartAxis
    public void setVisible(boolean value) {
        getDelete().setVal(!value);
    }

    @Override // org.apache.poi.ss.usermodel.charts.ChartAxis
    public AxisTickMark getMajorTickMark() {
        return toAxisTickMark(getMajorCTTickMark());
    }

    @Override // org.apache.poi.ss.usermodel.charts.ChartAxis
    public void setMajorTickMark(AxisTickMark tickMark) {
        getMajorCTTickMark().setVal(fromAxisTickMark(tickMark));
    }

    @Override // org.apache.poi.ss.usermodel.charts.ChartAxis
    public AxisTickMark getMinorTickMark() {
        return toAxisTickMark(getMinorCTTickMark());
    }

    @Override // org.apache.poi.ss.usermodel.charts.ChartAxis
    public void setMinorTickMark(AxisTickMark tickMark) {
        getMinorCTTickMark().setVal(fromAxisTickMark(tickMark));
    }

    private static STOrientation.Enum fromAxisOrientation(AxisOrientation orientation) {
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$ss$usermodel$charts$AxisOrientation[orientation.ordinal()];
        if (i == 1) {
            return STOrientation.MIN_MAX;
        }
        if (i == 2) {
            return STOrientation.MAX_MIN;
        }
        throw new IllegalArgumentException();
    }

    private static AxisOrientation toAxisOrientation(CTOrientation ctOrientation) {
        int iIntValue = ctOrientation.getVal().intValue();
        if (iIntValue == 1) {
            return AxisOrientation.MAX_MIN;
        }
        if (iIntValue == 2) {
            return AxisOrientation.MIN_MAX;
        }
        throw new IllegalArgumentException();
    }

    private static STCrosses.Enum fromAxisCrosses(AxisCrosses crosses) {
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$ss$usermodel$charts$AxisCrosses[crosses.ordinal()];
        if (i == 1) {
            return STCrosses.AUTO_ZERO;
        }
        if (i == 2) {
            return STCrosses.MIN;
        }
        if (i == 3) {
            return STCrosses.MAX;
        }
        throw new IllegalArgumentException();
    }

    private static AxisCrosses toAxisCrosses(CTCrosses ctCrosses) {
        int iIntValue = ctCrosses.getVal().intValue();
        if (iIntValue == 1) {
            return AxisCrosses.AUTO_ZERO;
        }
        if (iIntValue == 2) {
            return AxisCrosses.MAX;
        }
        if (iIntValue == 3) {
            return AxisCrosses.MIN;
        }
        throw new IllegalArgumentException();
    }

    private static STAxPos.Enum fromAxisPosition(AxisPosition position) {
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$ss$usermodel$charts$AxisPosition[position.ordinal()];
        if (i == 1) {
            return STAxPos.B;
        }
        if (i == 2) {
            return STAxPos.L;
        }
        if (i == 3) {
            return STAxPos.R;
        }
        if (i == 4) {
            return STAxPos.T;
        }
        throw new IllegalArgumentException();
    }

    private static AxisPosition toAxisPosition(CTAxPos ctAxPos) {
        int iIntValue = ctAxPos.getVal().intValue();
        if (iIntValue == 1) {
            return AxisPosition.BOTTOM;
        }
        if (iIntValue == 2) {
            return AxisPosition.LEFT;
        }
        if (iIntValue == 3) {
            return AxisPosition.RIGHT;
        }
        if (iIntValue == 4) {
            return AxisPosition.TOP;
        }
        return AxisPosition.BOTTOM;
    }

    /* JADX INFO: renamed from: org.apache.poi.xssf.usermodel.charts.XSSFChartAxis$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$ss$usermodel$charts$AxisCrosses;
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$ss$usermodel$charts$AxisOrientation;
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$ss$usermodel$charts$AxisPosition;
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$ss$usermodel$charts$AxisTickMark;

        static {
            int[] iArr = new int[AxisTickMark.values().length];
            $SwitchMap$org$apache$poi$ss$usermodel$charts$AxisTickMark = iArr;
            try {
                iArr[AxisTickMark.NONE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$charts$AxisTickMark[AxisTickMark.IN.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$charts$AxisTickMark[AxisTickMark.OUT.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$charts$AxisTickMark[AxisTickMark.CROSS.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            int[] iArr2 = new int[AxisPosition.values().length];
            $SwitchMap$org$apache$poi$ss$usermodel$charts$AxisPosition = iArr2;
            try {
                iArr2[AxisPosition.BOTTOM.ordinal()] = 1;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$charts$AxisPosition[AxisPosition.LEFT.ordinal()] = 2;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$charts$AxisPosition[AxisPosition.RIGHT.ordinal()] = 3;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$charts$AxisPosition[AxisPosition.TOP.ordinal()] = 4;
            } catch (NoSuchFieldError e8) {
            }
            int[] iArr3 = new int[AxisCrosses.values().length];
            $SwitchMap$org$apache$poi$ss$usermodel$charts$AxisCrosses = iArr3;
            try {
                iArr3[AxisCrosses.AUTO_ZERO.ordinal()] = 1;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$charts$AxisCrosses[AxisCrosses.MIN.ordinal()] = 2;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$charts$AxisCrosses[AxisCrosses.MAX.ordinal()] = 3;
            } catch (NoSuchFieldError e11) {
            }
            int[] iArr4 = new int[AxisOrientation.values().length];
            $SwitchMap$org$apache$poi$ss$usermodel$charts$AxisOrientation = iArr4;
            try {
                iArr4[AxisOrientation.MIN_MAX.ordinal()] = 1;
            } catch (NoSuchFieldError e12) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$charts$AxisOrientation[AxisOrientation.MAX_MIN.ordinal()] = 2;
            } catch (NoSuchFieldError e13) {
            }
        }
    }

    private static STTickMark.Enum fromAxisTickMark(AxisTickMark tickMark) {
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$ss$usermodel$charts$AxisTickMark[tickMark.ordinal()];
        if (i == 1) {
            return STTickMark.NONE;
        }
        if (i == 2) {
            return STTickMark.IN;
        }
        if (i == 3) {
            return STTickMark.OUT;
        }
        if (i == 4) {
            return STTickMark.CROSS;
        }
        throw new IllegalArgumentException("Unknown AxisTickMark: " + tickMark);
    }

    private static AxisTickMark toAxisTickMark(CTTickMark ctTickMark) {
        int iIntValue = ctTickMark.getVal().intValue();
        if (iIntValue == 1) {
            return AxisTickMark.CROSS;
        }
        if (iIntValue == 2) {
            return AxisTickMark.IN;
        }
        if (iIntValue == 3) {
            return AxisTickMark.NONE;
        }
        if (iIntValue == 4) {
            return AxisTickMark.OUT;
        }
        return AxisTickMark.CROSS;
    }
}
