package com.idrv.coach.utils.helper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import java.lang.reflect.Method;

import static com.idrv.coach.ZjbApplication.gContext;

/**
 * Created by sunjianfei on 15/6/5.
 * util xxUtil(不依赖),xxHelper(独立性不强)
 */
public class UIHelper {

    //动画插值
    private static final DecelerateInterpolator DECCELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);

    /**
     * 弹出toast(持续短时间)
     *
     * @param resId
     */
    public static void shortToast(int resId) {
        Toast.makeText(gContext, resId, Toast.LENGTH_SHORT).show();
    }

    public static void shortToast(String message) {
        Toast.makeText(gContext, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 弹出toast(持续长时间)
     *
     * @param resId
     */
    public static void longToast(int resId) {
        Toast.makeText(gContext, resId, Toast.LENGTH_LONG).show();
    }

    public static void longToast(String message) {
        Toast.makeText(gContext, message, Toast.LENGTH_LONG).show();
    }

    /**
     * 显示软键盘
     *
     * @param view the view
     */
    public static void showSoftInput(final View view) {
        if (null != view) {
            InputMethodManager imm = (InputMethodManager) gContext
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 显示输入法，在网上找的一种方案，不明白为什么要延迟一段时间，如果不延迟，
     * 输入会不显示出来
     */
    public static void showSoftInput(final View input, final long delay) {
        input.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    InputMethodManager imm = (InputMethodManager) gContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, delay);
    }

    /**
     * 隐藏软键盘
     *
     * @param view the view
     */
    public static void hideSoftInput(final View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) gContext
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 判断软键盘是否弹起
     *
     * @return
     */
    public static boolean isSoftInputShow() {
        InputMethodManager imm = (InputMethodManager) gContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.isActive();
    }

    public static void convertActivityFromTranslucent(Activity activity) {
        try {
            Method method = Activity.class.getDeclaredMethod("convertFromTranslucent");
            method.setAccessible(true);
            method.invoke(activity);
        } catch (Throwable t) {
        }
    }

    public static void convertActivityToTranslucent(Activity activity) {
        try {
            Class<?>[] classes = Activity.class.getDeclaredClasses();
            Class<?> translucentConversionListenerClazz = null;
            for (Class clazz : classes) {
                if (clazz.getSimpleName().contains("TranslucentConversionListener")) {
                    translucentConversionListenerClazz = clazz;
                }
            }
            Method method = Activity.class.getDeclaredMethod("convertToTranslucent",
                    translucentConversionListenerClazz);
            method.setAccessible(true);
            method.invoke(activity, new Object[]{
                    null
            });
        } catch (Throwable t) {
        }
    }

    public static void animateHeart(View view) {
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator imgScaleUpYAnim = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 1.2f);
        imgScaleUpYAnim.setDuration(300);
        ObjectAnimator imgScaleUpXAnim = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 1.2f);
        imgScaleUpXAnim.setDuration(300);
        ObjectAnimator imgScaleDownYAnim = ObjectAnimator.ofFloat(view, "scaleY", 1.2f, 1.0f);
        imgScaleDownYAnim.setDuration(300);
        imgScaleDownYAnim.setInterpolator(ACCELERATE_INTERPOLATOR);
        ObjectAnimator imgScaleDownXAnim = ObjectAnimator.ofFloat(view, "scaleX", 1.2f, 1.0f);
        imgScaleDownXAnim.setDuration(300);
        animatorSet.playTogether(imgScaleUpXAnim, imgScaleUpYAnim);
        animatorSet.play(imgScaleDownYAnim).with(imgScaleDownXAnim).after(imgScaleUpXAnim);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.clearAnimation();
                animatorSet.start();
            }
        });
        animatorSet.start();
    }

    /**
     * 照片双击点赞动画
     */
    public static void animatePhotoLike(final ImageView likeBg, final ImageView likeIcon) {
        likeBg.setVisibility(View.VISIBLE);
        likeIcon.setVisibility(View.VISIBLE);

        likeBg.setScaleY(0.1f);
        likeBg.setScaleX(0.1f);
        likeBg.setAlpha(1f);
        likeIcon.setScaleY(0.1f);
        likeIcon.setScaleX(0.1f);

        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator bgScaleYAnim = ObjectAnimator.ofFloat(likeBg, "scaleY", 0.1f, 1f);
        bgScaleYAnim.setDuration(200);
        bgScaleYAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
        ObjectAnimator bgScaleXAnim = ObjectAnimator.ofFloat(likeBg, "scaleX", 0.1f, 1f);
        bgScaleXAnim.setDuration(200);
        bgScaleXAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
        ObjectAnimator bgAlphaAnim = ObjectAnimator.ofFloat(likeBg, "alpha", 1f, 0f);
        bgAlphaAnim.setDuration(200);
        bgAlphaAnim.setStartDelay(150);
        bgAlphaAnim.setInterpolator(DECCELERATE_INTERPOLATOR);

        ObjectAnimator imgScaleUpYAnim = ObjectAnimator.ofFloat(likeIcon, "scaleY", 0.1f, 1f);
        imgScaleUpYAnim.setDuration(300);
        imgScaleUpYAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
        ObjectAnimator imgScaleUpXAnim = ObjectAnimator.ofFloat(likeIcon, "scaleX", 0.1f, 1f);
        imgScaleUpXAnim.setDuration(300);
        imgScaleUpXAnim.setInterpolator(DECCELERATE_INTERPOLATOR);

        ObjectAnimator imgScaleDownYAnim = ObjectAnimator.ofFloat(likeIcon, "scaleY", 1f, 0f);
        imgScaleDownYAnim.setDuration(300);
        imgScaleDownYAnim.setInterpolator(ACCELERATE_INTERPOLATOR);
        ObjectAnimator imgScaleDownXAnim = ObjectAnimator.ofFloat(likeIcon, "scaleX", 1f, 0f);
        imgScaleDownXAnim.setDuration(300);
        imgScaleDownXAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

        animatorSet.playTogether(bgScaleYAnim, bgScaleXAnim, bgAlphaAnim, imgScaleUpYAnim, imgScaleUpXAnim);
        animatorSet.play(imgScaleDownYAnim).with(imgScaleDownXAnim).after(imgScaleUpYAnim);

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                resetLikeAnimationState(likeBg, likeIcon);
            }
        });
        animatorSet.start();
    }

    private static void resetLikeAnimationState(ImageView likeBg, ImageView likeIcon) {
        likeBg.setVisibility(View.GONE);
        likeIcon.setVisibility(View.GONE);
    }


    /**
     * 获取屏幕宽高
     *
     * @param context
     * @return
     */
    public static int[] getScreenSize(Context context) {
        int[] screens;
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();
        screens = new int[]{dm.widthPixels, dm.heightPixels};
        return screens;
    }

    public static void animFlicker(View view,long duration,int repeatCount) {
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator alphaIn = ObjectAnimator.ofFloat(view, "alpha", 0.0f, 1.0f);
        ObjectAnimator alphaOut = ObjectAnimator.ofFloat(view, "alpha", 1.0f, 0.0f);

        alphaIn.setDuration(duration);
        alphaIn.setStartDelay(duration);
        alphaOut.setDuration(duration);

        alphaOut.setRepeatCount(repeatCount);
        alphaIn.setRepeatCount(repeatCount);

        animatorSet.playTogether(alphaOut, alphaIn);
        animatorSet.start();
    }

}
