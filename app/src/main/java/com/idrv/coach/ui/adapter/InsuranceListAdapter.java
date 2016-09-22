package com.idrv.coach.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.idrv.coach.R;
import com.idrv.coach.bean.InsuranceInfo;
import com.idrv.coach.bean.IsPrice;
import com.idrv.coach.ui.InsReViewsActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * time:2016/3/23
 * description:
 *
 * @author sunjianfei
 */
public class InsuranceListAdapter extends AbsRecycleAdapter<InsuranceInfo, RecyclerView.ViewHolder> {

    //审核中
    private static final int STATUS_AUDIT = 0;
    //已报价
    private static final int STATUS_OFFOR = 1;
    //已缴费
    private static final int STATUS_APPLY = 2;
    //已完成
    private static final int STATUS_COMPLETE = 3;
    //已失效
    private static final int STATUS_WASTE = 4;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vw_insurance_item, parent, false);
        return new InsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        InsuranceInfo info = mData.get(position);
        InsViewHolder insViewHolder = (InsViewHolder) holder;
        int status = info.getStatus();
        IsPrice price = info.getIsPrice();
        switch (status) {
            case STATUS_AUDIT:
                insViewHolder.mNameTv.setText(info.getName());
                insViewHolder.mTelTv.setText(info.getPhone());
                insViewHolder.mRightIv.setImageResource(R.drawable.ins_audit);
                insViewHolder.mTelLayout.setVisibility(View.VISIBLE);
                insViewHolder.mRightIv.setVisibility(View.VISIBLE);
                insViewHolder.mCompanyLayout.setVisibility(View.GONE);
                insViewHolder.mPriceLayout.setVisibility(View.GONE);
                insViewHolder.mBottomLayout.setVisibility(View.GONE);
                break;
            case STATUS_OFFOR: {
                insViewHolder.mNameTv.setText(info.getName());
                insViewHolder.mTelTv.setText(info.getPhone());
                if (null != price) {
                    insViewHolder.mPriceLayout.setVisibility(View.VISIBLE);
                    insViewHolder.mCompanyLayout.setVisibility(View.VISIBLE);

                    insViewHolder.mPriceTv.setText(price.getPrice());
                    insViewHolder.mCompanyTv.setText(price.getCompany());

                    if (!TextUtils.isEmpty(price.getAddress())) {
                        insViewHolder.mBottomLayout.setVisibility(View.VISIBLE);
                        insViewHolder.mBottomLayout.setOnClickListener(v -> InsReViewsActivity.launch(v.getContext(),
                                price.getAddress(),InsReViewsActivity.TYPE_CAR_INS));
                    } else {
                        insViewHolder.mBottomLayout.setVisibility(View.GONE);
                    }
                } else {
                    insViewHolder.mBottomLayout.setVisibility(View.GONE);
                    insViewHolder.mPriceLayout.setVisibility(View.GONE);
                    insViewHolder.mCompanyLayout.setVisibility(View.GONE);
                }
                insViewHolder.mTelLayout.setVisibility(View.VISIBLE);
                insViewHolder.mRightIv.setImageResource(R.drawable.ins_has_offor);
                insViewHolder.mRightIv.setVisibility(View.VISIBLE);
            }
            break;
            case STATUS_APPLY:
                insViewHolder.mNameTv.setText(info.getName());
                insViewHolder.mTelTv.setText(info.getPhone());
                if (null != price) {
                    insViewHolder.mPriceLayout.setVisibility(View.VISIBLE);
                    insViewHolder.mCompanyLayout.setVisibility(View.VISIBLE);

                    insViewHolder.mPriceTv.setText(price.getPrice());
                    insViewHolder.mCompanyTv.setText(price.getCompany());
                } else {
                    insViewHolder.mPriceLayout.setVisibility(View.GONE);
                    insViewHolder.mCompanyLayout.setVisibility(View.GONE);
                }
                insViewHolder.mRightIv.setImageResource(R.drawable.ins_has_payment);
                insViewHolder.mTelLayout.setVisibility(View.VISIBLE);
                insViewHolder.mRightIv.setVisibility(View.VISIBLE);
                insViewHolder.mBottomLayout.setVisibility(View.GONE);
                break;
            case STATUS_COMPLETE:
                insViewHolder.mNameTv.setText(info.getName());
                insViewHolder.mTelTv.setText(info.getPhone());
                if (null != price) {
                    insViewHolder.mPriceLayout.setVisibility(View.VISIBLE);
                    insViewHolder.mCompanyLayout.setVisibility(View.VISIBLE);

                    insViewHolder.mPriceTv.setText(price.getPrice());
                    insViewHolder.mCompanyTv.setText(price.getCompany());
                } else {
                    insViewHolder.mPriceLayout.setVisibility(View.GONE);
                    insViewHolder.mCompanyLayout.setVisibility(View.GONE);
                }
                insViewHolder.mRightIv.setImageResource(R.drawable.ins_completed);
                insViewHolder.mTelLayout.setVisibility(View.VISIBLE);
                insViewHolder.mRightIv.setVisibility(View.VISIBLE);
                insViewHolder.mBottomLayout.setVisibility(View.GONE);
                break;
            case STATUS_WASTE:
                insViewHolder.mNameTv.setText(info.getName());
                insViewHolder.mTelTv.setText(info.getPhone());
                insViewHolder.mRightIv.setImageResource(R.drawable.ins_expired);
                insViewHolder.mTelLayout.setVisibility(View.VISIBLE);
                insViewHolder.mRightIv.setVisibility(View.VISIBLE);
                insViewHolder.mCompanyLayout.setVisibility(View.GONE);
                insViewHolder.mPriceLayout.setVisibility(View.GONE);
                insViewHolder.mBottomLayout.setVisibility(View.GONE);
                break;
        }
    }

    class InsViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.car_owner_name_tv)
        TextView mNameTv;
        @InjectView(R.id.insurance_price_tv)
        TextView mPriceTv;
        @InjectView(R.id.price_layout)
        View mPriceLayout;
        @InjectView(R.id.insurance_company_tv)
        TextView mCompanyTv;
        @InjectView(R.id.company_layout)
        View mCompanyLayout;
        @InjectView(R.id.insurance_tel_tv)
        TextView mTelTv;
        @InjectView(R.id.tel_layout)
        View mTelLayout;
        @InjectView(R.id.right_image)
        ImageView mRightIv;
        @InjectView(R.id.bottom_layout)
        View mBottomLayout;
        @InjectView(R.id.ins_reviews)
        TextView mReviewsTv;

        public InsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}
