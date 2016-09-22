package com.idrv.coach.bean.event;

import com.tencent.mm.sdk.modelmsg.SendAuth;

/**
 * Created by sunjianfei on 15-9-23.
 * description:微信的事件回调
 *
 * @author crab
 */
public class WeiXinEvent {
    public static class LoginEvent extends BaseEvent<SendAuth.Resp> {
    }
}
