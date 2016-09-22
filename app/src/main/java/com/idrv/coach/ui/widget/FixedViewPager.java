package com.idrv.coach.ui.widget;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * time:2016/4/25
 * description:
 *
 * @author sunjianfei
 */
public class FixedViewPager extends ViewPager {
    private boolean mIsDisallowIntercept = false;
    protected OnViewPagerTouchListener mOnViewPagerTouchListener;

    public FixedViewPager(Context context) {
        super(context);
    }

    public FixedViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        // keep the info about if the innerViews do
        // requestDisallowInterceptTouchEvent
        mIsDisallowIntercept = disallowIntercept;
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // the incorrect array size will only happen in the multi-touch
        // scenario.
        if (ev.getPointerCount() > 1 && mIsDisallowIntercept) {
            requestDisallowInterceptTouchEvent(false);
            boolean handled = super.dispatchTouchEvent(ev);
            requestDisallowInterceptTouchEvent(true);
            return handled;
        } else {
            return super.dispatchTouchEvent(ev);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (MotionEventCompat.getActionMasked(ev)) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEventCompat.ACTION_POINTER_DOWN:
                if (null != mOnViewPagerTouchListener) {
                    mOnViewPagerTouchListener.onTouchDown();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_POINTER_2_UP:
            case MotionEvent.ACTION_POINTER_3_UP:
            case MotionEvent.ACTION_CANCEL:
                if (null != mOnViewPagerTouchListener) {
                    mOnViewPagerTouchListener.onTouchUp();
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    public interface OnViewPagerTouchListener {
        void onTouchDown();

        void onTouchUp();
    }

    public void setOnViewPagerTouchListener(OnViewPagerTouchListener listener) {
        this.mOnViewPagerTouchListener = listener;
    }
}
