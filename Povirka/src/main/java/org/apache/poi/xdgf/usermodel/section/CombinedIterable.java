package org.apache.poi.xdgf.usermodel.section;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;

/* JADX INFO: loaded from: classes.dex */
public class CombinedIterable<T> implements Iterable<T> {
    final SortedMap<Long, T> _baseItems;
    final SortedMap<Long, T> _masterItems;

    public CombinedIterable(SortedMap<Long, T> baseItems, SortedMap<Long, T> masterItems) {
        this._baseItems = baseItems;
        this._masterItems = masterItems;
    }

    @Override // java.lang.Iterable
    public Iterator<T> iterator() {
        Iterator<Map.Entry<Long, T>> vmasterI;
        SortedMap<Long, T> sortedMap = this._masterItems;
        if (sortedMap != null) {
            vmasterI = sortedMap.entrySet().iterator();
        } else {
            Set<Map.Entry<Long, T>> empty = Collections.emptySet();
            vmasterI = empty.iterator();
        }
        return new Iterator<T>(vmasterI) { // from class: org.apache.poi.xdgf.usermodel.section.CombinedIterable.1
            Iterator<Map.Entry<Long, T>> baseI;
            Iterator<Map.Entry<Long, T>> masterI;
            final /* synthetic */ Iterator val$vmasterI;
            Long lastI = Long.MIN_VALUE;
            Map.Entry<Long, T> currentBase = null;
            Map.Entry<Long, T> currentMaster = null;

            {
                this.val$vmasterI = vmasterI;
                this.baseI = CombinedIterable.this._baseItems.entrySet().iterator();
                this.masterI = vmasterI;
            }

            @Override // java.util.Iterator
            public boolean hasNext() {
                return this.currentBase != null || this.currentMaster != null || this.baseI.hasNext() || this.masterI.hasNext();
            }

            @Override // java.util.Iterator
            public T next() {
                long baseIdx = Long.MAX_VALUE;
                long masterIdx = Long.MAX_VALUE;
                Map.Entry<Long, T> entry = this.currentBase;
                if (entry == null) {
                    while (true) {
                        if (!this.baseI.hasNext()) {
                            break;
                        }
                        Map.Entry<Long, T> next = this.baseI.next();
                        this.currentBase = next;
                        if (next.getKey().longValue() > this.lastI.longValue()) {
                            baseIdx = this.currentBase.getKey().longValue();
                            break;
                        }
                    }
                } else {
                    baseIdx = entry.getKey().longValue();
                }
                Map.Entry<Long, T> entry2 = this.currentMaster;
                if (entry2 == null) {
                    while (true) {
                        if (!this.masterI.hasNext()) {
                            break;
                        }
                        Map.Entry<Long, T> next2 = this.masterI.next();
                        this.currentMaster = next2;
                        if (next2.getKey().longValue() > this.lastI.longValue()) {
                            masterIdx = this.currentMaster.getKey().longValue();
                            break;
                        }
                    }
                } else {
                    masterIdx = entry2.getKey().longValue();
                }
                if (this.currentBase == null) {
                    Map.Entry<Long, T> entry3 = this.currentMaster;
                    if (entry3 != null) {
                        this.lastI = entry3.getKey();
                        T val = this.currentMaster.getValue();
                        this.currentMaster = null;
                        return val;
                    }
                    throw new NoSuchElementException();
                }
                if (baseIdx <= masterIdx) {
                    this.lastI = Long.valueOf(baseIdx);
                    T val2 = this.currentBase.getValue();
                    if (masterIdx == baseIdx) {
                        this.currentMaster = null;
                    }
                    this.currentBase = null;
                    return val2;
                }
                this.lastI = Long.valueOf(masterIdx);
                Map.Entry<Long, T> entry4 = this.currentMaster;
                T val3 = entry4 != null ? entry4.getValue() : null;
                this.currentMaster = null;
                return val3;
            }

            @Override // java.util.Iterator
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
