package com.idrv.coach.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.idrv.coach.R;
import com.idrv.coach.bean.TeamInfo;
import com.idrv.coach.bean.TeamMember;
import com.idrv.coach.bean.event.EventConstant;
import com.idrv.coach.data.constants.SPConstant;
import com.idrv.coach.data.manager.LoginManager;
import com.idrv.coach.data.manager.RxBusManager;
import com.idrv.coach.data.model.TeamMineModel;
import com.idrv.coach.ui.adapter.TeamMemberAdapter;
import com.idrv.coach.ui.view.MasterItemView;
import com.idrv.coach.ui.widget.BaseLayout;
import com.idrv.coach.utils.Logger;
import com.idrv.coach.utils.PreferenceUtil;
import com.idrv.coach.utils.helper.UIHelper;
import com.zjb.volley.core.exception.NetworkError;
import com.zjb.volley.utils.NetworkUtil;

import java.util.Iterator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;


/**
 * time:2016/3/22
 * description: 我的团队
 *
 * @author bigflower
 */
public class TeamMineActivity extends BaseActivity<TeamMineModel> {

    @InjectView(R.id.myteam_item_teamName)
    MasterItemView mMyItemItemView;
    @InjectView(R.id.myteam_numberSumTv)
    TextView munberTv;
    @InjectView(R.id.myteam_recyclerView)
    RecyclerView mRecyclerView;

    private TeamMemberAdapter mAdapter;

    public static void launch(Context context) {
        Intent intent = new Intent(context, TeamMineActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getProgressBg() {
        return R.color.bg_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_team_mine);
        ButterKnife.inject(this);

        // 0.初始化ViewModel
        mViewModel = new TeamMineModel();

        //1.初始化标题栏
        initToolBar();
        //2.初始化团队名称item
        initItem();
        //3.初始化RecyclerView
        initRecyclerView();
        //4.获取我的团队信息
        getSignState();
        //5.注册事件
        registerEvent();
    }

    private void initToolBar() {
        mToolbarLayout.setTitle(R.string.title_myteam);
    }

    private void initItem() {
        mMyItemItemView.setText(R.string.myteam_item_teamname);
        mMyItemItemView.setLineVisible(View.GONE);
        mMyItemItemView.setOnMasterItemClickListener(v -> {
            TeamNameActivity.launch(this,
                    mViewModel.gTeamInfo.getTeam().getName(),
                    mViewModel.gTeamInfo.getTeam().getId()
            );
        });
    }

    /**
     * 初始化RecyclerView， adapter有一个接口：点击加号
     */
    private void initRecyclerView() {
        final GridLayoutManager mLayoutManager = new GridLayoutManager(this, 5);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new TeamMemberAdapter(this,() -> {
                    if (mViewModel.gTeamInfo.getInviteUsers() == null
                            || mViewModel.gTeamInfo.getInviteUsers().size() == 0) {
                        noInviterShow();
                    } else {
                        TeamInviteActivity.launch(this, mViewModel.gTeamInfo.getInviteUsers()
                                , mViewModel.gTeamInfo.getTeam().getId());
                    }
                });
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 获取是否是签约教练
     */
    private void getSignState() {
        Subscription subscription = mViewModel.getSignState()
                .subscribe(__ -> getMyTeam(), this::onTeamInfoError);
        addSubscription(subscription);
    }

    /**
     * 获取我的团队
     */
    private void getMyTeam() {
        Subscription subscription = mViewModel.getMyTeam()
                .subscribe(this::onNext, this::onTeamInfoError);
        addSubscription(subscription);
    }

    /**
     * 获取团队信息成功
     * <p>
     * 5种状态：3中显示空、2种显示团队（有team）
     *
     * @param teamInfo
     */
    private void onNext(TeamInfo teamInfo) {
        switch (mViewModel.gType) {
            case TeamMineModel.TYPE_ERROR:
                showErrorView();
                break;
            case TeamMineModel.TYPE_NOSIGN:
                onTeamShow(teamInfo);
                break;
            case TeamMineModel.TYPE_NOSIGN_NOTEAM:
                showEmptyView();
                break;
            case TeamMineModel.TYPE_SIGN:
                onTeamShow(teamInfo);
                break;
            case TeamMineModel.TYPE_SIGN_NOTEAM:
                // 未弹出过提示框，则弹出（仅弹出一次）
                if (!PreferenceUtil.getBoolean(SPConstant.KEY_CREATE_TEAM_NOTICED, false)) {
                    PreferenceUtil.putBoolean(SPConstant.KEY_CREATE_TEAM_NOTICED, true);
                    newFunctionShow();
                }
                mBaseLayout.setEmptyView(this, R.layout.vw_team_empty_signed);
                showEmptyView();
                break;
            case TeamMineModel.TYPE_SIGN_NOINVITE: // 没有同一个邀请码的成员（即没有人可以邀请）
                mBaseLayout.setEmptyView(this, R.layout.vw_team_noinvite);
                showEmptyView();
                break;
        }
    }

    /**
     * 如果有team，则显示
     *
     * @param teamInfo
     */
    private void onTeamShow(TeamInfo teamInfo) {
        // 1.获取团队名称
        String teamName = teamInfo.getTeam().getName();
        mMyItemItemView.setRightText(teamName);
        // 2.处理adapter
        mAdapter.clear();
        // 2.1判断教练的类型
        if (mViewModel.gType == TeamMineModel.TYPE_SIGN) {
            mAdapter.addData(new TeamMember(getString(R.string.add_mumber), "drawable://" + R.drawable.icon_team_add));
            mAdapter.setInvitable(true);
            if (teamInfo.getTeam().getUsers() != null) {
                // 显示已邀请人数
                int numberOfPeople = teamInfo.getTeam().getUsers().size();
                munberTv.setText(getString(R.string.have_invited) + numberOfPeople + getString(R.string.person));
            }
            inviteUserFilter();
        } else {
            mMyItemItemView.setRightArrowVisible(View.GONE);
            mMyItemItemView.setOnMasterItemClickListener(null);
        }
        // 显示网格
        List<TeamMember> users = teamInfo.getTeam().getUsers();
        mAdapter.addData(users);
        mAdapter.notifyDataSetChanged();
        showContentView();
    }

    /**
     * 如果出现了自己，过滤掉。
     * 自己显然不能邀请自己
     */
    private void inviteUserFilter() {
        String uid = LoginManager.getInstance().getUid();
        if (TextUtils.isEmpty(uid)) {
            return;
        }
        if (mViewModel.gTeamInfo.getInviteUsers() == null) {
            return;
        }
        // list删除最好还是用这种
        Iterator<TeamMember> it = mViewModel.gTeamInfo.getInviteUsers().iterator();
        while (it.hasNext()) {
            TeamMember teamMember = it.next();
            Logger.i(teamMember.toString());
            if (uid.equals(teamMember.getId())) {
                it.remove();
            }
        }
    }

    private void onTeamInfoError(Throwable e) {
        dismissProgressDialog();
        showErrorView();
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

    /**
     * 如果签约教练没有创建团队，则弹出这个dialog
     */
    private void newFunctionShow() {
        Dialog dialog = new Dialog(this, R.style.BaseDialog);
        dialog.setContentView(R.layout.dlg_team_new_function);
        dialog.setCancelable(false);
        dialog.findViewById(R.id.dlg_newfunction_know_bt).setOnClickListener(view -> {
            dialog.dismiss();
        });
        dialog.show();
    }

    /**
     * 没有可邀请的成员 的提示
     */
    private void noInviterShow() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(R.layout.dlg_team_noinviter);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.findViewById(R.id.dlg_noinviter_bt).setOnClickListener(v -> dialog.dismiss());
    }

    @Override
    public void onClickEmpty() {
        super.onClickEmpty();
        //这里要做判断，不是谁都可以点的 根据 gType, 没有team的才可以去创建
        try {
            if (mViewModel.gType == TeamMineModel.TYPE_SIGN_NOTEAM) {
                TeamCreateActivity.launch(this, mViewModel.gTeamInfo.getInviteUsers());
            }
        } catch (Exception e) {
            UIHelper.shortToast(R.string.error);
        }
    }

    @Override
    public void onClickRetry() {
        if (NetworkUtil.isConnected(this)) {
            showProgressView();
            getSignState();
        } else {
            UIHelper.shortToast(R.string.network_error);
        }
    }

    @Override
    protected boolean hasBaseLayout() {
        return true;
    }

    @Override
    protected BaseLayout.Builder getLayoutBuilder() {
        return super.getLayoutBuilder().setEmptyView(R.layout.vw_team_empty);
    }

    /**
     * 注册事件
     */
    private void registerEvent() {
        RxBusManager.register(this, EventConstant.KEY_TEAM_NAME, String.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onNameSuccess, Logger::e);
        RxBusManager.register(this, EventConstant.KEY_TEAM_MUMBER, String.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onInviteSuccess, Logger::e);
        RxBusManager.register(this, EventConstant.KEY_TEAM_CREATE, String.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onCreateSuccess, Logger::e);
    }

    private void onNameSuccess(String name) {
        mViewModel.gTeamInfo.getTeam().setName(name);
        mMyItemItemView.setRightText(name);
    }

    private void onInviteSuccess(String members) {
        showProgressView();
        getMyTeam();
    }

    private void onCreateSuccess(String team) {
        showProgressView();
        getMyTeam();
    }
}
