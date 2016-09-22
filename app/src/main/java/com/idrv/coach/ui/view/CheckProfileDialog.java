package com.idrv.coach.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.idrv.coach.R;
import com.idrv.coach.bean.Coach;
import com.idrv.coach.data.manager.LoginManager;
import com.idrv.coach.ui.SelectCityActivity;
import com.idrv.coach.ui.UserInfoActivity;
import com.idrv.coach.utils.TimeUtil;

/**
 * time:2016/6/12
 * description:个人网站分享的时候,判断资料是否完善的弹窗
 *
 * @author sunjianfei
 */
public class CheckProfileDialog extends Dialog {
    MasterItemView mSchoolSettingItem;
    MasterItemView mTeachAgeSettingItem;

    public CheckProfileDialog(Context context) {
        super(context, R.style.BaseDialog);
        //初始化布局
        setContentView(R.layout.vw_detection_profile_dialog);
        Window dialogWindow = getWindow();
        dialogWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogWindow.setGravity(Gravity.CENTER);

        mSchoolSettingItem = (MasterItemView) findViewById(R.id.item_school);
        mTeachAgeSettingItem = (MasterItemView) findViewById(R.id.item_teach_age);

        findViewById(R.id.btn_close).setOnClickListener(v -> dismiss());
        setUp();
    }

    public void setUp() {
        Coach coach = LoginManager.getInstance().getCoach();
        String schoolName = coach.getDrivingSchool();
        String teachAge = coach.getCoachingDate();

        if (!TextUtils.isEmpty(schoolName) && !TextUtils.isEmpty(teachAge) && !"0001-01-01".equals(teachAge)) {
            dismiss();
        }

        if (TextUtils.isEmpty(schoolName)) {
            updateView(mSchoolSettingItem, R.string.title_drive_school,
                    R.drawable.icon_teach_school, getContext().getString(R.string.to_set), true);
            mSchoolSettingItem.setOnClickListener(v -> SelectCityActivity.launch(v.getContext()));
        } else {
            updateView(mSchoolSettingItem, R.string.title_drive_school,
                    R.drawable.icon_teach_school, schoolName, false);
            mSchoolSettingItem.setOnClickListener(null);
        }

        // 判断教龄
        if (TextUtils.isEmpty(teachAge) || "0001-01-01".equals(teachAge)) {
            updateView(mTeachAgeSettingItem, R.string.item_user_teachAge,
                    R.drawable.icon_teach_age, getContext().getString(R.string.to_set), true);
            mTeachAgeSettingItem.setOnClickListener(v -> UserInfoActivity.launch(v.getContext()));
        } else {
            updateView(mTeachAgeSettingItem, R.string.item_user_teachAge,
                    R.drawable.icon_teach_age, TimeUtil.getAge(teachAge), false);
            mTeachAgeSettingItem.setOnClickListener(null);
        }
    }

    private void updateView(MasterItemView itemView, int titleResId, int leftDrawable, String content, boolean showArrow) {
        itemView.setText(titleResId);
        itemView.setRightTextWithArrowText(content);
        itemView.setLeftDrawableRes(leftDrawable);

        itemView.setRightArrowVisible(showArrow ? View.VISIBLE : View.GONE);
    }
}
