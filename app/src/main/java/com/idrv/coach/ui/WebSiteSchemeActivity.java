package com.idrv.coach.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;

import com.idrv.coach.data.constants.SPConstant;
import com.idrv.coach.data.manager.LoginManager;
import com.idrv.coach.utils.PreferenceUtil;

/**
 * time:2016/8/22
 * description:
 *
 * @author sunjianfei
 */
public class WebSiteSchemeActivity extends Activity {

    public static void launch(Context context, Uri data) {
        Intent intent = new Intent(context, WebSiteSchemeActivity.class);
        intent.setData(data);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        init();
    }

    private void init() {
        if (isOpenWebSite()) {
            MyWebSiteActivity.launch(this);
        } else {
            CreateWebSiteActivity.launch(this);
        }
        finish();
    }

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
