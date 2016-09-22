package com.idrv.coach.ui.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.idrv.coach.R;

/**
 * Created by MingMing_is_a_beautiful_girl on 2016/1/21.
 * the plus for TextSwitcher, you can use it easily
 * implement the it's ViewFactory instead of add two TextView when we use the TextSwitcher
 */
public class TextSwitcherView extends TextSwitcher implements TextSwitcher.ViewFactory {

    private CharSequence text;
    private int textColor = Color.BLACK;
    private int textSize = 64;

    public TextSwitcherView(Context context, int textColor, int textSize) {
        super(context);
        this.textColor = textColor;
        this.textSize = textSize;
    }

    public TextSwitcherView(Context context) {
        super(context);
        init();
    }

    public TextSwitcherView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.TimeDown, 0, 0);
        try {
            textColor = a.getColor(R.styleable.TimeDown_switcherColor, Color.BLACK);
            textSize = a.getDimensionPixelSize(R.styleable.TimeDown_switcherSize, 16);
        } finally {
            a.recycle();
        }

        init();
    }

    private void init() {
        setMinimumHeight(10);
        initAnim();
        setFactory(this);
    }


    private void initAnim() {
        this.setInAnimation(getContext(), R.anim.slide_in_counter);
        this.setOutAnimation(getContext(), R.anim.slide_out_counter);
    }

    @Override
    public void setText(CharSequence text) {
        super.setText(text);
        this.text = text;
    }

    @Override
    public void setCurrentText(CharSequence text) {
        super.setCurrentText(text);
        this.text = text;
    }

    public CharSequence getText() {
        return text;
    }

    public void setTextSize(int textSize) {
        ((TextView)getChildAt(0)).setTextSize(textSize);
        ((TextView)getChildAt(1)).setTextSize(textSize);
    }

    public void setTextColor(int textColor) {
        ((TextView)getChildAt(0)).setTextColor(textColor);
        ((TextView)getChildAt(1)).setTextColor(textColor);
    }

    @Override
    public View makeView() {
        TextView textView = new TextView(getContext());
        textView.setTextColor(textColor);
        textView.setTextSize(textSize);
        textView.setPadding(3,0,3,0);
        return textView;
    }
}