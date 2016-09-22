package com.idrv.coach.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.idrv.coach.R;
import com.idrv.coach.bean.Coach;
import com.idrv.coach.bean.event.EventConstant;
import com.idrv.coach.data.constants.SPConstant;
import com.idrv.coach.data.manager.RxBusManager;
import com.idrv.coach.data.model.UserInfoModel;
import com.idrv.coach.data.model.UserReviseModel;
import com.idrv.coach.ui.view.MasterItemView;
import com.idrv.coach.utils.Logger;
import com.idrv.coach.utils.PreferenceUtil;
import com.zjb.volley.utils.GsonUtil;

import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * time: 2016/3/25
 * description:
 * UserInfo的二级界面，仅仅是数据的显示，没有网络请求
 *
 * @author bigflower
 */
public class UserInfoAddressActivity extends BaseActivity implements MasterItemView.OnMasterItemClickListener{

    @InjectView(R.id.item_address_driveSchool)
    MasterItemView mDriveSchoolItemView;
    @InjectView(R.id.item_address_testSite)
    MasterItemView mTestSiteItemView;
    @InjectView(R.id.item_address_trainSite)
    MasterItemView mTrainSiteItemView;


    public static void launch(Context context) {
        Intent intent = new Intent(context, UserInfoAddressActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected int getProgressBg() {
        return R.color.bg_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_address);
        ButterKnife.inject(this);

        //1.初始化标题栏
        initToolBar();
        //2.初始化ViewModel
        mViewModel = new UserInfoModel();
        // 3. 加载布局，并根据状态赋值
        initViews();
        initInfo();
        // Rxbus
        initRxBus();
    }

    private void initToolBar() {
        mToolbarLayout.setTitle(R.string.title_address);
    }

    private void initViews() {
        mDriveSchoolItemView.setText(R.string.item_address_driveSchool);
        mDriveSchoolItemView.setRightTextWithArrowText(R.string.unFilled);

        mTrainSiteItemView.setText(R.string.item_address_trainSite);
        mTrainSiteItemView.setRightTextWithArrowText(R.string.unFilled);

        mTestSiteItemView.setText(R.string.item_address_testSite);
        mTestSiteItemView.setRightTextWithArrowText(R.string.unFilled);

        mTestSiteItemView.setLineVisible(View.GONE);

        mDriveSchoolItemView.setOnMasterItemClickListener(this);
        mTrainSiteItemView.setOnMasterItemClickListener(this);
        mTestSiteItemView.setOnMasterItemClickListener(this);
    }

    private void initInfo() {
        try {
            Coach coach = GsonUtil.fromJson(PreferenceUtil.getString(SPConstant.KEY_COACH), Coach.class);
            if (!TextUtils.isEmpty(coach.getDrivingSchool()))
                mDriveSchoolItemView.setRightText(coach.getDrivingSchool());
            if (!TextUtils.isEmpty(coach.getTestSite()))
                mTestSiteItemView.setRightText(coach.getTestSite());
            if (!TextUtils.isEmpty(coach.getTrainingSite()))
                mTrainSiteItemView.setRightText(coach.getTrainingSite());
        } catch (Exception e) {
            Logger.e(e.toString());
        }

    }
    @Override
    public void onMasterItemClick(View v) {
        if (notClickable())
            return;
        switch (v.getId()) {
            case R.id.item_address_driveSchool:
                SelectCityActivity.launch(this);
                break;
            case R.id.item_address_testSite:
                UserReviseActivity.launch(this,
                        UserReviseModel.KEY_TEST,
                        mTestSiteItemView.getRightText());
                break;
            case R.id.item_address_trainSite:
                UserReviseActivity.launch(this,
                        UserReviseModel.KEY_TRAIN,
                        mTrainSiteItemView.getRightText());
                break;

        }
    }

    private void initRxBus() {
// 1. 当修改了驾校，更新这里的显示
        RxBusManager.register(this, EventConstant.KEY_REVISE_DRISCHOOL, String.class)
                .subscribe(driveSchool -> mDriveSchoolItemView.setRightText(driveSchool), Logger::e);
// 2. 当修改了考场，更新这里的显示
        RxBusManager.register(this, EventConstant.KEY_REVISE_TESTSITE, String.class)
                .subscribe(testSite -> mTestSiteItemView.setRightText(testSite), Logger::e);
// 3. 当修改了练车场，更新这里的显示
        RxBusManager.register(this, EventConstant.KEY_REVISE_TRAINSITE, String.class)
                .subscribe(trainSite -> mTrainSiteItemView.setRightText(trainSite), Logger::e);
    }


    // 工具

    /**
     * 避免重复点击，也可以避免点击两个
     */
    long lastClickTime = 0;
    long currentTime = 0;

    private boolean notClickable() {
        currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > 1000) {
            lastClickTime = currentTime;
            return false;
        } else {
            return true;
        }
    }
}
