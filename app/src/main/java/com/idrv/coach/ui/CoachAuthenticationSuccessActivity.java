package com.idrv.coach.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.idrv.coach.R;

/**
 * time:2016/8/19
 * description:教练认证成功页面
 *
 * @author sunjianfei
 */
public class CoachAuthenticationSuccessActivity extends BaseActivity {

    public static void launch(Context context) {
        Intent intent = new Intent(context, CoachAuthenticationSuccessActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_auth_success);
        initToolBar();
    }

    private void initToolBar() {
        mToolbarLayout.setTitle(R.string.coach_auth_success);
    }
}
