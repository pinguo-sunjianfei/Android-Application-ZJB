package com.idrv.coach.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.idrv.coach.R;
import com.idrv.coach.bean.CarInsuranceInfo;
import com.idrv.coach.bean.event.EventConstant;
import com.idrv.coach.data.manager.RxBusManager;
import com.idrv.coach.data.model.ApplyInsuranceModel;
import com.idrv.coach.ui.view.InputItemView;
import com.idrv.coach.ui.view.MasterItemView;
import com.idrv.coach.ui.view.SelectPhotoDialog;
import com.idrv.coach.utils.Logger;
import com.idrv.coach.utils.PictureUtil;
import com.idrv.coach.utils.SystemUtil;
import com.idrv.coach.utils.helper.DialogHelper;
import com.idrv.coach.utils.helper.UIHelper;
import com.tbruyelle.rxpermissions.RxPermissions;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * time:2016/3/19
 * description:车险询价
 *
 * @author sunjianfei
 */
public class ApplyInsuranceActivity extends BaseActivity<ApplyInsuranceModel> implements
        MasterItemView.OnMasterItemClickListener, SelectPhotoDialog.OnButtonClickListener,
        MasterItemView.onMasterItemRightIvClickListener {
    @InjectView(R.id.item_owner_name)
    InputItemView mItemNameView;
    @InjectView(R.id.item_tel)
    InputItemView mItemTelView;
    @InjectView(R.id.item_card)
    InputItemView mItemCardView;

    @InjectView(R.id.item_positive_identification)
    MasterItemView mItemCardPositiveView;
    @InjectView(R.id.item_negative_identification)
    MasterItemView mItemCardNegativeView;
    @InjectView(R.id.item_driving_license_first_page)
    MasterItemView mItemDrivingLicenseFirstView;
    @InjectView(R.id.item_driving_license_second_page)
    MasterItemView mItemDrivingLicenseSecondView;
    @InjectView(R.id.radio_btn_new_car)
    RadioButton mNewCarRadioBtn;
    @InjectView(R.id.radio_btn_old_car)
    RadioButton mOldCarRadioBtn;

    @InjectView(R.id.tips_description)
    TextView mTipsDesTv;

    private SelectPhotoDialog mDialog;


    public static void launch(Context context) {
        Intent intent = new Intent(context, ApplyInsuranceActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_apply_for_insurance);
        ButterKnife.inject(this);
        initToolBar();
        initView();
        initViewModel();
        registerEvent();
    }

    private void initToolBar() {
        mToolbarLayout.setTitleTxt(getString(R.string.app_ins_title));
    }

    private void initView() {
        mItemNameView.setInputType(InputType.TYPE_CLASS_TEXT);
        mItemTelView.setInputType(InputType.TYPE_CLASS_PHONE);
        mItemCardView.setInputType(InputType.TYPE_CLASS_TEXT);

        mItemNameView.setText(R.string.item_owner_name);
        mItemTelView.setText(R.string.item_tel);
        mItemCardView.setText(R.string.item_owner_card);

        mItemNameView.setTextHint(R.string.pls_input_owner_name);
        mItemTelView.setTextHint(R.string.pls_input_tel);
        mItemCardView.setTextHint(R.string.pls_input_card_id);

        mItemCardPositiveView.setText(R.string.item_positive_identification);
        mItemCardNegativeView.setText(R.string.item_negative_identification);
        mItemDrivingLicenseFirstView.setText(R.string.car_invoice);
        mItemDrivingLicenseSecondView.setText(R.string.car_certificate);

        mItemCardPositiveView.setRightImage("drawable://" + R.drawable.id_card_default);
        mItemCardNegativeView.setRightImage("drawable://" + R.drawable.id_card_default);

        mItemDrivingLicenseFirstView.setRightImage("drawable://" + R.drawable.driver_license_default);
        mItemDrivingLicenseSecondView.setRightImage("drawable://" + R.drawable.driver_license_default);

        mItemCardPositiveView.setOnMasterItemClickListener(this);
        mItemCardNegativeView.setOnMasterItemClickListener(this);
        mItemDrivingLicenseFirstView.setOnMasterItemClickListener(this);
        mItemDrivingLicenseSecondView.setOnMasterItemClickListener(this);

        mItemCardPositiveView.setRightImageViewClickListener(this);
        mItemCardNegativeView.setRightImageViewClickListener(this);
        mItemDrivingLicenseFirstView.setRightImageViewClickListener(this);
        mItemDrivingLicenseSecondView.setRightImageViewClickListener(this);

        mItemDrivingLicenseSecondView.setLineVisible(View.GONE);

        mNewCarRadioBtn.setChecked(true);
        mOldCarRadioBtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mTipsDesTv.setText(R.string.old_car_insurance_tips);
                mViewModel.setInsuranceType(ApplyInsuranceModel.InsuranceType.OLD_CAR);
                showAllImageWhenSwitchItem();
            } else {
                mTipsDesTv.setText(R.string.insurance_tips);
            }
        });

        mNewCarRadioBtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mTipsDesTv.setText(R.string.insurance_tips);
                mViewModel.setInsuranceType(ApplyInsuranceModel.InsuranceType.NEW_CAR);
                showAllImageWhenSwitchItem();
            } else {
                mTipsDesTv.setText(R.string.old_car_insurance_tips);
            }
        });
    }

    /**
     * 注册事件
     */
    private void registerEvent() {
        RxBusManager.register(this, EventConstant.KEY_FILE_UPLOAD_FAILED, String.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onUploadFailed, Logger::e);
        RxBusManager.register(this, EventConstant.KEY_FILE_UPLOAD_SUCCESS, String.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onUploadSuccess, Logger::e);
    }

    private void initViewModel() {
        mViewModel = new ApplyInsuranceModel();
    }

    @Override
    public void onMasterItemClick(View v) {
        switch (v.getId()) {
            case R.id.item_positive_identification:
                showDialog(ApplyInsuranceModel.PhotoType.PHOTO_CARD_POSITIVE);
                break;
            case R.id.item_negative_identification:
                showDialog(ApplyInsuranceModel.PhotoType.PHOTO_CARD_NEGATIVE);
                break;
            case R.id.item_driving_license_first_page: {
                ApplyInsuranceModel.InsuranceType type = mViewModel.getInsuranceType();
                ApplyInsuranceModel.PhotoType photoType = type == ApplyInsuranceModel.InsuranceType.NEW_CAR ?
                        ApplyInsuranceModel.PhotoType.CAR_INVOICE : ApplyInsuranceModel.PhotoType.DRIVING_LICENSE_FIRST_PAGE;
                showDialog(photoType);
            }
            break;
            case R.id.item_driving_license_second_page:
                ApplyInsuranceModel.InsuranceType type = mViewModel.getInsuranceType();
                ApplyInsuranceModel.PhotoType photoType = type == ApplyInsuranceModel.InsuranceType.NEW_CAR ?
                        ApplyInsuranceModel.PhotoType.CAR_CERTIFICATE : ApplyInsuranceModel.PhotoType.DRIVING_LICENSE_SECOND_PAGE;
                showDialog(photoType);
                break;
        }
    }

    @OnClick(R.id.btn_commit)
    public void onCommitClick() {
        if (checkInputValid()) {
            showDialog();
            mViewModel.bulkUpload();
        }
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

    @Override
    public void camera() {
        openCamera();
    }

    @Override
    public void gallery() {
        PictureUtil.FindPhoto(this);
    }

    private void showSelectImage(String path) {
        switch (mViewModel.getPhotoType()) {
            case PHOTO_CARD_POSITIVE:
                mItemCardPositiveView.setRightImage("file:///" + path);
                break;
            case PHOTO_CARD_NEGATIVE:
                mItemCardNegativeView.setRightImage("file:///" + path);
                break;
            case DRIVING_LICENSE_FIRST_PAGE:
            case CAR_INVOICE:
                mItemDrivingLicenseFirstView.setRightImage("file:///" + path);
                break;
            case DRIVING_LICENSE_SECOND_PAGE:
            case CAR_CERTIFICATE:
                mItemDrivingLicenseSecondView.setRightImage("file:///" + path);
                break;
        }
    }

    private void openCamera() {
        RxPermissions.getInstance(this)
                .request(Manifest.permission.CAMERA)
                .subscribe(granted -> {
                    if (granted) {
                        PictureUtil.TakePhoto(this);
                    } else {
                        UIHelper.shortToast(R.string.open_camera_permission);
                    }
                }, Logger::e);
    }

    private void showAllImageWhenSwitchItem() {
        CarInsuranceInfo info = mViewModel.getInfo();
        String firstPath = info.getCardPositivePath();
        String secondPath = info.getCardNegativePath();
        String thirdPath = mViewModel.getInsuranceType() == ApplyInsuranceModel.InsuranceType.NEW_CAR ?
                info.getInvoicePath() : info.getDrivingLicenseFirstPagePath();
        String fourPath = mViewModel.getInsuranceType() == ApplyInsuranceModel.InsuranceType.NEW_CAR ?
                info.getCarCertificatePath() : info.getDrivingLicenseSecondPagePath();
        firstPath = TextUtils.isEmpty(firstPath) ? ("drawable://" + R.drawable.id_card_default) :
                "file:///" + firstPath;
        secondPath = TextUtils.isEmpty(secondPath) ? ("drawable://" + R.drawable.id_card_default) :
                "file:///" + secondPath;
        thirdPath = TextUtils.isEmpty(thirdPath) ? ("drawable://" + R.drawable.driver_license_default) :
                "file:///" + thirdPath;
        fourPath = TextUtils.isEmpty(fourPath) ? ("drawable://" + R.drawable.driver_license_default) :
                "file:///" + fourPath;
        mItemCardPositiveView.setRightImage(firstPath);
        mItemCardNegativeView.setRightImage(secondPath);
        mItemDrivingLicenseFirstView.setRightImage(thirdPath);
        mItemDrivingLicenseSecondView.setRightImage(fourPath);

        int thirdLeftResId = mViewModel.getInsuranceType() == ApplyInsuranceModel.InsuranceType.NEW_CAR ?
                R.string.car_invoice : R.string.item_driving_license_first_page;
        int fourLeftResId = mViewModel.getInsuranceType() == ApplyInsuranceModel.InsuranceType.NEW_CAR ?
                R.string.car_certificate : R.string.item_driving_license_second_page;
        mItemDrivingLicenseFirstView.setText(thirdLeftResId);
        mItemDrivingLicenseSecondView.setText(fourLeftResId);
    }

    private void showDialog(ApplyInsuranceModel.PhotoType type) {
        mViewModel.setPhotoType(type);
        if (null != mDialog && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        } else {
            mDialog = new SelectPhotoDialog(this);
            mDialog.setOnButtonClickListener(this);
            mDialog.show();
        }
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
                    .content(getString(R.string.improve_personal_information))
                    .bottomButton(getString(R.string.improve_personal_information_now), R.color.themes_main)
                    .bottomBtnClickListener((dialog, v) -> {
                        dialog.dismiss();
                        BindPhoneActivity.launch(this);
                    })
                    .show();
            return false;
        } else if (TextUtils.isEmpty(name)) {
            UIHelper.shortToast(R.string.name_not_be_null);
            return false;
        } else if (!SystemUtil.checkPhoneNumber(tel)) {
            UIHelper.shortToast(R.string.pls_input_valid_tel_num);
            return false;
        } else if (TextUtils.isEmpty(card_id) || card_id.length() != 18) {
            UIHelper.shortToast(R.string.pls_input_valid_card_id);
            return false;
        } else if (!mViewModel.checkValidPhoto()) {
            UIHelper.shortToast(R.string.pls_select_upload_file);
            return false;
        }

        CarInsuranceInfo info = mViewModel.getInfo();
        info.setName(name);
        info.setTelNum(tel);
        info.setCardId(card_id);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            PictureUtil.deleteUri(this);
        } else if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PictureUtil.REQUEST_CODE_FROM_CAMERA:
                    if (PictureUtil.imgUri != null) {
                        String filePath = PictureUtil.getPath(ApplyInsuranceActivity.this, PictureUtil.imgUri);
                        mViewModel.setImagePath(filePath);
                        showSelectImage(filePath);
                    } else {
                        Logger.e("图片选择失败!请联系益驾科技");
                    }
                    break;
                case PictureUtil.REQUEST_CODE_FROM_ALBUM:
                    if (data != null) {
                        PictureUtil.imgUri = data.getData();
                        String filePath = PictureUtil.getPath(ApplyInsuranceActivity.this, PictureUtil.imgUri);
                        mViewModel.setImagePath(filePath);
                        showSelectImage(filePath);
                    } else {
                        Logger.e("onActivityResult", "图片data居然为空");
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onRightImageViewClick(String path) {
        PhotoActivity.launch(this, path);
    }

    private void onUploadFailed(String s) {
        dismissProgressDialog();
        DialogHelper.create(DialogHelper.TYPE_NORMAL)
                .cancelable(true)
                .canceledOnTouchOutside(false)
                .title(getString(R.string.status_error_tip))
                .content(getString(R.string.upload_pic_error))
                .leftButton(getString(R.string.cancel), getResources().getColor(R.color.black_54))
                .rightButton(getString(R.string.sure), getResources().getColor(R.color.themes_main))
                .leftBtnClickListener((dialog, v) -> dialog.dismiss())
                .rightBtnClickListener((dialog, v) -> {
                    onCommitClick();
                    dialog.dismiss();
                })
                .show();
    }

    private void onUploadSuccess(String s) {
        Subscription subscription = mViewModel.postInsuranceInfo()
                .subscribe(this::onCommitSuccess, this::onCommitError);
        addSubscription(subscription);
    }

    private void onCommitSuccess(String s) {
        dismissProgressDialog();
        DialogHelper.create(DialogHelper.TYPE_NORMAL)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .title(getString(R.string.inquiry_success))
                .content(getString(R.string.inquiry_tips))
                .bottomButton(getString(R.string.confirm), getResources().getColor(R.color.themes_main))
                .bottomBtnClickListener((dialog, v) -> {
                    dialog.dismiss();
                    ApplyInsuranceActivity.this.finish();
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
                    onUploadSuccess("");
                    showDialog();
                    dialog.dismiss();
                })
                .show();
    }
}
