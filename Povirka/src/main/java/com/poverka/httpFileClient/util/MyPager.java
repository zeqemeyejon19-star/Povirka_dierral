package com.poverka.httpFileClient.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.viewpager.widget.ViewPager;

/* JADX INFO: loaded from: classes2.dex */
public class MyPager extends ViewPager {
    public MyPager(Context context) {
        super(context);
    }

    public MyPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // androidx.viewpager.widget.ViewPager, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = (int) (((double) widthMeasureSpec) * 0.75d);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec, height);
            int h = child.getMeasuredHeight();
            if (h > height) {
                height = h;
            }
        }
        if (height != 0) {
            heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, 1073741824);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
