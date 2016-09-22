package com.idrv.coach.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.idrv.coach.R;
import com.idrv.coach.bean.Coach;
import com.idrv.coach.bean.User;
import com.idrv.coach.bean.event.EventConstant;
import com.idrv.coach.data.constants.SPConstant;
import com.idrv.coach.data.manager.RxBusManager;
import com.idrv.coach.data.model.UserInfoModel;
import com.idrv.coach.ui.view.MasterItemView;
import com.idrv.coach.utils.Logger;
import com.idrv.coach.utils.PreferenceUtil;
import com.idrv.coach.utils.helper.ViewUtils;
import com.zjb.volley.utils.GsonUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * time: 2016/3/25
 * description:
 * UserInfo的二级界面，仅仅是数据的显示，没有网络请求
 *
 * @author bigflower
 */
public class UserInfoContactActivity extends BaseActivity implements MasterItemView.OnMasterItemClickListener {

    @InjectView(R.id.item_contact_phone)
    MasterItemView mPhoneItemView;
    @InjectView(R.id.item_contact_qrCode)
    MasterItemView mQrCodeItemView;

    public static void launch(Context context) {
        Intent intent = new Intent(context, UserInfoContactActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getProgressBg() {
        return R.color.bg_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_contact_way);
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
        mToolbarLayout.setTitle(R.string.title_contact_way);
    }

    private void initViews() {
        mPhoneItemView.setText(R.string.tel_num);
        mPhoneItemView.setRightTextWithArrowText(R.string.unBind);

        mQrCodeItemView.setText(R.string.item_contact_qrCode);
        mQrCodeItemView.setRightTextWithArrowText(R.string.unUpload);

        mQrCodeItemView.setLineVisible(View.GONE);

        mQrCodeItemView.setOnMasterItemClickListener(this);
    }

    private void initInfo() {
        try {
            // 1.获取数据
            Coach coach = GsonUtil.fromJson(PreferenceUtil.getString(SPConstant.KEY_COACH), Coach.class);
            User user = GsonUtil.fromJson(PreferenceUtil.getString(SPConstant.KEY_USER), User.class);
            // 2.判断是否绑定了手机号
            if (!TextUtils.isEmpty(user.getPhone())) {
                mPhoneItemView.setClickable(false);
                mPhoneItemView.setRightText(user.getPhone());
                mPhoneItemView.setRightArrowVisible(View.GONE);
            } else {
                mPhoneItemView.setOnMasterItemClickListener(this);
            }
            // 图片信息不为空，则显示图标
            if (!TextUtils.isEmpty(coach.getQrCode())) {
                mQrCodeItemView.setRightImageWithoutText("drawable://" + R.drawable.ic_qrcode);
            }
            mQrCodeItemView.setDynamicRedPointStatus(false);
        } catch (Exception e) {
            Logger.e(e.toString());
        }
    }

    @Override
    public void onMasterItemClick(View v) {
        switch (v.getId()) {
            case R.id.item_contact_phone:
                ViewUtils.setDelayedClickable(mPhoneItemView, 1000);
//                BindPhoneActivity.launch(this);
                break;
            case R.id.item_contact_qrCode:
                ViewUtils.setDelayedClickable(mQrCodeItemView, 1000);
                QrCodeActivity.launch(this);
                break;
        }
    }

    private void initRxBus() {
        // 1. 绑定了手机号
        RxBusManager.register(this, EventConstant.KEY_REVISE_PHONE, String.class)
                .subscribe(phone -> {
                    mPhoneItemView.setRightText(phone);
                    mPhoneItemView.setOnMasterItemClickListener(null);
                    mPhoneItemView.setRightArrowVisible(View.GONE);
                }, Logger::e);
        // 2. 当上传了微信二维码，这里显示右侧图片，隐藏文字
        RxBusManager.register(this, EventConstant.KEY_REVISE_QRCODE, String.class)
                .subscribe(__ -> {
                    mQrCodeItemView.setRightImageWithoutText("drawable://" + R.drawable.ic_qrcode);
                }, Logger::e);
    }

}
