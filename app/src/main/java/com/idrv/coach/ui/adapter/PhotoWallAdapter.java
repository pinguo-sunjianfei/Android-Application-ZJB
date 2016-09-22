package com.idrv.coach.ui.adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.idrv.coach.R;
import com.idrv.coach.bean.Picture;
import com.idrv.coach.ui.PhotoReviewActivity;
import com.idrv.coach.ui.view.DynamicHeightImageView;
import com.idrv.coach.ui.view.transformer.ScaleInTransformer;
import com.idrv.coach.ui.widget.FixedViewPager;
import com.idrv.coach.utils.PixelUtil;
import com.idrv.coach.utils.ValidateUtil;
import com.idrv.coach.utils.helper.ResHelper;
import com.zjb.loader.ZjbImageLoader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

/**
 * time:2016/8/9
 * description:
 *
 * @author sunjianfei
 */
public class PhotoWallAdapter extends AbsRecycleAdapter<Picture, RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0X000;
    private static final int TYPE_DEFAULT = 0X001;
    FixedViewPager.OnViewPagerTouchListener mOnViewPagerTouchListener;

    List<Picture> mPictures = new ArrayList<>();

    public PhotoWallAdapter(FixedViewPager.OnViewPagerTouchListener listener) {
        this.mOnViewPagerTouchListener = listener;
    }

    public void setPictures(List<Picture> mPictures) {
        this.mPictures.clear();
        this.mPictures.addAll(mPictures);
    }

    /**
     * 插入新数据
     *
     * @param pictures
     */
    public void insertData(List<Picture> pictures) {
        if (null == mData) {
            mData = new ArrayList<>();
        }
        mData.addAll(0, pictures);
        notifyDataSetChanged();
    }

    /**
     * 移除假数据
     */
    public void removeFakeItem() {
        Iterator<Picture> iterator = mData.iterator();
        while (iterator.hasNext()) {
            Picture picture = iterator.next();
            if (picture.isFake()) {
                iterator.remove();
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() + 1 : 1;
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
            view = inflater.inflate(R.layout.vw_photowall_header, parent, false);
        } else {
            view = inflater.inflate(R.layout.vw_photo_wall_tem, parent, false);
        }
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder viewHolder = (ItemViewHolder) holder;
        int type = getItemViewType(position);
        Context context = holder.itemView.getContext();

        //如果是头
        if (type == TYPE_HEADER) {
            //头部铺满
            StaggeredGridLayoutManager.LayoutParams lp = ((StaggeredGridLayoutManager.LayoutParams)
                    holder.itemView.getLayoutParams());
            lp.setFullSpan(true);
            lp.leftMargin = (int) PixelUtil.dp2px(-10.f);
            lp.rightMargin = lp.leftMargin;

            if (ValidateUtil.isValidate(mPictures)) {
                viewHolder.mViewPager.setVisibility(View.VISIBLE);
                viewHolder.mDefaultIv.setVisibility(View.GONE);

                ImagePagerAdapter mAdapter = new ImagePagerAdapter();
                viewHolder.mViewPager.setPageMargin((int) PixelUtil.dp2px(25));
                viewHolder.mViewPager.setOffscreenPageLimit(3);
                viewHolder.mViewPager.setOnViewPagerTouchListener(mOnViewPagerTouchListener);
                viewHolder.mViewPager.setPageTransformer(true, new ScaleInTransformer());
                viewHolder.mPagerLayout.setOnTouchListener(((v, event) -> viewHolder.mViewPager.dispatchTouchEvent(event)));
                mAdapter.setData(mPictures);
                //设置图片点击事件
                mAdapter.setOnImageClickListener(pos -> PhotoReviewActivity.launch(context, (ArrayList) mData, pos, true));
                viewHolder.mViewPager.setAdapter(mAdapter);
            } else {
                viewHolder.mViewPager.setAdapter(null);
                viewHolder.mViewPager.setVisibility(View.GONE);
                viewHolder.mDefaultIv.setVisibility(View.VISIBLE);
            }
        } else {
            //普通的item
            StaggeredGridLayoutManager.LayoutParams lp = ((StaggeredGridLayoutManager.LayoutParams)
                    holder.itemView.getLayoutParams());
            lp.setFullSpan(false);

            Picture picture = mData.get(position - 1);
            int picWidth = ResHelper.getScreenWidth() / 3;

            double positionHeight;
            if (position % 3 == 0) {
                positionHeight = 1.2f;
            } else if (position % 5 == 0) {
                positionHeight = 1.4f;
            } else if (position % 7 == 0) {
                positionHeight = 1.5f;
            } else {
                positionHeight = 1.1f;
            }
            viewHolder.mImageView.setHeightRatio(positionHeight);
            ZjbImageLoader.create(picture.getUrl())
                    .setDisplayType(ZjbImageLoader.DISPLAY_FADE_IN)
                    .setQiniu(picWidth, picWidth)
                    .setFadeInTime(1000)
                    .setDefaultDrawable(new ColorDrawable(0xffe0dedc))
                    .into(viewHolder.mImageView);
            viewHolder.mImageView.setOnClickListener(v -> PhotoReviewActivity.launch(context, (ArrayList) mData, position - 1, true));
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        @Optional
        @InjectView(R.id.viewpager)
        FixedViewPager mViewPager;
        @Optional
        @InjectView(R.id.pager_layout)
        FrameLayout mPagerLayout;
        @Optional
        @InjectView(R.id.photo_image)
        DynamicHeightImageView mImageView;
        @Optional
        @InjectView(R.id.default_image)
        ImageView mDefaultIv;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}
