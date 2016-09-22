package com.idrv.coach.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.idrv.coach.R;
import com.idrv.coach.ZjbApplication;
import com.idrv.coach.bean.User;
import com.idrv.coach.data.constants.SPConstant;
import com.idrv.coach.data.db.DBService;
import com.idrv.coach.data.manager.AppInitManager;
import com.idrv.coach.data.manager.LoginManager;
import com.idrv.coach.data.model.SplashModel;
import com.idrv.coach.utils.PreferenceUtil;
import com.idrv.coach.utils.handler.WeakHandler;
import com.idrv.coach.utils.helper.ResHelper;
import com.zjb.loader.ZjbImageLoader;
import com.zjb.volley.utils.GsonUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by sunjianfei on 2016/3/7.
 * 闪屏页面
 */
public class SplashActivity extends BaseActivity<SplashModel> {
    @InjectView(R.id.splash_image)
    FrameLayout mSplashImage;

    WeakHandler mHandler;
    boolean mIsLoginValidate;
    long mExecuteTime;

    private static final long MS_DURATION = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_splash);
        ButterKnife.inject(this);
        //1.解决安装后直接打开，home键切换到后台再启动重复出现闪屏页面的问题
        // http://stackoverflow.com/questions/2280361/app-always-starts-fresh-from-root-activity-instead-of-resuming-background-state
        if (!isTaskRoot()) {
            if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
                finish();
                return;
            }
        }
        //2.初始化
        initialize(this);
        //3.显示广告图片
        initView();
        //4.执行闪屏alpha动画
        animateSplash();
        //5.跳转
        jump();
    }

    @Override
    protected void onLazyLoad() {
        mViewModel = new SplashModel();
        mViewModel.getSplashData();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //闪屏页面屏蔽掉返回按钮事件
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected boolean isToolbarEnable() {
        return false;
    }

    @Override
    public boolean isSwipeBackEnabled() {
        return false;
    }

    private void initView() {
        String splashPic = PreferenceUtil.getString(SPConstant.KEY_SPLASH_PIC);
        if (TextUtils.isEmpty(splashPic)) {
            mSplashImage.setBackgroundResource(R.drawable.splash_default_bg);
        } else {
            String filePath = ZjbImageLoader.getQiniuDiskCachePath(splashPic);
            if (!TextUtils.isEmpty(filePath)) {
                ZjbImageLoader.create(splashPic)
                        .into(mSplashImage);
            } else {
                mSplashImage.setBackgroundResource(R.drawable.splash_default_bg);
            }
        }
    }

    private void initialize(Context context) {
        //1.防止application没有初始化完毕,再次初始化
        AppInitManager.getInstance().initializeApp(context);
        //2.处理初始化
        mHandler = new WeakHandler();
        //3.创建快捷方式
        if (!LoginManager.getInstance().isNotFirstUse()) {
            shortcut(this);
        }
        //4. 初始化数据库
        mExecuteTime = System.currentTimeMillis();
        mIsLoginValidate = isLoginValidate();
        if (mIsLoginValidate) {
            //初始化数据库
            DBService.init(ZjbApplication.gContext);
        }
    }

    /**
     * 在桌面创建快捷方式
     *
     * @param context 应用上下文
     */
    private void shortcut(Context context) {
        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        shortcut.putExtra("duplicate", false);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, ResHelper.getString(R.string.app_name));
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(context, R.mipmap.ic_app));
        Intent intent = new Intent();
        intent.setClass(context.getApplicationContext(), SplashActivity.class);
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        context.sendBroadcast(shortcut);
    }

    /**
     * 判断是否登录有效
     *
     * @return
     */
    private boolean isLoginValidate() {
        long tokenValidateTime = PreferenceUtil.getLong(SPConstant.KEY_USER_TOKEN_TIME);
        boolean isValid = System.currentTimeMillis() - tokenValidateTime < 2505600000L
                && LoginManager.getInstance().isLoginValidate();
        PreferenceUtil.putLong(SPConstant.KEY_USER_TOKEN_TIME, System.currentTimeMillis());
        return isValid;
    }

    /**
     * 闪屏页的alpha动画
     */
    private void animateSplash() {
        Animation alphaAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_alpha_anim);
        mSplashImage.startAnimation(alphaAnimation);
    }

    /**
     * 跳转
     */
    private void jump() {
        long delay = MS_DURATION - (System.currentTimeMillis() - mExecuteTime);
        delay = delay > 0 ? delay : 0;
        if (mIsLoginValidate) {
            if (LoginManager.getInstance().isBindPhone()) {
                mHandler.postDelayed(this::toMainPage, delay);
            } else {
                mHandler.postDelayed(this::toBind, delay);
            }
        } else {
            if (LoginManager.getInstance().isNotFirstUse()) {
                mHandler.postDelayed(this::toLogin, delay);
            } else {
                mHandler.postDelayed(this::toGuide, delay);
            }
        }
    }

    /**
     * 跳转到主界面
     */
    private void toMainPage() {
        Intent intent = getIntent();
        if (null != intent) {
            Uri uri = getIntent().getData();
            if (null == uri) {
                MainActivity.launch(this);
            } else {
                MainActivity.launch(this, uri);
            }
        }
        finish();
    }

    /**
     * 跳转到登录界面
     */
    private void toLogin() {
        LoginActivity.launch(this);
        finish();
    }

    /**
     * 跳转到引导界面
     */
    private void toGuide() {
        GuideActivity.launch(this);
        finish();
    }

    /**
     * 跳转到邀请码界面
     */
    private void toBind() {
        BindPhoneActivity.launch(this);
        finish();
    }

    private void cheat() {
        /*********模拟假数据开始**********/
        User user = User.getFakeUser();
        PreferenceUtil.putString(SPConstant.KEY_USER, GsonUtil.toJson(user));
        PreferenceUtil.putLong(SPConstant.KEY_USER_TOKEN_TIME, System.currentTimeMillis());
        AppInitManager.getInstance().updateAfterLogin(user.getUid(), user.getToken());
        PreferenceUtil.putBoolean(SPConstant.KEY_FIRST_USE, true);
        PreferenceUtil.putBoolean(DynamicActivity.KEY_FIRST_USE_DYNAMIC, true);
        PreferenceUtil.putBoolean(NewsHallActivity.KEY_FIRST_USE_NEWS, true);
        PreferenceUtil.putBoolean(BusinessHallActivity.KEY_FIRST_USE_BUSINESS, true);
        /***********模拟假数据结束*****/
    }
}
