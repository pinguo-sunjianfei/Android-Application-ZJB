package com.idrv.coach.ui.view;

import android.app.Dialog;
import android.content.Context;

import com.idrv.coach.R;

/**
 * time:2016/6/2
 * description:
 *
 * @author sunjianfei
 */
public class MakePosterNotifyDialog extends Dialog {

    public MakePosterNotifyDialog(Context context) {
        super(context, R.style.BaseDialog);
        //初始化布局
        setContentView(R.layout.vw_poster_tips_pop);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
    }

}
