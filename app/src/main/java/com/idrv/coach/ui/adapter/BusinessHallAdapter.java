package com.idrv.coach.ui.adapter;

import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.idrv.coach.R;
import com.idrv.coach.bean.BusinessHall;
import com.idrv.coach.bean.WebParamBuilder;
import com.idrv.coach.data.constants.SchemeConstant;
import com.idrv.coach.ui.CarInsuranceActivity;
import com.idrv.coach.ui.DrivingTestInsActivity;
import com.idrv.coach.ui.ToolBoxWebActivity;
import com.idrv.coach.utils.PixelUtil;
import com.zjb.loader.ZjbImageLoader;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * time:2016/3/14
 * description:
 *
 * @author sunjianfei
 */
public class BusinessHallAdapter extends AbsRecycleAdapter<BusinessHall, RecyclerView.ViewHolder> {
    public static final int ITEM_HEIGHT = (int) PixelUtil.dp2px(98);

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vw_business_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder mItemViewHolder = (ItemViewHolder) holder;
        BusinessHall businessHall = mData.get(position);

        if (!businessHall.isFake()) {
            mItemViewHolder.mBottomTv.setText(businessHall.getName());
            ZjbImageLoader.create(businessHall.getIcon())
                    .setBitmapConfig(Bitmap.Config.ARGB_8888)
                    .setDisplayType(ZjbImageLoader.DISPLAY_DEFAULT)
                    .setDefaultDrawable(new ColorDrawable(0xff000000))
                    .into(mItemViewHolder.mTopIv);
            holder.itemView.setOnClickListener(v -> {
                // 正文
                String url = businessHall.getUrl();
                Uri uri = Uri.parse(url);
                String path = uri.getPath();
                String scheme = uri.getScheme();
                if (SchemeConstant.KEY_APP.equals(scheme)) {
                    switch (path) {
                        case SchemeConstant.PATH_INS:
                            CarInsuranceActivity.launch(v.getContext());
                            break;
                        case SchemeConstant.PATH_DRIVER_INS:
                            DrivingTestInsActivity.launch(v.getContext());
                            break;
                    }
                } else if (SchemeConstant.KEY_THIRD.equals(scheme)) {
                    //TODO
                } else {
                    WebParamBuilder builder = WebParamBuilder.create()
                            .setUrl(businessHall.getUrl())
                            .setTitle(businessHall.getName());
                    ToolBoxWebActivity.launch(v.getContext(), builder);
                }
            });
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.image)
        ImageView mTopIv;
        @InjectView(R.id.text)
        TextView mBottomTv;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
            ViewGroup.LayoutParams lp = itemView.getLayoutParams();
            lp.height = ITEM_HEIGHT;
        }
    }
}
