package com.idrv.coach.ui.adapter;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunjianfei on 15-6-18.
 * description:
 *
 * @author crab
 */
public abstract class AbsRecycleAdapter<T, K extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<K> {
    protected List<T> mData;

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

    public void remove(T t) {
        if (null != mData) {
            mData.remove(t);
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
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public T getItem(int position) {
        if (mData != null) {
            return mData.get(position);
        }
        return null;
    }
}
