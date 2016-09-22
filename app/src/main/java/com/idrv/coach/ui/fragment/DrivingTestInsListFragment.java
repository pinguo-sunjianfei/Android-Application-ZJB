package com.idrv.coach.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * time:2016/8/18
 * description:
 *
 * @author sunjianfei
 */
public class DrivingTestInsListFragment extends BaseFragment<DrivingTestInsModel> {
    @InjectView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @InjectView(R.id.refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private DrivingTestInsListAdapter mAdapter;

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.act_insurance_list, container, false);
    }

    @Override
    public void initView(View view) {
        ButterKnife.inject(this, view);

        mAdapter = new DrivingTestInsListAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.addItemDecoration(new SpacesItemDecoration(0, 0, 0, (int) PixelUtil.dp2px(20), false));
        mRecyclerView.setAdapter(mAdapter);

        //设置刷新逻辑
        mSwipeRefreshLayout.setMode(SwipeRefreshLayout.Mode.BOTH);
        mSwipeRefreshLayout.setOnRefreshListener(this::refresh);
        mSwipeRefreshLayout.setOnPullUpRefreshListener(this::loadMore);

        initViewModel();
        registerEvent();
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
        if (NetworkUtil.isConnected(getContext())) {
            showProgressView();
            refresh();
        } else {
            UIHelper.shortToast(R.string.network_error);
        }
    }

    private void registerEvent() {
        //1.注册微信支付完成的消息
        RxBusManager.register(this, EventConstant.KEY_PAY_RESULT, Integer.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::processPayResult, Logger::e);
        //2.保单提交成功的消息
        RxBusManager.register(this, EventConstant.KEY_DRIVING_INS_COMMIT_SUCCESS, String.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> onClickRetry(), Logger::e);
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

    @OnClick(R.id.back)
    public void onBackClick(View v) {
        getActivity().finish();
    }
}
