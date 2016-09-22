package com.idrv.coach.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

import com.buihha.audiorecorder.Mp3Recorder;
import com.idrv.coach.R;
import com.idrv.coach.utils.FileUtil;
import com.idrv.coach.utils.Logger;

import java.io.File;
import java.io.IOException;


/**
 * 录音部分，自定义的按钮 功能基本在这个按钮实现了
 * TODO string
 *
 * @author 明明大美女
 */

public class RecorderClickBt extends Button implements Mp3Recorder.VolumeChangeListener {

    private AudioRecorderListener mListener;

    // Y方向移动的距离
    private static final int DISTANCE_Y_CANCEL = 50;
    // 用来判断按下时间过短的常量
    private static final float MSG_SHORT_TIME = 0.6f;

    public static final int STATE_NORMAL = 1;
    public static final int STATE_RECORDING = 2;
    public static final int STATE_SHORT = 4;
    public static final int STATE_DONE = 5;

    private int mCurState = STATE_NORMAL;
    // 已经开始录音
    private boolean isRecording = false;
    // 时间已经到了
    private boolean isTimeOver = false;

    private Mp3Recorder mRecorder;

    public RecorderClickBt(Context context, AttributeSet attrs) {
        super(context, attrs);

        mRecorder = new Mp3Recorder();
        mRecorder.setVolumeChangeListener(this);
        mRecorder.setAudioOutputPath(FileUtil.createPath(FileUtil.DIR_TYPE_TEMP, "cacheRecord"));

        setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                setBackgroundResource(R.drawable.shape_record_p);
            } else if (event.getAction() == MotionEvent.ACTION_UP &&
                    context.getString(R.string.loosen_over).equals(getText().toString())) {
                setText(R.string.press_long_record_again);
                setBackgroundResource(R.drawable.shape_record);
                File file = new File(mRecorder.getAudioOutputPath());
                if (!file.exists() || file.length() == 0) {
                    mListener.onNoPermission();
                    file.delete();
                } else if (mListener != null) {
                    mListener.onOk();
                }
                try {
                    mRecorder.stopRecording();
                } catch (IOException e) {
                    Logger.e("mp3 audio recorder stop error!");
                    e.printStackTrace();
                }
                reset();
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                setBackgroundResource(R.drawable.shape_record);
            }
            return false;
        });
    }

    public boolean onLongClick() {
        if (!getContext().getString(R.string.loosen_over).equals(getText().toString())) {
            try {
                mRecorder.stopRecording();
            } catch (Exception e) {

            }
            try {
                mRecorder.startRecording();
                if (null != mListener) {
                    mListener.onRecordStart();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            setText(R.string.loosen_over);
            return true;
        } else {
            return false;
        }
    }

    public RecorderClickBt(Context context) {
        this(context, null);
    }

    /**
     * 恢复状态及标志位
     */
    private void reset() {
        isRecording = false;
        changeState(STATE_NORMAL);
    }

    /**
     * 通过ontouch 判断button的状态，并改变显示等。同时改变dialog的显示
     *
     * @param state
     */
    private void changeState(int state) {
        if (mCurState != state) {
            mCurState = state;
            switch (state) {
                case STATE_NORMAL:
                    setText(R.string.press_long_record);
                    break;
                case STATE_RECORDING:
                    setText(R.string.loosen_over);
                    if (isRecording) {
                        mListener.onRecordStart();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void timeIsOver() {
        // 回复到初始状态
        setText(R.string.press_long_record);
        mCurState = STATE_NORMAL;
        // 完全模拟up！
        isTimeOver = true;
        reset();
        try {
            mRecorder.stopRecording();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (mListener != null) {
            mListener.onOk();
        }
    }

    public void release() {
        reset();
        try {
            mRecorder.stopRecording();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isRecording() {
        return isRecording;
    }

    public String getCurrentPath() {
        return mRecorder.getAudioOutputPath();
    }

    @Override
    public void onVolumeChange(int level) {
        if (null != mListener) {
            mListener.onVoiceLevel(level);
        }
    }

    /**
     * 录音完成后的回调
     *
     * @author 明明大美女
     */
    public interface AudioRecorderListener {
        void onRecordStart();

        void onVoiceLevel(int level);

        void onCancel();

        void onOk();

        void onNoPermission();
    }

    public void setAudioRecorderListener(AudioRecorderListener listener) {
        mListener = listener;
    }


}
