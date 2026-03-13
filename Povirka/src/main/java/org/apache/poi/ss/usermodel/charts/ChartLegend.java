package org.apache.poi.ss.usermodel.charts;

/* JADX INFO: loaded from: classes.dex */
public interface ChartLegend extends ManuallyPositionable {
    LegendPosition getPosition();

    boolean isOverlay();

    void setOverlay(boolean z);

    void setPosition(LegendPosition legendPosition);
}
