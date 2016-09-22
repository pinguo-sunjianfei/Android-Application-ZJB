package com.idrv.coach.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.MenuRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;

import com.idrv.coach.R;
import com.idrv.coach.ZjbApplication;
import com.idrv.coach.data.manager.RxBusManager;
import com.idrv.coach.ui.fragment.BaseFragment;
import com.idrv.coach.ui.view.compat.StatusBarCompat;
import com.idrv.coach.ui.widget.BaseLayout;
import com.idrv.coach.ui.widget.SwipeBackLayout;
import com.idrv.coach.ui.widget.ToolbarLayout;
import com.idrv.coach.utils.SystemBarUtil;
import com.idrv.coach.utils.ViewLayoutUtil;
import com.idrv.coach.utils.handler.WeakHandler;
import com.idrv.coach.utils.helper.DialogHelper;
import com.idrv.coach.utils.helper.UIHelper;
import com.tendcloud.tenddata.TCAgent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * time: 15/6/9
 * description:activity的基础类，理论上所有的activity都必须继承自本activity
 *
 * @author sunjianfei
 */
public abstract class BaseActivity<ViewModel> extends AppCompatActivity implements BaseLayout.OnBaseLayoutClickListener,
        DialogInterface.OnCancelListener {

    protected BaseLayout mBaseLayout;

    protected ToolbarLayout mToolbarLayout;

    protected Dialog mProgressDialog;

    protected boolean mTranslucentStatusEnable = false;
    protected boolean mTranslucentNavigationEnable = false;

    protected boolean mDestroyed = false;

    protected ViewModel mViewModel;

    protected CompositeSubscription mCompositeSubscription;

    protected SwipeBackLayout mSwipeBackLayout;
    protected boolean mIsInitialized;
    protected PopupMenu mPopupMenu;
    protected int mMenuRes;
    protected boolean mIsPopupMenuShow;

    private BaseFragment mBaseFragment;

    protected WeakHandler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initWindowView();
        super.onCreate(savedInstanceState);
        mCompositeSubscription = new CompositeSubscription();
        //1.将背景设置为透明
        setOverflowShowingAlways();
        mDestroyed = false;
        ZjbApplication.addActivity(this);
        StatusBarCompat.compat(this, getStatusBarColor());
    }


    public void setBackground(int resId) {
        mToolbarLayout.setBackground(resId);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (mTranslucentStatusEnable) {
            translucentStatusBar();
        }

        if (mTranslucentNavigationEnable) {
            translucentNavigationBar();
        }

        mSwipeBackLayout.attachToActivity(this);
    }

    private void initWindowView() {
        Window window = getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            window.getDecorView().setBackground(null);
        } else {
            window.getDecorView().setBackgroundDrawable(null);
        }
    }

    /**
     * Set action bar overflow showing always, avoid it's not showing on some phones.
     */
    private void setOverflowShowingAlways() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            menuKeyField.setAccessible(true);
            menuKeyField.setBoolean(config, false);
        } catch (Exception e) {
            // ignore
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        addSwipeLayout(LayoutInflater.from(this).inflate(layoutResID, null));
    }

    @Override
    public void setContentView(View view) {
        addSwipeLayout(view);
    }

    private View buildToolbarLayout(View view) {
        if (isToolbarEnable()) {
            mToolbarLayout = new ToolbarLayout.Builder(this)
                    .setContentView(view)
                    .setOverlay(isToolbarOverlay())
                    .setShadow(isToolbarHasShadow())
                    .build();
            mToolbarLayout.setLeftIcon(R.drawable.back);
            mToolbarLayout.setOnClickListener(new ToolbarLayout.OnClickListener() {
                @Override
                public void onLeftClick(View view) {
                    onToolbarLeftClick(view);
                }

                @Override
                public void onCenterClick(View view) {
                    onToolbarTitleClick(view);
                }

                @Override
                public void onRightClick(View view) {
                    onToolbarRightClick(view);
                }
            });
            return mToolbarLayout;
        }
        return view;
    }

    private void addSwipeLayout(View view) {
        //1.将view转换为四层结构的baseLayout
        view = buildContentView(view);
        //2.将view转换成带标题栏的view
        view = buildToolbarLayout(view);
        //3.加上swipeBack
        buildSwipeLayout();
        mSwipeBackLayout.setEnableGesture(isSwipeBackEnabled());
        super.setContentView(view);
    }

    private SwipeBackLayout buildSwipeLayout() {
        mSwipeBackLayout = (SwipeBackLayout) LayoutInflater.from(this).inflate(
                R.layout.vw_swipeback_layout, null);
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        final Activity activity = this;
        mSwipeBackLayout.addSwipeListener(new SwipeBackLayout.SwipeListener() {
            @Override
            public void onScrollStateChange(int state, float scrollPercent) {
                onSwipeScrollStateChange(state, scrollPercent);
            }

            @Override
            public void onEdgeTouch(int edgeFlag) {
                UIHelper.convertActivityToTranslucent(activity);
                onSwipeEdgeTouch(edgeFlag);
            }

            @Override
            public void onScrollOverThreshold() {

            }
        });
        return mSwipeBackLayout;
    }


    protected void onSwipeScrollStateChange(int state, float scrollPercent) {
    }

    protected void onSwipeEdgeTouch(int edgeFlag) {
    }

    /**
     * 标题栏左侧图标/文字 点击时候的回调
     *
     * @param view
     */
    public void onToolbarLeftClick(View view) {
        finish();
    }

    /**
     * 标题栏中间图标/文字 点击时候的回调
     *
     * @param view
     */
    public void onToolbarTitleClick(View view) {

    }

    /**
     * 标题栏右侧图标/文字 点击时候的回调
     *
     * @param view
     */
    public void onToolbarRightClick(View view) {
        showPopMenu();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
            if (null != mHandler) {
                //用handler post 是为了修复点击菜单键，popMenu闪现的问题,Bugzilla:30303
                mHandler.postDelayed(this::showPopMenu, 140);
            }
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mBaseFragment == null || !mBaseFragment.onBackPressed()) {
                return super.onKeyDown(keyCode, event);
            } else {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showPopMenu() {
        if (mPopupMenu != null) {
            if (mIsPopupMenuShow) {
                mPopupMenu.dismiss();
            } else {
                mPopupMenu.show();
            }
            mIsPopupMenuShow = !mIsPopupMenuShow;
        }
    }

    private View buildContentView(View view) {
        if (hasBaseLayout()) {
            BaseLayout.Builder builder = getLayoutBuilder();
            if (null != builder) {
                mBaseLayout = builder.setContentView(view)
                        .setProgressBarViewBg(getProgressBg())
                        .build();
                return mBaseLayout;
            }
        }
        return view;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mIsInitialized) {
            mIsInitialized = true;
            onLazyLoad();
        }
        // TalkData统计
        TCAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // TalkData统计
        TCAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * 懒加载，需要请求网络的activity需要将网络请求放在此方法当中
     */
    protected void onLazyLoad() {
    }


    /**
     * 避免一些耗时的事件返回的回调在返回时出现NullPointException，需要把这些Subscription在生命周期结束时接触订阅关系，见onDestroy()中业务
     *
     * @param subscription
     */
    public void addSubscription(Subscription subscription) {
        if (subscription != null) {
            if (mCompositeSubscription == null || mCompositeSubscription.isUnsubscribed()) {
                mCompositeSubscription = new CompositeSubscription();
            }
            mCompositeSubscription.add(subscription);
        }
    }


    @Override
    protected void onDestroy() {
        //回收资源
        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
        }
        if (mViewModel != null) {
            mViewModel = null;
        }
        mDestroyed = true;
        RxBusManager.unregister(this);
        // 这里统一隐藏显示的对话框
        dismissProgressDialog();
        ZjbApplication.removeActivity(this);
        ViewLayoutUtil.fixInputMethodManagerLeak(this);
        super.onDestroy();
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
    }

    @Override
    public boolean isDestroyed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return mDestroyed || super.isDestroyed();
        } else {
            return mDestroyed;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
            if ("MenuBuilder".equals(menu.getClass().getSimpleName())) {
                try {
                    Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible",
                            Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu, true);
                } catch (Exception e) {
                    // ignore
                }
            }
        }

        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public void onClickRetry() {
        // 如果子类需要处理错误重试事件，则需覆盖此方法
    }

    @Override
    public void onClickEmpty() {
        // 如果子类需要处理空页面点击事件，则需覆盖此方法
    }

    @Override
    public void onCancel(DialogInterface dialog) {

    }

    /**
     * <p> 获取布局Builder，主要用于自定义每个页面的progress、empty、error等View. <br/>
     * 需要自定义的页面需自行覆盖实现. </p>
     *
     * @return
     */
    protected BaseLayout.Builder getLayoutBuilder() {
        BaseLayout.Builder builder = new BaseLayout.Builder(this);
        builder.setOnBaseLayoutClickListener(this);
        return builder;
    }

    /**
     * 是否包含基本view如progress、empty、error等.
     *
     * @return
     */
    protected boolean hasBaseLayout() {
        return false;
    }

    /**
     * Is Toolbar enable.
     *
     * @return
     */
    protected boolean isToolbarEnable() {
        return true;
    }

    protected boolean isToolbarHasShadow() {
        return true;
    }

    /**
     * @return 颜色的resource id
     */
    protected int getProgressBg() {
        return android.R.color.transparent;
    }

    /**
     * Is Toolbar overlay.
     *
     * @return
     */
    protected boolean isToolbarOverlay() {
        return false;
    }

    /**
     * Has Toolbar navigation.
     *
     * @return
     */
    protected boolean hasToolbarNavigation() {
        return true;
    }

    /**
     * 进度条取消事件回调，需要的子类自行实现
     */
    protected void onProgressCanceled() {

    }

    /**
     * Get status bar height.
     *
     * @return
     */
    public final float getStatusBarHeight() {
        int height = getResources().getIdentifier("status_bar_height", "dimen", "android");
        return getResources().getDimension(height);
    }

    /**
     * Hide navigation bar.
     */
    public final void hideNavigationBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_IMMERSIVE;

            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(uiOptions);

        } else {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(lp);
        }
    }

    /**
     * Make full screen in all Android version.
     */
    public final void makeFullScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // Hide the status bar and navigation bar.
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(uiOptions);

        } else {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(lp);
        }
    }

    /**
     * If the Android version is higher than KitKat(API>=19) <br> use this call to show & hide
     *
     * @param enable
     */
    @SuppressLint("NewApi")
    public final void makeFullScreenAfterKitKat(boolean enable) {
        try {
            View decorView = getWindow().getDecorView();
            if (enable) {
                int uiOptionsEnable = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                decorView.setSystemUiVisibility(uiOptionsEnable);

            } else {
                int uiOptionsDisable = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                decorView.setSystemUiVisibility(uiOptionsDisable);
            }

        } catch (Exception e) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            if (enable) {
                lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            } else {
                lp.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
            getWindow().setAttributes(lp);
        }
    }

    /**
     * Set translucent status bar enable.
     *
     * @param enable
     */
    protected final void setTranslucentStatusEnable(boolean enable) {
        mTranslucentStatusEnable = enable;
    }

    /**
     * Set translucent navigation bar enable.
     *
     * @param enable
     */
    protected final void setTranslucentNavigationEnable(boolean enable) {
        mTranslucentNavigationEnable = enable;
    }

    /**
     * 透明导航栏 API>=19才有效
     */
    private void translucentNavigationBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        SystemBarUtil systemBarUtil = new SystemBarUtil(this);
        systemBarUtil.setNavigationBarTintEnabled(true);
        systemBarUtil.setNavigationBarTintResource(android.R.color.transparent);
    }

    /**
     * 透明状态栏 API>=19才有效
     */
    private void translucentStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // setTranslucentStatus(true);
        }

        SystemBarUtil systemBarUtil = new SystemBarUtil(this);
        systemBarUtil.setStatusBarTintEnabled(true);
        systemBarUtil.setStatusBarTintResource(R.color.toolbar_bg);
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }


    protected boolean canShowDialog() {
        return !(isDestroyed() || isFinishing());
    }

    /**
     * 显示progress
     */
    public void showProgressDialog(int resId) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            return;
        }
        mProgressDialog = DialogHelper.create(DialogHelper.TYPE_PROGRESS)
                .cancelable(true)
                .canceledOnTouchOutside(false)
                .progressText(getResources().getString(resId))
                .show();
    }

    /**
     * Dismiss progress dialog.
     */
    public void dismissProgressDialog() {
        if (isProgressDialogShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    /**
     * Is progress dialog showing.
     *
     * @return
     */
    public boolean isProgressDialogShowing() {
        return null != mProgressDialog && mProgressDialog.isShowing();
    }

    /**
     * Show empty view when the data of current page is null.
     */
    public void showEmptyView() {
        if (null != mBaseLayout) {
            mBaseLayout.showEmptyView();
        }
    }

    /**
     * Show error view when the request of current page is failed.
     */
    public void showErrorView() {
        if (null != mBaseLayout) {
            mBaseLayout.showErrorView();
        }
    }

    /**
     * Show progress view when request data first come in the page.
     */
    public void showProgressView() {
        if (null != mBaseLayout) {
            mBaseLayout.showProgressView();
        }
    }

    /**
     * Show content view of current page.
     */
    public void showContentView() {
        if (null != mBaseLayout) {
            mBaseLayout.showContentView();
        }
    }

    public void inflateMenu(@MenuRes int menuRes) {
        mToolbarLayout.setRightIcon(R.drawable.toolbar_overflow);
        if (mPopupMenu == null) {
            mPopupMenu = new PopupMenu(this, mToolbarLayout.getTitlebar(),
                    Gravity.TOP | Gravity.END);
        }
        if (mMenuRes != menuRes) {
            mMenuRes = menuRes;
            mPopupMenu.inflate(menuRes);
            mPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    return onPopMenuItemClick(item);
                }
            });
            mPopupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
                @Override
                public void onDismiss(PopupMenu menu) {
                    mIsPopupMenuShow = false;
                }
            });
        }
    }

    public void hidePopMenu() {
        if (mPopupMenu != null) {
            mPopupMenu.dismiss();
        }
    }

    protected boolean onPopMenuItemClick(MenuItem item) {
        return false;
    }


    /**
     * 是否可以滑动退出界面
     *
     * @return shi否可以滑动退出
     */
    public boolean isSwipeBackEnabled() {
        return true;
    }


    /**
     * 获取状态栏的颜色
     */
    public int getStatusBarColor() {
        return getResources().getColor(R.color.themes_main);
    }

}
