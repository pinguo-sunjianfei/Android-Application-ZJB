package com.idrv.coach.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.idrv.coach.bean.WebParamBuilder;
import com.idrv.coach.bean.event.EventConstant;
import com.idrv.coach.data.manager.RxBusManager;
import com.idrv.coach.data.model.ToolBoxModel;
import com.idrv.coach.utils.Logger;

import rx.Subscription;

/**
 * time:2016/3/18
 * description:
 *
 * @author sunjianfei
 */
public class ToolBoxWebActivity extends BaseWebActivity<ToolBoxModel> {

    public static void launch(Context context, WebParamBuilder builder) {
        Intent intent = new Intent(context, ToolBoxWebActivity.class);
        intent.putExtra(KEY_WEB_PARAM, builder);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ToolBoxModel();
        if (mParamBuilder.isNeedShareCallBack()) {
            registerEvent();
        }
    }

    @Override
    public WebParamBuilder getParams() {
        return getIntent().getParcelableExtra(KEY_WEB_PARAM);
    }

    private void registerEvent() {
        RxBusManager.register(this, EventConstant.KEY_SHARE_COMPLETE, String.class)
                .subscribe(__ -> shareComplete(), Logger::e);
    }

    /**
     * 分享完成之后的服务器回调
     */
    private void shareComplete() {
        Subscription subscription = mViewModel.shareComplete()
                .subscribe(Logger::e, Logger::e);
        addSubscription(subscription);
    }
}
