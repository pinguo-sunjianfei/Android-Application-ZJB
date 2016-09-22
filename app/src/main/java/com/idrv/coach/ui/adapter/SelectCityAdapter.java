package com.idrv.coach.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.idrv.coach.R;
import com.idrv.coach.ZjbApplication;
import com.idrv.coach.bean.City;
import com.idrv.coach.bean.Location;
import com.idrv.coach.data.db.TDSchool;
import com.idrv.coach.ui.SchoolSelectActivity;
import com.idrv.coach.utils.ValidateUtil;
import com.idrv.coach.utils.helper.DialogHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * time:2016/5/25
 * description:
 *
 * @author sunjianfei
 */
public class SelectCityAdapter extends AbsRecycleAdapter<City, RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0x000;
    private static final int TYPE_DEFAULT = 0x001;
    private Location location;
    City mLocationCity;
    SearchSchoolAdapter.OnSchoolSelectListener mListener;

    public void setListener(SearchSchoolAdapter.OnSchoolSelectListener mListener) {
        this.mListener = mListener;
    }

    public void setLocation(Location location) {
        this.location = location;
        getCityByName(location.getCity());
    }

    @Override
    public int getItemCount() {
        return ValidateUtil.isValidate(mData) ? mData.size() + 1 : 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_DEFAULT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER) {
            view = inflater.inflate(R.layout.vw_select_city_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            view = inflater.inflate(R.layout.vw_select_city_item, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position != 0) {
            City city = mData.get(position - 1);
            ItemViewHolder viewHolder = (ItemViewHolder) holder;
            viewHolder.mCityNameTv.setText(city.getCity());
            viewHolder.itemView.setOnClickListener(v -> SchoolSelectActivity.launch(v.getContext(), city.getNumber()));
        } else {
            HeaderViewHolder viewHolder = (HeaderViewHolder) holder;
            if (null != location) {
                int errorCode = location.getCode();
                if (errorCode == AMapLocation.LOCATION_SUCCESS) {
                    String city = location.getCity();
                    String province = location.getProvince();
                    viewHolder.mLocationTv.setVisibility(View.GONE);
                    viewHolder.mLocationCityTv.setVisibility(View.VISIBLE);
                    viewHolder.mLocationCityTv.setText(province + " " + city);
                    viewHolder.mLocationCityTv.setOnClickListener(v -> {
                        if (null != mLocationCity) {
                            SchoolSelectActivity.launch(v.getContext(), mLocationCity.getNumber());
                        } else {
                            if (null != mListener) {
                                mListener.onCustomSchoolSelect();
                            }
                        }
                    });
                } else {
                    viewHolder.mLocationTv.setVisibility(View.VISIBLE);
                    viewHolder.mLocationCityTv.setVisibility(View.GONE);

                    Context context = ZjbApplication.gContext;
                    viewHolder.mLocationTv.setTextColor(context.getResources().getColor(R.color.themes_main));
                    viewHolder.mLocationTv.setText(R.string.location_failed);
                    //如果没有开启权限
                    if (errorCode == AMapLocation.ERROR_CODE_FAILURE_LOCATION_PERMISSION) {
                        DialogHelper.create(DialogHelper.TYPE_NORMAL)
                                .cancelable(true)
                                .canceledOnTouchOutside(true)
                                .title(context.getString(R.string.no_location_permission))
                                .content(context.getString(R.string.no_location_permission_tips))
                                .bottomButton(context.getString(R.string.Iknowit), context.getResources().getColor(R.color.themes_main))
                                .bottomBtnClickListener((dialog, view) -> dialog.dismiss())
                                .show();

                    }
                }
            }
            viewHolder.mOtherCityTv.setOnClickListener(v -> {
                if (null != mListener) {
                    mListener.onCustomSchoolSelect();
                }
            });
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.location_text)
        TextView mLocationTv;
        @InjectView(R.id.location_city)
        TextView mLocationCityTv;
        @InjectView(R.id.other_city_tv)
        TextView mOtherCityTv;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.city_name_tv)
        TextView mCityNameTv;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    private void getCityByName(String cityName) {
        cityName = TDSchool.parseName(cityName);
        for (City city : mData) {
            if (city.getCity().equals(cityName)) {
                mLocationCity = city;
                return;
            }
        }
    }
}
