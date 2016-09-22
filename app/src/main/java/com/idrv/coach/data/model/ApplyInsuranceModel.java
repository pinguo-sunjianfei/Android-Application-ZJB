package com.idrv.coach.data.model;

import android.text.TextUtils;

import com.google.gson.JsonSyntaxException;
import com.idrv.coach.bean.CarInsuranceInfo;
import com.idrv.coach.bean.User;
import com.idrv.coach.bean.event.EventConstant;
import com.idrv.coach.data.constants.ApiConstant;
import com.idrv.coach.data.constants.SPConstant;
import com.idrv.coach.data.manager.RxBusManager;
import com.idrv.coach.data.pool.RequestPool;
import com.idrv.coach.utils.BitmapUtil;
import com.idrv.coach.utils.EncryptUtil;
import com.idrv.coach.utils.Logger;
import com.idrv.coach.utils.PreferenceUtil;
import com.idrv.coach.utils.ValidateUtil;
import com.zjb.volley.core.request.HttpGsonRequest;
import com.zjb.volley.core.request.Request;
import com.zjb.volley.core.request.RequestBuilder;
import com.zjb.volley.utils.GsonUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * time:2016/3/21
 * description:
 *
 * @author sunjianfei
 */
public class ApplyInsuranceModel extends BaseModel {
    private PhotoType mPhotoType;
    private InsuranceType mInsuranceType;
    private CarInsuranceInfo info;
    boolean isResize = false;
    List<String> urls = new ArrayList<>();
    List<String> paths;

    public ApplyInsuranceModel() {
        info = new CarInsuranceInfo();
        mInsuranceType = InsuranceType.NEW_CAR;
    }

    public enum PhotoType {
        //身份证正面
        PHOTO_CARD_POSITIVE,
        //身份证反面
        PHOTO_CARD_NEGATIVE,
        //行驶证正面
        DRIVING_LICENSE_FIRST_PAGE,
        //行驶证反面
        DRIVING_LICENSE_SECOND_PAGE,
        //购车发票
        CAR_INVOICE,
        //车辆合格证
        CAR_CERTIFICATE
    }

    public enum InsuranceType {
        NEW_CAR,
        OLD_CAR
    }

    public void setPhotoType(PhotoType photoType) {
        this.mPhotoType = photoType;
    }

    public PhotoType getPhotoType() {
        return mPhotoType;
    }

    public InsuranceType getInsuranceType() {
        return mInsuranceType;
    }

    public void setInsuranceType(InsuranceType mInsuranceType) {
        this.mInsuranceType = mInsuranceType;
    }

    /**
     * 设置对应的图片
     *
     * @param path
     */
    public void setImagePath(String path) {
        switch (mPhotoType) {
            case PHOTO_CARD_POSITIVE:
                info.setCardPositivePath(path);
                break;
            case PHOTO_CARD_NEGATIVE:
                info.setCardNegativePath(path);
                break;
            case CAR_INVOICE:
            case DRIVING_LICENSE_FIRST_PAGE:
                if (mInsuranceType == InsuranceType.NEW_CAR) {
                    info.setInvoicePath(path);
                } else {
                    info.setDrivingLicenseFirstPagePath(path);
                }
                break;
            case CAR_CERTIFICATE:
            case DRIVING_LICENSE_SECOND_PAGE:
                if (mInsuranceType == InsuranceType.NEW_CAR) {
                    info.setCarCertificatePath(path);
                } else {
                    info.setDrivingLicenseSecondPagePath(path);
                }
                break;
        }
    }

    /**
     * 判断证件是否选择完
     *
     * @return
     */
    public boolean checkValidPhoto() {
        return info.isDocumentSelectComplete(mInsuranceType);
    }

    public CarInsuranceInfo getInfo() {
        return info;
    }

    /**
     * 七牛上传图片
     *
     * @param filePath
     */
    private void upLoadPhoto(String filePath) {
        String eTag = EncryptUtil.getQETAG(filePath);
        if (TextUtils.isEmpty(eTag)) {
            eTag = EncryptUtil.md5(filePath);
        }
        String newETag = eTag;
        photoUploadAuth(eTag)
                .doOnNext(auth -> urls.add(auth.getUrl()))
                .switchMap(auth -> fileUpload(newETag, filePath, auth.getToken()))
                .subscribe(this::onSingleUploadNext,
                        e -> RxBusManager.post(EventConstant.KEY_FILE_UPLOAD_FAILED, ""));
    }

    private void onSingleUploadNext(String s) {
        if (ValidateUtil.isValidate(paths)) {
            upLoadPhoto(paths.remove(0));
        } else {
            RxBusManager.post(EventConstant.KEY_FILE_UPLOAD_SUCCESS, "");
        }
    }

    public boolean checkUserInfoInValid() {
        String userJson = PreferenceUtil.getString(SPConstant.KEY_USER);
        User mUser;
        try {
            if (!TextUtils.isEmpty(userJson)) {
                mUser = GsonUtil.fromJson(userJson, User.class);

                String tel = mUser.getPhone();

                if (!TextUtils.isEmpty(tel)) {
                    return false;
                }
            } else {
                return true;
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void bulkUpload() {
        paths = info.toPathList(mInsuranceType);
        urls.clear();
        Observable<String> observable = Observable.<String>create(subscriber -> {
            if (!isResize) {
                for (int i = 0; i < paths.size(); i++) {
                    String filePath = paths.get(i);
                    String newPath = null;
                    try {
                        newPath = BitmapUtil.saveBitmap(BitmapUtil
                                .decodeSampledBitmapFromFile(filePath, 500, 500));
                        newPath = TextUtils.isEmpty(newPath) ? filePath : newPath;
                    } catch (IOException e) {
                        e.printStackTrace();
                        newPath = filePath;
                    }
                    switch (i) {
                        case 0:
                            info.setCardPositivePath(newPath);
                            break;
                        case 1:
                            info.setCardNegativePath(newPath);
                            break;
                        case 2:
                            if (mInsuranceType == InsuranceType.NEW_CAR) {
                                info.setInvoicePath(newPath);
                            } else {
                                info.setDrivingLicenseFirstPagePath(newPath);
                            }
                            break;
                        case 3:
                            if (mInsuranceType == InsuranceType.NEW_CAR) {
                                info.setCarCertificatePath(newPath);
                            } else {
                                info.setDrivingLicenseSecondPagePath(newPath);
                            }
                            break;
                    }
                }
            }
            isResize = true;
            paths.clear();
            paths = info.toPathList(mInsuranceType);
            subscriber.onNext("");
        }).subscribeOn(Schedulers.computation());
        observable.subscribe(__ -> upLoadPhoto(paths.remove(0)), Logger::e);
    }

    public Observable<String> postInsuranceInfo() {
        RequestBuilder builder = RequestBuilder.create(String.class)
                .requestMethod(Request.Method.POST)
                .url(ApiConstant.API_CAR_INSURANCE)
                .put("name", info.getName())
                .put("phone", info.getTelNum())
                .put("idCard", info.getCardId())
                .put("type", mInsuranceType == InsuranceType.NEW_CAR ? 1 : 2);
        if (mInsuranceType == InsuranceType.NEW_CAR) {
            builder.put("idCardA", urls.get(0))
                    .put("idCardB", urls.get(1))
                    .put("invoice", urls.get(2))
                    .put("carCertificate", urls.get(3));
        } else {
            builder.put("idCardA", urls.get(0))
                    .put("idCardB", urls.get(1))
                    .put("vehicleLicenseA", urls.get(2))
                    .put("vehicleLicenseB", urls.get(3));
        }

        HttpGsonRequest<String> request = builder.build();

        return RequestPool.gRequestPool.request(request)
                .filter(response -> response != null && response.data != null)
                .map(response -> response.data)
                .observeOn(AndroidSchedulers.mainThread());
    }
}
