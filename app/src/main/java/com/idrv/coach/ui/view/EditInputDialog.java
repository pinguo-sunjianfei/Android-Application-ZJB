package com.idrv.coach.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import com.idrv.coach.R;
import com.idrv.coach.utils.helper.UIHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * time:2016/7/14
 * description:海报模板编辑输入对话框
 *
 * @author sunjianfei
 */
public class EditInputDialog extends Dialog {
    @InjectView(R.id.cancel_btn)
    TextView mCancelBtn;
    @InjectView(R.id.save_btn)
    TextView mSaveBtn;
    @InjectView(R.id.edit_text)
    EditText mEditText;
    @InjectView(R.id.largest_length_tv)
    TextView mMaxLengthTv;

    OnSaveListener mListener;
    int maxInputLength = 100;


    public EditInputDialog(Context context) {
        super(context, R.style.BaseDialog);
        //初始化布局
        setContentView(R.layout.vw_poster_edit_dialog);
        setCanceledOnTouchOutside(false);
        setCancelable(true);
        ButterKnife.inject(this, this);

        //设置限制长度
        mMaxLengthTv.setText(getContext().getString(R.string.the_largest_text_length, maxInputLength));
        //设置保存事件
        mSaveBtn.setOnClickListener(v -> {
            if (null != mListener) {
                String text = mEditText.getText().toString();
                if (TextUtils.isEmpty(text)) {
                    UIHelper.shortToast(R.string.input_not_be_null);
                    return;
                }
                mListener.onSave(text);
                dismiss();
            }
        });

        //取消事件
        mCancelBtn.setOnClickListener(v -> dismiss());

        //输入监听,用于显示剩余字数
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mMaxLengthTv.setText(getContext().getString(R.string.the_largest_text_length, (maxInputLength - s.length())));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void setOnSaveListener(OnSaveListener listener) {
        this.mListener = listener;
    }

    public void setDefaultText(String text) {
        mEditText.setText(text);
        mEditText.setSelection(TextUtils.isEmpty(text) ? 0 : text.length());
    }

    public void setMaxInputLength(int length) {
        this.maxInputLength = length;
        mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxInputLength)});
        mMaxLengthTv.setText(getContext().getString(R.string.the_largest_text_length, maxInputLength));
    }

    public interface OnSaveListener {
        void onSave(String text);
    }
}
