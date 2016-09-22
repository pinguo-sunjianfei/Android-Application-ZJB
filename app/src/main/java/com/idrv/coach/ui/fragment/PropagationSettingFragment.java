package com.idrv.coach.ui.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.idrv.coach.R;
import com.idrv.coach.bean.event.EventConstant;
import com.idrv.coach.data.manager.LoginManager;
import com.idrv.coach.data.manager.RxBusManager;
import com.idrv.coach.data.model.TransmissionModel;
import com.idrv.coach.ui.CreateWebSiteActivity;
import com.idrv.coach.ui.NewsHallActivity;
import com.idrv.coach.ui.PhotoActivity;
import com.idrv.coach.ui.PhotoWallActivity;
import com.idrv.coach.ui.RecordActivity;
import com.idrv.coach.utils.Logger;
import com.idrv.coach.utils.StatisticsUtil;
import com.idrv.coach.utils.helper.DialogHelper;
import com.idrv.coach.utils.helper.UIHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Subscription;

/**
 * time:2016/6/7
 * description:
 *
 * @author sunjianfei
 */
public class PropagationSettingFragment extends BaseFragment<TransmissionModel> {
    public static final int OPTION_ONE = 0x000;
    public static final int OPTION_TWO = 0x001;
    public static final int OPTION_THREE = 0x002;

    private static final String KEY_PARAM_TYPE = "param_type";
    private static final String KEY_PARAM_ENABLE = "param_enable";

    private static final int ACTION_CREATE = 0x000;
    private static final int ACTION_ALBUM = 0x001;
    private static final int ACTION_RECORD = 0x002;

    @InjectView(R.id.title)
    ImageView mTitleIv;
    @InjectView(R.id.desc)
    TextView mDescTv;
    @InjectView(R.id.enable_iv)
    ImageView mEnableIv;
    @InjectView(R.id.content_iv)
    ImageView mContentIv;

    boolean isEnable;
    int currentType;

    Dialog mProgressDialog = null;


    public static PropagationSettingFragment newInstance(int type, boolean isEnable) {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_PARAM_TYPE, type);
        bundle.putBoolean(KEY_PARAM_ENABLE, isEnable);
        PropagationSettingFragment fragment = new PropagationSettingFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_propagation_scheme_settings, container, false);
    }

    @Override
    public void initView(View view) {
        ButterKnife.inject(this, view);
        Bundle bundle = getArguments();
        currentType = bundle.getInt(KEY_PARAM_TYPE);
        isEnable = bundle.getBoolean(KEY_PARAM_ENABLE);
        mViewModel = new TransmissionModel();
        setUp(currentType, isEnable);
        registerEvent();
    }

    private void registerEvent() {
        RxBusManager.register(this, EventConstant.KEY_SET_TRANSMISSION_SUCCESS, Integer.class)
                .subscribe(this::onSetSuccess, Logger::e);
    }

    private void setUp(int type, boolean isEnable) {
        int titleRes;
        int descRes;
        int contentRes;
        switch (type) {
            case OPTION_ONE:
                titleRes = R.drawable.option_1_title;
                contentRes = R.drawable.option_1_small;
                descRes = R.string.option_1;
                break;
            case OPTION_TWO:
                titleRes = R.drawable.option_2_title;
                contentRes = R.drawable.option_2_small;
                descRes = R.string.option_2;
                break;
            case OPTION_THREE:
                titleRes = R.drawable.option_3_title;
                contentRes = R.drawable.option_3_small;
                descRes = R.string.option_3;
                break;
            default:
                titleRes = R.drawable.option_1_title;
                contentRes = R.drawable.option_3_small;
                descRes = R.string.option_1;
                break;
        }
        mContentIv.setImageResource(contentRes);
        mTitleIv.setImageResource(titleRes);
        mDescTv.setText(descRes);
        mEnableIv.setVisibility(isEnable ? View.VISIBLE : View.GONE);
    }

    @OnClick({R.id.btn_commit})
    public void onCommit(View view) {
        if (isEnable) {
            UIHelper.shortToast(R.string.pro_setting_error);
        } else {
            //如果是方案3,才做此判断
            if (currentType == OPTION_THREE) {
                //点击方案3的统计
                StatisticsUtil.onEvent(R.string.spread3);
                boolean isEnableWebSite = mViewModel.isOpenWebSite();
                int picNum = mViewModel.getNativePicNum();
                //如果已经开通了个人网站
                if (isEnableWebSite) {
                    //如果照片数量大于6张
                    if (picNum >= 6) {
                        showProgressDialog();
                        setTransmissionModel();
                    } else {
                        //弹出对话框,提示去上传照片
                        showJumpDialog(R.string.pro_upload_tips, R.string.pro_upload_now, ACTION_ALBUM);
                    }
                } else {
                    showJumpDialog(R.string.create_website_tips, R.string.pro_create_now, ACTION_CREATE);
                }
            } else if (currentType == OPTION_TWO) {
                //点击方案2的统计
                StatisticsUtil.onEvent(R.string.spread2);
                //如果是方案2
                if (!mViewModel.isRecord()) {
                    showJumpDialog(R.string.create_website_record, R.string.record_now, ACTION_RECORD);
                } else {
                    showProgressDialog();
                    setTransmissionModel();
                }
            } else {
                //默认方案一
                showProgressDialog();
                setTransmissionModel();
            }
        }
    }

    @OnClick(R.id.content_iv)
    public void onViewLarger(View view) {
        int resId;
        switch (currentType) {
            case OPTION_ONE:
                resId = R.drawable.option_1_big;
                break;
            case OPTION_TWO:
                resId = R.drawable.option_2_big;
                break;
            case OPTION_THREE:
                resId = R.drawable.option_3_big;
                break;
            default:
                resId = R.drawable.option_1_big;
                break;
        }
        PhotoActivity.launch(getActivity(), "drawable://" + resId);
    }

    private void setTransmissionModel() {
        Subscription subscription = mViewModel.setTransmissionModel(currentType)
                .subscribe(this::onNext, this::onError);
        addSubscription(subscription);
    }

    private void onNext(String s) {
        isEnable = true;
        RxBusManager.post(EventConstant.KEY_SET_TRANSMISSION_SUCCESS, currentType);
        if (null != mProgressDialog && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        showDialog();
    }

    private void onSetSuccess(int type) {
        isEnable = type == currentType;
        mEnableIv.setVisibility(isEnable ? View.VISIBLE : View.GONE);
    }

    private void onError(Throwable e) {
        if (null != mProgressDialog && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        UIHelper.shortToast(R.string.setting_error_tips);
    }

    /**
     * 提交信息的弹窗
     */
    private void showProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            return;
        }

        mProgressDialog = DialogHelper.create(DialogHelper.TYPE_PROGRESS)
                .cancelable(true)
                .canceledOnTouchOutside(false)
                .progressText(getResources().getString(R.string.setting_now_pls_wait))
                .show();
    }

    /**
     * 设置成功的弹窗
     */
    private void showDialog() {
        DialogHelper.create(DialogHelper.TYPE_NORMAL)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .title(getString(R.string.tip))
                .content(getString(R.string.propagation_enable_tips))
                .bottomButton(getString(R.string.pro_share_tips), getResources().getColor(R.color.themes_main))
                .bottomBtnClickListener(((dialog, view) -> {
                    dialog.dismiss();
                    NewsHallActivity.launch(getContext());
                })).show();
    }

    private void showJumpDialog(int contentRes, int buttonRes, int action) {
        DialogHelper.create(DialogHelper.TYPE_NORMAL)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .title(getString(R.string.tip))
                .content(getString(contentRes))
                .bottomButton(getString(buttonRes), getResources().getColor(R.color.themes_main))
                .bottomBtnClickListener((dialog, view) -> {
                    dialog.dismiss();
                    if (action == ACTION_CREATE) {
                        CreateWebSiteActivity.launch(getContext());
                    } else if (action == ACTION_ALBUM) {
                        PhotoWallActivity.launch(getContext());
                    } else if (action == ACTION_RECORD) {
                        RecordActivity.launch(getContext(), LoginManager.getInstance().getCoach().getTeachingDeclaration());
                    }
                }).show();
    }
}
