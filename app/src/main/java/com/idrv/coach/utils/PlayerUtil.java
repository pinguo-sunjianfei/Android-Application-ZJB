package com.idrv.coach.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.idrv.coach.ZjbApplication;

import java.io.IOException;

import static com.idrv.coach.ZjbApplication.gContext;

/**
 * time:2016/8/17
 * description:
 *
 * @author sunjianfei
 */
public class PlayerUtil implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener {

    private OnPlayListener mInterface;

    private static final int STATE_NORMAL = 0;
    private static final int STATE_PLAYING = 1;
    private static final int STATE_PAUSE = 2;
    private static final int STATE_STOP = 3;
    private int mState = -1;

    private int mDuration;
    private String mPath = "";

    MediaPlayer mMediaPlayer;

    /**
     * 加载资源，如何判断是否是同一个文件呢
     *
     * @param dataPath
     */
    public boolean setResource(String dataPath) {
        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnPreparedListener(this);
            AudioManager am = (AudioManager) ZjbApplication.gContext.getSystemService(Context.AUDIO_SERVICE);
            int volume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setVolume(volume, volume);

            mMediaPlayer.setDataSource(dataPath);
            mMediaPlayer.prepare();
            mDuration = mMediaPlayer.getDuration();
            return true;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setResource(int resId) {
        try {
            AssetFileDescriptor afd = gContext.getResources().openRawResourceFd(resId);
            if (null == afd) {
                return;
            }
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnPreparedListener(this);
            AudioManager am = (AudioManager) ZjbApplication.gContext.getSystemService(Context.AUDIO_SERVICE);
            int volume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setVolume(volume, volume);


            mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            mMediaPlayer.prepare();
            mDuration = mMediaPlayer.getDuration();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        if (mMediaPlayer != null) {
            mState = STATE_PLAYING;
            mMediaPlayer.start();
        }
    }

    public void resume() {
        if (mState == STATE_PAUSE && null != mMediaPlayer) {
            mMediaPlayer.start();
        }
    }

    public void pause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mState = STATE_PAUSE;
            mMediaPlayer.pause();
        }
    }

    public void stop() {
        if (mMediaPlayer != null) {
            mState = STATE_STOP;
            mMediaPlayer.stop();
        }
    }

    /**
     * 释放播放器占用的资源
     */
    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnPreparedListener(this);
        }
    }

    /**
     * 使播放器从error中恢复到IDLE状态
     */
    public void reset() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
        }
    }

    public int getDuration() {
        try {
            mDuration = mMediaPlayer.getDuration();
        } catch (Exception e) {
            mDuration = 0;
        }
        return mDuration;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mInterface != null) {
            mInterface.OnCompleted();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Logger.i("onPreared playerTool");
        if (mInterface != null) {
            mInterface.OnPrepared();
        }
    }

    public void prepare() {
        try {
            mMediaPlayer.prepare();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void setOnPlayListener(OnPlayListener onPlayListener) {
        mInterface = onPlayListener;
    }

    public interface OnPlayListener {
        void OnCompleted();

        void OnPrepared();

        void OnError(String errorInfo);
    }
}
