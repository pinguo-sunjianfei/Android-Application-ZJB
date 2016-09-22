package com.idrv.coach.utils.helper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.idrv.coach.R;
import com.idrv.coach.ZjbApplication;
import com.idrv.coach.ui.widget.MaterialDesignProgressBar;
import com.idrv.coach.utils.PixelUtil;

/**
 * time: 2015/9/15
 * description:统一对话框的样式
 *
 * @author sunjianfei
 */
public class DialogHelper {
    public static final int TYPE_OTHER = 0;
    public static final int TYPE_NORMAL = 1;
    public static final int TYPE_PROGRESS = TYPE_NORMAL << 1;
    public static final int TYPE_LIST = TYPE_PROGRESS << 1;
    public static final int TYPE_RADIO = TYPE_LIST << 1;
    public static final int TYPE_EDIT = TYPE_RADIO << 1;
    //1.进度条相关的控制
    private int mType;
    //2.标题文字
    private String mTitle;
    //3.内容文字
    private String mContent;
    //4.输入框的hint
    private String mEditHint;
    //5.输入框的文字
    private String mEditText;
    //输入框的背景
    private int mEditTextDrawable;
    //6.进度条的文字
    private String mProgressTxt;
    //7.checkbox的文字提示
    private String mCheckBox;
    //8.左侧的按钮文字样式
    private int mLeftBtnColor;
    private String mLeftBtn;
    private int mLeftBtnBgDrawable;
    //9.右侧的按钮文字样式
    private int mRightBtnColor;
    //右侧的按钮背景
    private int mRightBtnBgDrawable;
    private String mRightBtn;
    //10.下边栏的文字样式
    private int mBottomBtnColor;
    //底下按钮的背景
    private int mBottomBtnDrawable;
    private String mBottomBtn;
    //11.左边按钮的点击事件
    private OnDialogClickListener mLeftOnClickListener;
    //12.右边按钮的点击事件
    private OnDialogClickListener mRightOnClickListener;
    //13.下边栏对应的点击事件
    private OnDialogClickListener mBottomOnClickListener;
    //14.文字显示样式的集合，用于显示一个list
    private View[] mListContent;
    //15.每个条目点击效果
    private OnItemClickListener mOnItemClickListener;
    //16.点击区域外的部分是否消失
    private boolean mCanceledOnTouchOutside = true;
    //17.点击返回键，对话框是否消失
    private boolean mCancelable = true;
    //17.RadioButton的选择序号
    private int mRadioIndex = -1;
    //18.对话框消失对应的回调
    private Dialog.OnDismissListener mOnDismissListener;
    //19.RadioGroup的点击事件
    private OnRadioGroupSelectListener mOnRadioGroupSelectListener;
    //20.当前显示的activity
    private Activity mActivity;

    private DialogHelper(int type) {
        this.mType = type;
    }

    public DialogHelper canceledOnTouchOutside(boolean canceledOnTouchOutside) {
        this.mCanceledOnTouchOutside = canceledOnTouchOutside;
        return this;
    }

    public DialogHelper activity(Activity activity) {
        this.mActivity = activity;
        return this;
    }

    public DialogHelper onRadioGroupSelectListener(OnRadioGroupSelectListener onRadioGroupSelectListener) {
        this.mOnRadioGroupSelectListener = onRadioGroupSelectListener;
        return this;
    }

    public DialogHelper cancelable(boolean cancelable) {
        this.mCancelable = cancelable;
        return this;
    }

    public DialogHelper onDismissListener(Dialog.OnDismissListener dismissListener) {
        this.mOnDismissListener = dismissListener;
        return this;
    }

    public static DialogHelper create(int type) {
        return new DialogHelper(type);
    }

    public DialogHelper title(String title) {
        this.mTitle = title;
        return this;
    }

    public DialogHelper listContent(View... views) {
        this.mListContent = views;
        return this;
    }

    public DialogHelper content(String content) {
        this.mContent = content;
        return this;
    }

    public DialogHelper editHint(String hint) {
        this.mEditHint = hint;
        return this;
    }

    public DialogHelper editText(String text) {
        this.mEditText = text;
        return this;
    }

    public DialogHelper editBackGround(int drawable) {
        this.mEditTextDrawable = drawable;
        return this;
    }

    public DialogHelper progressText(String text) {
        this.mProgressTxt = text;
        return this;
    }


    public DialogHelper checkBox(String text) {
        this.mCheckBox = text;
        return this;
    }

    public DialogHelper radioSelectedIndex(int index) {
        this.mRadioIndex = index;
        return this;
    }

    public DialogHelper leftButton(String leftButton, int color) {
        this.mLeftBtn = leftButton;
        this.mLeftBtnColor = color;
        return this;
    }

    public DialogHelper leftButton(String leftButton, int color, int drawable) {
        this.mLeftBtn = leftButton;
        this.mLeftBtnColor = color;
        this.mLeftBtnBgDrawable = drawable;
        return this;
    }

    public DialogHelper rightButton(String rightButton, int color) {
        this.mRightBtn = rightButton;
        this.mRightBtnColor = color;
        return this;
    }

    public DialogHelper rightButton(String rightButton, int color, int drawable) {
        this.mRightBtn = rightButton;
        this.mRightBtnColor = color;
        this.mRightBtnBgDrawable = drawable;
        return this;
    }

    public DialogHelper bottomButton(String bottomButton, int color) {
        this.mBottomBtn = bottomButton;
        this.mBottomBtnColor = color;
        return this;
    }

    public DialogHelper bottomButton(String bottomButton, int color, int drawable) {
        this.mBottomBtn = bottomButton;
        this.mBottomBtnColor = color;
        this.mBottomBtnDrawable = drawable;
        return this;
    }

    public DialogHelper leftBtnClickListener(OnDialogClickListener listener) {
        this.mLeftOnClickListener = listener;
        return this;
    }

    public DialogHelper rightBtnClickListener(OnDialogClickListener listener) {
        this.mRightOnClickListener = listener;
        return this;
    }


    public DialogHelper bottomBtnClickListener(OnDialogClickListener listener) {
        this.mBottomOnClickListener = listener;
        return this;
    }

    public DialogHelper onItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
        return this;
    }

    public Dialog show() {
        //1.得到activity
        if (null == mActivity) {
            mActivity = ZjbApplication.getCurrentActivity();
        }
        Dialog dialog = new Dialog(mActivity, R.style.BaseDialog);
        //2.设置显示样式
        View view = createDialogView(dialog, mActivity);
        dialog.setContentView(view);
        dialog.setCancelable(mCancelable);
        dialog.setCanceledOnTouchOutside(mCanceledOnTouchOutside);
        //3.设置对话框的宽度
        int margin = mActivity.getResources().getDimensionPixelSize(R.dimen.dialog_width_margin);
        int screenWidth = mActivity.getResources().getDisplayMetrics().widthPixels;
        int width = screenWidth - margin * 2;
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.width = width;
        window.setAttributes(lp);
        //4.添加监听
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (null != mOnDismissListener) {
                    mOnDismissListener.onDismiss(dialog);
                }
                mActivity = null;
            }
        });
        //5.显示出来
        dialog.show();
        return dialog;
    }

    /**
     * 根据输入的相关参数显示一个视图
     *
     * @param context activity
     * @return
     */
    private View createDialogView(final Dialog dialog, Context context) {
        View view = View.inflate(context, R.layout.vw_dialog, null);
        //1.得到控件
        TextView title = (TextView) view.findViewById(R.id.dialog_title);
        LinearLayout contentLayout = (LinearLayout) view.findViewById(R.id.dialog_content_layout);
        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.dialog_radio_layout);
        TextView content = (TextView) contentLayout.findViewById(R.id.dialog_content);
        AppCompatEditText edit = (AppCompatEditText) contentLayout.findViewById(R.id.dialog_edittext);
        LinearLayout progressLayout = (LinearLayout) view.findViewById(R.id.dialog_progress_layout);
        final MaterialDesignProgressBar progressBar = (MaterialDesignProgressBar) progressLayout.findViewById(R.id.dialog_progress);
        TextView progressText = (TextView) progressLayout.findViewById(R.id.dialog_progress_txt);
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.dialog_checkbox);
        LinearLayout bottomLayout = (LinearLayout) view.findViewById(R.id.dialog_bottom);
        TextView leftBtn = (TextView) bottomLayout.findViewById(R.id.dialog_left);
        TextView rightBtn = (TextView) bottomLayout.findViewById(R.id.dialog_right);
        TextView centerBtn = (TextView) bottomLayout.findViewById(R.id.dialog_center_ok);
        //2.根据参数显示
        //2.1处理标题
        if (!TextUtils.isEmpty(mTitle)) {
            title.setVisibility(View.VISIBLE);
            title.setText(mTitle);
        } else {
            title.setVisibility(View.GONE);
        }
        //2.2处理内容
        if (TYPE_PROGRESS == mType) {
            radioGroup.setVisibility(View.GONE);
            progressLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            contentLayout.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(mProgressTxt)) {
                progressText.setText(mProgressTxt);
            }
        } else if (TYPE_LIST == mType) {
            radioGroup.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            contentLayout.setVisibility(View.VISIBLE);
            contentLayout.removeAllViews();
            if (mListContent != null) {
                int length = mListContent.length;
                int i = 0;
                for (; i < length; i++) {
                    final int position = i;
                    final View textView = mListContent[position];
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (null != mOnItemClickListener) {
                                mOnItemClickListener.onItemClick(dialog, position, textView);
                            }
                        }
                    });
                    contentLayout.addView(textView);
                    if (i < length - 1) {
                        contentLayout.addView(getBottomLine(context));
                    }
                }
            }
        } else if (TYPE_RADIO == mType) {
            progressLayout.setVisibility(View.GONE);
            contentLayout.setVisibility(View.GONE);
            radioGroup.setVisibility(View.VISIBLE);
            if (0 == mRadioIndex) {
                ((RadioButton) radioGroup.findViewById(R.id.dialog_female)).setChecked(true);
                ((RadioButton) radioGroup.findViewById(R.id.dialog_male)).setChecked(false);
            } else if (1 == mRadioIndex) {
                ((RadioButton) radioGroup.findViewById(R.id.dialog_female)).setChecked(false);
                ((RadioButton) radioGroup.findViewById(R.id.dialog_male)).setChecked(true);
            }
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (group.getCheckedRadioButtonId() == R.id.dialog_female) {
                        if (null != mOnRadioGroupSelectListener) {
                            mOnRadioGroupSelectListener.onLeftClick();
                        }
                    } else if (group.getCheckedRadioButtonId() == R.id.dialog_male) {
                        if (null != mOnRadioGroupSelectListener) {
                            mOnRadioGroupSelectListener.onRightClick();
                        }
                    }

                }
            });
        } else {
            radioGroup.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            contentLayout.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(mContent)) {
                content.setVisibility(View.VISIBLE);
                content.setText(mContent);
            } else {
                content.setVisibility(View.GONE);
            }
            boolean hasHint = !TextUtils.isEmpty(mEditHint);
            boolean hasEditText = !TextUtils.isEmpty(mEditText);
            if (mEditTextDrawable != 0) {
                edit.setBackgroundResource(mEditTextDrawable);
            }
            if (hasHint || hasEditText) {
                edit.setVisibility(View.VISIBLE);
                if (hasHint) {
                    edit.setHint(mEditHint);
                }
                if (hasEditText) {
                    edit.setText(mEditText);
                }
            } else {
                edit.setVisibility(View.GONE);
            }
        }
        //2.3处理checkbox
        if (!TextUtils.isEmpty(mCheckBox)) {
            checkBox.setVisibility(View.VISIBLE);
            checkBox.setText(mCheckBox);
        } else {
            checkBox.setVisibility(View.GONE);
        }
        //2.4处理下边栏
        boolean hasLeft = !TextUtils.isEmpty(mLeftBtn);
        boolean hasRight = !TextUtils.isEmpty(mRightBtn);
        boolean hasBottom = !TextUtils.isEmpty(mBottomBtn);
        if (hasLeft || hasRight || hasBottom) {
            bottomLayout.setVisibility(View.VISIBLE);
            if (hasLeft || hasRight) {
                leftBtn.setVisibility(View.VISIBLE);
                rightBtn.setVisibility(View.VISIBLE);
                centerBtn.setVisibility(View.GONE);
                if (hasLeft) {
                    leftBtn.setText(mLeftBtn);
                    leftBtn.setTextColor(mLeftBtnColor);
                    if (mLeftBtnBgDrawable != 0) {
                        leftBtn.setBackgroundResource(mLeftBtnBgDrawable);
                    }
                    if (mLeftOnClickListener != null) {
                        leftBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mLeftOnClickListener.onClick(dialog, v);
                            }
                        });
                    }
                }
                if (hasRight) {
                    rightBtn.setText(mRightBtn);
                    rightBtn.setTextColor(mRightBtnColor);
                    if (mRightBtnBgDrawable != 0) {
                        rightBtn.setBackgroundResource(mRightBtnBgDrawable);
                    }
                    if (mRightOnClickListener != null) {
                        rightBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mRightOnClickListener.onClick(dialog, v);
                            }
                        });
                    }
                }
            }
            if (hasBottom) {
                leftBtn.setVisibility(View.GONE);
                rightBtn.setVisibility(View.GONE);
                centerBtn.setVisibility(View.VISIBLE);
                centerBtn.setText(mBottomBtn);
                centerBtn.setTextColor(mBottomBtnColor);
                if (mBottomBtnDrawable != 0) {
                    centerBtn.setBackgroundResource(mBottomBtnDrawable);
                }
                if (null != mBottomOnClickListener) {
                    centerBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mBottomOnClickListener.onClick(dialog, v);
                        }
                    });
                }
            }
        } else {
            bottomLayout.setVisibility(View.GONE);
        }

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (TYPE_PROGRESS == mType) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
        return view;
    }

    public interface OnItemClickListener {
        void onItemClick(Dialog dialog, int position, View view);
    }

    public interface OnDialogClickListener {
        void onClick(Dialog dialog, View view);
    }

    public interface OnRadioGroupSelectListener {
        void onLeftClick();

        void onRightClick();
    }

    private View getBottomLine(Context context) {
        View view = new View(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2);
        params.leftMargin = (int) PixelUtil.dp2px(16);
        params.rightMargin = (int) PixelUtil.dp2px(16);
        view.setLayoutParams(params);
        view.setBackgroundColor(0x33000000);
        return view;
    }

}
