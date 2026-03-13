package org.apache.poi.sl.draw.geom;

import org.apache.poi.sl.draw.binding.CTGeomGuide;

/* JADX INFO: loaded from: classes.dex */
public class Guide implements Formula {
    private final String fmla;
    private final String name;
    private final Op op;
    private final String[] operands;

    enum Op {
        muldiv,
        addsub,
        adddiv,
        ifelse,
        val,
        abs,
        sqrt,
        max,
        min,
        at2,
        sin,
        cos,
        tan,
        cat2,
        sat2,
        pin,
        mod
    }

    public Guide(CTGeomGuide gd) {
        this(gd.getName(), gd.getFmla());
    }

    public Guide(String nm, String fm) {
        this.name = nm;
        this.fmla = fm;
        String[] strArrSplit = fm.split("\\s+");
        this.operands = strArrSplit;
        this.op = Op.valueOf(strArrSplit[0].replace("*", "mul").replace("/", "div").replace("+", "add").replace("-", "sub").replace("?:", "ifelse"));
    }

    public String getName() {
        return this.name;
    }

    String getFormula() {
        return this.fmla;
    }

    @Override // org.apache.poi.sl.draw.geom.Formula
    public double evaluate(Context ctx) {
        String[] strArr = this.operands;
        double x = strArr.length > 1 ? ctx.getValue(strArr[1]) : 0.0d;
        String[] strArr2 = this.operands;
        double y = strArr2.length > 2 ? ctx.getValue(strArr2[2]) : 0.0d;
        String[] strArr3 = this.operands;
        double z = strArr3.length > 3 ? ctx.getValue(strArr3[3]) : 0.0d;
        switch (AnonymousClass1.$SwitchMap$org$apache$poi$sl$draw$geom$Guide$Op[this.op.ordinal()]) {
            case 1:
                return Math.abs(x);
            case 2:
                return (x + y) / z;
            case 3:
                return (x + y) - z;
            case 4:
                return Math.toDegrees(Math.atan2(y, x)) * 60000.0d;
            case 5:
                return Math.cos(Math.toRadians(y / 60000.0d)) * x;
            case 6:
                return Math.cos(Math.atan2(z, y)) * x;
            case 7:
                return x > 0.0d ? y : z;
            case 8:
                return x;
            case 9:
                return Math.max(x, y);
            case 10:
                return Math.min(x, y);
            case 11:
                return Math.sqrt((x * x) + (y * y) + (z * z));
            case 12:
                return (x * y) / z;
            case 13:
                if (y < x) {
                    return x;
                }
                if (y > z) {
                    return z;
                }
                return y;
            case 14:
                return Math.sin(Math.atan2(z, y)) * x;
            case 15:
                return Math.sin(Math.toRadians(y / 60000.0d)) * x;
            case 16:
                return Math.sqrt(x);
            case 17:
                return Math.tan(Math.toRadians(y / 60000.0d)) * x;
            default:
                return 0.0d;
        }
    }

    /* JADX INFO: renamed from: org.apache.poi.sl.draw.geom.Guide$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$sl$draw$geom$Guide$Op;

        static {
            int[] iArr = new int[Op.values().length];
            $SwitchMap$org$apache$poi$sl$draw$geom$Guide$Op = iArr;
            try {
                iArr[Op.abs.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$draw$geom$Guide$Op[Op.adddiv.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$draw$geom$Guide$Op[Op.addsub.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$draw$geom$Guide$Op[Op.at2.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$draw$geom$Guide$Op[Op.cos.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$draw$geom$Guide$Op[Op.cat2.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$draw$geom$Guide$Op[Op.ifelse.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$draw$geom$Guide$Op[Op.val.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$draw$geom$Guide$Op[Op.max.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$draw$geom$Guide$Op[Op.min.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$draw$geom$Guide$Op[Op.mod.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$draw$geom$Guide$Op[Op.muldiv.ordinal()] = 12;
            } catch (NoSuchFieldError e12) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$draw$geom$Guide$Op[Op.pin.ordinal()] = 13;
            } catch (NoSuchFieldError e13) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$draw$geom$Guide$Op[Op.sat2.ordinal()] = 14;
            } catch (NoSuchFieldError e14) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$draw$geom$Guide$Op[Op.sin.ordinal()] = 15;
            } catch (NoSuchFieldError e15) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$draw$geom$Guide$Op[Op.sqrt.ordinal()] = 16;
            } catch (NoSuchFieldError e16) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$draw$geom$Guide$Op[Op.tan.ordinal()] = 17;
            } catch (NoSuchFieldError e17) {
            }
        }
    }
}
