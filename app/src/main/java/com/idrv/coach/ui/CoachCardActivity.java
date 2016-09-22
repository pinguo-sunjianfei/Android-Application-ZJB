//package com.idrv.coach.ui;
//
//import android.Manifest;
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//
//import com.idrv.coach.R;
//import com.idrv.coach.bean.event.EventConstant;
//import com.idrv.coach.data.constants.SPConstant;
//import com.idrv.coach.data.manager.RxBusManager;
//import com.idrv.coach.data.model.QrCodeCoachCardModel;
//import com.idrv.coach.ui.widget.BigImgView;
//import com.idrv.coach.utils.BitmapUtil;
//import com.idrv.coach.utils.EncryptUtil;
//import com.idrv.coach.utils.FileUtil;
//import com.idrv.coach.utils.Logger;
//import com.idrv.coach.utils.PictureUtil;
//import com.idrv.coach.utils.PreferenceUtil;
//import com.idrv.coach.utils.helper.DialogHelper;
//import com.idrv.coach.utils.helper.UIHelper;
//import com.idrv.coach.utils.helper.ViewUtils;
//import com.qiniu.android.http.ResponseInfo;
//import com.qiniu.android.storage.UpCompletionHandler;
//import com.tbruyelle.rxpermissions.RxPermissions;
//import com.zjb.loader.ZjbImageLoader;
//import com.zjb.volley.core.exception.NetworkError;
//import com.zjb.volley.utils.GsonUtil;
//
//import org.json.JSONObject;
//
//import java.io.File;
//
//import butterknife.ButterKnife;
//import butterknife.InjectView;
//import butterknife.OnClick;
//import rx.Subscription;
//
///**
// * time: 2016/3/18
// * description:
// * 教练证的上传
// * <p>
// * PS:为了避免三星手机屏幕旋转造成的bug，需在 Manifest中添加
// * android:configChanges="orientation|screenSize|keyboardHidden"
// *
// * @author bigflower
// */
//public class CoachCardActivity extends BaseActivity<QrCodeCoachCardModel> {
//
//    @InjectView(R.id.coachCard_img)
//    ImageView mImageView;
//    @InjectView(R.id.coachCard_bt)
//    Button mButton;
//
//
//    public static void launch(Context context) {
//        Intent intent = new Intent(context, CoachCardActivity.class);
//        context.startActivity(intent);
//    }
//
//    @Override
//    protected int getProgressBg() {
//        return R.color.bg_main;
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.act_coach_card);
//        ButterKnife.inject(this);
//
//        // 1.初始化ViewModel
//        mViewModel = new QrCodeCoachCardModel();
//        init();
//    }
//
//    private void initToolBar() {
//        mToolbarLayout.setTitle(R.string.title_coach_card);
//    }
//
//    public void init() {
//        // 1.初始化标题栏
//        initToolBar();
//        // 2.如果有图片，则显示，且更换按钮的文字
//        String imgUrl = mViewModel.gCoach.getCoachingBadge();
//        if (TextUtils.isEmpty(imgUrl))
//            return;
//        ZjbImageLoader.create(imgUrl)
//                .setDisplayType(ZjbImageLoader.DISPLAY_DEFAULT)
//                .setDefaultRes(R.drawable.img_idcard)
//                .into(mImageView);
//        mButton.setText(R.string.change_coach_card);
//    }
//
//    /**
//     * 上传图片的点击事件
//     *
//     * @param v
//     */
//    public void UpLoadImgClick(View v) {
//        ViewUtils.setDelayedClickable(v, 500);
//        DialogHelper.create(DialogHelper.TYPE_NORMAL)
//                .leftButton(getString(R.string.album), 0xff2b2a2a)
//                .rightButton(getString(R.string.camera), 0xff2b2a2a)
//                .title(getString(R.string.pls_choose_upload_way))
//                .leftBtnClickListener((dialog, view) -> {
//                    dialog.dismiss();
//                    PictureUtil.FindPhoto(this);
//                })
//                .rightBtnClickListener((dialog, view) -> {
//                    dialog.dismiss();
//                    openCamera();
//                })
//                .show();
//    }
//
//    /**
//     * 先申请权限,再打开相机
//     */
//    private void openCamera() {
//        RxPermissions.getInstance(this)
//                .request(Manifest.permission.CAMERA,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                .subscribe(granted -> {
//                    if (granted) {
//                        PictureUtil.TakePhoto(this);
//                    } else {
//                        UIHelper.shortToast(R.string.open_camera_permission);
//                    }
//                }, Logger::e);
//    }
//
//    /**
//     * 点击查看大图
//     */
//    @OnClick(R.id.coachCard_img)
//    void imgClick(View v) {
//        ViewUtils.setDelayedClickable(v, 500);
//        String url = mViewModel.gCoach.getCoachingBadge();
//        if (!TextUtils.isEmpty(url)) {
//            new BigImgView(this, url, mImageView);
//        }
//    }
//
//    //////////////////////////////////////////////
//
//    /**
//     * 获取上传图片所需的 token
//     */
//    private void httpGetToken() {
//        detectImageIsBig();
//        mViewModel.mEtagKey = EncryptUtil.getQETAG(mViewModel.mQiNiuFilePath);
//        // 如果etag为空，则说明没有该图片路径啊
//        if (mViewModel.mEtagKey == null) {
//            UIHelper.shortToast("上传失败，请重新选择图片");
//            return;
//        }
//
//        mProgressDialog = DialogHelper.create(DialogHelper.TYPE_PROGRESS)
//                .progressText(getString(R.string.dialog_uploading)).show();
//        Subscription subscription = mViewModel.getToken()
//                .subscribe(__ -> {
//                    Logger.i("接收到回调了");
//                    isFileExit();
//                }, this::onError);
//        addSubscription(subscription);
//    }
//
//    /**
//     * 判断该文件是否存在
//     */
//    private void isFileExit() {
//        Logger.i("发出isFileExit的请求");
//        Subscription subscription = mViewModel.isFileExit()
//                .subscribe(__ -> httpCoachInfo(), __ -> imgUpload());
//        addSubscription(subscription);
//    }
//
//    /**
//     * 将图片地址上传到服务器
//     */
//    private void httpCoachInfo() {
//        Logger.i("图片存在，直接告诉服务器");
//        Subscription subscription = mViewModel.putCoachInfo("coachingBadge")
//                .subscribe(__ -> onUpLoadSuccess(), this::onError);
//        addSubscription(subscription);
//    }
//
//    /**
//     * 最终的，上传成功后，的处理
//     */
//    private void onUpLoadSuccess() {
//        dismissProgressDialog();
//        mImageView.setPadding(0, 0, 0, 0);
//        mButton.setText(R.string.change_coach_card);
//        // 显示图片
//        ZjbImageLoader.create(mViewModel.mImgUrl)
//                .setDisplayType(ZjbImageLoader.DISPLAY_DEFAULT)
//                .into(mImageView);
//
//        mViewModel.gCoach.setCoachingBadge(mViewModel.mImgUrl);
//        PreferenceUtil.putString(SPConstant.KEY_COACH, GsonUtil.toJson(mViewModel.gCoach));
//
//        // 发送事件给UserInfoActivity，告知修改内容
//        RxBusManager.post(EventConstant.KEY_REVISE_COACHCARD, "");
//
//    }
//
//    private void onError(Throwable e) {
//        dismissProgressDialog();
//        if (null != e) {
//            if (e instanceof NetworkError) {
//                NetworkError error = (NetworkError) e;
//                UIHelper.shortToast(error.getErrorCode().getMessage());
//            } else {
//                UIHelper.shortToast(R.string.upload_error_repick_pic);
//                e.printStackTrace();
//            }
//        }
//    }
//
//    /**
//     * 通过七牛上传图片
//     */
//    private void imgUpload() {
//        Logger.i("图片bu存在，七牛上传");
//        mViewModel.upLoad(new UpCompletionHandler() {
//            @Override
//            public void complete(String key, ResponseInfo info,
//                                 JSONObject response) {
//                if (info.isOK()) {
//                    httpCoachInfo();
//                } else {
//                    dismissProgressDialog();
//                    UIHelper.shortToast(R.string.upload_error_repick_pic);
//                }
//            }
//        });
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        dismissProgressDialog();
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_CANCELED) {
//            PictureUtil.deleteUri(this);
//        } else if (resultCode == Activity.RESULT_OK) {
//            String path = getFilesDir().getAbsolutePath();
//            File dir = new File(path + "/image");
//            if (!dir.exists()) {
//                dir.mkdirs();
//            }
//            switch (requestCode) {
//                case PictureUtil.REQUEST_CODE_FROM_CAMERA:
//                    if (PictureUtil.imgUri != null) {
//                        httpGetToken();
//                    } else {
//                        UIHelper.shortToast("出错啦，请联系益驾科技");
//                    }
//                    break;
//                case PictureUtil.REQUEST_CODE_FROM_ALBUM:
//                    if (data != null) {
//                        PictureUtil.imgUri = data.getData();
//                        httpGetToken();
//                    } else {
//                        Logger.i("onActivityResult", "图片data居然为空");
//                    }
//                    break;
//            }
//        }
//    }
//
//    /**
//     * 判断图片是否超过 500k
//     * 如果超过，则压缩一道
//     */
//    private void detectImageIsBig() {
//        String filePath = PictureUtil.getPath(CoachCardActivity.this, PictureUtil.imgUri);
//        double size = FileUtil.getFileOrFilesSize(filePath, 2);
//        if (size > 500) { // 判断图片是否大于500kb,大于压缩
//            mViewModel.mQiNiuFilePath = saveToSdCard(PictureUtil
//                    .compressImageByPixel(filePath));
//        } else {
//            mViewModel.mQiNiuFilePath = filePath;
//        }
//    }
//
//    /**
//     * 将Bitmap保存为图片
//     *
//     * @param bitmap
//     * @return
//     */
//    public String saveToSdCard(Bitmap bitmap) {
//        return BitmapUtil.saveBitmap(bitmap);
//    }
//
//}
