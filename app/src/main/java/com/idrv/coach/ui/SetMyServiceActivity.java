package com.idrv.coach.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import com.idrv.coach.R;
import com.idrv.coach.bean.WebSiteServicesPage;
import com.idrv.coach.data.model.SetMyServicesModel;
import com.idrv.coach.ui.adapter.EditServiceAdapter;
import com.idrv.coach.ui.view.decoration.ServiceItemDecoration;
import com.idrv.coach.ui.widget.EmptyRecyclerView;
import com.idrv.coach.utils.PixelUtil;
import com.idrv.coach.utils.helper.DialogHelper;
import com.idrv.coach.utils.helper.UIHelper;
import com.zjb.volley.utils.NetworkUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscription;

/**
 * time:2016/4/19
 * description:业务编辑
 *
 * @author sunjianfei
 */
public class SetMyServiceActivity extends BaseActivity<SetMyServicesModel> {
    @InjectView(R.id.added_recycler_view)
    EmptyRecyclerView mAddedRecyclerView;
    @InjectView(R.id.select_recycler_view)
    EmptyRecyclerView mSelectRecyclerView;
    @InjectView(R.id.add_empty_view)
    View mAddEmptyView;
    @InjectView(R.id.select_empty_view)
    View mSelectEmptyView;

    EditServiceAdapter mAddedAdapter;
    EditServiceAdapter mSelectAdapter;

    private boolean isEdit = false;


    public static void launch(Context context) {
        Intent intent = new Intent(context, SetMyServiceActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_set_my_service);
        ButterKnife.inject(this);
        initToolBar();
        initView();
        initViewModel();
    }

    @Override
    public void onToolbarRightClick(View view) {
        if (isEdit) {
            saveModify();
        }
        isEdit = !isEdit;
        changeStatus();
    }

    @Override
    protected boolean hasBaseLayout() {
        return true;
    }

    @Override
    protected int getProgressBg() {
        return R.color.bg_main;
    }

    @Override
    public void onClickRetry() {
        if (NetworkUtil.isConnected(this)) {
            showProgressView();
            refresh();
        } else {
            UIHelper.shortToast(R.string.network_error);
        }
    }

    private void initToolBar() {
        mToolbarLayout.setTitle(R.string.set_my_service);
        mToolbarLayout.setRightTxt(R.string.service_edit);
    }

    private void initView() {
        mAddedAdapter = new EditServiceAdapter(R.drawable.service_delete, false);
        mSelectAdapter = new EditServiceAdapter(R.drawable.service_add, false);

        GridLayoutManager mAddedLayoutManager = new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false);
        GridLayoutManager mSelectLayoutManager = new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false);
        mAddedRecyclerView.setLayoutManager(mAddedLayoutManager);
        mSelectRecyclerView.setLayoutManager(mSelectLayoutManager);

        mAddedRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mSelectRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAddedRecyclerView.setEmptyView(mAddEmptyView);
        mSelectRecyclerView.setEmptyView(mSelectEmptyView);

        mAddedRecyclerView.addItemDecoration(new ServiceItemDecoration((int) PixelUtil.dp2px(10)));
        mSelectRecyclerView.addItemDecoration(new ServiceItemDecoration((int) PixelUtil.dp2px(10)));

        mAddedRecyclerView.setAdapter(mAddedAdapter);
        mSelectRecyclerView.setAdapter(mSelectAdapter);

        mAddedAdapter.setOnItemClickListener((services, position) -> {
            if (position < 0)
                return;
            mViewModel.computeValues(-services.getId());
            mAddedAdapter.getData().remove(position);
            mAddedAdapter.notifyItemRemoved(position);

            mSelectAdapter.getData().add(services);
            mSelectAdapter.notifyDataSetChanged();
        });
        mSelectAdapter.setOnItemClickListener((services, position) -> {
            if (position < 0)
                return;
            mViewModel.computeValues(services.getId());
            mSelectAdapter.getData().remove(position);
            mSelectAdapter.notifyItemRemoved(position);

            mAddedAdapter.getData().add(services);
            mAddedAdapter.notifyDataSetChanged();
        });
    }

    private void initViewModel() {
        mViewModel = new SetMyServicesModel();
        refresh();
    }

    private void refresh() {
        Subscription subscription = mViewModel.getWebsiteServices()
                .subscribe(this::onNext, __ -> showErrorView());
        addSubscription(subscription);
    }

    private void saveModify() {
        if (mViewModel.getValues() != 0) {
            showDialog();
            Subscription subscription = mViewModel.updateWebsiteServices()
                    .subscribe(this::onSaveNext, this::onSaveError);
            addSubscription(subscription);
        }
    }

    private void onNext(WebSiteServicesPage page) {
        mAddedAdapter.setData(page.getCoachBusiness());
        mSelectAdapter.setData(page.getAbleBusiness());

        mAddedAdapter.notifyDataSetChanged();
        mSelectAdapter.notifyDataSetChanged();
        showContentView();
    }

    private void onSaveNext(String s) {
        dismissProgressDialog();
        if ("true".equals(s)) {
            UIHelper.shortToast(R.string.save_success);
        }
        finish();
    }

    private void onSaveError(Throwable e) {
        dismissProgressDialog();
        UIHelper.shortToast(R.string.save_failed);
        //状态重置
        isEdit = true;
        changeStatus();
    }

    final protected void showDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            return;
        }
        mProgressDialog = DialogHelper.create(DialogHelper.TYPE_PROGRESS)
                .cancelable(true)
                .canceledOnTouchOutside(false)
                .progressText(getResources().getString(R.string.commit_now))
                .onDismissListener(__ -> {
                    if (mCompositeSubscription != null) {
                        mCompositeSubscription.unsubscribe();
                    }
                }).show();
    }

    private void changeStatus() {
        mToolbarLayout.setRightTxt(isEdit ? R.string.save : R.string.service_edit);
        mAddedAdapter.setIsEditStatus(isEdit);
        mSelectAdapter.setIsEditStatus(isEdit);
        mAddedAdapter.notifyDataSetChanged();
        mSelectAdapter.notifyDataSetChanged();
    }
}
