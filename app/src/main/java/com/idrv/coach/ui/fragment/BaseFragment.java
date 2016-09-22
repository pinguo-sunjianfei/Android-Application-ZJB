package com.idrv.coach.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.idrv.coach.data.manager.RxBusManager;
import com.idrv.coach.ui.BaseActivity;
import com.idrv.coach.ui.widget.BaseLayout;
import com.idrv.coach.utils.helper.DialogHelper;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * time: 15/6/9
 * description:fragment的基础类
 *
 * @author sunjianfei
 */
public abstract class BaseFragment<ViewModel> extends Fragment implements BaseLayout.OnBaseLayoutClickListener {
    protected BaseActivity mActivity;

    protected View mRootView;
    protected BaseLayout mBaseLayout;
    protected Dialog mProgressDialog;

    protected boolean mIsInitialized;

    protected ViewModel mViewModel;

    protected boolean mIsDestroyed;
    protected CompositeSubscription mSubscription;

    public abstract View createView(LayoutInflater inflater, ViewGroup container,
                                    Bundle savedInstanceState);

    public abstract void initView(View view);

    @Override
    public void onAttach(Activity activity) {
        mActivity = (BaseActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        mActivity = null;
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mSubscription = new CompositeSubscription();
    }

    /**
     * <p>
     * 获取布局Builder，主要用于自定义每个页面的progress、empty、error等View.
     * 需要自定义的页面需自行覆盖实现.
     * </p>
     *
     * @return
     */
    protected BaseLayout.Builder getLayoutBuilder() {
        BaseLayout.Builder builder = new BaseLayout.Builder(mActivity);
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

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container,
                                   Bundle savedInstanceState) {
        mIsInitialized = true;
        mIsDestroyed = false;
        if (mRootView == null) {
            View view = createView(inflater, container, savedInstanceState);
            mRootView = view;
            if (hasBaseLayout()) {
                BaseLayout.Builder builder = getLayoutBuilder();
                if (null != builder) {
                    mBaseLayout = builder.setContentView(view)
                            .setProgressBarViewBg(getProgressBg())
                            .build();
                    mRootView = mBaseLayout;
                }
            }
            initView(view);
        } else {
            ViewGroup localViewGroup = (ViewGroup) mRootView.getParent();
            if (localViewGroup != null) {
                localViewGroup.removeView(mRootView);
            }
        }
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 判断当前fragment是否显示
        if (getUserVisibleHint()) {
            if (mIsInitialized) {
                mIsInitialized = false;
                // 加载各种数据
                onLazyLoad();
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        // 每次切换fragment时调用的方法
        if (isVisibleToUser) {
            if (mIsInitialized) {
                mIsInitialized = false;
                // 加载各种数据
                onLazyLoad();
            }
        }
    }


    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mIsDestroyed = true;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    public void addSubscription(Subscription subscription) {
        if (subscription != null) {
            if (mSubscription == null || mSubscription.isUnsubscribed()) {
                mSubscription = new CompositeSubscription();
            }
            mSubscription.add(subscription);
        }
    }

    @Override
    public void onDestroy() {
        RxBusManager.unregister(this);
        if (mSubscription != null) {
            mSubscription.unsubscribe();
            mSubscription = null;
        }
        if (mViewModel != null) {
            mViewModel = null;
        }
        super.onDestroy();
    }

    /**
     * 首次可见时回调加载数据，用于延迟加载
     */
    protected void onLazyLoad() {

    }

    @Override
    public void onClickRetry() {
        // 如果子类需要处理错误重试事件，则需覆盖此方法
    }

    @Override
    public void onClickEmpty() {
        // 如果子类需要处理空页面点击事件，则需覆盖此方法
    }

    /**
     * 进度条取消事件回调，需要的子类自行实现
     */
    protected void onProgressCanceled() {
        // 需要的子类自行实现
    }

    protected boolean isDestroyed() {
        return mIsDestroyed;
    }

    protected boolean canShowDialog() {
        return isAdded() && !isDetached() && (null != mActivity && !mActivity.isFinishing()
                && !mActivity.isDestroyed());
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

    protected int getProgressBg() {
        return android.R.color.transparent;
    }

    /**
     * 返回键的监听
     *
     * @return
     */
    public boolean onBackPressed() {
        return false;
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
}