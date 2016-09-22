package com.idrv.coach.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.idrv.coach.R;
import com.idrv.coach.bean.Rank;
import com.idrv.coach.data.model.CoachRankModel;
import com.idrv.coach.ui.adapter.CoachRankAdapter;
import com.idrv.coach.ui.view.decoration.GridSpacingItemDecoration;
import com.idrv.coach.utils.PixelUtil;
import com.idrv.coach.utils.helper.UIHelper;
import com.zjb.volley.utils.NetworkUtil;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscription;

/**
 * time:2016/6/24
 * description:教练排行榜
 *
 * @author sunjianfei
 */
public class CoachRankActivity extends BaseActivity<CoachRankModel> {
    @InjectView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    CoachRankAdapter mAdapter;

    public static void launch(Context context) {
        Intent intent = new Intent(context, CoachRankActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_coach_rank);
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
        mToolbarLayout.setTitle(R.string.rank_title);
    }

    private void initView() {
        mAdapter = new CoachRankAdapter();
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0) {
                    return 3;
                }
                return 1;
            }
        });
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration((int) PixelUtil.dp2px(20), 3, 1));
    }

    private void initViewModel() {
        mViewModel = new CoachRankModel();
        refresh();
    }

    private void refresh() {
        Subscription subscription = mViewModel.getCoachRank()
                .subscribe(this::onNext, this::onError, this::onComplete);
        addSubscription(subscription);
    }

    private void onNext(List<Rank> rankList) {
        if (rankList.size() >= 6) {
            List<Rank> ranks = rankList.subList(0, 6);
            mAdapter.setHeaderList(ranks);
            mAdapter.setData(rankList.subList(6, rankList.size()));
        } else {
            mAdapter.setHeaderList(rankList);
        }
        mRecyclerView.setAdapter(mAdapter);
    }

    private void onError(Throwable e) {
        showErrorView();
    }

    private void onComplete() {
        if (mAdapter.getItemCount() == 0) {
            showEmptyView();
        } else {
            showContentView();
        }
    }
}
