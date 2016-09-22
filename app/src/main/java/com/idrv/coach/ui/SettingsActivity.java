package com.idrv.coach.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.idrv.coach.R;
import com.idrv.coach.ZjbApplication;
import com.idrv.coach.data.cache.ACache;
import com.idrv.coach.data.db.DBOpenHelper;
import com.idrv.coach.data.manager.AppInitManager;
import com.idrv.coach.data.manager.AppUpdateManager;
import com.idrv.coach.data.manager.LoginManager;
import com.idrv.coach.data.manager.UrlParserManager;
import com.idrv.coach.ui.view.MasterItemView;
import com.idrv.coach.utils.helper.DialogHelper;
import com.idrv.coach.utils.helper.ResHelper;
import com.idrv.coach.utils.helper.UIHelper;
import com.idrv.coach.utils.helper.ViewUtils;
import com.zjb.loader.ZjbImageLoader;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * time:2016/7/29
 * description:
 *
 * @author sunjianfei
 */
public class SettingsActivity extends BaseActivity implements MasterItemView.OnMasterItemClickListener {
    @InjectView(R.id.item_clear_cache)
    MasterItemView mClearCacheItemView;
    @InjectView(R.id.item_check_update)
    MasterItemView mCheckUpdateItemView;
    @InjectView(R.id.item_manual)
    MasterItemView mManualItemView;
    @InjectView(R.id.item_contact_customer)
    MasterItemView mContactCustomerItemView;
    @InjectView(R.id.item_about)
    MasterItemView mAboutItemView;


    public static void launch(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_settings);
        ButterKnife.inject(this);
        initToolBar();
        initView();
    }

    private void initToolBar() {
        mToolbarLayout.setTitle(R.string.settings);
    }

    private void initView() {
        mClearCacheItemView.setText(R.string.clear_cache);
        mClearCacheItemView.setLineVisible(View.GONE);

        mCheckUpdateItemView.setText(R.string.check_update);
        mManualItemView.setText(R.string.use_manual);
        mManualItemView.setLineVisible(View.GONE);

        mContactCustomerItemView.setText(R.string.contact_customer);
        mAboutItemView.setText(R.string.about_us);
        mAboutItemView.setLineVisible(View.GONE);

        mClearCacheItemView.setOnMasterItemClickListener(this);
        mCheckUpdateItemView.setOnMasterItemClickListener(this);
        mManualItemView.setOnMasterItemClickListener(this);
        mContactCustomerItemView.setOnMasterItemClickListener(this);
        mAboutItemView.setOnMasterItemClickListener(this);
    }


    @Override
    public void onMasterItemClick(View v) {
        ViewUtils.setDelayedClickable(v, 500);
        switch (v.getId()) {
            case R.id.item_clear_cache:
                clearCache();
                break;
            case R.id.item_check_update:
                //版本更新
                AppUpdateManager.newInstance().checkUpdate(true);
                break;
            case R.id.item_manual:
                //TODO
                break;
            case R.id.item_contact_customer:
                customer();
                break;
            case R.id.item_about:
                SettingAboutUsActivity.launch(this);
                break;
        }
    }

    /**
     * 清除文件和图片缓存
     */
    private void clearCache() {
        mProgressDialog = DialogHelper.create(DialogHelper.TYPE_PROGRESS)
                .progressText(ResHelper.getString(R.string.clear_ing))
                .show();
        Observable.<String>create(subscriber -> {
            try {
                Thread.sleep(1000);
                //清除文件缓存
                ACache mCache = ACache.get(ZjbApplication.gContext);
                mCache.clear();
                //清除图片缓存
                ZjbImageLoader.clearDiskCache();
                subscriber.onNext(ResHelper.getString(R.string.clear_cache_success));
            } catch (Exception e) {
                e.printStackTrace();
                subscriber.onError(e);
            } finally {
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(UIHelper::shortToast
                        , __ -> UIHelper.shortToast(R.string.clear_cache_error)
                        , this::dismissProgressDialog);

    }

    /**
     * 联系客服
     */
    private void customer() {
        try {
            String uri = "mqqwpa://im/chat?chat_type=wpa&uin=" + "2334348606";
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
        } catch (Exception e) { // 未安装QQ或安装的版本不支持
            UIHelper.shortToast(R.string.qq_support);
        }
    }

    /**
     * 退出登录
     */
    @OnClick(R.id.item_logout)
    void logoutClick() {
        DialogHelper.create(DialogHelper.TYPE_NORMAL)
                .leftButton(getString(R.string.cancel), 0xffd03b3b)
                .rightButton(getString(R.string.confirm), 0xff2b2a2a)
                .title(getString(R.string.is_sure_exit_zjb))
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .leftBtnClickListener((dialog, view) -> dialog.dismiss())
                .rightBtnClickListener((dialog, view) -> {
                    dialog.dismiss();
                    logout();
                })
                .show();
    }

    private void logout() {
        // 清空LoginManager
        LoginManager.getInstance().logout();
        AppInitManager.getInstance().updateBeforeLogin();
        DBOpenHelper.resetInstance();
        UrlParserManager.getInstance().release();
        // 切换界面
        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
