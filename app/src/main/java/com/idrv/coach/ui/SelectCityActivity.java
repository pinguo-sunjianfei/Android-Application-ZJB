package com.idrv.coach.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.amap.api.location.AMapLocation;
import com.idrv.coach.R;
import com.idrv.coach.bean.Location;
import com.idrv.coach.bean.event.EventConstant;
import com.idrv.coach.data.manager.LocationManager;
import com.idrv.coach.data.manager.RxBusManager;
import com.idrv.coach.data.model.SchoolModel;
import com.idrv.coach.data.model.UserReviseModel;
import com.idrv.coach.ui.adapter.SearchSchoolAdapter;
import com.idrv.coach.ui.adapter.SelectCityAdapter;
import com.idrv.coach.utils.Logger;
import com.idrv.coach.utils.helper.DialogHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * time:2016/5/25
 * description:
 *
 * @author sunjianfei
 */
public class SelectCityActivity extends BaseActivity<SchoolModel> implements SearchSchoolAdapter.OnSchoolSelectListener {
    @InjectView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    SelectCityAdapter mAdapter;

    public static void launch(Context context) {
        Intent intent = new Intent(context, SelectCityActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_city_select);
        ButterKnife.inject(this);
        initToolBar();
        initView();
        initViewModel();
        registerEvent();
    }

    private void initToolBar() {
        mToolbarLayout.setTitle(R.string.select_city);
    }

    private void initView() {
        mAdapter = new SelectCityAdapter();
        mAdapter.setListener(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initViewModel() {
        mViewModel = new SchoolModel();
        getCityList();
    }

    private void registerEvent() {
        //定位
        LocationManager.getInstance().registerLocation(this, Location.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onLocationNext, Logger::e);

        //驾校选择完成之后,关闭页面
        RxBusManager.register(this, EventConstant.KEY_SCHOOL_SAVE_SUCCESS, String.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(__ -> finish(), Logger::e);
    }

    private void getCityList() {
        Dialog dialog = DialogHelper.create(DialogHelper.TYPE_PROGRESS)
                .progressText(getString(R.string.dialog_loading))
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .show();
        Subscription subscription = mViewModel.getCityList()
                .subscribe(mAdapter::addData, __ -> showErrorView(), () -> {
                    dialog.dismiss();
                    mAdapter.notifyDataSetChanged();
                });
        addSubscription(subscription);
    }

    private void onLocationNext(Location location) {
        //如果已经定位到了,就不需要再定位
        RxBusManager.unregister(this, EventConstant.KEY_LOCATION);
        if (mViewModel.hasLocation()) {
            return;
        }
        int errorCode = location.getCode();
        //如果定位成功
        if (errorCode == AMapLocation.LOCATION_SUCCESS) {
            mViewModel.setLocation(location);

        }
        mAdapter.setLocation(location);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSchoolSelect(String school) {
        //do nothing
    }

    @Override
    public void onCustomSchoolSelect() {
        UserReviseActivity.launch(this, UserReviseModel.KEY_SCHOOL, mViewModel.getOldSchool());
    }
}
