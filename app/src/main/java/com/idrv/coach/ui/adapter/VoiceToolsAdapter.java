package com.idrv.coach.ui.adapter;

import android.graphics.drawable.AnimationDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.idrv.coach.R;
import com.idrv.coach.bean.VoiceTool;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

/**
 * time:2016/8/17
 * description:
 *
 * @author sunjianfei
 */
public class VoiceToolsAdapter extends AbsRecycleAdapter<VoiceTool, RecyclerView.ViewHolder> {
    private static final int TYPE_GROUP = 0x000;
    private static final int TYPE_DEFAULT = 0x001;
    OnVoicePlayListener mListener;

    int currentPlayPosition = -1;

    public void setOnVoicePlayListener(OnVoicePlayListener listener) {
        this.mListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        VoiceTool data = mData.get(position);
        return data.isGroup() ? TYPE_GROUP : TYPE_DEFAULT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_GROUP) {
            view = inflater.inflate(R.layout.vw_group_item, parent, false);
        } else {
            view = inflater.inflate(R.layout.vw_voice_item, parent, false);
        }
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder viewHolder = (ItemViewHolder) holder;
        VoiceTool tool = mData.get(position);
        int type = getItemViewType(position);

        if (type == TYPE_GROUP) {
            viewHolder.mGroupNameTv.setText(tool.getGroupName());
        } else {
            viewHolder.mVoiceNameTv.setText(tool.getName());
            viewHolder.mVoiceIconIv.setImageResource(tool.getIconRes());
            if (tool.isPlay()) {
                viewHolder.mVoicePlayLayout.setVisibility(View.VISIBLE);
            } else {
                viewHolder.mVoicePlayLayout.setVisibility(View.GONE);
            }
            //设置点击事件
            viewHolder.itemView.setOnClickListener(v -> {
                viewHolder.mPlayIv.setImageResource(R.drawable.voice_tool_anim);
                AnimationDrawable animationDrawable = (AnimationDrawable) viewHolder.mPlayIv.getDrawable();
                animationDrawable.start();
                
                if (currentPlayPosition >= 0 && currentPlayPosition != position) {
                    VoiceTool preVoice = mData.get(currentPlayPosition);
                    preVoice.setPlay(false);
                    notifyItemChanged(currentPlayPosition);
                }
                //重新赋值当前播放的索引
                currentPlayPosition = position;

                if (null != mListener) {
                    boolean isPlay = tool.isPlay();
                    tool.setPlay(!isPlay);
                    mListener.onPlay(tool, !isPlay, position);
                }
                notifyItemChanged(currentPlayPosition);
            });
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        @Optional
        @InjectView(R.id.image)
        ImageView mVoiceIconIv;
        @Optional
        @InjectView(R.id.text)
        TextView mVoiceNameTv;
        @Optional
        @InjectView(R.id.voice_play_layout)
        FrameLayout mVoicePlayLayout;
        @Optional
        @InjectView(R.id.group_name)
        TextView mGroupNameTv;
        @Optional
        @InjectView(R.id.play_iv)
        ImageView mPlayIv;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    public interface OnVoicePlayListener {
        void onPlay(VoiceTool tool, boolean isPlay, int position);
    }
}
