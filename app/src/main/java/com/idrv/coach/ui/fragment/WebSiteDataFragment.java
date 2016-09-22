package com.idrv.coach.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.idrv.coach.R;
import com.idrv.coach.bean.ESite;
import com.idrv.coach.bean.event.EventConstant;
import com.idrv.coach.data.manager.RxBusManager;
import com.idrv.coach.utils.Logger;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.android.schedulers.AndroidSchedulers;

/**
 * time:2016/4/20
 * description:
 *
 * @author sunjianfei
 */
public class WebSiteDataFragment extends BaseFragment {
    @InjectView(R.id.today_vis)
    TextView mTodayVisTv;
    @InjectView(R.id.today_forwarding)
    TextView mTodayForWardingTv;
    @InjectView(R.id.total_vis)
    TextView mTotalVisTv;
    @InjectView(R.id.total_forwarding)
    TextView mTotalForwardingTv;

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_personal_website_data, container, false);
    }

    @Override
    protected boolean hasBaseLayout() {
        return false;
    }

    @Override
    public void initView(View view) {
        ButterKnife.inject(this, view);
        view.getBackground().setAlpha(0);
        registerEvent();
    }

    private void registerEvent() {
        RxBusManager.register(this, EventConstant.KEY_WEBSITE_DATA_LOAD_SUCCESS, ESite.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onNext, Logger::e);
    }

    private void onNext(ESite site) {
        mTodayVisTv.setText(site.getTodayVisits() + "");
        mTodayForWardingTv.setText(site.getTodayShares() + "");
        mTotalVisTv.setText(site.getTotalVisits() + "");
        mTotalForwardingTv.setText(site.getTotalShares() + "");
    }
}
