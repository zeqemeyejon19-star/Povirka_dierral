package com.almeros.android.multitouch;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/* JADX INFO: loaded from: classes2.dex */
public abstract class TwoFingerGestureDetector extends BaseGestureDetector {
    private float mBottomSlopEdge;
    protected float mCurrFingerDiffX;
    protected float mCurrFingerDiffY;
    private float mCurrLen;
    private final float mEdgeSlop;
    protected float mPrevFingerDiffX;
    protected float mPrevFingerDiffY;
    private float mPrevLen;
    private float mRightSlopEdge;

    @Override // com.almeros.android.multitouch.BaseGestureDetector
    protected abstract void handleInProgressEvent(int i, MotionEvent motionEvent);

    @Override // com.almeros.android.multitouch.BaseGestureDetector
    protected abstract void handleStartProgressEvent(int i, MotionEvent motionEvent);

    public TwoFingerGestureDetector(Context context) {
        super(context);
        ViewConfiguration config = ViewConfiguration.get(context);
        this.mEdgeSlop = config.getScaledEdgeSlop();
    }

    @Override // com.almeros.android.multitouch.BaseGestureDetector
    protected void updateStateByEvent(MotionEvent curr) {
        super.updateStateByEvent(curr);
        MotionEvent prev = this.mPrevEvent;
        this.mCurrLen = -1.0f;
        this.mPrevLen = -1.0f;
        float px0 = prev.getX(0);
        float py0 = prev.getY(0);
        float px1 = prev.getX(1);
        float py1 = prev.getY(1);
        float pvx = px1 - px0;
        float pvy = py1 - py0;
        this.mPrevFingerDiffX = pvx;
        this.mPrevFingerDiffY = pvy;
        float cx0 = curr.getX(0);
        float cy0 = curr.getY(0);
        float cx1 = curr.getX(1);
        float cy1 = curr.getY(1);
        float cvx = cx1 - cx0;
        float cvy = cy1 - cy0;
        this.mCurrFingerDiffX = cvx;
        this.mCurrFingerDiffY = cvy;
    }

    public float getCurrentSpan() {
        if (this.mCurrLen == -1.0f) {
            float cvx = this.mCurrFingerDiffX;
            float cvy = this.mCurrFingerDiffY;
            this.mCurrLen = (float) Math.sqrt((cvx * cvx) + (cvy * cvy));
        }
        return this.mCurrLen;
    }

    public float getPreviousSpan() {
        if (this.mPrevLen == -1.0f) {
            float pvx = this.mPrevFingerDiffX;
            float pvy = this.mPrevFingerDiffY;
            this.mPrevLen = (float) Math.sqrt((pvx * pvx) + (pvy * pvy));
        }
        return this.mPrevLen;
    }

    protected static float getRawX(MotionEvent event, int pointerIndex) {
        float offset = event.getX() - event.getRawX();
        if (pointerIndex < event.getPointerCount()) {
            return event.getX(pointerIndex) + offset;
        }
        return 0.0f;
    }

    protected static float getRawY(MotionEvent event, int pointerIndex) {
        float offset = event.getY() - event.getRawY();
        if (pointerIndex < event.getPointerCount()) {
            return event.getY(pointerIndex) + offset;
        }
        return 0.0f;
    }

    protected boolean isSloppyGesture(MotionEvent event) {
        DisplayMetrics metrics = this.mContext.getResources().getDisplayMetrics();
        this.mRightSlopEdge = metrics.widthPixels - this.mEdgeSlop;
        this.mBottomSlopEdge = metrics.heightPixels - this.mEdgeSlop;
        float edgeSlop = this.mEdgeSlop;
        float rightSlop = this.mRightSlopEdge;
        float bottomSlop = this.mBottomSlopEdge;
        float x0 = event.getRawX();
        float y0 = event.getRawY();
        float x1 = getRawX(event, 1);
        float y1 = getRawY(event, 1);
        boolean p0sloppy = x0 < edgeSlop || y0 < edgeSlop || x0 > rightSlop || y0 > bottomSlop;
        boolean p1sloppy = x1 < edgeSlop || y1 < edgeSlop || x1 > rightSlop || y1 > bottomSlop;
        if ((p0sloppy && p1sloppy) || p0sloppy || p1sloppy) {
            return true;
        }
        return false;
    }
}
