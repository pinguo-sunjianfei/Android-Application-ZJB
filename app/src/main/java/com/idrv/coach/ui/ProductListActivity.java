package com.idrv.coach.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.idrv.coach.R;
import com.idrv.coach.bean.SpreadTool;
import com.idrv.coach.bean.event.EventConstant;
import com.idrv.coach.data.manager.RxBusManager;
import com.idrv.coach.data.model.SpreadModel;
import com.idrv.coach.ui.adapter.ProductListAdapter;
import com.idrv.coach.ui.view.decoration.SpacesItemDecoration;
import com.idrv.coach.ui.widget.SwipeRefreshLayout;
import com.idrv.coach.utils.Logger;
import com.idrv.coach.utils.PixelUtil;
import com.idrv.coach.utils.helper.UIHelper;
import com.zjb.volley.core.exception.NetworkError;
import com.zjb.volley.utils.NetworkUtil;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * time:2016/7/13
 * description:积分商城-商品列表
 *
 * @author sunjianfei
 */
public class ProductListActivity extends BaseActivity<SpreadModel> {
    @InjectView(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;
    @InjectView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    ProductListAdapter mAdapter;

    public static void launch(Context context) {
        Intent intent = new Intent(context, ProductListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_product_list);
        ButterKnife.inject(this);
        registerEvent();
        initToolBar();
        initView();
        initViewModel();
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
    public void onClickRetry() {
        if (NetworkUtil.isConnected(this)) {
            showProgressView();
            refresh();
        } else {
            UIHelper.shortToast(R.string.network_error);
        }
    }

    private void registerEvent() {
        //支付完成,刷新列表
        RxBusManager.register(this, EventConstant.KEY_SPREAD_TOOL_PAY_SUCCESS, String.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> refresh(), Logger::e);
    }

    private void initToolBar() {
        mToolbarLayout.setTitle(R.string.points_mall);
    }

    private void initViewModel() {
        mViewModel = new SpreadModel();
        refresh();
    }

    private void initView() {
        mAdapter = new ProductListAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.addItemDecoration(new SpacesItemDecoration(0, 0, 0, (int) PixelUtil.dp2px(18), false));
        mRecyclerView.setAdapter(mAdapter);

        //设置刷新逻辑
        mRefreshLayout.setMode(SwipeRefreshLayout.Mode.BOTH);
        mRefreshLayout.setOnRefreshListener(this::refresh);
        mRefreshLayout.setOnPullUpRefreshListener(this::loadMore);
    }

    /**
     * 获取传播工具
     */
    private void refresh() {
        Subscription subscription = mViewModel.refresh(mAdapter::clear)
                .subscribe(this::onNext, this::onError, this::onComplete);
        addSubscription(subscription);
    }

    /**
     * 加载更多
     */
    private void loadMore() {
        Subscription subscription = mViewModel.loadMore()
                .subscribe(this::onLoadMoreNext, this::onError, this::onComplete);
        addSubscription(subscription);
    }

    private void onNext(List<SpreadTool> tools) {
        mAdapter.setServerTime(mViewModel.getServerTime());
        mAdapter.setData(tools);
        mAdapter.notifyDataSetChanged();
    }

    private void onLoadMoreNext(List<SpreadTool> tools) {
        mAdapter.setServerTime(mViewModel.getServerTime());
        mAdapter.addData(tools);
        mAdapter.notifyDataSetChanged();
    }

    private void onError(Throwable e) {
        mRefreshLayout.setRefreshing(false);
        mRefreshLayout.setPullUpRefreshing(false);
        if (e instanceof NetworkError) {
            NetworkError error = (NetworkError) e;
            UIHelper.shortToast(error.getErrorCode().getMessage());
        }
        if (mAdapter.getItemCount() <= 0) {
            showErrorView();
        }
    }

    private void onComplete() {
        showContentView();
        mRefreshLayout.setRefreshing(false);
        mRefreshLayout.setPullUpRefreshing(false);
    }
}
