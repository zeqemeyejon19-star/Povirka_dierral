package org.apache.poi.sl.draw.geom;

import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import org.apache.poi.sl.draw.binding.CTPath2DArcTo;

/* JADX INFO: loaded from: classes.dex */
public class ArcToCommand implements PathCommand {
    private String hr;
    private String stAng;
    private String swAng;
    private String wr;

    ArcToCommand(CTPath2DArcTo arc) {
        this.hr = arc.getHR();
        this.wr = arc.getWR();
        this.stAng = arc.getStAng();
        this.swAng = arc.getSwAng();
    }

    @Override // org.apache.poi.sl.draw.geom.PathCommand
    public void execute(Path2D.Double path, Context ctx) {
        double rx = ctx.getValue(this.wr);
        double ry = ctx.getValue(this.hr);
        double ooStart = ctx.getValue(this.stAng) / 60000.0d;
        double ooExtent = ctx.getValue(this.swAng) / 60000.0d;
        double awtStart = convertOoxml2AwtAngle(ooStart, rx, ry);
        double awtSweep = convertOoxml2AwtAngle(ooStart + ooExtent, rx, ry) - awtStart;
        double radStart = Math.toRadians(ooStart);
        double invStart = Math.atan2(Math.sin(radStart) * rx, ry * Math.cos(radStart));
        Point2D pt = path.getCurrentPoint();
        double x0 = (pt.getX() - (Math.cos(invStart) * rx)) - rx;
        double y0 = (pt.getY() - (Math.sin(invStart) * ry)) - ry;
        path.append(new Arc2D.Double(x0, y0, rx * 2.0d, ry * 2.0d, awtStart, awtSweep, 0), true);
    }

    private double convertOoxml2AwtAngle(double ooAngle, double width, double height) {
        double aspect = height / width;
        double awtAngle = -ooAngle;
        double awtAngle2 = awtAngle % 360.0d;
        double awtAngle3 = awtAngle - awtAngle2;
        int i = (int) (awtAngle2 / 90.0d);
        if (i == -3) {
            awtAngle3 -= 360.0d;
            awtAngle2 += 360.0d;
        } else if (i == -2 || i == -1) {
            awtAngle3 -= 180.0d;
            awtAngle2 += 180.0d;
        } else if (i == 1 || i == 2) {
            awtAngle3 += 180.0d;
            awtAngle2 -= 180.0d;
        } else if (i == 3) {
            awtAngle3 += 360.0d;
            awtAngle2 -= 360.0d;
        }
        return Math.toDegrees(Math.atan2(Math.tan(Math.toRadians(awtAngle2)), aspect)) + awtAngle3;
    }
}
