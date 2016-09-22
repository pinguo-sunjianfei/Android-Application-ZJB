package com.idrv.coach.ui.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.idrv.coach.R;
import com.idrv.coach.utils.helper.ViewUtils;

/**
 * time: 15/7/27
 * description:
 *
 * @author crab
 */
public class MasterItemView extends LinearLayout implements View.OnClickListener {
    private TextView mTextView, mRightTextView;
    private ImageButton mRightArrow;
    private RedPointView mRedPointView;
    private RedPointView mDynamicRedPointView;
    private View mRightTextLayout;
    private View mNewDynamicLayout;
    private View mLine;
    private ImageView mAvatarIv;
    private ImageView mRightIv;
    private OnMasterItemClickListener mItemClickListener;
    private onMasterItemRightIvClickListener mRightImageViewClickListener;
    private TextView mDynamicRightTv;
    private ImageView mDynamicLeftIv;

    public MasterItemView(Context context) {
        this(context, null);
    }

    public MasterItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MasterItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundResource(R.drawable.list_item_bg);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTextView = (TextView) findViewById(R.id.master_item_text_view);
        mRightArrow = (ImageButton) findViewById(R.id.master_item_right_arrow_view);
        mRightTextView = (TextView) findViewById(R.id.master_item_right_text_view_with_arrow);
        mLine = findViewById(R.id.master_item_line);
        mRedPointView = (RedPointView) findViewById(R.id.new_msg);
        mDynamicRedPointView = (RedPointView) findViewById(R.id.dynamic_red_point);
        mRightTextLayout = findViewById(R.id.master_item_right_text_layout);
        mNewDynamicLayout = findViewById(R.id.new_dynamic_layout);
        mAvatarIv = (ImageView) findViewById(R.id.avatar);
        mRightIv = (ImageView) findViewById(R.id.right_image_view);
        mDynamicRightTv = (TextView) findViewById(R.id.dynamic_right_tv);
        mDynamicLeftIv = (ImageView) findViewById(R.id.dynamic_left_icon);
        setOnClickListener(this);
        mTextView.setOnClickListener(this);
        mRightArrow.setOnClickListener(this);
        mRightTextView.setOnClickListener(this);
        mRightIv.setOnClickListener(v -> {
            Object tag = v.getTag();
            if (null != tag) {
                if (null != mRightImageViewClickListener) {
                    mRightImageViewClickListener.onRightImageViewClick((String) tag);
                }
            } else {
                MasterItemView.this.performClick();
            }
        });
    }

    @Override
    public void onClick(View view) {
        ViewUtils.setDelayedClickable(view, 500);
        if (mItemClickListener != null) {
            mItemClickListener.onMasterItemClick(this);
        }
    }

    public void setLeftDrawableRes(int res) {
        mDynamicLeftIv.setVisibility(VISIBLE);
        mDynamicLeftIv.setImageResource(res);
    }

    public void setLeftDrawableRes(String url) {
        mDynamicLeftIv.setVisibility(VISIBLE);
        ViewUtils.showImage(mDynamicLeftIv, url);
    }

    public void setTitleLeftDrawableRes(int res) {
        mDynamicLeftIv.setVisibility(GONE);
        Drawable drawable = getResources().getDrawable(res);
        mTextView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
    }

    public void setLineVisible(int visible) {
        mLine.setVisibility(visible);
    }

    public void setText(int resId) {
        mTextView.setText(resId);
    }

    public void setText(String text) {
        mTextView.setText(text);
    }

    public void setRightArrowVisible(int visible) {
        mRightArrow.setVisibility(visible);
    }

    public void setRightTextViewWithArrowVisible(int visible) {
        mRightTextView.setVisibility(visible);
    }

    public void setRightTextWithArrowText(int res) {
        mRightTextView.setVisibility(VISIBLE);
        mRightTextView.setText(res);
    }

    public void setRightTextWithArrowText(String content) {
        mRightTextView.setVisibility(VISIBLE);
        mRightTextView.setText(content);
    }

    public void setRightTextWithOutArrowText(int res) {
        mRightTextView.setVisibility(VISIBLE);
        mRightTextView.setText(res);
        mRightArrow.setVisibility(GONE);
    }

    public void setRightImage(String path) {
        mRightIv.setVisibility(VISIBLE);
        if (path.startsWith("file")) {
            mRightIv.setTag(path);
        } else {
            mRightIv.setTag(null);
        }
        ViewUtils.showImage(mRightIv, path);
    }

    public void setRedPointStatus(boolean status) {
        mRedPointView.setVisibility(status ? VISIBLE : GONE);
    }

    public void setItemEnable(boolean enable) {
        setEnabled(enable);
        mTextView.setEnabled(enable);
        mRightArrow.setEnabled(enable);
        mRightTextView.setEnabled(enable);
    }

    public void setOnMasterItemClickListener(OnMasterItemClickListener listener) {
        mItemClickListener = listener;
    }

    public void setRightImageViewClickListener(onMasterItemRightIvClickListener rightImageViewClickListener) {
        this.mRightImageViewClickListener = rightImageViewClickListener;
    }

    public void showAvatarLayout(boolean visible) {
        mNewDynamicLayout.setVisibility(visible ? VISIBLE : GONE);
    }

    ///////////////////////////////////////////////////////////
    // add these below for UserInfoActivity.class by bigflower
    public void setRightText(String text) {
        mRightTextView.setVisibility(VISIBLE);
        mRightTextView.setText(text);
    }

    public String getRightText() {
        return mRightTextView.getText().toString();

    }

    public void setRightTextWithoutImage(String text) {
        mRightTextView.setText(text);
        mNewDynamicLayout.setVisibility(GONE);
    }

    public void setRightCImageWithoutText(String imgUrl) {
        ViewUtils.showCirCleAvatar(mAvatarIv, imgUrl);
        mNewDynamicLayout.setVisibility(VISIBLE);
        mRightTextLayout.setVisibility(GONE);
    }

    public void setRightImageWithoutText(String imgUrl) {
        ViewUtils.showAvatar(mAvatarIv, imgUrl, 90);
        mNewDynamicLayout.setVisibility(VISIBLE);
        mRightTextLayout.setVisibility(GONE);
    }

    public void setNewDynamicText(String text) {
        mDynamicRightTv.setText(text);
    }

    public void setDynamicRedPointStatus(boolean status) {
        mDynamicRedPointView.setVisibility(status ? VISIBLE : GONE);
    }

    public void setRightTextLayoutStatus(boolean status) {
        mRightTextLayout.setVisibility(status ? VISIBLE : GONE);
    }

    // add those above for UserInfoActivity.class by bigflower
    ///////////////////////////////////////////////////////////
    public interface OnMasterItemClickListener {
        void onMasterItemClick(View v);
    }

    public interface onMasterItemRightIvClickListener {
        void onRightImageViewClick(String path);
    }
}
