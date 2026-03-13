package org.apache.poi.xdgf.geom;

import com.graphbuilder.curve.ControlPath;
import com.graphbuilder.curve.GroupIterator;
import com.graphbuilder.curve.NURBSpline;
import com.graphbuilder.curve.ShapeMultiPath;
import com.graphbuilder.curve.ValueVector;

/* JADX INFO: loaded from: classes.dex */
public class SplineRenderer {
    public static ShapeMultiPath createNurbsSpline(ControlPath controlPoints, ValueVector knots, ValueVector weights, int degree) {
        double firstKnot = knots.get(0);
        int count = knots.size();
        double lastKnot = knots.get(count - 1);
        for (int i = 0; i < count; i++) {
            knots.set((knots.get(i) - firstKnot) / lastKnot, i);
        }
        int i2 = controlPoints.numPoints();
        int knotsToAdd = i2 + degree + 1;
        for (int i3 = count; i3 < knotsToAdd; i3++) {
            knots.add(1.0d);
        }
        GroupIterator gi = new GroupIterator("0:n-1", controlPoints.numPoints());
        NURBSpline spline = new NURBSpline(controlPoints, gi);
        spline.setDegree(degree);
        spline.setKnotVectorType(2);
        spline.setKnotVector(knots);
        if (weights == null) {
            spline.setUseWeightVector(false);
        } else {
            spline.setWeightVector(weights);
        }
        ShapeMultiPath shape = new ShapeMultiPath();
        shape.setFlatness(0.01d);
        spline.appendTo(shape);
        return shape;
    }
}
