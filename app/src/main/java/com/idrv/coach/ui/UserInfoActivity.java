package com.idrv.coach.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.flowerfat.wheellib.expend.io.OnPopupListener;
import com.flowerfat.wheellib.expend.view.YearPopup;
import com.idrv.coach.R;
import com.idrv.coach.bean.Coach;
import com.idrv.coach.bean.User;
import com.idrv.coach.bean.event.EventConstant;
import com.idrv.coach.data.manager.RxBusManager;
import com.idrv.coach.data.model.UserInfoModel;
import com.idrv.coach.data.model.UserReviseModel;
import com.idrv.coach.ui.view.MasterItemView;
import com.idrv.coach.utils.BitmapUtil;
import com.idrv.coach.utils.EncryptUtil;
import com.idrv.coach.utils.Logger;
import com.idrv.coach.utils.PictureUtil;
import com.idrv.coach.utils.TimeUtil;
import com.idrv.coach.utils.helper.DialogHelper;
import com.idrv.coach.utils.helper.UIHelper;
import com.idrv.coach.utils.helper.ViewUtils;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.soundcloud.android.crop.Crop;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.zjb.volley.core.exception.NetworkError;

import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscription;

/**
 * Created by bigflower on 2016/03/14
 */
public class UserInfoActivity extends BaseActivity<UserInfoModel> implements MasterItemView.OnMasterItemClickListener {

    @InjectView(R.id.item_user_photo)
    MasterItemView mPhotoItemView;
    @InjectView(R.id.item_user_nickname)
    MasterItemView mNicknameItemView;
    @InjectView(R.id.item_user_teachAge)
    MasterItemView mTeachAgeItemView;
    @InjectView(R.id.item_user_record)
    MasterItemView mRecordItemView;

    @InjectView(R.id.item_user_contact)
    MasterItemView mContactItemView;
    @InjectView(R.id.item_user_address)
    MasterItemView mAddressItemView;

    public static void launch(Context context) {
        Intent intent = new Intent(context, UserInfoActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getProgressBg() {
        return R.color.bg_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_user_info);
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
        mToolbarLayout.setTitle(R.string.title_user_info);
    }

    /**
     * 初始化所有Item
     */
    private void initViews() {
        initView(mPhotoItemView, R.string.item_user_photo, R.string.unUpload, false);
        initView(mNicknameItemView, R.string.item_user_nickname, R.string.unFilled, false);
        initView(mTeachAgeItemView, R.string.item_user_teachAge, R.string.unSetting, true);

        initView(mRecordItemView, R.string.item_user_record, R.string.unUpload, false);

        initView(mContactItemView, R.string.title_contact_way, R.string.unSetting, false);
        initView(mAddressItemView, R.string.title_address, R.string.unComplete, true);

        if (Build.VERSION.SDK_INT < 16) {
            mRecordItemView.setVisibility(View.GONE);
        }

        mPhotoItemView.setDynamicRedPointStatus(false);
        mRecordItemView.setDynamicRedPointStatus(false);
    }

    /**
     * 初始化单个Item
     *
     * @param itemView     操作的item
     * @param titleResId   item的标题
     * @param contentResId item的内容
     * @param isNotLine    是否有下面的横线
     */
    private void initView(MasterItemView itemView, int titleResId, int contentResId, boolean isNotLine) {
        itemView.setText(titleResId);
        itemView.setRightTextWithArrowText(contentResId);
        // 是否有横线
        if (isNotLine)
            itemView.setLineVisible(View.GONE);
        itemView.setOnMasterItemClickListener(this);
    }

    /**
     * 获取userInfo和coachInfo
     */
    private void initInfo() {
        onInitContents(mViewModel.getgUser(), mViewModel.getCoach());
    }

    private void onInitContents(User user, Coach coach) {
        mPhotoItemView.setRightCImageWithoutText(user.getHeadimgurl());
        mNicknameItemView.setRightText(user.getNickname());

        // 预加载两张图片
        ViewUtils.preLoadImage(coach.getQrCode());
        // 判断教龄
        if (!"0001-01-01".equals(coach.getCoachingDate())) {
            mTeachAgeItemView.setRightText(TimeUtil.getAge(coach.getCoachingDate()));
        }

        // 判断教学宣言
        if (!TextUtils.isEmpty(coach.getTeachingDeclaration())) {
            mRecordItemView.setRightImageWithoutText("drawable://" + R.drawable.ic_user_vol);
        }
        initRightText(mContactItemView, user.getPhone(), coach.getQrCode());
        initRightText(mAddressItemView, coach.getDrivingSchool(), coach.getTestSite(), coach.getTrainingSite());
    }

    /**
     * 哈哈，这个方法我很满意！！！
     * <p>
     * 根据资料，判断item应该显示的内容
     * 1.该部分全填满了，则右侧不显示
     * 2.该部分一个都没填写，则右侧显示默认的未设置
     * 3.该部分只填写了某些，则右侧显示未完善
     */
    private void initRightText(MasterItemView itemView, String... detectStr) {
        int detect = 0;
        for (String content : detectStr) {
            if (!TextUtils.isEmpty(content)) {
                detect++;
            }
        }
        if (detect == detectStr.length) {
            itemView.setRightText("");
        } else if (detect != 0) {
            itemView.setRightText(getString(R.string.unComplete));
        }
    }

    @Override
    public void onMasterItemClick(View v) {
        ViewUtils.setDelayedClickable(v, 500);
        switch (v.getId()) {
            case R.id.item_user_nickname:
                String realStr = mViewModel.gUser.getNickname();
                UserReviseActivity.launch(this,
                        UserReviseModel.KEY_NAME,
                        realStr);
                break;
            case R.id.item_user_teachAge:
                teachYearClick();
                break;
            case R.id.item_user_contact:
                UserInfoContactActivity.launch(this);
                break;
            case R.id.item_user_address:
                UserInfoAddressActivity.launch(this);
                break;
            case R.id.item_user_record:
                RecordActivity.launch(this, mViewModel.gCoach.getTeachingDeclaration());
                break;
            case R.id.item_user_photo:
                showPhotoDialog();
                break;
        }
    }

    private void showPhotoDialog() {
        PictureUtil.FindPhoto(this);
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

    private void teachYearClick() {
        YearPopup popupWheel = new YearPopup(this, 1970, 2017);
        popupWheel.setWheelLoop(false).setNoticeText("请选择执教年份");
        popupWheel.show(new OnPopupListener() {
            @Override
            public void ok(String clickStr) {
                // 成功后，自己手动保存
                String time = clickStr.substring(0, 4) + "-01-01";
                if (time.equals(mViewModel.gCoach.getCoachingDate())) {

                } else {
                    mTeachAgeItemView.setRightText(TimeUtil.getAge(clickStr));
                    putCoachInfo("coachingDate", time);
                }
            }

            @Override
            public void cancel() {

            }
        });
        // 对界面进行设置
        TextView textView = popupWheel.getNoticeTv();
        textView.setTextSize(18);
        textView.setTextColor(Color.rgb(46, 46, 46));
        int color = Color.rgb(208, 59, 59);
        popupWheel.getCancelTv().setTextColor(color);
        popupWheel.getOkTv().setTextColor(color);

        // 将年份转换成 position
        String year = mTeachAgeItemView.getRightText();
        if ("未设置".equals(year)) {
            popupWheel.setInitPosition(46);
        } else {
            popupWheel.setInitPosition(47 - Integer.parseInt(year.substring(0, year.length() - 1)));
        }
    }

    /**
     * 修改教练信息
     *
     * @param key
     * @param value
     */
    private void putCoachInfo(String key, String value) {
        mProgressDialog = DialogHelper.create(DialogHelper.TYPE_PROGRESS)
                .progressText(getString(R.string.dialog_revising)).show();
        Subscription subscription = mViewModel.putCoachInfo(key, value)
                .subscribe(this::onCoachNext, this::onError);
        addSubscription(subscription);
    }

    private void onCoachNext(Coach coach) {
        dismissProgressDialog();
        // 发送事件给【我的】，告知修改内容
        RxBusManager.post(EventConstant.KEY_REVISE_USERINFO, "");
    }

    /**
     * 在请求之前先改变内容
     * 通过key的值来判断 修改哪个item 。
     *
     * @param value
     */
    private void reviseItemContent(String value) {
        key2Item(mViewModel.httpKey).setRightText(value);
    }

    /**
     * 如果请求失败，则内容改变为之前的
     * 通过key的值来判断 修改哪个item 。
     */
    private void returnItemContent() {
        key2Item(mViewModel.httpKey).setRightText(TimeUtil.getAge(mViewModel.gCoach.getCoachingDate()));
    }

    private void onError(Throwable e) {
        dismissProgressDialog();
        returnItemContent();
        if (null != e) {
            if (e instanceof NetworkError) {
                NetworkError error = (NetworkError) e;
                UIHelper.shortToast(error.getErrorCode().getMessage());
            } else {
                UIHelper.shortToast(R.string.http_error);
                e.printStackTrace();
            }
        }
    }

    //////////////////////////////////////////////////////////
    // RxBus 处理
    private void initRxBus() {
        // 1. 昵称的变化
        RxBusManager.register(this, EventConstant.KEY_REVISE_NICKNAME, String.class)
                .subscribe(str -> {
                    mViewModel.refreshUser();
                    mNicknameItemView.setRightText(str);
                }, Logger::e);
        // 2. 考场的变化
        RxBusManager.register(this, EventConstant.KEY_REVISE_TESTSITE, String.class)
                .subscribe(str -> refreshAddress(), Logger::e);
        // 3. 练车场的变化
        RxBusManager.register(this, EventConstant.KEY_REVISE_TRAINSITE, String.class)
                .subscribe(str -> refreshAddress(), Logger::e);
        // 4. 驾校的变化
        RxBusManager.register(this, EventConstant.KEY_REVISE_DRISCHOOL, String.class)
                .subscribe(str -> refreshAddress(), Logger::e);
        // 5. 手机号变化，更新联系方式的显示
        RxBusManager.register(this, EventConstant.KEY_REVISE_PHONE, String.class)
                .subscribe(phone -> {
                    mViewModel.gUser.setPhone(phone);
                    initRightText(mContactItemView, mViewModel.gCoach.getQrCode(),
                            mViewModel.gUser.getPhone());
                }, Logger::e);
        // 6. 微信二维码发生变化
        RxBusManager.register(this, EventConstant.KEY_REVISE_QRCODE, String.class)
                .subscribe(__ -> {
                    mViewModel.refreshCoach();
                    initRightText(mContactItemView, mViewModel.gCoach.getQrCode(),
                            mViewModel.gUser.getPhone());
                }, Logger::e);
        // 7. 当上传了录音
        RxBusManager.register(this, EventConstant.KEY_REVISE_RECORD, String.class)
                .subscribe(recordUrl -> {
                    mViewModel.gCoach.setTeachingDeclaration(recordUrl);
                    mRecordItemView.setRightImageWithoutText("drawable://" + R.drawable.ic_user_vol);
                }, Logger::e);
    }

    private void refreshAddress() {
        mViewModel.refreshCoach();
        initRightText(mAddressItemView, mViewModel.gCoach.getDrivingSchool(),
                mViewModel.gCoach.getTestSite(), mViewModel.gCoach.getTrainingSite());
    }

    ////////////////////////////////////////////
    // 头像上传

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
                .subscribe(__ -> isFileExit(), this::onAvatarError);
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
        Subscription subscription = mViewModel.putUserInfo()
                .subscribe(__ -> onUpLoadSuccess(), this::onAvatarError);
        addSubscription(subscription);
    }

    /**
     * 最终的，上传成功后，的处理
     */
    private void onUpLoadSuccess() {
        dismissProgressDialog();
        RxBusManager.post(EventConstant.KEY_REVISE_USERINFO, "");
        UIHelper.shortToast(R.string.upload_success);
    }

    private void onAvatarError(Throwable e) {
        dismissProgressDialog();
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
//        super.onActivityResult(requestCode, resultCode, data);
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
                        mViewModel.mQiNiuFilePath = PictureUtil.getPath(UserInfoActivity.this, PictureUtil.imgUri);
                        mPhotoItemView.setRightCImageWithoutText("file://" + mViewModel.mQiNiuFilePath);
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

    /**
     * 通过 key  来判断是哪个 item
     *
     * @return 返回对应的 item
     */
    private MasterItemView key2Item(String key) {
        if ("headimgurl".equals(key)) {
            return mPhotoItemView;
        } else if ("nickname".equals(key)) {
            return mNicknameItemView;
        } else {
            return mTeachAgeItemView;
        }
    }
}
