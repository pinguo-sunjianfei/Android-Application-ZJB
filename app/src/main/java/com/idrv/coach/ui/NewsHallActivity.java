package com.idrv.coach.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.idrv.coach.R;
import com.idrv.coach.bean.Task;
import com.idrv.coach.bean.Visitor;
import com.idrv.coach.bean.WebParamBuilder;
import com.idrv.coach.bean.WebShareBean;
import com.idrv.coach.bean.event.EventConstant;
import com.idrv.coach.bean.share.ShareWebProvider;
import com.idrv.coach.bean.visitInfoPage;
import com.idrv.coach.data.constants.SPConstant;
import com.idrv.coach.data.constants.SchemeConstant;
import com.idrv.coach.data.manager.LoginManager;
import com.idrv.coach.data.manager.PushCenterManager;
import com.idrv.coach.data.manager.RxBusManager;
import com.idrv.coach.data.manager.UrlParserManager;
import com.idrv.coach.data.model.NewsHallModel;
import com.idrv.coach.ui.view.NewsDialog;
import com.idrv.coach.utils.BitmapUtil;
import com.idrv.coach.utils.Logger;
import com.idrv.coach.utils.PreferenceUtil;
import com.idrv.coach.utils.StatisticsUtil;
import com.idrv.coach.utils.TimeUtil;
import com.idrv.coach.utils.ValidateUtil;
import com.idrv.coach.utils.helper.UIHelper;
import com.idrv.coach.wxapi.WXEntryActivity;
import com.zjb.loader.ZjbImageLoader;
import com.zjb.volley.utils.NetworkUtil;

import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * time:2016/3/14
 * description:资讯大厅
 *
 * @author sunjianfei
 */
public class NewsHallActivity extends BaseActivity<NewsHallModel> implements View.OnClickListener {
    public static final String KEY_FIRST_USE_NEWS = "first_use_news";
    private static final String NEWS_DEFAULT_URL = "{HOST}/news/{DATE}?taskId={TASKID}&uid={uid}&appName={appName}";
    private static final String NEWS_SHARE_DEFAULT_URL = "{HOST}/news/{DATE}?taskId={TASKID}&uid={uid}";

    @InjectView(R.id.news_time)
    TextView mNewsTimeTv;
    @InjectView(R.id.news_title)
    TextView mNewsTitleTv;
    @InjectView(R.id.news_short_desc)
    TextView mNewsDescTv;
    @InjectView(R.id.tags_layout)
    LinearLayout mTagsLayout;
    @InjectView(R.id.how_to_use)
    TextView mHelpTv;
    //资讯帮助弹窗
    private PopupWindow mPopupWindow;

    public static void launch(Context context) {
        Intent intent = new Intent(context, NewsHallActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_news_hall);
        ButterKnife.inject(this);
        initToolBar();
        initModel();
        registerEvent();
        showGuideView();
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
        RxBusManager.register(this, EventConstant.KEY_SHARE_COMPLETE, String.class)
                .subscribe(this::shareComplete, Logger::e);
    }

    private void initToolBar() {
        mToolbarLayout.setTitleTxt(R.string.today_news);
    }

    /**
     * 初始化model
     */
    private void initModel() {
        mViewModel = new NewsHallModel();
        getCache();
        //清除小红点数据
        PushCenterManager.getInstance().updateRedPoint(SchemeConstant.TYPE_NEW_NEWS, true);
    }

    private void setUp(Task task) {
        mNewsTimeTv.setText(TimeUtil.getSimpleTime());
        mNewsTitleTv.setText(task.getTaskName());
        mNewsDescTv.setText(task.getDescribe());
        showNewsTags(mTagsLayout, task.getTaskType());
        //下载分享的icon
        ZjbImageLoader.create(task.getShareIcon())
                .setDisplayType(ZjbImageLoader.DISPLAY_DEFAULT)
                .load();
    }

    private void getVisitInfo() {
        int week = TimeUtil.getDayOfWeek();
        String time = PreferenceUtil.getString(SPConstant.KEY_NEWS_ACCESS_CHECKED);

        //如果是星期五,才会弹出教育对话框
        if (week == 5 && !TimeUtil.isSameDay(time)) {
            Subscription subscription = mViewModel.getVisitInfo()
                    .subscribe(this::showEducateDialog, Logger::e);
            addSubscription(subscription);
        }
    }

    /**
     * 获取缓存
     */
    private void getCache() {
        Subscription subscription = mViewModel.getTaskCache()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onNext, this::onCacheError, this::onCacheComplete);
        addSubscription(subscription);
    }

    /**
     * 获取最新任务
     */
    private void refresh() {
        Subscription subscription = mViewModel.getNewTask()
                .subscribe(this::onNext, this::onRefreshError, this::showContentView);
        addSubscription(subscription);
    }

    /**
     * 分享完成之后的服务器回调
     */
    private void shareComplete(String channel) {
        //1.访问数变更对话框
        getVisitInfo();

        //2.先通知首页增加打广告的次数
        RxBusManager.post(EventConstant.KEY_NEWS_OR_WEBSITE_SHARE_COMPLETE, "");
        //3.回调服务器
        Subscription subscription = mViewModel.shareComplete(channel)
                .subscribe(this::onCallBackComplete, Logger::e);
        addSubscription(subscription);

        //上次分享的时间
        String time = PreferenceUtil.getString(SPConstant.KEY_SHARE_NEWS_TIME);
        if (TextUtils.isEmpty(time) || !TimeUtil.isSameDay(time)) {
            //如果当天没分享过,第一次分享,给toast提示
            UIHelper.shortToast(R.string.first_share);
            PreferenceUtil.putString(SPConstant.KEY_SHARE_NEWS_TIME, TimeUtil.getSimpleTime());
        }
    }

    private void onCallBackComplete(String s) {
        RxBusManager.post(EventConstant.KEY_SHARE_CALL_BACK_COMPLETE, "");
    }

    private void onNext(Task task) {
        //添加H5相关的基本参数
        UrlParserManager.getInstance().addParams(UrlParserManager.METHOD_TASKID, String.valueOf(task.getId()));
        setUp(task);
    }

    private void onCacheError(Throwable e) {
        refresh();
    }

    private void onCacheComplete() {
        refresh();
    }

    private void onRefreshError(Throwable e) {
        if (null == mViewModel.getTask()) {
            //如果没有缓存,请求错误,显示错误界面
            showErrorView();
        } else {
            //如果有缓存,但是请求错误,显示缓存
            showContentView();
        }
        Logger.e(e);
    }

    /**
     * 分享
     */
    private void share() {
        //分享资讯统计
        StatisticsUtil.onEvent(R.string.share_news_hall);
        ShareWebProvider provider = new ShareWebProvider();
        Task task = mViewModel.getTask();
        String shareUrl = task.getShareUrl();
        if (TextUtils.isEmpty(shareUrl)) {
            shareUrl = NEWS_SHARE_DEFAULT_URL;
        }
        String imagePath = BitmapUtil.getImagePath(LoginManager.getInstance().getLoginUser().getHeadimgurl());
        provider.setTitle(task.getShareTitle());
        provider.setDesc(task.getShareDescribe());
        provider.setImagePath(imagePath);
        provider.setUrl(shareUrl);
        WXEntryActivity.launch(this, provider, R.string.share_str, R.string.share_subject);
    }

    @OnClick({R.id.btn_share, R.id.view_news, R.id.how_to_use})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_share:
                //统计
                share();
                break;
            case R.id.view_news:
                gotoWebActivity();
                break;
            case R.id.how_to_use:
                showInfPopWindow();
                break;
        }
    }

    private void gotoWebActivity() {
        Task task = mViewModel.getTask();
        String url = task.getDetailUrl();
        if (TextUtils.isEmpty(url)) {
            url = NEWS_DEFAULT_URL;
        }
        ToolBoxWebActivity.launch(this, WebParamBuilder.create()
                .setTitle(getString(R.string.views_adv_title))
                .setUrl(url)
                .setPageTag(R.string.share_news_preview)
                .setShareBean(buildShareBean(task)));
    }

    /**
     * 构建分享的bean
     *
     * @return
     */
    public WebShareBean buildShareBean(Task task) {
        WebShareBean bean = new WebShareBean();
        String targetUrl = task.getShareUrl();
        if (TextUtils.isEmpty(targetUrl)) {
            targetUrl = NEWS_SHARE_DEFAULT_URL;
        }

        bean.setShareTitle(task.getShareTitle());
        bean.setShareContent(task.getShareDescribe());
        bean.setShareUrl(targetUrl);
        bean.setPageSubject(R.string.share_subject);
        bean.setShareImageUrl(LoginManager.getInstance().getLoginUser().getHeadimgurl());
        return bean;
    }

    /**
     * 教育的对话框
     */
    private void showEducateDialog(visitInfoPage page) {
        int currentVisNum = page.getVisitNum();
        int lastVisNum = PreferenceUtil.getInt(SPConstant.KEY_NEWS_ACCESS_NUM);
        List<Visitor> list = page.getVisitors();

        PreferenceUtil.putString(SPConstant.KEY_NEWS_ACCESS_CHECKED, TimeUtil.getSimpleTime());
        PreferenceUtil.putInt(SPConstant.KEY_NEWS_ACCESS_NUM, currentVisNum);
        //必须要大于3个才弹出对话框
        if (ValidateUtil.isValidate(list) && list.size() >= 3) {
            int added = currentVisNum - lastVisNum;
            //如果现在的访问数量比上次的访问数量增加了10
            if (currentVisNum - lastVisNum > 10) {
                NewsDialog dialog = new NewsDialog(this);
                dialog.setAvatar(list);
                dialog.setVisNum(added);
                dialog.show();
            }
        }
    }

    private void showNewsTags(LinearLayout layout, String tagStr) {
        if (TextUtils.isEmpty(tagStr)) {
            layout.setVisibility(View.GONE);
        } else {
            layout.setVisibility(View.VISIBLE);
            String[] tags = tagStr.split(",");
            String[] resultTags;
            if (tags.length == 0) {
                layout.setVisibility(View.GONE);
            } else {
                layout.setVisibility(View.VISIBLE);
                if (tags.length > 3) {
                    resultTags = Arrays.copyOfRange(tags, 0, 3);
                } else {
                    resultTags = tags;
                }
                int size = resultTags.length;
                int count = layout.getChildCount();
                for (int i = 0; i < count; i++) {
                    TextView textView = (TextView) layout.getChildAt(i);
                    if (size > i) {
                        textView.setVisibility(View.VISIBLE);
                        textView.setText(resultTags[i]);
                    } else {
                        textView.setVisibility(View.GONE);
                    }
                }
            }
        }
    }

    private void showInfPopWindow() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        } else {
            View view = LayoutInflater.from(this).inflate(R.layout.vw_news_hall_pop, null, false);
            mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            Drawable drawable = new ColorDrawable(0x00FFFFFF);
            mPopupWindow.setBackgroundDrawable(drawable);
            mPopupWindow.setOutsideTouchable(true);

            view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            int popupWidth = view.getMeasuredWidth();
            int popupHeight = view.getMeasuredHeight();
            int[] location = new int[2];
            mHelpTv.getLocationOnScreen(location);
            mPopupWindow.showAsDropDown(mHelpTv, (int) (-location[0] * 0.6), 0);
        }
    }

    /**
     * 第一次进入 显示引导
     */
    private void showGuideView() {
        //判断是否显示过引导,如果没有显示过,则显示引导
        if (!PreferenceUtil.getBoolean(KEY_FIRST_USE_NEWS)) {
            View mGuideView = LayoutInflater.from(this).inflate(R.layout.vw_news_hall_guide, null, false);
            View mRootLayout = mGuideView.findViewById(R.id.root_layout);
            mGuideView.getBackground().setAlpha(190);
            mRootLayout.setOnTouchListener((__, ___) -> true);
            mGuideView.findViewById(R.id.btn_ok).setOnClickListener(v -> {
                mGuideView.setVisibility(View.GONE);
                PreferenceUtil.putBoolean(KEY_FIRST_USE_NEWS, true);
            });
            ((ViewGroup) mNewsTitleTv.getRootView().findViewById(android.R.id.content)).addView(mGuideView);
        }
    }
}
