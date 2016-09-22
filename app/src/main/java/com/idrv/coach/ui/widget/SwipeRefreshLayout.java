package com.idrv.coach.ui.widget;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * time: 15/7/27
 * description:上下都能刷新的refreshLayout
 *
 * @author sunjianfei
 */
public class SwipeRefreshLayout extends SwipeRefresh {
    private CircleImageView mBottomCircleView;
    private MaterialProgressDrawable mBottomProgress;
    private int mCircleViewIndexForBottom = -1;
    private int mTargetIndex = -1;
    private int mCurrentTargetOffsetTopForBottom = -1;
    private int mOriginalOffsetTopForBottom = -1;
    private int mActivePointerId = INVALID_POINTER;
    private static final int INVALID_POINTER = -1;
    private boolean mIsBeingDragged;
    private float mInitialMotionY;
    private float mSpinnerFinalOffsetForBottom;
    private float mTotalDragDistanceForBottom = -1;

    private Animation mScaleAnimation;

    private Animation mScaleDownAnimation;

    private Animation mAlphaStartAnimation;

    private Animation mAlphaMaxAnimation;

    private boolean mNotify;

    private OnPullUpRefreshListener mListener;

    private int mFrom;

    private boolean mOriginalOffsetCalculatedForBottom;


    private Animation.AnimationListener mRefreshListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {

            if (mRefreshingForBottom) {
                // Make sure the progress view is fully visible
                mBottomProgress.setAlpha(MAX_ALPHA);
                mBottomProgress.start();
                if (mNotify) {
                    if (mListener != null) {
                        mListener.onPullUpRefresh();
                    }
                }
            } else {
                mBottomProgress.stop();
                mBottomCircleView.setVisibility(View.GONE);
                setColorViewAlpha(MAX_ALPHA);
                setTargetOffsetTopAndBottom(mOriginalOffsetTopForBottom - mCurrentTargetOffsetTopForBottom,
                        true /* requires update */);
            }
            mCurrentTargetOffsetTopForBottom = mBottomCircleView.getTop();
        }
    };


    public SwipeRefreshLayout(Context context) {
        super(context);
        setColorSchemeColors(0xff2f2f2f);
    }

    public SwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        createBottomProgressView();
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        // the absolute offset has to take into account that the circle starts at an offset
        mSpinnerFinalOffsetForBottom = DEFAULT_CIRCLE_TARGET * metrics.density;
        mTotalDragDistanceForBottom = mSpinnerFinalOffsetForBottom;
        //默认的颜色值
        setColorSchemeColors(0xff2f2f2f);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childCount = getChildCount();
        if (childCount != 3) {
            throw new RuntimeException("child count must be == 3");
        }
        mBottomCircleView.measure(View.MeasureSpec.makeMeasureSpec(mCircleWidth, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(mCircleHeight, View.MeasureSpec.EXACTLY));
        mCircleViewIndexForBottom = -1;
        mTargetIndex = -1;
        mCircleViewIndex = -1;
        for (int index = 0; index < getChildCount(); index++) {
            if (getChildAt(index) == mBottomCircleView) {
                mCircleViewIndexForBottom = index;
            } else if (getChildAt(index) == mCircleView) {
                mCircleViewIndex = index;
            } else if (getChildAt(index) == mTarget) {
                mTargetIndex = index;
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (!mOriginalOffsetCalculatedForBottom) {
            mOriginalOffsetCalculatedForBottom = true;
            mCurrentTargetOffsetTopForBottom = mOriginalOffsetTopForBottom = getMeasuredHeight();
        }
        super.onLayout(changed, left, top, right, bottom);
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        int circleWidth = mBottomCircleView.getMeasuredWidth();
        int circleHeight = mBottomCircleView.getMeasuredHeight();
        int bottomViewTopOffset = mCurrentTargetOffsetTopForBottom;
        mBottomCircleView.layout((width / 2 - circleWidth / 2), bottomViewTopOffset,
                (width / 2 + circleWidth / 2), bottomViewTopOffset + circleHeight);
    }

    @Override
    protected void ensureTarget() {
        // Don't bother getting the parent height if the parent hasn't been laid
        // out yet.
        if (mTarget == null) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (!child.equals(mCircleView) && !child.equals(mBottomCircleView)) {
                    mTarget = child;
                    break;
                }
            }
        }
    }

    private void setColorViewAlpha(int targetAlpha) {
        mBottomCircleView.getBackground().setAlpha(targetAlpha);
        mBottomProgress.setAlpha(targetAlpha);
    }

    private void createBottomProgressView() {
        mBottomCircleView = new CircleImageView(getContext(), CIRCLE_BG_LIGHT, CIRCLE_DIAMETER / 2);
        mBottomProgress = new MaterialProgressDrawable(getContext(), this);
        mBottomProgress.setBackgroundColor(CIRCLE_BG_LIGHT);
        mBottomCircleView.setImageDrawable(mBottomProgress);
        mBottomCircleView.setVisibility(View.GONE);
        addView(mBottomCircleView);
    }

    public void setColorSchemeColors(int... colors) {
        ensureTarget();
        mProgress.setColorSchemeColors(colors);
        mBottomProgress.setColorSchemeColors(colors);
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        //目前写死的
        if (i == 0) {
            return mTargetIndex;
        } else if (i == 1) {
            return mCircleViewIndex;
        } else {
            return mCircleViewIndexForBottom;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        mSuperInterceptEvent = super.onInterceptTouchEvent(ev);
        if (mSuperInterceptEvent) {
            return true;
        } else {
            mChildInterceptEvent = mineInterceptTouchEvent(ev);
            return mChildInterceptEvent;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean superHandler = super.onTouchEvent(ev);
        if (superHandler || mSuperInterceptEvent) {
            return true;
        } else {
            return mineTouchEvent(ev);
        }
    }

    private boolean mineInterceptTouchEvent(MotionEvent ev) {
        if (!canExecuteUpRefresh()) {
            return false;
        }
        ensureTarget();
        final int action = MotionEventCompat.getActionMasked(ev);
        if (!isEnabled() || canChildScrollDown() || mRefreshingForBottom || mRefreshing) {
            // Fail fast if we're not in a state where a swipe is possible
            return false;
        }
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                setTargetOffsetTopAndBottomForBottom(mOriginalOffsetTopForBottom - mBottomCircleView.getTop(), true);
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                mIsBeingDragged = false;
                final float initialMotionY = getMotionEventY(ev, mActivePointerId);
                if (initialMotionY == -1) {
                    return false;
                }
                mInitialMotionY = initialMotionY;
            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == INVALID_POINTER) {
                    return false;
                }

                final float y = getMotionEventY(ev, mActivePointerId);
                if (y == -1) {
                    return false;
                }
                final float yDiff = y - mInitialMotionY;
                //上拉<0
                if (Math.abs(yDiff) > (mTouchSlop * 2.0f) && yDiff < 0 && !mIsBeingDragged) {
                    mIsBeingDragged = true;
                    mBottomProgress.setAlpha(STARTING_PROGRESS_ALPHA);
                }
                break;

            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsBeingDragged = false;
                mActivePointerId = INVALID_POINTER;
                break;
        }

        return mIsBeingDragged;
    }

    //只适配4.0以上的机器
    public boolean canChildScrollDown() {
        return ViewCompat.canScrollVertically(mTarget, 1);
    }

    private boolean mineTouchEvent(MotionEvent ev) {
        if (!canExecuteUpRefresh()) {
            return false;
        }
        final int action = MotionEventCompat.getActionMasked(ev);

        if (!isEnabled() || canChildScrollDown()) {
            // Fail fast if we're not in a state where a swipe is possible
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                mIsBeingDragged = false;
                break;

            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }

                final float y = MotionEventCompat.getY(ev, pointerIndex);
                float overscrollTop = (y - mInitialMotionY) * DRAG_RATE;
                if (mIsBeingDragged) {
                    mBottomProgress.showArrow(true);
                    float originalDragPercent = overscrollTop / mTotalDragDistanceForBottom;
                    if (originalDragPercent > 0) {
                        return false;
                    }
                    overscrollTop = Math.abs(overscrollTop);
                    float dragPercent = Math.min(1f, Math.abs(originalDragPercent));
                    float adjustedPercent = (float) Math.max(dragPercent - .4, 0) * 5 / 3;
                    float extraOS = Math.abs(overscrollTop) - mTotalDragDistanceForBottom;
                    float slingshotDist = mSpinnerFinalOffsetForBottom;
                    float tensionSlingshotPercent = Math.max(0,
                            Math.min(extraOS, slingshotDist * 2) / slingshotDist);
                    float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow(
                            (tensionSlingshotPercent / 4), 2)) * 2f;
                    float extraMove = (slingshotDist) * tensionPercent * 2;
                    int targetY = mOriginalOffsetTopForBottom - (int) ((slingshotDist * dragPercent) + extraMove);
                    // where 1.0f is a full circle
                    if (mBottomCircleView.getVisibility() != View.VISIBLE) {
                        mBottomCircleView.setVisibility(View.VISIBLE);
                    }
                    ViewCompat.setScaleX(mBottomCircleView, 1f);
                    ViewCompat.setScaleY(mBottomCircleView, 1f);

                    if (overscrollTop < mTotalDragDistanceForBottom) {
                        if (mBottomProgress.getAlpha() > STARTING_PROGRESS_ALPHA
                                && !isAnimationRunning(mAlphaStartAnimation)) {
                            // Animate the alpha
                            startProgressAlphaStartAnimation();
                        }
                        float strokeStart = (float) (adjustedPercent * .8f);
                        mBottomProgress.setStartEndTrim(0f, Math.min(MAX_PROGRESS_ANGLE, strokeStart));
                        mBottomProgress.setArrowScale(Math.min(1f, adjustedPercent));
                    } else {
                        if (mBottomProgress.getAlpha() < MAX_ALPHA
                                && !isAnimationRunning(mAlphaMaxAnimation)) {
                            // Animate the alpha
                            startProgressAlphaMaxAnimation();
                        }
                    }
                    float rotation = (-0.25f + .4f * adjustedPercent + tensionPercent * 2) * .5f;
                    mBottomProgress.setProgressRotation(rotation);
                    setTargetOffsetTopAndBottom(targetY - mCurrentTargetOffsetTopForBottom, true /* requires update */);
                }
                break;
            }
            case MotionEventCompat.ACTION_POINTER_DOWN: {
                final int index = MotionEventCompat.getActionIndex(ev);
                mActivePointerId = MotionEventCompat.getPointerId(ev, index);
                break;
            }

            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                if (mActivePointerId == INVALID_POINTER) {
                    if (action == MotionEvent.ACTION_UP) {
                    }
                    return false;
                }
                final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                final float y = MotionEventCompat.getY(ev, pointerIndex);
                final float overscrollTop = (y - mInitialMotionY) * DRAG_RATE;
                mIsBeingDragged = false;
                if (Math.abs(overscrollTop) > mTotalDragDistanceForBottom) {
                    setRefreshing(true, true /* notify */);
                } else {
                    // cancel refresh
                    mRefreshingForBottom = false;
                    mBottomProgress.setStartEndTrim(0f, 0f);
                    Animation.AnimationListener listener = null;
                    listener = new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            startScaleDownAnimation(null);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }

                    };
                    animateOffsetToStartPosition(mCurrentTargetOffsetTopForBottom, listener);
                    mBottomProgress.showArrow(false);
                }
                mActivePointerId = INVALID_POINTER;
                return false;
            }
        }

        return true;
    }

    private void setRefreshing(boolean refreshing, final boolean notify) {
        if (mRefreshingForBottom != refreshing) {
            mNotify = notify;
            ensureTarget();
            mRefreshingForBottom = refreshing;
            if (mRefreshingForBottom) {
                animateOffsetToCorrectPosition(mCurrentTargetOffsetTopForBottom, mRefreshListener);
            } else {
                startScaleDownAnimation(mRefreshListener);
            }
        }
    }

    private void animateOffsetToCorrectPosition(int from, Animation.AnimationListener listener) {
        mFrom = from;
        mAnimateToCorrectPosition.reset();
        mAnimateToCorrectPosition.setDuration(ANIMATE_TO_TRIGGER_DURATION);
        mAnimateToCorrectPosition.setInterpolator(mDecelerateInterpolator);
        if (listener != null) {
            mBottomCircleView.setAnimationListener(listener);
        }
        mBottomCircleView.clearAnimation();
        mBottomCircleView.startAnimation(mAnimateToCorrectPosition);
    }

    private final Animation mAnimateToCorrectPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            int targetTop = 0;
            int endTarget = 0;
            if (!mUsingCustomStart) {
                endTarget = (int) (-mSpinnerFinalOffsetForBottom + Math.abs(mOriginalOffsetTopForBottom));
            } else {
                endTarget = (int) mSpinnerFinalOffsetForBottom;
            }
            targetTop = (mFrom + (int) ((endTarget - mFrom) * interpolatedTime));
            int offset = targetTop - mBottomCircleView.getTop();
            setTargetOffsetTopAndBottom(offset, false /* requires update */);
        }
    };

    private void animateOffsetToStartPosition(int from, Animation.AnimationListener listener) {
        mFrom = from;
        mAnimateToStartPosition.reset();
        mAnimateToStartPosition.setDuration(ANIMATE_TO_START_DURATION);
        mAnimateToStartPosition.setInterpolator(mDecelerateInterpolator);
        if (listener != null) {
            mBottomCircleView.setAnimationListener(listener);
        }
        mBottomCircleView.clearAnimation();
        mBottomCircleView.startAnimation(mAnimateToStartPosition);
    }

    private final Animation mAnimateToStartPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            moveToStart(interpolatedTime);
        }
    };

    private void moveToStart(float interpolatedTime) {
        int targetTop = 0;
        targetTop = (mFrom + (int) ((mOriginalOffsetTopForBottom - mFrom) * interpolatedTime));
        int offset = targetTop - mBottomCircleView.getTop();
        setTargetOffsetTopAndBottom(offset, false /* requires update */);
    }

    private void startScaleDownAnimation(Animation.AnimationListener listener) {
        mScaleDownAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                setAnimationProgress(1 - interpolatedTime);
            }
        };
        mScaleDownAnimation.setDuration(SCALE_DOWN_DURATION);
        mBottomCircleView.setAnimationListener(listener);
        mBottomCircleView.clearAnimation();
        mBottomCircleView.startAnimation(mScaleDownAnimation);
    }

    private void setAnimationProgress(float progress) {
        ViewCompat.setScaleX(mBottomCircleView, progress);
        ViewCompat.setScaleY(mBottomCircleView, progress);
    }

    private void setTargetOffsetTopAndBottom(int offset, boolean requiresUpdate) {
        mBottomCircleView.bringToFront();
        mBottomCircleView.offsetTopAndBottom(offset);
        mCurrentTargetOffsetTopForBottom = mBottomCircleView.getTop();
        if (requiresUpdate && android.os.Build.VERSION.SDK_INT < 11) {
            invalidate();
        }
    }

    private void startProgressAlphaMaxAnimation() {
        mAlphaMaxAnimation = startAlphaAnimation(mBottomProgress.getAlpha(), MAX_ALPHA);
    }

    private void startProgressAlphaStartAnimation() {
        mAlphaStartAnimation = startAlphaAnimation(mBottomProgress.getAlpha(), STARTING_PROGRESS_ALPHA);
    }

    private Animation startAlphaAnimation(final int startingAlpha, final int endingAlpha) {
        Animation alpha = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                mBottomProgress
                        .setAlpha((int) (startingAlpha + ((endingAlpha - startingAlpha)
                                * interpolatedTime)));
            }
        };
        alpha.setDuration(ALPHA_ANIMATION_DURATION);
        // Clear out the previous animation listeners.
        mBottomCircleView.setAnimationListener(null);
        mBottomCircleView.clearAnimation();
        mBottomCircleView.startAnimation(alpha);
        return alpha;
    }


    private boolean isAnimationRunning(Animation animation) {
        return animation != null && animation.hasStarted() && !animation.hasEnded();
    }

    private void setTargetOffsetTopAndBottomForBottom(int offset, boolean requiresUpdate) {
        mBottomCircleView.bringToFront();
        mBottomCircleView.offsetTopAndBottom(offset);
        mCurrentTargetOffsetTopForBottom = mBottomCircleView.getTop();
        if (requiresUpdate && android.os.Build.VERSION.SDK_INT < 11) {
            invalidate();
        }
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
        }
    }

    public void setPullUpRefreshing(boolean refreshing) {
        if (refreshing && mRefreshingForBottom != refreshing) {
            // scale and show
            mRefreshingForBottom = refreshing;
            int endTarget = 0;
            if (!mUsingCustomStart) {
                endTarget = (int) (mSpinnerFinalOffsetForBottom + mOriginalOffsetTopForBottom);
            } else {
                endTarget = (int) mSpinnerFinalOffsetForBottom;
            }
            setTargetOffsetTopAndBottom(endTarget - mCurrentTargetOffsetTopForBottom,
                    true /* requires update */);
            mNotify = false;
            startScaleUpAnimation(mRefreshListener);
        } else {
            setRefreshing(refreshing, false /* notify */);
        }
    }

    private void startScaleUpAnimation(Animation.AnimationListener listener) {
        mBottomCircleView.setVisibility(View.VISIBLE);
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            // Pre API 11, alpha is used in place of scale up to show the
            // progress circle appearing.
            // Don't adjust the alpha during appearance otherwise.
            mBottomProgress.setAlpha(MAX_ALPHA);
        }
        mScaleAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                setAnimationProgress(interpolatedTime);
            }
        };
        mScaleAnimation.setDuration(mMediumAnimationDuration);
        if (listener != null) {
            mBottomCircleView.setAnimationListener(listener);
        }
        mBottomCircleView.clearAnimation();
        mBottomCircleView.startAnimation(mScaleAnimation);
    }

    /**
     * Set the listener to be notified when a refresh is triggered via the swipe
     * gesture.
     */
    public void setOnPullUpRefreshListener(OnPullUpRefreshListener listener) {
        mListener = listener;
    }

    public interface OnPullUpRefreshListener {
        void onPullUpRefresh();
    }

}
