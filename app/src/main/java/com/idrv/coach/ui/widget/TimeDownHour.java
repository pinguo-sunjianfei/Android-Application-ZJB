package com.idrv.coach.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.idrv.coach.R;
import com.idrv.coach.utils.PixelUtil;
import com.idrv.coach.utils.helper.ResHelper;

/**
 * Created by bigflower on 2016/1/21.
 */
public class TimeDownHour extends LinearLayout {

    private TimeCount timeCount;

    private TextView timeDownSecond;
    private TextSwitcherView timeDownMinute;
    private TextSwitcherView timeDownHour;

    private boolean isStart;
    private int hourCount, minuteCount, secondCount;
    private CharSequence timeOverStr;
    private int textColor = Color.BLACK;
    private int autoTextSize;

    public TimeDownHour(Context context) {
        super(context);
        initWidget(context);
    }

    public TimeDownHour(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.TimeDown, 0, 0);
        try {
            textColor = a.getColor(R.styleable.TimeDown_switcherColor, Color.BLACK);
        } finally {
            a.recycle();
        }

        initWidget(context);
    }


    public TimeDownHour(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initWidget(context);
    }

    private void initWidget(Context context) {

        initTextSize();

        View v = LayoutInflater.from(context).inflate(R.layout.vw_time_down_minute, this, true);
        timeDownHour = (TextSwitcherView) v.findViewById(R.id.hourTsv);
        timeDownMinute = (TextSwitcherView) v.findViewById(R.id.minuteTsv);
        timeDownSecond = (TextView) v.findViewById(R.id.secondTsv);
        initHour();
        initMinute();
        initMidContent(v);
        initSecond();
    }

    private void initTextSize() {
        autoTextSize = (int) ((ResHelper.getScreenWidth() - PixelUtil.dp2px(95)) / 10);
        autoTextSize = (int) PixelUtil.px2dp(autoTextSize, getContext());
    }

    private void initHour() {
        timeDownHour.setTextSize(autoTextSize);
        timeDownHour.setTextColor(textColor);
    }

    private void initMinute() {
        timeDownMinute.setTextSize(autoTextSize);
        timeDownMinute.setTextColor(textColor);
    }

    private void initSecond() {
        timeDownSecond.setTextSize(autoTextSize);
        timeDownSecond.setTextColor(textColor);
    }

    private void initMidContent(View v) {
        TextView midTextView1 = (TextView) v.findViewById(R.id.midHourTv);
        midTextView1.setTextSize(autoTextSize);
        midTextView1.setTextColor(textColor);
        TextView midTextView2 = (TextView) v.findViewById(R.id.midMinuteTv);
        midTextView2.setTextSize(autoTextSize);
        midTextView2.setTextColor(textColor);
    }

    ///////////////////////////////////////////////
    // outside use
    ///////////////////////////////////////////////
    public TimeDownHour init(long millisInFuture) {
        return init(millisInFuture, "0");
    }

    public TimeDownHour init(long millisInFuture, CharSequence timeOverStr) {
        if (isStart)
            return null;
        if (millisInFuture > 86400000)
            throw new IndexOutOfBoundsException("you can only use a time below 24h(86400s--86400000ms)");
        timeCount = new TimeCount(millisInFuture, 1000);
        int allSeconds = (int) millisInFuture / 1000;
        hourCount = allSeconds / 3600;
        secondCount = allSeconds % 3600 % 60;
        minuteCount = allSeconds % 3600 / 60;
        timeDownHour.setCurrentText(number2Full(hourCount));
        timeDownMinute.setCurrentText(number2Full(minuteCount));
        timeDownSecond.setText(number2Full(secondCount));
        this.timeOverStr = timeOverStr;
        return this;
    }

    public void start() {
        if (isStart) {
            timeCount.cancel();
            timeCount.start();
        } else {
            isStart = true;
            timeCount.start();
        }
    }

    public boolean isStart() {
        return isStart;
    }

    class TimeCount extends CountDownTimer {
        // total time and the interval time
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            isStart = false;
        }

        // the count is doing
        // make the millisUntilFinished divide 1 is to make sure it is not equal with initTime ;
        @Override
        public void onTick(long millisUntilFinished) {
            couting((millisUntilFinished - 1) / 1000);
        }
    }

    private void couting(long second) {
        int hourCount = (int) second / 3600;
        second = second % 3600;
        int secondCount = (int) second % 60;
        int minuteCount = (int) second / 60;

        if (this.secondCount != secondCount) {
            this.secondCount = secondCount;
            timeDownSecond.setText(number2Full(secondCount));
        }
        if (this.minuteCount != minuteCount) {
            this.minuteCount = minuteCount;
            timeDownMinute.setText(number2Full(minuteCount));
        }
        if (this.hourCount != hourCount) {
            this.hourCount = hourCount;
            timeDownHour.setText(number2Full(hourCount));
        }
    }

    /**
     * make the time beauty
     *
     * @return
     */
    private String number2Full(int time) {
        if (time < 10) {
            return "0" + time;
        } else {
            return "" + time;
        }
    }


}