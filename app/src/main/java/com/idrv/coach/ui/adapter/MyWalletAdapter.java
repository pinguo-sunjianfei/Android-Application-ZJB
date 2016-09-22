package com.idrv.coach.ui.adapter;

import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.idrv.coach.R;
import com.idrv.coach.bean.PurseDetails;
import com.idrv.coach.ui.ProductListActivity;
import com.idrv.coach.ui.WithDrawActivity;
import com.idrv.coach.utils.TimeUtil;
import com.idrv.coach.utils.ValidateUtil;
import com.zjb.loader.ZjbImageLoader;
import com.zjb.loader.internal.core.assist.ImageScaleType;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * time:2016/5/19
 * description:
 *
 * @author sunjianfei
 */
public class MyWalletAdapter extends AbsRecycleAdapter<PurseDetails, RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0x000;
    private static final int TYPE_GROUP = 0x001;
    private static final int TYPE_DEFAULT = 0x002;

    //佣金余额
    private String balance;
    //积分
    private String credit;

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public boolean hasData() {
        return !TextUtils.isEmpty(balance) && !TextUtils.isEmpty(credit);
    }

    @Override
    public int getItemCount() {
        return ValidateUtil.isValidate(mData) ? mData.size() + 1 : 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else {
            PurseDetails data = mData.get(position - 1);
            if (data.isGroup()) {
                return TYPE_GROUP;
            } else {
                return TYPE_DEFAULT;
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case TYPE_HEADER: {
                view = inflater.inflate(R.layout.vw_my_wallet_header, parent, false);
                viewHolder = new HeaderViewHolder(view);
            }
            break;
            case TYPE_GROUP: {
                view = inflater.inflate(R.layout.vw_group_item, parent, false);
                viewHolder = new GroupViewHolder(view);
            }
            break;
            case TYPE_DEFAULT: {
                view = inflater.inflate(R.layout.vw_wallet_item, parent, false);
                viewHolder = new DefaultViewHolder(view);
            }
            break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        PurseDetails data = null;
        if (position != 0) {
            data = mData.get(position - 1);
        }

        switch (type) {
            case TYPE_HEADER: {
                HeaderViewHolder viewHolder = (HeaderViewHolder) holder;
                viewHolder.mBalanceTv.setText(balance);
                viewHolder.mWithDrawView.setOnClickListener(v -> WithDrawActivity.launch(v.getContext()));
                viewHolder.mCreditTv.setText(credit);
                viewHolder.mRedeemBtn.setOnClickListener(v -> ProductListActivity.launch(v.getContext()));
            }
            break;
            case TYPE_GROUP: {
                GroupViewHolder viewHolder = (GroupViewHolder) holder;
                viewHolder.mGroupNameTv.setText(data.getGroupName());
            }
            break;
            case TYPE_DEFAULT: {
                DefaultViewHolder viewHolder = (DefaultViewHolder) holder;
                int showType = data.getType();

                if (showType == 1 || showType == 2 || showType == 3) {
                    String studentName = data.getStudentName();
                    String phone = data.getStudentPhone();

                    studentName = TextUtils.isEmpty(studentName) ? "" : studentName;
                    phone = TextUtils.isEmpty(phone) ? "" : phone;
                    viewHolder.mNameTv.setVisibility(View.VISIBLE);
                    viewHolder.mNameTv.setText(studentName + "  " + phone);
                } else {
                    viewHolder.mNameTv.setVisibility(View.GONE);
                }

                viewHolder.mTitleTv.setText(data.getLabel());
                viewHolder.mTimeTv.setText(TimeUtil.getChinaDate(data.getCreated()));
                String money = data.getInAccount();
                viewHolder.mMoneyTv.setText(money);
                //加载图片
                ZjbImageLoader.create(data.getIcon())
                        .setDisplayType(ZjbImageLoader.DISPLAY_DEFAULT)
                        .setDefaultDrawable(new ColorDrawable(0xffe0dedc))
                        .setImageScaleType(ImageScaleType.EXACTLY)
                        .into(viewHolder.mLeftImage);
            }
            break;
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.balance_tv)
        TextView mBalanceTv;
        @InjectView(R.id.with_draw_tv)
        View mWithDrawView;
        @InjectView(R.id.current_credit_tv)
        TextView mCreditTv;
        @InjectView(R.id.btn_redeem)
        TextView mRedeemBtn;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    class GroupViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.group_name)
        TextView mGroupNameTv;

        public GroupViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    class DefaultViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.left_icon)
        ImageView mLeftImage;
        @InjectView(R.id.title_tv)
        TextView mTitleTv;
        @InjectView(R.id.name_tv)
        TextView mNameTv;
        @InjectView(R.id.time_tv)
        TextView mTimeTv;
        @InjectView(R.id.added_money_tv)
        TextView mMoneyTv;

        public DefaultViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

//    private int getDrawableResIdFromType(int type) {
//        int leftImageResId;
//        switch (type) {
//            // 明细类型 0:红包(福利) 1:防爆险 2:新车保险 3:续保 4:提现
//            case 0:
//                leftImageResId = R.drawable.icon_wel;
//                break;
//            case 1:
//                leftImageResId = R.drawable.icon_driving_ins;
//                break;
//            case 2:
//                leftImageResId = R.drawable.icon_new_car_ins;
//                break;
//            case 3:
//                leftImageResId = R.drawable.icon_car_ins;
//                break;
//            case 4:
//                leftImageResId = R.drawable.icon_withdraw;
//                break;
//            case 5:
//                leftImageResId = R.drawable.commission_detail_5;
//                break;
//            case 6:
//                leftImageResId = R.drawable.icon_buy_spread_tool;
//                break;
//            case 7:
//            case 8:
//                leftImageResId = R.drawable.commission_detail_7;
//                break;
//            case 9:
//                leftImageResId = R.drawable.commission_detail_9;
//                break;
//            case 10:
//                leftImageResId = R.drawable.icon_buy_poster;
//                break;
//            default:
//                leftImageResId = R.drawable.icon_other;
//                break;
//        }
//        return leftImageResId;
//    }
//
//    private int getTextResIdFromType(int type) {
//        int textResId;
//        switch (type) {
//            // 明细类型 0:红包(福利) 1:防爆险 2:新车保险 3:续保 4:提现
//            case 0:
//                textResId = R.string.welfare;
//                break;
//            case 1:
//                textResId = R.string.driving_ins;
//                break;
//            case 2:
//                textResId = R.string.new_car_ins;
//                break;
//            case 3:
//                textResId = R.string.old_car_ins;
//                break;
//            case 4:
//                textResId = R.string.withdraw;
//                break;
//            case 5:
//                textResId = R.string.commission_detail_5;
//                break;
//            case 6:
//                textResId = R.string.buy_spread_tool;
//                break;
//            case 7:
//            case 8:
//                textResId = R.string.commission_detail_7;
//                break;
//            case 9:
//                textResId = R.string.commission_detail_9;
//                break;
//            case 10:
//                textResId = R.string.buy_poster;
//                break;
//            default:
//                textResId = R.string.other;
//                break;
//        }
//        return textResId;
//    }
}
