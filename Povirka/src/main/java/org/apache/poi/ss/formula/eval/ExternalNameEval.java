package org.apache.poi.ss.formula.eval;

import org.apache.poi.ss.formula.EvaluationName;

/* JADX INFO: loaded from: classes.dex */
public final class ExternalNameEval implements ValueEval {
    private final EvaluationName _name;

    public ExternalNameEval(EvaluationName name) {
        this._name = name;
    }

    public EvaluationName getName() {
        return this._name;
    }

    public String toString() {
        return getClass().getName() + " [" + this._name.getNameText() + "]";
    }
}
