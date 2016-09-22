package com.idrv.coach.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.idrv.coach.R;
import com.idrv.coach.bean.DynamicPage;
import com.idrv.coach.bean.TeamInvite;
import com.idrv.coach.bean.event.EventConstant;
import com.idrv.coach.data.constants.SPConstant;
import com.idrv.coach.data.constants.SchemeConstant;
import com.idrv.coach.data.manager.PushCenterManager;
import com.idrv.coach.data.manager.RxBusManager;
import com.idrv.coach.data.model.DynamicModel;
import com.idrv.coach.ui.adapter.DynamicAdapter;
import com.idrv.coach.ui.view.decoration.SpacesItemDecoration;
import com.idrv.coach.ui.widget.EmptyRecyclerView;
import com.idrv.coach.ui.widget.SwipeRefreshLayout;
import com.idrv.coach.utils.Logger;
import com.idrv.coach.utils.PixelUtil;
import com.idrv.coach.utils.PreferenceUtil;
import com.idrv.coach.utils.ScrollUtils;
import com.idrv.coach.utils.helper.DialogHelper;
import com.idrv.coach.utils.helper.UIHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Subscription;

/**
 * time:2016/3/24
 * description:
 *
 * @author sunjianfei
 */
public class DynamicActivity extends BaseActivity<DynamicModel> implements DynamicAdapter.LikeListener {
    public static final String KEY_FIRST_USE_DYNAMIC = "first_use_dynamic";

    @InjectView(R.id.recycler_view)
    EmptyRecyclerView mRecyclerView;
    @InjectView(R.id.refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.title_bar_layout)
    View mTitleBarLayout;
    @InjectView(R.id.empty_layout)
    View mEmptyView;

    DynamicAdapter mAdapter;
    //列表滑动的距离
    private int mCurrentScroll;

    private float mHeaderViewHeight = PixelUtil.dp2px(225);

    public static void launch(Context context) {
        Intent intent = new Intent(context, DynamicActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_dynamic);
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

    private void initView() {
        mAdapter = new DynamicAdapter();
        mAdapter.setListener(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setEmptyView(mEmptyView);
        mRecyclerView.toggleHeaderStatus(true);

        mRecyclerView.addItemDecoration(new SpacesItemDecoration(0, 0, 0, (int) PixelUtil.dp2px(1), false));
        mRecyclerView.setAdapter(mAdapter);
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
        PushCenterManager.getInstance().updateRedPoint(SchemeConstant.TYPE_NEW_DYNAMIC, true);
//        showGuideView();
    }

    private void registerEvent() {
        RxBusManager.register(this, EventConstant.KEY_SHARE_CALL_BACK_COMPLETE, String.class)
                .subscribe(__ -> refresh(), Logger::e);
    }

    private void initViewModel() {
        mViewModel = new DynamicModel();
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

    private void onRefreshNext(DynamicPage page) {
        TeamInvite invite = page.getTeamInvite();
        mAdapter.setData(page.getTrends());
        mAdapter.setPraiserAvators(page.getPraiserAvators());
        mAdapter.setPraiseSum(page.getPraiseSum());
        mAdapter.setTeamInvite(invite);
        mAdapter.notifyDataSetChanged();
        if (null != invite && !mViewModel.isShowInviteDialog()) {
            showInviteDialog(invite);
        }
    }

    /**
     * 邀请加入团队的对话框
     *
     * @param invite
     */
    private void showInviteDialog(TeamInvite invite) {
        DialogHelper.create(DialogHelper.TYPE_NORMAL)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .title(getString(R.string.invite_notify))
                .content(getString(R.string.invite_content, invite.getOwnerName(), invite.getTeamName()))
                .leftButton(getString(R.string.not_join), getResources().getColor(R.color.black_54))
                .rightButton(getString(R.string.join), getResources().getColor(R.color.themes_main))
                .rightBtnClickListener((dialog, v) -> {
                    dialog.dismiss();
                    joinTeam(invite.getTeamId());
                })
                .leftBtnClickListener((dialog, v) -> {
                    PreferenceUtil.putBoolean(SPConstant.KEY_INVITE, true);
                    dialog.dismiss();
                })
                .show();
    }

    /**
     * 阻塞用户操作的加载圈
     */
    final protected void showDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            return;
        }
        mProgressDialog = DialogHelper.create(DialogHelper.TYPE_PROGRESS)
                .cancelable(true)
                .canceledOnTouchOutside(false)
                .progressText(getResources().getString(R.string.join_now)).show();
    }

    private void joinTeam(String teamId) {
        showDialog();
        Subscription subscription = mViewModel.joinTeam(teamId)
                .subscribe(__ -> {
                    dismissProgressDialog();
                    mAdapter.setTeamInvite(null);
                    mAdapter.notifyItemChanged(0);
                    refresh();
                }, this::onError);
        addSubscription(subscription);
    }

    private void onLoadMoreNext(DynamicPage page) {
        mAdapter.addData(page.getTrends());
        mAdapter.notifyDataSetChanged();
    }

    private void onError(Throwable e) {
        UIHelper.shortToast(R.string.network_error);
        showContentView();
        mSwipeRefreshLayout.setRefreshing(false);
        mSwipeRefreshLayout.setPullUpRefreshing(false);
    }

    private void onComplete() {
        showContentView();
        mSwipeRefreshLayout.setRefreshing(false);
        mSwipeRefreshLayout.setPullUpRefreshing(false);
    }

    @Override
    public void onLike(String targetId, String coachId, int type) {
        Subscription subscription = mViewModel.like(targetId, coachId, type)
                .subscribe(Logger::e, Logger::e);
        addSubscription(subscription);
    }

    @Override
    public void onShowInvite(TeamInvite invite) {
        showInviteDialog(invite);
    }

//    private void showGuideView() {
//        boolean isEntered = PreferenceUtil.getBoolean(KEY_FIRST_USE_DYNAMIC);
//        if (isEntered) {
//            return;
//        }
//        View view = LayoutInflater.from(this).inflate(R.layout.vw_guide, null, false);
//        ViewPager mViewPager = (ViewPager) view.findViewById(R.id.guide_viewpager);
//        CircleIndicator mCircleIndicator = (CircleIndicator) view.findViewById(R.id.guide_indicator);
//
//
//        Fragment fragment1 = TeachGuideFragment.newInstance(R.drawable.dynamic_1, false);
//        Fragment fragment2 = TeachGuideFragment.newInstance(R.drawable.dynamic_2, false);
//        Fragment fragment3 = TeachGuideFragment.newInstance(R.drawable.dynamic_3, true);
//
//        List<Fragment> fragments = new ArrayList<>(4);
//        fragments.add(fragment1);
//        fragments.add(fragment2);
//        fragments.add(fragment3);
//
//        ViewPagerAdapter mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
//        mAdapter.setData(fragments);
//        mViewPager.setAdapter(mAdapter);
//        mCircleIndicator.setViewPager(mViewPager);
//        view.getBackground().setAlpha(130);
//
//        ViewGroup rootView = (ViewGroup) mRecyclerView.getRootView().findViewById(android.R.id.content);
//        RxBusManager.register(this, EventConstant.KEY_TEACH_GUIDE_CLOSE, String.class)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(__ -> {
//                    //写入标志,标记已经非第一次使用
//                    PreferenceUtil.putBoolean(KEY_FIRST_USE_DYNAMIC, true);
//                    rootView.removeView(view);
//
//                }, Logger::e);
//        rootView.addView(view);
//    }

    @OnClick(R.id.back)
    public void onBackClick() {
        this.finish();
    }

    @OnClick(R.id.share_btn)
    public void onShareBtnClick() {
        NewsHallActivity.launch(this);
    }
}
