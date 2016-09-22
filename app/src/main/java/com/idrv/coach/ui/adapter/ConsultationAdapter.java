package com.idrv.coach.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.idrv.coach.R;
import com.idrv.coach.bean.Consultation;
import com.idrv.coach.utils.SystemUtil;
import com.idrv.coach.utils.TimeUtil;
import com.idrv.coach.utils.helper.DialogHelper;
import com.zjb.loader.ZjbImageLoader;

/**
 * time:2016/6/3
 * description:
 *
 * @author sunjianfei
 */
public class ConsultationAdapter extends AbsListAdapter<Consultation> {
    private static final int TYPE_DEFAULT = 0;
    private static final int TYPE_EMPTY = 1;

    @Override
    public int getItemViewType(int position) {
        Consultation comment = mData.get(position);
        return comment.isFake() ? TYPE_EMPTY : TYPE_DEFAULT;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        EmptyHolder emptyHolder = null;

        int type = getItemViewType(position);

        if (null == convertView) {
            convertView = createViewByType(type, parent.getContext());

            if (type == TYPE_DEFAULT) {
                holder = new ViewHolder();

                holder.mAvatarIv = (ImageView) convertView.findViewById(R.id.avatar);
                holder.mNickNameTv = (TextView) convertView.findViewById(R.id.nick_name);
                holder.mContentTv = (TextView) convertView.findViewById(R.id.content_tv);
                holder.mCallBtn = convertView.findViewById(R.id.btn_call);
                holder.mTimeTv = (TextView) convertView.findViewById(R.id.time);

                convertView.setTag(holder);
            } else {
                emptyHolder = new EmptyHolder();

                emptyHolder.mEmptyView = (ImageView) convertView.findViewById(R.id.empty_iv);
                convertView.setTag(emptyHolder);
            }
        } else {
            if (type == TYPE_DEFAULT) {
                holder = (ViewHolder) convertView.getTag();
            } else {
                emptyHolder = (EmptyHolder) convertView.getTag();
            }
        }

        if (type == TYPE_DEFAULT) {
            Consultation consultation = mData.get(position);
            String phone = consultation.getPhone();
            String time = TimeUtil.getFeedsTime(TimeUtil.getTempTime(consultation.getCreated()));
            String nickName = consultation.getNickname();

            if (TextUtils.isEmpty(nickName)) {
                nickName = parent.getContext().getResources().getString(R.string.student);
            }

            holder.mNickNameTv.setText(nickName);
            holder.mContentTv.setText(consultation.getDescription());
            holder.mTimeTv.setText(time);

            if (TextUtils.isEmpty(phone) || !SystemUtil.checkPhoneNumber(phone)) {
                holder.mCallBtn.setVisibility(View.GONE);
            } else {
                holder.mCallBtn.setVisibility(View.VISIBLE);
                holder.mCallBtn.setOnClickListener(v -> {
                    Context context = v.getContext();
                    DialogHelper.create(DialogHelper.TYPE_NORMAL)
                            .cancelable(true)
                            .canceledOnTouchOutside(true)
                            .title(context.getString(R.string.tip))
                            .content(context.getString(R.string.call_now, phone))
                            .leftButton(context.getString(R.string.cancel), ContextCompat.getColor(context, R.color.black_54))
                            .rightButton(context.getString(R.string.call), ContextCompat.getColor(context, R.color.themes_main))
                            .rightBtnClickListener((dialog, view) -> {
                                dialog.dismiss();
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:" + phone));
                                if (intent.resolveActivity(context.getPackageManager()) != null) {
                                    context.startActivity(intent);
                                }
                            }).leftBtnClickListener((dialog, view) -> dialog.dismiss())
                            .show();
                });
            }

            ZjbImageLoader.create(consultation.getHeadimgurl())
                    .setQiniu(80, 80)
                    .setDefaultRes(R.drawable.avatar_circle_default)
                    .setDisplayType(ZjbImageLoader.DISPLAY_CIRCLE)
                    .into(holder.mAvatarIv);
        } else {
            emptyHolder.mEmptyView.setImageResource(R.drawable.consult_empty_bg);
        }

        return convertView;
    }

    private View createViewByType(int type, Context context) {
        View view;
        if (type == TYPE_DEFAULT) {
            view = LayoutInflater.from(context).inflate(R.layout.vw_consultation_item, null, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.vw_website_empty_item, null, false);
        }
        return view;
    }

    class ViewHolder {
        ImageView mAvatarIv;
        TextView mNickNameTv;
        TextView mContentTv;
        View mCallBtn;
        TextView mTimeTv;
    }

    class EmptyHolder {
        ImageView mEmptyView;
    }
}
