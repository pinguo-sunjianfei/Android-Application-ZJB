package com.idrv.coach.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.idrv.coach.R;
import com.idrv.coach.bean.MainFragment;
import com.idrv.coach.bean.event.EventConstant;
import com.idrv.coach.data.db.DBOpenHelper;
import com.idrv.coach.data.manager.AppInitManager;
import com.idrv.coach.data.manager.AppUpdateManager;
import com.idrv.coach.data.manager.LoginManager;
import com.idrv.coach.data.manager.ReportLoginManager;
import com.idrv.coach.data.manager.RxBusManager;
import com.idrv.coach.data.manager.UrlParserManager;
import com.idrv.coach.ui.adapter.MainViewPagerAdapter;
import com.idrv.coach.ui.adapter.TabSelectedAdapter;
import com.idrv.coach.utils.Logger;
import com.idrv.coach.utils.helper.DialogHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by sunjianfei on 2016/3/7.
 * 主导航页面
 */
public class MainActivity extends BaseActivity {
    @InjectView(R.id.navigate_view_pager)
    ViewPager mViewPager;
    @InjectView(R.id.navigate_tab_layout)
    TabLayout mTabLayout;

    private TabLayout.OnTabSelectedListener mOnTabSelectedListener;
    private MainViewPagerAdapter mViewPagerAdapter;
    //用于标识是否弹出token过期的对话框
    private boolean flag = false;

    {
        this.mOnTabSelectedListener = new TabSelectedAdapter() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition(), true);
            }
        };
    }

    public static void launch(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    public static void launch(Context context, Uri data) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setData(data);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        schemaJump();
        setContentView(R.layout.act_main);
        ButterKnife.inject(this);
        //1.再次防止push跳转没有被初始化
        AppInitManager.getInstance().initializeApp(this);
        //2.初始化View
        initView();
        //3.上报登录
        ReportLoginManager.getInstance().report();
        //注册token过期事件
        registerEvent();
        //版本更新
        AppUpdateManager.newInstance().checkUpdate(false);
    }

    @Override
    protected boolean isToolbarEnable() {
        return false;
    }

    @Override
    public boolean isSwipeBackEnabled() {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }

    private void registerEvent() {
        RxBusManager.register(this, EventConstant.KEY_TOKEN_EXPIRED, String.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(__ -> showTokenExpiredDialog(), Logger::e);
    }

    private void initView() {
        //1.得到LayoutInflater
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        //2.得到要显示的数据
        MainFragment[] fragments = MainFragment.values();
        //3.得到适配器
        mViewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager());
        //4.遍历数据，进行显示
        for (int i = 0; i < fragments.length; i++) {
            TextView view = (TextView) inflater.inflate(R.layout.vw_tab_item, null, false);
            view.setText(fragments[i].getTitle());
            view.setCompoundDrawablesWithIntrinsicBounds(0, fragments[i].getIconResId(), 0, 0);
            mTabLayout.addTab(mTabLayout.newTab().setCustomView(view));
            mViewPagerAdapter.addFragment(fragments[i].getFragment());
        }
        //5.设置适配器
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);
        //6.viewpager与TabLayout联动
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setOnTabSelectedListener(mOnTabSelectedListener);
    }

    /**
     * 处理推送跳转
     */
    private void schemaJump() {
        Intent intent = getIntent();
        if (null != intent) {
            Uri data = intent.getData();
            if (null != data) {
                SchemeActivity.launch(this, data);
            }
        }
    }

    private void showTokenExpiredDialog() {
        if (!flag) {
            flag = true;
            DialogHelper.create(DialogHelper.TYPE_NORMAL)
                    .cancelable(false)
                    .canceledOnTouchOutside(false)
                    .title(getString(R.string.error))
                    .content(getString(R.string.token_expired))
                    .bottomButton(getString(R.string.sure), getResources().getColor(R.color.themes_main))
                    .bottomBtnClickListener((dialog, view) -> {
                        logout();
                        dialog.dismiss();
                    }).show();
        }
    }

    private void logout() {
        // 清空LoginManager
        LoginManager.getInstance().logout();
        AppInitManager.getInstance().updateBeforeLogin();
        DBOpenHelper.resetInstance();
        UrlParserManager.getInstance().release();
        // 切换界面
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
