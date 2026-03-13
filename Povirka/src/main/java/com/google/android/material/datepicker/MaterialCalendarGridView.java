package com.google.android.material.datepicker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.ListAdapter;
import androidx.core.util.Pair;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import com.google.android.material.R;
import java.util.Calendar;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
final class MaterialCalendarGridView extends GridView {
    private final Calendar dayCompute;

    public MaterialCalendarGridView(Context context) {
        this(context, null);
    }

    public MaterialCalendarGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialCalendarGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.dayCompute = UtcDates.getUtcCalendar();
        if (MaterialDatePicker.isFullscreen(getContext())) {
            setNextFocusLeftId(R.id.cancel_button);
            setNextFocusRightId(R.id.confirm_button);
        }
        ViewCompat.setAccessibilityDelegate(this, new AccessibilityDelegateCompat() { // from class: com.google.android.material.datepicker.MaterialCalendarGridView.1
            @Override // androidx.core.view.AccessibilityDelegateCompat
            public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
                accessibilityNodeInfoCompat.setCollectionInfo(null);
            }
        });
    }

    @Override // android.widget.AbsListView, android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getAdapter().notifyDataSetChanged();
    }

    @Override // android.widget.GridView, android.widget.AdapterView
    public void setSelection(int position) {
        if (position < getAdapter().firstPositionInMonth()) {
            super.setSelection(getAdapter().firstPositionInMonth());
        } else {
            super.setSelection(position);
        }
    }

    @Override // android.widget.GridView, android.widget.AbsListView, android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean result = super.onKeyDown(keyCode, event);
        if (!result) {
            return false;
        }
        if (getSelectedItemPosition() == -1 || getSelectedItemPosition() >= getAdapter().firstPositionInMonth()) {
            return true;
        }
        if (19 != keyCode) {
            return false;
        }
        setSelection(getAdapter().firstPositionInMonth());
        return true;
    }

    @Override // android.widget.GridView, android.widget.AdapterView
    public MonthAdapter getAdapter() {
        return (MonthAdapter) super.getAdapter();
    }

    @Override // android.widget.AdapterView
    public final void setAdapter(ListAdapter adapter) {
        if (!(adapter instanceof MonthAdapter)) {
            throw new IllegalArgumentException(String.format("%1$s must have its Adapter set to a %2$s", MaterialCalendarGridView.class.getCanonicalName(), MonthAdapter.class.getCanonicalName()));
        }
        super.setAdapter(adapter);
    }

    @Override // android.view.View
    protected final void onDraw(Canvas canvas) {
        int firstHighlightPosition;
        int rangeHighlightStart;
        int lastHighlightPosition;
        int rangeHighlightEnd;
        MaterialCalendarGridView materialCalendarGridView = this;
        super.onDraw(canvas);
        MonthAdapter monthAdapter = getAdapter();
        DateSelector<?> dateSelector = monthAdapter.dateSelector;
        CalendarStyle calendarStyle = monthAdapter.calendarStyle;
        Long firstOfMonth = monthAdapter.getItem(monthAdapter.firstPositionInMonth());
        Long lastOfMonth = monthAdapter.getItem(monthAdapter.lastPositionInMonth());
        Iterator<Pair<Long, Long>> it = dateSelector.getSelectedRanges().iterator();
        while (it.hasNext()) {
            Pair<Long, Long> range = it.next();
            if (range.first == null) {
                materialCalendarGridView = this;
            } else if (range.second == null) {
                continue;
            } else {
                long startItem = range.first.longValue();
                long endItem = range.second.longValue();
                if (skipMonth(firstOfMonth, lastOfMonth, Long.valueOf(startItem), Long.valueOf(endItem))) {
                    return;
                }
                if (startItem < firstOfMonth.longValue()) {
                    firstHighlightPosition = monthAdapter.firstPositionInMonth();
                    rangeHighlightStart = monthAdapter.isFirstInRow(firstHighlightPosition) ? 0 : materialCalendarGridView.getChildAt(firstHighlightPosition - 1).getRight();
                } else {
                    materialCalendarGridView.dayCompute.setTimeInMillis(startItem);
                    firstHighlightPosition = monthAdapter.dayToPosition(materialCalendarGridView.dayCompute.get(5));
                    rangeHighlightStart = horizontalMidPoint(materialCalendarGridView.getChildAt(firstHighlightPosition));
                }
                if (endItem > lastOfMonth.longValue()) {
                    lastHighlightPosition = Math.min(monthAdapter.lastPositionInMonth(), getChildCount() - 1);
                    if (monthAdapter.isLastInRow(lastHighlightPosition)) {
                        rangeHighlightEnd = getWidth();
                    } else {
                        rangeHighlightEnd = materialCalendarGridView.getChildAt(lastHighlightPosition).getRight();
                    }
                } else {
                    materialCalendarGridView.dayCompute.setTimeInMillis(endItem);
                    lastHighlightPosition = monthAdapter.dayToPosition(materialCalendarGridView.dayCompute.get(5));
                    rangeHighlightEnd = horizontalMidPoint(materialCalendarGridView.getChildAt(lastHighlightPosition));
                }
                Long firstOfMonth2 = firstOfMonth;
                Long lastOfMonth2 = lastOfMonth;
                int firstRow = (int) monthAdapter.getItemId(firstHighlightPosition);
                Iterator<Pair<Long, Long>> it2 = it;
                int lastRow = (int) monthAdapter.getItemId(lastHighlightPosition);
                int row = firstRow;
                while (row <= lastRow) {
                    MonthAdapter monthAdapter2 = monthAdapter;
                    int firstPositionInRow = row * getNumColumns();
                    DateSelector<?> dateSelector2 = dateSelector;
                    int lastPositionInRow = (firstPositionInRow + getNumColumns()) - 1;
                    View firstView = materialCalendarGridView.getChildAt(firstPositionInRow);
                    int top = firstView.getTop() + calendarStyle.day.getTopInset();
                    Iterator<Pair<Long, Long>> it3 = it2;
                    int bottom = firstView.getBottom() - calendarStyle.day.getBottomInset();
                    int left = firstPositionInRow > firstHighlightPosition ? 0 : rangeHighlightStart;
                    int right = lastHighlightPosition > lastPositionInRow ? getWidth() : rangeHighlightEnd;
                    int right2 = firstRow;
                    canvas.drawRect(left, top, right, bottom, calendarStyle.rangeFill);
                    row++;
                    materialCalendarGridView = this;
                    monthAdapter = monthAdapter2;
                    dateSelector = dateSelector2;
                    it2 = it3;
                    firstRow = right2;
                }
                Iterator<Pair<Long, Long>> it4 = it2;
                materialCalendarGridView = this;
                firstOfMonth = firstOfMonth2;
                lastOfMonth = lastOfMonth2;
                it = it4;
            }
        }
    }

    @Override // android.widget.GridView, android.widget.AbsListView, android.view.View
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        if (gainFocus) {
            gainFocus(direction, previouslyFocusedRect);
        } else {
            super.onFocusChanged(false, direction, previouslyFocusedRect);
        }
    }

    private void gainFocus(int direction, Rect previouslyFocusedRect) {
        if (direction == 33) {
            setSelection(getAdapter().lastPositionInMonth());
        } else if (direction == 130) {
            setSelection(getAdapter().firstPositionInMonth());
        } else {
            super.onFocusChanged(true, direction, previouslyFocusedRect);
        }
    }

    private static boolean skipMonth(Long firstOfMonth, Long lastOfMonth, Long startDay, Long endDay) {
        return firstOfMonth == null || lastOfMonth == null || startDay == null || endDay == null || startDay.longValue() > lastOfMonth.longValue() || endDay.longValue() < firstOfMonth.longValue();
    }

    private static int horizontalMidPoint(View view) {
        return view.getLeft() + (view.getWidth() / 2);
    }
}
