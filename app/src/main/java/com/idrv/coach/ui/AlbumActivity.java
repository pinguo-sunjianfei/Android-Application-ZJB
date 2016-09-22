//package com.idrv.coach.ui;
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//
//import com.idrv.coach.R;
//import com.idrv.coach.bean.Album;
//import com.idrv.coach.bean.event.EventConstant;
//import com.idrv.coach.data.manager.RxBusManager;
//import com.idrv.coach.data.model.AlbumModel;
//import com.idrv.coach.ui.view.AlbumItemLayout;
//import com.idrv.coach.utils.Logger;
//import com.idrv.coach.utils.ValidateUtil;
//import com.idrv.coach.utils.helper.UIHelper;
//import com.zjb.volley.utils.NetworkUtil;
//
//import java.util.List;
//
//import butterknife.ButterKnife;
//import butterknife.InjectView;
//import butterknife.OnClick;
//import rx.Subscription;
//import rx.android.schedulers.AndroidSchedulers;
//
///**
// * time:2016/5/23
// * description:
// *
// * @author sunjianfei
// */
//public class AlbumActivity extends BaseActivity<AlbumModel> implements View.OnClickListener {
//    @InjectView(R.id.album_mine)
//    AlbumItemLayout mAlbumViewMine;
//    @InjectView(R.id.album_student)
//    AlbumItemLayout mAlbumViewStudent;
//
//    boolean hasShow = false;
//
//    public static void launch(Context context) {
//        Intent intent = new Intent(context, AlbumActivity.class);
//        context.startActivity(intent);
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.act_my_album);
//        ButterKnife.inject(this);
//        initToolBar();
//        initViewModel();
//        registerEvent();
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
//    @Override
//    public void onClickRetry() {
//        if (NetworkUtil.isConnected(this)) {
//            showProgressView();
//            refresh();
//        } else {
//            UIHelper.shortToast(R.string.network_error);
//        }
//    }
//
//    private void initToolBar() {
//        mToolbarLayout.setTitle(R.string.my_album);
//    }
//
//    private void initViewModel() {
//        mViewModel = new AlbumModel();
//        refresh();
//    }
//
//    private void registerEvent() {
//        //1.照片上传成功,刷新相册
//        RxBusManager.register(this, EventConstant.KEY_REFRESH_PIC, String.class)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(__ -> refresh(), Logger::e);
//        //2.照片删除成功,刷新相册
//        RxBusManager.register(this, EventConstant.KEY_PHOTO_DELETE_SUCCESS, String.class)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(__ -> refresh(), Logger::e);
//    }
//
//    private void refresh() {
//        Subscription subscription = mViewModel.getAlbum()
//                .subscribe(this::onNext, this::onError);
//        addSubscription(subscription);
//    }
//
//    private void onNext(List<Album> albumList) {
//        hasShow = true;
//        if (!ValidateUtil.isValidate(albumList) || albumList.size() != 2) {
//            showErrorView();
//        } else {
//            setUp(albumList);
//        }
//    }
//
//    private void onError(Throwable e) {
//        if (!hasShow) {
//            showErrorView();
//        }
//    }
//
//    private void setUp(List<Album> list) {
//        Album album1 = list.get(0);
//        Album album2 = list.get(1);
//
//        //学员风采
//        if (album1.getAlbumType() == 0) {
//            showAlbum(album1, mAlbumViewStudent, R.string.student_photo,R.drawable.album_student_empty);
//            showAlbum(album2, mAlbumViewMine, R.string.my_photo,R.drawable.album_mine_empty);
//        } else {
//            showAlbum(album2, mAlbumViewStudent, R.string.student_photo,R.drawable.album_student_empty);
//            showAlbum(album1, mAlbumViewMine, R.string.my_photo,R.drawable.album_mine_empty);
//        }
//
//        showContentView();
//    }
//
//    private void showAlbum(Album album, AlbumItemLayout itemLayout, int titleResId, int drawableResId) {
//        itemLayout.setImage(album.getCover(), drawableResId);
//        itemLayout.setTitle(titleResId);
//        itemLayout.setPicNum(getString(R.string.num_photo, album.getPictureNumber()));
//    }
//
//    @OnClick({R.id.album_mine, R.id.album_student})
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.album_mine:
//                PhotoWallBackActivity.launch(v.getContext(), PhotoWallBackActivity.TYPE_COACH);
//                break;
//            case R.id.album_student:
//                PhotoWallBackActivity.launch(v.getContext(), PhotoWallBackActivity.TYPE_STUDENT);
//                break;
//        }
//    }
//}
