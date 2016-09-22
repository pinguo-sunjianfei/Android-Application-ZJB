package com.idrv.coach.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;

import com.idrv.coach.R;
import com.idrv.coach.bean.DrivingTestIns;
import com.idrv.coach.bean.WebParamBuilder;
import com.idrv.coach.bean.WebShareBean;
import com.idrv.coach.data.constants.ApiConstant;
import com.idrv.coach.data.constants.ShareConstant;
import com.idrv.coach.data.model.DrivingTestInsModel;
import com.idrv.coach.ui.view.InputItemView;
import com.idrv.coach.utils.SystemUtil;
import com.idrv.coach.utils.helper.DialogHelper;
import com.idrv.coach.utils.helper.UIHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Subscription;

/**
 * time:2016/3/29
 * description:驾考防爆险
 *
 * @author sunjianfei
 */
public class ApplyInsForDrivingTestActivity extends BaseActivity<DrivingTestInsModel> implements View.OnClickListener {
    @InjectView(R.id.item_owner_name)
    InputItemView mItemNameView;
    @InjectView(R.id.item_tel)
    InputItemView mItemTelView;
    @InjectView(R.id.item_card)
    InputItemView mItemCardView;

    public static void launch(Context context) {
        Intent intent = new Intent(context, ApplyInsForDrivingTestActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_apply_driving_test_ins);
        ButterKnife.inject(this);
        initToolBar();
        initView();
        initViewModel();
    }

    private void initToolBar() {
        mToolbarLayout.setTitleTxt(R.string.driving_test_ins);
        mToolbarLayout.setRightTxt(R.string.ins_detail);
    }

    private void initView() {
        mItemNameView.setInputType(InputType.TYPE_CLASS_TEXT);
        mItemTelView.setInputType(InputType.TYPE_CLASS_PHONE);
        mItemCardView.setInputType(InputType.TYPE_CLASS_TEXT);

        mItemNameView.setText(R.string.name);
        mItemTelView.setText(R.string.tel_num);
        mItemCardView.setText(R.string.item_owner_card);

        mItemNameView.setTextHint(R.string.pls_input_insured_name);
        mItemTelView.setTextHint(R.string.pls_input_insured_tel);
        mItemCardView.setTextHint(R.string.pls_input_insured_card_id);
    }

    private void initViewModel() {
        mViewModel = new DrivingTestInsModel();
    }

    @Override
    public void onToolbarRightClick(View view) {
        String url = ApiConstant.HOST + "/is/dis/detail";
        WebShareBean shareBean = new WebShareBean();
        shareBean.setShareTitle(getString(R.string.d_ins_share_title));
        shareBean.setShareContent(getString(R.string.d_ins_share_content));
        shareBean.setShareUrl(url);
        shareBean.setShareImageUrl(ShareConstant.SHARE_DEFAULT_IMAGE);
        ToolBoxWebActivity.launch(this, WebParamBuilder.create()
                .setTitle(getString(R.string.car_test_ins_details))
                .setUrl(url)
                .setPageTag(R.string.share_stu_insurance)
                .setShareBean(shareBean));
    }

    @OnClick({R.id.next_step})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next_step:
                if (checkInputValid()) {
                    commit();
                }
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
                    DrivingTestInsListActivity.launch(this);
                    ApplyInsForDrivingTestActivity.this.finish();
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
                .leftButton(getString(R.string.cancel), getResources().getColor(R.color.black_54))
                .rightButton(getString(R.string.sure), getResources().getColor(R.color.themes_main))
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
                    if (mCompositeSubscription != null) {
                        mCompositeSubscription.unsubscribe();
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
                        BindPhoneActivity.launch(this);
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
}
