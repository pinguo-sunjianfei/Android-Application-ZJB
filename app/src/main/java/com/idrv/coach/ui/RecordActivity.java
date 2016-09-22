package com.idrv.coach.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.idrv.coach.R;
import com.idrv.coach.bean.event.EventConstant;
import com.idrv.coach.data.manager.RxBusManager;
import com.idrv.coach.data.model.RecordModel;
import com.idrv.coach.ui.widget.RecordLayout;
import com.idrv.coach.ui.widget.RecorderClickBt;
import com.idrv.coach.utils.CountTimerUtil;
import com.idrv.coach.utils.EncryptUtil;
import com.idrv.coach.utils.Logger;
import com.idrv.coach.utils.PlayerUtil;
import com.idrv.coach.utils.helper.DialogHelper;
import com.idrv.coach.utils.helper.UIHelper;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.tencent.open.utils.Util;
import com.zjb.loader.internal.utils.L;
import com.zjb.volley.core.exception.NetworkError;
import com.zjb.volley.download.DownloadResult;
import com.zjb.volley.download.DownloadTask;
import com.zjb.volley.download.IDownloadListener;
import com.zjb.volley.utils.NetworkUtil;

import org.json.JSONObject;

import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Subscription;

/**
 * time: 2016/3/28
 * description: 录音界面
 * TODO 如下：
 * 1. 与IOS兼容，具体见 RecordManager，AudioEncoder为AAC，OutputFormat可尝试 MPEG_4
 * 2. 可否用isPlaying或者isRecording来代替 gState
 *
 * @author bigflower
 */
public class RecordActivity extends BaseActivity<RecordModel> implements RecorderClickBt.AudioRecorderListener {
    private static final String KEY_RECORD_URL = "recordUrl";

    private PlayerUtil mPlayerUtil;
    private TimeCount timer;

    @InjectView(R.id.record_longBt)
    RecorderClickBt mRecorderLongBt;
    @InjectView(R.id.record_recordLayout)
    RecordLayout mRecordLayout;
    @InjectView(R.id.record_voice)
    ImageView voiceIv;
    @InjectView(R.id.record_imgMicro)
    ImageView microIv;
    @InjectView(R.id.record_notice)
    TextView noticeTv;
    @InjectView(R.id.record_time)
    TextView timeTv;
    @InjectView(R.id.record_save)
    TextView saveTv;

    DownloadTask mDownloadTask;

    public static void launch(Context context, String recordUrl) {
        Intent intent = new Intent(context, RecordActivity.class);
        intent.putExtra(KEY_RECORD_URL, recordUrl);
        context.startActivity(intent);
    }

    @Override
    protected int getProgressBg() {
        return R.color.bg_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_record);
        ButterKnife.inject(this);

        //1.初始化
        mViewModel = new RecordModel();
        mPlayerUtil = new PlayerUtil();
        //2.初始化标题栏
        initToolBar();
        //3.初始化播放器
        initListener();
        //4.获取音频
        initRecord();
        //5.将上传按钮置为false,在xml中设置不管用，我猜因为你set了listener以后就自动true了
        saveTv.setClickable(false);
        getPerMission();
    }

    private void initToolBar() {
        mToolbarLayout.setTitle(R.string.title_record);
        mToolbarLayout.getRightTextView().setTextColor(Color.WHITE);
    }

    /**
     * 保存按钮
     *
     * @param v
     */
    @OnClick(R.id.record_save)
    void SaveClick(View v) {
        if (notClickable()) {
            return;
        }
        UpLoadRecord();
    }

    private void initRecord() {
        // 1.如果不存在网络音频，则从头开始；2.如果存在音频，则下载到本地
        String recordUrl = getIntent().getStringExtra(KEY_RECORD_URL);
        if (TextUtils.isEmpty(recordUrl)) {
            mViewModel.gDownloadRecordPath = null;
            // 1.1 界面初始化
            stateRecordNotExit();
            // 1.2 定时器初始化
            recordTimer();
            // 1.3 显示界面
            showContentView();
        } else {
            // 2.下载音频
            downRecord(recordUrl);
        }
    }


    @Override
    public void onClickEmpty() {
        super.onClickEmpty();
    }

    @Override
    public void onClickRetry() {
        if (NetworkUtil.isConnected(this)) {
            showProgressView();
            String recordUrl = getIntent().getStringExtra(KEY_RECORD_URL);
            if (TextUtils.isEmpty(recordUrl)) {
                downRecord(recordUrl);
            }
        } else {
            UIHelper.shortToast(R.string.network_error);
        }
    }

    @Override
    protected boolean hasBaseLayout() {
        return true;
    }

    /**
     * 下载录音
     *
     * @param recordUrl
     */
    private void downRecord(String recordUrl) {
        mDownloadTask = new DownloadTask(this, recordUrl,
                mViewModel.gDownloadRecordPath, new IDownloadListener() {
            @Override
            public void onDownloadStarted() {
                L.i("RecordActivity", "onDownloadStarted");
            }

            @Override
            public void onDownloadFinished(DownloadResult result) {
                Logger.i("onDownloadFinished " + result.toString());
                if (result.code == 0) {
                    onDownSuccess();
                    showContentView();
                } else {
                    onDownFailed();
                    showErrorView();
                }
            }

            @Override
            public void onProgressUpdate(Float... value) {
                Logger.i("onProgressUpdate " + value);
            }
        });
        mDownloadTask.execute();
    }

    private void onDownSuccess() {
        mViewModel.gNowPath = mViewModel.gDownloadRecordPath;
        Logger.i("音频链接:" + mViewModel.gNowPath);
        stateRecordExit();
        mPlayerUtil.setResource(mViewModel.gNowPath);
    }

    private void onDownFailed() {
        mViewModel.gDownloadRecordPath = null;
        stateRecordNotExit();
        initListener();
        recordTimer();
    }

    /**
     * 有录音
     * UI变换：出现播放或暂停
     */
    private void stateRecordExit() {
        mViewModel.gState = mViewModel.STATE_VOICE;
        noticeTv.setText("");
        microIv.setVisibility(View.GONE);
        voiceIv.setVisibility(View.GONE);
        mRecordLayout.setState(mRecordLayout.STATE_NORMAL);
    }

    /**
     * 没有录音
     * UI变换: 出现麦克风和音量
     */
    private void stateRecordNotExit() {
        Logger.i("出现麦克风和音量");
        mViewModel.gState = mViewModel.STATE_RECORD;
        microIv.setVisibility(View.VISIBLE);
        voiceIv.setVisibility(View.VISIBLE);
        mRecordLayout.setProgressInit();
        mRecordLayout.setState(mRecordLayout.STATE_NULL);
    }

    /**
     * 保存录音按钮的UI变化及使能
     *
     * @param isClickable
     */
    private void setSaveClickable(boolean isClickable) {
        saveTv.setBackgroundResource(isClickable ? R.drawable.shape_record_upload : R.drawable.shape_record_upload_unable);
        saveTv.setClickable(isClickable);
    }

    //////////////////////////////////////////////////////
    // 上面是界面的不同状态
    // 下面是录音操作
    //////////////////////////////////////////////////////
    private void initListener() {
        // 录音按钮的监听
        mRecorderLongBt.setAudioRecorderListener(this);
        mRecorderLongBt.setOnLongClickListener(v -> onRecordBtnLongClick());
        // 音频播放的监听
        mPlayerUtil.setOnPlayListener(new PlayerUtil.OnPlayListener() {
            @Override
            public void OnCompleted() { // 播放完成
                mRecordLayout.setState(mRecordLayout.STATE_NORMAL);
            }

            @Override
            public void OnPrepared() { // 播放准备完成
                Logger.i("prepeared " + mViewModel.gNowPath);
                int timelength = mPlayerUtil.getDuration() / 1000;
                Logger.i("prepeared " + timelength);
                if (timelength == 0) {
                    timeTv.setText("00:00");
                } else {
                    second2TextShow(timelength);
                }
                dismissProgressDialog();
            }

            @Override
            public void OnError(String errorInfo) { // 播放出错

            }
        });
        // 播放按钮的监听
        mRecordLayout.setOnRecordLayoutListener(new RecordLayout.OnRecordLayoutListener() {
            @Override
            public void play() {
                Logger.e("play", mViewModel.gNowPath);
                mPlayerUtil.setResource(mViewModel.gNowPath);
                mPlayerUtil.prepare();
                mPlayerUtil.start();
                int duration = mPlayerUtil.getDuration();
                voiceTimer(duration);
                mRecordLayout.setMediaDuration(duration);
                if (timer != null)
                    timer.start();
            }

            @Override
            public void restart() {
                mPlayerUtil.start();
                if (timer != null)
                    timer.restart();
            }

            @Override
            public void pause() {
                mPlayerUtil.pause();
                if (timer != null)
                    timer.pause();
            }

        });
    }

    /**
     * 判断录音长度，是否超过10s
     *
     * @return
     */
    private boolean isOverTenSeconds() {
        String[] times = timeTv.getText().toString().split(":");
        Logger.i(times[0] + " " + times[1]);
        if (Integer.parseInt(times[0]) > 0) {
            return true;
        } else if (Integer.parseInt(times[1]) > 9) {
            return true;
        }
        return false;
    }

    ////////////////////////////////////////////////////////////////////////
    // 退出登录时的判断，正在播放，以及录制了没有上传，都要提示下！！！
    ////////////////////////////////////////////////////////////////////////
    @Override
    public void onToolbarLeftClick(View view) {
        backClick();
    }

    @Override
    public void onBackPressed() {
        backClick();
    }

    private void backClick() {
        // 是否正在录音的判断
        if (!mRecorderLongBt.isRecording() && !mViewModel.isNotUpload()) {
            finish();
            return;
        }
        showExitNotice();
    }

    private void showExitNotice() {
        DialogHelper.create(DialogHelper.TYPE_NORMAL)
                .leftButton(getString(R.string.cancel), 0xffd03b3b)
                .rightButton(getString(R.string.leave), 0xff2b2a2a)
                .title(getString(R.string.is_exit_unsave_record))
                .content("")
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .leftBtnClickListener((dialog, view) -> {
                    dialog.dismiss();
                })
                .rightBtnClickListener((dialog, view) -> {
                    dialog.dismiss();
                    finish();
                })
                .show();
    }


    /////////////////////////////////////////////////////////
    // 上传录音
    /////////////////////////////////////////////////////////
    private void UpLoadRecord() {
        mViewModel.mEtagKey = EncryptUtil.getQETAG(mViewModel.gRecordLocalTemp) + ".mp3";
        mViewModel.mQiNiuFilePath = mViewModel.gRecordLocalTemp;
        // 如果etag为空，则说明没有该图片路径啊
        if (mViewModel.mEtagKey == null) {
            UIHelper.shortToast("上传失败，请重新上传");
            return;
        }
        mProgressDialog = DialogHelper.create(DialogHelper.TYPE_PROGRESS)
                .progressText(getString(R.string.dialog_uploading)).show();
        Subscription subscription = mViewModel.getToken()
                .subscribe(__ -> isFileExit(), this::onError);
        addSubscription(subscription);
    }

    /**
     * 判断网上该文件是否存在
     */
    private void isFileExit() {
        Subscription subscription = mViewModel.isFileExit()
                .subscribe(__ -> httpCoachInfo(), __ -> imgUpload());
        addSubscription(subscription);
    }

    /**
     * 通过七牛上传文件
     */
    private void imgUpload() {
        mViewModel.upLoad(new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info,
                                 JSONObject response) {
                if (info.isOK()) {
                    httpCoachInfo();
                } else {
                    dismissProgressDialog();
                    Logger.e("imgUpload", info.error);
                    UIHelper.shortToast(info.error);
                }
            }
        });
    }

    /**
     * 将文件地址上传到服务器
     */
    private void httpCoachInfo() {
        Subscription subscription = mViewModel.putCoachInfo()
                .subscribe(__ -> onUpLoadSuccess(), this::onError);
        addSubscription(subscription);
    }

    /**
     * 最终的，上传成功后，的处理
     */
    private void onUpLoadSuccess() {
        dismissProgressDialog();

        RxBusManager.post(EventConstant.KEY_REVISE_RECORD, mViewModel.mImgUrl);
        UIHelper.shortToast(R.string.upload_success);
        finish();
    }

    private void onError(Throwable e) {
        dismissProgressDialog();
        if (null != e) {
            if (e instanceof NetworkError) {
                NetworkError error = (NetworkError) e;
                UIHelper.shortToast(error.getErrorCode().getMessage());
            } else {
                UIHelper.shortToast(R.string.upload_error);
                e.printStackTrace();
            }
        }
    }
    /////////////////////////////////////////////////////////
    // 计时相关
    /////////////////////////////////////////////////////////

    /**
     * 录音，倒计时
     */
    private void recordTimer() {
        timer = new TimeCount(mViewModel.MAX_RECORD_TIME * 1000, 1000);
    }

    /**
     * 播放录音，倒计时，时间不定
     */
    private void voiceTimer() {
        voiceTimer(mPlayerUtil.getDuration());
    }

    private void voiceTimer(int voiceTime) {
        timer = new TimeCount(voiceTime, 100);
        second2TextShow(voiceTime / 1000);
    }

    @Override
    public void onNoPermission() { // 没有权限
        if (timer != null)
            timer.cancel();
        noticeTv.setText("");
        if (mViewModel.gNowPath != null) {
            timeTv.setText("00:00");
        } else if (!TextUtils.isEmpty(mViewModel.gDownloadRecordPath)) {
            mViewModel.gNowPath = mViewModel.gDownloadRecordPath;
            stateRecordExit();
        } else {
            stateRecordNotExit();
        }
        UIHelper.shortToast(R.string.pls_open_the_permission);
    }

    @Override
    public void onRecordStart() {
        mPlayerUtil.pause();
        if (timer != null)
            timer.cancel();
        recordTimer();
        timer.start();

        noticeTv.setText("");
        timeTv.setText("00:00");
        setSaveClickable(false);
        stateRecordNotExit();
    }

    @Override
    public void onVoiceLevel(int level) {  // 音量变化
        runOnUiThread(() -> {
            voiceIv.setImageResource(mViewModel.voiceLevels[level]);
        });
    }

    @Override
    public void onCancel() { // 录音取消
        timer.cancel();
        setSaveClickable(false);
        // 取消之后，有本地录音，则应该显示播放界面
        if (Util.isEmpty(mViewModel.gDownloadRecordPath)) {
            timeTv.setText("00:00");
            noticeTv.setText("");
            stateRecordNotExit();
        } else {
            mViewModel.gNowPath = mViewModel.gDownloadRecordPath;
            mPlayerUtil.setResource(mViewModel.gNowPath);
            Logger.i("onCancel", "not ok  2 " + mPlayerUtil.getDuration());
            second2TextShow(mPlayerUtil.getDuration() / 1000);
            stateRecordExit();
        }
    }

    @Override
    public void onOk() { // 录音成功
        if (isOverTenSeconds()) {
            Logger.i("onOk");
            mPlayerUtil.release();
            timer.cancel();
            setSaveClickable(true);
            mViewModel.gNowPath = mViewModel.gRecordLocalTemp;
            // 录音成功，界面变换
            stateRecordExit();
            mRecorderLongBt.setText(R.string.press_long_record_again);
            noticeTv.setText(R.string.record_notice2);
        } else {
            Logger.i("not onOk");
            UIHelper.shortToast(R.string.record_too_short);
            onCancel();
        }

    }

    /**
     * 先获取录音权限
     */
    private void getPerMission() {
        RxPermissions.getInstance(this)
                .request(Manifest.permission.RECORD_AUDIO
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {
                        Logger.e("有录音权限");
                    } else {
                        Logger.e("没有录音权限");
                    }
                }, Logger::e);
    }

    public boolean onRecordBtnLongClick() {
        RxPermissions.getInstance(this)
                .request(Manifest.permission.RECORD_AUDIO
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {
                        Logger.e("开启录音权限");
                        mRecorderLongBt.onLongClick();
                    } else {
                        UIHelper.shortToast(R.string.open_record_permission);
                    }
                }, Logger::e);
        return false;
    }


    // 倒计时类
    class TimeCount extends CountTimerUtil {
        // total time and the interval time
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished, int percent) {
            showTime((millisUntilFinished) / 1000);
            Logger.i("时间结束：？" + millisUntilFinished);
        }

        @Override
        public void onFinish() {
            // 时间到了，结束录音，并保存，等等
            if (mViewModel.gState == mViewModel.STATE_VOICE) {
                showTime(mPlayerUtil.getDuration() / 1000);
            } else if (mViewModel.gState == mViewModel.STATE_RECORD) {
                mRecorderLongBt.timeIsOver();
                Logger.i("时间结束：应该显示max-0？");
                second2TextShow(mViewModel.MAX_RECORD_TIME);
            }
        }
    }

    /**
     * 显示时间
     *
     * @param second
     */
    private void showTime(long second) {
        int showTime = (int) second;
        if (mViewModel.gState == mViewModel.STATE_RECORD) {
            if (showTime < 11) {
                noticeTv.setText("录音时间还剩" + showTime + "秒，请注意时间");
            }
            showTime = mViewModel.MAX_RECORD_TIME - showTime;
            second2TextShow(showTime);
        } else {
            mRecordLayout.setProgress(showTime);
        }
    }

    /**
     * 将秒转换成String格式的时间并显示
     *
     * @param second
     */
    private void second2TextShow(int second) {
        int realSecond = second % 60;
        StringBuilder sb = new StringBuilder();
        int m = second / 60;
        m = Math.abs(m);
        sb.append("0").append(String.valueOf(m)).append(":");
        if (realSecond < 10) {
            sb.append("0");
        }
        sb.append(String.valueOf(realSecond));
        timeTv.setText(sb.toString());
    }

    @Override
    protected void onPause() {
        super.onPause();
        mRecordLayout.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null)
            timer.cancel();
        mRecorderLongBt.release();
        mPlayerUtil.release();
        if (null != mDownloadTask) {
            mDownloadTask.cancel(true);
        }
    }

    /**
     * 避免重复点击，也可以避免点击两个
     */
    long lastClickTime = 0;
    long currentTime = 0;

    private boolean notClickable() {
        currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > 1000) {
            lastClickTime = currentTime;
            return false;
        } else {
            return true;
        }
    }
}
