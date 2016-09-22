package com.idrv.coach.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.idrv.coach.R;
import com.idrv.coach.bean.Visitor;
import com.idrv.coach.ui.DynamicActivity;
import com.idrv.coach.utils.PixelUtil;
import com.zjb.loader.ZjbImageLoader;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * time:2016/5/23
 * description:
 *
 * @author sunjianfei
 */
public class NewsDialog extends Dialog {
    @InjectView(R.id.num_tv)
    TextView mNumTv;
    @InjectView(R.id.avatar_layout)
    LinearLayout mAvatarLayout;

    int mItemSize = (int) PixelUtil.dp2px(50);

    public NewsDialog(Context context) {
        super(context, R.style.BaseDialog);
        //初始化布局
        setContentView(R.layout.vw_news_views_change_dialog);
        ButterKnife.inject(this, this);
    }

    public void setAvatar(List<Visitor> avatars) {
        View[] views = new View[3];
        views[0] = mAvatarLayout.findViewById(R.id.image_0);
        views[1] = mAvatarLayout.findViewById(R.id.image_1);
        views[2] = mAvatarLayout.findViewById(R.id.image_2);
        int size = views.length;
        for (int i = 0; i < size; i++) {
            View childView = views[i];
            ZjbImageLoader.create(avatars.get(i).getHeadimgurl())
                    .setQiniu(mItemSize, mItemSize)
                    .setBitmapConfig(Bitmap.Config.ARGB_8888)
                    .setDisplayType(ZjbImageLoader.DISPLAY_DEFAULT)
                    .setDefaultRes(R.drawable.icon_user_avatar_default_92)
                    .into(childView);
        }
    }

    public void setVisNum(int num) {
        mNumTv.setText(getContext().getString(R.string.vis_num, num));
    }

    @OnClick(R.id.btn_view)
    public void onBottonClick(View view) {
        DynamicActivity.launch(getContext());
        dismiss();
    }
}
