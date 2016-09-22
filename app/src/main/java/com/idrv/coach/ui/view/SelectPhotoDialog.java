package com.idrv.coach.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.idrv.coach.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * time:2016/3/21
 * description: 选择相机的对话框
 *
 * @author sunjianfei
 */
public class SelectPhotoDialog extends Dialog implements View.OnClickListener {
    private OnButtonClickListener onButtonClickListener;

    @InjectView(R.id.btn_camera)
    TextView mCameraItemView;


    public SelectPhotoDialog(Context context) {
        super(context, R.style.SelectPhotoDialogStyle);

        //初始化布局
        setContentView(R.layout.vw_select_camera_dialog);
        ButterKnife.inject(this);
        Window dialogWindow = getWindow();
        dialogWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogWindow.setGravity(Gravity.BOTTOM);
        setCanceledOnTouchOutside(true);
    }

    @OnClick({R.id.btn_camera, R.id.btn_gallery, R.id.btn_cancel})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_camera:
                onButtonClickListener.camera();
                dismiss();
                break;
            case R.id.btn_gallery:
                onButtonClickListener.gallery();
                dismiss();
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
            default:
                break;
        }
    }

    public void changeCameraItem(String text, int status) {
        mCameraItemView.setText(text);
        mCameraItemView.setVisibility(status);
    }

    public interface OnButtonClickListener {
        void camera();

        void gallery();
    }

    public OnButtonClickListener getOnButtonClickListener() {
        return onButtonClickListener;
    }

    public void setOnButtonClickListener(OnButtonClickListener onButtonClickListener) {
        this.onButtonClickListener = onButtonClickListener;
    }
}
