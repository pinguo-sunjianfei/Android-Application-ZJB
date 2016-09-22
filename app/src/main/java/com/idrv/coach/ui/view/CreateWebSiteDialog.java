package com.idrv.coach.ui.view;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.widget.ProgressBar;

import com.idrv.coach.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * time:2016/6/2
 * description:
 *
 * @author sunjianfei
 */
public class CreateWebSiteDialog extends Dialog {
    @InjectView(R.id.progressBar)
    ProgressBar mProgressBar;

    onAnimEndListener mAnimEndListener;

    private static final int MAX_PROGRESS = 100;

    public CreateWebSiteDialog(Context context) {
        super(context, R.style.BaseDialog);
        //初始化布局
        setContentView(R.layout.vw_create_web_site_dialog);
        ButterKnife.inject(this, this);
    }

    public void startAnim() {
        ValueAnimator animator = ValueAnimator.ofInt(0, 100)
                .setDuration(3000);
        animator.start();
        animator.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            mProgressBar.setProgress(value);
            if (value == MAX_PROGRESS && null != mAnimEndListener) {
                mAnimEndListener.onAnimEnd();
            }
        });
    }

    public void setAnimEndListener(onAnimEndListener mAnimEndListener) {
        this.mAnimEndListener = mAnimEndListener;
    }

    public interface onAnimEndListener {
        void onAnimEnd();
    }

}
