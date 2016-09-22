package com.idrv.coach.bean;

import android.text.TextUtils;

import com.idrv.coach.data.model.ApplyInsuranceModel;

import java.util.ArrayList;
import java.util.List;

/**
 * time:2016/3/21
 * description:车险需要提交的信息
 *
 * @author sunjianfei
 */
public class CarInsuranceInfo {
    //车主姓名
    private String name;
    //电话号码
    private String telNum;
    //身份证号
    private String cardId;
    //身份证正面url
    private String cardPositivePath;
    //身份证反面url
    private String cardNegativePath;
    //行驶证第一页url
    private String drivingLicenseFirstPagePath;
    //行驶证第二页url
    private String drivingLicenseSecondPagePath;

    //购车发票
    private String invoicePath;
    //车辆合格证
    private String carCertificatePath;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelNum() {
        return telNum;
    }

    public void setTelNum(String telNum) {
        this.telNum = telNum;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getCardPositivePath() {
        return cardPositivePath;
    }

    public void setCardPositivePath(String cardPositivePath) {
        this.cardPositivePath = cardPositivePath;
    }

    public String getCardNegativePath() {
        return cardNegativePath;
    }

    public void setCardNegativePath(String cardNegativePath) {
        this.cardNegativePath = cardNegativePath;
    }

    public String getDrivingLicenseFirstPagePath() {
        return drivingLicenseFirstPagePath;
    }

    public void setDrivingLicenseFirstPagePath(String drivingLicenseFirstPagePath) {
        this.drivingLicenseFirstPagePath = drivingLicenseFirstPagePath;
    }

    public String getDrivingLicenseSecondPagePath() {
        return drivingLicenseSecondPagePath;
    }

    public void setDrivingLicenseSecondPagePath(String drivingLicenseSecondPagePath) {
        this.drivingLicenseSecondPagePath = drivingLicenseSecondPagePath;
    }

    public String getInvoicePath() {
        return invoicePath;
    }

    public void setInvoicePath(String invoicePath) {
        this.invoicePath = invoicePath;
    }

    public String getCarCertificatePath() {
        return carCertificatePath;
    }

    public void setCarCertificatePath(String carCertificatePath) {
        this.carCertificatePath = carCertificatePath;
    }

    public boolean isDocumentSelectComplete(ApplyInsuranceModel.InsuranceType type) {
        if (type == ApplyInsuranceModel.InsuranceType.OLD_CAR) {
            return !(TextUtils.isEmpty(cardPositivePath)
                    || TextUtils.isEmpty(cardNegativePath)
                    || TextUtils.isEmpty(drivingLicenseFirstPagePath)
                    || TextUtils.isEmpty(drivingLicenseSecondPagePath));
        } else {
            return !(TextUtils.isEmpty(cardPositivePath)
                    || TextUtils.isEmpty(cardNegativePath)
                    || TextUtils.isEmpty(carCertificatePath)
                    || TextUtils.isEmpty(invoicePath));
        }
    }

    public List<String> toPathList(ApplyInsuranceModel.InsuranceType type) {
        List<String> list = new ArrayList<>();
        if (type == ApplyInsuranceModel.InsuranceType.OLD_CAR) {
            list.add(cardPositivePath);
            list.add(cardNegativePath);
            list.add(drivingLicenseFirstPagePath);
            list.add(drivingLicenseSecondPagePath);
        } else {
            list.add(cardPositivePath);
            list.add(cardNegativePath);
            list.add(invoicePath);
            list.add(carCertificatePath);
        }
        return list;
    }
}
