package com.idrv.coach.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.idrv.coach.R;
import com.idrv.coach.utils.ValidateUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

/**
 * time:2016/5/26
 * description:
 *
 * @author sunjianfei
 */
public class SearchSchoolAdapter extends AbsRecycleAdapter<String, SearchSchoolAdapter.ItemViewHolder>
        implements Filterable {
    private static final int TYPE_HEADER_FIRST = 0x000;
    private static final int TYPE_HEADER_SECOND = 0x001;
    private static final int TYPE_DEFAULT = 0x002;

    private List<String> mAllSchools;
    private List<String> mResultSchools;
    SearchSchoolAdapter.OnSchoolSelectListener mListener;

    public void setListener(OnSchoolSelectListener mListener) {
        this.mListener = mListener;
    }

    public SearchSchoolAdapter() {
        mResultSchools = new ArrayList<>();
    }

    public void setAllSchools(List<String> mAllSchools) {
        this.mAllSchools = mAllSchools;
    }

    @Override
    public int getItemCount() {
        return ValidateUtil.isValidate(mResultSchools) ? mResultSchools.size() + 2 : 2;
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
    public SearchSchoolAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
    public void onBindViewHolder(SearchSchoolAdapter.ItemViewHolder holder, int position) {
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
                String school = mResultSchools.get(position - 2);
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

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            mResultSchools.clear();
            mResultSchools.addAll((List<String>) results.values);
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetChanged();
            }
        }

        protected FilterResults performFiltering(CharSequence s) {
            String str = s.toString().toUpperCase();
            FilterResults results = new FilterResults();
            List<String> schoolList = new ArrayList<>();
            if (ValidateUtil.isValidate(mAllSchools)) {
                for (String school : mAllSchools) {
                    // 匹配全屏、首字母、和城市名中文
                    if (school.indexOf(str) > -1) {
                        schoolList.add(school);
                    }
                }
            }
            results.values = schoolList;
            results.count = schoolList.size();
            return results;
        }
    };

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        @Optional
        @InjectView(R.id.city_name_tv)
        TextView mSchoolNameTv;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    public interface OnSchoolSelectListener {
        void onSchoolSelect(String school);

        void onCustomSchoolSelect();
    }
}
