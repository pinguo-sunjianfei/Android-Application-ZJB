package com.idrv.coach.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * time:15-10-13
 * description:
 *
 * @author sunjianfei
 */
public class AbsFragmentPagerAdapter<T> extends FragmentStatePagerAdapter {
    protected List<T> mData;

    public AbsFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
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

    @Override
    public Fragment getItem(int position) {
        return null;
    }

    @Override
    public int getCount() {
        return 0;
    }
}
