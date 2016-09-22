package com.idrv.coach.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.idrv.coach.R;


/**
 * time: 15/6/24
 * description: 黑色主题的标题栏
 *
 * @author sunjianfei
 */
public class ToolbarLayout extends RelativeLayout implements View.OnClickListener {
    public ImageView mLeftIv;
    public TextView mLeftTv;
    public ImageView mTitleIv;
    public TextView mTitleTv;
    public ImageView mRightIv;
    public TextView mRightTv;
    public ImageView mTitleBg;

    protected RelativeLayout mTitlebar;

    private OnClickListener mOnClickListener;

    public ToolbarLayout(Context context) {
        super(context);
        // 这个构造方法仅仅为了在编辑器里面能够预览
    }

    private ToolbarLayout(Context context, View contentView, boolean isOverlay, boolean hasShadow) {
        super(context);
        if (null == contentView) {
            throw new IllegalArgumentException("The content view can not be null.");
        }
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        // content view
        LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        //如果有阴影，contentView需要向上移动移动距离，否则会透视前页面的背景
        int marginTop = (int) getContext().getResources().getDimension(R.dimen.title_bar_content_margin_top);
        if (!isOverlay) {
            params.addRule(BELOW, R.id.toolbar_layout);
        }
        if (hasShadow) {
            params.topMargin = marginTop;
            contentView.setPadding(0, marginTop, 0, 0);
        }
        addView(contentView, params);
        // titlebar
        mTitlebar = (RelativeLayout)
                inflater.inflate(R.layout.vw_toolbar, null);
        mLeftIv = (ImageView) mTitlebar.findViewById(R.id.titlebar_left_btn);
        mLeftTv = (TextView) mTitlebar.findViewById(R.id.titlebar_left_txt);
        mTitleIv = (ImageView) mTitlebar.findViewById(R.id.titlebar_title_icon);
        mTitleTv = (TextView) mTitlebar.findViewById(R.id.titlebar_title_txt);
        mRightIv = (ImageView) mTitlebar.findViewById(R.id.titlebar_right_btn);
        mRightTv = (TextView) mTitlebar.findViewById(R.id.titlebar_right_txt);
        mTitleBg = (ImageView) mTitlebar.findViewById(R.id.titlebar_bg);
        setOnClickListener(mLeftIv, mLeftTv, mTitleIv, mTitleTv, mRightIv, mRightTv);
        setBackgroundResource(android.R.color.transparent);
        if (hasShadow) {
            mTitleBg.setBackgroundResource(R.drawable.title_shadow);
        } else {
            mTitleBg.setBackgroundResource(android.R.color.transparent);
        }
        int height = (int) getContext().getResources().getDimension(R.dimen.title_bar_height);
        LayoutParams toolbarParams = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                height);
        toolbarParams.addRule(ALIGN_PARENT_TOP, TRUE);
        addView(mTitlebar, toolbarParams);
    }

    private void setOnClickListener(View... views) {
        for (View view : views) {
            view.setOnClickListener(this);
        }
    }


    public void setBackground(int resId) {
        setBackgroundResource(resId);
    }

    /**
     * 居中显示标题
     *
     * @param resId
     */
    public void setTitle(int resId) {
        mTitleTv.setVisibility(VISIBLE);
        mTitleTv.setText(resId);
    }

    /**
     * 居中显示标题
     *
     * @param title
     */
    public void setTitle(String title) {
        mTitleTv.setVisibility(VISIBLE);
        mTitleTv.setText(title);
    }

    public void setOnClickListener(OnClickListener listener) {
        this.mOnClickListener = listener;
    }

    public RelativeLayout getTitlebar() {
        return mTitlebar;
    }

    /**
     * 设置左边导航栏的图标
     *
     * @param icon 图标，可以是资源文件、Drawable、Bitmap
     */
    public void setLeftIcon(Object icon) {
        mLeftIv.setVisibility(VISIBLE);
        mLeftTv.setVisibility(GONE);
        if (icon instanceof Integer) {
            mLeftIv.setImageResource((int) icon);
        } else if (icon instanceof Drawable) {
            mLeftIv.setImageDrawable((Drawable) icon);
        } else if (icon instanceof Bitmap) {
            mLeftIv.setImageBitmap((Bitmap) icon);
        } else {
            throw new IllegalArgumentException("Icon must be resId,Bitmap or Drawable!");
        }
    }

    public void setTitleIcon(Object icon) {
        mTitleIv.setVisibility(VISIBLE);
        mTitleTv.setVisibility(GONE);
        if (icon instanceof Integer) {
            mTitleIv.setImageResource((int) icon);
        } else if (icon instanceof Drawable) {
            mTitleIv.setImageDrawable((Drawable) icon);
        } else if (icon instanceof Bitmap) {
            mTitleIv.setImageBitmap((Bitmap) icon);
        } else {
            throw new IllegalArgumentException("Icon must be resId,Bitmap or Drawable!");
        }
    }

    public void setRightIcon(Object icon) {
        mRightIv.setVisibility(VISIBLE);
        mRightTv.setVisibility(GONE);
        if (icon instanceof Integer) {
            mRightIv.setImageResource((int) icon);
        } else if (icon instanceof Drawable) {
            mRightIv.setImageDrawable((Drawable) icon);
        } else if (icon instanceof Bitmap) {
            mRightIv.setImageBitmap((Bitmap) icon);
        } else {
            throw new IllegalArgumentException("Icon must be resId,Bitmap or Drawable!");
        }
    }

    public void setRightIconShowStatus(boolean disable) {
        if (disable) {
            mRightIv.setVisibility(GONE);
        } else {
            mRightIv.setVisibility(VISIBLE);
        }
    }

    public void setRightTxt(Object text) {
        mRightIv.setVisibility(GONE);
        mRightTv.setVisibility(VISIBLE);
        if (text instanceof Integer) {
            mRightTv.setText((int) text);
        } else if (text instanceof String) {
            mRightTv.setText((String) text);
        } else {
            throw new IllegalArgumentException("Icon must be resId,String!");
        }
    }

    public void setTitleTxt(Object text) {
        mTitleIv.setVisibility(GONE);
        mTitleTv.setVisibility(VISIBLE);
        if (text instanceof Integer) {
            mTitleTv.setText((int) text);
        } else if (text instanceof String) {
            mTitleTv.setText((String) text);
        } else {
            throw new IllegalArgumentException("Icon must be resId,String!");
        }
    }

    public TextView getRightTextView() {
        return mRightTv;
    }

    public void setLeftTxt(Object text) {
        mLeftIv.setVisibility(GONE);
        mLeftTv.setVisibility(VISIBLE);
        if (text instanceof Integer) {
            mLeftTv.setText((int) text);
        } else if (text instanceof String) {
            mLeftTv.setText((String) text);
        } else {
            throw new IllegalArgumentException("Icon must be resId,String!");
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.titlebar_left_btn || id == R.id.titlebar_left_txt) {
            if (mOnClickListener != null) {
                mOnClickListener.onLeftClick(view);
            }
        } else if (id == R.id.titlebar_title_icon || id == R.id.titlebar_title_txt) {
            if (mOnClickListener != null) {
                mOnClickListener.onCenterClick(view);
            }
        } else if (id == R.id.titlebar_right_btn || id == R.id.titlebar_right_txt) {
            if (mOnClickListener != null) {
                mOnClickListener.onRightClick(view);
            }
        }
    }

    public interface OnClickListener {
        void onLeftClick(View view);

        void onCenterClick(View view);

        void onRightClick(View view);
    }

    public static class Builder {
        private Context mContext;
        private LayoutInflater mInflater;
        private boolean mIsOverlay = false;
        private boolean mHasShadow = true;
        private View mContentView;

        public Builder(Context context) {
            mContext = context;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public Builder setContentView(View view) {
            mContentView = view;
            return this;
        }

        public Builder setContentView(int layoutId) {
            mContentView = mInflater.inflate(layoutId, null);
            return this;
        }

        public Builder setOverlay(boolean isOverlay) {
            mIsOverlay = isOverlay;
            return this;
        }

        public Builder setShadow(boolean shadow) {
            mHasShadow = shadow;
            return this;
        }


        public ToolbarLayout build() {
            return new ToolbarLayout(mContext, mContentView, mIsOverlay, mHasShadow);
        }
    }
}
