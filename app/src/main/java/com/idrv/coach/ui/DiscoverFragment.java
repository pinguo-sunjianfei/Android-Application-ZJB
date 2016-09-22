package com.idrv.coach.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.idrv.coach.R;
import com.idrv.coach.bean.Banner;
import com.idrv.coach.bean.DiscoverMainItems;
import com.idrv.coach.bean.DiscoverPage;
import com.idrv.coach.data.model.DiscoverModel;
import com.idrv.coach.ui.adapter.DiscoverAdapter;
import com.idrv.coach.ui.fragment.BaseFragment;
import com.idrv.coach.ui.view.decoration.SpacesItemDecoration;
import com.idrv.coach.utils.Logger;
import com.idrv.coach.utils.PixelUtil;
import com.idrv.coach.utils.ValidateUtil;
import com.idrv.coach.utils.helper.UIHelper;
import com.zjb.volley.utils.NetworkUtil;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscription;

/**
 * time:2016/3/10
 * description:
 *
 * @author sunjianfei
 */
public class DiscoverFragment extends BaseFragment<DiscoverModel> {
    @InjectView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    DiscoverAdapter mAdapter;


    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_discover, container, false);
    }

    @Override
    protected boolean hasBaseLayout() {
        return true;
    }

    @Override
    protected int getProgressBg() {
        return R.color.bg_main;
    }

    @Override
    public void initView(View view) {
        ButterKnife.inject(this, view);
        mAdapter = new DiscoverAdapter(getActivity().getSupportFragmentManager());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.addItemDecoration(new SpacesItemDecoration(0, 0, 0, (int) PixelUtil.dp2px(15), true));
        mRecyclerView.setAdapter(mAdapter);

        initViewModel();
    }

    @Override
    public void onClickRetry() {
        if (NetworkUtil.isConnected(getActivity())) {
            showProgressView();
            refresh();
        } else {
            UIHelper.shortToast(R.string.network_error);
        }
    }

    private void initViewModel() {
        mViewModel = new DiscoverModel();
        getWebSiteStatus();
        getDiscoverPageCache();
    }

    /**
     * 获取教练空间开通状态
     */
    private void getWebSiteStatus() {
        Subscription subscription = mViewModel.getWebsiteOpenStatus()
                .subscribe(Logger::e, Logger::e);
        addSubscription(subscription);
    }

    /**
     * 获取缓存数据
     */
    private void getDiscoverPageCache() {
        Subscription subscription = mViewModel.getDiscoverPageCache()
                .doOnTerminate(this::refresh)
                .subscribe(this::onNext, Logger::e);
        addSubscription(subscription);
    }

    /**
     * 刷新最新数据
     */
    private void refresh() {
        Subscription subscription = mViewModel.getDiscoverPage()
                .subscribe(this::onNext, this::onError, this::showContentView);
        addSubscription(subscription);
    }

    private void onNext(DiscoverPage page) {
        List<Banner> banners = page.getBanner();
        List<DiscoverMainItems> mainItems = page.getMainItems();

        if (ValidateUtil.isValidate(mainItems)) {
            if (mainItems.size() > 3) {
                mAdapter.setMainItemses(mainItems.subList(0, 3));
            } else {
                mAdapter.setMainItemses(mainItems);
            }
        }

        mAdapter.setBanners(banners);

        mAdapter.setData(page.getItemlist());
        mAdapter.notifyDataSetChanged();
        showContentView();
    }

    private void onError(Throwable e) {
        UIHelper.shortToast(R.string.network_error);
        if (mAdapter.getItemCount() <= 1) {
            showErrorView();
        }
    }
}
