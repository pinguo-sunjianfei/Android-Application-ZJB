package com.idrv.coach.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.idrv.coach.R;
import com.idrv.coach.utils.PixelUtil;
import com.idrv.coach.utils.helper.ViewUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * time:2016/6/23
 * description:
 *
 * @author sunjianfei
 */
public class RankItemLayout extends LinearLayout {
    @InjectView(R.id.avatar_iv)
    ImageView mAvatarIv;
    @InjectView(R.id.background_iv)
    ImageView mBackgroundIv;
    @InjectView(R.id.circle_tv)
    CircleTextView mTextView;
    @InjectView(R.id.nick_name)
    TextView mNickNameTv;
    @InjectView(R.id.school_name)
    TextView mSchoolTv;
    @InjectView(R.id.num_layout)
    FrameLayout mNumLayout;
    @InjectView(R.id.chassis_iv)
    ImageView mChassisIv;
    @InjectView(R.id.teach_age)
    TextView mTeachAgeTv;

    public RankItemLayout(Context context) {
        super(context);
    }

    public RankItemLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RankItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    private void initView(Context context, AttributeSet attrs, int defStyle) {
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.RankItemLayout, defStyle, 0);
        int backgroundRes = a.getResourceId(R.styleable.RankItemLayout_item_background_drawable, 0);
        int size = a.getDimensionPixelSize(R.styleable.RankItemLayout_item_width_height, 160);
        int textColor = a.getColor(R.styleable.RankItemLayout_item_number_text_color, getResources().getColor(R.color.black));
        int textBackgroundColor = a.getColor(R.styleable.RankItemLayout_item_number_background_color, 0xffffd800);
        int topMargin = a.getDimensionPixelOffset(R.styleable.RankItemLayout_item_number_margin_top, -50);
        boolean showChassis = a.getBoolean(R.styleable.RankItemLayout_item_show_chassis, false);
        int chassisRes = a.getResourceId(R.styleable.RankItemLayout_item_chassis_drawable, R.drawable.default_bottom_image);
        int avatarTopMargin = a.getDimensionPixelOffset(R.styleable.RankItemLayout_item_avatar_top_margin, 0);
        int numTextSize = a.getInteger(R.styleable.RankItemLayout_item_num_text_size, 0);
        a.recycle();

        inflate(getContext(), R.layout.vw_rank, this);
        ButterKnife.inject(this, this);

        if (backgroundRes > 0) {
            mBackgroundIv.setImageResource(backgroundRes);
        }
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mAvatarIv.getLayoutParams();
        lp.width = size;
        lp.height = size;
        lp.topMargin = avatarTopMargin;

        if (numTextSize > 0) {
            mTextView.setTextSize(numTextSize);
        }
        mTextView.setTextColor(textColor);
        mTextView.setBackgroundColor(textBackgroundColor);
        LayoutParams layoutParams = (LayoutParams) mNumLayout.getLayoutParams();
        layoutParams.topMargin = topMargin;

        mChassisIv.setVisibility(showChassis ? VISIBLE : GONE);
        mChassisIv.setImageResource(chassisRes);
    }

    public void setAvatar(String url, boolean isRing) {
        if (!isRing) {
            ViewUtils.showCirCleAvatar(mAvatarIv, url, (int) PixelUtil.dp2px(80), R.drawable.avatar_circle_default);
        } else {
            ViewUtils.showRingAvatar(mAvatarIv, url, (int) PixelUtil.dp2px(80),
                    PixelUtil.dp2px(2), 0f, 0x4CFFFFFF, R.drawable.avatar_circle_default);
        }
    }

    public void setNickName(String nickName) {
        if (TextUtils.isEmpty(nickName)) {
            nickName = getContext().getString(R.string.coach_default_name);
        }
        mNickNameTv.setText(nickName);
    }

    public void setSchoolName(String schoolName) {
        mSchoolTv.setText(schoolName);
    }

    public void setTeachAge(int age) {
        mTeachAgeTv.setText(getContext().getString(R.string.rank_teach_age, age));
    }

    public void setRanking(int ranking) {
        mTextView.setText(ranking + "");
    }

}
