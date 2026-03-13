package org.apache.poi.sl.draw.geom;

import java.awt.geom.Path2D;

/* JADX INFO: loaded from: classes.dex */
public class ClosePathCommand implements PathCommand {
    ClosePathCommand() {
    }

    @Override // org.apache.poi.sl.draw.geom.PathCommand
    public void execute(Path2D.Double path, Context ctx) {
        path.closePath();
    }
}
