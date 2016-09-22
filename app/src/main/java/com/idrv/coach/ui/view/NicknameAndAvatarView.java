package com.idrv.coach.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.idrv.coach.R;
import com.idrv.coach.utils.PixelUtil;
import com.zjb.loader.ZjbImageLoader;
import com.zjb.loader.internal.core.assist.ImageScaleType;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * time:2016/8/1
 * description:
 *
 * @author sunjianfei
 */
public class NicknameAndAvatarView extends LinearLayout {
    @InjectView(R.id.child_avatar)
    ImageView mAvatarIv;
    @InjectView(R.id.child_nick_name)
    TextView mNicknameTv;

    public NicknameAndAvatarView(Context context) {
        super(context);
    }

    public NicknameAndAvatarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NicknameAndAvatarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    public void setNickname(String nickname) {
        mNicknameTv.setText(nickname);
    }

    public void setTextColor(int color) {
        mNicknameTv.setTextColor(color);
    }

    public void setTextGravity(int gravity) {
        mNicknameTv.setGravity(gravity);
    }

    public void setAvatar(String avatar) {
        ZjbImageLoader.create(avatar)
                .setDefaultRes(R.drawable.avatar_circle_default)
                .setQiniu((int) PixelUtil.dp2px(28), (int) PixelUtil.dp2px(28))
                .setDisplayType(ZjbImageLoader.DISPLAY_CIRCLE)
                .setImageScaleType(ImageScaleType.EXACTLY)
                .into(mAvatarIv);
    }
}
