package com.idrv.coach.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;

import com.idrv.coach.data.constants.SchemeConstant;
import com.idrv.coach.data.manager.LoginManager;

/**
 * time:2016/3/24
 * description:
 *
 * @author sunjianfei
 */
public class SchemeActivity extends Activity {

    public static void launch(Context context, Uri data) {
        Intent intent = new Intent(context, SchemeActivity.class);
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
        Intent intent = getIntent();
        Uri uri = intent.getData();
        //获取定义的跳转路径
        String path = uri.getPath();
        jump(path);
    }

    /**
     * 跳转
     *
     * @param path
     */
    private void jump(String path) {
        boolean isLoginValid = LoginManager.getInstance().isLoginValidate();
        if (!isLoginValid) {
            LoginActivity.launch(this);
        } else {
            switch (path) {
                case SchemeConstant.PATH_NEWS:
                    NewsHallActivity.launch(this);
                    break;
                case SchemeConstant.PATH_DISCOVER:
                    MainActivity.launch(this);
                    break;
                case SchemeConstant.PATH_DYNAMIC:
                    DynamicActivity.launch(this);
                    break;
                case SchemeConstant.DEFAULT:
                    MainActivity.launch(this);
                    break;
            }
        }
        finish();
    }
}
