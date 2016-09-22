package com.idrv.coach.utils.helper;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.idrv.coach.R;
import com.idrv.coach.ui.adapter.AnimationAdapter;
import com.zjb.loader.ZjbImageLoader;

/**
 * Created by sunjianfei on 15-9-1.
 * description:view的一些操作工具类，比如防止连续快速点击同一个view两次
 *
 * @author crab
 */
public class ViewUtils {
    private static final int ANIMATION_DURATION = 200;
    private static final LinearInterpolator sAnimationInterpolator = new LinearInterpolator();

    private ViewUtils() {

    }

    /**
     * 设置view多少毫秒后可以再次点击
     *
     * @param v
     * @param delayMillis
     */
    public static void setDelayedClickable(final View v, int delayMillis) {
        v.setClickable(false);
        setDelayedClickable(v, true, delayMillis);
    }

    /**
     * 设置view多少毫秒后可以再次点击
     *
     * @param v
     * @param delayMillis
     */
    public static void setDelayedEnable(final View v, int delayMillis) {
        v.setEnabled(false);
        setDelayedClickable(v, true, delayMillis);
    }

    /**
     * 显示圆形头像
     *
     * @param avatarView 显示头像的view
     * @param avatar     头像的url
     */
    public static void showCirCleAvatar(ImageView avatarView, String avatar) {
        showCirCleAvatar(avatarView, avatar, 86);
    }

    /**
     * 显示圆形的头像
     *
     * @param avatarView 显示头像的view
     * @param avatarSize 头像显示的大小
     * @param avatar     头像的url
     */
    public static void showCirCleAvatar(ImageView avatarView, String avatar, int avatarSize) {
        ZjbImageLoader.create(avatar)
                .setQiniu(avatarSize, avatarSize)
                .setDefaultRes(R.drawable.icon_user_avatar_default_92)
                .setDisplayType(ZjbImageLoader.DISPLAY_CIRCLE)
                .into(avatarView);
    }

    /**
     * 显示圆形的头像
     *
     * @param avatarView 显示头像的view
     * @param avatarSize 头像显示的大小
     * @param avatar     头像的url
     */
    public static void showCirCleAvatar(ImageView avatarView, String avatar, int avatarSize, int defaultAvatar) {
        ZjbImageLoader.create(avatar)
                .setQiniu(avatarSize, avatarSize)
                .setDefaultRes(defaultAvatar)
                .setDisplayType(ZjbImageLoader.DISPLAY_CIRCLE)
                .into(avatarView);
    }

    /**
     * 显示方形的头像
     *
     * @param avatarView 显示头像的view
     * @param avatarSize 头像显示的大小
     * @param avatar     头像的url
     */
    public static void showAvatar(ImageView avatarView, String avatar, int avatarSize) {
        ZjbImageLoader.create(avatar)
                .setQiniu(avatarSize, avatarSize)
                .setDefaultRes(R.drawable.icon_user_avatar_default_92)
                .setDisplayType(ZjbImageLoader.DISPLAY_DEFAULT)
                .into(avatarView);
    }

    /**
     * 显示一个圆环头像
     *
     * @param avatarView 显示头像的view
     * @param avatar     头像的url
     */
    public static void showRingAvatar(ImageView avatarView, String avatar) {
        showRingAvatar(avatarView, avatar, 86);
    }

    /**
     * 显示一个圆环头像
     *
     * @param avatarView 显示头像的view
     * @param avatar     头像的url
     * @param avatarSize 头像显示的大小
     */
    public static void showRingAvatar(ImageView avatarView, String avatar, int avatarSize) {
        showRingAvatar(avatarView, avatar, avatarSize, 1.0f, 0.8f, 0xfff1f1f1);
    }

    /**
     * 显示一个圆环头像
     *
     * @param avatarView  显示头像的view
     * @param avatar      头像的url
     * @param avatarSize  头像显示的大小
     * @param ringWidth   圆环的宽度
     * @param ringPadding 圆环的间距
     * @param ringColor   圆环的颜色
     */
    public static void showRingAvatar(ImageView avatarView, String avatar, int avatarSize, float ringWidth,
                                      float ringPadding, int ringColor) {
        ZjbImageLoader.create(avatar)
                .setQiniu(avatarSize, avatarSize)
                .setDefaultRes(R.drawable.avatar_circle_default)
                .setDisplayType(ZjbImageLoader.DISPLAY_CIRCLE_RING)
                .setStrokeWidth(ringWidth)
                .setRingPadding(ringPadding)
                .setRingColor(ringColor)
                .into(avatarView);
    }

    /**
     * 显示一个圆环头像
     *
     * @param avatarView  显示头像的view
     * @param avatar      头像的url
     * @param avatarSize  头像显示的大小
     * @param ringWidth   圆环的宽度
     * @param ringPadding 圆环的间距
     * @param ringColor   圆环的颜色
     */
    public static void showRingAvatar(ImageView avatarView, String avatar, int avatarSize, float ringWidth,
                                      float ringPadding, int ringColor, int defaultAvatar) {
        ZjbImageLoader.create(avatar)
                .setQiniu(avatarSize, avatarSize)
                .setDefaultRes(defaultAvatar)
                .setDisplayType(ZjbImageLoader.DISPLAY_CIRCLE_RING)
                .setStrokeWidth(ringWidth)
                .setRingPadding(ringPadding)
                .setRingColor(ringColor)
                .into(avatarView);
    }

    /**
     * 显示一张图片
     */
    public static void showImage(ImageView imageView, String imgUrl) {
        showImage(imageView, imgUrl, ZjbImageLoader.DISPLAY_DEFAULT);
    }

    /**
     * 显示一张图片
     */
    public static void showImage(ImageView imageView, String imgUrl, int displayType) {
        showImage(imageView, imgUrl, displayType, Color.TRANSPARENT);
    }

    /**
     * 显示一张图片
     */
    public static void showImage(ImageView imageView, String imgUrl, int displayType, int drawableColor) {
        showImage(imageView, imgUrl, displayType, new ColorDrawable(drawableColor));
    }

    /**
     * 显示一张图片
     */
    public static void showImage(ImageView imageView, String imgUrl, int displayType, Drawable drawable) {
        showImage(imageView, imgUrl, displayType, drawable, 0, 0);
    }


    public static void preLoadImage(String imgUrl) {
        ZjbImageLoader.create(imgUrl).setDisplayType(ZjbImageLoader.DISPLAY_DEFAULT);
    }

    /**
     * 显示一张图片
     *
     * @param imageView   显示图片的控件
     * @param imgUrl      图片的url
     * @param displayType 图片显示的类型
     * @param drawable    没有加载出来前显示的默认图片
     * @param imgWidth    七牛裁剪图片的宽度
     * @param imgHeight   七牛裁剪图片的高度
     */
    public static void showImage(ImageView imageView, String imgUrl, int displayType, Drawable drawable, int imgWidth, int imgHeight) {
        if (TextUtils.isEmpty(imgUrl)) {
            imageView.setImageResource(R.drawable.album_cover_default);
        } else {
            ZjbImageLoader.create(imgUrl)
                    .setQiniu(imgWidth, imgHeight)
                    .setDisplayType(displayType)
                    .setDefaultDrawable(drawable)
                    .into(imageView);
        }
    }

    private static void setDelayedClickable(final View v, final boolean clickable, int delayMillis) {
        new Handler().postDelayed(() -> {
            v.setClickable(clickable);
            v.setEnabled(true);
        }, delayMillis); //
    }

    public static void showViewFromBottom(View view) {
        if (view.getVisibility() == View.VISIBLE) {
            return;
        }
        view.setVisibility(View.VISIBLE);
        int height = view.getHeight();
        TranslateAnimation translateAnimation = new TranslateAnimation(Animation.ABSOLUTE, 0, Animation.ABSOLUTE, 0, Animation.ABSOLUTE, height, Animation.ABSOLUTE, 0);
        translateAnimation.setDuration(ANIMATION_DURATION);
        translateAnimation.setInterpolator(sAnimationInterpolator);
        view.startAnimation(translateAnimation);
    }

    public static void hideViewFromBottom(final View view) {
        if (view.getVisibility() == View.INVISIBLE) {
            return;
        }
        int height = view.getHeight();
        TranslateAnimation translateAnimation = new TranslateAnimation(Animation.ABSOLUTE, 0, Animation.ABSOLUTE, 0, Animation.ABSOLUTE, 0, Animation.ABSOLUTE, height);
        translateAnimation.setDuration(ANIMATION_DURATION);
        translateAnimation.setInterpolator(sAnimationInterpolator);
        translateAnimation.setAnimationListener(new AnimationAdapter() {
            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.INVISIBLE);
            }
        });
        view.startAnimation(translateAnimation);
    }


}
