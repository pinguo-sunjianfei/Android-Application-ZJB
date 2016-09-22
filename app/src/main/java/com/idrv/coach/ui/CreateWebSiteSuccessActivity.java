package com.idrv.coach.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.idrv.coach.R;
import com.idrv.coach.bean.Coach;
import com.idrv.coach.bean.WebSite;
import com.idrv.coach.bean.event.EventConstant;
import com.idrv.coach.data.manager.LoginManager;
import com.idrv.coach.data.manager.RxBusManager;
import com.idrv.coach.data.model.CreateWebSiteModel;
import com.idrv.coach.ui.view.ImageGridLayout;
import com.idrv.coach.utils.Logger;
import com.idrv.coach.utils.PixelUtil;
import com.idrv.coach.utils.TimeUtil;
import com.idrv.coach.utils.ValidateUtil;
import com.idrv.coach.utils.helper.UIHelper;
import com.idrv.coach.utils.helper.ViewUtils;
import com.zjb.volley.utils.NetworkUtil;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Subscription;

/**
 * time:2016/6/12
 * description:
 *
 * @author sunjianfei
 */
public class CreateWebSiteSuccessActivity extends BaseActivity<CreateWebSiteModel> {
    @InjectView(R.id.avatar)
    ImageView mAvatarIv;
    @InjectView(R.id.nick_name)
    TextView mNickNameTv;
    @InjectView(R.id.teach_age)
    TextView mTeachAgeTv;
    @InjectView(R.id.school)
    TextView mSchoolTv;
    @InjectView(R.id.same_school)
    TextView mSameSchoolTv;
    @InjectView(R.id.grid_view)
    ImageGridLayout mGridLayout;

    @InjectView(R.id.recommend_tv)
    View mRecommendTv;
    @InjectView(R.id.recommend_layout)
    View mRecommendLayout;
    @InjectView(R.id.btn_reviews)
    TextView mBtnReviews;
    @InjectView(R.id.btn_website)
    TextView mBtnWebsite;

    public static void launch(Context context) {
        Intent intent = new Intent(context, CreateWebSiteSuccessActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_create_website_success);
        ButterKnife.inject(this);
        initToolBar();
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
        if (NetworkUtil.isConnected(this)) {
            showProgressView();
            refresh();
        } else {
            UIHelper.shortToast(R.string.network_error);
        }
    }

    private void registerEvent() {
        RxBusManager.register(this, EventConstant.KEY_CLOSE_SUCCESS_PAGE, String.class)
                .subscribe(s -> finish(), Logger::e);
    }

    private void initToolBar() {
        mToolbarLayout.setTitle(R.string.create_website_success);
    }

    private void initViewModel() {
        mViewModel = new CreateWebSiteModel();
        refresh();
    }

    public void refresh() {
        Subscription subscription = mViewModel.getExcellentWebSite()
                .subscribe(this::initView, __ -> showErrorView(), this::onComplete);
        addSubscription(subscription);
    }

    private void initView(List<WebSite> webSites) {
        WebSite webSite = webSites.get(0);
        String teachAgeData = webSite.getCoachingDate();
        Coach coach = LoginManager.getInstance().getCoach();
        String school = webSite.getDrivingSchool();
        String teachAge;

        //头像
        ViewUtils.showCirCleAvatar(mAvatarIv, webSite.getHeadimgurl(), (int) PixelUtil.dp2px(60));
        mNickNameTv.setText(webSite.getNickname());

        // 判断教龄
        if (!TextUtils.isEmpty(teachAgeData) && !"0001-01-01".equals(teachAgeData)) {
            teachAge = getString(R.string.teach_age_s, TimeUtil.getAge(teachAgeData));
        } else {
            teachAge = getString(R.string.teach_age_s, 0);
        }
        mTeachAgeTv.setText(teachAge);
        if (!TextUtils.isEmpty(school)) {
            mSchoolTv.setText(school);
            mSameSchoolTv.setVisibility(school.equals(coach.getDrivingSchool()) ? View.VISIBLE : View.GONE);
        } else {
            mSchoolTv.setVisibility(View.GONE);
            mSameSchoolTv.setVisibility(View.GONE);
        }

        mGridLayout.setData(webSite.getPictures());
    }

    private void onComplete() {
        showContentView();
        if (!ValidateUtil.isValidate(mViewModel.getWebSites())) {
            mRecommendTv.setVisibility(View.GONE);
            mRecommendLayout.setVisibility(View.GONE);
            mBtnReviews.setVisibility(View.GONE);
            mBtnWebsite.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.btn_reviews, R.id.btn_website})
    public void onReviewsClick(View view) {
        if (view.getId() == R.id.btn_reviews) {
            RecommendWebSiteActivity.launch(this, mViewModel.getWebSites());
        } else if (view.getId() == R.id.btn_website) {
            MyWebSiteActivity.launch(this);
            finish();
        }

    }

}
