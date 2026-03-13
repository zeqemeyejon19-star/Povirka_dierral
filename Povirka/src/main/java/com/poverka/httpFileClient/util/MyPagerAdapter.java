package com.poverka.httpFileClient.util;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes2.dex */
public class MyPagerAdapter extends PagerAdapter {
    private ArrayList<TextView> views = new ArrayList<>();

    @Override // androidx.viewpager.widget.PagerAdapter
    public int getItemPosition(Object object) {
        int index;
        if (!(object instanceof TextView) || (index = this.views.indexOf(object)) == -1) {
            return -2;
        }
        return index;
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public Object instantiateItem(ViewGroup container, int position) {
        View v = this.views.get(position);
        container.addView(v);
        return v;
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(this.views.get(position));
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public int getCount() {
        return this.views.size();
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public int addView(ViewPager pager, TextView v) {
        return addView(pager, v, this.views.size());
    }

    public int addView(ViewPager pager, TextView v, int position) throws IndexOutOfBoundsException {
        pager.setAdapter(null);
        this.views.add(position, v);
        pager.setAdapter(this);
        return position;
    }

    public int removeView(ViewPager pager, TextView v) {
        return removeView(pager, this.views.indexOf(v));
    }

    public int removeView(ViewPager pager, int position) {
        pager.setAdapter(null);
        this.views.remove(position);
        pager.setAdapter(this);
        return position;
    }

    public View getView(int position) {
        return this.views.get(position);
    }
}
