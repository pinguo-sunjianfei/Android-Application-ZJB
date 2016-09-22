package com.idrv.coach.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.idrv.coach.R;
import com.idrv.coach.bean.event.EventConstant;
import com.idrv.coach.data.manager.RxBusManager;
import com.idrv.coach.data.model.SchoolModel;
import com.idrv.coach.data.model.UserReviseModel;
import com.idrv.coach.ui.adapter.SearchSchoolAdapter;
import com.idrv.coach.ui.adapter.SelectSchoolAdapter;
import com.idrv.coach.ui.adapter.TextWatcherAdapter;
import com.idrv.coach.utils.Logger;
import com.idrv.coach.utils.helper.DialogHelper;
import com.idrv.coach.utils.helper.UIHelper;
import com.zjb.volley.core.exception.NetworkError;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * time:2016/5/25
 * description:
 *
 * @author sunjianfei
 */
public class SchoolSelectActivity extends BaseActivity<SchoolModel> implements SearchSchoolAdapter.OnSchoolSelectListener {
    private static final String PARAM_CID = "cid";

    @InjectView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @InjectView(R.id.search_recycler_view)
    RecyclerView mSearchRecyclerView;
    @InjectView(R.id.search_edit)
    EditText mSearchEditText;
    SelectSchoolAdapter mAdapter;
    SearchSchoolAdapter mSearchAdapter;

    private TextWatcher mTextWatcher;

    {
        this.mTextWatcher = new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                mSearchRecyclerView.setTextFilterEnabled(true);
                if (mAdapter.getItemCount() < 2 || TextUtils.isEmpty(s)) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mSearchRecyclerView.setVisibility(View.INVISIBLE);
                } else {
                    mRecyclerView.setVisibility(View.INVISIBLE);
                    mSearchRecyclerView.setVisibility(View.VISIBLE);
                    mSearchAdapter.getFilter().filter(s);
                }
            }
        };
    }

    public static void launch(Context context, String cid) {
        Intent intent = new Intent(context, SchoolSelectActivity.class);
        intent.putExtra(PARAM_CID, cid);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_school_select);
        ButterKnife.inject(this);
        initToolBar();
        initView();
        initViewModel();
        registerEvent();
    }

    private void registerEvent() {
        //驾校选择完成之后,关闭页面
        RxBusManager.register(this, EventConstant.KEY_SCHOOL_SAVE_SUCCESS, String.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(__ -> finish(), Logger::e);
    }

    private void initToolBar() {
        mToolbarLayout.setTitle(R.string.select_school);
    }

    private void initView() {
        mAdapter = new SelectSchoolAdapter();
        mAdapter.setListener(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mSearchEditText.addTextChangedListener(mTextWatcher);
    }

    private void initViewModel() {
        mViewModel = new SchoolModel();
        getSchoolList();
    }

    private void getSchoolList() {
        Dialog dialog = DialogHelper.create(DialogHelper.TYPE_PROGRESS)
                .progressText(getString(R.string.dialog_loading))
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .show();
        String cid = getIntent().getStringExtra(PARAM_CID);
        Subscription subscription = mViewModel.getSchoolList(cid)
                .subscribe(this::setUp, __ -> showErrorView(), dialog::dismiss);
        addSubscription(subscription);
    }

    private void setUp(List<String> list) {
        mAdapter.addData(list);
        mAdapter.notifyDataSetChanged();
        //获取到数据之后,初始化搜索的recyclerView
        mSearchAdapter = new SearchSchoolAdapter();
        mSearchAdapter.setListener(this);
        mSearchAdapter.setAllSchools(list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mSearchRecyclerView.setLayoutManager(layoutManager);
        mSearchRecyclerView.setAdapter(mSearchAdapter);

    }

    @Override
    public void onSchoolSelect(String school) {
        modifyCoachSchool(school);
    }

    @Override
    public void onCustomSchoolSelect() {
        UserReviseActivity.launch(this, UserReviseModel.KEY_SCHOOL, mViewModel.getOldSchool());
    }

    private void modifyCoachSchool(final String value) {
        mProgressDialog = DialogHelper.create(DialogHelper.TYPE_PROGRESS)
                .progressText(getString(R.string.dialog_revising)).show();
        Subscription subscription = mViewModel.modifyCoachInfo(value)
                .subscribe(__ -> onNext(value), this::onError, this::dismissProgressDialog);
        addSubscription(subscription);
    }

    private void onNext(String school) {
        dismissProgressDialog();
        // 发送事件给UserInfoActivity，告知修改内容
        RxBusManager.post(EventConstant.KEY_REVISE_USERINFO, "");
        RxBusManager.post(EventConstant.KEY_REVISE_DRISCHOOL, school);
        //通知上一个页面关闭
        RxBusManager.post(EventConstant.KEY_SCHOOL_SAVE_SUCCESS, "");
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
}
