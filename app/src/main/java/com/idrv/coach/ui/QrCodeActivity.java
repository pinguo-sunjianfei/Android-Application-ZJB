package com.idrv.coach.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.idrv.coach.R;
import com.idrv.coach.bean.event.EventConstant;
import com.idrv.coach.data.constants.SPConstant;
import com.idrv.coach.data.manager.RxBusManager;
import com.idrv.coach.data.model.QrCodeCoachCardModel;
import com.idrv.coach.ui.widget.BigImgView;
import com.idrv.coach.utils.BitmapUtil;
import com.idrv.coach.utils.EncryptUtil;
import com.idrv.coach.utils.Logger;
import com.idrv.coach.utils.PictureUtil;
import com.idrv.coach.utils.PreferenceUtil;
import com.idrv.coach.utils.helper.DialogHelper;
import com.idrv.coach.utils.helper.UIHelper;
import com.idrv.coach.utils.helper.ViewUtils;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.soundcloud.android.crop.Crop;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.zjb.loader.ZjbImageLoader;
import com.zjb.volley.core.exception.NetworkError;
import com.zjb.volley.utils.GsonUtil;

import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Subscription;

/**
 * time: 2016/3/18
 * description: 修改二维码界面，其实就是图片上传
 *
 * @author bigflower
 */
public class QrCodeActivity extends BaseActivity<QrCodeCoachCardModel> {

    @InjectView(R.id.QrCode_img)
    ImageView mImageView;
    @InjectView(R.id.QrCode_bt)
    Button mButton;

    public static void launch(Context context) {
        Intent intent = new Intent(context, QrCodeActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getProgressBg() {
        return R.color.bg_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_qr_code);
        ButterKnife.inject(this);

        // 1.初始化ViewModel
        mViewModel = new QrCodeCoachCardModel();
        init();
    }

    private void initToolBar() {
        mToolbarLayout.setTitle(R.string.title_qrcode);
    }

    public void init() {
        // 1.初始化标题栏
        initToolBar();
        // 2.如果有图片，则显示，且更换按钮的文字
        String imgUrl = mViewModel.gCoach.getQrCode();
        if (TextUtils.isEmpty(imgUrl))
            return;
        showQrImage(mViewModel.gCoach.getQrCode());
        mButton.setText(R.string.change_qrcode);
    }

    /**
     * 上传图片
     *
     * @param v
     */
    public void UpLoadImgClick(View v) {
        ViewUtils.setDelayedClickable(v, 1000);
//        DialogHelper.create(DialogHelper.TYPE_NORMAL)
//                .leftButton(getString(R.string.album), 0xff2b2a2a)
//                .rightButton(getString(R.string.camera), 0xff2b2a2a)
//                .title(getString(R.string.pls_choose_upload_way))
//                .leftBtnClickListener((dialog, view) -> {
//                    dialog.dismiss();
//                    PictureUtil.FindPhotoCrop(this);
//                })
//                .rightBtnClickListener((dialog, view) -> {
//                    dialog.dismiss();
//                    PictureUtil.TakePhoto(this);
//                })
//                .show();
        RxPermissions.getInstance(this)
                .request(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {
                        PictureUtil.FindPhoto(this);
                    } else {
                        UIHelper.shortToast(R.string.open_sdcard_permission);
                    }
                }, Logger::e);
    }

    /**
     * 点击查看大图
     */
    @OnClick(R.id.QrCode_img)
    void imgClick(View v) {
        ViewUtils.setDelayedClickable(v, 1000);
        String url = mViewModel.gCoach.getQrCode();
        if (!TextUtils.isEmpty(url)) {
            new BigImgView(this, url, mImageView);
        }
    }

    /**
     * 获取上传图片所需的 token
     */
    private void httpGetToken() {
        mViewModel.mEtagKey = EncryptUtil.getQETAG(mViewModel.mQiNiuFilePath);
        // 如果etag为空，则说明没有该图片路径啊
        if (TextUtils.isEmpty(mViewModel.mEtagKey)) {
            UIHelper.shortToast(R.string.upload_error_repick_pic);
            return;
        }
        mProgressDialog = DialogHelper.create(DialogHelper.TYPE_PROGRESS)
                .progressText(getString(R.string.dialog_uploading)).show();
        Subscription subscription = mViewModel.getToken()
                .subscribe(__ -> isFileExit(), this::onError);
        addSubscription(subscription);
    }

    /**
     * 判断网上该文件是否存在
     */
    private void isFileExit() {
        Subscription subscription = mViewModel.isFileExit()
                .subscribe(__ -> httpCoachInfo(), __ -> imgUpload());
        addSubscription(subscription);
    }

    /**
     * 将图片地址上传到服务器
     */
    private void httpCoachInfo() {
        Subscription subscription = mViewModel.putCoachInfo("qrCode")
                .subscribe(__ -> onUpLoadSuccess(), this::onError);
        addSubscription(subscription);
    }

    /**
     * 最终的，上传成功后，的处理
     */
    private void onUpLoadSuccess() {
        dismissProgressDialog();
        mButton.setText(R.string.change_qrcode);

        mViewModel.gCoach.setQrCode(mViewModel.mImgUrl);
        PreferenceUtil.putString(SPConstant.KEY_COACH, GsonUtil.toJson(mViewModel.gCoach));

        // 发送事件给UserInfoActivity，告知修改
        RxBusManager.post(EventConstant.KEY_REVISE_QRCODE, "");
    }

    private void onError(Throwable e) {
        dismissProgressDialog();
        showQrImage(mViewModel.gCoach.getQrCode());
        if (null != e) {
            if (e instanceof NetworkError) {
                NetworkError error = (NetworkError) e;
                UIHelper.shortToast(error.getErrorCode().getMessage());
            } else {
                UIHelper.shortToast(R.string.upload_error_repick_pic);
                e.printStackTrace();
            }
        }
    }

    /**
     * 通过七牛上传图片
     */
    private void imgUpload() {
        mViewModel.upLoad(new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info,
                                 JSONObject response) {
                if (info.isOK()) {
                    httpCoachInfo();
                } else {
                    dismissProgressDialog();
                    Logger.e("imgUpload", info.error);
                    UIHelper.shortToast(info.error);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissProgressDialog();
    }

    ////////////////////////////////////////////

    /**
     * 将Bitmap保存为图片
     *
     * @param bitmap
     * @return
     */
    public String saveToSdCard(Bitmap bitmap) {
        return BitmapUtil.saveBitmap(bitmap);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            PictureUtil.deleteUri(this);
        } else if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PictureUtil.REQUEST_CODE_FROM_CAMERA:
                    if (PictureUtil.imgUri != null) {
                        PictureUtil.photoZoom(this, PictureUtil.imgUri);
                    } else { // 某些机型，不使用output方法，在data里找，所以imageUri==null
                        PictureUtil.imgUri = data.getData();
                        if (PictureUtil.imgUri != null) {
                            PictureUtil.photoZoom(this, PictureUtil.imgUri);
                        } else {
                            Bitmap bitmap = data.getExtras().getParcelable("data");
                            String path = saveToSdCard(bitmap);
                            PictureUtil.photoZoom(this, path);
                        }
                    }
                    break;
                case PictureUtil.REQUEST_CODE_FROM_ALBUM:
                    if (data != null) {
                        Uri uri = data.getData();
                        PictureUtil.imgUri = PictureUtil.createImageUri(this);
                        if (uri != null) {
                            Crop.of(uri, PictureUtil.imgUri).asSquare().start(this);
                        } else {
                            UIHelper.shortToast(R.string.select_pic_error);
                        }
                    }
                    break;
                case Crop.REQUEST_CROP:
                    if (null != PictureUtil.imgUri) {
                        mViewModel.mQiNiuFilePath = PictureUtil.getPath(QrCodeActivity.this, PictureUtil.imgUri);
                        showQrImage("file://" + mViewModel.mQiNiuFilePath);
                        httpGetToken();
                    } else {
                        UIHelper.shortToast(R.string.select_pic_error);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void showQrImage(String imgUrl) {
        ZjbImageLoader.create(imgUrl)
                .setDisplayType(ZjbImageLoader.DISPLAY_DEFAULT)
                .setDefaultRes(R.drawable.img_qrcode)
                .into(mImageView);
    }

}
