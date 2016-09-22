//package com.idrv.coach.ui;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.support.v7.widget.GridLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.View;
//import android.widget.TextView;
//
//import com.idrv.coach.R;
//import com.idrv.coach.bean.Picture;
//import com.idrv.coach.bean.event.EventConstant;
//import com.idrv.coach.data.constants.SPConstant;
//import com.idrv.coach.data.manager.RxBusManager;
//import com.idrv.coach.data.model.PhotoWallModel;
//import com.idrv.coach.ui.adapter.PhotoWallBackAdapter;
//import com.idrv.coach.ui.view.decoration.PhotoWallItemDecoration;
//import com.idrv.coach.ui.widget.EmptyRecyclerView;
//import com.idrv.coach.ui.widget.SwipeRefreshLayout;
//import com.idrv.coach.utils.Logger;
//import com.idrv.coach.utils.PixelUtil;
//import com.idrv.coach.utils.PreferenceUtil;
//import com.idrv.coach.utils.ScrollUtils;
//import com.idrv.coach.utils.StatisticsUtil;
//import com.idrv.coach.utils.ValidateUtil;
//import com.idrv.coach.utils.helper.DialogHelper;
//import com.idrv.coach.utils.helper.UIHelper;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import butterknife.ButterKnife;
//import butterknife.InjectView;
//import butterknife.OnClick;
//import rx.Subscription;
//import rx.android.schedulers.AndroidSchedulers;
//
///**
// * time:2016/4/20
// * description:个人或者学员的照片墙
// *
// * @author sunjianfei
// */
//public class PhotoWallBackActivity extends BaseActivity<PhotoWallModel>
//        implements PhotoWallBackAdapter.OnCameraClickListener {
//    private static final int CAMERA_REQUEST_CODE = 0x001;
//    private static final String KEY_PARAM = "param";
//    //学员
//    public static final int TYPE_STUDENT = 0;
//    //教练
//    public static final int TYPE_COACH = 1;
//
//    @InjectView(R.id.recycler_view)
//    EmptyRecyclerView mRecyclerView;
//    @InjectView(R.id.refresh_layout)
//    SwipeRefreshLayout mSwipeRefreshLayout;
//    @InjectView(R.id.title_bar_layout)
//    View mTitleBarLayout;
//    @InjectView(R.id.title_tv)
//    TextView mTitleTv;
//
//    PhotoWallBackAdapter mAdapter;
//
//    //列表滑动的距离
//    private int mCurrentScroll;
//    private float mHeaderViewHeight = PixelUtil.dp2px(200);
//
//    public static void launch(Context context, int type) {
//        Intent intent = new Intent(context, PhotoWallBackActivity.class);
//        intent.putExtra(KEY_PARAM, type);
//        context.startActivity(intent);
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.act_photo_wall);
//        ButterKnife.inject(this);
//        initView();
//        initViewModel();
//        registerEvent();
//    }
//
//    @Override
//    protected boolean isToolbarEnable() {
//        return false;
//    }
//
//    @Override
//    protected boolean hasBaseLayout() {
//        return true;
//    }
//
//    @Override
//    protected int getProgressBg() {
//        return R.color.bg_main;
//    }
//
//    private void initView() {
//        int type = getIntent().getIntExtra(KEY_PARAM, 0);
//        int titleResId = type == TYPE_COACH ?
//                R.string.my_photo : R.string.student_photo;
//
//        mTitleTv.setText(titleResId);
//
//        mAdapter = new PhotoWallBackAdapter();
//        mAdapter.setType(type);
//        mAdapter.setOnCameraClickListener(this);
//        //1.得到LayoutManager
//        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false);
//        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//            @Override
//            public int getSpanSize(int position) {
//                if (position == 0 || position == 1) {
//                    return 4;
//                }
//                return 1;
//            }
//        });
//        mRecyclerView.setLayoutManager(mLayoutManager);
//        mRecyclerView.addItemDecoration(new PhotoWallItemDecoration((int) PixelUtil.dp2px(5.5f)));
//        mRecyclerView.setAdapter(mAdapter);
//
//        //设置刷新逻辑
//        mSwipeRefreshLayout.setMode(SwipeRefreshLayout.Mode.BOTH);
//        mSwipeRefreshLayout.setOnRefreshListener(this::refresh);
//        mSwipeRefreshLayout.setOnPullUpRefreshListener(this::loadMore);
//
//        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                mCurrentScroll += dy;
//                int baseColor = getResources().getColor(R.color.themes_main);
//                float alpha = Math.min(1, (float) mCurrentScroll / mHeaderViewHeight);
//                mTitleBarLayout.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
//            }
//        });
//    }
//
//    private void initViewModel() {
//        int type = getIntent().getIntExtra(KEY_PARAM, 0);
//        mViewModel = new PhotoWallModel();
//        mViewModel.setType(type);
//        refresh();
//    }
//
//    private void refresh() {
//        Subscription subscription = mViewModel.refresh(mAdapter::clear)
//                .subscribe(this::onRefreshNext, this::onError, this::onComplete);
//        addSubscription(subscription);
//    }
//
//    private void loadMore() {
//        Subscription subscription = mViewModel.loadMore()
//                .subscribe(this::onLoadMoreNext, this::onError, this::onComplete);
//        addSubscription(subscription);
//    }
//
//    private void onRefreshNext(List<Picture> list) {
//        mAdapter.setData(list);
//        mAdapter.notifyDataSetChanged();
//    }
//
//    private void onLoadMoreNext(List<Picture> list) {
//        mAdapter.addData(list);
//        mAdapter.notifyDataSetChanged();
//    }
//
//    private void onError(Throwable e) {
//        UIHelper.shortToast(R.string.network_error);
//        showContentView();
//        mSwipeRefreshLayout.setRefreshing(false);
//        mSwipeRefreshLayout.setPullUpRefreshing(false);
//    }
//
//    private void onComplete() {
//        showContentView();
//        mSwipeRefreshLayout.setRefreshing(false);
//        mSwipeRefreshLayout.setPullUpRefreshing(false);
//    }
//
//    private void registerEvent() {
//        //1.照片上传失败
//        RxBusManager.register(this, EventConstant.KEY_PHOTO_WALL_UPLOAD_FAILED, String.class)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(s -> onUploadFailed(false), Logger::e);
//        //2.照片上传成功
//        RxBusManager.register(this, EventConstant.KEY_PHOTO_WALL_UPLOAD_SUCCESS, String.class)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(__ -> submit(), Logger::e);
//        //3.删除照片
//        RxBusManager.register(this, EventConstant.KEY_PHOTO_DELETE, Integer.class)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(this::photoDelete, Logger::e);
//    }
//
//    private void submit() {
//        Subscription subscription = mViewModel.onUploadSuccess()
//                .subscribe(this::onUploadSuccess, e -> onUploadFailed(true));
//        addSubscription(subscription);
//    }
//
//    private void onUploadFailed(boolean isLastStep) {
//        dismissProgressDialog();
//        DialogHelper.create(DialogHelper.TYPE_NORMAL)
//                .cancelable(false)
//                .canceledOnTouchOutside(false)
//                .title(getString(R.string.status_error_tip))
//                .content(getString(R.string.upload_pic_error))
//                .leftButton(getString(R.string.cancel), getResources().getColor(R.color.black_54))
//                .rightButton(getString(R.string.sure), getResources().getColor(R.color.themes_main))
//                .leftBtnClickListener((dialog, v) -> {
//                    mAdapter.removeFakeItem();
//                    mViewModel.reset();
//                    dialog.dismiss();
//                })
//                .rightBtnClickListener((dialog, v) -> {
//                    showDialog();
//                    if (isLastStep) {
//                        submit();
//                    } else {
//                        mViewModel.reUpload();
//                    }
//                    dialog.dismiss();
//                })
//                .show();
//    }
//
//    private void onUploadSuccess(String s) {
//        dismissProgressDialog();
//        UIHelper.shortToast(R.string.upload_success);
//        RxBusManager.post(EventConstant.KEY_REFRESH_PIC, "");
//        boolean hasUpload = mViewModel.hasUpload();
//        if (!hasUpload) {
//            DialogHelper.create(DialogHelper.TYPE_NORMAL)
//                    .cancelable(true)
//                    .canceledOnTouchOutside(true)
//                    .title(getString(R.string.tip))
//                    .content(getString(R.string.upload_tips))
//                    .bottomButton(getString(R.string.Iknowit), getResources().getColor(R.color.themes_main))
//                    .bottomBtnClickListener((dialog, view) -> dialog.dismiss())
//                    .onDismissListener(dialog -> PreferenceUtil.putBoolean(SPConstant.KEY_HAS_UPLOAD_PHOTO, true))
//                    .show();
//        }
//        refresh();
//    }
//
//    private void photoDelete(int pos) {
//        Picture picture = mAdapter.getData().remove(pos);
//        mAdapter.notifyDataSetChanged();
//        Subscription subscription = mViewModel.photoDelete(picture.getId())
//                .subscribe(this::onPhotoDeleteSuccess, Logger::e);
//        addSubscription(subscription);
//    }
//
//    private void onPhotoDeleteSuccess(String s) {
//        RxBusManager.post(EventConstant.KEY_PHOTO_DELETE_SUCCESS, "");
//        int picNum = PreferenceUtil.getInt(SPConstant.KEY_PIC_NUMBER);
//        if (picNum != 0) {
//            PreferenceUtil.putInt(SPConstant.KEY_PIC_NUMBER, picNum - 1);
//        }
//    }
//
//    private void showDialog() {
//        if (mProgressDialog != null && mProgressDialog.isShowing()) {
//            return;
//        }
//        mProgressDialog = DialogHelper.create(DialogHelper.TYPE_PROGRESS)
//                .cancelable(true)
//                .canceledOnTouchOutside(false)
//                .progressText(getResources().getString(R.string.upload_photo_now))
//                .show();
//    }
//
//    /**
//     * 在进入相册前的提示对话框
//     */
//    private void showDialogBeforeSelectPhoto() {
//        int type = mViewModel.getType();
//        String title = type == TYPE_COACH ? getString(R.string.upload_photo_mine)
//                : getString(R.string.upload_photo_student);
//        String content = type == TYPE_COACH ? getString(R.string.upload_photo_mine_tips)
//                : getString(R.string.upload_photo_student_tips);
//
//        DialogHelper.create(DialogHelper.TYPE_NORMAL)
//                .cancelable(true)
//                .canceledOnTouchOutside(false)
//                .title(title)
//                .content(content)
//                .bottomButton(getString(R.string.open_gallery), getResources().getColor(R.color.themes_main))
//                .bottomBtnClickListener((dialog, v) -> {
//                    GalleryActivity.launch(this, CAMERA_REQUEST_CODE, false);
//                    dialog.dismiss();
//                })
//                .show();
//    }
//
//    @OnClick(R.id.back)
//    public void onBack() {
//        finish();
//    }
//
//    @Override
//    public void onCameraClick() {
//        //统计点击上传照片的事件
//        StatisticsUtil.onEvent(R.string.click_upload);
//        showDialogBeforeSelectPhoto();
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == Activity.RESULT_OK) {
//            if (requestCode == CAMERA_REQUEST_CODE) {
//                if (null != data) {
//                    ArrayList<String> list = data.getStringArrayListExtra(GalleryActivity.DATA);
//                    if (ValidateUtil.isValidate(list)) {
//                        List<Picture> pictures = new ArrayList<>();
//                        for (String path : list) {
//                            Picture picture = new Picture();
//                            picture.setIsFake(true);
//                            picture.setUrl(path);
//                            pictures.add(picture);
//                        }
//
//                        new Handler().postDelayed(() -> {
//                            //显示对话框
//                            showDialog();
//                            //更新数据
//                            mAdapter.insertData(pictures);
//                        }, 300);
//                        //上传
//                        mViewModel.batchUpload(pictures);
//                    }
//                }
//            }
//        }
//    }
//}
