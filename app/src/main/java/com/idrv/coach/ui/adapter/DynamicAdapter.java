package com.idrv.coach.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.idrv.coach.R;
import com.idrv.coach.ZjbApplication;
import com.idrv.coach.bean.TeamInvite;
import com.idrv.coach.bean.Trend;
import com.idrv.coach.bean.User;
import com.idrv.coach.data.manager.LoginManager;
import com.idrv.coach.ui.view.NoneView;
import com.idrv.coach.ui.view.VerticalImageSpan;
import com.idrv.coach.utils.TimeUtil;
import com.idrv.coach.utils.ValidateUtil;
import com.idrv.coach.utils.helper.ViewUtils;
import com.zjb.loader.ZjbImageLoader;
import com.zjb.loader.internal.core.assist.ImageScaleType;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.idrv.coach.ZjbApplication.gContext;

/**
 * time:2016/3/24
 * description:
 *
 * @author sunjianfei
 */
public class DynamicAdapter extends AbsRecycleAdapter<Trend, RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0X000;
    private static final int TYPE_ACCESS = 0X001;
    private static final int TYPE_JOIN = 0X002;
    private static final int TYPE_SHARE = 0X003;
    private static final int TYPE_ASK = 0X004;
    private static final int TYPE_DEFAULT = 0x005;

    //学员点赞
    private static final int TREND_0 = 0;
    private static final int TREND_1 = 1;
    //教练入团
    private static final int TREND_2 = 2;
    //教练分享资讯
    private static final int TREND_3 = 3;
    //助驾帮访问
    private static final int TREND_4 = 4;
    //学员转发
    private static final int TREND_5 = 5;
    //学员访问
    private static final int TREND_6 = 6;
    //评论
    private static final int TREND_7 = 7;
    //咨询
    private static final int TREND_8 = 8;

    //header最多显示9个头像
    private static final int LIKE_COUNT = 9;

    private TeamInvite mTeamInvite;
    private int praiseSum;
    private List<String> praiserAvators;

    private LikeListener mListener;

    public void setTeamInvite(TeamInvite teamInvite) {
        this.mTeamInvite = teamInvite;
    }

    public void setPraiseSum(int praiseSum) {
        this.praiseSum = praiseSum;
    }

    public void setPraiserAvators(List<String> praiserAvators) {
        this.praiserAvators = praiserAvators;
    }

    public void setListener(LikeListener listener) {
        this.mListener = listener;
    }

    @Override
    public int getItemCount() {
        return ValidateUtil.isValidate(mData) ? mData.size() + 1 : 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else {
            Trend trend = mData.get(position - 1);
            int type = trend.getType();
            switch (type) {
                case TREND_0:
                case TREND_3:
                case TREND_4:
                case TREND_5:
                case TREND_6:
                    return TYPE_SHARE;
                case TREND_1:
                    return TYPE_ACCESS;
                case TREND_2:
                    return TYPE_JOIN;
                case TREND_7:
                case TREND_8:
                    return TYPE_ASK;
            }
        }
        return TYPE_DEFAULT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return createViewHolder(viewType, parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        if (type == TYPE_HEADER) {
            showItem(holder, null, type, position);
        } else {
            Trend trend = mData.get(position - 1);
            showItem(holder, trend, type, position);
        }
    }

    private RecyclerView.ViewHolder createViewHolder(int type, ViewGroup parent) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = null;
        switch (type) {
            case TYPE_HEADER:
                view = inflater.inflate(R.layout.vw_dynamic_header, parent, false);
                viewHolder = new HeaderViewHolder(view);
                break;
            case TYPE_ACCESS:
                view = inflater.inflate(R.layout.vw_dynamic_item_type_access, parent, false);
                viewHolder = new ItemTypeAccessViewHolder(view);
                break;
            case TYPE_JOIN:
                view = inflater.inflate(R.layout.vw_dynamic_item_type_join, parent, false);
                viewHolder = new ItemTypeJoinViewHolder(view);
                break;
            case TYPE_SHARE:
                view = inflater.inflate(R.layout.vw_dynamic_item_type_share, parent, false);
                viewHolder = new ItemTypeShareViewHolder(view);
                break;
            case TYPE_ASK:
                view = inflater.inflate(R.layout.vw_dynamic_item_type_ask, parent, false);
                viewHolder = new ItemAskViewHolder(view);
                break;
            case TYPE_DEFAULT:
                view = new NoneView(parent.getContext());
                viewHolder = new ItemDefaultViewHolder(view);
                break;
        }
        return viewHolder;
    }

    private void showItem(RecyclerView.ViewHolder holder, Trend trend, int type, int pos) {
        Context context = holder.itemView.getContext();
        switch (type) {
            case TYPE_HEADER: {
                //1.header部分
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                User user = LoginManager.getInstance().getLoginUser();
                ViewUtils.showRingAvatar(headerViewHolder.mAvatar, user.getHeadimgurl());
                headerViewHolder.mNickNameTv.setText(user.getNickname());
                headerViewHolder.mLikeTv.setText(praiseSum + "");

                if (null == mTeamInvite) {
                    headerViewHolder.mBubbleIv.setVisibility(View.GONE);
                } else {
                    headerViewHolder.mBubbleIv.setVisibility(View.VISIBLE);
                    headerViewHolder.mBubbleIv.setOnClickListener(v -> {
                        if (null != mListener) {
                            mListener.onShowInvite(mTeamInvite);
                        }
                    });
                }
            }
            break;
            case TYPE_ACCESS: {
                ItemTypeAccessViewHolder viewHolder = (ItemTypeAccessViewHolder) holder;
                String time = TimeUtil.getFeedsTime(TimeUtil.getTempTime(trend.getTime()));
                User user = LoginManager.getInstance().getLoginUser();
                viewHolder.mNickNameTv.setText(user.getNickname());
                viewHolder.mInfluenceTv.setText(R.string.access_tips);
                viewHolder.mTimeTv.setText(time);
                List<String> avatars = trend.getAvators();
                if (ValidateUtil.isValidate(avatars)) {
                    //只显示最多5个头像
                    if (avatars.size() > 5) {
                        avatars = avatars.subList(0, 5);
                    }
                    updateLikeLayout(avatars, viewHolder.mAccessLayout, false);
                }
                //1.显示头像
                ZjbImageLoader.create(user.getHeadimgurl())
                        .setDefaultRes(R.drawable.icon_user_avatar_default_92)
                        .setQiniu(100, 100)
                        .setImageScaleType(ImageScaleType.EXACTLY)
                        .setDisplayType(ZjbImageLoader.DISPLAY_ROUND)
                        .into(viewHolder.mAvatar);
            }
            break;
            case TYPE_JOIN: {
                ItemTypeJoinViewHolder viewHolder = (ItemTypeJoinViewHolder) holder;
                String teamName = context.getString(R.string.join_team, trend.getTeamName());
                String time = TimeUtil.getFeedsTime(TimeUtil.getTempTime(trend.getTime()));
                //1.显示头像
                ZjbImageLoader.create(trend.getCoachHeadimgurl())
                        .setDefaultRes(R.drawable.icon_user_avatar_default_92)
                        .setQiniu(100, 100)
                        .setImageScaleType(ImageScaleType.EXACTLY)
                        .setDisplayType(ZjbImageLoader.DISPLAY_ROUND)
                        .into(viewHolder.mAvatar);
                viewHolder.mNickNameTv.setText(context.getString(R.string.coach, trend.getCoachNickName()));
                viewHolder.mSubjectTv.setText(teamName);
                viewHolder.mTimeTv.setText(time);
                if (!ValidateUtil.isValidate(trend.getNickNames())) {
                    viewHolder.mLikeLayout.setVisibility(View.GONE);
                } else {
                    viewHolder.mLikeLayout.setVisibility(View.VISIBLE);
                    viewHolder.mLikeTv.setText(getImageSpan(trend));
                }
                if (trend.isPraised()) {
                    viewHolder.mLikeBtn.setImageResource(R.drawable.icon_good_on);
                    viewHolder.mLikeBtn.setOnClickListener(null);
                } else {
                    viewHolder.mLikeBtn.setImageResource(R.drawable.icon_good_off);
                    viewHolder.mLikeBtn.setOnClickListener(v -> {
                        ViewUtils.setDelayedClickable(v, 500);

                        trend.setIsPraised(true);
                        trend.addPraiseSum();
                        User user = LoginManager.getInstance().getLoginUser();
                        trend.addLikeName(user.getNickname());

                        String coachId = trend.getCoachId();
                        //如果是我自己的消息
                        if (user.getUid().equals(coachId)) {
                            //如果列表里没有我的头像,则添加
                            if (!praiserAvators.contains(user.getHeadimgurl())) {
                                praiserAvators.add(user.getHeadimgurl());
                            }
                            praiseSum += 1;
                        }
                        //刷新header
                        notifyItemChanged(0);
                        notifyItemChanged(pos);
                        if (null != mListener) {
                            mListener.onLike(trend.getTargetId(), trend.getCoachId(), trend.getType());
                        }
                    });
                }
            }
            break;
            case TYPE_SHARE: {
                //1.如果是:教练动态
                ItemTypeShareViewHolder viewHolder = (ItemTypeShareViewHolder) holder;
                String time = TimeUtil.getFeedsTime(TimeUtil.getTempTime(trend.getTime()));
                String avatarUrl;
                if (trend.getType() == TREND_3) {
                    avatarUrl = trend.getCoachHeadimgurl();
                    viewHolder.mMyTeamIcon.setVisibility(View.VISIBLE);
                    viewHolder.mOpIv.setVisibility(View.GONE);
                    viewHolder.mNickNameTv.setText(context.getString(R.string.coach, trend.getCoachNickName()));
                    viewHolder.mInfluenceTv.setText(context.getString(R.string.day_influence, trend.getImpact()));
                    viewHolder.mTimeTv.setText(time);
                    viewHolder.mLikeBtn.setVisibility(View.VISIBLE);
                    viewHolder.mLikeLayout.setVisibility(View.VISIBLE);
                    viewHolder.mHline.setVisibility(View.VISIBLE);

                    if (ValidateUtil.isValidate(trend.getNickNames())) {
                        viewHolder.mLikeLayout.setVisibility(View.VISIBLE);
                        viewHolder.mLikeTv.setText(getImageSpan(trend));
                    } else {
                        viewHolder.mLikeLayout.setVisibility(View.GONE);
                    }
                    if (trend.isPraised()) {
                        viewHolder.mLikeBtn.setImageResource(R.drawable.icon_good_on);
                        viewHolder.mLikeBtn.setOnClickListener(null);
                    } else {
                        viewHolder.mLikeBtn.setImageResource(R.drawable.icon_good_off);
                        viewHolder.mLikeBtn.setOnClickListener(v -> {
                            ViewUtils.setDelayedClickable(v, 500);

                            trend.setIsPraised(true);
                            trend.addPraiseSum();

                            User user = LoginManager.getInstance().getLoginUser();
                            trend.addLikeName(user.getNickname());

                            String coachId = trend.getCoachId();
                            //如果是我自己的消息
                            if (user.getUid().equals(coachId)) {
                                //如果列表里没有我的头像,则添加
                                if (!praiserAvators.contains(user.getHeadimgurl())) {
                                    praiserAvators.add(user.getHeadimgurl());
                                }
                                praiseSum += 1;
                            }
                            //刷新header
                            notifyItemChanged(0);
                            notifyItemChanged(pos);
                            if (null != mListener) {
                                mListener.onLike(trend.getTargetId(), trend.getCoachId(), trend.getType());
                            }
                        });
                    }
                } else {
                    String subject = null;
                    //1.头像
                    avatarUrl = trend.getStudentHeadimgurl();
                    //2.昵称
                    String nickname = trend.getStudentNickName();
                    int mType = trend.getType();
                    if (mType == TREND_4) {
                        subject = trend.isHasQrCode() ? context.getString(R.string.type_forwarding)
                                : context.getString(R.string.upload_qr_code);
                        viewHolder.mOpIv.setVisibility(View.VISIBLE);
                        viewHolder.mOpIv.setImageResource(R.drawable.re_share);
                    } else {
                        String studentNickName = trend.getStudentNickName();
                        if (!TextUtils.isEmpty(studentNickName)) {
                            if (studentNickName.length() > 6) {
                                studentNickName = studentNickName.substring(0, 6);
                            }
                        } else {
                            studentNickName = context.getString(R.string.default_student_name);
                        }
                        if (mType == TREND_0) {
                            //需要判断点赞的主体是我本人还是团队内的其他教练
                            if (LoginManager.getInstance().getUid().equals(trend.getCoachId())) {
                                viewHolder.mOpIv.setVisibility(View.VISIBLE);
                                viewHolder.mOpIv.setImageResource(R.drawable.lighting);
                                viewHolder.mMyTeamIcon.setVisibility(View.GONE);
                                subject = context.getString(R.string.type_like);
                            } else if (!TextUtils.isEmpty(trend.getCoachNickName())) {
                                viewHolder.mMyTeamIcon.setVisibility(View.VISIBLE);
                                viewHolder.mOpIv.setVisibility(View.GONE);
                                nickname = trend.getCoachNickName();
                                avatarUrl = trend.getCoachHeadimgurl();
                                subject = String.format(context.getString(R.string.type_share_like), studentNickName);
                            }
                        } else if (mType == TREND_5) {
                            //需要判断分享的主体是我本人还是团队内的其他教练
                            if (LoginManager.getInstance().getUid().equals(trend.getCoachId())) {
                                viewHolder.mOpIv.setVisibility(View.VISIBLE);
                                viewHolder.mOpIv.setImageResource(R.drawable.re_share);
                                viewHolder.mMyTeamIcon.setVisibility(View.GONE);
                                subject = context.getString(R.string.type_forwarding);
                            } else if (!TextUtils.isEmpty(trend.getCoachNickName())) {
                                viewHolder.mMyTeamIcon.setVisibility(View.VISIBLE);
                                viewHolder.mOpIv.setVisibility(View.GONE);
                                nickname = trend.getCoachNickName();
                                avatarUrl = trend.getCoachHeadimgurl();
                                subject = String.format(context.getString(R.string.type_share_forwarding), studentNickName);
                            }
                        } else if (mType == TREND_6) {
                            viewHolder.mMyTeamIcon.setVisibility(View.GONE);
                            viewHolder.mOpIv.setVisibility(View.VISIBLE);
                            viewHolder.mOpIv.setImageResource(R.drawable.icon_access);
                            nickname = trend.getStudentNickName();
                            avatarUrl = trend.getStudentHeadimgurl();
                            subject = context.getString(R.string.someone_read_my_shared_news);
                        }
                    }

                    if (TextUtils.isEmpty(nickname)) {
                        nickname = context.getResources().getString(R.string.student);
                    }

                    viewHolder.mNickNameTv.setText(nickname);
                    viewHolder.mInfluenceTv.setText(subject);
                    viewHolder.mTimeTv.setText(time);
                    viewHolder.mLikeBtn.setVisibility(View.GONE);
                    viewHolder.mLikeLayout.setVisibility(View.GONE);
                    viewHolder.mHline.setVisibility(View.GONE);
                }
                viewHolder.mWebLayout.setVisibility(View.VISIBLE);
                viewHolder.mWebNameTv.setText(trend.getTaskName());

                ZjbImageLoader.create(avatarUrl)
                        .setDefaultRes(R.drawable.icon_user_avatar_default_92)
                        .setQiniu(100, 100)
                        .setImageScaleType(ImageScaleType.EXACTLY)
                        .setDisplayType(ZjbImageLoader.DISPLAY_ROUND)
                        .into(viewHolder.mAvatar);

            }
            break;
            case TYPE_ASK: {
                ItemAskViewHolder viewHolder = (ItemAskViewHolder) holder;
                int mType = trend.getType();
                String nickName = trend.getStudentNickName();
                String avatarUrl = trend.getStudentHeadimgurl();
                String time = TimeUtil.getFeedsTime(TimeUtil.getTempTime(trend.getTime()));
                boolean isComment = mType == TREND_7;
                String subText = isComment ? context.getString(R.string.comment_me_at_my_website)
                        : context.getString(R.string.ask_me_at_my_website);
                viewHolder.mNickNameTv.setText(nickName);
                viewHolder.mSubTv.setText(subText);
                viewHolder.mTimeTv.setText(time);
                viewHolder.mContentTv.setText(trend.getContent());

                ZjbImageLoader.create(avatarUrl)
                        .setDefaultRes(R.drawable.icon_user_avatar_default_92)
                        .setQiniu(100, 100)
                        .setImageScaleType(ImageScaleType.EXACTLY)
                        .setDisplayType(ZjbImageLoader.DISPLAY_ROUND)
                        .into(viewHolder.mAvatar);

                if (!isComment) {
                    viewHolder.mSubImageView.setImageResource(R.drawable.ask_me);
                    viewHolder.mRightOpBtn.setVisibility(View.VISIBLE);
                    viewHolder.mRightOpBtn.setOnClickListener(v -> {
                        String phoneNumber = trend.getStudentPhone();
                        if (!TextUtils.isEmpty(phoneNumber)) {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + phoneNumber));
                            if (intent.resolveActivity(context.getPackageManager()) != null) {
                                context.startActivity(intent);
                            }
                        }
                    });
                } else {
                    viewHolder.mSubImageView.setImageResource(R.drawable.commented);
                    viewHolder.mRightOpBtn.setVisibility(View.GONE);
                }
            }
            break;
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.avatar)
        ImageView mAvatar;
        @InjectView(R.id.name_tv)
        TextView mNickNameTv;
        @InjectView(R.id.like_count_tv)
        TextView mLikeTv;
        @InjectView(R.id.invite_bubble)
        View mBubbleIv;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    class ItemTypeJoinViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.avatar)
        ImageView mAvatar;
        @InjectView(R.id.nick_name)
        TextView mNickNameTv;
        @InjectView(R.id.subject_tv)
        TextView mSubjectTv;
        @InjectView(R.id.time)
        TextView mTimeTv;
        @InjectView(R.id.like_btn)
        ImageView mLikeBtn;
        @InjectView(R.id.like_tv)
        TextView mLikeTv;
        @InjectView(R.id.like_layout)
        View mLikeLayout;

        public ItemTypeJoinViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    class ItemTypeAccessViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.avatar)
        ImageView mAvatar;
        @InjectView(R.id.nick_name)
        TextView mNickNameTv;
        @InjectView(R.id.influence_tv)
        TextView mInfluenceTv;
        @InjectView(R.id.time)
        TextView mTimeTv;
        @InjectView(R.id.access_layout)
        LinearLayout mAccessLayout;


        public ItemTypeAccessViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    class ItemTypeShareViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.my_team_iv)
        ImageView mMyTeamIcon;
        @InjectView(R.id.avatar)
        ImageView mAvatar;
        @InjectView(R.id.nick_name)
        TextView mNickNameTv;
        @InjectView(R.id.influence_tv)
        TextView mInfluenceTv;
        @InjectView(R.id.time)
        TextView mTimeTv;
        @InjectView(R.id.like_btn)
        ImageView mLikeBtn;
        @InjectView(R.id.like_tv)
        TextView mLikeTv;
        @InjectView(R.id.like_layout)
        View mLikeLayout;
        @InjectView(R.id.web_layout)
        View mWebLayout;
        @InjectView(R.id.web_name)
        TextView mWebNameTv;
        @InjectView(R.id.h_line)
        View mHline;
        @InjectView(R.id.op_icon)
        ImageView mOpIv;

        public ItemTypeShareViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    class ItemAskViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.avatar)
        ImageView mAvatar;
        @InjectView(R.id.nick_name)
        TextView mNickNameTv;
        @InjectView(R.id.right_op_btn)
        ImageView mRightOpBtn;
        @InjectView(R.id.sub_text_view)
        TextView mSubTv;
        @InjectView(R.id.content_tv)
        TextView mContentTv;
        @InjectView(R.id.time)
        TextView mTimeTv;
        @InjectView(R.id.sub_image)
        ImageView mSubImageView;

        public ItemAskViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    class ItemDefaultViewHolder extends RecyclerView.ViewHolder {

        public ItemDefaultViewHolder(View itemView) {
            super(itemView);
        }
    }

    private void updateLikeLayout(List<String> avatars, LinearLayout mLikeLayout, boolean needPadding) {
        if (ValidateUtil.isValidate(avatars)) {
            int count = 0;
            int sWidth = gContext.getResources().getDisplayMetrics().widthPixels;
            int firstElementMarginLeft = Math.round(gContext.getResources()
                    .getDimension(R.dimen.like_author_margin));
            int contentWidth = sWidth - 2 * firstElementMarginLeft;
            int imageWidth = Math.round(contentWidth / (((LIKE_COUNT) - 1) * 0.4167f + LIKE_COUNT));
            int marginLeft = Math.round(imageWidth * 0.4167f);
            mLikeLayout.setVisibility(View.VISIBLE);
            mLikeLayout.setOrientation(LinearLayout.HORIZONTAL);
            mLikeLayout.removeAllViews();
            for (int i = 0; i < avatars.size(); i++) {
                String avatar = avatars.get(i);
                if (TextUtils.isEmpty(avatar)) {
                    continue;
                }
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imageWidth, imageWidth);
                if (needPadding) {
                    params.leftMargin = count > 0 ? marginLeft : firstElementMarginLeft;
                } else {
                    if (i != 0) {
                        params.leftMargin = count > 0 ? marginLeft : firstElementMarginLeft;
                    }
                }
                if (count >= LIKE_COUNT - 1) {
                    int num = avatars.size();
                    View view = getMoreImageView(num + "");
                    mLikeLayout.addView(view, params);
                    view.setOnClickListener(v -> {
                        ViewUtils.setDelayedClickable(v, 800);
                    });
                    break;
                } else {
                    ImageView likeImageView = getLikeImageView(avatar);
                    mLikeLayout.addView(likeImageView, params);
                    likeImageView.setOnClickListener(v -> {
                        ViewUtils.setDelayedClickable(v, 800);
                    });
                }
                count++;
            }
        }
    }


    public View getMoreImageView(String num) {
        View view = LayoutInflater.from(gContext).inflate(R.layout.vw_dynamic_header_arrow_right, null, false);
        TextView textView = (TextView) view.findViewById(R.id.text);
        int number = Integer.parseInt(num);
        number = number > 99 ? 99 : number;
        textView.setText(number + "");
        return view;
    }

    public ImageView getLikeImageView(String url) {
        ImageView imageView = new ImageView(gContext);
        ZjbImageLoader.create(url)
                .setDefaultRes(R.drawable.icon_user_avatar_default_92)
                .setQiniu(100, 100)
                .setImageScaleType(ImageScaleType.EXACTLY)
                .setDisplayType(ZjbImageLoader.DISPLAY_ROUND)
                .into(imageView);
        return imageView;
    }

    public interface LikeListener {
        void onLike(String targetId, String coachId, int type);

        void onShowInvite(TeamInvite invite);

    }

    private SpannableString getImageSpan(Trend trend) {
        Bitmap b = BitmapFactory.decodeResource(gContext.getResources(), R.drawable.icon_good_for_name);
        VerticalImageSpan imgSpan = new VerticalImageSpan(gContext, b);
        String strSpan = trend.getNameStr();
        int praiseSum = trend.getPraiseSum();
        if (praiseSum > 6) {
            strSpan = strSpan + ZjbApplication.gContext.getString(R.string.praise_sum, praiseSum);
        }
        SpannableString spanString = new SpannableString("icon" + strSpan);
        spanString.setSpan(imgSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanString;
    }
}
