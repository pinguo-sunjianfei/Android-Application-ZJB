package com.idrv.coach.ui.view;

import android.content.Context;
import android.util.AttributeSet;

import com.idrv.coach.bean.DiscoverMainItems;

import java.util.List;

/**
 * time:2016/8/4
 * description:
 *
 * @author sunjianfei
 */
public class DiscoverGirdLayout extends AbsGridLayout<DiscoverMainItems> {
    private int imageSize;

    public DiscoverGirdLayout(Context context) {
        super(context);
    }

    public DiscoverGirdLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DiscoverGirdLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setImageSize(int imageSize) {
        this.imageSize = imageSize;
    }

    @Override
    protected void addChildViews(int count) {
        for (int i = 0; i < count; i++) {
            DiscoverItemLayout view = new DiscoverItemLayout(getContext());
            if (mDrawableRes != -1) {
                view.setBackgroundDrawable(mDrawableRes);
            }
            view.setTextColor(mTextColor);
            addView(view);
        }
    }

    @Override
    public void setData(List<DiscoverMainItems> list) {
        super.setData(list);
        int size = list.size();
        //最新的子View数量
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            DiscoverItemLayout view = (DiscoverItemLayout) getChildAt(i);
            if (i >= size) {
                view.setVisibility(GONE);
            } else {
                DiscoverMainItems items = list.get(i);
                view.setVisibility(VISIBLE);

                view.setImage(imageSize, items.getIcon());
                view.setText(items.getTitle());
                final int finalIndex = i;
                view.setTag(i);
                view.setOnClickListener(v -> {
                    if (null != mClickListener) {
                        int index = finalIndex + position * itemRowViewCount;
                        mClickListener.onImageClick(index, v);
                    }
                });
            }
        }
    }
}
