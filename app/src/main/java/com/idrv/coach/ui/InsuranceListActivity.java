package com.idrv.coach.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.idrv.coach.R;
import com.idrv.coach.bean.InsuranceInfo;
import com.idrv.coach.data.model.InsuranceModel;
import com.idrv.coach.ui.adapter.InsuranceListAdapter;
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

/**
 * time:2016/3/23
 * description:车险列表
 *
 * @author sunjianfei
 */
public class InsuranceListActivity extends BaseActivity<InsuranceModel> {
    @InjectView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @InjectView(R.id.refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    InsuranceListAdapter mAdapter;

    public static void launch(Context context) {
        Intent intent = new Intent(context, InsuranceListActivity.class);
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

    private void initToolBar() {
        mToolbarLayout.setTitleTxt(getString(R.string.ins_list_title));
    }

    private void initView() {
        mAdapter = new InsuranceListAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.addItemDecoration(new SpacesItemDecoration(0, 0, 0, (int) PixelUtil.dp2px(20), true));
        mRecyclerView.setAdapter(mAdapter);

        //设置刷新逻辑
        mSwipeRefreshLayout.setMode(SwipeRefreshLayout.Mode.BOTH);
        mSwipeRefreshLayout.setOnRefreshListener(this::refresh);
        mSwipeRefreshLayout.setOnPullUpRefreshListener(this::loadMore);
    }

    private void initViewModel() {
        mViewModel = new InsuranceModel();
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

    private void onRefreshNext(List<InsuranceInfo> list) {
        mAdapter.setData(list);
        mAdapter.notifyDataSetChanged();
    }

    private void onLoadMoreNext(List<InsuranceInfo> list) {
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
}
