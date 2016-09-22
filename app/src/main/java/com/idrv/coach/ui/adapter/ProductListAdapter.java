package com.idrv.coach.ui.adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.idrv.coach.R;
import com.idrv.coach.bean.SpreadTool;
import com.idrv.coach.bean.WebParamBuilder;
import com.idrv.coach.data.constants.SPConstant;
import com.idrv.coach.ui.MakePosterActivity;
import com.idrv.coach.ui.PosterShareActivity;
import com.idrv.coach.ui.SpreadToolPreviewActivity;
import com.idrv.coach.ui.view.DynamicHeightImageView;
import com.idrv.coach.utils.MathUtils;
import com.idrv.coach.utils.PreferenceUtil;
import com.zjb.loader.ZjbImageLoader;

import java.io.File;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * time:2016/7/13
 * description:
 *
 * @author sunjianfei
 */
public class ProductListAdapter extends AbsRecycleAdapter<SpreadTool, RecyclerView.ViewHolder> {
    long serverTime;

    public void setServerTime(long serverTime) {
        this.serverTime = serverTime;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vw_spread_tool_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        ItemViewHolder viewHolder = (ItemViewHolder) holder;
        SpreadTool tool = mData.get(position);
        //设置图片高度
        viewHolder.mContentIv.setHeightRatio(0.7f);
        //显示图片
        ZjbImageLoader.create(tool.getImage())
                .setDisplayType(ZjbImageLoader.DISPLAY_DEFAULT)
                .setDefaultDrawable(new ColorDrawable(0xffd1ceca))
                .into(viewHolder.mContentIv);

        //设置会员免费的状态
        viewHolder.mMemberFreeIv.setVisibility(tool.isMemberFree() ? View.VISIBLE : View.GONE);

        //设置支付的状态
        String payType = tool.getPayType();
        viewHolder.mPayStatusIv.setVisibility(View.VISIBLE);
        if ("wxPay".equals(payType) || "bPay".equals(payType)) {
            viewHolder.mPayStatusIv.setImageResource(R.drawable.has_pay_right);
        } else if ("cPay".equals(payType)) {
            viewHolder.mPayStatusIv.setImageResource(R.drawable.has_exchange_right);
        } else if ("memberFree".equals(payType)) {
            viewHolder.mPayStatusIv.setImageResource(R.drawable.has_free);
        } else {
            viewHolder.mPayStatusIv.setVisibility(View.GONE);
        }

        //设置工具类型
        int type = tool.getType();
        if (type == SpreadTool.TYPE_SPREAD) {
            viewHolder.mToolTypeIv.setImageResource(R.drawable.icon_spread_tool);
            holder.itemView.setOnClickListener(v ->
                    SpreadToolPreviewActivity.launch(context, WebParamBuilder
                            .create()
                            .setTitle(tool.getTitle())
                            .setUrl(tool.getUrl()), tool));
        } else if (type == SpreadTool.TYPE_POSTER) {
            viewHolder.mToolTypeIv.setImageResource(R.drawable.icon_poster);
            holder.itemView.setOnClickListener(v -> onPosterItemClick(tool, context));
        }

        //设置标题
        viewHolder.mTitleTv.setText(tool.getTitle());
        //设置兑换积分
        viewHolder.mPriceIntegralTv.setText(context.getString(R.string.price_integral, tool.getCredit()));
        //设置购买价格
        String price = MathUtils.decimalFormat(tool.getPrice() * 1.0f / 100);
        viewHolder.mPriceTv.setText(context.getString(R.string.price_rmb, price));
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.content_iv)
        DynamicHeightImageView mContentIv;
        @InjectView(R.id.free_membership)
        ImageView mMemberFreeIv;
        @InjectView(R.id.pay_status)
        ImageView mPayStatusIv;
        @InjectView(R.id.tool_type_iv)
        ImageView mToolTypeIv;
        @InjectView(R.id.title_tv)
        TextView mTitleTv;
        @InjectView(R.id.price_integral)
        TextView mPriceIntegralTv;
        @InjectView(R.id.price_tv)
        TextView mPriceTv;


        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    private void onPosterItemClick(SpreadTool tool, Context context) {
        String path = PreferenceUtil.getString(String.format(Locale.US, SPConstant.KEY_SAVED_POSTER_PATH, tool.getId()));
        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);
            if (null != file && file.exists()) {
                PosterShareActivity.launch(context, path, tool);
            } else {
                MakePosterActivity.launch(context, tool, false);
            }
        } else {
            MakePosterActivity.launch(context, tool, false);
        }
    }
}
