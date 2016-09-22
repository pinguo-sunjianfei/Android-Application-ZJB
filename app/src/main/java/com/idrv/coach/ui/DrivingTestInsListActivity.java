package com.idrv.coach.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.idrv.coach.R;
import com.idrv.coach.bean.DrivingTestInsurance;
import com.idrv.coach.bean.event.EventConstant;
import com.idrv.coach.data.manager.RxBusManager;
import com.idrv.coach.data.model.DrivingTestInsModel;
import com.idrv.coach.ui.adapter.DrivingTestInsListAdapter;
import com.idrv.coach.ui.view.decoration.SpacesItemDecoration;
import com.idrv.coach.ui.widget.BaseLayout;
import com.idrv.coach.ui.widget.SwipeRefreshLayout;
import com.idrv.coach.utils.Logger;
import com.idrv.coach.utils.PixelUtil;
import com.idrv.coach.utils.ValidateUtil;
import com.idrv.coach.utils.helper.UIHelper;
import com.zjb.volley.utils.NetworkUtil;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * time:2016/3/30
 * description:学车险列表
 *
 * @author sunjianfei
 */
public class DrivingTestInsListActivity extends BaseActivity<DrivingTestInsModel> {
    @InjectView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @InjectView(R.id.refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private DrivingTestInsListAdapter mAdapter;

    public static void launch(Context context) {
        Intent intent = new Intent(context, DrivingTestInsListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_insurance_list);
        ButterKnife.inject(this);
        initToolBar();
        initView();
        initViewModel();
        registerEvent();
    }

    private void registerEvent() {
        RxBusManager.register(this, EventConstant.KEY_PAY_RESULT, Integer.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::processPayResult, Logger::e);
    }

    private void initToolBar() {
        mToolbarLayout.setTitleTxt(R.string.already_apply);
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

    private void initView() {
        mAdapter = new DrivingTestInsListAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.addItemDecoration(new SpacesItemDecoration(0, 0, 0, (int) PixelUtil.dp2px(20), false));
        mRecyclerView.setAdapter(mAdapter);

        //设置刷新逻辑
        mSwipeRefreshLayout.setMode(SwipeRefreshLayout.Mode.BOTH);
        mSwipeRefreshLayout.setOnRefreshListener(this::refresh);
        mSwipeRefreshLayout.setOnPullUpRefreshListener(this::loadMore);
    }

    private void initViewModel() {
        mViewModel = new DrivingTestInsModel();
        refresh();
    }

    private void refresh() {
        Subscription subscription = mViewModel.refresh(mAdapter::clear)
                .subscribe(this::onRefreshNext, this::onError, this::onComplete);
        addSubscription(subscription);
    }

    private void loadMore() {
        Subscription subscription = mViewModel.loadMore()
                .subscribe(this::onLoadMoreNext, this::onError, this::onComplete);
        addSubscription(subscription);
    }

    private void onRefreshNext(List<DrivingTestInsurance> list) {
        if (!ValidateUtil.isValidate(list)) {
            showEmptyView();
        } else {
            mAdapter.setData(list);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void onLoadMoreNext(List<DrivingTestInsurance> list) {
        mAdapter.addData(list);
        mAdapter.notifyDataSetChanged();
    }

    private void onError(Throwable e) {
        Logger.e(e);
        mSwipeRefreshLayout.setRefreshing(false);
        mSwipeRefreshLayout.setPullUpRefreshing(false);

        if (!ValidateUtil.isValidate(mAdapter.getData())) {
            showErrorView();
        }
    }

    private void onComplete() {
        if (mAdapter.getItemCount() == 0) {
            showEmptyView();
        } else {
            showContentView();
        }
        mSwipeRefreshLayout.setRefreshing(false);
        mSwipeRefreshLayout.setPullUpRefreshing(false);
    }

    @Override
    protected BaseLayout.Builder getLayoutBuilder() {
        return super.getLayoutBuilder().setEmptyView(R.layout.vw_insurance_empty);
    }

    private void processPayResult(int code) {
        onClickRetry();
    }
}
