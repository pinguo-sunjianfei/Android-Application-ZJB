package com.idrv.coach.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.idrv.coach.R;
import com.idrv.coach.utils.helper.UIHelper;

import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by 明明大美女 on 2016/2/25.
 */
public class UserTextDialog extends Dialog {

    // 会有三种输入类型， phone， qq， 文字
    public static final int TYPE_NAME = 0;
    public static final int TYPE_PHONE = 1;
    public static final int TYPE_QQ = 2;

    private int dialogType;

    private OnDialogSure mInterface;

    @InjectView(R.id.dialogUserText_edit)
    EditText mEditText;

    private String hintTextStr;
    private String realTextStr;

    public UserTextDialog(Context context, String hintTextStr, OnDialogSure listener) {
        super(context);
        this.hintTextStr = hintTextStr;
        this.realTextStr = realTextStr;
        mInterface = listener;

        init();
        initHint();
    }

    public UserTextDialog(Context context, int themeResId, String realTextStr, String hintTextStr, OnDialogSure listener) {
        super(context, themeResId);
        this.hintTextStr = hintTextStr;
        this.realTextStr = realTextStr;
        mInterface = listener;

        init();
        initHint();
    }

    /**
     * 初始化
     */
    private void init() {
        // 弹出自定义dialog
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.dlg_user_text, null);

        // 对话框
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 设置宽度为屏幕的宽度
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = 666; // 设置宽度
        getWindow().setAttributes(lp);
        getWindow().setContentView(view);
        setCancelable(false);
        ButterKnife.inject(this);
    }

    /**
     * 根据 hint 来判断 type
     */
    private void initHint() {
        if ("请输入QQ号".equals(hintTextStr)) {
            dialogType = TYPE_QQ;
            setMaxLength(18);
            mEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else if ("请输入手机号".equals(hintTextStr)) {
            dialogType = TYPE_PHONE;
            setMaxLength(11);
            mEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else if ("请输入常去考场".equals(hintTextStr) || "请输入常练车地址".equals(hintTextStr)) {
            setMaxLength(28);
        } else {
            setMaxLength(23);
        }
        mEditText.setHint(hintTextStr);
        mEditText.setText(realTextStr);
        mEditText.setSelection(realTextStr.length());
    }

    @OnClick(R.id.dialogUserText_clear)
    void clearClick() {
        mEditText.setText("");
    }

    @OnClick(R.id.dialogUserText_cancel)
    void cancelClick() {
        UIHelper.hideSoftInput(mEditText);
        dismiss();
    }

    /**
     * 确定按钮的点击
     * 根据不同的 type 来对输入做不同的判断
     */
    @OnClick(R.id.dialogUserText_sure)
    void sureClick() {
        UIHelper.hideSoftInput(mEditText);
        if (notClickable())
            return;
        String text = mEditText.getText().toString().trim();
        if (text.isEmpty()) {
            showToast("输入不能为空");
            return;
        }

        if (text.equals(realTextStr)) {
            dismiss();
            return;
        }

        if (dialogType == TYPE_QQ) {
            checkQQ(text);
        } else if (dialogType == TYPE_PHONE) {
            checkPhone(text);
        } else {
            callback(text);
        }

    }

    /**
     * 检测 qq号 的输入
     *
     * @param text
     */
    private void checkQQ(String text) {
        if (text.length() < 5 || text.length() > 18) {
            showToast("请输入正确的qq号");
            return;
        }
        callback(text);
    }

    /**
     * 检测 手机号 的输入
     *
     * @param text
     */
    private void checkPhone(String text) {
        if (text.length() != 11) {
            showToast("请输入正确的手机号");
            return;
        }
        if (!text.startsWith("1")) {
            showToast("请输入正确的手机号");
            return;
        }
        callback(text);
    }

    private void setMaxLength(int maxLength) {
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLength);
        mEditText.setFilters(fArray);
    }

    /**
     * 回调
     *
     * @param text
     */
    private void callback(String text) {
        dismiss();
        if (mInterface != null) {
            mInterface.sure(text);
        }
    }

    private void showToast(String text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }

    private long lastClickTime = 0;

    private boolean notClickable() {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > 1000) {
            lastClickTime = currentTime;
            return false;
        } else {
            return true;
        }
    }

    public interface OnDialogSure {
        void sure(String result);
    }
}
