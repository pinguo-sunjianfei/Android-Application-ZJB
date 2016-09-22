package com.idrv.coach.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.idrv.coach.R;
import com.idrv.coach.bean.WebParamBuilder;
import com.idrv.coach.data.constants.SchemeConstant;
import com.idrv.coach.ui.ToolBoxWebActivity;
import com.idrv.coach.utils.helper.UIHelper;

/**
 * time:2016/8/8
 * description: 处理scheme的工具类
 *
 * @author sunjianfei
 */
public class SchemeUtils {


    public static void schemeJump(Context context, String str) {
        try {
            Uri uri = Uri.parse(str);
            String scheme = uri.getScheme();

            if (SchemeConstant.KEY_APP.equals(scheme)) {
                //如果是本地跳转
                Intent intent = new Intent(SchemeConstant.INTENT_ACTION, uri);
                context.startActivity(intent);
            } else if (SchemeConstant.KEY_HTTP.equals(scheme) || SchemeConstant.KEY_HTTPS.equals(scheme)) {
                //如果是H5
                ToolBoxWebActivity.launch(context, WebParamBuilder.create()
                        .setUrl(str));
            } else if (SchemeConstant.KEY_TEL.equals(scheme)) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(uri);
                context.startActivity(intent);
            } else if (SchemeConstant.KEY_SMS.equals(scheme)) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(uri);
                context.startActivity(intent);
            } else if (SchemeConstant.KEY_QQ.equals(scheme)) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                context.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            UIHelper.shortToast(R.string.scheme_error);
        }

    }
}
