package com.idrv.coach.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.idrv.coach.R;
import com.idrv.coach.data.constants.SPConstant;
import com.idrv.coach.data.model.TransmissionModel;
import com.idrv.coach.ui.adapter.ViewPagerAdapter;
import com.idrv.coach.ui.fragment.PropagationSettingFragment;
import com.idrv.coach.ui.widget.CircleIndicator;
import com.idrv.coach.utils.Logger;
import com.idrv.coach.utils.PreferenceUtil;
import com.idrv.coach.utils.helper.DialogHelper;
import com.idrv.coach.utils.helper.UIHelper;
import com.zjb.volley.utils.NetworkUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscription;

/**
 * time:2016/6/7
 * description:传播方案设置
 *
 * @author sunjianfei
 */
public class PropagationSettingActivity extends BaseActivity<TransmissionModel> {
    private static final String PARAM = "param";
    private static final String KEY_PRO_FIRST_USE = "pro_first_use";

    @InjectView(R.id.viewpager)
    ViewPager mViewPager;
    @InjectView(R.id.indicator)
    CircleIndicator mCircleIndicator;
    @InjectView(R.id.guide_image)
    ImageView mGuideIv;

    public static void launch(Context context, boolean receive) {
        Intent intent = new Intent(context, PropagationSettingActivity.class);
        intent.putExtra(PARAM, receive);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_pro_setting);
        ButterKnife.inject(this);
        initToolBar();
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

    private void initToolBar() {
        mToolbarLayout.setTitle(R.string.plan_setting);
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

    private void initViewModel() {
        mViewModel = new TransmissionModel();
        getPicNum();
        refresh();
    }

    private void getPicNum() {
        int picNum = mViewModel.getNativePicNum();
        if (picNum == -1) {
            Subscription subscription = mViewModel.getPicNum()
                    .subscribe(__ -> {
                    }, Logger::e);
            addSubscription(subscription);
        }
    }

    private void refresh() {
        Subscription subscription = mViewModel.getTransmissionModel()
                .subscribe(this::initView, __ -> showErrorView(), this::showContentView);
        addSubscription(subscription);

    }

    private void initView(String model) {
        int type = Integer.parseInt(model);

        PreferenceUtil.putInt(SPConstant.KEY_PRO_TYPE, type);
        Fragment fragment1 = PropagationSettingFragment.newInstance(PropagationSettingFragment.OPTION_ONE,
                type == PropagationSettingFragment.OPTION_ONE);
        Fragment fragment2 = PropagationSettingFragment.newInstance(PropagationSettingFragment.OPTION_TWO,
                type == PropagationSettingFragment.OPTION_TWO);
        Fragment fragment3 = PropagationSettingFragment.newInstance(PropagationSettingFragment.OPTION_THREE,
                type == PropagationSettingFragment.OPTION_THREE);

        List<Fragment> fragments = new ArrayList<>(3);
        fragments.add(fragment1);
        fragments.add(fragment2);
        fragments.add(fragment3);

        ViewPagerAdapter mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mAdapter.setData(fragments);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(3);
        mCircleIndicator.setViewPager(mViewPager);

        boolean isReceive = getIntent().getBooleanExtra(PARAM, false);
        if (isReceive) {
            DialogHelper.create(DialogHelper.TYPE_NORMAL)
                    .cancelable(true)
                    .canceledOnTouchOutside(true)
                    .title(getString(R.string.receive_success_title))
                    .content(getString(R.string.receive_success_subject))
                    .bottomButton(getString(R.string.Iknowit), getResources().getColor(R.color.themes_main))
                    .bottomBtnClickListener((dialog, view) -> dialog.dismiss())
                    .show();
        }

        boolean isShowed = PreferenceUtil.getBoolean(KEY_PRO_FIRST_USE, false);
        mGuideIv.setVisibility(isShowed ? View.GONE : View.VISIBLE);

        mViewPager.setOnTouchListener((v, event) -> {
            mGuideIv.setVisibility(View.GONE);
            PreferenceUtil.putBoolean(KEY_PRO_FIRST_USE, true);
            return false;
        });
    }
}
