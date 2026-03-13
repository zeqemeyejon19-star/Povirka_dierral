package org.apache.poi.xssf.usermodel.charts;

import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Chart;
import org.apache.poi.ss.usermodel.charts.ChartAxis;
import org.apache.poi.ss.usermodel.charts.ChartDataSource;
import org.apache.poi.ss.usermodel.charts.ScatterChartData;
import org.apache.poi.ss.usermodel.charts.ScatterChartSeries;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPlotArea;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTScatterChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTScatterSer;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTScatterStyle;
import org.openxmlformats.schemas.drawingml.x2006.chart.STScatterStyle;

/* JADX INFO: loaded from: classes.dex */
public class XSSFScatterChartData implements ScatterChartData {
    private List<Series> series = new ArrayList();

    static class Series extends AbstractXSSFChartSeries implements ScatterChartSeries {
        private int id;
        private int order;
        private ChartDataSource<?> xs;
        private ChartDataSource<? extends Number> ys;

        protected Series(int id, int order, ChartDataSource<?> xs, ChartDataSource<? extends Number> ys) {
            this.id = id;
            this.order = order;
            this.xs = xs;
            this.ys = ys;
        }

        @Override // org.apache.poi.ss.usermodel.charts.ScatterChartSeries
        public ChartDataSource<?> getXValues() {
            return this.xs;
        }

        @Override // org.apache.poi.ss.usermodel.charts.ScatterChartSeries
        public ChartDataSource<? extends Number> getYValues() {
            return this.ys;
        }

        protected void addToChart(CTScatterChart ctScatterChart) {
            CTScatterSer scatterSer = ctScatterChart.addNewSer();
            scatterSer.addNewIdx().setVal(this.id);
            scatterSer.addNewOrder().setVal(this.order);
            CTAxDataSource xVal = scatterSer.addNewXVal();
            XSSFChartUtil.buildAxDataSource(xVal, this.xs);
            CTNumDataSource yVal = scatterSer.addNewYVal();
            XSSFChartUtil.buildNumDataSource(yVal, this.ys);
            if (isTitleSet()) {
                scatterSer.setTx(getCTSerTx());
            }
        }
    }

    @Override // org.apache.poi.ss.usermodel.charts.ScatterChartData
    public ScatterChartSeries addSerie(ChartDataSource<?> xs, ChartDataSource<? extends Number> ys) {
        if (!ys.isNumeric()) {
            throw new IllegalArgumentException("Y axis data source must be numeric.");
        }
        int numOfSeries = this.series.size();
        Series newSerie = new Series(numOfSeries, numOfSeries, xs, ys);
        this.series.add(newSerie);
        return newSerie;
    }

    @Override // org.apache.poi.ss.usermodel.charts.ChartData
    public void fillChart(Chart chart, ChartAxis... axis) {
        if (!(chart instanceof XSSFChart)) {
            throw new IllegalArgumentException("Chart must be instance of XSSFChart");
        }
        XSSFChart xssfChart = (XSSFChart) chart;
        CTPlotArea plotArea = xssfChart.getCTChart().getPlotArea();
        CTScatterChart scatterChart = plotArea.addNewScatterChart();
        addStyle(scatterChart);
        for (Series s : this.series) {
            s.addToChart(scatterChart);
        }
        for (ChartAxis ax : axis) {
            scatterChart.addNewAxId().setVal(ax.getId());
        }
    }

    @Override // org.apache.poi.ss.usermodel.charts.ScatterChartData
    public List<? extends Series> getSeries() {
        return this.series;
    }

    private void addStyle(CTScatterChart ctScatterChart) {
        CTScatterStyle scatterStyle = ctScatterChart.addNewScatterStyle();
        scatterStyle.setVal(STScatterStyle.LINE_MARKER);
    }
}
