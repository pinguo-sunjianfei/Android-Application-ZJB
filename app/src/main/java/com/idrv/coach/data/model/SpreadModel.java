package com.idrv.coach.data.model;

import android.text.TextUtils;

import com.idrv.coach.bean.Commission;
import com.idrv.coach.bean.PosterPage;
import com.idrv.coach.bean.SpreadTool;
import com.idrv.coach.bean.WxPay;
import com.idrv.coach.bean.WxPayInfo;
import com.idrv.coach.bean.parser.SparedToolParser;
import com.idrv.coach.data.constants.ApiConstant;
import com.idrv.coach.data.constants.SPConstant;
import com.idrv.coach.data.manager.AppInitManager;
import com.idrv.coach.data.manager.LoginManager;
import com.idrv.coach.data.manager.WChatManager;
import com.idrv.coach.utils.PreferenceUtil;
import com.idrv.coach.utils.ValidateUtil;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.zjb.volley.core.request.HttpGsonRequest;
import com.zjb.volley.core.request.Request;
import com.zjb.volley.core.request.RequestBuilder;
import com.zjb.volley.core.response.HttpResponse;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;

import static com.idrv.coach.data.pool.RequestPool.gRequestPool;

/**
 * time:2016/6/27
 * description:传播工具
 *
 * @author sunjianfei
 */
public class SpreadModel {
    SpreadTool tool;
    long serverTime;
    WxPayInfo mWxPayInfo;
    Commission mCommission;
    private String sp;
    //默认传播工具
    private int toolType = 2;

    public void setToolType(int toolType) {
        this.toolType = toolType;
    }

    public SpreadTool getTool() {
        return tool;
    }

    public void setTool(SpreadTool tool) {
        this.tool = tool;
    }

    public long getServerTime() {
        return serverTime;
    }

    public boolean isEnoughCredit() {
        if (null == mCommission) {
            return false;
        }
        int price = tool.getCredit();
        if (mCommission.getCredit() >= price) {
            return true;
        }
        return false;
    }

    public boolean isEnoughMoney() {
        if (null == mCommission) {
            return false;
        }
        int price = tool.getPrice();
        float balance = getBalance();
        //如果余额大于等于价格
        if (balance >= price * 1.0f / 100) {
            return true;
        }
        return false;
    }

    /**
     * 更新支付状态
     *
     * @param payType
     */
    public void updatePayType(String payType) {
        tool.setPayType(payType);
    }

    private float getBalance() {
        String balanceStr = mCommission.getBalance();
        if (!TextUtils.isEmpty(balanceStr)) {
            return Float.valueOf(balanceStr);
        }
        return 0f;
    }

    /**
     * 工具列表下拉刷新
     *
     * @param clearAdapter
     * @return
     */
    public Observable<List<SpreadTool>> refresh(Action0 clearAdapter) {
        sp = "";
        return requestSpreadTool()
                .doOnNext(__ -> clearAdapter.call());
    }

    /**
     * 工具列表加载更多
     *
     * @return
     */
    public Observable<List<SpreadTool>> loadMore() {
        return requestSpreadTool();
    }

    /**
     * 获取传播工具
     *
     * @return
     */
    private Observable<List<SpreadTool>> requestSpreadTool() {
        //1.创建Request
        HttpGsonRequest<List<SpreadTool>> mRefreshRequest = RequestBuilder.<List<SpreadTool>>create()
                .parser(new SparedToolParser())
                .requestMethod(Request.Method.POST)
                .put("count", 10)
                .put("sp", sp)
                .url(ApiConstant.API_SPREAD_TOOLS)
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp && null != resp.data)
                .doOnNext(response -> serverTime = response.serverTime)
                .map(resp -> {
                    List<SpreadTool> tools = resp.getData();
                    sp = tools.get(tools.size() - 1).getStartTime();
                    return tools;
                })
                .filter(ValidateUtil::isValidate)
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取佣金
     *
     * @return
     */
    public Observable<Commission> getCommission() {
        //1.创建Request
        HttpGsonRequest<Commission> mRefreshRequest = RequestBuilder.create(Commission.class)
                .requestMethod(Request.Method.GET)
                .url(ApiConstant.API_COMMISSION)
                .putHeaders("Authorization", AppInitManager.getSdkEntity().getToken())
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter(resp -> null != resp && null != resp.data)
                .map(HttpResponse::getData)
                .doOnNext(commission -> mCommission = commission)
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取订单信息
     *
     * @return
     */
    public Observable<WxPayInfo> getOrder() {
        //1.创建Request
        HttpGsonRequest<WxPay> mRefreshRequest = RequestBuilder.create(WxPay.class)
                .requestMethod(Request.Method.POST)
                .url(ApiConstant.API_GET_ORDER)
                .put("gid", tool.getId())
                .put("type", toolType)
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp && null != resp.data)
                .map(resp -> {
                    WxPay pay = resp.data;
                    mWxPayInfo = pay.getM_values();
                    return mWxPayInfo;
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 佣金或者积分购买传播工具
     *
     * @param payType 1.余额购买；2-积分;3-会员免费使用
     * @return
     */
    public Observable<String> buySpreadTool(int payType) {
        //1.创建Request
        HttpGsonRequest<String> mRefreshRequest = RequestBuilder.create(String.class)
                .requestMethod(Request.Method.POST)
                .url(ApiConstant.API_BUY_GOODS)
                .put("goodsType", toolType)
                .put("gid", tool.getId())
                .put("payType", payType)
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp && null != resp.data)
                .map(HttpResponse::getData)
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取海报模板
     *
     * @return
     */
    public Observable<PosterPage> getPoster() {
        //1.创建Request
        HttpGsonRequest<PosterPage> mRefreshRequest = RequestBuilder.create(PosterPage.class)
                .requestMethod(Request.Method.POST)
                .url(ApiConstant.API_GET_POSTER)
                .put("transmissionToolId", tool.getId())
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp && null != resp.data)
                .map(HttpResponse::getData)
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 微信支付
     */
    public void wxPay() {
        PayReq req = new PayReq();
        req.appId = mWxPayInfo.getAppid();
        req.partnerId = mWxPayInfo.getPartnerid();
        req.prepayId = mWxPayInfo.getPrepayid();
        req.nonceStr = mWxPayInfo.getNoncestr();
        req.timeStamp = mWxPayInfo.getTimestamp();
        req.packageValue = "Sign=WXPay";
        req.sign = mWxPayInfo.getSign();
        WChatManager.getInstance().WXAPI.sendReq(req);
    }

    /**
     * 判断是否开通了个人网站
     *
     * @return
     */
    public boolean isOpenWebSite() {
        String result = PreferenceUtil.getString(SPConstant.KEY_IS_OPEN_WEBSITE + LoginManager.getInstance().getUid());
        if (!TextUtils.isEmpty(result)) {
            if (result.equals("true")) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
}
