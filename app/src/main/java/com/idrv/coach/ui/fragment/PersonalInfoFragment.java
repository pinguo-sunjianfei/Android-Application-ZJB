package com.idrv.coach.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.idrv.coach.R;
import com.idrv.coach.bean.Coach;
import com.idrv.coach.bean.User;
import com.idrv.coach.bean.event.EventConstant;
import com.idrv.coach.bean.share.ShareWebProvider;
import com.idrv.coach.data.constants.SPConstant;
import com.idrv.coach.data.constants.ShareConstant;
import com.idrv.coach.data.manager.LoginManager;
import com.idrv.coach.data.manager.RxBusManager;
import com.idrv.coach.data.model.CoachInfoModel;
import com.idrv.coach.ui.CoachAuthenticationActivity;
import com.idrv.coach.ui.CoachAuthenticationCommitActivity;
import com.idrv.coach.ui.CoachAuthenticationSuccessActivity;
import com.idrv.coach.ui.CommentActivity;
import com.idrv.coach.ui.MyWalletActivity;
import com.idrv.coach.ui.PhotoWallActivity;
import com.idrv.coach.ui.PropagationSettingActivity;
import com.idrv.coach.ui.SettingsActivity;
import com.idrv.coach.ui.UserInfoActivity;
import com.idrv.coach.ui.view.MasterItemView;
import com.idrv.coach.utils.Logger;
import com.idrv.coach.utils.PixelUtil;
import com.idrv.coach.utils.PreferenceUtil;
import com.idrv.coach.utils.StatisticsUtil;
import com.idrv.coach.utils.helper.UIHelper;
import com.idrv.coach.utils.helper.ViewUtils;
import com.idrv.coach.wxapi.WXEntryActivity;
import com.zjb.volley.utils.NetworkUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * time:2016/3/11
 * description:
 *
 * @author sunjianfei
 */
public class PersonalInfoFragment extends BaseFragment<CoachInfoModel> implements View.OnClickListener {
    @InjectView(R.id.item_user_info)
    MasterItemView mUserInfoItemView;
    @InjectView(R.id.item_my_wallet)
    MasterItemView mMyWalletItemView;
    @InjectView(R.id.item_plan_setting)
    MasterItemView mPlanSettingItem;
    @InjectView(R.id.item_settings)
    MasterItemView mSettingsItem;
    @InjectView(R.id.item_share)
    MasterItemView mShareItem;

    @InjectView(R.id.avatar)
    ImageView mAvatarIv;
    @InjectView(R.id.personal_name_tv)
    TextView mPersonalNameTv;
    @InjectView(R.id.personal_tel_tv)
    TextView mPersonalTelTv;
    @InjectView(R.id.personal_school_tv)
    TextView mPersonalSchoolTv;
    @InjectView(R.id.pic_num_tv)
    TextView mPicNumTv;
    @InjectView(R.id.comment_num_tv)
    TextView mCommentNumTv;
    @InjectView(R.id.coach_auth_status_iv)
    ImageView mCoachAuthStatusIv;
    @InjectView(R.id.member_iv)
    ImageView mMemberIv;


    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_mine, container, false);
    }

    @Override
    public void initView(View view) {
        ButterKnife.inject(this, view);
        mUserInfoItemView.setLeftDrawableRes(R.drawable.icon_user_info);
        mUserInfoItemView.setText(R.string.item_profile);

        mMyWalletItemView.setLeftDrawableRes(R.drawable.icon_wallet);
        mMyWalletItemView.setText(R.string.my_wallet);
        mMyWalletItemView.setRightTextWithArrowText(R.string.item_wallet);

        mPlanSettingItem.setLeftDrawableRes(R.drawable.icon_pro_tool);
        mPlanSettingItem.setRightTextWithArrowText(getProSetting());
        mPlanSettingItem.setText(R.string.plan_setting);

        mSettingsItem.setLeftDrawableRes(R.drawable.icon_settings);
        mSettingsItem.setText(R.string.settings);
        mShareItem.setLeftDrawableRes(R.drawable.icon_profile_share);
        mShareItem.setText(R.string.share_to_other);

        mPlanSettingItem.setLineVisible(View.GONE);
        mShareItem.setLineVisible(View.GONE);

        initViewModel();
        //注册事件
        registerEvent();
    }

    @OnClick({R.id.item_user_info, R.id.item_my_wallet,
            R.id.item_plan_setting, R.id.item_settings,
            R.id.item_photo, R.id.item_comment,
            R.id.coach_auth_status_iv, R.id.item_share})
    @Override
    public void onClick(View v) {
        ViewUtils.setDelayedClickable(v, 500);
        if (v.getId() == R.id.item_my_wallet) {
            //统计点击我的钱包
            StatisticsUtil.onEvent(R.string.visit_wallet);
            MyWalletActivity.launch(v.getContext());
        } else if (v.getId() == R.id.item_user_info) {
            UserInfoActivity.launch(v.getContext());
        } else if (v.getId() == R.id.item_plan_setting) {
            //点击传播方案设置的统计
            StatisticsUtil.onEvent(R.string.spread_setting);
            PropagationSettingActivity.launch(getContext(), false);
        } else if (v.getId() == R.id.item_settings) {
            SettingsActivity.launch(v.getContext());
        } else if (v.getId() == R.id.item_photo) {
            PhotoWallActivity.launch(v.getContext());
        } else if (v.getId() == R.id.item_comment) {
            CommentActivity.launch(getContext());
        } else if (v.getId() == R.id.coach_auth_status_iv) {
            Coach coach = LoginManager.getInstance().getCoach();
            int authStatus = coach.getAuthenticationState();
            if (authStatus == Coach.STATE_AUTH_FAILED || authStatus == Coach.STATE_DEFAULT) {
                //如果是审核失败或者没有提交审核
                CoachAuthenticationActivity.launch(getContext());
            } else if (authStatus == Coach.STATE_AUTH_SUCCESS) {
                //如果是审核成功
                CoachAuthenticationSuccessActivity.launch(getContext());
            } else if (authStatus == Coach.STATE_APPLY) {
                //如果正在审核中
                CoachAuthenticationCommitActivity.launch(getContext());
            }
        } else if (v.getId() == R.id.item_share) {
            share();
        }
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
        if (NetworkUtil.isConnected(getActivity())) {
            showProgressView();
            refresh();
        } else {
            UIHelper.shortToast(R.string.network_error);
        }
    }

    private void initViewModel() {
        mViewModel = new CoachInfoModel();
        requestData();
    }

    private void requestData() {
        getUserData();
    }

    private void registerEvent() {
        //1.修改个人资料
        RxBusManager.register(this, EventConstant.KEY_REVISE_USERINFO, String.class)
                .subscribe(__ -> onNext(mViewModel.getCacheData()));
        //2.照片上传成功,刷新个人主页照片
        RxBusManager.register(this, EventConstant.KEY_REFRESH_PIC, String.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(__ -> {
                    setPhotoAndCommentNum();
                }, Logger::e);
        //3.照片删除成功,刷新个人主页照片
        RxBusManager.register(this, EventConstant.KEY_PHOTO_DELETE_SUCCESS, String.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(__ -> {
                    setPhotoAndCommentNum();
                }, Logger::e);
        //4.评论删除成功,刷新个人主页数据
        RxBusManager.register(this, EventConstant.KEY_COMMENT_DELETE_SUCCESS, String.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(__ -> {
                    setPhotoAndCommentNum();
                }, Logger::e);
    }

    /**
     * 拉取个人资料数据
     */
    private void getUserData() {
        Coach coach = LoginManager.getInstance().getCoach();
        //1.拉取照片评论数量
        getPicAndCommentNum();
        //2.如果本地有缓存,则不需要从服务器拉取个人资料
        if (null != coach) {
            //每次都去拉取教练认证状态
            getAuthenticationState();
            onNext(coach);
        } else {
            refresh();
        }
    }

    private void getPicAndCommentNum() {
        Subscription subscription = mViewModel.getPicNum()
                .subscribe(__ -> setPhotoAndCommentNum(), Logger::e);
        addSubscription(subscription);
    }

    /**
     * 拉取教练资料
     */
    private void refresh() {
        //获取教练信息
        Subscription subscription = mViewModel.getCoachInfo()
                .subscribe(this::onNext, this::onError);
        addSubscription(subscription);
    }

    private void getAuthenticationState() {
        Subscription subscription = mViewModel.getAuthenticationState()
                .subscribe(status -> setCoachAuthStatus(status.getAuthenticationState()), Logger::e);
        addSubscription(subscription);
    }

    private void onNext(Coach coach) {
        User user = mViewModel.getUser();
        ViewUtils.showRingAvatar(mAvatarIv, user.getHeadimgurl(), (int) PixelUtil.dp2px(70), PixelUtil.dp2px(1), 0f, 0xFFFFFFFF);
        mPersonalNameTv.setText(user.getNickname());
        mPersonalTelTv.setText(user.getPhone());
        mPersonalSchoolTv.setText(coach.getDrivingSchool());

        //设置认证状态
        setCoachAuthStatus(coach.getAuthenticationState());

        //设置资料完善情况
        if (!mViewModel.isProfileValid()) {
            mUserInfoItemView.setRightTextWithArrowText(R.string.unComplete);
        } else {
            mUserInfoItemView.setRightTextWithArrowText(R.string.complete);
        }

        //设置会员状态
        mMemberIv.setVisibility(user.getMember() == 0 ? View.GONE : View.VISIBLE);

        //设置照片、评论数量
        setPhotoAndCommentNum();
        showContentView();
    }

    private void onError(Throwable e) {
        showErrorView();
    }

    private void setCoachAuthStatus(int status) {
        int resId = status != Coach.STATE_AUTH_SUCCESS ? R.drawable.coach_not_auth : R.drawable.coach_has_auth;
        mCoachAuthStatusIv.setImageResource(resId);
    }

    /**
     * 设置照片和评论数量
     */
    private void setPhotoAndCommentNum() {
        int photoNum = PreferenceUtil.getInt(SPConstant.KEY_PIC_NUMBER);
        int commentNum = PreferenceUtil.getInt(SPConstant.KEY_COMMENT_NUM);

        mPicNumTv.setText(getString(R.string.unit_photo, photoNum));
        mCommentNumTv.setText(getString(R.string.unit_comment, commentNum));
    }

    /**
     * 分享
     */
    public void share() {
        ShareWebProvider provider = new ShareWebProvider();
        String targetUrl = "{HOST}/office";

        provider.setDesc(getString(R.string.profile_share_subject));
        provider.setTitle(getString(R.string.profile_share_title));
        provider.setImagePath(ShareConstant.SHARE_DEFAULT_IMAGE);
        provider.setUrl(targetUrl);

        WXEntryActivity.launch(getActivity(), provider, R.string.share_str);
    }

    private String getProSetting() {
        int type = PreferenceUtil.getInt(SPConstant.KEY_PRO_TYPE, -1);
        String str = "";
        switch (type) {
            case PropagationSettingFragment.OPTION_ONE:
                str = getString(R.string.pro_setting_1);
                break;
            case PropagationSettingFragment.OPTION_TWO:
                str = getString(R.string.pro_setting_2);
                break;
            case PropagationSettingFragment.OPTION_THREE:
                str = getString(R.string.pro_setting_3);
                break;
        }
        return str;
    }
}
