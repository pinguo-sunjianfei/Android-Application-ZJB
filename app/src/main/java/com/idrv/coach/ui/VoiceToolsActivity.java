package com.idrv.coach.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.idrv.coach.R;
import com.idrv.coach.bean.VoiceTool;
import com.idrv.coach.data.model.VoiceToolModel;
import com.idrv.coach.ui.adapter.VoiceToolsAdapter;
import com.idrv.coach.utils.Logger;
import com.idrv.coach.utils.PlayerUtil;
import com.idrv.coach.utils.helper.UIHelper;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.zjb.volley.utils.NetworkUtil;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscription;

/**
 * time:2016/8/16
 * description:
 *
 * @author sunjianfei
 */
public class VoiceToolsActivity extends BaseActivity<VoiceToolModel> implements VoiceToolsAdapter.OnVoicePlayListener {
    @InjectView(R.id.business_recycler_view)
    RecyclerView mRecyclerView;
    private PlayerUtil mPlayerUtil;

    VoiceToolsAdapter mAdapter;
    long mDefaultScreenOffTime = -1;

    public static void launch(Context context) {
        Intent intent = new Intent(context, VoiceToolsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_voice_tools);
        ButterKnife.inject(this);
        initToolBar();
        initView();
        initViewModel();
        setScreenOffTime(60 * 60 * 1000);
    }

    @Override
    protected void onResume() {
        mPlayerUtil.resume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mPlayerUtil.pause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mPlayerUtil.release();
        if (mDefaultScreenOffTime != -1) {
            setScreenOffTime(mDefaultScreenOffTime);
        }
        super.onDestroy();
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
        mToolbarLayout.setTitle(R.string.voice_broadcast);
    }

    private void initView() {
        mAdapter = new VoiceToolsAdapter();
        mAdapter.setOnVoicePlayListener(this);
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 4);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0 || position == 9) {
                    return 4;
                }
                return 1;
            }
        });
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initViewModel() {
        mPlayerUtil = new PlayerUtil();
        mViewModel = new VoiceToolModel();
        refresh();
    }

    private void refresh() {
        Subscription subscription = mViewModel.getVoiceToolList()
                .subscribe(this::onNext, e -> showErrorView(), this::showContentView);
        addSubscription(subscription);
    }

    private void onNext(List<VoiceTool> list) {
        mAdapter.setData(list);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 设置熄屏时间
     */
    private void setScreenOffTime(long time) {
        RxPermissions.getInstance(this)
                .request(Manifest.permission.WRITE_SETTINGS)
                .subscribe(granted -> {
                    if (granted) {
                        try {
                            //获取自动灭屏的时间
                            if (mDefaultScreenOffTime == -1) {
                                mDefaultScreenOffTime = Settings.System.getLong(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);
                            }
                            Settings.System.putLong(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, time);
                        } catch (Settings.SettingNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Logger.e("no settings write permission!");
                    }
                }, Logger::e);
    }

    @Override
    public void onPlay(VoiceTool tool, boolean isPlay, int position) {
        mPlayerUtil.release();
        if (isPlay) {
            mPlayerUtil.setResource(tool.getResId());
            mPlayerUtil.setOnPlayListener(new PlayerUtil.OnPlayListener() {
                @Override
                public void OnCompleted() {
                    tool.setPlay(false);
                    mAdapter.notifyItemChanged(position);
                    mPlayerUtil.release();
                }

                @Override
                public void OnPrepared() {

                }

                @Override
                public void OnError(String errorInfo) {

                }
            });
            mPlayerUtil.start();
        } else {
            mPlayerUtil.release();
        }

    }
}
