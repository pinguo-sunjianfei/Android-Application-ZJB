package com.idrv.coach.ui.adapter;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.idrv.coach.R;
import com.idrv.coach.bean.Banner;
import com.idrv.coach.bean.Coach;
import com.idrv.coach.bean.DiscoverItem;
import com.idrv.coach.bean.DiscoverMainItems;
import com.idrv.coach.data.manager.LoginManager;
import com.idrv.coach.ui.CoachAuthenticationActivity;
import com.idrv.coach.ui.CoachAuthenticationCommitActivity;
import com.idrv.coach.ui.view.DiscoverGirdLayout;
import com.idrv.coach.ui.view.LoopViewPager;
import com.idrv.coach.utils.PixelUtil;
import com.idrv.coach.utils.SchemeUtils;
import com.idrv.coach.utils.ValidateUtil;
import com.idrv.coach.utils.helper.DialogHelper;
import com.idrv.coach.utils.helper.ViewUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

/**
 * time:2016/8/4
 * description:发现adapter
 *
 * @author sunjianfei
 */
public class DiscoverAdapter extends AbsRecycleAdapter<DiscoverItem, RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 1;
    private static final int TYPE_DEFAULT = TYPE_HEADER << 1;

    private FragmentManager fragmentManager;
    private List<Banner> banners;
    private List<DiscoverMainItems> mainItemses;

    public DiscoverAdapter(FragmentManager manager) {
        this.fragmentManager = manager;
    }

    public void setBanners(List<Banner> banners) {
        this.banners = banners;
    }

    public void setMainItemses(List<DiscoverMainItems> mainItemses) {
        this.mainItemses = mainItemses;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_DEFAULT;
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() + 1 : 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER) {
            view = inflater.inflate(R.layout.vw_discover_header, parent, false);
        } else {
            view = inflater.inflate(R.layout.vw_discover_item_default, parent, false);
        }
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        ItemViewHolder viewHolder = (ItemViewHolder) holder;
        int type = getItemViewType(position);
        //如果是头
        if (type == TYPE_HEADER) {
            //1.广告
            if (ValidateUtil.isValidate(banners)) {
                viewHolder.mViewPager.setVisibility(View.VISIBLE);
                viewHolder.mDefaultIv.setVisibility(View.GONE);

                BannerAdapter mAdapter = new BannerAdapter(fragmentManager);
                mAdapter.setData(banners);
                viewHolder.mViewPager.setAdapter(mAdapter);

                //如果广告数量大于1,开启轮播
                if (mAdapter.getCount() > 1) {
                    viewHolder.mViewPager.setOffscreenPageLimit(mAdapter.getCount());
                    viewHolder.mViewPager.startAutoScroll(3000);
                }
            } else {
                viewHolder.mViewPager.setVisibility(View.GONE);
                viewHolder.mDefaultIv.setVisibility(View.VISIBLE);
            }

            //2.顶部item
            if (ValidateUtil.isValidate(mainItemses)) {
                viewHolder.mImageGridLayout.setVisibility(View.VISIBLE);
                viewHolder.mImageGridLayout.setImageSize((int) PixelUtil.dp2px(50));
                viewHolder.mImageGridLayout.setData(mainItemses);
                viewHolder.mImageGridLayout.setClickListener((index, v) -> {
                    ViewUtils.setDelayedClickable(v, 500);
                    DiscoverMainItems item = mainItemses.get(index);
                    onItemClick(context, item);
                });
            } else {
                viewHolder.mImageGridLayout.setVisibility(View.GONE);
            }
        } else {
            //3.列表item
            DiscoverItem items = mData.get(position - 1);
            List<DiscoverMainItems> itemsList = items.getItems();
            viewHolder.mTitleTv.setText(items.getLabel());
            viewHolder.mImageGridLayout.setImageSize((int) PixelUtil.dp2px(30));
            viewHolder.mImageGridLayout.setData(itemsList);
            viewHolder.mImageGridLayout.setClickListener((index, v) -> {
                ViewUtils.setDelayedClickable(v, 500);
                DiscoverMainItems item = itemsList.get(index);
                onItemClick(context, item);
            });

            if (position == mData.size()) {
                viewHolder.mBottomView.setVisibility(View.VISIBLE);
            } else {
                viewHolder.mBottomView.setVisibility(View.GONE);
            }
        }

    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        @Optional
        @InjectView(R.id.discover_viewpager)
        LoopViewPager mViewPager;
        @Optional
        @InjectView(R.id.default_iv)
        ImageView mDefaultIv;
        @Optional
        @InjectView(R.id.title_tv)
        TextView mTitleTv;
        @InjectView(R.id.grid_view)
        DiscoverGirdLayout mImageGridLayout;
        @Optional
        @InjectView(R.id.bottom_view)
        View mBottomView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    /**
     * 处理item的点击事件
     *
     * @param context
     * @param item
     */
    private void onItemClick(Context context, DiscoverMainItems item) {
        //判断是否需要教练认证
        if (!TextUtils.isEmpty(item.getConditionality())
                && "authentication".equals(item.getConditionality())) {
            Coach coach = LoginManager.getInstance().getCoach();
            int authStatus = coach.getAuthenticationState();
            if (authStatus == Coach.STATE_AUTH_SUCCESS) {
                //审核通过
                SchemeUtils.schemeJump(context, item.getUrl());
            } else {
                DialogHelper.create(DialogHelper.TYPE_NORMAL)
                        .title(context.getString(R.string.coach_authentication))
                        .content(context.getString(R.string.coach_auth_tip))
                        .leftButton(context.getString(R.string.cancel), ContextCompat.getColor(context, R.color.black_54))
                        .rightButton(context.getString(R.string.auth_now), ContextCompat.getColor(context, R.color.themes_main))
                        .leftBtnClickListener((dialog, view) -> dialog.dismiss())
                        .rightBtnClickListener((dialog, view) -> {
                            dialog.dismiss();
                            if (authStatus == Coach.STATE_AUTH_FAILED || authStatus == Coach.STATE_DEFAULT) {
                                //如果是审核失败或者没有提交审核
                                CoachAuthenticationActivity.launch(context);
                            } else if (authStatus == Coach.STATE_APPLY) {
                                //如果正在审核中
                                CoachAuthenticationCommitActivity.launch(context);
                            }
                        }).show();
            }
        } else {
            SchemeUtils.schemeJump(context, item.getUrl());
        }
    }
}
