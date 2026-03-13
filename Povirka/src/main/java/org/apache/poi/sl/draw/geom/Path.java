package org.apache.poi.sl.draw.geom;

import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.sl.draw.binding.CTAdjPoint2D;
import org.apache.poi.sl.draw.binding.CTPath2D;
import org.apache.poi.sl.draw.binding.CTPath2DArcTo;
import org.apache.poi.sl.draw.binding.CTPath2DClose;
import org.apache.poi.sl.draw.binding.CTPath2DCubicBezierTo;
import org.apache.poi.sl.draw.binding.CTPath2DLineTo;
import org.apache.poi.sl.draw.binding.CTPath2DMoveTo;
import org.apache.poi.sl.draw.binding.CTPath2DQuadBezierTo;
import org.apache.poi.sl.draw.binding.STPathFillMode;
import org.apache.poi.sl.usermodel.PaintStyle;

/* JADX INFO: loaded from: classes.dex */
public class Path {
    PaintStyle.PaintModifier _fill;
    long _h;
    boolean _stroke;
    long _w;
    private final List<PathCommand> commands;

    public Path() {
        this(true, true);
    }

    public Path(boolean fill, boolean stroke) {
        this.commands = new ArrayList();
        this._w = -1L;
        this._h = -1L;
        this._fill = fill ? PaintStyle.PaintModifier.NORM : PaintStyle.PaintModifier.NONE;
        this._stroke = stroke;
    }

    /* JADX INFO: renamed from: org.apache.poi.sl.draw.geom.Path$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$sl$draw$binding$STPathFillMode;

        static {
            int[] iArr = new int[STPathFillMode.values().length];
            $SwitchMap$org$apache$poi$sl$draw$binding$STPathFillMode = iArr;
            try {
                iArr[STPathFillMode.NONE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$draw$binding$STPathFillMode[STPathFillMode.DARKEN.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$draw$binding$STPathFillMode[STPathFillMode.DARKEN_LESS.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$draw$binding$STPathFillMode[STPathFillMode.LIGHTEN.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$draw$binding$STPathFillMode[STPathFillMode.LIGHTEN_LESS.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$draw$binding$STPathFillMode[STPathFillMode.NORM.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
        }
    }

    public Path(CTPath2D spPath) {
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$sl$draw$binding$STPathFillMode[spPath.getFill().ordinal()];
        if (i == 1) {
            this._fill = PaintStyle.PaintModifier.NONE;
        } else if (i == 2) {
            this._fill = PaintStyle.PaintModifier.DARKEN;
        } else if (i == 3) {
            this._fill = PaintStyle.PaintModifier.DARKEN_LESS;
        } else if (i == 4) {
            this._fill = PaintStyle.PaintModifier.LIGHTEN;
        } else if (i == 5) {
            this._fill = PaintStyle.PaintModifier.LIGHTEN_LESS;
        } else {
            this._fill = PaintStyle.PaintModifier.NORM;
        }
        this._stroke = spPath.isStroke();
        this._w = spPath.isSetW() ? spPath.getW() : -1L;
        this._h = spPath.isSetH() ? spPath.getH() : -1L;
        this.commands = new ArrayList();
        for (Object ch : spPath.getCloseOrMoveToOrLnTo()) {
            if (ch instanceof CTPath2DMoveTo) {
                CTAdjPoint2D pt = ((CTPath2DMoveTo) ch).getPt();
                this.commands.add(new MoveToCommand(pt));
            } else if (ch instanceof CTPath2DLineTo) {
                CTAdjPoint2D pt2 = ((CTPath2DLineTo) ch).getPt();
                this.commands.add(new LineToCommand(pt2));
            } else if (ch instanceof CTPath2DArcTo) {
                CTPath2DArcTo arc = (CTPath2DArcTo) ch;
                this.commands.add(new ArcToCommand(arc));
            } else if (ch instanceof CTPath2DQuadBezierTo) {
                CTPath2DQuadBezierTo bez = (CTPath2DQuadBezierTo) ch;
                CTAdjPoint2D pt1 = bez.getPt().get(0);
                CTAdjPoint2D pt22 = bez.getPt().get(1);
                this.commands.add(new QuadToCommand(pt1, pt22));
            } else if (ch instanceof CTPath2DCubicBezierTo) {
                CTPath2DCubicBezierTo bez2 = (CTPath2DCubicBezierTo) ch;
                CTAdjPoint2D pt12 = bez2.getPt().get(0);
                CTAdjPoint2D pt23 = bez2.getPt().get(1);
                CTAdjPoint2D pt3 = bez2.getPt().get(2);
                this.commands.add(new CurveToCommand(pt12, pt23, pt3));
            } else {
                if (!(ch instanceof CTPath2DClose)) {
                    throw new IllegalStateException("Unsupported path segment: " + ch);
                }
                this.commands.add(new ClosePathCommand());
            }
        }
    }

    public void addCommand(PathCommand cmd) {
        this.commands.add(cmd);
    }

    public Path2D.Double getPath(Context ctx) {
        Path2D.Double path = new Path2D.Double();
        for (PathCommand cmd : this.commands) {
            cmd.execute(path, ctx);
        }
        return path;
    }

    public boolean isStroked() {
        return this._stroke;
    }

    public boolean isFilled() {
        return this._fill != PaintStyle.PaintModifier.NONE;
    }

    public PaintStyle.PaintModifier getFill() {
        return this._fill;
    }

    public long getW() {
        return this._w;
    }

    public long getH() {
        return this._h;
    }
}
