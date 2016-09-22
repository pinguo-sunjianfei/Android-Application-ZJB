package com.idrv.coach.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.idrv.coach.R;
import com.idrv.coach.bean.event.EventConstant;
import com.idrv.coach.data.manager.RxBusManager;
import com.idrv.coach.data.model.AvatarModel;
import com.idrv.coach.utils.BitmapUtil;
import com.idrv.coach.utils.EncryptUtil;
import com.idrv.coach.utils.Logger;
import com.idrv.coach.utils.PictureUtil;
import com.idrv.coach.utils.helper.DialogHelper;
import com.idrv.coach.utils.helper.UIHelper;
import com.idrv.coach.utils.helper.ViewUtils;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.zjb.loader.ZjbImageLoader;
import com.zjb.volley.core.exception.NetworkError;

import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscription;

/**
 * time: 2016/3/28
 * description: 修改头像界面，2016-04-07弃用！
 *
 * @author bigflower
 */
public class AvatarActivity extends BaseActivity<AvatarModel> {

    private static final String KEY_IMAGE_PATH = "path";

    @InjectView(R.id.avatar_avatarIv)
    ImageView mAvatarIv;

    public static void launch(Activity activity, String path) {
        Intent intent = new Intent(activity, AvatarActivity.class);
        intent.putExtra(KEY_IMAGE_PATH, path);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_avatar);
        ButterKnife.inject(this);
        Logger.i(UIHelper.getScreenSize(this)[0] + " " + UIHelper.getScreenSize(this)[1]);
        //2.初始化ViewModel
        mViewModel = new AvatarModel();
        // 3. 加载布局，并根据状态赋值
        initViews();
    }

    @Override
    protected boolean isToolbarEnable() {
        return false;
    }

    private void initViews() {
        // 初始化尺寸
        mViewModel.gScreenHeight = UIHelper.getScreenSize(this)[0];
        ViewGroup.LayoutParams lp = mAvatarIv.getLayoutParams();
        lp.height = mViewModel.gScreenHeight;
        mAvatarIv.setLayoutParams(lp);

        // 加载头像
        mViewModel.gAvatarUrl = getIntent().getStringExtra(KEY_IMAGE_PATH);
        showAvatar(mViewModel.gAvatarUrl);
    }

    ////////////////////////////////////////////
    // 点击事件
    ////////////////////////////////////////////
    public void CameraClick(View v) {
        PictureUtil.TakePhoto(this);
    }

    public void AlbumClick(View v) {
        PictureUtil.FindPhotoCrop(this);
    }

    public void CancelClick(View v) {
        finish();
    }


    //////////////////////////////////////////////
    // 下面都是上传相关的

    /**
     * 获取上传图片所需的 token
     */
    private void httpGetToken() {
        mViewModel.mEtagKey = EncryptUtil.getQETAG(mViewModel.mQiNiuFilePath);
        // 如果etag为空，则说明没有该图片路径啊
        if (mViewModel.mEtagKey == null) {
            UIHelper.shortToast("上传失败，请重新选择图片");
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
                .subscribe(__ -> httpUserInfo(), __ -> imgUpload());
        addSubscription(subscription);
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
                    httpUserInfo();
                } else {
                    dismissProgressDialog();
                    Logger.e("imgUpload", info.error);
                    UIHelper.shortToast(info.error);
                }
            }
        });
    }

    /**
     * 将图片地址上传到服务器
     */
    private void httpUserInfo() {
        Subscription subscription = mViewModel.putUserInfo("headimgurl", mViewModel.mImgUrl)
                .subscribe(__ -> onUpLoadSuccess(), this::onError);
        addSubscription(subscription);
    }

    /**
     * 最终的，上传成功后，的处理
     */
    private void onUpLoadSuccess() {
        dismissProgressDialog();

        RxBusManager.post(EventConstant.KEY_REVISE_USERINFO, "");
        RxBusManager.post(EventConstant.KEY_REVISE_AVATAR, mViewModel.mImgUrl);
        UIHelper.shortToast(R.string.upload_success);
        finish();
    }

    private void onError(Throwable e) {
        dismissProgressDialog();
        ViewUtils.showImage(mAvatarIv, mViewModel.gAvatarUrl);
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


    ////////////////////////////////////////////
    // 相册相关用
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


    public void startPhotoZoom(Object object) {
        String path;
        if (object instanceof Uri) {
            path = PictureUtil.getPath(this, (Uri) object); // 获取图片的绝对路径
        } else {
            path = object.toString();
        }

        Uri imgUri;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            imgUri = Uri.parse("file:///" + path); // 将绝对路径转换为URL
        } else {
            imgUri = Uri.parse(path); // 将绝对路径转换为URL
        }

        Intent intent = CropIntent();
        intent.setDataAndType(imgUri, "image/*");
        startActivityForResult(intent, PictureUtil.REQUEST_CODE_FROM_CROP);
    }

    /**
     * 图片裁剪
     *
     * @param uri 图片的uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = CropIntent();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {

            Uri selectedImage = uri;
            String imagePath = PictureUtil.getPath(this, selectedImage); // 获取图片的绝对路径
            Uri newUri = Uri.parse("file:///" + imagePath); // 将绝对路径转换为URL
            intent.setDataAndType(newUri, "image/*");

            startActivityForResult(intent, PictureUtil.REQUEST_CODE_FROM_CROP);// 4.4版本
        } else {
            intent.setDataAndType(uri, "image/*");
            startActivityForResult(intent, PictureUtil.REQUEST_CODE_FROM_CROP);// 4.4以下版本
        }
    }

    /**
     * 开始裁剪
     *
     * @return
     */
    private Intent CropIntent() {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 240);
        intent.putExtra("outputY", 240);
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);
        intent.putExtra("return-data", true);

        return intent;
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
                        startPhotoZoom(PictureUtil.imgUri);
                    } else { // 某些机型，不使用output方法，在data里找，所以imageUri==null
                        PictureUtil.imgUri = data.getData();
                        if (PictureUtil.imgUri != null) {
                            startPhotoZoom(PictureUtil.imgUri);
                        } else {
                            Bitmap bitmap = data.getExtras().getParcelable("data");
                            String path = saveToSdCard(bitmap);
                            startPhotoZoom(path);
                        }
                    }
                    break;
                case PictureUtil.REQUEST_CODE_FROM_CROP:
                    if (data != null) {
                        Bundle extras = data.getExtras();
                        if (extras != null) {
                            Logger.i(" crop");
                            Bitmap bitmap = extras.getParcelable("data");
                            mViewModel.mQiNiuFilePath = saveToSdCard(bitmap);
                        } else {
                            mViewModel.mQiNiuFilePath = PictureUtil.getPath(AvatarActivity.this, PictureUtil.imgUri);
                        }
                        showAvatar("file://" + mViewModel.mQiNiuFilePath);
                        httpGetToken();
                    }
                    break;
                case PictureUtil.REQUEST_CODE_ALBUM_CROP:
                    if (!PictureUtil.isPhotoReallyCropped(PictureUtil.imgUri)) {
                        PictureUtil.imgUri = data.getData();
                        if (PictureUtil.imgUri != null) {
                            PictureUtil.photoZoom(this, PictureUtil.imgUri);
                        } else {
                            Bitmap bitmap = data.getExtras().getParcelable("data");
                            String path = saveToSdCard(bitmap);
                            PictureUtil.photoZoom(this, path);
                        }
                    } else {
                        mViewModel.mQiNiuFilePath = PictureUtil.getPath(AvatarActivity.this, PictureUtil.imgUri);
                        showAvatar("file://" + mViewModel.mQiNiuFilePath);
                        httpGetToken();
                    }
                    break;
                default:
                    break;
            }
        }
    }


    private void showAvatar(String imgUrl) {
        ZjbImageLoader.create(imgUrl)
                .setDisplayType(ZjbImageLoader.DISPLAY_DEFAULT)
                .setDefaultRes(R.drawable.img_avatar_default_big)
                .into(mAvatarIv);
    }
}
