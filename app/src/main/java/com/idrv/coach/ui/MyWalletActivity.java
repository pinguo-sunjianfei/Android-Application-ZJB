package com.idrv.coach.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.idrv.coach.R;
import com.idrv.coach.bean.WalletPage;
import com.idrv.coach.bean.event.EventConstant;
import com.idrv.coach.data.manager.RxBusManager;
import com.idrv.coach.data.model.WalletModel;
import com.idrv.coach.ui.adapter.MyWalletAdapter;
import com.idrv.coach.ui.view.decoration.SpacesItemDecoration;
import com.idrv.coach.ui.widget.EmptyRecyclerView;
import com.idrv.coach.ui.widget.SwipeRefreshLayout;
import com.idrv.coach.utils.Logger;
import com.idrv.coach.utils.PixelUtil;
import com.idrv.coach.utils.ScrollUtils;
import com.idrv.coach.utils.helper.UIHelper;
import com.idrv.coach.utils.helper.ViewUtils;
import com.zjb.volley.utils.NetworkUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * time:2016/5/19
 * description:
 *
 * @author sunjianfei
 */
public class MyWalletActivity extends BaseActivity<WalletModel> {
    @InjectView(R.id.recycler_view)
    EmptyRecyclerView mRecyclerView;
    @InjectView(R.id.refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.title_bar_layout)
    View mTitleBarLayout;

    MyWalletAdapter mAdapter;

    //列表滑动的距离
    private int mCurrentScroll;
    private float mHeaderViewHeight = PixelUtil.dp2px(200);


    public static void launch(Context context) {
        Intent intent = new Intent(context, MyWalletActivity.class);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_my_wallet);
        ButterKnife.inject(this);
        initView();
        initViewModel();
        registerEvent();
    }

    @Override
    protected boolean isToolbarEnable() {
        return false;
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
        RxBusManager.register(this, EventConstant.KEY_WITHDRAW_COMPLETE, String.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(__ -> refresh(), Logger::e);
    }

    private void initView() {
        mAdapter = new MyWalletAdapter();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(0, 0, 0, (int) PixelUtil.dp2px(1), true));

        mRecyclerView.setAdapter(mAdapter);

        //设置刷新逻辑
        mSwipeRefreshLayout.setMode(SwipeRefreshLayout.Mode.BOTH);
        mSwipeRefreshLayout.setOnRefreshListener(this::refresh);
        mSwipeRefreshLayout.setOnPullUpRefreshListener(this::loadMore);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mCurrentScroll += dy;
                int baseColor = getResources().getColor(R.color.themes_main);
                float alpha = Math.min(1, (float) mCurrentScroll / mHeaderViewHeight);
                mTitleBarLayout.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
            }
        });
    }

    private void initViewModel() {
        mViewModel = new WalletModel();
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

    private void onRefreshNext(WalletPage page) {
        mAdapter.setBalance(page.getBalance());
        mAdapter.setCredit(page.getCredit() + "");
        mAdapter.setData(page.getInAccounts());
        mAdapter.notifyDataSetChanged();
    }

    private void onLoadMoreNext(WalletPage page) {
        mAdapter.setData(page.getInAccounts());
        mAdapter.notifyDataSetChanged();
    }

    private void onError(Throwable e) {
        mSwipeRefreshLayout.setRefreshing(false);
        mSwipeRefreshLayout.setPullUpRefreshing(false);

        if (!mAdapter.hasData()) {
            showErrorView();
        }
    }

    private void onComplete() {
        showContentView();
        mSwipeRefreshLayout.setRefreshing(false);
        mSwipeRefreshLayout.setPullUpRefreshing(false);
    }

    @OnClick(R.id.back)
    public void onBackClick(View v) {
        ViewUtils.setDelayedClickable(v, 500);
        this.finish();
    }

}
