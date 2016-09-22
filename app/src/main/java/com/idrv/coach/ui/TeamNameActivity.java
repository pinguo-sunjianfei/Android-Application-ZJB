package com.idrv.coach.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.idrv.coach.R;
import com.idrv.coach.bean.event.EventConstant;
import com.idrv.coach.data.manager.RxBusManager;
import com.idrv.coach.data.model.TeamNameModel;
import com.idrv.coach.ui.widget.ClearEditText;
import com.idrv.coach.utils.helper.DialogHelper;
import com.idrv.coach.utils.helper.UIHelper;
import com.zjb.volley.core.exception.NetworkError;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscription;

/**
 * time:2016/3/22
 * description: 团队名称
 *
 * @author bigflower
 */

public class TeamNameActivity extends BaseActivity<TeamNameModel> {

    private static final String INTENT_EXTRA_NAME = "teamName";
    private static final String INTENT_EXTRA_ID = "teamId";

    @InjectView(R.id.team_name_et)
    ClearEditText mTeamNameEt;
    @InjectView(R.id.team_leftNumber_tv)
    TextView mLeftNumberTv;

    /**
     * 启动
     * @param context
     * @param oldName 缓存之前的名字，判断新的值是否有改变
     * @param teamId 团队id
     */
    public static void launch(Context context, String oldName, int teamId) {
        Intent intent = new Intent(context, TeamNameActivity.class);
        intent.putExtra(INTENT_EXTRA_NAME, oldName);
        intent.putExtra(INTENT_EXTRA_ID, teamId);
        context.startActivity(intent);
    }
    @Override
    protected int getProgressBg() {
        return R.color.bg_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_team_name);
        ButterKnife.inject(this);

        // 0.初始化ViewModel
        mViewModel = new TeamNameModel();

        //1.初始化标题栏
        initToolBar();
        //2.监听edit的输入
        initListener();
        //3.初始化界面
        initUI();
    }
    /**
     * 初始化标题
     */
    private void initToolBar() {
        mToolbarLayout.setTitle(R.string.title_team_name);
        mToolbarLayout.setRightTxt("保存");
        mToolbarLayout.getRightTextView().setTextColor(Color.WHITE);
        mToolbarLayout.getRightTextView().setOnClickListener(v -> {
            reviseName();
        });
    }

    /**
     * 初始化输入监听
     */
    private void initListener() {
        mTeamNameEt.setOnTextChangeListener((s, start, count, after) -> {
            mLeftNumberTv.setText("" + (15 - s.length()));
        });
    }
    /**
     * 初始化部分UI
     */
    private void initUI() {
        mViewModel.gName = getIntent().getStringExtra(INTENT_EXTRA_NAME);
        mViewModel.gTeamId = getIntent().getIntExtra(INTENT_EXTRA_ID, -1);
        mTeamNameEt.setText(mViewModel.gName);
        mTeamNameEt.setSelection(mViewModel.gName.length());
    }

    /**
     * 修改名字
     */
    private void reviseName(){
        String name = mTeamNameEt.getText().toString().trim();
        // 输入为空，不行
        if(TextUtils.isEmpty(name)) {
            UIHelper.shortToast(R.string.team_name_not_be_null);
            return;
        }
        // 输入未更改，直接关闭
        if(name.equals(mViewModel.gName)) {
            finish();
        }
        // 网络请求
        mProgressDialog = DialogHelper.create(DialogHelper.TYPE_PROGRESS)
                .progressText(getString(R.string.dialog_revising)).show();
        Subscription subscription = mViewModel.reviseTeamName(name)
                .subscribe(this::onNext, this::onTeamNameError);
        addSubscription(subscription);
    }

    private void onNext(String name){
        UIHelper.shortToast(R.string.revise_success);
        RxBusManager.post(EventConstant.KEY_TEAM_NAME, name);
        finish();
    }

    private void onTeamNameError(Throwable e) {
        dismissProgressDialog();
        if (null != e) {
            if (e instanceof NetworkError) {
                NetworkError error = (NetworkError) e;
                UIHelper.shortToast(error.getErrorCode().getMessage());
            } else {
                UIHelper.shortToast(R.string.http_error);
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        UIHelper.hideSoftInput(mTeamNameEt);
    }
}
