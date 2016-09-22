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
public class ReceiveCpDialog extends Dialog {
    TextView mButton;

    public ReceiveCpDialog(Context context) {
        super(context, R.style.BaseDialog);
        //初始化布局
        setContentView(R.layout.vw_receive_communication_programs);
        Window dialogWindow = getWindow();
        dialogWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogWindow.setGravity(Gravity.CENTER);
        mButton = (TextView) findViewById(R.id.btn_receive);
    }

    public void setClickListener(View.OnClickListener listener) {
        mButton.setOnClickListener(listener);
    }
}
