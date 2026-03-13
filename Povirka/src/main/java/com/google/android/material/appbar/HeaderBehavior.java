package com.google.android.material.appbar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.OverScroller;
import androidx.appcompat.widget.ActivityChooserView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.math.MathUtils;
import androidx.core.view.ViewCompat;

/* JADX INFO: loaded from: classes.dex */
abstract class HeaderBehavior<V extends View> extends ViewOffsetBehavior<V> {
    private static final int INVALID_POINTER = -1;
    private int activePointerId;
    private Runnable flingRunnable;
    private boolean isBeingDragged;
    private int lastMotionY;
    OverScroller scroller;
    private int touchSlop;
    private VelocityTracker velocityTracker;

    public HeaderBehavior() {
        this.activePointerId = -1;
        this.touchSlop = -1;
    }

    public HeaderBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.activePointerId = -1;
        this.touchSlop = -1;
    }

    @Override // androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, V child, MotionEvent ev) {
        int pointerIndex;
        if (this.touchSlop < 0) {
            this.touchSlop = ViewConfiguration.get(parent.getContext()).getScaledTouchSlop();
        }
        if (ev.getActionMasked() == 2 && this.isBeingDragged) {
            int i = this.activePointerId;
            if (i == -1 || (pointerIndex = ev.findPointerIndex(i)) == -1) {
                return false;
            }
            int y = (int) ev.getY(pointerIndex);
            int yDiff = Math.abs(y - this.lastMotionY);
            if (yDiff > this.touchSlop) {
                this.lastMotionY = y;
                return true;
            }
        }
        if (ev.getActionMasked() == 0) {
            this.activePointerId = -1;
            int x = (int) ev.getX();
            int y2 = (int) ev.getY();
            boolean z = canDragView(child) && parent.isPointInChildBounds(child, x, y2);
            this.isBeingDragged = z;
            if (z) {
                this.lastMotionY = y2;
                this.activePointerId = ev.getPointerId(0);
                ensureVelocityTracker();
                OverScroller overScroller = this.scroller;
                if (overScroller != null && !overScroller.isFinished()) {
                    this.scroller.abortAnimation();
                    return true;
                }
            }
        }
        VelocityTracker velocityTracker = this.velocityTracker;
        if (velocityTracker != null) {
            velocityTracker.addMovement(ev);
        }
        return false;
    }

    /* JADX WARN: Removed duplicated region for block: B:28:0x0085  */
    @Override // androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean onTouchEvent(androidx.coordinatorlayout.widget.CoordinatorLayout r13, V r14, android.view.MotionEvent r15) {
        /*
            r12 = this;
            r0 = 0
            int r1 = r15.getActionMasked()
            r2 = -1
            r3 = 1
            r4 = 0
            if (r1 == r3) goto L4f
            r5 = 2
            if (r1 == r5) goto L2f
            r5 = 3
            if (r1 == r5) goto L73
            r2 = 6
            if (r1 == r2) goto L15
            goto L81
        L15:
            int r1 = r15.getActionIndex()
            if (r1 != 0) goto L1d
            r1 = 1
            goto L1e
        L1d:
            r1 = 0
        L1e:
            int r2 = r15.getPointerId(r1)
            r12.activePointerId = r2
            float r2 = r15.getY(r1)
            r5 = 1056964608(0x3f000000, float:0.5)
            float r2 = r2 + r5
            int r2 = (int) r2
            r12.lastMotionY = r2
            goto L81
        L2f:
            int r1 = r12.activePointerId
            int r1 = r15.findPointerIndex(r1)
            if (r1 != r2) goto L38
            return r4
        L38:
            float r2 = r15.getY(r1)
            int r2 = (int) r2
            int r5 = r12.lastMotionY
            int r5 = r5 - r2
            r12.lastMotionY = r2
            int r10 = r12.getMaxDragOffset(r14)
            r11 = 0
            r6 = r12
            r7 = r13
            r8 = r14
            r9 = r5
            r6.scroll(r7, r8, r9, r10, r11)
            goto L81
        L4f:
            android.view.VelocityTracker r1 = r12.velocityTracker
            if (r1 == 0) goto L73
            r0 = 1
            r1.addMovement(r15)
            android.view.VelocityTracker r1 = r12.velocityTracker
            r5 = 1000(0x3e8, float:1.401E-42)
            r1.computeCurrentVelocity(r5)
            android.view.VelocityTracker r1 = r12.velocityTracker
            int r5 = r12.activePointerId
            float r1 = r1.getYVelocity(r5)
            int r5 = r12.getScrollRangeForDragFling(r14)
            int r9 = -r5
            r10 = 0
            r6 = r12
            r7 = r13
            r8 = r14
            r11 = r1
            r6.fling(r7, r8, r9, r10, r11)
        L73:
            r12.isBeingDragged = r4
            r12.activePointerId = r2
            android.view.VelocityTracker r1 = r12.velocityTracker
            if (r1 == 0) goto L81
            r1.recycle()
            r1 = 0
            r12.velocityTracker = r1
        L81:
            android.view.VelocityTracker r1 = r12.velocityTracker
            if (r1 == 0) goto L88
            r1.addMovement(r15)
        L88:
            boolean r1 = r12.isBeingDragged
            if (r1 != 0) goto L90
            if (r0 == 0) goto L8f
            goto L90
        L8f:
            r3 = 0
        L90:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.material.appbar.HeaderBehavior.onTouchEvent(androidx.coordinatorlayout.widget.CoordinatorLayout, android.view.View, android.view.MotionEvent):boolean");
    }

    int setHeaderTopBottomOffset(CoordinatorLayout parent, V header, int newOffset) {
        return setHeaderTopBottomOffset(parent, header, newOffset, Integer.MIN_VALUE, ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED);
    }

    int setHeaderTopBottomOffset(CoordinatorLayout parent, V header, int newOffset, int minOffset, int maxOffset) {
        int newOffset2;
        int curOffset = getTopAndBottomOffset();
        if (minOffset == 0 || curOffset < minOffset || curOffset > maxOffset || curOffset == (newOffset2 = MathUtils.clamp(newOffset, minOffset, maxOffset))) {
            return 0;
        }
        setTopAndBottomOffset(newOffset2);
        int consumed = curOffset - newOffset2;
        return consumed;
    }

    int getTopBottomOffsetForScrollingSibling() {
        return getTopAndBottomOffset();
    }

    final int scroll(CoordinatorLayout coordinatorLayout, V header, int dy, int minOffset, int maxOffset) {
        return setHeaderTopBottomOffset(coordinatorLayout, header, getTopBottomOffsetForScrollingSibling() - dy, minOffset, maxOffset);
    }

    final boolean fling(CoordinatorLayout coordinatorLayout, V layout, int minOffset, int maxOffset, float velocityY) {
        Runnable runnable = this.flingRunnable;
        if (runnable != null) {
            layout.removeCallbacks(runnable);
            this.flingRunnable = null;
        }
        if (this.scroller == null) {
            this.scroller = new OverScroller(layout.getContext());
        }
        this.scroller.fling(0, getTopAndBottomOffset(), 0, Math.round(velocityY), 0, 0, minOffset, maxOffset);
        if (this.scroller.computeScrollOffset()) {
            FlingRunnable flingRunnable = new FlingRunnable(coordinatorLayout, layout);
            this.flingRunnable = flingRunnable;
            ViewCompat.postOnAnimation(layout, flingRunnable);
            return true;
        }
        onFlingFinished(coordinatorLayout, layout);
        return false;
    }

    void onFlingFinished(CoordinatorLayout parent, V layout) {
    }

    boolean canDragView(V view) {
        return false;
    }

    int getMaxDragOffset(V view) {
        return -view.getHeight();
    }

    int getScrollRangeForDragFling(V view) {
        return view.getHeight();
    }

    private void ensureVelocityTracker() {
        if (this.velocityTracker == null) {
            this.velocityTracker = VelocityTracker.obtain();
        }
    }

    private class FlingRunnable implements Runnable {
        private final V layout;
        private final CoordinatorLayout parent;

        FlingRunnable(CoordinatorLayout parent, V layout) {
            this.parent = parent;
            this.layout = layout;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (this.layout != null && HeaderBehavior.this.scroller != null) {
                if (HeaderBehavior.this.scroller.computeScrollOffset()) {
                    HeaderBehavior headerBehavior = HeaderBehavior.this;
                    headerBehavior.setHeaderTopBottomOffset(this.parent, this.layout, headerBehavior.scroller.getCurrY());
                    ViewCompat.postOnAnimation(this.layout, this);
                    return;
                }
                HeaderBehavior.this.onFlingFinished(this.parent, this.layout);
            }
        }
    }
}
