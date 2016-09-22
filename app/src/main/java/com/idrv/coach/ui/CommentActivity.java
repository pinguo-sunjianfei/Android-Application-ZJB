package com.idrv.coach.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.idrv.coach.R;
import com.idrv.coach.bean.Comment;
import com.idrv.coach.bean.event.EventConstant;
import com.idrv.coach.data.constants.SPConstant;
import com.idrv.coach.data.manager.RxBusManager;
import com.idrv.coach.data.model.CommentModel;
import com.idrv.coach.ui.adapter.CommentListAdapter;
import com.idrv.coach.ui.view.decoration.SpacesItemDecoration;
import com.idrv.coach.ui.widget.SwipeRefreshLayout;
import com.idrv.coach.utils.Logger;
import com.idrv.coach.utils.PixelUtil;
import com.idrv.coach.utils.PreferenceUtil;
import com.idrv.coach.utils.ValidateUtil;
import com.idrv.coach.utils.helper.UIHelper;
import com.zjb.volley.utils.NetworkUtil;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscription;

/**
 * time:2016/8/8
 * description:评论/咨询
 *
 * @author sunjianfei
 */
public class CommentActivity extends BaseActivity<CommentModel> implements CommentListAdapter.DeleteCommentListener {
    @InjectView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @InjectView(R.id.refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    CommentListAdapter mAdapter;

    public static void launch(Context context) {
        Intent intent = new Intent(context, CommentActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_comment);
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

    @Override
    public void onClickEmpty() {
        if (NetworkUtil.isConnected(this)) {
            showProgressView();
            refresh();
        } else {
            UIHelper.shortToast(R.string.network_error);
        }
    }

    private void initToolBar() {
        mToolbarLayout.setTitle(R.string.comment_title);
    }

    private void initView() {
        mAdapter = new CommentListAdapter();
        mAdapter.setDeleteCommentListener(this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.addItemDecoration(new SpacesItemDecoration(0, 0, 0, (int) PixelUtil.dp2px(0), true));
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout.setMode(SwipeRefreshLayout.Mode.BOTH);
        mSwipeRefreshLayout.setOnRefreshListener(this::refresh);
        mSwipeRefreshLayout.setOnPullUpRefreshListener(this::loadMore);
    }

    private void initViewModel() {
        mViewModel = new CommentModel();
        refresh();
    }

    /**
     * 刷新
     */
    private void refresh() {
        Subscription subscription = mViewModel.refresh(mAdapter::clear)
                .subscribe(this::onRefreshNext, this::onError, this::onComplete);
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

    private void onRefreshNext(List<Comment> list) {
        mAdapter.setData(list);
        mAdapter.notifyDataSetChanged();
    }

    private void onLoadMoreNext(List<Comment> list) {
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
    public void onCommentDelete(Comment comment, int position) {
        showProgressDialog(comment.getMessageType() == 4 ? R.string.delete_access_now : R.string.delete_comment_now);
        Subscription subscription = mViewModel.deleteComment(comment.getMessageType(), comment.getId())
                .doOnTerminate(this::dismissProgressDialog)
                .subscribe(s -> {
                    //更新照片数量
                    int commentNum = PreferenceUtil.getInt(SPConstant.KEY_COMMENT_NUM, -1);
                    commentNum -= 1;
                    PreferenceUtil.putInt(SPConstant.KEY_COMMENT_NUM, commentNum);
                    RxBusManager.post(EventConstant.KEY_COMMENT_DELETE_SUCCESS, s);
                }, Logger::e);
        addSubscription(subscription);

        mAdapter.getData().remove(position);
        mAdapter.notifyDataSetChanged();
    }
}
