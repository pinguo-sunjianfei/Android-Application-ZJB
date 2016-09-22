package com.idrv.coach.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.idrv.coach.R;
import com.idrv.coach.bean.event.EventConstant;
import com.idrv.coach.data.manager.RxBusManager;
import com.idrv.coach.data.model.UserReviseModel;
import com.idrv.coach.utils.PixelUtil;
import com.idrv.coach.utils.helper.DialogHelper;
import com.idrv.coach.utils.helper.UIHelper;
import com.zjb.volley.core.exception.NetworkError;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Subscription;

/**
 * time:2016/3/22
 * description: 修改文字：
 * 1. 姓名；单行
 * 2. 练车场地、训练场地；多行，50字限制； 昵称 18
 *
 * @author bigflower
 */
public class UserReviseActivity extends BaseActivity<UserReviseModel> {

    @InjectView(R.id.user_textRivse_et)
    EditText mEditText;
    @InjectView(R.id.user_textRivse_showTv)
    TextView mLeftNumberTv;
    @InjectView(R.id.user_textRivse_clearIv)
    ImageView mClearIv;

    public static void launch(Context context, String key, String text) {
        Intent intent = new Intent(context, UserReviseActivity.class);
        intent.putExtra(UserReviseModel.INTENT_KEY, key);
        intent.putExtra(UserReviseModel.INTENT_TEXT, text);
        context.startActivity(intent);
    }

    @Override
    protected int getProgressBg() {
        return R.color.bg_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_user_revise);
        ButterKnife.inject(this);

        //1.初始化标题栏
        initToolBar();
        //2.初始化ViewModel
        mViewModel = new UserReviseModel();
        // 3. 加载布局，并根据状态赋值
        initInfo();
        initViews();
    }

    private void initToolBar() {
        mToolbarLayout.setTitle(R.string.title_qrcode);
        mToolbarLayout.setRightTxt(R.string.save);
        mToolbarLayout.getRightTextView().setTextColor(Color.WHITE);
    }

    /**
     * 进行判断，为修改哪个，且界面不同
     */
    private void initInfo() {
        mViewModel.gHttpKey = getIntent().getStringExtra(UserReviseModel.INTENT_KEY);
        if (UserReviseModel.KEY_NAME.equals(mViewModel.gHttpKey)) {
            mEditText.setSingleLine();
            mEditText.setHint(R.string.hint_nickname);
            mClearIv.setVisibility(View.VISIBLE);
            mToolbarLayout.setTitle(R.string.item_user_nickname);
        } else if (UserReviseModel.KEY_TRAIN.equals(mViewModel.gHttpKey)) {
            mToolbarLayout.setTitle(R.string.item_address_trainSite);
            mEditText.setMinHeight((int) PixelUtil.dp2px(82));
            mEditText.setHint(R.string.hint_train_site);
            mViewModel.gMaxLength = 50;
            setMaxLength(50);
        } else if (UserReviseModel.KEY_SCHOOL.equals(mViewModel.gHttpKey)) {
            mToolbarLayout.setTitle(R.string.input_school);
            mEditText.setMinHeight((int) PixelUtil.dp2px(82));
            mEditText.setHint(R.string.input_school_text_hint);
            mViewModel.gMaxLength = 30;
            setMaxLength(30);
        } else {
            mToolbarLayout.setTitle(R.string.item_address_testSite);
            mEditText.setMinHeight((int) PixelUtil.dp2px(82));
            mEditText.setHint(R.string.hint_test_site);
            mViewModel.gMaxLength = 50;
            setMaxLength(50);
        }
        // 显示文字，和右下角的剩余长度
        mViewModel.gOldText = getIntent().getStringExtra(UserReviseModel.INTENT_TEXT);
        if (getString(R.string.unFilled).equals(mViewModel.gOldText)) {
            mViewModel.gOldText = "";
        }
        mEditText.setText(mViewModel.gOldText);
        mEditText.setSelection(mViewModel.gOldText.length());
        mLeftNumberTv.setText("" + (mViewModel.gMaxLength - mViewModel.gOldText.length()));
    }

    // 为edittext添加listener
    private void initViews() {
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mLeftNumberTv.setText("" + (mViewModel.gMaxLength - s.length()));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void onToolbarRightClick(View view) {
        super.onToolbarRightClick(view);
        UIHelper.hideSoftInput(mEditText);

        String text = mEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(mViewModel.gOldText) && mViewModel.gOldText.equals(text)) {
            finish();
        } else if (UserReviseModel.KEY_NAME.equals(mViewModel.gHttpKey)) {
            checkNickName(text);
        } else if (UserReviseModel.KEY_TRAIN.equals(mViewModel.gHttpKey)) {
            checkTrainSite(text);
        } else if (UserReviseModel.KEY_SCHOOL.equals(mViewModel.gHttpKey)) {
            checkSchool(text);
        } else {
            checkTestSite(text);
        }
    }

    @OnClick(R.id.user_textRivse_clearIv)
    void clearClick() {
        mEditText.setText("");
    }

    /**
     * 检查昵称
     */
    private void checkNickName(String nickname) {
        if (TextUtils.isEmpty(nickname)) {
            UIHelper.shortToast(R.string.nickname_not_null);
            return;
        }
        mProgressDialog = DialogHelper.create(DialogHelper.TYPE_PROGRESS)
                .progressText(getString(R.string.dialog_revising)).show();
        Subscription subscription = mViewModel.putUserInfo(nickname)
                .subscribe(this::onSuccess, this::onError);
        addSubscription(subscription);
    }

    /**
     * 检查练车场地
     */
    private void checkTrainSite(String text) {
        if (TextUtils.isEmpty(text)) {
            UIHelper.shortToast(R.string.train_not_null);
            return;
        }
        mProgressDialog = DialogHelper.create(DialogHelper.TYPE_PROGRESS)
                .progressText(getString(R.string.dialog_revising)).show();
        Subscription subscription = mViewModel.putCoachInfo(text)
                .subscribe(this::onSuccess, this::onError);
        addSubscription(subscription);
    }

    /**
     * 检查驾校
     */
    private void checkSchool(String school) {
        if (TextUtils.isEmpty(school)) {
            UIHelper.shortToast(R.string.school_name_not_be_null);
            return;
        }
        mProgressDialog = DialogHelper.create(DialogHelper.TYPE_PROGRESS)
                .progressText(getString(R.string.dialog_revising)).show();
        Subscription subscription = mViewModel.putCoachInfo(school)
                .subscribe(this::onSuccess, this::onError);
        addSubscription(subscription);
    }

    /**
     * 检查考场
     */
    private void checkTestSite(String text) {
        if (TextUtils.isEmpty(text)) {
            UIHelper.shortToast(R.string.test_not_null);
            return;
        }
        mProgressDialog = DialogHelper.create(DialogHelper.TYPE_PROGRESS)
                .progressText(getString(R.string.dialog_revising)).show();
        Subscription subscription = mViewModel.putCoachInfo(text)
                .subscribe(this::onSuccess, this::onError);
        addSubscription(subscription);
    }

    private void onSuccess(String text) {
        dismissProgressDialog();
        RxBusManager.post(EventConstant.KEY_REVISE_USERINFO, "");
        if (UserReviseModel.KEY_NAME.equals(mViewModel.gHttpKey)) {
            RxBusManager.post(EventConstant.KEY_REVISE_NICKNAME, text);
        } else if (UserReviseModel.KEY_TRAIN.equals(mViewModel.gHttpKey)) {
            RxBusManager.post(EventConstant.KEY_REVISE_TRAINSITE, text);
        } else if (UserReviseModel.KEY_SCHOOL.equals(mViewModel.gHttpKey)) {
            RxBusManager.post(EventConstant.KEY_REVISE_DRISCHOOL, text);
            RxBusManager.post(EventConstant.KEY_SCHOOL_SAVE_SUCCESS, "");
        } else {
            RxBusManager.post(EventConstant.KEY_REVISE_TESTSITE, text);
        }
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
    protected void onPause() {
        super.onPause();
        UIHelper.hideSoftInput(mEditText);
    }

    ///////////////////////////////////////
    // 工具
    private void setMaxLength(int maxLength) {
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLength);
        mEditText.setFilters(fArray);
    }
}
