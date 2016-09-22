//package com.idrv.coach.ui.adapter;
//
//import android.graphics.drawable.ColorDrawable;
//import android.support.v7.widget.GridLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//
//import com.idrv.coach.R;
//import com.idrv.coach.bean.Picture;
//import com.idrv.coach.ui.PhotoReviewActivity;
//import com.idrv.coach.ui.PhotoWallBackActivity;
//import com.idrv.coach.utils.PixelUtil;
//import com.idrv.coach.utils.ValidateUtil;
//import com.idrv.coach.utils.helper.ResHelper;
//import com.zjb.loader.ZjbImageLoader;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//
//import butterknife.ButterKnife;
//import butterknife.InjectView;
//import butterknife.Optional;
//
///**
// * time:2016/4/22
// * description:
// *
// * @author sunjianfei
// */
//public class PhotoWallBackAdapter extends AbsRecycleAdapter<Picture, PhotoWallBackAdapter.PhotoWallViewHolder> {
//    private static final int TYPE_HEADER = 0X000;
//    private static final int TYPE_TAKE_PHOTO = 0X001;
//    private static final int TYPE_DEFAULT_ITEM = 0X002;
//
//    private int mItemSize;
//    private int type;
//    OnCameraClickListener mListener;
//    boolean removeFakeItem = false;
//
//    public PhotoWallBackAdapter() {
//        this.mItemSize = (int) ((ResHelper.getScreenWidth() - PixelUtil.dp2px(25)) / 4.f);
//    }
//
//    public void setType(int type) {
//        this.type = type;
//    }
//
//    public void setOnCameraClickListener(OnCameraClickListener listener) {
//        this.mListener = listener;
//    }
//
//    public void insertData(List<Picture> pictures) {
//        if (null == mData) {
//            mData = new ArrayList<>();
//        }
//        mData.addAll(0, pictures);
//        notifyDataSetChanged();
//    }
//
//    public void removeFakeItem() {
//        Iterator<Picture> iterator = mData.iterator();
//        while (iterator.hasNext()) {
//            Picture picture = iterator.next();
//            if (picture.isFake()) {
//                iterator.remove();
//            }
//        }
//        notifyDataSetChanged();
//    }
//
//    @Override
//    public int getItemCount() {
//        return ValidateUtil.isValidate(mData) ? mData.size() + 2 : 2;
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        if (position == 0) {
//            return TYPE_HEADER;
//        } else if (position == 1) {
//            return TYPE_TAKE_PHOTO;
//        }
//        return TYPE_DEFAULT_ITEM;
//    }
//
//    @Override
//    public PhotoWallBackAdapter.PhotoWallViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view;
//        if (viewType == TYPE_HEADER) {
//            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vw_photo_wall_header_back, parent, false);
//        } else if (viewType == TYPE_TAKE_PHOTO) {
//            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vw_photo_wall_camera_item, parent, false);
//        } else {
//            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vw_photo_wall_tem, parent, false);
//        }
//        return new PhotoWallViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(PhotoWallBackAdapter.PhotoWallViewHolder holder, int position) {
//        int viewType = getItemViewType(position);
//
//        switch (viewType) {
//            case TYPE_HEADER: {
//                int drawId = type == PhotoWallBackActivity.TYPE_COACH ?
//                        R.drawable.photo_wall_header_coach : R.drawable.photo_wall_header_student;
//                holder.mImageView.setImageResource(drawId);
//            }
//            break;
//            case TYPE_TAKE_PHOTO:
//                holder.itemView.setOnClickListener(v -> {
//                    if (null != mListener) {
//                        mListener.onCameraClick();
//                    }
//                });
//                break;
//            case TYPE_DEFAULT_ITEM:
//                GridLayoutManager.LayoutParams params = new GridLayoutManager.LayoutParams(mItemSize, mItemSize);
//                holder.itemView.setLayoutParams(params);
//                Picture picture = mData.get(position - 2);
//                ZjbImageLoader.create(picture.getUrl())
//                        .setQiniu(mItemSize, mItemSize)
//                        .setDisplayType(ZjbImageLoader.DISPLAY_DEFAULT)
//                        .setDefaultDrawable(new ColorDrawable(0xffe0dedc))
//                        .into(holder.mImageView);
//                holder.mImageView.setOnClickListener(v -> PhotoReviewActivity.launch(v.getContext(), (ArrayList) mData, position - 2, true));
//                break;
//        }
//    }
//
//    static class PhotoWallViewHolder extends RecyclerView.ViewHolder {
//        @Optional
//        @InjectView(R.id.photo_image)
//        ImageView mImageView;
//        @Optional
//        @InjectView(R.id.camera_layout)
//        View mCameraLayout;
//
//        public PhotoWallViewHolder(View itemView) {
//            super(itemView);
//            ButterKnife.inject(this, itemView);
//        }
//    }
//
//    public interface OnCameraClickListener {
//        void onCameraClick();
//    }
//}
