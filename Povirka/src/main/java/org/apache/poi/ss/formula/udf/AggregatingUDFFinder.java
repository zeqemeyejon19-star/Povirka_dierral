package org.apache.poi.ss.formula.udf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.apache.poi.ss.formula.atp.AnalysisToolPak;
import org.apache.poi.ss.formula.functions.FreeRefFunction;

/* JADX INFO: loaded from: classes.dex */
public class AggregatingUDFFinder implements UDFFinder {
    public static final UDFFinder DEFAULT = new AggregatingUDFFinder(AnalysisToolPak.instance);
    private final Collection<UDFFinder> _usedToolPacks;

    public AggregatingUDFFinder(UDFFinder... usedToolPacks) {
        ArrayList arrayList = new ArrayList(usedToolPacks.length);
        this._usedToolPacks = arrayList;
        arrayList.addAll(Arrays.asList(usedToolPacks));
    }

    @Override // org.apache.poi.ss.formula.udf.UDFFinder
    public FreeRefFunction findFunction(String name) {
        for (UDFFinder pack : this._usedToolPacks) {
            FreeRefFunction evaluatorForFunction = pack.findFunction(name);
            if (evaluatorForFunction != null) {
                return evaluatorForFunction;
            }
        }
        return null;
    }

    public void add(UDFFinder toolPack) {
        this._usedToolPacks.add(toolPack);
    }
}
