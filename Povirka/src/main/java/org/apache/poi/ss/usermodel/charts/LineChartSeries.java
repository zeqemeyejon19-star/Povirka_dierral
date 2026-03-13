package org.apache.poi.ss.usermodel.charts;

/* JADX INFO: loaded from: classes.dex */
public interface LineChartSeries extends ChartSeries {
    ChartDataSource<?> getCategoryAxisData();

    ChartDataSource<? extends Number> getValues();
}
