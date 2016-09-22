package com.idrv.coach.ui.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.idrv.coach.utils.ValidateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * time:2016/8/9
 * description:
 *
 * @author sunjianfei
 */
public class AbsPagerAdapter<T> extends PagerAdapter {
    protected List<T> mData;

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return ValidateUtil.isValidate(mData) ? mData.size() : 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    public void setData(List<T> beans) {
        if (null != mData) {
            mData.clear();
            if (beans != null) {
                mData.addAll(beans);
            }
        } else {
            mData = beans;
        }
    }

    public void addData(List<T> beans) {
        if (null == mData) {
            mData = beans;
        } else {
            if (beans != null) {
                mData.removeAll(beans);
                mData.addAll(beans);
            }
        }
    }

    public void addData(T t) {
        if (mData == null) {
            mData = new ArrayList<>();
        }
        if (!mData.contains(t)) {
            mData.add(t);
        }
    }

    public List<T> getData() {
        return mData;
    }

    public void clear() {
        if (mData != null) {
            mData.clear();
        }
    }
}
