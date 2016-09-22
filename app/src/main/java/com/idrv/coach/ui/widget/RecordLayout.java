
package com.idrv.coach.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.idrv.coach.R;
import com.idrv.coach.utils.Logger;
import com.idrv.coach.utils.PixelUtil;
import com.idrv.coach.utils.helper.ViewUtils;

/**
 * Created by bigflower on 2016/2/26.
 */
public class RecordLayout extends LinearLayout {

    private static final int WIDTH = (int) PixelUtil.dp2px(2);

    private OnRecordLayoutListener mInterface;

    public final int STATE_NULL = -1;
    public final int STATE_NORMAL = 0;
    public final int STATE_PLAYING = 1;
    public final int STATE_PAUSE = 2;
    private int state = STATE_NULL;

    private int angle = 360;
    // 小圆（s） 和  大圆（b）的半径
    private int sRadius, bRadius;
    private Paint backPaint;

    private int mediaDuration;

    private ImageView imageView;

    public RecordLayout(Context context) {
        super(context);
        init();
        setWillNotDraw(false);
    }

    public RecordLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        setWillNotDraw(false);
    }

    public RecordLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        setWillNotDraw(false);
    }

    private void init() {
        Logger.i("RecordLayout", "init");
        initPaint();
        initButton();
    }


    private RectF oval;

    private void initPaint() {
        backPaint = new Paint();
        backPaint.setColor(Color.BLACK);
        backPaint.setAntiAlias(true);
        backPaint.setStyle(Paint.Style.FILL);

        oval = new RectF(WIDTH, WIDTH, PixelUtil.dp2px(154) - WIDTH, PixelUtil.dp2px(154) - WIDTH);
    }

    private void initButton() {
        imageView = new ImageView(getContext());
        imageView.setImageResource(0);
        imageView.setOnClickListener(v -> {
            ViewUtils.setDelayedClickable(v, 1000);
            if (state == STATE_NULL) {
                Logger.e("recordLayout", "怎么可能出现这个");
            } else if (state == STATE_PLAYING) {
                pause();
            } else if (state == STATE_PAUSE) {
                replay();
            } else if (state == STATE_NORMAL) {
                play();
            }
        });
        addView(imageView);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 画圆背景
        backPaint.setStyle(Paint.Style.FILL);
        backPaint.setColor(Color.WHITE);
        canvas.drawCircle(bRadius, bRadius, sRadius - PixelUtil.dp2px(6), backPaint);

        // 画进度条
        backPaint.setStyle(Paint.Style.STROKE);
        backPaint.setStrokeWidth(WIDTH);
        backPaint.setColor(0xffcf3e3b);
        canvas.drawArc(oval, -90, angle, false, backPaint);
    }

    /**
     * @param state
     */
    public void setState(int state) {
        if (state == STATE_NULL) {
            this.state = state;
            imageView.setVisibility(GONE);
        } else if (state == STATE_NORMAL) {
            this.state = state;
            imageView.setVisibility(VISIBLE);
            imageView.setImageResource(R.drawable.img_record_play);
        }
    }

    public void pause() {
        if (mInterface != null) {
            mInterface.pause();
        }
        state = STATE_PAUSE;
        imageView.setImageResource(R.drawable.img_record_play);
    }

    public void play() {
        if (mInterface != null) {
            mInterface.play();
        }
        state = STATE_PLAYING;
        imageView.setVisibility(VISIBLE);
        imageView.setImageResource(R.drawable.img_record_pause);
    }

    public void replay() {
        if (mInterface != null) {
            mInterface.restart();
        }
        state = STATE_PLAYING;
        imageView.setImageResource(R.drawable.img_record_pause);
    }

    public void setProgress(int timeNow) {
        int totalTime = (mediaDuration / 1000);
        if (totalTime == 0)
            return;
        angle = (totalTime - timeNow) * 360 / totalTime;
        invalidate();
    }

    public void setProgressInit() {
        angle = 360;
        invalidate();
    }


    public void setMediaDuration(int mediaDuration) {
        this.mediaDuration = mediaDuration;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        bRadius = this.getMeasuredWidth() / 2;
        sRadius = this.getMeasuredHeight() / 2;
    }

    public interface OnRecordLayoutListener {
        void play();

        void restart();

        void pause();
    }

    public void setOnRecordLayoutListener(OnRecordLayoutListener listener) {
        mInterface = listener;
    }
}
