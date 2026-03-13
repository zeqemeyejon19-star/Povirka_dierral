package com.almeros.android.multitouch;

import android.content.Context;
import android.graphics.PointF;
import android.view.MotionEvent;

/* JADX INFO: loaded from: classes2.dex */
public class MoveGestureDetector extends BaseGestureDetector {
    private static final PointF FOCUS_DELTA_ZERO = new PointF();
    private PointF mCurrFocusInternal;
    private PointF mFocusDeltaExternal;
    private PointF mFocusExternal;
    private final OnMoveGestureListener mListener;
    private PointF mPrevFocusInternal;

    public interface OnMoveGestureListener {
        boolean onMove(MoveGestureDetector moveGestureDetector);

        boolean onMoveBegin(MoveGestureDetector moveGestureDetector);

        void onMoveEnd(MoveGestureDetector moveGestureDetector);
    }

    public static class SimpleOnMoveGestureListener implements OnMoveGestureListener {
        @Override // com.almeros.android.multitouch.MoveGestureDetector.OnMoveGestureListener
        public boolean onMove(MoveGestureDetector detector) {
            return false;
        }

        @Override // com.almeros.android.multitouch.MoveGestureDetector.OnMoveGestureListener
        public boolean onMoveBegin(MoveGestureDetector detector) {
            return true;
        }

        @Override // com.almeros.android.multitouch.MoveGestureDetector.OnMoveGestureListener
        public void onMoveEnd(MoveGestureDetector detector) {
        }
    }

    public MoveGestureDetector(Context context, OnMoveGestureListener listener) {
        super(context);
        this.mFocusExternal = new PointF();
        this.mFocusDeltaExternal = new PointF();
        this.mListener = listener;
    }

    @Override // com.almeros.android.multitouch.BaseGestureDetector
    protected void handleStartProgressEvent(int actionCode, MotionEvent event) {
        if (actionCode != 0) {
            if (actionCode == 2) {
                this.mGestureInProgress = this.mListener.onMoveBegin(this);
            }
        } else {
            resetState();
            this.mPrevEvent = MotionEvent.obtain(event);
            this.mTimeDelta = 0L;
            updateStateByEvent(event);
        }
    }

    @Override // com.almeros.android.multitouch.BaseGestureDetector
    protected void handleInProgressEvent(int actionCode, MotionEvent event) {
        if (actionCode != 1) {
            if (actionCode == 2) {
                if (this.mPrevEvent == null) {
                    return;
                }
                updateStateByEvent(event);
                if (this.mCurrPressure / this.mPrevPressure > 0.67f) {
                    boolean updatePrevious = this.mListener.onMove(this);
                    if (updatePrevious) {
                        this.mPrevEvent.recycle();
                        this.mPrevEvent = MotionEvent.obtain(event);
                        return;
                    }
                    return;
                }
                return;
            }
            if (actionCode != 3) {
                return;
            }
        }
        this.mListener.onMoveEnd(this);
        resetState();
    }

    @Override // com.almeros.android.multitouch.BaseGestureDetector
    protected void updateStateByEvent(MotionEvent curr) {
        super.updateStateByEvent(curr);
        MotionEvent prev = this.mPrevEvent;
        this.mCurrFocusInternal = determineFocalPoint(curr);
        this.mPrevFocusInternal = determineFocalPoint(prev);
        boolean mSkipNextMoveEvent = prev.getPointerCount() != curr.getPointerCount();
        this.mFocusDeltaExternal = mSkipNextMoveEvent ? FOCUS_DELTA_ZERO : new PointF(this.mCurrFocusInternal.x - this.mPrevFocusInternal.x, this.mCurrFocusInternal.y - this.mPrevFocusInternal.y);
        this.mFocusExternal.x += this.mFocusDeltaExternal.x;
        this.mFocusExternal.y += this.mFocusDeltaExternal.y;
    }

    private PointF determineFocalPoint(MotionEvent e) {
        int pCount = e.getPointerCount();
        float x = 0.0f;
        float y = 0.0f;
        for (int i = 0; i < pCount; i++) {
            x += e.getX(i);
            y += e.getY(i);
        }
        return new PointF(x / pCount, y / pCount);
    }

    public float getFocusX() {
        return this.mFocusExternal.x;
    }

    public float getFocusY() {
        return this.mFocusExternal.y;
    }

    public PointF getFocusDelta() {
        return this.mFocusDeltaExternal;
    }
}
