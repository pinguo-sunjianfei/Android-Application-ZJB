package com.idrv.coach.ui;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.idrv.coach.R;
import com.idrv.coach.bean.AdvBean;
import com.idrv.coach.bean.AdvShareInfo;
import com.idrv.coach.bean.HomePage;
import com.idrv.coach.bean.Message;
import com.idrv.coach.bean.WebParamBuilder;
import com.idrv.coach.bean.WebShareBean;
import com.idrv.coach.bean.event.EventConstant;
import com.idrv.coach.data.constants.SPConstant;
import com.idrv.coach.data.manager.RxBusManager;
import com.idrv.coach.data.manager.UrlParserManager;
import com.idrv.coach.data.model.HomePageModel;
import com.idrv.coach.ui.adapter.HomeAdapter;
import com.idrv.coach.ui.fragment.BaseFragment;
import com.idrv.coach.ui.view.AdvDialog;
import com.idrv.coach.ui.view.decoration.SpacesItemDecoration;
import com.idrv.coach.ui.widget.SwipeRefreshLayout;
import com.idrv.coach.utils.Logger;
import com.idrv.coach.utils.PixelUtil;
import com.idrv.coach.utils.PreferenceUtil;
import com.idrv.coach.utils.StatisticsUtil;
import com.idrv.coach.utils.TimeUtil;
import com.idrv.coach.utils.ValidateUtil;
import com.idrv.coach.utils.helper.ResHelper;
import com.idrv.coach.utils.helper.UIHelper;
import com.zjb.loader.ZjbImageLoader;
import com.zjb.volley.utils.NetworkUtil;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * time:2016/8/1
 * description:
 *
 * @author sunjianfei
 */
public class HomeFragment extends BaseFragment<HomePageModel> {
    public static final String KEY_FIRST_USE_HOME_PAGE = "first_use_home_page";

    @InjectView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @InjectView(R.id.refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.use_time_tv)
    TextView mUseTimeTv;
    @InjectView(R.id.header_layout)
    FrameLayout mHeaderLayout;
    @InjectView(R.id.main_layout)
    FrameLayout mMainLayout;

    HomeAdapter mAdapter;
    boolean isFirstRefresh = true;


    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_home, container, false);
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
        mAdapter = new HomeAdapter();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.addItemDecoration(new SpacesItemDecoration(0, 0, 0, (int) PixelUtil.dp2px(30), true));
        mRecyclerView.setAdapter(mAdapter);
        //设置滚动监听
//        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                mCurrentScroll += dy;
//                mHeaderLayout.setTranslationY(mCurrentScroll);
//            }
//        });


        //设置上下拉刷新逻辑
        mSwipeRefreshLayout.setMode(SwipeRefreshLayout.Mode.PULL_FROM_START);
        mSwipeRefreshLayout.setOnRefreshListener(this::loadHistory);

        initViewModel();
        registerEvent();
    }

    @Override
    protected void onLazyLoad() {
        showGuideView();
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

    private void registerEvent() {
        //1.分享资讯或者个人网站之后更新广告次数
        RxBusManager.register(this, EventConstant.KEY_NEWS_OR_WEBSITE_SHARE_COMPLETE, String.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::updateShareCus, Logger::e);
        //2.注册新消息事件
        RxBusManager.register(this, EventConstant.KEY_HOME_NEW_MESSAGE, Message.class)
                .subscribe(this::onNewMessage, Logger::e);
    }

    private void initViewModel() {
        mViewModel = new HomePageModel();
        getHomePageCache();
        getPopAdv();
    }


    /**
     * 获取弹窗广告数据
     */
    private void getPopAdv() {
        //1.先从本地获取缓存
        Subscription cacheSubscription = mViewModel.getAdvCache()
                .subscribe(__ -> Logger.e("success"), Logger::e);
        //2.然后再从服务器获取最新的广告数据
        Subscription subscription = mViewModel.getPopAdv()
                .subscribe(__ -> Logger.e("success"), Logger::e);
        addSubscription(cacheSubscription);
        addSubscription(subscription);
    }

    /**
     * 获取列表缓存数据
     */
    private void getHomePageCache() {
        Subscription subscription = mViewModel.getHomePageCache()
                .doOnTerminate(this::onCacheComplete)
                .subscribe(this::onNext, Logger::e);
        addSubscription(subscription);
    }

    /**
     * 拉去最新的列表数据
     */
    private void refresh() {
        Subscription subscription = mViewModel.refresh()
                .doOnTerminate(() -> {
                    //延迟一秒关闭,防止关闭太快.看着很奇怪
                    new Handler().postDelayed(() -> mSwipeRefreshLayout.setRefreshing(false), 1000);
                })
                .subscribe(this::onNext, this::onError, this::showContentView);
        addSubscription(subscription);
    }

    /**
     * 拉取历史记录
     */
    private void loadHistory() {
        Subscription subscription = mViewModel.loadHistory()
                .doOnTerminate(() -> mSwipeRefreshLayout.setRefreshing(false))
                .subscribe(this::onLoadHistoryNext, Logger::e);
        addSubscription(subscription);
    }

    private void onNext(HomePage page) {
        //设置背景图片
        ZjbImageLoader.create(page.getBgImaUrl())
                .setDisplayType(ZjbImageLoader.DISPLAY_FADE_IN)
                .setQiniu(ResHelper.getScreenWidth(), ResHelper.getScreenHeight())
                .setFadeInTime(1000)
                .setDefaultDrawable(new ColorDrawable(ContextCompat.getColor(getContext(),R.color.bg_main)))
                .into(mMainLayout);

        List<Message> messages = page.getMessages();
        mUseTimeTv.setText(page.getLoginSum() + "");

        mAdapter.setData(messages);
        mAdapter.notifyDataSetChanged();
        mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
        //是否是第一次刷新
        if (isFirstRefresh) {
            showAllTips();
        }
        isFirstRefresh = false;
        //显示主界面
        showContentView();
    }

    private void onLoadHistoryNext(HomePage page) {
        List<Message> messages = page.getMessages();
        if (ValidateUtil.isValidate(messages)) {
            mAdapter.addDataFromFirst(messages);
            mAdapter.notifyDataSetChanged();
            mRecyclerView.scrollToPosition(messages.size());
        } else {
            UIHelper.shortToast(R.string.no_more_message);
        }
    }

    private void onError(Throwable e) {
        UIHelper.shortToast(R.string.network_error);
        if (null == mViewModel.getHomePage()) {
            showErrorView();
        }
    }


    private void onCacheComplete() {
        mSwipeRefreshLayout.setRefreshing(true);
        refresh();
    }

    /**
     * 更新传播次数
     *
     * @param s
     */
    private void updateShareCus(String s) {
        mViewModel.updateData();
    }

    /**
     * 有新消息时
     *
     * @param message
     */
    private void onNewMessage(Message message) {
        //更新缓存
        Subscription subscription = mViewModel.updateMessageCache(message)
                .subscribe(date -> {
                }, Logger::e);
        addSubscription(subscription);

        mAdapter.addData(message);
        mAdapter.notifyDataSetChanged();
        mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    /**
     * 显示首页的所有弹窗
     */
    private void showAllTips() {
        //广告实体
        AdvBean mAdvBean = mViewModel.getAdvBean();
        //最新广告对应的url
        String loadedAdvPicUrl = PreferenceUtil.getString(SPConstant.KEY_ADV_PIC);
        //点击查看的广告
        String viewsAdvId = PreferenceUtil.getString(SPConstant.KEY_ADV_VIEWS);
        //广告的有效期
        String deadTime = null != mAdvBean ? mAdvBean.getDeadtime() : null;
        boolean isAdvValid = true;
        if (!TimeUtil.compareDate(deadTime)) {
            isAdvValid = false;
        }
        //上次广告弹出的时间
        String time = PreferenceUtil.getString(SPConstant.KEY_SHOW_ADV_TIME);
        //距离上次广告弹出是否超过2天,如果没有2天,则不弹出,反之则弹
        boolean isValidTime = true;
        if (!TextUtils.isEmpty(time)) {
            isValidTime = TimeUtil.compareDate(time, 2);
        }

        if (null != mAdvBean
                && mAdvBean.getImageUrl().equals(loadedAdvPicUrl)
                && !mAdvBean.getId().equals(viewsAdvId)
                && isValidTime
                && isAdvValid) {
            //2.广告
            AdvDialog dialog = new AdvDialog(getContext());
            dialog.showImage(mAdvBean.getImageUrl());
            dialog.setOnDismissListener(dl -> {
                //如果是取消,则存入时间,2天后再弹
                PreferenceUtil.putString(SPConstant.KEY_SHOW_ADV_TIME, TimeUtil.getSimpleTime());
            });
            dialog.setClickListener(() -> {
                //首页广告点击统计
                StatisticsUtil.onEvent(R.string.visit_home_educate_h5);
                PreferenceUtil.putString(SPConstant.KEY_ADV_VIEWS, mAdvBean.getId());
                PreferenceUtil.putString(SPConstant.KEY_SHOW_ADV_TIME, TimeUtil.getSimpleTime());
                String schema = mAdvBean.getSchema();
                //如果是链接跳转
                if (!TextUtils.isEmpty(schema)) {
                    //添加H5基本参数
                    UrlParserManager.getInstance().addParams(UrlParserManager.METHOD_ADVTYPE, "0");
                    WebShareBean shareBean = null;
                    AdvShareInfo shareInfo = mAdvBean.getShareInfo();
                    //如果需要分享
                    if (mAdvBean.isShare() && null != shareInfo) {
                        String shareUrl = shareInfo.getShareUrl();
                        shareBean = new WebShareBean();
                        shareBean.setShareTitle(shareInfo.getShareTitle());
                        shareBean.setShareContent(shareInfo.getShareContent());
                        shareBean.setShareUrl(shareUrl);
                        shareBean.setShareImageUrl(shareInfo.getShareImageUrl());
                    }
                    ToolBoxWebActivity.launch(getContext(), WebParamBuilder.create()
                            .setTitle(mAdvBean.getTitle())
                            .setUrl(schema)
                            .setPageTag(R.string.share_home_educate_h5)
                            .setShareBean(shareBean));
                }
                dialog.dismiss();
            });
            dialog.show();
        }
    }

    /**
     * 第一次进入 显示引导
     */
    private void showGuideView() {
        //判断是否显示过引导,如果没有显示过,则显示引导
        if (!PreferenceUtil.getBoolean(KEY_FIRST_USE_HOME_PAGE)) {
            View mGuideView = LayoutInflater.from(getContext()).inflate(R.layout.vw_home_page_guide, null, false);
            View mRootLayout = mGuideView.findViewById(R.id.root_layout);
            mGuideView.getBackground().setAlpha(190);
            mRootLayout.setOnTouchListener((__, ___) -> true);
            mGuideView.findViewById(R.id.btn_ok).setOnClickListener(v -> {
                mGuideView.setVisibility(View.GONE);
                PreferenceUtil.putBoolean(KEY_FIRST_USE_HOME_PAGE, true);
            });
            ((ViewGroup) mRecyclerView.getRootView().findViewById(android.R.id.content)).addView(mGuideView);
        }
    }

}
