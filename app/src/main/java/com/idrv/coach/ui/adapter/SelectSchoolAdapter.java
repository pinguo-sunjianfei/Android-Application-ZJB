package com.idrv.coach.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.idrv.coach.R;
import com.idrv.coach.utils.ValidateUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

/**
 * time:2016/5/26
 * description:
 *
 * @author sunjianfei
 */
public class SelectSchoolAdapter extends AbsRecycleAdapter<String, SelectSchoolAdapter.ItemViewHolder> {
    private static final int TYPE_HEADER_FIRST = 0x000;
    private static final int TYPE_HEADER_SECOND = 0x001;
    private static final int TYPE_DEFAULT = 0x002;

    SearchSchoolAdapter.OnSchoolSelectListener mListener;

    public void setListener(SearchSchoolAdapter.OnSchoolSelectListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public int getItemCount() {
        return ValidateUtil.isValidate(mData) ? mData.size() + 2 : 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER_FIRST;
        } else if (position == 1) {
            return TYPE_HEADER_SECOND;
        } else {
            return TYPE_DEFAULT;
        }
    }

    @Override
    public SelectSchoolAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER_FIRST) {
            view = inflater.inflate(R.layout.vw_select_school_header, parent, false);
        } else {
            view = inflater.inflate(R.layout.vw_select_city_item, parent, false);
        }
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SelectSchoolAdapter.ItemViewHolder holder, int position) {
        int type = getItemViewType(position);
        switch (type) {
            case TYPE_HEADER_FIRST:
                break;
            case TYPE_HEADER_SECOND: {
                holder.mSchoolNameTv.setText(R.string.other);
                holder.itemView.setOnClickListener(v -> {
                    if (null != mListener) {
                        mListener.onCustomSchoolSelect();
                    }
                });
            }
            break;
            case TYPE_DEFAULT: {
                String school = mData.get(position - 2);
                holder.mSchoolNameTv.setText(school);
                holder.itemView.setOnClickListener(v -> {
                    if (null != mListener) {
                        mListener.onSchoolSelect(school);
                    }
                });
            }
            break;
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        @Optional
        @InjectView(R.id.city_name_tv)
        TextView mSchoolNameTv;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}
