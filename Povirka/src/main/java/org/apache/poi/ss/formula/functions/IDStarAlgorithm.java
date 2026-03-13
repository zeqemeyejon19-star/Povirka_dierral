package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.ValueEval;

/* JADX INFO: loaded from: classes.dex */
public interface IDStarAlgorithm {
    ValueEval getResult();

    boolean processMatch(ValueEval valueEval);
}
