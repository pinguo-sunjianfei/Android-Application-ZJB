package com.idrv.coach.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.idrv.coach.R;
import com.idrv.coach.bean.Task;
import com.idrv.coach.bean.share.ShareWebProvider;
import com.idrv.coach.data.manager.LoginManager;
import com.idrv.coach.data.model.NewsHallModel;
import com.idrv.coach.ui.widget.TimeDownHour;
import com.idrv.coach.utils.BitmapUtil;
import com.idrv.coach.utils.Logger;
import com.idrv.coach.utils.PixelUtil;
import com.idrv.coach.utils.TimeUtil;
import com.idrv.coach.utils.helper.ViewUtils;
import com.idrv.coach.wxapi.WXEntryActivity;
import com.joooonho.SelectableRoundedImageView;
import com.zjb.loader.ZjbImageLoader;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * time:2016/3/9
 * description:任务大厅
 *
 * @author sunjianfei
 */
public class TaskActivity extends BaseActivity<NewsHallModel> implements View.OnClickListener {
    @InjectView(R.id.task_slogan)
    ImageView mSloganIv;
    @InjectView(R.id.oneTask11_card_view)
    CardView mCardView;
    @InjectView(R.id.oneTask11_timeDown)
    TimeDownHour mTimeDownHour;
    @InjectView(R.id.oneTask11_shareBt)
    Button mShareBtn;
    @InjectView(R.id.oneTask11_description)
    ImageView mDescribeIv;
    @InjectView(R.id.oneTask11_img)
    SelectableRoundedImageView mRoundedIv;
    @InjectView(R.id.oneTask11_descTv)
    TextView mDescTv;

    PopupWindow mPopupWindow;

    public static void launch(Context context) {
        Intent intent = new Intent(context, TaskActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_task);
        ButterKnife.inject(this);
        initModel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTimeDownHour.init(TimeUtil.getDownTime());
        mTimeDownHour.start();
    }

    @Override
    protected boolean isToolbarEnable() {
        return false;
    }

    @Override
    protected int getProgressBg() {
        return R.color.bg_main;
    }

    @OnClick({R.id.oneTask11_description, R.id.oneTask11_shareBt, R.id.oneTask11_img})
    @Override
    public void onClick(View v) {
        //防止重复快速点击
        ViewUtils.setDelayedClickable(v, 500);
        switch (v.getId()) {
            case R.id.oneTask11_description:
                showPopWindow(mViewModel.getTask().getDescribe(), mDescTv);
                break;
            case R.id.oneTask11_shareBt:
                share();
                break;
            case R.id.oneTask11_img:
                Task task = mViewModel.getTask();
                String url = task.getDetailUrl() + "/template/demo.html?coachId="
                        + LoginManager.getInstance().getUid() + "&taskId=" + task.getId();
//                NewsWebActivity.launch(this, url, task.getTaskName());
                break;
        }
    }

    private void setUp(Task task) {
        ZjbImageLoader.create(task.getTaskImgUrl())
                .setDisplayType(ZjbImageLoader.DISPLAY_DEFAULT)
                .setDefaultDrawable(new ColorDrawable(0xff000000))
                .into(mRoundedIv);
        mDescTv.setText(mViewModel.getTask().getDescribe());

        //下载分享的icon
        ZjbImageLoader.create(task.getShareIcon())
                .setDisplayType(ZjbImageLoader.DISPLAY_DEFAULT)
                .load();
    }

    /**
     * 初始化model
     */
    private void initModel() {
        mViewModel = new NewsHallModel();
        getCache();
    }

    /**
     * 获取缓存
     */
    private void getCache() {
        Subscription subscription = mViewModel.getTaskCache()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onNext, this::onCacheError, this::onCacheComplete);
        addSubscription(subscription);
    }

    /**
     * 获取最新任务
     */
    private void refresh() {
        Subscription subscription = mViewModel.getNewTask()
                .subscribe(this::onNext, this::onRefreshError, this::showContentView);
        addSubscription(subscription);
    }

    private void onNext(Task task) {
        setUp(task);
    }

    private void onCacheError(Throwable e) {
        showProgressView();
        refresh();
    }

    private void onCacheComplete() {
        refresh();
    }

    private void onRefreshError(Throwable e) {
        Logger.e(e);
    }

    /**
     * 弹出悬浮窗
     *
     * @param text
     * @param view
     */
    private void showPopWindow(String text, View view) {
        if (null != mPopupWindow && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        } else {
            final View contentView = LayoutInflater.from(this).inflate(
                    R.layout.vw_pop_task_description, null);

            mPopupWindow = new PopupWindow(contentView,
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setTouchable(true);

            TextView textView = (TextView) contentView.findViewById(R.id.pop_student_contacts);
            textView.setText(text);

            // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
            // 我觉得这里是API的一个bug
            mPopupWindow.setBackgroundDrawable(new ColorDrawable());
            // 设置好参数之后再show
//        popupWindow.showAsDropDown(view);

            int[] location = new int[2];
            view.getLocationOnScreen(location);

            mPopupWindow.showAtLocation(view, Gravity.NO_GRAVITY, location[0],
                    (int) (view.getTop() + PixelUtil.dp2px(20)));
        }
    }

    /**
     * 获取缓存图片地址
     *
     * @return
     */
    public String getImagePath() {
        String filePath = ZjbImageLoader.getQiniuDiskCachePath(mViewModel.getTask().getShareIcon());
        File imageFile = new File(filePath);
        if (imageFile != null && imageFile.exists()) {
            filePath = imageFile.getAbsolutePath();
        } else {
            filePath = BitmapUtil.saveBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_app));
        }
        return filePath;
    }

    /**
     * 分享
     */
    private void share() {
        ShareWebProvider provider = new ShareWebProvider();
        Task task = mViewModel.getTask();
        String uid = LoginManager.getInstance().getUid();
        String imagePath = getImagePath();
        String targetUrl = task.getDetailUrl();
        targetUrl = targetUrl + "?coachId=" + uid + "&taskId=" + task.getId();
        provider.setTitle(task.getShareTitle());
        provider.setDesc(task.getShareDescribe());
        provider.setImagePath(imagePath);
        provider.setUrl(targetUrl);
        WXEntryActivity.launch(this, provider, R.string.share_str);
    }
}
