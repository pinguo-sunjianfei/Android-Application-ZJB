package com.idrv.coach.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.idrv.coach.R;
import com.idrv.coach.bean.DrivingTestInsDetail;
import com.idrv.coach.data.model.DrivingTestInsModel;
import com.idrv.coach.ui.view.DrivingInsDetailItemView;
import com.idrv.coach.utils.SchemeUtils;
import com.idrv.coach.utils.ValidateUtil;
import com.idrv.coach.utils.helper.UIHelper;
import com.zjb.volley.utils.NetworkUtil;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Subscription;

/**
 * time:2016/8/18
 * description:
 *
 * @author sunjianfei
 */
public class DrivingTestInsDescFragment extends BaseFragment<DrivingTestInsModel> {
    @InjectView(R.id.item_1)
    DrivingInsDetailItemView mItem1;
    @InjectView(R.id.item_2)
    DrivingInsDetailItemView mItem2;


    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_driving_test_ins_details, container, false);
    }

    @Override
    public void initView(View view) {
        ButterKnife.inject(this, view);
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
        mViewModel = new DrivingTestInsModel();
        refresh();
    }

    public void refresh() {
        Subscription subscription = mViewModel.getInsDetail()
                .subscribe(this::onNext, this::onError);
        addSubscription(subscription);
    }

    private void onNext(List<DrivingTestInsDetail> details) {
        if (ValidateUtil.isValidate(details)) {
            int size = details.size();
            if (size < 2) {
                mItem2.setVisibility(View.INVISIBLE);
            } else {
                DrivingInsDetailItemView[] itemViews = {mItem1, mItem2};
                for (int i = 0; i < 2; i++) {
                    DrivingTestInsDetail detail = details.get(i);
                    itemViews[i].setText(detail.getTitle());
                    itemViews[i].setImage(detail.getImage());
                    //设置点击事件
                    itemViews[i].setOnClickListener(v -> SchemeUtils.schemeJump(getContext(), detail.getSchema()));
                }
            }
            showContentView();
        } else {
            showErrorView();
        }
    }

    private void onError(Throwable e) {
        showErrorView();
    }

    @OnClick(R.id.back)
    public void onBackClick(View v) {
        getActivity().finish();
    }
}
