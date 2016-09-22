package com.idrv.coach.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.idrv.coach.R;
import com.idrv.coach.bean.Coach;
import com.idrv.coach.bean.Comment;
import com.idrv.coach.bean.Picture;
import com.idrv.coach.bean.Rank;
import com.idrv.coach.bean.User;
import com.idrv.coach.bean.WebParamBuilder;
import com.idrv.coach.bean.WebShareBean;
import com.idrv.coach.bean.WebSitePage;
import com.idrv.coach.bean.WebSitePhoto;
import com.idrv.coach.bean.event.EventConstant;
import com.idrv.coach.bean.share.ShareWebProvider;
import com.idrv.coach.data.constants.SPConstant;
import com.idrv.coach.data.manager.LoginManager;
import com.idrv.coach.data.manager.RxBusManager;
import com.idrv.coach.data.model.WebSiteModel;
import com.idrv.coach.ui.adapter.CommentAdapter;
import com.idrv.coach.ui.adapter.WebSitePhotoAdapter;
import com.idrv.coach.ui.view.CheckProfileDialog;
import com.idrv.coach.ui.widget.SwipeRefreshLayout;
import com.idrv.coach.utils.BitmapUtil;
import com.idrv.coach.utils.Logger;
import com.idrv.coach.utils.PixelUtil;
import com.idrv.coach.utils.PreferenceUtil;
import com.idrv.coach.utils.StatisticsUtil;
import com.idrv.coach.utils.helper.UIHelper;
import com.idrv.coach.utils.helper.ViewUtils;
import com.idrv.coach.wxapi.WXEntryActivity;
import com.zjb.volley.utils.NetworkUtil;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * time:2016/6/3
 * description:
 *
 * @author sunjianfei
 */
public class MyWebSiteActivity extends BaseActivity<WebSiteModel> implements View.OnClickListener {
    @InjectView(R.id.list_view)
    ListView mListView;
    @InjectView(R.id.refresh_layout)
    public SwipeRefreshLayout mRefreshLayout;
    @InjectView(R.id.title_tv)
    TextView mTitleTv;
    @InjectView(R.id.bottom_layout)
    View mBottomLayout;
    @InjectView(R.id.right_share)
    ImageView mRightShareBtn;

    CheckProfileDialog mCheckProfileDialog;

    TextView mNickNameTv;
    ImageView mAvatarIv;
    TextView mSchoolTv;
    TextView mNumOfDaysTv;
    TextView mAdvTimesTv;
    RadioButton mPhotoRadioBtn;
    RadioButton mCommentRadioBtn;

    CommentAdapter mCommentAdapter;
    WebSitePhotoAdapter mPhotoAdapter;

    private static final String KEY_PARAM_RANK = "param_rank";

    //默认显示照片
    private static final String TAG_PHOTO = "photo";
    private static final String TAG_COMMENT = "comment";

    public static final String WEB_SITE_DEFAULT_URL = "{HOST}/profile/{uid}?appName={appName}";
    private static final String WEB_SITE_DEFAULT_SHARE_URL = "{HOST}/profile/{uid}";

    //标识当前是哪个列表,默认是照片
    private String currentTag = TAG_PHOTO;

    boolean isOwner;
    Rank mRank;

    public static void launch(Context context) {
        Intent intent = new Intent(context, MyWebSiteActivity.class);
        context.startActivity(intent);
    }

    public static void launch(Context context, Rank rank) {
        Intent intent = new Intent(context, MyWebSiteActivity.class);
        intent.putExtra(KEY_PARAM_RANK, rank);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_my_web_site);
        ButterKnife.inject(this);
        initViewModel();
        initView();
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
        //1.删除照片
        RxBusManager.register(this, EventConstant.KEY_PHOTO_DELETE, Integer.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::photoDelete, Logger::e);
        //2.照片上传成功
        RxBusManager.register(this, EventConstant.KEY_REFRESH_PIC, String.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(__ -> refresh(), Logger::e);
        // 3. 驾校或者教龄的变化
        RxBusManager.register(this, EventConstant.KEY_REVISE_USERINFO, String.class)
                .subscribe(__ -> refreshDialog(), Logger::e);
        //4.分享个人网站后的回调
        RxBusManager.register(this, EventConstant.KEY_SHARE_COMPLETE, String.class)
                .subscribe(__ -> shareComplete(), Logger::e);
    }

    private void initView() {
        mTitleTv.setText(isOwner ? R.string.my_website : R.string.his_space);
        mBottomLayout.setVisibility(isOwner ? View.VISIBLE : View.GONE);

        if (!isOwner) {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mRefreshLayout.getLayoutParams();
            lp.bottomMargin = 0;
            mRightShareBtn.setVisibility(View.VISIBLE);
        }

        mCommentAdapter = new CommentAdapter();
        mPhotoAdapter = new WebSitePhotoAdapter();

        mPhotoAdapter.setOwner(isOwner);

        View mHeaderView = initHeaderView();
        mListView.addHeaderView(mHeaderView);
        mListView.setAdapter(mPhotoAdapter);

        mRefreshLayout.setMode(SwipeRefreshLayout.Mode.BOTH);
        mRefreshLayout.setOnRefreshListener(this::refresh);
        mRefreshLayout.setOnPullUpRefreshListener(this::loadMore);
    }

    private View initHeaderView() {
        View view = LayoutInflater.from(this).inflate(R.layout.vw_my_website_header, null, false);
        mNickNameTv = (TextView) view.findViewById(R.id.nick_name);
        mAvatarIv = (ImageView) view.findViewById(R.id.avatar);
        mSchoolTv = (TextView) view.findViewById(R.id.school);
        mNumOfDaysTv = (TextView) view.findViewById(R.id.use_app_day);
        mAdvTimesTv = (TextView) view.findViewById(R.id.adv_times);


        RadioButton[] radioButtons = new RadioButton[2];
        mPhotoRadioBtn = (RadioButton) view.findViewById(R.id.radio_photo);
        mCommentRadioBtn = (RadioButton) view.findViewById(R.id.radio_comment);

        radioButtons[0] = mPhotoRadioBtn;
        radioButtons[1] = mCommentRadioBtn;

        radioButtons[0].setTag(TAG_PHOTO);
        radioButtons[1].setTag(TAG_COMMENT);


        String nickName;
        String avatar;
        String school = null;

        if (null == mRank) {
            User user = LoginManager.getInstance().getLoginUser();
            Coach coach = LoginManager.getInstance().getCoach();

            nickName = user.getNickname();
            avatar = user.getHeadimgurl();

            if (null != coach) {
                school = coach.getDrivingSchool();
            }
        } else {
            nickName = mRank.getNickname();
            avatar = mRank.getHeadimgurl();
            school = mRank.getDrivingSchool();
        }

        mNickNameTv.setText(nickName);
        ViewUtils.showRingAvatar(mAvatarIv, avatar, (int) PixelUtil.dp2px(80), PixelUtil.dp2px(2), 0f, 0x4CFFFFFF);
        if (TextUtils.isEmpty(school)) {
            mSchoolTv.setVisibility(View.GONE);
        } else {
            mSchoolTv.setText(school);
        }

        for (int i = 0; i < radioButtons.length; i++) {
            RadioButton radioButton = radioButtons[i];
            ColorDrawable drawableNormal = new ColorDrawable(getResources().getColor(R.color.text_color_white));
            ColorDrawable drawableSelected = new ColorDrawable(getResources().getColor(R.color.themes_main));
            Rect rect = new Rect(0, 0, (int) PixelUtil.dp2px(80), 3);

            drawableNormal.setBounds(rect);
            drawableSelected.setBounds(rect);
            if (i == 0) {
                radioButton.setCompoundDrawables(null, null, null, drawableSelected);
            } else {
                radioButton.setCompoundDrawables(null, null, null, drawableNormal);
            }

            radioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                String tag = (String) buttonView.getTag();
                if (isChecked) {
                    currentTag = tag;
                    buttonView.setCompoundDrawables(null, null, null, drawableSelected);
                    // save index and top position
                    int index = mListView.getFirstVisiblePosition();
                    View v = mListView.getChildAt(0);
                    int top = (v == null) ? 0 : v.getTop();

                    if (tag.equals(TAG_PHOTO)) {
                        mListView.setAdapter(mPhotoAdapter);
                    } else if (tag.equals(TAG_COMMENT)) {
                        mListView.setAdapter(mCommentAdapter);
                    }
                    mListView.setSelectionFromTop(index, top);
                } else {
                    buttonView.setCompoundDrawables(null, null, null, drawableNormal);
                }
            });
        }
        return view;
    }

    private void initViewModel() {
        mViewModel = new WebSiteModel();
        mRank = getIntent().getParcelableExtra(KEY_PARAM_RANK);
        if (null == mRank) {
            isOwner = true;
            mViewModel.setUid(LoginManager.getInstance().getUid());
        } else {
            mViewModel.setUid(mRank.getId());
        }
        refresh();
    }

    private void refresh() {
        Subscription subscription = mViewModel.refresh()
                .subscribe(this::onRefreshNext, this::onError, this::onComplete);
        addSubscription(subscription);
    }

    /**
     * 加载更多
     */
    private void loadMore() {
        Subscription subscription = null;
        switch (currentTag) {
            case TAG_PHOTO:
                subscription = mViewModel.loadPhoto()
                        .subscribe(this::onPhotoLoadNext, this::onError, this::onComplete);
                break;
            case TAG_COMMENT:
                subscription = mViewModel.loadComment()
                        .subscribe(this::onCommentLoadNext, this::onError, this::onComplete);
                break;
        }

        if (null != subscription) {
            addSubscription(subscription);
        }
    }

    /**
     * 分享完成之后的服务器回调
     */
    private void shareComplete() {
        //先通知首页增加打广告的次数
        RxBusManager.post(EventConstant.KEY_NEWS_OR_WEBSITE_SHARE_COMPLETE, "");
        //回调服务器
        Subscription subscription = mViewModel.shareComplete()
                .subscribe(Logger::e, Logger::e);
        addSubscription(subscription);
    }

    private void photoDelete(int pos) {
        Picture picture = mPhotoAdapter.getResources().remove(pos);
        mPhotoAdapter.setData(WebSitePage.changePhoto(mPhotoAdapter.getResources()));
        mPhotoAdapter.notifyDataSetChanged();
        Subscription subscription = mViewModel.photoDelete(picture.getId())
                .subscribe(this::onPhotoDeleteSuccess, Logger::e);
        addSubscription(subscription);

        mViewModel.setPictureNum(mViewModel.getPictureNum() - 1);
        mPhotoRadioBtn.setText(R.string.photo_tab);
    }

    private void onPhotoDeleteSuccess(String s) {
        RxBusManager.post(EventConstant.KEY_PHOTO_DELETE_SUCCESS, "");
        int picNum = PreferenceUtil.getInt(SPConstant.KEY_PIC_NUMBER);
        if (picNum != 0) {
            PreferenceUtil.putInt(SPConstant.KEY_PIC_NUMBER, picNum - 1);
        }
        PreferenceUtil.putInt(SPConstant.KEY_PIC_NUMBER, picNum - 1);
    }

    private void onError(Throwable e) {
        Logger.e(e);
        mRefreshLayout.setRefreshing(false);
        mRefreshLayout.setPullUpRefreshing(false);
        if (!mViewModel.isLoaded()) {
            showErrorView();
        }
        UIHelper.shortToast(R.string.network_error);
    }

    private void onRefreshNext(WebSitePage page) {

        mPhotoAdapter.setData(page.getWebSitePhotos());
        mPhotoAdapter.setResources(page.getPictureList());
        mCommentAdapter.setData(page.getMessageList());

        mNumOfDaysTv.setText(getString(R.string.num_day, page.getLoginDay()));
        mAdvTimesTv.setText(getString(R.string.num_time, page.getConvey()));
        mPhotoRadioBtn.setText(R.string.photo_tab);
        mCommentRadioBtn.setText(R.string.comment_tab);

        mPhotoAdapter.notifyDataSetChanged();
        mCommentAdapter.notifyDataSetChanged();
    }

    private void onPhotoLoadNext(List<WebSitePhoto> list) {
        mPhotoAdapter.setResources(mViewModel.getPicList());
        mPhotoAdapter.setData(list);
        mPhotoAdapter.notifyDataSetChanged();
    }

    private void onCommentLoadNext(List<Comment> comments) {
        mCommentAdapter.addData(comments);
        mCommentAdapter.notifyDataSetChanged();
    }

    private void onComplete() {
        showContentView();
        mRefreshLayout.setRefreshing(false);
        mRefreshLayout.setPullUpRefreshing(false);
    }

    @OnClick({R.id.btn_review, R.id.btn_share, R.id.back, R.id.right_share})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_review:
                //统计
                StatisticsUtil.onEvent(R.string.Click_Preview_My_site);
                ToolBoxWebActivity.launch(this, WebParamBuilder.create()
                        .setUrl(WEB_SITE_DEFAULT_URL)
                        .setTitle(getString(R.string.reviews_my_website))
                        .setPageTag(R.string.share_my_site_preview)
                        .setShareBean(null));
                break;
            case R.id.btn_share:
                boolean isComplete = mViewModel.checkProfileComplete();
                if (!isComplete) {
                    showCheckProfileDialog();
                } else {
                    share(buildShareBean());
                }
                break;
            case R.id.back:
                this.finish();
                break;
            case R.id.right_share:
                share(buildShareBean());
                break;
        }
    }

    private void showCheckProfileDialog() {
        if (null != mCheckProfileDialog && mCheckProfileDialog.isShowing()) {
            return;
        }
        mCheckProfileDialog = new CheckProfileDialog(this);
        mCheckProfileDialog.setCancelable(true);
        mCheckProfileDialog.setCanceledOnTouchOutside(false);
        mCheckProfileDialog.show();
    }

    private void refreshDialog() {
        if (null != mCheckProfileDialog && mCheckProfileDialog.isShowing()) {
            mCheckProfileDialog.setUp();
        }
    }

    /**
     * 分享
     */
    private void share(WebShareBean bean) {
        //分享我的网站,统计
        User user = LoginManager.getInstance().getLoginUser();
        StatisticsUtil.onEvent(R.string.share_my_site, user.getUid(), user.getNickname());
        ShareWebProvider provider = new ShareWebProvider();
        String imagePath;
        String url = bean.getShareImageUrl();
        if (!TextUtils.isEmpty(url) && url.startsWith("http://")) {
            if (isOwner) {
                imagePath = BitmapUtil.getImagePath(url);
            } else {
                int size = (int) PixelUtil.dp2px(80);
                imagePath = BitmapUtil.getImagePath(url, size, size);
            }
        } else {
            imagePath = url;
        }
        provider.setTitle(bean.getShareTitle());
        provider.setDesc(bean.getShareContent());
        provider.setImagePath(imagePath);
        provider.setUrl(bean.getShareUrl());
        WXEntryActivity.launch(this, provider, R.string.share_str, bean.getPageSubject());
    }

    /**
     * 构建分享的bean
     *
     * @return
     */
    public WebShareBean buildShareBean() {
        WebShareBean bean = new WebShareBean();
        String title = isOwner ? getString(R.string.website_share_student_title,
                LoginManager.getInstance().getLoginUser().getNickname())
                : getString(R.string.guest_share_title, mRank.getNickname());
        String shareIcon = isOwner ? LoginManager.getInstance().getLoginUser().getHeadimgurl()
                : mRank.getHeadimgurl();

        String content = isOwner ? getString(R.string.website_share_student_content)
                : getString(R.string.guest_share_content);
        //区分主人态和客人态的状态
        String url = isOwner ? WEB_SITE_DEFAULT_SHARE_URL.replace("{uid}", LoginManager.getInstance().getUid())
                : WEB_SITE_DEFAULT_SHARE_URL.replace("{uid}", mRank.getId());
        bean.setShareTitle(title);
        bean.setShareContent(content);
        bean.setShareUrl(url);

        bean.setShareImageUrl(shareIcon);
        return bean;
    }
}
