package com.idrv.coach.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.NinePatch;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.idrv.coach.R;
import com.idrv.coach.bean.ChildView;
import com.idrv.coach.bean.Message;
import com.idrv.coach.ui.view.NicknameAndAvatarView;
import com.idrv.coach.utils.Logger;
import com.idrv.coach.utils.PixelUtil;
import com.idrv.coach.utils.SchemeUtils;
import com.idrv.coach.utils.StringUtil;
import com.idrv.coach.utils.TimeUtil;
import com.idrv.coach.utils.ValidateUtil;
import com.joooonho.SelectableRoundedImageView;
import com.zjb.loader.ZjbImageLoader;
import com.zjb.loader.internal.core.assist.ImageScaleType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * time:2016/8/1
 * description:首页列表适配器
 *
 * @author sunjianfei
 */
public class HomeAdapter extends AbsRecycleAdapter<Message, HomeAdapter.ItemViewHolder> {
    //内存缓存气泡
    Map<String, NinePatchDrawable> mBubbleMap = new HashMap<>();

    public void addDataFromFirst(List<Message> messages) {
        if (ValidateUtil.isValidate(mData)) {
            if (ValidateUtil.isValidate(messages)) {
                mData.addAll(2, messages);
            }
        } else {
            setData(messages);
        }
    }

    public void addDataFromFirst(Message message) {
        if (ValidateUtil.isValidate(mData)) {
            if (null != message) {
                mData.add(0, message);
            }
        }
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vw_chat_left_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        Message message = mData.get(position);
        List<ChildView> childViewList = message.getComponents();

        //加载头像
        ZjbImageLoader.create(message.getIcon())
                .setDefaultRes(R.drawable.icon_user_avatar_default_92)
                .setQiniu((int) PixelUtil.dp2px(50), (int) PixelUtil.dp2px(50))
                .setImageScaleType(ImageScaleType.EXACTLY)
                .into(holder.mAvatarIv);

        //气泡的下载链接
        String bubbleImageUrl = message.getBubble();
        if (!TextUtils.isEmpty(bubbleImageUrl)) {
            NinePatchDrawable drawable = mBubbleMap.get(message.getBubble());
            if (null != drawable) {
                holder.mBubbleLayout.setBackgroundDrawable(drawable);
            } else {
                //下载气泡
                ZjbImageLoader.create(message.getBubble())
                        .setBitmapConfig(Bitmap.Config.ARGB_8888)
                        .isCompress(false)
                        .load();
                //加载到内存
                getBubbleDrawable(bubbleImageUrl, context)
                        .subscribe(Logger::e, Logger::e);
                holder.mBubbleLayout.setBackgroundResource(R.drawable.bubble_default);
            }
        } else {
            holder.mBubbleLayout.setBackgroundResource(R.drawable.bubble_default);
        }

        //主昵称
        String nickName = TextUtils.isEmpty(message.getSource()) ? context.getString(R.string.student) : message.getSource();
        String subNickname = message.getChannel();
        holder.mNickNameTv.setText(nickName);
        if (!TextUtils.isEmpty(subNickname)) {
            holder.mSubNickNameTv.setText(subNickname);
        } else {
            holder.mSubNickNameTv.setText("");
        }

        //消息时间
        if (position < 2) {
            holder.mMessageTimeTv.setVisibility(View.GONE);
        } else if (position == 2) {
            holder.mMessageTimeTv.setVisibility(View.VISIBLE);
            holder.mMessageTimeTv.setText(TimeUtil.getLastMessageTime(TimeUtil.getTempTime(message.getTime())));
        } else {
            //上一条消息
            Message prevMessage = mData.get(position - 1);
            if (TimeUtil.showMessageTime(message.getTime(), prevMessage.getTime())) {
                holder.mMessageTimeTv.setVisibility(View.VISIBLE);
                holder.mMessageTimeTv.setText(TimeUtil.getLastMessageTime(TimeUtil.getTempTime(message.getTime())));
            } else {
                holder.mMessageTimeTv.setVisibility(View.GONE);
            }
        }

        //设置item的点击事件
        String itemScheme = message.getSchema();
        if (!TextUtils.isEmpty(itemScheme)) {
            holder.itemView.setOnClickListener(v -> SchemeUtils.schemeJump(v.getContext(), itemScheme));
        } else {
            holder.itemView.setOnClickListener(null);
        }


        //遍历子View,初始化状态
        for (View view : holder.mChildViews) {
            view.setVisibility(View.GONE);
        }

        boolean hasVDividerLine = false;
        boolean hasHDividerLine = false;

        //遍历服务器配置的子View
        for (ChildView childView : childViewList) {
            //对应子View显示的位置
            int areaId = childView.getAreaId();
            int index = areaId - 1;
            String scheme = childView.getSchema();
            View view = holder.mChildViews[index];

            //特殊处理分割线,如果有底部按钮
            if (index == 8 || index == 9 || index == 10) {
                hasHDividerLine = true;
                if (index != 10) {
                    hasVDividerLine = true;
                }
            }

            //控制分割线的显示
            holder.mVDividerLine.setVisibility(hasVDividerLine ? View.VISIBLE : View.GONE);
            holder.mHDividerLine.setVisibility(hasHDividerLine ? View.VISIBLE : View.GONE);

            //显示
            view.setVisibility(View.VISIBLE);

            //设置View的点击跳转事件
            if (!TextUtils.isEmpty(scheme)) {
                view.setOnClickListener(v -> SchemeUtils.schemeJump(v.getContext(), scheme));
            }

            if (view instanceof TextView) {
                //文本
                TextView mTextView = (TextView) view;
                int color = TextUtils.isEmpty(childView.getColor()) ? 0xFF000000 : Color.parseColor("#" + childView.getColor());
                int gravity = getTextGravity(childView.getAlign());

                //设置文本内容
                String content = childView.getText();
                if (StringUtil.isHtml(content)) {
                    mTextView.setText(Html.fromHtml(content));
                } else {
                    mTextView.setText(content);
                    //字体颜色
                    mTextView.setTextColor(color);
                }

                String mStr = mTextView.getText().toString();
                if (!TextUtils.isEmpty(mStr)) {
                    if (mStr.length() >= 13) {
                        mTextView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                    } else {
                        mTextView.setGravity(Gravity.CENTER);
                    }
                }

            } else if (view instanceof NicknameAndAvatarView) {
                NicknameAndAvatarView nicknameAndAvatarView = (NicknameAndAvatarView) view;
                int color = TextUtils.isEmpty(childView.getColor()) ? 0xFF000000 : Color.parseColor("#" + childView.getColor());
                int gravity = getTextGravity(childView.getAlign());

                nicknameAndAvatarView.setAvatar(childView.getIcon());
                nicknameAndAvatarView.setNickname(childView.getText());
                nicknameAndAvatarView.setTextGravity(gravity);
                nicknameAndAvatarView.setTextColor(color);
            } else if (view instanceof ImageView) {
                ImageView mImageView = (ImageView) view;
                //显示图片
                ZjbImageLoader.create(childView.getIcon())
                        .setDisplayType(ZjbImageLoader.DISPLAY_DEFAULT)
                        .setDefaultDrawable(new ColorDrawable(0xffe0dedc))
                        .setImageScaleType(ImageScaleType.EXACTLY)
                        .into(mImageView);
            }
        }

    }

    private int getTextGravity(int align) {
        if (align == 0) {
            return Gravity.LEFT | Gravity.CENTER_VERTICAL;
        } else if (align == 1) {
            return Gravity.CENTER;
        } else {
            return Gravity.RIGHT | Gravity.CENTER_VERTICAL;
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.avatar)
        SelectableRoundedImageView mAvatarIv;
        @InjectView(R.id.nick_name)
        TextView mNickNameTv;
        @InjectView(R.id.sub_nick)
        TextView mSubNickNameTv;
        @InjectView(R.id.image_1)
        ImageView mImage1;
        @InjectView(R.id.text_2)
        TextView mText2;
        @InjectView(R.id.text_3)
        TextView mText3;
        @InjectView(R.id.image_4)
        NicknameAndAvatarView mImage4;
        @InjectView(R.id.image_5)
        ImageView mImage5;
        @InjectView(R.id.text_6)
        TextView mText6;
        @InjectView(R.id.text_7)
        TextView mText7;
        @InjectView(R.id.text_8)
        TextView mText8;
        @InjectView(R.id.text_9)
        TextView mText9;
        @InjectView(R.id.text10)
        TextView mText10;
        @InjectView(R.id.image11)
        ImageView mImage11;
        @InjectView(R.id.text12)
        TextView mText12;
        @InjectView(R.id.image13)
        ImageView mImage13;
        @InjectView(R.id.text14)
        TextView mText14;
        @InjectView(R.id.button_h_divider_line)
        View mHDividerLine;
        @InjectView(R.id.button_v_divider_line)
        View mVDividerLine;
        @InjectView(R.id.message_time_tv)
        TextView mMessageTimeTv;
        @InjectView(R.id.bubble_layout)
        View mBubbleLayout;
        View[] mChildViews = new View[14];


        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);

            mChildViews[0] = mImage1;
            mChildViews[1] = mText2;
            mChildViews[2] = mText3;
            mChildViews[3] = mImage4;
            mChildViews[4] = mImage5;
            mChildViews[5] = mText6;
            mChildViews[6] = mText7;
            mChildViews[7] = mText8;
            mChildViews[8] = mText9;
            mChildViews[9] = mText10;
            mChildViews[10] = mImage11;
            mChildViews[11] = mText12;
            mChildViews[12] = mImage13;
            mChildViews[13] = mText14;
        }
    }

    private Observable<String> getBubbleDrawable(String imageUrl, Context context) {
        return Observable.<String>create(subscriber -> {
            if (TextUtils.isEmpty(imageUrl)) {
                subscriber.onError(new NullPointerException());
                return;
            }
            //先从内存缓存中获取
            NinePatchDrawable drawable = mBubbleMap.get(imageUrl);
            if (null != drawable) {
                subscriber.onNext("bitmap has cached!");
                subscriber.onCompleted();
                return;
            }

            String filePath = ZjbImageLoader.getQiniuDiskCachePath(imageUrl);
            File imageFile = new File(filePath);

            if (imageFile.exists()) {
                filePath = imageFile.getAbsolutePath();
                Rect rect = new Rect();
                Bitmap bm = null;
                InputStream stream = null;
                try {
                    stream = new FileInputStream(filePath);
                    bm = BitmapFactory.decodeStream(stream, rect, null);
                    byte[] chunk = bm.getNinePatchChunk();
                    if (NinePatch.isNinePatchChunk(chunk)) {
                        //如果是合法的NinePatchDrawable
                        NinePatchDrawable patchDrawable = new NinePatchDrawable(context.getResources(), bm, chunk,
                                rect, null);
                        mBubbleMap.put(imageUrl, patchDrawable);
                    }
                    subscriber.onNext("complete!");
                    subscriber.onCompleted();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            } else {
                subscriber.onNext("image not download!");
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io());
    }
}
