package com.idrv.coach.ui.fragment;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.idrv.coach.R;
import com.idrv.coach.bean.DrivingTestIns;
import com.idrv.coach.bean.event.EventConstant;
import com.idrv.coach.data.manager.RxBusManager;
import com.idrv.coach.data.model.DrivingTestInsModel;
import com.idrv.coach.ui.BindPhoneActivity;
import com.idrv.coach.ui.view.InputItemView;
import com.idrv.coach.utils.SystemUtil;
import com.idrv.coach.utils.helper.DialogHelper;
import com.idrv.coach.utils.helper.UIHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Subscription;

/**
 * time:2016/8/18
 * description:投保
 *
 * @author sunjianfei
 */
public class ApplyInsForDrivingTestFragment extends BaseFragment<DrivingTestInsModel> {
    @InjectView(R.id.item_owner_name)
    InputItemView mItemNameView;
    @InjectView(R.id.item_tel)
    InputItemView mItemTelView;
    @InjectView(R.id.item_card)
    InputItemView mItemCardView;

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.act_apply_driving_test_ins, container, false);
    }

    @Override
    public void initView(View view) {
        ButterKnife.inject(this, view);
        mItemNameView.setInputType(InputType.TYPE_CLASS_TEXT);
        mItemTelView.setInputType(InputType.TYPE_CLASS_PHONE);
        mItemCardView.setInputType(InputType.TYPE_CLASS_TEXT);

        mItemNameView.setText(R.string.name);
        mItemTelView.setText(R.string.tel_num);
        mItemCardView.setText(R.string.item_owner_card);

        mItemNameView.setTextHint(R.string.pls_input_insured_name);
        mItemTelView.setTextHint(R.string.pls_input_insured_tel);
        mItemCardView.setTextHint(R.string.pls_input_insured_card_id);

        initViewModel();
    }

    private void initViewModel() {
        mViewModel = new DrivingTestInsModel();
    }

    @OnClick({R.id.next_step,R.id.back})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next_step:
                if (checkInputValid()) {
                    commit();
                }
                break;
            case R.id.back:
                getActivity().finish();
                break;
        }
    }

    private void commit() {
        showDialog();
        Subscription subscription = mViewModel.commit()
                .subscribe(this::onCommitNext, this::onCommitError);
        addSubscription(subscription);
    }

    private void onCommitNext(String s) {
        dismissProgressDialog();
        DialogHelper.create(DialogHelper.TYPE_NORMAL)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .title(getString(R.string.apply_success))
                .content(getString(R.string.apply_success_content))
                .bottomButton(getString(R.string.to_pay), getResources().getColor(R.color.themes_main))
                .bottomBtnClickListener((dialog, v) -> {
                    dialog.dismiss();
                    RxBusManager.post(EventConstant.KEY_DRIVING_INS_COMMIT_SUCCESS, "");
                    clear();
                })
                .show();
    }

    private void onCommitError(Throwable e) {
        dismissProgressDialog();
        DialogHelper.create(DialogHelper.TYPE_NORMAL)
                .cancelable(true)
                .canceledOnTouchOutside(false)
                .title(getString(R.string.status_error_tip))
                .content(getString(R.string.post_info_error))
                .leftButton(getString(R.string.cancel), ContextCompat.getColor(getContext(), R.color.black_54))
                .rightButton(getString(R.string.sure), ContextCompat.getColor(getContext(), R.color.themes_main))
                .leftBtnClickListener((dialog, v) -> dialog.dismiss())
                .rightBtnClickListener((dialog, v) -> {
                    commit();
                    dialog.dismiss();
                })
                .show();
    }

    final protected void showDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            return;
        }
        mProgressDialog = DialogHelper.create(DialogHelper.TYPE_PROGRESS)
                .cancelable(true)
                .canceledOnTouchOutside(false)
                .progressText(getResources().getString(R.string.commit_now))
                .onDismissListener(__ -> {
                    if (mSubscription != null) {
                        mSubscription.unsubscribe();
                    }
                }).show();
    }

    private boolean checkInputValid() {
        String name = mItemNameView.getInputText();
        String tel = mItemTelView.getInputText();
        String card_id = mItemCardView.getInputText();

        if (mViewModel.checkUserInfoInValid()) {
            //1.先检查教练的资料是否填写
            DialogHelper.create(DialogHelper.TYPE_NORMAL)
                    .cancelable(true)
                    .canceledOnTouchOutside(false)
                    .title(getString(R.string.tip))
                    .content(getString(R.string.pls_improve_personal_information))
                    .bottomButton(getString(R.string.improve_personal_information_now), R.color.themes_main)
                    .bottomBtnClickListener((dialog, v) -> {
                        dialog.dismiss();
                        BindPhoneActivity.launch(getContext());
                    })
                    .show();
            return false;
        } else if (TextUtils.isEmpty(name)) {
            UIHelper.shortToast(R.string.insured_name_not_be_null);
            return false;
        } else if (!SystemUtil.checkPhoneNumber(tel)) {
            UIHelper.shortToast(R.string.pls_input_valid_tel_num);
            return false;
        } else if (TextUtils.isEmpty(card_id) || card_id.length() != 18) {
            UIHelper.shortToast(R.string.pls_input_valid_card_id);
            return false;
        }
        DrivingTestIns ins = mViewModel.getData();
        ins.setName(name);
        ins.setCoachPhone(tel);
        ins.setIdCard(card_id);
        return true;
    }

    /**
     * 清除所有的输入框
     */
    private void clear() {
        mItemNameView.clear();
        mItemCardView.clear();
        mItemTelView.clear();
    }
}
