package com.idrv.coach.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.idrv.coach.R;
import com.idrv.coach.bean.TeamMember;
import com.idrv.coach.bean.event.EventConstant;
import com.idrv.coach.data.manager.RxBusManager;
import com.idrv.coach.data.model.TeamCreateModel;
import com.idrv.coach.ui.adapter.TeamCreateAdapter;
import com.idrv.coach.ui.widget.ClearEditText;
import com.idrv.coach.utils.helper.DialogHelper;
import com.idrv.coach.utils.helper.UIHelper;
import com.zjb.volley.core.exception.NetworkError;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Subscription;

/**
 * time:2016/3/23
 * description:
 * 创建团队
 * 分为两个步骤：输入名字，邀请队员（非必需）
 *
 * @author bigflower
 */
public class TeamCreateActivity extends BaseActivity<TeamCreateModel> {

    private static final String INTENT_EXTRA_MEMBERS = "teamMembers";

    @InjectView(R.id.create_team_nameEt)
    ClearEditText mNameEt;
    @InjectView(R.id.create_team_bottomBt)
    Button mBottomBt;
    @InjectView(R.id.create_team_recyclerView)
    RecyclerView mRecyclerView;
    @InjectView(R.id.create_team_layoutName)
    View mNameLayout;
    @InjectView(R.id.create_team_layoutMumber)
    LinearLayout mMemberLayout;
    @InjectView(R.id.team_name_length_tv)
    TextView mTeamNameLengthTv;

    private TeamCreateAdapter mAdapter;

    public static void launch(Context context, ArrayList<TeamMember> members) {
        Intent intent = new Intent(context, TeamCreateActivity.class);
        intent.putParcelableArrayListExtra(INTENT_EXTRA_MEMBERS, members);
        context.startActivity(intent);
    }

    @Override
    protected int getProgressBg() {
        return R.color.bg_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_team_create);
        ButterKnife.inject(this);

        //0.model
        mViewModel = new TeamCreateModel();
        //1.初始化标题栏
        initToolBar();
        //2.初始化界面
        initUI();
    }

    private void initToolBar() {
        mToolbarLayout.setTitle(R.string.title_create_team);
    }

    private void initUI() {
        mNameEt.setOnTextChangeListener((s, start, count, after) -> mTeamNameLengthTv.setText("" + (15 - s.length())));

        // 获取成员信息
        List<TeamMember> inviteMembers = getIntent().getParcelableArrayListExtra(INTENT_EXTRA_MEMBERS);
        try {
            // 初始化RecyclerView
            initRecyclerView(inviteMembers);
            // 初始化底部的按钮
            mViewModel.gButtonName = getString(R.string.create_team_over);
            mViewModel.gTotalNumber = inviteMembers.size();
        } catch (Exception e) {
            UIHelper.shortToast(R.string.error);
            finish();
        }

    }

    private void initRecyclerView(List<TeamMember> teamMemberList) {
        final GridLayoutManager mLayoutManager = new GridLayoutManager(this, 5);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter = new TeamCreateAdapter(this, number -> {
            mBottomBt.setText(mViewModel.gButtonName + "(" + number + "/" + mViewModel.gTotalNumber + ")");
        }));
        mAdapter.setData(teamMemberList);
    }

    @OnClick(R.id.create_team_bottomBt)
    void click() {
        if (getString(R.string.next_step).equals(mBottomBt.getText().toString())) {
            mViewModel.gTeamName = mNameEt.getText().toString().trim();
            if (TextUtils.isEmpty(mViewModel.gTeamName)) {
                UIHelper.shortToast(R.string.team_name_not_be_null);
                return;
            }
            UIHelper.hideSoftInput(mNameEt);
            mNameLayout.setVisibility(View.GONE);
            mMemberLayout.setVisibility(View.VISIBLE);
            mBottomBt.setText(mViewModel.gButtonName +
                    "(" + mAdapter.getChoosedSize() + "/" + mViewModel.gTotalNumber + ")");
        } else {
            createTeam();
        }
    }

    private void createTeam() {
        mProgressDialog = DialogHelper.create(DialogHelper.TYPE_PROGRESS)
                .progressText(getString(R.string.dialog_creating)).show();
        Subscription subscription = mViewModel.createTeam(mViewModel.gTeamName, mAdapter.getChoosedIds())
                .subscribe(this::onNext, this::onError);
        addSubscription(subscription);
    }

    private void onNext(int teamId) {
        // 将团队信息发送给上一个界面TeamInfoActivity
        dismissProgressDialog();
        RxBusManager.post(EventConstant.KEY_TEAM_CREATE, "");
        UIHelper.shortToast(R.string.create_success);
        finish();
    }

    private void onError(Throwable e) {
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
    public void onToolbarLeftClick(View view) {
        if (getString(R.string.next_step).equals(mBottomBt.getText().toString())) {
            finish();
        } else {
            mNameLayout.setVisibility(View.VISIBLE);
            mMemberLayout.setVisibility(View.GONE);
            mBottomBt.setText(getString(R.string.next_step));
        }
    }

    @Override
    public void onBackPressed() {
        if (getString(R.string.next_step).equals(mBottomBt.getText().toString())) {
            finish();
        } else {
            mNameLayout.setVisibility(View.VISIBLE);
            mMemberLayout.setVisibility(View.GONE);
            mBottomBt.setText(getString(R.string.next_step));
        }
    }

}
