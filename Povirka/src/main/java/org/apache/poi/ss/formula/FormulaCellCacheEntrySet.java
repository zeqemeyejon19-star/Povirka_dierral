package org.apache.poi.ss.formula;

/* JADX INFO: loaded from: classes.dex */
final class FormulaCellCacheEntrySet {
    private static final FormulaCellCacheEntry[] EMPTY_ARRAY = new FormulaCellCacheEntry[0];
    private FormulaCellCacheEntry[] _arr = EMPTY_ARRAY;
    private int _size;

    public FormulaCellCacheEntry[] toArray() {
        int nItems = this._size;
        if (nItems < 1) {
            return EMPTY_ARRAY;
        }
        FormulaCellCacheEntry[] result = new FormulaCellCacheEntry[nItems];
        int j = 0;
        int i = 0;
        while (true) {
            FormulaCellCacheEntry[] formulaCellCacheEntryArr = this._arr;
            if (i >= formulaCellCacheEntryArr.length) {
                break;
            }
            FormulaCellCacheEntry cce = formulaCellCacheEntryArr[i];
            if (cce != null) {
                result[j] = cce;
                j++;
            }
            i++;
        }
        if (j != nItems) {
            throw new IllegalStateException("size mismatch");
        }
        return result;
    }

    public void add(CellCacheEntry cce) {
        int i = this._size * 3;
        FormulaCellCacheEntry[] formulaCellCacheEntryArr = this._arr;
        if (i >= formulaCellCacheEntryArr.length * 2) {
            FormulaCellCacheEntry[] prevArr = this._arr;
            FormulaCellCacheEntry[] newArr = new FormulaCellCacheEntry[((formulaCellCacheEntryArr.length * 3) / 2) + 4];
            for (int i2 = 0; i2 < prevArr.length; i2++) {
                FormulaCellCacheEntry prevCce = this._arr[i2];
                if (prevCce != null) {
                    addInternal(newArr, prevCce);
                }
            }
            this._arr = newArr;
        }
        FormulaCellCacheEntry[] prevArr2 = this._arr;
        if (addInternal(prevArr2, cce)) {
            this._size++;
        }
    }

    private static boolean addInternal(CellCacheEntry[] arr, CellCacheEntry cce) {
        int startIx = Math.abs(cce.hashCode() % arr.length);
        for (int i = startIx; i < arr.length; i++) {
            CellCacheEntry item = arr[i];
            if (item == cce) {
                return false;
            }
            if (item == null) {
                arr[i] = cce;
                return true;
            }
        }
        for (int i2 = 0; i2 < startIx; i2++) {
            CellCacheEntry item2 = arr[i2];
            if (item2 == cce) {
                return false;
            }
            if (item2 == null) {
                arr[i2] = cce;
                return true;
            }
        }
        throw new IllegalStateException("No empty space found");
    }

    public boolean remove(CellCacheEntry cce) {
        FormulaCellCacheEntry[] arr = this._arr;
        int i = this._size * 3;
        FormulaCellCacheEntry[] formulaCellCacheEntryArr = this._arr;
        if (i < formulaCellCacheEntryArr.length && formulaCellCacheEntryArr.length > 8) {
            boolean found = false;
            FormulaCellCacheEntry[] prevArr = this._arr;
            FormulaCellCacheEntry[] newArr = new FormulaCellCacheEntry[formulaCellCacheEntryArr.length / 2];
            for (int i2 = 0; i2 < prevArr.length; i2++) {
                FormulaCellCacheEntry prevCce = this._arr[i2];
                if (prevCce != null) {
                    if (prevCce == cce) {
                        found = true;
                        this._size--;
                    } else {
                        addInternal(newArr, prevCce);
                    }
                }
            }
            this._arr = newArr;
            return found;
        }
        int startIx = Math.abs(cce.hashCode() % arr.length);
        for (int i3 = startIx; i3 < arr.length; i3++) {
            FormulaCellCacheEntry item = arr[i3];
            if (item == cce) {
                arr[i3] = null;
                this._size--;
                return true;
            }
        }
        for (int i4 = 0; i4 < startIx; i4++) {
            FormulaCellCacheEntry item2 = arr[i4];
            if (item2 == cce) {
                arr[i4] = null;
                this._size--;
                return true;
            }
        }
        return false;
    }
}
