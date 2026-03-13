package androidx.lifecycle;

/* JADX INFO: loaded from: classes.dex */
public class MutableLiveData<T> extends LiveData<T> {
    @Override // androidx.lifecycle.LiveData
    public void postValue(T value) throws Throwable {
        super.postValue(value);
    }

    @Override // androidx.lifecycle.LiveData
    public void setValue(T value) {
        super.setValue(value);
    }
}
