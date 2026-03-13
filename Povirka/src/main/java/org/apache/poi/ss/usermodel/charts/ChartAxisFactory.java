package org.apache.poi.ss.usermodel.charts;

/* JADX INFO: loaded from: classes.dex */
public interface ChartAxisFactory {
    ChartAxis createCategoryAxis(AxisPosition axisPosition);

    ChartAxis createDateAxis(AxisPosition axisPosition);

    ValueAxis createValueAxis(AxisPosition axisPosition);
}
