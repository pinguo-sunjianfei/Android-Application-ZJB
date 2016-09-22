package com.idrv.coach.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.idrv.coach.BuildConfig;
import com.idrv.coach.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * time:2016/8/24
 * description:
 *
 * @author sunjianfei
 */
public class SettingAboutUsActivity extends BaseActivity {
    @InjectView(R.id.version_tv)
    TextView mVersionTv;

    public static void launch(Context context) {
        Intent intent = new Intent(context, SettingAboutUsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_about_us);
        ButterKnife.inject(this);
        initToolBar();
        initView();
    }

    private void initToolBar() {
        mToolbarLayout.setTitle(R.string.about_us);
    }

    private void initView() {
        mVersionTv.setText(getString(R.string.version, BuildConfig.VERSION_NAME));
    }
}
