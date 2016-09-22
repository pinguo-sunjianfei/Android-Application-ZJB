package com.idrv.coach.data.model;

import com.idrv.coach.bean.BusinessHall;
import com.idrv.coach.bean.BusinessHallPage;
import com.idrv.coach.data.constants.ApiConstant;
import com.idrv.coach.utils.ValidateUtil;
import com.zjb.volley.core.request.HttpGsonRequest;
import com.zjb.volley.core.request.Request;
import com.zjb.volley.core.request.RequestBuilder;
import com.zjb.volley.core.response.HttpResponse;

import java.util.Iterator;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static com.idrv.coach.data.pool.RequestPool.gRequestPool;

/**
 * time:2016/3/17
 * description:
 *
 * @author sunjianfei
 */
public class BusinessHallModel extends BaseModel {

    /**
     * 获取任务大厅数据
     *
     * @return
     */
    public Observable<BusinessHallPage> getBusinessHall() {
        //1.创建Request
        HttpGsonRequest<BusinessHallPage> mRefreshRequest = RequestBuilder.create(BusinessHallPage.class)
                .requestMethod(Request.Method.POST)
                .url(ApiConstant.API_BUSINESS_HALL)
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp && null != resp.data)
                .map(HttpResponse::getData)
                .doOnNext(page -> buildResultData(page.getBoxes()))
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void buildResultData(List<BusinessHall> businessHalls) {
        if (ValidateUtil.isValidate(businessHalls)) {
            for (Iterator<BusinessHall> iterator = businessHalls.iterator(); iterator.hasNext(); ) {
                BusinessHall businessHall = iterator.next();
                //如果开关没有打开,则移除数据集
                if (!businessHall.isToggle()) {
                    iterator.remove();
                }
            }
            //数据至少有9条,没有的话就放假数据
            int num = 9 - businessHalls.size();
            if (num > 0) {
                for (int i = 0; i < num; i++) {
                    BusinessHall hall = new BusinessHall();
                    hall.setIsFake(true);
                    businessHalls.add(hall);
                }
            }
        }
    }
}
