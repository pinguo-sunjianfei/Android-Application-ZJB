package com.idrv.coach.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
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

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * time:2016/8/8
 * description:评论列表
 *
 * @author sunjianfei
 */
public class CommentListAdapter extends AbsRecycleAdapter<Comment, RecyclerView.ViewHolder> {
    DeleteCommentListener mListener;

    public void setDeleteCommentListener(DeleteCommentListener listener) {
        this.mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vw_comment_list_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        ItemViewHolder viewHolder = (ItemViewHolder) holder;
        Comment comment = mData.get(position);
        String phone = comment.getPhone();
        int messageType = comment.getMessageType();
        int messageTypeDrawableRes = messageType == 3 ? R.drawable.icon_comment : R.drawable.icon_comment_access;
        String messageTypeStr = messageType == 3 ? context.getString(R.string.comment) : context.getString(R.string.access);

        //显示头像
        ZjbImageLoader.create(comment.getHeadimgurl())
                .setQiniu(80, 80)
                .setDefaultRes(R.drawable.avatar_circle_default)
                .setDisplayType(ZjbImageLoader.DISPLAY_CIRCLE)
                .into(viewHolder.mAvatarIv);
        viewHolder.mNicknameTv.setText(TextUtils.isEmpty(comment.getNickname()) ? "学员" : comment.getNickname());
        viewHolder.mContentTv.setText(comment.getContent());


        viewHolder.mCommentTypeIv.setImageResource(messageTypeDrawableRes);

        if (!TextUtils.isEmpty(phone) && SystemUtil.checkPhoneNumber(phone)) {
            //如果是电话号码
            viewHolder.mContactLayout.setVisibility(View.VISIBLE);
            //拨打电话
            viewHolder.mCallTv.setOnClickListener(v -> {
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

            viewHolder.mMessageTv.setOnClickListener(v -> {
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
            viewHolder.mContactLayout.setVisibility(View.GONE);
        }

        viewHolder.mDeleteTv.setOnClickListener(v -> {
            if (null != mListener) {
                DialogHelper.create(DialogHelper.TYPE_NORMAL)
                        .title(context.getString(R.string.tip))
                        .content(context.getString(R.string.confirm_delete_comment, messageTypeStr))
                        .leftButton(context.getString(R.string.cancel), ContextCompat.getColor(context, R.color.black_54))
                        .rightButton(context.getString(R.string.confirm), ContextCompat.getColor(context, R.color.themes_main))
                        .leftBtnClickListener((dialog, view) -> dialog.dismiss())
                        .rightBtnClickListener((dialog, view) -> {
                            dialog.dismiss();
                            mListener.onCommentDelete(comment, position);
                        }).show();

            }
        });
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.avatar)
        ImageView mAvatarIv;
        @InjectView(R.id.nick_name)
        TextView mNicknameTv;
        @InjectView(R.id.image_type)
        ImageView mCommentTypeIv;
        @InjectView(R.id.delete_tv)
        TextView mDeleteTv;
        @InjectView(R.id.content_tv)
        TextView mContentTv;
        @InjectView(R.id.contact_layout)
        View mContactLayout;
        @InjectView(R.id.call_tv)
        TextView mCallTv;
        @InjectView(R.id.message_tv)
        TextView mMessageTv;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    public interface DeleteCommentListener {
        void onCommentDelete(Comment comment, int pos);
    }
}
