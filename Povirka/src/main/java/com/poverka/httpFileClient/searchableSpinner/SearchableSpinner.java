package com.poverka.httpFileClient.searchableSpinner;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import androidx.appcompat.widget.AppCompatSpinner;
import com.poverka.httpFileClient.R;
import com.poverka.httpFileClient.searchableSpinner.SearchableListDialog;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class SearchableSpinner extends AppCompatSpinner implements View.OnTouchListener, SearchableListDialog.SearchableItem {
    public static final int NO_ITEM_SELECTED = -1;
    private ArrayAdapter _arrayAdapter;
    private Context _context;
    private boolean _isDirty;
    private boolean _isFromInit;
    private List _items;
    private SearchableListDialog _searchableListDialog;
    private String _strHintText;

    public SearchableSpinner(Context context) {
        super(context);
        this._context = context;
        init();
    }

    public SearchableSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        this._context = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SearchableSpinner);
        int N = a.getIndexCount();
        for (int i = 0; i < N; i++) {
            int attr = a.getIndex(i);
            if (attr == 0) {
                this._strHintText = a.getString(attr);
            }
        }
        a.recycle();
        init();
    }

    public SearchableSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this._context = context;
        init();
    }

    private void init() {
        ArrayList arrayList = new ArrayList();
        this._items = arrayList;
        SearchableListDialog searchableListDialogNewInstance = SearchableListDialog.newInstance(arrayList);
        this._searchableListDialog = searchableListDialogNewInstance;
        searchableListDialogNewInstance.setOnSearchableItemClickListener(this);
        setOnTouchListener(this);
        this._arrayAdapter = (ArrayAdapter) getAdapter();
        if (!TextUtils.isEmpty(this._strHintText)) {
            ArrayAdapter arrayAdapter = new ArrayAdapter(this._context, android.R.layout.simple_list_item_1, new String[]{this._strHintText});
            this._isFromInit = true;
            setAdapter((SpinnerAdapter) arrayAdapter);
        }
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View v, MotionEvent event) {
        if (!this._searchableListDialog.isAdded() && event.getAction() == 1 && this._arrayAdapter != null) {
            this._items.clear();
            for (int i = 0; i < this._arrayAdapter.getCount(); i++) {
                this._items.add(this._arrayAdapter.getItem(i));
            }
            this._searchableListDialog.show(scanForActivity(this._context).getFragmentManager(), "TAG");
        }
        return true;
    }

    @Override // androidx.appcompat.widget.AppCompatSpinner, android.widget.AdapterView
    public void setAdapter(SpinnerAdapter adapter) {
        if (!this._isFromInit) {
            this._arrayAdapter = (ArrayAdapter) adapter;
            if (!TextUtils.isEmpty(this._strHintText) && !this._isDirty) {
                ArrayAdapter arrayAdapter = new ArrayAdapter(this._context, android.R.layout.simple_list_item_1, new String[]{this._strHintText});
                super.setAdapter((SpinnerAdapter) arrayAdapter);
                return;
            } else {
                super.setAdapter(adapter);
                return;
            }
        }
        this._isFromInit = false;
        super.setAdapter(adapter);
    }

    @Override // com.poverka.httpFileClient.searchableSpinner.SearchableListDialog.SearchableItem
    public void onSearchableItemClicked(Object item, int position) {
        setSelection(this._items.indexOf(item));
        if (!this._isDirty) {
            this._isDirty = true;
            setAdapter((SpinnerAdapter) this._arrayAdapter);
            setSelection(this._items.indexOf(item));
        }
    }

    public void setTitle(String strTitle) {
        this._searchableListDialog.setTitle(strTitle);
    }

    public void setPositiveButton(String strPositiveButtonText) {
        this._searchableListDialog.setPositiveButton(strPositiveButtonText);
    }

    public void setPositiveButton(String strPositiveButtonText, DialogInterface.OnClickListener onClickListener) {
        this._searchableListDialog.setPositiveButton(strPositiveButtonText, onClickListener);
    }

    public void setOnSearchTextChangedListener(SearchableListDialog.OnSearchTextChanged onSearchTextChanged) {
        this._searchableListDialog.setOnSearchTextChangedListener(onSearchTextChanged);
    }

    private Activity scanForActivity(Context cont) {
        if (cont == null) {
            return null;
        }
        if (cont instanceof Activity) {
            return (Activity) cont;
        }
        if (!(cont instanceof ContextWrapper)) {
            return null;
        }
        return scanForActivity(((ContextWrapper) cont).getBaseContext());
    }

    @Override // android.widget.AdapterView
    public int getSelectedItemPosition() {
        if (!TextUtils.isEmpty(this._strHintText) && !this._isDirty) {
            return -1;
        }
        return super.getSelectedItemPosition();
    }

    @Override // android.widget.AdapterView
    public Object getSelectedItem() {
        if (!TextUtils.isEmpty(this._strHintText) && !this._isDirty) {
            return null;
        }
        return super.getSelectedItem();
    }
}
