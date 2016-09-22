package com.idrv.coach.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.idrv.coach.R;
import com.idrv.coach.ui.widget.ClearEditText;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * time:2016/3/11
 * description:
 *
 * @author sunjianfei
 */
public class InputItemView extends LinearLayout {
    @InjectView(R.id.left_tv)
    TextView mTextView;
    @InjectView(R.id.input_edit)
    ClearEditText mEditText;
    @InjectView(R.id.h_line)
    View mLine;

    public InputItemView(Context context) {
        super(context);
    }

    public InputItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InputItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    public void setText(int resId) {
        mTextView.setText(resId);
    }

    public void setTextHint(int resId) {
        mEditText.setHint(resId);
    }

    public void setInputType(int inputType) {
        mEditText.setInputType(inputType);
    }

    public String getInputText() {
        return mEditText.getText().toString();
    }

    public void clear() {
        mEditText.setText("");
    }
}
