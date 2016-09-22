//package com.idrv.coach.ui.fragment;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import com.idrv.coach.R;
//import com.idrv.coach.bean.ESite;
//import com.idrv.coach.bean.event.EventConstant;
//import com.idrv.coach.data.manager.RxBusManager;
//import com.idrv.coach.data.model.EnrollmentModel;
//import com.idrv.coach.ui.DynamicActivity;
//import com.idrv.coach.ui.NewsHallActivity;
//import com.idrv.coach.ui.PhotoWallBackActivity;
//import com.idrv.coach.ui.SetMyServiceActivity;
//import com.idrv.coach.ui.UserInfoActivity;
//import com.idrv.coach.ui.view.WebSiteItemView;
//import com.idrv.coach.utils.Logger;
//
//import butterknife.ButterKnife;
//import butterknife.InjectView;
//import butterknife.OnClick;
//import rx.Subscription;
//import rx.android.schedulers.AndroidSchedulers;
//
///**
// * time:2016/4/20
// * description:
// *
// * @author sunjianfei
// */
//public class WebSiteFragment extends BaseFragment<EnrollmentModel> implements View.OnClickListener {
//    @InjectView(R.id.item_profile)
//    WebSiteItemView mProfileItemView;
//    @InjectView(R.id.item_services)
//    WebSiteItemView mSetServicesItemView;
//    @InjectView(R.id.item_student_photo)
//    WebSiteItemView mStudentPhotoItemView;
//    @InjectView(R.id.item_my_photo)
//    WebSiteItemView mMyPhotoItemView;
//    @InjectView(R.id.news_sub_text_view)
//    TextView mNewsSubTv;
//    @InjectView(R.id.new_msg)
//    View mRedPointView;
//
//
//    @Override
//    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.frag_personal_website, container, false);
//    }
//
//    @Override
//    protected boolean hasBaseLayout() {
//        return false;
//    }
//
//    @Override
//    protected int getProgressBg() {
//        return R.color.bg_main;
//    }
//
//    @Override
//    public void initView(View view) {
//        ButterKnife.inject(this, view);
//        view.getBackground().setAlpha(0);
//
//        mProfileItemView.setIcon(R.drawable.icon_profile);
//        mProfileItemView.setTitle(R.string.item_profile);
//
//        mSetServicesItemView.setIcon(R.drawable.icon_my_services);
//        mSetServicesItemView.setTitle(R.string.my_services);
//
//        mStudentPhotoItemView.setIcon(R.drawable.icon_student_photo);
//        mStudentPhotoItemView.setTitle(R.string.student_photo);
//
//        mMyPhotoItemView.setIcon(R.drawable.icon_my_photo);
//        mMyPhotoItemView.setTitle(R.string.my_photo);
//
//        mViewModel = new EnrollmentModel();
//        registerEvent();
//    }
//
//    private void registerEvent() {
//        RxBusManager.register(this, EventConstant.KEY_WEBSITE_DATA_LOAD_SUCCESS, ESite.class)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(this::onNext, Logger::e);
//    }
//
//    private void onNext(ESite site) {
//        String profileUndone = mViewModel.getDoneProfileStr();
//        mProfileItemView.setSubText(profileUndone);
//        mSetServicesItemView.setSubText(getString(R.string.options, site.getBusiness()));
//        mStudentPhotoItemView.setSubText(getString(R.string.piece, site.getStudentPictureCount()));
//        mMyPhotoItemView.setSubText(getString(R.string.piece, site.getCoachPictureCount()));
//        mNewsSubTv.setText(site.isTodayShared() ? R.string.had_shared : R.string.not_share);
//        mRedPointView.setVisibility(site.isHasNewMessage() ? View.VISIBLE : View.GONE);
//    }
//
//    @OnClick({R.id.item_profile, R.id.item_services,
//            R.id.news_share_layout, R.id.item_my_photo, R.id.item_student_photo, R.id.message_layout})
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.item_profile:
//                UserInfoActivity.launch(getContext());
//                break;
//            case R.id.item_services:
//                SetMyServiceActivity.launch(getContext());
//                break;
//            case R.id.news_share_layout:
//                NewsHallActivity.launch(getContext());
//                break;
//            case R.id.item_my_photo:
//                PhotoWallBackActivity.launch(getContext(), PhotoWallBackActivity.TYPE_COACH);
//                break;
//            case R.id.item_student_photo:
//                PhotoWallBackActivity.launch(getContext(), PhotoWallBackActivity.TYPE_STUDENT);
//                break;
//            case R.id.message_layout:
//                onMessageLayoutClick(v.getContext());
//                break;
//        }
//    }
//
//    private void onMessageLayoutClick(Context context) {
//        Subscription subscription = mViewModel.refreshNewMessage()
//                .subscribe(Logger::e, Logger::e);
//        addSubscription(subscription);
//
//        mRedPointView.setVisibility(View.GONE);
//        DynamicActivity.launch(context);
//    }
//}
