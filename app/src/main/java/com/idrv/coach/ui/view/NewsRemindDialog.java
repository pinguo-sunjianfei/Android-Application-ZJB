package com.idrv.coach.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.idrv.coach.R;

/**
 * time:2016/4/26
 * description:
 *
 * @author sunjianfei
 */
public class NewsRemindDialog extends Dialog implements View.OnClickListener {
    private OnBottomButtonClick mListener;
    TextView mContentTv;

    public NewsRemindDialog(Context context) {
        super(context, R.style.BaseDialog);
        //初始化布局
        setContentView(R.layout.vw_news_remind_dialog);
        Window dialogWindow = getWindow();
        dialogWindow.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogWindow.setGravity(Gravity.CENTER);
        setCanceledOnTouchOutside(true);
        findViewById(R.id.bottom_btn).setOnClickListener(this);
        mContentTv = (TextView) findViewById(R.id.content_tv);
    }


    public void setContent(String content) {
        mContentTv.setText(content);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bottom_btn:
                if (null != mListener) {
                    mListener.onDialogButtonClick();
                }
                break;
        }
    }

    public void setListener(OnBottomButtonClick listener) {
        mListener = listener;
    }

    public interface OnBottomButtonClick {
        void onDialogButtonClick();
    }
}
