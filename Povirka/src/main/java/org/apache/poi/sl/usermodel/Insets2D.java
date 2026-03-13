package org.apache.poi.sl.usermodel;

/* JADX INFO: loaded from: classes.dex */
public final class Insets2D implements Cloneable {
    public double bottom;
    public double left;
    public double right;
    public double top;

    public Insets2D(double top, double left, double bottom, double right) {
        this.top = top;
        this.left = left;
        this.bottom = bottom;
        this.right = right;
    }

    public void set(double top, double left, double bottom, double right) {
        this.top = top;
        this.left = left;
        this.bottom = bottom;
        this.right = right;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Insets2D)) {
            return false;
        }
        Insets2D insets = (Insets2D) obj;
        return this.top == insets.top && this.left == insets.left && this.bottom == insets.bottom && this.right == insets.right;
    }

    public int hashCode() {
        double d = this.left;
        double sum1 = this.bottom + d;
        double d2 = this.right;
        double d3 = this.top;
        double sum2 = d2 + d3;
        double val1 = (((sum1 + 1.0d) * sum1) / 2.0d) + d;
        double val2 = (((sum2 + 1.0d) * sum2) / 2.0d) + d3;
        double sum3 = val1 + val2;
        return (int) ((((1.0d + sum3) * sum3) / 2.0d) + val2);
    }

    public String toString() {
        return getClass().getName() + "[top=" + this.top + ",left=" + this.left + ",bottom=" + this.bottom + ",right=" + this.right + "]";
    }

    public Insets2D clone() {
        return new Insets2D(this.top, this.left, this.bottom, this.right);
    }
}
