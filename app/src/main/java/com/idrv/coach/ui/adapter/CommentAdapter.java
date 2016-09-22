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
import com.idrv.coach.bean.Comment;
import com.idrv.coach.utils.SystemUtil;
import com.idrv.coach.utils.helper.DialogHelper;
import com.zjb.loader.ZjbImageLoader;

/**
 * time:2016/6/3
 * description:我的网站评论
 *
 * @author sunjianfei
 */
public class CommentAdapter extends AbsListAdapter<Comment> {
    private static final int TYPE_DEFAULT = 0;
    private static final int TYPE_EMPTY = 1;

    @Override
    public int getItemViewType(int position) {
        Comment comment = mData.get(position);
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
                holder.mHDividerLine = convertView.findViewById(R.id.h_divider_line);
                holder.mContactLayout = convertView.findViewById(R.id.contact_layout);
                holder.mTelTv = (TextView) convertView.findViewById(R.id.tel_tv);
                holder.mSmsTv = (TextView) convertView.findViewById(R.id.sms_tv);

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
            Comment comment = mData.get(position);
            String nickName = comment.getNickname();
            String phone = comment.getPhone();
            if (TextUtils.isEmpty(nickName)) {
                nickName = parent.getContext().getResources().getString(R.string.student);
            }
            holder.mNickNameTv.setText(nickName);
            holder.mContentTv.setText(comment.getContent());

            ZjbImageLoader.create(comment.getHeadimgurl())
                    .setQiniu(80, 80)
                    .setDefaultRes(R.drawable.avatar_circle_default)
                    .setDisplayType(ZjbImageLoader.DISPLAY_CIRCLE)
                    .into(holder.mAvatarIv);

            if (!TextUtils.isEmpty(phone) && SystemUtil.checkPhoneNumber(phone)) {
                //如果是电话号码
                holder.mContactLayout.setVisibility(View.VISIBLE);
                holder.mHDividerLine.setVisibility(View.VISIBLE);
                //拨打电话
                holder.mTelTv.setOnClickListener(v -> {
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

                holder.mSmsTv.setOnClickListener(v -> {
                    Context context = v.getContext();
                    DialogHelper.create(DialogHelper.TYPE_NORMAL)
                            .cancelable(true)
                            .canceledOnTouchOutside(true)
                            .title(context.getString(R.string.tip))
                            .content(context.getString(R.string.send_message_tips, phone))
                            .leftButton(context.getString(R.string.cancel), ContextCompat.getColor(context, R.color.black_54))
                            .rightButton(context.getString(R.string.confirm), ContextCompat.getColor(context, R.color.themes_main))
                            .rightBtnClickListener((dialog, view) -> {
                                dialog.dismiss();
                                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phone));
                                if (intent.resolveActivity(context.getPackageManager()) != null) {
                                    context.startActivity(intent);
                                }
                            }).leftBtnClickListener((dialog, view) -> dialog.dismiss())
                            .show();
                });
            } else {
                holder.mContactLayout.setVisibility(View.GONE);
                holder.mHDividerLine.setVisibility(View.GONE);
            }
        } else {
            emptyHolder.mEmptyView.setImageResource(R.drawable.comment_empty_bg);
        }
        return convertView;
    }

    private View createViewByType(int type, Context context) {
        View view;
        if (type == TYPE_DEFAULT) {
            view = LayoutInflater.from(context).inflate(R.layout.vw_comment_item, null, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.vw_website_empty_item, null, false);
        }
        return view;
    }

    class ViewHolder {
        ImageView mAvatarIv;
        TextView mNickNameTv;
        TextView mContentTv;
        View mHDividerLine;
        View mContactLayout;
        TextView mTelTv;
        TextView mSmsTv;
    }

    class EmptyHolder {
        ImageView mEmptyView;
    }
}
