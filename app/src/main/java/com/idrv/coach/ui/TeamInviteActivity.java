package com.idrv.coach.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import com.idrv.coach.R;
import com.idrv.coach.bean.TeamMember;
import com.idrv.coach.bean.event.EventConstant;
import com.idrv.coach.data.manager.RxBusManager;
import com.idrv.coach.data.model.TeamInviteModel;
import com.idrv.coach.ui.adapter.TeamCreateAdapter;
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
 * time:2016/3/24
 * description: 邀请成员
 *
 * @author bigflower
 */
public class TeamInviteActivity extends BaseActivity<TeamInviteModel> implements TeamCreateAdapter.OnChooseListener {

    private static final String INTENT_EXTRA_MEMBERS = "teamMembers";
    private static final String INTENT_EXTRA_ID = "teamId";

    @InjectView(R.id.team_invite_recyclerView)
    RecyclerView mRecyclerView;
    @InjectView(R.id.team_invite_bottomBt)
    Button mBottomBt;

    private TeamCreateAdapter mAdapter;

    public static void launch(Context context, ArrayList<TeamMember> list, int teamId) {
        Intent intent = new Intent(context, TeamInviteActivity.class);
        intent.putParcelableArrayListExtra(INTENT_EXTRA_MEMBERS, list);
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
        setContentView(R.layout.act_team_invite);
        ButterKnife.inject(this);

        //0.model
        mViewModel = new TeamInviteModel();
        //1.初始化标题栏
        initToolBar();
        //2.初始化界面
        initUi();
    }

    private void initToolBar() {
        mToolbarLayout.setTitle(R.string.title_invite_mumber);
    }

    private void initUi() {
        // 获取成员信息
        List<TeamMember> inviteMembers = getIntent().getParcelableArrayListExtra(INTENT_EXTRA_MEMBERS);
        mViewModel.teamId = getIntent().getIntExtra(INTENT_EXTRA_ID, -1);
        try {
            // 初始化RecyclerView
            initRecyclerView(inviteMembers);
            // 初始化底部的按钮
            mViewModel.gButtonName = getString(R.string.invite);
            mViewModel.gTotalNumer = inviteMembers.size();
            mBottomBt.setText(mViewModel.gButtonName + "(0/" + mViewModel.gTotalNumer + ")");
        } catch (Exception e) {
            UIHelper.shortToast(R.string.error);
            finish();
        }
    }

    private void initRecyclerView(List<TeamMember> teamNumberList) {
        final GridLayoutManager mLayoutManager = new GridLayoutManager(this, 5);
        mAdapter = new TeamCreateAdapter(this, this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setData(mViewModel.listDeduplication(teamNumberList));
    }

    @OnClick(R.id.team_invite_bottomBt)
    void invite() {
        String inviteMembers = mAdapter.getChoosedIds();
        if (inviteMembers.length() == 0) {
            UIHelper.shortToast(R.string.pls_invite_someone);
            return;
        }
        mProgressDialog = DialogHelper
                .create(DialogHelper.TYPE_PROGRESS)
                .progressText(getString(R.string.dialog_inviting))
                .show();
        Subscription subscription = mViewModel.inviteMumber(inviteMembers)
                .subscribe(__ -> onNext(), this::onTeamInfoError);
        addSubscription(subscription);
    }

    private void onNext() {
        dismissProgressDialog();
        UIHelper.shortToast(R.string.invite_success);
        RxBusManager.post(EventConstant.KEY_TEAM_MUMBER, "");
        finish();
    }

    private void onTeamInfoError(Throwable e) {
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
    public void onChoose(int number) {
        mBottomBt.setText(mViewModel.gButtonName + "(" + number + "/" + mViewModel.gTotalNumer + ")");
    }
}
