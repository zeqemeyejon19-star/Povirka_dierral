package com.poverka.httpFileClient.searchableSpinner;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import com.poverka.httpFileClient.containers.Address;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class SearchableAdapter extends ArrayAdapter<Address> implements Filterable {
    private ArrayList<Address> filteredItems;
    private ArrayList<Address> items;

    public SearchableAdapter(Context context, int resource, ArrayList<Address> objects) {
        super(context, resource, objects);
        this.items = objects;
        this.filteredItems = objects;
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public int getCount() {
        return this.filteredItems.size();
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public Address getItem(int position) {
        return this.filteredItems.get(position);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public List<Address> getAllItems() {
        return this.items;
    }

    @Override // android.widget.ArrayAdapter, android.widget.Filterable
    public Filter getFilter() {
        Filter filter = new Filter() { // from class: com.poverka.httpFileClient.searchableSpinner.SearchableAdapter.1
            @Override // android.widget.Filter
            protected Filter.FilterResults performFiltering(CharSequence constraint) {
                Filter.FilterResults results = new Filter.FilterResults();
                ArrayList<Address> filteredItems = new ArrayList<>();
                List<Address> items = SearchableAdapter.this.getAllItems();
                if (constraint == null || constraint.length() == 0) {
                    results.values = items;
                    results.count = items.size();
                } else {
                    for (Address item : items) {
                        if (item.getName().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                            filteredItems.add(item);
                        }
                    }
                    results.values = filteredItems;
                    results.count = filteredItems.size();
                }
                return results;
            }

            @Override // android.widget.Filter
            protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
                SearchableAdapter.this.filteredItems = (ArrayList) results.values;
                SearchableAdapter.this.notifyDataSetChanged();
            }
        };
        return filter;
    }
}
