package com.idrv.coach.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.idrv.coach.R;
import com.idrv.coach.bean.Coach;
import com.idrv.coach.data.constants.SPConstant;
import com.idrv.coach.data.manager.LoginManager;
import com.idrv.coach.data.model.CoachAuthModel;
import com.idrv.coach.ui.view.SelectPhotoDialog;
import com.idrv.coach.utils.Logger;
import com.idrv.coach.utils.PictureUtil;
import com.idrv.coach.utils.PreferenceUtil;
import com.idrv.coach.utils.helper.DialogHelper;
import com.idrv.coach.utils.helper.UIHelper;
import com.idrv.coach.utils.helper.ViewUtils;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.zjb.volley.core.exception.NetworkError;
import com.zjb.volley.utils.GsonUtil;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * time:2016/8/19
 * description:教练认证
 *
 * @author sunjianfei
 */
public class CoachAuthenticationActivity extends BaseActivity<CoachAuthModel>
        implements SelectPhotoDialog.OnButtonClickListener {
    @InjectView(R.id.coach_card_iv)
    ImageView mCoachCardIv;
    @InjectView(R.id.id_card_iv)
    ImageView mIDCardIv;
    @InjectView(R.id.auth_failed_tv)
    TextView mAuthFailedTv;

    private SelectPhotoDialog mDialog;

    public static void launch(Context context) {
        Intent intent = new Intent(context, CoachAuthenticationActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_coach_authentication);
        ButterKnife.inject(this);
        initToolBar();
        initViewModel();
    }

    @Override
    public void camera() {
        openCamera();
    }

    @Override
    public void gallery() {
        PictureUtil.FindPhoto(this);
    }

    private void initToolBar() {
        mToolbarLayout.setTitle(R.string.coach_authentication);
        Coach coach = LoginManager.getInstance().getCoach();
        if (coach.getAuthenticationState() == Coach.STATE_AUTH_FAILED) {
            //如果是审核失败
            mAuthFailedTv.setVisibility(View.VISIBLE);
        }
    }

    private void initViewModel() {
        mViewModel = new CoachAuthModel();
    }

    private void showDialog() {
        if (null != mDialog && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        } else {
            mDialog = new SelectPhotoDialog(this);
            mDialog.setOnButtonClickListener(this);
            mDialog.show();
        }
    }

    /**
     * 先申请权限,再打开相机
     */
    private void openCamera() {
        RxPermissions.getInstance(this)
                .request(Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {
                        PictureUtil.TakePhoto(this);
                    } else {
                        UIHelper.shortToast(R.string.open_camera_permission);
                    }
                }, Logger::e);
    }

    /**
     * 显示选择的图片
     *
     * @param path
     */
    private void showSelectImage(String path) {
        if (mViewModel.getCurrentType() == CoachAuthModel.TYPE_COACH) {
            ViewUtils.showImage(mCoachCardIv, "file:///" + path);
        } else {
            ViewUtils.showImage(mIDCardIv, "file:///" + path);
        }
    }

    /**
     * 上传图片
     *
     * @param filePath
     */
    private void uploadImage(String filePath) {
        showProgressDialog(R.string.upload_photo_now);
        //1.压缩图片
        Subscription subscription = mViewModel.resizeImage(filePath, 600)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::getQiNiuToken, this::onError);
        addSubscription(subscription);
    }

    /**
     * 获取七牛token
     *
     * @param filePath
     */
    private void getQiNiuToken(String filePath) {
        mViewModel.setQiNiuFilePath(filePath);
        //2.获取token
        Subscription subscription = mViewModel.getToken()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(__ -> uploadToQiNiu(), this::onError);
        addSubscription(subscription);
    }

    /**
     * 上传图片到七牛
     */
    private void uploadToQiNiu() {
        mViewModel.upLoad((key, info, response) -> {
            dismissProgressDialog();
            if (info.isOK()) {
                mViewModel.setImageUrl();
            } else {
                onError(new NetworkError());
            }
        });
    }

    /**
     * 提交认证信息
     */
    private void authentication() {
        showProgressDialog(R.string.commit_now);
        Subscription subscription = mViewModel.authentication()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onAuthenticationNext, this::onUploadFailed);
        addSubscription(subscription);
    }

    private void onError(Throwable e) {
        dismissProgressDialog();
        if (mViewModel.getCurrentType() == CoachAuthModel.TYPE_COACH) {
            mCoachCardIv.setImageResource(R.drawable.auth_coach_card_default);
        } else {
            mIDCardIv.setImageResource(R.drawable.auth_id_card_default);
        }
        UIHelper.shortToast(R.string.upload_error_repick_pic);
    }

    private void onUploadFailed(Throwable e) {
        dismissProgressDialog();
        DialogHelper.create(DialogHelper.TYPE_NORMAL)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .title(getString(R.string.status_error_tip))
                .content(getString(R.string.auth_commit_failed))
                .bottomButton(getString(R.string.ok), getResources().getColor(R.color.themes_main))
                .bottomBtnClickListener(((dialog, view) -> dialog.dismiss()))
                .show();
    }

    private void onAuthenticationNext(String s) {
        dismissProgressDialog();
        updateCoachInfo();
        CoachAuthenticationCommitActivity.launch(this);
        this.finish();
    }

    /**
     * 提交审核后,更新教练资料
     */
    private void updateCoachInfo() {
        Coach coach = LoginManager.getInstance().getCoach();
        coach.setAuthenticationState(Coach.STATE_APPLY);
        PreferenceUtil.putString(SPConstant.KEY_COACH, GsonUtil.toJson(coach));
    }

    private boolean checkInfoValid() {
        String mCoachCardUrl = mViewModel.getCoachCardUrl();
        String mIDCardUrl = mViewModel.getIDCardUrl();

        if (TextUtils.isEmpty(mCoachCardUrl)) {
            UIHelper.shortToast(R.string.upload_coach_card_pls);
            return false;
        } else if (TextUtils.isEmpty(mIDCardUrl)) {
            UIHelper.shortToast(R.string.upload_id_card_pls);
            return false;
        }
        return true;
    }

    @OnClick({R.id.coach_card_layout, R.id.id_card_layout, R.id.btn_commit})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.coach_card_layout:
                mViewModel.setCurrentType(CoachAuthModel.TYPE_COACH);
                showDialog();
                break;
            case R.id.id_card_layout:
                mViewModel.setCurrentType(CoachAuthModel.TYPE_ID);
                showDialog();
                break;
            case R.id.btn_commit:
                if (checkInfoValid()) {
                    authentication();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            String path = getFilesDir().getAbsolutePath();
            File dir = new File(path + "/image");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            switch (requestCode) {
                case PictureUtil.REQUEST_CODE_FROM_CAMERA:
                    if (PictureUtil.imgUri != null) {
                        String filePath = PictureUtil.getPath(this, PictureUtil.imgUri);
                        showSelectImage(filePath);
                        uploadImage(filePath);
                    } else {
                        UIHelper.shortToast("出错啦，请联系益驾科技");
                    }
                    break;
                case PictureUtil.REQUEST_CODE_FROM_ALBUM:
                    if (data != null) {
                        PictureUtil.imgUri = data.getData();
                        String filePath = PictureUtil.getPath(this, PictureUtil.imgUri);
                        showSelectImage(filePath);
                        uploadImage(filePath);
                    } else {
                        UIHelper.shortToast("出错啦，请联系益驾科技");
                    }
                    break;
            }
        }
    }
}
