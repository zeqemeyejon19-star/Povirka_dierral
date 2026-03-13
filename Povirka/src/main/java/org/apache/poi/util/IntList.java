package org.apache.poi.util;

/* JADX INFO: loaded from: classes.dex */
public class IntList {
    private static final int _default_size = 128;
    private int[] _array;
    private int _limit;
    private int fillval;

    public IntList() {
        this(128);
    }

    public IntList(int initialCapacity) {
        this(initialCapacity, 0);
    }

    public IntList(IntList list) {
        this(list._array.length);
        int[] iArr = list._array;
        int[] iArr2 = this._array;
        System.arraycopy(iArr, 0, iArr2, 0, iArr2.length);
        this._limit = list._limit;
    }

    public IntList(int initialCapacity, int fillvalue) {
        this.fillval = 0;
        this._array = new int[initialCapacity];
        this._limit = 0;
    }

    private void fillArray(int val, int[] array, int index) {
        for (int k = index; k < array.length; k++) {
            array[k] = val;
        }
    }

    public void add(int index, int value) {
        int i = this._limit;
        if (index > i) {
            throw new IndexOutOfBoundsException();
        }
        if (index == i) {
            add(value);
            return;
        }
        if (i == this._array.length) {
            growArray(i * 2);
        }
        int[] iArr = this._array;
        System.arraycopy(iArr, index, iArr, index + 1, this._limit - index);
        this._array[index] = value;
        this._limit++;
    }

    public boolean add(int value) {
        int i = this._limit;
        if (i == this._array.length) {
            growArray(i * 2);
        }
        int[] iArr = this._array;
        int i2 = this._limit;
        this._limit = i2 + 1;
        iArr[i2] = value;
        return true;
    }

    public boolean addAll(IntList c) {
        int i = c._limit;
        if (i != 0) {
            int i2 = this._limit;
            if (i2 + i > this._array.length) {
                growArray(i2 + i);
            }
            System.arraycopy(c._array, 0, this._array, this._limit, c._limit);
            this._limit += c._limit;
            return true;
        }
        return true;
    }

    public boolean addAll(int index, IntList c) {
        int i = this._limit;
        if (index > i) {
            throw new IndexOutOfBoundsException();
        }
        int i2 = c._limit;
        if (i2 != 0) {
            if (i + i2 > this._array.length) {
                growArray(i + i2);
            }
            int[] iArr = this._array;
            System.arraycopy(iArr, index, iArr, c._limit + index, this._limit - index);
            System.arraycopy(c._array, 0, this._array, index, c._limit);
            this._limit += c._limit;
            return true;
        }
        return true;
    }

    public void clear() {
        this._limit = 0;
    }

    public boolean contains(int o) {
        boolean rval = false;
        for (int j = 0; !rval && j < this._limit; j++) {
            if (this._array[j] == o) {
                rval = true;
            }
        }
        return rval;
    }

    public boolean containsAll(IntList c) {
        boolean rval = true;
        if (this != c) {
            for (int j = 0; rval && j < c._limit; j++) {
                if (!contains(c._array[j])) {
                    rval = false;
                }
            }
        }
        return rval;
    }

    public boolean equals(Object o) {
        boolean rval = this == o;
        if (!rval && o != null && o.getClass() == getClass()) {
            IntList other = (IntList) o;
            if (other._limit == this._limit) {
                rval = true;
                for (int j = 0; rval && j < this._limit; j++) {
                    rval = this._array[j] == other._array[j];
                }
            }
        }
        return rval;
    }

    public int get(int index) {
        if (index >= this._limit) {
            throw new IndexOutOfBoundsException(index + " not accessible in a list of length " + this._limit);
        }
        return this._array[index];
    }

    public int hashCode() {
        int hash = 0;
        for (int j = 0; j < this._limit; j++) {
            hash = (hash * 31) + this._array[j];
        }
        return hash;
    }

    public int indexOf(int o) {
        int i;
        int rval = 0;
        while (true) {
            i = this._limit;
            if (rval >= i || o == this._array[rval]) {
                break;
            }
            rval++;
        }
        if (rval == i) {
            return -1;
        }
        return rval;
    }

    public boolean isEmpty() {
        return this._limit == 0;
    }

    public int lastIndexOf(int o) {
        int rval = this._limit - 1;
        while (rval >= 0 && o != this._array[rval]) {
            rval--;
        }
        return rval;
    }

    public int remove(int index) {
        int i = this._limit;
        if (index >= i) {
            throw new IndexOutOfBoundsException();
        }
        int[] iArr = this._array;
        int rval = iArr[index];
        System.arraycopy(iArr, index + 1, iArr, index, i - index);
        this._limit--;
        return rval;
    }

    public boolean removeValue(int o) {
        boolean rval = false;
        int j = 0;
        while (!rval) {
            int i = this._limit;
            if (j >= i) {
                break;
            }
            int[] iArr = this._array;
            if (o == iArr[j]) {
                if (j + 1 < i) {
                    System.arraycopy(iArr, j + 1, iArr, j, i - j);
                }
                this._limit--;
                rval = true;
            }
            j++;
        }
        return rval;
    }

    public boolean removeAll(IntList c) {
        boolean rval = false;
        for (int j = 0; j < c._limit; j++) {
            if (removeValue(c._array[j])) {
                rval = true;
            }
        }
        return rval;
    }

    public boolean retainAll(IntList c) {
        boolean rval = false;
        int j = 0;
        while (j < this._limit) {
            if (!c.contains(this._array[j])) {
                remove(j);
                rval = true;
            } else {
                j++;
            }
        }
        return rval;
    }

    public int set(int index, int element) {
        if (index >= this._limit) {
            throw new IndexOutOfBoundsException();
        }
        int[] iArr = this._array;
        int rval = iArr[index];
        iArr[index] = element;
        return rval;
    }

    public int size() {
        return this._limit;
    }

    public int[] toArray() {
        int i = this._limit;
        int[] rval = new int[i];
        System.arraycopy(this._array, 0, rval, 0, i);
        return rval;
    }

    public int[] toArray(int[] a) {
        int length = a.length;
        int i = this._limit;
        if (length == i) {
            System.arraycopy(this._array, 0, a, 0, i);
            return a;
        }
        int[] rval = toArray();
        return rval;
    }

    private void growArray(int new_size) {
        int[] iArr = this._array;
        int size = new_size == iArr.length ? new_size + 1 : new_size;
        int[] new_array = new int[size];
        int i = this.fillval;
        if (i != 0) {
            fillArray(i, new_array, iArr.length);
        }
        System.arraycopy(this._array, 0, new_array, 0, this._limit);
        this._array = new_array;
    }
}
