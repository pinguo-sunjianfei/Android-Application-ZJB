package com.idrv.coach.bean.share;

import android.os.Parcelable;

/**
 * time: 15/6/11
 * description: 封装了创建分享实体的接口
 *
 * @author sunjianfei
 */
public interface IShareProvider extends Parcelable {
    int SHARE_CONTENT_TYPE_IMAGE = 0;

    int SHARE_CONTENT_TYPE_WEB = 1;

    ShareBean createSinaShareBean();

    ShareBean createWeixinShareBean();

    ShareBean createTimelineShareBean();

    ShareBean createQzoneShareBean();

    int getShareType();
}
