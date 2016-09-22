package com.idrv.coach.ui.adapter;

import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.idrv.coach.R;
import com.idrv.coach.bean.Services;
import com.idrv.coach.utils.PixelUtil;
import com.idrv.coach.utils.helper.ResHelper;
import com.zjb.loader.ZjbImageLoader;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * time:2016/4/19
 * description:
 *
 * @author sunjianfei
 */
public class EditServiceAdapter extends AbsRecycleAdapter<Services, EditServiceAdapter.EditServiceViewHolder> {
    private int editBtnResId;
    private boolean isEditStatus;
    private int mItemSize;
    OnItemClickListener mListener;

    public EditServiceAdapter(int resId, boolean isEdit) {
        editBtnResId = resId;
        isEditStatus = isEdit;
        this.mItemSize = (int) ((ResHelper.getScreenWidth() - PixelUtil.dp2px(50.0f)) / 4.f + 0.5f);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public void setIsEditStatus(boolean isEditStatus) {
        this.isEditStatus = isEditStatus;
    }

    public boolean isEditStatus() {
        return isEditStatus;
    }

    @Override
    public EditServiceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vw_item_services, parent, false);
        return new EditServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EditServiceViewHolder holder, int position) {
        GridLayoutManager.LayoutParams params = new GridLayoutManager.LayoutParams(mItemSize, mItemSize);
        holder.itemView.setLayoutParams(params);
        holder.mEditImageView.setImageResource(editBtnResId);
        holder.mEditImageView.setVisibility(isEditStatus ? View.VISIBLE : View.INVISIBLE);

        Services service = mData.get(position);
        ZjbImageLoader.create(service.getIcon())
                .setBitmapConfig(Bitmap.Config.ARGB_8888)
                .setDisplayType(ZjbImageLoader.DISPLAY_DEFAULT)
                .setDefaultDrawable(new ColorDrawable(0xffe0dedc))
                .into(holder.mImageView);
        holder.itemView.setOnClickListener(v -> {
            if (null != mListener && isEditStatus) {
                mListener.onItemClick(service, holder.getAdapterPosition());
            }
        });
    }

    public class EditServiceViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.image)
        ImageView mImageView;
        @InjectView(R.id.edit_btn)
        ImageView mEditImageView;

        public EditServiceViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Services services, int position);
    }
}
