package com.idrv.coach.ui.adapter;

import android.widget.BaseAdapter;

import com.idrv.coach.utils.ValidateUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by sunjianfei on 14-4-16.
 */
public abstract class AbsListAdapter<T> extends BaseAdapter {
    protected List<T> mData;

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public T getItem(int position) {
        if (mData != null && mData.size() > 0) {
            return mData.get(position);
        }
        return null;
    }

    public void setData(List<T> beans) {
        if (mData == null) {
            mData = new ArrayList<>();
        }
        mData.clear();
        if (ValidateUtil.isValidate(beans)) {
            for (T t : beans) {
                if (!mData.contains(t)) {
                    mData.add(t);
                }
            }
        }
    }

    public void addData(List<T> beans) {
        if (null == mData) {
            mData = new ArrayList<>();
        }
        if (ValidateUtil.isValidate(beans)) {
            mData.removeAll(beans);
            mData.addAll(beans);
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

    public void clear() {
        if (mData != null) {
            mData.clear();
        }
    }

    final public int getRealSize() {
        return ValidateUtil.isValidate(mData) ? mData.size() : 0;
    }

    public List<T> getData() {
        return mData;
    }

}
