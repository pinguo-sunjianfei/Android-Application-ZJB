package com.idrv.coach.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.idrv.coach.R;
import com.idrv.coach.bean.event.EventConstant;
import com.idrv.coach.data.manager.RxBusManager;
import com.idrv.coach.data.manager.WChatManager;
import com.idrv.coach.utils.Logger;
import com.idrv.coach.utils.helper.ResHelper;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

/**
 * time:2016/5/5
 * description:
 *
 * @author sunjianfei
 */
public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
    private static final String TAG = WXPayEntryActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        WChatManager.getInstance().handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                Logger.e(TAG, ResHelper.getString(R.string.weixin_pay_success));
                RxBusManager.post(EventConstant.KEY_PAY_RESULT, BaseResp.ErrCode.ERR_OK);
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                Logger.e(TAG, ResHelper.getString(R.string.weixin_pay_cancel));
                RxBusManager.post(EventConstant.KEY_PAY_RESULT, BaseResp.ErrCode.ERR_USER_CANCEL);
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                Logger.e(TAG, ResHelper.getString(R.string.weixin_pay_fail));
                RxBusManager.post(EventConstant.KEY_PAY_RESULT, BaseResp.ErrCode.ERR_AUTH_DENIED);
                break;
            default:
                break;
        }
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        WChatManager.getInstance().handleIntent(intent, this);
    }
}
