package org.apache.poi.xdgf.geom;

import java.awt.geom.Dimension2D;

/* JADX INFO: loaded from: classes.dex */
public class Dimension2dDouble extends Dimension2D {
    double height;
    double width;

    public Dimension2dDouble() {
        this.width = 0.0d;
        this.height = 0.0d;
    }

    public Dimension2dDouble(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public double getWidth() {
        return this.width;
    }

    public double getHeight() {
        return this.height;
    }

    public void setSize(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Dimension2dDouble)) {
            return false;
        }
        Dimension2dDouble other = (Dimension2dDouble) obj;
        return this.width == other.width && this.height == other.height;
    }

    public int hashCode() {
        double d = this.width;
        double sum = this.height + d;
        return (int) Math.ceil((((1.0d + sum) * sum) / 2.0d) + d);
    }

    public String toString() {
        return "Dimension2dDouble[" + this.width + ", " + this.height + "]";
    }
}
