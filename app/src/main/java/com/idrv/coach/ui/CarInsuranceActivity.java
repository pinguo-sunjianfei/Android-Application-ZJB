package com.idrv.coach.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.idrv.coach.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * time:2016/3/17
 * description:车险入口
 *
 * @author sunjianfei
 */
public class CarInsuranceActivity extends BaseActivity implements View.OnClickListener {


    public static void launch(Context context) {
        Intent intent = new Intent(context, CarInsuranceActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_car_insurance);
        ButterKnife.inject(this);
        initToolBar();
    }

    private void initToolBar() {
        mToolbarLayout.setTitleTxt(getString(R.string.car_insurance));
    }

    @OnClick({R.id.btn_has_inquiry, R.id.btn_inquiry})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_inquiry:
                ApplyInsuranceActivity.launch(v.getContext());
                break;
            case R.id.btn_has_inquiry:
                InsuranceListActivity.launch(v.getContext());
                break;
        }
    }
}
