package com.idrv.coach.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.idrv.coach.R;
import com.idrv.coach.ZjbApplication;
import com.idrv.coach.bean.DrivingTestInsurance;
import com.idrv.coach.ui.DrivingTestInsPayActivity;
import com.idrv.coach.ui.InsReViewsActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * time:2016/3/30
 * description:
 *
 * @author sunjianfei
 */
public class DrivingTestInsListAdapter extends AbsRecycleAdapter<DrivingTestInsurance, RecyclerView.ViewHolder> {

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vw_driving_test_ins_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder viewHolder = (ItemViewHolder) holder;
        DrivingTestInsurance insurance = mData.get(position);
        int state = insurance.getState();
        viewHolder.mNameTv.setText(insurance.getName());
        viewHolder.mPhone.setText(insurance.getPhone());
        String time = insurance.getCreated();
        if (!TextUtils.isEmpty(time) && time.length() > 10) {
            time = time.substring(0, 10);
        }
        viewHolder.mApplyTimeTv.setText(ZjbApplication.gContext.getString(R.string.apply_time,
                time));
        switch (state) {
            case 0:
            case 1:
            case 3:
                viewHolder.mPaddingView.setVisibility(View.GONE);
                viewHolder.mReviewsLayout.setVisibility(View.VISIBLE);
                viewHolder.mReviewsTv.setText(R.string.pay_action);
                viewHolder.mFailedLayout.setVisibility(View.GONE);
                viewHolder.mImageView.setImageResource(R.drawable.bill_pay);
                viewHolder.mReviewsLayout.setOnClickListener(v -> DrivingTestInsPayActivity.launch(v.getContext(), insurance));
                break;
            case 4:
                viewHolder.mPaddingView.setVisibility(View.VISIBLE);
                viewHolder.mReviewsLayout.setVisibility(View.GONE);
                viewHolder.mFailedLayout.setVisibility(View.GONE);
                viewHolder.mImageView.setImageResource(R.drawable.bill_wait);
                break;
            case 2:
            case 6:
                viewHolder.mPaddingView.setVisibility(View.GONE);
                viewHolder.mReviewsLayout.setVisibility(View.GONE);
                viewHolder.mFailedLayout.setVisibility(View.VISIBLE);
                viewHolder.mImageView.setImageResource(R.drawable.bill_faild);
                viewHolder.mFailureCausesTv.setText(insurance.getReason());
                break;
            case 5:
                viewHolder.mPaddingView.setVisibility(View.GONE);
                viewHolder.mReviewsLayout.setVisibility(View.VISIBLE);
                viewHolder.mFailedLayout.setVisibility(View.GONE);
                viewHolder.mReviewsTv.setText(R.string.reviews_ins);
                viewHolder.mReviewsLayout.setOnClickListener(v -> InsReViewsActivity.launch(v.getContext(),
                        insurance.getDetailUrl(), InsReViewsActivity.TYPE_DRIVING_INS));
                viewHolder.mImageView.setImageResource(R.drawable.bill_success);

                if (!TextUtils.isEmpty(insurance.getDetailUrl())) {
                    viewHolder.mReviewsLayout.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.mReviewsLayout.setVisibility(View.GONE);
                }
                break;
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.name_tv)
        TextView mNameTv;
        @InjectView(R.id.insurance_tel_tv)
        TextView mPhone;
        @InjectView(R.id.apply_time)
        TextView mApplyTimeTv;
        @InjectView(R.id.padding_view)
        View mPaddingView;
        @InjectView(R.id.reviews_layout)
        View mReviewsLayout;
        @InjectView(R.id.reviews_tv)
        TextView mReviewsTv;
        @InjectView(R.id.failed_layout)
        View mFailedLayout;
        @InjectView(R.id.failure_causes_tv)
        TextView mFailureCausesTv;
        @InjectView(R.id.right_image)
        ImageView mImageView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}
