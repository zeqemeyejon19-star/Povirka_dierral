package org.apache.poi.sl.draw.geom;

import org.apache.poi.sl.draw.binding.CTGeomGuide;

/* JADX INFO: loaded from: classes.dex */
public class AdjustValue extends Guide {
    public AdjustValue(CTGeomGuide gd) {
        super(gd.getName(), gd.getFmla());
    }

    @Override // org.apache.poi.sl.draw.geom.Guide, org.apache.poi.sl.draw.geom.Formula
    public double evaluate(Context ctx) {
        String name = getName();
        Guide adj = ctx.getAdjustValue(name);
        return adj != null ? adj.evaluate(ctx) : super.evaluate(ctx);
    }
}
