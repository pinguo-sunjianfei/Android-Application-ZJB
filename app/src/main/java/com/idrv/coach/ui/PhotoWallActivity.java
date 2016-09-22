package com.idrv.coach.ui;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.idrv.coach.R;
import com.idrv.coach.bean.Picture;
import com.idrv.coach.bean.event.EventConstant;
import com.idrv.coach.data.constants.SPConstant;
import com.idrv.coach.data.manager.RxBusManager;
import com.idrv.coach.data.model.PhotoWallModel;
import com.idrv.coach.ui.adapter.PhotoWallAdapter;
import com.idrv.coach.ui.widget.EmptyRecyclerView;
import com.idrv.coach.ui.widget.FixedViewPager;
import com.idrv.coach.ui.widget.SwipeRefreshLayout;
import com.idrv.coach.utils.Logger;
import com.idrv.coach.utils.PixelUtil;
import com.idrv.coach.utils.PreferenceUtil;
import com.idrv.coach.utils.ScrollUtils;
import com.idrv.coach.utils.ValidateUtil;
import com.idrv.coach.utils.helper.DialogHelper;
import com.idrv.coach.utils.helper.UIHelper;
import com.idrv.coach.utils.helper.ViewUtils;
import com.zjb.volley.utils.NetworkUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * time:2016/8/9
 * description:
 *
 * @author sunjianfei
 */
public class PhotoWallActivity extends BaseActivity<PhotoWallModel> {
    private static final int CAMERA_REQUEST_CODE = 0x001;

    @InjectView(R.id.recycler_view)
    EmptyRecyclerView mRecyclerView;
    @InjectView(R.id.refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.title_bar_layout)
    View mTitleBarLayout;

    PhotoWallAdapter mAdapter;

    private FixedViewPager.OnViewPagerTouchListener mOnViewPagerTouchListener;

    {
        this.mOnViewPagerTouchListener = new FixedViewPager.OnViewPagerTouchListener() {
            @Override
            public void onTouchDown() {
                mSwipeRefreshLayout.setRefreshing(false);
                mSwipeRefreshLayout.setPullUpRefreshing(false);
                mSwipeRefreshLayout.setMode(SwipeRefreshLayout.Mode.DISABLED);
            }

            @Override
            public void onTouchUp() {
                mSwipeRefreshLayout.setMode(SwipeRefreshLayout.Mode.BOTH);
            }
        };
    }

    //列表滑动的距离
    private int mCurrentScroll;
    private float mHeaderViewHeight = PixelUtil.dp2px(200);

    public static void launch(Context context) {
        Intent intent = new Intent(context, PhotoWallActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_photowall);
        ButterKnife.inject(this);
        initView();
        initViewModel();
        registerEvent();
    }

    @Override
    protected boolean isToolbarEnable() {
        return false;
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

    @Override
    public boolean isSwipeBackEnabled() {
        return false;
    }

    @OnClick({R.id.upload_photo, R.id.back})
    public void onClick(View v) {
        ViewUtils.setDelayedClickable(v, 500);
        switch (v.getId()) {
            case R.id.upload_photo:
                GalleryActivity.launch(this, CAMERA_REQUEST_CODE, false);
                break;
            case R.id.back:
                this.finish();
                break;
        }
    }

    /**
     * 初始化View
     */
    private void initView() {
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new PhotoWallAdapter(mOnViewPagerTouchListener);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mCurrentScroll += dy;
                int baseColor = getResources().getColor(R.color.themes_main);
                float alpha = Math.min(1, (float) mCurrentScroll / mHeaderViewHeight);
                mTitleBarLayout.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
            }
        });

        mSwipeRefreshLayout.setMode(SwipeRefreshLayout.Mode.BOTH);
        mSwipeRefreshLayout.setOnRefreshListener(this::refresh);
        mSwipeRefreshLayout.setOnPullUpRefreshListener(this::loadMore);

        //拦截点击事件
        mTitleBarLayout.setOnTouchListener((v, event) -> true);
    }

    private void initViewModel() {
        mViewModel = new PhotoWallModel();
        refresh();
    }

    /**
     * 注册Rx事件
     */
    private void registerEvent() {
        //1.照片上传失败
        RxBusManager.register(this, EventConstant.KEY_PHOTO_WALL_UPLOAD_FAILED, String.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> onUploadFailed(false), Logger::e);
        //2.照片上传成功
        RxBusManager.register(this, EventConstant.KEY_PHOTO_WALL_UPLOAD_SUCCESS, String.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(__ -> submit(), Logger::e);
        //3.删除照片
        RxBusManager.register(this, EventConstant.KEY_PHOTO_DELETE, Integer.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::photoDelete, Logger::e);
    }

    /**
     * 图片上传七牛成功,提交到服务器
     */
    private void submit() {
        Subscription subscription = mViewModel.onUploadSuccess()
                .subscribe(this::onUploadSuccess, e -> onUploadFailed(true));
        addSubscription(subscription);
    }

    private void onUploadFailed(boolean isLastStep) {
        dismissProgressDialog();
        DialogHelper.create(DialogHelper.TYPE_NORMAL)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .title(getString(R.string.status_error_tip))
                .content(getString(R.string.upload_pic_error))
                .leftButton(getString(R.string.cancel), getResources().getColor(R.color.black_54))
                .rightButton(getString(R.string.sure), getResources().getColor(R.color.themes_main))
                .leftBtnClickListener((dialog, v) -> {
                    mAdapter.removeFakeItem();
                    mViewModel.reset();
                    dialog.dismiss();
                })
                .rightBtnClickListener((dialog, v) -> {
                    showProgressDialog(R.string.upload_photo_now);
                    if (isLastStep) {
                        submit();
                    } else {
                        mViewModel.reUpload();
                    }
                    dialog.dismiss();
                })
                .show();
    }

    private void onUploadSuccess(String s) {
        dismissProgressDialog();
        UIHelper.shortToast(R.string.upload_success);
        RxBusManager.post(EventConstant.KEY_REFRESH_PIC, "");
        boolean hasUpload = mViewModel.hasUpload();
        if (!hasUpload) {
            DialogHelper.create(DialogHelper.TYPE_NORMAL)
                    .cancelable(true)
                    .canceledOnTouchOutside(true)
                    .title(getString(R.string.tip))
                    .content(getString(R.string.upload_tips))
                    .bottomButton(getString(R.string.Iknowit), getResources().getColor(R.color.themes_main))
                    .bottomBtnClickListener((dialog, view) -> dialog.dismiss())
                    .onDismissListener(dialog -> PreferenceUtil.putBoolean(SPConstant.KEY_HAS_UPLOAD_PHOTO, true))
                    .show();
        }
        refresh();
    }

    /**
     * 删除照片
     *
     * @param pos
     */
    private void photoDelete(int pos) {
        Picture picture = mAdapter.getData().remove(pos);
        List<Picture> pictures = mAdapter.getData();
        int size = pictures.size();
        if (size > 10) {
            mAdapter.setPictures(pictures.subList(0, 10));
        } else {
            mAdapter.setPictures(pictures);
        }
        mAdapter.notifyDataSetChanged();
        Subscription subscription = mViewModel.photoDelete(picture.getId())
                .subscribe(this::onPhotoDeleteSuccess, Logger::e);
        addSubscription(subscription);
    }

    /**
     * 删除照片成功
     *
     * @param s
     */
    private void onPhotoDeleteSuccess(String s) {
        RxBusManager.post(EventConstant.KEY_PHOTO_DELETE_SUCCESS, "");
        int picNum = PreferenceUtil.getInt(SPConstant.KEY_PIC_NUMBER);
        if (picNum != 0) {
            PreferenceUtil.putInt(SPConstant.KEY_PIC_NUMBER, picNum - 1);
        }
    }

    private void refresh() {
        Subscription subscription = mViewModel.refresh(mAdapter::clear)
                .subscribe(this::onRefreshNext, this::onError, this::onComplete);
        addSubscription(subscription);
    }

    private void loadMore() {
        Subscription subscription = mViewModel.loadMore()
                .subscribe(this::onLoadMoreNext, this::onError, this::onComplete);
        addSubscription(subscription);
    }

    private void onRefreshNext(List<Picture> list) {
        int size = list.size();
        if (size > 10) {
            mAdapter.setPictures(list.subList(0, 10));
        } else {
            mAdapter.setPictures(list);
        }
        mAdapter.setData(list);
        mAdapter.notifyDataSetChanged();
    }

    private void onLoadMoreNext(List<Picture> list) {
        mAdapter.addData(list);
        mAdapter.notifyDataSetChanged();
    }

    private void onError(Throwable e) {
        UIHelper.shortToast(R.string.network_error);
        mSwipeRefreshLayout.setRefreshing(false);
        mSwipeRefreshLayout.setPullUpRefreshing(false);
        if (mAdapter.getItemCount() == 1) {
            showErrorView();
        }
    }

    private void onComplete() {
        mSwipeRefreshLayout.setRefreshing(false);
        mSwipeRefreshLayout.setPullUpRefreshing(false);
        showContentView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                if (null != data) {
                    ArrayList<String> list = data.getStringArrayListExtra(GalleryActivity.DATA);
                    if (ValidateUtil.isValidate(list)) {
                        List<Picture> pictures = new ArrayList<>();
                        for (String path : list) {
                            Picture picture = new Picture();
                            picture.setIsFake(true);
                            picture.setUrl(path);
                            pictures.add(picture);
                        }

                        new Handler().postDelayed(() -> {
                            //显示对话框
                            showProgressDialog(R.string.upload_photo_now);
                            //更新数据
                            mAdapter.insertData(pictures);
                        }, 300);
                        //上传
                        mViewModel.batchUpload(pictures);
                    }
                }
            }
        }
    }
}
